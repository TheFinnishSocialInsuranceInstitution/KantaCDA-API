package fi.kela.kanta.cda;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MaarittelyLuokkaTest {

    @Test
    public void tuettuTest() {
        for (MaarittelyLuokka luokka : MaarittelyLuokka.values()) {
            if ( luokka == MaarittelyLuokka.EI_TUETTU ) {
                assertFalse(luokka.isTuettu());
            }
            else if ( luokka == MaarittelyLuokka.PUUTTUU ) {
                assertFalse(luokka.isTuettu());
            }
            else if ( luokka == MaarittelyLuokka.TULEVA ) {
                assertFalse(luokka.isTuettu());
            }
            else {
                assertTrue(luokka.isTuettu());
            }
        }
    }
}
