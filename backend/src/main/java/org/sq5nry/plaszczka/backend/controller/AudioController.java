package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.sq5nry.plaszczka.backend.api.audio.Channel;
import org.sq5nry.plaszczka.backend.api.audio.InputSelector;
import org.sq5nry.plaszczka.backend.api.audio.MuteAndLoudness;
import org.sq5nry.plaszczka.backend.api.audio.OutputAmplifier;
import org.sq5nry.plaszczka.backend.impl.AudioUnit;

@RestController
public class AudioController {
    private static final Logger logger = LoggerFactory.getLogger(AudioController.class);

    @Autowired
    private AudioUnit audioUnit;

    @RequestMapping(value = "/audio/muteLoudness/{mode}", method = RequestMethod.GET)
    public String setMuteLoudness(@PathVariable String mode) throws Exception {
        logger.debug("mute/loudness setting requested: {}", mode);
        audioUnit.setMuteLoudness(MuteAndLoudness.valueOf(mode));
        return "result=TODO";
    }

    @RequestMapping(value = "/audio/input/{mode}", method = RequestMethod.GET)
    public String setInput(@PathVariable String mode) throws Exception {
        logger.debug("audio input setting requested: {}", mode);
        audioUnit.setInput(InputSelector.valueOf(mode));
        return "result=TODO";
    }

    @RequestMapping(value = "/audio/volume/{channel}/{volume}", method = RequestMethod.GET)
    public String setInput(@PathVariable String channel, @PathVariable Integer volume) throws Exception {
        logger.debug("volume change requested for channel {} to -{}dB", channel, volume);
        audioUnit.setVolume(volume, Channel.valueOf(channel.toUpperCase()));
        int[] vol = audioUnit.getVolume();
        return "result= L=-" + vol[0] + "dB, R=-" + vol[1] + "dB";
    }

    @RequestMapping(value = "/audio/output/{output}", method = RequestMethod.GET)
    public String setOutput(@PathVariable String output) throws Exception {
        logger.debug("routing to output amplifier(s) {} requested", output);
        audioUnit.setOutputAmplifier(new OutputAmplifier(output));
        return "result=" + audioUnit.getOutputAmp();
    }
}
