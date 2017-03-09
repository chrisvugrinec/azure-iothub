import com.microsoft.azure.iothub.DeviceClient;
import com.microsoft.azure.iothub.IotHubClientProtocol;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class AzureIotHubClient {


   static String clientConnectionString;
   static  String deviceId;
   static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
   static DeviceClient client;


 
   protected void sendMessages(String clientConnectionString, String deviceId ) throws Exception {


     client = new DeviceClient(clientConnectionString, protocol);
     client.open();
     MessageSender sender = new MessageSender();

     ExecutorService executor = Executors.newFixedThreadPool(1);
     executor.execute(sender);

     System.out.println("Press ENTER to exit.");
     System.in.read();
     executor.shutdownNow();
     client.close();
   }



   public static void main(String[] args){
      clientConnectionString = args[0];
      deviceId = args[1];
      AzureIotHubClient t = new AzureIotHubClient();
      try{
        t.sendMessages(clientConnectionString,deviceId);
      }catch(Exception ex){
        ex.printStackTrace();
      }
   }
}

