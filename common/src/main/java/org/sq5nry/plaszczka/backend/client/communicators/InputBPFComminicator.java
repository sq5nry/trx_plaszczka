package org.sq5nry.plaszczka.backend.client.communicators;

import org.sq5nry.plaszczka.backend.api.inputfilter.Band;
import org.sq5nry.plaszczka.backend.api.inputfilter.BandPassFilter;
import org.sq5nry.plaszczka.backend.client.RequestSender;

public class InputBPFComminicator extends BaseCommunicator implements BandPassFilter {
    public InputBPFComminicator(RequestSender requestSender) {
        super(requestSender);
    }

    @Override
    public void setBand(Band band) {
        requestSender.sendRequest(BandPassFilter.RESOURCE_PATH.replace("{band}", band.name()));
    }
}
