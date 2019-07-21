package org.sq5nry.plaszczka.backend.hw.i2c.chips;

import java.io.IOException;

/**
 * 16-bit I/O expander for the two-line bidirectional bus (I2C) is designed for 2.5-V to 5.5-V VCC	operation.
 * The PCF8575 device provides general-purpose remote I/O expansion for most microcontroller families
 * by way of the I2C interface [serial clock (SCL), serial data (SDA)].
 * The device features a 16-bit quasi-bidirectional input/output (I/O) port (P07–P00, P17–P10), including latched
 * outputs with high-current drive capability for directly driving LEDs. Each quasi-bidirectional I/O can be used
 * as an input or output without the use of a data-direction control signal. At power on, the I/Os are high. In this mode,
 * only a current source to VCC is active.
 *
 * http://www.ti.com/product/PCF8575
 */
public class Pcf8575 extends Pcf8574 {
    public Pcf8575(int address) {
        super(address);
    }

    public void writePort(byte p0, byte p1) throws IOException {
        getDevice().write(new byte[]{p0, p1});
    }

    public void writePort(byte[] p) throws IOException {
        if (p.length == 2) {
            getDevice().write(p);
        } else {
            throw new IllegalArgumentException("port data size != 2 bytes");
        }
    }
}
