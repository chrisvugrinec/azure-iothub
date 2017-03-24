import com.microsoft.azure.iothub.DeviceClient;
import com.microsoft.azure.iothub.IotHubClientProtocol;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class AzureIotHubClient {


   static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
   static DeviceClient client;


 
   protected void sendMessages(String clientConnectionString, String deviceName ) throws Exception {


     client = new DeviceClient(clientConnectionString, protocol);
     client.open();
     new MessageSender().send(deviceName);
     client.close();
   }


    //  arg0 = connectionstring
   public static void main(String[] args){

      AzureIotHubClient t = new AzureIotHubClient();
      try{
        //t.sendMessages(System.getenv("IOTHUB"),System.getenv("DEVICE") );
        t.sendMessages(args[0],args[1] );
      }catch(Exception ex){
        ex.printStackTrace();
      }
   }
}


