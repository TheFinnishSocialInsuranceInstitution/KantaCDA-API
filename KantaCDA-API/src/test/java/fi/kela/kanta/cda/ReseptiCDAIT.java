package fi.kela.kanta.cda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component4;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040Section;
import org.hl7.v3.XActRelationshipDocument;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fi.kela.kanta.cda.Kasaaja.MaaraajanRooli;
import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.LaakemaarayksenKorjausTO;
import fi.kela.kanta.to.LaakemaarayksenLukitusTO;
import fi.kela.kanta.to.LaakemaarayksenMitatointiTO;
import fi.kela.kanta.to.LaakemaarayksenVarausTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.OrganisaatioTO;
import fi.kela.kanta.to.OsoiteTO;
import fi.kela.kanta.to.VaikuttavaAineTO;
import fi.kela.kanta.to.ValmisteTO;
import fi.kela.kanta.to.ValmisteenKayttotapaTO;
import fi.kela.kanta.to.ValmisteenMuutTiedotTO;
import fi.kela.kanta.to.ValmisteenYksilointitiedotTO;
import fi.kela.kanta.util.AsiakirjaVersioUtil;
import fi.kela.kanta.util.JaxbUtil;
import fi.kela.kanta.util.LMTOKasaaja;

/**
 * Integraatio Testi Testaa ReseptiPurkajan ja ReseptiKasaajan yhteistyökykyä
 */
public class ReseptiCDAIT extends LMTOKasaaja {
    List<String> cdaList = new ArrayList<String>();

    @Before
    public void setupTestSet() {
        setupCdaList();
    }

    private ReseptinMitatointiKasaaja setupReseptinMitatointiKasaaja(
            LaakemaarayksenMitatointiTO laakemaarayksenMitatointiTO, LaakemaaraysTO alkuperainenLaakemaaraysTO) {

        // LMTOKasaaja.utils.mockProperty(Kasaaja.BASE_OID_PROPERTY, KantaCDATestUtils.testBaseId);
        // OidGenerator.resetSequence();
        try {

            return new ReseptinMitatointiKasaaja(KantaCDATestUtils.loadProperties("testi_properties.properties"),
                    laakemaarayksenMitatointiTO, alkuperainenLaakemaaraysTO);

        }
        catch (IOException e) {
            e.printStackTrace();
            TestCase.fail();
        }
        return null;
    }

    private ReseptinMitatointiKasaajaEteenpainYht setupReseptinMitatointiKasaajaEteenpainYht(
            LaakemaarayksenMitatointiTO mitatointi, LaakemaaraysTO alkuperainenLaakemaarays,
            String alkuperainenClinicalDocument) throws IOException, JAXBException {
        return new ReseptinMitatointiKasaajaEteenpainYht(
                KantaCDATestUtils.loadProperties("testi_properties.properties"), mitatointi, alkuperainenLaakemaarays,
                JaxbUtil.getInstance().unmarshaller(alkuperainenClinicalDocument));
    }

    @Test
    public void testPuraJaKasaaResepti() throws Exception {
        String cda = KantaCDATestUtils.lataa("LM1_pkvlaake_iteroitu_pienempi_pakkaus.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO laakemaarays = purkaja.puraLaakemaarays(cda);
        assertNotNull("purettu lääkemääräys ei voi olla null", laakemaarays);
        // XXX Huom! LaakemaaraysTO:lle pitää antaa organisaation Y tunnus oidin generointia varten
        // laakemaarays.setOrgYTunnus("1234567-8");
        ReseptinUudenKasaaja kasaaja = new ReseptinUudenKasaaja(
                KantaCDATestUtils.loadProperties("testi_properties.properties"), laakemaarays);
        String uusiCda = kasaaja.kasaaReseptiAsiakirja();
        assertNotNull("uusi cda ei voi olla null", uusiCda);
    }

    @Test
    public void testPuraJaKasaaEDoctoralResepti() throws Exception {
        String cda = KantaCDATestUtils.lataa("Test_CDA/V3_3/eDoctoral/4 Renitec LM.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO laakemaarays = purkaja.puraLaakemaarays(cda);
        assertNotNull("purettu lääkemääräys ei voi olla null", laakemaarays);
        // XXX Huom! LaakemaaraysTO:lle pitää antaa organisaation Y tunnus oidin generointia varten
        // laakemaarays.setOrgYTunnus("1234567-8");
        ReseptinUudenKasaaja kasaaja = new ReseptinUudenKasaaja(
                KantaCDATestUtils.loadProperties("testi_properties.properties"), laakemaarays);
        String uusiCda = kasaaja.kasaaReseptiAsiakirja();
        assertNotNull("uusi cda ei voi olla null", uusiCda);
    }

    @Test
    public void testPuraJaKasaaMedireseptiResepti() throws Exception {
        String cda = KantaCDATestUtils.lataa("Test_CDA/V3_3/Mediresepti/2 verruxin lm.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO laakemaarays = purkaja.puraLaakemaarays(cda);
        assertNotNull("purettu lääkemääräys ei voi olla null", laakemaarays);
        // XXX Huom! LaakemaaraysTO:lle pitää antaa organisaation Y tunnus oidin generointia varten
        // laakemaarays.setOrgYTunnus("1234567-8");
        ReseptinUudenKasaaja kasaaja = new ReseptinUudenKasaaja(
                KantaCDATestUtils.loadProperties("testi_properties.properties"), laakemaarays);
        String uusiCda = kasaaja.kasaaReseptiAsiakirja();
        assertNotNull("uusi cda ei voi olla null", uusiCda);
    }

    @Test
    public void testPuraJaKasaaDynamicHealthResepti() throws Exception {
        String cda = KantaCDATestUtils.lataa("Test_CDA/V3_3/DynamicHealth/2 verruxin lm.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO laakemaarays = purkaja.puraLaakemaarays(cda);
        assertNotNull("purettu lääkemääräys ei voi olla null", laakemaarays);
        // XXX Huom! LaakemaaraysTO:lle pitää antaa organisaation Y tunnus oidin generointia varten
        // laakemaarays.setOrgYTunnus("1234567-8");
        ReseptinUudenKasaaja kasaaja = new ReseptinUudenKasaaja(
                KantaCDATestUtils.loadProperties("testi_properties.properties"), laakemaarays);
        String uusiCda = kasaaja.kasaaReseptiAsiakirja();
        assertNotNull("uusi cda ei voi olla null", uusiCda);
    }

    @Test
    public void testMitatoiResepti() throws Exception {
        String cda = KantaCDATestUtils.lataa("LM_testiasiakirja_mitatointiin.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO alkupLaakemaarays = purkaja.puraLaakemaarays(cda);
        Integer alkupKokonaisMaara = new Integer(400);

        // Tsekataan, ettei purkaessa ole kadonnut mitään odotettua
        assertNotNull("purettu lääkemääräys ei voi olla null", alkupLaakemaarays);
        assertEquals("alkuperaisen lääkemääräyksen tekijän rooli tulee olla 'LAL'", "LAL",
                alkupLaakemaarays.getAmmattihenkilo().getRooli());
        assertNull("alkuperäisen lääkemääräyksen tekijän kirjautumisaikaa ei ole asiakirjalla joten tulee olla null",
                alkupLaakemaarays.getAmmattihenkilo().getKirjautumisaika());
        assertNotNull("alkuperäisen lääkemääräyksen tekijän puhelinumeron use attribuutti ei voi olla null",
                alkupLaakemaarays.getAmmattihenkilo().getOrganisaatio().getPuhelinumeroKayttotarkoitus());
        Assert.assertNotNull("alkuperäisen reseptin palveluyksikkö ei saa olla null",
                alkupLaakemaarays.getLaatimispaikka());
        Assert.assertNotNull("alkuperäisen reseptin palveluyksikön nimi ei saa olla null",
                alkupLaakemaarays.getLaatimispaikka().getNimi());
        Assert.assertNotNull("alkuperäisen reseptin palveluyksikön osoite ei saa olla null",
                alkupLaakemaarays.getLaatimispaikka().getOsoite());
        Assert.assertNotNull("alkuperäisen reseptin palveluyksikön tunniste ei saa olla null",
                alkupLaakemaarays.getLaatimispaikka().getYksilointitunnus());
        Assert.assertEquals("alkuperäisen reseptin palveluyksikön nimi väärä", "Kotkan terveyskeskus",
                alkupLaakemaarays.getLaatimispaikka().getNimi());
        Assert.assertEquals("alkuperäisen reseptin palveluyksikön osoite - katu väärä", "Kotkantie 2",
                alkupLaakemaarays.getLaatimispaikka().getOsoite().getKatuosoite());
        Assert.assertEquals("alkuperäisen reseptin palveluyksikön osoite - pnro väärä", "50600",
                alkupLaakemaarays.getLaatimispaikka().getOsoite().getPostinumero());

        Assert.assertEquals("alkuperäisen reseptin palveluyksikön osoite - ptoimipaikka väärä", "Kotka",
                alkupLaakemaarays.getLaatimispaikka().getOsoite().getPostitoimipaikka());
        Assert.assertEquals("alkuperäisen reseptin palveluyksikön tunniste väärä", "1.2.246.10.1602257.10.1",
                alkupLaakemaarays.getLaatimispaikka().getYksilointitunnus());
        Assert.assertEquals("alkuperäisen reseptin kokonaismäärän yksikkö muuttunut", "fol",
                alkupLaakemaarays.getLaakkeenKokonaismaaraUnit());

        // Tarkastetaan että alkuperäisen lääkemääräyksen määrääjän toimintayksikön (palvelunantajan) tiedot on purettu
        // oikein
        Assert.assertNotNull("alkuperäisen reseptin toimintayksikkö ei saa olla null",
                alkupLaakemaarays.getLaatimispaikka().getToimintaYksikko());
        Assert.assertNotNull("alkuperäisen reseptin toimintayksikön nimi ei saa olla null",
                alkupLaakemaarays.getLaatimispaikka().getToimintaYksikko().getNimi());
        Assert.assertNotNull("alkuperäisen reseptin toimintayksikön osoite ei saa olla null",
                alkupLaakemaarays.getLaatimispaikka().getToimintaYksikko().getOsoite());
        Assert.assertNotNull("alkuperäisen reseptin toimintayksikön tunniste ei saa olla null",
                alkupLaakemaarays.getLaatimispaikka().getToimintaYksikko().getYksilointitunnus());
        Assert.assertEquals("alkuperäisen reseptin toimintayksikön tunniste väärä", "1.2.246.10.1602257.10.0",
                alkupLaakemaarays.getLaatimispaikka().getToimintaYksikko().getYksilointitunnus());

        // Tarkistetaan että lääkkeen määrääjällä on puhelinnumero
        Assert.assertNotNull("alkuperäisen reseptin määrääjän puhelinnumero ei saa olla null",
                alkupLaakemaarays.getAmmattihenkilo().getOrganisaatio().getPuhelinnumero());
        String alkupLALPuhelin = alkupLaakemaarays.getAmmattihenkilo().getOrganisaatio().getPuhelinnumero();

        // TODO: tarkista, että testaa "oikein" ja sitten ota assertti käyttöön
        // Assert.assertEquals("alkuperäisen reseptin organisaation osoite - puhelinnro väärä", "tel:02023456789",
        // alkupLaakemaarays.getAmmattihenkilo().getOrganisaatio().getPuhelinnumero());

        Assert.assertEquals("alkuperäisen reseptin kokonaismäärä muuttunut", alkupKokonaisMaara,
                alkupLaakemaarays.getLaakkeenKokonaismaaraValue());

        Assert.assertEquals("alkuperäisen reseptin tyyppi ei voi olla vielä muuttunut", "2",
                alkupLaakemaarays.getReseptintyyppi());

        LaakemaarayksenMitatointiTO laakemaarayksenMitatointiTO = new LaakemaarayksenMitatointiTO();
        // laakemaarayksenMitatointiTO.setOrgYTunnus("1234567-8");
        laakemaarayksenMitatointiTO.setMitatoija(KantaCDATestUtils.luoAmmattihenkilo());
        laakemaarayksenMitatointiTO.getMitatoija().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        // laatimispaikka ei saa muuttua mitätöinnissä - vaikka mitätöintiTOhon laitettaisi mitä(?)
        laakemaarayksenMitatointiTO.setLaatimispaikka(KantaCDATestUtils.luoOrganisaatio());
        laakemaarayksenMitatointiTO.setMitatoinninSyyKoodi("1");
        laakemaarayksenMitatointiTO.setMitatoinninPerustelu("mitätöinnin testi on perusteluna");

        ReseptinMitatointiKasaaja tested = setupReseptinMitatointiKasaaja(laakemaarayksenMitatointiTO,
                alkupLaakemaarays);
        String cda_mit = tested.kasaaReseptiAsiakirja();
        Assert.assertNotNull("reseptin Mitatointi ei saa olla null", cda_mit);

        // System.out.println(cda_mit);

        // tarkistetaan, ettei purkaja-kasaaja-purkaja round-tripin yhteydessä ole jäänyt mitään
        // pois tietoja
        ReseptiPurkaja rp = new ReseptiPurkaja();
        rp.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO lm = rp.puraLaakemaarays(cda_mit);
        // Laakemaarayksen_CDA_R2_Header_v3.30
        // 3.10 componentOf
        // Lääkemääräyksen määräyspäivää, -paikkaa ja palvelutapahtuman tunnusta ei saa muuttaa lääkemääräyksen
        // korjauksessa tai mitätöinnissä.

        // XXX voidaanko irrottaa laatimispaikka ammattihenkilön organisaatiosta
        Assert.assertNotNull("alkuperäisen reseptin palveluyksikkö ei saa olla null", lm.getLaatimispaikka());
        Assert.assertNotNull("alkuperäisen reseptin palveluyksikön nimi ei saa olla null",
                lm.getLaatimispaikka().getNimi());
        Assert.assertNotNull("alkuperäisen reseptin palveluyksikön osoite ei saa olla null",
                lm.getLaatimispaikka().getOsoite());
        Assert.assertNotNull("alkuperäisen reseptin palveluyksikön tunniste ei saa olla null",
                lm.getLaatimispaikka().getYksilointitunnus());
        Assert.assertEquals("alkuperäisen reseptin palveluyksikön nimi väärä", "Kotkan terveyskeskus",
                lm.getLaatimispaikka().getNimi());
        Assert.assertEquals("alkuperäisen reseptin palveluyksikön osoite - katu väärä", "Kotkantie 2",
                lm.getLaatimispaikka().getOsoite().getKatuosoite());
        Assert.assertEquals("alkuperäisen reseptin palveluyksikön osoite - pnro väärä", "50600",
                lm.getLaatimispaikka().getOsoite().getPostinumero());

        // TODO: tarkista, että testaa "oikein" ja sitten ota assertti käyttöön
        // Assert.assertEquals("alkuperäisen reseptin palveluyksikön osoite - puhelinnro väärä", "tel:02023456789",
        // lm.getAmmattihenkilo().getOrganisaatio().getPalveluYksikko().getPuhelinnumero());
        Assert.assertEquals("alkuperäisen reseptin palveluyksikön osoite - ptoimipaikka väärä", "Kotka",
                lm.getLaatimispaikka().getOsoite().getPostitoimipaikka());
        Assert.assertEquals("alkuperäisen reseptin palveluyksikön tunniste väärä", "1.2.246.10.1602257.10.1",
                lm.getLaatimispaikka().getYksilointitunnus());
        Assert.assertEquals("alkuperäisen reseptin tyyppi muuttunut", "2", lm.getReseptintyyppi());
        Assert.assertEquals("alkuperäisen reseptin kokonaismäärän yksikkö muuttunut", "fol",
                lm.getLaakkeenKokonaismaaraUnit());
        Assert.assertEquals("alkuperäisen reseptin kokonaismäärä muuttunut", alkupKokonaisMaara,
                lm.getLaakkeenKokonaismaaraValue());

        Assert.assertNotNull("alkuperäisen lääkkeen määräjän puhelinnumero ei saa olla null",
                lm.getAmmattihenkilo().getOrganisaatio().getPuhelinnumero());
        Assert.assertEquals("alkuperäisen lääkkeen määrääjän puhelinumero ei saa olla muuttunut", alkupLALPuhelin,
                lm.getAmmattihenkilo().getOrganisaatio().getPuhelinnumero());
    }

    @Test
    public void testMitatoinninTietosisalto() throws Exception {
        // String cda = utils.lataa("LM1_pkvlaake_iteroitu_pienempi_pakkaus.xml");
        String cda = KantaCDATestUtils.lataa("LM_testiasiakirja_mitatoinnin_tietosisalto.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO alkupLaakemaarays = purkaja.puraLaakemaarays(cda);
        String pkvtieto = "Z";
        String kayttotarkoitus = "Ahdistuneisuuden hoitoon.";
        String viestiApteekille = "Toimitetaan viikon annoksissa väärinkäytön estämiseksi.";
        String reseptinLaji = "1";
        List<String> hoitolajit = Arrays.asList("S", "T", "M");

        // Tsekataan, ettei purkaessa ole kadonnut mitään odotettua
        assertNotNull("purettu lääkemääräys ei voi olla null", alkupLaakemaarays);
        assertEquals("alkuperaisen lääkemääräyksen tekijän rooli tulee olla 'LAL'", "LAL",
                alkupLaakemaarays.getAmmattihenkilo().getRooli());
        assertNotNull("alkuperäisen lääkemääräyksen tekijän kirjautumisaika ei voi olla null",
                alkupLaakemaarays.getAmmattihenkilo().getKirjautumisaika());
        assertNotNull("alkuperäisen lääkemääräyksen pkv tieto ei voi olla null",
                alkupLaakemaarays.getPKVlaakemaarays());
        assertNotNull("alkuperäisen lääkemääräyksen käyttötarkoitus ei voi olla null",
                alkupLaakemaarays.getKayttotarkoitusTeksti());
        assertNotNull("alkuperäisen lääkemääräyksen viesti apteekille ei voi olla null",
                alkupLaakemaarays.getViestiApteekille());
        assertNotNull("alkuperäisen lääkemääräyksen reseptin laji ei voi olla null",
                alkupLaakemaarays.getReseptinLaji());
        assertEquals("alkuperäisen lääkemääräyksen pkv tieto tieto tulee olla: " + pkvtieto, pkvtieto,
                alkupLaakemaarays.getPKVlaakemaarays());
        assertEquals("alkuperäisen lääkemääräyksen käyttötarkoitus tulee olla: " + kayttotarkoitus, kayttotarkoitus,
                alkupLaakemaarays.getKayttotarkoitusTeksti());
        assertEquals("alkuperäisen lääkemääräyksen viesti apteekille tulee olla: " + viestiApteekille, viestiApteekille,
                alkupLaakemaarays.getViestiApteekille());
        assertEquals("alkuperäisen lääkemääräyksen reseptin laji tulee olla :" + reseptinLaji, reseptinLaji,
                alkupLaakemaarays.getReseptinLaji());
        assertEquals("alkuperäisen lääkemääräyksen hoitolajien tulee olla: S, T, M", hoitolajit,
                alkupLaakemaarays.getHoitolajit());
        assertTrue("alkuperäisen lääkemääräyksen pysyvä lääkitys tulee olla true",
                alkupLaakemaarays.isPysyvaislaakitys());
        assertTrue("alkuperäisen lääkemääräyksen uudistamiskielto tulee olla true",
                alkupLaakemaarays.isUudistamiskielto());
        assertTrue("alkuperäisen lääkemääräyksen lääkevaihtokielto tulee olla true",
                alkupLaakemaarays.isLaakevaihtokielto());
        assertTrue("alkuperäisen lääkemääräyksen annosjakelu tulee olla true", alkupLaakemaarays.isAnnosjakelu());
        assertTrue("alkuperäisen lääkemääräyksen kyseessä lääkkeen käytön aloitus tulee olla true",
                alkupLaakemaarays.isKyseessaLaakkeenkaytonAloitus());
        assertTrue("alkuperäisen lääkemääräyksen huume tulee olla true", alkupLaakemaarays.isHuume());

        Assert.assertEquals("alkuperäisen reseptin tyyppi ei voi olla vielä muuttunut", "2",
                alkupLaakemaarays.getReseptintyyppi());

        LaakemaarayksenMitatointiTO laakemaarayksenMitatointiTO = new LaakemaarayksenMitatointiTO();
        laakemaarayksenMitatointiTO.setMitatoija(KantaCDATestUtils.luoAmmattihenkilo());
        laakemaarayksenMitatointiTO.getMitatoija().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        laakemaarayksenMitatointiTO.getMitatoija().getOrganisaatio().setPuhelinumeroKayttotarkoitus("BAD");
        // laatimispaikka ei saa muuttua mitätöinnissä - vaikka mitätöintiTOhon laitettaisi mitä(?)
        laakemaarayksenMitatointiTO.setLaatimispaikka(KantaCDATestUtils.luoOrganisaatio());
        laakemaarayksenMitatointiTO.setMitatoinninSyyKoodi("1");
        laakemaarayksenMitatointiTO.setMitatoinninPerustelu("mitätöinnin testi on perusteluna");

        ReseptinMitatointiKasaaja tested = setupReseptinMitatointiKasaaja(laakemaarayksenMitatointiTO,
                alkupLaakemaarays);
        POCDMT000040ClinicalDocument mitatointicda = tested.kasaaReseptiCDA();

        // tarkistetaan että mitätöinti asiakirjalla on muut tiedot osio
        assertNotNull("mitatöinti ei voi olla null", mitatointicda);
        assertNotNull(
                "clinicalDocument/component/structuredBody/component/section/component/section/component/section ei voi olla null",
                mitatointicda.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents()
                        .get(0).getSection().getComponents().get(0).getSection());
        boolean foundMuutTiedot = false;
        for (POCDMT000040Entry entry : mitatointicda.getComponent().getStructuredBody().getComponents().get(0)
                .getSection().getComponents().get(0).getSection().getComponents().get(0).getSection().getEntries()) {
            if ( entry.getOrganizer() != null && entry.getOrganizer().getCode() != null
                    && entry.getOrganizer().getCode().getCode() != null ) {
                if ( entry.getOrganizer().getCode().getCode()
                        .equals(KantaCDAConstants.Laakityslista.LAAKEMAARAYKSEN_MITATOINNIN_MUUT_TIEDOT) ) {
                    foundMuutTiedot = true;
                }
            }
        }
        assertTrue("mitätöinnillä tulee olla lääkemääräyksen mitätöinnin muut tiedot osio", foundMuutTiedot);

        String cda_mit = tested.kasaaReseptiAsiakirja();
        Assert.assertNotNull("reseptin Mitatointi ei saa olla null", cda_mit);

        // System.out.println(cda_mit);

        // tarkistetaan, ettei purkaja-kasaaja-purkaja round-tripin yhteydessä ole jäänyt mitään
        // pois tietoja
        ReseptiPurkaja rp = new ReseptiPurkaja();
        rp.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO lm = rp.puraLaakemaarays(cda_mit);

        assertEquals("PKV tieto ei voi olla muuttunut", pkvtieto, lm.getPKVlaakemaarays());
        assertNotNull("mitätöinnin pkv tieto ei voi olla null", lm.getPKVlaakemaarays());
        assertNotNull("mitätöinnin käyttötarkoitus ei voi olla null", lm.getKayttotarkoitusTeksti());
        assertNotNull("mitätöinnin viesti apteekille ei voi olla null", lm.getViestiApteekille());
        assertNotNull("mitätöinnin reseptin laji ei voi olla null", lm.getReseptinLaji());
        assertEquals("mitätöinnin pkv tieto tieto tulee olla: " + pkvtieto, pkvtieto, lm.getPKVlaakemaarays());
        assertEquals("mitätöinnin käyttötarkoitus tulee olla: " + kayttotarkoitus, kayttotarkoitus,
                lm.getKayttotarkoitusTeksti());
        assertEquals("mitätöinnin viesti apteekille tulee olla: " + viestiApteekille, viestiApteekille,
                lm.getViestiApteekille());
        assertEquals("mitätöinnin reseptin laji tulee olla :" + reseptinLaji, reseptinLaji, lm.getReseptinLaji());
        assertEquals("mitätöinnin hoitolajien tulee olla: S, T, M", hoitolajit, lm.getHoitolajit());
        assertTrue("mitätöinnin pysyvä lääkitys tulee olla true", lm.isPysyvaislaakitys());
        assertTrue("mitätöinnin uudistamiskielto tulee olla true", lm.isUudistamiskielto());
        assertTrue("mitätöinnin lääkevaihtokielto tulee olla true", lm.isLaakevaihtokielto());
        assertTrue("mitätöinnin annosjakelu tulee olla true", lm.isAnnosjakelu());
        assertTrue("mitätöinnin kyseessä lääkkeen käytön aloitus tulee olla true",
                lm.isKyseessaLaakkeenkaytonAloitus());
        assertTrue("mitätöinnin huume tulee olla true", lm.isHuume());

    }

    /**
     * Testaa lääkemääräyksen mitätöintiä kun järjestelmän käyttämä määrittely versio on vanhempi kuin mitätöitävän
     * lääkemääräyksen määrittelyversio.
     * 
     * Huom! jos/kun testi_properties.properties asiakirjaversio.oid.jarjestelma muutetaan niin assert "Asiakirjan
     * eteenpäin yteensopivuus on oltava tuettu" saatta epäonnistua jos LM_testiasiakirja_mitatointiKasaaja2.xml
     * templateId:tä ei samalla päivitetä
     * 
     */
    @Test
    public void testMitatoiEteenpainYhteensopiva() throws Exception {
        String cdaAsiakirja = KantaCDATestUtils.lataa("LM_testiasiakirja_mitatointiKasaaja2.xml");
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));

        LaakemaaraysTO laakemaarays = purkaja.puraLaakemaarays(cdaAsiakirja);
        int templateIdCount = laakemaarays.getTemplateIds().size();

        assertTrue("Aisakirjan eteenpäin yhteensopivuus on oltava tuettu",
                laakemaarays.isAsiakirjaEteenpainYhteensopiva());

        LaakemaarayksenMitatointiTO laakemaarayksenMitatointiTO = new LaakemaarayksenMitatointiTO();
        AmmattihenkiloTO ammattihenkilo = KantaCDATestUtils.luoAmmattihenkilo();
        ammattihenkilo.setRooli(MaaraajanRooli.LAAK_ALOIT_HENK.getRooliKoodi());
        laakemaarayksenMitatointiTO.setMitatoija(ammattihenkilo);
        laakemaarayksenMitatointiTO.setMitatoinninSyyKoodi("1");
        laakemaarayksenMitatointiTO.setPotilas(luoPotilas());
        assertTrue("Alkuperäisen asiakirjanmäärittelyluokan tulee olla mallia: tuleva",
                laakemaarays.getMaarittelyLuokka() == MaarittelyLuokka.TULEVA);
        laakemaarayksenMitatointiTO.getTemplateIds().addAll(laakemaarays.getTemplateIds());

        ReseptinMitatointiKasaajaEteenpainYht mitatointiKasaaja = setupReseptinMitatointiKasaajaEteenpainYht(
                laakemaarayksenMitatointiTO, laakemaarays, cdaAsiakirja);

        POCDMT000040ClinicalDocument cda = mitatointiKasaaja.kasaaReseptiCDA();
        assertEquals(
                "Jotta eteenpäin yhteensopivuus toteutuisi tulee mitätöinti asiakirjan määrittely version olla sama kuin mikä alkuperäisen lääkemääräyksen määrittely versio",
                cda.getTemplateIds().get(0).getRoot(), laakemaarays.getTemplateIds().get(0));

        // Tarkistetaan että templateId:n määrä ei tässä lisäänny lainkaan
        assertEquals("TemplateIdiden määrä ei saa muttua", cda.getTemplateIds().size(), templateIdCount);

        boolean laakarinPalkkioLoytyy = false;
        boolean laakemaarayksenMuuttiedotLoytyy = false;
        String bodynMaaraysAikaleima = null;
        POCDMT000040Section nakymataso = cda.getComponent().getStructuredBody().getComponents().get(0).getSection();
        POCDMT000040Section hoitoprosessinVaihe = nakymataso.getComponents().get(0).getSection();
        POCDMT000040Section otsikkotaso = hoitoprosessinVaihe.getComponents().get(0).getSection();
        for (POCDMT000040Entry entry : otsikkotaso.getEntries()) {
            if ( null != entry.getOrganizer() && null != entry.getOrganizer().getCode() ) {
                if ( "83".equals(entry.getOrganizer().getCode().getCode()) ) {
                    bodynMaaraysAikaleima = entry.getOrganizer().getComponents().get(0).getSubstanceAdministration()
                            .getEffectiveTimes().get(0).getValue();
                }
                if ( "88".equals(entry.getOrganizer().getCode().getCode()) ) {
                    laakemaarayksenMuuttiedotLoytyy = true;
                }
                if ( "98".equals(entry.getOrganizer().getCode().getCode()) ) {

                    for (POCDMT000040Component4 component : entry.getOrganizer().getComponents()) {
                        if ( null != component.getObservation() && null != component.getObservation().getCode()
                                && "214".equals(component.getObservation().getCode().getCode()) ) {
                            laakarinPalkkioLoytyy = true;
                        }
                    }
                }

            }
        }

        assertFalse("Lääkemääräyksen mitätöinnissä organizer code 88 tulee muuttaa arvoon 98",
                laakemaarayksenMuuttiedotLoytyy);

        // Tarkisetaan että headerin elementissä encompassiveEncounter.effectiveTime on oltava sama aikaleima kuin
        // Bodyssa lääkemääräyksen määräyspäivä/lääkemääräyksen toimitushetki.
        assertNotNull("Bodyn aikaleima ei voi olla null", bodynMaaraysAikaleima);
        assertEquals("encompassingEncounter/effectiveTime tulee olla sama kuin bodyssa määräyspäivä",
                cda.getComponentOf().getEncompassingEncounter().getEffectiveTime().getValue(), bodynMaaraysAikaleima);
        String headerId = cda.getId().getRoot();
        String nakymatasoId = nakymataso.getId().getRoot();
        String hoitoprosessinVaiheId = hoitoprosessinVaihe.getId().getRoot();
        String otsikkotasoId = otsikkotaso.getId().getRoot();
        // Tarkistetaan että näkymätason, hoitoprosessin vaiheen ja otsikkotason id:t ovat muotoa headerin id + . ja
        // juokseva numero
        assertEquals("Nakymatason section/id:n tulee olla headerin id+.1", headerId + ".1", nakymatasoId);
        assertEquals("Hoitoprosessin vaihe section/id:n tulee olla headerin id+.2", headerId + ".2",
                hoitoprosessinVaiheId);
        assertEquals("Otsikkotason section/id:n tulee olla headerin id+.3", headerId + ".3", otsikkotasoId);

        assertTrue("Mitätöintiasiakirjalta puuttuu lääkärinpalkkio", laakarinPalkkioLoytyy);
        // System.out.println(JaxbUtil.marshalloi(cda, "urn:hl7-org:v3 CDA_Fi.xsd"));
    }

    @Test
    public void testKorjaaReseptia() throws Exception {
        // String cda = utils.lataa("LM1_pkvlaake_iteroitu_pienempi_pakkaus.xml");
        String cda = KantaCDATestUtils.lataa("LM12_potilaskohtainen_erityislupavalmiste.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO alkupLaakemaarays = purkaja.puraLaakemaarays(cda);
        assertNotNull("purettu lääkemääräys ei voi olla null", alkupLaakemaarays);
        assertEquals("alkuperaisen lääkemääräyksen tekijän rooli tulee olla 'LAL'", "LAL",
                alkupLaakemaarays.getAmmattihenkilo().getRooli());
        assertNotNull("alkuperäisen lääkemääräyksen palvelutapahtuman oid ei voi olla null",
                alkupLaakemaarays.getPalvelutapahtumanOid());
        assertNotNull("alkuperäisen lääkemääräyksen palveluyksikkö ei voi olla null",
                alkupLaakemaarays.getLaatimispaikka());
        assertNotNull("alkuperäisen lääkemääräyksen määräyspäivä ei voi olla null",
                alkupLaakemaarays.getMaarayspaiva());

        LaakemaarayksenKorjausTO korjaus = new LaakemaarayksenKorjausTO();
        korjaus.setKorjaaja(KantaCDATestUtils.luoAmmattihenkilo());
        korjaus.getKorjaaja().setOppiarvo("Lääketieteen lisensiaatti");
        korjaus.getKorjaaja().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        korjaus.setKorjauksenSyyKoodi("5");
        korjaus.setKorjauksenPerustelu("Huvinvuoksi");
        korjaus.setValmiste(alkupLaakemaarays.getValmiste());
        korjaus.getValmiste().getYksilointitiedot().setKauppanimi("PURANA");
        korjaus.setAnnosjakeluTeksti("Aina kun tarvetta");
        // FIXME: Mistä Org Y tunnus pitäisi saada
        // korjaus.setOrgYTunnus("1234567-8");
        ReseptinKorjausKasaaja kasaaja = new ReseptinKorjausKasaaja(
                KantaCDATestUtils.loadProperties("testi_properties.properties"), korjaus, alkupLaakemaarays);
        POCDMT000040ClinicalDocument doc = kasaaja.kasaaReseptiCDA();
        // String uusiCda = kasaaja.kasaaReseptiAsiakirja();
        assertNotNull("korjaus ei voi olla null", doc);
        assertFalse("clinicalDocument/author ei voi olla tyhjä", doc.getAuthors().isEmpty());
        assertEquals("korjauksessa authoreita tulee olla 2", 2, doc.getAuthors().size());
        assertEquals("1. author functionCode tulee olla 'LAL'", "LAL",
                doc.getAuthors().get(0).getFunctionCode().getCode());
        assertEquals("2. author functionCode tulee olla 'KOR'", "KOR",
                doc.getAuthors().get(1).getFunctionCode().getCode());

        assertFalse("clinicalDocument/relatedDocuments ei voi olla tyhjä", doc.getRelatedDocuments().isEmpty());
        assertEquals("relatedDocument typeCode tulee olla 'RPLC'", XActRelationshipDocument.RPLC,
                doc.getRelatedDocuments().get(0).getTypeCode());
        assertEquals(
                "relatedDocument/parentDocument/id root tulee olla alkuperaisen lääkemääräyksen id: "
                        + alkupLaakemaarays.getOid(),
                alkupLaakemaarays.getOid(),
                doc.getRelatedDocuments().get(0).getParentDocument().getIds().get(0).getRoot());
        assertEquals(
                "relatedDocument/parentDocument/setId root tulee olla alkuperaisen lääkemääräyksen setId: "
                        + alkupLaakemaarays.getSetId(),
                alkupLaakemaarays.getSetId(),
                doc.getRelatedDocuments().get(0).getParentDocument().getSetId().getRoot());
        assertEquals("relatedDocument/parentDocument/code code tulee olla alkuperaisen lääkemääräyksen tyyppi",
                String.valueOf(alkupLaakemaarays.getCdaTyyppi()),
                doc.getRelatedDocuments().get(0).getParentDocument().getCode().getCode());

        assertEquals(
                "korjauksen clinicalDocument/componentOf/encompassianEncounter/id tulee olla alkuperäisen lääkemääräyksen palvelutapahtuman oid: "
                        + alkupLaakemaarays.getPalvelutapahtumanOid(),
                alkupLaakemaarays.getPalvelutapahtumanOid(),
                doc.getComponentOf().getEncompassingEncounter().getIds().get(0).getRoot());
        assertNotNull(
                "korjauksen clinicalDocument/componentOf/encompassianEncounter/effectiveTime ei voi oll null (tulee olla alkuperäisen lääkemääräyksen määräyspäivä)",
                doc.getComponentOf().getEncompassingEncounter().getEffectiveTime().getValue());
        assertEquals(
                "korjauksen clinicalDocument/componentOf/encompassianEncounter/location/healthCareFacility/id tulee olla alkuperäisen lääkemääräyksen laatimispaikan yksilöintitunnus: "
                        + alkupLaakemaarays.getLaatimispaikka().getYksilointitunnus(),
                alkupLaakemaarays.getLaatimispaikka().getYksilointitunnus(), doc.getComponentOf()
                        .getEncompassingEncounter().getLocation().getHealthCareFacility().getIds().get(0).getRoot());

        boolean laakemaarayksenkorjauksenmuuttiedot_loytyi = false;
        boolean laakemaarayksenkorjauksenperustelu_loytyi = false;
        for (POCDMT000040Entry entry : doc.getComponent().getStructuredBody().getComponents().get(0).getSection()
                .getComponents().get(0).getSection().getComponents().get(0).getSection().getEntries()) {
            if ( entry.getOrganizer().getCode().getCode().equals("99") ) {
                laakemaarayksenkorjauksenmuuttiedot_loytyi = true;
                for (POCDMT000040Component4 component : entry.getOrganizer().getComponents()) {
                    if ( component.getObservation().getCode().getCode().equals("97") ) {
                        laakemaarayksenkorjauksenperustelu_loytyi = true;
                    }
                }
            }
        }
        assertTrue("korjauksessa tulee olla lääkemääräyksen korjauksen muut tiedot osio (99)",
                laakemaarayksenkorjauksenmuuttiedot_loytyi);
        assertTrue("korjauksessa tulee olla lääkemääräyksen korjauksen perustelu osio (97)",
                laakemaarayksenkorjauksenperustelu_loytyi);
        // System.out.println("uusiCda :"+uusiCda);
    }

    @Test
    public void testKorjaaApteekissaValmistettavanLaakemaarays() throws Exception {
        String cda = KantaCDATestUtils.lataa("LM8_ex_tempore_rakenteinen_koodattu_ainesosa.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO alkupLaakemaarays = purkaja.puraLaakemaarays(cda);
        String valmisteeenNimi = "Oksikodonilius";
        assertNotNull("purettu lääkemääräys ei voi olla null", alkupLaakemaarays);
        assertEquals("puretun lääkemääräyksen valmisteen lajin tulee olla 7 (Apteekissa valmistettu lääke)", "7",
                alkupLaakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji());
        assertEquals("puretun lääkemääräyksen valmisteen nimi tulee olla: " + valmisteeenNimi, valmisteeenNimi,
                alkupLaakemaarays.getLaaketietokannanUlkopuolinenValmiste());

        LaakemaarayksenKorjausTO korjaus = new LaakemaarayksenKorjausTO();
        korjaus.setKorjaaja(KantaCDATestUtils.luoAmmattihenkilo());
        korjaus.getKorjaaja().setOppiarvo("Lääketieteen lisensiaatti");
        korjaus.getKorjaaja().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        korjaus.setKorjauksenSyyKoodi("5");
        korjaus.setKorjauksenPerustelu("Huvinvuoksi");
        korjaus.setLaakkeenKokonaismaaraUnit("dl");
        ReseptinKorjausKasaaja kasaaja = new ReseptinKorjausKasaaja(
                KantaCDATestUtils.loadProperties("testi_properties.properties"), korjaus, alkupLaakemaarays);
        POCDMT000040ClinicalDocument doc = kasaaja.kasaaReseptiCDA();

        assertNotNull("korjaus ei voi olla null", doc);
        assertEquals("korjauksen valmisteen lajin tulee olla 7 (Apteekissa valmistettu lääke)", "7",
                korjaus.getValmiste().getYksilointitiedot().getValmisteenLaji());
        assertEquals("korjauksen valmisteen nimi tulee olla:" + valmisteeenNimi, valmisteeenNimi,
                korjaus.getLaaketietokannanUlkopuolinenValmiste());

        // System.out.println("uusiCda :"+kasaaja.kasaaReseptiAsiakirja());
    }

    @Test
    public void testKorjaaAcuteApteekissaValmistettavanLaakemaarays() throws Exception {
        String cda = KantaCDATestUtils.lataa("Test_CDA/V3_3/Acute/2_Voideseos_LM_.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO alkupLaakemaarays = purkaja.puraLaakemaarays(cda);
        assertNotNull("purettu lääkemääräys ei voi olla null", alkupLaakemaarays);
        assertEquals("puretun lääkemääräyksen valmisteen lajin tulee olla 7 (Apteekissa valmistettu lääke)", "7",
                alkupLaakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji());

        LaakemaarayksenKorjausTO korjaus = new LaakemaarayksenKorjausTO();
        korjaus.setKorjaaja(KantaCDATestUtils.luoAmmattihenkilo());
        korjaus.getKorjaaja().setOppiarvo("Lääketieteen lisensiaatti");
        korjaus.getKorjaaja().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        korjaus.setKorjauksenSyyKoodi("5");
        korjaus.setKorjauksenPerustelu("Huvinvuoksi");
        korjaus.setLaakkeenKokonaismaaraUnit("dl");
        ReseptinKorjausKasaaja kasaaja = new ReseptinKorjausKasaaja(
                KantaCDATestUtils.loadProperties("testi_properties.properties"), korjaus, alkupLaakemaarays);
        POCDMT000040ClinicalDocument doc = kasaaja.kasaaReseptiCDA();

        assertNotNull("korjaus ei voi olla null", doc);
        assertEquals("korjauksen valmisteen lajin tulee olla 7 (Apteekissa valmistettu lääke)", "7",
                korjaus.getValmiste().getYksilointitiedot().getValmisteenLaji());
    }

    @Test
    public void testKorjaaDynamicHealthApteekissaValmistettavanLaakemaarays() throws Exception {
        String cda = KantaCDATestUtils.lataa("Test_CDA/V3_3/DynamicHealth/2 voideseos lm.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO alkupLaakemaarays = purkaja.puraLaakemaarays(cda);
        assertNotNull("purettu lääkemääräys ei voi olla null", alkupLaakemaarays);
        assertEquals("puretun lääkemääräyksen valmisteen lajin tulee olla 7 (Apteekissa valmistettu lääke)", "7",
                alkupLaakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji());

        LaakemaarayksenKorjausTO korjaus = new LaakemaarayksenKorjausTO();
        korjaus.setKorjaaja(KantaCDATestUtils.luoAmmattihenkilo());
        korjaus.getKorjaaja().setOppiarvo("Lääketieteen lisensiaatti");
        korjaus.getKorjaaja().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        korjaus.setKorjauksenSyyKoodi("5");
        korjaus.setKorjauksenPerustelu("Huvinvuoksi");
        korjaus.setLaakkeenKokonaismaaraUnit("dl");
        ReseptinKorjausKasaaja kasaaja = new ReseptinKorjausKasaaja(
                KantaCDATestUtils.loadProperties("testi_properties.properties"), korjaus, alkupLaakemaarays);
        POCDMT000040ClinicalDocument doc = kasaaja.kasaaReseptiCDA();

        assertNotNull("korjaus ei voi olla null", doc);
        assertEquals("korjauksen valmisteen lajin tulee olla 7 (Apteekissa valmistettu lääke)", "7",
                korjaus.getValmiste().getYksilointitiedot().getValmisteenLaji());
    }

    @Test
    public void testKorjaaEskoApteekissaValmistettavanLaakemaarays() throws Exception {
        String cda = KantaCDATestUtils.lataa("Test_CDA/V3_3/Esko/2_Voideseos_LM.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO alkupLaakemaarays = purkaja.puraLaakemaarays(cda);
        assertNotNull("purettu lääkemääräys ei voi olla null", alkupLaakemaarays);
        assertEquals("puretun lääkemääräyksen valmisteen lajin tulee olla 7 (Apteekissa valmistettu lääke)", "7",
                alkupLaakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji());

        LaakemaarayksenKorjausTO korjaus = new LaakemaarayksenKorjausTO();
        korjaus.setKorjaaja(KantaCDATestUtils.luoAmmattihenkilo());
        korjaus.getKorjaaja().setOppiarvo("Lääketieteen lisensiaatti");
        korjaus.getKorjaaja().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        korjaus.setKorjauksenSyyKoodi("5");
        korjaus.setKorjauksenPerustelu("Huvinvuoksi");
        korjaus.setLaakkeenKokonaismaaraUnit("dl");
        ReseptinKorjausKasaaja kasaaja = new ReseptinKorjausKasaaja(
                KantaCDATestUtils.loadProperties("testi_properties.properties"), korjaus, alkupLaakemaarays);
        POCDMT000040ClinicalDocument doc = kasaaja.kasaaReseptiCDA();

        assertNotNull("korjaus ei voi olla null", doc);
        assertEquals("korjauksen valmisteen lajin tulee olla 7 (Apteekissa valmistettu lääke)", "7",
                korjaus.getValmiste().getYksilointitiedot().getValmisteenLaji());
    }

    @Test
    public void testKorjaaDoctoralReseptia() throws Exception {
        // String cda = utils.lataa("LM1_pkvlaake_iteroitu_pienempi_pakkaus.xml");
        String cda = KantaCDATestUtils.lataa("Test_CDA/V3_3/eDoctoral/1 Morfin special LM.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO alkupLaakemaarays = purkaja.puraLaakemaarays(cda);
        assertNotNull("purettu lääkemääräys ei voi olla null", alkupLaakemaarays);
        assertEquals("alkuperaisen lääkemääräyksen tekijän rooli tulee olla 'LAL'", "LAL",
                alkupLaakemaarays.getAmmattihenkilo().getRooli());
        assertNotNull("alkuperäisen lääkemääräyksen palvelutapahtuman oid ei voi olla null",
                alkupLaakemaarays.getPalvelutapahtumanOid());
        assertNotNull("alkuperäisen lääkemääräyksen palveluyksikkö ei voi olla null",
                alkupLaakemaarays.getLaatimispaikka());
        assertNotNull("alkuperäisen lääkemääräyksen määräyspäivä ei voi olla null",
                alkupLaakemaarays.getMaarayspaiva());

        LaakemaarayksenKorjausTO korjaus = new LaakemaarayksenKorjausTO();
        korjaus.setKorjaaja(KantaCDATestUtils.luoAmmattihenkilo());
        korjaus.getKorjaaja().setOppiarvo("Lääketieteen lisensiaatti");
        korjaus.getKorjaaja().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        korjaus.setKorjauksenSyyKoodi("5");
        korjaus.setKorjauksenPerustelu("Huvinvuoksi");
        korjaus.setValmiste(alkupLaakemaarays.getValmiste());
        korjaus.getValmiste().getYksilointitiedot().setKauppanimi("PURANA");
        korjaus.setAnnosjakeluTeksti("Aina kun tarvetta");
        // FIXME: Mistä Org Y tunnus pitäisi saada
        // korjaus.setOrgYTunnus("1234567-8");
        ReseptinKorjausKasaaja kasaaja = new ReseptinKorjausKasaaja(
                KantaCDATestUtils.loadProperties("testi_properties.properties"), korjaus, alkupLaakemaarays);
        POCDMT000040ClinicalDocument doc = kasaaja.kasaaReseptiCDA();
        // String uusiCda = kasaaja.kasaaReseptiAsiakirja();
        assertNotNull("korjaus ei voi olla null", doc);
        assertFalse("clinicalDocument/author ei voi olla tyhjä", doc.getAuthors().isEmpty());
        assertEquals("korjauksessa authoreita tulee olla 2", 2, doc.getAuthors().size());
        assertEquals("1. author functionCode tulee olla 'LAL'", "LAL",
                doc.getAuthors().get(0).getFunctionCode().getCode());
        assertEquals("2. author functionCode tulee olla 'KOR'", "KOR",
                doc.getAuthors().get(1).getFunctionCode().getCode());

        assertFalse("clinicalDocument/relatedDocuments ei voi olla tyhjä", doc.getRelatedDocuments().isEmpty());
        assertEquals("relatedDocument typeCode tulee olla 'RPLC'", XActRelationshipDocument.RPLC,
                doc.getRelatedDocuments().get(0).getTypeCode());
        assertEquals(
                "relatedDocument/parentDocument/id root tulee olla alkuperaisen lääkemääräyksen id: "
                        + alkupLaakemaarays.getOid(),
                alkupLaakemaarays.getOid(),
                doc.getRelatedDocuments().get(0).getParentDocument().getIds().get(0).getRoot());
        assertEquals(
                "relatedDocument/parentDocument/setId root tulee olla alkuperaisen lääkemääräyksen setId: "
                        + alkupLaakemaarays.getSetId(),
                alkupLaakemaarays.getSetId(),
                doc.getRelatedDocuments().get(0).getParentDocument().getSetId().getRoot());
        assertEquals("relatedDocument/parentDocument/code code tulee olla alkuperaisen lääkemääräyksen tyyppi",
                String.valueOf(alkupLaakemaarays.getCdaTyyppi()),
                doc.getRelatedDocuments().get(0).getParentDocument().getCode().getCode());

        assertEquals(
                "korjauksen clinicalDocument/componentOf/encompassianEncounter/id tulee olla alkuperäisen lääkemääräyksen palvelutapahtuman oid: "
                        + alkupLaakemaarays.getPalvelutapahtumanOid(),
                alkupLaakemaarays.getPalvelutapahtumanOid(),
                doc.getComponentOf().getEncompassingEncounter().getIds().get(0).getRoot());
        assertNotNull(
                "korjauksen clinicalDocument/componentOf/encompassianEncounter/effectiveTime ei voi oll null (tulee olla alkuperäisen lääkemääräyksen määräyspäivä)",
                doc.getComponentOf().getEncompassingEncounter().getEffectiveTime().getValue());
        assertEquals(
                "korjauksen clinicalDocument/componentOf/encompassianEncounter/location/healthCareFacility/id tulee olla alkuperäisen lääkemääräyksen laatimispaikan yksilöintitunnus: "
                        + alkupLaakemaarays.getLaatimispaikka().getYksilointitunnus(),
                alkupLaakemaarays.getLaatimispaikka().getYksilointitunnus(), doc.getComponentOf()
                        .getEncompassingEncounter().getLocation().getHealthCareFacility().getIds().get(0).getRoot());

        boolean laakemaarayksenkorjauksenmuuttiedot_loytyi = false;
        boolean laakemaarayksenkorjauksenperustelu_loytyi = false;
        for (POCDMT000040Entry entry : doc.getComponent().getStructuredBody().getComponents().get(0).getSection()
                .getComponents().get(0).getSection().getComponents().get(0).getSection().getEntries()) {
            if ( entry.getOrganizer().getCode().getCode().equals("99") ) {
                laakemaarayksenkorjauksenmuuttiedot_loytyi = true;
                for (POCDMT000040Component4 component : entry.getOrganizer().getComponents()) {
                    if ( component.getObservation().getCode().getCode().equals("97") ) {
                        laakemaarayksenkorjauksenperustelu_loytyi = true;
                    }
                }
            }
        }
        assertTrue("korjauksessa tulee olla lääkemääräyksen korjauksen muut tiedot osio (99)",
                laakemaarayksenkorjauksenmuuttiedot_loytyi);
        assertTrue("korjauksessa tulee olla lääkemääräyksen korjauksen perustelu osio (97)",
                laakemaarayksenkorjauksenperustelu_loytyi);
        // System.out.println("uusiCda :"+uusiCda);
    }

    @Test
    public void testKorjaaMedireseptiReseptia() throws Exception {
        // String cda = utils.lataa("LM1_pkvlaake_iteroitu_pienempi_pakkaus.xml");
        String cda = KantaCDATestUtils.lataa("Test_CDA/V3_3/Mediresepti/1 morfinspecial lm.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO alkupLaakemaarays = purkaja.puraLaakemaarays(cda);
        assertNotNull("purettu lääkemääräys ei voi olla null", alkupLaakemaarays);
        assertEquals("alkuperaisen lääkemääräyksen tekijän rooli tulee olla 'LAL'", "LAL",
                alkupLaakemaarays.getAmmattihenkilo().getRooli());
        assertNotNull("alkuperäisen lääkemääräyksen palveluyksikkö ei voi olla null",
                alkupLaakemaarays.getLaatimispaikka());
        assertNotNull("alkuperäisen lääkemääräyksen määräyspäivä ei voi olla null",
                alkupLaakemaarays.getMaarayspaiva());

        LaakemaarayksenKorjausTO korjaus = new LaakemaarayksenKorjausTO();
        korjaus.setKorjaaja(KantaCDATestUtils.luoAmmattihenkilo());
        korjaus.getKorjaaja().setOppiarvo("Lääketieteen lisensiaatti");
        korjaus.getKorjaaja().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        korjaus.setKorjauksenSyyKoodi("5");
        korjaus.setKorjauksenPerustelu("Huvinvuoksi");
        korjaus.setValmiste(alkupLaakemaarays.getValmiste());
        korjaus.getValmiste().getYksilointitiedot().setKauppanimi("PURANA");
        korjaus.setAnnosjakeluTeksti("Aina kun tarvetta");
        // FIXME: Mistä Org Y tunnus pitäisi saada
        // korjaus.setOrgYTunnus("1234567-8");
        ReseptinKorjausKasaaja kasaaja = new ReseptinKorjausKasaaja(
                KantaCDATestUtils.loadProperties("testi_properties.properties"), korjaus, alkupLaakemaarays);
        POCDMT000040ClinicalDocument doc = kasaaja.kasaaReseptiCDA();
        // String uusiCda = kasaaja.kasaaReseptiAsiakirja();
        assertNotNull("korjaus ei voi olla null", doc);
        assertFalse("clinicalDocument/author ei voi olla tyhjä", doc.getAuthors().isEmpty());
        assertEquals("korjauksessa authoreita tulee olla 2", 2, doc.getAuthors().size());
        assertEquals("1. author functionCode tulee olla 'LAL'", "LAL",
                doc.getAuthors().get(0).getFunctionCode().getCode());
        assertEquals("2. author functionCode tulee olla 'KOR'", "KOR",
                doc.getAuthors().get(1).getFunctionCode().getCode());

        assertFalse("clinicalDocument/relatedDocuments ei voi olla tyhjä", doc.getRelatedDocuments().isEmpty());
        assertEquals("relatedDocument typeCode tulee olla 'RPLC'", XActRelationshipDocument.RPLC,
                doc.getRelatedDocuments().get(0).getTypeCode());
        assertEquals(
                "relatedDocument/parentDocument/id root tulee olla alkuperaisen lääkemääräyksen id: "
                        + alkupLaakemaarays.getOid(),
                alkupLaakemaarays.getOid(),
                doc.getRelatedDocuments().get(0).getParentDocument().getIds().get(0).getRoot());
        assertEquals(
                "relatedDocument/parentDocument/setId root tulee olla alkuperaisen lääkemääräyksen setId: "
                        + alkupLaakemaarays.getSetId(),
                alkupLaakemaarays.getSetId(),
                doc.getRelatedDocuments().get(0).getParentDocument().getSetId().getRoot());
        assertEquals("relatedDocument/parentDocument/code code tulee olla alkuperaisen lääkemääräyksen tyyppi",
                String.valueOf(alkupLaakemaarays.getCdaTyyppi()),
                doc.getRelatedDocuments().get(0).getParentDocument().getCode().getCode());

        assertNotNull(
                "korjauksen clinicalDocument/componentOf/encompassianEncounter/effectiveTime ei voi oll null (tulee olla alkuperäisen lääkemääräyksen määräyspäivä)",
                doc.getComponentOf().getEncompassingEncounter().getEffectiveTime().getValue());
        assertEquals(
                "korjauksen clinicalDocument/componentOf/encompassianEncounter/location/healthCareFacility/id tulee olla alkuperäisen lääkemääräyksen laatimispaikan yksilöintitunnus: "
                        + alkupLaakemaarays.getLaatimispaikka().getYksilointitunnus(),
                alkupLaakemaarays.getLaatimispaikka().getYksilointitunnus(), doc.getComponentOf()
                        .getEncompassingEncounter().getLocation().getHealthCareFacility().getIds().get(0).getRoot());

        boolean laakemaarayksenkorjauksenmuuttiedot_loytyi = false;
        boolean laakemaarayksenkorjauksenperustelu_loytyi = false;
        for (POCDMT000040Entry entry : doc.getComponent().getStructuredBody().getComponents().get(0).getSection()
                .getComponents().get(0).getSection().getComponents().get(0).getSection().getEntries()) {
            if ( entry.getOrganizer().getCode().getCode().equals("99") ) {
                laakemaarayksenkorjauksenmuuttiedot_loytyi = true;
                for (POCDMT000040Component4 component : entry.getOrganizer().getComponents()) {
                    if ( component.getObservation().getCode().getCode().equals("97") ) {
                        laakemaarayksenkorjauksenperustelu_loytyi = true;
                    }
                }
            }
        }
        assertTrue("korjauksessa tulee olla lääkemääräyksen korjauksen muut tiedot osio (99)",
                laakemaarayksenkorjauksenmuuttiedot_loytyi);
        assertTrue("korjauksessa tulee olla lääkemääräyksen korjauksen perustelu osio (97)",
                laakemaarayksenkorjauksenperustelu_loytyi);
        // System.out.println("uusiCda :"+uusiCda);
    }

    @Test
    public void testKorjaaMediatriReseptia() throws Exception {
        // String cda = utils.lataa("LM1_pkvlaake_iteroitu_pienempi_pakkaus.xml");
        String cda = KantaCDATestUtils.lataa("Test_CDA/V3_3/Mediatri/4 renitec lm.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO alkupLaakemaarays = purkaja.puraLaakemaarays(cda);
        assertNotNull("purettu lääkemääräys ei voi olla null", alkupLaakemaarays);
        assertEquals("alkuperaisen lääkemääräyksen tekijän rooli tulee olla 'LAL'", "LAL",
                alkupLaakemaarays.getAmmattihenkilo().getRooli());
        assertNotNull("alkuperäisen lääkemääräyksen palveluyksikkö ei voi olla null",
                alkupLaakemaarays.getLaatimispaikka());
        assertNotNull("alkuperäisen lääkemääräyksen määräyspäivä ei voi olla null",
                alkupLaakemaarays.getMaarayspaiva());

        LaakemaarayksenKorjausTO korjaus = new LaakemaarayksenKorjausTO();
        korjaus.setKorjaaja(KantaCDATestUtils.luoAmmattihenkilo());
        korjaus.getKorjaaja().setOppiarvo("Lääketieteen lisensiaatti");
        korjaus.getKorjaaja().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        korjaus.setKorjauksenSyyKoodi("5");
        korjaus.setKorjauksenPerustelu("Huvinvuoksi");
        korjaus.setValmiste(alkupLaakemaarays.getValmiste());
        korjaus.getValmiste().getYksilointitiedot().setKauppanimi("PURANA");
        korjaus.setAnnosjakeluTeksti("Aina kun tarvetta");
        // FIXME: Mistä Org Y tunnus pitäisi saada
        // korjaus.setOrgYTunnus("1234567-8");
        ReseptinKorjausKasaaja kasaaja = new ReseptinKorjausKasaaja(
                KantaCDATestUtils.loadProperties("testi_properties.properties"), korjaus, alkupLaakemaarays);
        POCDMT000040ClinicalDocument doc = kasaaja.kasaaReseptiCDA();
        // String uusiCda = kasaaja.kasaaReseptiAsiakirja();
        assertNotNull("korjaus ei voi olla null", doc);
        assertFalse("clinicalDocument/author ei voi olla tyhjä", doc.getAuthors().isEmpty());
        assertEquals("korjauksessa authoreita tulee olla 2", 2, doc.getAuthors().size());
        assertEquals("1. author functionCode tulee olla 'LAL'", "LAL",
                doc.getAuthors().get(0).getFunctionCode().getCode());
        assertEquals("2. author functionCode tulee olla 'KOR'", "KOR",
                doc.getAuthors().get(1).getFunctionCode().getCode());

        assertFalse("clinicalDocument/relatedDocuments ei voi olla tyhjä", doc.getRelatedDocuments().isEmpty());
        assertEquals("relatedDocument typeCode tulee olla 'RPLC'", XActRelationshipDocument.RPLC,
                doc.getRelatedDocuments().get(0).getTypeCode());
        assertEquals(
                "relatedDocument/parentDocument/id root tulee olla alkuperaisen lääkemääräyksen id: "
                        + alkupLaakemaarays.getOid(),
                alkupLaakemaarays.getOid(),
                doc.getRelatedDocuments().get(0).getParentDocument().getIds().get(0).getRoot());
        assertEquals(
                "relatedDocument/parentDocument/setId root tulee olla alkuperaisen lääkemääräyksen setId: "
                        + alkupLaakemaarays.getSetId(),
                alkupLaakemaarays.getSetId(),
                doc.getRelatedDocuments().get(0).getParentDocument().getSetId().getRoot());
        assertEquals("relatedDocument/parentDocument/code code tulee olla alkuperaisen lääkemääräyksen tyyppi",
                String.valueOf(alkupLaakemaarays.getCdaTyyppi()),
                doc.getRelatedDocuments().get(0).getParentDocument().getCode().getCode());

        assertNotNull(
                "korjauksen clinicalDocument/componentOf/encompassianEncounter/effectiveTime ei voi oll null (tulee olla alkuperäisen lääkemääräyksen määräyspäivä)",
                doc.getComponentOf().getEncompassingEncounter().getEffectiveTime().getValue());
        assertEquals(
                "korjauksen clinicalDocument/componentOf/encompassianEncounter/location/healthCareFacility/id tulee olla alkuperäisen lääkemääräyksen laatimispaikan yksilöintitunnus: "
                        + alkupLaakemaarays.getLaatimispaikka().getYksilointitunnus(),
                alkupLaakemaarays.getLaatimispaikka().getYksilointitunnus(), doc.getComponentOf()
                        .getEncompassingEncounter().getLocation().getHealthCareFacility().getIds().get(0).getRoot());

        boolean laakemaarayksenkorjauksenmuuttiedot_loytyi = false;
        boolean laakemaarayksenkorjauksenperustelu_loytyi = false;
        for (POCDMT000040Entry entry : doc.getComponent().getStructuredBody().getComponents().get(0).getSection()
                .getComponents().get(0).getSection().getComponents().get(0).getSection().getEntries()) {
            if ( entry.getOrganizer().getCode().getCode().equals("99") ) {
                laakemaarayksenkorjauksenmuuttiedot_loytyi = true;
                for (POCDMT000040Component4 component : entry.getOrganizer().getComponents()) {
                    if ( component.getObservation().getCode().getCode().equals("97") ) {
                        laakemaarayksenkorjauksenperustelu_loytyi = true;
                    }
                }
            }
        }
        assertTrue("korjauksessa tulee olla lääkemääräyksen korjauksen muut tiedot osio (99)",
                laakemaarayksenkorjauksenmuuttiedot_loytyi);
        assertTrue("korjauksessa tulee olla lääkemääräyksen korjauksen perustelu osio (97)",
                laakemaarayksenkorjauksenperustelu_loytyi);
        // System.out.println("uusiCda :"+uusiCda);
    }

    @Test
    public void testMitaotoiERaResepti() throws Exception {
        String cda = KantaCDATestUtils.lataa("Test_CDA/V3_3/eRa/4 physiogel lm.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO alkupLaakemaarays = purkaja.puraLaakemaarays(cda);

        // Tsekataan, ettei purkaessa ole kadonnut mitään odotettua
        assertNotNull("purettu lääkemääräys ei voi olla null", alkupLaakemaarays);
        assertEquals("alkuperaisen lääkemääräyksen tekijän rooli tulee olla 'LAL'", "LAL",
                alkupLaakemaarays.getAmmattihenkilo().getRooli());
        Assert.assertNotNull("alkuperäisen reseptin palveluyksikkö ei saa olla null",
                alkupLaakemaarays.getLaatimispaikka());
        Assert.assertNotNull("alkuperäisen reseptin palveluyksikön nimi ei saa olla null",
                alkupLaakemaarays.getLaatimispaikka().getNimi());
        Assert.assertNotNull("alkuperäisen reseptin palveluyksikön osoite ei saa olla null",
                alkupLaakemaarays.getLaatimispaikka().getOsoite());
        Assert.assertNotNull("alkuperäisen reseptin palveluyksikön tunniste ei saa olla null",
                alkupLaakemaarays.getLaatimispaikka().getYksilointitunnus());

        // Tarkastetaan että alkuperäisen lääkemääräyksen määrääjän toimintayksikön (palvelunantajan) tiedot on purettu
        // oikein
        Assert.assertNotNull("alkuperäisen reseptin toimintayksikkö ei saa olla null",
                alkupLaakemaarays.getLaatimispaikka().getToimintaYksikko());
        Assert.assertNotNull("alkuperäisen reseptin toimintayksikön nimi ei saa olla null",
                alkupLaakemaarays.getLaatimispaikka().getToimintaYksikko().getNimi());
        Assert.assertNotNull("alkuperäisen reseptin toimintayksikön osoite ei saa olla null",
                alkupLaakemaarays.getLaatimispaikka().getToimintaYksikko().getOsoite());
        Assert.assertNotNull("alkuperäisen reseptin toimintayksikön tunniste ei saa olla null",
                alkupLaakemaarays.getLaatimispaikka().getToimintaYksikko().getYksilointitunnus());

        // Tarkistetaan että lääkkeen määrääjällä on puhelinnumero
        Assert.assertNotNull("alkuperäisen reseptin määrääjän puhelinnumero ei saa olla null",
                alkupLaakemaarays.getAmmattihenkilo().getOrganisaatio().getPuhelinnumero());

        // TODO: tarkista, että testaa "oikein" ja sitten ota assertti käyttöön
        // Assert.assertEquals("alkuperäisen reseptin organisaation osoite - puhelinnro väärä", "tel:02023456789",
        // alkupLaakemaarays.getAmmattihenkilo().getOrganisaatio().getPuhelinnumero());

        LaakemaarayksenMitatointiTO laakemaarayksenMitatointiTO = new LaakemaarayksenMitatointiTO();
        // laakemaarayksenMitatointiTO.setOrgYTunnus("1234567-8");
        laakemaarayksenMitatointiTO.setMitatoija(KantaCDATestUtils.luoAmmattihenkilo());
        laakemaarayksenMitatointiTO.getMitatoija().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        // laatimispaikka ei saa muuttua mitätöinnissä - vaikka mitätöintiTOhon laitettaisi mitä(?)
        laakemaarayksenMitatointiTO.setLaatimispaikka(KantaCDATestUtils.luoOrganisaatio());
        laakemaarayksenMitatointiTO.setMitatoinninSyyKoodi("1");
        laakemaarayksenMitatointiTO.setMitatoinninPerustelu("mitätöinnin testi on perusteluna");

        ReseptinMitatointiKasaaja tested = setupReseptinMitatointiKasaaja(laakemaarayksenMitatointiTO,
                alkupLaakemaarays);
        String cda_mit = tested.kasaaReseptiAsiakirja();
        Assert.assertNotNull("reseptin Mitatointi ei saa olla null", cda_mit);

        // System.out.println(cda_mit);

        // tarkistetaan, ettei purkaja-kasaaja-purkaja round-tripin yhteydessä ole jäänyt mitään
        // pois tietoja
        ReseptiPurkaja rp = new ReseptiPurkaja();
        rp.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO lm = rp.puraLaakemaarays(cda_mit);
        // Laakemaarayksen_CDA_R2_Header_v3.30
        // 3.10 componentOf
        // Lääkemääräyksen määräyspäivää, -paikkaa ja palvelutapahtuman tunnusta ei saa muuttaa lääkemääräyksen
        // korjauksessa tai mitätöinnissä.

        // XXX voidaanko irrottaa laatimispaikka ammattihenkilön organisaatiosta
        Assert.assertNotNull("alkuperäisen reseptin palveluyksikkö ei saa olla null", lm.getLaatimispaikka());
        Assert.assertNotNull("alkuperäisen reseptin palveluyksikön nimi ei saa olla null",
                lm.getLaatimispaikka().getNimi());
        Assert.assertNotNull("alkuperäisen reseptin palveluyksikön osoite ei saa olla null",
                lm.getLaatimispaikka().getOsoite());
        Assert.assertNotNull("alkuperäisen reseptin palveluyksikön tunniste ei saa olla null",
                lm.getLaatimispaikka().getYksilointitunnus());

        // TODO: tarkista, että testaa "oikein" ja sitten ota assertti käyttöön
        // Assert.assertEquals("alkuperäisen reseptin palveluyksikön osoite - puhelinnro väärä", "tel:02023456789",
        // lm.getAmmattihenkilo().getOrganisaatio().getPalveluYksikko().getPuhelinnumero());
        Assert.assertNotNull("alkuperäisen lääkkeen määräjän puhelinnumero ei saa olla null",
                lm.getAmmattihenkilo().getOrganisaatio().getPuhelinnumero());
    }

    @Test
    @Ignore
    public void testPuraReseptit() throws Exception {
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        for (String cda : cdaList) {
            String cdaFile = KantaCDATestUtils.lataa(cda);
            try {
                purkaja.puraLaakemaarays(cdaFile);
            }
            catch (Exception e) {
                System.out.println(traceSubstring(e));
                Assert.fail("Asiakirjan " + cda + " purku epäonnistui");
            }
        }
    }

    @Test
    @Ignore
    public void testPuraJaKasaaReseptit() throws Exception {
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        ReseptinUudenKasaaja kasaaja = null;
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        for (String cda : cdaList) {
            String cdaFile = KantaCDATestUtils.lataa(cda);
            try {
                LaakemaaraysTO laakemaarays = purkaja.puraLaakemaarays(cdaFile);
                kasaaja = new ReseptinUudenKasaaja(KantaCDATestUtils.loadProperties("testi_properties.properties"),
                        laakemaarays);
                String uusiCda = kasaaja.kasaaReseptiAsiakirja();
                if ( uusiCda == null ) {
                    System.out.println("Asiakirjan " + cda + " uudelleen kasaus epäonnistui");
                }
            }
            catch (Exception e) {
                System.out.println("Asiakirjan " + cda + " uudelleen kasaus epäonnistui:");
                System.out.println(traceSubstring(e));
            }
        }
    }

    @Test
    public void testVarmistaValmisteenLajiLtkUlkopuolinenValmiste() throws Exception {
        String cda = KantaCDATestUtils.lataa("Test_CDA/V3_1/eDoctoral/12 Diasporal LM.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO laakemaarays = purkaja.puraLaakemaarays(cda);
        Assert.assertNotNull("purettu lääkemääräys ei voi olla null", laakemaarays);
        Assert.assertNotNull("valmiste ei voi olla null", laakemaarays.getValmiste());
        Assert.assertNotNull("yksilöintitiedot ei voi olla null", laakemaarays.getValmiste().getYksilointitiedot());
        Assert.assertEquals("6", laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji());
    }

    @Test
    public void testVarmistaValmisteenLajiApteekissaValmistettava() throws Exception {
        String cda = KantaCDATestUtils.lataa("Test_CDA/V3_0/Esko/Voideseos LM.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO laakemaarays = purkaja.puraLaakemaarays(cda);
        Assert.assertNotNull("purettu lääkemääräys ei voi olla null", laakemaarays);
        Assert.assertNotNull("valmiste ei voi olla null", laakemaarays.getValmiste());
        Assert.assertNotNull("yksilöintitiedot ei voi olla null", laakemaarays.getValmiste().getYksilointitiedot());
        Assert.assertEquals("7", laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji());
    }

    @Test
    public void testVarmistaValmisteenLajiVaikuttavallaAineella() throws Exception {
        String cda = KantaCDATestUtils.lataa("Test_CDA/V3_0/Esko/Diatsepaami LM.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO laakemaarays = purkaja.puraLaakemaarays(cda);
        Assert.assertNotNull("purettu lääkemääräys ei voi olla null", laakemaarays);
        Assert.assertNotNull("valmiste ei voi olla null", laakemaarays.getValmiste());
        Assert.assertNotNull("yksilöintitiedot ei voi olla null", laakemaarays.getValmiste().getYksilointitiedot());
        Assert.assertEquals("9", laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji());
    }

    @Test
    public void testPuraKorjaus() throws Exception {
        String cda = KantaCDATestUtils.lataa("Test_CDA/V3_3/Esko/2_Verruxin_KOR.xml");
        Assert.assertNotNull("testi CDA ei voi olla null", cda);
        Assert.assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO lm = purkaja.puraLaakemaarays(cda);
        Assert.assertNotNull("purettu lääkemääräys ei voi olla null", lm);
        Assert.assertTrue(lm instanceof LaakemaarayksenKorjausTO);
        LaakemaarayksenKorjausTO korjaus = (LaakemaarayksenKorjausTO) lm;
        Assert.assertNotNull(korjaus.getAmmattihenkilo());
        Assert.assertNotNull(korjaus.getKorjaaja());
        Assert.assertNotNull(korjaus.getAmmattihenkilo().getKokonimi());
        Assert.assertNotNull(korjaus.getKorjaaja().getKokonimi());
        Assert.assertNotNull(korjaus.getAmmattihenkilo().getOrganisaatio());
        Assert.assertNotNull(korjaus.getKorjaaja().getOrganisaatio());
        Assert.assertEquals("Talvinen Tes", korjaus.getAmmattihenkilo().getKokonimi().getSukunimi());
        Assert.assertEquals("Backman Tes", korjaus.getKorjaaja().getKokonimi().getSukunimi());
        Assert.assertEquals("Laina", korjaus.getAmmattihenkilo().getKokonimi().getEtunimi());
        Assert.assertEquals("Inkeri", korjaus.getKorjaaja().getKokonimi().getEtunimi());
        Assert.assertEquals("Atk.os.er, OYS, PPSHP", korjaus.getAmmattihenkilo().getOrganisaatio().getNimi());
        Assert.assertEquals("Atk.os.er, OYS, PPSHP", korjaus.getKorjaaja().getOrganisaatio().getNimi());
        Assert.assertEquals("030700", korjaus.getAmmattihenkilo().getSvNumero());
        Assert.assertEquals("100073", korjaus.getKorjaaja().getSvNumero());
        Assert.assertEquals("01098701608", korjaus.getAmmattihenkilo().getRekisterointinumero());
        Assert.assertEquals("00398703108", korjaus.getKorjaaja().getRekisterointinumero());
        // Assert.assertNotNull(korjaus.getNayttomuoto());
        // Assert.assertNotNull(korjaus.getNayttomuoto().size() == 7);
    }

    @Test
    public void testPuraKorjausLaatijaKorjaajaNayttomuoto() throws Exception {
        String cda = KantaCDATestUtils.lataa("Test_CDA/V3_3/DynamicHealth/2 verruxin kor.xml");
        Assert.assertNotNull("testi CDA ei voi olla null", cda);
        Assert.assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO lm = purkaja.puraLaakemaarays(cda);
        Assert.assertNotNull("purettu lääkemääräys ei voi olla null", lm);
        Assert.assertTrue(lm instanceof LaakemaarayksenKorjausTO);
        LaakemaarayksenKorjausTO korjaus = (LaakemaarayksenKorjausTO) lm;
        Assert.assertNotNull(korjaus.getNayttomuoto());
        Assert.assertNotNull(korjaus.getNayttomuoto().size() == 7);
    }

    @Test
    public void testPuraLaatijaNayttomuoto() throws Exception {
        String cda = KantaCDATestUtils.lataa("Test_CDA/V3_3/DynamicHealth/2 verruxin lm.xml");
        Assert.assertNotNull("testi CDA ei voi olla null", cda);
        Assert.assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO lm = purkaja.puraLaakemaarays(cda);
        Assert.assertNotNull("purettu lääkemääräys ei voi olla null", lm);
        Assert.assertNotNull(lm.getNayttomuoto());
        Assert.assertNotNull(lm.getNayttomuoto().size() == 3);
    }

    @Override
    protected void setupProperties() {
        KantaCDATestUtils.setupProperties();
    }

    public void setupCdaList() {
        cdaList.add("Test_CDA/V3_1/Amantadiini_v1_POHJA.xml");
        cdaList.add("Test_CDA/V3_1/Relenza_v1_POHJA.xml");
        cdaList.add("Test_CDA/V3_1/eDoctoral/11 Tenox LM.xml");
        cdaList.add("Test_CDA/V3_1/eDoctoral/12 Diasporal LM.xml");
        cdaList.add("Test_CDA/V3_1/eDoctoral/12 Dolcontin LM.xml");
        cdaList.add("Test_CDA/V3_1/eDoctoral/12 Nutridrink LM.xml");
        cdaList.add("Test_CDA/V3_3/Acute/1_Diatsepaami_LM_.xml");
        cdaList.add("Test_CDA/V3_3/Acute/1_Leponex_LM_.xml");
        cdaList.add("Test_CDA/V3_3/Acute/1_Leponex_UUS.xml");
        cdaList.add("Test_CDA/V3_3/Acute/1_MorfinSpecial_LM_.xml");
        cdaList.add("Test_CDA/V3_3/Acute/2_Renavit_LM_.xml");
        cdaList.add("Test_CDA/V3_3/Acute/2_Verruxin_KOR.xml");
        cdaList.add("Test_CDA/V3_3/Acute/2_Verruxin_LM_.xml");
        cdaList.add("Test_CDA/V3_3/Acute/2_Voideseos_KOR.xml");
        cdaList.add("Test_CDA/V3_3/Acute/2_Voideseos_LM_.xml");
        cdaList.add("Test_CDA/V3_3/Acute/2_Voideseos_UUS.xml");
        cdaList.add("Test_CDA/V3_3/Acute/4_Physiogel_LM_.xml");
        cdaList.add("Test_CDA/V3_3/Acute/4_Renitec_KOR.xml");
        cdaList.add("Test_CDA/V3_3/Acute/4_Renitec_LM_.xml");
        cdaList.add("Test_CDA/V3_3/Acute/4_Renitec_MIT.xml");
        cdaList.add("Test_CDA/V3_3/Acute/5_ENfamil_hetuton.xml");
        cdaList.add("Test_CDA/V3_3/DynamicHealth/1 diatsepaami lm.xml");
        cdaList.add("Test_CDA/V3_3/DynamicHealth/1 leponex lm.xml");
        cdaList.add("Test_CDA/V3_3/DynamicHealth/1 leponex lm2.xml");
        cdaList.add("Test_CDA/V3_3/DynamicHealth/1 morfinspecial lm.xml");
        cdaList.add("Test_CDA/V3_3/DynamicHealth/2 renavit lm.xml");
        cdaList.add("Test_CDA/V3_3/DynamicHealth/2 verruxin kor.xml");
        cdaList.add("Test_CDA/V3_3/DynamicHealth/2 voideseos kor.xml");
        cdaList.add("Test_CDA/V3_3/DynamicHealth/2 voideseos lm.xml");
        cdaList.add("Test_CDA/V3_3/DynamicHealth/2 voideseos uus.xml");
        cdaList.add("Test_CDA/V3_3/eDoctoral/1 diatsepaami LM.xml");
        cdaList.add("Test_CDA/V3_3/eDoctoral/1 Leponex LM.xml");
        cdaList.add("Test_CDA/V3_3/eDoctoral/1 Morfin special LM.xml");
        cdaList.add("Test_CDA/V3_3/eDoctoral/11 Tenox KOR.xml");
        cdaList.add("Test_CDA/V3_3/eDoctoral/12 Diasporal MIT.xml");
        cdaList.add("Test_CDA/V3_3/eDoctoral/12 Dolcontin MIT.xml");
        cdaList.add("Test_CDA/V3_3/eDoctoral/12 Nutridrink MIT.xml");
        cdaList.add("Test_CDA/V3_3/eDoctoral/13 Relenza LM.xml");
        cdaList.add("Test_CDA/V3_3/eDoctoral/4 Physiogel LM.xml");
        cdaList.add("Test_CDA/V3_3/eDoctoral/4 Physiogel MIT.xml");
        cdaList.add("Test_CDA/V3_3/eDoctoral/4 Renitec KOR.xml");
        cdaList.add("Test_CDA/V3_3/eDoctoral/4 Renitec LM.xml");
        cdaList.add("Test_CDA/V3_3/eDoctoral/4 Renitec MIT.xml");
        cdaList.add("Test_CDA/V3_3/eDoctoral/5 Enfamil LM.xml");
        cdaList.add("Test_CDA/V3_3/eDoctoral/5 Enfamil MIT.xml");
        cdaList.add("Test_CDA/V3_3/eDoctoral/5 Nutrilon KOR.xml");
        cdaList.add("Test_CDA/V3_3/eDoctoral/5 Nutrilon LM.xml");
        cdaList.add("Test_CDA/V3_3/eRa/1 diatsepaami lm.xml");
        cdaList.add("Test_CDA/V3_3/eRa/1 leponex lm.xml");
        cdaList.add("Test_CDA/V3_3/eRa/1 leponex uus.xml");
        cdaList.add("Test_CDA/V3_3/eRa/1 morfin special lm.xml");
        cdaList.add("Test_CDA/V3_3/eRa/4 physiogel kor.xml");
        cdaList.add("Test_CDA/V3_3/eRa/4 physiogel lm.xml");
        cdaList.add("Test_CDA/V3_3/eRa/4 physiogel mit.xml");
        cdaList.add("Test_CDA/V3_3/eRa/4 remitec mit.xml");
        cdaList.add("Test_CDA/V3_3/eRa/4 renitec lm.xml");
        cdaList.add("Test_CDA/V3_3/Esko/1_Diatsepaami_LM_398.xml");
        cdaList.add("Test_CDA/V3_3/Esko/1_Diatsepaami_LM.xml");
        cdaList.add("Test_CDA/V3_3/Esko/1_Diatsepaami_MIT.xml");
        cdaList.add("Test_CDA/V3_3/Esko/1_Diatsepaami_UUS.xml");
        cdaList.add("Test_CDA/V3_3/Esko/1_Leponex_LM_399.xml");
        cdaList.add("Test_CDA/V3_3/Esko/1_Leponex_LM.xml");
        cdaList.add("Test_CDA/V3_3/Esko/1_Leponex_MIT.xml");
        cdaList.add("Test_CDA/V3_3/Esko/1_Leponex_UUS.xml");
        cdaList.add("Test_CDA/V3_3/Esko/1_MorfinSpecial_LM_397.xml");
        cdaList.add("Test_CDA/V3_3/Esko/1_MorfinSpecial_LM.xml");
        cdaList.add("Test_CDA/V3_3/Esko/1_MorfinSpecial_MIT.xml");
        cdaList.add("Test_CDA/V3_3/Esko/1_MorfinSpecial_UUS.xml");
        cdaList.add("Test_CDA/V3_3/Esko/2_Renavit_LM.xml");
        cdaList.add("Test_CDA/V3_3/Esko/2_Verruxin_KOR.xml");
        cdaList.add("Test_CDA/V3_3/Esko/2_Verruxin_LM.xml");
        cdaList.add("Test_CDA/V3_3/Esko/2_Voideseos_KOR.xml");
        cdaList.add("Test_CDA/V3_3/Esko/2_Voideseos_LM.xml");
        cdaList.add("Test_CDA/V3_3/Esko/2_Voideseos_UUS.xml");
        cdaList.add("Test_CDA/V3_3/Esko/3_Ceridal_LM.xml");
        cdaList.add("Test_CDA/V3_3/Esko/3_Zopinox_LM.xml");
        cdaList.add("Test_CDA/V3_3/Esko/4_Physiogel_LM.xml");
        cdaList.add("Test_CDA/V3_3/Esko/4_Physiogel_MIT.xml");
        cdaList.add("Test_CDA/V3_3/Esko/4_Renitec_KOR.xml");
        cdaList.add("Test_CDA/V3_3/Esko/4_Renitec_LM.xml");
        cdaList.add("Test_CDA/V3_3/Esko/4_Renitec_MIT.xml");
        cdaList.add("Test_CDA/V3_3/Esko/5_Enfamil_hetuton.xml");
        cdaList.add("Test_CDA/V3_3/Esko/5_Enfamil_MIT.xml");
        cdaList.add("Test_CDA/V3_3/Esko/5_Nutrilon_LM_hetuton.xml");
        cdaList.add("Test_CDA/V3_3/GFS/1_Diatsepaami_LM.xml");
        cdaList.add("Test_CDA/V3_3/GFS/1_Leponex_LM.xml");
        cdaList.add("Test_CDA/V3_3/GFS/1_Leponex_UUS.xml");
        cdaList.add("Test_CDA/V3_3/GFS/1_Morfin_Special_LM.xml");
        cdaList.add("Test_CDA/V3_3/GFS/2_Renavit_LM.xml");
        cdaList.add("Test_CDA/V3_3/GFS/3_Ceridal_LM.xml");
        cdaList.add("Test_CDA/V3_3/GFS/3_Zopinox_LM.xml");
        cdaList.add("Test_CDA/V3_3/Mediatri/4 physiogel lm.xml");
        cdaList.add("Test_CDA/V3_3/Mediatri/4 physiogel mit.xml");
        cdaList.add("Test_CDA/V3_3/Mediatri/4 renitec kor2.xml");
        cdaList.add("Test_CDA/V3_3/Mediatri/4 renitec lm.xml");
        cdaList.add("Test_CDA/V3_3/Mediatri/4 renitec mit.xml");
        cdaList.add("Test_CDA/V3_3/Mediresepti/1 diatsepaami lm.xml");
        cdaList.add("Test_CDA/V3_3/Mediresepti/1 leponex lm.xml");
        cdaList.add("Test_CDA/V3_3/Mediresepti/1 morfinspecial lm.xml");
        cdaList.add("Test_CDA/V3_3/Mediresepti/2 verruxin kor.xml");
        cdaList.add("Test_CDA/V3_3/Mediresepti/2 verruxin lm.xml");
        cdaList.add("Test_CDA/V3_3/Mediresepti/4 physiogel lm.xml");
        cdaList.add("Test_CDA/V3_3/Mediresepti/4 physiogel mit.xml");
        cdaList.add("Test_CDA/V3_3/Mediresepti/4 remitec mit.xml");
        cdaList.add("Test_CDA/V3_3/Mediresepti/4 renitec kor.xml");
        cdaList.add("Test_CDA/V3_3/Mediresepti/4 renitec lm.xml");
        cdaList.add("Test_CDA/V3_0/Esko/Voideseos LM.xml");
        cdaList.add("Test_CDA/V3_0/Esko/Diatsepaami LM.xml");
    }

    private String traceSubstring(Exception e) {
        String trace = "";
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        String tmp = writer.toString();
        if ( tmp != null && !tmp.isEmpty() ) {
            int end = tmp.length() < 350 ? tmp.length() : 350;
            trace = tmp.substring(0, end);
        }
        return trace;
    }

    @Test
    public void testPuraLukitusAsiakirja() throws Exception {
        String cda = KantaCDATestUtils.lataa("LM_lukitus.xml");
        Assert.assertNotNull("testi CDA ei voi olla null", cda);
        Assert.assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptinLukituksenPurkaja purkaja = new ReseptinLukituksenPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaarayksenLukitusTO lm = purkaja.puraReseptinLukitus(cda);
        Assert.assertNotNull("purettu lääkemääräys ei voi olla null", lm);
        Assert.assertTrue(lm instanceof LaakemaarayksenLukitusTO);
        LaakemaarayksenLukitusTO lukitus = (LaakemaarayksenLukitusTO) lm;
        Assert.assertNotNull(lukitus.getAmmattihenkilo());
        Assert.assertNotNull(lukitus.getAmmattihenkilo().getKokonimi());
        Assert.assertNotNull(lukitus.getAmmattihenkilo().getOrganisaatio());
        Assert.assertNotNull(lukitus.getSelitys());
        Assert.assertEquals("Lähdekivi Tes", lukitus.getAmmattihenkilo().getKokonimi().getSukunimi());
        Assert.assertEquals("Uno", lukitus.getAmmattihenkilo().getKokonimi().getEtunimi());
        Assert.assertEquals("Kelan MAXX apteekki", lukitus.getAmmattihenkilo().getOrganisaatio().getNimi());
        Assert.assertEquals("00698700481", lukitus.getAmmattihenkilo().getRekisterointinumero());
    }

    @Test
    public void testPuraVarausAsiakirja() throws Exception {
        String cda = KantaCDATestUtils.lataa("LM_varaus.xml");
        Assert.assertNotNull("testi CDA ei voi olla null", cda);
        Assert.assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptinVarauksenPurkaja purkaja = new ReseptinVarauksenPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaarayksenVarausTO lm = purkaja.puraReseptinVaraus(cda);
        Assert.assertNotNull("purettu lääkemääräys ei voi olla null", lm);
        Assert.assertTrue(lm instanceof LaakemaarayksenVarausTO);
        LaakemaarayksenVarausTO varaus = (LaakemaarayksenVarausTO) lm;
        Assert.assertNotNull(varaus.getAmmattihenkilo());
        Assert.assertNotNull(varaus.getAmmattihenkilo().getKokonimi());
        Assert.assertNotNull(varaus.getAmmattihenkilo().getOrganisaatio());
        Assert.assertNotNull(varaus.getSelitys());
        Assert.assertEquals("Lähdekivi Tes", varaus.getAmmattihenkilo().getKokonimi().getSukunimi());
        Assert.assertEquals("Uno", varaus.getAmmattihenkilo().getKokonimi().getEtunimi());
        Assert.assertEquals("Kelan MAXX apteekki", varaus.getAmmattihenkilo().getOrganisaatio().getNimi());
        Assert.assertEquals("Varauksen syy on salainen ehkä.", varaus.getSelitys());
        Assert.assertEquals("00698700481", varaus.getAmmattihenkilo().getRekisterointinumero());
    }

    @Test
    public void testPuraAnnosteluVarausAsiakirja() throws Exception {
        String cda = KantaCDATestUtils.lataa("LM_annosjakelu.xml");
        Assert.assertNotNull("testi CDA ei voi olla null", cda);
        Assert.assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptinVarauksenPurkaja purkaja = new ReseptinVarauksenPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaarayksenVarausTO lm = purkaja.puraReseptinVaraus(cda);
        Assert.assertNotNull("purettu lääkemääräys ei voi olla null", lm);
        Assert.assertTrue(lm instanceof LaakemaarayksenVarausTO);
        LaakemaarayksenVarausTO varaus = (LaakemaarayksenVarausTO) lm;
        Assert.assertNotNull(varaus.getAmmattihenkilo());
        Assert.assertNotNull(varaus.getAmmattihenkilo().getKokonimi());
        Assert.assertNotNull(varaus.getAmmattihenkilo().getOrganisaatio());
        Assert.assertNotNull(varaus.getSelitys());
        Assert.assertEquals("Lähdekivi Tes", varaus.getAmmattihenkilo().getKokonimi().getSukunimi());
        Assert.assertEquals("Uno", varaus.getAmmattihenkilo().getKokonimi().getEtunimi());
        Assert.assertEquals("Kelan MAXX apteekki", varaus.getAmmattihenkilo().getOrganisaatio().getNimi());
        Assert.assertEquals("Varauksen syy on salainen ehkä.", varaus.getSelitys());
        Assert.assertEquals("00698700481", varaus.getAmmattihenkilo().getRekisterointinumero());
    }

    @Test
    public void testLaakePalkkiollaRoundtripWrongWay() throws Exception {
        Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
        LaakemaaraysTO to = new LaakemaaraysTO();
        ValmisteTO vto = new ValmisteTO();

        ValmisteenYksilointitiedotTO vytto = new ValmisteenYksilointitiedotTO();
        vytto.setATCkoodi("M01AE01");
        vytto.setATCnimi("Ibuprofeeni");
        vytto.setVahvuus("600 mg");
        vytto.setPakkausyksikko("fol");
        vytto.setPakkauskoko(100);
        vytto.setPakkauskokokerroin(1);
        vytto.setPakkauskokoteksti("100 fol");
        vytto.setValmisteenLaji("9");
        vytto.setValmisteenLajiNimi("Vaikuttavan aineen nimellä määrätty lääke");
        vto.setYksilointitiedot(vytto);

        VaikuttavaAineTO vato = new VaikuttavaAineTO();
        vto.getVaikuttavatAineet().add(vato);

        ValmisteenKayttotapaTO vktto = new ValmisteenKayttotapaTO();
        vktto.setLaakemuoto("tabletti");

        vto.getKayttotavat().add(vktto);
        ValmisteenMuutTiedotTO vmtto = new ValmisteenMuutTiedotTO();
        vto.setMuutTiedot(vmtto);
        to.setValmiste(vto);
        AmmattihenkiloTO ato = new AmmattihenkiloTO();
        ato.setAmmattioikeus("034");
        ato.setAmmattioikeusName("laillistettu erikoislääkäri");
        ato.setErikoisala("86173-680");
        ato.setErikoisalaName("erikoislääkäri yleislääketiede");
        ato.setKokonimi(new KokoNimiTO("TestiEtu", "SukuTesti", Arrays.asList("Mikael", "Eetvartti")));
        ato.setOppiarvo("Lääketieteen lisensiaatti");
        ato.setRekisterointinumero("01234567890");
        ato.setRooli("LAL");
        ato.setSvNumero("123455");
        ato.setVirkanimike("Ylilääkäri");
        OrganisaatioTO oto = new OrganisaatioTO();
        oto.setNimi("Testi terveysasema");
        OsoiteTO osoiteTo = new OsoiteTO();
        osoiteTo.setKatuosoite("Potilastie 2");
        osoiteTo.setMaa("");
        osoiteTo.setPostinumero("50600");
        osoiteTo.setPostitoimipaikka("Kotka");
        oto.setOsoite(osoiteTo);
        oto.setPuhelinnumero("tel:0201234567");
        oto.setYksilointitunnus("1.2.246.10.1602257.10.1");
        ato.setOrganisaatio(oto);
        to.setAmmattihenkilo(ato);
        to.setLaatimispaikka(oto);
        HenkilotiedotTO hto = new HenkilotiedotTO(
                new KokoNimiTO("PotilasKutsuma", "PotilasSuku", Arrays.asList("PotilasToinenEtunimi")),
                KantaCDATestUtils.testHetu);
        to.setPotilas(hto);
        to.setReseptintyyppi("1");
        to.setLaaketietokannanVersio("2014.008");
        to.getHoitolajit().add("S");
        to.setLaakarinPalkkio(1.52);
        ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, to);
        String cda = tested.kasaaReseptiAsiakirja();
        // utils.tallenna(cda);
        ReseptiPurkaja testDisassembly = new ReseptiPurkaja();
        LaakemaaraysTO purettu = testDisassembly.puraLaakemaarays(cda);
        assertNotNull(purettu.getLaakarinPalkkio());
        assertTrue(1.52 == purettu.getLaakarinPalkkio().doubleValue());
        assertFalse(purettu.isLaakarinpalkkioErikoislaakarina());
    }

    @Test
    public void testKorjaaApteekissaKirjattuaReseptia() throws Exception {
        // String cda = utils.lataa("LM1_pkvlaake_iteroitu_pienempi_pakkaus.xml");
        String cda = KantaCDATestUtils.lataa("apteekissa_tallennettu_laakemaarays_puhelin.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO alkupLaakemaarays = purkaja.puraLaakemaarays(cda);
        assertNotNull("purettu lääkemääräys ei voi olla null", alkupLaakemaarays);
        assertEquals("alkuperaisen lääkemääräyksen tekijän rooli tulee olla 'LAL'", "LAL",
                alkupLaakemaarays.getAmmattihenkilo().getRooli());
        assertNotNull("alkuperäisen lääkemääräyksen palvelutapahtuman oid ei voi olla null",
                alkupLaakemaarays.getPalvelutapahtumanOid());
        assertNotNull("alkuperäisen lääkemääräyksen palveluyksikkö ei voi olla null",
                alkupLaakemaarays.getLaatimispaikka());
        assertNotNull("alkuperäisen lääkemääräyksen määräyspäivä ei voi olla null",
                alkupLaakemaarays.getMaarayspaiva());
        assertEquals("alkuperaisen lääkemääräyksen kirjaajan rooli tulee olla 'KIR'", "KIR",
                alkupLaakemaarays.getKirjaaja().getRooli());
        assertNotNull("alkuperäisen apteekissa talennettu lääkemääräys-tieto ei voi olla null",
                alkupLaakemaarays.getApteekissaTallennettuLaakemaarays());
        assertNotNull("alkuperäisen apteekissa tallennuksen perustelu ei voi olla null",
                alkupLaakemaarays.getApteekissaTallennettuLaakemaaraysPerustelu());
        assertNotNull("alkuperäisen apteekissa tallenuksen muu syy ei voi olla null",
                alkupLaakemaarays.getApteekissaTallennettuLaakemaaraysMuuSyy());

        LaakemaarayksenKorjausTO korjaus = new LaakemaarayksenKorjausTO();
        korjaus.setKorjaaja(KantaCDATestUtils.luoAmmattihenkilo());
        korjaus.getKorjaaja().setOppiarvo("Lääketieteen lisensiaatti");
        korjaus.getKorjaaja().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        korjaus.setKorjauksenSyyKoodi("5");
        korjaus.setKorjauksenPerustelu("Huvinvuoksi");
        korjaus.setValmiste(alkupLaakemaarays.getValmiste());
        korjaus.getValmiste().getYksilointitiedot().setKauppanimi("PURANA");
        korjaus.setAnnosjakeluTeksti("Aina kun tarvetta");
        // FIXME: Mistä Org Y tunnus pitäisi saada
        // korjaus.setOrgYTunnus("1234567-8");
        ReseptinKorjausKasaaja kasaaja = new ReseptinKorjausKasaaja(
                KantaCDATestUtils.loadProperties("testi_properties.properties"), korjaus, alkupLaakemaarays);
        POCDMT000040ClinicalDocument doc = kasaaja.kasaaReseptiCDA();
        // String uusiCda = kasaaja.kasaaReseptiAsiakirja();
        assertNotNull("korjaus ei voi olla null", doc);
        assertFalse("clinicalDocument/author ei voi olla tyhjä", doc.getAuthors().isEmpty());
        assertEquals("korjauksessa authoreita tulee olla 3", 3, doc.getAuthors().size());
        assertEquals("1. author functionCode tulee olla 'LAL'", "LAL",
                doc.getAuthors().get(0).getFunctionCode().getCode());
        assertEquals("2. author functionCode tulee olla 'KIR'", "KIR",
                doc.getAuthors().get(1).getFunctionCode().getCode());
        assertEquals("3. author functionCode tulee olla 'KOR'", "KOR",
                doc.getAuthors().get(2).getFunctionCode().getCode());

        assertFalse("clinicalDocument/relatedDocuments ei voi olla tyhjä", doc.getRelatedDocuments().isEmpty());
        assertEquals("relatedDocument typeCode tulee olla 'RPLC'", XActRelationshipDocument.RPLC,
                doc.getRelatedDocuments().get(0).getTypeCode());
        assertEquals(
                "relatedDocument/parentDocument/id root tulee olla alkuperaisen lääkemääräyksen id: "
                        + alkupLaakemaarays.getOid(),
                alkupLaakemaarays.getOid(),
                doc.getRelatedDocuments().get(0).getParentDocument().getIds().get(0).getRoot());
        assertEquals(
                "relatedDocument/parentDocument/setId root tulee olla alkuperaisen lääkemääräyksen setId: "
                        + alkupLaakemaarays.getSetId(),
                alkupLaakemaarays.getSetId(),
                doc.getRelatedDocuments().get(0).getParentDocument().getSetId().getRoot());
        assertEquals("relatedDocument/parentDocument/code code tulee olla alkuperaisen lääkemääräyksen tyyppi",
                String.valueOf(alkupLaakemaarays.getCdaTyyppi()),
                doc.getRelatedDocuments().get(0).getParentDocument().getCode().getCode());

        assertEquals(
                "korjauksen clinicalDocument/componentOf/encompassianEncounter/id tulee olla alkuperäisen lääkemääräyksen palvelutapahtuman oid: "
                        + alkupLaakemaarays.getPalvelutapahtumanOid(),
                alkupLaakemaarays.getPalvelutapahtumanOid(),
                doc.getComponentOf().getEncompassingEncounter().getIds().get(0).getRoot());
        assertNotNull(
                "korjauksen clinicalDocument/componentOf/encompassianEncounter/effectiveTime ei voi oll null (tulee olla alkuperäisen lääkemääräyksen määräyspäivä)",
                doc.getComponentOf().getEncompassingEncounter().getEffectiveTime().getValue());
        assertEquals(
                "korjauksen clinicalDocument/componentOf/encompassianEncounter/location/healthCareFacility/id tulee olla alkuperäisen lääkemääräyksen laatimispaikan yksilöintitunnus: "
                        + alkupLaakemaarays.getLaatimispaikka().getYksilointitunnus(),
                alkupLaakemaarays.getLaatimispaikka().getYksilointitunnus(), doc.getComponentOf()
                        .getEncompassingEncounter().getLocation().getHealthCareFacility().getIds().get(0).getRoot());

        boolean laakemaarayksenkorjauksenmuuttiedot_loytyi = false;
        boolean laakemaarayksenkorjauksenperustelu_loytyi = false;
        for (POCDMT000040Entry entry : doc.getComponent().getStructuredBody().getComponents().get(0).getSection()
                .getComponents().get(0).getSection().getComponents().get(0).getSection().getEntries()) {
            if ( entry.getOrganizer().getCode().getCode().equals("99") ) {
                laakemaarayksenkorjauksenmuuttiedot_loytyi = true;
                for (POCDMT000040Component4 component : entry.getOrganizer().getComponents()) {
                    if ( component.getObservation().getCode().getCode().equals("97") ) {
                        laakemaarayksenkorjauksenperustelu_loytyi = true;
                    }
                }
            }
        }
        assertTrue("korjauksessa tulee olla lääkemääräyksen korjauksen muut tiedot osio (99)",
                laakemaarayksenkorjauksenmuuttiedot_loytyi);
        assertTrue("korjauksessa tulee olla lääkemääräyksen korjauksen perustelu osio (97)",
                laakemaarayksenkorjauksenperustelu_loytyi);

        boolean aptekissa_tallennettu_loytyi = false;
        boolean apteekissa_tallennuksen_perustelu_loytyi = false;
        for (POCDMT000040Entry entry : doc.getComponent().getStructuredBody().getComponents().get(0).getSection()
                .getComponents().get(0).getSection().getComponents().get(0).getSection().getEntries()) {
            if ( entry.getOrganizer().getCode().getCode().equals("99") ) {
                for (POCDMT000040Component4 component : entry.getOrganizer().getComponents()) {
                    if ( component.getObservation().getCode().getCode().equals("212") ) {
                        aptekissa_tallennettu_loytyi = true;
                    }
                }
            }
        }

        for (POCDMT000040Entry entry : doc.getComponent().getStructuredBody().getComponents().get(0).getSection()
                .getComponents().get(0).getSection().getComponents().get(0).getSection().getEntries()) {
            if ( entry.getOrganizer().getCode().getCode().equals("99") ) {
                for (POCDMT000040Component4 component : entry.getOrganizer().getComponents()) {
                    if ( component.getObservation().getCode().getCode().equals("213") ) {
                        apteekissa_tallennuksen_perustelu_loytyi = true;
                    }
                }
            }
        }

        assertTrue("korjauksessa tulee olla apteekissa tallennettu osio (212)", aptekissa_tallennettu_loytyi);
        assertTrue("korjauksessa tulee olla apteekissa tallennuksen perustelu osio (213)",
                apteekissa_tallennuksen_perustelu_loytyi);

        // cda = kasaaja.kasaaReseptiAsiakirja();
        // System.out.println(cda);
    }

    @Test
    public void testPuraJaKasaaPotilaskohtainenErityislupavalmisteResepti() throws Exception {
        String cda = KantaCDATestUtils.lataa("potilaskohtainen_erityislupavalmiste_atc.xml");
        assertNotNull("testi CDA ei voi olla null", cda);
        assertFalse("testi CDA ei voi olla tyhjä", cda.isEmpty());
        ReseptiPurkaja purkaja = new ReseptiPurkaja();
        purkaja.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO laakemaarays = purkaja.puraLaakemaarays(cda);
        assertNotNull("purettu lääkemääräys ei voi olla null", laakemaarays);
        // XXX Huom! LaakemaaraysTO:lle pitää antaa organisaation Y tunnus oidin generointia varten
        // laakemaarays.setOrgYTunnus("1234567-8");
        ReseptinUudenKasaaja kasaaja = new ReseptinUudenKasaaja(
                KantaCDATestUtils.loadProperties("testi_properties.properties"), laakemaarays);
        String uusiCda = kasaaja.kasaaReseptiAsiakirja();
        assertNotNull("uusi cda ei voi olla null", uusiCda);

        // System.out.println(uusiCda);
    }

}
