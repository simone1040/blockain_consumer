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
	Semaphore lock_chain = new Semaphore(1);

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

	@RabbitListener(queues = "#{SyncroQueue.name}")
	public void receive_syncro(SyncroMessage message){
		if(!message.getId_consumer().equals(Constants.UUID)){//Messaggio che non arriva da me stesso
			//Controlliamo che il blocco abbia l'hash giusto.
			if(message.getBlock().computeHash(false).equals(message.getBlock().getHash())){//Hash corretto
				try {
					lock_chain.acquire();
					if(chain.getIdLastBlock() < message.getBlock().getId_block()){ // Controllo che non ci sia un blocco uguale
						chain.insertBlock(message.getBlock());
						MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Blocco già risolto da un altro consumers, aggiungo il suo");
					}
					else{
						MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Blocco già da me inserito");
						for(int i = chain.getChain().size() - 1 ; i >= 0 ;i--){
							Block block = chain.getChain().get(i);
							if(block.getId_block() == message.getBlock().getId_block()){
								if(block.getTimestamp() > message.getBlock().getTimestamp()){
									MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Sostituisco perchè il blocco da me calcolato ha timestamp minore");
									chain.getChain().set(i,message.getBlock());
								}
							}
							else if(block.getId_block() < message.getBlock().getId_block()){
								break;
							}
						}
					}
					lock_chain.release();
				}
				catch (Exception ex){
					MyLogger.getInstance().error(Receiver.class.getName(),"Eccezione nell'acquire --> "+ex.toString(),ex);
				}
			}
		}
	}
}
