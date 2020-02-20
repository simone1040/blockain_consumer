package com.simone.progetto;

import com.simone.progetto.syncro.SyncroCommunicator;
import com.simone.progetto.syncro.SyncroMessage;
import com.simone.progetto.utils.MyLogger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


public class Receiver {
	@Autowired private Chain chain;
	@Autowired private TransactionRules transactionRules;
	@Qualifier("syncronization_queue")
	@Autowired private SyncroCommunicator communicator;

	@RabbitListener(queues = "#{TransactionQueue.name}")
	public void receive(Transaction transaction){
		MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Transazione arrivata,nome prodotto --> " + transaction.getProduct().getName());
		if(transactionRules.canInsert(transaction)){
			Block b = chain.createBlock(transaction);
			if(chain.insertToChain(b,false)){
				MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Blocco Inserito correttamente{Inserita da me}");
				communicator.sendMessage(new SyncroMessage(b));
				MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Messaggio di syncronizzazione inviato a tutti i consumers");
			}
			else{
				MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Transazione non inserita correttamente");
			}
		}
		else{
			MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"transaction non inserita, non ha superato i controlli");
		}
	}
}
