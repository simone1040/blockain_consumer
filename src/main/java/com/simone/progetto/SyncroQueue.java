package com.simone.progetto;

import com.simone.progetto.syncro.SyncroCodeRequestMessage;
import com.simone.progetto.syncro.SyncroMessage;
import com.simone.progetto.syncro.SyncronizationCodeResponseQueue;
import com.simone.progetto.utils.Configuration;
import com.simone.progetto.utils.InsertChainSemaphore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SyncroQueue {
    @Autowired private Chain chain;
    @Autowired private InsertChainSemaphore insertChainSemaphore;
    @Autowired private SyncronizationCodeResponseQueue communicator;

    public void requestPreviousBlock(String previousHash){
        SyncroCodeRequestMessage msg = new SyncroCodeRequestMessage(Configuration.UUID,previousHash);
        //Richiedo dal blocco precedente e mi faccio mandare l'intera catena a partire da esso
        communicator.sendRequest(msg);
    }

    public void tryToInsertBlockFromOtherConsumer(Block block){
        if(chain.insertToChain(block)){
            insertChainSemaphore.blockComputation();
            log.info("{" + Configuration.UUID + "{ Block successful inserted ! {computed from another consumer}");
        }
        else{//Non Ho inserito correttamente, potrebbe mancarmi qualcosa
            this.requestPreviousBlock(block.getPreviousHash());
        }
    }

    @RabbitListener(queues = "#{SyncroQueue.name}")
    public void receive_syncro(SyncroMessage message){
        if(!message.getId_consumer().equals(Configuration.UUID)){//Messaggio che non arriva da me stesso
            //Controlliamo che il blocco abbia l'hash giusto.
            if(chain.checkHashBlock(message.getBlock())){//Hash corretto
                this.tryToInsertBlockFromOtherConsumer(message.getBlock());
            }
            else{
                log.info("{" + Configuration.UUID + "} Block hash corrupted !");
            }
        }
    }

}
