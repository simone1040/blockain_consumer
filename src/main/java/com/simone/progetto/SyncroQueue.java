package com.simone.progetto;

import com.simone.progetto.syncro.SyncroCodeRequestMessage;
import com.simone.progetto.syncro.SyncroMessage;
import com.simone.progetto.syncro.SyncronizationCodeResponseQueue;
import com.simone.progetto.utils.MyLogger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;


public class SyncroQueue {
    @Autowired private Chain chain;
    @Autowired private SyncronizationCodeResponseQueue communicator;

    @RabbitListener(queues = "#{SyncroQueue.name}")
    public void receive_syncro(SyncroMessage message){
        if(!message.getId_consumer().equals(Constants.UUID)){//Messaggio che non arriva da me stesso
            //Controlliamo che il blocco abbia l'hash giusto.
            if(message.getBlock().computeHash(false).equals(message.getBlock().getHash())){//Hash corretto
                try {
                    //Controllo che sia il blocco consecutivo in ordine numerico,altrimenti lo richiedo
                    if(chain.insertToChain(message.getBlock(),true)){
                        MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Blocco Inserito correttamente{Proveniente da altro consumer}");
                        if(chain.hasBrother(message.getBlock().getHash())){
                            MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Blocco Già da me o altro consumer (Ha dei fratelli)");
                        }
                    }
                    else{//Non Ho inserito correttamente, potrebbe mancarmi qualcosa
                        SyncroCodeRequestMessage msg = new SyncroCodeRequestMessage(Constants.UUID, Constants.Status_request_block.ANY);
                        msg.addRequestBlock(message.getBlock().getPreviousHash());
                        //Richiedo dal blocco precedente e mi faccio mandare l'intera catena
                        communicator.sendRequest(msg);
                    }
                    chain.printChain();
                }
                catch (Exception ex){
                    MyLogger.getInstance().error(Receiver.class.getName() + " - " + Constants.UUID,"Eccezione nell'acquire --> "+ex.toString(),ex);
                }
            }
            else{
                MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Blocco con Hash scorretto, il blocco viene scartato");
            }
        }
    }
}
