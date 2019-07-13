package org.sq5nry.plaszczka.backend.hw.i2c.chips;

import com.pi4j.io.i2c.I2CBus;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericChip;

@Component
@Scope("prototype")
public class Ad5242 extends GenericChip {

    public Ad5242(I2CBus bus, int address) {
        super(bus, address);
    }

    public void dupa(){

    }
}
