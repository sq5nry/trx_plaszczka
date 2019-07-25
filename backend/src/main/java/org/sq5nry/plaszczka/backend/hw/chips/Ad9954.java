package org.sq5nry.plaszczka.backend.hw.chips;

import com.pi4j.io.gpio.*;
import com.pi4j.io.spi.SpiDevice;
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

    private static final int CHANNEL = 0;
    private static final double RESOLUTION  = 4294967296.0d;
    private static final Pin RESET_P = RaspiPin.GPIO_00;
    private static final Pin UPDATE_P = RaspiPin.GPIO_02;

    private static final byte CFR_1[] = {0x00, 0x00, 0x00, 0x00};
    private static final byte CFR1Info[] = {0x00};
    private static final byte REGISTER_INFO[] = {0x04};
    public static final byte INIT_REG[] = {0x00};
    public static final byte INIT_DATA[] = {0x00, 0x00, 0x00, 0x00};


    private GpioPinDigitalOutput reset, update;
    private long refClk;


    public Ad9954(long refClk) throws ChipInitializationException {
        this.refClk = refClk;

        logger.debug("initializing GPIO");
        final GpioController gpio = GpioFactory.getInstance();
        reset = gpio.provisionDigitalOutputPin(RESET_P, "p", PinState.LOW);
        reset.setShutdownOptions(false, PinState.LOW);
        update = gpio.provisionDigitalOutputPin(UPDATE_P, "p", PinState.LOW);
        update.setShutdownOptions(false, PinState.LOW);
        logger.debug("initialized: RESET pin: {}", reset);
        logger.debug("initialized: UPDATE pin: {}", update);

        logger.debug("initializing wiringPiSPISetup, channel={}", CHANNEL);
        int fdSpi = Spi.wiringPiSPISetup(CHANNEL, SpiDevice.DEFAULT_SPI_SPEED);
        if (fdSpi <= -1) {
            logger.error("SPI bus setup failed for channel {}, FD={}", CHANNEL, fdSpi);
            throw new ChipInitializationException("SPI bus setup failed for channel " + CHANNEL + ", fd=" + fdSpi);
        } else {
            logger.debug("SPI initialized for channel {}, FD={}", CHANNEL, fdSpi);
        }
    }

    public void setFrequency(int freq) {
        long ftw = (long) (freq * RESOLUTION / refClk);
        logger.debug("setFrequency: freq={}, ftw={}", freq, ftw);
        byte ftws[] = {(byte) ((ftw >> 24) & 0xFF), (byte) ((ftw >> 16) & 0xFF), (byte) ((ftw >> 8) & 0xFF), (byte) (ftw & 0xFF)};

        writeRegister(CFR1Info, 4, CFR_1);
        writeRegister(REGISTER_INFO, 4, ftws);
        update();
    }

    //no PLL
    public void initialize() {
        reset();
        writeRegister(INIT_REG, 4, INIT_DATA);
    }

    private void reset() {
        logger.debug("device reset");
        reset.high();
        delay(1);
        reset.low();
    }

    private void update() {
        logger.debug("update frequency");
        update.high();
        update.low();
    }

    private void writeRegister(byte registerInfo[], int len, byte data[]) {
        if (logger.isDebugEnabled()) {
            logger.debug("writeRegister: reg={}, data={}", HexUtils.toHexString(registerInfo), HexUtils.toHexString(data));
        }
        wiringPiSPIDataRW(CHANNEL, registerInfo, 1);
        wiringPiSPIDataRW(CHANNEL, data, len);
    }
}
