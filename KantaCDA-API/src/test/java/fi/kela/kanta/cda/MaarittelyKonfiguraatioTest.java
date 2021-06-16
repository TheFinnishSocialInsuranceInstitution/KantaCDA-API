package fi.kela.kanta.cda;

import org.junit.Before;
import org.junit.Test;

import org.apache.commons.configuration.ConfigurationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.LinkedList;

public class MaarittelyKonfiguraatioTest {

    private MaarittelyKonfiguraatio mk;

    @Before
    public void setup() throws ConfigurationException {
        mk = MaarittelyKonfiguraatio.lueKonfiguraatio();
    }

    /**
     * TemplateId puuttuu -> PUUTTUU, ei tuettu.
     */
    @Test
    public void missingLuokkaTest() {
        assertEquals(MaarittelyLuokka.PUUTTUU, mk.haeMaarittelyLuokka(null, null));
        LinkedList<String> testList = new LinkedList<String>();
        testList.add("");
        MaarittelyLuokka ml = mk.haeMaarittelyLuokka(testList, "");
        assertEquals(MaarittelyLuokka.PUUTTUU, ml);
        assertFalse(ml.isTuettu());
    }

    /**
     * Konfiguraatiossa olevat yksittäiset templateId:t + yksi, joka ei ole konfiguraatiossa
     */
    @Test
    public void singleLuokkaTest() {
        LinkedList<String> testList = new LinkedList<String>();
        testList.add("1.2.246.777.11.2016.2"); // TULEVA
        assertEquals(MaarittelyLuokka.TULEVA, mk.haeMaarittelyLuokka(testList, "1"));
        testList.clear();
        testList.add("1.2.246.777.11.2015.11"); // NYKYINEN
        assertEquals(MaarittelyLuokka.NYKYINEN, mk.haeMaarittelyLuokka(testList, "1"));
        testList.clear();
        testList.add("1.2.246.777.11.2014.2"); // VANHA
        assertEquals(MaarittelyLuokka.VANHA, mk.haeMaarittelyLuokka(testList, "1"));
        testList.clear();
        testList.add("1.2.246.777.11.2011.17"); // VANHA
        assertEquals(MaarittelyLuokka.VANHA, mk.haeMaarittelyLuokka(testList, "1"));
        testList.clear();
        testList.add("1.2.246.777.11.2007.17"); // VANHA
        assertEquals(MaarittelyLuokka.VANHA, mk.haeMaarittelyLuokka(testList, "1"));
        testList.clear();
        testList.add("1.2.246.777.11.2007.1"); // EI_TUETTU
        assertEquals(MaarittelyLuokka.EI_TUETTU, mk.haeMaarittelyLuokka(testList, "1"));
    }

    @Test
    public void eiTuettuKoodiTest() {
        LinkedList<String> testList = new LinkedList<String>();
        testList.add("1.2.246.777.11.2016.2"); // TULEVA, paitsi koodilla 2 EI_TUETTU
        assertEquals(MaarittelyLuokka.TULEVA, mk.haeMaarittelyLuokka(testList, "1"));
        assertEquals(MaarittelyLuokka.TULEVA, mk.haeMaarittelyLuokka(testList, "3"));
        assertEquals(MaarittelyLuokka.TULEVA, mk.haeMaarittelyLuokka(testList, "5"));
        assertEquals(MaarittelyLuokka.TULEVA, mk.haeMaarittelyLuokka(testList, "11"));
        assertEquals(MaarittelyLuokka.TULEVA, mk.haeMaarittelyLuokka(testList, "21"));
        assertEquals(MaarittelyLuokka.TULEVA, mk.haeMaarittelyLuokka(testList, "1000"));
        assertEquals(MaarittelyLuokka.EI_TUETTU, mk.haeMaarittelyLuokka(testList, "2"));
    }

    private void kokeileKumminkinPain(String a, String b, MaarittelyLuokka luokka, boolean tuettu) {
        LinkedList<String> testList = new LinkedList<String>();
        testList.add(a);
        testList.add(b);
        MaarittelyLuokka ml = mk.haeMaarittelyLuokka(testList, "1");
        assertEquals(luokka, ml);
        assertEquals(tuettu, ml.isTuettu());

        // Sama toisinpäin:
        testList.clear();
        testList.add(b);
        testList.add(a);
        ml = mk.haeMaarittelyLuokka(testList, "1");
        assertEquals(luokka, ml);
        assertEquals(tuettu, ml.isTuettu());
    }

    /**
     * Sisältää tulevan templateId:n ja nykyisen templateId:n -> TULEVA, Ei tuettu
     */
    @Test
    public void tulevaVsNykyinenTest() {
        // TULEVA, NYKYINEN
        kokeileKumminkinPain("1.2.246.777.11.2016.2", "1.2.246.777.11.2015.11", MaarittelyLuokka.TULEVA, false);
    }

    /**
     * Sisältää nykyisen templateId:n ja vanhan templateId:n -> VANHA, Tuettu
     */
    @Test
    public void nykyinenVsVanhaTest() {
        // VANHA, NYKYINEN
        kokeileKumminkinPain("1.2.246.777.11.2014.2", "1.2.246.777.11.2015.11", MaarittelyLuokka.VANHA, true);
    }

    /**
     * Sisältää tulevan templateId:n ja vanhan templateId:n -> TULEVA, Ei tuettu
     */
    @Test
    public void tulevaVsVanhaTest() {
        // TULEVA, VANHA
        kokeileKumminkinPain("1.2.246.777.11.2016.2", "1.2.246.777.11.2014.2", MaarittelyLuokka.TULEVA, false);
    }

    /**
     * Sisältää ei-tuetun templateId:n ja nykyisen templateId:n -> EI_TUETTU, Ei tuettu
     */
    @Test
    public void eiTuettuVsNykyinenTest() {
        // EI_TUETTU, NYKYINEN
        kokeileKumminkinPain("1.2.246.777.11.2007.1", "1.2.246.777.11.2015.11", MaarittelyLuokka.EI_TUETTU, false);
    }

    /**
     * Sisältää ei-tuetun templateId:n ja vanhan templateId:n -> EI_TUETTU, Ei tuettu
     */
    @Test
    public void eiTuettuVsVanhaTest() {
        // EI_TUETTU, VANHA
        kokeileKumminkinPain("1.2.246.777.11.2007.1", "1.2.246.777.11.2011.17", MaarittelyLuokka.EI_TUETTU, false);
    }

    /**
     * Sisältää ei-tuetun templateId:n ja tulevan templateId:n -> EI_TUETTU, Ei tuettu
     */
    @Test
    public void eiTuettuVsTulevaTest() {
        // EI_TUETTU, TULEVA
        kokeileKumminkinPain("1.2.246.777.11.2007.1", "1.2.246.777.11.2016.2", MaarittelyLuokka.EI_TUETTU, false);
    }
}
