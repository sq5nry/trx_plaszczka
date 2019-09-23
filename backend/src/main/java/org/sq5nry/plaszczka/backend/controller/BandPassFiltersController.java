package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.sq5nry.plaszczka.backend.api.inputfilter.Band;
import org.sq5nry.plaszczka.backend.api.inputfilter.BandPassFilter;
import org.sq5nry.plaszczka.backend.impl.BpfUnit;

import java.io.IOException;

/*
http://localhost:8090/bandPassFilter/band/M20
 */
@RestController
public class BandPassFiltersController implements BandPassFilter {
    private static final Logger logger = LoggerFactory.getLogger(BandPassFiltersController.class);

    @Autowired
    private BpfUnit bpfUnit;

    @RequestMapping(value = RESOURCE_PATH, method = RequestMethod.GET)
    @Override
    public void setBand(@PathVariable Band band) throws IOException {
        logger.debug("band filter requested, band={}", band);
        bpfUnit.setBand(band);
    }
}
