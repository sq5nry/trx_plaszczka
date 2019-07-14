package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.sq5nry.plaszczka.backend.api.Mode;
import org.sq5nry.plaszczka.backend.impl.QSDUnit;

@RestController
public class DetectorController {
    private static final Logger logger = LoggerFactory.getLogger(DetectorController.class);

    @Autowired
    QSDUnit detectorUnit;

    @GetMapping(value = "/detector/mode/{mode}")
    public String setRoofingFilter(@PathVariable String mode) throws Exception {
        logger.debug("mode requested: {}", mode);
        detectorUnit.setRoofingFilter(Mode.valueOf(mode.toUpperCase()));
        return "result=" + detectorUnit.getRoofingFilter();
    }

    @RequestMapping(value = "/detector/enabled/{enabled}", method = RequestMethod.GET)
    public String setEnabled(@PathVariable Boolean enabled) throws Exception {
        logger.debug("detector enable requested to: {}", enabled);
        detectorUnit.setEnabled(enabled);
        return "result=enabled:{}" + detectorUnit.isEnabled();
    }
}
