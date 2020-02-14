package com.simone.progetto.syncro;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("syncronization_request_queue")
public class SyncronizationCodeResponseQueue{
    private final RabbitTemplate rabbitTemplate;

    @Qualifier("fanout_syncro_request_code")
    @Autowired private FanoutExchange fanoutExchange;

    @Autowired
    public SyncronizationCodeResponseQueue(final RabbitTemplate template) {
        this.rabbitTemplate = template;
    }

    public void sendRequest(SyncroCodeRequestMessage message) {
        rabbitTemplate.convertAndSend(fanoutExchange.getName(),
                "",message);
    }

    public void sendResponse(SyncroCodeResponseMessage message) {
        rabbitTemplate.convertAndSend(fanoutExchange.getName(),
                "",message);
    }
}
