package com.simone.progetto.syncro;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("syncronization_queue")
public class SyncronizationCodeResponseQueue{
    private final RabbitTemplate rabbitTemplate;

    @Qualifier("fanout_syncro_response_code")
    @Autowired private FanoutExchange fanoutExchange;

    @Autowired
    public SyncronizationCodeResponseQueue(final RabbitTemplate template) {
        this.rabbitTemplate = template;
    }

    public void sendMessage(SyncroCodeResponseMessage message) {
        rabbitTemplate.convertAndSend(fanoutExchange.getName(),
                "",message);
    }
}
