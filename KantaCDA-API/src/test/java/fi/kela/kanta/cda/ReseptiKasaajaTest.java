package fi.kela.kanta.cda;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import org.hl7.v3.IVLTS;
import org.hl7.v3.POCDMT000040Component4;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040EntryRelationship;
import org.hl7.v3.SXCMTS;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.OrganisaatioTO;
import fi.kela.kanta.to.OsoiteTO;
import fi.kela.kanta.to.ValmisteTO;
import fi.kela.kanta.to.ValmisteenMuutTiedotTO;
import fi.kela.kanta.to.ValmisteenYksilointitiedotTO;
import junit.framework.TestCase;

@RunWith(JUnit4.class)
public class ReseptiKasaajaTest {

    private Properties props = null;
    private LaakemaaraysTO lmto = null;

    @Before
    public void createKasaajaProperties() {

        props = new Properties();
        props.put(Kasaaja.BASE_OID_PROPERTY, "2.246");

        OsoiteTO osoite = new OsoiteTO();
        osoite.setKatuosoite("Klinikenbürger 10");
        osoite.setMaa("Saksa");
        osoite.setPostinumero("77933");
        osoite.setPostitoimipaikka("Schwartzwald");

        OrganisaatioTO org = new OrganisaatioTO();
        org.setNimi("Schwartzwaldklinik GmbH");
        org.setOsoite(osoite);
        org.setPuhelinnumero("000-0000000");
        org.setSahkoposti("schwartzwaldklinik@kanta.fi");
        org.setYksilointitunnus("0-00000000");

        AmmattihenkiloTO henkilo = new AmmattihenkiloTO();
        henkilo.setAmmattioikeus("1");
        henkilo.setAmmattioikeusName("lääkäri");
        henkilo.setErikoisala("vuoristo");
        henkilo.setErikoisalaName("VUO");
        henkilo.setKokonimi(new KokoNimiTO("Klaus", "Brinkmann"));
        henkilo.setOppiarvo("vl");
        henkilo.setOppiarvoTekstina("vuoristolääkäri");
        henkilo.setOrganisaatio(org);
        henkilo.setRekisterointinumero("0123456789");
        henkilo.setRooli("määrääjä");
        henkilo.setSvNumero("0123456789");
        henkilo.setVirkanimike("doktor");

        HenkilotiedotTO potilas = new HenkilotiedotTO(new KokoNimiTO("Claudia", "Schubert"), "01.04.1972", 0);
        potilas.setHetu("010472-154D");

        ValmisteenMuutTiedotTO muut = new ValmisteenMuutTiedotTO();
        muut.setHuume(false);
        ValmisteTO valmiste = new ValmisteTO();
        valmiste.setMuutTiedot(muut);
        ValmisteenYksilointitiedotTO yksilointi = new ValmisteenYksilointitiedotTO();
        yksilointi.setATCkoodi("ATC123");
        yksilointi.setATCnimi("Tulivesi");
        yksilointi.setKauppanimi("Ihmelääke");
        yksilointi.setMarkkinoija("Masa");
        yksilointi.setMyyntiluvanHaltija("Mä");
        yksilointi.setPakkauskoko(1.0);
        yksilointi.setPakkauskokokerroin(1);
        yksilointi.setPakkauskokoteksti("litra");
        yksilointi.setSailytysastia("pullo");
        yksilointi.setTunnuksenTyyppi(0);
        yksilointi.setVahvuus("90%");
        yksilointi.setValmisteenLaji("viina");
        yksilointi.setValmisteenLajiNimi("booze");
        yksilointi.setValmisteenLisatieto("");
        yksilointi.setYksilointitunnus("12345");
        valmiste.setYksilointitiedot(yksilointi);

        lmto = new LaakemaaraysTO();
        lmto.setAikaleima(new Date());
        lmto.setAmmattihenkilo(henkilo);
        lmto.setOid("2.246.12345.67890");
        // lmto.setOrgYTunnus("0-00000000");
        lmto.setPotilaanTunnistaminen("1");
        lmto.setPotilaanTunnistaminenTeksti("ajokortti");
        lmto.setPotilas(potilas);
        lmto.setSetId("2.246.12345.67890");
        lmto.setValmiste(valmiste);
        lmto.setVersio(1);
    }

    @Test
    public void testaaAjalleMaaratynTiedonAsettaminen() {

        GregorianCalendar greg = new GregorianCalendar();
        greg.set(Calendar.YEAR, 2015);
        greg.set(Calendar.MONTH, 10);
        greg.set(Calendar.DAY_OF_MONTH, 30);

        ReseptiKasaaja kasaaja = new ReseptinUudenKasaaja(props, lmto);

        // lmto.setReseptinLaji("3");
        lmto.setReseptintyyppi("3");
        lmto.setAjalleMaaratynReseptinAikamaaraUnit("wk");
        lmto.setAjalleMaaratynReseptinAikamaaraValue(2);
        lmto.setAjalleMaaratynReseptinAlkuaika(greg.getTime());

        POCDMT000040Entry result = kasaaja.luoValmisteenJaPakkauksenTiedot(lmto, "111111", lmto.getAmmattihenkilo());
        try {
            SXCMTS sxcmts = result.getOrganizer().getComponents().get(0).getSubstanceAdministration()
                    .getEntryRelationships().get(0).getSupply().getEffectiveTimes().get(0);
            TestCase.assertNotNull(sxcmts);
            TestCase.assertTrue(sxcmts instanceof IVLTS);
            IVLTS ivlts = (IVLTS) sxcmts;
            TestCase.assertEquals("20151130", ivlts.getLow().getValue());
            TestCase.assertEquals("wk", ivlts.getWidth().getUnit());
            TestCase.assertEquals("2", ivlts.getWidth().getValue());
        }
        catch (Exception e) {
            TestCase.fail("Ei pystytty hakemaan ajallemäärätyn lääkemääräyksen alkuaikaa");
        }
    }
    
    @Test
    public void testLuoAnnostusTarvittaessa() {
    	ReseptiKasaaja kasaaja = new ReseptinUudenKasaaja(props, lmto);
    	POCDMT000040EntryRelationship er = kasaaja.luoTarvittaessaEntry("", true);
    }
    
    @Test
    public void luoLuoAnnosteluComponent() {
    	ReseptiKasaaja kasaaja = new ReseptinUudenKasaaja(props, lmto);
    	POCDMT000040Component4 comp =kasaaja.luoAnnostelukausiComponent(lmto);
    	
    }
    
    @Test
    public void testLuoAnnostelukaudenAika_molemmatPaivat() throws IOException {
		Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
		LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
		ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
		LocalDateTime date = LocalDateTime.of(2021, 6, 1, 12, 30, 45);
		laakemaaraysTO.setAnnostelukaudenAlkupvm(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
		date = LocalDateTime.of(2021, 6, 30, 12, 30, 45);
		laakemaaraysTO.setAnnostelukaudenLoppupvm(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
		IVLTS aika = tested.luoAnnostelukaudenAika(laakemaaraysTO);
		Assert.assertNotNull(aika);
		Assert.assertNotNull(aika.getLow());
		Assert.assertNotNull(aika.getHigh());
		Assert.assertTrue("20210601".equals(aika.getLow().getValue()));
		Assert.assertTrue("20210630".equals(aika.getHigh().getValue()));
	   }

    @Test
    public void testLuoAnnostelukaudenAika_alkupaiva() throws IOException {
		Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
		LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
		ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
		LocalDateTime date = LocalDateTime.of(2021, 6, 1, 12, 30, 45);
		laakemaaraysTO.setAnnostelukaudenAlkupvm(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
		IVLTS aika = tested.luoAnnostelukaudenAika(laakemaaraysTO);
		Assert.assertNotNull(aika);
		Assert.assertTrue("20210601".equals(aika.getLow().getValue()));
		Assert.assertNull(aika.getHigh());
	}
    
    @Test
	public void testLuoAnnostelukaudenAika_loppupaiva() throws IOException {
		Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
		LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
		ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
		LocalDateTime date = LocalDateTime.of(2021, 6, 30, 12, 30, 45);
		laakemaaraysTO.setAnnostelukaudenLoppupvm(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
		IVLTS aika = tested.luoAnnostelukaudenAika(laakemaaraysTO);
		Assert.assertNotNull(aika);
		Assert.assertTrue("20210630".equals(aika.getHigh().getValue()));
		Assert.assertNull(aika.getLow());
	}
    
    @Test
	public void testLuoAnnostelukaudenAika_paivatNULL() throws IOException {
		Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
		LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
		ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
		IVLTS aika = tested.luoAnnostelukaudenAika(laakemaaraysTO);
		Assert.assertNull(aika);
	}

    @Test
	public void testLuoAnnostelukaudenAika_laakemaaraysNULL() throws IOException {
		Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
		LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
		ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
		IVLTS aika = tested.luoAnnostelukaudenAika(null);
		Assert.assertNull(aika);
	}

    @Test
    public void testLuoLaakeTauolla_molemmatPaivat() throws IOException {
		Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
		LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
		ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
		LocalDateTime date = LocalDateTime.of(2021, 6, 1, 12, 30, 45);
		laakemaaraysTO.setLaakeTauollaAlkupvm(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
		date = LocalDateTime.of(2021, 6, 30, 12, 30, 45);
		laakemaaraysTO.setLaakeTauollaLoppupvm(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
		POCDMT000040EntryRelationship aika = tested.luoLaakeTauolla(laakemaaraysTO);
		Assert.assertNotNull(aika);
		Assert.assertNotNull(aika.getObservation().getEffectiveTime());
		Assert.assertNotNull(aika.getObservation().getEffectiveTime().getLow());
		Assert.assertNotNull(aika.getObservation().getEffectiveTime().getHigh());
		Assert.assertTrue("20210601".equals(aika.getObservation().getEffectiveTime().getLow().getValue()));
		Assert.assertTrue("20210630".equals(aika.getObservation().getEffectiveTime().getHigh().getValue()));
	   }

    @Test
    public void testLuoLaakeTauolla_alkupaiva() throws IOException {
		Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
		LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
		ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
		LocalDateTime date = LocalDateTime.of(2021, 6, 1, 12, 30, 45);
		laakemaaraysTO.setLaakeTauollaAlkupvm(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
		POCDMT000040EntryRelationship aika = tested.luoLaakeTauolla(laakemaaraysTO);
		Assert.assertNotNull(aika);
		Assert.assertNotNull(aika.getObservation().getEffectiveTime().getLow());
		Assert.assertTrue("20210601".equals(aika.getObservation().getEffectiveTime().getLow().getValue()));
		Assert.assertNull(aika.getObservation().getEffectiveTime().getHigh());
	}
    
    @Test
	public void testLuoLaakeTauolla_loppupaiva() throws IOException {
		Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
		LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
		ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
		LocalDateTime date = LocalDateTime.of(2021, 6, 30, 12, 30, 45);
		laakemaaraysTO.setLaakeTauollaLoppupvm(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
		POCDMT000040EntryRelationship aika = tested.luoLaakeTauolla(laakemaaraysTO);
		Assert.assertNull(aika);
	}
    
    @Test
	public void testLuoLaakeTauolla_paivatNULL() throws IOException {
		Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
		LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
		ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
		POCDMT000040EntryRelationship aika = tested.luoLaakeTauolla(laakemaaraysTO);
		Assert.assertNull(aika);
	}

    @Test
	public void testLuoLaakeTauolla_laakemaaraysNULL() throws IOException {
		Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
		LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
		ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, laakemaaraysTO);
		POCDMT000040EntryRelationship aika = tested.luoLaakeTauolla(null);
		Assert.assertNull(aika);
	}

}
