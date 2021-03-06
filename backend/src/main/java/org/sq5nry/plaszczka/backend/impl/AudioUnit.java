package org.sq5nry.plaszczka.backend.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sq5nry.plaszczka.backend.api.audio.*;
import org.sq5nry.plaszczka.backend.hw.common.GenericChip;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;
import org.sq5nry.plaszczka.backend.hw.chips.Pcf8574;
import org.sq5nry.plaszczka.backend.hw.chips.Tda7309;
import org.sq5nry.plaszczka.backend.impl.common.BaseUnit;

import java.io.IOException;
import java.util.List;

@Component
public class AudioUnit extends BaseUnit implements AfAmplifier {
    private static final Logger logger = LoggerFactory.getLogger(FrontEndMixerUnit.class);

    private final int EXPANDER_ADDR = 0x25;
    private final int AUDIO_PROCESSOR_ADDR = 0x19;

    private static final byte OUTPUT_AMP_HEADPHONES = 0x08;
    private static final byte OUTPUT_AMP_SPEAKERS = 0x10;
    private static final byte OUTPUT_AMP_REC = 0x04;

    private OutputAmplifier outputAmp;
    private int volume_r;
    private int volume_l;

    @Autowired
    public AudioUnit(I2CBusProvider i2cBusProv) throws Exception {
        super(i2cBusProv);
    }

    @Override
    public void createChipset(List<GenericChip> chipset) {
        chipset.add(new Pcf8574(EXPANDER_ADDR));
        chipset.add(new Tda7309(AUDIO_PROCESSOR_ADDR));
    }

    @Override
    public void initializeUnit() throws IOException {
        //TODO maybe mute?
    }

    @Override
    public String setInput(InputSelector mode) throws IOException {
        Tda7309 audioProc = (Tda7309) getChip(AUDIO_PROCESSOR_ADDR);
        switch (mode) {
            case II_MONO:
                audioProc.setInputMux(Tda7309.INPUTS_IN3);  //TODO check I/Q vs L/R assignment
            case QQ_MONO:
                audioProc.setInputMux(Tda7309.INPUTS_IN2);  //TODO check I/Q vs L/R assignment
                break;
            case IQ_STEREO:
                audioProc.setInputMux(Tda7309.INPUTS_IN1);
                break;
            case IQ_OFF:
                audioProc.setInputMux(Tda7309.INPUTS_MUTE);
                break;
            default: throw new IllegalArgumentException("unknown input: " + mode);
        }

        return mode.name();
    }

    @Override
    public String setVolume(Channel channel, Integer volume) throws IOException {
        Tda7309 audioProc = (Tda7309) getChip(AUDIO_PROCESSOR_ADDR);
        switch (channel) {
            case BOTH:
                audioProc.setChannel(Tda7309.CHANNEL_BOTH);
                volume_l = volume_r = volume;
                break;
            case LEFT:
                audioProc.setChannel(Tda7309.CHANNEL_LEFT);
                volume_l = volume;
                break;
            case RIGHT:
                audioProc.setChannel(Tda7309.CHANNEL_RIGHT);
                volume_r = volume;
                break;
             default:
                 throw new IllegalArgumentException("unknown channel: " + channel);
        }
        audioProc.setVolume(volume);

        return channel + "@-" + volume + "dB";
    }

    /**
     * Get volume.
     * @return [0] Left channel, [1] Right channel
     */
    public int[] getVolume() {
        return new int[]{volume_l, volume_r};
    }

    @Override
    public String setMuteLoudness(MuteAndLoudness loudness) throws IOException {
        Tda7309 audioProc = (Tda7309) getChip(AUDIO_PROCESSOR_ADDR);
        switch(loudness) {
            case LOUD_ON_10DB:
                audioProc.setLoudness(Tda7309.LOUD_ON_10DB);
                break;
            case LOUD_ON_20DB:
                audioProc.setLoudness(Tda7309.LOUD_ON_20DB);
                break;
            case LOUD_OFF:
                audioProc.setLoudness(Tda7309.LOUD_OFF);
                break;
            case FAST_SOFT_MUTE:
                audioProc.setMute(Tda7309.MUTE_FAST_SOFT_MUTE_ON);
                break;
            case SLOW_SOFT_MUTE:
                audioProc.setMute(Tda7309.MUTE_SLOW_SOFT_MUTE_ON);
                break;
            case SOFT_MUTE_OFF:
                audioProc.setMute(Tda7309.MUTE_SOFT_MUTE_OFF);
                break;
            default:
                throw new IllegalArgumentException("unknown loudness or mute mode: " + loudness);
        }
        return loudness.name();
    }

    @Override
    public String setOutputAmplifier(OutputAmplifier amp) throws IOException {
        Pcf8574 expander = (Pcf8574) getChip(EXPANDER_ADDR);
        byte data = 0x00;
        if (amp.isHeadphones()) {
            data |= OUTPUT_AMP_HEADPHONES;
        }
        if (amp.isRec()) {
            data |= OUTPUT_AMP_REC;
        }
        if (amp.isSpeaker()) {
            data |= OUTPUT_AMP_SPEAKERS;
        }
        expander.writePort(data);
        this.outputAmp = amp;

        return amp.toString();
    }

    public OutputAmplifier getOutputAmp() {
        return outputAmp;
    }

    @Override
    public String getName() {
        return "Audio Subsystem";
    }
}
