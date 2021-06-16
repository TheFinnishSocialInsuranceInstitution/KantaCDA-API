package fi.kela.kanta.util;

import static org.junit.Assert.assertFalse;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ OidGenerator.class })
public class OidGeneratorPowerMockTest {

    @Test
    public void testCreateNewDocumentOidAfterMidnight() throws Exception {
        int year = 2015;
        int hour = 24;
        int minute = 30;
        GregorianCalendar greg = new GregorianCalendar();
        greg.set(Calendar.YEAR, year);
        greg.set(Calendar.HOUR_OF_DAY, hour);
        greg.set(Calendar.MINUTE, minute);
        OidGenerator generator = OidGenerator.getInstance();
        PowerMockito.whenNew(GregorianCalendar.class).withAnyArguments().thenReturn(greg);

        String newOid = generator.createNewDocumentOid("1.2.3.4");
        for (String solmu : newOid.split("\\.")) {
            assertFalse("solmun ensimmäinen merkki ei voi olla nolla (0)", solmu.startsWith("0"));
        }
    }
}
