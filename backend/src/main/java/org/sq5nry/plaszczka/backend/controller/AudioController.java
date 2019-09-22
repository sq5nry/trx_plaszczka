package org.sq5nry.plaszczka.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.sq5nry.plaszczka.backend.api.audio.*;
import org.sq5nry.plaszczka.backend.impl.AudioUnit;

/*
http://localhost:8090/audio/input/QQ_MONO
http://localhost:8090/audio/volume/LEFT/20
http://localhost:8090/audio/muteLoudness/LOUD_ON_10DB
http://localhost:8090/audio/output/head
http://localhost:8090/audio/output/head_speaker
 */
@RestController
public class AudioController implements AfAmplifier {
    private static final Logger logger = LoggerFactory.getLogger(AudioController.class);

    @Autowired
    private AudioUnit audioUnit;

    @RequestMapping(value = RESOURCE_PATH_INPUT, method = RequestMethod.GET)
    @Override
    public String setInput(@PathVariable InputSelector mode) throws Exception {
        logger.debug("audio input setting requested: {}", mode);
        return audioUnit.setInput(mode);
    }

    @RequestMapping(value = RESOURCE_PATH_VOLUME, method = RequestMethod.GET)
    @Override
    public String setVolume(@PathVariable Channel channel, @PathVariable Integer volume) throws Exception {
        logger.debug("volume change requested for channel {} to -{}dB", channel, volume);
        audioUnit.setVolume(channel, volume);
        int[] vol = audioUnit.getVolume();
        return "L=-" + vol[0] + "dB, R=-" + vol[1] + "dB";
    }

    @RequestMapping(value = RESOURCE_PATH_MUTE_LOUDNESS, method = RequestMethod.GET)
    @Override
    public String setMuteLoudness(@PathVariable MuteAndLoudness mode) throws Exception {
        logger.debug("mute/loudness setting requested: {}", mode);
        return audioUnit.setMuteLoudness(mode);
    }

    @RequestMapping(value = RESOURCE_PATH_OUTPUT_AMPLIFIER, method = RequestMethod.GET)
    @Override
    public String setOutputAmplifier(@PathVariable OutputAmplifier output) throws Exception {
        logger.debug("routing to output amplifier(s) {} requested", output);
        audioUnit.setOutputAmplifier(output);
        return audioUnit.getOutputAmp().toString();
    }
}
