package org.sq5nry.plaszczka.backend.hw.chips;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sq5nry.plaszczka.backend.hw.common.ChipInitializationException;
import org.sq5nry.plaszczka.backend.hw.common.GenericChip;
import org.sq5nry.plaszczka.backend.hw.common.GenericSpiChip;

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
public class Ad9954 extends GenericSpiChip {
    private static final Logger logger = LoggerFactory.getLogger(Ad9954.class);

    private static final byte REG_CFR1Info[] = {0x00};
    private static final byte REG_FREQ[] = {0x04};
    private static final byte DATA_CFR1[] = { 0x00, 0x00, 0x00, 0x00 };

    private static final double RESOLUTION  = 4294967296.0d;
    private long refClk;

    private static final Pin RESET_P = RaspiPin.GPIO_00;
    private static final Pin UPDATE_P = RaspiPin.GPIO_02;

    private GpioPinDigitalOutput reset, update;

    public Ad9954(long refClk) throws ChipInitializationException {
        super(-1);  //assigned by SPI conf
        this.refClk = refClk;
    }

    @Override
    public GenericChip initialize() {
        initGpio();
        reset();
        writeRegister(REG_CFR1Info.clone(), DATA_CFR1.clone());
        return this;
    }

    @Override
    public boolean needsGpio() {
        return true;
    }

    public void setFrequency(int freq) {
        long ftw = (long) (freq * RESOLUTION / refClk);
        logger.debug("setFrequency: freq={}, ftw={}", freq, ftw);
        byte ftws[] = {(byte) ((ftw >> 24) & 0xFF), (byte) ((ftw >> 16) & 0xFF), (byte) ((ftw >> 8) & 0xFF), (byte) (ftw & 0xFF)};

        writeRegister(REG_CFR1Info.clone(), DATA_CFR1.clone()); //TODO why?
        writeRegister(REG_FREQ.clone(), ftws);
        update();
    }

    private void initGpio() {
        logger.debug("initializing GPIO");

        reset = getGpioController().provisionDigitalOutputPin(RESET_P, "reset", PinState.LOW);
        reset.setShutdownOptions(false, PinState.LOW);
        logger.debug("initialized: RESET pin: {}", reset);

        update = getGpioController().provisionDigitalOutputPin(UPDATE_P, "update", PinState.LOW);
        update.setShutdownOptions(false, PinState.LOW);
        logger.debug("initialized: UPDATE pin: {}", update);
    }

    private void reset() {
        logger.debug("device reset");
        reset.high();
        gpioDelay(1);
        reset.low();
    }

    private void update() {
        logger.debug("FQ update");
        update.high();
        update.low();
    }

    private void writeRegister(byte registerInfo[], byte data[]) {
        if (logger.isDebugEnabled()) {
            logger.debug("writeRegister: @0x{}=0x{}", HexUtils.toHexString(registerInfo), HexUtils.toHexString(data));
        }
        writeSpi(registerInfo, 1);
        writeSpi(data, data.length);
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
