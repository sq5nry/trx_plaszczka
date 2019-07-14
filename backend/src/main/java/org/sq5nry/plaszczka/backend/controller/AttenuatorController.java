package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.sq5nry.plaszczka.backend.impl.BpfUnit;

import java.io.IOException;

@RestController
public class AttenuatorController {
    private static final Logger logger = LoggerFactory.getLogger(AttenuatorController.class);

    @Autowired
    private BpfUnit bpfService;

    @GetMapping(value = "/attenuator/{id}")
    public String setMarker(@PathVariable Integer id) throws IOException {
        logger.debug("attenuator requested, att={}", id);
        bpfService.setAttenuation(id);
        return "result=att " + bpfService.getAttenuation() + "dB";
    }
}
