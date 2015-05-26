package com.farukon.resource.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.annotation.JmsListener;

public class ResourceConsumer {
    @Autowired
    ConfigurableApplicationContext context;

    @JmsListener(destination = "resource-queue", containerFactory = "resourceMQContainerFactory")
    public void receiveMessage(String url) {
        System.out.println("Received: " + url);
    }
}
