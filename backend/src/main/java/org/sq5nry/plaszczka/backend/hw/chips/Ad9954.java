package org.sq5nry.plaszczka.backend.hw.chips;

import com.pi4j.io.gpio.*;
import com.pi4j.wiringpi.Spi;
import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.pi4j.wiringpi.Gpio.delay;
import static com.pi4j.wiringpi.Spi.wiringPiSPIDataRW;

/**
 * The AD9954 is a direct digital synthesizer (DDS) that uses advanced technology, coupled with an internal high speed,
 * high performance DAC to form a complete, digitally programmable, high frequency synthesizer capable of generating
 * a frequency-agile analog output sinusoidal waveform at up to 160 MHz. The AD9954 enables fast frequency hopping
 * coupled with fine tuning of both frequency (0.01 Hz or better) and phase (0.022° granularity).
 *
 * The AD9954 is programmed via a high speed serial I/O port. The device includes static RAM to support flexible
 * frequency sweep capability in several modes, plus a user-defined linear sweep mode of operation. Also included is
 * an on-chip high speed comparator for applications requiring a square wave output. An on-chip oscillator and PLL
 * circuitry provide users with multiple approaches to generate the device’s system clock.
 *
 * https://www.analog.com/en/products/ad9954.html
 *
 * Basic implementation with no PLL, just frequency setting.
 */
public class Ad9954 {
    private static final Logger logger = LoggerFactory.getLogger(Ad9954.class);

    public static final int SPI_SPEED = 5000000;
    private static final int SPI_CHANNEL = 0;

    private static final byte REG_CFR1Info[] = {0x00};
    private static final byte REG_FREQ[] = {0x04};
    private static final byte DATA_CFR1[] = { 0x00, 0x00, 0x00, 0x00 };

    private static final double RESOLUTION  = 4294967296.0d;
    private long refClk;

    private static final Pin RESET_P = RaspiPin.GPIO_00;
    private static final Pin UPDATE_P = RaspiPin.GPIO_02;

    private GpioPinDigitalOutput reset, update;

    public Ad9954(long refClk) throws ChipInitializationException {
        this.refClk = refClk;

        logger.debug("initializing GPIO");
        final GpioController gpio = GpioFactory.getInstance();
        logger.debug("GPIO initialized");

        reset = gpio.provisionDigitalOutputPin(RESET_P, "reset", PinState.LOW);
        reset.setShutdownOptions(false, PinState.LOW);
        logger.debug("initialized: RESET pin: {}", reset);

        update = gpio.provisionDigitalOutputPin(UPDATE_P, "update", PinState.LOW);
        update.setShutdownOptions(false, PinState.LOW);
        logger.debug("initialized: UPDATE pin: {}", update);

        logger.debug("initializing wiringPiSPISetup, channel={}", SPI_CHANNEL);
        int fdSpi = Spi.wiringPiSPISetup(SPI_CHANNEL, SPI_SPEED);
        if (fdSpi <= -1) {
            logger.error("SPI bus setup failed for channel {}, FD={}", SPI_CHANNEL, fdSpi);
            throw new ChipInitializationException("SPI bus setup failed for channel " + SPI_CHANNEL + ", fd=" + fdSpi);
        } else {
            logger.debug("SPI initialized for channel {}, FD={}", SPI_CHANNEL, fdSpi);
        }
    }

    public void setFrequency(int freq) {
        long ftw = (long) (freq * RESOLUTION / refClk);
        logger.debug("setFrequency: freq={}, ftw={}", freq, ftw);
        byte ftws[] = {(byte) ((ftw >> 24) & 0xFF), (byte) ((ftw >> 16) & 0xFF), (byte) ((ftw >> 8) & 0xFF), (byte) (ftw & 0xFF)};

        writeRegister(REG_CFR1Info.clone(), DATA_CFR1.clone()); //TODO why?
        writeRegister(REG_FREQ.clone(), ftws);
        update();
    }

    //no PLL
    public void initialize() {
        reset();
        writeRegister(REG_CFR1Info.clone(), DATA_CFR1.clone());
    }

    private void reset() {
        logger.debug("device reset");
        reset.high();
        delay(1);
        reset.low();
    }

    private void update() {
        update.high();
        update.low();
    }

    private static void writeRegister(byte registerInfo[], byte data[]) {
        if (logger.isDebugEnabled()) {
            logger.debug("writeRegister: @{}={}", HexUtils.toHexString(registerInfo), HexUtils.toHexString(data));
        }
        wiringPiSPIDataRW(SPI_CHANNEL, registerInfo, 1);
        wiringPiSPIDataRW(SPI_CHANNEL, data, data.length);
    }

    @Override
    public String toString() {
        return "AD9954{" +
                "refClk=" + refClk +
                ", reset=" + reset +
                ", update=" + update +
                '}';
    }
}
