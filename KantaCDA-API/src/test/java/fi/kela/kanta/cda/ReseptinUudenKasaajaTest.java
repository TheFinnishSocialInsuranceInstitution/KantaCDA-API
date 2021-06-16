package fi.kela.kanta.cda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import javax.xml.bind.JAXBException;

import org.hl7.v3.CD;
import org.hl7.v3.IVLPQ;
import org.hl7.v3.IVLTS;
import org.hl7.v3.IVXBPQ;
import org.hl7.v3.ObjectFactory;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component4;
import org.hl7.v3.POCDMT000040Component5;
import org.hl7.v3.POCDMT000040Consumable;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040EntryRelationship;
import org.hl7.v3.POCDMT000040Observation;
import org.hl7.v3.POCDMT000040Participant2;
import org.hl7.v3.POCDMT000040Product;
import org.hl7.v3.POCDMT000040Reference;
import org.hl7.v3.POCDMT000040Subject;
import org.hl7.v3.SXCMTS;
import org.hl7.v3.StrucDocParagraph;
import org.hl7.v3.StrucDocText;
import org.hl7.v3.XActRelationshipEntryRelationship;
import org.hl7.v3.XDocumentSubstanceMood;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import fi.kela.kanta.to.AnnostelukaudenPituusTO;
import fi.kela.kanta.to.ApteekissaValmistettavaLaakeTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.MuuAinesosaTO;
import fi.kela.kanta.to.ValmisteTO;
import fi.kela.kanta.to.ValmisteenYksilointitiedotTO;
import fi.kela.kanta.util.LMTOKasaaja;
import fi.kela.kanta.util.OidGenerator;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*" })
public class ReseptinUudenKasaajaTest extends ReseptiKasaajaCDATest {

    private static LaakemaaraysTO leimakentta;

    @BeforeClass
    public static void init() {
        ReseptinUudenKasaajaTest.leimakentta = new LaakemaaraysTO();
        // ReseptinUudenKasaajaTest.leimakentta.setOrgRootOid("537");
        // ReseptinUudenKasaajaTest.leimakentta.setOrgYTunnus("1234567-8");
    }

    @Override
    protected void setupProperties() {
        KantaCDATestUtils.setupProperties();
    }

    // Testissä tarkoituksella suppressoitu
    @SuppressWarnings("deprecation")
    private ReseptinUudenKasaaja setupReseptinUudenKasaaja(LaakemaaraysTO resepti) {
        KantaCDATestUtils.mockProperty(Kasaaja.BASE_OID_PROPERTY, KantaCDATestUtils.testBaseId);
        OidGenerator.resetSequence();
        return new ReseptinUudenKasaaja(KantaCDATestUtils.getProperties(), resepti);
    }

    @Test
    public void testKasaaReseptiApt() throws JAXBException {

        LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
        laakemaaraysTO.setApteekissaValmistettavaLaake(true);
        laakemaaraysTO.setApteekissaValmistettavaLaake(new ApteekissaValmistettavaLaakeTO());
        laakemaaraysTO.getApteekissaValmistettavaLaake().setValmistusohje("valmistusohje");
        laakemaaraysTO.getValmiste().getYksilointitiedot().setKauppanimi(LMTOKasaaja.testKauppanimi);
        // laakemaaraysTO.setOrgYTunnus("1234567-8");
        ReseptinUudenKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        String cda = tested.kasaaReseptiAsiakirja();
        Assert.assertNotNull(cda);
    }

    /*
     * @Test public void testKasaaReseptinKorjaus() { ReseptiKasaaja tested = setupReseptinUudenKasaaja();
     * LaakemaarayksenKorjausTO korjaus = new LaakemaarayksenKorjausTO(); Assert.assertNull(
     * "annosjakelun tulee olla null",korjaus.isAnnosjakelu()); korjaus.setKorjaaja(utils.luoAmmattihenkilo());
     * korjaus.getKorjaaja().setOrganisaatio(utils.luoOrganisaatio()); korjaus.setKorjauksenSyyKoodi("5");
     * korjaus.setKorjauksenPerustelu(testPerustelu); korjaus.setPakkauksienLukumaara(80); String cda =
     * tested.kasaaReseptinKorjaus(korjaus, luoLaakemaarays()); Assert.assertNotNull(cda);
     * System.out.println("Korjaus:"+cda); }
     */

    @Test
    public void testKasaaResepti() throws Exception {
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(luoLaakemaarays());
        String cda = tested.kasaaReseptiAsiakirja();
        Assert.assertNotNull(cda);
    }

    @Test
    public void testKasaaResepti_HetutonPotilas() throws Exception {
        LaakemaaraysTO laakemaarays = luoLaakemaarays();
        laakemaarays.getPotilas().setHetu(null);
        laakemaarays.getPotilas().setSukupuoli(new Integer(1));

        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaarays);
        String cda = tested.kasaaReseptiAsiakirja();
        Assert.assertNotNull("CDA ei voi olla null", cda);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testKasaaResepti_HetutonPotilas_Sukupuoli_null() throws Exception {
        LaakemaaraysTO laakemaarays = luoLaakemaarays();
        laakemaarays.getPotilas().setHetu(null);
        laakemaarays.getPotilas().setSukupuoli(null);

        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaarays);
        tested.kasaaReseptiAsiakirja();
    }

    @Test
    public void testLuoNarrativeLaakemaarays() throws Exception {
        LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
        laakemaaraysTO.getValmiste().getYksilointitiedot().setValmisteenLaji("test");
        laakemaaraysTO.setApteekissaValmistettavaLaake(true);
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        StrucDocText text = Whitebox.invokeMethod(tested, "luoNarrativeLaakemaarays", laakemaaraysTO);
        Assert.assertEquals(4, text.getContent().size());
    }

    @Test
    public void testLuoNarrativeLaakemaaraysMinimal() throws Exception {
        LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
        laakemaaraysTO.getValmiste().getYksilointitiedot().setKauppanimi(LMTOKasaaja.testKauppanimi);
        laakemaaraysTO.getValmiste().getYksilointitiedot().setValmisteenLaji("test");
        laakemaaraysTO.getValmiste().getYksilointitiedot().setVahvuus(null);
        laakemaaraysTO.getValmiste().getKayttotavat().clear();
        laakemaaraysTO.setAnnostusohje("");
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        StrucDocText text = Whitebox.invokeMethod(tested, "luoNarrativeLaakemaarays", laakemaaraysTO);
        Assert.assertEquals(1, text.getContent().size());
    }

    @Test
    public void testLuoNarrativePaikkaPvmLaakari() throws Exception {
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(null);
        StrucDocText text = Whitebox.invokeMethod(tested, "luoNarrativePaikkaPvmLaakari", luoLaakemaarays(),
                "11.3.2015");
        Assert.assertEquals(3, text.getContent().size());
    }

    @Test
    public void testLuoParagraphContent() throws Exception {
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(null);
        String testContent = "TestContent";
        StrucDocParagraph paragraph = Whitebox.invokeMethod(tested, "luoParagraphContent", testContent);
        Assert.assertEquals(1, paragraph.getContent().size());
    }

    @Test
    public void testLisataankoVaikuttavatAineet() throws Exception {
        LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
//        laakemaaraysTO.setApteekissaValmistettavaLaake(true);
        laakemaaraysTO.setValmiste(new ValmisteTO());
        laakemaaraysTO.getValmiste().setYksilointitiedot(new ValmisteenYksilointitiedotTO());
        laakemaaraysTO.getValmiste().getYksilointitiedot().setValmisteenLaji("7");
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        Assert.assertTrue((Boolean) Whitebox.invokeMethod(tested, "lisataankoVaikuttavatAineet", laakemaaraysTO));
        laakemaaraysTO = luoLaakemaarays();
        laakemaaraysTO.getValmiste().getYksilointitiedot().setKauppanimi(LMTOKasaaja.testKauppanimi);
        laakemaaraysTO.getValmiste().getYksilointitiedot().setValmisteenLaji(LMTOKasaaja.testValmisteenLaji2);
        Assert.assertFalse((Boolean) Whitebox.invokeMethod(tested, "lisataankoVaikuttavatAineet", laakemaaraysTO));
        laakemaaraysTO = luoLaakemaarays();
        laakemaaraysTO.getValmiste().getYksilointitiedot().setKauppanimi(LMTOKasaaja.testKauppanimi);
        Assert.assertTrue((Boolean) Whitebox.invokeMethod(tested, "lisataankoVaikuttavatAineet", laakemaaraysTO));
        laakemaaraysTO = luoLaakemaarays();
        Assert.assertFalse((Boolean) Whitebox.invokeMethod(tested, "lisataankoVaikuttavatAineet", laakemaaraysTO));
    }

    @Test
    public void testOnkoLaaketietokannanValmiste() throws Exception {
        LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        Assert.assertFalse((Boolean) Whitebox.invokeMethod(tested, "onkoLaaketietokannanValmiste", laakemaaraysTO));
        laakemaaraysTO = luoLaakemaarays();
        laakemaaraysTO.getValmiste().getYksilointitiedot().setValmisteenLaji("1");
        Assert.assertTrue((Boolean) Whitebox.invokeMethod(tested, "onkoLaaketietokannanValmiste", laakemaaraysTO));
        laakemaaraysTO = luoLaakemaarays();
        laakemaaraysTO.getValmiste().getYksilointitiedot().setValmisteenLaji("2");
        Assert.assertTrue((Boolean) Whitebox.invokeMethod(tested, "onkoLaaketietokannanValmiste", laakemaaraysTO));
        laakemaaraysTO = luoLaakemaarays();
        laakemaaraysTO.getValmiste().getYksilointitiedot().setValmisteenLaji("3");
        Assert.assertTrue((Boolean) Whitebox.invokeMethod(tested, "onkoLaaketietokannanValmiste", laakemaaraysTO));
        laakemaaraysTO = luoLaakemaarays();
        laakemaaraysTO.getValmiste().getYksilointitiedot().setValmisteenLaji("4");
        Assert.assertTrue((Boolean) Whitebox.invokeMethod(tested, "onkoLaaketietokannanValmiste", laakemaaraysTO));

    }

    @Test
    public void testLuoComponent() throws Exception {
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(null);
        POCDMT000040Component5 component = Whitebox.invokeMethod(tested, "luoComponent",
                ReseptinUudenKasaajaTest.leimakentta);
        Assert.assertNotNull(component.getSection());
        Assert.assertNotNull(component.getSection().getAttributeID());
        Assert.assertNotNull(component.getSection().getId().getRoot());
        Assert.assertEquals(tested.getId(ReseptinUudenKasaajaTest.leimakentta),
                component.getSection().getId().getRoot());
    }

    @Test
    public void testLuoMuutTiedot() throws Exception {
        LaakemaaraysTO laakemaarays = luoLaakemaarays();
        laakemaarays.setViestiApteekille("viestiApteekille");
        laakemaarays.setPKVlaakemaarays("pKVlaakemaarays");
        laakemaarays.setUudistamiskielto(true);
        laakemaarays.setUusimiskiellonSyy("3");
        laakemaarays.setUusimiskiellonPerustelu("uusimiskiellonPerustelu");
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaarays);
        POCDMT000040Entry entry = Whitebox.invokeMethod(tested, "luoMuutTiedot", laakemaarays);
        Assert.assertNotNull(entry);
        Assert.assertNotNull(entry.getOrganizer());
        // Assert.assertEquals("88",entry.getOrganizer().getCode().getCode());
    }

    @Test
    public void testLuoErillisselvitys() throws Exception {

        LaakemaaraysTO laakemaarays = new LaakemaaraysTO();
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaarays);
        Collection<POCDMT000040Component4> retval = Whitebox.invokeMethod(tested, "luoErillisselvitys", laakemaarays);
        Assert.assertEquals("erillisslevitystä ei luotu - koko on 0", 0, retval.size());

        laakemaarays.setErillisselvitys("erillisselvitys");
        retval = Whitebox.invokeMethod(tested, "luoErillisselvitys", laakemaarays);
        Assert.assertEquals("erillisslevitystä ei luotu - koko on 0", 0, retval.size());

        laakemaarays = new LaakemaaraysTO();
        laakemaarays.setErillisselvitysteksti("erillisselvitysteksti");
        retval = Whitebox.invokeMethod(tested, "luoErillisselvitys", laakemaarays);
        Assert.assertEquals("erillisslevitystä ei luotu - koko on 0", 0, retval.size());

        laakemaarays = new LaakemaaraysTO();
        laakemaarays.setErillisselvitys("erillisselvitys");
        laakemaarays.setErillisselvitysteksti("erillisselvitysteksti");
        retval = Whitebox.invokeMethod(tested, "luoErillisselvitys", laakemaarays);
        Assert.assertEquals("erillisslevitystä on luotu - koko on 1", 1, retval.size());
    }

    @Test
    public void testLuoAlle12VuotiaanPaino() throws Exception {

        LaakemaaraysTO laakemaarays = new LaakemaaraysTO(false);
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaarays);
        Collection<POCDMT000040Component4> retval = Whitebox.invokeMethod(tested, "luoAlle12VuotiaanPaino",
                laakemaarays);
        Assert.assertEquals("alle 12v painoa ei luotu - koko on 0", 0, retval.size());

        laakemaarays.setAlle12VuotiaanPainoUnit("alle12VuotiaanPainoUnit");
        retval = Whitebox.invokeMethod(tested, "luoAlle12VuotiaanPaino", laakemaarays);
        Assert.assertEquals("alle 12v painoa ei luotu - koko on 0", 0, retval.size());

        laakemaarays = new LaakemaaraysTO();
        laakemaarays.setAlle12VuotiaanPainoValue(new BigDecimal(53.5));
        retval = Whitebox.invokeMethod(tested, "luoAlle12VuotiaanPaino", laakemaarays);
        Assert.assertEquals("alle 12v painoa ei luotu - koko on 0", 0, retval.size());

        laakemaarays = new LaakemaaraysTO();
        laakemaarays.setAlle12VuotiaanPainoUnit("kg");
        laakemaarays.setAlle12VuotiaanPainoValue(new BigDecimal(23.5f));
        retval = Whitebox.invokeMethod(tested, "luoAlle12VuotiaanPaino", laakemaarays);
        Assert.assertEquals("alle 12v painoa on luotu - koko on 1", 1, retval.size());
    }

    @Test
    public void testLuoBLComponent() throws Exception {
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(null);
        Whitebox.invokeMethod(tested, "luoBLComponent", KantaCDATestUtils.testCode, true);
    }

    @Test
    public void testLuoAnnostus() throws Exception {
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(null);
        Whitebox.invokeMethod(tested, "luoAnnostus", luoLaakemaarays());
    }

    @Test
    public void testLuoMuutAinesosat() throws Exception {
        LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
        laakemaaraysTO.setApteekissaValmistettavaLaake(true);
        ApteekissaValmistettavaLaakeTO apteekissaValmistettavaLaakeTO = new ApteekissaValmistettavaLaakeTO();
        MuuAinesosaTO muuAinesosaTO = new MuuAinesosaTO();
        muuAinesosaTO.setAinesosanMaaraValue(10);
        muuAinesosaTO.setAinesosanMaaraUnit("io");
        muuAinesosaTO.setAinesosanMaaraTekstina("ainesosanmaara tekstina");
        muuAinesosaTO.setNimi(LMTOKasaaja.testATCNimi);
        apteekissaValmistettavaLaakeTO.getMuutAinesosat().add(muuAinesosaTO);
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        Whitebox.invokeMethod(tested, "luoMuutAinesosat", laakemaaraysTO);
    }

    @Test
    public void testLuoVaikuttavatAinesosat() throws Exception {
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(null);
        Whitebox.invokeMethod(tested, "luoVaikuttavatAinesosat", luoLaakemaarays());
    }

    @Test
    public void testLuoVaikuttavaAine() throws Exception {
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(null);
        String vahvuus = "1";
        String yksikko = "mg";
        String vahvuusTeksti = "1 mg";
        String atcKoodi = "123ABC";
        String atcNimi = "AtcNimi";
        String koodaamatonNimi = "koodamatonNimi";
        String laaketietokannanVersio = "2015.005";
        POCDMT000040Component4 component = Whitebox.invokeMethod(tested, "luoVaikuttavaAine", "", "", vahvuusTeksti,
                atcKoodi, atcNimi, koodaamatonNimi, laaketietokannanVersio);
        Assert.assertNotNull(component.getSubstanceAdministration().getDoseQuantity().getTranslations().get(0)
                .getOriginalText().getContent().get(0));
        Assert.assertEquals(laaketietokannanVersio, component.getSubstanceAdministration().getConsumable()
                .getManufacturedProduct().getManufacturedLabeledDrug().getCode().getCodeSystemVersion());
        Assert.assertEquals(koodaamatonNimi, component.getSubstanceAdministration().getConsumable()
                .getManufacturedProduct().getManufacturedLabeledDrug().getName().getContent().get(0));

        component = Whitebox.invokeMethod(tested, "luoVaikuttavaAine", vahvuus, yksikko, vahvuusTeksti, atcKoodi, null,
                null, null);
        Assert.assertNotNull(component.getSubstanceAdministration().getDoseQuantity().getCenter().getValue());
        Assert.assertEquals(atcKoodi, component.getSubstanceAdministration().getConsumable().getManufacturedProduct()
                .getManufacturedLabeledDrug().getCode().getCode());

        component = Whitebox.invokeMethod(tested, "luoVaikuttavaAine", vahvuus, null, null, null, null, null, null);
        Assert.assertNotNull(component.getSubstanceAdministration().getConsumable().getManufacturedProduct()
                .getManufacturedLabeledDrug().getCode());
        Assert.assertNull(component.getSubstanceAdministration().getDoseQuantity());
        Assert.assertNull(component.getSubstanceAdministration().getConsumable().getManufacturedProduct()
                .getManufacturedLabeledDrug().getCode().getCode());
        Assert.assertNull(component.getSubstanceAdministration().getConsumable().getManufacturedProduct()
                .getManufacturedLabeledDrug().getCode().getCodeSystemVersion());
        Assert.assertNull(component.getSubstanceAdministration().getConsumable().getManufacturedProduct()
                .getManufacturedLabeledDrug().getName());
    }

    @Test
    public void testLuoValmisteenJaPakkauksenTiedot() throws Exception {
        String testEffectivetime = "201503151212";
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(null);
        LaakemaaraysTO maarays = luoLaakemaarays();
        POCDMT000040Entry entry = Whitebox.invokeMethod(tested, "luoValmisteenJaPakkauksenTiedot", maarays,
                testEffectivetime, maarays.getAmmattihenkilo());
        Assert.assertNotNull(entry);

    }

    @Test
    public void testLuoTyonantajaJaVakuutuslaitos() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        Collection<POCDMT000040Participant2> participants = Whitebox.invokeMethod(tested,
                "luoTyonantajaJaVakuutuslaitos", laakemaaraysTO);
        Assert.assertTrue("participants tulee olla tyhjä", participants.isEmpty());

        laakemaaraysTO.setTyonantaja("tyonantaja");
        laakemaaraysTO.setVakuutuslaitos("vakuutuslaitos");
        participants = Whitebox.invokeMethod(tested, "luoTyonantajaJaVakuutuslaitos", laakemaaraysTO);
        Assert.assertFalse("participants ei saa olla tyhjä", participants.isEmpty());
        Assert.assertEquals("participants koko pitää olla 2", 2, participants.size());
    }

    @Test
    public void testLuoParticipant() throws Exception {
        String testName = "nimi";
        String testType = "TYPE";
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(null);
        POCDMT000040Participant2 participant = Whitebox.invokeMethod(tested, "luoParticipant", testName, testType);
        Assert.assertNotNull("participant ei saa olla null", participant);
        Assert.assertEquals("participant/participantRole classCode pitää olla " + testType, testType,
                participant.getParticipantRole().getClassCodes().get(0));
        Assert.assertEquals("participant/participantRole/playingEntity/name pitää olla " + testName, testName,
                participant.getParticipantRole().getPlayingEntity().getNames().get(0).getContent().get(0));
    }

    @Test
    public void testLuoMaaraysPaiva() throws Exception {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        String testEffectivetime = "201503151212";
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        SXCMTS aika = Whitebox.invokeMethod(tested, "luoMaaraysPaiva", laakemaaraysTO, testEffectivetime);
        Assert.assertEquals("Ilman määräyspäivää ja voimassaolon loppuaikaa aika SXCMTS:n valuessa", testEffectivetime,
                aika.getValue());
        Assert.assertFalse("Ilman määräyspäivää ja voimassaolon loppuaikaa aika ei ole IVLTS tyyppinen",
                aika instanceof IVLTS);
        laakemaaraysTO.setMaarayspaiva(new Date());
        aika = Whitebox.invokeMethod(tested, "luoMaaraysPaiva", laakemaaraysTO, testEffectivetime);
        Assert.assertFalse("Ilman voimassaolon loppuaikaa ajan value ei ole tyhjä", aika.getValue().isEmpty());
        Assert.assertFalse("Ilman voimassaolon loppuaikaa aika ei ole IVLTS tyyppinen", aika instanceof IVLTS);
        laakemaaraysTO.setLaakemaarayksenVoimassaolonLoppuaika(new Date());
        aika = Whitebox.invokeMethod(tested, "luoMaaraysPaiva", laakemaaraysTO, testEffectivetime);
        Assert.assertTrue("Kun voimassaolon loppuaika on asetettu aika on IVLTS tyyppinen", aika instanceof IVLTS);
        Assert.assertFalse("Kun voimassaolon loppuaika on asetettu low value ei ole tyhjä",
                ((IVLTS) aika).getLow().getValue().isEmpty());
        Assert.assertFalse("Kun voimassaolon loppuaika on asetettu high value ei ole tyhjä",
                ((IVLTS) aika).getHigh().getValue().isEmpty());
    }

    @Test
    public void testLuoLaakemuoto() throws Exception {
        LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        Collection<POCDMT000040EntryRelationship> entries = Whitebox.invokeMethod(tested, "luoLaakemuoto",
                laakemaaraysTO);
        Assert.assertEquals(1, entries.size());
        laakemaaraysTO = luoLaakemaarays();
        laakemaaraysTO.setValmiste(null);
        entries = Whitebox.invokeMethod(tested, "luoLaakemuoto", laakemaaraysTO);
        Assert.assertEquals(0, entries.size());
    }

    @Test
    public void testLuoSailytysastia() throws Exception {
        LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
        laakemaaraysTO.getValmiste().getYksilointitiedot().setSailytysastia("sailytysastia");
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        Collection<POCDMT000040EntryRelationship> entries = Whitebox.invokeMethod(tested, "luoSailytysastia",
                laakemaaraysTO);
        Assert.assertEquals(1, entries.size());
    }

    @Test
    public void testLuoValmisteenlaji() throws Exception {
        LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        Whitebox.invokeMethod(tested, "luoValmisteenlaji", laakemaaraysTO);
    }

    @Test
    public void testLuoMyyntiluvanHaltija() throws Exception {
        LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
        laakemaaraysTO.getValmiste().getYksilointitiedot().setMyyntiluvanHaltija("myyntiluvanHaltija");
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        Collection<POCDMT000040Participant2> participants = Whitebox.invokeMethod(tested, "luoMyyntiluvanHaltija",
                laakemaaraysTO);
        Assert.assertEquals(1, participants.size());
    }

    @Test
    public void testLuoViittaukset() throws Exception {
        String testSetId = "testSetId";
        LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        Collection<POCDMT000040Reference> references = Whitebox.invokeMethod(tested, "luoViittaukset", laakemaaraysTO);
        Assert.assertEquals(tested.getDocumentId(ReseptinUudenKasaajaTest.leimakentta),
                references.iterator().next().getExternalDocument().getSetId().getRoot());
        laakemaaraysTO.setSetId(testSetId);
        references = Whitebox.invokeMethod(tested, "luoViittaukset", laakemaaraysTO);
        Assert.assertEquals(testSetId, references.iterator().next().getExternalDocument().getSetId().getRoot());
    }

    @Test
    public void testLuoIterointi() throws Exception {
        LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
        int iterointienMaara = 10;
        int iterointienValiValue = 4;
        String iterointiValiYksikko = "m";
        String iterointiTeksti = "iter deciens";
        String odotettuIterointiTeksti = "iter deciens 4 m";
        laakemaaraysTO.setIterointienMaara(iterointienMaara);
        laakemaaraysTO.setIterointienValiValue(iterointienValiValue);
        laakemaaraysTO.setIterointienValiUnit(iterointiValiYksikko);
        laakemaaraysTO.setIterointiTeksti(iterointiTeksti);
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        Collection<POCDMT000040EntryRelationship> entries = Whitebox.invokeMethod(tested, "luoIterointi",
                laakemaaraysTO);
        assertEquals("iterointi entryjä tulee palautua vain 1", 1, entries.size());
        POCDMT000040EntryRelationship entry = entries.iterator().next();
        assertNotNull("entry ei voi olla null", entry);
        assertEquals("text tulee olla: " + odotettuIterointiTeksti, odotettuIterointiTeksti,
                entry.getObservation().getText().getContent().get(0));
        assertEquals("repeatNumber tulee olla: " + iterointienMaara, iterointienMaara,
                entry.getObservation().getRepeatNumber().getValue().intValue());

        IVLTS effectiveTime = entry.getObservation().getEffectiveTime();
        assertNotNull("observation/effectiveTime ei voi olla null", effectiveTime);
        assertNull("effectiveTime high tulee olla null", effectiveTime.getHigh());
        assertNull("effectiveTime low tulee olla null", effectiveTime.getLow());
        assertNull("effectiveTime value tulee olla null", effectiveTime.getValue());
        assertNotNull("effectiveTime width ei voi olla null", effectiveTime.getWidth());
        assertEquals("effectiveTime/width unit tulee olla: " + iterointiValiYksikko, iterointiValiYksikko,
                effectiveTime.getWidth().getUnit());
        assertEquals("effectiveTime/width value tulee olla: " + iterointienValiValue,
                String.valueOf(iterointienValiValue), effectiveTime.getWidth().getValue());
    }

    @Test
    public void testLuoIterointi_ilman_toimitusvalia() throws Exception {
        LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
        laakemaaraysTO.setIterointienMaara(2);
        laakemaaraysTO.setIterointienValiUnit(null);
        laakemaaraysTO.setIterointienValiValue(null);
        laakemaaraysTO.setIterointiTeksti("iter bis");
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        Collection<POCDMT000040EntryRelationship> entries = Whitebox.invokeMethod(tested, "luoIterointi",
                laakemaaraysTO);
        assertEquals("iterointi entryjä tulee palautua vain 1", 1, entries.size());
        POCDMT000040EntryRelationship entry = entries.iterator().next();
        assertNotNull("entry ei voi olla null", entry);
        IVLTS effectiveTime = entry.getObservation().getEffectiveTime();
        assertNotNull("observation/effectiveTime ei voi olla null", effectiveTime);
        assertNull("effectiveTime high tulee olla null", effectiveTime.getHigh());
        assertNull("effectiveTime low tulee olla null", effectiveTime.getLow());
        assertNull("effectiveTime value tulee olla null", effectiveTime.getValue());
        assertNull("effectiveTime width tulee olla null", effectiveTime.getWidth());
    }

    @Test
    public void testLuoLaaketietokannanlkopuolinenValmiste() throws Exception {
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(null);
        Whitebox.invokeMethod(tested, "luoLaaketietokannanlkopuolinenValmiste", "laaketietokannaulkopuolinenvalmiste");
    }

    @Test
    public void testLuoValmiste() throws Exception {
        LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        POCDMT000040Consumable consumable = Whitebox.invokeMethod(tested, "luoValmiste", laakemaaraysTO);
        Assert.assertEquals(LMTOKasaaja.testATCNimi,
                consumable.getManufacturedProduct().getManufacturedLabeledDrug().getCode().getDisplayName());

        String testLaaketietokannanUlkopuolinenValmiste = "laaketietokannanUlkopuolinenValmiste";
        laakemaaraysTO.setLaaketietokannanUlkopuolinenValmiste(testLaaketietokannanUlkopuolinenValmiste);
        laakemaaraysTO.getValmiste().getYksilointitiedot().setATCkoodi(null);
        consumable = Whitebox.invokeMethod(tested, "luoValmiste", laakemaaraysTO);
        Assert.assertEquals(testLaaketietokannanUlkopuolinenValmiste,
                consumable.getManufacturedProduct().getManufacturedMaterial().getName().getContent().get(0));
        Assert.assertEquals("NI",
                consumable.getManufacturedProduct().getManufacturedMaterial().getCode().getNullFlavors().get(0));

        laakemaaraysTO.setValmiste(null);
        consumable = Whitebox.invokeMethod(tested, "luoValmiste", laakemaaraysTO);
        Assert.assertEquals(testLaaketietokannanUlkopuolinenValmiste,
                consumable.getManufacturedProduct().getManufacturedMaterial().getName().getContent().get(0));

        laakemaaraysTO.setLaaketietokannanUlkopuolinenValmiste(null);
        consumable = Whitebox.invokeMethod(tested, "luoValmiste", laakemaaraysTO);
        Assert.assertNull(consumable);
    }

    @Test
    public void testLuoVNRKoodi_laaketietokannanValmiste_ei_VNR_numeroa() throws Exception {
        LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
        laakemaaraysTO.getValmiste().getYksilointitiedot().setValmisteenLaji(LMTOKasaaja.testValmisteenLaji2);
        laakemaaraysTO.getValmiste().getYksilointitiedot().setKauppanimi(LMTOKasaaja.testKauppanimi);
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        POCDMT000040Product product = Whitebox.invokeMethod(tested, "luoVNRKoodi", laakemaaraysTO);
        Assert.assertEquals(LMTOKasaaja.testKauppanimi,
                product.getManufacturedProduct().getManufacturedLabeledDrug().getName().getContent().get(0));
    }

    @Test
    public void testLuoVNRKoodi_laaketietokannanValmiste_VNR_numerolla() throws Exception {
        LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
        laakemaaraysTO.getValmiste().getYksilointitiedot().setValmisteenLaji(LMTOKasaaja.testValmisteenLaji2);
        laakemaaraysTO.getValmiste().getYksilointitiedot().setKauppanimi(LMTOKasaaja.testKauppanimi);
        laakemaaraysTO.getValmiste().getYksilointitiedot().setYksilointitunnus(LMTOKasaaja.testYksilointitunnus);
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        POCDMT000040Product product = Whitebox.invokeMethod(tested, "luoVNRKoodi", laakemaaraysTO);
        Assert.assertNull(product.getManufacturedProduct().getManufacturedLabeledDrug().getCode().getCode());
    }

    @Test
    public void testLuoVNRKoodi_myyntiluvallinenValmiste() throws Exception {
        LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
        laakemaaraysTO = luoLaakemaarays();
        laakemaaraysTO.getValmiste().getYksilointitiedot().setValmisteenLaji("1");
        laakemaaraysTO.getValmiste().getYksilointitiedot().setKauppanimi(LMTOKasaaja.testKauppanimi);
        laakemaaraysTO.getValmiste().getYksilointitiedot().setYksilointitunnus(LMTOKasaaja.testYksilointitunnus);
        laakemaaraysTO.getValmiste().getYksilointitiedot().setTunnuksenTyyppi(1); // VNR-numero
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        POCDMT000040Product product = Whitebox.invokeMethod(tested, "luoVNRKoodi", laakemaaraysTO);
        Assert.assertEquals(LMTOKasaaja.testYksilointitunnus,
                product.getManufacturedProduct().getManufacturedLabeledDrug().getCode().getCode());
        Assert.assertEquals(LMTOKasaaja.testKauppanimi,
                product.getManufacturedProduct().getManufacturedLabeledDrug().getCode().getDisplayName());
    }

    @Test
    public void testLuoPotilaantiedot() throws Exception {
        LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        POCDMT000040Subject subject = Whitebox.invokeMethod(tested, "luoPotilaantiedot", laakemaaraysTO);
        Assert.assertEquals(KantaCDATestUtils.testHetu, subject.getRelatedSubject().getCode().getCode());
    }

    @Test
    public void testLuoLaakeaineenVahvuus() throws Exception {
        LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        IVLPQ doseQuantity = Whitebox.invokeMethod(tested, "luoLaakeaineenVahvuus", laakemaaraysTO);
        Assert.assertEquals(doseQuantity.getTranslations().get(0).getOriginalText().getContent().get(0),
                LMTOKasaaja.testVahvuus);
        laakemaaraysTO.getValmiste().getYksilointitiedot().setVahvuus(null);
        tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        doseQuantity = Whitebox.invokeMethod(tested, "luoLaakeaineenVahvuus", laakemaaraysTO);
        Assert.assertNull(doseQuantity);
    }

    @Test
    public void testAsetaObservation() throws Exception {
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(null);
        String key = "TESTKEY";
        CD cd = new CD();
        POCDMT000040Observation observation = new POCDMT000040Observation();
        Whitebox.invokeMethod(tested, "asetaObservation", key, cd, observation);
        Assert.assertEquals(key, observation.getCode().getCode());
    }

    @Ignore
    @Test
    public void testKasaaReseptiVaikuttavallaAineella() throws Exception {
        Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");

        LaakemaaraysTO laakemaaraysTO = teeLaakemaaraysVaikuttavallaAineella();
        ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
        String cda = tested.kasaaReseptiAsiakirja();
        KantaCDATestUtils.tallenna(cda);
    }

    @Ignore
    @Test
    public void testKasaaReseptiKokonaismaaralla() throws Exception {
        Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
        LaakemaaraysTO laakemaaraysTO = teeLaakemaaraysKokonaismaaralla();
        ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
        String cda = tested.kasaaReseptiAsiakirja();
        KantaCDATestUtils.tallenna(cda);
    }

    @Ignore
    @Test
    public void testKasaaReseptiYhdistelmaValmisteAjalleAnnosjakelu() throws Exception {
        Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
        LaakemaaraysTO laakemaaraysTO = teeLaakemaaraysYhdValmAjalleAnnos();
        ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
        String cda = tested.kasaaReseptiAsiakirja();
        KantaCDATestUtils.tallenna(cda);
        // Vaikuttavat ainesosat pielessä
    }

    @Ignore
    @Test
    public void testKasaaReseptiAptRakenteinenKoodattuTehdasvalmisteesta() throws Exception {
        Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
        LaakemaaraysTO laakemaaraysTO = teeLaakemaaraysAptRakentKoodTehdas();
        ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
        String cda = tested.kasaaReseptiAsiakirja();
        KantaCDATestUtils.tallenna(cda);
    }

    @Ignore
    @Test
    public void testKasaaReseptiPKV_iteroitu_pienempi_pakkaus() throws Exception {
        Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
        LaakemaaraysTO laakemaaraysTO = teeLaakemaaraysPKV_iteroitu_pienempi_pakkaus();
        ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
        String cda = tested.kasaaReseptiAsiakirja();
        KantaCDATestUtils.tallenna(cda);
    }

    @Ignore
    @Test
    public void testKasaaReseptiLaaketietokannan_ulkopuolinen_valmiste() throws Exception {
        Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
        LaakemaaraysTO laakemaaraysTO = teeLaakemaaraysLaaketietokannan_ulkopuolinen_valmiste();
        ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
        String cda = tested.kasaaReseptiAsiakirja();
        KantaCDATestUtils.tallenna(cda);
    }

    @Test
    public void testKasaaReseptiPKV_iteroitu_iterointivaliksi_0() throws IOException {
        Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
        LaakemaaraysTO laakemaaraysTO = teeLaakemaaraysPKV_iteroitu_pienempi_pakkaus();
        laakemaaraysTO.setIterointienValiUnit(null);
        laakemaaraysTO.setIterointienValiValue(0);
        laakemaaraysTO.setIterointiTeksti("iter bis");
        ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
        POCDMT000040ClinicalDocument doc = tested.kasaaReseptiCDA();
        assertNotNull("clinicalDocument ei voi olla null", doc);
        // /ClinicalDocument/component/structuredBody/component/section/component/section/component/section/entry[1]/organizer/component/substanceAdministration/entryRelationship/supply/entryRelationship[2]/observation/effectiveTime
        POCDMT000040Observation obs = doc.getComponent().getStructuredBody().getComponents().get(0).getSection()
                .getComponents().get(0).getSection().getComponents().get(0).getSection().getEntries().get(0)
                .getOrganizer().getComponents().get(0).getSubstanceAdministration().getEntryRelationships().get(0)
                .getSupply().getEntryRelationships().get(1).getObservation();
        assertEquals("observation code tulee olla 121", "121", obs.getCode().getCode());
        IVLTS effectiveTime = obs.getEffectiveTime();
        assertNotNull("observation effectiveTime ei voi olla null", effectiveTime);
        assertNull("effectiveTime value tulee olla null", effectiveTime.getValue());
        assertNull("effectiveTime high tulee olla null", effectiveTime.getHigh());
        assertNull("effectiveTime low tulee olla null", effectiveTime.getLow());
        assertNull("effectiveTime width tulee olla null", effectiveTime.getWidth());

        // String cda = tested.kasaaReseptiAsiakirja();
        // System.out.println(cda);
    }

    private LaakemaaraysTO teeLaakemaaraysVaikuttavallaAineella() {
        LaakemaaraysTO to = new LaakemaaraysTO();
        to.setPotilas(teePotilas());
        to.setAmmattihenkilo(teeAmmattihenkilo());
        to.getAmmattihenkilo().setOrganisaatio(teeOrganisaatio());

        to.setReseptintyyppi("1");
        to.setValmiste(teeValmisteVaikuttavallaAineella());
        to.setPakkauksienLukumaara(2);
        to.setApteekissaValmistettavaLaake(false);
        to.setAnnosteluPelkastaanTekstimuodossa(true);
        to.setAnnostusohje("1 tabletti korkeintaan 6 kertaa vuorokaudessa.");
        to.setSICmerkinta(true);
        to.setKayttotarkoitusTeksti("Tulehduskipuun.");
        to.getHoitolajit().add("S");
        to.setPysyvaislaakitys(false);
        to.setUudistamiskielto(false);
        to.setLaakevaihtokielto(false);
        to.setAnnosjakelu(false);
        to.setKyseessaLaakkeenkaytonAloitus(true);
        to.setHuume(false);
        to.setReseptinLaji("1");
        to.setLaaketietokannanVersio("2014.008");
        return to;
    }

    private LaakemaaraysTO teeLaakemaaraysKokonaismaaralla() {
        LaakemaaraysTO to = new LaakemaaraysTO();
        to.setPotilas(teePotilas());
        to.setAmmattihenkilo(teeAmmattihenkilo());
        to.getAmmattihenkilo().setOrganisaatio(teeOrganisaatio());

        to.setReseptintyyppi("2");
        to.setLaakkeenKokonaismaaraUnit("g");
        to.setLaakkeenKokonaismaaraValue(3000);
        to.setLaaketietokannanVersio("2014.008");
        to.setValmiste(teeValmisteKokonaismaaralla());
        to.setAnnosteluPelkastaanTekstimuodossa(true);
        to.setAnnostusohje("Käytetään säännöllisesti ihon rasvaukseen.");
        to.setKayttotarkoitusTeksti("Pitkäaikaisen ihotaudin hoitoon.");
        to.getHoitolajit().add("S");
        to.setPysyvaislaakitys(true);
        to.setReseptinLaji("1");
        return to;
    }

    private LaakemaaraysTO teeLaakemaaraysYhdValmAjalleAnnos() {
        LaakemaaraysTO to = new LaakemaaraysTO();
        to.setPotilas(teePotilas());
        to.setAmmattihenkilo(teeAmmattihenkilo());
        to.getAmmattihenkilo().setOrganisaatio(teeOrganisaatio());
        to.setReseptintyyppi("3");
        to.setValmiste(teeValmisteYhdValmAjalleAnnos());
        to.setAjalleMaaratynReseptinAikamaaraUnit("a");
        to.setAjalleMaaratynReseptinAikamaaraValue(1);
        to.setAjalleMaaratynReseptinAlkuaika(Calendar.getInstance(TimeZone.getTimeZone("EET")).getTime());
        to.setAnnosteluPelkastaanTekstimuodossa(true);
        to.setAnnostusohje("1 tabletti aamuisin.");
        to.setKayttotarkoitusTeksti("Verenpainelääke.");
        to.getHoitolajit().add("S");
        to.setPysyvaislaakitys(true);
        to.setAnnosjakelu(true);
        to.setReseptinLaji("1");
        to.setLaaketietokannanVersio("2014.008");

        return to;
    }

    private LaakemaaraysTO teeLaakemaaraysAptRakentKoodTehdas() {
        LaakemaaraysTO to = new LaakemaaraysTO();
        to.setPotilas(teePotilas());
        to.setAmmattihenkilo(teeAmmattihenkilo());
        to.getAmmattihenkilo().setOrganisaatio(teeOrganisaatio());
        to.setReseptintyyppi("2");
        to.setValmiste(teeValmisteAptRakentKoodTehdas());
        to.setApteekissaValmistettavaLaake(teeApteekissaValmistettavaRakentkoodTehdas());
        to.setLaakkeenKokonaismaaraValue(100);
        to.setLaakkeenKokonaismaaraUnit("g");
        to.setApteekissaValmistettavaLaake(true);
        to.setAnnosteluPelkastaanTekstimuodossa(true);
        to.setAnnostusohje("Ihottuma-alueelle kerran päivässä.");
        to.setKayttotarkoitusTeksti("Ihottuman hoitoon.");
        to.getHoitolajit().add("S");
        to.setKyseessaLaakkeenkaytonAloitus(true);
        to.setReseptinLaji("1");
        to.setLaaketietokannanVersio("2014.008");
        return to;
    }

    private LaakemaaraysTO teeLaakemaaraysPKV_iteroitu_pienempi_pakkaus() {
        LaakemaaraysTO to = new LaakemaaraysTO();
        to.setPotilas(teePotilas());
        to.setAmmattihenkilo(teeAmmattihenkilo());
        to.getAmmattihenkilo().setOrganisaatio(teeOrganisaatio());
        to.setLaatimispaikka(teeOrganisaatio());
        to.setReseptintyyppi("2");
        to.setValmiste(teeValmistePKV_iteroitu_pienempi_pakkaus());
        Calendar end = Calendar.getInstance();
        end.add(Calendar.MONTH, 6);
        to.setLaakemaarayksenVoimassaolonLoppuaika(end.getTime());
        to.setLaakkeenKokonaismaaraValue(21);
        to.setLaakkeenKokonaismaaraUnit("fol");
        to.setAnnostusohje("1 tabletti tarvittaessa 3 kertaa vuorokaudessa.");
        to.setKayttotarkoitusTeksti("Ahdistuneisuuden hoitoon.");
        to.getHoitolajit().add("S");
        to.setViestiApteekille("Toimitetaan viikon annoksissa väärinkäytön estämiseksi.");
        to.setPKVlaakemaarays("Z");
        to.setKyseessaLaakkeenkaytonAloitus(true);
        to.setReseptinLaji("1");
        to.setLaaketietokannanVersio("2015.005");
        to.setIterointienMaara(3);
        to.setIterointienValiUnit("d");
        to.setIterointienValiValue(7);
        to.setIterointiTeksti("iter ter");
        return to;
    }

    private LaakemaaraysTO teeLaakemaaraysLaaketietokannan_ulkopuolinen_valmiste() {
        LaakemaaraysTO to = new LaakemaaraysTO();
        to.setPotilas(teePotilas());
        to.setAmmattihenkilo(teeAmmattihenkilo());
        to.getAmmattihenkilo().setOrganisaatio(teeOrganisaatio());
        to.setReseptintyyppi("1");
        to.setValmiste(teeValmisteLaaketietokannanUlkopuolinenValmiste());
        to.setPakkauksienLukumaara(1);
        to.setAnnosteluPelkastaanTekstimuodossa(true);
        to.setAnnostusohje("Yksi purutabletti kerran päivässä.");
        to.setKayttotarkoitusTeksti("Turistiripulin ehkäisyyn.");
        to.getHoitolajit().add("S");
        to.setKyseessaLaakkeenkaytonAloitus(true);
        to.setReseptinLaji("1");
        to.setLaaketietokannanUlkopuolinenValmiste("RELATABS +D3 10 mikrog purutabletti");
        return to;
    }

    @Test
    public void testLuoIterointiUnitPv() throws Exception {
        LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
        int iterointienMaara = 10;
        int iterointienValiValue = 4;
        String iterointiValiYksikko = "d";
        String iterointiTeksti = "iter deciens";
        String odotettuIterointiTeksti = "iter deciens 4 pv";
        laakemaaraysTO.setIterointienMaara(iterointienMaara);
        laakemaaraysTO.setIterointienValiValue(iterointienValiValue);
        laakemaaraysTO.setIterointienValiUnit(iterointiValiYksikko);
        laakemaaraysTO.setIterointiTeksti(iterointiTeksti);
        ReseptiKasaaja tested = setupReseptinUudenKasaaja(laakemaaraysTO);
        Collection<POCDMT000040EntryRelationship> entries = Whitebox.invokeMethod(tested, "luoIterointi",
                laakemaaraysTO);
        assertEquals("iterointi entryjä tulee palautua vain 1", 1, entries.size());
        POCDMT000040EntryRelationship entry = entries.iterator().next();
        assertNotNull("entry ei voi olla null", entry);
        assertEquals("text tulee olla: " + odotettuIterointiTeksti, odotettuIterointiTeksti,
                entry.getObservation().getText().getContent().get(0));
        assertEquals("repeatNumber tulee olla: " + iterointienMaara, iterointienMaara,
                entry.getObservation().getRepeatNumber().getValue().intValue());

        IVLTS effectiveTime = entry.getObservation().getEffectiveTime();
        assertNotNull("observation/effectiveTime ei voi olla null", effectiveTime);
        assertNull("effectiveTime high tulee olla null", effectiveTime.getHigh());
        assertNull("effectiveTime low tulee olla null", effectiveTime.getLow());
        assertNull("effectiveTime value tulee olla null", effectiveTime.getValue());
        assertNotNull("effectiveTime width ei voi olla null", effectiveTime.getWidth());
        assertEquals("effectiveTime/width unit tulee olla: " + iterointiValiYksikko, iterointiValiYksikko,
                effectiveTime.getWidth().getUnit());
        assertEquals("effectiveTime/width value tulee olla: " + iterointienValiValue,
                String.valueOf(iterointienValiValue), effectiveTime.getWidth().getValue());
    }
    
    @Test
    public void testAnnostelukaudenpituus() throws Exception {
        Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        AnnostelukaudenPituusTO kaudenpituus = new AnnostelukaudenPituusTO();

        kaudenpituus.setVakio(42);
        kaudenpituus.setYksikko("d");

        ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
        POCDMT000040EntryRelationship entry = Whitebox.invokeMethod(tested, "luoJaksonPituus", kaudenpituus, KantaCDAConstants.Laakityslista.ANNOSTELUKAUDEN_KESTO_CODE);
        assertEquals("OBS", entry.getObservation().getClassCodes().get(0).toString());
        assertEquals("EVN", entry.getObservation().getMoodCode().toString());
        assertEquals(KantaCDAConstants.Laakityslista.ANNOSTELUKAUDEN_KESTO_CODE, entry.getObservation().getCode().getCode());
        assertEquals("1.2.246.537.6.12.2002.126", entry.getObservation().getCode().getCodeSystem());
        assertEquals("Lääkityslista", entry.getObservation().getCode().getCodeSystemName());
        assertEquals("annostelukauden kesto", entry.getObservation().getCode().getDisplayName());
        IVLPQ value = (IVLPQ)entry.getObservation().getValues().get(0);
        assertEquals("42", value.getWidth().getValue());
        assertEquals("d", value.getWidth().getUnit());
        
        kaudenpituus.setVakio(null);
        kaudenpituus.setLow(2);
        kaudenpituus.setHigh(22);
        entry = Whitebox.invokeMethod(tested, "luoJaksonPituus", kaudenpituus, KantaCDAConstants.Laakityslista.ANNOSTELUKAUDEN_KESTO_CODE);
        value = (IVLPQ)entry.getObservation().getValues().get(0);
        assertEquals("2", value.getLow().getValue());
        assertEquals("22", value.getHigh().getValue());
    }

    
    @Test
    public void testLuoAnnostelukausi_Annostelukaudenpituus() throws Exception {
        Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
        LaakemaaraysTO laakemaaraysTO = luoLaakemaaraysRakenteinenAnnostusKayttoohjeenLisatiedot();
        AnnostelukaudenPituusTO kaudenpituus = new AnnostelukaudenPituusTO();
        kaudenpituus.setVakio(42);
        kaudenpituus.setYksikko("d");
        laakemaaraysTO.setAnnostelukaudenPituus(kaudenpituus);
        ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
        POCDMT000040Component4 annostelukausi = Whitebox.invokeMethod(tested, "luoAnnostelukausiComponent", laakemaaraysTO);
        assertEquals("SBADM", annostelukausi.getSubstanceAdministration().getClassCodes().get(0).toString());
        assertEquals("EVN", annostelukausi.getSubstanceAdministration().getMoodCode().toString());

        boolean ok = false;
        for(POCDMT000040EntryRelationship entry : annostelukausi.getSubstanceAdministration().getEntryRelationships()) {
        	if(entry != null && entry.getObservation() != null && entry.getObservation().getCode() != null) {
        		if(entry.getObservation().getCode().getCode().equals(KantaCDAConstants.Laakityslista.ANNOSTELUKAUDEN_KESTO_CODE)) {
        	        assertEquals("COMP", entry.getTypeCode().toString());
        	        assertEquals("OBS", entry.getObservation().getClassCodes().get(0).toString());
        	        assertEquals("EVN", entry.getObservation().getMoodCode().toString());
        	        assertEquals("235", entry.getObservation().getCode().getCode());
        	        assertEquals("1.2.246.537.6.12.2002.126", entry.getObservation().getCode().getCodeSystem());
        	        assertEquals("Lääkityslista", entry.getObservation().getCode().getCodeSystemName());
        	        assertEquals("annostelukauden kesto", entry.getObservation().getCode().getDisplayName());
        	        IVLPQ value = (IVLPQ)entry.getObservation().getValues().get(0);
        	        assertEquals("42", value.getWidth().getValue());
        	        assertEquals("d", value.getWidth().getUnit());
        	        ok = true;
        		}
        	}
        }
		if(!ok) {
			fail("Annostelukaudella tulee olla annostelukaudenpituus entry");
		}
    }
    
    @Test
    public void testLuoAnnostusKayttohjeenLisatiedot() throws Exception {
        Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
        LaakemaaraysTO laakemaaraysTO = luoLaakemaaraysRakenteinenAnnostusKayttoohjeenLisatiedot();
        ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
        Whitebox.invokeMethod(tested, "luoAnnostus", laakemaaraysTO);
    }

    @Test
    public void testLuoRakenteinenAnnostusFysikaalinenAnnos() throws Exception {
        Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
        LaakemaaraysTO laakemaaraysTO = luoLaakemaaraysRakenteinenAnnostusFysikaalinenAnnos();
        ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
        String cda = tested.kasaaReseptiAsiakirja();
        Whitebox.invokeMethod(tested, "luoAnnostus", laakemaaraysTO);
        Assert.assertNotNull(cda);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testFysikaalinenAnnosKasaus() throws Exception {
    	Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
        LaakemaaraysTO laakemaaraysTO = luoLaakemaaraysRakenteinenAnnostusFysikaalinenAnnos();
        laakemaaraysTO.getAnnokset().get(0).setHighAnnos(100.5);
        ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
        String cda = tested.kasaaReseptiAsiakirja();
    }

    @Test
    public void testFysikaalinenAnnos() throws Exception {
    	ObjectFactory of = new ObjectFactory();
    	IVLPQ doseQ = of.createIVLPQ();
        IVXBPQ lowValue = of.createIVXBPQ();
    	lowValue.setUnit("mg");
    	lowValue.setValue("2.0");
    	doseQ.setLow(lowValue);

    	IVXBPQ highValue = of.createIVXBPQ();
    	highValue.setUnit("mg");
    	highValue.setValue("3.0");
    	doseQ.setHigh(highValue);

    	Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
        LaakemaaraysTO laakemaaraysTO = luoLaakemaaraysRakenteinenAnnostusFysikaalinenAnnos();
        ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
        POCDMT000040EntryRelationship entry = Whitebox.invokeMethod(tested, "luoFysikaalinenAnnos", doseQ);
        assertEquals(XActRelationshipEntryRelationship.COMP,entry.getTypeCode());
        assertEquals(XDocumentSubstanceMood.EVN,entry.getSubstanceAdministration().getMoodCode());
        assertEquals("SBADM",entry.getSubstanceAdministration().getClassCodes().get(0));
        assertEquals("241",entry.getSubstanceAdministration().getCode().getCode());
        assertEquals("Lääkityslista",entry.getSubstanceAdministration().getCode().getCodeSystemName());
        assertEquals("2.0", entry.getSubstanceAdministration().getDoseQuantity().getLow().getValue());
        assertEquals("3.0", entry.getSubstanceAdministration().getDoseQuantity().getHigh().getValue());
        assertEquals("NI", entry.getSubstanceAdministration().getConsumable().getManufacturedProduct().getManufacturedLabeledDrug().getNullFlavors().get(0));
        Assert.assertNotNull(entry);
    }
        
}
