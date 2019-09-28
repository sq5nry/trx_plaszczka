package org.sq5nry.plaszczka.backend.client.communicators;

import org.sq5nry.plaszczka.backend.api.selectivity.Bandwidth;
import org.sq5nry.plaszczka.backend.api.selectivity.Selectivity;
import org.sq5nry.plaszczka.backend.client.RequestSender;

public class SelectivityCommunicator extends BaseCommunicator implements Selectivity {
    public SelectivityCommunicator(RequestSender requestSender) {
        super(requestSender);
    }

    @Override
    public void setFilter(Bandwidth bw) {
        requestSender.sendRequest(Selectivity.RESOURCE_PATH_BANDWIDTH.replace("{bw}", "" + bw));
    }

    @Override
    public void bypass() {
        requestSender.sendRequest(Selectivity.RESOURCE_PATH_BYPASS);
    }
}
