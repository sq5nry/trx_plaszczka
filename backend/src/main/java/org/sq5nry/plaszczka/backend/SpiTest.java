package org.sq5nry.plaszczka.backend;

import org.sq5nry.plaszczka.backend.hw.chips.Ad9954;

public class SpiTest {
    public static void main(String[] args) {
        new Ad9954(500000000).initialize();
    }
}
