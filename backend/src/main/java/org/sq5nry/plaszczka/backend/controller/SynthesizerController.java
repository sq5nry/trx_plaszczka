package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.sq5nry.plaszczka.backend.impl.DdsUnit;

public class SynthesizerController {
    private static final Logger logger = LoggerFactory.getLogger(SynthesizerController.class);

    @Autowired
    DdsUnit ddsUnit;

    @GetMapping(value = "/lo/{freq}")
    public String setFrequency(@PathVariable int freq) throws Exception {
        logger.debug("synthesizer frequency requested {}Hz", freq);
        ddsUnit.setFrequency(freq);
        return "result=OK";
    }
}
