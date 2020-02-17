package com.simone.progetto;

import com.simone.progetto.syncro.SyncroCodeRequestMessage;
import com.simone.progetto.syncro.SyncroCodeResponseMessage;
import com.simone.progetto.syncro.SyncronizationCodeResponseQueue;
import com.simone.progetto.utils.MyLogger;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

@RabbitListener(id="multi",queues = "#{SyncroRequestCode.name}")
public class syncroRequestCode {
    @Autowired
    private Chain chain;
    @Autowired private Semaphore lock_chain;
    @Autowired private SyncronizationCodeResponseQueue communicator;

    @RabbitHandler
    public void receive(SyncroCodeRequestMessage syncroCodeRequestMessage){
        if(!syncroCodeRequestMessage.getId_applicant().equals(Constants.UUID)){
            SyncroCodeResponseMessage msg = new SyncroCodeResponseMessage(Constants.UUID,
                    syncroCodeRequestMessage.getId_applicant(),syncroCodeRequestMessage.getRequest());
            switch (syncroCodeRequestMessage.getRequest()){
                case ALL:
                    msg.setBlock_of_transaction(chain.getChain());
                    break;
                case ANY:
                    ArrayList<Block> blocks = new ArrayList<Block>();
                    for (Integer i: syncroCodeRequestMessage.getRequest_block()) {
                        if(i < chain.getIdLastBlock()){
                            Block b = chain.getElementChain(i);
                            if(b != null){
                                blocks.add(b);
                            }
                        }
                    }
                    msg.setBlock_of_transaction(blocks);
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
                lock_chain.acquire();
                if(chain.isToUpdate()){//Controllo che già non sia stata effettuata la syncronizzazione
                    switch (syncroCodeResponseMessage.getType_request()){
                        case ALL:
                            //Controllo che la chain sia valida
                            if(chain.setChain(syncroCodeResponseMessage.getBlock_of_transaction())){
                                MyLogger.getInstance().info(syncroRequestCode.class.getName() + " - " + Constants.UUID,
                                        "Syncronizzazione del consumers con la blockchain effettuata correttamente");
                            }
                            else{
                                MyLogger.getInstance().info(syncroRequestCode.class.getName() + " - " + Constants.UUID,
                                        "Syncronizzazione del consumers con la blockchain non effettuata correttamente,uscita");
                                toExit = true;
                            }
                            break;
                        case ANY:
                            for (Block b: syncroCodeResponseMessage.getBlock_of_transaction()) {
                                chain.setBlockFromOtherConsumer(b);
                            }
                            break;
                    }
                }
            }
            catch (Exception ex){
                MyLogger.getInstance().error(syncroRequestCode.class.getName() + " - " + Constants.UUID,"Eccezione nell'acquire --> "+ex.toString(),ex);
            }
            lock_chain.release();
        }
        if (toExit){
            System.exit(1);
        }
    }
}

