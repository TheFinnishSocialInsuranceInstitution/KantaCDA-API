package fi.kela.kanta.exceptions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import junit.framework.TestCase;

@RunWith(JUnit4.class)
public class VirheellisetTiedotExceptionTest {

    @Test
    public void testVirheviestinKoostaminen() {
        VirheellisetTiedotException vte = new VirheellisetTiedotException();
        vte.addVirhe("testi.virhe.1", "testi.arvo.1");
        vte.addVirhe("testi.virhe.2", "testi.arvo.2");

        TestCase.assertEquals(
                "Objektissa seuraavia virheitä [testi.virhe.1: testi.arvo.1, testi.virhe.2: testi.arvo.2]",
                vte.getMessage());

        VirheellisetTiedotException vte2 = new VirheellisetTiedotException("TestiObjekti");
        vte2.addVirhe("testi.virhe.1", "testi.arvo.1");
        vte2.addVirhe("testi.virhe.2", "testi.arvo.2");

        TestCase.assertEquals(
                "Objektissa 'TestiObjekti' seuraavia virheitä [testi.virhe.1: testi.arvo.1, testi.virhe.2: testi.arvo.2]",
                vte2.getMessage());
    }
}
