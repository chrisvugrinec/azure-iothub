package iot;

import com.microsoft.azure.iot.service.exceptions.IotHubException;
import com.microsoft.azure.iot.service.sdk.Device;
import com.microsoft.azure.iot.service.sdk.RegistryManager;
import org.apache.log4j.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;

import java.io.IOException;
import com.microsoft.azure.eventhubs.*;
import com.microsoft.azure.servicebus.*;

import com.microsoft.azure.iothub.DeviceClient;
import com.microsoft.azure.iothub.IotHubClientProtocol;
import com.microsoft.azure.iothub.Message;
import com.microsoft.azure.iothub.IotHubStatusCode;
import com.microsoft.azure.iothub.IotHubEventCallback;
import com.microsoft.azure.iothub.IotHubMessageResult;
import com.google.gson.Gson;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import org.springframework.context.annotation.PropertySource;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Component
@ConfigurationProperties()
public class IotComponent{

   @Value("${app.connection}")
   private String connectionString;

   @Value("${app.deviceId}")
   private String deviceId;

    Device device;

   private static Logger logger = Logger.getLogger(IotComponent.class);

   // EXAMPLE
   // private final String connectionString = "HostName=iotdemo-hub.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=XXXXX=";
   // private final String deviceId = "e5e7f870-8XXXXSECRETXXXXX82f07e31974d";

   private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
   private static DeviceClient client;

   void init() throws Exception{

       System.out.println("XXXX FROM PROP: "+connectionString);
       System.out.println("XXXX FROM PROP2: "+deviceId);


       RegistryManager registryManager = RegistryManager.createFromConnectionString(connectionString);


       device = Device.createFromId(deviceId, null, null);

       System.out.println("XXXX3: "+deviceId);
       System.out.println("XXXX4: "+connectionString);

       try {
       device = registryManager.addDevice(device);
     } catch (IotHubException iote) {
       try {
         device = registryManager.getDevice(deviceId);
       } catch (IotHubException iotf) {
         iotf.printStackTrace();
       }
     }
     System.out.println("Device ID: " + device.getDeviceId());
     System.out.println("Device key: " + device.getPrimaryKey());
   }


  private class MessageSender implements Runnable {

      public volatile boolean stopThread = false;

    public void run()  {
      try {
        double avgWindSpeed = 10; // m/s
        Random rand = new Random();

        while (!stopThread) {
          double currentWindSpeed = avgWindSpeed + rand.nextDouble() * 4 - 2;
          TelemetryDataPoint telemetryDataPoint = new TelemetryDataPoint();
          telemetryDataPoint.deviceId = deviceId;
          telemetryDataPoint.windSpeed = currentWindSpeed;

          String msgStr = telemetryDataPoint.serialize();
          Message msg = new Message(msgStr);
          System.out.println("Sending: " + msgStr);

          Object lockobj = new Object();
          EventCallback callback = new EventCallback();
          client.sendEventAsync(msg, callback, lockobj);

          synchronized (lockobj) {
            lockobj.wait();
          }
          Thread.sleep(1000);
        }
      } catch (InterruptedException e) {
        System.out.println("Finished.");
      }
    }
  }
  
   protected void sendMessages() throws Exception {

     client = new DeviceClient(connectionString, protocol);
     client.open();

     MessageSender sender = new MessageSender();

     ExecutorService executor = Executors.newFixedThreadPool(1);
     executor.execute(sender);

     System.out.println("Press ENTER to exit.");
     System.in.read();
     executor.shutdownNow();
     client.close();
   }


   private class TelemetryDataPoint {
     public String deviceId;
     public double windSpeed;

     public String serialize() {
       Gson gson = new Gson();
       System.out.println("DeviceID: "+deviceId);
       System.out.println(gson.toJson(this));
       return gson.toJson(this);
     }
   }

   private class EventCallback implements IotHubEventCallback
   {
     public void execute(IotHubStatusCode status, Object context) {
       System.out.println("IoT Hub responded to message with status: " + status.name());

       if (context != null) {
         synchronized (context) {
           context.notify();
         }
       }
     }
   }
}
