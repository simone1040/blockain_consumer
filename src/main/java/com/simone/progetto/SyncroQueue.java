package com.simone.progetto;

import com.simone.progetto.syncro.SyncroCodeRequestMessage;
import com.simone.progetto.syncro.SyncroMessage;
import com.simone.progetto.syncro.SyncronizationCodeResponseQueue;
import com.simone.progetto.utils.Configuration;
import com.simone.progetto.utils.InsertChainSemaphore;
import com.simone.progetto.utils.MyLogger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;


public class SyncroQueue {
    @Autowired private Chain chain;
    @Autowired private InsertChainSemaphore insertChainSemaphore;
    @Autowired private SyncronizationCodeResponseQueue communicator;

    @RabbitListener(queues = "#{SyncroQueue.name}")
    public void receive_syncro(SyncroMessage message){
        if(!message.getId_consumer().equals(Configuration.UUID)){//Messaggio che non arriva da me stesso
            //Controlliamo che il blocco abbia l'hash giusto.
            if(chain.checkHashBlock(message.getBlock())){//Hash corretto
                if(chain.insertToChain(message.getBlock())){
                    insertChainSemaphore.blockComputation();
                    MyLogger.getInstance().info(Receiver.class.getName() + " - " + Configuration.UUID,"Blocco Inserito correttamente{Proveniente da altro consumer}");
                }
                else{//Non Ho inserito correttamente, potrebbe mancarmi qualcosa
                    SyncroCodeRequestMessage msg = new SyncroCodeRequestMessage(Configuration.UUID);
                    msg.setRequest_block(message.getBlock().getPreviousHash());
                    //Richiedo dal blocco precedente e mi faccio mandare l'intera catena a partire da esso
                    communicator.sendRequest(msg);
                }
            }
            else{
                MyLogger.getInstance().info(Receiver.class.getName() + " - " + Configuration.UUID,"Blocco con Hash scorretto, il blocco viene scartato");
            }
        }
    }

}
