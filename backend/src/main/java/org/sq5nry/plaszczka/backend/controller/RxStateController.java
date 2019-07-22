package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.sq5nry.plaszczka.backend.impl.Reinitializable;
import org.sq5nry.plaszczka.backend.impl.Unit;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RxStateController {
    private static final Logger logger = LoggerFactory.getLogger(RxStateController.class);

    @Autowired
    private Map<String, ? extends Unit> units;

    @RequestMapping(value = "/mgmt/state/rx", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Unit.State> getState() throws Exception {
        logger.debug("state check requested");
        Map<String, Unit.State> states = new HashMap<>();
        for(String unitName: units.keySet()) {
            states.put(unitName, units.get(unitName).getState());
        }
        return states;
    }

    @RequestMapping(value = "/mgmt/initialize/rx", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Unit.State> initialize() throws Exception {
        logger.debug("rx path initialize requested");

        Map<String, Unit.State> states = new HashMap<>();
        for(Unit unit: units.values()) {
            String name = unit.getClass().getSimpleName();
            try {
                logger.debug("initializing chipset in {}", name);
                unit.initializeChipset();
                if (unit instanceof Reinitializable) {
                    logger.debug("initializing unit {}", name);
                    ((Reinitializable) unit).initializeUnit();
                }
            } catch (Exception e) {
                logger.warn("initialization error for unit " + name , e);
            } finally {
                states.put(name, unit.getState());
            }
        }
        return states;
    }
}
