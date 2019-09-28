package org.sq5nry.plaszczka.backend.client.communicators;

import org.sq5nry.plaszczka.backend.api.vga.IfAmp;
import org.sq5nry.plaszczka.backend.client.RequestSender;

public class IfAmpCommunicator extends BaseCommunicator implements IfAmp {
    public IfAmpCommunicator(RequestSender requestSender) {
        super(requestSender);
    }

    @Override
    public void setDecaySpeedInDecayStateForHangMode(float speed) {
        requestSender.sendRequest(IfAmp.RESOURCE_PATH_VSPH.replace("{val}", Float.toString(speed)));
    }

    @Override
    public void setDecaySpeedForAttackDecayMode(float speed) {
        requestSender.sendRequest(IfAmp.RESOURCE_PATH_VSPA.replace("{val}", Float.toString(speed)));
    }

    @Override
    public void setDecaySpeedInHangStateForHangMode(float speed) {
        requestSender.sendRequest(IfAmp.RESOURCE_PATH_VLEAK.replace("{val}", Float.toString(speed)));
    }

    @Override
    public void setNoiseFloorCompensation(float val) {
        requestSender.sendRequest(IfAmp.RESOURCE_PATH_VFLOOR.replace("{val}", Float.toString(val)));
    }

    @Override
    public void setStrategyThreshold(float val) {
        requestSender.sendRequest(IfAmp.RESOURCE_PATH_VATH.replace("{val}", Float.toString(val)));
    }

    @Override
    public void setHangThreshold(float val) {
        requestSender.sendRequest(IfAmp.RESOURCE_PATH_VHTH.replace("{val}", Float.toString(val)));
    }

    @Override
    public void setVLoop(float val) {
        requestSender.sendRequest(IfAmp.RESOURCE_PATH_VLOOP.replace("{val}", Float.toString(val)));
    }

    @Override
    public void setMaximumGain(float gain) {
        requestSender.sendRequest(IfAmp.RESOURCE_PATH_MAXGAIN.replace("{val}", Float.toString(gain)));
    }

    @Override
    public void setMaximumHangTimeInHangMode(float val) {
        requestSender.sendRequest(IfAmp.RESOURCE_PATH_VSPD.replace("{val}", Float.toString(val)));
    }

    @Override
    public void setAttackTime(float val) {
        requestSender.sendRequest(IfAmp.RESOURCE_PATH_ATTACK.replace("{val}", Float.toString(val)));
    }

    @Override
    public void setHangOnTransmit(boolean enabled) {
        requestSender.sendRequest(IfAmp.RESOURCE_PATH_HANG_ON_TRANSMIT.replace("{val}", Boolean.toString(enabled)));
    }

    @Override
    public void setMute(boolean enabled) {
        requestSender.sendRequest(IfAmp.RESOURCE_PATH_MUTE.replace("{val}", Boolean.toString(enabled)));
    }

    @Override
    public int getVAgc() {
        return 0;
    }
}
