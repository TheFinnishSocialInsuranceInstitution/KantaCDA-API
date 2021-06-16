package fi.kela.kanta.cda;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Date;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.OstopalvelunvaltuutusTO;

public class OstopalvelunvaltuutusKasaajaTest {

	private static final String propertyFile = "testi_arkisto.properties";
	private String potilasEtuNimi = "PotilasEtu";
	private String potilasSukuNimi = "PotilasSuku";
	private String potilasHetu = "030875-999Y";//"280275-912S";
	private String ammattihenkilonPalveluyksikko = "1.2.345.67889";
	private String ammattihenkilonPalveluyksikonNimi = "ammattihenkilonPalveluyksikonNimi";
	private String ammattihenkilonKatsoTunnus = "12345667890123";
	private String asiakirjanRekisterinpitaja = "1.2.246.10.99999995.5.3";
	private String asiakirjanRekisterinpitajaNimi = "Asiakirjan rekisterinpitäjä";
	private int hakuRekisteri = 4;
	private String hakuRekisterinpitaja = "1.2.246.10.99999984.19.0";
	private String hakuRekisterinTarkenne = "1234567-1";
	private String hakuRekisterinTarkentimenNimi = "Yritys Oy";
	private int ostopalvelunTyyppi = 1;
	private String palvelunJarjestaja = "1.2.246.10.99999984.10.0";
	private String palvelunJarjestajaNimi = "Testi tk";
	private String palvelunJarjestajanPalveluyksikko = "1.2.246.10.99999984.10.1";
	private String palvelunJarjestajanPalveluyksikonNimi = "Testi tk pääterveysasema";
	private String palvelunTuottaja = "1.2.246.10.48484841.10.0";
	private String palvelunTuottajanNimi = "Testi yksityinen";
	private int tallennusRekisteri = 4;
	private String tallennusRekisterinpitaja = "1.2.246.10.99999984.19.0";
	private String tallennusRekisterinTarkenne = "1234567-1";
	private String tallennusRekisterinTarkentimenNimi = "Yritys Oy";
	private String tallentajaEtuNimi = "TallentajaEtu";
	private String tallentajaSukuNimi = "TallentajaSuku";
	private String tallentajaHetu = "151136-917U";
	private String testCDAOidBase = "1.2.246.10";
	private String testPalvelutapahtuma = "1.2.3.45.678.7777.26092017";
	
	@Test
	public void testKasaaAsiakirja() throws IOException, JAXBException {
		OstopalvelunvaltuutusTO ostopalvelunvaltuutus = luoOstopalvelunvaltuutusTO();
		ostopalvelunvaltuutus.setAikaleima(new Date());
		//ostopalvelunvaltuutus.getTemplateIds().add("1.2.345.2.234.54.345");
		ostopalvelunvaltuutus.setCDAOidBase(testCDAOidBase);
		OstopalvelunvaltuutusKasaaja tested = new OstopalvelunvaltuutusKasaaja(KantaCDATestUtils.loadProperties(propertyFile), ostopalvelunvaltuutus);
		String cda = tested.kasaaAsiakirja();
		assertNotNull("cda ei voi olla null", cda);
		//System.out.println(cda);
	}
	
	@Test
	public void testKasaaAsiakirja_tekninenProsessi() throws IOException, JAXBException {
		OstopalvelunvaltuutusTO ostopalvelunvaltuutus = luoOstopalvelunvaltuutusTO();
		ostopalvelunvaltuutus.setAikaleima(new Date());
		//ostopalvelunvaltuutus.getTemplateIds().add("1.2.345.2.234.54.345");
		ostopalvelunvaltuutus.setCDAOidBase(testCDAOidBase);
		OstopalvelunvaltuutusKasaaja tested = new OstopalvelunvaltuutusKasaaja(KantaCDATestUtils.loadProperties(propertyFile), ostopalvelunvaltuutus);
		tested.setTekninenProsessi(true);
		String cda = tested.kasaaAsiakirja();
		assertNotNull("cda ei voi olla null", cda);
		//System.out.println(cda);
	}
	
	@Test
	public void testKasaaAsiakirja_korvaava() throws JAXBException, IOException {
		OstopalvelunvaltuutusTO ostopalvelunvaltuutus = luoOstopalvelunvaltuutusTO();
		ostopalvelunvaltuutus.setAikaleima(new Date());
		ostopalvelunvaltuutus.setCDAOidBase(testCDAOidBase);
		String vanhaOid = "1.2.1770";
		String uusiOid = "1.2.2018";
		ostopalvelunvaltuutus.setOid(uusiOid);
		ostopalvelunvaltuutus.setSetId(vanhaOid);
		ostopalvelunvaltuutus.setVersio(1);
		OstopalvelunvaltuutusKasaaja tested = new OstopalvelunvaltuutusKasaaja(KantaCDATestUtils.loadProperties(propertyFile),ostopalvelunvaltuutus, vanhaOid);
		String cda = tested.kasaaAsiakirja();
		assertNotNull("cda ei voi olla null", cda);
		//System.out.println(cda);
	}
	
	@Test
	public void testKasaaAsiakirja_LuovutettavillaAsiakirjoilla() throws IOException, JAXBException {
		OstopalvelunvaltuutusTO ostopalvelunvaltuutus = luoOstopalvelunvaltuutusTO();
		ostopalvelunvaltuutus.setAikaleima(new Date());
		ostopalvelunvaltuutus.setCDAOidBase(testCDAOidBase);
		ostopalvelunvaltuutus.setKaikkiAsiakirjat(false);
		for (int i=0; i<5; i++) {
			ostopalvelunvaltuutus.getLuovutettavatPalvelutapahtumat().add(testPalvelutapahtuma+"."+i);
		}
		OstopalvelunvaltuutusKasaaja tested = new OstopalvelunvaltuutusKasaaja(KantaCDATestUtils.loadProperties(propertyFile), ostopalvelunvaltuutus);
		String cda = tested.kasaaAsiakirja();
		assertNotNull("cda ei voi olla null", cda);
		//System.out.println(cda);
	}
	
	@Test
	public void testKasaaMitatointiAsiakirja() throws IOException, JAXBException {
		OstopalvelunvaltuutusTO ostopalvelunvaltuutus = luoOstopalvelunvaltuutusTO();
		ostopalvelunvaltuutus.setAikaleima(new Date());
		ostopalvelunvaltuutus.setCDAOidBase(testCDAOidBase);
		String vanhaOid = "1.2.1770";
		String uusiOid = "1.2.2018";
		ostopalvelunvaltuutus.setOid(uusiOid);
		ostopalvelunvaltuutus.setSetId(vanhaOid);
		ostopalvelunvaltuutus.setVersio(1);
		ostopalvelunvaltuutus.getTemplateIds().add("DiipadaapaID");
		OstopalvelunvaltuutusKasaaja tested = new OstopalvelunvaltuutusKasaaja(KantaCDATestUtils.loadProperties(propertyFile), ostopalvelunvaltuutus, vanhaOid);
		
		String cda = tested.kasaaMitatointiAsiakirja();
		assertNotNull("cda ei voi olla null", cda);
		//System.out.println(cda);
	}

	private OstopalvelunvaltuutusTO luoOstopalvelunvaltuutusTO() {
		OstopalvelunvaltuutusTO ov = new OstopalvelunvaltuutusTO();
		ov.setAsiakirjanRekisterinpitaja(asiakirjanRekisterinpitaja);
		ov.setAsiakirjanRekisterinpitajaNimi(asiakirjanRekisterinpitajaNimi);
		ov.setAmmattihenkilonPalveluyksikko(ammattihenkilonPalveluyksikko);
		ov.setAmmattihenkilonPalveluyksikonNimi(ammattihenkilonPalveluyksikonNimi);
		ov.setAmmattihenkilonKatsoTunnus(ammattihenkilonKatsoTunnus);
		ov.setAsiakirjanTallentaja(luoAsiakirjanTallentaja());
		ov.setHakuRekisteri(hakuRekisteri);
		ov.setHakuRekisterinpitaja(hakuRekisterinpitaja);
		ov.setHakuRekisterinTarkenne(hakuRekisterinTarkenne);
		ov.setHakuRekisterinTarkentimenNimi(hakuRekisterinTarkentimenNimi);
		ov.setKaikkiAsiakirjat(true);
		ov.setOstopalvelunTyyppi(ostopalvelunTyyppi);
		ov.setPalvelunJarjestaja(palvelunJarjestaja);
		ov.setPalvelunJarjestajaNimi(palvelunJarjestajaNimi);
		ov.setPalvelunJarjestajanPalveluyksikko(palvelunJarjestajanPalveluyksikko);
		ov.setPalvelunJarjestajanPalveluyksikonNimi(palvelunJarjestajanPalveluyksikonNimi);
		ov.setPalvelunTuottaja(palvelunTuottaja);
		ov.setPalvelunTuottajanNimi(palvelunTuottajanNimi);
		ov.setPotilas(luoPotilas());
		ov.setTallennusRekisteri(tallennusRekisteri);
		ov.setTallennusRekisterinpitaja(tallennusRekisterinpitaja);
		ov.setTallennusRekisterinTarkenne(tallennusRekisterinTarkenne);
		ov.setTallennusRekisterinTarkentimenNimi(tallennusRekisterinTarkentimenNimi);
		ov.setValtuutuksenVoimassaoloAlku(new Date());
		ov.setValtuutuksenVoimassaoloLoppu(new Date());
		
		return ov;
	}

	private HenkilotiedotTO luoPotilas() {
		KokoNimiTO nimi = new KokoNimiTO(potilasEtuNimi, potilasSukuNimi);
		HenkilotiedotTO potilas = new HenkilotiedotTO(nimi, potilasHetu);
		return potilas;
	}

	private HenkilotiedotTO luoAsiakirjanTallentaja() {
		KokoNimiTO nimi = new KokoNimiTO(tallentajaEtuNimi, tallentajaSukuNimi);
		HenkilotiedotTO tallentaja = new HenkilotiedotTO(nimi, tallentajaHetu);
		return tallentaja;
	}
}
