package org.sq5nry.plaszczka.backend.hw.gpio;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.trigger.GpioTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class DummyGpioController implements GpioController {
    private static final Logger logger = LoggerFactory.getLogger(DummyGpioController.class);

    @Override
    public void export(PinMode pinMode, PinState pinState, GpioPin... gpioPins) {
        logger.debug("export: {} {}", pinMode, pinState);
    }

    @Override
    public void export(PinMode pinMode, GpioPin... gpioPins) {
        logger.debug("export: {} {}", pinMode, gpioPins);
    }

    @Override
    public boolean isExported(GpioPin... gpioPins) {
        return false;
    }

    @Override
    public void unexport(Pin... pins) {
        logger.debug("unexport: {}", pins);
    }

    @Override
    public void unexport(GpioPin... gpioPins) {
        logger.debug("unexport: {}", gpioPins);
    }

    @Override
    public void unexportAll() {
        logger.debug("unexportAll");
    }

    @Override
    public void setMode(PinMode pinMode, GpioPin... gpioPins) {

    }

    @Override
    public PinMode getMode(GpioPin gpioPin) {
        return PinMode.DIGITAL_OUTPUT;
    }

    @Override
    public boolean isMode(PinMode pinMode, GpioPin... gpioPins) {
        return false;
    }

    @Override
    public void setPullResistance(PinPullResistance pinPullResistance, GpioPin... gpioPins) {
        logger.debug("setPullResistance: {}", pinPullResistance);
    }

    @Override
    public PinPullResistance getPullResistance(GpioPin gpioPin) {
        return PinPullResistance.PULL_UP;
    }

    @Override
    public boolean isPullResistance(PinPullResistance pinPullResistance, GpioPin... gpioPins) {
        return false;
    }

    @Override
    public void high(GpioPinDigitalOutput... gpioPinDigitalOutputs) {
        logger.debug("high: {}", gpioPinDigitalOutputs);
    }

    @Override
    public boolean isHigh(GpioPinDigital... gpioPinDigitals) {
        return false;
    }

    @Override
    public void low(GpioPinDigitalOutput... gpioPinDigitalOutputs) {
        logger.debug("low: {}", gpioPinDigitalOutputs);
    }

    @Override
    public boolean isLow(GpioPinDigital... gpioPinDigitals) {
        return false;
    }

    @Override
    public void setState(PinState pinState, GpioPinDigitalOutput... gpioPinDigitalOutputs) {
        logger.debug("setState: {}", pinState);
    }

    @Override
    public void setState(boolean b, GpioPinDigitalOutput... gpioPinDigitalOutputs) {
        logger.debug("setState: {}", b);
    }

    @Override
    public boolean isState(PinState pinState, GpioPinDigital... gpioPinDigitals) {
        return false;
    }

    @Override
    public PinState getState(GpioPinDigital gpioPinDigital) {
        return PinState.HIGH;
    }

    @Override
    public void toggle(GpioPinDigitalOutput... gpioPinDigitalOutputs) {
        logger.debug("toggle: {}", gpioPinDigitalOutputs);
    }

    @Override
    public void pulse(long l, GpioPinDigitalOutput... gpioPinDigitalOutputs) {
        logger.debug("pulse: {}", l);
    }

    @Override
    public void setValue(double v, GpioPinAnalogOutput... gpioPinAnalogOutputs) {
        logger.debug("setValue: {}", v);
    }

    @Override
    public double getValue(GpioPinAnalog gpioPinAnalog) {
        return 0;
    }

    @Override
    public void addListener(GpioPinListener gpioPinListener, GpioPinInput... gpioPinInputs) {
        logger.debug("addListener");
    }

    @Override
    public void addListener(GpioPinListener[] gpioPinListeners, GpioPinInput... gpioPinInputs) {
        logger.debug("addListener");
    }

    @Override
    public void removeListener(GpioPinListener gpioPinListener, GpioPinInput... gpioPinInputs) {
        logger.debug("removeListener");
    }

    @Override
    public void removeListener(GpioPinListener[] gpioPinListeners, GpioPinInput... gpioPinInputs) {
        logger.debug("removeListener");
    }

    @Override
    public void removeAllListeners() {
        logger.debug("removeAllListeners");
    }

    @Override
    public void addTrigger(GpioTrigger gpioTrigger, GpioPinInput... gpioPinInputs) {
        logger.debug("addTrigger");
    }

    @Override
    public void addTrigger(GpioTrigger[] gpioTriggers, GpioPinInput... gpioPinInputs) {
        logger.debug("addTrigger");
    }

    @Override
    public void removeTrigger(GpioTrigger gpioTrigger, GpioPinInput... gpioPinInputs) {
        logger.debug("removeTrigger");
    }

    @Override
    public void removeTrigger(GpioTrigger[] gpioTriggers, GpioPinInput... gpioPinInputs) {
        logger.debug("removeTrigger");
    }

    @Override
    public void removeAllTriggers() {
        logger.debug("removeAllTriggers");
    }

    @Override
    public GpioPinDigitalMultipurpose provisionDigitalMultipurposePin(GpioProvider gpioProvider, Pin pin, String s, PinMode pinMode, PinPullResistance pinPullResistance) {
        return null;
    }

    @Override
    public GpioPinDigitalMultipurpose provisionDigitalMultipurposePin(GpioProvider gpioProvider, Pin pin, PinMode pinMode, PinPullResistance pinPullResistance) {
        return null;
    }

    @Override
    public GpioPinDigitalMultipurpose provisionDigitalMultipurposePin(GpioProvider gpioProvider, Pin pin, String s, PinMode pinMode) {
        return null;
    }

    @Override
    public GpioPinDigitalMultipurpose provisionDigitalMultipurposePin(GpioProvider gpioProvider, Pin pin, PinMode pinMode) {
        return null;
    }

    @Override
    public GpioPinDigitalMultipurpose provisionDigitalMultipurposePin(Pin pin, String s, PinMode pinMode, PinPullResistance pinPullResistance) {
        return null;
    }

    @Override
    public GpioPinDigitalMultipurpose provisionDigitalMultipurposePin(Pin pin, PinMode pinMode, PinPullResistance pinPullResistance) {
        return null;
    }

    @Override
    public GpioPinDigitalMultipurpose provisionDigitalMultipurposePin(Pin pin, String s, PinMode pinMode) {
        return null;
    }

    @Override
    public GpioPinDigitalMultipurpose provisionDigitalMultipurposePin(Pin pin, PinMode pinMode) {
        return null;
    }

    @Override
    public GpioPinDigitalInput provisionDigitalInputPin(GpioProvider gpioProvider, Pin pin, String s, PinPullResistance pinPullResistance) {
        return null;
    }

    @Override
    public GpioPinDigitalInput provisionDigitalInputPin(GpioProvider gpioProvider, Pin pin, PinPullResistance pinPullResistance) {
        return null;
    }

    @Override
    public GpioPinDigitalInput provisionDigitalInputPin(GpioProvider gpioProvider, Pin pin, String s) {
        return null;
    }

    @Override
    public GpioPinDigitalInput provisionDigitalInputPin(GpioProvider gpioProvider, Pin pin) {
        return null;
    }

    @Override
    public GpioPinDigitalInput provisionDigitalInputPin(Pin pin, String s, PinPullResistance pinPullResistance) {
        return null;
    }

    @Override
    public GpioPinDigitalInput provisionDigitalInputPin(Pin pin, PinPullResistance pinPullResistance) {
        return null;
    }

    @Override
    public GpioPinDigitalInput provisionDigitalInputPin(Pin pin, String s) {
        return null;
    }

    @Override
    public GpioPinDigitalInput provisionDigitalInputPin(Pin pin) {
        return null;
    }

    @Override
    public GpioPinDigitalOutput provisionDigitalOutputPin(GpioProvider gpioProvider, Pin pin, String name, PinState pinState) {
        return new DummyGpioPinDigitalOutput(pin, name, pinState);
    }

    @Override
    public GpioPinDigitalOutput provisionDigitalOutputPin(GpioProvider gpioProvider, Pin pin, PinState pinState) {
        return new DummyGpioPinDigitalOutput(pin, "-", pinState);
    }

    @Override
    public GpioPinDigitalOutput provisionDigitalOutputPin(GpioProvider gpioProvider, Pin pin, String s) {
        return new DummyGpioPinDigitalOutput(pin, "-", PinState.LOW);
    }

    @Override
    public GpioPinDigitalOutput provisionDigitalOutputPin(GpioProvider gpioProvider, Pin pin) {
        return new DummyGpioPinDigitalOutput(pin, "-", PinState.LOW);
    }

    @Override
    public GpioPinDigitalOutput provisionDigitalOutputPin(Pin pin, String s, PinState pinState) {
        return new DummyGpioPinDigitalOutput(pin, s, pinState);
    }

    @Override
    public GpioPinDigitalOutput provisionDigitalOutputPin(Pin pin, PinState pinState) {
        return new DummyGpioPinDigitalOutput(pin, "-", PinState.LOW);
    }

    @Override
    public GpioPinDigitalOutput provisionDigitalOutputPin(Pin pin, String s) {
        return new DummyGpioPinDigitalOutput(pin, s, PinState.LOW);
    }

    @Override
    public GpioPinDigitalOutput provisionDigitalOutputPin(Pin pin) {
        return new DummyGpioPinDigitalOutput(pin, "-", PinState.LOW);
    }

    @Override
    public GpioPinAnalogInput provisionAnalogInputPin(GpioProvider gpioProvider, Pin pin, String s) {
        return null;
    }

    @Override
    public GpioPinAnalogInput provisionAnalogInputPin(GpioProvider gpioProvider, Pin pin) {
        return null;
    }

    @Override
    public GpioPinAnalogInput provisionAnalogInputPin(Pin pin, String s) {
        return null;
    }

    @Override
    public GpioPinAnalogInput provisionAnalogInputPin(Pin pin) {
        return null;
    }

    @Override
    public GpioPinAnalogOutput provisionAnalogOutputPin(GpioProvider gpioProvider, Pin pin, String s, double v) {
        return null;
    }

    @Override
    public GpioPinAnalogOutput provisionAnalogOutputPin(GpioProvider gpioProvider, Pin pin, double v) {
        return null;
    }

    @Override
    public GpioPinAnalogOutput provisionAnalogOutputPin(GpioProvider gpioProvider, Pin pin, String s) {
        return null;
    }

    @Override
    public GpioPinAnalogOutput provisionAnalogOutputPin(GpioProvider gpioProvider, Pin pin) {
        return null;
    }

    @Override
    public GpioPinAnalogOutput provisionAnalogOutputPin(Pin pin, String s, double v) {
        return null;
    }

    @Override
    public GpioPinAnalogOutput provisionAnalogOutputPin(Pin pin, double v) {
        return null;
    }

    @Override
    public GpioPinAnalogOutput provisionAnalogOutputPin(Pin pin, String s) {
        return null;
    }

    @Override
    public GpioPinAnalogOutput provisionAnalogOutputPin(Pin pin) {
        return null;
    }

    @Override
    public GpioPinPwmOutput provisionPwmOutputPin(GpioProvider gpioProvider, Pin pin, String s, int i) {
        return null;
    }

    @Override
    public GpioPinPwmOutput provisionPwmOutputPin(GpioProvider gpioProvider, Pin pin, int i) {
        return null;
    }

    @Override
    public GpioPinPwmOutput provisionPwmOutputPin(GpioProvider gpioProvider, Pin pin, String s) {
        return null;
    }

    @Override
    public GpioPinPwmOutput provisionPwmOutputPin(GpioProvider gpioProvider, Pin pin) {
        return null;
    }

    @Override
    public GpioPinPwmOutput provisionPwmOutputPin(Pin pin, String s, int i) {
        return null;
    }

    @Override
    public GpioPinPwmOutput provisionPwmOutputPin(Pin pin, int i) {
        return null;
    }

    @Override
    public GpioPinPwmOutput provisionPwmOutputPin(Pin pin, String s) {
        return null;
    }

    @Override
    public GpioPinPwmOutput provisionPwmOutputPin(Pin pin) {
        return null;
    }

    @Override
    public GpioPinPwmOutput provisionSoftPwmOutputPin(GpioProvider gpioProvider, Pin pin, String s, int i) {
        return null;
    }

    @Override
    public GpioPinPwmOutput provisionSoftPwmOutputPin(GpioProvider gpioProvider, Pin pin, int i) {
        return null;
    }

    @Override
    public GpioPinPwmOutput provisionSoftPwmOutputPin(GpioProvider gpioProvider, Pin pin, String s) {
        return null;
    }

    @Override
    public GpioPinPwmOutput provisionSoftPwmOutputPin(GpioProvider gpioProvider, Pin pin) {
        return null;
    }

    @Override
    public GpioPinPwmOutput provisionSoftPwmOutputPin(Pin pin, String s, int i) {
        return null;
    }

    @Override
    public GpioPinPwmOutput provisionSoftPwmOutputPin(Pin pin, int i) {
        return null;
    }

    @Override
    public GpioPinPwmOutput provisionSoftPwmOutputPin(Pin pin, String s) {
        return null;
    }

    @Override
    public GpioPinPwmOutput provisionSoftPwmOutputPin(Pin pin) {
        return null;
    }

    @Override
    public GpioPin provisionPin(GpioProvider gpioProvider, Pin pin, String s, PinMode pinMode, PinState pinState) {
        return null;
    }

    @Override
    public GpioPin provisionPin(GpioProvider gpioProvider, Pin pin, String s, PinMode pinMode) {
        return null;
    }

    @Override
    public GpioPin provisionPin(GpioProvider gpioProvider, Pin pin, PinMode pinMode) {
        return null;
    }

    @Override
    public GpioPin provisionPin(Pin pin, String s, PinMode pinMode) {
        return null;
    }

    @Override
    public GpioPin provisionPin(Pin pin, PinMode pinMode) {
        return null;
    }

    @Override
    public void setShutdownOptions(GpioPinShutdown gpioPinShutdown, GpioPin... gpioPins) {

    }

    @Override
    public void setShutdownOptions(Boolean aBoolean, GpioPin... gpioPins) {

    }

    @Override
    public void setShutdownOptions(Boolean aBoolean, PinState pinState, GpioPin... gpioPins) {

    }

    @Override
    public void setShutdownOptions(Boolean aBoolean, PinState pinState, PinPullResistance pinPullResistance, GpioPin... gpioPins) {

    }

    @Override
    public void setShutdownOptions(Boolean aBoolean, PinState pinState, PinPullResistance pinPullResistance, PinMode pinMode, GpioPin... gpioPins) {

    }

    @Override
    public Collection<GpioPin> getProvisionedPins() {
        return null;
    }

    @Override
    public GpioPin getProvisionedPin(Pin pin) {
        return null;
    }

    @Override
    public GpioPin getProvisionedPin(String s) {
        return null;
    }

    @Override
    public void unprovisionPin(GpioPin... gpioPins) {

    }

    @Override
    public void shutdown() {
        logger.debug("shutdown");
    }

    @Override
    public boolean isShutdown() {
        return false;
    }
}
