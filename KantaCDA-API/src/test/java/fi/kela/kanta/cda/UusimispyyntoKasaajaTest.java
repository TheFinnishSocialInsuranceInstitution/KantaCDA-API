package fi.kela.kanta.cda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.bind.JAXBException;

import org.hl7.v3.CE;
import org.hl7.v3.POCDMT000040Act;
import org.hl7.v3.POCDMT000040Author;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040EntryRelationship;
import org.hl7.v3.POCDMT000040InformationRecipient;
import org.hl7.v3.POCDMT000040Participant2;
import org.hl7.v3.POCDMT000040Patient;
import org.hl7.v3.POCDMT000040PatientRole;
import org.hl7.v3.POCDMT000040RecordTarget;
import org.hl7.v3.POCDMT000040Subject;
import org.hl7.v3.ParticipationTargetSubject;
import org.hl7.v3.XActMoodDocumentObservation;
import org.hl7.v3.XActRelationshipEntryRelationship;
import org.hl7.v3.XDocumentSubject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.UusimispyyntoTO;
import fi.kela.kanta.util.LMTOKasaaja;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*" })
public class UusimispyyntoKasaajaTest extends LMTOKasaaja {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static final String testOid = "1.2.34.567.8910";
    private static final String testSetId = "1.2.34.56.78910";
    private static final String testOrgNimi = "TESTIORGANISAATIO";
    private static final String testKey = "TESTKEY";
    private static final String testCode = "testCode";
    private static final String testCodeSystem = "testCodeSystem";
    private static final String testCodeSystemName = "testCodeSystemName";
    private static final String testDisplayName = "testDisplayName";
    private static final String testValmisteenNimi = "TESTVALMISTE";
    private static final String testEtunumi = "TESTETU";
    private static final String testSukunimi = "TESTSUKU";
    private static final String testKatu = "testKatu";
    private static final String testPostinumero = "12345";
    private static final String testKaupunki = "testKaupunki";
    private static final String testPuhelinnumero = "123456678";
    private static final String testHetu = "123456-321O";
    private static final String testVastaanottajaId = "1.12.123.1234.12345";
    private static final String testVastaanottajaNimi = "Vastaanottaja.Nimi";
    private static final String testVastaanottajaKatu = "Vastaanottaja.Katu";
    private static final String testVastaanottajaPostinumero = "Vastaanottaja.Postinumero";
    private static final String testVastaanottajaKaupunki = "Vastaanottaja.Kaupunki";
    private static final String testVastaanottajaPuhelinnumero = "Vastaanottaja.Puhelinnumero";
    private static final String testMaaraajaEtunimi = "Määräjä.Etunimi";
    private static final String testMaaraajaSukunimi = "Määräjä.Sukunimi";
    private static final String testMaaraajaId = "1.11.111.1111.11111";
    private static final String testUusittavaLaakemaaraysOid = "1.23.456.7891.01112";
    private static final String testUusittavaLaakemaaraysSetId = "1.23.456.7891.011121";
    private static final String testRoot = "1.2.3.4.5";
    private static final String testExtension = "123456789";
    private static final String testOrgYTunnus = "1234567890";
    private final String testIdRoot = "1.2.3.4";
    private final String testOrgId = "1.12.123.1234";
    private final String testSvNumero = "65433456";
    private final String testEffectiveTime = "20150828094530";
    private final String uusimisTypeKey = "UUSIMIS";

    @Before
    public void setUp() {
        setupProperties();
    }

    protected void setupProperties() {
        KantaCDATestUtils.setupProperties();
    }

    private UusimispyyntoTO setupTestiUusimispyyntoTO() throws ParseException {
        UusimispyyntoTO to = new UusimispyyntoTO();
        to.setVastaanottajaId(testVastaanottajaId);
        to.setVastaanottajaNimi(testVastaanottajaNimi);
        to.setVastaanottajaKatu(testVastaanottajaKatu);
        to.setVastaanottajaPostinumero(testVastaanottajaPostinumero);
        to.setVastaanottajaKaupunki(testVastaanottajaKaupunki);
        to.setVastaanottajaPuhelinnumero(testVastaanottajaPuhelinnumero);

        to.setMaaraajanKokonimi(new KokoNimiTO(testMaaraajaEtunimi, testMaaraajaSukunimi));
        to.setMaaraajanId(testMaaraajaId);

        HenkilotiedotTO ht = new HenkilotiedotTO(new KokoNimiTO(testEtunumi, testSukunimi), testHetu);
        to.setHenkilotiedot(ht);
        to.setMatkapuhelinnumero(testPuhelinnumero);

        to.setUusittavaLaakemaaraysOid(testUusittavaLaakemaaraysOid);
        to.setUusittavaLaakemaaraysSetId(testUusittavaLaakemaaraysSetId);
        to.setValmisteenNimi(testValmisteenNimi);

        to.setAikaleima(new SimpleDateFormat("yyyyMMddhhmmss").parse(testEffectiveTime));

        // to.setOrgYTunnus(testOrgYTunnus);
        return to;
    }

    private UusimispyyntoKasaaja setupUusimispyyntoKasaaja() throws ParseException {
        return setupUusimispyyntoKasaaja(setupTestiUusimispyyntoTO());
    }

    private UusimispyyntoKasaaja setupUusimispyyntoKasaaja(UusimispyyntoTO uusimispyynto) {
        KantaCDATestUtils.mockProperty(Kasaaja.BASE_OID_PROPERTY, KantaCDATestUtils.testBaseId);
        return new UusimispyyntoKasaaja(KantaCDATestUtils.getProperties(), uusimispyynto);
    }

    @Test
    public void testGetTypeKey() throws ParseException {
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        Assert.assertEquals("getTypeKeyn tulee palauttaa: " + uusimisTypeKey, uusimisTypeKey, tested.getTypeKey());
    }

    @Test
    public void testKasaaUusimispyynto() throws Exception {
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        String cda = tested.kasaaReseptiAsiakirja();
        assertNotNull("cda ei voi olla null.", cda);
        assertFalse("cda ei voi olla tyhjä", cda.isEmpty());
    }

    @Test
    public void testLuoPaaosa() throws Exception {
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        POCDMT000040Entry entry = Whitebox.invokeMethod(tested, "luoPaaosa", setupTestiUusimispyyntoTO());
        assertNotNull("entry ei voi olla null.", entry);
        assertNotNull("entryn act ei voi olla null.", entry.getAct());
        assertNotNull("entry/act/code ei voi olla null", entry.getAct().getCode());
        assertNotNull("entry/act/subject ei voi olla null", entry.getAct().getSubject());
        assertFalse("entry/act/participants ei voi tyhjä", entry.getAct().getParticipants().isEmpty());
        assertFalse("entry/act/entryRelationships ei voi olla tyhjä", entry.getAct().getEntryRelationships().isEmpty());
    }

    @Test
    public void testLuoValimisteenNimiMaaraajaJaMaaraysPvm() throws Exception {
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        UusimispyyntoTO to = setupTestiUusimispyyntoTO();
        POCDMT000040Entry entry = Whitebox.invokeMethod(tested, "luoValimisteenNimiMaaraajaJaMaaraysPvm", to);
        assertNotNull("entry ei voi olla null.", entry);
        assertNotNull("entry/substanceAdministration ei voi olla null.", entry.getSubstanceAdministration());
        assertFalse("substanceAdministration effectivetime ei voi olla tyhjä.",
                entry.getSubstanceAdministration().getEffectiveTimes().isEmpty());
        assertNotNull("substanceAdministration/consumable ei voi olla tyhjä.",
                entry.getSubstanceAdministration().getConsumable());
        assertNotNull("consumable/manufacturedProduct ei voi olla tyhjä.",
                entry.getSubstanceAdministration().getConsumable().getManufacturedProduct());
        assertNotNull("manufacturedProduct/ManufaacturedLabeledDrug ei voi olla tyhjä.", entry
                .getSubstanceAdministration().getConsumable().getManufacturedProduct().getManufacturedLabeledDrug());
        assertEquals("manufacturedLabeledDrug/name tulee olla :" + to.getValmisteenNimi(), to.getValmisteenNimi(),
                entry.getSubstanceAdministration().getConsumable().getManufacturedProduct().getManufacturedLabeledDrug()
                        .getName().getContent().get(0));
        assertFalse("substanceAdministration/authors ei voi olla tyhjä.",
                entry.getSubstanceAdministration().getAuthors().isEmpty());
    }

    @Test
    public void testLisaaMuuttiedot() throws Exception {
        String code113 = "113";
        String code120 = "120";
        KantaCDATestUtils.mockCodeProperty(code113, code113, testCodeSystem, testCodeSystemName, testDisplayName);
        KantaCDATestUtils.mockCodeProperty(code120, code120, testCodeSystem, testCodeSystemName, testDisplayName);
        POCDMT000040Act act = new POCDMT000040Act();
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        UusimispyyntoTO to = setupTestiUusimispyyntoTO();
        Whitebox.invokeMethod(tested, "lisaaMuuttiedot", act, to);
        assertFalse("act/entryRelationship ei voi olla tyhjä.", act.getEntryRelationships().isEmpty());
        assertEquals("act/entryRelaitionshp size tulee olla 2", 2, act.getEntryRelationships().size());
        assertEquals("ensimmäinen entryRelationship/observation/code tulee olla 113", "113",
                act.getEntryRelationships().get(0).getObservation().getCode().getCode());
        assertEquals("toinen entryRelationship/observation/code tulee olla 120", "120",
                act.getEntryRelationships().get(1).getObservation().getCode().getCode());
    }

    @Test
    public void testLisaaMuuttiedot_withUusija() throws Exception {
        String code113 = "113";
        String code120 = "120";
        KantaCDATestUtils.mockCodeProperty(code113, code113, testCodeSystem, testCodeSystemName, testDisplayName);
        KantaCDATestUtils.mockCodeProperty(code120, code120, testCodeSystem, testCodeSystemName, testDisplayName);
        POCDMT000040Act act = new POCDMT000040Act();
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        UusimispyyntoTO to = setupTestiUusimispyyntoTO();
        to.setUusija(KantaCDATestUtils.luoAmmattihenkilo());
        Whitebox.invokeMethod(tested, "lisaaMuuttiedot", act, to);
        assertFalse("act/entryRelationship ei voi olla tyhjä.", act.getEntryRelationships().isEmpty());
        assertEquals("act/entryRelaitionshp size tulee olla 2", 2, act.getEntryRelationships().size());
        assertEquals("ensimmäinen entryRelationship/observation/code tulee olla 113", "113",
                act.getEntryRelationships().get(0).getObservation().getCode().getCode());
        assertEquals("toinen entryRelationship/observation/code tulee olla 120", "120",
                act.getEntryRelationships().get(1).getObservation().getCode().getCode());
    }

    @Test
    public void testLuoEntryRelationshipObservationCodeRakenne() throws Exception {
        KantaCDATestUtils.mockCodeProperty(testCode, testCode, testCodeSystem, testCodeSystemName, testDisplayName);
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        POCDMT000040EntryRelationship entryRelationship = Whitebox.invokeMethod(tested,
                "luoEntryRelationshipObservationCodeRakenne", testCode);
        assertNotNull("entryRelationShip ei voi olla null.", entryRelationship);
        assertEquals("entryRelationship typeCode tulee olla COMP", XActRelationshipEntryRelationship.COMP,
                entryRelationship.getTypeCode());
        assertNotNull("entryRelationship/observation ei voi olla null.", entryRelationship.getObservation());
        assertEquals("observation classCode tulee olla OBS", "OBS",
                entryRelationship.getObservation().getClassCodes().get(0));
        assertEquals("observation moodCode tulee olla EVN", XActMoodDocumentObservation.EVN,
                entryRelationship.getObservation().getMoodCode());
        assertNotNull("observation/code ei voi olla null.", entryRelationship.getObservation().getCode());
        assertEquals("testCode", testCode, entryRelationship.getObservation().getCode().getCode());
    }

    @Test
    public void testLuoKohdeOrganisaatio() throws Exception {
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        UusimispyyntoTO to = setupTestiUusimispyyntoTO();
        POCDMT000040Participant2 participant = Whitebox.invokeMethod(tested, "luoKohdeOrganisaatio", to);
        assertNotNull("participant ei voi olla null.", participant);
        assertNotNull("participant/participantRole ei voi olla null.", participant.getParticipantRole());
        assertEquals("participantRole classCode tulee olla ROL", "ROL",
                participant.getParticipantRole().getClassCodes().get(0));
        assertFalse("participantRole/addr ei voi olla tyhjä", participant.getParticipantRole().getAddrs().isEmpty());
        assertEquals("participantRole/addr/content size tulee olla 3", 3,
                participant.getParticipantRole().getAddrs().get(0).getContent().size());
        assertFalse("participantRole/telecom ei voi olla tyhjä.",
                participant.getParticipantRole().getTelecoms().isEmpty());
        assertNotNull("participantRole/playingEntity ei voi olla null.",
                participant.getParticipantRole().getPlayingEntity());
        assertNotNull("participantRole/scopingEntity ei voi olla null.",
                participant.getParticipantRole().getScopingEntity());
        assertEquals("scopingEntity classCode tulee olla ORG", "ORG",
                participant.getParticipantRole().getScopingEntity().getClassCodes().get(0));
        assertEquals("scopingEntity/id root tulee olla " + testVastaanottajaId, testVastaanottajaId,
                participant.getParticipantRole().getScopingEntity().getIds().get(0).getRoot());
        assertNotNull("scopingEntity/desc ei voi olla null.",
                participant.getParticipantRole().getScopingEntity().getDesc());
        assertEquals("desc tulee olla " + testVastaanottajaNimi, testVastaanottajaNimi,
                participant.getParticipantRole().getScopingEntity().getDesc().getContent().get(0));
    }

    @Test
    public void testLuoKohdeOrganisaatio_noVastaanottajaPuhelinnumeroa() throws Exception {
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        UusimispyyntoTO to = setupTestiUusimispyyntoTO();
        to.setVastaanottajaPuhelinnumero(null);

        POCDMT000040Participant2 participant = Whitebox.invokeMethod(tested, "luoKohdeOrganisaatio", to);
        assertNotNull("participant ei voi olla null.", participant);
        assertNotNull("participant/participantRole ei voi olla null.", participant.getParticipantRole());
        assertEquals("participantRole classCode tulee olla ROL", "ROL",
                participant.getParticipantRole().getClassCodes().get(0));
        assertFalse("participantRole/addr ei voi olla tyhjä", participant.getParticipantRole().getAddrs().isEmpty());
        assertEquals("participantRole/addr/content size tulee olla 3", 3,
                participant.getParticipantRole().getAddrs().get(0).getContent().size());
        assertTrue("participantRole/telecom tulee olla tyhjä.",
                participant.getParticipantRole().getTelecoms().isEmpty());
        assertNotNull("participantRole/playingEntity ei voi olla null.",
                participant.getParticipantRole().getPlayingEntity());
        assertNotNull("participantRole/scopingEntity ei voi olla null.",
                participant.getParticipantRole().getScopingEntity());
        assertEquals("scopingEntity classCode tulee olla ORG", "ORG",
                participant.getParticipantRole().getScopingEntity().getClassCodes().get(0));
        assertEquals("scopingEntity/id root tulee olla " + testVastaanottajaId, testVastaanottajaId,
                participant.getParticipantRole().getScopingEntity().getIds().get(0).getRoot());
        assertNotNull("scopingEntity/desc ei voi olla null.",
                participant.getParticipantRole().getScopingEntity().getDesc());
        assertEquals("desc tulee olla " + testVastaanottajaNimi, testVastaanottajaNimi,
                participant.getParticipantRole().getScopingEntity().getDesc().getContent().get(0));
    }

    @Test
    public void testLuoKohdeOrganisaatio_noVastaanottajaKatua() throws Exception {
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        UusimispyyntoTO to = setupTestiUusimispyyntoTO();
        to.setVastaanottajaKatu("");
        POCDMT000040Participant2 participant = Whitebox.invokeMethod(tested, "luoKohdeOrganisaatio", to);
        assertNotNull("participant ei voi olla null.", participant);
        assertNotNull("participant/participantRole ei voi olla null.", participant.getParticipantRole());
        assertEquals("participantRole classCode tulee olla ROL", "ROL",
                participant.getParticipantRole().getClassCodes().get(0));
        assertFalse("participantRole/addr ei voi olla tyhjä", participant.getParticipantRole().getAddrs().isEmpty());
        assertEquals("participantRole/addr/content size tulee olla 2", 2,
                participant.getParticipantRole().getAddrs().get(0).getContent().size());
        assertFalse("participantRole/telecom ei voi olla tyhjä.",
                participant.getParticipantRole().getTelecoms().isEmpty());
        assertNotNull("participantRole/playingEntity ei voi olla null.",
                participant.getParticipantRole().getPlayingEntity());
        assertNotNull("participantRole/scopingEntity ei voi olla null.",
                participant.getParticipantRole().getScopingEntity());
        assertEquals("scopingEntity classCode tulee olla ORG", "ORG",
                participant.getParticipantRole().getScopingEntity().getClassCodes().get(0));
        assertEquals("scopingEntity/id root tulee olla " + testVastaanottajaId, testVastaanottajaId,
                participant.getParticipantRole().getScopingEntity().getIds().get(0).getRoot());
        assertNotNull("scopingEntity/desc ei voi olla null.",
                participant.getParticipantRole().getScopingEntity().getDesc());
        assertEquals("desc tulee olla " + testVastaanottajaNimi, testVastaanottajaNimi,
                participant.getParticipantRole().getScopingEntity().getDesc().getContent().get(0));
    }

    @Test
    public void testLuoKohdeOrganisaatio_noVastaanottajaPostinumeroa() throws Exception {
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        UusimispyyntoTO to = setupTestiUusimispyyntoTO();
        to.setVastaanottajaPostinumero("");
        POCDMT000040Participant2 participant = Whitebox.invokeMethod(tested, "luoKohdeOrganisaatio", to);
        assertNotNull("participant ei voi olla null.", participant);
        assertNotNull("participant/participantRole ei voi olla null.", participant.getParticipantRole());
        assertEquals("participantRole classCode tulee olla ROL", "ROL",
                participant.getParticipantRole().getClassCodes().get(0));
        assertFalse("participantRole/addr ei voi olla tyhjä", participant.getParticipantRole().getAddrs().isEmpty());
        assertEquals("participantRole/addr/content size tulee olla 2", 2,
                participant.getParticipantRole().getAddrs().get(0).getContent().size());
        assertFalse("participantRole/telecom ei voi olla tyhjä.",
                participant.getParticipantRole().getTelecoms().isEmpty());
        assertNotNull("participantRole/playingEntity ei voi olla null.",
                participant.getParticipantRole().getPlayingEntity());
        assertNotNull("participantRole/scopingEntity ei voi olla null.",
                participant.getParticipantRole().getScopingEntity());
        assertEquals("scopingEntity classCode tulee olla ORG", "ORG",
                participant.getParticipantRole().getScopingEntity().getClassCodes().get(0));
        assertEquals("scopingEntity/id root tulee olla " + testVastaanottajaId, testVastaanottajaId,
                participant.getParticipantRole().getScopingEntity().getIds().get(0).getRoot());
        assertNotNull("scopingEntity/desc ei voi olla null.",
                participant.getParticipantRole().getScopingEntity().getDesc());
        assertEquals("desc tulee olla " + testVastaanottajaNimi, testVastaanottajaNimi,
                participant.getParticipantRole().getScopingEntity().getDesc().getContent().get(0));
    }

    @Test
    public void testLuoKohdeOrganisaatio_noVastaanottajaKaupunkia() throws Exception {
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        UusimispyyntoTO to = setupTestiUusimispyyntoTO();
        to.setVastaanottajaKaupunki("");
        POCDMT000040Participant2 participant = Whitebox.invokeMethod(tested, "luoKohdeOrganisaatio", to);
        assertNotNull("participant ei voi olla null.", participant);
        assertNotNull("participant/participantRole ei voi olla null.", participant.getParticipantRole());
        assertEquals("participantRole classCode tulee olla ROL", "ROL",
                participant.getParticipantRole().getClassCodes().get(0));
        assertFalse("participantRole/addr ei voi olla tyhjä", participant.getParticipantRole().getAddrs().isEmpty());
        assertEquals("participantRole/addr/content size tulee olla 2", 2,
                participant.getParticipantRole().getAddrs().get(0).getContent().size());
        assertFalse("participantRole/telecom ei voi olla tyhjä.",
                participant.getParticipantRole().getTelecoms().isEmpty());
        assertNotNull("participantRole/playingEntity ei voi olla null.",
                participant.getParticipantRole().getPlayingEntity());
        assertNotNull("participantRole/scopingEntity ei voi olla null.",
                participant.getParticipantRole().getScopingEntity());
        assertEquals("scopingEntity classCode tulee olla ORG", "ORG",
                participant.getParticipantRole().getScopingEntity().getClassCodes().get(0));
        assertEquals("scopingEntity/id root tulee olla " + testVastaanottajaId, testVastaanottajaId,
                participant.getParticipantRole().getScopingEntity().getIds().get(0).getRoot());
        assertNotNull("scopingEntity/desc ei voi olla null.",
                participant.getParticipantRole().getScopingEntity().getDesc());
        assertEquals("desc tulee olla " + testVastaanottajaNimi, testVastaanottajaNimi,
                participant.getParticipantRole().getScopingEntity().getDesc().getContent().get(0));
    }

    @Test
    public void testLuoPotilaantiedot() throws Exception {
        String key = "subject.relatedSubject.code";
        KantaCDATestUtils.mockCodeProperty(key, "NA", testCodeSystem, testCodeSystemName, testDisplayName);
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        UusimispyyntoTO to = setupTestiUusimispyyntoTO();
        POCDMT000040Subject subject = Whitebox.invokeMethod(tested, "luoPotilaantiedot", to);
        assertNotNull("subject ei voi olla null.", subject);
        assertEquals("subject typeCode tulee olla SBJ", ParticipationTargetSubject.SBJ, subject.getTypeCode());
        assertNotNull("subject/RelatedSubject ei voi olla null.", subject.getRelatedSubject());
        assertEquals("relatedSubject classCode tulee olla PAT", XDocumentSubject.PAT,
                subject.getRelatedSubject().getClassCode());
        assertNotNull("relatedSubject/code", subject.getRelatedSubject().getCode());
        CE code = subject.getRelatedSubject().getCode();
        assertEquals("code code tulee olla " + testHetu, testHetu, code.getCode());
        assertEquals("code codeSystem tulee olla" + testCodeSystem, testCodeSystem, code.getCodeSystem());
        assertEquals("code codeSystemName tulee olla" + testCodeSystemName, testCodeSystemName,
                code.getCodeSystemName());
        assertEquals("code displayName tulee olla" + testDisplayName, testDisplayName, code.getDisplayName());
        assertFalse("relatedSubject/telecom ei voi olla tyhjä.", subject.getRelatedSubject().getTelecoms().isEmpty());
        assertEquals("telecom size tulee olla 1", 1, subject.getRelatedSubject().getTelecoms().size());
        assertEquals("telecom use tulee olla MC", "MC",
                subject.getRelatedSubject().getTelecoms().get(0).getUses().get(0));
        assertEquals("telecom value tulee olla " + KantaCDAConstants.TEL_PREFIX + testPuhelinnumero,
                KantaCDAConstants.TEL_PREFIX + testPuhelinnumero,
                subject.getRelatedSubject().getTelecoms().get(0).getValue());

        assertNotNull("relatedSubject/subject ei voi olla null.", subject.getRelatedSubject().getSubject());
        assertEquals("subject classCode tulee olla PSN", "PSN",
                subject.getRelatedSubject().getSubject().getClassCodes().get(0));
        assertFalse("subject names ei voi olla tyhjä.", subject.getRelatedSubject().getSubject().getNames().isEmpty());
        assertFalse("subject/birthTime value ei voi olla tyhjä",
                subject.getRelatedSubject().getSubject().getBirthTime().getValue().isEmpty());
    }

    @Test
    public void testLuoPotilaantiedot_noMatkapuhelinnumeroa() throws Exception {
        String key = "subject.relatedSubject.code";
        KantaCDATestUtils.mockCodeProperty(key, "NA", testCodeSystem, testCodeSystemName, testDisplayName);
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        UusimispyyntoTO to = setupTestiUusimispyyntoTO();
        to.setMatkapuhelinnumero("");
        POCDMT000040Subject subject = Whitebox.invokeMethod(tested, "luoPotilaantiedot", to);
        assertNotNull("subject ei voi olla null.", subject);
        assertEquals("subject typeCode tulee olla SBJ", ParticipationTargetSubject.SBJ, subject.getTypeCode());
        assertNotNull("subject/RelatedSubject ei voi olla null.", subject.getRelatedSubject());
        assertEquals("relatedSubject classCode tulee olla PAT", XDocumentSubject.PAT,
                subject.getRelatedSubject().getClassCode());
        assertNotNull("relatedSubject/code", subject.getRelatedSubject().getCode());
        CE code = subject.getRelatedSubject().getCode();
        assertEquals("code code tulee olla " + testHetu, testHetu, code.getCode());
        assertEquals("code codeSystem tulee olla" + testCodeSystem, testCodeSystem, code.getCodeSystem());
        assertEquals("code codeSystemName tulee olla" + testCodeSystemName, testCodeSystemName,
                code.getCodeSystemName());
        assertEquals("code displayName tulee olla" + testDisplayName, testDisplayName, code.getDisplayName());
        assertTrue("relatedSubject/telecom tulee olla tyhjä.", subject.getRelatedSubject().getTelecoms().isEmpty());

        assertNotNull("relatedSubject/subject ei voi olla null.", subject.getRelatedSubject().getSubject());
        assertEquals("subject classCode tulee olla PSN", "PSN",
                subject.getRelatedSubject().getSubject().getClassCodes().get(0));
        assertFalse("subject names ei voi olla tyhjä.", subject.getRelatedSubject().getSubject().getNames().isEmpty());
        assertFalse("subject/birthTime value ei voi olla tyhjä",
                subject.getRelatedSubject().getSubject().getBirthTime().getValue().isEmpty());
    }

    @Test
    public void testLuoInformationRecipient() throws Exception {
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        UusimispyyntoTO to = setupTestiUusimispyyntoTO();
        POCDMT000040InformationRecipient informationRecipient = Whitebox.invokeMethod(tested, "luoInformationRecipient",
                to);
        assertNotNull("informationRecipient ei voi olla null.", informationRecipient);
        assertNotNull("informationRecipient/intendedRecipient ei voi olla null.",
                informationRecipient.getIntendedRecipient());
        assertNotNull("intendedRecipient/receivedOrganization ei voi olla null.",
                informationRecipient.getIntendedRecipient().getReceivedOrganization());
        assertEquals("receivedOrganization/id root tulee olla " + to.getVastaanottajaId(), to.getVastaanottajaId(),
                informationRecipient.getIntendedRecipient().getReceivedOrganization().getIds().get(0).getRoot());
        assertEquals("receivedOrganization/name tulee olla " + to.getVastaanottajaNimi(), to.getVastaanottajaNimi(),
                informationRecipient.getIntendedRecipient().getReceivedOrganization().getNames().get(0).getContent()
                        .get(0));
    }

    @Test
    public void testLuoAuthor() throws Exception {
        String representedOrganizationKey = "author.assignedAuthor.representedOrganization.id";
        KantaCDATestUtils.mockCodeProperty(representedOrganizationKey, testRoot, testExtension);
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        UusimispyyntoTO to = setupTestiUusimispyyntoTO();
        // sanity check - varmistetaan että uusija on null
        to.setUusija(null);
        POCDMT000040Author author = Whitebox.invokeMethod(tested, "luoAuthor", to);
        assertNotNull("author ei voi olla null.", author);
        assertFalse("author nullFlavor ei voi olla tyhjä.", author.getNullFlavors().isEmpty());
        assertEquals("author nullFlavor tulee olla " + KantaCDAConstants.NullFlavor.NA.getCode(),
                KantaCDAConstants.NullFlavor.NA.getCode(), author.getNullFlavors().get(0));
        assertNotNull("author/assignedAuthor ei ola null.", author.getAssignedAuthor());
        assertFalse("assignedAuthor/id ei voi olla tyhjä", author.getAssignedAuthor().getIds().isEmpty());
        assertFalse("id nullFlavor ei voi olla tyhjä.",
                author.getAssignedAuthor().getIds().get(0).getNullFlavors().isEmpty());
        assertEquals("id nullFlavor tulee olla " + KantaCDAConstants.NullFlavor.NA.getCode(),
                KantaCDAConstants.NullFlavor.NA.getCode(),
                author.getAssignedAuthor().getIds().get(0).getNullFlavors().get(0));
        assertNotNull("assignedAuthor/representedOrganization ei voi olla null.",
                author.getAssignedAuthor().getRepresentedOrganization());
        assertEquals("representedOrganization/id root tulee olla " + testRoot, testRoot,
                author.getAssignedAuthor().getRepresentedOrganization().getIds().get(0).getRoot());
        assertEquals("representedOrganization/id extension tulee olla " + testExtension, testExtension,
                author.getAssignedAuthor().getRepresentedOrganization().getIds().get(0).getExtension());
    }

    @Test
    public void testLuoAuthor_withUusija() throws Exception {
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        UusimispyyntoTO to = setupTestiUusimispyyntoTO();
        AmmattihenkiloTO uusija = KantaCDATestUtils.luoAmmattihenkilo();
        to.setUusija(uusija);
        POCDMT000040Author author = Whitebox.invokeMethod(tested, "luoAuthor", to);
        assertNotNull("author ei voi olla null.", author);
        assertTrue("author nullFlavor tulee olla tyhjä.", author.getNullFlavors().isEmpty());
        assertFalse("author typeCode ei voi olla tyhjä.", author.getTypeCodes().isEmpty());
        assertEquals("author typeCode tulee olla AUT", "AUT", author.getTypeCodes().get(0));
        assertNotNull("author/assignedAuthor ei ola null.", author.getAssignedAuthor());
        assertFalse("assignedAuthor/id ei voi olla tyhjä", author.getAssignedAuthor().getIds().isEmpty());
        assertTrue("id nullFlavor tulee olla tyhjä.",
                author.getAssignedAuthor().getIds().get(0).getNullFlavors().isEmpty());
        assertNotNull("assignedAuthor/representedOrganization ei voi olla null.",
                author.getAssignedAuthor().getRepresentedOrganization());
    }

    @Test
    public void testLisaaPuhnoRecordTargettiin() throws Exception {
        POCDMT000040ClinicalDocument doc = new POCDMT000040ClinicalDocument();
        POCDMT000040RecordTarget recordTarget = new POCDMT000040RecordTarget();
        POCDMT000040PatientRole patientRole = new POCDMT000040PatientRole();
        patientRole.setPatient(new POCDMT000040Patient());
        recordTarget.setPatientRole(patientRole);
        doc.getRecordTargets().add(recordTarget);
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        Whitebox.invokeMethod(tested, "lisaaPuhnoRecordTargettiin", doc, testPuhelinnumero);
        assertFalse("telecom ei voi olla tyhjä.",
                doc.getRecordTargets().get(0).getPatientRole().getTelecoms().isEmpty());
        assertFalse("telecom use ei voi olla tyhjä.",
                doc.getRecordTargets().get(0).getPatientRole().getTelecoms().get(0).getUses().isEmpty());
        assertEquals("telecom use tulee olla MC", "MC",
                doc.getRecordTargets().get(0).getPatientRole().getTelecoms().get(0).getUses().get(0));
        assertEquals("telecom value tulee olla " + KantaCDAConstants.TEL_PREFIX + testPuhelinnumero,
                KantaCDAConstants.TEL_PREFIX + testPuhelinnumero,
                doc.getRecordTargets().get(0).getPatientRole().getTelecoms().get(0).getValue());
    }

    @Test
    public void testLisaaPuhnoRecordTargettiin_noPatient() throws Exception {
        POCDMT000040ClinicalDocument doc = new POCDMT000040ClinicalDocument();
        POCDMT000040RecordTarget recordTarget = new POCDMT000040RecordTarget();
        POCDMT000040PatientRole patientRole = new POCDMT000040PatientRole();
        recordTarget.setPatientRole(patientRole);
        doc.getRecordTargets().add(recordTarget);
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        Whitebox.invokeMethod(tested, "lisaaPuhnoRecordTargettiin", doc, testPuhelinnumero);
        assertTrue("telecom tulee olla tyhjä.", doc.getRecordTargets().get(0).getPatientRole().getTelecoms().isEmpty());
    }

    @Test
    public void testLisaaPuhnoRecordTargettiin_invalidPuhelinnumero() throws Exception {
        POCDMT000040ClinicalDocument doc = new POCDMT000040ClinicalDocument();
        POCDMT000040RecordTarget recordTarget = new POCDMT000040RecordTarget();
        POCDMT000040PatientRole patientRole = new POCDMT000040PatientRole();
        recordTarget.setPatientRole(patientRole);
        doc.getRecordTargets().add(recordTarget);
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        Whitebox.invokeMethod(tested, "lisaaPuhnoRecordTargettiin", doc, "");
        assertTrue("telecom tulee olla tyhjä.", doc.getRecordTargets().get(0).getPatientRole().getTelecoms().isEmpty());
    }

    @Test
    public void testGetRecordTargetPatientRole() throws Exception {
        POCDMT000040ClinicalDocument doc = new POCDMT000040ClinicalDocument();
        POCDMT000040RecordTarget recordTarget = new POCDMT000040RecordTarget();
        POCDMT000040PatientRole patientRole1 = new POCDMT000040PatientRole();
        patientRole1.setPatient(new POCDMT000040Patient());
        recordTarget.setPatientRole(patientRole1);
        doc.getRecordTargets().add(recordTarget);
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        POCDMT000040PatientRole patientRole = Whitebox.invokeMethod(tested, "getRecordTargetPatientRole", doc);
        assertNotNull("patientRole ei voi olla null.", patientRole);
    }

    @Test
    public void testGetRecordTargetPatientRole_noClinicalDocument() throws Exception {
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        POCDMT000040PatientRole patientRole = Whitebox.invokeMethod(tested, "getRecordTargetPatientRole",
                Matchers.isNull(POCDMT000040ClinicalDocument.class));
        assertNull("patientRole tulee olla null.", patientRole);
    }

    @Test
    public void testGetRecordTargetPatientRole_noRecordTargets() throws Exception {
        POCDMT000040ClinicalDocument doc = new POCDMT000040ClinicalDocument();
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        POCDMT000040PatientRole patientRole = Whitebox.invokeMethod(tested, "getRecordTargetPatientRole", doc);
        assertNull("patientRole tulee olla null.", patientRole);
    }

    @Test
    public void testGetRecordTargetPatientRole_noPatientRole() throws Exception {
        POCDMT000040ClinicalDocument doc = new POCDMT000040ClinicalDocument();
        doc.getRecordTargets().add(new POCDMT000040RecordTarget());
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        POCDMT000040PatientRole patientRole = Whitebox.invokeMethod(tested, "getRecordTargetPatientRole", doc);
        assertNull("patientRole tulee olla null.", patientRole);
    }

    @Test
    public void testGetRecordTargetPatientRole_noPatient() throws Exception {
        POCDMT000040ClinicalDocument doc = new POCDMT000040ClinicalDocument();
        POCDMT000040RecordTarget recordTarget = new POCDMT000040RecordTarget();
        POCDMT000040PatientRole patientRole1 = new POCDMT000040PatientRole();
        recordTarget.setPatientRole(patientRole1);
        doc.getRecordTargets().add(recordTarget);
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        POCDMT000040PatientRole patientRole = Whitebox.invokeMethod(tested, "getRecordTargetPatientRole", doc);
        assertNull("patientRole tulee olla null.", patientRole);
    }

    @Test
    public void testValidoiLaakemaarays() throws ParseException {
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        tested.validoiLaakemaarays();
    }

    @Test
    public void testKasaaReseptiAsiakirja() throws ParseException, JAXBException {
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        String cda = tested.kasaaReseptiAsiakirja();
        assertNotNull("cda ei voi olla null.", cda);
        assertFalse("cda ei voi olla tyhjä.", cda.isEmpty());
    }

    @Test
    public void testLuoAsiakirjanMuutTiedot() throws ParseException {
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        tested.luoAsiakirjanMuutTiedot(new POCDMT000040Entry());
    }

    @Test
    public void testLuoAsiakirjakohtaisetRakenteet() throws ParseException {
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        tested.luoAsiakirjakohtaisetRakenteet(new LaakemaaraysTO());
    }

    @Test
    public void testLuoViittaukset() throws ParseException {
        UusimispyyntoKasaaja tested = setupUusimispyyntoKasaaja();
        tested.luoViittaukset(new LaakemaaraysTO());
    }
}
