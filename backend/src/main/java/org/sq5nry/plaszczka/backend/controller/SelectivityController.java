package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.sq5nry.plaszczka.backend.api.selectivity.Bandwidth;
import org.sq5nry.plaszczka.backend.impl.SelectivityUnit;

@RestController
public class SelectivityController {
    private static final Logger logger = LoggerFactory.getLogger(SelectivityController.class);

    @Autowired
    SelectivityUnit selectivityService;

    @GetMapping(value = "/selectivity/{freq}")
    public String setRoofingFilter(@PathVariable int freq) throws Exception {
        logger.debug("selectivity requested, bw={}", freq);
        selectivityService.setFilter(Bandwidth.fromBandwidth(freq));
        return "result=" + selectivityService.getFilter();
    }
}
