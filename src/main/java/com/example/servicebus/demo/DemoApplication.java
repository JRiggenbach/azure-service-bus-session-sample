package com.example.servicebus.demo;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.azure.messaging.servicebus.ServiceBusProcessorClient;

@SpringBootApplication
@ComponentScan("com.example")
public class DemoApplication {

	@Autowired
	ServiceBusProcessorClient processorClient;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@PostConstruct
    void started() {
        processorClient.start();
    }
}
