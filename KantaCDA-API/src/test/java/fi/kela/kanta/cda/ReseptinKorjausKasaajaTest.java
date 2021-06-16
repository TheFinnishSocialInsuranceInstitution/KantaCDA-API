package fi.kela.kanta.cda;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import javax.xml.bind.JAXBException;

import org.hl7.v3.CE;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component4;
import org.hl7.v3.POCDMT000040Reference;
import org.hl7.v3.POCDMT000040RelatedDocument;
import org.hl7.v3.XActRelationshipDocument;
import org.hl7.v3.XActRelationshipExternalReference;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import org.powermock.reflect.Whitebox;

import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.ApteekissaValmistettavaLaakeTO;
import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.LaakemaarayksenKorjausTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.OrganisaatioTO;
import fi.kela.kanta.to.ValmisteTO;
import fi.kela.kanta.to.ValmisteenYksilointitiedotTO;
import fi.kela.kanta.util.LMTOKasaaja;

public class ReseptinKorjausKasaajaTest extends LMTOKasaaja {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static String testAlkuperainenOid = "1.2.3.4.5.6";
    private static String testAlkuperainenSetId = "1.2.3.4.5.6.7";
    private static int testAlkuperainenVersio = 5;
    private static int testAlkuperainenCdaTyyppi = 1;

    private ReseptinKorjausKasaaja setupReseptinKorjausKasaaja(LaakemaarayksenKorjausTO korjaus,
            LaakemaaraysTO alkuperainenLaakemaarays) {
        KantaCDATestUtils.mockProperty(Kasaaja.BASE_OID_PROPERTY, KantaCDATestUtils.testBaseId);
        return new ReseptinKorjausKasaaja(KantaCDATestUtils.getProperties(), korjaus, alkuperainenLaakemaarays);
    }

    @Override
    protected void setupProperties() {
        KantaCDATestUtils.setupProperties();
    }

    private void setupLaakemaarayksenLeimakentat(LaakemaaraysTO laakemaaraysTO) {
        laakemaaraysTO.setOid(ReseptinKorjausKasaajaTest.testAlkuperainenOid);
        laakemaaraysTO.setSetId(ReseptinKorjausKasaajaTest.testAlkuperainenSetId);
        laakemaaraysTO.setVersio(ReseptinKorjausKasaajaTest.testAlkuperainenVersio);
        laakemaaraysTO.setCdaTyyppi(ReseptinKorjausKasaajaTest.testAlkuperainenCdaTyyppi);
    }

    @Test
    public void testPaivataKorjaus_nullAlkuperainenLaakemaarays() throws Exception {

        LaakemaarayksenKorjausTO laakemaarayksenKorjausTO = new LaakemaarayksenKorjausTO();
        Assert.assertNull("LaakemaarayksenKorjausTOn oid tulee olla null ennen paivitaKorjaus kutsua",
                laakemaarayksenKorjausTO.getOid());
        ReseptinKorjausKasaaja tested = setupReseptinKorjausKasaaja(laakemaarayksenKorjausTO, null);
        Whitebox.invokeMethod(tested, "paivataKorjaus");
        Assert.assertNull("LaakemaarayksenKorjausTOn oid tulee olla null paivitaKorjaus kutsun jälkeen",
                laakemaarayksenKorjausTO.getOid());
    }

    @Test
    public void testPaivataKorjaus_leimatiedot() throws Exception {
        LaakemaarayksenKorjausTO laakemaarayksenKorjausTO = new LaakemaarayksenKorjausTO();
        // laakemaarayksenKorjausTO.setOrgYTunnus("1234567-8");
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        setupLaakemaarayksenLeimakentat(laakemaaraysTO);
        ReseptinKorjausKasaaja tested = setupReseptinKorjausKasaaja(laakemaarayksenKorjausTO, laakemaaraysTO);
        Whitebox.invokeMethod(tested, "paivataKorjaus");
        Assert.assertEquals("korjauksen alkuperainenOid pitää olla alkuperaienenOid",
                ReseptinKorjausKasaajaTest.testAlkuperainenOid, laakemaarayksenKorjausTO.getAlkuperainenOid());
        Assert.assertEquals("korjauksen alkuperainenCdaTyyppi pitää olla alkuperainenCdaTyyppi",
                ReseptinKorjausKasaajaTest.testAlkuperainenCdaTyyppi,
                laakemaarayksenKorjausTO.getAlkuperainenCdaTyyppi());
        Assert.assertEquals("korjauksen setId pitää olla alkuperainenSetId",
                ReseptinKorjausKasaajaTest.testAlkuperainenSetId, laakemaarayksenKorjausTO.getSetId());
        Assert.assertEquals("korjauksen versio pitää olla alkuperainenVersio",
                ReseptinKorjausKasaajaTest.testAlkuperainenVersio, laakemaarayksenKorjausTO.getVersio());
    }

    @Test
    public void testPaivataKorjaus_eiKorjauksia() throws Exception {
        LaakemaarayksenKorjausTO laakemaarayksenKorjausTO = new LaakemaarayksenKorjausTO();
        // laakemaarayksenKorjausTO.setOrgYTunnus("1234567-8");
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

        ReseptinKorjausKasaaja tested = setupReseptinKorjausKasaaja(laakemaarayksenKorjausTO, laakemaaraysTO);
        Whitebox.invokeMethod(tested, "paivataKorjaus");

        // Assert.assertNotNull("korjauksella tulee olla oid", laakemaarayksenKorjausTO.getOid());
        Assert.assertEquals("korjauksen alkuperainenOid pitää olla alkuperaienenOid",
                ReseptinKorjausKasaajaTest.testAlkuperainenOid, laakemaarayksenKorjausTO.getAlkuperainenOid());
        Assert.assertEquals("korjauksen setId pitää olla alkuperainenSetId",
                ReseptinKorjausKasaajaTest.testAlkuperainenSetId, laakemaarayksenKorjausTO.getSetId());
        Assert.assertEquals("korjauksen versio pitää olla alkuperainenVersio",
                ReseptinKorjausKasaajaTest.testAlkuperainenVersio, laakemaarayksenKorjausTO.getVersio());
        Assert.assertEquals("Määräyspäivän tulee olla samat", laakemaaraysTO.getMaarayspaiva(),
                laakemaarayksenKorjausTO.getMaarayspaiva());
        Assert.assertEquals("Reseptintyyppi tulee olla: " + laakemaaraysTO.getReseptintyyppi(),
                laakemaaraysTO.getReseptintyyppi(), laakemaarayksenKorjausTO.getReseptintyyppi());
        Assert.assertEquals("Pakkauksien lukumäärä tulee olla: " + laakemaaraysTO.getPakkauksienLukumaara(),
                laakemaaraysTO.getPakkauksienLukumaara(), laakemaarayksenKorjausTO.getPakkauksienLukumaara());
        Assert.assertEquals(
                "Lääkkeen kokonaismäärä value tulee olla: " + laakemaaraysTO.getLaakkeenKokonaismaaraValue(),
                laakemaaraysTO.getLaakkeenKokonaismaaraValue(),
                laakemaarayksenKorjausTO.getLaakkeenKokonaismaaraValue());
        Assert.assertEquals("Lääkkeen kokonaismäärä unit tulee olla: " + laakemaaraysTO.getLaakkeenKokonaismaaraUnit(),
                laakemaaraysTO.getLaakkeenKokonaismaaraUnit(), laakemaarayksenKorjausTO.getLaakkeenKokonaismaaraUnit());
        Assert.assertEquals("Ajalle määrätyn reseptin alkuaika tulee olla samat",
                laakemaaraysTO.getAjalleMaaratynReseptinAlkuaika(),
                laakemaarayksenKorjausTO.getAjalleMaaratynReseptinAlkuaika());
        Assert.assertEquals(
                "Ajalle määrätyn reseptin aikamaara value tulee olla: "
                        + laakemaaraysTO.getAjalleMaaratynReseptinAikamaaraValue(),
                laakemaarayksenKorjausTO.getAjalleMaaratynReseptinAikamaaraValue(),
                laakemaaraysTO.getAjalleMaaratynReseptinAikamaaraValue());
        Assert.assertEquals(
                "Ajalle määrätyn reseptin aikamaara unit tulee olla: "
                        + laakemaaraysTO.getAjalleMaaratynReseptinAikamaaraUnit(),
                laakemaarayksenKorjausTO.getAjalleMaaratynReseptinAikamaaraUnit(),
                laakemaaraysTO.getAjalleMaaratynReseptinAikamaaraUnit());
        Assert.assertEquals("Iterointi tekstin tulee olla: " + laakemaaraysTO.getIterointiTeksti(),
                laakemaaraysTO.getIterointiTeksti(), laakemaarayksenKorjausTO.getIterointiTeksti());
        Assert.assertEquals("Iterointien määrän tulee olla: " + laakemaaraysTO.getIterointienMaara(),
                laakemaaraysTO.getIterointienMaara(), laakemaarayksenKorjausTO.getIterointienMaara());
        Assert.assertEquals("Iterointien väli valuen tulee olla: " + laakemaaraysTO.getIterointienValiValue(),
                laakemaaraysTO.getIterointienValiValue(), laakemaarayksenKorjausTO.getIterointienValiValue());
        Assert.assertEquals("Iterointien väli unitin tulee olla: " + laakemaaraysTO.getIterointienValiUnit(),
                laakemaaraysTO.getIterointienValiUnit(), laakemaarayksenKorjausTO.getIterointienValiUnit());
        Assert.assertEquals("Valmisteen tulee olla samat", laakemaaraysTO.getValmiste(),
                laakemaarayksenKorjausTO.getValmiste());
        Assert.assertEquals("Apteekissa valmistettavan lääkkeen tulee olla samat",
                laakemaaraysTO.getApteekissaValmistettavaLaake(),
                laakemaarayksenKorjausTO.getApteekissaValmistettavaLaake());
        Assert.assertEquals(
                "Lääketietokannan ulkopuolisen valmisteen tulee olla: "
                        + laakemaaraysTO.getLaaketietokannanUlkopuolinenValmiste(),
                laakemaaraysTO.getLaaketietokannanUlkopuolinenValmiste(),
                laakemaarayksenKorjausTO.getLaaketietokannanUlkopuolinenValmiste());
        Assert.assertEquals("Tyonantajan tulee olla: " + laakemaaraysTO.getTyonantaja(), laakemaaraysTO.getTyonantaja(),
                laakemaarayksenKorjausTO.getTyonantaja());
        Assert.assertEquals("Vakuutuslaitoksen tulee olla: " + laakemaaraysTO.getVakuutuslaitos(),
                laakemaaraysTO.getVakuutuslaitos(), laakemaarayksenKorjausTO.getVakuutuslaitos());
        Assert.assertEquals("Ammattihenkilön tulee olla samat", laakemaaraysTO.getAmmattihenkilo(),
                laakemaarayksenKorjausTO.getAmmattihenkilo());
        Assert.assertEquals("Lääkkeen määrääjän toimipaikan tulee olla samat",
                laakemaaraysTO.getAmmattihenkilo().getOrganisaatio(),
                laakemaarayksenKorjausTO.getAmmattihenkilo().getOrganisaatio());
        Assert.assertEquals("Potilaan tulee olla samat", laakemaaraysTO.getPotilas(),
                laakemaarayksenKorjausTO.getPotilas());
        Assert.assertTrue("Apteekissa valmistettavan lääkkeen tulee olla true",
                laakemaarayksenKorjausTO.isApteekissaValmistettavaLaake());
        Assert.assertTrue("Annostelu pelkästään tekstimuodossa tulee olla true",
                laakemaarayksenKorjausTO.isAnnosteluPelkastaanTekstimuodossa());
        Assert.assertEquals("Annostusohjeen tulee olla: " + laakemaaraysTO.getAnnostusohje(),
                laakemaaraysTO.getAnnostusohje(), laakemaarayksenKorjausTO.getAnnostusohje());
        Assert.assertTrue("SICmerkinnän tulee olla true", laakemaarayksenKorjausTO.isSICmerkinta());
        Assert.assertTrue("Lääkevaihtokiellon tulee olla true", laakemaarayksenKorjausTO.isLaakevaihtokielto());
        Assert.assertEquals("Käyttotarkoitus tekstin tulee olla: " + laakemaaraysTO.getKayttotarkoitusTeksti(),
                laakemaaraysTO.getKayttotarkoitusTeksti(), laakemaarayksenKorjausTO.getKayttotarkoitusTeksti());
        Assert.assertEquals("Alle 12 vuotiaan paino valuen tulee olla: " + laakemaaraysTO.getAlle12VuotiaanPainoValue(),
                laakemaaraysTO.getAlle12VuotiaanPainoValue(), laakemaarayksenKorjausTO.getAlle12VuotiaanPainoValue());
        Assert.assertEquals("Alle 12 vuotiaan paino unitin tulee olla: " + laakemaaraysTO.getAlle12VuotiaanPainoUnit(),
                laakemaaraysTO.getAlle12VuotiaanPainoUnit(), laakemaarayksenKorjausTO.getAlle12VuotiaanPainoUnit());
        Assert.assertTrue("Annosjakelun tulee olla true", laakemaarayksenKorjausTO.isAnnosjakelu());
        Assert.assertEquals("Annosjakelu tekstin tulee olla: " + laakemaaraysTO.getAnnosjakeluTeksti(),
                laakemaaraysTO.getAnnosjakeluTeksti(), laakemaarayksenKorjausTO.getAnnosjakeluTeksti());
        Assert.assertArrayEquals("Hoitolajit tulee olla samat", laakemaaraysTO.getHoitolajit().toArray(),
                laakemaarayksenKorjausTO.getHoitolajit().toArray());
        Assert.assertEquals("Viesti apteekille tulee olla: " + laakemaaraysTO.getViestiApteekille(),
                laakemaaraysTO.getViestiApteekille(), laakemaarayksenKorjausTO.getViestiApteekille());
        Assert.assertEquals("Erillisselvityksen tulee olla: " + laakemaaraysTO.getErillisselvitys(),
                laakemaaraysTO.getErillisselvitys(), laakemaarayksenKorjausTO.getErillisselvitys());
        Assert.assertEquals("Erillisselvitystekstin tulee olla: " + laakemaaraysTO.getErillisselvitysteksti(),
                laakemaaraysTO.getErillisselvitysteksti(), laakemaarayksenKorjausTO.getErillisselvitysteksti());
        Assert.assertEquals("Potilaan tunnistamisen tulee olla: " + laakemaaraysTO.getPotilaanTunnistaminen(),
                laakemaaraysTO.getPotilaanTunnistaminen(), laakemaarayksenKorjausTO.getPotilaanTunnistaminen());
        Assert.assertEquals(
                "Potilaan tunnistaminen tekstin tulee olla: " + laakemaaraysTO.getPotilaanTunnistaminenTeksti(),
                laakemaaraysTO.getPotilaanTunnistaminenTeksti(),
                laakemaarayksenKorjausTO.getPotilaanTunnistaminenTeksti());
        Assert.assertEquals("PKV lääkemääräyksen tulee olla: " + laakemaaraysTO.getPKVlaakemaarays(),
                laakemaaraysTO.getPKVlaakemaarays(), laakemaarayksenKorjausTO.getPKVlaakemaarays());
        Assert.assertTrue("Pysyväislääkityksen tulee olla true", laakemaarayksenKorjausTO.isPysyvaislaakitys());
        Assert.assertTrue("Kyseessä lääkkeenkäytön aloitus tulee olla true",
                laakemaarayksenKorjausTO.isKyseessaLaakkeenkaytonAloitus());
        Assert.assertTrue("Huume tulee olla true", laakemaarayksenKorjausTO.isHuume());
        Assert.assertEquals("Reseptin lajin tulee olla: " + laakemaaraysTO.getReseptinLaji(),
                laakemaaraysTO.getReseptinLaji(), laakemaarayksenKorjausTO.getReseptinLaji());
        Assert.assertTrue("Uudistamiskiellon tulee olla true", laakemaarayksenKorjausTO.isUudistamiskielto());
        Assert.assertEquals("Uusimiskiellon syyn tulee olla: " + laakemaaraysTO.getUusimiskiellonSyy(),
                laakemaaraysTO.getUusimiskiellonSyy(), laakemaarayksenKorjausTO.getUusimiskiellonSyy());
        Assert.assertEquals("Uusimiskiellon perustelun tulee olla: " + laakemaaraysTO.getUusimiskiellonPerustelu(),
                laakemaaraysTO.getUusimiskiellonPerustelu(), laakemaarayksenKorjausTO.getUusimiskiellonPerustelu());
        Assert.assertEquals("Lääketietokannan version tulee olla: " + laakemaaraysTO.getLaaketietokannanVersio(),
                laakemaaraysTO.getLaaketietokannanVersio(), laakemaarayksenKorjausTO.getLaaketietokannanVersio());
    }

    @Test
    public void testPaivataKorjaus_kaikkiaKorjattu() throws Exception {

        LaakemaarayksenKorjausTO laakemaarayksenKorjausTO = new LaakemaarayksenKorjausTO();
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

        Date korMaarayspaiva = new Date();
        Date korLaakemaarayksenVoimassaolonLoppuaika = new Date();
        String korReseptintyyppi = "reseptintyyppiKOR";
        int korPakkauksienLukumaara = 13;
        int korLaakkeenKokonaismaaraValue = 14;
        String korLaakkeenKokonaismaaraUnit = "laakkeenKokonaismaaraUnitKOR";
        Date korAjalleMaaratynReseptinAlkuaika = new Date();
        int korAjalleMaaratynReseptinAikamaaraValue = 15;
        String korAjalleMaaratynReseptinAikamaaraUnit = "ajalleMaaratynReseptinAikamaaraUnitKOR";
        String korIterointiTeksti = "iterointiTekstiKOR";
        int korIterointienMaara = 16;
        int korIterointienValiValue = 17;
        String korIterointienValiUnit = "iterointienValiUnitKOR";
        ValmisteTO korValmiste = new ValmisteTO();
        ApteekissaValmistettavaLaakeTO korApteekissaValmistettavaLaake = new ApteekissaValmistettavaLaakeTO();
        String korLaaketietokannanUlkopuolinenValmiste = "laaketietokannanUlkopuolinenValmisteKOR";
        String korTyonantaja = "tyonantajaKOR";
        String korVakuutuslaitos = "vakuutuslaitosKOR";
        AmmattihenkiloTO korAmmattihenkilo = new AmmattihenkiloTO();
        OrganisaatioTO korLaakkeenMaaraajanToimipaikka = new OrganisaatioTO();
        korAmmattihenkilo.setOrganisaatio(korLaakkeenMaaraajanToimipaikka);
        HenkilotiedotTO korPotilas = new HenkilotiedotTO(new KokoNimiTO(), "hetu");
        String korAnnostusohje = "annostusohjeKOR";
        String korKayttotarkoitusTeksti = "kayttotarkoitusTekstiKOR";
        int korAlle12VuotiaanPainoValue = 18;
        String korAlle12VuotiaanPainoUnit = "alle12VuotiaanPainoUnitKOR";
        String korAnnosjakeluTeksti = "annosjakeluTekstiKOR";
        String korHoitolaji = "hoitolajiKOR";
        String korViestiApteekille = "viestiApteekilleKOR";
        String korErillisselvitys = "erillisselvitysKOR";
        String korErillisselvitysteksti = "erillisselvitystekstiKOR";
        String korPotilaanTunnistaminen = "potilaanTunnistaminenKOR";
        String korPotilaanTunnistaminenTeksti = "potilaanTunnistaminenTekstiKOR";
        String korPKVlaakemaarays = "pKVlaakemaaraysKOR";
        String korReseptinLaji = "reseptinLajiKOR";
        String korUusimiskiellonSyy = "uusimiskiellonSyyKOR";
        String korUusimiskiellonPerustelu = "uusimiskiellonPerusteluKOR";
        String korLaaketietokannanVersio = "laaketietokannanVersioKOR";

        laakemaarayksenKorjausTO.setMaarayspaiva(korMaarayspaiva);
        laakemaarayksenKorjausTO.setLaakemaarayksenVoimassaolonLoppuaika(korLaakemaarayksenVoimassaolonLoppuaika);
        laakemaarayksenKorjausTO.setReseptintyyppi(korReseptintyyppi);
        laakemaarayksenKorjausTO.setPakkauksienLukumaara(korPakkauksienLukumaara);
        laakemaarayksenKorjausTO.setLaakkeenKokonaismaaraValue(korLaakkeenKokonaismaaraValue);
        laakemaarayksenKorjausTO.setLaakkeenKokonaismaaraUnit(korLaakkeenKokonaismaaraUnit);
        laakemaarayksenKorjausTO.setAjalleMaaratynReseptinAlkuaika(korAjalleMaaratynReseptinAlkuaika);
        laakemaarayksenKorjausTO.setAjalleMaaratynReseptinAikamaaraValue(korAjalleMaaratynReseptinAikamaaraValue);
        laakemaarayksenKorjausTO.setAjalleMaaratynReseptinAikamaaraUnit(korAjalleMaaratynReseptinAikamaaraUnit);
        laakemaarayksenKorjausTO.setIterointiTeksti(korIterointiTeksti);
        laakemaarayksenKorjausTO.setIterointienMaara(korIterointienMaara);
        laakemaarayksenKorjausTO.setIterointienValiValue(korIterointienValiValue);
        laakemaarayksenKorjausTO.setIterointienValiUnit(korIterointienValiUnit);
        laakemaarayksenKorjausTO.setValmiste(korValmiste);
        laakemaarayksenKorjausTO.setApteekissaValmistettavaLaake(korApteekissaValmistettavaLaake);
        laakemaarayksenKorjausTO.setLaaketietokannanUlkopuolinenValmiste(korLaaketietokannanUlkopuolinenValmiste);
        laakemaarayksenKorjausTO.setTyonantaja(korTyonantaja);
        laakemaarayksenKorjausTO.setVakuutuslaitos(korVakuutuslaitos);
        laakemaarayksenKorjausTO.setAmmattihenkilo(korAmmattihenkilo);
        laakemaarayksenKorjausTO.setPotilas(korPotilas);
        laakemaarayksenKorjausTO.setApteekissaValmistettavaLaake(false);
        laakemaarayksenKorjausTO.setAnnosteluPelkastaanTekstimuodossa(true);
        laakemaarayksenKorjausTO.setAnnostusohje(korAnnostusohje);
        laakemaarayksenKorjausTO.setSICmerkinta(false);
        laakemaarayksenKorjausTO.setLaakevaihtokielto(false);
        laakemaarayksenKorjausTO.setKayttotarkoitusTeksti(korKayttotarkoitusTeksti);
        laakemaarayksenKorjausTO.setAlle12VuotiaanPainoValue(new BigDecimal(18.5));
        laakemaarayksenKorjausTO.setAlle12VuotiaanPainoUnit(korAlle12VuotiaanPainoUnit);
        laakemaarayksenKorjausTO.setAnnosjakelu(false);
        laakemaarayksenKorjausTO.setAnnosjakeluTeksti(korAnnosjakeluTeksti);
        laakemaarayksenKorjausTO.getHoitolajit().add(korHoitolaji);
        laakemaarayksenKorjausTO.setViestiApteekille(korViestiApteekille);
        laakemaarayksenKorjausTO.setErillisselvitys(korErillisselvitys);
        laakemaarayksenKorjausTO.setErillisselvitysteksti(korErillisselvitysteksti);
        laakemaarayksenKorjausTO.setPotilaanTunnistaminen(korPotilaanTunnistaminen);
        laakemaarayksenKorjausTO.setPotilaanTunnistaminenTeksti(korPotilaanTunnistaminenTeksti);
        laakemaarayksenKorjausTO.setPKVlaakemaarays(korPKVlaakemaarays);
        laakemaarayksenKorjausTO.setPysyvaislaakitys(false);
        laakemaarayksenKorjausTO.setKyseessaLaakkeenkaytonAloitus(false);
        laakemaarayksenKorjausTO.setHuume(false);
        laakemaarayksenKorjausTO.setReseptinLaji(korReseptinLaji);
        laakemaarayksenKorjausTO.setUudistamiskielto(false);
        laakemaarayksenKorjausTO.setUusimiskiellonSyy(korUusimiskiellonSyy);
        laakemaarayksenKorjausTO.setUusimiskiellonPerustelu(korUusimiskiellonPerustelu);
        laakemaarayksenKorjausTO.setLaaketietokannanVersio(korLaaketietokannanVersio);
        // laakemaarayksenKorjausTO.setOrgYTunnus("1234567-8");

        ReseptinKorjausKasaaja tested = setupReseptinKorjausKasaaja(laakemaarayksenKorjausTO, laakemaaraysTO);
        Whitebox.invokeMethod(tested, "paivataKorjaus");

        Assert.assertEquals("korjauksen alkuperainenOid pitää olla alkuperaienenOid",
                ReseptinKorjausKasaajaTest.testAlkuperainenOid, laakemaarayksenKorjausTO.getAlkuperainenOid());
        Assert.assertEquals("korjauksen setId pitää olla alkuperainenSetId",
                ReseptinKorjausKasaajaTest.testAlkuperainenSetId, laakemaarayksenKorjausTO.getSetId());
        Assert.assertEquals("korjauksen versio pitää olla alkuperainenVersio",
                ReseptinKorjausKasaajaTest.testAlkuperainenVersio, laakemaarayksenKorjausTO.getVersio());
        Assert.assertEquals("Määräyspäivän tulee olla korjattu", korMaarayspaiva,
                laakemaarayksenKorjausTO.getMaarayspaiva());
        Assert.assertEquals("Lääkemääräyksen voimassaolon loppuaika tulee olla korjattu",
                korLaakemaarayksenVoimassaolonLoppuaika,
                laakemaarayksenKorjausTO.getLaakemaarayksenVoimassaolonLoppuaika());
        Assert.assertEquals("Reseptintyyppi tulee olla: " + korReseptintyyppi, korReseptintyyppi,
                laakemaarayksenKorjausTO.getReseptintyyppi());
        Assert.assertEquals("Pakkauksien lukumäärä tulee olla: " + korPakkauksienLukumaara, korPakkauksienLukumaara,
                laakemaarayksenKorjausTO.getPakkauksienLukumaara().intValue());
        Assert.assertEquals("Lääkkeen kokonaismäärä value tulee olla: " + korLaakkeenKokonaismaaraValue,
                korLaakkeenKokonaismaaraValue, laakemaarayksenKorjausTO.getLaakkeenKokonaismaaraValue().intValue());
        Assert.assertEquals("Lääkkeen kokonaismäärä unit tulee olla: " + korLaakkeenKokonaismaaraUnit,
                korLaakkeenKokonaismaaraUnit, laakemaarayksenKorjausTO.getLaakkeenKokonaismaaraUnit());
        Assert.assertEquals("Ajalle määrätyn reseptin alkuaika tulee olla korjattu", korAjalleMaaratynReseptinAlkuaika,
                laakemaarayksenKorjausTO.getAjalleMaaratynReseptinAlkuaika());
        Assert.assertEquals(
                "Ajalle määrätyn reseptin aikamaara value tulee olla: " + korAjalleMaaratynReseptinAikamaaraValue,
                korAjalleMaaratynReseptinAikamaaraValue,
                laakemaarayksenKorjausTO.getAjalleMaaratynReseptinAikamaaraValue().intValue());
        Assert.assertEquals(
                "Ajalle määrätyn reseptin aikamaara unit tulee olla: " + korAjalleMaaratynReseptinAikamaaraUnit,
                korAjalleMaaratynReseptinAikamaaraUnit,
                laakemaarayksenKorjausTO.getAjalleMaaratynReseptinAikamaaraUnit());
        Assert.assertEquals("Iterointi tekstin tulee olla: " + korIterointiTeksti, korIterointiTeksti,
                laakemaarayksenKorjausTO.getIterointiTeksti());
        Assert.assertEquals("Iterointien määrän tulee olla: " + korIterointienMaara, korIterointienMaara,
                laakemaarayksenKorjausTO.getIterointienMaara().intValue());
        Assert.assertEquals("Iterointien väli valuen tulee olla: " + korIterointienValiValue, korIterointienValiValue,
                laakemaarayksenKorjausTO.getIterointienValiValue().intValue());
        Assert.assertEquals("Iterointien väli unitin tulee olla: " + korIterointienValiUnit, korIterointienValiUnit,
                laakemaarayksenKorjausTO.getIterointienValiUnit());
        Assert.assertEquals("Valmisteen tulee olla korjattu", korValmiste, laakemaarayksenKorjausTO.getValmiste());
        Assert.assertEquals("Apteekissa valmistettavan lääkkeen tulee olla korjattu", korApteekissaValmistettavaLaake,
                laakemaarayksenKorjausTO.getApteekissaValmistettavaLaake());
        Assert.assertEquals(
                "Lääketietokannan ulkopuolisen valmisteen tulee olla: " + korLaaketietokannanUlkopuolinenValmiste,
                korLaaketietokannanUlkopuolinenValmiste,
                laakemaarayksenKorjausTO.getLaaketietokannanUlkopuolinenValmiste());
        Assert.assertEquals("Tyonantajan tulee olla: " + korTyonantaja, korTyonantaja,
                laakemaarayksenKorjausTO.getTyonantaja());
        Assert.assertEquals("Vakuutuslaitoksen tulee olla: " + korVakuutuslaitos, korVakuutuslaitos,
                laakemaarayksenKorjausTO.getVakuutuslaitos());
        Assert.assertEquals("Ammattihenkilön tulee olla korjattu", korAmmattihenkilo,
                laakemaarayksenKorjausTO.getAmmattihenkilo());
        Assert.assertEquals("Lääkkeen määrääjän toimipaikan tulee olla korjattu", korLaakkeenMaaraajanToimipaikka,
                laakemaarayksenKorjausTO.getAmmattihenkilo().getOrganisaatio());
        Assert.assertEquals("Potilaan tulee olla korjattu", korPotilas, laakemaarayksenKorjausTO.getPotilas());
        Assert.assertFalse("Apteekissa valmistettavan lääkkeen tulee olla true",
                laakemaarayksenKorjausTO.isApteekissaValmistettavaLaake());
        Assert.assertTrue("Annostelu pelkästään tekstimuodossa tulee olla true",
                laakemaarayksenKorjausTO.isAnnosteluPelkastaanTekstimuodossa());
        Assert.assertEquals("Annostusohjeen tulee olla: " + korAnnostusohje, korAnnostusohje,
                laakemaarayksenKorjausTO.getAnnostusohje());
        Assert.assertFalse("SICmerkinnän tulee olla true", laakemaarayksenKorjausTO.isSICmerkinta());
        Assert.assertFalse("Lääkevaihtokiellon tulee olla true", laakemaarayksenKorjausTO.isLaakevaihtokielto());
        Assert.assertEquals("Käyttotarkoitus tekstin tulee olla: " + korKayttotarkoitusTeksti, korKayttotarkoitusTeksti,
                laakemaarayksenKorjausTO.getKayttotarkoitusTeksti());
        Assert.assertEquals("Alle 12 vuotiaan paino valuen tulee olla: " + korAlle12VuotiaanPainoValue,
                korAlle12VuotiaanPainoValue, laakemaarayksenKorjausTO.getAlle12VuotiaanPainoValue().intValue());
        Assert.assertEquals("Alle 12 vuotiaan paino unitin tulee olla: " + korAlle12VuotiaanPainoUnit,
                korAlle12VuotiaanPainoUnit, laakemaarayksenKorjausTO.getAlle12VuotiaanPainoUnit());
        Assert.assertFalse("Annosjakelun tulee olla true", laakemaarayksenKorjausTO.isAnnosjakelu());
        Assert.assertEquals("Annosjakelu tekstin tulee olla: " + korAnnosjakeluTeksti, korAnnosjakeluTeksti,
                laakemaarayksenKorjausTO.getAnnosjakeluTeksti());
        Assert.assertEquals("Hoitolajin tulee olla: " + korHoitolaji, korHoitolaji,
                laakemaarayksenKorjausTO.getHoitolajit().iterator().next());
        Assert.assertEquals("Viesti apteekille tulee olla: " + korViestiApteekille, korViestiApteekille,
                laakemaarayksenKorjausTO.getViestiApteekille());
        Assert.assertEquals("Erillisselvityksen tulee olla: " + korErillisselvitys, korErillisselvitys,
                laakemaarayksenKorjausTO.getErillisselvitys());
        Assert.assertEquals("Erillisselvitystekstin tulee olla: " + korErillisselvitysteksti, korErillisselvitysteksti,
                laakemaarayksenKorjausTO.getErillisselvitysteksti());
        Assert.assertEquals("Potilaan tunnistamisen tulee olla: " + korPotilaanTunnistaminen, korPotilaanTunnistaminen,
                laakemaarayksenKorjausTO.getPotilaanTunnistaminen());
        Assert.assertEquals("Potilaan tunnistaminen tekstin tulee olla: " + korPotilaanTunnistaminenTeksti,
                korPotilaanTunnistaminenTeksti, laakemaarayksenKorjausTO.getPotilaanTunnistaminenTeksti());
        Assert.assertEquals("PKV lääkemääräyksen tulee olla: " + korPKVlaakemaarays, korPKVlaakemaarays,
                laakemaarayksenKorjausTO.getPKVlaakemaarays());
        Assert.assertFalse("Pysyväislääkityksen tulee olla true", laakemaarayksenKorjausTO.isPysyvaislaakitys());
        Assert.assertFalse("Kyseessä lääkkeenkäytön aloitus tulee olla true",
                laakemaarayksenKorjausTO.isKyseessaLaakkeenkaytonAloitus());
        Assert.assertFalse("Huume tulee olla true", laakemaarayksenKorjausTO.isHuume());
        Assert.assertEquals("Reseptin lajin tulee olla: " + korReseptinLaji, korReseptinLaji,
                laakemaarayksenKorjausTO.getReseptinLaji());
        Assert.assertFalse("Uudistamiskiellon tulee olla true", laakemaarayksenKorjausTO.isUudistamiskielto());
        Assert.assertEquals("Uusimiskiellon syyn tulee olla: " + korUusimiskiellonSyy, korUusimiskiellonSyy,
                laakemaarayksenKorjausTO.getUusimiskiellonSyy());
        Assert.assertEquals("Uusimiskiellon perustelun tulee olla: " + korUusimiskiellonPerustelu,
                korUusimiskiellonPerustelu, laakemaarayksenKorjausTO.getUusimiskiellonPerustelu());
        Assert.assertEquals("Lääketietokannan version tulee olla: " + korLaaketietokannanVersio,
                korLaaketietokannanVersio, laakemaarayksenKorjausTO.getLaaketietokannanVersio());
    }

    @Test
    public void testKasaaReseptinKorjaus() throws JAXBException {
        LaakemaarayksenKorjausTO laakemaarayksenKorjausTO = new LaakemaarayksenKorjausTO();
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        setupLaakemaarayksenLeimakentat(laakemaaraysTO);
        laakemaaraysTO.setPotilas(
                new HenkilotiedotTO(new KokoNimiTO("potilasEtu", "potilasSuku"), KantaCDATestUtils.testHetu));
        AmmattihenkiloTO ah = KantaCDATestUtils.luoAmmattihenkilo();
        ah.setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        laakemaaraysTO.setAmmattihenkilo(ah);
        laakemaaraysTO.setLaatimispaikka(KantaCDATestUtils.luoOrganisaatio());
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

        ReseptinKorjausKasaaja tested = setupReseptinKorjausKasaaja(laakemaarayksenKorjausTO, laakemaaraysTO);
        String cda = tested.kasaaReseptiAsiakirja();
        Assert.assertNotNull("reseptin korjaus ei saa olla null", cda);
        // System.out.println(cda);
    }

    @Test
    public void testKasaaReseptinKorjauksenKorjaus() throws JAXBException {
        LaakemaarayksenKorjausTO laakemaarayksenKorjausTO = new LaakemaarayksenKorjausTO();
        // laakemaarayksenKorjausTO.setOrgYTunnus("1234567-8");
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        setupLaakemaarayksenLeimakentat(laakemaaraysTO);
        laakemaaraysTO.setCdaTyyppi(KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYKSEN_KORJAUS.getTyyppi());
        laakemaaraysTO.setPotilas(
                new HenkilotiedotTO(new KokoNimiTO("potilasEtu", "potilasSuku"), KantaCDATestUtils.testHetu));
        laakemaaraysTO.setAmmattihenkilo(KantaCDATestUtils.luoAmmattihenkilo());
        laakemaaraysTO.getAmmattihenkilo().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        laakemaaraysTO.setLaatimispaikka(KantaCDATestUtils.luoOrganisaatio());
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

        KantaCDATestUtils.mockCodeProperty("code", "1", "test_lm_codeSystem", "test_lm_codeSystemName",
                "test_lm_displayName");
        KantaCDATestUtils.mockCodeProperty("korjaus.code", "3", "test_korjaus_codeSystem",
                "test_korjaus_codeSystemName", "test_korjaus_displayName");

        ReseptinKorjausKasaaja tested = setupReseptinKorjausKasaaja(laakemaarayksenKorjausTO, laakemaaraysTO);
        String cda = tested.kasaaReseptiAsiakirja();
        Assert.assertNotNull("reseptin korjauksen korjaus ei saa olla null", cda);
        // System.out.println(cda);
    }

    @Test
    public void testKasaaCdaRelatedDocumentTiedonKanssa() throws Exception {
        LaakemaarayksenKorjausTO laakemaarayksenKorjausTO = new LaakemaarayksenKorjausTO();
        // laakemaarayksenKorjausTO.setOrgYTunnus("1234567-8");
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        setupLaakemaarayksenLeimakentat(laakemaaraysTO);
        laakemaaraysTO.setCdaTyyppi(KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYS.getTyyppi());
        laakemaaraysTO.setPotilas(
                new HenkilotiedotTO(new KokoNimiTO("potilasEtu", "potilasSuku"), KantaCDATestUtils.testHetu));
        laakemaaraysTO.setAmmattihenkilo(KantaCDATestUtils.luoAmmattihenkilo());
        laakemaaraysTO.getAmmattihenkilo().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        laakemaaraysTO.setLaatimispaikka(KantaCDATestUtils.luoOrganisaatio());
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

        KantaCDATestUtils.mockCodeProperty("code", "1", "test_lm_codeSystem", "test_lm_codeSystemName",
                "test_lm_displayName");
        KantaCDATestUtils.mockCodeProperty("korjaus.code", "3", "test_korjaus_codeSystem",
                "test_korjaus_codeSystemName", "test_korjaus_displayName");

        ReseptinKorjausKasaaja tested = setupReseptinKorjausKasaaja(laakemaarayksenKorjausTO, laakemaaraysTO);
        Whitebox.invokeMethod(tested, "paivataKorjaus");

        POCDMT000040ClinicalDocument cda = Whitebox.invokeMethod(tested, "kasaaCdaRelatedDocumentTiedonKanssa",
                laakemaarayksenKorjausTO, laakemaaraysTO);
        Assert.assertNotNull("cda ei voi olla null", cda);
        Assert.assertNotNull("cda relatedDocument ei voi olla null", cda.getRelatedDocuments());
        Assert.assertFalse("cda relatedDocument ei voi olla tyhjä", cda.getRelatedDocuments().isEmpty());
        Assert.assertEquals("relatedDocument size tulee olla 1", 1, cda.getRelatedDocuments().size());
        POCDMT000040RelatedDocument relatedDocument = cda.getRelatedDocuments().get(0);
        Assert.assertNotNull("relatedDocument ei voi olla null", relatedDocument);
        Assert.assertEquals("relatedDocument typeCode tulee olla RPLC", XActRelationshipDocument.RPLC,
                relatedDocument.getTypeCode());
        Assert.assertEquals("relateddocument/parentDocument/code/code pitää olla alkuperäisen dokumentin tyyppi",
                laakemaaraysTO.getCdaTyyppi(),
                Integer.parseInt(relatedDocument.getParentDocument().getCode().getCode()));
    }

    @Test
    public void testKasaaCdaRelatedDocumentTiedonKanssa_KorjauksenKorjaus() throws Exception {
        LaakemaarayksenKorjausTO laakemaarayksenKorjausTO = new LaakemaarayksenKorjausTO();
        // laakemaarayksenKorjausTO.setOrgYTunnus("1234567-8");
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        setupLaakemaarayksenLeimakentat(laakemaaraysTO);
        laakemaaraysTO.setCdaTyyppi(KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYKSEN_KORJAUS.getTyyppi());
        laakemaaraysTO.setPotilas(
                new HenkilotiedotTO(new KokoNimiTO("potilasEtu", "potilasSuku"), KantaCDATestUtils.testHetu));
        laakemaaraysTO.setAmmattihenkilo(KantaCDATestUtils.luoAmmattihenkilo());
        laakemaaraysTO.getAmmattihenkilo().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        laakemaaraysTO.setLaatimispaikka(KantaCDATestUtils.luoOrganisaatio());
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

        KantaCDATestUtils.mockCodeProperty("code", "1", "test_lm_codeSystem", "test_lm_codeSystemName",
                "test_lm_displayName");
        KantaCDATestUtils.mockCodeProperty("korjaus.code", "3", "test_korjaus_codeSystem",
                "test_korjaus_codeSystemName", "test_korjaus_displayName");

        ReseptinKorjausKasaaja tested = setupReseptinKorjausKasaaja(laakemaarayksenKorjausTO, laakemaaraysTO);
        Whitebox.invokeMethod(tested, "paivataKorjaus");

        POCDMT000040ClinicalDocument cda = Whitebox.invokeMethod(tested, "kasaaCdaRelatedDocumentTiedonKanssa",
                laakemaarayksenKorjausTO, laakemaaraysTO);
        Assert.assertNotNull("cda ei voi olla null", cda);
        Assert.assertNotNull("cda relatedDocument ei voi olla null", cda.getRelatedDocuments());
        Assert.assertFalse("cda relatedDocument ei voi olla tyhjä", cda.getRelatedDocuments().isEmpty());
        Assert.assertEquals("relatedDocument size tulee olla 1", 1, cda.getRelatedDocuments().size());
        POCDMT000040RelatedDocument relatedDocument = cda.getRelatedDocuments().get(0);
        Assert.assertNotNull("relatedDocument ei voi olla null", relatedDocument);
        Assert.assertEquals("relatedDocument typeCode tulee olla RPLC", XActRelationshipDocument.RPLC,
                relatedDocument.getTypeCode());
        Assert.assertEquals("relateddocument/parentDocument/code/code pitää olla alkuperäisen dokumentin tyyppi",
                laakemaaraysTO.getCdaTyyppi(),
                Integer.parseInt(relatedDocument.getParentDocument().getCode().getCode()));
    }

    @Test
    public void testGetPropertyCode() throws Exception {
        ReseptinKorjausKasaaja tested = setupReseptinKorjausKasaaja(null, null);
        Object[] args1 = { "korjaus" };
        Assert.assertEquals("getPropertyCode tulee palauttaa :'code'", "code", Whitebox.invokeMethod(tested,
                "getPropertyCode", KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYS.getTyyppi(), args1));
        Assert.assertEquals("getPropertyCode tulee palauttaa :'korjaus.code'", "korjaus.code",
                Whitebox.invokeMethod(tested, "getPropertyCode",
                        KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYKSEN_KORJAUS.getTyyppi(), args1));
        Assert.assertNull("getPropertyCode tulee palauttaa null", Whitebox.invokeMethod(tested, "getPropertyCode",
                KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYKSEN_TOIMITUS.getTyyppi(), args1));
    }

    @Test
    public void testLuoViittaukset() throws Exception {
        KantaCDATestUtils.mockCodeProperty("korjaus.code", "3", "1.2.246.537.5.40105.2006",
                "Sähköinen lääkemääräys - Reseptisanoman tyyppi", "Lääkemääräyksen korjaus");
        KantaCDATestUtils.mockCodeProperty("contentsCode", "1", "1.2.246.537.5.40105.2006",
                "Sähköinen lääkemääräys - Reseptisanoman tyyppi", "Lääkemääräys");

        LaakemaarayksenKorjausTO laakemaarayksenKorjausTO = new LaakemaarayksenKorjausTO();
        String korjauksenOid = "1.12.123.1234";
        String korjauksenSetId = ReseptinKorjausKasaajaTest.testAlkuperainenSetId;
        laakemaarayksenKorjausTO.setOid(korjauksenOid);
        laakemaarayksenKorjausTO.setSetId(korjauksenSetId);
        laakemaarayksenKorjausTO.setAlkuperainenOid(ReseptinKorjausKasaajaTest.testAlkuperainenOid);
        ReseptinKorjausKasaaja tested = setupReseptinKorjausKasaaja(laakemaarayksenKorjausTO, null);
        Collection<POCDMT000040Reference> viittaukset = Whitebox.invokeMethod(tested, "luoViittaukset",
                laakemaarayksenKorjausTO);
        Assert.assertEquals("viittauksien määrä pitää olla 2", 2, viittaukset.size());
        for (POCDMT000040Reference viittaus : viittaukset) {
            if ( viittaus.getTypeCode() == XActRelationshipExternalReference.SPRT ) {
                tarkistaViittaus(viittaus, korjauksenOid, korjauksenSetId, "3");
            }
            else {
                tarkistaViittaus(viittaus, ReseptinKorjausKasaajaTest.testAlkuperainenOid, korjauksenSetId, "1");
            }
        }

    }

    @Test
    public void testLuoViittaukset_KorjauksenKorjaus() throws Exception {
        KantaCDATestUtils.mockCodeProperty("korjaus.code", "3", "1.2.246.537.5.40105.2006",
                "Sähköinen lääkemääräys - Reseptisanoman tyyppi", "Lääkemääräyksen korjaus");

        LaakemaarayksenKorjausTO laakemaarayksenKorjausTO = new LaakemaarayksenKorjausTO();
        String korjauksenOid = "1.12.123.1234";
        String korjauksenSetId = ReseptinKorjausKasaajaTest.testAlkuperainenSetId;
        laakemaarayksenKorjausTO.setOid(korjauksenOid);
        laakemaarayksenKorjausTO.setSetId(korjauksenSetId);
        laakemaarayksenKorjausTO.setAlkuperainenOid(ReseptinKorjausKasaajaTest.testAlkuperainenOid);
        laakemaarayksenKorjausTO
                .setAlkuperainenCdaTyyppi(KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYKSEN_KORJAUS.getTyyppi());
        ReseptinKorjausKasaaja tested = setupReseptinKorjausKasaaja(laakemaarayksenKorjausTO, null);
        Collection<POCDMT000040Reference> viittaukset = Whitebox.invokeMethod(tested, "luoViittaukset",
                laakemaarayksenKorjausTO);
        Assert.assertEquals("viittauksien määrä pitää olla 2", 2, viittaukset.size());
        for (POCDMT000040Reference viittaus : viittaukset) {
            if ( viittaus.getTypeCode() == XActRelationshipExternalReference.SPRT ) {
                tarkistaViittaus(viittaus, korjauksenOid, korjauksenSetId, "3");
            }
            else {
                tarkistaViittaus(viittaus, ReseptinKorjausKasaajaTest.testAlkuperainenOid, korjauksenSetId, "3");
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
    public void testLuoKorjauksenSyyPerusteluJaKorjaaja() throws Exception {
        String syyKoodi = "SYYKOODI";
        String korjauksenPerustelu = "korjauksenPerustelu";
        ReseptinKorjausKasaaja tested = setupReseptinKorjausKasaaja(null, null);
        Assert.assertNull("palautettava null jos parametrina ei ole LaakemaarayksenKorjausTO instanssi", Whitebox
                .invokeMethod(tested, "luoKorjauksenSyyPerusteluJaKorjaaja", Matchers.isA(LaakemaaraysTO.class)));
        LaakemaarayksenKorjausTO laakemaarayksenKorjausTO = new LaakemaarayksenKorjausTO();
        tested = setupReseptinKorjausKasaaja(laakemaarayksenKorjausTO, null);
        Assert.assertNull("palautettava null jos korjauksen syykoodi null tai tyhjä",
                Whitebox.invokeMethod(tested, "luoKorjauksenSyyPerusteluJaKorjaaja", laakemaarayksenKorjausTO));
        laakemaarayksenKorjausTO.setKorjaaja(KantaCDATestUtils.luoAmmattihenkilo());
        laakemaarayksenKorjausTO.setKorjauksenSyyKoodi(syyKoodi);
        tested = setupReseptinKorjausKasaaja(laakemaarayksenKorjausTO, null);
        POCDMT000040Component4 component = Whitebox.invokeMethod(tested, "luoKorjauksenSyyPerusteluJaKorjaaja",
                laakemaarayksenKorjausTO);
        Assert.assertEquals("observationin valueiden määrä tulee olla 1", 1,
                component.getObservation().getValues().size());
        Assert.assertEquals("observationin valuen coden tulee olla: " + syyKoodi, syyKoodi,
                ((CE) component.getObservation().getValues().get(0)).getCode());
        Assert.assertNotNull("authors ei voi olla null", component.getObservation().getAuthors());
        Assert.assertEquals("authoreja tulee olla 1", 1, component.getObservation().getAuthors().size());
        Assert.assertEquals("author time tulee olla nullFlavor NI", "NI",
                component.getObservation().getAuthors().get(0).getTime().getNullFlavors().get(0));

        laakemaarayksenKorjausTO.setKorjauksenPerustelu(korjauksenPerustelu);
        tested = setupReseptinKorjausKasaaja(laakemaarayksenKorjausTO, null);
        component = Whitebox.invokeMethod(tested, "luoKorjauksenSyyPerusteluJaKorjaaja", laakemaarayksenKorjausTO);
        Assert.assertEquals("observationin valueiden määrä tulee olla 2", 2,
                component.getObservation().getValues().size());
    }

    @Test
    public void testGetMuutTiedotCode() {
        ReseptinKorjausKasaaja tested = setupReseptinKorjausKasaaja(null, null);
        Assert.assertEquals("getMuutTiedotCode() tulee palauttaa 99", "99", tested.getMuutTiedotCode());
    }

    @Test
    public void testKasaaReseptinKorjausKirjaaja() throws JAXBException {
        LaakemaarayksenKorjausTO laakemaarayksenKorjausTO = new LaakemaarayksenKorjausTO();
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        setupLaakemaarayksenLeimakentat(laakemaaraysTO);
        laakemaaraysTO.setPotilas(
                new HenkilotiedotTO(new KokoNimiTO("potilasEtu", "potilasSuku"), KantaCDATestUtils.testHetu));
        AmmattihenkiloTO ah = KantaCDATestUtils.luoAmmattihenkilo();
        ah.setRooli("LAL");
        ah.setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        laakemaaraysTO.setAmmattihenkilo(ah);
        ah = KantaCDATestUtils.luoAmmattihenkilo();
        ah.setRooli("KIR");
        ah.setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        laakemaaraysTO.setKirjaaja(ah);
        laakemaaraysTO.setLaatimispaikka(KantaCDATestUtils.luoOrganisaatio());
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

        ReseptinKorjausKasaaja tested = setupReseptinKorjausKasaaja(laakemaarayksenKorjausTO, laakemaaraysTO);
        String cda = tested.kasaaReseptiAsiakirja();
        Assert.assertNotNull("reseptin korjaus ei saa olla null", cda);
        // System.out.println(cda);
    }
}
