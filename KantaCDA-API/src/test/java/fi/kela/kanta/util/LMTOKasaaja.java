package fi.kela.kanta.util;

import org.junit.Before;

import fi.kela.kanta.cda.KantaCDATestUtils;
import fi.kela.kanta.to.AnnosTO;
import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.KoodiTO;
import fi.kela.kanta.to.VaikuttavaAineTO;
import fi.kela.kanta.to.ValmisteTO;
import fi.kela.kanta.to.ValmisteenKayttotapaTO;
import fi.kela.kanta.to.ValmisteenMuutTiedotTO;
import fi.kela.kanta.to.ValmisteenYksilointitiedotTO;

public abstract class LMTOKasaaja {

    protected static String testKauppanimi = "SATIVEX";
    protected static String testATCKoodi = "N02BG10";
    protected static String testATCNimi = "Kannabinoidit";
    protected static String testVahvuus = "10";
    protected static String testPakkausyksikko = "ml";
    protected static String testLaakeaine = "kannabidioli";
    protected static String testLaakeaineTarkenne = "10048";
    protected static String testValmisteenLaji2 = "2";
    protected static String testValmisteenLaji9 = "9";
    protected static String testValmisteenLaji9Nimi = "Vaikuttavan aineen nimellä määrätty lääke";
    protected static String testLaakemuoto = "sumute suuonteloon";
    protected static String testReseptinTyyppi1 = "1";
    protected static String testAnnostusohje = "Testattaessa";
    protected static String testKayttotarkoitusTeksti = "Testaukseen";
    protected static String testHoitolajiS = "S";
    protected static String testReseptinLaji1 = "1";
    protected static String testLaaketietokannanVersio = "2015.005";
    protected static String testYksilointitunnus = "173653";

    /**
     * Override toteuttavassa luokassa
     */
    protected abstract void setupProperties();

    @Before
    public void setup() {
        setupProperties();
    }

    protected ValmisteTO luoValmiste() {
        ValmisteenYksilointitiedotTO valmisteenYksilointitiedotTO = new ValmisteenYksilointitiedotTO();
        valmisteenYksilointitiedotTO.setATCkoodi(LMTOKasaaja.testATCKoodi);
        valmisteenYksilointitiedotTO.setATCnimi(LMTOKasaaja.testATCNimi);
        valmisteenYksilointitiedotTO.setVahvuus(LMTOKasaaja.testVahvuus);
        valmisteenYksilointitiedotTO.setPakkausyksikko(LMTOKasaaja.testPakkausyksikko);
        valmisteenYksilointitiedotTO.setPakkauskoko(10);
        valmisteenYksilointitiedotTO.setPakkauskokokerroin(3);
        valmisteenYksilointitiedotTO.setPakkauskokoteksti(String.valueOf(valmisteenYksilointitiedotTO.getPakkauskoko())
                + " " + valmisteenYksilointitiedotTO.getPakkausyksikko());
        valmisteenYksilointitiedotTO.setValmisteenLaji(LMTOKasaaja.testValmisteenLaji9);
        valmisteenYksilointitiedotTO.setValmisteenLajiNimi(LMTOKasaaja.testValmisteenLaji9Nimi);

        VaikuttavaAineTO vaikuttavaAineTO = new VaikuttavaAineTO();
        vaikuttavaAineTO.setLaakeaine(LMTOKasaaja.testLaakeaine);
        vaikuttavaAineTO.setLaakeaineenTarkenne(LMTOKasaaja.testLaakeaineTarkenne);
        ValmisteenKayttotapaTO valmisteenKayttotapaTO = new ValmisteenKayttotapaTO();
        valmisteenKayttotapaTO.setLaakemuoto(LMTOKasaaja.testLaakemuoto);

        ValmisteTO valmisteTO = new ValmisteTO();
        valmisteTO.setYksilointitiedot(valmisteenYksilointitiedotTO);
        valmisteTO.getVaikuttavatAineet().add(vaikuttavaAineTO);
        valmisteTO.getKayttotavat().add(valmisteenKayttotapaTO);
        valmisteTO.setMuutTiedot(new ValmisteenMuutTiedotTO());
        return valmisteTO;
    }

    protected HenkilotiedotTO luoPotilas() {
        HenkilotiedotTO henkilotiedotTO = new HenkilotiedotTO(
                new KokoNimiTO(KantaCDATestUtils.testEtunimi, KantaCDATestUtils.testSukunimi),
                KantaCDATestUtils.testHetu);
        return henkilotiedotTO;
    }

    protected HenkilotiedotTO luoPotilas__Mies_Ei_Hetua() {
        HenkilotiedotTO henkilotiedotTO = new HenkilotiedotTO(
                new KokoNimiTO(KantaCDATestUtils.testEtunimi, KantaCDATestUtils.testSukunimi), "01.03.1968",
                new Integer(1));
        return henkilotiedotTO;
    }

    protected LaakemaaraysTO luoLaakemaarays() {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        // laakemaaraysTO.setOid("123.test.456");
        // laakemaaraysTO.setSetId("123.test.456");
        laakemaaraysTO.setPotilas(luoPotilas());
        laakemaaraysTO.setAmmattihenkilo(KantaCDATestUtils.luoAmmattihenkilo());
        laakemaaraysTO.getAmmattihenkilo().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        laakemaaraysTO.setLaatimispaikka(KantaCDATestUtils.luoOrganisaatio());
        laakemaaraysTO.setReseptintyyppi(LMTOKasaaja.testReseptinTyyppi1);
        laakemaaraysTO.setValmiste(luoValmiste());
        laakemaaraysTO.setPakkauksienLukumaara(2);
        laakemaaraysTO.setAnnosteluPelkastaanTekstimuodossa(true);
        laakemaaraysTO.setAnnostusohje(LMTOKasaaja.testAnnostusohje);
        laakemaaraysTO.setSICmerkinta(true);
        laakemaaraysTO.setAnnostusTarvittaessa(true);
        laakemaaraysTO.setKayttotarkoitusTeksti(LMTOKasaaja.testKayttotarkoitusTeksti);
        laakemaaraysTO.getHoitolajit().add(LMTOKasaaja.testHoitolajiS);
        laakemaaraysTO.setKyseessaLaakkeenkaytonAloitus(true);
        laakemaaraysTO.setHuume(true);
        laakemaaraysTO.setReseptinLaji(LMTOKasaaja.testReseptinLaji1);
        laakemaaraysTO.setLaaketietokannanVersio(LMTOKasaaja.testLaaketietokannanVersio);
        // laakemaaraysTO.setOrgYTunnus("1234567-8");
        return laakemaaraysTO;
    }

    protected LaakemaaraysTO luoLaakemaaraysRakenteinenAnnostusKayttoohjeenLisatiedot() {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        // laakemaaraysTO.setOid("123.test.456");
        // laakemaaraysTO.setSetId("123.test.456");
        laakemaaraysTO.setPotilas(luoPotilas());
        laakemaaraysTO.setAmmattihenkilo(KantaCDATestUtils.luoAmmattihenkilo());
        laakemaaraysTO.getAmmattihenkilo().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        laakemaaraysTO.setLaatimispaikka(KantaCDATestUtils.luoOrganisaatio());
        laakemaaraysTO.setReseptintyyppi(LMTOKasaaja.testReseptinTyyppi1);
        laakemaaraysTO.setValmiste(luoValmiste());
        laakemaaraysTO.setPakkauksienLukumaara(2);
        laakemaaraysTO.setAnnosteluPelkastaanTekstimuodossa(false);
        laakemaaraysTO.setAnnostusohje(LMTOKasaaja.testAnnostusohje);
        laakemaaraysTO.setSICmerkinta(true);
        laakemaaraysTO.setAnnostusTarvittaessa(true);
        laakemaaraysTO.setKayttotarkoitusTeksti(LMTOKasaaja.testKayttotarkoitusTeksti);
        laakemaaraysTO.getHoitolajit().add(LMTOKasaaja.testHoitolajiS);
        laakemaaraysTO.setKyseessaLaakkeenkaytonAloitus(true);
        laakemaaraysTO.setHuume(true);
        laakemaaraysTO.setReseptinLaji(LMTOKasaaja.testReseptinLaji1);
        laakemaaraysTO.setLaaketietokannanVersio(LMTOKasaaja.testLaaketietokannanVersio);
        laakemaaraysTO.setKayttoOhjeLisatiedot("Käyttöohjeen lisätiedot");
        KoodiTO antoreitti = new KoodiTO("1069", "ihon alle");
        laakemaaraysTO.setLaakkeenantoreitti(antoreitti);
        // laakemaaraysTO.setOrgYTunnus("1234567-8");
        return laakemaaraysTO;
    }

    protected LaakemaaraysTO luoLaakemaaraysRakenteinenAnnostusFysikaalinenAnnos() {
        LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
        AnnosTO fysAnnos = new AnnosTO();
        laakemaaraysTO.setPotilas(luoPotilas());
        laakemaaraysTO.setAmmattihenkilo(KantaCDATestUtils.luoAmmattihenkilo());
        laakemaaraysTO.getAmmattihenkilo().setOrganisaatio(KantaCDATestUtils.luoOrganisaatio());
        laakemaaraysTO.setLaatimispaikka(KantaCDATestUtils.luoOrganisaatio());
        laakemaaraysTO.setReseptintyyppi(LMTOKasaaja.testReseptinTyyppi1);
        laakemaaraysTO.setValmiste(luoValmiste());
        laakemaaraysTO.setPakkauksienLukumaara(2);
        laakemaaraysTO.setAnnosteluPelkastaanTekstimuodossa(false);
        laakemaaraysTO.setAnnostusohje(LMTOKasaaja.testAnnostusohje);
        laakemaaraysTO.setSICmerkinta(true);
        laakemaaraysTO.setAnnostusTarvittaessa(true);
        fysAnnos.setFysYksikko("mg");
        fysAnnos.setLowFysAnnos(2.0);
        fysAnnos.setHighFysAnnos(8.0);
        laakemaaraysTO.getAnnokset().add(fysAnnos);
        laakemaaraysTO.setKayttotarkoitusTeksti(LMTOKasaaja.testKayttotarkoitusTeksti);
        laakemaaraysTO.getHoitolajit().add(LMTOKasaaja.testHoitolajiS);
        laakemaaraysTO.setKyseessaLaakkeenkaytonAloitus(true);
        laakemaaraysTO.setHuume(true);
        laakemaaraysTO.setReseptinLaji(LMTOKasaaja.testReseptinLaji1);
        laakemaaraysTO.setLaaketietokannanVersio(LMTOKasaaja.testLaaketietokannanVersio);
        laakemaaraysTO.setKayttoOhjeLisatiedot("Käyttöohjeen lisätiedot");
        KoodiTO antoreitti = new KoodiTO("1069", "ihon alle");
        laakemaaraysTO.setLaakkeenantoreitti(antoreitti);
        return laakemaaraysTO;
    }

    /**
     * Bridge
     *
     * @param key
     * @param value
     */
    protected void mockProperty(String key, String value) {
        KantaCDATestUtils.mockProperty(key, value);
    }
}
