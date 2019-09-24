package org.sq5nry.plaszczka.backend.client.communicators;

import org.sq5nry.plaszczka.backend.api.mgmt.ReceiverCtrl;
import org.sq5nry.plaszczka.backend.client.RequestSender;
import org.sq5nry.plaszczka.backend.common.Unit;

import java.util.Map;

public class ReceiverCtrlCommunicator extends BaseCommunicator implements ReceiverCtrl {
    public ReceiverCtrlCommunicator(RequestSender requestSender) {
        super(requestSender);
    }

    @Override
    public Map<String, Unit.State> getState() {
        return null;
    }

    @Override
    public Map<String, Unit.State> initialize() {
        return null;
    }
}
