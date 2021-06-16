package fi.kela.kanta.cda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.hl7.v3.ANY;
import org.hl7.v3.BL;
import org.hl7.v3.CV;
import org.hl7.v3.EnFamily;
import org.hl7.v3.EnGiven;
import org.hl7.v3.II;
import org.hl7.v3.IVLTS;
import org.hl7.v3.IVXBTS;
import org.hl7.v3.ON;
import org.hl7.v3.ObjectFactory;
import org.hl7.v3.PN;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component1;
import org.hl7.v3.POCDMT000040Component2;
import org.hl7.v3.POCDMT000040Component3;
import org.hl7.v3.POCDMT000040Component5;
import org.hl7.v3.POCDMT000040Custodian;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040Organization;
import org.hl7.v3.POCDMT000040RecordTarget;
import org.hl7.v3.POCDMT000040Section;
import org.hl7.v3.StrucDocText;
import org.junit.Before;
import org.junit.Test;

import fi.kela.kanta.exceptions.PurkuException;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.OstopalvelunvaltuutusTO;

public class OstopalvelunvaltuutusPurkajaTest {
	
	private ObjectFactory of;
	private final static String testOid = "1.2.3.4.5.12234";
	private final static String testName = "testName";
	private static final String testHetu = "121212-9876";
	private static final String testKokoNimi = null;
	private static final int testRekisteri = 76;
	private static final int testOstopalvelunTyyppi = 28;
	private static final String testText = "test Text";
	private static final String tyoterveysTarkenneRoot = "1.2.246.10";
	private static final String testHetuRoot = "1.2.246.21";
	private Date testDate1;
	private Date testDate2;
	
	@Before
	public void setup() {
		of = new ObjectFactory();
		testDate1 = new Date();
		testDate2 = new Date();
	}

	@Test
	public void testPuraOstopalvelunvaltuutus() throws ConfigurationException, PurkuException {
		String cda = KantaCDATestUtils.lataa("Esim_OSVA_Omakannasta.xml");
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		OstopalvelunvaltuutusTO osva = tested.puraOstopalvelunvaltuutus(cda);
		
		assertNotNull("Purettu ostopalvelunvaltuutus ei voi olla null.", osva);
//		p("oid: "+osva.getOid());
//		p("setId: "+osva.getSetId());
//		p("cdaTyypi: "+osva.getCdaTyyppi());
//		p("versio: "+osva.getVersio());
//		p("aikaleima: "+osva.getAikaleima());
//		p("product: "+osva.getProduct());
//		p("productVersion: "+osva.getProductVersion());
//		p("TemplateIdt-");
//		for(String templateId : osva.getTemplateIds()) {
//			p(templateId);
//		}
//		p("Määreittelyluokka: "+osva.getMaarittelyLuokka().getKuvaus());
//		p("Ammattihenkilo:"+osva.getAsiakirjanTallentaja().getNimi().getKokoNimi());
	}

	public void p(String s) {
		System.out.println(s);
	}
	
	@Test
	public void testPuraCustodian() throws Exception {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		POCDMT000040ClinicalDocument clinicalDocument = of.createPOCDMT000040ClinicalDocument();
		OstopalvelunvaltuutusTO osva = new OstopalvelunvaltuutusTO();
		clinicalDocument.setCustodian(luoTestCustodian(testOid,testName));
		
		tested.puraCustodian(clinicalDocument, osva);
		
		assertEquals("",testOid,osva.getAsiakirjanRekisterinpitaja());
		assertEquals("",testName,osva.getAsiakirjanRekisterinpitajaNimi());
	}

	private POCDMT000040Custodian luoTestCustodian(String oid, String name) {
		POCDMT000040Custodian custodian = of.createPOCDMT000040Custodian();
		custodian.setAssignedCustodian(of.createPOCDMT000040AssignedCustodian());
		custodian.getAssignedCustodian().setRepresentedCustodianOrganization(of.createPOCDMT000040CustodianOrganization());
		II value = of.createII();
		value.setRoot(oid);
		custodian.getAssignedCustodian().getRepresentedCustodianOrganization().getIds().add(value);
		ON nameValue = of.createON();
		nameValue.getContent().add(name);
		custodian.getAssignedCustodian().getRepresentedCustodianOrganization().setName(nameValue);
		return custodian;
	}

	@Test
	public void testPuraComponentOf() throws ConfigurationException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		POCDMT000040ClinicalDocument clinicalDocument = of.createPOCDMT000040ClinicalDocument();
		OstopalvelunvaltuutusTO osva = new OstopalvelunvaltuutusTO();
		clinicalDocument.setComponentOf(luoTestComponentOf(testOid,testName));
		
		tested.puraComponentOf(clinicalDocument, osva);
		
		assertEquals("",testOid,osva.getPalvelunTuottaja());
		assertEquals("",testName,osva.getPalvelunTuottajanNimi());
	}

	private POCDMT000040Component1 luoTestComponentOf(String oid, String name) {
		POCDMT000040Component1 component = of.createPOCDMT000040Component1();
		component.setEncompassingEncounter(of.createPOCDMT000040EncompassingEncounter());
		component.getEncompassingEncounter().setResponsibleParty(of.createPOCDMT000040ResponsibleParty());
		component.getEncompassingEncounter().getResponsibleParty().setAssignedEntity(of.createPOCDMT000040AssignedEntity());
		POCDMT000040Organization organization = of.createPOCDMT000040Organization();
		II idValue = of.createII();
		idValue.setRoot(oid);
		organization.getIds().add(idValue);
		ON nameValue = of.createON();
		nameValue.getContent().add(name);
		organization.getNames().add(nameValue);
		component.getEncompassingEncounter().getResponsibleParty().getAssignedEntity().setRepresentedOrganization(organization);
		return component;
	}

	@Test
	public void testPuraOstopalvelunTiedot() throws ConfigurationException, PurkuException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		//POCDMT000040ClinicalDocument, OstopalvelunvaltuutusTO
		POCDMT000040ClinicalDocument clinicalDocument = of.createPOCDMT000040ClinicalDocument();
		OstopalvelunvaltuutusTO osva = new OstopalvelunvaltuutusTO();
		List<String> componentCodes = new ArrayList<String>();
		componentCodes.add(KantaCDAConstants.OstopalvelunValtuutus.OSTOPALVELUN_TYYPPI);
		componentCodes.add(KantaCDAConstants.OstopalvelunValtuutus.OSTOPALVELUN_VALTUUTUKSEN_VOIMASSAOLO);
		componentCodes.add(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_JARJESTAJA);
		componentCodes.add(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_TUOTTAJA);
		componentCodes.add(KantaCDAConstants.OstopalvelunValtuutus.OSTOP_TUOTT_OIKEUS_HAKEA_PALVELUN_JARJ_REK);
		componentCodes.add(KantaCDAConstants.OstopalvelunValtuutus.OSTOP_TUOTT_OIKEUS_TALLENTAA_PALV_JARJ_REK);
		componentCodes.add(KantaCDAConstants.OstopalvelunValtuutus.LUOVUTETTAVAT_ASIAKIRJAT);
		componentCodes.add(KantaCDAConstants.OstopalvelunValtuutus.ASIAKIRJAN_TALLENTAJA);
		componentCodes.add("53423");
		clinicalDocument.setComponent(luoTestOstopalvelunTiedot(componentCodes));
		
		tested.puraOstopalvelunTiedot(clinicalDocument, osva);
		
		//assertEquals("",testOid,osva.getPalvelunTuottaja());
		//assertEquals("",testName,osva.getPalvelunTuottajanNimi());
		//TODO
	}

	private POCDMT000040Component2 luoTestOstopalvelunTiedot(List<String> componentCodes) {
		POCDMT000040Component2 component = of.createPOCDMT000040Component2();
		component.setStructuredBody(of.createPOCDMT000040StructuredBody());
		for (String componentCode: componentCodes) {
			component.getStructuredBody().getComponents().add(luoOstopalvelunTietoComponent(componentCode));
		}
		return component;
	}

	private POCDMT000040Component3 luoOstopalvelunTietoComponent(String componentCode) {
		POCDMT000040Component3 component = of.createPOCDMT000040Component3();
		component.setSection(of.createPOCDMT000040Section());
		component.getSection().setCode(of.createCE());
		component.getSection().getCode().setCode(componentCode);
		return component;
	}
	
	private POCDMT000040Component5 luoOstopalvelunTarkempiTietoComponent(String componentCode, String text, ANY value) {
		POCDMT000040Component5 component = of.createPOCDMT000040Component5();
		component.setSection(of.createPOCDMT000040Section());
		component.getSection().setCode(of.createCE());
		component.getSection().getCode().setCode(componentCode);
		if (null != text) {
			component.getSection().setText(luoTestStrucDocText(text));
		}
		if (null != value) {
			component.getSection().getEntries().add(luoTestEntryObservationValue(value));
		}
		return component;
	}

	private POCDMT000040Entry luoTestEntryObservationValue(ANY value) {
		POCDMT000040Entry entry = of.createPOCDMT000040Entry();
		entry.setObservation(of.createPOCDMT000040Observation());
		entry.getObservation().getValues().add(value);
		return entry;
	}

	private StrucDocText luoTestStrucDocText(String text) {
		StrucDocText sdText = of.createStrucDocText();
		sdText.getContent().add(text);
		return sdText;
	}

	@Test
	public void testPuraOstopalvelunTyyppi() throws ConfigurationException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		POCDMT000040Section section = of.createPOCDMT000040Section();
		OstopalvelunvaltuutusTO osva = new OstopalvelunvaltuutusTO();
		section.getComponents().add(luoTestOstopalvelunTyyppi(testOstopalvelunTyyppi));
		
		tested.puraOstopalvelunTyyppi(section, osva);
		
		assertEquals("",testOstopalvelunTyyppi,osva.getOstopalvelunTyyppi());
	}

	private POCDMT000040Component5 luoTestOstopalvelunTyyppi(int testostopalveluntyyppi) {
		CV value = of.createCV();
		value.setCode(String.valueOf(testostopalveluntyyppi));
		return luoOstopalvelunTarkempiTietoComponent(KantaCDAConstants.OstopalvelunValtuutus.OSTOPALVELUN_TYYPPI_KOODI, testText, value);
	}

	@Test
	public void testPuraOstopalvelunvaltuutuksenVoimassaolo() throws ConfigurationException, PurkuException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		POCDMT000040Section section = of.createPOCDMT000040Section();
		OstopalvelunvaltuutusTO osva = new OstopalvelunvaltuutusTO();
		section.getComponents().add(luoTestOstopalvelunvaltuutuksenVoimassaolo(testDate1,testDate2));
		
		tested.puraOstopalvelunvaltuutuksenVoimassaolo(section, osva);
		
		//TODO pvm assertointi
		assertNotNull("",osva.getValtuutuksenVoimassaoloAlku());
		assertNotNull("",osva.getValtuutuksenVoimassaoloLoppu());
	}

	private POCDMT000040Component5 luoTestOstopalvelunvaltuutuksenVoimassaolo(Date date1, Date date2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyymmDD");
		IVLTS value = of.createIVLTS();
		value.setHigh(of.createIVXBTS());
		value.setLow(of.createIVXBTS());
		value.getHigh().setValue(sdf.format(date1));
		value.getLow().setValue(sdf.format(date2));
		return luoOstopalvelunTarkempiTietoComponent(KantaCDAConstants.OstopalvelunValtuutus.ASIAKIRJA_VOIMASSA, testText, value);
	}

	@Test
	public void testPuraOstopalvelunJarjestaja() throws ConfigurationException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		POCDMT000040Section section = of.createPOCDMT000040Section();
		OstopalvelunvaltuutusTO osva = new OstopalvelunvaltuutusTO();
		section.getComponents().addAll(luoTestOstopalvelunJarjestaja(testOid,testName,testOid,testName));
		
		tested.puraOstopalvelunJarjestaja(section, osva);
		
		assertEquals("",testOid,osva.getPalvelunJarjestaja());
		assertEquals("",testName,osva.getPalvelunJarjestajaNimi());
		assertEquals("",testOid,osva.getPalvelunJarjestajanPalveluyksikko());
		assertEquals("",testName,osva.getPalvelunJarjestajanPalveluyksikonNimi());
	}

	private List<POCDMT000040Component5> luoTestOstopalvelunJarjestaja(String oid, String name, String palveluyksikkooid,
			String palveluyksikkoname) {
		List<POCDMT000040Component5> components = new ArrayList<POCDMT000040Component5>();
		II oidValue = of.createII();
		oidValue.setRoot(oid);
		II pyOidValue = of.createII();
		pyOidValue.setRoot(palveluyksikkooid);
		components.add(luoOstopalvelunTarkempiTietoComponent(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_JARJESTAJA_YKSILOINTITUNNUS, oid, oidValue));
		components.add(luoOstopalvelunTarkempiTietoComponent(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_JARJESTAJAN_NIMI, name, null));
		components.add(luoOstopalvelunTarkempiTietoComponent(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_JARJESTAJA_PALVELUYKSIKKO, palveluyksikkooid,pyOidValue));
		components.add(luoOstopalvelunTarkempiTietoComponent(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_JARJESTAJA_PALVELUYKSIKON_NIMI, palveluyksikkoname, null));
		return components;
	}

	@Test
	public void testPuraOstopalvelunTuottaja() throws ConfigurationException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		POCDMT000040Section section = of.createPOCDMT000040Section();
		OstopalvelunvaltuutusTO osva = new OstopalvelunvaltuutusTO();
		section.getComponents().addAll(luoTestOstopalvelunTuottaja(testOid,testName));
		
		tested.puraOstopalvelunTuottaja(section, osva);
		
		assertEquals("",testOid,osva.getPalvelunTuottaja());
		assertEquals("",testName,osva.getPalvelunTuottajanNimi());
	}

	private List<POCDMT000040Component5> luoTestOstopalvelunTuottaja(String oid, String name) {
		List<POCDMT000040Component5> components = new ArrayList<POCDMT000040Component5>();
		II oidValue = of.createII();
		oidValue.setRoot(oid);
		components.add(luoOstopalvelunTarkempiTietoComponent(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_TUOTTAJAN_YKSILOINTITUNNUS, oid, oidValue));
		components.add(luoOstopalvelunTarkempiTietoComponent(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_TUOTTAJAN_NIMI, name, null));
		return components;
	}

	@Test
	public void testPuraOstopalvelunTuottajanHakuOikeus() throws ConfigurationException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		POCDMT000040Section section = of.createPOCDMT000040Section();
		OstopalvelunvaltuutusTO osva = new OstopalvelunvaltuutusTO();
		section.getComponents().addAll(luoTestOstopalvelunTuottajanHakuOikeus(testOid,testRekisteri,testOid,testName));
		
		tested.puraOstopalvelunTuottajanHakuOikeus(section, osva);
		
		assertEquals("",testOid,osva.getHakuRekisterinpitaja());
		assertEquals("",testRekisteri,osva.getHakuRekisteri());
		//Nämä vain työterveydessä
		//assertEquals("",testOid,osva.getHakuRekisterinTarkenne());
		//assertEquals("",testName,osva.getHakuRekisterinTarkentimenNimi());
	}

	private List<POCDMT000040Component5> luoTestOstopalvelunTuottajanHakuOikeus(String oid, int rekisteri,
			String tarkenne, String name) {
		List<POCDMT000040Component5> components = new ArrayList<POCDMT000040Component5>();
		II oidValue = of.createII();
		oidValue.setRoot(oid);
		CV rekisteriValue = of.createCV();
		rekisteriValue.setCode(String.valueOf(rekisteri));
		II tarkenneValue = of.createII();
		tarkenneValue.setRoot(tyoterveysTarkenneRoot);
		tarkenneValue.setExtension(tarkenne);
		components.add(luoOstopalvelunTarkempiTietoComponent(KantaCDAConstants.OstopalvelunValtuutus.REKISTERINPITAJA_HAKU, oid, oidValue));
		components.add(luoOstopalvelunTarkempiTietoComponent(KantaCDAConstants.OstopalvelunValtuutus.REKISTERI_HAKU, null, rekisteriValue));
		if (rekisteri == KantaCDAConstants.PotilasasiakirjanRekisteritunnus.TYOTERVEYSHUOLTO.getRekisteritunnusValue()) {
			components.add(luoOstopalvelunTarkempiTietoComponent(KantaCDAConstants.OstopalvelunValtuutus.REKISTERIN_TARKENNE_HAKU, tarkenne ,tarkenneValue));
			components.add(luoOstopalvelunTarkempiTietoComponent(KantaCDAConstants.OstopalvelunValtuutus.REKISTERIN_TARKENTIMEN_NIMI_HAKU, name, null));
		}
		return components;
	}

	@Test
	public void testPuraOstopalvelunTuottajanTallennusOikeus() throws ConfigurationException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		POCDMT000040Section section = of.createPOCDMT000040Section();
		OstopalvelunvaltuutusTO osva = new OstopalvelunvaltuutusTO();
		section.getComponents().addAll(luoTestOstopalvelunTuottajanTallennusOikeus(testOid,testRekisteri,testOid,testName));
		
		tested.puraOstopalvelunTuottajanTallennusOikeus(section, osva);
		
		assertEquals("",testOid,osva.getTallennusRekisterinpitaja());
		assertEquals("",testRekisteri,osva.getTallennusRekisteri());
		//Nämä vain työterveydessä
		//assertEquals("",testOid,osva.getTallennusRekisterinTarkenne());
		//assertEquals("",testName,osva.getTallennusRekisterinTarkentimenNimi());
	}

	private List<POCDMT000040Component5> luoTestOstopalvelunTuottajanTallennusOikeus(String oid, int rekisteri,
			String tarkenne, String tarkenneName) {
		List<POCDMT000040Component5> components = new ArrayList<POCDMT000040Component5>();
		II oidValue = of.createII();
		oidValue.setRoot(oid);
		CV rekisteriValue = of.createCV();
		rekisteriValue.setCode(String.valueOf(rekisteri));
		II tarkenneValue = of.createII();
		tarkenneValue.setRoot(tyoterveysTarkenneRoot);
		tarkenneValue.setExtension(tarkenne);
		components.add(luoOstopalvelunTarkempiTietoComponent(KantaCDAConstants.OstopalvelunValtuutus.REKISTERINPITAJA_TALLENNUS, oid, oidValue));
		components.add(luoOstopalvelunTarkempiTietoComponent(KantaCDAConstants.OstopalvelunValtuutus.REKISTERI_TALLENNUS, null, rekisteriValue));
		if (rekisteri == KantaCDAConstants.PotilasasiakirjanRekisteritunnus.TYOTERVEYSHUOLTO.getRekisteritunnusValue()) {
			components.add(luoOstopalvelunTarkempiTietoComponent(KantaCDAConstants.OstopalvelunValtuutus.REKISTERIN_TARKENNE_TALLENNUS, tarkenne ,tarkenneValue));
			components.add(luoOstopalvelunTarkempiTietoComponent(KantaCDAConstants.OstopalvelunValtuutus.REKISTERIN_TARKENTIMEN_NIMI_TALLENNUS, tarkenneName, null));
		}
		return components;
	}

	@Test
	public void testPuraOstopalvelunLuovuttettavatAsiakirjat() throws ConfigurationException, PurkuException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		POCDMT000040Section section = of.createPOCDMT000040Section();
		OstopalvelunvaltuutusTO osva = new OstopalvelunvaltuutusTO();
		section.getComponents().add(luoTestLuovuttettavatAsiakirjat(true));
		
		tested.puraOstopalvelunLuovuttettavatAsiakirjat(section, osva);
		
		assertTrue("",osva.isKaikkiAsiakirjat());
	}

	private POCDMT000040Component5 luoTestLuovuttettavatAsiakirjat(boolean kaikkiasiakirjat) {
		BL value = of.createBL();
		value.setValue(kaikkiasiakirjat);
		return luoOstopalvelunTarkempiTietoComponent(KantaCDAConstants.OstopalvelunValtuutus.KAIKKI_ASIAKIRJAT, kaikkiasiakirjat?"kyllä":"ei", value);
	}

	@Test
	public void testPuraAsiakirjanTallentaja() throws ConfigurationException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		POCDMT000040Section section = of.createPOCDMT000040Section();
		OstopalvelunvaltuutusTO osva = new OstopalvelunvaltuutusTO();
		List<String> names = new ArrayList<String>();
		names.add("eka");
		names.add("toka");
		section.getComponents().addAll(luoTestAsiakirjanTallentaja(testHetu,names,testName, testOid));
		
		tested.puraAsiakirjanTallentaja(section, osva);
		
		assertEquals("",testHetu,osva.getAsiakirjanTallentaja().getHetu());
		//TODO: assertoi muut tallentajan tiedot
		
		//assertEquals("",testOid,osva.getTallennusRekisterinTarkenne());
		//assertEquals("",testName,osva.getTallennusRekisterinTarkentimenNimi());
	}

	private List<POCDMT000040Component5> luoTestAsiakirjanTallentaja(String hetu, List<String> names, String palveluyksikkoOid, String palveluyksikkoName) {
		List<POCDMT000040Component5> components = new ArrayList<POCDMT000040Component5>();
		II hetuValue = of.createII();
		hetuValue.setRoot(testHetuRoot);
		hetuValue.setExtension(hetu);
		PN nameValue = luoTestPN(names);
		II pyOidValue = of.createII();
		pyOidValue.setRoot(palveluyksikkoOid);
		components.add(luoOstopalvelunTarkempiTietoComponent(KantaCDAConstants.OstopalvelunValtuutus.AMMATTIHENKILON_TUNNISTE, hetu, hetuValue));
		components.add(luoOstopalvelunTarkempiTietoComponent(KantaCDAConstants.OstopalvelunValtuutus.AMMATTIHENKILON_NIMI, null, nameValue));
		components.add(luoOstopalvelunTarkempiTietoComponent(KantaCDAConstants.OstopalvelunValtuutus.AMMATTIHENKILON_PALVELUYKSIKKO, palveluyksikkoName,pyOidValue));
		return components;
	}

	private PN luoTestPN(List<String> names) {
		PN nameValue = of.createPN();
		boolean family = false;
		boolean givenCL = false;
		for(String name : names) {
			if (!family) {
				EnFamily enfamily = of.createEnFamily();
				enfamily.getContent().add(name);
				nameValue.getContent().add(of.createENFamily(enfamily));
				family=true;
				continue;
			}
			EnGiven given = of.createEnGiven();
			given.getContent().add(name);
			if (!givenCL) {
				given.getQualifiers().add("CL");
				givenCL = true;
			}
			nameValue.getContent().add(of.createENGiven(given));
		}
		return nameValue;
	}

	@Test
	public void testPuraAuthor() throws ConfigurationException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		POCDMT000040ClinicalDocument clinicalDocument = of.createPOCDMT000040ClinicalDocument();
		OstopalvelunvaltuutusTO osva = new OstopalvelunvaltuutusTO();
		clinicalDocument.setCustodian(luoTestAuthor(testOid,testName));
		
		tested.puraAuthor(clinicalDocument, osva);
		
		//assertEquals("",testOid,osva.get);
	}

	private POCDMT000040Custodian luoTestAuthor(String testoid2, String testname2) {
		// TODO Auto-generated method stub
		return null;
	}

	//@Test
	public void testPuraPotilas() throws ConfigurationException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		POCDMT000040ClinicalDocument clinicalDocument = of.createPOCDMT000040ClinicalDocument();
		OstopalvelunvaltuutusTO osva = new OstopalvelunvaltuutusTO();
		clinicalDocument.getRecordTargets().add((luoTestPotilas(testHetu,testKokoNimi)));
		
		tested.puraPotilas(clinicalDocument, osva);
		
		assertEquals("",testOid,osva.getPalvelunJarjestaja());
		assertEquals("",testName,osva.getPalvelunJarjestajaNimi());
	}

	private POCDMT000040RecordTarget luoTestPotilas(String testhetu2, String testkokonimi2) {
		//TODO
		return null;
	}

	@Test
	public void testPuraAikaVali() throws ConfigurationException, PurkuException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
//		Date high = new Date();
//		Date low = new Date();
		String highDate ="201704151030";
		String lowDate = "195003200920";
		IVLTS value = of.createIVLTS();
		IVXBTS highValue = of.createIVXBTS();
		IVXBTS lowValue = of.createIVXBTS();
		highValue.setValue(highDate);
		lowValue.setValue(lowDate);
		value.setHigh(highValue);
		value.setLow(lowValue);
		OstopalvelunvaltuutusPurkaja.PurettuAikavali aikavali = tested.puraAikaVali(value);
		assertEquals("Korkea aika on väärässä", "Sat Apr 15 10:30:00 EEST 2017", aikavali.high.toString());
		assertEquals("Matala aika on väärässä", "Mon Mar 20 09:20:00 EET 1950", aikavali.low.toString());
	}
	
	@Test
	public void testPuraStrucDocText() throws ConfigurationException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		String teksti = "teksti";
		StrucDocText text = of.createStrucDocText();
		text.getContent().add(teksti);
		assertEquals(teksti, tested.puraStrucDocText(text));
	}
	
	@Test
	public void testHaeEntryObservationValue() throws ConfigurationException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		POCDMT000040Entry entry = of.createPOCDMT000040Entry();
		entry.setObservation(of.createPOCDMT000040Observation());
		BL value = of.createBL();
		entry.getObservation().getValues().add(value);
		assertEquals(value, tested.haeEntryObservationValue(entry));
	}
	
	@Test
	public void testHaeEntryObservationValue_nullEntry() throws ConfigurationException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		assertNull(tested.haeEntryObservationValue(null));
	}
	
	@Test
	public void testHaeSectionCodeCode() throws ConfigurationException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		String code = "code";
		POCDMT000040Component5 component = of.createPOCDMT000040Component5();
		component.setSection(of.createPOCDMT000040Section());
		component.getSection().setCode(of.createCE());
		component.getSection().getCode().setCode(code);
		assertEquals(code, tested.haeSectionCodeCode(component));
	}

	@Test
	public void testPuraBLValue() throws ConfigurationException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		BL value = of.createBL();
		value.setValue(true);
		assertTrue(tested.puraBLValue(value));
	}

	@Test
	public void testPuraPNValue() throws ConfigurationException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		String name1 = "name1";
		String name2 = "name2";
		String name3 = "name3";
		List<String> names = new ArrayList<String>();
		names.add(name1);
		names.add(name2);
		names.add(name3);
		PN value = luoTestPN(names);
		KokoNimiTO kokoNimi = tested.puraPNValue(value);
		//TODO: assertointi
	}

	@Test
	public void testPuraValue_CV() throws ConfigurationException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		String code = "code";
		CV cvValue = of.createCV();
		cvValue.setCode(code);
		assertEquals(code, tested.puraValue(cvValue));
	}
	
	@Test
	public void testPuraValue_null() throws ConfigurationException {
		OstopalvelunvaltuutusPurkaja tested = new OstopalvelunvaltuutusPurkaja();
		assertNull(tested.puraValue(null));
	}
}
