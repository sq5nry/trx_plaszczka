package org.sq5nry.plaszczka.backend.client.communicators;

import org.sq5nry.plaszczka.backend.api.audio.*;
import org.sq5nry.plaszczka.backend.client.RequestSender;

public class AfAmplifierCommunicator extends BaseCommunicator implements AfAmplifier {
    public AfAmplifierCommunicator(RequestSender requestSender) {
        super(requestSender);
    }

    @Override
    public String setInput(InputSelector mode) {
        return requestSender.sendRequest(AfAmplifier.RESOURCE_PATH_INPUT.replace("{mode}", mode.name()));
    }

    @Override
    public String setVolume(Channel channel, Integer volume) {
        return requestSender.sendRequest(AfAmplifier.RESOURCE_PATH_VOLUME.replace("{volume}", "" + volume));
    }

    @Override
    public String setMuteLoudness(MuteAndLoudness loudness) {
        return requestSender.sendRequest(AfAmplifier.RESOURCE_PATH_MUTE_LOUDNESS.replace("{mode}", loudness.name()));
    }

    @Override
    public String setOutputAmplifier(OutputAmplifier amp) {
        return requestSender.sendRequest(AfAmplifier.RESOURCE_PATH_OUTPUT_AMPLIFIER.replace("{output}", amp.toString()));
    }
}
