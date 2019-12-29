package com.simone.progetto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import rabbitMQ.RabbitMQConfiguration;

@Service
public class Receiver {
	
private static final Logger log = LoggerFactory.getLogger(Receiver.class);
	
	@RabbitListener(queues = RabbitMQConfiguration.DEFAULT_PARSING_QUEUE)
	public void consumeDefaultMessage(final Transaction transaction) {
		log.info(transaction.getProduct().getName());
		log.info(transaction.toString());
	}

}
