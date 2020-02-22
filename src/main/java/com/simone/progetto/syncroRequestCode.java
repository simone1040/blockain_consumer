package com.simone.progetto;

import com.simone.progetto.syncro.SyncroCodeRequestMessage;
import com.simone.progetto.syncro.SyncroCodeResponseMessage;
import com.simone.progetto.syncro.SyncronizationCodeResponseQueue;
import com.simone.progetto.utils.Configuration;
import com.simone.progetto.utils.MyLogger;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Stack;

@RabbitListener(id="multi",queues = "#{SyncroRequestCode.name}")
public class syncroRequestCode {
    @Autowired
    private Chain chain;
    @Autowired private SyncronizationCodeResponseQueue communicator;

    @RabbitHandler
    public void receive(SyncroCodeRequestMessage syncroCodeRequestMessage){
        Stack<Node> toSend = null;
        if(!syncroCodeRequestMessage.getId_applicant().equals(Configuration.UUID)){
            SyncroCodeResponseMessage msg = new SyncroCodeResponseMessage(Configuration.UUID,
                    syncroCodeRequestMessage.getId_applicant());
            for (Node node: chain.getTopList()) {
                //CERCO IL BLOCCO IN OGNI CATENA E RITORNO TUTTI QUELLI CHE LO SUCCEDONO
                toSend = chain.searchListOfBlock(node,syncroCodeRequestMessage.getRequest_block());
                if(toSend != null){
                    break;
                }
            }
            if(toSend != null){
                if(toSend.size() > 0){
                    msg.setRequest_node(toSend);
                    //DEVO INVIARLO NELLA CODA
                    communicator.sendResponse(msg);
                }
                else{
                    MyLogger.getInstance().info(syncroRequestCode.class.getName() + " - " + Configuration.UUID,
                            "Non rispondo perchè il consumer è già sincronizzato !");
                }
            }
            else{
                MyLogger.getInstance().info(syncroRequestCode.class.getName() + " - " + Configuration.UUID,
                        "Non rispondo perchè non ho trovato il blocco richiesto!");
            }
        }
    }

    @RabbitHandler
    public void receive(SyncroCodeResponseMessage syncroCodeResponseMessage){
        boolean syncro = true;
        Node toInsert = null;
        if(!syncroCodeResponseMessage.getId_publisher().equals(Configuration.UUID) &&
        syncroCodeResponseMessage.getId_consumer().equals(Configuration.UUID)) {
            //Controllo che già non sia stata effettuata la syncronizzazione
            if (chain.isToUpdate()) { ;
                while (syncroCodeResponseMessage.getRequest_node().size() > 0 && syncro) { //INserisco finchè lo stack è pieno
                    toInsert = syncroCodeResponseMessage.getRequest_node().pop();
                    if(chain.checkHashBlock(toInsert.getData())){
                        if (!chain.insertToChain(toInsert)) {
                            syncro = false;
                            if (toInsert.getParent() != null) {
                                MyLogger.getInstance().info(syncroRequestCode.class.getName() + " - " + Configuration.UUID,
                                        "Syncronizzazione del consumers con la blockchain non effettuata poichè " +
                                                "manca qualche blocco precedente");
                                SyncroCodeRequestMessage msg = new SyncroCodeRequestMessage(Configuration.UUID,toInsert.getParent().getData().getHash());
                                communicator.sendRequest(msg);
                            }
                        }
                    }
                }
                if (syncro) {
                    MyLogger.getInstance().info(syncroRequestCode.class.getName() + " - " + Configuration.UUID,
                                "Syncronizzazione del consumers con la blockchain effettuata correttamente ! ");
                    chain.restartTopList(toInsert);
                    chain.setToUpdate(false);
                    chain.printChain();
                }
            }
        }
    }

}

