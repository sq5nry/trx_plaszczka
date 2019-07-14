package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.sq5nry.plaszczka.backend.api.Mode;
import org.sq5nry.plaszczka.backend.impl.FrontEndMixerUnit;

@RestController
public class MixerController {
    private static final Logger logger = LoggerFactory.getLogger(MixerController.class);

    @Autowired
    FrontEndMixerUnit mixerService;

    @GetMapping(value = "/mixer/roofingMode/{id}")
    public String setRoofingFilter(@PathVariable String id) throws Exception {
        logger.debug("post-mixer roofing mode requested, v={}", id);
        mixerService.setRoofingFilter(Mode.valueOf(id.toUpperCase()));
        return "result=" + mixerService.getRoofingFilter();
    }

    @GetMapping(value = "/mixer/bias/{id}")
    public String setBias(@PathVariable Float id) throws Exception {
        logger.debug("mixer bias voltage requested, v={}", id);
        mixerService.setBiasPoint(id);
        return "result=" + mixerService.getBiasPoint() + "V";
    }

    @GetMapping(value = "/mixer/squarerThreshold/{id}")
    public String setSquarerThreshold(@PathVariable Float id) throws Exception {
        logger.debug("squarer threshold requested, v={}", id);
        mixerService.setSquarerThreshold(id);
        return "result=" + mixerService.getSquarerThreshold() + "%";
    }
}
