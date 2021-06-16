package fi.kela.kanta.cda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.hl7.v3.AD;
import org.hl7.v3.AdxpCity;
import org.hl7.v3.AdxpCountry;
import org.hl7.v3.AdxpCounty;
import org.hl7.v3.AdxpPostalCode;
import org.hl7.v3.AdxpStreetAddressLine;
import org.hl7.v3.ED;
import org.hl7.v3.EN;
import org.hl7.v3.EnDelimiter;
import org.hl7.v3.EnFamily;
import org.hl7.v3.EnGiven;
import org.hl7.v3.EnPrefix;
import org.hl7.v3.EnSuffix;
import org.hl7.v3.ObjectFactory;
import org.hl7.v3.PN;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Organization;
import org.hl7.v3.POCDMT000040OrganizationPartOf;
import org.hl7.v3.PQ;
import org.hl7.v3.ST;
import org.hl7.v3.TEL;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import fi.kela.kanta.exceptions.PurkuException;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.OrganisaatioTO;
import fi.kela.kanta.to.OsoiteTO;
import hl7finland.LocalHeader;
import hl7finland.LocalHeader.SoftwareSupport;

public class PurkajaTest {

    protected static ObjectFactory of;
    private static final String testOid = "1.12.123.1234.12345.123456";
    private static final String testSetId = "1.12.123.1234.12345.54321";
    private static final String testTypeCode = "1";
    private static final String testTime = "20150604090912";
    private static final String testOrgId = "1.12.123.1234.12345";
    private static final String testOrgName = "Organization Name";
    private static final String testAddrStreet = "StreetAddress 1";
    private static final String testAddrPostalCode = "123456";
    private static final String testAddrCity = "City";
    private static final String testAddrCountry = "Country";
    private static final String testGiven1 = "Given1";
    private static final String testGiven2 = "Given2";
    private static final String testFamily = "Family";
    private static final String testQualifier = "CL";
    private static final String testPrefix = "Sr.";
    private static final String testSuffix = "Ph.D.";
    private static final String testDelimiter = "!";
    private static final String testPhone = "tel:1234566";
    private static final String testEmail = "mailto:test@test.org";
    private static final String testProduct = "kelain";
    private static final String testProductVersion = "1.0";

    public class PurkajaImpl extends Purkaja {
        public PurkajaImpl() throws ConfigurationException {

        }

        @Override
        protected String getCodeSystem() {
            return "testi";
        }
    }

    @BeforeClass
    public static void setupClass() {
        of = new ObjectFactory();
    }

    private List<PN> luoPNList() {
        PN nimi = new PN();
        EnGiven enGiven1 = of.createEnGiven();
        enGiven1.getContent().add(testGiven1);
        enGiven1.getQualifiers().add(testQualifier);
        nimi.getContent().add(of.createENGiven(enGiven1));
        EnGiven enGiven2 = of.createEnGiven();
        enGiven2.getContent().add(testGiven2);
        nimi.getContent().add(of.createENGiven(enGiven2));
        EnFamily enFamily = of.createEnFamily();
        enFamily.getContent().add(testFamily);
        nimi.getContent().add(of.createENFamily(enFamily));
        EnPrefix enPrefix = of.createEnPrefix();
        enPrefix.getContent().add(testPrefix);
        nimi.getContent().add(of.createENPrefix(enPrefix));
        EnSuffix enSuffix = of.createEnSuffix();
        enSuffix.getContent().add(testSuffix);
        nimi.getContent().add(of.createENSuffix(enSuffix));
        ArrayList<PN> nimet = new ArrayList<PN>();
        nimet.add(nimi);
        return nimet;
    }

    private AD luoTestiAD(String katu, String postinro, String kaupunki, String maa) {
        AD ad = of.createAD();
        if ( null != katu ) {
            AdxpStreetAddressLine street = of.createAdxpStreetAddressLine();
            street.getContent().add(katu);
            ad.getContent().add(of.createADStreetAddressLine(street));
        }
        if ( null != postinro ) {
            AdxpPostalCode postal = of.createAdxpPostalCode();
            postal.getContent().add(postinro);
            ad.getContent().add(of.createADPostalCode(postal));
        }
        if ( null != kaupunki ) {
            AdxpCity city = of.createAdxpCity();
            city.getContent().add(kaupunki);
            ad.getContent().add(of.createADCity(city));
        }
        if ( null != maa ) {
            AdxpCountry country = of.createAdxpCountry();
            country.getContent().add(maa);
            ad.getContent().add(of.createADCountry(country));
        }
        return ad;
    }

    @Test
    public void testPuraLeimakentat() throws Exception {
        PurkajaImpl purkaja = new PurkajaImpl();
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        cda.setId(of.createII());
        cda.setSetId(of.createII());
        cda.setCode(of.createCE());
        cda.setVersionNumber(of.createINT());
        cda.setEffectiveTime(of.createTS());

        cda.getId().setRoot(testOid);
        cda.getSetId().setRoot(testSetId);

        Whitebox.invokeMethod(purkaja, "puraLeimakentat", cda, laakemaaraysTO, true);
        assertEquals(testOid, laakemaaraysTO.getOid());
        assertEquals(testSetId, laakemaaraysTO.getSetId());
        assertEquals(-1, laakemaaraysTO.getCdaTyyppi());
        assertEquals(0, laakemaaraysTO.getVersio());
        assertNull(laakemaaraysTO.getAikaleima());

        cda.getCode().setCode("");
        cda.getVersionNumber().setValue(BigInteger.ONE);
        cda.getEffectiveTime().setValue("nulldate");
        Whitebox.invokeMethod(purkaja, "puraLeimakentat", cda, laakemaaraysTO, true);
        assertEquals(testOid, laakemaaraysTO.getOid());
        assertEquals(testSetId, laakemaaraysTO.getSetId());
        assertEquals(-1, laakemaaraysTO.getCdaTyyppi());
        assertEquals(1, laakemaaraysTO.getVersio());
        assertNull(laakemaaraysTO.getAikaleima());

        cda.getCode().setCode(testTypeCode);
        cda.getEffectiveTime().setValue(testTime);
        Whitebox.invokeMethod(purkaja, "puraLeimakentat", cda, laakemaaraysTO, false);
        assertEquals(testOid, laakemaaraysTO.getOid());
        assertEquals(testSetId, laakemaaraysTO.getSetId());
        assertEquals(1, laakemaaraysTO.getCdaTyyppi());
        assertEquals(1, laakemaaraysTO.getVersio());
        assertNotNull(laakemaaraysTO.getAikaleima());

        hl7finland.ObjectFactory hl7fiOF = new hl7finland.ObjectFactory();
        LocalHeader localHeader = hl7fiOF.createLocalHeader();
        SoftwareSupport software = new SoftwareSupport();
        software.setProduct(testProduct);
        software.setVersion(testProductVersion);
        localHeader.setSoftwareSupport(software);
        cda.setLocalHeader(localHeader);
        Whitebox.invokeMethod(purkaja, "puraLeimakentat", cda, laakemaaraysTO, false);
        assertEquals(testProduct, laakemaaraysTO.getProduct());
        assertEquals(testProductVersion, laakemaaraysTO.getProductVersion());
    }

    @Test
    public void puraLeimakentatJaMaarittelyKonfiguraatioTest() throws Exception {

        PurkajaImpl purkaja = new PurkajaImpl();
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        cda.setId(of.createII());
        cda.setSetId(of.createII());
        cda.setCode(of.createCE());
        cda.getCode().setCode("1");
        cda.setVersionNumber(of.createINT());
        cda.setEffectiveTime(of.createTS());

        cda.getId().setRoot(testOid);
        cda.getSetId().setRoot(testSetId);

        cda.getTemplateIds().add(of.createPOCDMT000040InfrastructureRootTemplateId());
        cda.getTemplateIds().get(0).setRoot("1.2.246.777.11.2015.11");

        Whitebox.invokeMethod(purkaja, "puraLeimakentat", cda, laakemaaraysTO);
        assertEquals(MaarittelyLuokka.NYKYINEN, laakemaaraysTO.getMaarittelyLuokka());
    }

    @Test
    public void testPuraKokoNimi() throws ConfigurationException {
        PurkajaImpl purkaja = new PurkajaImpl();
        KokoNimiTO kokonimi = purkaja.puraKokoNimi(luoPNList());
        assertEquals(testGiven1 + " " + testGiven2, kokonimi.getEtunimi());
        assertEquals(testFamily, kokonimi.getSukunimi());
    }

    private POCDMT000040Organization luoOrganisaatio(String nimi) {
        POCDMT000040Organization org = of.createPOCDMT000040Organization();
        org.getIds().add(of.createII());
        org.getIds().get(0).setRoot(testOrgId);
        org.getNames().add(of.createON());
        org.getNames().get(0).getContent().add(nimi);
        TEL puh = of.createTEL();
        TEL email = of.createTEL();
        puh.setValue(testPhone);
        email.setValue(testEmail);
        org.getTelecoms().add(puh);
        org.getTelecoms().add(email);
        org.getAddrs().add(luoTestiAD(testAddrStreet, testAddrPostalCode, testAddrCity, testAddrCountry));
        return org;
    }

    @Test
    public void testPuraOrganisaatio() throws ConfigurationException {
        PurkajaImpl purkaja = new PurkajaImpl();
        POCDMT000040Organization org = luoOrganisaatio(testOrgName);

        OrganisaatioTO organisaatio = purkaja.puraOrganisaatio(org);
        assertEquals(testOrgId, organisaatio.getYksilointitunnus());
        assertEquals(testOrgName, organisaatio.getNimi());
        assertNotNull(organisaatio.getOsoite());
        assertEquals("puhelinumeron tulee olla: " + testPhone, testPhone, organisaatio.getPuhelinnumero());
        assertEquals("sähköpostin tulee olla: " + testEmail, testEmail, organisaatio.getSahkoposti());
    }

    @Test
    public void testPuraOrganisaatioAsOf() throws ConfigurationException {
        PurkajaImpl purkaja = new PurkajaImpl();
        POCDMT000040Organization org = luoOrganisaatio(testOrgName);
        POCDMT000040OrganizationPartOf partOf = of.createPOCDMT000040OrganizationPartOf();
        String testPartOfName = testOrgName + "_PARENT";
        partOf.setWholeOrganization(luoOrganisaatio(testPartOfName));
        org.setAsOrganizationPartOf(partOf);

        OrganisaatioTO organisaatio = purkaja.puraOrganisaatio(org);
        assertEquals(testOrgId, organisaatio.getYksilointitunnus());
        assertEquals(testOrgName, organisaatio.getNimi());
        assertNotNull(organisaatio.getOsoite());
        assertEquals("puhelinumeron tulee olla: " + testPhone, testPhone, organisaatio.getPuhelinnumero());
        assertEquals("sähköpostin tulee olla: " + testEmail, testEmail, organisaatio.getSahkoposti());
        assertEquals("asPartOfOrg namen tulee olla: " + testPartOfName, testPartOfName,
                organisaatio.getToimintaYksikko().getNimi());

    }

    @Test
    public void testPuraOsoite() throws ConfigurationException {
        PurkajaImpl purkaja = new PurkajaImpl();
        AD addr = luoTestiAD(testAddrStreet, testAddrPostalCode, testAddrCity, testAddrCountry);
        AdxpCounty county = of.createAdxpCounty();
        county.getContent().add("diipadaapa");
        addr.getContent().add(of.createADCounty(county));

        OsoiteTO osoite = purkaja.puraOsoite(addr);
        assertNotNull(osoite);
        assertEquals(testAddrStreet, osoite.getKatuosoite());
        assertEquals(testAddrPostalCode, osoite.getPostinumero());
        assertEquals(testAddrCity, osoite.getPostitoimipaikka());
        assertEquals(testAddrCountry, osoite.getMaa());
        osoite = purkaja.puraOsoite(luoTestiAD(null, null, null, null));
        assertNotNull(osoite);
        assertNull(osoite.getKatuosoite());
        assertNull(osoite.getPostinumero());
        assertNull(osoite.getPostitoimipaikka());
        assertNull(osoite.getMaa());
    }

    @Test
    public void testPuraAika() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        PurkajaImpl purkaja = new PurkajaImpl();
        String testiAika1 = "2015";
        String testiAika2 = "201506";
        String testiAika3 = "20150603";
        String testiAika4 = "2015060308";
        String testiAika5 = "201506030845";
        String testiAika6 = "20150603084515";
        String testiAika7 = "2015060308451511";

        Date date = purkaja.puraAika(testiAika1);
        assertEquals(testiAika1 + "0101000000", dateFormat.format(date));
        date = purkaja.puraAika(testiAika2);
        assertEquals(testiAika2 + "01000000", dateFormat.format(date));
        date = purkaja.puraAika(testiAika3);
        assertEquals(testiAika3 + "000000", dateFormat.format(date));
        date = purkaja.puraAika(testiAika4);
        assertEquals(testiAika4 + "0000", dateFormat.format(date));
        date = purkaja.puraAika(testiAika5);
        assertEquals(testiAika5 + "00", dateFormat.format(date));
        date = purkaja.puraAika(testiAika6);
        assertEquals(testiAika6, dateFormat.format(date));
        date = purkaja.puraAika(testiAika7);
        assertEquals(testiAika6, dateFormat.format(date));
    }

    @Test
    public void testPuraContent() throws ConfigurationException {
        PurkajaImpl purkaja = new PurkajaImpl();
        String testString = "TEST";
        ED ed = of.createED();
        EN en = of.createEN();
        ST st = of.createST();
        PN pn = of.createPN();
        AD ad = of.createAD();
        ed.getContent().add(testString);
        en.getContent().add(testString);
        st.getContent().add(testString);
        pn.getContent().add(testString);
        ad.getContent().add(testString);
        EN enNull = null;

        assertEquals("puraContent pitää palauttaa " + testString, testString, purkaja.puraContent(ed));
        assertEquals("puraContent pitää palauttaa " + testString, testString, purkaja.puraContent(en));
        assertEquals("puraContent pitää palauttaa " + testString, testString, purkaja.puraContent(st));
        assertEquals("puraContent pitää palauttaa " + testString, testString, purkaja.puraContent(pn));
        assertEquals("puraContent pitää palauttaa " + testString, testString, purkaja.puraContent(ad));
        assertNull("puraContent pitää palauttaa null", purkaja.puraContent(of.createANYNonNull()));
        assertNull("puraContent pitää palauttaa null", purkaja.puraContent(of.createPQ()));
        assertNull("puraContent pitää palauttaa null", purkaja.puraContent(null));
        assertNull("puraContent pitää palauttaa null", purkaja.puraContent(enNull));
        assertNull("puraContent pitää palauttaa null", purkaja.puraContent(of.createEN()));
        assertNull("puraContent pitää palauttaa null", purkaja.puraContent(of.createAD()));
        assertNull("puraContent pitää palauttaa null", purkaja.puraContent(of.createED()));
    }

    @Test
    public void testPuraAika_withNullOrInvalidInput() throws Exception {
        PurkajaImpl purkaja = new PurkajaImpl();

        Date date = purkaja.puraAika(null);
        assertNull(date);
        date = purkaja.puraAika("Kissa");
        assertNull(date);
    }

    @Test
    public void testPuraNimitieto() throws Exception {
        PurkajaImpl purkaja = new PurkajaImpl();
        KokoNimiTO kokoNimiTO = new KokoNimiTO();
        EnGiven enGiven1 = of.createEnGiven();
        enGiven1.getContent().add(testGiven1);
        enGiven1.getQualifiers().add(testQualifier);
        EnGiven enGiven2 = of.createEnGiven();
        enGiven2.getContent().add(testGiven2);
        EnFamily enFamily = of.createEnFamily();
        enFamily.getContent().add(testFamily);
        EnPrefix enPrefix = of.createEnPrefix();
        enPrefix.getContent().add(testPrefix);
        EnSuffix enSuffix = of.createEnSuffix();
        enSuffix.getContent().add(testSuffix);
        EnDelimiter enDelimiter = of.createEnDelimiter();
        enDelimiter.getContent().add(testDelimiter);

        Whitebox.invokeMethod(purkaja, "puraNimitieto", enGiven1, kokoNimiTO);
        assertEquals("etunimen pitää olla " + testGiven1, testGiven1, kokoNimiTO.getEtunimi());
        kokoNimiTO = new KokoNimiTO();
        Whitebox.invokeMethod(purkaja, "puraNimitieto", enGiven2, kokoNimiTO);
        assertEquals("etunimen pitää olla " + testGiven2, testGiven2, kokoNimiTO.getEtunimi());
        Whitebox.invokeMethod(purkaja, "puraNimitieto", enFamily, kokoNimiTO);
        assertEquals("sukunimen pitää olla " + testFamily, testFamily, kokoNimiTO.getSukunimi());
        Whitebox.invokeMethod(purkaja, "puraNimitieto", enPrefix, kokoNimiTO);
        Whitebox.invokeMethod(purkaja, "puraNimitieto", enSuffix, kokoNimiTO);
        Whitebox.invokeMethod(purkaja, "puraNimitieto", enDelimiter, kokoNimiTO);
        assertEquals("kokonimen pitää olla " + testFamily + ", " + testGiven2 + ", " + testSuffix,
                testFamily + ", " + testGiven2 + ", " + testSuffix, kokoNimiTO.getKokoNimi());
    }

	@Test
	public void testPuraIntegerValue_Null() throws Exception {
		Purkaja purkaja = new PurkajaImpl();
		assertNull(purkaja.puraIntegerValue(null));
	}

	@Test
	public void testPuraIntegerValue_NullValue() throws Exception {
		Purkaja purkaja = new PurkajaImpl();
		PQ value = of.createPQ();
		assertNull(purkaja.puraIntegerValue(value));
	}

	@Test
	public void testPuraIntegerValue_BlankValue() throws Exception {
		Purkaja purkaja = new PurkajaImpl();
		PQ value = of.createPQ();
		value.setValue(" ");
		assertNull(purkaja.puraIntegerValue(value));
	}

	@Test(expected = PurkuException.class)
	public void testPuraIntegerValue_WrongValue() throws Exception {
		Purkaja purkaja = new PurkajaImpl();
		PQ value = of.createPQ();
		value.setValue("ABC");
		purkaja.puraIntegerValue(value);
	}

	@Test
	public void testPuraIntegerValue() throws Exception {
		Purkaja purkaja = new PurkajaImpl();
		PQ value = of.createPQ();
		value.setValue("1");
		assertEquals(Integer.valueOf(1), purkaja.puraIntegerValue(value));
	}

	@Test
	public void testPuraDoubleValue_Null() throws Exception {
		Purkaja purkaja = new PurkajaImpl();
		assertNull(purkaja.puraDoubleValue(null));
	}

	@Test
	public void testPuraDoubleValue_NullValue() throws Exception {
		Purkaja purkaja = new PurkajaImpl();
		PQ value = of.createPQ();
		assertNull(purkaja.puraDoubleValue(value));
	}

	@Test
	public void testPuraDoubleValue_BlankValue() throws Exception {
		Purkaja purkaja = new PurkajaImpl();
		PQ value = of.createPQ();
		value.setValue(" ");
		assertNull(purkaja.puraDoubleValue(value));
	}

	@Test(expected = PurkuException.class)
	public void testPuraDoubleValue_WrongValue() throws Exception {
		Purkaja purkaja = new PurkajaImpl();
		PQ value = of.createPQ();
		value.setValue("ABC");
		purkaja.puraDoubleValue(value);
	}

	@Test
	public void testPuraDoubleValue() throws Exception {
		Purkaja purkaja = new PurkajaImpl();
		PQ value = of.createPQ();
		value.setValue("1.25");
		assertEquals(Double.valueOf(1.25), purkaja.puraDoubleValue(value));
	}
	
    @Test
    public void testPuraKellonaika() throws Exception {
         DateTimeFormatter  sdf = DateTimeFormatter.ofPattern("HHmm");

        PurkajaImpl purkaja = new PurkajaImpl();
        String testiAika1 = "2015";
        String testiAika2 = "201506";
        String testiAika3 = "abcs";
        String testiAika4 = "8:30";
        String testiAika5 = "2515";
        String testiAika6 = "-234";
        String testiAika7 = null;
        String testiAika8 = "0001";

        LocalTime date = purkaja.puraKellonaika(testiAika1);
        assertEquals(testiAika1, sdf.format(date));
        date = purkaja.puraKellonaika(testiAika2);
        assertNull(date);
        date = purkaja.puraKellonaika(testiAika3);
        assertNull(date);
        date = purkaja.puraKellonaika(testiAika4);
        assertNull(date);
        try {
        	date = purkaja.puraKellonaika(testiAika5);
        	fail("Odotettiin poikkeusta");
        } catch (Exception e) {
			// Odotettu tulos
		}
        date = purkaja.puraKellonaika(testiAika6);
        assertNull(date);
        date = purkaja.puraKellonaika(testiAika7);
        assertNull(date);
        date = purkaja.puraKellonaika(testiAika8);
        assertEquals(testiAika8, sdf.format(date));
    }

}
