package fi.kela.kanta.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import org.codehaus.plexus.util.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import junit.framework.TestCase;

//@RunWith(PowerMockRunner.class)
@RunWith(JUnit4.class)
@PowerMockIgnore({ "javax.management.*" })
public class OidGeneratorTest {

    private static final String dateFormat = "dd.MM.yyyy HH:mm:ss.S";
    private SimpleDateFormat sdf = null;
    // 12.08.2015 09:54:01.254
    // private static final long testDateTime = 1439362441254l;
    // 05.01.2014 04:20:44.444
    private static final long testDateTime = 1388888444444l;
    private OidGenerator generator;

    @Before
    public void setUp() {
        generator = OidGenerator.getInstance();
        sdf = new SimpleDateFormat(OidGeneratorTest.dateFormat);
    }

    /**
     * Testaa että saadaan järkevä sekvenssi arvo yleensä ja että samasta pohja-arvosta generoitu aika on yksilöllinen
     * sekin.
     */
    // Testi poistettu käytöstä, koska tätä ei käytetä ainakaan toistaiseksi varsinaisessa toteutuksessakaan
    @Ignore
    @Test
    public void testGettingYearlessMillis() {

        Date oidDatex = new Date(OidGeneratorTest.testDateTime);
        System.out.println(sdf.format(oidDatex));
        Date expected = null;
        String expectedDateTimeString = "05.01.1970 04:20:44.444";
        try {
            expected = sdf.parse(expectedDateTimeString);
        }
        catch (ParseException e) {
            Assert.fail("Could not parse expected datetime: " + e.getMessage());
        }
        System.out.println();

        String seq1 = generator.getYearlessMillis(OidGeneratorTest.testDateTime);
        Assert.assertNotNull(seq1);
        // System.out.println(OidGeneratorTest.testDateTime);
        // System.out.println(seq1);
        Date oidDate = new Date(new Long(seq1));
        Assert.assertEquals(expectedDateTimeString, sdf.format(oidDate));
    }

    @Test
    public void testGettingSVOid() {

        String oid = generator.createNewSvOid("123456789");
        Assert.assertNotNull(oid);
        TestCase.assertEquals("1.2.246.537.25.123456789", oid);
    }

    /**
     * Testaa että saadaan oid arvo yleensä ja että samasta pohja-arvosta generoitu aika on yksilöllinen sekin.
     */
    @Ignore
    @Test
    public void testGettingOid() {

        int year = 2015;
        GregorianCalendar greg = new GregorianCalendar();
        greg.set(Calendar.YEAR, year);
        String orgRootOid = "666";
        String orgYId = "555";
        String expectedStart = "1.2.246.666.555.93." + year + ".";
        String oid1 = generator.createNewDocumentOid("1.2.246.537.28.12345678");
        Assert.assertNotNull(oid1);
        Assert.assertTrue(oid1.startsWith(expectedStart));

        String oid2 = generator.createNewDocumentOid("1.2.246.537.28.12345678");
        Assert.assertNotNull(oid2);
        Assert.assertTrue(oid2.startsWith(expectedStart));

        Assert.assertFalse("Samasta millisekunnista luodut sekvenssit eivät saa olla samat", oid1.equals(oid2));

        int oid1Last = new Integer(oid1.charAt(oid1.length() - 1));
        int oid2Last = new Integer(oid2.charAt(oid2.length() - 1));
        if ( oid1Last == 9 ) {
            Assert.assertEquals(0, oid2Last);
        }
        else {
            Assert.assertEquals(oid1Last + 1, oid2Last);
        }
    }

    /**
     * Testaa että saadaan oidiin generoitu Y-tunnus on oikein.
     */
    @Test
    public void testYtunnuksenMuokkaus() {

        String ytunnus = "0123456-7";
        String muokattu = generator.modifyBusinessId(ytunnus);
        Assert.assertEquals(7, muokattu.length());
        Assert.assertEquals("1234567", muokattu);

        ytunnus = "1234567-8";
        muokattu = generator.modifyBusinessId(ytunnus);
        Assert.assertEquals(8, muokattu.length());
        Assert.assertEquals("12345678", muokattu);
    }

    /*
     * Muodostetulle viivakoodille pitää päteä seuraavat säännöt: Yleiset ehdot: . Solmujen lukumäärän oltava 8, 10 tai
     * 11 . 1. solmun oltava "1" . 2. solmun oltava "2" . 3. solmun oltava "246" . 4. solmun oltava joko "10" tai "537"
     * Jos 4. solmu on "10": . Solmujen lukumäärän oltava 8 . 5. solmun oltava 2-8 numeroa (käytetään viivakoodissa
     * y-tunnuksena) . 6. solmun oltava "93" tai "93xxx" jossa xxx on generaattorin tunniste 001, 002 jne (käytetään
     * viivakoodissa solmuluokkana) . 7. solmun oltava 4 numeroa (kahta viimeistä numeroa käytetään viivakoodissa
     * antovuoden sarjana) . 8. solmun oltava 1-37 numeroa (käytetään viivakoodissa juoksevana numerona) Jos 4. solmu on
     * "537": . Solmujen lukumäärän oltava 10 tai 11 . 5. solmun oltava "25" . 6. solmun oltava "1" . 7. solmun oltava
     * 1-8 numeroa (käytetään viivakoodissa yksilöintitunnuksena (sv-numerona)) . 8. solmun oltava "93" (käytetään
     * viivakoodissa solmuluokkana) . 9. solmun oltava 4 numeroa (kahta viimeistä numeroa käytetään viivakoodissa
     * antovuoden sarjana) Jos 4. solmu on "537" ja solmujen lukumäärä on 10: . 10. solmun oltava 1-37 numeroa
     * (käytetään viivakoodissa juoksevana numerona) Jos 4. solmu on "537" ja solmujen lukumäärä on 11: . 10. solmun
     * oltava 3-4 numeroa (käytetään viivakoodissa antopäivänä) . 11. solmun oltava 1-6 numeroa (käytetään viivakoodissa
     * kellonaikana)
     */
    @Test
    public void testViivakoodiKelpoisuus537rootOidille() {

        GregorianCalendar greg = new GregorianCalendar();
        int year = greg.get(Calendar.YEAR);
        greg.set(Calendar.YEAR, year);

        String orgRootOid = "537";

        String orgYId = "12345678";

        String expected = "1.2.246.537.25.1.12345678.93." + year + ".";

        String oid1 = generator.createNewDocumentOid("1.2.246.537.25.1.12345678");

        Assert.assertNotNull(oid1);
        Assert.assertTrue("oid alku '" + expected + "' Ei vastannut odotettua:" + oid1, oid1.startsWith(expected));

        StringTokenizer st = new StringTokenizer(oid1, ".");

        // Jos 4. solmu on "537": Solmujen lukumäärän oltava 10 tai 11
        TestCase.assertTrue(st.countTokens() == 10 || st.countTokens() == 11);

        boolean tenTokenOid = false;
        boolean elevenTokenOid = false;
        if ( st.countTokens() == 10 ) {
            tenTokenOid = true;
        }
        else if ( st.countTokens() == 11 ) {
            elevenTokenOid = true;
        }

        TestCase.assertTrue(tenTokenOid || elevenTokenOid);

        TestCase.assertEquals("1", st.nextToken());
        TestCase.assertEquals("2", st.nextToken());
        TestCase.assertEquals("246", st.nextToken());
        TestCase.assertEquals("537", st.nextToken());

        // 5. solmun oltava "25"
        TestCase.assertEquals("25", st.nextToken());

        // 6. solmun oltava "1"
        TestCase.assertEquals("1", st.nextToken());

        // 7. solmun oltava 1-8 numeroa (käytetään viivakoodissa yksilöintitunnuksena (sv-numerona))
        String seventhNode = st.nextToken();
        TestCase.assertTrue(seventhNode.length() > 0);
        TestCase.assertTrue("7.solmun pituus pitää olla max. 8: " + seventhNode, seventhNode.length() < 9);

        // 8. solmun oltava "93" (käytetään viivakoodissa solmuluokkana)
        TestCase.assertEquals("93", st.nextToken());

        // 9. solmun oltava 4 numeroa (kahta viimeistä numeroa käytetään viivakoodissa antovuoden sarjana)
        String ninthToken = st.nextToken();
        TestCase.assertEquals(4, ninthToken.length());
        TestCase.assertTrue(StringUtils.isNumeric(ninthToken));

        // Jos 4. solmu on "537" ja solmujen lukumäärä on 10:
        // 10. solmun oltava 1-37 numeroa (käytetään viivakoodissa juoksevana numerona)
        if ( tenTokenOid ) {
            String tenthToken = st.nextToken();
            TestCase.assertTrue(tenthToken.length() > 0);
            TestCase.assertTrue(tenthToken.length() < 38);
            TestCase.assertTrue(StringUtils.isNumeric(tenthToken));
        }

        // Jos 4. solmu on "537" ja solmujen lukumäärä on 11:
        // 10. solmun oltava 3-4 numeroa (käytetään viivakoodissa antopäivänä)
        // 11. solmun oltava 1-6 numeroa (käytetään viivakoodissa kellonaikana)
        if ( elevenTokenOid ) {
            String tenthToken = st.nextToken();
            TestCase.assertTrue(tenthToken.length() == 3 || tenthToken.length() == 4);

            String eleventhToken = st.nextToken();
            TestCase.assertTrue(eleventhToken.length() > 0);
            TestCase.assertTrue(eleventhToken.length() < 7);

            TestCase.assertTrue(StringUtils.isNumeric(eleventhToken));
        }

    }

    /**
     * Pikatesti että organisaation oid luodaan "oikein".
     */
    @Test
    public void testCreatingOrganisaatioOid() {

        String partialOid = "123456789";
        String oid = generator.createNewAMOOrganizationOid(partialOid);
        TestCase.assertEquals("1.2.246.537.30." + partialOid, oid);
    }
}
