package com.telehealth.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    // ─── Exchange Names ────────────────────────────────────────────────────────
    @Value("${telehealth.rabbitmq.exchanges.consultation}") private String consultationExchange;
    @Value("${telehealth.rabbitmq.exchanges.emergency}")    private String emergencyExchange;
    @Value("${telehealth.rabbitmq.exchanges.notification}") private String notificationExchange;

    // ─── Queue Names ───────────────────────────────────────────────────────────
    @Value("${telehealth.rabbitmq.queues.consultation-requested}") private String consultationRequestedQueue;
    @Value("${telehealth.rabbitmq.queues.consultation-assigned}")  private String consultationAssignedQueue;
    @Value("${telehealth.rabbitmq.queues.consultation-completed}") private String consultationCompletedQueue;
    @Value("${telehealth.rabbitmq.queues.emergency-triggered}")    private String emergencyTriggeredQueue;
    @Value("${telehealth.rabbitmq.queues.emergency-dispatched}")   private String emergencyDispatchedQueue;
    @Value("${telehealth.rabbitmq.queues.notification-patient}")   private String notificationPatientQueue;
    @Value("${telehealth.rabbitmq.queues.notification-doctor}")    private String notificationDoctorQueue;

    private static final String DLX_NAME   = "telehealth.dlx";
    private static final String DLQ_SUFFIX = ".dlq";

    // ─── Dead Letter Exchange ──────────────────────────────────────────────────

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_NAME, true, false);
    }

    // ─── Exchanges ─────────────────────────────────────────────────────────────

    @Bean
    public TopicExchange consultationTopicExchange() {
        return ExchangeBuilder.topicExchange(consultationExchange).durable(true).build();
    }

    @Bean
    public TopicExchange emergencyTopicExchange() {
        return ExchangeBuilder.topicExchange(emergencyExchange).durable(true).build();
    }

    @Bean
    public FanoutExchange notificationFanoutExchange() {
        return ExchangeBuilder.fanoutExchange(notificationExchange).durable(true).build();
    }

    // ─── Queues (with DLQ support) ─────────────────────────────────────────────

    @Bean public Queue consultationRequestedQ() { return durableQueue(consultationRequestedQueue); }
    @Bean public Queue consultationAssignedQ()  { return durableQueue(consultationAssignedQueue); }
    @Bean public Queue consultationCompletedQ() { return durableQueue(consultationCompletedQueue); }
    @Bean public Queue emergencyTriggeredQ()    { return durableQueue(emergencyTriggeredQueue); }
    @Bean public Queue emergencyDispatchedQ()   { return durableQueue(emergencyDispatchedQueue); }
    @Bean public Queue notificationPatientQ()   { return durableQueue(notificationPatientQueue); }
    @Bean public Queue notificationDoctorQ()    { return durableQueue(notificationDoctorQueue); }

    // ─── DLQs ─────────────────────────────────────────────────────────────────

    @Bean public Queue consultationRequestedDlq() { return new Queue(consultationRequestedQueue + DLQ_SUFFIX, true); }
    @Bean public Queue emergencyTriggeredDlq()    { return new Queue(emergencyTriggeredQueue + DLQ_SUFFIX, true); }

    @Bean public Binding consultationRequestedDlqBinding() {
        return BindingBuilder.bind(consultationRequestedDlq()).to(deadLetterExchange()).with(consultationRequestedQueue);
    }
    @Bean public Binding emergencyTriggeredDlqBinding() {
        return BindingBuilder.bind(emergencyTriggeredDlq()).to(deadLetterExchange()).with(emergencyTriggeredQueue);
    }

    // ─── Bindings ──────────────────────────────────────────────────────────────

    @Bean public Binding consultationRequestedBinding() {
        return BindingBuilder.bind(consultationRequestedQ()).to(consultationTopicExchange()).with("consultation.requested");
    }
    @Bean public Binding consultationAssignedBinding() {
        return BindingBuilder.bind(consultationAssignedQ()).to(consultationTopicExchange()).with("consultation.assigned");
    }
    @Bean public Binding consultationCompletedBinding() {
        return BindingBuilder.bind(consultationCompletedQ()).to(consultationTopicExchange()).with("consultation.completed");
    }
    @Bean public Binding emergencyTriggeredBinding() {
        return BindingBuilder.bind(emergencyTriggeredQ()).to(emergencyTopicExchange()).with("emergency.triggered");
    }
    @Bean public Binding emergencyDispatchedBinding() {
        return BindingBuilder.bind(emergencyDispatchedQ()).to(emergencyTopicExchange()).with("emergency.dispatched");
    }
    @Bean public Binding notificationPatientBinding() {
        return BindingBuilder.bind(notificationPatientQ()).to(notificationFanoutExchange());
    }
    @Bean public Binding notificationDoctorBinding() {
        return BindingBuilder.bind(notificationDoctorQ()).to(notificationFanoutExchange());
    }

    // ─── Serialization (JSON) ──────────────────────────────────────────────────

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonMessageConverter());
        template.setMandatory(true);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jacksonMessageConverter());
        factory.setDefaultRequeueRejected(false); // Send to DLQ on failure
        return factory;
    }

    // ─── Helper ────────────────────────────────────────────────────────────────

    private Queue durableQueue(String name) {
        return QueueBuilder.durable(name)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", name)
                .build();
    }
}
