package org.sq5nry.plaszczka.backend.hw.i2c;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

import java.io.IOException;

public abstract class GenericChip {
    private I2CBus i2CBus;
    private I2CDevice device;
    private int address;

    public GenericChip(int address) {
        this.address = address;
    }

    public GenericChip initialize() throws IOException {
        device = i2CBus.getDevice(address);
        if ((address >= 0x30 && address <= 0x37) || (address >= 0x50 && address <= 0x5F)) {
            device.read();
        } else {
            device.write((byte) 0);
        }
        return this;
    }

    public I2CDevice getDevice() {
        return device;
    }

    public void setI2CBus(I2CBus i2CBus) {
        this.i2CBus = i2CBus;
    }

    public int getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{address=0x" + Integer.toHexString(address) + '}';
    }
}
