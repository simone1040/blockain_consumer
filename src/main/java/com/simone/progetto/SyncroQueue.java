package com.simone.progetto;

import com.simone.progetto.syncro.SyncroCodeRequestMessage;
import com.simone.progetto.syncro.SyncroMessage;
import com.simone.progetto.syncro.SyncronizationCodeResponseQueue;
import com.simone.progetto.utils.MyLogger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Semaphore;

public class SyncroQueue {
    @Autowired private Chain chain;
    @Autowired private Semaphore lock_chain;
    @Autowired private SyncronizationCodeResponseQueue communicator;


    @RabbitListener(queues = "#{SyncroQueue.name}")
    public void receive_syncro(SyncroMessage message){
        if(!message.getId_consumer().equals(Constants.UUID)){//Messaggio che non arriva da me stesso
            //Controlliamo che il blocco abbia l'hash giusto.
            if(message.getBlock().computeHash(false).equals(message.getBlock().getHash())){//Hash corretto
                try {
                    lock_chain.acquire();
                    if(chain.getIdLastBlock() < message.getBlock().getId_block()){ // Controllo che non ci sia un blocco uguale
                        //Controllo che sia il blocco consecutivo in ordine numerico,altrimenti lo richiedo
                        if(chain.getIdLastBlock() + 1 == message.getBlock().getId_block()){
                            chain.insertBlock(message.getBlock());
                            MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Blocco già risolto da un altro consumers --> " + message.getBlock().getId_consumer() +", aggiungo il suo");
                        }
                        else{//Richiedo blocchi mancanti da inserire
                            SyncroCodeRequestMessage msg = new SyncroCodeRequestMessage(Constants.UUID, Constants.Status_request_block.ANY);
                            for(Integer index = chain.getIdLastBlock() + 1; index < message.getBlock().getId_block();index++){
                                msg.addRequestBlock(index);
                            }
                            communicator.sendRequest(msg);
                        }
                    }
                    else{
                        MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Blocco non risolto da altro consumers,inserisco il mio");
                        for(int i = chain.getChain().size() - 1 ; i >= 0 ;i--){
                            Block block = chain.getElementChain(chain.getChain(),i);
                            if(block != null){
                                if(block.getId_block().equals(message.getBlock().getId_block())){
                                    if(block.getTimestamp() > message.getBlock().getTimestamp()){
                                        MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Sostituisco perchè il blocco da me calcolato ha timestamp minore");
                                        chain.getChain().set(i,message.getBlock());
                                    }
                                }
                                else if(block.getId_block() < message.getBlock().getId_block()){
                                    break;
                                }
                            }
                        }
                    }
                    lock_chain.release();
                }
                catch (Exception ex){
                    MyLogger.getInstance().error(Receiver.class.getName() + " - " + Constants.UUID,"Eccezione nell'acquire --> "+ex.toString(),ex);
                }
            }
        }
    }
}
