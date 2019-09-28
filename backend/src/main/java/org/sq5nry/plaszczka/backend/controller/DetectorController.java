package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.sq5nry.plaszczka.backend.api.Mode;
import org.sq5nry.plaszczka.backend.api.detector.Detector;
import org.sq5nry.plaszczka.backend.impl.QSDUnit;

import java.io.IOException;

@RestController
public class DetectorController implements Detector {
    private static final Logger logger = LoggerFactory.getLogger(DetectorController.class);

    @Autowired
    QSDUnit detectorUnit;

    @GetMapping(value = RESOURCE_PATH_ROOFING)
    @Override
    public void setRoofingFilter(@PathVariable Mode mode) throws IOException {
        logger.debug("mode requested: {}", mode);
        detectorUnit.setRoofingFilter(mode);
    }

    @RequestMapping(value = RESOURCE_PATH_ENABLE, method = RequestMethod.GET)
    @Override
    public void setEnabled(@PathVariable boolean enabled) throws IOException {
        logger.debug("detector enable requested to: {}", enabled);
        detectorUnit.setEnabled(enabled);
    }
}
