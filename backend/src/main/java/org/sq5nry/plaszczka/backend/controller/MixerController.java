package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.sq5nry.plaszczka.backend.api.Mode;
import org.sq5nry.plaszczka.backend.api.mixer.HModeMixer;
import org.sq5nry.plaszczka.backend.impl.FrontEndMixerUnit;

@RestController
public class MixerController implements HModeMixer {
    private static final Logger logger = LoggerFactory.getLogger(MixerController.class);

    @Autowired
    FrontEndMixerUnit mixerService;

    @GetMapping(value = RESOURCE_PATH_ROOFING)
    @Override
    public void setRoofingFilter(@PathVariable Mode mode) throws Exception {
        logger.debug("post-mixer roofing mode requested, v={}", mode);
        mixerService.setRoofingFilter(mode);
    }

    @GetMapping(value = RESOURCE_PATH_BIAS)
    @Override
    public void setBiasPoint(@PathVariable float val) throws Exception {
        logger.debug("mixer bias voltage requested, v={}", val);
        mixerService.setBiasPoint(val);
    }

    @GetMapping(value = RESOURCE_PATH_SQUARER)
    @Override
    public void setSquarerThreshold(@PathVariable float val) throws Exception {
        logger.debug("squarer threshold requested, v={}", val);
        mixerService.setSquarerThreshold(val);
    }
}
