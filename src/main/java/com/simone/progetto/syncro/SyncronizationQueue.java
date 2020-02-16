package com.simone.progetto.syncro;
import com.simone.progetto.Constants;
import com.simone.progetto.utils.MyLogger;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service("syncronization_queue")
public class SyncronizationQueue implements SyncroCommunicator {
    private final RabbitTemplate rabbitTemplate;

    @Qualifier("fanout_syncro")
    @Autowired private FanoutExchange fanoutExchange;

    @Autowired
    private SyncronizationCodeResponseQueue communicator;

    @Autowired
    public SyncronizationQueue(final RabbitTemplate template) {
        this.rabbitTemplate = template;
    }

    public void sendMessage(SyncroMessage message) {
        rabbitTemplate.convertAndSend(fanoutExchange.getName(),
                "",message);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void SyncronizationStartup() {
        MyLogger.getInstance().info(SyncronizationQueue.class.getName() + " - " + Constants.UUID,"Richiesta syncronizzazione della coda");
        communicator.sendRequest(new SyncroCodeRequestMessage(Constants.UUID, Constants.Status_request_block.ALL));
    }
}
