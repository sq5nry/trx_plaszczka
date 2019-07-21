package org.sq5nry.plaszczka.backend.hw.chips;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;
import com.pi4j.wiringpi.Spi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.pi4j.wiringpi.Gpio.delay;
import static com.pi4j.wiringpi.Spi.wiringPiSPIDataRW;

public class Ad9954 {
    private static final Logger logger = LoggerFactory.getLogger(Ad9954.class);

    //private SpiDevice dev;

    private static final int CHANNEL = 1;
    private static final double RESOLUTION  = 4294967296.0d;
    private int resetPin = 0;
    private int updatePin = 2;
    private long refClk;

    public Ad9954(long refClk) {
        this.refClk = refClk;

        logger.debug("initializing wiringPiSPISetup, channel={}", CHANNEL);
        int fdSpi = Spi.wiringPiSPISetup(CHANNEL, 5000000);
        if (fdSpi <= -1) {
            logger.error("spi setup failed: {}", fdSpi);
            return;
        } else {
            logger.debug("spi initialized: {}", fdSpi);
        }

        logger.debug("initializing wiringPiSetup");
        int fdGpio = Gpio.wiringPiSetup();
        if (fdGpio <= -1) {
            logger.error("gpio setup failed: {}", fdGpio);
            return;
        } else {
            logger.debug("gpio initialized: {}", fdGpio);
        }

        GpioUtil.export(resetPin, GpioUtil.DIRECTION_OUT);
        Gpio.pinMode (resetPin, Gpio.OUTPUT) ;

        GpioUtil.export(updatePin, GpioUtil.DIRECTION_OUT);
        Gpio.pinMode (updatePin, Gpio.OUTPUT) ;

        Gpio.digitalWrite(resetPin, Gpio.LOW);
        Gpio.digitalWrite(updatePin, Gpio.LOW);
    }

    public void setFrequency(int freq) {
        long ftw = (long) (freq * RESOLUTION / refClk);
        logger.debug("setFrequency: freq={}, ftw={}", freq, ftw);
        byte ftws[] = {(byte) ((ftw >> 24) & 0xFF), (byte) ((ftw >> 16) & 0xFF), (byte) ((ftw >> 8) & 0xFF), (byte) (ftw & 0xFF)};

        byte registerInfo[] = {0x04};
        byte CFR1[] = { 0x00, 0x00, 0x00, 0x00 };
        byte CFR1Info[] = {0x00};

        writeRegister(CFR1Info, 4, CFR1);
        writeRegister(registerInfo, 4, ftws);
        update();
    }

    //no PLL
    public void initialize() {
        reset();
        byte registerInfo[] = {0x00};
        byte data[] = {0x00, 0x00, 0x00, 0x00};
        writeRegister(registerInfo, 4, data);
    }

    private void reset() {
        logger.debug("reset");
        Gpio.digitalWrite(resetPin, Gpio.HIGH);
        delay(1);
        Gpio.digitalWrite(resetPin, Gpio.LOW);
    }

    private void update() {
        logger.debug("update");
        Gpio.digitalWrite(updatePin, Gpio.HIGH);
        Gpio.digitalWrite(updatePin, Gpio.LOW);
    }

    private void writeRegister(byte registerInfo[], int len, byte data[]) {
        logger.debug("writeRegister");
        wiringPiSPIDataRW(CHANNEL, registerInfo, 1);
        wiringPiSPIDataRW(CHANNEL, data, len);
    }
}


//        dev = SpiFactory.getInstance(SpiChannel.CS1,
//                SpiDevice.DEFAULT_SPI_SPEED, // default spi speed 1 MHz //TODO 5MHz?
//                SpiDevice.DEFAULT_SPI_MODE); // default spi mode 0

//        final GpioController gpio = GpioFactory.getInstance();

// by default we will use gpio pin #01; however, if an argument
// has been provided, then lookup the pin by address
//        Pin pin = BananaPiPin.GPIO_01; //
//        final GpioPinDigitalOutput output = gpio.provisionDigitalOutputPin(pin, "My Output", PinState.HIGH);