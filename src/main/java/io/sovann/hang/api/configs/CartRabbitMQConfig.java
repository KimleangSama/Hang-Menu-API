package io.sovann.hang.api.configs;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CartRabbitMQConfig {

    @Value("${rabbitmq.queue.name}")
    public final static String QUEUE_NAME = "cart_queue";
    @Value("${rabbitmq.exchange.name}")
    public final static String EXCHANGE_NAME = "cart_exchange";

    @Bean
    public Queue queue() {
        return new Queue(CartRabbitMQConfig.QUEUE_NAME, false);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(CartRabbitMQConfig.EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("routing.key.#");
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();

        // Explicitly allow specific packages or classes
        typeMapper.setTrustedPackages("io.sovann.hang.api.features");

        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
