package iot;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;


public class IotUtil {

    GpioController gpio = GpioFactory.getInstance();
    static GpioPinDigitalOutput pin;

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
}
