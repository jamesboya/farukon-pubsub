package com.farukon.resource.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@Controller
public class ResourceAMQController {
    @Autowired
    ConfigurableApplicationContext applicationContext;

    @RequestMapping(value = "resources/signal", method = RequestMethod.POST)
    public ResponseEntity<Void> sendSignal(@RequestParam("url") String url) {
        MessageCreator messageCreator = new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(url);
            }
        };

        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class);
        jmsTemplate.send("resource-queue", messageCreator);

        return ResponseEntity.ok().build();
    }
}
