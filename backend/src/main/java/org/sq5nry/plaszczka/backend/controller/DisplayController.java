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

    @RequestMapping(value = "/frequencyDisplay/freq/{id}", method = RequestMethod.GET)
    public String setFrequency(@PathVariable Integer id) throws IOException {
        logger.debug("display frequency requested, value={}", id);
        freqDisplayService.setFrequency(id);
        return "result=" + freqDisplayService.getFrequency() + "Hz";
    }

    @RequestMapping(value = "/frequencyDisplay/marker/{id}", method = RequestMethod.GET)
    public String setMarker(@PathVariable Integer id) throws IOException {
        logger.debug("display marker requested, tube={}", id);
        freqDisplayService.setMarker(id);
        return "result=Tube[" + freqDisplayService.getMarker() + ']';
    }

    @RequestMapping(value = "/frequencyDisplay/blanking/{id}", method = RequestMethod.GET)
    public String setBlankLeadingZeroes(@PathVariable Boolean id) throws IOException {
        logger.debug("blanking of leading zeroes requested, flag={}", id);
        freqDisplayService.setBlankLeadingZeroes(id);

        int frequency = freqDisplayService.getFrequency();
        logger.debug("refresh display, freq={} Hz", frequency);
        freqDisplayService.setFrequency(frequency);

        return "result=BL[" + freqDisplayService.isBlankLeadingZeroes() + "], freq={}" + frequency + "Hz";
    }

    @RequestMapping(value = "/frequencyDisplay/initialize", method = RequestMethod.GET)
    public String initialize() throws Exception {
        logger.debug("initializing module");
        freqDisplayService.initialize();
        return "result=module reinitialized successfully";
    }
}
