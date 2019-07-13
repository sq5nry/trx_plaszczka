package trxplaszczka;

import com.pi4j.io.i2c.I2CFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CFactoryDummyBusProvider;
import org.sq5nry.plaszczka.backend.impl.NixieDisplayUnit;

public class NixieDisplayUnitTest {
    @BeforeClass
    public static void init() {
        I2CFactory.setFactory(new I2CFactoryDummyBusProvider());
    }

    @Test
    public void testMaxNumber() throws Exception {
        I2CBusProvider prov = new I2CBusProvider();
        NixieDisplayUnit disp =  new NixieDisplayUnit(prov);
        disp.setFrequency(9999999);
    }
}
