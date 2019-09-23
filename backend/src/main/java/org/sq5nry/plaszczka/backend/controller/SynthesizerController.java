package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.sq5nry.plaszczka.backend.api.synthesiser.Synthesizer;
import org.sq5nry.plaszczka.backend.impl.BfoUnit;
import org.sq5nry.plaszczka.backend.impl.VfoUnit;

import java.io.IOException;

@RestController
public class SynthesizerController implements Synthesizer {
    private static final Logger logger = LoggerFactory.getLogger(SynthesizerController.class);

    @Autowired
    BfoUnit bfoUnit;

    @Autowired
    VfoUnit vfoUnit;

    @GetMapping(value = RESOURCE_PATH_BFO)
    public void setBfoFrequency(@PathVariable int freq) throws IOException {
        logger.debug("synthesizer BFO frequency requested {}Hz", freq);
        bfoUnit.setFrequency(freq);
    }

    @GetMapping(value = RESOURCE_PATH_VFO)
    public void setVfoFrequency(@PathVariable int freq) throws IOException {
        logger.debug("synthesizer VFO frequency requested {}Hz", freq);
        vfoUnit.setFrequency(freq);
    }
}
