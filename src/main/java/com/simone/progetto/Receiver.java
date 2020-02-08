package com.simone.progetto;

import com.simone.progetto.syncro.SyncroCommunicator;
import com.simone.progetto.syncro.SyncroMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.CompletionService;

public class Receiver {
	
private static final Logger log = LoggerFactory.getLogger(Receiver.class);
	@Autowired private Chain chain;
	@Autowired private TransactionRules transactionRules;

	@Qualifier("syncronization_queue")
	@Autowired private SyncroCommunicator communicator;
	
	@RabbitListener(queues = "#{TransactionQueue.name}")
	public void receive(Transaction transaction)  {
		log.info("transaction arrived, product name --> " + transaction.getProduct().getName() );
		log.info(transaction.toString());
		if(transactionRules.canInsert(transaction)){
			chain.insertElement(transaction);
			//TODO MANDARE IL MESSAGGIO A TUTTI
			SyncroMessage msg  = new SyncroMessage();
			communicator.sendMessage(msg);
			log.info("transaction inserted");
		}
		else{
			log.info("transaction not inserted");
		}
	}

	@RabbitListener(queues = "#{SyncroQueue.name}")
	public void receive_syncro(SyncroMessage message)  {
		if(!message.getId_consumer().equals(Constants.UUID)){
			System.out.println(message.getMessage());
		}

	}

}
