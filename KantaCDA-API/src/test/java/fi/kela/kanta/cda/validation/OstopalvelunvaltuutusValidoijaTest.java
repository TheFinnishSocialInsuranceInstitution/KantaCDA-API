package fi.kela.kanta.cda.validation;

import java.util.Date;

import org.junit.Test;

import fi.kela.kanta.cda.KantaCDAConstants;
import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.OstopalvelunvaltuutusTO;

public class OstopalvelunvaltuutusValidoijaTest {

	private static final String testOID = "1.2.3.4.5";
	private static final String test = "TESTI";
	private static final String testHetu = "121212-9213";

	@Test
	public void testValidoi() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva(); 
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test
	public void testValidoiTuottajanHakuOikeus() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setHakuRekisterinpitaja(testOID);
		osva.setHakuRekisteri(KantaCDAConstants.PotilasasiakirjanRekisteritunnus.TYOTERVEYSHUOLTO.getRekisteritunnusValue());
		osva.setHakuRekisterinTarkenne(testOID);
		osva.setHakuRekisterinTarkentimenNimi(test);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoiOstopalvelunTuottajanHakuOikeus();
	}
	
	public void testValidoi_LuovutettavatPalvelutapahtumat() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setOstopalvelunTyyppi(KantaCDAConstants.OstopalvelunTyyppi.POTILASKOHTAINEN_OSTOPALVELU.getTyyppiValue());
		osva.setPotilas(new HenkilotiedotTO(new KokoNimiTO(test, test), testHetu));
		osva.setKaikkiAsiakirjat(false);
		osva.getLuovutettavatPalvelutapahtumat().add(testOID);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_nullOstopalvelunvalntuutusTO() {
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(null);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_asiakirjanRekisterinpitaja_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setAsiakirjanRekisterinpitaja(null);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_asiakirjanrekisterinpitajanimi_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setAsiakirjanRekisterinpitajaNimi(null);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_ostopalvelunTyyppi_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setOstopalvelunTyyppi(0);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_ostopalvelunVoimassaolonAlku_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setValtuutuksenVoimassaoloAlku(null);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_ostopalvelunVoimassaolonLoppu_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setValtuutuksenVoimassaoloLoppu(null);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_ostopalvelunJarjestajanOid_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setPalvelunJarjestaja(null);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_ostopalvelunJarjestajanNimi_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setPalvelunJarjestajaNimi(null);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_ostopalvelunJarjestajanPalveluyksikonOid_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setOstopalvelunTyyppi(KantaCDAConstants.OstopalvelunTyyppi.POTILASKOHTAINEN_OSTOPALVELU.getTyyppiValue());
		osva.setPalvelunJarjestajanPalveluyksikko(null);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_ostopalvelunJarjestajanPalveluyksikonNimi_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setOstopalvelunTyyppi(KantaCDAConstants.OstopalvelunTyyppi.POTILASKOHTAINEN_OSTOPALVELU.getTyyppiValue());
		osva.setPalvelunJarjestajanPalveluyksikonNimi(null);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_ostopalvelunTuottajanOid_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setPalvelunTuottaja(null);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_ostopalvelunTuottajanNimi_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setPalvelunTuottajanNimi(null);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_HakuRekisteri_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		//Asetetaan vain rekisterinpitäjä jollon validoija tarkistaa että rekisteri on asetettu
		// eli haku rekisterin pitäjä ei ole pakollinen tieto, mutta jos se on annettu niin silloin pitää antaa myös rekisteri
		osva.setHakuRekisterinpitaja(testOID);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_HakuRekisterinTarkenne_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setHakuRekisterinpitaja(testOID);
		osva.setHakuRekisteri(KantaCDAConstants.PotilasasiakirjanRekisteritunnus.TYOTERVEYSHUOLTO.getRekisteritunnusValue());
		//Työterveyshuollon rekisterissä tarkenne ja sen nimi ovat pakollisia tietoja
		osva.setHakuRekisterinTarkentimenNimi(test);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_HakuRekisterinTarkenneNimi_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setHakuRekisterinpitaja(testOID);
		osva.setHakuRekisteri(KantaCDAConstants.PotilasasiakirjanRekisteritunnus.TYOTERVEYSHUOLTO.getRekisteritunnusValue());
		//Työterveyshuollon rekisterissä tarkenne ja sen nimi ovat pakollisia tietoja
		osva.setHakuRekisterinTarkenne(testOID);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_TallennusRekisterinpitaja_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setTallennusRekisterinpitaja(null);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_TallennusRekisteri_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setTallennusRekisteri(0);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_TallennusRekisterinTarkenne_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setTallennusRekisteri(KantaCDAConstants.PotilasasiakirjanRekisteritunnus.TYOTERVEYSHUOLTO.getRekisteritunnusValue());
		//Työterveyshuollon rekisterissä tarkenne ja sen nimi ovat pakollisia tietoja
		osva.setTallennusRekisterinTarkentimenNimi(test);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_TallennusRekisterinTarkenneNimi_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setTallennusRekisteri(KantaCDAConstants.PotilasasiakirjanRekisteritunnus.TYOTERVEYSHUOLTO.getRekisteritunnusValue());
		//Työterveyshuollon rekisterissä tarkenne ja sen nimi ovat pakollisia tietoja
		osva.setTallennusRekisterinTarkenne(testOID);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_Potilas_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setOstopalvelunTyyppi(KantaCDAConstants.OstopalvelunTyyppi.POTILASKOHTAINEN_OSTOPALVELU.getTyyppiValue());
		osva.setPotilas(null);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_PotilanHetu_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setOstopalvelunTyyppi(KantaCDAConstants.OstopalvelunTyyppi.POTILASKOHTAINEN_OSTOPALVELU.getTyyppiValue());
		osva.setPotilas(new HenkilotiedotTO(new KokoNimiTO(test, test), null));
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_PotilanNimi_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setOstopalvelunTyyppi(KantaCDAConstants.OstopalvelunTyyppi.POTILASKOHTAINEN_OSTOPALVELU.getTyyppiValue());
		osva.setPotilas(new HenkilotiedotTO(null, testHetu));
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_LuovutuettevanAineistonAlkuJaLuovutettavatPalvelutapahtumat_puuttuvat() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setOstopalvelunTyyppi(KantaCDAConstants.OstopalvelunTyyppi.POTILASKOHTAINEN_OSTOPALVELU.getTyyppiValue());
		osva.setPotilas(new HenkilotiedotTO(new KokoNimiTO(test, test), testHetu));
		osva.setKaikkiAsiakirjat(false);
		osva.setLuovutettavanAineistonLoppu(new Date());
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_LuovutuettevanAineistonLoppuJaLuovutettavatPalvelutapahtumat_puuttuvat() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setOstopalvelunTyyppi(KantaCDAConstants.OstopalvelunTyyppi.POTILASKOHTAINEN_OSTOPALVELU.getTyyppiValue());
		osva.setPotilas(new HenkilotiedotTO(new KokoNimiTO(test, test), testHetu));
		osva.setKaikkiAsiakirjat(false);
		osva.setLuovutettavanAineistonAlku(new Date());
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_Tallentaja_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setAsiakirjanTallentaja(null);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_TallentajanHetu_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setAsiakirjanTallentaja(new HenkilotiedotTO(new KokoNimiTO(test, test), null));
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_TallentajanNimi_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setAsiakirjanTallentaja(new HenkilotiedotTO(null, testHetu));
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoi_TallentajanPalveluyksikko_puuttuu() {
		OstopalvelunvaltuutusTO osva = luoMinimiOsva();
		osva.setAsiakirjanTallentaja(new HenkilotiedotTO(new KokoNimiTO(test, test), testHetu));
		osva.setAmmattihenkilonPalveluyksikko(null);
		OstopalvelunvaltuutusValidoija tested = new OstopalvelunvaltuutusValidoija(osva);
		tested.validoi();
	}
	
	private OstopalvelunvaltuutusTO luoMinimiOsva() {
		OstopalvelunvaltuutusTO osva = new OstopalvelunvaltuutusTO();
		osva.setOstopalvelunTyyppi(KantaCDAConstants.OstopalvelunTyyppi.VAESTOTASOINEN_OSTOPALVELU.getTyyppiValue());
		osva.setValtuutuksenVoimassaoloAlku(new Date());
		osva.setValtuutuksenVoimassaoloLoppu(new Date());
		osva.setAsiakirjanRekisterinpitaja(testOID);
		osva.setAsiakirjanRekisterinpitajaNimi(test);
		osva.setPalvelunJarjestaja(testOID);
		osva.setPalvelunJarjestajaNimi(test);
		osva.setPalvelunJarjestajanPalveluyksikko(testOID);
		osva.setPalvelunJarjestajanPalveluyksikonNimi(test);
		osva.setPalvelunTuottaja(testOID);
		osva.setPalvelunTuottajanNimi(test);
		osva.setTallennusRekisterinpitaja(testOID);
		osva.setTallennusRekisteri(KantaCDAConstants.PotilasasiakirjanRekisteritunnus.ARKISTOASIAKIRJAT.getRekisteritunnusValue());
		osva.setKaikkiAsiakirjat(true);
		osva.setAsiakirjanTallentaja(new HenkilotiedotTO(new KokoNimiTO(test, test), testHetu));
		osva.setAmmattihenkilonPalveluyksikko(testOID);
		return osva;
	}
}
