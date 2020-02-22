package com.simone.progetto;

import com.simone.progetto.syncro.SyncroCommunicator;
import com.simone.progetto.syncro.SyncroMessage;
import com.simone.progetto.utils.Configuration;
import com.simone.progetto.utils.InsertChainSemaphore;
import com.simone.progetto.utils.MyLogger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


public class Receiver {
	@Autowired private Chain chain;
	@Autowired private TransactionRules transactionRules;
	@Autowired private InsertChainSemaphore insertChainSemaphore;
	@Qualifier("syncronization_queue")
	@Autowired private SyncroCommunicator communicator;

	@RabbitListener(queues = "#{TransactionQueue.name}")
	public void receive(Transaction transaction){
		insertChainSemaphore.restartSemaphore();
		MyLogger.getInstance().info(Receiver.class.getName() + " - " + Configuration.UUID,"Transazione arrivata --> " + transaction.getProduct().getName());
		if(transactionRules.canInsert(transaction)){
			Block b = chain.createBlock(transaction);
			if(insertChainSemaphore.isToCompute()){
				MyLogger.getInstance().info(Receiver.class.getName() + " - " + Configuration.UUID,"Blocco creato --> " + b.toString());
				if(chain.insertToChain(b)){
					MyLogger.getInstance().info(Receiver.class.getName() + " - " + Configuration.UUID,"Blocco Inserito correttamente{Inserita da me}");
					communicator.sendMessage(new SyncroMessage(b));
					MyLogger.getInstance().info(Receiver.class.getName() + " - " + Configuration.UUID,"Messaggio di syncronizzazione inviato a tutti i consumers");
				}
				else{
					MyLogger.getInstance().info(Receiver.class.getName() + " - " + Configuration.UUID,"Transazione non inserita correttamente");
				}
			}
			else{
				MyLogger.getInstance().info(Receiver.class.getName() + " - " + Configuration.UUID,"Terminata la computazione perchè blocco già risolto da un altro consumer");
			}
		}
		else {
			MyLogger.getInstance().info(Receiver.class.getName() + " - " + Configuration.UUID,"transaction non inserita, non ha superato i controlli");
		}
		chain.printChain();
	}
}
