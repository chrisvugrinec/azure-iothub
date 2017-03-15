import com.microsoft.azure.iothub.IotHubEventCallback;
import com.microsoft.azure.iothub.IotHubStatusCode;
import com.microsoft.azure.iothub.Message;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.lang.Math;

class MessageSender implements Runnable {

    protected volatile boolean stopThread = false;

    public void run()  {
        double maxval = 12.0;
        double minval = 8.0;

        try {

            while (!stopThread) {
                //double currentWindSpeed = IotUtil.getIOTMetric();

                double currentWindSpeed = (Math.random() * (maxval- minval)) + minval;

                //  Generate data object
                //{"deviceId": id, "windSpeed": currWindSpeed,
                // "powerOutput": currPowerOutput, "payerId": "chris@microsoft", "eventDate": now}
                TelemetryDataPoint telemetryDataPoint = new TelemetryDataPoint();
                telemetryDataPoint.windSpeed = currentWindSpeed;
                telemetryDataPoint.powerOutput  = 800;
                telemetryDataPoint.payerId = "chris@microsoft";
                telemetryDataPoint.eventDate = Instant.now().toString();
                telemetryDataPoint.eventType = "telemetry";

                String msgStr = telemetryDataPoint.serialize();
                Message msg = new Message(msgStr);
                System.out.println("Sending: " + msgStr);

                Object lockobj = new Object();
                EventCallback callback = new EventCallback();
                AzureIotHubClient.client.sendEventAsync(msg, callback, lockobj);

                synchronized (lockobj) {
                    lockobj.wait();
                }
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            System.out.println("Finished.");
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



