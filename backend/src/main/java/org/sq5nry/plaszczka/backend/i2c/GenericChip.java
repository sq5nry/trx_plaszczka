package org.sq5nry.plaszczka.backend.i2c;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

import java.io.IOException;

public abstract class GenericChip {
    private I2CBus bus;
    private I2CDevice device;
    private int address;
    private I2CDeviceState state = I2CDeviceState.CREATED;

    public GenericChip(I2CBus bus, int address) {
        this.bus = bus;
        this.address = address;
    }

    public void initialize() throws IOException {
        device = bus.getDevice(address);
        state = I2CDeviceState.INITIALIZED;
    }

    public I2CDevice getDevice() {
        return device;
    }

    public int getAddress() {
        return address;
    }
}
