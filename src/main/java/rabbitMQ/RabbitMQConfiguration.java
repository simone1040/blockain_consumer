package rabbitMQ;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

	public static final String DEFAULT_PARSING_QUEUE = "default_transaction_q";

	@Bean
	public Queue defaultParsingQueue() {
		return new Queue(DEFAULT_PARSING_QUEUE);
	}

}
