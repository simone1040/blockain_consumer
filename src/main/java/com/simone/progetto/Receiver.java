package com.simone.progetto;

import com.simone.progetto.syncro.SyncroCommunicator;
import com.simone.progetto.syncro.SyncroMessage;
import com.simone.progetto.utils.MyLogger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.Semaphore;

public class Receiver {
	@Autowired private Chain chain;
	@Autowired private TransactionRules transactionRules;
	@Qualifier("syncronization_queue")
	@Autowired private SyncroCommunicator communicator;
	@Autowired private Semaphore lock_chain;

	@RabbitListener(queues = "#{TransactionQueue.name}")
	public void receive(Transaction transaction){
		MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Transazione arrivata,nome prodotto --> " + transaction.getProduct().getName());
		if(transactionRules.canInsert(transaction)){
			Block b = chain.createBlock(transaction);
			try{
				lock_chain.acquire();
				if(chain.getChain().size() == 0 || chain.getIdLastBlock() < b.getId_block()){// ancora nessuno ha inserito
					chain.insertBlock(b);
					MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Transazione inserita correttamente");
					communicator.sendMessage(new SyncroMessage(b));
					MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Messaggio di syncronizzazione inviato a tutti i consumers");
				}
				else{
					for(int i = chain.getChain().size() - 1 ; i >= 0 ;i--){
						Block block = chain.getChain().get(i);
						if(block.getTimestamp() > b.getTimestamp()){
							MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Sostituisco perchè il blocco da me calcolato ha timestamp minore");
							chain.getChain().set(i,b);
						}
					}
				}
				lock_chain.release();
			}
			catch (Exception ex){
				MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Eccezione --> "+ ex);
			}
		}
		else{
			MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"transaction not inserted");
		}
	}
}
