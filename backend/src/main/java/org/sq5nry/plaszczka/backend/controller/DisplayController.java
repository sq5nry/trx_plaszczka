package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.sq5nry.plaszczka.backend.impl.NixieDisplay;

import java.io.IOException;

@RestController
public class DisplayController {
    private static final Logger logger = LoggerFactory.getLogger(DisplayController.class);

    @Autowired
    NixieDisplay freqDisplayService;

    @RequestMapping(value = "/frequencyDisplay/{id}", method = RequestMethod.GET)
    public String setFrequency(@PathVariable Integer id) throws IOException {
        logger.debug("getFrequency requested, freq={}", id);
        freqDisplayService.setFrequency(id);
        return "f=" + freqDisplayService.getFrequency();
    }
}
