package fi.kela.kanta.cda;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Reference;
import org.hl7.v3.XActRelationshipExternalReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.LaakemaarayksenKorjausTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.ValmisteTO;
import fi.kela.kanta.to.ValmisteenYksilointitiedotTO;
import fi.kela.kanta.util.LMTOKasaaja;
import junit.framework.TestCase;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Properties.class })
@PowerMockIgnore({ "javax.management.*" })
public class ReseptinUusimisKasaajaTest extends LMTOKasaaja {

    private static String testLMOid = "1.2.3.4.5.6";
    private static String testLMSetId = "1.2.3.4.5.6.7";
    private static String testUusimispyynnonOid = "1.2.3.4.5.U";
    private static String testUusimispyynnonSetId = "1.2.3.4.5.6.U";
    private static String testAlkuperainenOid = "1.2.3.4.5.A";
    private static String testAlkuperainenSetId = "1.2.3.4.5.6.A";
    private static int testAlkuperainenVersio = 5;
    private static int testAlkuperainenCdaTyyppi = 1;

    @Override
    protected void setupProperties() {
        KantaCDATestUtils.setupProperties();
    }

    /**
     * Alustaa Uusimiskasaajan testattavaksi
     *
     * @param uusi
     *            Lääkemääräys uusintaa varten
     * @param alkuperainenLaakemaarays
     *            Alkuperäinen lääkemääräys
     * @return Uusimiskasaaja
     */
    private ReseptinUusimisKasaaja setupReseptinUusimisKasaaja(LaakemaaraysTO uusi,
            LaakemaaraysTO alkuperainenLaakemaarays) {
        KantaCDATestUtils.mockProperty(Kasaaja.BASE_OID_PROPERTY, KantaCDATestUtils.testBaseId);
        try {
            return new ReseptinUusimisKasaaja(KantaCDATestUtils.loadProperties("testi_properties.properties"), uusi,
                    alkuperainenLaakemaarays, ReseptinUusimisKasaajaTest.testUusimispyynnonOid,
                    ReseptinUusimisKasaajaTest.testUusimispyynnonSetId);
        }
        catch (IOException e) {
            e.printStackTrace();
            TestCase.fail();
        }
        return null;
    }

    /**
     * Asettaa lääkemääräyksen cda:n muodostamisen kannalta tarpeelliset leimakentät
     *
     * @param laakemaaraysTO
     *            TO johon tiedot asetetaan
     */
    private void setupLaakemaarayksenLeimakentat(LaakemaaraysTO laakemaaraysTO) {
        laakemaaraysTO.setOid(ReseptinUusimisKasaajaTest.testAlkuperainenOid);
        laakemaaraysTO.setSetId(ReseptinUusimisKasaajaTest.testAlkuperainenSetId);
        laakemaaraysTO.setVersio(ReseptinUusimisKasaajaTest.testAlkuperainenVersio);
        laakemaaraysTO.setCdaTyyppi(ReseptinUusimisKasaajaTest.testAlkuperainenCdaTyyppi);
    }

    @Test
    public void testPaivataUusimisTiedot() throws Exception {
        LaakemaaraysTO alkuperainen = new LaakemaaraysTO();
        alkuperainen.setPakkauksienLukumaara(3);
        alkuperainen.setReseptintyyppi("Vastamyrkky");
        alkuperainen.setAnnostusohje("Ota tunnin välein, juo paljon");

        LaakemaaraysTO uusiminen = new LaakemaaraysTO();
        // uusiminen.setOrgYTunnus("1234567-8");
        uusiminen.setMaarayspaiva(new Date());
        setupLaakemaarayksenLeimakentat(uusiminen);
        uusiminen.setOid(null);
        uusiminen.setPakkauksienLukumaara(null);
        ReseptinUusimisKasaaja tested = setupReseptinUusimisKasaaja(uusiminen, alkuperainen);
        Assert.assertNotNull("Uudella lääkemääräyksellä pitäisi olla nyt oid", uusiminen.getOid());

        // Kutsuu tätä jo konstruktorissa
        // Whitebox.invokeMethod(tested, "paivataUusimisTiedot");

        // Assert.assertEquals("korjauksen alkuperainenCdaTyyppi pitää olla alkuperainenCdaTyyppi",
        // ReseptinUusimisKasaajaTest.testAlkuperainenCdaTyyppi, uusiminen.getAlkuperainenCdaTyyppi());
        Assert.assertEquals(new Integer(3), uusiminen.getPakkauksienLukumaara());
        Assert.assertEquals("Uuden lääkemääräyksen CdaTyyppi pitää olla sama kuin alkuperainen CdaTyyppi",
                ReseptinUusimisKasaajaTest.testAlkuperainenCdaTyyppi, uusiminen.getCdaTyyppi());
        Assert.assertEquals("Uuden lääkemääräyksen ReseptinTyyppi pitää olla sama kuin alkuperainen ReseptinTyyppi",
                "Vastamyrkky", uusiminen.getReseptintyyppi());
        Assert.assertEquals("Uuden lääkemääräyksen annostusohje pitää olla sama kuin alkuperainen annostusohje",
                "Ota tunnin välein, juo paljon", uusiminen.getAnnostusohje());
        // TODO: Voisi tarkistaa muitakin tietoja, tosin onko tietojen kopiointi kasaajassa yleensä järkevää?
    }

    @Test
    public void testKasaaReseptinUusiminen() throws JAXBException {
        LaakemaarayksenKorjausTO laakemaarayksenKorjausTO = new LaakemaarayksenKorjausTO();
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        setupLaakemaarayksenLeimakentat(laakemaaraysTO);
        laakemaaraysTO.setPotilas(
                new HenkilotiedotTO(new KokoNimiTO("potilasEtu", "potilasSuku"), KantaCDATestUtils.testHetu));
        laakemaaraysTO.setAmmattihenkilo(KantaCDATestUtils.luoAmmattihenkilo());
        laakemaaraysTO.getAmmattihenkilo().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        laakemaaraysTO.setValmiste(new ValmisteTO());
        laakemaaraysTO.getValmiste().setYksilointitiedot(new ValmisteenYksilointitiedotTO());
        laakemaaraysTO.getValmiste().getYksilointitiedot().setValmisteenLaji("1");
        laakemaaraysTO.getValmiste().getYksilointitiedot().setYksilointitunnus("yksilointitunnus");
        laakemaaraysTO.getValmiste().getYksilointitiedot().setKauppanimi("kauppanimi");
        laakemaaraysTO.setReseptintyyppi("reseptintyyppi");
        laakemaaraysTO.setMaarayspaiva(new Date());
        laakemaaraysTO.getHoitolajit().add("hoitolaji");
        laakemaarayksenKorjausTO.setKorjaaja(KantaCDATestUtils.luoAmmattihenkilo());
        laakemaarayksenKorjausTO.getKorjaaja().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        laakemaarayksenKorjausTO.setKorjauksenSyyKoodi("SYYKOODI");
        // laakemaarayksenKorjausTO.setOrgYTunnus("1234567-8");

        KantaCDATestUtils.mockCodeProperty("code", "1", "test_lm_codeSystem", "test_lm_codeSystemName",
                "test_lm_displayName");
        KantaCDATestUtils.mockCodeProperty("korjaus.code", "3", "test_korjaus_codeSystem",
                "test_korjaus_codeSystemName", "test_korjaus_displayName");

        ReseptinUusimisKasaaja tested = setupReseptinUusimisKasaaja(laakemaarayksenKorjausTO, laakemaaraysTO);
        String cda = tested.kasaaReseptiAsiakirja();
        Assert.assertNotNull("reseptin uusimisen cda ei saa olla null", cda);
        // System.out.println(cda);
    }

    @Test
    public void testGetPropertyCode() throws Exception {
        ReseptinUusimisKasaaja tested = setupReseptinUusimisKasaaja(null, null);

        Object[] args2 = { "uusiminen" };
        // Ei välitetä "uusiminen" avaimesta kun uusiminen on periaatteessa vain uusi lääkemääräys
        Assert.assertEquals("getPropertyCode tulee palauttaa :'code'", "code", Whitebox.invokeMethod(tested,
                "getPropertyCode", KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYS.getTyyppi(), args2));
    }

    @Test
    public void testLuoViittaukset() throws Exception {
        KantaCDATestUtils.mockCodeProperty("korjaus.code", "3", "1.2.246.537.5.40105.2006",
                "Sähköinen lääkemääräys - Reseptisanoman tyyppi", "Lääkemääräyksen korjaus");
        KantaCDATestUtils.mockCodeProperty("contentsCode", "1", "1.2.246.537.5.40105.2006",
                "Sähköinen lääkemääräys - Reseptisanoman tyyppi", "Lääkemääräys");

        LaakemaaraysTO uusiLM = new LaakemaaraysTO();
        uusiLM.setOid(ReseptinUusimisKasaajaTest.testLMOid);
        uusiLM.setSetId(ReseptinUusimisKasaajaTest.testLMSetId);
        ReseptinUusimisKasaaja tested = setupReseptinUusimisKasaaja(uusiLM, null);
        Collection<POCDMT000040Reference> viittaukset = Whitebox.invokeMethod(tested, "luoViittaukset", uusiLM);
        // Viittaukset itseensä (SPRT) ja uusimispyyntöön (REFR)
        Assert.assertEquals("viittauksien määrä pitää olla 2", 2, viittaukset.size());
        for (POCDMT000040Reference viittaus : viittaukset) {
            if ( viittaus.getTypeCode() == XActRelationshipExternalReference.SPRT ) {
                tarkistaViittaus(viittaus, ReseptinUusimisKasaajaTest.testLMOid, ReseptinUusimisKasaajaTest.testLMSetId,
                        "1");
            }
            else {
                TestCase.assertEquals(XActRelationshipExternalReference.REFR, viittaus.getTypeCode());
                tarkistaViittaus(viittaus, ReseptinUusimisKasaajaTest.testUusimispyynnonOid,
                        ReseptinUusimisKasaajaTest.testUusimispyynnonSetId, "8");
            }
        }

    }

    private void tarkistaViittaus(POCDMT000040Reference viittaus, String Oid, String SetId, String code) {
        Assert.assertNotNull("Viittaus ei voi olla null", viittaus);
        Assert.assertNotNull("Viittauksella on oltava externalDocument", viittaus.getExternalDocument());
        Assert.assertEquals("externalDocumentin id oltava: " + Oid, Oid,
                viittaus.getExternalDocument().getIds().get(0).getRoot());
        Assert.assertEquals("externalDocumentin setId oltava: " + SetId, SetId,
                viittaus.getExternalDocument().getSetId().getRoot());
        Assert.assertEquals("externalDocumentin code oltava: " + code, code,
                viittaus.getExternalDocument().getCode().getCode());
    }

    @Test
    public void testGetMuutTiedotCode() {
        ReseptinUusimisKasaaja tested = setupReseptinUusimisKasaaja(null, null);
        Assert.assertEquals("getMuutTiedotCode() tulee palauttaa uusitulle reseptille 88", "88",
                tested.getMuutTiedotCode());
    }

    @Test
    public void testUusimispyynnonOidinAsettaminen() {

        super.mockProperty("uusiminen.code.title", "uusimis title");
        // super.mockProperty("uusiminen.code.codeSystem", "uusimis codeSystem");
        // super.mockProperty("uusiminen.code.codeSystemName", "uusimis codeSystemName");
        // super.mockProperty("uusiminen.code.displayName", "uusimis displayName");

        LaakemaaraysTO alkuperainen = luoLaakemaarays();
        alkuperainen.setOid("123.test.456789");
        alkuperainen.setSetId("123.test.456789");
        String uusimispyynnonOid = "uusimispyynnon.oid";
        String uusimispyynnonSetId = "uusimispyynnon.setId";
        Calendar greg = new GregorianCalendar();
        greg.add(Calendar.MONDAY, -6);
        alkuperainen.setMaarayspaiva(greg.getTime());
        LaakemaaraysTO laakemaarays = luoLaakemaarays();
        laakemaarays.setMaarayspaiva(new Date());
        laakemaarays.setOid("234.test.567");
        laakemaarays.setSetId("234.test.567");

        ReseptinUusimisKasaaja tested = setupReseptinUusimisKasaaja(null, laakemaarays);
        POCDMT000040ClinicalDocument doc = tested.kasaaCdaRelatedDocumentTiedonKanssa(laakemaarays, alkuperainen,
                uusimispyynnonOid, uusimispyynnonSetId);
        Assert.assertNotNull("cda ei saa olla null", doc);
        Assert.assertNotNull("Oid täytyy löytyä", doc.getRelatedDocuments().get(0).getParentDocument().getIds());
        Assert.assertNotNull("Oid täytyy löytyä", doc.getRelatedDocuments().get(0).getParentDocument().getIds().get(0));
        Assert.assertEquals(uusimispyynnonOid,
                doc.getRelatedDocuments().get(0).getParentDocument().getIds().get(0).getRoot());
        Assert.assertNotNull("SetId täytyy löytyä", doc.getRelatedDocuments().get(0).getParentDocument().getSetId());
        Assert.assertEquals(uusimispyynnonSetId,
                doc.getRelatedDocuments().get(0).getParentDocument().getSetId().getRoot());
    }
}
