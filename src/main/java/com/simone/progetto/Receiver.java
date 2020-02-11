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
	public void receive(Transaction transaction)  {
		MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Transazione arrivata,nome prodotto --> " + transaction.getProduct().getName());
		if(transactionRules.canInsert(transaction)){
			Block b = chain.createBlock(transaction);
			if(chain.getChain().size() == 0 || chain.getIdLastBlock() < b.getId_block()){
				chain.insertBlock(b);
				MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Transazione inserita correttamente");
				communicator.sendMessage(new SyncroMessage(b));
				MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Messaggio di syncronizzazione inviato a tutti i consumers");
			}
			else{
				MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Blocco già risolto da altro consumers");
			}
		}
		else{
			MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"transaction not inserted");
		}
	}

	@RabbitListener(queues = "#{SyncroQueue.name}")
	public void receive_syncro(SyncroMessage message){
		if(!message.getId_consumer().equals(Constants.UUID)){//Messaggio che non arriva da me stesso
			//Controlliamo che il blocco abbia l'hash giusto.
			if(message.getBlock().computeHash().equals(message.getBlock().getHash())){//Hash corretto
				if(chain.getIdLastBlock() < message.getBlock().getId_block()){ // Controllo che non ci sia un blocco uguale
					chain.insertBlock(message.getBlock());
					MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Blocco già risolto da un altro consumers, aggiungo il suo");
				}
				else{
					MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Blocco già da me inserito");
				}
			}
		}
		for (Block b: chain.getChain()) {
			MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Chain element --> " + b.toString());
		}
	}




}
