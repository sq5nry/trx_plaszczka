package org.sq5nry.plaszczka.backend.hw.chips;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericChip;

import java.io.IOException;

/**
 * The AD7991/AD7995/AD7999 are 12-/10-/8-bit, low power, successive approximation ADCs with an I2C®-compatible
 * interface. Each part operates  from a single 2.7 V to 5.5 V power supply and features a 1μs conversion time.
 * The track-and-hold amplifier allows each part to handle input frequencies of up to 14 MHz, and a multiplexer
 * allows taking samples from four channels. Each AD7991/AD7995/AD7999 provides a 2-wire serial interface compatible
 * with I2C interfaces. The AD7991 and AD7995 come in two versions and each version has an individual I2C address.
 * This allows  two of the same devices to be connected to the same I2C bus. Both versions support standard, fast,
 * and high speed I2C interface modes. The AD7999 comes in one version.The AD7991/AD7995/AD7999 normally remain in
 * a shutdown state, powering up only for conversions. The conversion process is controlled by a command mode, during
 * which each I2C read operation initiates a conversion and returns the result over the I2C bus.
 *
 * https://www.analog.com/en/products/ad7999.html
 */
public class Ad7999 extends GenericChip {
    private static final Logger logger = LoggerFactory.getLogger(Ad7999.class);

    private static final byte CONFIG_CH0 = 0x10;
    private static final byte CONFIG_REFSEL = 0x08;
    private static final byte CONFIG_FLTR = 0x04;

    private byte conversion[];

    public Ad7999(int address) {
        super(address);
        conversion = new byte[2];
    }

    //TODO workaround for failing initialization. Write(0) gives IOException: Communication error on send but i2cset is ok
    public GenericChip initialize() {
        try {
            return super.initialize();
        } catch (IOException e) {
            logger.warn("initialization failed, enforcing skip", e);
            forceInitialized();
        }
        return this;
    }

    public void configure() throws IOException {
        getDevice().write((byte) (CONFIG_CH0 | CONFIG_REFSEL | CONFIG_FLTR));
    }

    public byte getConversionResult() throws IOException {
        getDevice().read(conversion, 0, 2);
        if (logger.isDebugEnabled()) {
            logger.debug("getConversionResult: 0x{}.0x{}", String.format("%02X", conversion[0]), String.format("%02X", conversion[1]));
        }
        return (byte) (conversion[0]<<4 | conversion[1]>>4);
    }
}
