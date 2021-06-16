package fi.kela.kanta.cda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hl7.v3.AD;
import org.hl7.v3.ANY;
import org.hl7.v3.AdxpCity;
import org.hl7.v3.AdxpPostalCode;
import org.hl7.v3.AdxpStreetAddressLine;
import org.hl7.v3.BL;
import org.hl7.v3.CD;
import org.hl7.v3.CE;
import org.hl7.v3.CR;
import org.hl7.v3.ED;
import org.hl7.v3.EnFamily;
import org.hl7.v3.EnGiven;
import org.hl7.v3.II;
import org.hl7.v3.INT;
import org.hl7.v3.IVLPQ;
import org.hl7.v3.IVLTS;
import org.hl7.v3.IVXBPQ;
import org.hl7.v3.IVXBTS;
import org.hl7.v3.ObjectFactory;
import org.hl7.v3.PN;
import org.hl7.v3.POCDMT000040Author;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component4;
import org.hl7.v3.POCDMT000040Consumable;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040EntryRelationship;
import org.hl7.v3.POCDMT000040HealthCareFacility;
import org.hl7.v3.POCDMT000040LabeledDrug;
import org.hl7.v3.POCDMT000040Observation;
import org.hl7.v3.POCDMT000040Organization;
import org.hl7.v3.POCDMT000040Participant2;
import org.hl7.v3.POCDMT000040PatientRole;
import org.hl7.v3.POCDMT000040Section;
import org.hl7.v3.POCDMT000040SubstanceAdministration;
import org.hl7.v3.POCDMT000040Supply;
import org.hl7.v3.PQ;
import org.hl7.v3.PQR;
import org.hl7.v3.SC;
import org.hl7.v3.ST;
import org.hl7.v3.StrucDocContent;
import org.hl7.v3.StrucDocParagraph;
import org.hl7.v3.StrucDocText;
import org.hl7.v3.TS;
import org.hl7.v3.XActRelationshipEntryRelationship;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.powermock.reflect.Whitebox;

import fi.kela.kanta.exceptions.PurkuException;
import fi.kela.kanta.to.AnnosTO;
import fi.kela.kanta.to.AnnosjaksonPituusTO;
import fi.kela.kanta.to.AnnostelukaudenPituusTO;
import fi.kela.kanta.to.ApteekissaValmistettavaLaakeTO;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.LaakemaarayksenKorjausTO;
import fi.kela.kanta.to.LaakemaarayksenMitatointiTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.MuuAinesosaTO;
import fi.kela.kanta.to.OrganisaatioTO;
import fi.kela.kanta.to.VaikuttavaAinesosaTO;
import fi.kela.kanta.to.ValmisteTO;
import fi.kela.kanta.to.ValmisteenYksilointitiedotTO;

public class ReseptiPurkajaTest {

    private ObjectFactory of;
    private static final String testAuthorVirkaOppiarvoCodeSystem = "1.2.246.537.6.12.999.2003";
    private static final String testAuthorVirkanimikeCode = "1.2";
    private static final String testAuthorOppiarvoCode = "1.3";
    private static final String testAuthorAmmattioikeusCodeSystem = "1.2.246.537.6.12.2002.126";
    private static final String testAuthorAmmattioikeusCode = "151";
    private static final String testCode = "TEST";
    private static final String testRole = "LAL";
    private static final String testAuthorSVRoot = "1.2.246.537.25";
    private static final String testAuthorSVExtension = "123455";
    private static final String testAuthorRekRoot = "1.2.246.537.26";
    private static final String testAuthorRekExtension = "01234567890";
    private static final String testOrgId = "1.2.246.10.1602257.10.1";
    private static final String testOrgName = "Testi terveysasema";
    private static final String testOrgTelecom = "tel:0201234567";
    private static final String testOrgStreet = "Potilastie 2";
    private static final String testOrgPostCode = "50600";
    private static final String testOrgCity = "Kotka";
    private static final String testAuthorErikosala = "86173-680";
    private static final String testAuthorErikoisalaName = "erikoislääkäri yleislääketiede";
    private static final String testAuthorVirkanimike = "Ylilääkäri";
    private static final String testAuthorOppiarvo = "002";
    private static final String testAuthorAmmattioikeus = "034";
    private static final String testAuthorAmmattioikeusName = "laillistettu erikoislääkäri";
    private static final String testAuthorEtunimi = "Timo";
    private static final String testAuthorSukunimi = "Markka";
    private static final String testAuthorKirjautumisaika = "20160530095215";
    private static final String testPatientHetu = "030875-999Y";
    private static final String testPatientEtunimi = "Pekka";
    private static final String testPatientSukunimi = "Potilas";
    private static final String testPatientSyntymaaika = "19750803";
    private static final Integer testPatientSukupuoli = 1;
    private static final String testEffectiveTime = "20150604105612";
    private static final String testAinesosa = "ainesosa";
    private static final int testAinesosanMaara = 4321;
    private static final String testAinesosanUnit = "yksikkö";
    private static final String testAinesosanmaaratekstina = "ainesosanmäärätekstinä";
    private static final String testAtcCode = "ATCCode";
    private static final String testAtcName = "ATCName";
    private static final String testLaaketietokantaVersio = "2015.003";
    private static final String testKoodamatonNimi = "Koodamaton nimi";
    private static final String testKauppanimi = "kauppanimi";
    private static final String testLaaketietokannanulkopuolinenValmiste = "laaketietokannanulkopuolinenvalmiste";
    private static final String testVahvuus = "vahvuus";
    private static final String testLaakemuoto = "laakemuoto";
    private static final String testIterointiteksti = "iterointiteksti";
    private static final String testIterointiUnit = "iterointiyksikkö";
    private static final int testIterointiValue = 7;
    private static final long testIterointiMaara = 12;
    private static final String testValmisteenLaji = "valmisteenlaji";
    private static final String testValmisteenLajiName = "valmisteenlajiname";
    private static final String testSailytysastia = "säilytysastia";
    private static final String testPakkauskokoteksti = "pakkauskokoteksti";
    private static final long testPakkauskoonkerroin = 20;
    private static final long testPakkauksienLukumaara = 4;
    private static final String testPakkausyksikko = "pakkausyksikko";
    private static final double testPakkauskoko = 50;
    private static final String testLaakkeenkokonaismaaraUnit = "total unit";
    private static final int testLaakeenkokonaismaaraValue = 123;
    private static final String testAjallemaaratynAlkuaika = "201508060914";
    private static final String testAjallemaaratynUnit = "aikaunit";
    private static final int testAjallemaaratynValue = 8;
    private static final String testValmistusohje = "valmistusohje";
    private static final String testMyyntiluvanhaltija = "myyntiluvanhaltija";
    private static final String testTyonantaja = "työnantaja";
    private static final String testVakuutuslaitos = "vakuutuslaitos";
    private static final String testKorjauskoodi = "korjauskoodi";
    private static final String testKorjausperustelu = "korjausperustelu";
    private static final String testMitatointiSyy = "mitatointisyy";
    private static final String testMitatointiPerustelu = "mitatointiperustelu";
    private static final String testMitatointiTyyppi = "mitatointityyppi";
    private static final String testMitatointiSuostumus = "mitatointisuostumus";
    private static final String testMitatointiOsapuoli = "mitatointiosapuoli";
    private static final String testRoleKor = "KOR";
    private static final String testRoleMit = "MIT";
    private static final String apteekissaTallennettuLaakemaaraysValue = "2";
    private static final String testRoleKir = "KIR";
    private static final String testLaakeenkokonaismaaraValueDesimaalina = "123.0";
    @Before
    public void setup() {
        of = new ObjectFactory();
    }

    @Test
    public void testAlustaApteekissaValmistettavaLaake() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        Whitebox.invokeMethod(tested, "alustaApteekissaValmistettavaLaake", laakemaaraysTO);
        Assert.assertNotNull(laakemaaraysTO.getApteekissaValmistettavaLaake());
        ApteekissaValmistettavaLaakeTO aptekiApteekissaValmistettavaLaakeTO = laakemaaraysTO
                .getApteekissaValmistettavaLaake();
        Whitebox.invokeMethod(tested, "alustaApteekissaValmistettavaLaake", laakemaaraysTO);
        Assert.assertSame(aptekiApteekissaValmistettavaLaakeTO, laakemaaraysTO.getApteekissaValmistettavaLaake());
    }

    @Test
    public void testAlustaValmiste() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        Whitebox.invokeMethod(tested, "alustaValmiste", laakemaaraysTO);
        Assert.assertNotNull(laakemaaraysTO.getValmiste());
        ValmisteTO valmisteTO = laakemaaraysTO.getValmiste();
        Whitebox.invokeMethod(tested, "alustaValmiste", laakemaaraysTO);
        Assert.assertSame(valmisteTO, laakemaaraysTO.getValmiste());
    }

    @Test
    public void testAlustaYksilointitiedot() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        Whitebox.invokeMethod(tested, "alustaYksilointitiedot", laakemaaraysTO);
        Assert.assertNotNull(laakemaaraysTO.getValmiste());
        Assert.assertNotNull(laakemaaraysTO.getValmiste().getYksilointitiedot());
        ValmisteenYksilointitiedotTO valmisteenYksilointitiedotTO = laakemaaraysTO.getValmiste().getYksilointitiedot();
        Whitebox.invokeMethod(tested, "alustaYksilointitiedot", laakemaaraysTO);
        Assert.assertSame(valmisteenYksilointitiedotTO, laakemaaraysTO.getValmiste().getYksilointitiedot());
    }

    @Test
    public void testOnkoObservationCodeCode() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();

        Assert.assertFalse((Boolean) Whitebox.invokeMethod(tested, "onkoObservationCodeCode",
                Matchers.isNull(POCDMT000040Observation.class), Matchers.eq(testCode)));
        Assert.assertFalse((Boolean) Whitebox.invokeMethod(tested, "onkoObservationCodeCode", observation, testCode));
        observation.setCode(of.createCD());
        Assert.assertFalse((Boolean) Whitebox.invokeMethod(tested, "onkoObservationCodeCode", observation, testCode));
        observation.getCode().setCode(testCode);
        Assert.assertTrue((Boolean) Whitebox.invokeMethod(tested, "onkoObservationCodeCode", observation, testCode));
        observation.getCode().setCode("NOTTESTCODE");
        Assert.assertFalse((Boolean) Whitebox.invokeMethod(tested, "onkoObservationCodeCode", observation, testCode));
    }

    @Test
    public void testPuraAuthor() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument clinicalDocument = of.createPOCDMT000040ClinicalDocument();

        clinicalDocument.getAuthors().add(luoTestAuthor(testRole));

        Whitebox.invokeMethod(tested, "puraAuthor", clinicalDocument, laakemaaraysTO);
        Assert.assertNotNull(laakemaaraysTO.getAmmattihenkilo());
        Assert.assertNotNull(laakemaaraysTO.getAmmattihenkilo().getOrganisaatio());
        Assert.assertNotNull(laakemaaraysTO.getAmmattihenkilo().getOrganisaatio().getOsoite());
        Assert.assertNotNull(laakemaaraysTO.getAmmattihenkilo().getKokonimi());

        Assert.assertNotNull(laakemaaraysTO.getAmmattihenkilo().getKirjautumisaika());

        Assert.assertEquals(testAuthorAmmattioikeus, laakemaaraysTO.getAmmattihenkilo().getAmmattioikeus());
        Assert.assertEquals(testAuthorAmmattioikeusName, laakemaaraysTO.getAmmattihenkilo().getAmmattioikeusName());
        Assert.assertEquals(testAuthorErikosala, laakemaaraysTO.getAmmattihenkilo().getErikoisala());
        Assert.assertEquals(testAuthorErikoisalaName, laakemaaraysTO.getAmmattihenkilo().getErikoisalaName());
        Assert.assertEquals(testAuthorOppiarvo, laakemaaraysTO.getAmmattihenkilo().getOppiarvo());
        // Assert.assertEquals(testAuthorO, laakemaaraysTO.getAmmattihenkilo().getOppiarvoTekstina());
        Assert.assertEquals(testAuthorRekExtension, laakemaaraysTO.getAmmattihenkilo().getRekisterointinumero());
        Assert.assertEquals(testRole, laakemaaraysTO.getAmmattihenkilo().getRooli());
        Assert.assertEquals(testAuthorSVExtension, laakemaaraysTO.getAmmattihenkilo().getSvNumero());
        Assert.assertEquals(testAuthorVirkanimike, laakemaaraysTO.getAmmattihenkilo().getVirkanimike());

        Assert.assertEquals(testOrgName, laakemaaraysTO.getAmmattihenkilo().getOrganisaatio().getNimi());
        Assert.assertEquals(testOrgTelecom, laakemaaraysTO.getAmmattihenkilo().getOrganisaatio().getPuhelinnumero());
        Assert.assertEquals(testOrgId, laakemaaraysTO.getAmmattihenkilo().getOrganisaatio().getYksilointitunnus());

        Assert.assertEquals(testOrgStreet,
                laakemaaraysTO.getAmmattihenkilo().getOrganisaatio().getOsoite().getKatuosoite());
        Assert.assertEquals(testOrgPostCode,
                laakemaaraysTO.getAmmattihenkilo().getOrganisaatio().getOsoite().getPostinumero());
        Assert.assertEquals(testOrgCity,
                laakemaaraysTO.getAmmattihenkilo().getOrganisaatio().getOsoite().getPostitoimipaikka());

        Assert.assertEquals(testAuthorEtunimi + " " + testAuthorEtunimi,
                laakemaaraysTO.getAmmattihenkilo().getKokonimi().getEtunimi());
        Assert.assertEquals(testAuthorSukunimi, laakemaaraysTO.getAmmattihenkilo().getKokonimi().getSukunimi());
    }

    @Test
    public void testPuraPotilas() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument clinicalDocument = of.createPOCDMT000040ClinicalDocument();

        clinicalDocument.getRecordTargets().add(of.createPOCDMT000040RecordTarget());
        clinicalDocument.getRecordTargets().get(0).setPatientRole(luoTestPotilas());

        Whitebox.invokeMethod(tested, "puraPotilas", clinicalDocument, laakemaaraysTO);
        Assert.assertNotNull(laakemaaraysTO.getPotilas());

        Assert.assertEquals(testPatientHetu, laakemaaraysTO.getPotilas().getHetu());
        Assert.assertEquals(testPatientEtunimi, laakemaaraysTO.getPotilas().getNimi().getEtunimi());
        Assert.assertEquals(testPatientSukunimi, laakemaaraysTO.getPotilas().getNimi().getSukunimi());
        Assert.assertEquals(testPatientSyntymaaika, laakemaaraysTO.getPotilas().getSyntymaaika());
        Assert.assertEquals(testPatientSukupuoli, laakemaaraysTO.getPotilas().getSukupuoli());
    }

    @Test
    public void testPuraPotilas_eiHetua() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument clinicalDocument = of.createPOCDMT000040ClinicalDocument();

        POCDMT000040PatientRole testPatientRole = of.createPOCDMT000040PatientRole();
        testPatientRole.getIds().add(of.createII());
        testPatientRole.getIds().get(0).getNullFlavors().add("UNK");
        testPatientRole.setPatient(of.createPOCDMT000040Patient());
        testPatientRole.getPatient().getNames().add(luoTestNames(testPatientEtunimi, testPatientSukunimi));
        testPatientRole.getPatient().setBirthTime(of.createTS());
        testPatientRole.getPatient().getBirthTime().setValue(testPatientSyntymaaika);
        testPatientRole.getPatient().setAdministrativeGenderCode(of.createCE());
        testPatientRole.getPatient().getAdministrativeGenderCode().setCode(String.valueOf(testPatientSukupuoli));
        clinicalDocument.getRecordTargets().add(of.createPOCDMT000040RecordTarget());
        clinicalDocument.getRecordTargets().get(0).setPatientRole(testPatientRole);

        Whitebox.invokeMethod(tested, "puraPotilas", clinicalDocument, laakemaaraysTO);
        Assert.assertNotNull(laakemaaraysTO.getPotilas());

        Assert.assertNull("Hetun tulee olla null", laakemaaraysTO.getPotilas().getHetu());
        Assert.assertEquals(testPatientEtunimi, laakemaaraysTO.getPotilas().getNimi().getEtunimi());
        Assert.assertEquals(testPatientSukunimi, laakemaaraysTO.getPotilas().getNimi().getSukunimi());
        Assert.assertEquals(testPatientSyntymaaika, laakemaaraysTO.getPotilas().getSyntymaaika());
        Assert.assertEquals(testPatientSukupuoli, laakemaaraysTO.getPotilas().getSukupuoli());
    }

    @Test
    public void testPuraPalveluyksikko() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040HealthCareFacility healthCareFacility = luoTestPalveluyksikko();

        Whitebox.invokeMethod(tested, "puraPalveluyksikko", healthCareFacility, laakemaaraysTO);
    }

    @Test
    public void testPuraComponentOf() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        cda.setComponentOf(of.createPOCDMT000040Component1());
        cda.getComponentOf().setEncompassingEncounter(of.createPOCDMT000040EncompassingEncounter());
        cda.getComponentOf().getEncompassingEncounter().setEffectiveTime(of.createIVLTS());
        cda.getComponentOf().getEncompassingEncounter().getEffectiveTime().setValue(testEffectiveTime);
        cda.getComponentOf().getEncompassingEncounter().setLocation(of.createPOCDMT000040Location());
        cda.getComponentOf().getEncompassingEncounter().getLocation().setHealthCareFacility(luoTestPalveluyksikko());

        Whitebox.invokeMethod(tested, "puraComponentOf", cda, laakemaaraysTO);
    }

    @Test
    public void testPuraLaakemaarayksenMuutTiedot() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        ArrayList<POCDMT000040Component4> components = new ArrayList<POCDMT000040Component4>();
        String kayttotarkoitus = "Käyttötarkoitus";
        String hoitolaji = "Hoitolaji";
        String uusimiskiellonperustelu = "Uusimiskiellonperustelu";
        int alle12vuotiaanPainoValue = 12;
        String alle12vuotiaanPainoUnit = "alle12-vuotiaanpainounit";
        String annosjakeluText = "Annosjakeluteksti";
        String viestiApteekille = "ViestiApteekille";
        String erillisselvitysCode = "01P";
        String erillisselvitysText = "Nielemisvaikeudet";
        String pkvLaake = "PKVlaake";
        String reseptinlaji = "Reseptinlaji";
        String tunnistaminen = "3";
        String tunnistamisTeksti = "Tunnistamisteksit";

        components.add(luoTestComponent(of.createST(), kayttotarkoitus,
                KantaCDAConstants.Laakityslista.KAYTTOTARKOITUS_TEKSTINA));
        components.add(luoTestComponent(of.createCE(), hoitolaji, KantaCDAConstants.Laakityslista.HOITOLAJI));
        components.add(luoTestComponent(of.createBL(), "true", KantaCDAConstants.Laakityslista.PYSYVA_LAAKITYS));
        components.add(luoTestComponent(of.createBL(), "true", KantaCDAConstants.Laakityslista.UUSIMISKIELTO));
        components.add(luoTestComponent(of.createBL(), "true", KantaCDAConstants.Laakityslista.LAAKEVAIHTOKIELTO));
        components.add(
                luoTestComponent(of.createST(), viestiApteekille, KantaCDAConstants.Laakityslista.VIESTI_APTEEKILLE));
        components.add(luoTestComponent(of.createCE(), pkvLaake,
                KantaCDAConstants.Laakityslista.HUUMAUSAINE_PKV_LAAKEMAARAYS));
        components.add(luoTestComponent(of.createBL(), "true",
                KantaCDAConstants.Laakityslista.KYSEESSA_LAAKKEEN_KAYTON_ALOITUS));
        components.add(luoTestComponent(of.createBL(), "true", KantaCDAConstants.Laakityslista.HUUME));
        components.add(luoTestComponent(of.createCE(), reseptinlaji, KantaCDAConstants.Laakityslista.RESEPTIN_LAJI));
        components.add(luoTestComponent(of.createCE(), apteekissaTallennettuLaakemaaraysValue,
                KantaCDAConstants.Laakityslista.APTEEKISSA_TALLENNETTU_LAAKEMAARAYS));

        // 194 uusimiskiellonperustelu
        POCDMT000040Component4 compUP = luoTestComponent(of.createCE(), "DILEDINDDILE",
                KantaCDAConstants.Laakityslista.UUDISTAMISKIELLON_SYY);
        ED originalText = of.createED();
        originalText.getContent().add(uusimiskiellonperustelu);
        ((CE) compUP.getObservation().getValues().get(0)).setOriginalText(originalText);
        components.add(compUP);

        // 89 alle 12-vuotiaan paino
        POCDMT000040Component4 compA12VP = luoTestComponent(of.createPQ(), String.valueOf(alle12vuotiaanPainoValue),
                KantaCDAConstants.Laakityslista.ALLE_12VUOTIAAN_PAINO);
        ((PQ) compA12VP.getObservation().getValues().get(0)).setUnit(alle12vuotiaanPainoUnit);
        components.add(compA12VP);

        // 91 annosjakelu
        POCDMT000040Component4 compAJ = luoTestComponent(of.createBL(), "true",
                KantaCDAConstants.Laakityslista.ANNOSJAKELU);
        compAJ.getObservation().setText(of.createED());
        compAJ.getObservation().getText().getContent().add(annosjakeluText);
        components.add(compAJ);

        // 69 erillisselvitys
        POCDMT000040Component4 compES = luoTestComponent(of.createCE(), erillisselvitysCode,
                KantaCDAConstants.Laakityslista.ERILLISSELVITYS);
        compES.getObservation().setText(of.createST());
        compES.getObservation().getText().getContent().add(erillisselvitysText);
        components.add(compES);

        // 117 Potilaan tunnistaminen
        POCDMT000040Component4 compPT = luoTestComponent(of.createCE(), tunnistaminen,
                KantaCDAConstants.Laakityslista.POTILAAN_TUNNISTAMINEN);
        compPT.getObservation().setText(of.createED());
        compPT.getObservation().getText().getContent().add(tunnistamisTeksti);
        components.add(compPT);

        // 97 Korjauksen perustelu, purkajan ei pitäisi reagoida tähän, koska käytössä on LaakemaaraysTO instanssi
        components.add(luoKorjauksenPerusteuluComponent());

        // 95 Mitatoinnin syy, purkajan ei pitäisi reagoida tähän, koska käytössä on LaakemaaraysTO instanssi
        components.add(luoMitatoinninSyyComponent());

        // 96 Mitätöinnin tyyppi, purkajan ei pitäisi reagoida tähän, koska käytössä on LaakemaaraysTO instanssi
        components.add(luoMitatoinninTyyppiComponent());

        Whitebox.invokeMethod(tested, "puraLaakemaarayksenMuutTiedot", components, laakemaaraysTO);
        Assert.assertEquals(kayttotarkoitus, laakemaaraysTO.getKayttotarkoitusTeksti());
        Assert.assertEquals(hoitolaji, laakemaaraysTO.getHoitolajit().get(0));
        Assert.assertTrue(laakemaaraysTO.isPysyvaislaakitys());
        Assert.assertTrue(laakemaaraysTO.isUudistamiskielto());
        Assert.assertEquals(uusimiskiellonperustelu, laakemaaraysTO.getUusimiskiellonPerustelu());
        Assert.assertTrue(laakemaaraysTO.isLaakevaihtokielto());
        Assert.assertEquals("alle 12-vuotiaan paino unit pitää olla: " + alle12vuotiaanPainoUnit,
                alle12vuotiaanPainoUnit, laakemaaraysTO.getAlle12VuotiaanPainoUnit());
        Assert.assertEquals("Alle 12-vuotiaan paino valuen pitää olla: " + alle12vuotiaanPainoValue,
                alle12vuotiaanPainoValue, laakemaaraysTO.getAlle12VuotiaanPainoValue().intValue());
        Assert.assertTrue(laakemaaraysTO.isAnnosjakelu());
        Assert.assertEquals("Annosjakelu teksti pitää olla:" + annosjakeluText, annosjakeluText,
                laakemaaraysTO.getAnnosjakeluTeksti());
        Assert.assertEquals("Viesti apteekille pitää olla: " + viestiApteekille, viestiApteekille,
                laakemaaraysTO.getViestiApteekille());
        Assert.assertEquals("Erillisselvitys pitää olla: " + erillisselvitysCode, erillisselvitysCode,
                laakemaaraysTO.getErillisselvitys());
        Assert.assertEquals("Erillisselvitys teksti pitää olla: " + erillisselvitysText, erillisselvitysText,
                laakemaaraysTO.getErillisselvitysteksti());
        Assert.assertEquals(tunnistaminen, laakemaaraysTO.getPotilaanTunnistaminen());
        Assert.assertEquals(tunnistamisTeksti, laakemaaraysTO.getPotilaanTunnistaminenTeksti());
        Assert.assertEquals(pkvLaake, laakemaaraysTO.getPKVlaakemaarays());
        Assert.assertTrue(laakemaaraysTO.isKyseessaLaakkeenkaytonAloitus());
        Assert.assertTrue(laakemaaraysTO.isHuume());
        Assert.assertEquals(reseptinlaji, laakemaaraysTO.getReseptinLaji());
        Assert.assertEquals(apteekissaTallennettuLaakemaaraysValue,
                laakemaaraysTO.getApteekissaTallennettuLaakemaarays());
    }

    @Test
    public void testPuraAnnostus() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        String annostusohje = "annostusohje";
        ArrayList<POCDMT000040Component4> components = new ArrayList<POCDMT000040Component4>();
        components.add(luoTestComponent(of.createBL(), "true", "87"));
        POCDMT000040Component4 comp = of.createPOCDMT000040Component4();
        comp.setSubstanceAdministration(of.createPOCDMT000040SubstanceAdministration());
        comp.getSubstanceAdministration().setText(of.createED());
        comp.getSubstanceAdministration().getText().getContent().add(annostusohje);
        comp.getSubstanceAdministration().getEntryRelationships().add(of.createPOCDMT000040EntryRelationship());
        comp.getSubstanceAdministration().getEntryRelationships().get(0)
                .setObservation(luoTestObservation(of.createBL(), "true", "56"));
        components.add(comp);
        Whitebox.invokeMethod(tested, "puraAnnostus", components, laakemaaraysTO);

        Assert.assertTrue(laakemaaraysTO.isAnnosteluPelkastaanTekstimuodossa());
        Assert.assertEquals(annostusohje, laakemaaraysTO.getAnnostusohje());
        Assert.assertTrue(laakemaaraysTO.isSICmerkinta());
    }

    @Test
    public void testPuraMuutAinesosat() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();

        ArrayList<POCDMT000040Component4> components = new ArrayList<POCDMT000040Component4>();
        POCDMT000040Component4 comp = of.createPOCDMT000040Component4();
        comp.setSubstanceAdministration(of.createPOCDMT000040SubstanceAdministration());
        comp.getSubstanceAdministration().setConsumable(of.createPOCDMT000040Consumable());
        comp.getSubstanceAdministration().getConsumable()
                .setManufacturedProduct(of.createPOCDMT000040ManufacturedProduct());
        comp.getSubstanceAdministration().getConsumable().getManufacturedProduct()
                .setManufacturedLabeledDrug(of.createPOCDMT000040LabeledDrug());
        comp.getSubstanceAdministration().getConsumable().getManufacturedProduct().getManufacturedLabeledDrug()
                .setName(of.createEN());
        comp.getSubstanceAdministration().getConsumable().getManufacturedProduct().getManufacturedLabeledDrug()
                .getName().getContent().add(testAinesosa);
        comp.getSubstanceAdministration().setDoseQuantity(of.createIVLPQ());
        comp.getSubstanceAdministration().getDoseQuantity().setCenter(of.createPQ());
        comp.getSubstanceAdministration().getDoseQuantity().getCenter().setValue(String.valueOf(testAinesosanMaara));
        comp.getSubstanceAdministration().getDoseQuantity().getCenter().setUnit(testAinesosanUnit);
        PQR pqr = of.createPQR();
        pqr.setOriginalText(of.createED());
        pqr.getOriginalText().getContent().add(testAinesosanmaaratekstina);
        comp.getSubstanceAdministration().getDoseQuantity().getTranslations().add(pqr);
        components.add(comp);

        Whitebox.invokeMethod(tested, "puraMuutAinesosat", components, laakemaaraysTO);

        Assert.assertNotNull(laakemaaraysTO.getApteekissaValmistettavaLaake());
        Assert.assertNotNull(laakemaaraysTO.getApteekissaValmistettavaLaake().getMuutAinesosat());
        Assert.assertEquals(1, laakemaaraysTO.getApteekissaValmistettavaLaake().getMuutAinesosat().size());
        MuuAinesosaTO muuainesosa = laakemaaraysTO.getApteekissaValmistettavaLaake().getMuutAinesosat().iterator()
                .next();
        Assert.assertEquals(testAinesosa, muuainesosa.getNimi());
        Assert.assertEquals(testAinesosanMaara, muuainesosa.getAinesosanMaaraValue(), 0.0);
        Assert.assertEquals(testAinesosanUnit, muuainesosa.getAinesosanMaaraUnit());
        Assert.assertEquals(testAinesosanmaaratekstina, muuainesosa.getAinesosanMaaraTekstina());
    }

    @Test
    public void testPuraVaikuttavatAinesosat() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();

        ArrayList<POCDMT000040Component4> components = new ArrayList<POCDMT000040Component4>();
        POCDMT000040Component4 comp = of.createPOCDMT000040Component4();
        comp.setSubstanceAdministration(of.createPOCDMT000040SubstanceAdministration());
        comp.getSubstanceAdministration().setConsumable(of.createPOCDMT000040Consumable());
        comp.getSubstanceAdministration().getConsumable()
                .setManufacturedProduct(of.createPOCDMT000040ManufacturedProduct());
        comp.getSubstanceAdministration().getConsumable().getManufacturedProduct()
                .setManufacturedLabeledDrug(of.createPOCDMT000040LabeledDrug());
        comp.getSubstanceAdministration().getConsumable().getManufacturedProduct().getManufacturedLabeledDrug()
                .setCode(of.createCE());
        comp.getSubstanceAdministration().getConsumable().getManufacturedProduct().getManufacturedLabeledDrug()
                .getCode().setCodeSystemVersion(testLaaketietokantaVersio);
        comp.getSubstanceAdministration().getConsumable().getManufacturedProduct().getManufacturedLabeledDrug()
                .getCode().setCode(testAtcCode);
        comp.getSubstanceAdministration().getConsumable().getManufacturedProduct().getManufacturedLabeledDrug()
                .getCode().setDisplayName(testAtcName);
        comp.getSubstanceAdministration().getConsumable().getManufacturedProduct().getManufacturedLabeledDrug()
                .setName(of.createEN());
        comp.getSubstanceAdministration().getConsumable().getManufacturedProduct().getManufacturedLabeledDrug()
                .getName().getContent().add(testKauppanimi);
        comp.getSubstanceAdministration().setDoseQuantity(of.createIVLPQ());
        comp.getSubstanceAdministration().getDoseQuantity().setCenter(of.createPQ());
        comp.getSubstanceAdministration().getDoseQuantity().getCenter().setValue(String.valueOf(testAinesosanMaara));
        comp.getSubstanceAdministration().getDoseQuantity().getCenter().setUnit(testAinesosanUnit);
        PQR pqr = of.createPQR();
        pqr.setOriginalText(of.createED());
        pqr.getOriginalText().getContent().add(testAinesosanmaaratekstina);
        comp.getSubstanceAdministration().getDoseQuantity().getTranslations().add(pqr);
        components.add(comp);

        // Ensin ei apteekissa valmistettava
        Whitebox.invokeMethod(tested, "puraVaikuttavatAinesosat", components, laakemaaraysTO);

        Assert.assertEquals(testLaaketietokantaVersio, laakemaaraysTO.getLaaketietokannanVersio());
        Assert.assertNull(laakemaaraysTO.getApteekissaValmistettavaLaake());
        // Fixme: Kumpi näistä? kauppanimi
        // Assert.assertEquals(testKauppanimi, laakemaaraysTO.getValmiste().getYksilointitiedot().getKauppanimi());
        // vai laakeaine
        Assert.assertEquals(testKauppanimi,
                laakemaaraysTO.getValmiste().getVaikuttavatAineet().iterator().next().getLaakeaine());

        // .. ja sitten aptekissa valmistettava
        laakemaaraysTO.setApteekissaValmistettavaLaake(true);
        comp.getSubstanceAdministration().getConsumable().getManufacturedProduct().getManufacturedLabeledDrug()
                .getName().getContent().clear();
        comp.getSubstanceAdministration().getConsumable().getManufacturedProduct().getManufacturedLabeledDrug()
                .getName().getContent().add(testKoodamatonNimi);

        Whitebox.invokeMethod(tested, "puraVaikuttavatAinesosat", components, laakemaaraysTO);

        Assert.assertFalse(laakemaaraysTO.getApteekissaValmistettavaLaake().getVaikuttavatAinesosat().isEmpty());
        VaikuttavaAinesosaTO vaikuttavaAinesosaTO = laakemaaraysTO.getApteekissaValmistettavaLaake()
                .getVaikuttavatAinesosat().iterator().next();
        Assert.assertEquals(testAtcCode, vaikuttavaAinesosaTO.getATCkoodi());
        Assert.assertEquals(testAtcName, vaikuttavaAinesosaTO.getATCNimi());
        Assert.assertEquals(testAinesosanUnit, vaikuttavaAinesosaTO.getAinesosanMaaraUnit());
        Assert.assertEquals(testAinesosanMaara, vaikuttavaAinesosaTO.getAinesosanMaaraValue(), 0);
        Assert.assertEquals(testAinesosanmaaratekstina, vaikuttavaAinesosaTO.getAinesosanMaaraTekstina());
        Assert.assertEquals(testKoodamatonNimi, vaikuttavaAinesosaTO.getKoodamatonNimi());
    }

    @Test
    public void testPuraValmiste() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Consumable consumable = of.createPOCDMT000040Consumable();

        Whitebox.invokeMethod(tested, "puraValmiste", Matchers.isNull(POCDMT000040Consumable.class), laakemaaraysTO);
        Assert.assertNull(laakemaaraysTO.getLaaketietokannanVersio());
        Assert.assertNull(laakemaaraysTO.getLaaketietokannanUlkopuolinenValmiste());

        Whitebox.invokeMethod(tested, "puraValmiste", consumable, laakemaaraysTO);
        Assert.assertNull(laakemaaraysTO.getLaaketietokannanVersio());
        Assert.assertNull(laakemaaraysTO.getLaaketietokannanUlkopuolinenValmiste());

        consumable.setManufacturedProduct(of.createPOCDMT000040ManufacturedProduct());

        Whitebox.invokeMethod(tested, "puraValmiste", consumable, laakemaaraysTO);
        Assert.assertNull(laakemaaraysTO.getLaaketietokannanVersio());
        Assert.assertNull(laakemaaraysTO.getLaaketietokannanUlkopuolinenValmiste());

        consumable.getManufacturedProduct().setManufacturedLabeledDrug(of.createPOCDMT000040LabeledDrug());
        consumable.getManufacturedProduct().getManufacturedLabeledDrug().setCode(of.createCE());
        consumable.getManufacturedProduct().getManufacturedLabeledDrug().getCode()
                .setCodeSystemVersion(testLaaketietokantaVersio);
        consumable.getManufacturedProduct().getManufacturedLabeledDrug().getCode().setCode(testAtcCode);
        consumable.getManufacturedProduct().getManufacturedLabeledDrug().getCode().setDisplayName(testAtcName);

        consumable.getManufacturedProduct().setManufacturedMaterial(of.createPOCDMT000040Material());
        consumable.getManufacturedProduct().getManufacturedMaterial().setName(of.createEN());
        consumable.getManufacturedProduct().getManufacturedMaterial().getName().getContent()
                .add(testLaaketietokannanulkopuolinenValmiste);

        Whitebox.invokeMethod(tested, "puraValmiste", consumable, laakemaaraysTO);
        Assert.assertEquals(testLaaketietokantaVersio, laakemaaraysTO.getLaaketietokannanVersio());
        Assert.assertEquals(testAtcCode, laakemaaraysTO.getValmiste().getYksilointitiedot().getATCkoodi());
        Assert.assertEquals(testAtcName, laakemaaraysTO.getValmiste().getYksilointitiedot().getATCnimi());

        Assert.assertEquals(testLaaketietokannanulkopuolinenValmiste,
                laakemaaraysTO.getLaaketietokannanUlkopuolinenValmiste());
    }

    @Test
    public void testPuraLaakeaineenVahvuus() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        IVLPQ doseQuantity = of.createIVLPQ();

        Whitebox.invokeMethod(tested, "puraLaakeaineenVahvuus", Matchers.isNull(IVLPQ.class), laakemaaraysTO);
        Whitebox.invokeMethod(tested, "puraLaakeaineenVahvuus", doseQuantity, laakemaaraysTO);

        doseQuantity.getTranslations().add(of.createPQR());
        doseQuantity.getTranslations().get(0).setOriginalText(of.createED());
        doseQuantity.getTranslations().get(0).getOriginalText().getContent().add(testVahvuus);

        Whitebox.invokeMethod(tested, "puraLaakeaineenVahvuus", doseQuantity, laakemaaraysTO);
        Assert.assertEquals(testVahvuus, laakemaaraysTO.getValmiste().getYksilointitiedot().getVahvuus());
    }

    @Test
    public void testPuraLaakemuoto() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation obs = luoTestLaakemuotoObservation();

        Whitebox.invokeMethod(tested, "puraLaakemuoto", obs, laakemaaraysTO);
        Assert.assertEquals(testLaakemuoto,
                laakemaaraysTO.getValmiste().getKayttotavat().iterator().next().getLaakemuoto());
    }

    @Test
    public void testPuraIterointi() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation obs = luoTestIterointiObservation();

        Whitebox.invokeMethod(tested, "puraIterointi", obs, laakemaaraysTO);

        Assert.assertEquals(testIterointiteksti, laakemaaraysTO.getIterointiTeksti());
        Assert.assertEquals(testIterointiUnit, laakemaaraysTO.getIterointienValiUnit());
        Assert.assertEquals(testIterointiValue, laakemaaraysTO.getIterointienValiValue().intValue());
        Assert.assertEquals(testIterointiMaara, laakemaaraysTO.getIterointienMaara().intValue());
    }

    @Test
    public void testPuraValmisteenlaji() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation obs = luoTestValmisteenLajiObservation();

        Whitebox.invokeMethod(tested, "puraValmisteenlaji", obs, laakemaaraysTO);

        Assert.assertEquals(testValmisteenLaji, laakemaaraysTO.getValmiste().getYksilointitiedot().getValmisteenLaji());
        Assert.assertEquals(testValmisteenLajiName,
                laakemaaraysTO.getValmiste().getYksilointitiedot().getValmisteenLajiNimi());
    }

    @Test
    public void testPuraValmisteenlaji_noValue() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        observation.setCode(of.createCD());
        observation.getCode().setCode("164");
        Whitebox.invokeMethod(tested, "puraValmisteenlaji", observation, laakemaaraysTO);

        Assert.assertNull("valmiste tulee olla null", laakemaaraysTO.getValmiste());
    }

    @Test
    public void testPuraSailytysastia() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation obs = luoTestSailytysastionObservation();

        Whitebox.invokeMethod(tested, "puraSailytysastia", obs, laakemaaraysTO);

        Assert.assertEquals(testSailytysastia, laakemaaraysTO.getValmiste().getYksilointitiedot().getSailytysastia());

    }

    @Test
    public void testPuraPakkauskoko() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation obs = luoTestPakkauskokoObservation();

        Whitebox.invokeMethod(tested, "puraPakkauskoko", obs, laakemaaraysTO);

        Assert.assertEquals(testPakkauskokoteksti,
                laakemaaraysTO.getValmiste().getYksilointitiedot().getPakkauskokoteksti());

    }

    @Test
    public void testPuraPakkauskoonKerroin() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation obs = luoTestPakkauskoonKerroinObservation();
        Whitebox.invokeMethod(tested, "puraPakkauskoonKerroin", obs, laakemaaraysTO);

        Assert.assertEquals(testPakkauskoonkerroin,
                laakemaaraysTO.getValmiste().getYksilointitiedot().getPakkauskokokerroin());
    }

    @Test
    public void testPuraApteekissavalmistettavanLaakkeenOsoitin() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation obs = luoTestApteekissavalmistettavanLaakkeenOsoitinObservation();
        Whitebox.invokeMethod(tested, "puraApteekissavalmistettavanLaakkeenOsoitin", obs, laakemaaraysTO);

        Assert.assertTrue(laakemaaraysTO.isApteekissaValmistettavaLaake());

    }

    @Test
    public void testPuraValmisteenJaPakkauksenSupplyEntryrelationshipObservationit() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        ArrayList<POCDMT000040EntryRelationship> entryrelationShips = new ArrayList<POCDMT000040EntryRelationship>();
        POCDMT000040EntryRelationship erLaakemuoto = of.createPOCDMT000040EntryRelationship();
        POCDMT000040EntryRelationship erIterointi = of.createPOCDMT000040EntryRelationship();
        POCDMT000040EntryRelationship erApteekissavalmistettava = of.createPOCDMT000040EntryRelationship();
        POCDMT000040EntryRelationship erPakkauskoonkerroin = of.createPOCDMT000040EntryRelationship();
        POCDMT000040EntryRelationship erPakkauskoko = of.createPOCDMT000040EntryRelationship();
        POCDMT000040EntryRelationship erSailytysastia = of.createPOCDMT000040EntryRelationship();
        POCDMT000040EntryRelationship erValmisteenlaji = of.createPOCDMT000040EntryRelationship();
        POCDMT000040EntryRelationship erMuu = of.createPOCDMT000040EntryRelationship();

        erLaakemuoto.setObservation(luoTestLaakemuotoObservation());
        erIterointi.setObservation(luoTestIterointiObservation());
        erApteekissavalmistettava.setObservation(luoTestApteekissavalmistettavanLaakkeenOsoitinObservation());
        erPakkauskoonkerroin.setObservation(luoTestPakkauskoonKerroinObservation());
        erPakkauskoko.setObservation(luoTestPakkauskokoObservation());
        erSailytysastia.setObservation(luoTestSailytysastionObservation());
        erValmisteenlaji.setObservation(luoTestApteekkivalmisteValmisteenLajiObservation());
        erMuu.setObservation(of.createPOCDMT000040Observation());
        erMuu.getObservation().setCode(of.createCD());
        erMuu.getObservation().getCode().setCode("MUUCODE");

        entryrelationShips.add(erLaakemuoto);
        entryrelationShips.add(erIterointi);
        entryrelationShips.add(erApteekissavalmistettava);
        entryrelationShips.add(erPakkauskoonkerroin);
        entryrelationShips.add(erPakkauskoko);
        entryrelationShips.add(erSailytysastia);
        entryrelationShips.add(erValmisteenlaji);
        entryrelationShips.add(erMuu);

        Whitebox.invokeMethod(tested, "puraValmisteenJaPakkauksenSupplyEntryrelationshipObservationit",
                entryrelationShips, laakemaaraysTO);

        Assert.assertEquals(testLaakemuoto,
                laakemaaraysTO.getValmiste().getKayttotavat().iterator().next().getLaakemuoto());
        Assert.assertEquals(testIterointiMaara, laakemaaraysTO.getIterointienMaara().intValue());
        Assert.assertTrue(laakemaaraysTO.isApteekissaValmistettavaLaake());
        Assert.assertEquals(testPakkauskoonkerroin,
                laakemaaraysTO.getValmiste().getYksilointitiedot().getPakkauskokokerroin());
        Assert.assertEquals(testPakkauskokoteksti,
                laakemaaraysTO.getValmiste().getYksilointitiedot().getPakkauskokoteksti());
        Assert.assertEquals(testSailytysastia, laakemaaraysTO.getValmiste().getYksilointitiedot().getSailytysastia());
        Assert.assertEquals("7", laakemaaraysTO.getValmiste().getYksilointitiedot().getValmisteenLaji());

    }

    @Test
    public void testPuraReseptinTyyppijaMaaraTiedot() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        String testReseptinTyyppi = "Koira";
        Whitebox.invokeMethod(tested, "puraReseptinTyyppijaMaaraTiedot", Matchers.isNull(POCDMT000040Supply.class),
                laakemaaraysTO);
        Assert.assertNull(laakemaaraysTO.getReseptintyyppi());

        POCDMT000040Supply supply = of.createPOCDMT000040Supply();
        Whitebox.invokeMethod(tested, "puraReseptinTyyppijaMaaraTiedot", supply, laakemaaraysTO);
        Assert.assertNull(laakemaaraysTO.getReseptintyyppi());

        supply.setCode(of.createCD());
        supply.getCode().setCode(testReseptinTyyppi);
        Whitebox.invokeMethod(tested, "puraReseptinTyyppijaMaaraTiedot", supply, laakemaaraysTO);
        Assert.assertEquals(testReseptinTyyppi, laakemaaraysTO.getReseptintyyppi());

        // Reseptintyyppi 1
        supply.getCode().setCode("1");
        supply.setRepeatNumber(of.createIVLINT());
        supply.getRepeatNumber().setValue(BigInteger.valueOf(testPakkauksienLukumaara));
        supply.setQuantity(of.createPQ());
        supply.getQuantity().setUnit(testPakkausyksikko);
        supply.getQuantity().setValue(String.valueOf(testPakkauskoko));
        Whitebox.invokeMethod(tested, "puraReseptinTyyppijaMaaraTiedot", supply, laakemaaraysTO);
        Assert.assertEquals(testPakkauksienLukumaara, laakemaaraysTO.getPakkauksienLukumaara().intValue());
        Assert.assertEquals(testPakkausyksikko, laakemaaraysTO.getValmiste().getYksilointitiedot().getPakkausyksikko());
        Assert.assertEquals(testPakkauskoko, laakemaaraysTO.getValmiste().getYksilointitiedot().getPakkauskoko(), 0);

        // Reseptintyyppi 2
        supply.getCode().setCode("2");
        supply.setQuantity(of.createPQ());
        supply.getQuantity().setUnit(testLaakkeenkokonaismaaraUnit);
        supply.getQuantity().setValue(String.valueOf(testLaakeenkokonaismaaraValue));
        Whitebox.invokeMethod(tested, "puraReseptinTyyppijaMaaraTiedot", supply, laakemaaraysTO);
        Assert.assertEquals(testLaakkeenkokonaismaaraUnit, laakemaaraysTO.getLaakkeenKokonaismaaraUnit());
        Assert.assertEquals(testLaakeenkokonaismaaraValue, laakemaaraysTO.getLaakkeenKokonaismaaraValue().intValue());

        // Reseptintyyppi 3
        supply.getCode().setCode("3");
        IVLTS aika = of.createIVLTS();
        aika.setLow(of.createIVXBTS());
        aika.getLow().setValue(testAjallemaaratynAlkuaika);
        aika.setWidth(of.createPQ());
        aika.getWidth().setUnit(testAjallemaaratynUnit);
        aika.getWidth().setValue(String.valueOf(testAjallemaaratynValue));
        supply.getEffectiveTimes().add(aika);
        Whitebox.invokeMethod(tested, "puraReseptinTyyppijaMaaraTiedot", supply, laakemaaraysTO);
        Assert.assertNotNull(laakemaaraysTO.getAjalleMaaratynReseptinAlkuaika());
        Assert.assertEquals(testAjallemaaratynUnit, laakemaaraysTO.getAjalleMaaratynReseptinAikamaaraUnit());
        Assert.assertEquals(testAjallemaaratynValue,
                laakemaaraysTO.getAjalleMaaratynReseptinAikamaaraValue().intValue());
    }

    @Test
    public void testPuraTuoteTiedot() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Supply supply = of.createPOCDMT000040Supply();

        Whitebox.invokeMethod(tested, "puraTuoteTiedot", Matchers.isNull(POCDMT000040Supply.class), laakemaaraysTO);

        Whitebox.invokeMethod(tested, "puraTuoteTiedot", supply, laakemaaraysTO);

        supply.setProduct(of.createPOCDMT000040Product());
        supply.getProduct().setManufacturedProduct(of.createPOCDMT000040ManufacturedProduct());
        supply.getProduct().getManufacturedProduct().setManufacturedLabeledDrug(of.createPOCDMT000040LabeledDrug());
        supply.getProduct().getManufacturedProduct().getManufacturedLabeledDrug().setCode(of.createCE());
        supply.getProduct().getManufacturedProduct().getManufacturedLabeledDrug().getCode().setCode(testAtcCode);
        supply.getProduct().getManufacturedProduct().getManufacturedLabeledDrug().getCode()
                .setDisplayName(testKauppanimi);
        supply.getProduct().getManufacturedProduct().getManufacturedLabeledDrug().setName(of.createEN());
        Whitebox.invokeMethod(tested, "puraTuoteTiedot", supply, laakemaaraysTO);
        Assert.assertEquals(testAtcCode, laakemaaraysTO.getValmiste().getYksilointitiedot().getYksilointitunnus());
        Assert.assertEquals(testKauppanimi, laakemaaraysTO.getValmiste().getYksilointitiedot().getKauppanimi());

        supply.getProduct().getManufacturedProduct().getManufacturedLabeledDrug().getName().getContent()
                .add("Kauppanimi");

        Whitebox.invokeMethod(tested, "puraTuoteTiedot", supply, laakemaaraysTO);
        Assert.assertEquals("Kauppanimi", laakemaaraysTO.getValmiste().getYksilointitiedot().getKauppanimi());

    }

    @Test
    public void testPuraParticipantTiedot() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Supply supply = of.createPOCDMT000040Supply();
        Whitebox.invokeMethod(tested, "puraParticipantTiedot", Matchers.isNull(POCDMT000040Supply.class),
                laakemaaraysTO);

        Whitebox.invokeMethod(tested, "puraParticipantTiedot", supply, laakemaaraysTO);

        supply.getParticipants().add(luoTestParticipant("OWN", testMyyntiluvanhaltija));
        supply.getParticipants().add(luoTestParticipant("EMP", testTyonantaja));
        supply.getParticipants().add(luoTestParticipant("PAYOR", testVakuutuslaitos));
        supply.getParticipants().add(luoTestParticipant("TONTTU", "EIVÄLIÄ"));

        Whitebox.invokeMethod(tested, "puraParticipantTiedot", supply, laakemaaraysTO);
        Assert.assertEquals(testMyyntiluvanhaltija,
                laakemaaraysTO.getValmiste().getYksilointitiedot().getMyyntiluvanHaltija());
        Assert.assertEquals(testTyonantaja, laakemaaraysTO.getTyonantaja());
        Assert.assertEquals(testVakuutuslaitos, laakemaaraysTO.getVakuutuslaitos());
    }

    @Test
    public void testPuraVoimassaolonLoppuaika_valuena() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040SubstanceAdministration sa = null;

        Whitebox.invokeMethod(tested, "puraVoimassaolonLoppuaika", sa, laakemaaraysTO);
        sa = of.createPOCDMT000040SubstanceAdministration();
        Whitebox.invokeMethod(tested, "puraVoimassaolonLoppuaika", sa, laakemaaraysTO);
        sa.getEffectiveTimes().add(of.createSXCMTS());
        sa.getEffectiveTimes().get(0).setValue(testEffectiveTime);
        Whitebox.invokeMethod(tested, "puraVoimassaolonLoppuaika", sa, laakemaaraysTO);
        Assert.assertNotNull(laakemaaraysTO.getMaarayspaiva());
    }

    @Test
    public void testPuraVoimassaolonLoppuaika_LowHigh() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040SubstanceAdministration sa = null;
        sa = of.createPOCDMT000040SubstanceAdministration();
        IVLTS ilvts = of.createIVLTS();
        ilvts.setLow(of.createIVXBTS());
        ilvts.getLow().setValue(testEffectiveTime);
        ilvts.setHigh(of.createIVXBTS());
        ilvts.getHigh().setValue(testEffectiveTime);
        sa.getEffectiveTimes().add(ilvts);
        Whitebox.invokeMethod(tested, "puraVoimassaolonLoppuaika", sa, laakemaaraysTO);
        Assert.assertNotNull(laakemaaraysTO.getMaarayspaiva());
        Assert.assertNotNull(laakemaaraysTO.getLaakemaarayksenVoimassaolonLoppuaika());
    }

    @Test
    public void testPuraLaakevalmisteenJaPakkauksenTiedot() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Component4 component = of.createPOCDMT000040Component4();
        component.setSubstanceAdministration(of.createPOCDMT000040SubstanceAdministration());
        component.getSubstanceAdministration().getEntryRelationships().add(of.createPOCDMT000040EntryRelationship());
        component.getSubstanceAdministration().getEntryRelationships().get(0).setSupply(of.createPOCDMT000040Supply());

        Whitebox.invokeMethod(tested, "puraLaakevalmisteenJaPakkauksenTiedot", component, laakemaaraysTO);

        // valmistusohje
        component.getSubstanceAdministration().setText(of.createED());
        component.getSubstanceAdministration().getText().getContent().add(testValmistusohje);
        laakemaaraysTO.setApteekissaValmistettavaLaake(true);

        Whitebox.invokeMethod(tested, "puraLaakevalmisteenJaPakkauksenTiedot", component, laakemaaraysTO);

        Assert.assertEquals(testValmistusohje, laakemaaraysTO.getApteekissaValmistettavaLaake().getValmistusohje());

    }

    @Test(expected = Exception.class)
    public void testHaeEntryt_nullCDA() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument cda = null;
        Whitebox.invokeMethod(tested, "haeEntryt", cda);
    }

    @Test(expected = Exception.class)
    public void testHaeEntryt_nullCDAComponent() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        Whitebox.invokeMethod(tested, "haeEntryt", cda);
    }

    @Test(expected = Exception.class)
    public void testHaeEntryt_nullCDAComponentStructuredbody() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        cda.setComponent(of.createPOCDMT000040Component2());
        Whitebox.invokeMethod(tested, "haeEntryt", cda);
    }

    @Test(expected = Exception.class)
    public void testHaeEntryt_emptyCDAComponentStructuredbodyComponents() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        cda.setComponent(of.createPOCDMT000040Component2());
        cda.getComponent().setStructuredBody(of.createPOCDMT000040StructuredBody());
        Whitebox.invokeMethod(tested, "haeEntryt", cda);
    }

    @Test(expected = Exception.class)
    public void testHaeEntryt_nullCDAComponentStructuredbodyComponentSection() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        cda.setComponent(of.createPOCDMT000040Component2());
        cda.getComponent().setStructuredBody(of.createPOCDMT000040StructuredBody());
        cda.getComponent().getStructuredBody().getComponents().add(of.createPOCDMT000040Component3());
        Whitebox.invokeMethod(tested, "haeEntryt", cda);
    }

    @Test(expected = Exception.class)
    public void testHaeEntryt_emptyCDAComponentStructuredbodyComponentSectionComponents() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        cda.setComponent(of.createPOCDMT000040Component2());
        cda.getComponent().setStructuredBody(of.createPOCDMT000040StructuredBody());
        cda.getComponent().getStructuredBody().getComponents().add(of.createPOCDMT000040Component3());
        cda.getComponent().getStructuredBody().getComponents().get(0).setSection(of.createPOCDMT000040Section());
        Whitebox.invokeMethod(tested, "haeEntryt", cda);
    }

    @Test(expected = Exception.class)
    public void testHaeEntryt_nullCDAComponentStructuredbodyComponentSectionComponentSection() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        cda.setComponent(of.createPOCDMT000040Component2());
        cda.getComponent().setStructuredBody(of.createPOCDMT000040StructuredBody());
        cda.getComponent().getStructuredBody().getComponents().add(of.createPOCDMT000040Component3());
        cda.getComponent().getStructuredBody().getComponents().get(0).setSection(of.createPOCDMT000040Section());
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents()
                .add(of.createPOCDMT000040Component5());
        Whitebox.invokeMethod(tested, "haeEntryt", cda);
    }

    @Test(expected = Exception.class)
    public void testHaeEntryt_emptyCDAComponentStructuredbodyComponentSectionComponentSectionComponents()
            throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        cda.setComponent(of.createPOCDMT000040Component2());
        cda.getComponent().setStructuredBody(of.createPOCDMT000040StructuredBody());
        cda.getComponent().getStructuredBody().getComponents().add(of.createPOCDMT000040Component3());
        cda.getComponent().getStructuredBody().getComponents().get(0).setSection(of.createPOCDMT000040Section());
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents()
                .add(of.createPOCDMT000040Component5());
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents().get(0)
                .setSection(of.createPOCDMT000040Section());
        Whitebox.invokeMethod(tested, "haeEntryt", cda);
    }

    @Test(expected = Exception.class)
    public void testHaeEntryt_nullCDAComponentStructuredbodyComponentSectionComponentSectionComponentSection()
            throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        cda.setComponent(of.createPOCDMT000040Component2());
        cda.getComponent().setStructuredBody(of.createPOCDMT000040StructuredBody());
        cda.getComponent().getStructuredBody().getComponents().add(of.createPOCDMT000040Component3());
        cda.getComponent().getStructuredBody().getComponents().get(0).setSection(of.createPOCDMT000040Section());
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents()
                .add(of.createPOCDMT000040Component5());
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents().get(0)
                .setSection(of.createPOCDMT000040Section());
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents().get(0).getSection()
                .getComponents().add(of.createPOCDMT000040Component5());
        Whitebox.invokeMethod(tested, "haeEntryt", cda);
    }

    @Test
    public void testPuraEntryt() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        cda.setComponent(of.createPOCDMT000040Component2());
        cda.getComponent().setStructuredBody(of.createPOCDMT000040StructuredBody());
        cda.getComponent().getStructuredBody().getComponents().add(of.createPOCDMT000040Component3());
        cda.getComponent().getStructuredBody().getComponents().get(0).setSection(of.createPOCDMT000040Section());
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents()
                .add(of.createPOCDMT000040Component5());
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents().get(0)
                .setSection(of.createPOCDMT000040Section());
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents().get(0).getSection()
                .getComponents().add(of.createPOCDMT000040Component5());
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents().get(0).getSection()
                .getComponents().get(0).setSection(of.createPOCDMT000040Section());
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents().get(0).getSection()
                .getComponents().get(0).getSection().getEntries()
                .add(luoEntry(KantaCDAConstants.Laakityslista.LAAKEVALMISTEEN_JA_PAKKAUKSEN_TIEDOT));
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents().get(0).getSection()
                .getComponents().get(0).getSection().getEntries().get(0).getOrganizer().getComponents()
                .add(of.createPOCDMT000040Component4());
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents().get(0).getSection()
                .getComponents().get(0).getSection().getEntries()
                .add(luoEntry(KantaCDAConstants.Laakityslista.LAAKEVALMISTEEN_JA_PAKKAUKSEN_TIEDOT));
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents().get(0).getSection()
                .getComponents().get(0).getSection().getEntries()
                .add(luoEntry(KantaCDAConstants.Laakityslista.VAIKUTTAVAT_AINESOSAT));
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents().get(0).getSection()
                .getComponents().get(0).getSection().getEntries()
                .add(luoEntry(KantaCDAConstants.Laakityslista.MUUT_AINESOSAT));
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents().get(0).getSection()
                .getComponents().get(0).getSection().getEntries()
                .add(luoEntry(KantaCDAConstants.Laakityslista.ANNOSOSIO_JA_JATKOOSIOT));
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents().get(0).getSection()
                .getComponents().get(0).getSection().getEntries()
                .add(luoEntry(KantaCDAConstants.Laakityslista.RESEPTIN_MUUT_TIEDOT));
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents().get(0).getSection()
                .getComponents().get(0).getSection().getEntries()
                .add(luoEntry(KantaCDAConstants.Laakityslista.LAAKEMAARAYKSEN_KORJAUKSEN_MUUT_TIEDOT));
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents().get(0).getSection()
                .getComponents().get(0).getSection().getEntries().add(luoEntry("kala"));

        Whitebox.invokeMethod(tested, "puraEntryt", cda, laakemaaraysTO);
    }

    @Test
    public void testPuraLaakemaarayksenKorjauksenMuutTiedot() throws Exception {
        LaakemaarayksenKorjausTO laakemaaraysTO = new LaakemaarayksenKorjausTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        ArrayList<POCDMT000040Component4> components = new ArrayList<POCDMT000040Component4>();
        String kayttotarkoitus = "Käyttötarkoitus";
        String hoitolaji = "Hoitolaji";
        String uusimiskiellonperustelu = "Uusimiskiellonperustelu";
        int alle12vuotiaanPainoValue = 12;
        String alle12vuotiaanPainoUnit = "alle12-vuotiaanpainounit";
        String annosjakeluText = "Annosjakeluteksti";
        String viestiApteekille = "ViestiApteekille";
        String erillisselvitysCode = "01P";
        String erillisselvitysText = "Nielemisvaikeudet";
        String pkvLaake = "PKVlaake";
        String reseptinlaji = "Reseptinlaji";
        String tunnistaminen = "3";
        String tunnistamisTeksti = "Tunnistamisteksit";
        components.add(luoTestComponent(of.createST(), kayttotarkoitus,
                KantaCDAConstants.Laakityslista.KAYTTOTARKOITUS_TEKSTINA));
        components.add(luoTestComponent(of.createCE(), hoitolaji, KantaCDAConstants.Laakityslista.HOITOLAJI));
        components.add(luoTestComponent(of.createBL(), "true", KantaCDAConstants.Laakityslista.PYSYVA_LAAKITYS));
        components.add(luoTestComponent(of.createBL(), "true", KantaCDAConstants.Laakityslista.UUSIMISKIELTO));
        components.add(luoTestComponent(of.createBL(), "true", KantaCDAConstants.Laakityslista.LAAKEVAIHTOKIELTO));
        components.add(
                luoTestComponent(of.createST(), viestiApteekille, KantaCDAConstants.Laakityslista.VIESTI_APTEEKILLE));
        components.add(luoTestComponent(of.createCE(), pkvLaake,
                KantaCDAConstants.Laakityslista.HUUMAUSAINE_PKV_LAAKEMAARAYS));
        components.add(luoTestComponent(of.createBL(), "true",
                KantaCDAConstants.Laakityslista.KYSEESSA_LAAKKEEN_KAYTON_ALOITUS));
        components.add(luoTestComponent(of.createBL(), "true", KantaCDAConstants.Laakityslista.HUUME));
        components.add(luoTestComponent(of.createCE(), reseptinlaji, KantaCDAConstants.Laakityslista.RESEPTIN_LAJI));
        components.add(luoTestComponent(of.createCE(), apteekissaTallennettuLaakemaaraysValue,
                KantaCDAConstants.Laakityslista.APTEEKISSA_TALLENNETTU_LAAKEMAARAYS));

        // 194 uusimiskiellonperustelu
        POCDMT000040Component4 compUP = luoTestComponent(of.createCE(), "DILEDINDDILE",
                KantaCDAConstants.Laakityslista.UUDISTAMISKIELLON_SYY);
        ED originalText = of.createED();
        originalText.getContent().add(uusimiskiellonperustelu);
        ((CE) compUP.getObservation().getValues().get(0)).setOriginalText(originalText);
        components.add(compUP);

        // 89 alle 12-vuotiaan paino
        POCDMT000040Component4 compA12VP = luoTestComponent(of.createPQ(), String.valueOf(alle12vuotiaanPainoValue),
                KantaCDAConstants.Laakityslista.ALLE_12VUOTIAAN_PAINO);
        ((PQ) compA12VP.getObservation().getValues().get(0)).setUnit(alle12vuotiaanPainoUnit);
        components.add(compA12VP);

        // 91 annosjakelu
        POCDMT000040Component4 compAJ = luoTestComponent(of.createBL(), "true",
                KantaCDAConstants.Laakityslista.ANNOSJAKELU);
        compAJ.getObservation().setText(of.createED());
        compAJ.getObservation().getText().getContent().add(annosjakeluText);
        components.add(compAJ);

        // 69 erillisselvitys
        POCDMT000040Component4 compES = luoTestComponent(of.createCE(), erillisselvitysCode,
                KantaCDAConstants.Laakityslista.ERILLISSELVITYS);
        compES.getObservation().setText(of.createST());
        compES.getObservation().getText().getContent().add(erillisselvitysText);
        components.add(compES);

        // 117 Potilaan tunnistaminen
        POCDMT000040Component4 compPT = luoTestComponent(of.createCE(), tunnistaminen,
                KantaCDAConstants.Laakityslista.POTILAAN_TUNNISTAMINEN);
        compPT.getObservation().setText(of.createED());
        compPT.getObservation().getText().getContent().add(tunnistamisTeksti);
        components.add(compPT);

        // 97 Korjauksen perustelu
        components.add(luoKorjauksenPerusteuluComponent());

        Whitebox.invokeMethod(tested, "puraLaakemaarayksenMuutTiedot", components, laakemaaraysTO);
        Assert.assertEquals(kayttotarkoitus, laakemaaraysTO.getKayttotarkoitusTeksti());
        Assert.assertEquals(hoitolaji, laakemaaraysTO.getHoitolajit().get(0));
        Assert.assertTrue(laakemaaraysTO.isPysyvaislaakitys());
        Assert.assertTrue(laakemaaraysTO.isUudistamiskielto());
        Assert.assertEquals(uusimiskiellonperustelu, laakemaaraysTO.getUusimiskiellonPerustelu());
        Assert.assertTrue(laakemaaraysTO.isLaakevaihtokielto());
        Assert.assertEquals("alle 12-vuotiaan paino unit pitää olla: " + alle12vuotiaanPainoUnit,
                alle12vuotiaanPainoUnit, laakemaaraysTO.getAlle12VuotiaanPainoUnit());
        Assert.assertEquals("Alle 12-vuotiaan paino valuen pitää olla: " + alle12vuotiaanPainoValue,
                alle12vuotiaanPainoValue, laakemaaraysTO.getAlle12VuotiaanPainoValue().intValue());
        Assert.assertTrue(laakemaaraysTO.isAnnosjakelu());
        Assert.assertEquals("Annosjakelu teksti pitää olla:" + annosjakeluText, annosjakeluText,
                laakemaaraysTO.getAnnosjakeluTeksti());
        Assert.assertEquals("Viesti apteekille pitää olla: " + viestiApteekille, viestiApteekille,
                laakemaaraysTO.getViestiApteekille());
        Assert.assertEquals("Erillisselvitys pitää olla: " + erillisselvitysCode, erillisselvitysCode,
                laakemaaraysTO.getErillisselvitys());
        Assert.assertEquals("Erillisselvitys teksti pitää olla: " + erillisselvitysText, erillisselvitysText,
                laakemaaraysTO.getErillisselvitysteksti());
        Assert.assertEquals(tunnistaminen, laakemaaraysTO.getPotilaanTunnistaminen());
        Assert.assertEquals(tunnistamisTeksti, laakemaaraysTO.getPotilaanTunnistaminenTeksti());
        Assert.assertEquals(pkvLaake, laakemaaraysTO.getPKVlaakemaarays());
        Assert.assertTrue(laakemaaraysTO.isKyseessaLaakkeenkaytonAloitus());
        Assert.assertTrue(laakemaaraysTO.isHuume());
        Assert.assertEquals(reseptinlaji, laakemaaraysTO.getReseptinLaji());
        Assert.assertEquals(testKorjausperustelu, laakemaaraysTO.getKorjauksenPerustelu());
        Assert.assertEquals(apteekissaTallennettuLaakemaaraysValue,
                laakemaaraysTO.getApteekissaTallennettuLaakemaarays());
        // Assert.assertNotNull(laakemaaraysTO.getKorjaaja());
    }

    @Test
    public void testPuraValmisteenlajiVanhatAsiakirjaversiot() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        laakemaaraysTO.setValmiste(new ValmisteTO());
        laakemaaraysTO.getValmiste().setYksilointitiedot(new ValmisteenYksilointitiedotTO());
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040LabeledDrug drug = of.createPOCDMT000040LabeledDrug();

        Whitebox.invokeMethod(tested, "puraValmisteenlaji", drug, laakemaaraysTO);

        drug.setCode(of.createCE());
        drug.getCode().setCode(testAtcCode);
        drug.getCode().setDisplayName(testKauppanimi);
        drug.setName(of.createEN());
        drug.getCode().getTranslations().add(of.createCD());
        drug.getCode().getTranslations().get(0).getQualifiers().add(of.createCR());
        CR qualifier = drug.getCode().getTranslations().get(0).getQualifiers().get(0);
        qualifier.setValue(of.createCD());
        qualifier.getValue().setCode(testValmisteenLaji);
        qualifier.getValue().setDisplayName(testValmisteenLajiName);
        laakemaaraysTO.setAsiakirjaVersio("1.2.246.777.11.2011.18");
        laakemaaraysTO.setAsiakirjaYhteensopivuus(KantaCDAConstants.AsiakirjaVersioYhteensopivuus.TAAKSEPAIN);

        Whitebox.invokeMethod(tested, "puraValmisteenlaji", drug, laakemaaraysTO);

        Assert.assertEquals(testValmisteenLaji, laakemaaraysTO.getValmiste().getYksilointitiedot().getValmisteenLaji());
        Assert.assertEquals(testValmisteenLajiName,
                laakemaaraysTO.getValmiste().getYksilointitiedot().getValmisteenLajiNimi());
    }

    @Test
    public void testPuraTuoteTiedotVanhatAsiakirjaversiot() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Supply supply = of.createPOCDMT000040Supply();

        Whitebox.invokeMethod(tested, "puraTuoteTiedot", Matchers.isNull(POCDMT000040Supply.class), laakemaaraysTO);

        Whitebox.invokeMethod(tested, "puraTuoteTiedot", supply, laakemaaraysTO);

        supply.setProduct(of.createPOCDMT000040Product());
        supply.getProduct().setManufacturedProduct(of.createPOCDMT000040ManufacturedProduct());
        supply.getProduct().getManufacturedProduct().setManufacturedLabeledDrug(of.createPOCDMT000040LabeledDrug());
        supply.getProduct().getManufacturedProduct().getManufacturedLabeledDrug().setCode(of.createCE());
        supply.getProduct().getManufacturedProduct().getManufacturedLabeledDrug().getCode().setCode(testAtcCode);
        supply.getProduct().getManufacturedProduct().getManufacturedLabeledDrug().getCode()
                .setDisplayName(testKauppanimi);
        supply.getProduct().getManufacturedProduct().getManufacturedLabeledDrug().setName(of.createEN());
        supply.getProduct().getManufacturedProduct().getManufacturedLabeledDrug().getCode().getTranslations()
                .add(of.createCD());
        supply.getProduct().getManufacturedProduct().getManufacturedLabeledDrug().getCode().getTranslations().get(0)
                .getQualifiers().add(of.createCR());
        CR qualifier = supply.getProduct().getManufacturedProduct().getManufacturedLabeledDrug().getCode()
                .getTranslations().get(0).getQualifiers().get(0);
        qualifier.setValue(of.createCD());
        qualifier.getValue().setCode(testValmisteenLaji);
        qualifier.getValue().setDisplayName(testValmisteenLajiName);
        laakemaaraysTO.setAsiakirjaVersio("1.2.246.777.11.2011.18");
        laakemaaraysTO.setAsiakirjaYhteensopivuus(KantaCDAConstants.AsiakirjaVersioYhteensopivuus.TAAKSEPAIN);
        Whitebox.invokeMethod(tested, "puraTuoteTiedot", supply, laakemaaraysTO);
        Assert.assertEquals(testAtcCode, laakemaaraysTO.getValmiste().getYksilointitiedot().getYksilointitunnus());
        Assert.assertEquals(testKauppanimi, laakemaaraysTO.getValmiste().getYksilointitiedot().getKauppanimi());
        Assert.assertEquals(testValmisteenLaji, laakemaaraysTO.getValmiste().getYksilointitiedot().getValmisteenLaji());
        Assert.assertEquals(testValmisteenLajiName,
                laakemaaraysTO.getValmiste().getYksilointitiedot().getValmisteenLajiNimi());

        supply.getProduct().getManufacturedProduct().getManufacturedLabeledDrug().getName().getContent()
                .add("Kauppanimi");

        Whitebox.invokeMethod(tested, "puraTuoteTiedot", supply, laakemaaraysTO);
        Assert.assertEquals("Kauppanimi", laakemaaraysTO.getValmiste().getYksilointitiedot().getKauppanimi());

    }

    @Test
    public void testPuraMitatoinninSyy() throws Exception {
        LaakemaarayksenMitatointiTO laakemaaraysTO = new LaakemaarayksenMitatointiTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation observation = luoTestMitatoinninSyyObservation();
        Whitebox.invokeMethod(tested, "puraLaakemaarayksenMitatoinninSyy", observation, laakemaaraysTO);
        Assert.assertEquals(testMitatointiSyy, laakemaaraysTO.getMitatoinninSyyKoodi());
        Assert.assertEquals(testMitatointiPerustelu, laakemaaraysTO.getMitatoinninPerustelu());
    }

    @Test
    public void testPuraMitatoinninTyyppi() throws Exception {
        LaakemaarayksenMitatointiTO laakemaaraysTO = new LaakemaarayksenMitatointiTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation observation = luoTestMitatoinninTyyppiObservation();
        Whitebox.invokeMethod(tested, "puraLaakemaarayksenMitatoinninTyyppi", observation, laakemaaraysTO);
        Assert.assertEquals(testMitatointiTyyppi, laakemaaraysTO.getMitatoinninTyyppiKoodi());
        Assert.assertEquals(testMitatointiSuostumus, laakemaaraysTO.getMitatoinninSuostumusKoodi());
    }

    @Test
    public void testPuraMitatoinninSyyObsNull() throws Exception {
        LaakemaarayksenMitatointiTO laakemaaraysTO = new LaakemaarayksenMitatointiTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        Whitebox.invokeMethod(tested, "puraLaakemaarayksenMitatoinninSyy",
                Matchers.isNull(POCDMT000040Observation.class), laakemaaraysTO);
        Assert.assertNull(laakemaaraysTO.getMitatoinninSyyKoodi());
        Assert.assertNull(laakemaaraysTO.getMitatoinninPerustelu());
    }

    @Test
    public void testPuraMitatoinninTyyppiObsNull() throws Exception {
        LaakemaarayksenMitatointiTO laakemaaraysTO = new LaakemaarayksenMitatointiTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        Whitebox.invokeMethod(tested, "puraLaakemaarayksenMitatoinninTyyppi",
                Matchers.isNull(POCDMT000040Observation.class), laakemaaraysTO);
        Assert.assertNull(laakemaaraysTO.getMitatoinninTyyppiKoodi());
        Assert.assertNull(laakemaaraysTO.getMitatoinninSuostumusKoodi());
    }

    @Test
    public void testPuraMitatoinninSyyValuesEmpty() throws Exception {
        LaakemaarayksenMitatointiTO laakemaaraysTO = new LaakemaarayksenMitatointiTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        Whitebox.invokeMethod(tested, "puraLaakemaarayksenMitatoinninSyy", observation, laakemaaraysTO);
        Assert.assertNull(laakemaaraysTO.getMitatoinninSyyKoodi());
        Assert.assertNull(laakemaaraysTO.getMitatoinninPerustelu());
    }

    @Test
    public void testPuraMitatoinninTyyppiValuesEmpty() throws Exception {
        LaakemaarayksenMitatointiTO laakemaaraysTO = new LaakemaarayksenMitatointiTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        Whitebox.invokeMethod(tested, "puraLaakemaarayksenMitatoinninTyyppi", observation, laakemaaraysTO);
        Assert.assertNull(laakemaaraysTO.getMitatoinninTyyppiKoodi());
        Assert.assertNull(laakemaaraysTO.getMitatoinninSuostumusKoodi());
    }

    @Test
    public void testPuraMitatoinninSyyValuePuuttuu() throws Exception {
        LaakemaarayksenMitatointiTO laakemaaraysTO = new LaakemaarayksenMitatointiTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation observation = luoTestMitatoinninSyyObservation();
        observation.getValues().remove(1);
        Whitebox.invokeMethod(tested, "puraLaakemaarayksenMitatoinninSyy", observation, laakemaaraysTO);
        Assert.assertEquals(testMitatointiSyy, laakemaaraysTO.getMitatoinninSyyKoodi());
        Assert.assertNull(laakemaaraysTO.getMitatoinninPerustelu());
    }

    @Test
    public void testPuraMitatoinninTyyppiQualifierPuuttuu() throws Exception {
        LaakemaarayksenMitatointiTO laakemaaraysTO = new LaakemaarayksenMitatointiTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation observation = luoTestMitatoinninTyyppiObservation();
        CD value = (CD) observation.getValues().get(0);
        value.getQualifiers().clear();
        Whitebox.invokeMethod(tested, "puraLaakemaarayksenMitatoinninTyyppi", observation, laakemaaraysTO);
        Assert.assertEquals(testMitatointiTyyppi, laakemaaraysTO.getMitatoinninTyyppiKoodi());
        Assert.assertNull(laakemaaraysTO.getMitatoinninSuostumusKoodi());
    }

    @Test
    public void testPuraLaakemaarayksenMitatoinninMuutTiedot() throws Exception {
        LaakemaarayksenMitatointiTO laakemaaraysTO = new LaakemaarayksenMitatointiTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        ArrayList<POCDMT000040Component4> components = new ArrayList<POCDMT000040Component4>();
        String kayttotarkoitus = "Käyttötarkoitus";
        String hoitolaji = "Hoitolaji";
        String uusimiskiellonperustelu = "Uusimiskiellonperustelu";
        int alle12vuotiaanPainoValue = 12;
        String alle12vuotiaanPainoUnit = "alle12-vuotiaanpainounit";
        String annosjakeluText = "Annosjakeluteksti";
        String viestiApteekille = "ViestiApteekille";
        String erillisselvitysCode = "01P";
        String erillisselvitysText = "Nielemisvaikeudet";
        String pkvLaake = "PKVlaake";
        String reseptinlaji = "Reseptinlaji";
        String tunnistaminen = "3";
        String tunnistamisTeksti = "Tunnistamisteksit";

        components.add(luoTestComponent(of.createST(), kayttotarkoitus,
                KantaCDAConstants.Laakityslista.KAYTTOTARKOITUS_TEKSTINA));
        components.add(luoTestComponent(of.createCE(), hoitolaji, KantaCDAConstants.Laakityslista.HOITOLAJI));
        components.add(luoTestComponent(of.createBL(), "true", KantaCDAConstants.Laakityslista.PYSYVA_LAAKITYS));
        components.add(luoTestComponent(of.createBL(), "true", KantaCDAConstants.Laakityslista.UUSIMISKIELTO));
        components.add(luoTestComponent(of.createBL(), "true", KantaCDAConstants.Laakityslista.LAAKEVAIHTOKIELTO));
        components.add(
                luoTestComponent(of.createST(), viestiApteekille, KantaCDAConstants.Laakityslista.VIESTI_APTEEKILLE));
        components.add(luoTestComponent(of.createCE(), pkvLaake,
                KantaCDAConstants.Laakityslista.HUUMAUSAINE_PKV_LAAKEMAARAYS));
        components.add(luoTestComponent(of.createBL(), "true",
                KantaCDAConstants.Laakityslista.KYSEESSA_LAAKKEEN_KAYTON_ALOITUS));
        components.add(luoTestComponent(of.createBL(), "true", KantaCDAConstants.Laakityslista.HUUME));
        components.add(luoTestComponent(of.createCE(), reseptinlaji, KantaCDAConstants.Laakityslista.RESEPTIN_LAJI));
        components.add(luoTestComponent(of.createCE(), apteekissaTallennettuLaakemaaraysValue,
                KantaCDAConstants.Laakityslista.APTEEKISSA_TALLENNETTU_LAAKEMAARAYS));

        // 194 uusimiskiellonperustelu
        POCDMT000040Component4 compUP = luoTestComponent(of.createCE(), "DILEDINDDILE",
                KantaCDAConstants.Laakityslista.UUDISTAMISKIELLON_SYY);
        ED originalText = of.createED();
        originalText.getContent().add(uusimiskiellonperustelu);
        ((CE) compUP.getObservation().getValues().get(0)).setOriginalText(originalText);
        components.add(compUP);

        // 89 alle 12-vuotiaan paino
        POCDMT000040Component4 compA12VP = luoTestComponent(of.createPQ(), String.valueOf(alle12vuotiaanPainoValue),
                KantaCDAConstants.Laakityslista.ALLE_12VUOTIAAN_PAINO);
        ((PQ) compA12VP.getObservation().getValues().get(0)).setUnit(alle12vuotiaanPainoUnit);
        components.add(compA12VP);

        // 91 annosjakelu
        POCDMT000040Component4 compAJ = luoTestComponent(of.createBL(), "true",
                KantaCDAConstants.Laakityslista.ANNOSJAKELU);
        compAJ.getObservation().setText(of.createED());
        compAJ.getObservation().getText().getContent().add(annosjakeluText);
        components.add(compAJ);

        // 69 erillisselvitys
        POCDMT000040Component4 compES = luoTestComponent(of.createCE(), erillisselvitysCode,
                KantaCDAConstants.Laakityslista.ERILLISSELVITYS);
        compES.getObservation().setText(of.createST());
        compES.getObservation().getText().getContent().add(erillisselvitysText);
        components.add(compES);

        // 117 Potilaan tunnistaminen
        POCDMT000040Component4 compPT = luoTestComponent(of.createCE(), tunnistaminen,
                KantaCDAConstants.Laakityslista.POTILAAN_TUNNISTAMINEN);
        compPT.getObservation().setText(of.createED());
        compPT.getObservation().getText().getContent().add(tunnistamisTeksti);
        components.add(compPT);

        // 97 Korjauksen perustelu, purkajan ei pitäisi reagoida tähän, koska käytössä on LaakemaaraysTO instanssi
        components.add(luoKorjauksenPerusteuluComponent());

        // 95 Mitatoinnin syy, purkajan ei pitäisi reagoida tähän, koska käytössä on LaakemaaraysTO instanssi
        components.add(luoMitatoinninSyyComponent());

        // 96 Mitätöinnin tyyppi, purkajan ei pitäisi reagoida tähän, koska käytössä on LaakemaaraysTO instanssi
        components.add(luoMitatoinninTyyppiComponent());

        Whitebox.invokeMethod(tested, "puraLaakemaarayksenMuutTiedot", components, laakemaaraysTO);
        Assert.assertEquals(kayttotarkoitus, laakemaaraysTO.getKayttotarkoitusTeksti());
        Assert.assertEquals(hoitolaji, laakemaaraysTO.getHoitolajit().get(0));
        Assert.assertTrue(laakemaaraysTO.isPysyvaislaakitys());
        Assert.assertTrue(laakemaaraysTO.isUudistamiskielto());
        Assert.assertEquals(uusimiskiellonperustelu, laakemaaraysTO.getUusimiskiellonPerustelu());
        Assert.assertTrue(laakemaaraysTO.isLaakevaihtokielto());
        Assert.assertEquals("alle 12-vuotiaan paino unit pitää olla: " + alle12vuotiaanPainoUnit,
                alle12vuotiaanPainoUnit, laakemaaraysTO.getAlle12VuotiaanPainoUnit());
        Assert.assertEquals("Alle 12-vuotiaan paino valuen pitää olla: " + alle12vuotiaanPainoValue,
                alle12vuotiaanPainoValue, laakemaaraysTO.getAlle12VuotiaanPainoValue().intValue());
        Assert.assertTrue(laakemaaraysTO.isAnnosjakelu());
        Assert.assertEquals("Annosjakelu teksti pitää olla:" + annosjakeluText, annosjakeluText,
                laakemaaraysTO.getAnnosjakeluTeksti());
        Assert.assertEquals("Viesti apteekille pitää olla: " + viestiApteekille, viestiApteekille,
                laakemaaraysTO.getViestiApteekille());
        Assert.assertEquals("Erillisselvitys pitää olla: " + erillisselvitysCode, erillisselvitysCode,
                laakemaaraysTO.getErillisselvitys());
        Assert.assertEquals("Erillisselvitys teksti pitää olla: " + erillisselvitysText, erillisselvitysText,
                laakemaaraysTO.getErillisselvitysteksti());
        Assert.assertEquals(tunnistaminen, laakemaaraysTO.getPotilaanTunnistaminen());
        Assert.assertEquals(tunnistamisTeksti, laakemaaraysTO.getPotilaanTunnistaminenTeksti());
        Assert.assertEquals(pkvLaake, laakemaaraysTO.getPKVlaakemaarays());
        Assert.assertTrue(laakemaaraysTO.isKyseessaLaakkeenkaytonAloitus());
        Assert.assertTrue(laakemaaraysTO.isHuume());
        Assert.assertEquals(reseptinlaji, laakemaaraysTO.getReseptinLaji());
        Assert.assertEquals(testMitatointiSyy, laakemaaraysTO.getMitatoinninSyyKoodi());
        Assert.assertEquals(testMitatointiPerustelu, laakemaaraysTO.getMitatoinninPerustelu());
        Assert.assertEquals(testMitatointiTyyppi, laakemaaraysTO.getMitatoinninTyyppiKoodi());
        Assert.assertEquals(testMitatointiSuostumus, laakemaaraysTO.getMitatoinninSuostumusKoodi());
        Assert.assertEquals(apteekissaTallennettuLaakemaaraysValue,
                laakemaaraysTO.getApteekissaTallennettuLaakemaarays());
    }

    @Test
    public void testPuraLaakemaarayksenKorjauksenPerustelu() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        LaakemaarayksenKorjausTO korjaus = new LaakemaarayksenKorjausTO();
        String testValue = "TestST";
        ST stValue = of.createST();
        stValue.getContent().add(testValue);
        POCDMT000040Observation observation = luoTestObservation(of.createCE(), testCode, "TILIPITÄPPI");
        observation.getValues().add(stValue);
        Whitebox.invokeMethod(tested, "puraLaakemaarayksenKorjauksenPerustelu", observation, korjaus);

        Assert.assertEquals("Korjauksen syykoodin tulee olla: " + testCode, testCode, korjaus.getKorjauksenSyyKoodi());
        Assert.assertEquals("Korjauksen perustelun tulee olla: " + testValue, testValue,
                korjaus.getKorjauksenPerustelu());
    }

    @Test
    public void testPuraLaakemaarayksenKorjauksenPerustelu_EiSyyKoodia() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        LaakemaarayksenKorjausTO korjaus = new LaakemaarayksenKorjausTO();
        String testValue = "TestST";
        POCDMT000040Observation observation = luoTestObservation(of.createST(), testValue, "TILIPITÄPPI");
        Whitebox.invokeMethod(tested, "puraLaakemaarayksenKorjauksenPerustelu", observation, korjaus);

        Assert.assertEquals("Korjauksen perustelun tulee olla: " + testValue, testValue,
                korjaus.getKorjauksenPerustelu());
    }

    @Test
    public void testPuraLaakemaarayksenKorjauksenPerustelu_EiTuettujaValueja() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        LaakemaarayksenKorjausTO korjaus = new LaakemaarayksenKorjausTO();
        POCDMT000040Observation observation = luoTestObservation(of.createBL(), "true", "TILIPITÄPPI");
        Whitebox.invokeMethod(tested, "puraLaakemaarayksenKorjauksenPerustelu", observation, korjaus);

        Assert.assertNull("Korjauksen perustelun tulee olla null", korjaus.getKorjauksenPerustelu());
        Assert.assertNull("Korjauksen syykoodin tulee olla null", korjaus.getKorjauksenSyyKoodi());
    }

    @Test
    public void testVarmistaValmisteenlajiApteekissaValmistettava() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        LaakemaaraysTO lm = new LaakemaaraysTO();
        lm.setApteekissaValmistettavaLaake(true);
        Whitebox.invokeMethod(tested, "varmistaValmisteenlaji", lm);
        Assert.assertEquals("7", lm.getValmiste().getYksilointitiedot().getValmisteenLaji());
    }

    @Test
    public void testVarmistaValmisteenlajiLtkUlkopuolinen() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        LaakemaaraysTO lm = new LaakemaaraysTO();
        lm.setApteekissaValmistettavaLaake(false);
        lm.setLaaketietokannanUlkopuolinenValmiste(testLaaketietokannanulkopuolinenValmiste);
        Whitebox.invokeMethod(tested, "varmistaValmisteenlaji", lm);
        Assert.assertEquals("6", lm.getValmiste().getYksilointitiedot().getValmisteenLaji());
    }

    @Test
    public void testVarmistaValmisteenlajiVaikuttavallaAineellaMaaratty() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        LaakemaaraysTO lm = new LaakemaaraysTO();
        lm.setValmiste(new ValmisteTO());
        lm.getValmiste().setYksilointitiedot(new ValmisteenYksilointitiedotTO());
        lm.getValmiste().getYksilointitiedot().setATCkoodi(testAtcCode);
        Whitebox.invokeMethod(tested, "varmistaValmisteenlaji", lm);
        Assert.assertEquals("9", lm.getValmiste().getYksilointitiedot().getValmisteenLaji());
    }

    @Test
    public void testVarmistaValmisteenlajiLajiAsiakirjalla() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        LaakemaaraysTO lm = new LaakemaaraysTO();
        lm.setValmiste(new ValmisteTO());
        lm.getValmiste().setYksilointitiedot(new ValmisteenYksilointitiedotTO());
        lm.getValmiste().getYksilointitiedot().setATCkoodi(testAtcCode);
        lm.getValmiste().getYksilointitiedot().setValmisteenLaji("1");
        Whitebox.invokeMethod(tested, "varmistaValmisteenlaji", lm);
        Assert.assertEquals("1", lm.getValmiste().getYksilointitiedot().getValmisteenLaji());
    }

    @Test
    public void testVarmistaValmisteenTunnuksenTyyppi_VNRNumero() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        LaakemaaraysTO lm = new LaakemaaraysTO();
        lm.setValmiste(new ValmisteTO());
        lm.getValmiste().setYksilointitiedot(new ValmisteenYksilointitiedotTO());
        lm.getValmiste().getYksilointitiedot().setYksilointitunnus("1234");
        lm.getValmiste().getYksilointitiedot().setValmisteenLaji("1");
        Whitebox.invokeMethod(tested, "varmistaValmisteenTunnuksenTyyppi", lm);
        Assert.assertEquals("Valmisteen tunnuksen tyypin tulee olla (VNR): " + KantaCDAConstants.VNR_tunnus,
                KantaCDAConstants.VNR_tunnus, lm.getValmiste().getYksilointitiedot().getTunnuksenTyyppi());
    }

    @Test
    public void testVarmistaValmisteenTunnuksenTyyppi_EiVNRNumero() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        LaakemaaraysTO lm = new LaakemaaraysTO();
        lm.setValmiste(new ValmisteTO());
        lm.getValmiste().setYksilointitiedot(new ValmisteenYksilointitiedotTO());
        lm.getValmiste().getYksilointitiedot().setYksilointitunnus("1234");
        lm.getValmiste().getYksilointitiedot().setValmisteenLaji("2");
        Whitebox.invokeMethod(tested, "varmistaValmisteenTunnuksenTyyppi", lm);
        Assert.assertNotEquals("Valmisteen tunnuksen tyyppi ei saa olla (VNR): " + KantaCDAConstants.VNR_tunnus,
                KantaCDAConstants.VNR_tunnus, lm.getValmiste().getYksilointitiedot().getTunnuksenTyyppi());
    }

    @Test
    public void testVarmistaValmisteenTunnuksenTyyppi_LTKUlkopValmiste() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        LaakemaaraysTO lm = new LaakemaaraysTO();
        lm.setValmiste(new ValmisteTO());
        lm.getValmiste().setYksilointitiedot(new ValmisteenYksilointitiedotTO());
        lm.getValmiste().getYksilointitiedot().setValmisteenLaji("6");
        Whitebox.invokeMethod(tested, "varmistaValmisteenTunnuksenTyyppi", lm);
        Assert.assertNotEquals("Valmisteen tunnuksen tyyppi ei saa olla (VNR): " + KantaCDAConstants.VNR_tunnus,
                KantaCDAConstants.VNR_tunnus, lm.getValmiste().getYksilointitiedot().getTunnuksenTyyppi());
    }

    @Test
    public void testVarmistaValmisteenTunnuksenTyyppi_ValmisteenLajiPuuttuu() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        LaakemaaraysTO lm = new LaakemaaraysTO();
        lm.setValmiste(new ValmisteTO());
        lm.getValmiste().setYksilointitiedot(new ValmisteenYksilointitiedotTO());
        lm.getValmiste().getYksilointitiedot().setYksilointitunnus("1234");
        Whitebox.invokeMethod(tested, "varmistaValmisteenTunnuksenTyyppi", lm);
        Assert.assertNotEquals("Valmisteen tunnuksen tyyppi ei saa olla (VNR): " + KantaCDAConstants.VNR_tunnus,
                KantaCDAConstants.VNR_tunnus, lm.getValmiste().getYksilointitiedot().getTunnuksenTyyppi());
    }

    @Test
    public void testPuraKorjausAuthor() throws Exception {
        LaakemaarayksenKorjausTO laakemaaraysTO = new LaakemaarayksenKorjausTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument clinicalDocument = of.createPOCDMT000040ClinicalDocument();

        clinicalDocument.getAuthors().add(luoTestAuthor(testRoleKor));

        Whitebox.invokeMethod(tested, "puraKorjausAuthor", clinicalDocument, laakemaaraysTO);
        Assert.assertNotNull(laakemaaraysTO.getKorjaaja());
        Assert.assertNotNull(laakemaaraysTO.getKorjaaja().getOrganisaatio());
        Assert.assertNotNull(laakemaaraysTO.getKorjaaja().getOrganisaatio().getOsoite());
        Assert.assertNotNull(laakemaaraysTO.getKorjaaja().getKokonimi());

        Assert.assertNotNull(laakemaaraysTO.getKorjaaja().getKirjautumisaika());

        Assert.assertEquals(testAuthorAmmattioikeus, laakemaaraysTO.getKorjaaja().getAmmattioikeus());
        Assert.assertEquals(testAuthorAmmattioikeusName, laakemaaraysTO.getKorjaaja().getAmmattioikeusName());
        Assert.assertEquals(testAuthorErikosala, laakemaaraysTO.getKorjaaja().getErikoisala());
        Assert.assertEquals(testAuthorErikoisalaName, laakemaaraysTO.getKorjaaja().getErikoisalaName());
        Assert.assertEquals(testAuthorOppiarvo, laakemaaraysTO.getKorjaaja().getOppiarvo());
        // Assert.assertEquals(testAuthorO, laakemaaraysTO.getAmmattihenkilo().getOppiarvoTekstina());
        Assert.assertEquals(testAuthorRekExtension, laakemaaraysTO.getKorjaaja().getRekisterointinumero());
        Assert.assertEquals(testRoleKor, laakemaaraysTO.getKorjaaja().getRooli());
        Assert.assertEquals(testAuthorSVExtension, laakemaaraysTO.getKorjaaja().getSvNumero());
        Assert.assertEquals(testAuthorVirkanimike, laakemaaraysTO.getKorjaaja().getVirkanimike());

        Assert.assertEquals(testOrgName, laakemaaraysTO.getKorjaaja().getOrganisaatio().getNimi());
        Assert.assertEquals(testOrgTelecom, laakemaaraysTO.getKorjaaja().getOrganisaatio().getPuhelinnumero());
        Assert.assertEquals(testOrgId, laakemaaraysTO.getKorjaaja().getOrganisaatio().getYksilointitunnus());

        Assert.assertEquals(testOrgStreet, laakemaaraysTO.getKorjaaja().getOrganisaatio().getOsoite().getKatuosoite());
        Assert.assertEquals(testOrgPostCode,
                laakemaaraysTO.getKorjaaja().getOrganisaatio().getOsoite().getPostinumero());
        Assert.assertEquals(testOrgCity,
                laakemaaraysTO.getKorjaaja().getOrganisaatio().getOsoite().getPostitoimipaikka());

        Assert.assertEquals(testAuthorEtunimi + " " + testAuthorEtunimi,
                laakemaaraysTO.getKorjaaja().getKokonimi().getEtunimi());
        Assert.assertEquals(testAuthorSukunimi, laakemaaraysTO.getKorjaaja().getKokonimi().getSukunimi());
    }

    @Test
    public void testPuraMitatointiAuthor() throws Exception {
        LaakemaarayksenMitatointiTO mitatointiTO = new LaakemaarayksenMitatointiTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument clinicalDocument = of.createPOCDMT000040ClinicalDocument();

        clinicalDocument.getAuthors().add(luoTestAuthor(testRoleMit));

        Whitebox.invokeMethod(tested, "puraMitatointiAuthor", clinicalDocument, mitatointiTO);
        Assert.assertNotNull(mitatointiTO.getMitatoija());
        Assert.assertNotNull(mitatointiTO.getMitatoija().getOrganisaatio());
        Assert.assertNotNull(mitatointiTO.getMitatoija().getOrganisaatio().getOsoite());
        Assert.assertNotNull(mitatointiTO.getMitatoija().getKokonimi());

        Assert.assertNotNull(mitatointiTO.getMitatoija().getKirjautumisaika());

        Assert.assertEquals(testAuthorAmmattioikeus, mitatointiTO.getMitatoija().getAmmattioikeus());
        Assert.assertEquals(testAuthorAmmattioikeusName, mitatointiTO.getMitatoija().getAmmattioikeusName());
        Assert.assertEquals(testAuthorErikosala, mitatointiTO.getMitatoija().getErikoisala());
        Assert.assertEquals(testAuthorErikoisalaName, mitatointiTO.getMitatoija().getErikoisalaName());
        Assert.assertEquals(testAuthorOppiarvo, mitatointiTO.getMitatoija().getOppiarvo());
        Assert.assertEquals(testAuthorRekExtension, mitatointiTO.getMitatoija().getRekisterointinumero());
        Assert.assertEquals(testRoleMit, mitatointiTO.getMitatoija().getRooli());
        Assert.assertEquals(testAuthorSVExtension, mitatointiTO.getMitatoija().getSvNumero());
        Assert.assertEquals(testAuthorVirkanimike, mitatointiTO.getMitatoija().getVirkanimike());

        Assert.assertEquals(testOrgName, mitatointiTO.getMitatoija().getOrganisaatio().getNimi());
        Assert.assertEquals(testOrgTelecom, mitatointiTO.getMitatoija().getOrganisaatio().getPuhelinnumero());
        Assert.assertEquals(testOrgId, mitatointiTO.getMitatoija().getOrganisaatio().getYksilointitunnus());

        Assert.assertEquals(testOrgStreet, mitatointiTO.getMitatoija().getOrganisaatio().getOsoite().getKatuosoite());
        Assert.assertEquals(testOrgPostCode,
                mitatointiTO.getMitatoija().getOrganisaatio().getOsoite().getPostinumero());
        Assert.assertEquals(testOrgCity,
                mitatointiTO.getMitatoija().getOrganisaatio().getOsoite().getPostitoimipaikka());

        Assert.assertEquals(testAuthorEtunimi + " " + testAuthorEtunimi,
                mitatointiTO.getMitatoija().getKokonimi().getEtunimi());
        Assert.assertEquals(testAuthorSukunimi, mitatointiTO.getMitatoija().getKokonimi().getSukunimi());
    }

    @Test
    public void testHaeLaatijanJaKorjaajanNayttomuoto() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        cda.setComponent(of.createPOCDMT000040Component2());
        cda.getComponent().setStructuredBody(of.createPOCDMT000040StructuredBody());
        cda.getComponent().getStructuredBody().getComponents().add(of.createPOCDMT000040Component3());
        cda.getComponent().getStructuredBody().getComponents().get(0).setSection(of.createPOCDMT000040Section());
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents()
                .add(of.createPOCDMT000040Component5());
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents().get(0)
                .setSection(of.createPOCDMT000040Section());
        POCDMT000040Section section = cda.getComponent().getStructuredBody().getComponents().get(0).getSection()
                .getComponents().get(0).getSection();
        StrucDocText text = of.createStrucDocText();
        text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent(testOrgName)));
        text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent(testEffectiveTime)));
        text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent(testAuthorSukunimi)));
        text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent("Lääkemääräyksen korjaaja:")));
        text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent("Kelain")));
        text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent("korjaus" + testEffectiveTime)));
        text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent("korjaus" + testAuthorSukunimi)));
        section.setText(text);
        Object retval = Whitebox.invokeMethod(tested, "haeLaatijanJaKorjaajanNayttomuoto", cda);
        Assert.assertTrue(retval instanceof List);
        List<String> nayttomuoto = (List<String>) retval;
        Assert.assertTrue(nayttomuoto.size() == 7);
        Assert.assertEquals(testOrgName, nayttomuoto.get(0));
        Assert.assertEquals(testEffectiveTime, nayttomuoto.get(1));
        Assert.assertEquals(testAuthorSukunimi, nayttomuoto.get(2));
        Assert.assertEquals("Lääkemääräyksen korjaaja:", nayttomuoto.get(3));
        Assert.assertEquals("Kelain", nayttomuoto.get(4));
        Assert.assertEquals("korjaus" + testEffectiveTime, nayttomuoto.get(5));
        Assert.assertEquals("korjaus" + testAuthorSukunimi, nayttomuoto.get(6));
    }

    @Test
    public void testHaeLaatijanJaKorjaajanNayttomuotoVainLaatija() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        cda.setComponent(of.createPOCDMT000040Component2());
        cda.getComponent().setStructuredBody(of.createPOCDMT000040StructuredBody());
        cda.getComponent().getStructuredBody().getComponents().add(of.createPOCDMT000040Component3());
        cda.getComponent().getStructuredBody().getComponents().get(0).setSection(of.createPOCDMT000040Section());
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents()
                .add(of.createPOCDMT000040Component5());
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents().get(0)
                .setSection(of.createPOCDMT000040Section());
        POCDMT000040Section section = cda.getComponent().getStructuredBody().getComponents().get(0).getSection()
                .getComponents().get(0).getSection();
        StrucDocText text = of.createStrucDocText();
        text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent(testOrgName)));
        text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent(testEffectiveTime)));
        text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent(testAuthorSukunimi)));
        section.setText(text);
        Object retval = Whitebox.invokeMethod(tested, "haeLaatijanJaKorjaajanNayttomuoto", cda);
        Assert.assertTrue(retval instanceof List);
        List<String> nayttomuoto = (List<String>) retval;
        Assert.assertTrue(nayttomuoto.size() == 3);
        Assert.assertEquals(testOrgName, nayttomuoto.get(0));
        Assert.assertEquals(testEffectiveTime, nayttomuoto.get(1));
        Assert.assertEquals(testAuthorSukunimi, nayttomuoto.get(2));
    }

    @Test
    public void testHaeLaatijanJaKorjaajanNayttomuotoEiNayttomuotoa() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        cda.setComponent(of.createPOCDMT000040Component2());
        cda.getComponent().setStructuredBody(of.createPOCDMT000040StructuredBody());
        cda.getComponent().getStructuredBody().getComponents().add(of.createPOCDMT000040Component3());
        cda.getComponent().getStructuredBody().getComponents().get(0).setSection(of.createPOCDMT000040Section());
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents()
                .add(of.createPOCDMT000040Component5());
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents().get(0)
                .setSection(of.createPOCDMT000040Section());
        Object retval = Whitebox.invokeMethod(tested, "haeLaatijanJaKorjaajanNayttomuoto", cda);
        Assert.assertTrue(retval instanceof List);
        List<String> nayttomuoto = (List<String>) retval;
        Assert.assertTrue(nayttomuoto.size() == 0);
    }

    @Test
    public void testHaeLaatijanJaKorjaajanNayttomuotoTextTyhja() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        cda.setComponent(of.createPOCDMT000040Component2());
        cda.getComponent().setStructuredBody(of.createPOCDMT000040StructuredBody());
        cda.getComponent().getStructuredBody().getComponents().add(of.createPOCDMT000040Component3());
        cda.getComponent().getStructuredBody().getComponents().get(0).setSection(of.createPOCDMT000040Section());
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents()
                .add(of.createPOCDMT000040Component5());
        cda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents().get(0)
                .setSection(of.createPOCDMT000040Section());
        POCDMT000040Section section = cda.getComponent().getStructuredBody().getComponents().get(0).getSection()
                .getComponents().get(0).getSection();
        StrucDocText text = of.createStrucDocText();
        section.setText(text);
        Object retval = Whitebox.invokeMethod(tested, "haeLaatijanJaKorjaajanNayttomuoto", cda);
        Assert.assertTrue(retval instanceof List);
        List<String> nayttomuoto = (List<String>) retval;
        Assert.assertTrue(nayttomuoto.size() == 0);
    }

    @Test
    public void testPuraAuthorJaKirjaaja() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040ClinicalDocument clinicalDocument = of.createPOCDMT000040ClinicalDocument();

        clinicalDocument.getAuthors().add(luoTestAuthor(testRole));
        clinicalDocument.getAuthors().add(luoTestAuthor(testRoleKir));

        Whitebox.invokeMethod(tested, "puraAuthor", clinicalDocument, laakemaaraysTO);
        Assert.assertNotNull(laakemaaraysTO.getAmmattihenkilo());
        Assert.assertNotNull(laakemaaraysTO.getKirjaaja());
        Assert.assertNotNull(laakemaaraysTO.getAmmattihenkilo().getOrganisaatio());
        Assert.assertNotNull(laakemaaraysTO.getAmmattihenkilo().getOrganisaatio().getOsoite());
        Assert.assertNotNull(laakemaaraysTO.getAmmattihenkilo().getKokonimi());
        Assert.assertNotNull(laakemaaraysTO.getKirjaaja().getOrganisaatio());
        Assert.assertNotNull(laakemaaraysTO.getKirjaaja().getOrganisaatio().getOsoite());
        Assert.assertNotNull(laakemaaraysTO.getKirjaaja().getKokonimi());

        Assert.assertNotNull(laakemaaraysTO.getAmmattihenkilo().getKirjautumisaika());

        Assert.assertEquals(testAuthorAmmattioikeus, laakemaaraysTO.getAmmattihenkilo().getAmmattioikeus());
        Assert.assertEquals(testAuthorAmmattioikeusName, laakemaaraysTO.getAmmattihenkilo().getAmmattioikeusName());
        Assert.assertEquals(testAuthorErikosala, laakemaaraysTO.getAmmattihenkilo().getErikoisala());
        Assert.assertEquals(testAuthorErikoisalaName, laakemaaraysTO.getAmmattihenkilo().getErikoisalaName());
        Assert.assertEquals(testAuthorOppiarvo, laakemaaraysTO.getAmmattihenkilo().getOppiarvo());
        // Assert.assertEquals(testAuthorO, laakemaaraysTO.getAmmattihenkilo().getOppiarvoTekstina());
        Assert.assertEquals(testAuthorRekExtension, laakemaaraysTO.getAmmattihenkilo().getRekisterointinumero());
        Assert.assertEquals(testRole, laakemaaraysTO.getAmmattihenkilo().getRooli());
        Assert.assertEquals(testAuthorSVExtension, laakemaaraysTO.getAmmattihenkilo().getSvNumero());
        Assert.assertEquals(testAuthorVirkanimike, laakemaaraysTO.getAmmattihenkilo().getVirkanimike());

        Assert.assertEquals(testOrgName, laakemaaraysTO.getAmmattihenkilo().getOrganisaatio().getNimi());
        Assert.assertEquals(testOrgTelecom, laakemaaraysTO.getAmmattihenkilo().getOrganisaatio().getPuhelinnumero());
        Assert.assertEquals(testOrgId, laakemaaraysTO.getAmmattihenkilo().getOrganisaatio().getYksilointitunnus());

        Assert.assertEquals(testOrgStreet,
                laakemaaraysTO.getAmmattihenkilo().getOrganisaatio().getOsoite().getKatuosoite());
        Assert.assertEquals(testOrgPostCode,
                laakemaaraysTO.getAmmattihenkilo().getOrganisaatio().getOsoite().getPostinumero());
        Assert.assertEquals(testOrgCity,
                laakemaaraysTO.getAmmattihenkilo().getOrganisaatio().getOsoite().getPostitoimipaikka());

        Assert.assertEquals(testAuthorEtunimi + " " + testAuthorEtunimi,
                laakemaaraysTO.getAmmattihenkilo().getKokonimi().getEtunimi());
        Assert.assertEquals(testAuthorSukunimi, laakemaaraysTO.getAmmattihenkilo().getKokonimi().getSukunimi());

        Assert.assertEquals(testAuthorAmmattioikeus, laakemaaraysTO.getKirjaaja().getAmmattioikeus());
        Assert.assertEquals(testAuthorAmmattioikeusName, laakemaaraysTO.getKirjaaja().getAmmattioikeusName());
        Assert.assertEquals(testAuthorErikosala, laakemaaraysTO.getKirjaaja().getErikoisala());
        Assert.assertEquals(testAuthorErikoisalaName, laakemaaraysTO.getKirjaaja().getErikoisalaName());
        Assert.assertEquals(testAuthorOppiarvo, laakemaaraysTO.getKirjaaja().getOppiarvo());
        // Assert.assertEquals(testAuthorO, laakemaaraysTO.getAmmattihenkilo().getOppiarvoTekstina());
        Assert.assertEquals(testAuthorRekExtension, laakemaaraysTO.getKirjaaja().getRekisterointinumero());
        Assert.assertEquals(testRoleKir, laakemaaraysTO.getKirjaaja().getRooli());
        Assert.assertEquals(testAuthorSVExtension, laakemaaraysTO.getKirjaaja().getSvNumero());
        Assert.assertEquals(testAuthorVirkanimike, laakemaaraysTO.getKirjaaja().getVirkanimike());

        Assert.assertEquals(testOrgName, laakemaaraysTO.getKirjaaja().getOrganisaatio().getNimi());
        Assert.assertEquals(testOrgTelecom, laakemaaraysTO.getKirjaaja().getOrganisaatio().getPuhelinnumero());
        Assert.assertEquals(testOrgId, laakemaaraysTO.getKirjaaja().getOrganisaatio().getYksilointitunnus());

        Assert.assertEquals(testOrgStreet, laakemaaraysTO.getKirjaaja().getOrganisaatio().getOsoite().getKatuosoite());
        Assert.assertEquals(testOrgPostCode,
                laakemaaraysTO.getKirjaaja().getOrganisaatio().getOsoite().getPostinumero());
        Assert.assertEquals(testOrgCity,
                laakemaaraysTO.getKirjaaja().getOrganisaatio().getOsoite().getPostitoimipaikka());

        Assert.assertEquals(testAuthorEtunimi + " " + testAuthorEtunimi,
                laakemaaraysTO.getKirjaaja().getKokonimi().getEtunimi());
        Assert.assertEquals(testAuthorSukunimi, laakemaaraysTO.getKirjaaja().getKokonimi().getSukunimi());

    }

    @Test
    public void testPuraReseptinTyyppijaMaaraTiedotKokonaismaaraDesimaalina() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        Whitebox.invokeMethod(tested, "puraReseptinTyyppijaMaaraTiedot", Matchers.isNull(POCDMT000040Supply.class),
                laakemaaraysTO);
 
        POCDMT000040Supply supply = of.createPOCDMT000040Supply();

        // Reseptintyyppi 2
        supply.setCode(of.createCD());
        supply.getCode().setCode("2");
        supply.setQuantity(of.createPQ());
        supply.getQuantity().setUnit(testLaakkeenkokonaismaaraUnit);
        supply.getQuantity().setValue(testLaakeenkokonaismaaraValueDesimaalina);
        Whitebox.invokeMethod(tested, "puraReseptinTyyppijaMaaraTiedot", supply, laakemaaraysTO);
        Assert.assertEquals(testLaakkeenkokonaismaaraUnit, laakemaaraysTO.getLaakkeenKokonaismaaraUnit());
        Assert.assertEquals(testLaakeenkokonaismaaraValue, laakemaaraysTO.getLaakkeenKokonaismaaraValue().intValue());
    }

	/*
	 * protected void puraAnnostelukausiComponent(POCDMT000040Component4 component,
	 * LaakemaaraysTO laakemaarays) throws PurkuException {
	 * POCDMT000040SubstanceAdministration sa =
	 * component.getSubstanceAdministration(); if (!sa.getIds().isEmpty()) {
	 * laakemaarays.setAnnostelukausiId(sa.getIds().get(0).getRoot()); }
	 * 
	 * for (POCDMT000040EntryRelationship er : sa.getEntryRelationships()) { if
	 * (onkoObservationCodeCode(er.getObservation(), ANNOSTUS_TARVITTAESSA_CODE)) {
	 * laakemaarays.setAnnostusTarvittaessa(puraObservationBooleanValue(er.
	 * getObservation())); } else if (onkoObservationCodeCode(er.getObservation(),
	 * ANNOSJAKSON_PITUUS_CODE)) {
	 * laakemaarays.setAnnosajaksonPituus(puraAnnosjaksonPituus(er.getObservation().
	 * getValues().get(0))); } else if (onkoAnnosEntry(er)) {
	 * laakemaarays.getAnnokset().add(puraAnnos(er)); } } }
	 */

	@Test
	public void testOnkoAnnostelukausiComponent_Empty() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();
		assertFalse(tested.onkoAnnostelukausiComponent(of.createPOCDMT000040Component4()));
	}

	@Test
	public void testOnkoAnnostelukausiComponent_NullSACode() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();

		POCDMT000040Component4 component = of.createPOCDMT000040Component4();
		POCDMT000040SubstanceAdministration sa = of.createPOCDMT000040SubstanceAdministration();
		component.setSubstanceAdministration(sa);
		assertFalse(tested.onkoAnnostelukausiComponent(component));
	}

	@Test
	public void testOnkoAnnostelukausiComponent_WrongSACode() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();

		POCDMT000040Component4 component = of.createPOCDMT000040Component4();
		POCDMT000040SubstanceAdministration sa = of.createPOCDMT000040SubstanceAdministration();
		CD code = of.createCD();
		code.setCode("BLAAA");
		sa.setCode(code);
		component.setSubstanceAdministration(sa);
		assertFalse(tested.onkoAnnostelukausiComponent(component));
	}

	@Test
	public void testOnkoAnnostelukausiComponent() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();

		POCDMT000040Component4 component = of.createPOCDMT000040Component4();
		POCDMT000040SubstanceAdministration sa = of.createPOCDMT000040SubstanceAdministration();
		CD code = of.createCD();
		code.setCode(fi.kela.kanta.cda.KantaCDAConstants.Laakityslista.ANNOSTELUKAUSI_CODE);
		sa.setCode(code);
		component.setSubstanceAdministration(sa);
		assertTrue(tested.onkoAnnostelukausiComponent(component));
	}

	@Test(expected = PurkuException.class)
	public void testPuraObservationBooleanValue_ValuesEmpty() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();
		POCDMT000040Observation o = of.createPOCDMT000040Observation();

		tested.puraObservationBooleanValue(o);
	}

	@Test(expected = PurkuException.class)
	public void testPuraObservationBooleanValue_Non_BL() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();
		POCDMT000040Observation o = of.createPOCDMT000040Observation();
		o.getValues().add(of.createCE());

		tested.puraObservationBooleanValue(o);
	}

	@Test
	public void testPuraObservationBooleanValue() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();
		POCDMT000040Observation o = of.createPOCDMT000040Observation();
		BL b = of.createBL();
		b.setValue(Boolean.TRUE);
		o.getValues().add(b);

		assertTrue(tested.puraObservationBooleanValue(o));
	}

	@Test
	public void testOnkoAnnosEntry_EmptyEntry() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();
		assertFalse(tested.onkoAnnosEntry(of.createPOCDMT000040EntryRelationship()));
	}

	@Test
	public void testOnkoAnnosEntry_NO_CODE() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();
		POCDMT000040EntryRelationship e = of.createPOCDMT000040EntryRelationship();
		POCDMT000040SubstanceAdministration sa = of.createPOCDMT000040SubstanceAdministration();
		e.setSubstanceAdministration(sa);

		assertFalse(tested.onkoAnnosEntry(e));
	}

	@Test
	public void testOnkoAnnosEntry_WRONG_CODE() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();
		POCDMT000040EntryRelationship e = of.createPOCDMT000040EntryRelationship();
		POCDMT000040SubstanceAdministration sa = of.createPOCDMT000040SubstanceAdministration();
		CD c = of.createCD();
		sa.setCode(c);
		c.setCode("AAA");
		e.setSubstanceAdministration(sa);

		assertFalse(tested.onkoAnnosEntry(e));
	}

	@Test
	public void testOnkoAnnosEntry() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();
		POCDMT000040EntryRelationship e = of.createPOCDMT000040EntryRelationship();
		POCDMT000040SubstanceAdministration sa = of.createPOCDMT000040SubstanceAdministration();
		CD c = of.createCD();
		sa.setCode(c);
		c.setCode(fi.kela.kanta.cda.KantaCDAConstants.Laakityslista.ANNOKSET_CODE);
		e.setSubstanceAdministration(sa);

		assertTrue(tested.onkoAnnosEntry(e));
	}

	/*
	 * protected AnnosTO puraAnnos(POCDMT000040EntryRelationship er) throws
	 * PurkuException { AnnosTO annos = new AnnosTO();
	 * 
	 * POCDMT000040SubstanceAdministration annosSa =
	 * er.getSubstanceAdministration();
	 * 
	 * if (annosSa.getIds().size() > 0) {
	 * annos.setAnnosId(annosSa.getIds().get(0).getRoot()); }
	 * 
	 * if (!annosSa.getEffectiveTimes().isEmpty()) {
	 * annos.setAnnosaika(puraAika(annosSa.getEffectiveTimes().get(0).getValue()));
	 * }
	 * 
	 * puraAnnosMaara(annosSa.getDoseQuantity(), annos);
	 * annos.setYksikko(annosSa.getAdministrationUnitCode().getCode());
	 * 
	 * for (POCDMT000040EntryRelationship saEr : annosSa.getEntryRelationships()) {
	 * if (onkoObservationCodeCode(saEr.getObservation(), ANNOS_TARVITTAESSA_CODE))
	 * {
	 * annos.setAnnosTarvittaessa(puraObservationBooleanValue(saEr.getObservation())
	 * ); } }
	 * 
	 * return annos; }
	 */

	@Test
	public void testPuraAnnosMaara_VakioArvo() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();

		IVLPQ doseQ = of.createIVLPQ();
		PQ center = of.createPQ();
		center.setUnit("1");
		center.setValue("2.5");
		doseQ.setCenter(center);
		AnnosTO annos = new AnnosTO();

		tested.puraAnnosMaara(doseQ, annos);
		assertEquals(Double.valueOf(2.5), annos.getVakioAnnos());
		assertNull(annos.getHighAnnos());
		assertNull(annos.getLowAnnos());

	}

	@Test
	public void testPuraAnnosMaara_ValiArvo() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();

		IVLPQ doseQ = of.createIVLPQ();
		IVXBPQ high = of.createIVXBPQ();
		high.setUnit("1");
		high.setValue("2.5");
		doseQ.setHigh(high);

		IVXBPQ low = of.createIVXBPQ();
		low.setUnit("1");
		low.setValue("0.5");
		doseQ.setLow(low);

		AnnosTO annos = new AnnosTO();

		tested.puraAnnosMaara(doseQ, annos);
		assertEquals(Double.valueOf(2.5), annos.getHighAnnos());
		assertEquals(Double.valueOf(0.5), annos.getLowAnnos());
		assertNull(annos.getVakioAnnos());
	}

	@Test
	public void testOnkoAnnostuksenLisatiedotComponent_Empty() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();
		assertFalse(tested.onkoAnnostuksenLisatiedotComponent(of.createPOCDMT000040Component4()));
	}

	@Test
	public void testOnkoAnnostuksenLisatiedotComponent_NullSACode() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();

		POCDMT000040Component4 component = of.createPOCDMT000040Component4();
		POCDMT000040SubstanceAdministration sa = of.createPOCDMT000040SubstanceAdministration();
		component.setSubstanceAdministration(sa);
		assertFalse(tested.onkoAnnostuksenLisatiedotComponent(component));
	}

	@Test
	public void testOnkoAnnostuksenLisatiedotComponent_WrongSACode() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();

		POCDMT000040Component4 component = of.createPOCDMT000040Component4();
		POCDMT000040SubstanceAdministration sa = of.createPOCDMT000040SubstanceAdministration();
		CD code = of.createCD();
		code.setCode("BLAAA");
		sa.setCode(code);
		component.setSubstanceAdministration(sa);
		assertFalse(tested.onkoAnnostuksenLisatiedotComponent(component));
	}

	@Test
	public void testOnkoAnnostuksenLisatiedotComponent() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();

		POCDMT000040Component4 component = of.createPOCDMT000040Component4();
		POCDMT000040SubstanceAdministration sa = of.createPOCDMT000040SubstanceAdministration();
		CD code = of.createCD();
		code.setCode(fi.kela.kanta.cda.KantaCDAConstants.Laakityslista.ANNOSTUKSEN_LISATIEDOT_CODE);
		sa.setCode(code);
		component.setSubstanceAdministration(sa);
		assertTrue(tested.onkoAnnostuksenLisatiedotComponent(component));
	}

    @Test
    public void testPuraAnnostuksenLisatiedotComponent() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Component4 comp = of.createPOCDMT000040Component4();
        comp.setSubstanceAdministration(of.createPOCDMT000040SubstanceAdministration());
        CD lisatiedot = of.createCD();
        lisatiedot.setCode(KantaCDAConstants.Laakityslista.ANNOSTUKSEN_LISATIEDOT_CODE);
        comp.getSubstanceAdministration().setCode(lisatiedot);

        CE antoreitti = of.createCE();
        antoreitti.setCode("1055");
        antoreitti.setDisplayName("suun kautta");
        comp.getSubstanceAdministration().setRouteCode(antoreitti);

        comp.getSubstanceAdministration().getEntryRelationships().add(of.createPOCDMT000040EntryRelationship());
        comp.getSubstanceAdministration().getEntryRelationships().get(0).setTypeCode(XActRelationshipEntryRelationship.COMP);
        comp.getSubstanceAdministration().getEntryRelationships().get(0).setObservation(of.createPOCDMT000040Observation());
        POCDMT000040Observation obs = luoTestObservation(of.createST(), "Käyttöohjeen lisätieto", KantaCDAConstants.Laakityslista.KAYTTOOHJEEN_LISATIETO_CODE);
        comp.getSubstanceAdministration().getEntryRelationships().get(0).setObservation(obs);
        Whitebox.invokeMethod(tested, "puraAnnostuksenLisatiedotComponent", comp, laakemaaraysTO);

        Assert.assertTrue(laakemaaraysTO.getLaakkeenantoreitti() != null);
        Assert.assertTrue(StringUtils.isNotEmpty(laakemaaraysTO.getLaakkeenantoreitti().getTunniste()));
        Assert.assertTrue(StringUtils.isNotEmpty(laakemaaraysTO.getKayttoOhjeLisatiedot()));
        Assert.assertEquals("Käyttöohjeen lisätieto", laakemaaraysTO.getKayttoOhjeLisatiedot());
    }

    @Test
    public void testPuraAnnostuksenLisatiedotComponentNULL() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Component4 comp = null;
        Whitebox.invokeMethod(tested, "puraAnnostuksenLisatiedotComponent", comp, laakemaaraysTO);

        Assert.assertTrue(laakemaaraysTO.getLaakkeenantoreitti() == null);
        Assert.assertTrue(StringUtils.isEmpty(laakemaaraysTO.getKayttoOhjeLisatiedot()));
    }

    @Test
    public void testPuraAnnostuksenLisatiedotComponentSaNULL() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Component4 comp = of.createPOCDMT000040Component4();
        Whitebox.invokeMethod(tested, "puraAnnostuksenLisatiedotComponent", comp, laakemaaraysTO);

        Assert.assertTrue(laakemaaraysTO.getLaakkeenantoreitti() == null);
        Assert.assertTrue(StringUtils.isEmpty(laakemaaraysTO.getKayttoOhjeLisatiedot()));
     }

    @Test
    public void testPuraAnnostuksenLisatiedotComponentRoutecodeNULL() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Component4 comp = of.createPOCDMT000040Component4();
        comp.setSubstanceAdministration(of.createPOCDMT000040SubstanceAdministration());
        CD lisatiedot = of.createCD();
        lisatiedot.setCode(KantaCDAConstants.Laakityslista.ANNOSTUKSEN_LISATIEDOT_CODE);
        comp.getSubstanceAdministration().setCode(lisatiedot);

        comp.getSubstanceAdministration().getEntryRelationships().add(of.createPOCDMT000040EntryRelationship());
        comp.getSubstanceAdministration().getEntryRelationships().get(0).setTypeCode(XActRelationshipEntryRelationship.COMP);
        comp.getSubstanceAdministration().getEntryRelationships().get(0).setObservation(of.createPOCDMT000040Observation());
        POCDMT000040Observation obs = luoTestObservation(of.createST(), "Käyttöohjeen lisätieto", KantaCDAConstants.Laakityslista.KAYTTOOHJEEN_LISATIETO_CODE);
        comp.getSubstanceAdministration().getEntryRelationships().get(0).setObservation(obs);
        Whitebox.invokeMethod(tested, "puraAnnostuksenLisatiedotComponent", comp, laakemaaraysTO);

        Assert.assertTrue(laakemaaraysTO.getLaakkeenantoreitti() == null);
        Assert.assertTrue(StringUtils.isNotEmpty(laakemaaraysTO.getKayttoOhjeLisatiedot()));
        Assert.assertEquals("Käyttöohjeen lisätieto", laakemaaraysTO.getKayttoOhjeLisatiedot());
    }

    @Test
    public void testPuraAnnostuksenLisatiedotComponentKayttoohjeLisatiedotNULL() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Component4 comp = of.createPOCDMT000040Component4();
        comp.setSubstanceAdministration(of.createPOCDMT000040SubstanceAdministration());
        CD lisatiedot = of.createCD();
        lisatiedot.setCode(KantaCDAConstants.Laakityslista.ANNOSTUKSEN_LISATIEDOT_CODE);
        comp.getSubstanceAdministration().setCode(lisatiedot);

        CE antoreitti = of.createCE();
        antoreitti.setCode("1055");
        antoreitti.setDisplayName("suun kautta");
        comp.getSubstanceAdministration().setRouteCode(antoreitti);
        Whitebox.invokeMethod(tested, "puraAnnostuksenLisatiedotComponent", comp, laakemaaraysTO);

        Assert.assertTrue(laakemaaraysTO.getLaakkeenantoreitti() != null);
        Assert.assertTrue(StringUtils.isNotEmpty(laakemaaraysTO.getLaakkeenantoreitti().getTunniste()));
        Assert.assertTrue(StringUtils.isEmpty(laakemaaraysTO.getKayttoOhjeLisatiedot()));
        Assert.assertTrue(StringUtils.isEmpty(laakemaaraysTO.getKayttoOhjeLisatiedot()));
    }

    @Test
    public void testPuraAnnostusAnnostuksenLisatiedot() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        ArrayList<POCDMT000040Component4> components = new ArrayList<POCDMT000040Component4>();
        POCDMT000040Component4 comp = of.createPOCDMT000040Component4();
        components.add(comp);
        comp.setSubstanceAdministration(of.createPOCDMT000040SubstanceAdministration());
        CD lisatiedot = of.createCD();
        lisatiedot.setCode(KantaCDAConstants.Laakityslista.ANNOSTUKSEN_LISATIEDOT_CODE);
        comp.getSubstanceAdministration().setCode(lisatiedot);

        CE antoreitti = of.createCE();
        antoreitti.setCode("1055");
        antoreitti.setDisplayName("suun kautta");
        comp.getSubstanceAdministration().setRouteCode(antoreitti);

        comp.getSubstanceAdministration().getEntryRelationships().add(of.createPOCDMT000040EntryRelationship());
        comp.getSubstanceAdministration().getEntryRelationships().get(0).setTypeCode(XActRelationshipEntryRelationship.COMP);
        comp.getSubstanceAdministration().getEntryRelationships().get(0).setObservation(of.createPOCDMT000040Observation());
        POCDMT000040Observation obs = luoTestObservation(of.createST(), "Käyttöohjeen lisätieto", KantaCDAConstants.Laakityslista.KAYTTOOHJEEN_LISATIETO_CODE);
        comp.getSubstanceAdministration().getEntryRelationships().get(0).setObservation(obs);
        Whitebox.invokeMethod(tested, "puraAnnostus", components, laakemaaraysTO);

        Assert.assertTrue(laakemaaraysTO.getLaakkeenantoreitti() != null);
        Assert.assertTrue(StringUtils.isNotEmpty(laakemaaraysTO.getLaakkeenantoreitti().getTunniste()));
        Assert.assertTrue(StringUtils.isNotEmpty(laakemaaraysTO.getKayttoOhjeLisatiedot()));
        Assert.assertEquals("Käyttöohjeen lisätieto", laakemaaraysTO.getKayttoOhjeLisatiedot());
     }

    @Test
    public void testPuraAnnosjaksonPituus_Vali() throws Exception {
    	IVLPQ jakso = of.createIVLPQ();
    	IVXBPQ low = of.createIVXBPQ();
    	IVXBPQ high = of.createIVXBPQ();
    	low.setValue("10");
    	low.setUnit("d");
    	jakso.setLow(low);
    	high.setValue("15");
    	high.setUnit("d");
    	jakso.setHigh(high);
        ReseptiPurkaja tested = new ReseptiPurkaja();
    	AnnosjaksonPituusTO annosjakso = tested.puraJaksonPituus(jakso, new AnnosjaksonPituusTO());
    	assertTrue(annosjakso != null);
    	assertTrue(annosjakso.getLow() != null && annosjakso.getLow() == 10);
    	assertTrue(annosjakso.getHigh() != null && annosjakso.getHigh() == 15);
    	assertTrue(annosjakso.getYksikko() != null && annosjakso.getYksikko().contentEquals("d"));
    	assertTrue(annosjakso.getVakio() == null);
    }
    
    @Test
    public void testPuraAnnosjaksonPituus_Vakio() throws Exception {
    	IVLPQ jakso = of.createIVLPQ();
    	IVXBPQ value = of.createIVXBPQ();
    	value.setValue("10");
    	value.setUnit("d");
    	jakso.setWidth(value);
        ReseptiPurkaja tested = new ReseptiPurkaja();
    	AnnosjaksonPituusTO annosjakso = tested.puraJaksonPituus(jakso, new AnnosjaksonPituusTO());
    	assertTrue(annosjakso != null);
    	assertTrue(annosjakso.getLow() == null);
    	assertTrue(annosjakso.getHigh() == null);
    	assertTrue(annosjakso.getYksikko() != null && annosjakso.getYksikko().contentEquals("d"));
    	assertTrue(annosjakso.getVakio() != null && annosjakso.getVakio() == 10);
    }

    @Test
    public void testPuraAnnostelukaudenPituus() throws Exception {
    	IVLPQ jakso = of.createIVLPQ();
    	IVXBPQ low = of.createIVXBPQ();
    	IVXBPQ high = of.createIVXBPQ();
    	low.setValue("10");
    	low.setUnit("d");
    	jakso.setLow(low);
    	high.setValue("15");
    	high.setUnit("d");
    	jakso.setHigh(high);
        ReseptiPurkaja tested = new ReseptiPurkaja();
        AnnostelukaudenPituusTO annosjakso = tested.puraJaksonPituus(jakso, new AnnostelukaudenPituusTO());
    	assertTrue(annosjakso != null);
    	assertTrue(annosjakso.getLow() != null && annosjakso.getLow() == 10);
    	assertTrue(annosjakso.getHigh() != null && annosjakso.getHigh() == 15);
    	assertTrue(annosjakso.getYksikko() != null && annosjakso.getYksikko().contentEquals("d"));
    	assertTrue(annosjakso.getVakio() == null);
    }
    
    @Test
    public void testPuraAnnostelukaudenPituus_Vakio() throws Exception {
    	IVLPQ jakso = of.createIVLPQ();
    	IVXBPQ value = of.createIVXBPQ();
    	value.setValue("10");
    	value.setUnit("d");
    	jakso.setWidth(value);
        ReseptiPurkaja tested = new ReseptiPurkaja();
        AnnostelukaudenPituusTO annosjakso = tested.puraJaksonPituus(jakso, new AnnostelukaudenPituusTO());
    	assertTrue(annosjakso != null);
    	assertTrue(annosjakso.getLow() == null);
    	assertTrue(annosjakso.getHigh() == null);
    	assertTrue(annosjakso.getYksikko() != null && annosjakso.getYksikko().contentEquals("d"));
    	assertTrue(annosjakso.getVakio() != null && annosjakso.getVakio() == 10);
    }

	@Test
	public void testPuraJaksonPituus_NULL() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();
		AnnostelukaudenPituusTO to = tested.puraJaksonPituus(null, new AnnostelukaudenPituusTO());
		assertTrue(to == null);
	}

    @Test
    public void testPuraJaksonPituus_EiTietoja() throws Exception {
    	IVLPQ jakso = of.createIVLPQ();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        AnnostelukaudenPituusTO annosjakso = tested.puraJaksonPituus(jakso, new AnnostelukaudenPituusTO());
    	assertTrue(annosjakso != null);
    	assertTrue(annosjakso.getLow() == null);
    	assertTrue(annosjakso.getHigh() == null);
    	assertTrue(annosjakso.getYksikko() == null);
    	assertTrue(annosjakso.getVakio() == null);
    }

    @Test
    public void testPuraAnnostelukaudenAika_Vali() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
    	POCDMT000040SubstanceAdministration sa = of.createPOCDMT000040SubstanceAdministration();  
    	IVLTS time = of.createIVLTS();
    	IVXBTS low = of.createIVXBTS();
    	IVXBTS high = of.createIVXBTS();
    	low.setValue("20210101");
    	high.setValue("20210131");
    	time.setLow(low);
    	time.setHigh(high);
    	sa.getEffectiveTimes().add(time);
    	
    	LaakemaaraysTO resepti = tested.puraAnnostelukaudenAika(sa, new LaakemaaraysTO());
    	assertTrue(resepti != null);
    	assertTrue(resepti.getAnnostelukaudenAlkupvm() != null);
    	assertTrue(resepti.getAnnostelukaudenLoppupvm() != null);  	
    }
    
    @Test
    public void testPuraAnnostelukaudenAika_Alkuaika() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
    	POCDMT000040SubstanceAdministration sa = of.createPOCDMT000040SubstanceAdministration();  
    	IVLTS time = of.createIVLTS();
    	IVXBTS low = of.createIVXBTS();
     	low.setValue("20210101");
    	time.setLow(low);
    	sa.getEffectiveTimes().add(time);
    	
    	LaakemaaraysTO resepti = tested.puraAnnostelukaudenAika(sa, new LaakemaaraysTO());
    	assertTrue(resepti != null);
    	assertTrue(resepti.getAnnostelukaudenAlkupvm() != null);
    	assertTrue(resepti.getAnnostelukaudenLoppupvm() == null);  	
    }

    @Test
    public void testPuraAnnostelukaudenAika_Loppuaika() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
    	POCDMT000040SubstanceAdministration sa = of.createPOCDMT000040SubstanceAdministration();  
    	IVLTS time = of.createIVLTS();
    	IVXBTS high = of.createIVXBTS();
     	high.setValue("20210101");
    	time.setHigh(high);
    	sa.getEffectiveTimes().add(time);
    	
    	LaakemaaraysTO resepti = tested.puraAnnostelukaudenAika(sa, new LaakemaaraysTO());
    	assertTrue(resepti != null);
    	assertTrue(resepti.getAnnostelukaudenAlkupvm() == null);
    	assertTrue(resepti.getAnnostelukaudenLoppupvm() != null);  	
    }

    @Test
    public void testPuraAnnostelukaudenAika_EiPaivia() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
    	POCDMT000040SubstanceAdministration sa = of.createPOCDMT000040SubstanceAdministration();  
    	IVLTS time = of.createIVLTS();
    	sa.getEffectiveTimes().add(time);
    	
    	LaakemaaraysTO resepti = tested.puraAnnostelukaudenAika(sa, new LaakemaaraysTO());
    	assertTrue(resepti != null);
    	assertTrue(resepti.getAnnostelukaudenAlkupvm() == null);
    	assertTrue(resepti.getAnnostelukaudenLoppupvm() == null);  	
    }

    @Test
    public void testPuraAnnostelukaudenAika_SaNULL() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();    	
    	LaakemaaraysTO resepti = tested.puraAnnostelukaudenAika(null, new LaakemaaraysTO());
    	assertTrue(resepti != null);
    	assertTrue(resepti.getAnnostelukaudenAlkupvm() == null);
    	assertTrue(resepti.getAnnostelukaudenLoppupvm() == null);  	
    }

    @Test
    public void testPuraAnnostelukaudenAika_ToNULL() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
    	POCDMT000040SubstanceAdministration sa = of.createPOCDMT000040SubstanceAdministration();  
    	IVLTS time = of.createIVLTS();
    	IVXBTS low = of.createIVXBTS();
    	IVXBTS high = of.createIVXBTS();
    	low.setValue("20210101");
    	high.setValue("20210131");
    	time.setLow(low);
    	time.setHigh(high);
    	sa.getEffectiveTimes().add(time);
    	
    	LaakemaaraysTO resepti = tested.puraAnnostelukaudenAika(sa, null);
    	assertTrue(resepti == null);
    }

    @Test (expected = Exception.class)
    public void testPuraAnnostelukaudenAika_alkupvmEiParsittavissa() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
    	POCDMT000040SubstanceAdministration sa = of.createPOCDMT000040SubstanceAdministration();  
    	IVLTS time = of.createIVLTS();
    	IVXBTS low = of.createIVXBTS();
    	IVXBTS high = of.createIVXBTS();
    	low.setValue("AAAABBCC");
    	high.setValue("20210131");
    	time.setLow(low);
    	time.setHigh(high);
    	sa.getEffectiveTimes().add(time);
    	
    	tested.puraAnnostelukaudenAika(sa, new LaakemaaraysTO());
     }

    @Test (expected = Exception.class)
    public void testPuraAnnostelukaudenAika_loppupvmEiParsittavissa() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
    	POCDMT000040SubstanceAdministration sa = of.createPOCDMT000040SubstanceAdministration();  
    	IVLTS time = of.createIVLTS();
    	IVXBTS low = of.createIVXBTS();
    	IVXBTS high = of.createIVXBTS();
    	low.setValue("20210131");
    	high.setValue("A1B2C3D4");
    	time.setLow(low);
    	time.setHigh(high);
       	sa.getEffectiveTimes().add(time);
    	
    	tested.puraAnnostelukaudenAika(sa, new LaakemaaraysTO());
     }
 
    @Test
    public void testPuraLaakeTauolla_Vali() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation obs = of.createPOCDMT000040Observation();
    	IVLTS time = of.createIVLTS();
    	IVXBTS low = of.createIVXBTS();
    	IVXBTS high = of.createIVXBTS();
    	low.setValue("20210101");
    	high.setValue("20210131");
    	time.setLow(low);
    	time.setHigh(high);
    	obs.setEffectiveTime(time);
    	
    	LaakemaaraysTO resepti = tested.puraLaakeTauolla(obs, new LaakemaaraysTO());
    	assertTrue(resepti != null);
    	assertTrue(resepti.getLaakeTauollaAlkupvm() != null);
    	assertTrue(resepti.getLaakeTauollaLoppupvm() != null);  	
    }
    
    @Test
    public void testPuraLaakeTauolla_Alkuaika() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation obs = of.createPOCDMT000040Observation();
    	IVLTS time = of.createIVLTS();
    	IVXBTS low = of.createIVXBTS();
     	low.setValue("20210101");
    	time.setLow(low);
    	obs.setEffectiveTime(time);
   	
    	LaakemaaraysTO resepti = tested.puraLaakeTauolla(obs, new LaakemaaraysTO());
    	assertTrue(resepti != null);
    	assertTrue(resepti.getLaakeTauollaAlkupvm() != null);
    	assertTrue(resepti.getLaakeTauollaLoppupvm() == null);  	
    }

    @Test
    public void testPuraLaakeTauolla_Loppuaika() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation obs = of.createPOCDMT000040Observation();
    	IVLTS time = of.createIVLTS();
    	IVXBTS high = of.createIVXBTS();
     	high.setValue("20210101");
    	time.setHigh(high);
    	obs.setEffectiveTime(time);
    	
    	LaakemaaraysTO resepti = tested.puraLaakeTauolla(obs, new LaakemaaraysTO());
    	assertTrue(resepti != null);
    	assertTrue(resepti.getLaakeTauollaAlkupvm() == null);
    	assertTrue(resepti.getLaakeTauollaLoppupvm() != null);  	
    }

    @Test
    public void testPuraLaakeTauolla_EiPaivia() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation obs = of.createPOCDMT000040Observation();
    	IVLTS time = of.createIVLTS();
    	obs.setEffectiveTime(time);
    	
    	LaakemaaraysTO resepti = tested.puraLaakeTauolla(obs, new LaakemaaraysTO());
    	assertTrue(resepti != null);
    	assertTrue(resepti.getLaakeTauollaAlkupvm() == null);
    	assertTrue(resepti.getLaakeTauollaLoppupvm() == null);  	
    }

    @Test
    public void testPuraLaakeTauolla_SaNULL() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();    	
    	LaakemaaraysTO resepti = tested.puraLaakeTauolla(null, new LaakemaaraysTO());
    	assertTrue(resepti != null);
    	assertTrue(resepti.getLaakeTauollaAlkupvm() == null);
    	assertTrue(resepti.getLaakeTauollaLoppupvm() == null);  	
    }

    @Test
    public void testPuraLaakeTauolla_ToNULL() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation obs = of.createPOCDMT000040Observation();
    	IVLTS time = of.createIVLTS();
    	IVXBTS low = of.createIVXBTS();
    	IVXBTS high = of.createIVXBTS();
    	low.setValue("20210101");
    	high.setValue("20210131");
    	time.setLow(low);
    	time.setHigh(high);
    	obs.setEffectiveTime(time);
    	
    	LaakemaaraysTO resepti = tested.puraLaakeTauolla(obs, null);
    	assertTrue(resepti == null);
    }

    @Test (expected = Exception.class)
    public void testPuraLaakeTauolla_alkupvmEiParsittavissa() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation obs = of.createPOCDMT000040Observation();
    	IVLTS time = of.createIVLTS();
    	IVXBTS low = of.createIVXBTS();
    	IVXBTS high = of.createIVXBTS();
    	low.setValue("AAAABBCC");
    	high.setValue("20210131");
    	time.setLow(low);
    	time.setHigh(high);
       	obs.setEffectiveTime(time);
    	
    	tested.puraLaakeTauolla(obs, new LaakemaaraysTO());
     }

    @Test (expected = Exception.class)
    public void testPuraLaakeTauolla_loppupvmEiParsittavissa() throws Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        POCDMT000040Observation obs = of.createPOCDMT000040Observation();
    	IVLTS time = of.createIVLTS();
    	IVXBTS low = of.createIVXBTS();
    	IVXBTS high = of.createIVXBTS();
    	low.setValue("20210131");
    	high.setValue("A1B2C3D4");
    	time.setLow(low);
    	time.setHigh(high);
       	obs.setEffectiveTime(time);
    	
    	tested.puraLaakeTauolla(obs, new LaakemaaraysTO());
     }

	@Test
	public void testOnkoFysikaalinenAnnosEntry_tyhja_entry() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();
		assertFalse(tested.onkoFysikaalinenAnnosEntry(of.createPOCDMT000040EntryRelationship()));
	}

	@Test
	public void testOnkoFysikaalinenAnnosEntry_ei_koodia() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();
		POCDMT000040EntryRelationship e = of.createPOCDMT000040EntryRelationship();
		POCDMT000040SubstanceAdministration sa = of.createPOCDMT000040SubstanceAdministration();
		e.setSubstanceAdministration(sa);

		assertFalse(tested.onkoFysikaalinenAnnosEntry(e));
	}

	@Test
	public void testOnkoFysikaalinenAnnosEntry_vaara_koodi() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();
		POCDMT000040EntryRelationship e = of.createPOCDMT000040EntryRelationship();
		POCDMT000040SubstanceAdministration sa = of.createPOCDMT000040SubstanceAdministration();
		CD c = of.createCD();
		sa.setCode(c);
		c.setCode("AAA");
		e.setSubstanceAdministration(sa);

		assertFalse(tested.onkoFysikaalinenAnnosEntry(e));
	}

	@Test
	public void testOnkoFysikaalinenAnnosEntry() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();
		POCDMT000040EntryRelationship e = of.createPOCDMT000040EntryRelationship();
		POCDMT000040SubstanceAdministration sa = of.createPOCDMT000040SubstanceAdministration();
		CD c = of.createCD();
		sa.setCode(c);
		c.setCode(KantaCDAConstants.Laakityslista.FYS_ANNOKSET_CODE);
		e.setSubstanceAdministration(sa);

		assertTrue(tested.onkoFysikaalinenAnnosEntry(e));
	}

	@Test
	public void testPuraFysikaalinenAnnosMaara_VakioArvo() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();

		IVLPQ doseQ = of.createIVLPQ();
		PQ center = of.createPQ();
		center.setUnit("mg");
		center.setValue("2.5");
		doseQ.setCenter(center);
		AnnosTO annos = new AnnosTO();
		tested.puraFysikaalinenAnnosMaara(doseQ, annos);
		
		assertEquals(Double.valueOf(2.5), annos.getVakioFysAnnos());
		assertEquals("mg", annos.getFysYksikko());
		assertNull(annos.getHighAnnos());
		assertNull(annos.getLowAnnos());

	}

	@Test
	public void testPuraFysikaalinenAnnosMaara_ValiArvo() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();
		IVLPQ doseQ = of.createIVLPQ();
		IVXBPQ high = of.createIVXBPQ();
		high.setUnit("mg");
		high.setValue("2.5");
		doseQ.setHigh(high);
		IVXBPQ low = of.createIVXBPQ();
		low.setUnit("mg");
		low.setValue("0.5");
		doseQ.setLow(low);
		AnnosTO annos = new AnnosTO();
		tested.puraFysikaalinenAnnosMaara(doseQ, annos);
		
		assertEquals(Double.valueOf(2.5), annos.getHighFysAnnos());
		assertEquals(Double.valueOf(0.5), annos.getLowFysAnnos());
		assertEquals("mg", annos.getFysYksikko());
		assertNull(annos.getVakioAnnos());
	}
	
	@Test
	public void testPuraFysikaalinenAnnosMaara_ValueNULL() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();
		AnnosTO annos = tested.puraFysikaalinenAnnosMaara(null, new AnnosTO());
		
    	assertTrue(annos != null);
    	assertTrue(annos.getLowFysAnnos() == null);
    	assertTrue(annos.getHighFysAnnos() == null);
    	assertTrue(annos.getFysYksikko() == null);
    	assertTrue(annos.getVakioFysAnnos() == null);
	}

    @Test
    public void testPuraFysikaalinenAnnosMaara_EiTietoja() throws Exception {
    	IVLPQ doseQ = of.createIVLPQ();
        ReseptiPurkaja tested = new ReseptiPurkaja();
        AnnosTO annos = tested.puraFysikaalinenAnnosMaara(doseQ, new AnnosTO());
 
        assertTrue(annos != null);
    	assertTrue(annos.getLowFysAnnos() == null);
    	assertTrue(annos.getHighFysAnnos() == null);
    	assertTrue(annos.getFysYksikko() == null);
    	assertTrue(annos.getVakioFysAnnos() == null);
    }

	@Test
	public void testPuraFysikaalinenAnnosMaara_ToNULL() throws Exception {
		ReseptiPurkaja tested = new ReseptiPurkaja();
		IVLPQ doseQ = of.createIVLPQ();
		PQ center = of.createPQ();
		center.setUnit("mg");
		center.setValue("2.5");
		doseQ.setCenter(center);
		AnnosTO annos = tested.puraFysikaalinenAnnosMaara(doseQ, null);

		assertTrue(annos == null);
	}

	private POCDMT000040Entry luoEntry(String code) {
        POCDMT000040Entry entry = of.createPOCDMT000040Entry();
        entry.setOrganizer(of.createPOCDMT000040Organizer());
        entry.getOrganizer().setCode(of.createCD());
        entry.getOrganizer().getCode().setCode(code);
        return entry;
    }

    private POCDMT000040Participant2 luoTestParticipant(String role, String name) {
        POCDMT000040Participant2 participant = of.createPOCDMT000040Participant2();
        participant.setParticipantRole(of.createPOCDMT000040ParticipantRole());
        participant.getParticipantRole().getClassCodes().add(role);
        participant.getParticipantRole().setPlayingEntity(of.createPOCDMT000040PlayingEntity());
        participant.getParticipantRole().getPlayingEntity().getNames().add(of.createPN());
        participant.getParticipantRole().getPlayingEntity().getNames().get(0).getContent().add(name);
        return participant;
    }

    private POCDMT000040Observation luoTestIterointiObservation() {
        POCDMT000040Observation obs = luoTestObservation(of.createST(), "EIMITÄÄNVÄLIÄ", "121");
        obs.setText(of.createED());
        obs.getText().getContent().add(testIterointiteksti);
        obs.setEffectiveTime(of.createIVLTS());
        obs.getEffectiveTime().setWidth(of.createPQ());
        obs.getEffectiveTime().getWidth().setUnit(testIterointiUnit);
        obs.getEffectiveTime().getWidth().setValue(String.valueOf(testIterointiValue));
        obs.setRepeatNumber(of.createIVLINT());
        obs.getRepeatNumber().setValue(BigInteger.valueOf(testIterointiMaara));
        return obs;
    }

    private POCDMT000040Observation luoTestLaakemuotoObservation() {
        POCDMT000040Observation obs = luoTestObservation(of.createST(), testLaakemuoto, "24");
        return obs;
    }

    private POCDMT000040Observation luoTestValmisteenLajiObservation() {
        POCDMT000040Observation obs = luoTestObservation(of.createCD(), testValmisteenLaji, "164");
        ((CD) obs.getValues().get(0)).setDisplayName(testValmisteenLajiName);
        return obs;
    }

    private POCDMT000040Observation luoTestSailytysastionObservation() {
        POCDMT000040Observation obs = luoTestObservation(of.createSC(), testSailytysastia, "128");
        return obs;
    }

    private POCDMT000040Observation luoTestPakkauskokoObservation() {
        POCDMT000040Observation obs = luoTestObservation(of.createST(), testPakkauskokoteksti, "126");
        return obs;
    }

    private POCDMT000040Observation luoTestPakkauskoonKerroinObservation() {
        POCDMT000040Observation obs = luoTestObservation(of.createINT(), "EIMITÄÄNVÄLIÄ", "125");
        ((INT) obs.getValues().get(0)).setValue(BigInteger.valueOf(testPakkauskoonkerroin));
        return obs;
    }

    private POCDMT000040Observation luoTestApteekissavalmistettavanLaakkeenOsoitinObservation() {
        POCDMT000040Observation obs = luoTestObservation(of.createBL(), "true", "124");
        return obs;
    }

    private POCDMT000040Component4 luoTestComponent(ANY type, String value, String code) {
        POCDMT000040Component4 component = of.createPOCDMT000040Component4();
        component.setObservation(luoTestObservation(type, value, code));
        return component;
    }

    private POCDMT000040Observation luoTestObservation(ANY type, String value, String code) {
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        observation.setCode(of.createCD());
        observation.getCode().setCode(code);
        if ( type instanceof ST ) {
            ((ST) type).getContent().add(value);
        }
        else if ( type instanceof CE ) {
            ((CE) type).setCode(value);
        }
        else if ( type instanceof BL ) {
            ((BL) type).setValue(null == value ? false : ("true".equalsIgnoreCase(value)));
        }
        else if ( type instanceof CD ) {
            ((CD) type).setCode(value);
        }
        else if ( type instanceof SC ) {
            ((SC) type).getContent().add(value);
        }
        else if ( type instanceof PQ ) {
            ((PQ) type).setValue(value);
        }
        observation.getValues().add(type);
        return observation;
    }

    private POCDMT000040HealthCareFacility luoTestPalveluyksikko() {
        POCDMT000040HealthCareFacility healthCareFacility = of.createPOCDMT000040HealthCareFacility();
        healthCareFacility.getIds().add(of.createII());
        healthCareFacility.getIds().get(0).setRoot(testOrgId);
        healthCareFacility.setLocation(of.createPOCDMT000040Place());
        healthCareFacility.getLocation().setName(of.createEN());
        healthCareFacility.getLocation().getName().getContent().add(testOrgName);
        healthCareFacility.getLocation().setAddr(luoTestOsoite());
        return healthCareFacility;
    }

    private AD luoTestOsoite() {
        AD ad = of.createAD();
        AdxpStreetAddressLine street = of.createAdxpStreetAddressLine();
        street.getContent().add(testOrgStreet);
        ad.getContent().add(of.createADStreetAddressLine(street));
        AdxpPostalCode postal = of.createAdxpPostalCode();
        postal.getContent().add(testOrgPostCode);
        ad.getContent().add(of.createADPostalCode(postal));
        AdxpCity city = of.createAdxpCity();
        city.getContent().add(testOrgCity);
        ad.getContent().add(of.createADCity(city));
        return ad;
    }

    private POCDMT000040PatientRole luoTestPotilas() {
        POCDMT000040PatientRole patientRole = of.createPOCDMT000040PatientRole();
        patientRole.getIds().add(of.createII());
        patientRole.getIds().get(0).setExtension(testPatientHetu);
        patientRole.setPatient(of.createPOCDMT000040Patient());
        patientRole.getPatient().getNames().add(luoTestNames(testPatientEtunimi, testPatientSukunimi));
        return patientRole;
    }

    private POCDMT000040Author luoTestAuthor(String role) {
        return luoTestAuthor(role, testAuthorSVExtension, testAuthorRekExtension, testAuthorErikosala,
                testAuthorErikoisalaName, testAuthorVirkanimike, testAuthorOppiarvo, testAuthorAmmattioikeus,
                testAuthorAmmattioikeusName, testAuthorKirjautumisaika,
                new KokoNimiTO(testAuthorEtunimi, testAuthorSukunimi), null);
    }

    private POCDMT000040Author luoTestAuthor(String rooli, String svNro, String rekNro, String erikoisala,
            String erikoisalaName, String virkaNimike, String oppiarvo, String ammattioikeus, String ammattioikeusName,
            String kirjautumisaika, KokoNimiTO nimi, OrganisaatioTO organisaatio) {
        POCDMT000040Author author = of.createPOCDMT000040Author();

        if ( null != kirjautumisaika ) {
            TS time = of.createTS();
            time.setValue(kirjautumisaika);
            author.setTime(time);
        }
        author.setFunctionCode(of.createCE());
        author.getFunctionCode().setCode(rooli);
        author.setAssignedAuthor(of.createPOCDMT000040AssignedAuthor());

        if ( null != svNro ) {
            II id = of.createII();
            id.setRoot(testAuthorSVRoot);
            id.setExtension(svNro);
            author.getAssignedAuthor().getIds().add(id);
        }
        if ( null != rekNro ) {
            II id = of.createII();
            id.setRoot(testAuthorRekRoot);
            id.setExtension(rekNro);
            author.getAssignedAuthor().getIds().add(id);
        }
        if ( null != erikoisala ) {
            author.getAssignedAuthor().setCode(of.createCE());
            author.getAssignedAuthor().getCode().setCode(erikoisala);
            author.getAssignedAuthor().getCode().setDisplayName(erikoisalaName);
            CD translation = of.createCD();
            if ( null != virkaNimike ) {
                CR cr = of.createCR();
                cr.setName(of.createCV());
                cr.getName().setCodeSystem(testAuthorVirkaOppiarvoCodeSystem);
                cr.getName().setCode(testAuthorVirkanimikeCode);
                cr.setValue(of.createCD());
                cr.getValue().setOriginalText(of.createED());
                cr.getValue().getOriginalText().getContent().add(virkaNimike);
                translation.getQualifiers().add(cr);
            }
            if ( null != oppiarvo ) {
                CR cr = of.createCR();
                cr.setName(of.createCV());
                cr.getName().setCodeSystem(testAuthorVirkaOppiarvoCodeSystem);
                cr.getName().setCode(testAuthorOppiarvoCode);
                cr.setValue(of.createCD());
                cr.getValue().setCode(oppiarvo);
                cr.getValue().setOriginalText(of.createED());
                cr.getValue().getOriginalText().getContent().add(oppiarvo);
                translation.getQualifiers().add(cr);
            }
            if ( null != ammattioikeus ) {
                CR cr = of.createCR();
                cr.setName(of.createCV());
                cr.getName().setCodeSystem(testAuthorAmmattioikeusCodeSystem);
                cr.getName().setCode(testAuthorAmmattioikeusCode);
                cr.setValue(of.createCD());
                cr.getValue().setCode(ammattioikeus);
                cr.getValue().setDisplayName(ammattioikeusName);
                translation.getQualifiers().add(cr);
            }
            author.getAssignedAuthor().getCode().getTranslations().add(translation);
            author.getAssignedAuthor().setAssignedPerson(of.createPOCDMT000040Person());
            author.getAssignedAuthor().getAssignedPerson().getNames()
                    .add(luoTestNames(nimi.getEtunimi(), nimi.getSukunimi()));
            author.getAssignedAuthor().setRepresentedOrganization(luoTestOrganization(organisaatio));
        }
        return author;
    }

    private PN luoTestNames(String etunimi, String sukunimi) {
        PN nimi = of.createPN();
        EnGiven engiven = of.createEnGiven();
        engiven.getContent().add(etunimi);
        nimi.getContent().add(of.createENGiven(engiven));
        EnFamily enfamily = of.createEnFamily();
        enfamily.getContent().add(sukunimi);
        nimi.getContent().add(of.createENFamily(enfamily));
        return nimi;
    }

    private POCDMT000040Organization luoTestOrganization(OrganisaatioTO organisaatio) {
        POCDMT000040Organization org = of.createPOCDMT000040Organization();
        org.getIds().add(of.createII());
        org.getIds().get(0).setRoot(testOrgId);
        org.getNames().add(of.createON());
        org.getNames().get(0).getContent().add(testOrgName);
        org.getTelecoms().add(of.createTEL());
        org.getTelecoms().get(0).setValue(testOrgTelecom);
        org.getTelecoms().get(0).getUses().add("DIR");
        org.getAddrs().add(of.createAD());
        AdxpStreetAddressLine streetAddressLine = of.createAdxpStreetAddressLine();
        AdxpPostalCode postalCode = of.createAdxpPostalCode();
        AdxpCity city = of.createAdxpCity();
        streetAddressLine.getContent().add(testOrgStreet);
        postalCode.getContent().add(testOrgPostCode);
        city.getContent().add(testOrgCity);
        org.getAddrs().get(0).getContent().add(of.createADStreetAddressLine(streetAddressLine));
        org.getAddrs().get(0).getContent().add(of.createADPostalCode(postalCode));
        org.getAddrs().get(0).getContent().add(of.createADCity(city));
        return org;
    }

    private POCDMT000040Component4 luoKorjauksenPerusteuluComponent() {
        POCDMT000040Component4 comp = luoTestComponent(of.createCE(), testKorjauskoodi,
                KantaCDAConstants.Laakityslista.KORJAUKSEN_PERUSTELU);
        ST korjausPerusteluValue = of.createST();
        korjausPerusteluValue.getContent().add(testKorjausperustelu);
        comp.getObservation().getValues().add(korjausPerusteluValue);
        POCDMT000040Author author = of.createPOCDMT000040Author();
        author.setTime(of.createTS());
        author.getTime().getNullFlavors().add("NI");
        author.setAssignedAuthor(of.createPOCDMT000040AssignedAuthor());
        author.getAssignedAuthor().getIds().add(of.createII());
        author.getAssignedAuthor().getIds().get(0).getNullFlavors().add("NI");
        author.getAssignedAuthor().setAssignedPerson(of.createPOCDMT000040Person());
        author.getAssignedAuthor().getAssignedPerson().getNames()
                .add(luoTestNames(testAuthorEtunimi, testAuthorSukunimi));
        comp.getObservation().getAuthors().add(author);
        return comp;
    }

    private POCDMT000040Observation luoTestMitatoinninSyyObservation() {
        POCDMT000040Observation obs = luoTestObservation(of.createCE(), testMitatointiSyy, "95");
        ST mitatointiPerusteluValue = of.createST();
        mitatointiPerusteluValue.getContent().add(testMitatointiPerustelu);
        obs.getValues().add(mitatointiPerusteluValue);
        return obs;
    }

    private POCDMT000040Observation luoTestMitatoinninTyyppiObservation() {
        POCDMT000040Observation obs = luoTestObservation(of.createCD(), testMitatointiTyyppi, "96");
        CD value = (CD) obs.getValues().get(0);
        CR cr = of.createCR();
        cr.setName(of.createCV());
        cr.getName().setCode("96.1");
        cr.setValue(of.createCD());
        cr.getValue().setCode(testMitatointiOsapuoli);
        value.getQualifiers().add(cr);
        cr = of.createCR();
        cr.setName(of.createCV());
        cr.getName().setCode("96.2");
        cr.setValue(of.createCD());
        cr.getValue().setCode(testMitatointiSuostumus);
        value.getQualifiers().add(cr);
        return obs;
    }

    private POCDMT000040Component4 luoMitatoinninSyyComponent() {
        POCDMT000040Component4 component = of.createPOCDMT000040Component4();
        component.setObservation(luoTestMitatoinninSyyObservation());
        return component;
    }

    private POCDMT000040Component4 luoMitatoinninTyyppiComponent() {
        POCDMT000040Component4 component = of.createPOCDMT000040Component4();
        component.setObservation(luoTestMitatoinninTyyppiObservation());
        return component;
    }

    protected StrucDocParagraph luoParagraphContent(String string) {
        StrucDocParagraph paragraph = of.createStrucDocParagraph();
        StrucDocContent content = of.createStrucDocContent();
        content.getContent().add(string);
        paragraph.getContent().add(of.createStrucDocItemContent(content));
        return paragraph;
    }
    
    private POCDMT000040Observation luoTestApteekkivalmisteValmisteenLajiObservation() {
        POCDMT000040Observation obs = luoTestObservation(of.createCD(), "7", "164");
        ((CD) obs.getValues().get(0)).setDisplayName("Apteekissa valmistettava lääke");
        return obs;
    }


}
