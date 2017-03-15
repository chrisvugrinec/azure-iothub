import com.microsoft.azure.iothub.DeviceClient;
import com.microsoft.azure.iothub.IotHubClientProtocol;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class AzureIotHubClient {


   static String clientConnectionString;
   static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
   static DeviceClient client;


 
   protected void sendMessages(String clientConnectionString ) throws Exception {


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


    //  arg0 = connectionstring
   public static void main(String[] args){

      //clientConnectionString = args[0];
      clientConnectionString = System.getenv("IOTHUB");
      AzureIotHubClient t = new AzureIotHubClient();
      try{
        t.sendMessages(clientConnectionString);
      }catch(Exception ex){
        ex.printStackTrace();
      }
   }
}

