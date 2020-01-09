package rabbitMQ;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

	public static final String EXCHANGE_NAME = "tips_tx";
	public static final String DEFAULT_PARSING_QUEUE = "default_parser_q";
	public static final String ROUTING_KEY = "tips";
	
	@Bean
	public TopicExchange tipsExchange() {
		return new TopicExchange(EXCHANGE_NAME);
	}
	
	@Bean
	public Queue defaultParsingQueue() {
		return new Queue(DEFAULT_PARSING_QUEUE);
	}
	
	@Bean
	public Binding queueToExchangeBinding() {
		return BindingBuilder.bind(defaultParsingQueue()).to(tipsExchange()).with(ROUTING_KEY);
	}
}
