package org.sq5nry.plaszczka.backend.common;

public interface Unit {
    enum State { CREATED, CHIPSET_INITIALIZED, UNIT_INITIALIZED, FAILED }

    void initializeChipsetAndUnit();

    State getState();

    String getName();
}
