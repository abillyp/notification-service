package com.notification.notification_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;


@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.queues.email}")
    private String queueEmail;

    @Value("${rabbitmq.queues.sms}")
    private String queueSms;

    @Value("${rabbitmq.routing-keys.email}")
    private String routingKeyEmail;

    @Value("${rabbitmq.routing-keys.sms}")
    private String routingKeySms;


    @Bean
    public DirectExchange notificationsExchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(queueEmail)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "notifications.dlq")
                .build();
    }

    @Bean
    public Queue smsQueue() {
        return QueueBuilder.durable(queueSms)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "notifications.dlq")
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("notifications.dlq")
                .build();
    }

    @Bean
    public Binding createBindingEmail() {
        return BindingBuilder.bind(emailQueue()).to(notificationsExchange()).with(routingKeyEmail);
    }

    @Bean
    public Binding createBindingSms() {
        return BindingBuilder.bind(smsQueue()).to(notificationsExchange()).with(routingKeySms);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public ApplicationRunner rabbitInitializer(RabbitAdmin rabbitAdmin) {
        return args -> rabbitAdmin.initialize();
    }

}
