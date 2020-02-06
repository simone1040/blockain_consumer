package com.simone.progetto.bean;
import com.simone.progetto.Receiver;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
	public static final String FANOUT_EXCHANGE = "FanoutExchange";

	@Bean
	public FanoutExchange fanout() {
		return new FanoutExchange(FANOUT_EXCHANGE);
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
	public Binding binding(FanoutExchange fanout, Queue TransactionQueue) {
		return BindingBuilder.bind(TransactionQueue).to(fanout);
	}


}
