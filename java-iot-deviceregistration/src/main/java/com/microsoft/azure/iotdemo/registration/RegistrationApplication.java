package com.microsoft.azure.iotdemo.registration;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RegistrationApplication {

	
    @Bean
    public RegistrationApplication repository() throws Exception {
        return null;
        //return new DeviceRepository(hostName, keyName, keyValue);
    }

    public static void main(String[] args)  {
        new SpringApplicationBuilder(RegistrationApplication.class).web(true).run(args);
    }
}