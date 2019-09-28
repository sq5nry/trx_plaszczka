package org.sq5nry.plaszczka.backend.client.communicators;

import org.sq5nry.plaszczka.backend.api.Mode;
import org.sq5nry.plaszczka.backend.api.mixer.HModeMixer;
import org.sq5nry.plaszczka.backend.client.RequestSender;

public class HModeMixerCommunicator extends BaseCommunicator implements HModeMixer {
    public HModeMixerCommunicator(RequestSender requestSender) {
        super(requestSender);
    }

    @Override
    public void setBiasPoint(float val) {
        requestSender.sendRequest(HModeMixer.RESOURCE_PATH_BIAS.replace("{val}", Float.toString(val)));
    }

    @Override
    public void setSquarerThreshold(float percentage) {
        requestSender.sendRequest(HModeMixer.RESOURCE_PATH_SQUARER.replace("{val}", Float.toString(percentage)));
    }

    @Override
    public void setRoofingFilter(Mode mode) {
        requestSender.sendRequest(HModeMixer.RESOURCE_PATH_ROOFING.replace("{mode}", mode.name()));
    }
}
