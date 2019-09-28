package org.sq5nry.plaszczka.backend.client.communicators;

import org.sq5nry.plaszczka.backend.api.synthesiser.Synthesizer;
import org.sq5nry.plaszczka.backend.client.RequestSender;

public class SynthesizerCommunicator extends BaseCommunicator implements Synthesizer {
    public SynthesizerCommunicator(RequestSender requestSender) {
        super(requestSender);
    }

    @Override
    public void setBfoFrequency(int freq) {
        requestSender.sendRequest(Synthesizer.RESOURCE_PATH_BFO.replace("{freq}", "" + freq));
    }

    @Override
    public void setVfoFrequency(int freq) {
        requestSender.sendRequest(Synthesizer.RESOURCE_PATH_VFO.replace("{freq}", "" + freq));
    }
}
