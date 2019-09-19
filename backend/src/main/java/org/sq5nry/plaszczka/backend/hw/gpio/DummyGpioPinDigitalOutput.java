package org.sq5nry.plaszczka.backend.hw.gpio;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DummyGpioPinDigitalOutput implements GpioPinDigitalOutput {
    private static final Logger logger = LoggerFactory.getLogger(DummyGpioController.class);

    private Pin pin;
    private String name;
    private PinState pinState;

    public DummyGpioPinDigitalOutput(Pin pin, String name, PinState pinState) {
        this.pin = pin;
        this.name = name;
        this.pinState = pinState;
    }

    @Override
    public void high() {
        logger.debug("high: {}, {}", pin, name);
    }

    @Override
    public void low() {
        logger.debug("low: {}, {}", pin, name);
    }

    @Override
    public void toggle() {
        logger.debug("toggle");
    }

    @Override
    public Future<?> blink(long l) {
        return null;
    }

    @Override
    public Future<?> blink(long l, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public Future<?> blink(long l, PinState pinState) {
        return null;
    }

    @Override
    public Future<?> blink(long l, PinState pinState, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public Future<?> blink(long l, long l1) {
        return null;
    }

    @Override
    public Future<?> blink(long l, long l1, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public Future<?> blink(long l, long l1, PinState pinState) {
        return null;
    }

    @Override
    public Future<?> blink(long l, long l1, PinState pinState, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public Future<?> pulse(long l) {
        return null;
    }

    @Override
    public Future<?> pulse(long l, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public Future<?> pulse(long l, Callable<Void> callable) {
        return null;
    }

    @Override
    public Future<?> pulse(long l, Callable<Void> callable, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public Future<?> pulse(long l, boolean b) {
        return null;
    }

    @Override
    public Future<?> pulse(long l, boolean b, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public Future<?> pulse(long l, boolean b, Callable<Void> callable) {
        return null;
    }

    @Override
    public Future<?> pulse(long l, boolean b, Callable<Void> callable, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public Future<?> pulse(long l, PinState pinState) {
        return null;
    }

    @Override
    public Future<?> pulse(long l, PinState pinState, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public Future<?> pulse(long l, PinState pinState, Callable<Void> callable) {
        return null;
    }

    @Override
    public Future<?> pulse(long l, PinState pinState, Callable<Void> callable, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public Future<?> pulse(long l, PinState pinState, boolean b) {
        return null;
    }

    @Override
    public Future<?> pulse(long l, PinState pinState, boolean b, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public Future<?> pulse(long l, PinState pinState, boolean b, Callable<Void> callable) {
        return null;
    }

    @Override
    public Future<?> pulse(long l, PinState pinState, boolean b, Callable<Void> callable, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public void setState(PinState pinState) {

    }

    @Override
    public void setState(boolean b) {

    }

    @Override
    public boolean isHigh() {
        return false;
    }

    @Override
    public boolean isLow() {
        return false;
    }

    @Override
    public PinState getState() {
        return null;
    }

    @Override
    public boolean isState(PinState pinState) {
        return false;
    }

    @Override
    public GpioProvider getProvider() {
        return null;
    }

    @Override
    public Pin getPin() {
        return null;
    }

    @Override
    public void setName(String s) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setTag(Object o) {

    }

    @Override
    public Object getTag() {
        return null;
    }

    @Override
    public void setProperty(String s, String s1) {

    }

    @Override
    public boolean hasProperty(String s) {
        return false;
    }

    @Override
    public String getProperty(String s) {
        return null;
    }

    @Override
    public String getProperty(String s, String s1) {
        return null;
    }

    @Override
    public Map<String, String> getProperties() {
        return null;
    }

    @Override
    public void removeProperty(String s) {

    }

    @Override
    public void clearProperties() {

    }

    @Override
    public void export(PinMode pinMode) {

    }

    @Override
    public void export(PinMode pinMode, PinState pinState) {

    }

    @Override
    public void unexport() {

    }

    @Override
    public boolean isExported() {
        return false;
    }

    @Override
    public void setMode(PinMode pinMode) {

    }

    @Override
    public PinMode getMode() {
        return null;
    }

    @Override
    public boolean isMode(PinMode pinMode) {
        return false;
    }

    @Override
    public void setPullResistance(PinPullResistance pinPullResistance) {

    }

    @Override
    public PinPullResistance getPullResistance() {
        return null;
    }

    @Override
    public boolean isPullResistance(PinPullResistance pinPullResistance) {
        return false;
    }

    @Override
    public Collection<GpioPinListener> getListeners() {
        return null;
    }

    @Override
    public void addListener(GpioPinListener... gpioPinListeners) {

    }

    @Override
    public void addListener(List<? extends GpioPinListener> list) {

    }

    @Override
    public boolean hasListener(GpioPinListener... gpioPinListeners) {
        return false;
    }

    @Override
    public void removeListener(GpioPinListener... gpioPinListeners) {

    }

    @Override
    public void removeListener(List<? extends GpioPinListener> list) {

    }

    @Override
    public void removeAllListeners() {

    }

    @Override
    public GpioPinShutdown getShutdownOptions() {
        return null;
    }

    @Override
    public void setShutdownOptions(GpioPinShutdown gpioPinShutdown) {

    }

    @Override
    public void setShutdownOptions(Boolean aBoolean) {

    }

    @Override
    public void setShutdownOptions(Boolean aBoolean, PinState pinState) {

    }

    @Override
    public void setShutdownOptions(Boolean aBoolean, PinState pinState, PinPullResistance pinPullResistance) {

    }

    @Override
    public void setShutdownOptions(Boolean aBoolean, PinState pinState, PinPullResistance pinPullResistance, PinMode pinMode) {

    }

    @Override
    public String toString() {
        return "DummyGpioPin{" +
                "pin=" + pin +
                ", name='" + name + '\'' +
                '}';
    }
}
