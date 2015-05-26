package com.farukon;

import com.farukon.resource.jms.ResourceConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@SpringBootApplication
@EnableJms
public class App {
    @Bean
    JmsListenerContainerFactory<?> resourceMQContainerFactory(ConnectionFactory connectionFactory) {
        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        return factory;
    }

    @Bean
    public ResourceConsumer resourceConsumer() {
        return new ResourceConsumer();
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(App.class);
    }
}

