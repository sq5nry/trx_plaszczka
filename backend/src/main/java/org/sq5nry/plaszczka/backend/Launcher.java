package org.sq5nry.plaszczka.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.sq5nry.plaszczka.backend.hw.common.BusInitializationException;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;
import org.sq5nry.plaszczka.backend.hw.spi.SPIInitializer;

import javax.annotation.PostConstruct;

@SpringBootApplication
@Configuration
@PropertySources({
        @PropertySource("classpath:io.properties")
})
public class Launcher {
    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

    @Autowired
    private I2CBusProvider i2cProv;

    @Autowired
    private SPIInitializer spiProv;


    public static void main(String[] args) {
        logger.info("starting application...");
        SpringApplication.run(Launcher.class, args);
    }

    @PostConstruct
    private void init() throws BusInitializationException {
        logger.info("initializing I/O subsystems...");
        i2cProv.initialize();
        spiProv.initialize();
        logger.info("I/O subsystems initialized");
    }
}
