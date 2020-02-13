package com.simone.progetto;

import com.simone.progetto.syncro.SyncroCodeRequestMessage;
import com.simone.progetto.syncro.SyncroCodeResponseMessage;
import com.simone.progetto.syncro.SyncroCommunicator;
import com.simone.progetto.syncro.SyncronizationCodeResponseQueue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;

public class syncroRequestCode {
    @Autowired
    private Chain chain;
    @Autowired private SyncronizationCodeResponseQueue communicator;

    @RabbitListener(queues = "#{SyncroRequestCode.name}")
    public void receive(SyncroCodeRequestMessage syncroCodeRequestMessage){
        if(syncroCodeRequestMessage.getId_applicant().equals(Constants.UUID)){
            SyncroCodeResponseMessage msg = new SyncroCodeResponseMessage(syncroCodeRequestMessage.getId_applicant(),
                    syncroCodeRequestMessage.getRequest());
            switch (syncroCodeRequestMessage.getRequest()){
                case ALL:
                    msg.setBlock_of_transaction(chain.getChain());
                    break;
                case ANY:
                    ArrayList<Block> blocks = new ArrayList<Block>();
                    for (Integer i: syncroCodeRequestMessage.getRequest_block()) {
                        if(i < chain.getIdLastBlock()){
                            blocks.add(chain.getChain().get(i));
                        }
                    }
                    msg.setBlock_of_transaction(blocks);
                    break;
            }
            //DEVO INVIARLO NELLA CODA
            communicator.sendMessage(msg);
        }
    }
}
