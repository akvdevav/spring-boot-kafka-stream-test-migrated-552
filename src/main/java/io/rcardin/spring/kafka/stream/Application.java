package io.rcardin.spring.rabbitmq.stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public Queue wordsQueue() {
		return new Queue("words", true);
	}

	@Bean
	public Queue wordCountersQueue() {
		return new Queue("word-counters", true);
	}

	@RabbitListener(queuesToDeclare = @Queue(name = "words"))
	public void processWord(String word) {
		// Simulate word counting logic
		AtomicLong counter = new AtomicLong(0);
		String processedWord = word.toLowerCase().trim();
		if (!processedWord.isEmpty()) {
			counter.incrementAndGet();
			// Send result to word-counters queue
			rabbitTemplate.convertAndSend("word-counters", processedWord + ":" + counter.get());
		}
	}

	// Placeholder for RabbitTemplate injection
	private RabbitTemplate rabbitTemplate;

	public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}
}

@Configuration
class RabbitConfig {

	@Bean
	public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory) {
		return new RabbitTemplate(connectionFactory);
	}
}