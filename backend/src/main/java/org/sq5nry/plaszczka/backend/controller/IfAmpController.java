package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.sq5nry.plaszczka.backend.impl.VgaUnit;

@RestController
public class IfAmpController {
    private static final Logger logger = LoggerFactory.getLogger(IfAmpController.class);

    @Autowired
    private VgaUnit vgaUnit;

    @RequestMapping(value = "/ifAmp/decaySpeedInDecayStateForHangMode/{val}", method = RequestMethod.GET)
    public String setDecaySpeedInDecayStateForHangMode(@PathVariable Float val) throws Exception {
        logger.debug("Vsph (DecaySpeedInDecayStateForHangMode) to {}", val);
        vgaUnit.setDecaySpeedInDecayStateForHangMode(val);
        return "result=OK";
    }

    @RequestMapping(value = "/ifAmp/decaySpeedForAttackDecayMode/{val}", method = RequestMethod.GET)
    public String setDecaySpeedForAttackDecayMode(@PathVariable Float val) throws Exception {
        logger.debug("Vspa (DecaySpeedInDecayStateForHangMode) to {}", val);
        vgaUnit.setDecaySpeedForAttackDecayMode(val);
        return "result=OK";
    }

    @RequestMapping(value = "/ifAmp/decaySpeedInHangStateForHangMode/{val}", method = RequestMethod.GET)
    public String setDecaySpeedInHangStateForHangMode(@PathVariable Float val) throws Exception {
        logger.debug("Vleak (DecaySpeedInDecayStateForHangMode) to {}", val);
        vgaUnit.setDecaySpeedInHangStateForHangMode(val);
        return "result=OK";
    }

    @RequestMapping(value = "/ifAmp/noiseFloorCompensation/{val}", method = RequestMethod.GET)
    public String setNoiseFloorCompensation(@PathVariable Float val) throws Exception {
        logger.debug("Vfloor (NoiseFloorCompensation) to {}", val);
        vgaUnit.setNoiseFloorCompensation(val);
        return "result=OK";
    }

    @RequestMapping(value = "/ifAmp/strategyThreshold/{val}", method = RequestMethod.GET)
    public String setStrategyThreshold(@PathVariable Float val) throws Exception {
        logger.debug("Vath (StrategyThreshold) to {}", val);
        vgaUnit.setStrategyThreshold(val);
        return "result=OK";
    }

    @RequestMapping(value = "/ifAmp/hangThreshold/{val}", method = RequestMethod.GET)
    public String setHangThreshold(@PathVariable Float val) throws Exception {
        logger.debug("Vhth (HangThreshold) to {}", val);
        vgaUnit.setHangThreshold(val);
        return "result=OK";
    }

    @RequestMapping(value = "/ifAmp/VLoop/{val}", method = RequestMethod.GET)
    public String setVLoop(@PathVariable Float val) throws Exception {
        logger.debug("Vloop (VLoop) to {}", val);
        vgaUnit.setVLoop(val);
        return "result=OK";
    }

    @RequestMapping(value = "/ifAmp/maximumGain/{val}", method = RequestMethod.GET)
    public String setMaximumGain(@PathVariable Float val) throws Exception {
        logger.debug("MaximumGain (Vgain) to {}", val);
        vgaUnit.setMaximumGain(val);
        return "result=OK";
    }

    @RequestMapping(value = "/ifAmp/maximumHangTimeInHangMode/{val}", method = RequestMethod.GET)
    public String setMaximumHangTimeInHangMode(@PathVariable Float val) throws Exception {
        logger.debug("MaximumHangTimeInHangMode (Vspd) to {}", val);
        vgaUnit.setMaximumHangTimeInHangMode(val);
        return "result=OK";
    }

    @RequestMapping(value = "/ifAmp/attackTime/{val}", method = RequestMethod.GET)
    public String setAttackTime(@PathVariable Float val) throws Exception {
        logger.debug("AttackTime (Attack) to {}", val);
        vgaUnit.setAttackTime(val);
        return "result=OK";
    }

    @RequestMapping(value = "/ifAmp/hangOnTransmit/{val}", method = RequestMethod.GET)
    public String setHangOnTransmit(@PathVariable Boolean val) throws Exception {
        logger.debug("setHangOnTransmit to {}", val);
        vgaUnit.setHangOnTransmit(val);
        return "result=OK";
    }

    @RequestMapping(value = "/ifAmp/mute/{val}", method = RequestMethod.GET)
    public String setMute(@PathVariable Boolean val) throws Exception {
        logger.debug("setMute to {}", val);
        vgaUnit.setMute(val);
        return "result=OK";
    }
}
