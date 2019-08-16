package org.sq5nry.plaszczka.backend.hw.gpio;

import com.pi4j.io.gpio.GpioController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Wrapper for GpioController as Spring thing. Could this be done springlier?
 */
@Component
public class GpioControllerProvider {
    private static final Logger logger = LoggerFactory.getLogger(GpioControllerProvider.class);

    @Value("${gpio.provider.class}")
    private String gpioProviderClass;

    public GpioController getGpioController() throws Exception {
        logger.debug("setting GPIO provider: {}", gpioProviderClass);
        Class clazz = Class.forName(gpioProviderClass);
        GpioController controller = (GpioController) clazz.newInstance();
        return controller;
    }
}
