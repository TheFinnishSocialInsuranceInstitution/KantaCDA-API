package fi.kela.kanta.cda;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import org.hl7.v3.AD;
import org.hl7.v3.CD;
import org.hl7.v3.II;
import org.hl7.v3.PN;
import org.hl7.v3.POCDMT000040Author;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component5;
import org.hl7.v3.POCDMT000040InfrastructureRootTemplateId;
import org.hl7.v3.POCDMT000040Organization;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.OrganisaatioTO;
import fi.kela.kanta.to.OsoiteTO;
import fi.kela.kanta.util.OidGenerator;
import junit.framework.TestCase;

@RunWith(JUnit4.class)
@PrepareForTest({ Kasaaja.class })
public class KasaajaTest {

    private static String testKey = "TEST";
    private static LaakemaaraysTO leimakentat;

    public class KasaajaImpl extends Kasaaja {

        public KasaajaImpl(Properties properties) {
            super(properties);

        }

        @Override
        protected String getTypeKey() {
            return KasaajaTest.testKey;
        }

    }

    @BeforeClass
    public static void setupClass() {
        KasaajaTest.leimakentat = new LaakemaaraysTO();
        KasaajaTest.leimakentat.setCDAOidBase(KantaCDATestUtils.testBaseId + ".537.25.1.12345678.93.");
        // KasaajaTest.leimakentat.setOrgRootOid("537");
        // KasaajaTest.leimakentat.setOrgYTunnus("1234567-8");
    }

    private void setupProperties() {
        KantaCDATestUtils.setupProperties();
    }

    // sekvenssin resetointi on testissä tarkoituksella
    @SuppressWarnings("deprecation")
    private KasaajaImpl setupKasaaja() {
        KantaCDATestUtils.mockProperty("id.root", KantaCDATestUtils.testIdRoot);
        KantaCDATestUtils.mockProperty(Kasaaja.BASE_OID_PROPERTY, KantaCDATestUtils.testBaseId);
        OidGenerator.resetSequence();
        return new KasaajaImpl(KantaCDATestUtils.getProperties());
    }

    @Before
    public void setUp() {
        setupProperties();

    }

    @Test
    public void testKasaajaConstructor() {
        KasaajaImpl tested = setupKasaaja();
        Assert.assertNotNull(tested);
        Assert.assertNotNull(tested.of);

        String rootOid = KantaCDATestUtils.testBaseId + ".537.25.1.12345678.93.";
        String generated = tested.getDocumentId(KasaajaTest.leimakentat);
        Assert.assertTrue("Odotettiin root oidin alkavan '" + rootOid + "', mutta on '" + generated + "'",
                generated.startsWith(rootOid));
        // Assert.assertEquals(KantaCDATestUtils.testBaseId, tested.getDocumentId(KasaajaTest.leimakentta).substring(0,
        // 9));
    }

    @Test
    public void testGetId() {
        KasaajaImpl tested = setupKasaaja();
        String id = tested.getId(KasaajaTest.leimakentat);
        TestCase.assertTrue(id + " ei sisällä oikeaa root oid:tä",
                id.startsWith(tested.getDocumentId(KasaajaTest.leimakentat)));
        TestCase.assertTrue(id + " ei lopu ykköseen", id.endsWith(".1"));
    }

    @Test
    public void testGetOID() {
        KasaajaImpl tested = setupKasaaja();
        String id = tested.getOID(KasaajaTest.leimakentat);
        TestCase.assertTrue(id + " ei ala 'OID' tekstillä", id.startsWith("OID"));
    }

    @Test
    public void testGetNextIdJaOid() {
        KasaajaImpl tested = setupKasaaja();

        String id = tested.getNextId(KasaajaTest.leimakentat);
        String lastNumber = id.substring(id.length() - 1, id.length());
        id = tested.getNextId(KasaajaTest.leimakentat);
        TestCase.assertFalse(id + " Ei pitäisi loppua ykkösellä", id.endsWith(".1"));
        TestCase.assertFalse(
                id + " Oid:in lopun juoksevan numeron ei pitäisi olla sama kuin edellisen oid:in '" + lastNumber + "'",
                id.endsWith(lastNumber));

        // Kaksi testiä yhdistetty, koska OidGenerator instanssi on molemmille sama
        id = tested.getNextOID(KasaajaTest.leimakentat);
        TestCase.assertTrue(id + " ei ala 'OID' tekstillä", id.startsWith("OID"));
        int last = new Integer(id.substring(id.length() - 1, id.length()));
        TestCase.assertTrue("Juoksevan numeron pitäisi olla isompi kuin '2'", last > 2);
        id = tested.getNextOID(KasaajaTest.leimakentat);
        TestCase.assertTrue(id + " ei ala 'OID' tekstillä", id.startsWith("OID"));

        last = new Integer(id.substring(id.length() - 1, id.length()));
        TestCase.assertTrue("Juoksevan numeron pitäisi olla isompi kuin '2'", last > 3);
    }

    @Test
    public void testGetNextObservationID() {
        KasaajaImpl tested = setupKasaaja();
        Assert.assertEquals(tested.getDocumentId(KasaajaTest.leimakentat) + ".3.1.100",
                tested.getNextObservationID(KasaajaTest.leimakentat));
    }

    @Test
    public void testAddIdFields() {

        POCDMT000040ClinicalDocument cda = new POCDMT000040ClinicalDocument();
        String testCode = "testiKoodi";
        String effectiveTimeValue = "test_effectiveTimeValue_test";
        String templateId2 = "12.34.56.78.2";
        String templateId3 = "12.34.56.78.3";
        KantaCDATestUtils.mockProperty("realmCode.code", testCode);
        KantaCDATestUtils.mockProperty("typeId.root", KantaCDATestUtils.testIdRoot);
        KantaCDATestUtils.mockProperty("templateId1", KantaCDATestUtils.testIdRoot);
        KantaCDATestUtils.mockProperty("templateId1.root", KantaCDATestUtils.testIdRoot);
        KantaCDATestUtils.mockProperty("templateId2.root", templateId2);
        KantaCDATestUtils.mockProperty("templateId3.root", templateId3);
        KantaCDATestUtils.mockCodeProperty("TEST.code", testCode, KantaCDATestUtils.testCodeSystem,
                KantaCDATestUtils.testCodeSystemName, KantaCDATestUtils.testDisplayName);
        KantaCDATestUtils.mockProperty("TEST.title", KantaCDATestUtils.testIdRoot);
        KantaCDATestUtils.mockProperty("confidentialityCode.code", KantaCDATestUtils.testIdRoot);
        KantaCDATestUtils.mockProperty("TEST.languageCode.code", KantaCDATestUtils.testIdRoot);

        KasaajaImpl tested = setupKasaaja();
        tested.addIdFields(cda, KasaajaTest.leimakentat, effectiveTimeValue);
        Assert.assertEquals(testCode, cda.getRealmCodes().get(0).getCode());
        Assert.assertEquals(effectiveTimeValue, cda.getEffectiveTime().getValue());
        Iterator<POCDMT000040InfrastructureRootTemplateId> templateIdIter = cda.getTemplateIds().iterator();
        Assert.assertTrue(templateIdIter.next().getRoot().equals(KantaCDATestUtils.testIdRoot));
        Assert.assertTrue(templateIdIter.next().getRoot().equals(templateId2));
        Assert.assertTrue(templateIdIter.next().getRoot().equals(templateId3));

        // Tarkista että oid ja setid generoidaan kun ne on null leimakentissä
        String oid = cda.getId().getRoot();
        Assert.assertNotNull("Oid ei saa olla tyhjä", oid);
        Assert.assertTrue(effectiveTimeValue, oid.startsWith("1.2.246.537."));
        String setId = cda.getSetId().getRoot();
        Assert.assertTrue(effectiveTimeValue, setId.startsWith("1.2.246.537."));
        Assert.assertSame(
                "Generoidun Oid:in ja setId:n pitäisi olla samat, nyt Oid '" + oid + "' ja setId '" + setId + "'", oid,
                setId);

        // Tarkista että käytetään ennalta annettua Oidia ja setId:tä
        LaakemaaraysTO tmpLeimakentat = new LaakemaaraysTO();
        oid = "test.oid";
        tmpLeimakentat.setOid(oid);
        setId = "test.setid";
        tmpLeimakentat.setSetId(setId);

        cda = new POCDMT000040ClinicalDocument();
        tested.addIdFields(cda, tmpLeimakentat, effectiveTimeValue);

        Assert.assertNotNull("Oid ei saa olla tyhjä", cda.getId().getRoot());
        Assert.assertEquals(oid, cda.getId().getRoot());
        Assert.assertEquals(setId, cda.getSetId().getRoot());
    }

    @Test
    public void testAddRecordTarget_Hetu_Mies() {
        POCDMT000040ClinicalDocument cda = new POCDMT000040ClinicalDocument();
        HenkilotiedotTO henkilotiedot = new HenkilotiedotTO(
                new KokoNimiTO(KantaCDATestUtils.testEtunimi, KantaCDATestUtils.testSukunimi),
                KantaCDATestUtils.testHetu);
        KasaajaImpl tested = null;
        try {
            tested = new KasaajaImpl(KantaCDATestUtils.loadProperties("testi_properties.properties"));
        }
        catch (IOException e) {
            e.printStackTrace();
            TestCase.fail("Ei kyetty lataamaan property tiedostoa");
        }
        tested.addRecordTarget(cda, henkilotiedot);
        Assert.assertEquals(KantaCDATestUtils.testHetu,
                cda.getRecordTargets().get(0).getPatientRole().getIds().get(0).getExtension());
        Assert.assertEquals("1",
                cda.getRecordTargets().get(0).getPatientRole().getPatient().getAdministrativeGenderCode().getCode());
        Assert.assertEquals("Mies", cda.getRecordTargets().get(0).getPatientRole().getPatient()
                .getAdministrativeGenderCode().getDisplayName());
        Assert.assertEquals("1.2.246.537.5.1.1997", cda.getRecordTargets().get(0).getPatientRole().getPatient()
                .getAdministrativeGenderCode().getCodeSystem());
        Assert.assertEquals("AR/YDIN - Sukupuoli", cda.getRecordTargets().get(0).getPatientRole().getPatient()
                .getAdministrativeGenderCode().getCodeSystemName());

    }

    @Test
    public void testAddRecordTarget_Hetu_Nainen() {
        POCDMT000040ClinicalDocument cda = new POCDMT000040ClinicalDocument();
        String femHetu = "250787-358S";
        HenkilotiedotTO henkilotiedot = new HenkilotiedotTO(new KokoNimiTO("Pirkko", KantaCDATestUtils.testSukunimi),
                femHetu);
        KasaajaImpl tested = null;
        try {
            tested = new KasaajaImpl(KantaCDATestUtils.loadProperties("testi_properties.properties"));
        }
        catch (IOException e) {
            e.printStackTrace();
            TestCase.fail("Ei kyetty lataamaan property tiedostoa");
        }
        tested.addRecordTarget(cda, henkilotiedot);
        Assert.assertEquals(femHetu, cda.getRecordTargets().get(0).getPatientRole().getIds().get(0).getExtension());
        Assert.assertEquals("2",
                cda.getRecordTargets().get(0).getPatientRole().getPatient().getAdministrativeGenderCode().getCode());
        Assert.assertEquals("Nainen", cda.getRecordTargets().get(0).getPatientRole().getPatient()
                .getAdministrativeGenderCode().getDisplayName());
        Assert.assertEquals("1.2.246.537.5.1.1997", cda.getRecordTargets().get(0).getPatientRole().getPatient()
                .getAdministrativeGenderCode().getCodeSystem());
        Assert.assertEquals("AR/YDIN - Sukupuoli", cda.getRecordTargets().get(0).getPatientRole().getPatient()
                .getAdministrativeGenderCode().getCodeSystemName());
    }

    @Test
    public void testAddRecordTarget_Ei_Hetua_Tuntematon() {
        POCDMT000040ClinicalDocument cda = new POCDMT000040ClinicalDocument();
        HenkilotiedotTO henkilotiedot = new HenkilotiedotTO(new KokoNimiTO("Pirkko", KantaCDATestUtils.testSukunimi),
                "01.01.1956", new Integer(0));
        KasaajaImpl tested = null;
        try {
            tested = new KasaajaImpl(KantaCDATestUtils.loadProperties("testi_properties.properties"));
        }
        catch (IOException e) {
            e.printStackTrace();
            TestCase.fail("Ei kyetty lataamaan property tiedostoa");
        }
        tested.addRecordTarget(cda, henkilotiedot);
        Assert.assertEquals("0",
                cda.getRecordTargets().get(0).getPatientRole().getPatient().getAdministrativeGenderCode().getCode());
        Assert.assertEquals("Tuntematon", cda.getRecordTargets().get(0).getPatientRole().getPatient()
                .getAdministrativeGenderCode().getDisplayName());
        Assert.assertEquals("1.2.246.537.5.1.1997", cda.getRecordTargets().get(0).getPatientRole().getPatient()
                .getAdministrativeGenderCode().getCodeSystem());
        Assert.assertEquals("AR/YDIN - Sukupuoli", cda.getRecordTargets().get(0).getPatientRole().getPatient()
                .getAdministrativeGenderCode().getCodeSystemName());
    }

    @Test
    public void testAddRecordTarget_Ei_Hetua_Mies() {
        POCDMT000040ClinicalDocument cda = new POCDMT000040ClinicalDocument();
        HenkilotiedotTO henkilotiedot = new HenkilotiedotTO(new KokoNimiTO("Pirkko", KantaCDATestUtils.testSukunimi),
                "01.01.1956", new Integer(1));
        KasaajaImpl tested = null;
        try {
            tested = new KasaajaImpl(KantaCDATestUtils.loadProperties("testi_properties.properties"));
        }
        catch (IOException e) {
            e.printStackTrace();
            TestCase.fail("Ei kyetty lataamaan property tiedostoa");
        }
        tested.addRecordTarget(cda, henkilotiedot);
        Assert.assertEquals("1",
                cda.getRecordTargets().get(0).getPatientRole().getPatient().getAdministrativeGenderCode().getCode());
        Assert.assertEquals("Mies", cda.getRecordTargets().get(0).getPatientRole().getPatient()
                .getAdministrativeGenderCode().getDisplayName());
        Assert.assertEquals("1.2.246.537.5.1.1997", cda.getRecordTargets().get(0).getPatientRole().getPatient()
                .getAdministrativeGenderCode().getCodeSystem());
        Assert.assertEquals("AR/YDIN - Sukupuoli", cda.getRecordTargets().get(0).getPatientRole().getPatient()
                .getAdministrativeGenderCode().getCodeSystemName());
    }

    @Test
    public void testAddRecordTarget_Ei_Hetua_Nainen() {
        POCDMT000040ClinicalDocument cda = new POCDMT000040ClinicalDocument();
        HenkilotiedotTO henkilotiedot = new HenkilotiedotTO(new KokoNimiTO("Pirkko", KantaCDATestUtils.testSukunimi),
                "01.01.1956", new Integer(2));
        KasaajaImpl tested = null;
        try {
            tested = new KasaajaImpl(KantaCDATestUtils.loadProperties("testi_properties.properties"));
        }
        catch (IOException e) {
            e.printStackTrace();
            TestCase.fail("Ei kyetty lataamaan property tiedostoa");
        }
        tested.addRecordTarget(cda, henkilotiedot);
        Assert.assertEquals("2",
                cda.getRecordTargets().get(0).getPatientRole().getPatient().getAdministrativeGenderCode().getCode());
        Assert.assertEquals("Nainen", cda.getRecordTargets().get(0).getPatientRole().getPatient()
                .getAdministrativeGenderCode().getDisplayName());
        Assert.assertEquals("1.2.246.537.5.1.1997", cda.getRecordTargets().get(0).getPatientRole().getPatient()
                .getAdministrativeGenderCode().getCodeSystem());
        Assert.assertEquals("AR/YDIN - Sukupuoli", cda.getRecordTargets().get(0).getPatientRole().getPatient()
                .getAdministrativeGenderCode().getCodeSystemName());
    }

    @Test
    public void testAddRecordTarget_Ei_Hetua_Maarittamatta() {
        POCDMT000040ClinicalDocument cda = new POCDMT000040ClinicalDocument();
        HenkilotiedotTO henkilotiedot = new HenkilotiedotTO(new KokoNimiTO("Pirkko", KantaCDATestUtils.testSukunimi),
                "01.01.1956", new Integer(9));
        KasaajaImpl tested = null;
        try {
            tested = new KasaajaImpl(KantaCDATestUtils.loadProperties("testi_properties.properties"));
        }
        catch (IOException e) {
            e.printStackTrace();
            TestCase.fail("Ei kyetty lataamaan property tiedostoa");
        }
        tested.addRecordTarget(cda, henkilotiedot);
        Assert.assertEquals("9",
                cda.getRecordTargets().get(0).getPatientRole().getPatient().getAdministrativeGenderCode().getCode());
        Assert.assertEquals("Määrittelemättä", cda.getRecordTargets().get(0).getPatientRole().getPatient()
                .getAdministrativeGenderCode().getDisplayName());
        Assert.assertEquals("1.2.246.537.5.1.1997", cda.getRecordTargets().get(0).getPatientRole().getPatient()
                .getAdministrativeGenderCode().getCodeSystem());
        Assert.assertEquals("AR/YDIN - Sukupuoli", cda.getRecordTargets().get(0).getPatientRole().getPatient()
                .getAdministrativeGenderCode().getCodeSystemName());
    }

    @Test
    public void testAddAuthor() {
        POCDMT000040ClinicalDocument cda = new POCDMT000040ClinicalDocument();

        KasaajaImpl tested = setupKasaaja();
        tested.addAuthor(cda);

        Assert.assertNotNull(cda.getAuthors().get(0));
        Assert.assertNotNull(cda.getAuthors().get(0).getNullFlavors().get(0));
        Assert.assertEquals("NA", cda.getAuthors().get(0).getNullFlavors().get(0));
        Assert.assertEquals("NA", cda.getAuthors().get(0).getTime().getNullFlavors().get(0));
        Assert.assertNotNull(cda.getAuthors().get(0).getAssignedAuthor().getIds().get(0).getNullFlavors().get(0));
        Assert.assertEquals("NA", cda.getAuthors().get(0).getAssignedAuthor().getIds().get(0).getNullFlavors().get(0));
    }

    @Test
    public void testAddAuthorElement() {
        POCDMT000040ClinicalDocument cda = new POCDMT000040ClinicalDocument();
        POCDMT000040Author author = new POCDMT000040Author();

        KasaajaImpl tested = setupKasaaja();
        tested.addAuthor(cda, author);
        Assert.assertEquals(author, cda.getAuthors().get(0));
    }

    @Test
    public void testLuoAuthor() {
        String erikoisala = "erikoisala";
        String erikoisalaNimi = "erikoisalanimi";
        String virkaNimike = "virkanimike";
        String oppiarvo = "koppiarvo";
        String oppiarvoNimi = "oppiarvonimi";
        String ammattioikeus = "ammattioikeus";
        String ammattioikeusNimi = "ammattioikeusNimi";

        AmmattihenkiloTO ammattihenkiloTO = KantaCDATestUtils.luoAmmattihenkilo();
        ammattihenkiloTO.setErikoisala(erikoisala);
        ammattihenkiloTO.setErikoisalaName(erikoisalaNimi);
        ammattihenkiloTO.setVirkanimike(virkaNimike);
        ammattihenkiloTO.setOppiarvo(oppiarvo);
        ammattihenkiloTO.setOppiarvoTekstina(oppiarvoNimi);
        ammattihenkiloTO.setAmmattioikeus(ammattioikeus);
        ammattihenkiloTO.setAmmattioikeusName(ammattioikeusNimi);
        ammattihenkiloTO.setKirjautumisaika(new Date());

        OrganisaatioTO organisaatioTO = KantaCDATestUtils.luoOrganisaatio();
        KantaCDATestUtils.mockProperty("recordTarget.patientRole.id.root", KantaCDATestUtils.testIdRoot);
        KantaCDATestUtils.mockCodeProperty("author.functionCode", KantaCDATestUtils.testCode,
                KantaCDATestUtils.testCodeSystem, KantaCDATestUtils.testCodeSystemName, null);
        KantaCDATestUtils.mockProperty("author.id.svnumero.root", KantaCDATestUtils.testIdRoot);
        KantaCDATestUtils.mockProperty("author.id.terhikki.root", KantaCDATestUtils.testIdRoot);
        KantaCDATestUtils.mockCodeProperty("author.assignedAuthor.code", null, KantaCDATestUtils.testCodeSystem,
                KantaCDATestUtils.testCodeSystemName, null);
        KantaCDATestUtils.mockCodeProperty("author.assignedAuthor.code.translation.qualifier.title",
                KantaCDATestUtils.testCode, KantaCDATestUtils.testCodeSystem, null, KantaCDATestUtils.testDisplayName);
        KantaCDATestUtils.mockCodeProperty("author.assignerAuthor.code.translation.qualifier.degree",
                KantaCDATestUtils.testCode, KantaCDATestUtils.testCodeSystem, null, KantaCDATestUtils.testDisplayName);
        KantaCDATestUtils.mockCodeProperty("author.assignerAuthor.code.translation.qualifier.name",
                KantaCDATestUtils.testCode, KantaCDATestUtils.testCodeSystem, null, KantaCDATestUtils.testDisplayName);
        KantaCDATestUtils.mockCodeProperty("author.assignerAuthor.code.translation.qualifier.value",
                KantaCDATestUtils.testCode, KantaCDATestUtils.testCodeSystem, null, KantaCDATestUtils.testDisplayName);

        ammattihenkiloTO.setOrganisaatio(organisaatioTO);

        KasaajaImpl tested = setupKasaaja();
        POCDMT000040Author author = tested.luoAuthor(ammattihenkiloTO);

        Assert.assertEquals(KantaCDATestUtils.testRooli, author.getFunctionCode().getCode());
        Assert.assertEquals(2, author.getAssignedAuthor().getIds().size());
    }

    @Test
    public void testLuoOrganization() throws Exception {
        KasaajaImpl tested = setupKasaaja();
        OrganisaatioTO organisaatio = KantaCDATestUtils.luoOrganisaatio();
        String testSahkoposti = "test@test.org";
        String expectedSposti = "mailto:" + testSahkoposti;
        organisaatio.setSahkoposti(testSahkoposti);
        POCDMT000040Organization org = Whitebox.invokeMethod(tested, "luoOrganization", organisaatio);
        Assert.assertNotNull("organization ei saa olla null", org);
        Assert.assertEquals("shäköpostin tulee olla: " + expectedSposti, expectedSposti,
                org.getTelecoms().get(1).getValue());
    }

    @Test
    public void testLuoAddress() throws Exception {
        OsoiteTO osoite = new OsoiteTO();
        osoite.setKatuosoite("katuosoite");
        osoite.setPostinumero("postinumero");
        osoite.setPostitoimipaikka("postitoimipaikka");
        KasaajaImpl tested = setupKasaaja();
        AD addr = Whitebox.invokeMethod(tested, "luoAddress", osoite);
        Assert.assertNotNull("addr ei saa olla null", addr);
    }

    @Test
    public void testMuodostaTelecomValue() throws Exception {
        KasaajaImpl tested = setupKasaaja();
        String telPrefix = "tel:";
        String emailPrefix = "mailto:";
        String puhnro = "123456";
        String sposti = "test@test.org";
        String puhnroPrefix = telPrefix + puhnro;
        String spostiPrefix = emailPrefix + sposti;
        Assert.assertEquals("pitää olla: " + puhnroPrefix, puhnroPrefix,
                Whitebox.invokeMethod(tested, "muodostaTelecomValue", puhnro, telPrefix));
        Assert.assertEquals("pitää olla: " + puhnroPrefix, puhnroPrefix,
                Whitebox.invokeMethod(tested, "muodostaTelecomValue", puhnroPrefix, telPrefix));
        Assert.assertEquals("pitää olla: " + spostiPrefix, spostiPrefix,
                Whitebox.invokeMethod(tested, "muodostaTelecomValue", sposti, emailPrefix));
        Assert.assertEquals("pitää olla: " + spostiPrefix, spostiPrefix,
                Whitebox.invokeMethod(tested, "muodostaTelecomValue", spostiPrefix, emailPrefix));
        puhnro = " 123 456 ";
        puhnroPrefix = telPrefix + "123456";
        Assert.assertEquals("pitää olla: " + puhnroPrefix, puhnroPrefix,
                Whitebox.invokeMethod(tested, "muodostaTelecomValue", puhnro, telPrefix));
    }

    @Test
    public void testAddCustodian() {
        POCDMT000040ClinicalDocument cda = new POCDMT000040ClinicalDocument();

        KantaCDATestUtils.mockProperty("custodian.assignedCustodian.representedCustodianOrganization.id.root",
                KantaCDATestUtils.testIdRoot);
        KantaCDATestUtils.mockProperty("custodian.assignedCustodian.representedCustodianOrganization.name",
                KantaCDATestUtils.testNimi);
        KantaCDATestUtils.mockProperty("custodian.assignedCustodian.representedCustodianOrganization.postBox", "15");
        KantaCDATestUtils.mockProperty("custodian.assignedCustodian.representedCustodianOrganization.postNumber",
                KantaCDATestUtils.testPostiNro);
        KantaCDATestUtils.mockProperty("custodian.assignedCustodian.representedCustodianOrganization.city",
                KantaCDATestUtils.testPostitoimipaikka);
        KasaajaImpl tested = setupKasaaja();
        tested.addCustodian(cda);
        Assert.assertNotNull(cda.getCustodian());
        Assert.assertNotNull(
                cda.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getIds().get(0));
        Assert.assertEquals(KantaCDATestUtils.testNimi, cda.getCustodian().getAssignedCustodian()
                .getRepresentedCustodianOrganization().getName().getContent().get(0));
        Assert.assertEquals("PST", cda.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization()
                .getAddr().getUses().get(0));
        Assert.assertEquals(3, cda.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr()
                .getContent().size());

    }

    @Test
    public void testAddComponentOf_Empty() {
        POCDMT000040ClinicalDocument cda = new POCDMT000040ClinicalDocument();
        KantaCDATestUtils.mockCodeProperty("componentOf.encompassingEncounter.responsibleParty.assignedEntity.id",
                KantaCDATestUtils.testIdRoot, KantaCDATestUtils.testExtension);
        KantaCDATestUtils.mockCodeProperty(
                "componentOf.encompassingEncounter.responsibleParty.assignedEntity.representedOrganization.id",
                KantaCDATestUtils.testIdRoot, KantaCDATestUtils.testExtension);
        KantaCDATestUtils.mockProperty(
                "componentOf.encompassingEncounter.responsibleParty.assignedEntity.representedOrganization.name",
                KantaCDATestUtils.testNimi);
        KasaajaImpl tested = setupKasaaja();
        tested.addComponentOf(cda);

        Assert.assertEquals("NA",
                cda.getComponentOf().getEncompassingEncounter().getEffectiveTime().getNullFlavors().get(0));
        Assert.assertEquals(KantaCDATestUtils.testExtension, cda.getComponentOf().getEncompassingEncounter()
                .getResponsibleParty().getAssignedEntity().getIds().get(0).getExtension());
        Assert.assertEquals(KantaCDATestUtils.testIdRoot, cda.getComponentOf().getEncompassingEncounter()
                .getResponsibleParty().getAssignedEntity().getRepresentedOrganization().getIds().get(0).getRoot());
        Assert.assertEquals(KantaCDATestUtils.testNimi,
                cda.getComponentOf().getEncompassingEncounter().getResponsibleParty().getAssignedEntity()
                        .getRepresentedOrganization().getNames().get(0).getContent().get(0));
    }

    @Test
    public void testAddComponentOf() {
        POCDMT000040ClinicalDocument cda = new POCDMT000040ClinicalDocument();
        OrganisaatioTO organisaatioTO = KantaCDATestUtils.luoOrganisaatio();
        String effectivetimevalue = "20150310124055";

        KasaajaImpl tested = setupKasaaja();
        tested.addComponentOf(cda, effectivetimevalue, organisaatioTO, KantaCDATestUtils.testIdRoot);

        Assert.assertEquals(effectivetimevalue,
                cda.getComponentOf().getEncompassingEncounter().getEffectiveTime().getValue());
        Assert.assertEquals(KantaCDATestUtils.testIdRoot,
                cda.getComponentOf().getEncompassingEncounter().getIds().get(0).getRoot());
        Assert.assertEquals(KantaCDATestUtils.testYksilointitunnus, cda.getComponentOf().getEncompassingEncounter()
                .getLocation().getHealthCareFacility().getIds().get(0).getRoot());
        Assert.assertEquals(3, cda.getComponentOf().getEncompassingEncounter().getLocation().getHealthCareFacility()
                .getLocation().getAddr().getContent().size());
    }

    @Test
    public void testAddLocalHeader() {
        POCDMT000040ClinicalDocument cda = new POCDMT000040ClinicalDocument();

        KantaCDATestUtils.mockCodeProperty("contentsCode", KantaCDATestUtils.testCode, KantaCDATestUtils.testCodeSystem,
                KantaCDATestUtils.testCodeSystemName, KantaCDATestUtils.testDisplayName);
        KantaCDATestUtils.mockCodeProperty("localHeader.fileFormat", KantaCDATestUtils.testCode, null, null, null);

        // Mockito.when(KasaajaTest.utils.getProperties().getProperty(Matchers.matches("moderator"),
        // Matchers.anyString())).thenReturn(KantaCDATestUtils.testNimi);
        // Mockito.when(KasaajaTest.utils.getProperties().getProperty(Matchers.matches("version"),
        // Matchers.anyString())).thenReturn(KantaCDATestUtils.testCode);
        // Tämä vastaa vanhaa toteutusta edellä (tarpeeksi => tulee vihreää)
        KantaCDATestUtils.mockProperty("moderator", KantaCDATestUtils.testNimi);
        KantaCDATestUtils.mockProperty("version", KantaCDATestUtils.testCode);

        KantaCDATestUtils.mockCodeProperty("localHeader.documentType", KantaCDATestUtils.testCode,
                KantaCDATestUtils.testCodeSystem, null, null);
        KantaCDATestUtils.mockCodeProperty("localHeader.functionCode", KantaCDATestUtils.testCode, null,
                KantaCDATestUtils.testCodeSystemName, null);
        KantaCDATestUtils.mockCodeProperty("localHeader.recordStatus", KantaCDATestUtils.testCode, null, null,
                KantaCDATestUtils.testDisplayName);
        KantaCDATestUtils.mockCodeProperty("localHeader.retentionPeriodClass", KantaCDATestUtils.testCode,
                KantaCDATestUtils.testCodeSystem, null, null);

        KasaajaImpl tested = setupKasaaja();
        tested.addLocalHeader(cda);
        Assert.assertEquals(KantaCDATestUtils.testCode,
                cda.getLocalHeader().getTableOfContents().getContentsCodes().get(0).getCode());
        Assert.assertEquals(KantaCDATestUtils.testCode, cda.getLocalHeader().getFileFormat().getCode());
        Assert.assertEquals(KantaCDATestUtils.testNimi, cda.getLocalHeader().getSoftwareSupport().getModerator());
        Assert.assertEquals(KantaCDATestUtils.testCode, cda.getLocalHeader().getSoftwareSupport().getVersion());
        Assert.assertEquals(KantaCDATestUtils.testCodeSystem, cda.getLocalHeader().getDocumentType().getCodeSystem());
        Assert.assertEquals(KantaCDATestUtils.testCodeSystemName,
                cda.getLocalHeader().getFunctionCode().getCodeSystemName());
        Assert.assertEquals(KantaCDATestUtils.testDisplayName, cda.getLocalHeader().getRecordStatus().getDisplayName());
        Assert.assertEquals(KantaCDATestUtils.testCode, cda.getLocalHeader().getRetentionPeriodClass().getCode());
    }

    @Test
    public void testCreateComponent() {
        String codeNum = "5";

        KantaCDATestUtils.mockCodeProperty(codeNum , KantaCDATestUtils.testCode,
                KantaCDATestUtils.testCodeSystem, KantaCDATestUtils.testCodeSystemName,
                KantaCDATestUtils.testDisplayName);
        KasaajaImpl tested = setupKasaaja();
        POCDMT000040Component5 component = tested.createComponent(codeNum);
        Assert.assertNotNull(component);
        Assert.assertNotNull(component.getSection());
        Assert.assertNotNull(component.getSection().getCode());
        Assert.assertNotNull(component.getSection().getTitle());
        Assert.assertEquals(codeNum, component.getSection().getCode().getCode());
        Assert.assertEquals(KantaCDATestUtils.testDisplayName, component.getSection().getTitle().getContent().get(0));
    }

    @Test
    public void testGetNames() {
        KokoNimiTO kokoNimiTO = new KokoNimiTO();
        kokoNimiTO.lisaa("given", "CL", KantaCDATestUtils.testEtunimi);
        kokoNimiTO.lisaa("given", null, "TestiToinenNimi");
        kokoNimiTO.lisaa("family", null, KantaCDATestUtils.testSukunimi);
        kokoNimiTO.lisaa("suffix", null, "von Wurstburg");
        kokoNimiTO.lisaa("prefix", null, "Dr");
        kokoNimiTO.lisaa("delimiter", null, ".");

        KasaajaImpl tested = setupKasaaja();
        PN name = tested.getNames(kokoNimiTO);
        Assert.assertNotNull(name);
        Assert.assertEquals(6, name.getContent().size());
    }

    @Test
    public void testGetProperty_extension() throws Exception {

        KantaCDATestUtils.mockProperty(KasaajaTest.testKey + ".extension", KantaCDATestUtils.testCode);
        KasaajaImpl tested = setupKasaaja();
        Assert.assertEquals(KantaCDATestUtils.testCode,
                Whitebox.invokeMethod(tested, "getProperty", "diipadaapa", "extension"));
    }

    @Test
    public void testGetProperty() throws Exception {
        String myKey = "KEY";
        KantaCDATestUtils.mockProperty(myKey, KantaCDATestUtils.testCode);
        KasaajaImpl tested = setupKasaaja();
        Assert.assertEquals(KantaCDATestUtils.testCode, Whitebox.invokeMethod(tested, "getProperty", myKey));
    }

    @Test
    public void testFetchProperty() {
        String myKey = "KEY";
        KantaCDATestUtils.mockProperty(myKey, KantaCDATestUtils.testCode);
        KasaajaImpl tested = setupKasaaja();
        Assert.assertEquals(KantaCDATestUtils.testCode, tested.fetchProperty(myKey));
    }

    /**
     * Asetetaan todellinen property file kasaajalle ja tarkistetaan että koodien haku sieltä toimii oikein.
     */
    @Test
    public void testFetchAttributesFromRealPropertyFile() {

        Properties props = null;
        try {
            props = KantaCDATestUtils.loadProperties("testi_properties.properties");
        }
        catch (IOException e) {
            e.printStackTrace();
            TestCase.fail("Ei kyetty lataamaan property tiedostoa");
        }
        KasaajaImpl tested = new KasaajaImpl(props);
        Object arg1 = new String("uusimispyynto");
        Object arg2 = new CD();
        try {
            Boolean ok = Whitebox.invokeMethod(tested, "fetchAttributes", arg1, arg2);
            TestCase.assertTrue("pitäisi saada kaikki koodit koostettua", ok.booleanValue());

            CD results = (CD) arg2;
            Assert.assertEquals("8", results.getCode());
            Assert.assertEquals("1.2.246.537.5.40105.2006", results.getCodeSystem());
            Assert.assertEquals("Reseptisanoman tyyppi", results.getCodeSystemName());
            Assert.assertEquals("Lääkemääräyksen uusimispyyntö", results.getDisplayName());
            Assert.assertNull(results.getCodeSystemVersion());

        }
        catch (Exception e) {
            e.printStackTrace();
            TestCase.fail("Ei kyetty suorittamaan testattavaa metodia 'getProperty'");
        }
    }

    @Test
    public void testFetchAttributes_II() {
        II ii = new II();
        KantaCDATestUtils.mockCodeProperty(KasaajaTest.testKey, KantaCDATestUtils.testIdRoot,
                KantaCDATestUtils.testExtension);
        KasaajaImpl tested = setupKasaaja();
        tested.fetchAttributes(KasaajaTest.testKey, ii);
        Assert.assertEquals(KantaCDATestUtils.testIdRoot, ii.getRoot());
        Assert.assertEquals(KantaCDATestUtils.testExtension, ii.getExtension());
    }

    @Test
    public void testFetchAttributes_CD() {
        CD cd = new CD();
        KantaCDATestUtils.mockCodeProperty(KasaajaTest.testKey, KantaCDATestUtils.testCode,
                KantaCDATestUtils.testCodeSystem, KantaCDATestUtils.testCodeSystemName,
                KantaCDATestUtils.testDisplayName);
        KantaCDATestUtils.mockProperty(KasaajaTest.testKey + ".codeSystemVersion",
                KantaCDATestUtils.testCodeSystemVersion);
        KasaajaImpl tested = setupKasaaja();
        tested.fetchAttributes(KasaajaTest.testKey, cd);
        Assert.assertEquals(KantaCDATestUtils.testCode, cd.getCode());
        Assert.assertEquals(KantaCDATestUtils.testCodeSystem, cd.getCodeSystem());
        Assert.assertEquals(KantaCDATestUtils.testCodeSystemName, cd.getCodeSystemName());
        Assert.assertEquals(KantaCDATestUtils.testDisplayName, cd.getDisplayName());
        Assert.assertEquals(KantaCDATestUtils.testCodeSystemVersion, cd.getCodeSystemVersion());

    }

    @Test
    public void testOnkoNullTaiTyhja() {

        KantaCDATestUtils.mockProperty(Kasaaja.BASE_OID_PROPERTY, KantaCDATestUtils.testBaseId);
        KasaajaImpl tested = new KasaajaImpl(KantaCDATestUtils.getProperties());
        Assert.assertTrue(tested.onkoNullTaiTyhja(null));
        Assert.assertTrue(tested.onkoNullTaiTyhja(""));
        Assert.assertFalse(tested.onkoNullTaiTyhja("merkkijono"));
    }

    @Test
    public void testPoistaEtuNollat() {
        KantaCDATestUtils.mockProperty(Kasaaja.BASE_OID_PROPERTY, KantaCDATestUtils.testBaseId);
        KasaajaImpl tested = new KasaajaImpl(KantaCDATestUtils.getProperties());

        Assert.assertEquals(tested.poistaEtuNollat("01234"), "1234");
        Assert.assertEquals(tested.poistaEtuNollat("00500"), "500");
        Assert.assertEquals(tested.poistaEtuNollat("5003412"), "5003412");
        Assert.assertEquals(tested.poistaEtuNollat("00000"), "");
        Assert.assertEquals(tested.poistaEtuNollat(""), "");
        Assert.assertNull(tested.poistaEtuNollat(null));
    }

    @Test
    public void testLuoServiceProviderOrganization() {
        OrganisaatioTO organisaatioTO = KantaCDATestUtils.luoOrganisaatio();
        KasaajaImpl tested = setupKasaaja();
        POCDMT000040Organization provider = tested.luoServiceProviderOrganization(organisaatioTO);
        Assert.assertNotNull(provider);
        Assert.assertNotNull(provider.getIds());
        Assert.assertTrue(provider.getIds().size() == 1);
        Assert.assertEquals(KantaCDATestUtils.testYksilointitunnus, provider.getIds().get(0).getRoot());
        Assert.assertNotNull(provider.getNames());
        Assert.assertTrue(provider.getNames().size() == 1);
        Assert.assertNotNull(provider.getNames().get(0).getContent());
        Assert.assertTrue(provider.getNames().get(0).getContent().size() == 1);
        Assert.assertEquals(KantaCDATestUtils.testNimi, provider.getNames().get(0).getContent().get(0));
        Assert.assertNotNull(provider.getAddrs());
        Assert.assertTrue(provider.getAddrs().size() == 1);
        Assert.assertNotNull(provider.getAddrs().get(0).getContent().size() == 3);
    }

    @Test
    public void testAddComponentOf_ServiceProviderOrganization() {
        POCDMT000040ClinicalDocument cda = new POCDMT000040ClinicalDocument();
        OrganisaatioTO organisaatioTO = KantaCDATestUtils.luoOrganisaatio();
        organisaatioTO.setToimintaYksikko(KantaCDATestUtils.luoOrganisaatio());
        String effectivetimevalue = "20150310124055";

        KasaajaImpl tested = setupKasaaja();
        tested.addComponentOf(cda, effectivetimevalue, organisaatioTO, KantaCDATestUtils.testIdRoot);

        Assert.assertEquals(effectivetimevalue,
                cda.getComponentOf().getEncompassingEncounter().getEffectiveTime().getValue());
        Assert.assertEquals(KantaCDATestUtils.testIdRoot,
                cda.getComponentOf().getEncompassingEncounter().getIds().get(0).getRoot());
        Assert.assertEquals(KantaCDATestUtils.testYksilointitunnus, cda.getComponentOf().getEncompassingEncounter()
                .getLocation().getHealthCareFacility().getIds().get(0).getRoot());
        Assert.assertEquals(3, cda.getComponentOf().getEncompassingEncounter().getLocation().getHealthCareFacility()
                .getLocation().getAddr().getContent().size());
        Assert.assertNotNull(cda.getComponentOf().getEncompassingEncounter().getLocation().getHealthCareFacility()
                .getServiceProviderOrganization());
        POCDMT000040Organization provider = cda.getComponentOf().getEncompassingEncounter().getLocation()
                .getHealthCareFacility().getServiceProviderOrganization();
        Assert.assertNotNull(provider);
        Assert.assertNotNull(provider.getIds());
        Assert.assertTrue(provider.getIds().size() == 1);
        Assert.assertEquals(KantaCDATestUtils.testYksilointitunnus, provider.getIds().get(0).getRoot());
        Assert.assertNotNull(provider.getNames());
        Assert.assertTrue(provider.getNames().size() == 1);
        Assert.assertNotNull(provider.getNames().get(0).getContent());
        Assert.assertTrue(provider.getNames().get(0).getContent().size() == 1);
        Assert.assertEquals(KantaCDATestUtils.testNimi, provider.getNames().get(0).getContent().get(0));
        Assert.assertNotNull(provider.getAddrs());
        Assert.assertTrue(provider.getAddrs().size() == 1);
        Assert.assertNotNull(provider.getAddrs().get(0).getContent().size() == 3);
    }

    @Test
    public void incTest() {
        KasaajaImpl tested = setupKasaaja();
        String id = tested.getNextId(KasaajaTest.leimakentat);
        TestCase.assertTrue(id + " ei lopu 1", id.endsWith(".1"));
        id = tested.getNextId(KasaajaTest.leimakentat);
        TestCase.assertTrue(id + " ei lopu 2", id.endsWith(".2"));
        id = tested.getNextId(KasaajaTest.leimakentat);
        TestCase.assertTrue(id + " ei lopu 3", id.endsWith(".3"));
        id = tested.getNextId(KasaajaTest.leimakentat);
        TestCase.assertTrue(id + " ei lopu 4", id.endsWith(".4"));
    }
}
