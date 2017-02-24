package iot;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import iot.IotComponent;

@SpringBootApplication
public class Application implements CommandLineRunner {


    private static Logger logger = Logger.getLogger(Application.class);
    
    @Autowired
    private IotComponent iotc;

    public static void main(String... args) {
       SpringApplication.run(Application.class);
    }

    @Override
    public void run(String... strings) throws Exception {
        try{
            iotc.init();
            iotc.sendMessages();
        }catch(java.net.URISyntaxException uex){
            logger.error(uex.getMessage());
        }catch(IOException iex){
            logger.error(iex.getMessage());
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

}
