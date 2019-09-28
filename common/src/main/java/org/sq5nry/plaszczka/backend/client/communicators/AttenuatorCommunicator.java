package org.sq5nry.plaszczka.backend.client.communicators;

import org.sq5nry.plaszczka.backend.api.inputfilter.Attenuator;
import org.sq5nry.plaszczka.backend.client.RequestSender;

public class AttenuatorCommunicator extends BaseCommunicator implements Attenuator {
    public AttenuatorCommunicator(RequestSender requestSender) {
        super(requestSender);
    }

    @Override
    public int setAttenuation(int db) {
        String result = requestSender.sendRequest(Attenuator.RESOURCE_PATH.replace("{att}", Integer.toString(db)));
        return Integer.parseInt(result);    //TODO makes sense?
    }
}
