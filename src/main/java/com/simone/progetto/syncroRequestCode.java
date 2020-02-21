package com.simone.progetto;

import com.simone.progetto.syncro.SyncroCodeRequestMessage;
import com.simone.progetto.syncro.SyncroCodeResponseMessage;
import com.simone.progetto.syncro.SyncronizationCodeResponseQueue;
import com.simone.progetto.utils.Configuration;
import com.simone.progetto.utils.MyLogger;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@RabbitListener(id="multi",queues = "#{SyncroRequestCode.name}")
public class syncroRequestCode {
    @Autowired
    private Chain chain;
    @Autowired private SyncronizationCodeResponseQueue communicator;

    @RabbitHandler
    public void receive(SyncroCodeRequestMessage syncroCodeRequestMessage){
        if(!syncroCodeRequestMessage.getId_applicant().equals(Configuration.UUID)){
            SyncroCodeResponseMessage msg = new SyncroCodeResponseMessage(Configuration.UUID,
                    syncroCodeRequestMessage.getId_applicant());
            List<Node> toSend = new ArrayList<Node>();
            Node searched = chain.searchBlock(syncroCodeRequestMessage.getRequest_block());
            if(searched != null){
                toSend.add(searched);
                msg.setRequest_node(toSend);
            }
            //DEVO INVIARLO NELLA CODA
            communicator.sendResponse(msg);
        }
    }

    @RabbitHandler
    public void receive(SyncroCodeResponseMessage syncroCodeResponseMessage){
        boolean toExit = false;
        if(!syncroCodeResponseMessage.getId_publisher().equals(Configuration.UUID) &&
        syncroCodeResponseMessage.getId_consumer().equals(Configuration.UUID)){
            //Controllo che già non sia stata effettuata la syncronizzazione
            if(syncroCodeResponseMessage.getRequest_node().size() > 0){
                Node requested = syncroCodeResponseMessage.getRequest_node().get(0);
                if(chain.insertToChain(requested)){
                    MyLogger.getInstance().info(syncroRequestCode.class.getName() + " - " + Configuration.UUID,
                            "Syncronizzazione del consumers con la blockchain effettuata correttamente{Caso Any}");
                }
                else{
                    if(requested.getParent() != null){//Se non riesco ad aggiungere, vuol dire che manca qualcosa alla catena
                        //Richiedo allora la catena che parte dal padre
                        SyncroCodeRequestMessage msg = new SyncroCodeRequestMessage(Configuration.UUID);
                        msg.setRequest_block(requested.getParent().getData().getHash());
                        communicator.sendRequest(msg);
                    }
                    else{
                        MyLogger.getInstance().info(syncroRequestCode.class.getName() + " - " + Configuration.UUID,
                                "Syncronizzazione del consumers con la blockchain non effettuata correttamente,uscita{Caso Any}");
                        toExit = true;
                    }
                }
            }
        }
        if (toExit){
            System.exit(1);
        }
        chain.printChain();
    }


}

