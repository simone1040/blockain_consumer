package com.simone.progetto;

import com.simone.progetto.syncro.SyncroCodeRequestMessage;
import com.simone.progetto.syncro.SyncroCodeResponseMessage;
import com.simone.progetto.syncro.SyncronizationCodeResponseQueue;
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
        if(!syncroCodeRequestMessage.getId_applicant().equals(Constants.UUID)){
            SyncroCodeResponseMessage msg = new SyncroCodeResponseMessage(Constants.UUID,
                    syncroCodeRequestMessage.getId_applicant(),syncroCodeRequestMessage.getRequest());
            switch (syncroCodeRequestMessage.getRequest()){
                case ALL:
                    msg.setRequest_node(chain.getRoot());
                    break;
                case ANY:
                    //Dal request prendo lo string dell'hash richiesto e mando il nodo con tutti i figli
                    List<Node> toSend = new ArrayList<Node>();
                    Node searched = chain.search(syncroCodeRequestMessage.getRequest_block());
                    if(searched != null){
                        toSend.add(searched);
                        msg.setRequest_node(toSend);
                    }
                    break;
            }
            //DEVO INVIARLO NELLA CODA
            communicator.sendResponse(msg);
        }
    }

    @RabbitHandler
    public void receive(SyncroCodeResponseMessage syncroCodeResponseMessage){
        boolean toExit = false;
        if(!syncroCodeResponseMessage.getId_publisher().equals(Constants.UUID) &&
        syncroCodeResponseMessage.getId_consumer().equals(Constants.UUID)){
            try{
                if(chain.isToUpdate()){//Controllo che già non sia stata effettuata la syncronizzazione
                    switch (syncroCodeResponseMessage.getType_request()){
                        case ALL:
                            //Controllo che la chain sia valida
                            if(chain.setChain(syncroCodeResponseMessage.getRequest_node())){
                                chain.setStartComputationNode();
                                MyLogger.getInstance().info(syncroRequestCode.class.getName() + " - " + Constants.UUID,
                                        "Syncronizzazione del consumers con la blockchain effettuata correttamente{Caso ALL}");
                                chain.printChain();
                            }
                            else{
                                MyLogger.getInstance().info(syncroRequestCode.class.getName() + " - " + Constants.UUID,
                                        "Syncronizzazione del consumers con la blockchain non effettuata correttamente,uscita{Caso ALL}");
                                toExit = true;
                            }
                            break;
                        case ANY:
                            if(syncroCodeResponseMessage.getRequest_node().size() > 0){
                                Node requested = syncroCodeResponseMessage.getRequest_node().get(0);
                                if(chain.insertToChain(requested,true)){
                                    MyLogger.getInstance().info(syncroRequestCode.class.getName() + " - " + Constants.UUID,
                                            "Syncronizzazione del consumers con la blockchain effettuata correttamente{Caso Any}");
                                    chain.printChain();
                                }
                                else{
                                    if(requested.getParent() != null){
                                        SyncroCodeRequestMessage msg = new SyncroCodeRequestMessage(Constants.UUID, Constants.Status_request_block.ANY);
                                        msg.setRequest_block(requested.getParent().getData().getHash());
                                        communicator.sendRequest(msg);
                                    }
                                    else{
                                        MyLogger.getInstance().info(syncroRequestCode.class.getName() + " - " + Constants.UUID,
                                                "Syncronizzazione del consumers con la blockchain non effettuata correttamente,uscita{Caso Any}");
                                        toExit = true;
                                    }
                                }
                            }
                            break;
                    }
                }
            }
            catch (Exception ex){
                MyLogger.getInstance().error(syncroRequestCode.class.getName() + " - " + Constants.UUID,"Eccezione nell'acquire --> "+ex.toString(),ex);
            }
        }
        if (toExit){
            System.exit(1);
        }
    }


}

