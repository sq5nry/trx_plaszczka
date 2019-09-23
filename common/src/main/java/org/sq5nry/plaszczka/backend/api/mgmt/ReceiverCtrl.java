package org.sq5nry.plaszczka.backend.api.mgmt;

import org.sq5nry.plaszczka.backend.common.Unit;

import java.util.Map;

public interface ReceiverCtrl {
    String ROOT = "/mgmt/rx";
    String RESOURCE_PATH_STATE = ROOT + "/state";
    String RESOURCE_PATH_INITIALIZE = ROOT + "/initialize";

    Map<String, Unit.State> getState();
    Map<String, Unit.State> initialize();
}
