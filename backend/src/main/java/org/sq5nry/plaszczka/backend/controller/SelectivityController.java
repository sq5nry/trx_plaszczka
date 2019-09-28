package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.sq5nry.plaszczka.backend.api.selectivity.Bandwidth;
import org.sq5nry.plaszczka.backend.api.selectivity.Selectivity;
import org.sq5nry.plaszczka.backend.impl.SelectivityUnit;

import java.io.IOException;

@RestController
public class SelectivityController implements Selectivity {
    private static final Logger logger = LoggerFactory.getLogger(SelectivityController.class);

    @Autowired
    SelectivityUnit selectivityUnit;

    @GetMapping(value = RESOURCE_PATH_BANDWIDTH)
    @Override
    public void setFilter(@PathVariable Bandwidth bw) throws IOException {
        logger.debug("selectivity requested, bw={}", bw);
        selectivityUnit.setFilter(bw);
    }

    @GetMapping(value = RESOURCE_PATH_BYPASS)
    @Override
    public void bypass() throws IOException {
        logger.debug("bypass requested");
        selectivityUnit.bypass();
    }
}
