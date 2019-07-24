package org.sq5nry.plaszczka.backend;

import com.pi4j.io.gpio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.pi4j.io.gpio.RaspiPin.GPIO_00;
import static com.pi4j.io.gpio.RaspiPin.GPIO_02;

/**
 * A rough test for Bananapi M3 GPIO for DDS unit, with PI4Jv1.2 and BPI-WiringPi2
 *
 * sudo $JAVA_HOME/bin/java -classpath target/classes:target/slf4j-api-1.7.26.jar:target/log4j-to-slf4j-2.11.2.jar:target/log4j-api-2.11.2.jar:target/pi4j-core-1.2.jar:target/slf4j-simple-1.6.1.jar -Dorg.slf4j.simpleLogger.defaultLogLevel=debug  org.sq5nry.plaszczka.backend.SpiTest
 */
public class GpioTest {
    private static final Logger logger = LoggerFactory.getLogger(GpioTest.class);

    public static void main(String[] args) throws Exception {
        //PlatformManager.setPlatform(Platform.BANANAPI);
        //GpioFactory.setDefaultProvider(new BananaPiGpioProvider());

        Pin[] pins = new Pin[]{GPIO_00, GPIO_02};
        GpioPinDigitalOutput[] outs = new GpioPinDigitalOutput[pins.length];

        final GpioController gpio = GpioFactory.getInstance();
        for(int ct=0; ct<pins.length; ct++) {
            logger.info("prov p" + ct);
            outs[ct] = gpio.provisionDigitalOutputPin(pins[ct], "p" + ct, PinState.LOW);
            outs[ct].setShutdownOptions(false, PinState.LOW);
        }

        while(true)
        for(GpioPinDigitalOutput out: outs) {
                logger.info("s p" + out + ", " + out.getName() + ", " + out.getProvider());
                try {
                    out.high();
                    Thread.sleep(1000);
                    logger.info("START p" + out);
                    out.low();
                    Thread.sleep(1000);
                    logger.info("MID p" + out);
                    out.high();
                    logger.info("END p" + out);
                } catch (Exception e) {
                    logger.warn("e p" + out, e);
                }
        }
    }
}
