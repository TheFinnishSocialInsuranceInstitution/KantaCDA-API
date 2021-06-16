package fi.kela.kanta.cda;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.commons.configuration.ConfigurationException;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.junit.Test;

import fi.kela.kanta.exceptions.PurkuException;
import fi.kela.kanta.to.OstopalvelunvaltuutusTO;

public class OstopalvelunvaltuutusCDAIT {

	private static final String propertyFile = "testi_arkisto.properties";
	
	@Test
	public void puraKasaaJaPuraOstopalvelunvaltuutusTest() throws ConfigurationException, PurkuException, IOException, JAXBException {
		String cda = KantaCDATestUtils.lataa("Esim_OSVA_Omakannasta.xml");
		OstopalvelunvaltuutusPurkaja testedPurkaja = new OstopalvelunvaltuutusPurkaja();
		OstopalvelunvaltuutusTO osva = testedPurkaja.puraOstopalvelunvaltuutus(cda);
		
		assertNotNull("Purettu ostopalvelunvaltuutus ei voi olla null.", osva);
		String uusiOid = "1.2.3.4.5.1234";
		String vanhaOid = osva.getOid();
		osva.setOid(uusiOid);
		
		OstopalvelunvaltuutusKasaaja testedKasaaja = new OstopalvelunvaltuutusKasaaja(KantaCDATestUtils.loadProperties(propertyFile), osva, vanhaOid);
		POCDMT000040ClinicalDocument kasattuCda = testedKasaaja.kasaaCDA();
		assertNotNull("Kasattu cda ei voi olla null.", kasattuCda);
		
		String kasattuCdaDoc = testedKasaaja.kasaaAsiakirja(); 
		
		//System.out.println(kasattuCdaDoc);
		OstopalvelunvaltuutusTO purettuOsva = testedPurkaja.puraOstopalvelunvaltuutus(kasattuCdaDoc);
		assertPuretut(osva, purettuOsva);
	}

	private void assertPuretut(OstopalvelunvaltuutusTO osva1, OstopalvelunvaltuutusTO osva2) {
		assertNotNull("Purettu osva ei voi olla null", osva1);
		assertNotNull("Kasattu cda ei voi olla null", osva2);
		//osva.getPotilas()
		assertTrue("Ostopalvelun tyyppi ei saa muuttua", osva1.getOstopalvelunTyyppi() == osva2.getOstopalvelunTyyppi());

		//Onko Date toString riittävä tarkkuus?
		assertEquals("Ostopalvelun valutuutuksen voimassaolon alku ei saa muuttua", osva1.getValtuutuksenVoimassaoloAlku().toString(), osva2.getValtuutuksenVoimassaoloAlku().toString());
		assertEquals("Ostopalvelun valutuutuksen voimassaolon loppu ei saa muuttua", osva1.getValtuutuksenVoimassaoloLoppu().toString(), osva2.getValtuutuksenVoimassaoloLoppu().toString());
		assertEquals("Ostopalvelun palvelunjärjestäjä ei saa muuttua", osva1.getPalvelunJarjestaja(), osva2.getPalvelunJarjestaja());
		assertEquals("Ostopalvelun palvelunjärjestäjän nimi ei saa muuttua", osva1.getPalvelunJarjestajaNimi(), osva2.getPalvelunJarjestajaNimi());
		assertEquals("Ostopalvelun palvelunjärjestäjän palveluyksikkö ei saa muuttua", osva1.getPalvelunJarjestajanPalveluyksikko(), osva2.getPalvelunJarjestajanPalveluyksikko());
		assertEquals("Ostopalvelun palvelunjärjestäjän palveluyksikön nimi ei saa muuttua", osva1.getPalvelunJarjestajanPalveluyksikonNimi(), osva2.getPalvelunJarjestajanPalveluyksikonNimi());
		assertEquals("Ostopalvelun palveluntuottaja ei saa muuttua", osva1.getPalvelunTuottaja(), osva2.getPalvelunTuottaja());
		assertEquals("Ostopalvelun palveluntuottajan nimi ei saa muuttua", osva1.getPalvelunTuottajanNimi(), osva2.getPalvelunTuottajanNimi());
		assertEquals("Ostopalvelun haku rekisterinpitäjä ei saa muuttua", osva1.getHakuRekisterinpitaja(), osva2.getHakuRekisterinpitaja());
		assertEquals("Ostopalvelun haku rekisteri ei saa muuttua", osva1.getHakuRekisteri(), osva2.getHakuRekisteri());
		assertEquals("Ostopalvelun haku rekisterin tarkenne ei saa muuttua", osva1.getHakuRekisterinTarkenne(), osva2.getHakuRekisterinTarkenne());
		assertEquals("Ostopalvelun haku rekisterin tarkenteen nimi ei saa muuttua", osva1.getHakuRekisterinTarkentimenNimi(), osva2.getHakuRekisterinTarkentimenNimi());
		assertEquals("Ostopalvelun tallennus rekisterinpitäjä ei saa muuttua", osva1.getTallennusRekisterinpitaja(), osva2.getTallennusRekisterinpitaja());
		assertEquals("Ostopalvelun tallennus rekisteri ei saa muuttua", osva1.getTallennusRekisteri(), osva2.getTallennusRekisteri());
		assertEquals("Ostopalvelun tallennus rekisterin tarkenne ei saa muuttua", osva1.getTallennusRekisterinTarkenne(), osva2.getTallennusRekisterinTarkenne());
		assertEquals("Ostopalvelun tallennus rekisterin tarkenteen nimi ei saa muuttua", osva1.getTallennusRekisterinTarkentimenNimi(), osva2.getTallennusRekisterinTarkentimenNimi());
		assertEquals("Ostopalvelun tieto kaikkien asiakirjojen luovutuksesta ei saa muuttua", osva1.isKaikkiAsiakirjat(), osva2.isKaikkiAsiakirjat());
		//assertEquals("Ostopalvelun tallentaja ei saa muuttua", osva1.getAsiakirjanTallentaja(), osva2.getAsiakirjanTallentaja());
		assertEquals("Ostopalvelun ammattihenkilön palveluyksikkö ei saa muuttua", osva1.getAmmattihenkilonPalveluyksikko(), osva2.getAmmattihenkilonPalveluyksikko());
		assertEquals("Ostopalvelun ammattihenkilön palveluyksikön nimi ei saa muuttua", osva1.getAmmattihenkilonPalveluyksikonNimi(), osva2.getAmmattihenkilonPalveluyksikonNimi());
		assertEquals("Ostopalvelun luovutettavan aineiston alku ei saa muuttua", osva1.getLuovutettavanAineistonAlku(), osva2.getLuovutettavanAineistonAlku());
		assertEquals("Ostopalvelun luovutettavan aineiston loppu ei saa muuttua", osva1.getLuovutettavanAineistonLoppu(), osva2.getLuovutettavanAineistonLoppu());
		assertEquals("Ostopalvelun luovutettavat palvelutapahtumat ei saa muuttua", osva1.getLuovutettavatPalvelutapahtumat(), osva2.getLuovutettavatPalvelutapahtumat());	
	}

}
