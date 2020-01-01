package com.simone.progetto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rabbitMQ.RabbitMQConfiguration;

@Service
public class Receiver {
	
private static final Logger log = LoggerFactory.getLogger(Receiver.class);
	@Autowired
	private Chain chain;
	@Autowired
	private TransactionRules transactionRules;
	
	@RabbitListener(queues = RabbitMQConfiguration.DEFAULT_PARSING_QUEUE)
	public void consumeDefaultMessage(Transaction transaction) {
		log.info("transaction arrived, product name --> " + transaction.getProduct().getName() );
		log.info(transaction.toString());
		if(transactionRules.canInsert(transaction)){
			chain.insertElement(transaction);
			log.info("transaction inserted");
		}
		else{
			log.info("transaction not inserted");
		}


	}

}
