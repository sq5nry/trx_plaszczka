package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.sq5nry.plaszczka.backend.api.inputfilter.Attenuator;
import org.sq5nry.plaszczka.backend.impl.BpfUnit;

import java.io.IOException;

/*
http://localhost:8090/bandPassFilter/attenuator/12
 */
@RestController
public class AttenuatorController implements Attenuator {
    private static final Logger logger = LoggerFactory.getLogger(AttenuatorController.class);

    @Autowired
    private BpfUnit bpfUnit;

    @GetMapping(value = RESOURCE_PATH)
    @Override
    public int setAttenuation(@PathVariable int att) throws IOException {
        logger.debug("attenuation requested, att={}dB", att);
        return bpfUnit.setAttenuation(att);
    }
}
