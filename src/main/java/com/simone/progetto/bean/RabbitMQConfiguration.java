package com.simone.progetto.bean;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
	public static final String FANOUT_EXCHANGE_TRANSACTION = "FanoutExchange";
	public static final String FANOUT_EXCHANGE_SYNCRO = "FanoutExchangeSyncro";
	/*
	Coda che permette la sincronizzazione dei nuovi consumer, e dei vecchi
	 */
	public static final String FANOUT_EXCHANGE_SYNCRO_REQUEST_CODE = "FanoutExchangeSyncroRequestCode";
	public static final String FANOUT_EXCHANGE_SYNCRO_RESPONSE_CODE = "FanoutExchangeSyncroResponseCode";


	@Bean(name = "fanout_transaction")
	public FanoutExchange fanout_transaction() {
		return new FanoutExchange(FANOUT_EXCHANGE_TRANSACTION);
	}

	@Bean(name = "fanout_syncro")
	public FanoutExchange fanout_syncro() {
		return new FanoutExchange(FANOUT_EXCHANGE_SYNCRO);
	}

	@Bean(name = "fanout_syncro_request_code")
	public FanoutExchange fanout_syncro_request_code() {
		return new FanoutExchange(FANOUT_EXCHANGE_SYNCRO_REQUEST_CODE);
	}

	@Bean(name = "fanout_syncro_response_code")
	public FanoutExchange fanout_syncro_response_code() {
		return new FanoutExchange(FANOUT_EXCHANGE_SYNCRO_RESPONSE_CODE);
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
	public Queue SyncroRequestCode() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue SyncroResponseCode() {
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

	@Bean
	public Binding binding_syncro_request_code(FanoutExchange fanout_syncro_request_code, Queue SyncroRequestCode) {
		return BindingBuilder.bind(SyncroRequestCode).to(fanout_syncro_request_code);
	}

	@Bean
	public Binding binding_syncro_response_code(FanoutExchange fanout_syncro_response_code, Queue SyncroResponseCode) {
		return BindingBuilder.bind(SyncroResponseCode).to(fanout_syncro_response_code);
	}
}
