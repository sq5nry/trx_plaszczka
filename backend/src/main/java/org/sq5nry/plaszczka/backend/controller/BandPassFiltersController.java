package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.sq5nry.plaszczka.backend.api.inputfilter.Band;
import org.sq5nry.plaszczka.backend.impl.BpfUnit;

import java.io.IOException;

@RestController
public class BandPassFiltersController {
    private static final Logger logger = LoggerFactory.getLogger(BandPassFiltersController.class);

    @Autowired
    private BpfUnit bpfUnit;

    @RequestMapping(value = "/bandPassFilter/band/{id}", method = RequestMethod.GET)
    public String setFrequency(@PathVariable String id) throws IOException {
        logger.debug("band filter requested, band={}", id);
        bpfUnit.setBand(Band.fromMeters(id));
        return "result=" + bpfUnit.getBand();
    }

    @RequestMapping(value = "/bandPassFilter/attenuator/{id}", method = RequestMethod.GET)
    public String setMarker(@PathVariable Integer id) throws IOException {
        logger.debug("attenuator requested, att={}", id);
        bpfUnit.setAttenuation(id);
        return "result=att" + bpfUnit.getAttenuation() + "dB";
    }
}