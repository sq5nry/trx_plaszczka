package org.sq5nry.plaszczka.backend;

import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.pi4j.io.gpio.RaspiPin.GPIO_00;
import static com.pi4j.io.gpio.RaspiPin.GPIO_02;

/**
 * A rough test for Bananapi M3 GPIO for DDS unit, with PI4Jv1.2 and BPI-WiringPi2
 *
 * sudo $JAVA_HOME/bin/java -classpath target/classes:target/slf4j-api-1.7.26.jar:target/log4j-to-slf4j-2.11.2.jar:target/log4j-api-2.11.2.jar:target/pi4j-core-1.2.jar:target/slf4j-simple-1.6.1.jar -Dorg.slf4j.simpleLogger.defaultLogLevel=debug  org.sq5nry.plaszczka.backend.GpioTest
 */
public class GpioTest {
    private static final Logger logger = LoggerFactory.getLogger(GpioTest.class);

    public static void main(String[] args) throws Exception {
        I2CBus i2c = I2CFactory.getInstance(I2CBus.BUS_2);
        logger.info("i2c=" + i2c);
        I2CDevice dev = i2c.getDevice(0x3f);
        logger.info("I2CDevice=" + dev);
        dev.write((byte) 0xff);
        Thread.sleep(1000);
        dev.write((byte) 0x00);
        logger.info("I2CDevice test done");

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
