package iot;

import com.microsoft.azure.iot.service.exceptions.IotHubException;
import com.microsoft.azure.iot.service.sdk.Device;
import com.microsoft.azure.iot.service.sdk.RegistryManager;
import com.microsoft.azure.iothub.DeviceClient;
import com.microsoft.azure.iothub.IotHubClientProtocol;
import com.microsoft.azure.servicebus.MessageSender;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@ConfigurationProperties()
class IotComponent {


    @Value("${app.connection}")
    private String connectionString;

    @Value("${app.clientconnection}")
    private String clientConnectionString;


    @Value("${app.deviceId}")
    static String deviceId;

    GpioController gpio = GpioFactory.getInstance();
    private static GpioPinDigitalOutput pin;

    private static Logger logger = Logger.getLogger(IotComponent.class);

    private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
    protected static DeviceClient client;

    private void registerDevice() throws Exception {

        RegistryManager registryManager = RegistryManager.createFromConnectionString(connectionString);
        Device device;

        device = Device.createFromId(deviceId, null, null);
        try {
            device = registryManager.addDevice(device);
        } catch (IotHubException iote) {
            try {
                device = registryManager.getDevice(deviceId);
            } catch (IotHubException iotf) {
                logger.error(iotf.getStackTrace());
            }
        }
    }


    int getIOTMetric() throws InterruptedException {
        int count = 0;

        //  Initialize pin 07 on Raspberry PI
        if (pin == null) {
            pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "lightsensor", PinState.LOW);
        } else {
            pin.setMode(PinMode.DIGITAL_OUTPUT);
        }
        //  Wait 1/2 sec
        Thread.sleep(500);

        // Make Pin 7 now an InputPIn now (Difference between in and out is difference between sensor and capacitator)
        pin.setMode(PinMode.DIGITAL_INPUT);

        // When Low
        while (pin.isLow()) {
            count++;
        }

        return count;

    }

    void sendMessages() throws Exception {

        client = new DeviceClient(clientConnectionString, protocol);
        client.open();
        IotMessageSender sender = new IotMessageSender();

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(sender);
        System.out.println("Press ENTER to exit.");
        System.in.read();
        executor.shutdownNow();
        client.close();
    }

}
