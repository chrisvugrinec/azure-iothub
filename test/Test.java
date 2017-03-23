import com.google.gson.Gson;
//import com.microsoft.azure.iot.service.sdk.Device;
import com.microsoft.azure.iothub.DeviceClient;
import com.microsoft.azure.iothub.IotHubClientProtocol;
import com.microsoft.azure.iothub.IotHubEventCallback;
import com.microsoft.azure.iothub.IotHubStatusCode;
import com.microsoft.azure.iothub.Message;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;


import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Test {


   private static String clientConnectionString ="HostName=iotdemo-hub.azure-devices.net;DeviceId=chrisjedevice1;SharedAccessKey=U7Etxx4pUie/p08DR4oc6A==";

   private static  String deviceId="chrisjedevice1";

   //Device device;

   private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
   private static DeviceClient client;


  private class MessageSender implements Runnable {

      public volatile boolean stopThread = false;

    public void run()  {
      try {
        double avgWindSpeed = 10; // m/s
        Random rand = new Random();

        while (!stopThread) {
          int currentWindSpeed = getMetric();
          //int currentWindSpeed = 12232;
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
          Thread.sleep(2000);
        }
      } catch (InterruptedException e) {
        System.out.println("Finished.");
      }
    }
  }


  private static GpioPinDigitalOutput pin;

  private int getMetric(){
      int count = 0;
      if (pin == null ){
          GpioController gpio = GpioFactory.getInstance();
          pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07,"lightsensor",PinState.LOW);
      }
      while (pin.isLow()){
          count++;
      }

     return count;
     
  }
  
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
   

   public static void main(String[] args){
      Test t = new Test();
      try{
        t.sendMessages(clientConnectionString,deviceId);
      }catch(Exception ex){
        ex.printStackTrace();
      }
   }
}

