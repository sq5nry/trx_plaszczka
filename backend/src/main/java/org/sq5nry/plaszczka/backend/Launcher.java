package org.sq5nry.plaszczka.backend;

import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.impl.I2CProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import javax.annotation.PostConstruct;

@SpringBootApplication
@Configuration
@PropertySources({
        @PropertySource("classpath:i2c.properties"),
        @PropertySource("classpath:spi.properties")
})
//@EnableScheduling
public class Launcher {
    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {
        logger.info("starting application...");
        SpringApplication.run(Launcher.class, args);
    }

    @Value("${i2c.provider.class}")
    private String i2cProviderClass;

    @PostConstruct
    private void init() throws Exception {
        //PlatformManager.setPlatform(Platform.BANANAPI); //TODO config

        logger.debug("setting i2c factory: {}", i2cProviderClass);
        Class clazz = Class.forName(i2cProviderClass);
        I2CProviderImpl provider = (I2CProviderImpl) clazz.newInstance();
        I2CFactory.setFactory(provider);
        //logger.debug("I2CFactory.getBusIds {}", I2CFactory.getBusIds());

        //TODO spi?
    }
}
