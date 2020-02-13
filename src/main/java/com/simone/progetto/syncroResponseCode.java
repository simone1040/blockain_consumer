package com.simone.progetto;

import com.simone.progetto.syncro.SyncroCodeRequestMessage;
import com.simone.progetto.syncro.SyncroCodeResponseMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class syncroResponseCode {


    @RabbitListener(queues = "#{SyncroResponseCode.name}")
    public void receive(SyncroCodeResponseMessage syncroCodeResponseMessage){
        if(syncroCodeResponseMessage.getId_consumer().equals(Constants.UUID)){
            System.out.println(syncroCodeResponseMessage.getId_consumer());
        }
    }
}
