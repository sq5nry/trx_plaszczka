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
    SelectivityUnit selectivityUnit;

    @GetMapping(value = "/selectivity/bw/{freq}")
    public String setRoofingFilter(@PathVariable int freq) throws Exception {
        logger.debug("selectivity requested, bw={}", freq);
        selectivityUnit.setFilter(Bandwidth.fromBandwidth(freq));
        return "result=" + selectivityUnit.getFilter();
    }

    @GetMapping(value = "/selectivity/bypass")
    public String setBypass() throws Exception {
        logger.debug("bypass requested");
        selectivityUnit.bypass();
        return "result=ok";
    }
}
