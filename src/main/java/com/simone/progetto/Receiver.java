package com.simone.progetto;

import com.simone.progetto.syncro.SyncroCommunicator;
import com.simone.progetto.syncro.SyncroMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class Receiver {
	private static final Logger log = LoggerFactory.getLogger(Receiver.class);
	@Autowired private Chain chain;
	@Autowired private TransactionRules transactionRules;
	@Qualifier("syncronization_queue")
	@Autowired private SyncroCommunicator communicator;
	
	@RabbitListener(queues = "#{TransactionQueue.name}")
	public void receive(Transaction transaction)  {
		log.info("transaction arrived, product name --> " + transaction.getProduct().getName());
		if(transactionRules.canInsert(transaction)){
			Block b = chain.createBlock(transaction);
			chain.insertBlock(b);
			log.info("transaction inserted");
			communicator.sendMessage(new SyncroMessage(b));
			log.info("syncro message send to all consumers");
		}
		else{
			log.info("transaction not inserted");
		}
	}

	@RabbitListener(queues = "#{SyncroQueue.name}")
	public void receive_syncro(SyncroMessage message)  {
		if(!message.getId_consumer().equals(Constants.UUID)){
			System.out.println("ciao");
		}
	}




}
