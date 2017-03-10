package iot;


import com.microsoft.azure.iothub.IotHubEventCallback;
import com.microsoft.azure.iothub.IotHubStatusCode;
import com.microsoft.azure.iothub.Message;

import org.springframework.beans.factory.annotation.Autowired;

public class IotMessageSender implements Runnable {

    @Autowired
    private IotComponent iotc;

    @Autowired
    private IotUtil iotutil;

    public volatile boolean stopThread = false;

    public void run()  {
        try {

            while (!stopThread) {

                int currentWindSpeed = iotutil.getIOTMetric();

                TelemetryDataPoint telemetryDataPoint = new TelemetryDataPoint();
                telemetryDataPoint.deviceId = iotc.deviceId;
                telemetryDataPoint.windSpeed = currentWindSpeed;

                String msgStr = telemetryDataPoint.serialize();
                Message msg = new Message(msgStr);

                Object lockobj = new Object();
                EventCallback callback = new EventCallback();
                iotc.client.sendEventAsync(msg, callback, lockobj);

                synchronized (lockobj) {
                    lockobj.wait();
                }
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            System.out.println("Finished.");
        }
    }

    class EventCallback implements IotHubEventCallback {
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
