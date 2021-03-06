package org.sq5nry.plaszczka.backend.impl.common;

/**
 * Mostly for initial live testing where modules are re-connected thus re-powered.
 */
public interface Reinitializable {
    /**
     * Initialize a module as during power up. Default initial settings are applied.
     * @throws Exception
     */
    void reinitializeUnit() throws Exception;
}
