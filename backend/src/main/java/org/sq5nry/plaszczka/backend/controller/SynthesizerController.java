package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.sq5nry.plaszczka.backend.impl.BfoUnit;
import org.sq5nry.plaszczka.backend.impl.VfoUnit;

@RestController
public class SynthesizerController {
    private static final Logger logger = LoggerFactory.getLogger(SynthesizerController.class);

    @Autowired
    VfoUnit ddsUnit;

    @Autowired
    BfoUnit bfoUnit;

    @GetMapping(value = "/vfo/{freq}")
    public String setVfoFrequency(@PathVariable int freq) throws Exception {
        logger.debug("synthesizer VFO frequency requested {}Hz", freq);
        ddsUnit.setFrequency(freq);
        return "result=OK";
    }

    @GetMapping(value = "/bfo/{freq}")
    public String setBfoFrequency(@PathVariable int freq) throws Exception {
        logger.debug("synthesizer BFO frequency requested {}Hz", freq);
        bfoUnit.setFrequency(freq);
        return "result=OK";
    }
}
