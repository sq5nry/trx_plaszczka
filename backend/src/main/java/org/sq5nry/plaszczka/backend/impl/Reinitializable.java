package org.sq5nry.plaszczka.backend.impl;

/**
 * Mostly for initial live testing where modules are re-connected thus re-powered.
 */
public interface Reinitializable {
    /**
     * Initialize a module as during power up.
     * @throws Exception
     */
    void initialize() throws Exception;
}
