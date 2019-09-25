package org.sq5nry.plaszczka.backend.client.communicators;

import org.sq5nry.plaszczka.backend.api.audio.*;
import org.sq5nry.plaszczka.backend.client.RequestSender;
import org.sq5nry.plaszczka.backend.client.communicators.BaseCommunicator;

public class AfAmplifierCommunicator extends BaseCommunicator implements AfAmplifier {
    public AfAmplifierCommunicator(RequestSender requestSender) {
        super(requestSender);
    }

    @Override
    public String setInput(InputSelector mode) throws Exception {
        return requestSender.sendRequest(AfAmplifier.RESOURCE_PATH_INPUT.replace("{mode}", mode.name()));
    }

    @Override
    public String setVolume(Channel channel, Integer volume) throws Exception {
        return requestSender.sendRequest(AfAmplifier.RESOURCE_PATH_VOLUME.replace("{volume}", "" + volume));
    }

    @Override
    public String setMuteLoudness(MuteAndLoudness loudness) throws Exception {
        return requestSender.sendRequest(AfAmplifier.RESOURCE_PATH_MUTE_LOUDNESS.replace("{mode}", loudness.name()));
    }

    @Override
    public String setOutputAmplifier(OutputAmplifier amp) throws Exception {
        return requestSender.sendRequest(AfAmplifier.RESOURCE_PATH_OUTPUT_AMPLIFIER.replace("{output}", amp.toString()));
    }
}
