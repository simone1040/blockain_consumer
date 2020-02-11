package com.simone.progetto.syncro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("syncronization_queue")
public class SyncronizationQueue implements SyncroCommunicator {
    private final RabbitTemplate rabbitTemplate;

    @Qualifier("fanout_syncro")
    @Autowired private FanoutExchange fanoutExchange;

    @Autowired
    public SyncronizationQueue(final RabbitTemplate template) {
        this.rabbitTemplate = template;
    }

    public boolean sendMessage(SyncroMessage message) {
        rabbitTemplate.convertAndSend(fanoutExchange.getName(),
                "",message);
        return true;
    }
}
