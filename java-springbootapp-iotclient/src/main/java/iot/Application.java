package iot;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

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
            //iotc.registerDevice();
            iotc.sendMessages();
        }catch(java.net.URISyntaxException uex){
            logger.error(uex.getMessage());
        }catch(IOException iex){
            logger.error(iex.getMessage());
        }catch(Exception ex){
            System.out.println("Exception occured:");
            ex.printStackTrace();
        }
    }

}
