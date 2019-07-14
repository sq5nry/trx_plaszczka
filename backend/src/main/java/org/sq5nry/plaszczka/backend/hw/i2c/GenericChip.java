package org.sq5nry.plaszczka.backend.hw.i2c;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

import java.io.IOException;

public abstract class GenericChip {
    private I2CBus i2CBus;
    private I2CDevice device;
    private int address;

    private I2CDeviceState state = I2CDeviceState.CREATED;

    public GenericChip(I2CBus i2CBus, int address) {
        this.i2CBus = i2CBus;
        this.address = address;
    }

    public GenericChip initialize() throws IOException {
        device = i2CBus.getDevice(address);
        state = I2CDeviceState.INITIALIZED;
        return this;
    }

    public I2CDevice getDevice() {
        if (state != I2CDeviceState.INITIALIZED) {
            throw new IllegalStateException("device not initialized");
        }
        return device;
    }

    public int getAddress() {
        return address;
    }

    public I2CDeviceState getState() {
        return state;
    }
}