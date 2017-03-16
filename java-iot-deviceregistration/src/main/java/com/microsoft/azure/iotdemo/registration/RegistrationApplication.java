package com.microsoft.azure.iotdemo.registration;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RegistrationApplication {

	
    public static void main(String[] args)  {
        new SpringApplicationBuilder(RegistrationApplication.class).web(true).run(args);
    }
}