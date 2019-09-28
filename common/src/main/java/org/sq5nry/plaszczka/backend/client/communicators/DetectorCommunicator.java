package org.sq5nry.plaszczka.backend.client.communicators;

import org.sq5nry.plaszczka.backend.api.Mode;
import org.sq5nry.plaszczka.backend.api.detector.Detector;
import org.sq5nry.plaszczka.backend.client.RequestSender;

public class DetectorCommunicator extends BaseCommunicator implements Detector {
    public DetectorCommunicator(RequestSender requestSender) {
        super(requestSender);
    }

    @Override
    public void setRoofingFilter(Mode mode) {
        requestSender.sendRequest(Detector.RESOURCE_PATH_ROOFING.replace("{mode}", mode.name()));
    }

    @Override
    public void setEnabled(boolean enabled) {
        requestSender.sendRequest(Detector.RESOURCE_PATH_ENABLE.replace("{enabled}", Boolean.toString(enabled)));
    }
}
