package org.example.accountservice.configurations;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ConfigLogger {

    @Autowired
    private Environment environment;

    @PostConstruct
    public void logProperties() {
        System.out.println("bank.gateway: " + environment.getProperty("bank.gateway"));
    }
}