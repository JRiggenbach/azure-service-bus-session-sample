package com.example.servicebus.demo;

import java.time.Duration;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.core.amqp.AmqpRetryOptions;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;

@Configuration
public class ServiceBusConfiguration {   
    @Value("${azure.clientid}")
    private String azureClientId;

    @Value("${azure.clientsecret}")
    private String azureClientSecret;

    @Value("${azure.tenantid}")
    private String azureTenantId;

    @Value("${azure.servicebus.namespace}")
    protected String serviceBusNamespace;

    @Value("${azure.servicebus.topic}")
    protected String topicName;

    @Value("${azure.servicebus.subscription}")
    protected String subscriptionName;
    
    @Bean
    public ClientSecretCredential clientSecretCredential() {
        return new ClientSecretCredentialBuilder()
            .clientSecret(azureClientSecret)
            .clientId(azureClientId)
            .tenantId(azureTenantId)
            .build();
    }

    @Bean
    public ServiceBusClientBuilder serviceBusClientBuilder() {
        return new ServiceBusClientBuilder()
                .credential(serviceBusNamespace, clientSecretCredential());
    }

    @Bean
    public ServiceBusProcessorClient something() {
        return new ServiceBusClientBuilder()
            .credential(serviceBusNamespace, clientSecretCredential())
            .retryOptions(new AmqpRetryOptions()
                .setTryTimeout(Duration.ofSeconds(5)))
            .sessionProcessor()
            .topicName(topicName)
            .subscriptionName(subscriptionName)
            .receiveMode(ServiceBusReceiveMode.PEEK_LOCK)
            .maxConcurrentSessions(2)
            .processMessage(context -> {
                ServiceBusReceivedMessage message = context.getMessage();
                System.out.printf( new Date() + ": Processing message. Session: %s, Sequence #: %s. Contents: %s %n",
                    message.getSessionId(), message.getSequenceNumber(), message.getBody());
                context.complete();
            })
            .processError(ServiceBusErrorContext::getEntityPath)
            .buildProcessorClient();
    }
}