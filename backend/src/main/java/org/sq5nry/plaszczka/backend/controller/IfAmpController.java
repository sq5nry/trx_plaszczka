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
import org.sq5nry.plaszczka.backend.api.vga.IfAmp;
import org.sq5nry.plaszczka.backend.impl.VgaUnit;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ScheduledFuture;

@RestController
@EnableScheduling
public class IfAmpController implements IfAmp, SchedulingConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(IfAmpController.class);

    public static final int STREAMER_CTRL_RESP_STARTED = -1;
    public static final int STREAMER_CTRL_RESP_STOPPED = -2;

    @Autowired
    private VgaUnit vgaUnit;

    @Autowired
    private TaskScheduler scheduler;

    @Autowired
    private SimpMessagingTemplate template;

    private ScheduledFuture<?> future;
    private int period = Integer.MAX_VALUE;


    @RequestMapping(value = RESOURCE_PATH_VSPH, method = RequestMethod.GET)
    @Override
    public void setDecaySpeedInDecayStateForHangMode(@PathVariable float val) throws Exception {
        logger.debug("Vsph (DecaySpeedInDecayStateForHangMode) to {}", val);
        vgaUnit.setDecaySpeedInDecayStateForHangMode(val);
    }

    @RequestMapping(value = RESOURCE_PATH_VSPA, method = RequestMethod.GET)
    @Override
    public void setDecaySpeedForAttackDecayMode(@PathVariable float val) throws Exception {
        logger.debug("Vspa (DecaySpeedForAttackDecayMode) to {}", val);
        vgaUnit.setDecaySpeedForAttackDecayMode(val);
    }

    @RequestMapping(value = RESOURCE_PATH_VLEAK, method = RequestMethod.GET)
    @Override
    public void setDecaySpeedInHangStateForHangMode(@PathVariable float val) throws Exception {
        logger.debug("Vleak (DecaySpeedInHangStateForHangMode) to {}", val);
        vgaUnit.setDecaySpeedInHangStateForHangMode(val);
    }

    @RequestMapping(value = RESOURCE_PATH_VFLOOR, method = RequestMethod.GET)
    @Override
    public void setNoiseFloorCompensation(@PathVariable float val) throws Exception {
        logger.debug("Vfloor (NoiseFloorCompensation) to {}", val);
        vgaUnit.setNoiseFloorCompensation(val);
    }

    @RequestMapping(value = RESOURCE_PATH_VATH, method = RequestMethod.GET)
    @Override
    public void setStrategyThreshold(@PathVariable float val) throws Exception {
        logger.debug("Vath (StrategyThreshold) to {}", val);
        vgaUnit.setStrategyThreshold(val);
    }

    @RequestMapping(value = RESOURCE_PATH_VHTH, method = RequestMethod.GET)
    @Override
    public void setHangThreshold(@PathVariable float val) throws Exception {
        logger.debug("Vhth (HangThreshold) to {}", val);
        vgaUnit.setHangThreshold(val);
    }

    @RequestMapping(value = RESOURCE_PATH_VLOOP, method = RequestMethod.GET)
    @Override
    public void setVLoop(@PathVariable float val) throws Exception {
        logger.info("Vloop (VLoop) to {}", val);
        vgaUnit.setVLoop(val);
    }

    @RequestMapping(value = RESOURCE_PATH_MAXGAIN, method = RequestMethod.GET)
    @Override
    public void setMaximumGain(@PathVariable float val) throws Exception {
        logger.info("MaximumGain (Vgain) to {}", val);
        vgaUnit.setMaximumGain(val);
    }

    @RequestMapping(value = RESOURCE_PATH_VSPD, method = RequestMethod.GET)
    @Override
    public void setMaximumHangTimeInHangMode(@PathVariable float val) throws Exception {
        logger.info("MaximumHangTimeInHangMode (Vspd) to {}", val);
        vgaUnit.setMaximumHangTimeInHangMode(val);
    }

    @RequestMapping(value = RESOURCE_PATH_ATTACK, method = RequestMethod.GET)
    @Override
    public void setAttackTime(@PathVariable float val) throws Exception {
        logger.info("AttackTime (Attack) to {}", val);
        vgaUnit.setAttackTime(val);
    }

    @RequestMapping(value = RESOURCE_PATH_HANG_ON_TRANSMIT, method = RequestMethod.GET)
    @Override
    public void setHangOnTransmit(@PathVariable boolean flag) throws Exception {
        logger.info("setHangOnTransmit to {}", flag);
        vgaUnit.setHangOnTransmit(flag);
    }

    @RequestMapping(value = RESOURCE_PATH_MUTE, method = RequestMethod.GET)
    @Override
    public void setMute(@PathVariable boolean flag) throws Exception {
        logger.info("setMute to {}", flag);
        vgaUnit.setMute(flag);
    }

    @RequestMapping(value = RESOURCE_PATH_VAGC, method = RequestMethod.GET)
    @Override
    public int getVAgc() throws Exception {
        logger.debug("getVagc");
        return vgaUnit.getVAgc();
    }

    @MessageMapping("/vagc_stream_control")
    @SendTo("/topic/vagc")
    public Integer controlVAgcStreamer(String message) {
        logger.info("controlVAgcStreamer: message={}", message);
        if ("start".equals(message)) {
            start();
            return STREAMER_CTRL_RESP_STARTED;
        } else if ("stop".equals(message)) {
            stop();
            return STREAMER_CTRL_RESP_STOPPED;
        } else {
            period = Integer.valueOf(message);
            return period;
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

    public void stop() {
        if (future != null) {
            logger.info("stopping VAgc streamer");
            future.cancel(true);
        } else {
            logger.info("no active VAgc streamer to stop");
        }
    }

    private void despatchVAgc() throws Exception {
        template.convertAndSend("/topic/vagc", String.valueOf(vgaUnit.getVAgc()));
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {  //TODO needed?
        logger.info("configureTasks: {}", taskRegistrar);
    }
}
