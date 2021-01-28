package com.simone.progetto;

import com.simone.progetto.syncro.SyncroCommunicator;
import com.simone.progetto.syncro.SyncroMessage;
import com.simone.progetto.utils.Configuration;
import com.simone.progetto.utils.InsertChainSemaphore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Receiver {
	@Autowired private Chain chain;
	@Autowired private TransactionRules transactionRules;
	@Autowired private InsertChainSemaphore insertChainSemaphore;
	@Qualifier("syncronization_queue")
	@Autowired private SyncroCommunicator communicator;



	public void tryToInsert(Block block){
		if(chain.insertToChain(block)) {
			log.info("{"+Configuration.UUID + "} Block Successful inserted from this Receiver");
			communicator.sendMessage(new SyncroMessage(block));
			log.info("{"+Configuration.UUID + "} Syncro message send to other consumers");
		}
		else{
			log.info("{"+Configuration.UUID + "} Transaction not inserted");
		}
	}

	@RabbitListener(queues = "#{TransactionQueue.name}")
	public void receive(Transaction transaction){
		insertChainSemaphore.restartSemaphore();
		log.info("{"+Configuration.UUID + "} New transaction arrived --> " + transaction.getProduct().toString());
		if(transactionRules.canInsert(transaction)){
			Block b = chain.createBlock(transaction);
			if(insertChainSemaphore.isToCompute()){
				this.tryToInsert(b);
			}
			else{
				log.info("{"+Configuration.UUID + "} proof of work already computed. Check hash aborted");
			}
		}
		else {
			log.info("{"+Configuration.UUID + "} Compared hash not egual, transaction rejected");
		}
		chain.printChain();
	}


}
