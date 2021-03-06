package org.sq5nry.plaszczka.backend.hw.common;

import com.pi4j.io.gpio.GpioController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericChip {
    private static final Logger logger = LoggerFactory.getLogger(GenericChip.class);

    private final String name;
    protected int address;
    protected boolean initialized;

    private GpioController controller;
    private boolean isGpioReal;

    public GenericChip(int address, String name) {
        this.address = address;
        this.name = name;
    }

    public abstract GenericChip initialize() throws ChipInitializationException;

    public int getAddress() {
        return address;
    }

    public abstract boolean needsGpio();

    public void setGpioController(GpioController controller) {
        logger.info("setGpioController: {}", controller);
        this.controller = controller;
        //TODO as forced by pi4j design...
        if (controller.getClass().getPackage().getName().contains("org.sq5nry")) {
            isGpioReal = false;
        } else {
            isGpioReal = true;
        }
        logger.info("isGpioReal: {}", isGpioReal);
    }

    protected GpioController getGpioController() {
        return controller;
    }

    protected void gpioDelay(long delay) {
        if (isGpioReal) {
            com.pi4j.wiringpi.Gpio.delay(delay);
        }
    }

    @Override
    public String toString() {
        return name + "_@" + Integer.toHexString(address) + (needsGpio() ? "/GPIO=" + controller.toString() : "");
    }
}
