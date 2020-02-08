package com.simone.progetto.bean;
import com.simone.progetto.Receiver;
import com.simone.progetto.syncro.SyncronizationQueue;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
	public static final String FANOUT_EXCHANGE_TRANSACTION = "FanoutExchange";
	public static final String FANOUT_EXCHANGE_SYNCRO = "FanoutExchangeSyncro";

	@Bean(name = "fanout_transaction")
	public FanoutExchange fanout_transaction() {
		return new FanoutExchange(FANOUT_EXCHANGE_TRANSACTION);
	}

	@Bean(name = "fanout_syncro")
	public FanoutExchange fanout_syncro() {
		return new FanoutExchange(FANOUT_EXCHANGE_SYNCRO);
	}

	@Bean
	public Receiver receiver() {
		return new Receiver();
	}

	@Bean
	public Queue TransactionQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue SyncroQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Binding binding(FanoutExchange fanout_transaction, Queue TransactionQueue) {
		return BindingBuilder.bind(TransactionQueue).to(fanout_transaction);
	}

	@Bean
	public Binding binding_syncro(FanoutExchange fanout_syncro, Queue SyncroQueue) {
		return BindingBuilder.bind(SyncroQueue).to(fanout_syncro);
	}


}
