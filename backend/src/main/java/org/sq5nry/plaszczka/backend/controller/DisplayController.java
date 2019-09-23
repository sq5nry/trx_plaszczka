package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.sq5nry.plaszczka.backend.api.display.FrequencyDisplay;
import org.sq5nry.plaszczka.backend.impl.NixieDisplayUnit;

import java.io.IOException;

@RestController
public class DisplayController implements FrequencyDisplay {
    private static final Logger logger = LoggerFactory.getLogger(DisplayController.class);

    @Autowired
    NixieDisplayUnit freqDisplayService;

    @RequestMapping(value = RESOURCE_PATH_SET_FREQUENCY, method = RequestMethod.GET)
    @Override
    public void setFrequency(@PathVariable int freq) throws IOException {
        logger.debug("display frequency requested, value={}", freq);
        freqDisplayService.setFrequency(freq);
    }

    @RequestMapping(value = RESOURCE_PATH_SET_MARKER, method = RequestMethod.GET)
    @Override
    public void setMarker(@PathVariable int id) throws IOException {
        logger.debug("display marker requested, tube={}", id);
        freqDisplayService.setMarker(id);
    }

    @RequestMapping(value = RESOURCE_PATH_SET_BLANK_LEADING_ZEROES, method = RequestMethod.GET)
    @Override
    public void setBlankLeadingZeroes(@PathVariable boolean flag) throws IOException {
        logger.debug("blanking of leading zeroes requested, flag={}", flag);
        freqDisplayService.setBlankLeadingZeroes(flag);

        int frequency = freqDisplayService.getFrequency();
        logger.debug("refresh display, freq={} Hz", frequency);
        freqDisplayService.setFrequency(frequency);
    }
}
