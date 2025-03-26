package io.sovann.hang.api.configs;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

@Configuration
public class RabbitMQConfig {
    @Value("${rabbitmq.exchange}")
    private String exchange;
    @Value("${rabbitmq.queue}")
    private String queue;
    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    public static final String BATCH_MENU_QUEUE = "batch-menu-queue";
    public static final String BATCH_MENU_EXCHANGE = "batch-menu-exchange";
    public static final String NOTIFICATION_QUEUE = "notification-queue";
    public static final String NOTIFICATION_EXCHANGE = "notification-exchange";
    public static final String NOTIFICATION_ROUTING_KEY = "notification-routing-key";

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Queue orderQueue() {
        return new Queue(queue, true);
    }

    @Bean
    public Binding binding(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(routingKey);
    }

    @Bean
    public Queue batchMenuQueue() {
        return new Queue(BATCH_MENU_QUEUE, true);
    }

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue).to(notificationExchange).with(NOTIFICATION_ROUTING_KEY);
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
