package fi.kela.kanta.cda;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import javax.xml.bind.JAXBException;

import org.hl7.v3.BL;
import org.hl7.v3.CE;
import org.hl7.v3.POCDMT000040Author;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component4;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040Organizer;
import org.hl7.v3.POCDMT000040Reference;
import org.hl7.v3.XActRelationshipExternalReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.powermock.reflect.Whitebox;

import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.ApteekissaValmistettavaLaakeTO;
import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.LaakemaarayksenMitatointiTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.OrganisaatioTO;
import fi.kela.kanta.to.ValmisteTO;
import fi.kela.kanta.to.ValmisteenYksilointitiedotTO;
import fi.kela.kanta.util.JaxbUtil;
import fi.kela.kanta.util.LMTOKasaaja;
import junit.framework.TestCase;

public class ReseptinMitatointiKasaajaEteenpainYhtTest extends LMTOKasaaja {

    private static String testAlkuperainenOid = "1.2.3.4.5.6";
    private static String testAlkuperainenSetId = "1.2.3.4.5.6.7";
    private static int testAlkuperainenVersio = 5;
    private static int testAlkuperainenCdaTyyppi = 1;

    @Override
    protected void setupProperties() {
        KantaCDATestUtils.setupProperties();
    }

    private ReseptinMitatointiKasaajaEteenpainYht setupReseptinMitatointiKasaaja2(
            LaakemaarayksenMitatointiTO laakemaarayksenMitatointiTO, LaakemaaraysTO alkuperainenLaakemaaraysTO)
            throws JAXBException {

        String cda = KantaCDATestUtils.lataa("LM_testiasiakirja_mitatointiin.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());

        POCDMT000040ClinicalDocument alkuperainenClinicalDocument = null;
        if ( cda != null && !cda.isEmpty() ) {
            alkuperainenClinicalDocument = JaxbUtil.getInstance().unmarshaller(cda);
        }

        KantaCDATestUtils.mockProperty(Kasaaja.BASE_OID_PROPERTY, KantaCDATestUtils.testBaseId);
        try {

            return new ReseptinMitatointiKasaajaEteenpainYht(
                    KantaCDATestUtils.loadProperties("testi_properties.properties"), laakemaarayksenMitatointiTO,
                    alkuperainenLaakemaaraysTO, alkuperainenClinicalDocument);

        }
        catch (IOException e) {
            e.printStackTrace();
            TestCase.fail();
        }
        return null;
    }

    private void setupLaakemaarayksenLeimakentat(LaakemaaraysTO laakemaaraysTO) {
        laakemaaraysTO.setOid(ReseptinMitatointiKasaajaEteenpainYhtTest.testAlkuperainenOid);
        // laakemaaraysTO.setOrgYTunnus("1234567-8");
        laakemaaraysTO.setSetId(ReseptinMitatointiKasaajaEteenpainYhtTest.testAlkuperainenSetId);
        laakemaaraysTO.setVersio(ReseptinMitatointiKasaajaEteenpainYhtTest.testAlkuperainenVersio);
        laakemaaraysTO.setCdaTyyppi(ReseptinMitatointiKasaajaEteenpainYhtTest.testAlkuperainenCdaTyyppi);
    }

    @Test
    public void testPaivataMitatointi_leimatiedot() throws Exception {

        LaakemaarayksenMitatointiTO laakemaarayksenMitatointiTO = new LaakemaarayksenMitatointiTO();
        // laakemaarayksenMitatointiTO.setOrgYTunnus("1234567-8");
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        setupLaakemaarayksenLeimakentat(laakemaaraysTO);
        ReseptinMitatointiKasaajaEteenpainYht tested = setupReseptinMitatointiKasaaja2(laakemaarayksenMitatointiTO,
                laakemaaraysTO);
        Whitebox.invokeMethod(tested, "paivitaMitatointi");
        Assert.assertNotNull("mitatoinnilla tulee olla oid", laakemaarayksenMitatointiTO.getOid());
        Assert.assertEquals("mitatoinnin alkuperainenOid pitää olla alkuperaienenOid",
                ReseptinMitatointiKasaajaEteenpainYhtTest.testAlkuperainenOid,
                laakemaarayksenMitatointiTO.getAlkuperainenOid());
        Assert.assertEquals("mitatoinnin alkuperainenCdaTyyppi pitää olla alkuperainenCdaTyyppi",
                ReseptinMitatointiKasaajaEteenpainYhtTest.testAlkuperainenCdaTyyppi,
                laakemaarayksenMitatointiTO.getAlkuperainenCdaTyyppi());
        Assert.assertEquals("mitatoinnin setId pitää olla alkuperainenSetId",
                ReseptinMitatointiKasaajaEteenpainYhtTest.testAlkuperainenSetId,
                laakemaarayksenMitatointiTO.getSetId());
        Assert.assertEquals("mitatoinnin versio pitää olla alkuperainenVersio",
                ReseptinMitatointiKasaajaEteenpainYhtTest.testAlkuperainenVersio,
                laakemaarayksenMitatointiTO.getVersio());
    }

    @Test
    public void testPaivataMitatointi_arvot() throws Exception {

        LaakemaarayksenMitatointiTO laakemaarayksenMitatointiTO = new LaakemaarayksenMitatointiTO();
        // laakemaarayksenMitatointiTO.setOrgYTunnus("1234567-8");
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        setupLaakemaarayksenLeimakentat(laakemaaraysTO);
        laakemaaraysTO.setMaarayspaiva(new Date());
        laakemaaraysTO.setLaakemaarayksenVoimassaolonLoppuaika(new Date());
        laakemaaraysTO.setReseptintyyppi("reseptintyyppi");
        laakemaaraysTO.setPakkauksienLukumaara(7);
        laakemaaraysTO.setLaakkeenKokonaismaaraValue(8);
        laakemaaraysTO.setLaakkeenKokonaismaaraUnit("laakkeenKokonaismaaraUnit");
        laakemaaraysTO.setAjalleMaaratynReseptinAlkuaika(new Date());
        laakemaaraysTO.setAjalleMaaratynReseptinAikamaaraValue(9);
        laakemaaraysTO.setAjalleMaaratynReseptinAikamaaraUnit("ajalleMaaratynReseptinAikamaaraUnit");
        laakemaaraysTO.setIterointiTeksti("iterointiTeksti");
        laakemaaraysTO.setIterointienMaara(10);
        laakemaaraysTO.setIterointienValiValue(11);
        laakemaaraysTO.setIterointienValiUnit("iterointienValiUnit");
        laakemaaraysTO.setValmiste(new ValmisteTO());
        laakemaaraysTO.setApteekissaValmistettavaLaake(new ApteekissaValmistettavaLaakeTO());
        laakemaaraysTO.setLaaketietokannanUlkopuolinenValmiste("laaketietokannanUlkopuolinenValmiste");
        laakemaaraysTO.setTyonantaja("tyonantaja");
        laakemaaraysTO.setVakuutuslaitos("vakuutuslaitos");
        laakemaaraysTO.setAmmattihenkilo(new AmmattihenkiloTO());
        laakemaaraysTO.getAmmattihenkilo().setOrganisaatio(new OrganisaatioTO());
        laakemaaraysTO.setPotilas(new HenkilotiedotTO(new KokoNimiTO(), "hetu"));
        laakemaaraysTO.setApteekissaValmistettavaLaake(true);
        laakemaaraysTO.setAnnosteluPelkastaanTekstimuodossa(true);
        laakemaaraysTO.setAnnostusohje("annostusohje");
        laakemaaraysTO.setSICmerkinta(true);
        laakemaaraysTO.setLaakevaihtokielto(true);
        laakemaaraysTO.setKayttotarkoitusTeksti("kayttotarkoitusTeksti");
        laakemaaraysTO.setAlle12VuotiaanPainoValue(new BigDecimal(12.5));
        laakemaaraysTO.setAlle12VuotiaanPainoUnit("alle12VuotiaanPainoUnit");
        laakemaaraysTO.setAnnosjakelu(true);
        laakemaaraysTO.setAnnosjakeluTeksti("annosjakeluTeksti");
        laakemaaraysTO.getHoitolajit().add("hoitolaji");
        laakemaaraysTO.setViestiApteekille("viestiApteekille");
        laakemaaraysTO.setErillisselvitys("erillisselvitys");
        laakemaaraysTO.setErillisselvitysteksti("erillisselvitysteksti");
        laakemaaraysTO.setPotilaanTunnistaminen("potilaanTunnistaminen");
        laakemaaraysTO.setPotilaanTunnistaminenTeksti("potilaanTunnistaminenTeksti");
        laakemaaraysTO.setPKVlaakemaarays("pKVlaakemaarays");
        laakemaaraysTO.setPysyvaislaakitys(true);
        laakemaaraysTO.setKyseessaLaakkeenkaytonAloitus(true);
        laakemaaraysTO.setHuume(true);
        laakemaaraysTO.setReseptinLaji("reseptinLaji");
        laakemaaraysTO.setUudistamiskielto(true);
        laakemaaraysTO.setUusimiskiellonSyy("uusimiskiellonSyy");
        laakemaaraysTO.setUusimiskiellonPerustelu("uusimiskiellonPerustelu");
        laakemaaraysTO.setLaaketietokannanVersio("laaketietokannanVersio");
        laakemaaraysTO.setApteekissaTallennettuLaakemaarays("2");

        ReseptinMitatointiKasaajaEteenpainYht tested = setupReseptinMitatointiKasaaja2(laakemaarayksenMitatointiTO,
                laakemaaraysTO);
        Whitebox.invokeMethod(tested, "paivitaMitatointi");

        Assert.assertNotNull("mitätöinnillä tulee olla oid", laakemaarayksenMitatointiTO.getOid());
        Assert.assertEquals("mitätöinnillä alkuperainenOid pitää olla alkuperaienenOid",
                ReseptinMitatointiKasaajaEteenpainYhtTest.testAlkuperainenOid,
                laakemaarayksenMitatointiTO.getAlkuperainenOid());
        Assert.assertEquals("mitätöinnillä setId pitää olla alkuperainenSetId",
                ReseptinMitatointiKasaajaEteenpainYhtTest.testAlkuperainenSetId,
                laakemaarayksenMitatointiTO.getSetId());
        Assert.assertEquals("mitätöinnillä versio pitää olla alkuperainenVersio",
                ReseptinMitatointiKasaajaEteenpainYhtTest.testAlkuperainenVersio,
                laakemaarayksenMitatointiTO.getVersio());
        Assert.assertEquals("Määräyspäivän tulee olla samat", laakemaaraysTO.getMaarayspaiva(),
                laakemaarayksenMitatointiTO.getMaarayspaiva());
        Assert.assertEquals("Lääkemääräyksen voimassaolon loppuaika tulee olla samat",
                laakemaaraysTO.getLaakemaarayksenVoimassaolonLoppuaika(),
                laakemaarayksenMitatointiTO.getLaakemaarayksenVoimassaolonLoppuaika());
        Assert.assertEquals("Reseptintyyppi tulee olla MITÄTÖINTI: ",
                /* "LAAKEMAARAYKSEN_MITATOINTI" */String
                        .valueOf(KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYKSEN_MITATOINTI.getTyyppi()),
                laakemaarayksenMitatointiTO.getReseptintyyppi());
        Assert.assertEquals("Pakkauksien lukumäärä tulee olla: " + laakemaaraysTO.getPakkauksienLukumaara(),
                laakemaaraysTO.getPakkauksienLukumaara(), laakemaarayksenMitatointiTO.getPakkauksienLukumaara());
        Assert.assertEquals(
                "Lääkkeen kokonaismäärä value tulee olla: " + laakemaaraysTO.getLaakkeenKokonaismaaraValue(),
                laakemaaraysTO.getLaakkeenKokonaismaaraValue(),
                laakemaarayksenMitatointiTO.getLaakkeenKokonaismaaraValue());
        Assert.assertEquals("Lääkkeen kokonaismäärä unit tulee olla: " + laakemaaraysTO.getLaakkeenKokonaismaaraUnit(),
                laakemaaraysTO.getLaakkeenKokonaismaaraUnit(),
                laakemaarayksenMitatointiTO.getLaakkeenKokonaismaaraUnit());
        Assert.assertEquals("Ajalle määrätyn reseptin alkuaika tulee olla samat",
                laakemaaraysTO.getAjalleMaaratynReseptinAlkuaika(),
                laakemaarayksenMitatointiTO.getAjalleMaaratynReseptinAlkuaika());
        Assert.assertEquals(
                "Ajalle määrätyn reseptin aikamaara value tulee olla: "
                        + laakemaaraysTO.getAjalleMaaratynReseptinAikamaaraValue(),
                laakemaarayksenMitatointiTO.getAjalleMaaratynReseptinAikamaaraValue(),
                laakemaaraysTO.getAjalleMaaratynReseptinAikamaaraValue());
        Assert.assertEquals(
                "Ajalle määrätyn reseptin aikamaara unit tulee olla: "
                        + laakemaaraysTO.getAjalleMaaratynReseptinAikamaaraUnit(),
                laakemaarayksenMitatointiTO.getAjalleMaaratynReseptinAikamaaraUnit(),
                laakemaaraysTO.getAjalleMaaratynReseptinAikamaaraUnit());
        Assert.assertEquals("Iterointi tekstin tulee olla: " + laakemaaraysTO.getIterointiTeksti(),
                laakemaaraysTO.getIterointiTeksti(), laakemaarayksenMitatointiTO.getIterointiTeksti());
        Assert.assertEquals("Iterointien määrän tulee olla: " + laakemaaraysTO.getIterointienMaara(),
                laakemaaraysTO.getIterointienMaara(), laakemaarayksenMitatointiTO.getIterointienMaara());
        Assert.assertEquals("Iterointien väli valuen tulee olla: " + laakemaaraysTO.getIterointienValiValue(),
                laakemaaraysTO.getIterointienValiValue(), laakemaarayksenMitatointiTO.getIterointienValiValue());
        Assert.assertEquals("Iterointien väli unitin tulee olla: " + laakemaaraysTO.getIterointienValiUnit(),
                laakemaaraysTO.getIterointienValiUnit(), laakemaarayksenMitatointiTO.getIterointienValiUnit());
        Assert.assertEquals("Valmisteen tulee olla samat", laakemaaraysTO.getValmiste(),
                laakemaarayksenMitatointiTO.getValmiste());
        Assert.assertEquals("Apteekissa valmistettavan lääkkeen tulee olla samat",
                laakemaaraysTO.getApteekissaValmistettavaLaake(),
                laakemaarayksenMitatointiTO.getApteekissaValmistettavaLaake());
        Assert.assertEquals(
                "Lääketietokannan ulkopuolisen valmisteen tulee olla: "
                        + laakemaaraysTO.getLaaketietokannanUlkopuolinenValmiste(),
                laakemaaraysTO.getLaaketietokannanUlkopuolinenValmiste(),
                laakemaarayksenMitatointiTO.getLaaketietokannanUlkopuolinenValmiste());
        Assert.assertEquals("Tyonantajan tulee olla: " + laakemaaraysTO.getTyonantaja(), laakemaaraysTO.getTyonantaja(),
                laakemaarayksenMitatointiTO.getTyonantaja());
        Assert.assertEquals("Vakuutuslaitoksen tulee olla: " + laakemaaraysTO.getVakuutuslaitos(),
                laakemaaraysTO.getVakuutuslaitos(), laakemaarayksenMitatointiTO.getVakuutuslaitos());
        Assert.assertEquals("Ammattihenkilön tulee olla samat", laakemaaraysTO.getAmmattihenkilo(),
                laakemaarayksenMitatointiTO.getAmmattihenkilo());
        Assert.assertEquals("Lääkkeen määrääjän toimipaikan tulee olla samat",
                laakemaaraysTO.getAmmattihenkilo().getOrganisaatio(),
                laakemaarayksenMitatointiTO.getAmmattihenkilo().getOrganisaatio());
        Assert.assertEquals("Potilaan tulee olla samat", laakemaaraysTO.getPotilas(),
                laakemaarayksenMitatointiTO.getPotilas());
        Assert.assertTrue("Apteekissa valmistettavan lääkkeen tulee olla true",
                laakemaarayksenMitatointiTO.isApteekissaValmistettavaLaake());
        Assert.assertTrue("Annostelu pelkästään tekstimuodossa tulee olla true",
                laakemaarayksenMitatointiTO.isAnnosteluPelkastaanTekstimuodossa());
        Assert.assertEquals("Annostusohjeen tulee olla: " + laakemaaraysTO.getAnnostusohje(),
                laakemaaraysTO.getAnnostusohje(), laakemaarayksenMitatointiTO.getAnnostusohje());
        Assert.assertTrue("SICmerkinnän tulee olla true", laakemaarayksenMitatointiTO.isSICmerkinta());
        Assert.assertTrue("Lääkevaihtokiellon tulee olla true", laakemaarayksenMitatointiTO.isLaakevaihtokielto());
        Assert.assertEquals("Käyttotarkoitus tekstin tulee olla: " + laakemaaraysTO.getKayttotarkoitusTeksti(),
                laakemaaraysTO.getKayttotarkoitusTeksti(), laakemaarayksenMitatointiTO.getKayttotarkoitusTeksti());
        Assert.assertEquals("Alle 12 vuotiaan paino valuen tulee olla: " + laakemaaraysTO.getAlle12VuotiaanPainoValue(),
                laakemaaraysTO.getAlle12VuotiaanPainoValue(),
                laakemaarayksenMitatointiTO.getAlle12VuotiaanPainoValue());
        Assert.assertEquals("Alle 12 vuotiaan paino unitin tulee olla: " + laakemaaraysTO.getAlle12VuotiaanPainoUnit(),
                laakemaaraysTO.getAlle12VuotiaanPainoUnit(), laakemaarayksenMitatointiTO.getAlle12VuotiaanPainoUnit());
        Assert.assertTrue("Annosjakelun tulee olla true", laakemaarayksenMitatointiTO.isAnnosjakelu());
        Assert.assertEquals("Annosjakelu tekstin tulee olla: " + laakemaaraysTO.getAnnosjakeluTeksti(),
                laakemaaraysTO.getAnnosjakeluTeksti(), laakemaarayksenMitatointiTO.getAnnosjakeluTeksti());
        Assert.assertArrayEquals("Hoitolajit tulee olla samat", laakemaaraysTO.getHoitolajit().toArray(),
                laakemaarayksenMitatointiTO.getHoitolajit().toArray());
        Assert.assertEquals("Viesti apteekille tulee olla: " + laakemaaraysTO.getViestiApteekille(),
                laakemaaraysTO.getViestiApteekille(), laakemaarayksenMitatointiTO.getViestiApteekille());
        Assert.assertEquals("Erillisselvityksen tulee olla: " + laakemaaraysTO.getErillisselvitys(),
                laakemaaraysTO.getErillisselvitys(), laakemaarayksenMitatointiTO.getErillisselvitys());
        Assert.assertEquals("Erillisselvitystekstin tulee olla: " + laakemaaraysTO.getErillisselvitysteksti(),
                laakemaaraysTO.getErillisselvitysteksti(), laakemaarayksenMitatointiTO.getErillisselvitysteksti());
        Assert.assertEquals("Potilaan tunnistamisen tulee olla: " + laakemaaraysTO.getPotilaanTunnistaminen(),
                laakemaaraysTO.getPotilaanTunnistaminen(), laakemaarayksenMitatointiTO.getPotilaanTunnistaminen());
        Assert.assertEquals(
                "Potilaan tunnistaminen tekstin tulee olla: " + laakemaaraysTO.getPotilaanTunnistaminenTeksti(),
                laakemaaraysTO.getPotilaanTunnistaminenTeksti(),
                laakemaarayksenMitatointiTO.getPotilaanTunnistaminenTeksti());
        Assert.assertEquals("PKV lääkemääräyksen tulee olla: " + laakemaaraysTO.getPKVlaakemaarays(),
                laakemaaraysTO.getPKVlaakemaarays(), laakemaarayksenMitatointiTO.getPKVlaakemaarays());
        Assert.assertTrue("Pysyväislääkityksen tulee olla true", laakemaarayksenMitatointiTO.isPysyvaislaakitys());
        Assert.assertTrue("Kyseessä lääkkeenkäytön aloitus tulee olla true",
                laakemaarayksenMitatointiTO.isKyseessaLaakkeenkaytonAloitus());
        Assert.assertTrue("Huume tulee olla true", laakemaarayksenMitatointiTO.isHuume());
        Assert.assertEquals("Reseptin lajin tulee olla: " + laakemaaraysTO.getReseptinLaji(),
                laakemaaraysTO.getReseptinLaji(), laakemaarayksenMitatointiTO.getReseptinLaji());
        Assert.assertTrue("Uudistamiskiellon tulee olla true", laakemaarayksenMitatointiTO.isUudistamiskielto());
        Assert.assertEquals("Uusimiskiellon syyn tulee olla: " + laakemaaraysTO.getUusimiskiellonSyy(),
                laakemaaraysTO.getUusimiskiellonSyy(), laakemaarayksenMitatointiTO.getUusimiskiellonSyy());
        Assert.assertEquals("Uusimiskiellon perustelun tulee olla: " + laakemaaraysTO.getUusimiskiellonPerustelu(),
                laakemaaraysTO.getUusimiskiellonPerustelu(), laakemaarayksenMitatointiTO.getUusimiskiellonPerustelu());
        Assert.assertEquals("Lääketietokannan version tulee olla: " + laakemaaraysTO.getLaaketietokannanVersio(),
                laakemaaraysTO.getLaaketietokannanVersio(), laakemaarayksenMitatointiTO.getLaaketietokannanVersio());
        Assert.assertEquals(
                "Apteekissa tallennetun lääkemääräyksen arvon tulee sailyä mitaötointiasiakirjalla arvolla 2",
                laakemaaraysTO.getApteekissaTallennettuLaakemaarays(),
                laakemaarayksenMitatointiTO.getApteekissaTallennettuLaakemaarays());
    }

    @Test
    public void testKasaaReseptinMitatointi() throws Exception {

        LaakemaarayksenMitatointiTO laakemaarayksenMitatointiTO = new LaakemaarayksenMitatointiTO();
        // laakemaarayksenMitatointiTO.setOrgYTunnus("1234567-8");
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        setupLaakemaarayksenLeimakentat(laakemaaraysTO);
        laakemaaraysTO.setPotilas(
                new HenkilotiedotTO(new KokoNimiTO("potilasEtu", "potilasSuku"), KantaCDATestUtils.testHetu));
        laakemaaraysTO.setAmmattihenkilo(KantaCDATestUtils.luoAmmattihenkilo());
        OrganisaatioTO org = KantaCDATestUtils.luoOrganisaatio();
        OrganisaatioTO pyorg = KantaCDATestUtils.luoOrganisaatio();
        org.setPalveluYksikko(pyorg);
        laakemaaraysTO.getAmmattihenkilo().setOrganisaatio(org);
        laakemaaraysTO.setValmiste(new ValmisteTO());
        laakemaaraysTO.getValmiste().setYksilointitiedot(new ValmisteenYksilointitiedotTO());
        laakemaaraysTO.getValmiste().getYksilointitiedot().setValmisteenLaji("1");
        laakemaaraysTO.getValmiste().getYksilointitiedot().setYksilointitunnus("yksilointitunnus");
        laakemaaraysTO.getValmiste().getYksilointitiedot().setKauppanimi("kauppanimi");
        laakemaaraysTO.setReseptintyyppi("reseptintyyppi");
        laakemaaraysTO.setMaarayspaiva(new Date());
        laakemaaraysTO.getHoitolajit().add("hoitolaji");
        laakemaaraysTO.setLaatimispaikka(KantaCDATestUtils.luoOrganisaatio());

        laakemaarayksenMitatointiTO.setMitatoija(KantaCDATestUtils.luoAmmattihenkilo());
        laakemaarayksenMitatointiTO.getMitatoija().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        laakemaarayksenMitatointiTO.setMitatoinninSyyKoodi("1");
        laakemaarayksenMitatointiTO.setMitatoinninPerustelu("mitätöinnin testi on perusteluna");

        KantaCDATestUtils.mockCodeProperty("code", "2", "test_lm_codeSystem", "test_lm_codeSystemName",
                "test_lm_displayName");
        KantaCDATestUtils.mockCodeProperty("Mitatointi.code", "1", "test_Mitatointi_codeSystem",
                "test_Mitatointi_codeSystemName", "test_Mitatointi_displayName");

        ReseptinMitatointiKasaajaEteenpainYht tested = setupReseptinMitatointiKasaaja2(laakemaarayksenMitatointiTO,
                laakemaaraysTO);
        POCDMT000040ClinicalDocument doc = tested.kasaaReseptiCDA();
        Assert.assertNotNull("Reseptin mitatointi cda ei saa olla null", doc);
        Assert.assertEquals("mitatointi cda code tulee olla 2", "2", doc.getCode().getCode());
        Assert.assertEquals("mitatoinnin authorien maaran tulee olla 2", 2, doc.getAuthors().size());
        boolean mitFound = false;
        boolean lalFound = false;
        for (POCDMT000040Author author : doc.getAuthors()) {
            Assert.assertNotNull("authorilla tulee olla functionCode", author.getFunctionCode());
            Assert.assertNotNull("author functionCode code ei voi olla null", author.getFunctionCode().getCode());
            if ( author.getFunctionCode().getCode().equals("LAL") ) {
                lalFound = true;
            }
            else if ( author.getFunctionCode().getCode().equals("MIT") ) {
                mitFound = true;
            }
        }
        Assert.assertTrue("mitatoinnilla tulee olla 'LAL' author", lalFound);
        Assert.assertTrue("mitatoinnilla tulee olla 'MIT' author", mitFound);

        // Assert.assertNotNull("reseptin Mitatointi ei saa olla null", cda);
    }

    @Test
    public void testGetPropertyCode() throws Exception {
        ReseptinMitatointiKasaajaEteenpainYht tested = setupReseptinMitatointiKasaaja2(null, null);
        Object[] args1 = { "Mitatointi" };
        Assert.assertEquals("getPropertyCode tulee palauttaa :'code'", "code", Whitebox.invokeMethod(tested,
                "getPropertyCode", KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYS.getTyyppi(), args1));
        Assert.assertEquals("getPropertyCode tulee palauttaa :'Mitatointi.code'", "Mitatointi.code",
                Whitebox.invokeMethod(tested, "getPropertyCode",
                        KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYKSEN_MITATOINTI.getTyyppi(), args1));
        Assert.assertNull("getPropertyCode tulee palauttaa null", Whitebox.invokeMethod(tested, "getPropertyCode",
                KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYKSEN_TOIMITUS.getTyyppi(), args1));

        Object[] args2 = { "uusiminen" };
        Assert.assertEquals("getPropertyCode tulee palauttaa :'code'", "code", Whitebox.invokeMethod(tested,
                "getPropertyCode", KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYS.getTyyppi(), args2));
        Assert.assertEquals("getPropertyCode tulee palauttaa :'uusiminen.code'", "uusiminen.code",
                Whitebox.invokeMethod(tested, "getPropertyCode",
                        KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYKSEN_MITATOINTI.getTyyppi(), args2));
        Assert.assertNull("getPropertyCode tulee palauttaa null", Whitebox.invokeMethod(tested, "getPropertyCode",
                KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYKSEN_TOIMITUS.getTyyppi(), args2));
    }

    @Test
    public void testLuoViittaukset() throws Exception {
        KantaCDATestUtils.mockCodeProperty("mitatointi.code", "2", "1.2.246.537.5.40105.2006",
                "Sähköinen lääkemääräys - Reseptisanoman tyyppi", "Lääkemääräyksen Mitatointi");
        KantaCDATestUtils.mockCodeProperty("contentsCode", "1", "1.2.246.537.5.40105.2006",
                "Sähköinen lääkemääräys - Reseptisanoman tyyppi", "Lääkemääräys");

        LaakemaarayksenMitatointiTO laakemaarayksenMitatointiTO = new LaakemaarayksenMitatointiTO();
        String mitatoinninnOid = "1.12.123.1234";
        String mitatoinninSetId = ReseptinMitatointiKasaajaEteenpainYhtTest.testAlkuperainenSetId;
        laakemaarayksenMitatointiTO.setOid(mitatoinninnOid);
        laakemaarayksenMitatointiTO.setSetId(mitatoinninSetId);
        laakemaarayksenMitatointiTO.setAlkuperainenOid(ReseptinMitatointiKasaajaEteenpainYhtTest.testAlkuperainenOid);
        ReseptinMitatointiKasaajaEteenpainYht tested = setupReseptinMitatointiKasaaja2(laakemaarayksenMitatointiTO,
                null);
        Collection<POCDMT000040Reference> viittaukset = Whitebox.invokeMethod(tested, "luoViittaukset",
                laakemaarayksenMitatointiTO);
        Assert.assertEquals("viittauksien määrä pitää olla 2", 2, viittaukset.size());
        for (POCDMT000040Reference viittaus : viittaukset) {
            if ( viittaus.getTypeCode() == XActRelationshipExternalReference.SPRT ) {
                tarkistaViittaus(viittaus, mitatoinninnOid, mitatoinninSetId, "2"); // mitätöinnin asiakirjan
                // cda-tyyppikoodi on 2
            }
            else {
                tarkistaViittaus(viittaus, ReseptinMitatointiKasaajaEteenpainYhtTest.testAlkuperainenOid,
                        mitatoinninSetId, "1");
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
    public void testLuoMitatoinninSyyPerusteluJaMitatoija() throws Exception {
        String syyKoodi = "1";
        String mitatoinninPerustelu = "mitatoinninPerustelu";
        ReseptinMitatointiKasaajaEteenpainYht tested = setupReseptinMitatointiKasaaja2(null, null);
        Assert.assertNull("palautettava null jos parametrina ei ole LaakemaarayksenMitatointiTO instanssi", Whitebox
                .invokeMethod(tested, "luoMitatoinninSyyPerusteluJaMitatoija", Matchers.isA(LaakemaaraysTO.class)));
        LaakemaarayksenMitatointiTO laakemaarayksenMitatointiTO = new LaakemaarayksenMitatointiTO();
        Assert.assertNull("palautettava null jos mitatoinnin syykoodi null tai tyhjä",
                Whitebox.invokeMethod(tested, "luoMitatoinninSyyPerusteluJaMitatoija", laakemaarayksenMitatointiTO));
        laakemaarayksenMitatointiTO.setMitatoija(KantaCDATestUtils.luoAmmattihenkilo());
        laakemaarayksenMitatointiTO.setMitatoinninSyyKoodi(syyKoodi);
        POCDMT000040Component4 component = Whitebox.invokeMethod(tested, "luoMitatoinninSyyPerusteluJaMitatoija",
                laakemaarayksenMitatointiTO);
        Assert.assertEquals("observationin valueiden määrä tulee olla 1", 1,
                component.getObservation().getValues().size());
        Assert.assertEquals("observationin valuen coden tulee olla: " + syyKoodi, syyKoodi,
                ((CE) component.getObservation().getValues().get(0)).getCode());
        Assert.assertNotNull("authors ei voi olla null", component.getObservation().getAuthors());
        Assert.assertEquals("authoreja tulee olla 0", 0, component.getObservation().getAuthors().size());

        laakemaarayksenMitatointiTO.setMitatoinninPerustelu(mitatoinninPerustelu);
        component = Whitebox.invokeMethod(tested, "luoMitatoinninSyyPerusteluJaMitatoija", laakemaarayksenMitatointiTO);
        Assert.assertEquals("observationin valueiden määrä tulee olla 2", 2,
                component.getObservation().getValues().size());
    }

    @Test
    public void testGetMuutTiedotCode() throws Exception {
        ReseptinMitatointiKasaajaEteenpainYht tested = setupReseptinMitatointiKasaaja2(null, null);
        /*
         * Organizeriin (code=88) lisätään (verrattuna varsinaiseen lääkemääräyssanomaan) seuraavat observation-actit
         * mitätöintisanomassa ja organizerille annetaan nyt code-arvo 98 (organizerin 88 tiedot siis kopioidaan
         * sellaisenaan, koodi 88 muutetaan arvoon 98 -> Mitätöinnissä tämän koodin tulee olla 98
         */
        Assert.assertEquals("getMuutTiedotCode() tulee palauttaa 98", "98", tested.getMuutTiedotCode());
    }

    // TODO: tee testi: Lääkemääräyksen määräyspäivää ei saa muuttaa lääkemääräyksen mitätöinnissä.

    // TODO: tee testi: Lääkemääräyksen mitätöinnin syy voidaan koodiston mukaisen arvon (pakollinen) lisäksi antaa
    // vapaana tekstinä.
    // Vapaa teksti on pakollinen, mikäli koodiston mukainen arvo on Muu syy.

    //
    // Mitätöitäessa resptin myöhempiä versioita (korjauksia) korjaajan tiedot täytyy säilyä asiakirjan bodyssa
    //
    @Test
    public void testLuoKorjauksenSyyPerusteluJaKorjaaja() throws Exception {
        String syyKoodi = "SYYKOODI";
        String korjauksenPerustelu = "korjauksenPerustelu";
        ReseptinMitatointiKasaajaEteenpainYht tested = setupReseptinMitatointiKasaaja2(null, null);
        Assert.assertNull("palautettava null jos parametrina ei ole LaakemaarayksenMitatointiTO instanssi", Whitebox
                .invokeMethod(tested, "luoKorjauksenSyyPerusteluJaKorjaaja", Matchers.isA(LaakemaaraysTO.class)));
        LaakemaarayksenMitatointiTO laakemaarayksenMitatointiTO = new LaakemaarayksenMitatointiTO();
        tested = setupReseptinMitatointiKasaaja2(laakemaarayksenMitatointiTO, null);
        Assert.assertNull("palautettava null jos korjauksen syykoodi null tai tyhjä",
                Whitebox.invokeMethod(tested, "luoKorjauksenSyyPerusteluJaKorjaaja", laakemaarayksenMitatointiTO));
        laakemaarayksenMitatointiTO.setAlkuperainenKorjaaja(KantaCDATestUtils.luoAmmattihenkilo());
        laakemaarayksenMitatointiTO.setAlkuperainenKorjauksenSyyKoodi(syyKoodi);
        tested = setupReseptinMitatointiKasaaja2(laakemaarayksenMitatointiTO, null);
        POCDMT000040Component4 component = Whitebox.invokeMethod(tested, "luoKorjauksenSyyPerusteluJaKorjaaja",
                laakemaarayksenMitatointiTO);
        Assert.assertEquals("observationin valueiden määrä tulee olla 1", 1,
                component.getObservation().getValues().size());
        Assert.assertEquals("observationin valuen coden tulee olla: " + syyKoodi, syyKoodi,
                ((CE) component.getObservation().getValues().get(0)).getCode());
        Assert.assertNotNull("authors ei voi olla null", component.getObservation().getAuthors());
        Assert.assertEquals("authoreja tulee olla 1", 1, component.getObservation().getAuthors().size());
        Assert.assertEquals("author time tulee olla nullFlavor NI", "NI",
                component.getObservation().getAuthors().get(0).getTime().getNullFlavors().get(0));

        laakemaarayksenMitatointiTO.setAlkuperainenKorjauksenPerustelu(korjauksenPerustelu);
        tested = setupReseptinMitatointiKasaaja2(laakemaarayksenMitatointiTO, null);
        component = Whitebox.invokeMethod(tested, "luoKorjauksenSyyPerusteluJaKorjaaja", laakemaarayksenMitatointiTO);
        Assert.assertEquals("observationin valueiden määrä tulee olla 2", 2,
                component.getObservation().getValues().size());
    }

    @Test
    public void testluoUudistamiskielto() throws Exception {
        LaakemaarayksenMitatointiTO laakemaarayksenMitatointiTO = new LaakemaarayksenMitatointiTO();
        laakemaarayksenMitatointiTO.setUudistamiskielto(Boolean.TRUE);
        laakemaarayksenMitatointiTO.setUusimiskiellonPerustelu("perustelu");
        laakemaarayksenMitatointiTO.setUusimiskiellonSyy("syy");
        ReseptinMitatointiKasaajaEteenpainYht tested = setupReseptinMitatointiKasaaja2(laakemaarayksenMitatointiTO,
                null);
        Assert.assertNull("palautettava null jos parametrina ei ole LaakemaarayksenMitatointiTO instanssi", Whitebox
                .invokeMethod(tested, "luoMitatoinninSyyPerusteluJaMitatoija", Matchers.isA(LaakemaaraysTO.class)));
        POCDMT000040Entry entry = new POCDMT000040Entry();
        entry.setOrganizer(new POCDMT000040Organizer());
        POCDMT000040Component4 component = Whitebox.invokeMethod(tested, "luoUudistamiskielto", entry);

        Assert.assertEquals("Komponenttien määrä tulee olla 2", 2, entry.getOrganizer().getComponents().size());
        Assert.assertEquals("Obervation määrä tulee olla 1", 1,
                entry.getOrganizer().getComponents().get(0).getObservation().getValues().size());
        Assert.assertTrue(
                entry.getOrganizer().getComponents().get(0).getObservation().getValues().get(0) instanceof BL);
        Assert.assertEquals("Obervation määrä tulee olla 1", 1,
                entry.getOrganizer().getComponents().get(1).getObservation().getValues().size());
        Assert.assertTrue(
                entry.getOrganizer().getComponents().get(1).getObservation().getValues().get(0) instanceof CE);
    }
}
