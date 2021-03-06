import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;



public class IotUtil {

    private static GpioPinDigitalOutput pin;

    private static GpioController gpio = GpioFactory.getInstance();

    static double getIOTMetric() throws InterruptedException{

        int count = 0;

        //  Initialize pin 07 on Raspberry PI
        if (pin == null) {
            pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "lightsensor", PinState.LOW);
        } else {

            pin.setMode(PinMode.DIGITAL_OUTPUT);
            pin.setState(PinState.LOW);
        }
        //  Wait 1/2 sec
        Thread.sleep(500);

        // Make Pin 7 now an InputPIn now (Difference between in and out is difference between sensor and capacitator)
        pin.setMode(PinMode.DIGITAL_INPUT);

        // When Low
        while (pin.isLow()) {
            count++;
        }
        return reformatIotMeasure(count);
    }

    //  Should return 8,9,10,11 or 12
    private static double reformatIotMeasure(int iotMeasure){
        int minIn = 1800, maxIn = 5000, minOut = 8, maxOut = 12;
        int rangeIn = maxIn - minIn, rangeOut = maxOut - minOut;

        int in = Math.max(minIn, Math.min(maxIn, iotMeasure));
        return ((in - minIn) / (double) rangeIn) * rangeOut + minOut;
    }
}
