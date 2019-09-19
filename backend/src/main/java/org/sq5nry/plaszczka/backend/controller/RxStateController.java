package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.sq5nry.plaszczka.backend.impl.Unit;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RxStateController {
    private static final Logger logger = LoggerFactory.getLogger(RxStateController.class);

    @Autowired
    private Map<String, ? extends Unit> units;

    @RequestMapping(value = "/mgmt/state/rx", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Unit.State> getState() {
        logger.debug("state check requested");
        Map<String, Unit.State> states = new HashMap<>();
        for(String unitName: units.keySet()) {
            states.put(unitName, units.get(unitName).getState());
        }
        return states;
    }

    @RequestMapping(value = "/mgmt/initialize/rx", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Unit.State> initialize() {
        logger.info("rx path initialize requested");

        Map<String, Unit.State> states = new HashMap<>();
        for(Unit unit: units.values()) {
            try {
                unit.initializeChipsetAndUnit();
            } catch (Exception e) {
                logger.warn("initialization error for unit " + unit.getName() , e);
            } finally {
                states.put(unit.getName(), unit.getState());
            }
        }
        return states;
    }
}
