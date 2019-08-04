package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.sq5nry.plaszczka.backend.impl.VgaUnit;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ScheduledFuture;

@RestController
@EnableScheduling
public class IfAmpController implements SchedulingConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(IfAmpController.class);

    @Autowired
    private VgaUnit vgaUnit;

    @Autowired
    private TaskScheduler scheduler;

    @Autowired
    private SimpMessagingTemplate template;

    private ScheduledFuture<?> future;
    private int period = Integer.MAX_VALUE;


    @RequestMapping(value = "/ifAmp/decaySpeedInDecayStateForHangMode/{val}", method = RequestMethod.GET)
    public String setDecaySpeedInDecayStateForHangMode(@PathVariable Float val) throws Exception {
        logger.debug("Vsph (DecaySpeedInDecayStateForHangMode) to {}", val);
        vgaUnit.setDecaySpeedInDecayStateForHangMode(val);
        return "result=OK";
    }

    @RequestMapping(value = "/ifAmp/decaySpeedForAttackDecayMode/{val}", method = RequestMethod.GET)
    public String setDecaySpeedForAttackDecayMode(@PathVariable Float val) throws Exception {
        logger.debug("Vspa (DecaySpeedForAttackDecayMode) to {}", val);
        vgaUnit.setDecaySpeedForAttackDecayMode(val);
        return "result=OK";
    }

    @RequestMapping(value = "/ifAmp/decaySpeedInHangStateForHangMode/{val}", method = RequestMethod.GET)
    public String setDecaySpeedInHangStateForHangMode(@PathVariable Float val) throws Exception {
        logger.debug("Vleak (DecaySpeedInHangStateForHangMode) to {}", val);
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

    @RequestMapping(value = "/ifAmp/vagc", method = RequestMethod.GET)
    public String getVagc() throws Exception {
        logger.debug("getVagc");
        return String.valueOf(vgaUnit.getVAgc());
    }

    @MessageMapping("/vagc_stream_control")
    @SendTo("/topic/vagc")
    public String controlVAgcStreamer(String message) {
        logger.debug("getSS: message={}", message);
        if ("start".equals(message)) {
            start();
            return "started";
        } else if ("stop".equals(message)) {
            stop();
            return "stopped";
        } else {
            period = Integer.valueOf(message);
            return "period set";
        }
    }

    private void start() {
        future = scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    despatchVAgc();
                } catch (Exception e) {
                    logger.error("despatchVAgc failed, stopping the scheduler");
                    stop();
                }
            }
        }, new Trigger() {
            @Override public Date nextExecutionTime(TriggerContext triggerContext) {
                Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
                Calendar nextExecutionTime =  new GregorianCalendar();
                nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
                nextExecutionTime.add(Calendar.MILLISECOND, period);
                return nextExecutionTime.getTime();
            }
        });
    }

    private void stop() {
        future.cancel(true);
    }

    private void despatchVAgc() throws Exception {
        template.convertAndSend("/topic/vagc", String.valueOf(vgaUnit.getVAgc()));
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {  //TODO needed?
        logger.debug("configureTasks: {}", taskRegistrar);
    }
}
