package fi.kela.kanta.cda;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import javax.xml.bind.JAXBException;

import org.hl7.v3.ANY;
import org.hl7.v3.BL;
import org.hl7.v3.CD;
import org.hl7.v3.CV;
import org.hl7.v3.II;
import org.hl7.v3.IVLTS;
import org.hl7.v3.ON;
import org.hl7.v3.POCDMT000040AssignedCustodian;
import org.hl7.v3.POCDMT000040AssignedEntity;
import org.hl7.v3.POCDMT000040Author;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component2;
import org.hl7.v3.POCDMT000040Component3;
import org.hl7.v3.POCDMT000040Component5;
import org.hl7.v3.POCDMT000040EncompassingEncounter;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040InfrastructureRootTemplateId;
import org.hl7.v3.POCDMT000040Observation;
import org.hl7.v3.POCDMT000040Subject;
import org.hl7.v3.ParticipationTargetSubject;
import org.hl7.v3.StrucDocParagraph;
import org.hl7.v3.StrucDocText;
import org.hl7.v3.TS;
import org.hl7.v3.XActMoodDocumentObservation;
import org.hl7.v3.XActRelationshipDocument;
import org.hl7.v3.XDocumentSubject;

import fi.kela.kanta.cda.validation.OstopalvelunvaltuutusValidoija;
import fi.kela.kanta.cda.validation.OstoplavelunvaltuutuksenMitatointiValidoija;
import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.OstopalvelunvaltuutusTO;
import fi.kela.kanta.util.JaxbUtil;
import fi.kela.kanta.util.KantaCDAUtil;

public class OstopalvelunvaltuutusKasaaja extends ArkistoKasaaja {

	private static final String classCodeCOND = "COND";
	private static final String OSVA_PREFIX = "OSVA";
//TODO: maarittelyVerio, BL_KYLLA ja BL_EI .. ehkä kuitenkin voisi hakea propsuista
	private static final String BL_KYLLA = "Kyllä";
	private static final String BL_EI = "Ei";
	private static final String maarittelyVersio = "maarittelyversio";
	private static final String OSTOPALVELUN_TYYPPI_TEMPLATE = "ostopalvelunTyyppi.%d";
	private static final String POTILASASIAKIRJAN_REKISTERITUNNUS_TEMPLATE = "potilasasiakirjanRekisteritunnus.%d";
	private static final String henkilotunnusRoot = "henkilotunnus";
	private static final String yritysJaYhteisorekisteriRoot = "yritysJaYhteisorekisteri";
	private static final String MAARITTAMATON_HOITOPROSESSIN_VAIHE = "99";
	private static final String MUU_MERKINTA = "76";
	private final OstopalvelunvaltuutusTO osva;
	private final int edellinenVersio;
	private final String edellinenOid;
	private final String edellinenSetId;
	private boolean tekninenProsessi = false;
	
	public OstopalvelunvaltuutusKasaaja(Properties properties, OstopalvelunvaltuutusTO ostopalvelunvaltuutus) {
		this(properties,ostopalvelunvaltuutus,null);
	}
	
	public OstopalvelunvaltuutusKasaaja(Properties properties, OstopalvelunvaltuutusTO ostopalvelunvaltuutus, String edellinenOid) {
		super(properties);
		this.osva = ostopalvelunvaltuutus;
		this.edellinenOid = edellinenOid;
		this.edellinenVersio = osva.getVersio();
		this.edellinenSetId = osva.getSetId();
		validoija = new OstopalvelunvaltuutusValidoija(ostopalvelunvaltuutus);
	}
	
	public void setTekninenProsessi(boolean tekninenProsessi) {
		this.tekninenProsessi = tekninenProsessi;
	}

	/**
     * Kasaa uusi asiakirja konstruktorissa annetun OstopalvelunvaltuutusTOn pohjalta.
     * 
     * @return Uusi asiakirja XML-muodossa
     * @throws JAXBException
     */
	@Override
	public String kasaaAsiakirja() throws JAXBException {
		return JaxbUtil.getInstance().arkistomarshalloi(kasaaCDA(), "urn:hl7-org:v3 CDA_Fi.xsd");
		//return JaxbUtil.getInstance().marshalloi(kasaaCDA(), "urn:hl7-org:v3 CDA_Fi.xsd");
	}
	
	public String kasaaMitatointiAsiakirja() throws JAXBException {
		validoija = new OstoplavelunvaltuutuksenMitatointiValidoija(osva);
		return JaxbUtil.getInstance().arkistomarshalloi(mitatoiCDA(), "urn:hl7-org:v3 CDA_Fi.xsd");
	}
	
	public POCDMT000040ClinicalDocument mitatoiCDA() {
		validoiAsiakirja();
		validoiMitatointitiedot();
		Calendar now = Calendar.getInstance(TimeZone.getTimeZone(ArkistoKasaaja.TIME_ZONE));
		POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
		
		String effectiveTime = getDateFormat().format(now.getTime());
		
		if (!onkoNullTaiTyhja(osva.getOid())) {
			setDocumentId(osva.getOid());
		}
		addIdFields(cda, osva, effectiveTime);
		
		//Mitätöitäessä ylikirjoitetaan TemplateIdt varmuuden vuoksi
		cda.getTemplateIds().clear();
		POCDMT000040InfrastructureRootTemplateId templateIdElement = of
        	.createPOCDMT000040InfrastructureRootTemplateId();
		fetchAttributes("templateId1", templateIdElement);
		cda.getTemplateIds().add(templateIdElement);

		fetchRestTemplateIds(cda,2);
		
		addRecordTarget(cda, osva.getPotilas());
		
		addAuthor(cda);
		
		addCustodian(cda);
		
		addRelatedDocument(cda, edellinenOid, edellinenSetId, edellinenVersio, XActRelationshipDocument.RPLC);
		
		addComponentOf(cda);
		addLocalHeader(cda);
		//Mitätöitäessä Ylikirjoitetaan recordstatus 
		CV recordStatus = of.createCV();
        if ( fetchAttributes("OSVA.MITATOINTI.localHeader.recordStatus", recordStatus) ) {
            cda.getLocalHeader().setRecordStatus(recordStatus);
        }
		
		POCDMT000040Component2 component2  = of.createPOCDMT000040Component2();
		cda.setComponent(component2);
		component2.setStructuredBody(of.createPOCDMT000040StructuredBody());
		component2.getStructuredBody().setID(getOID(osva));
		POCDMT000040Component3 component3 = of.createPOCDMT000040Component3();
		component2.getStructuredBody().getComponents().add(component3);
		component3.setSection(of.createPOCDMT000040Section());
		
		component3.getSection().setId(of.createII());
		component3.getSection().getId().setRoot(getDocumentId(osva));
		component3.getSection().setCode(of.createCE());
		fetchAttributes(Kasaaja.LM_CONTENTS, component3.getSection().getCode());
		component3.getSection().setTitle(of.createST());
		component3.getSection().getTitle().getContent().add(component3.getSection().getCode().getDisplayName());
		
		component3.getSection().setSubject(luoMitatointiPotilas());
		component3.getSection().getAuthors().add(luoMitatointiAuthor());
		component3.getSection().getComponents().add(luoMitatointiHoitoprosessinVaihe());
		return cda;
	}

	private void validoiMitatointitiedot() {
		if (KantaCDAUtil.onkoNullTaiTyhja(edellinenOid)) {
			throw new IllegalArgumentException("Mitätöitäessä tarvitaan mitätöitävän asiakirjan OID!");
		}
		
	}

	@Override
	public POCDMT000040ClinicalDocument kasaaCDA() {
		validoiAsiakirja();
		Calendar now = Calendar.getInstance(TimeZone.getTimeZone(ArkistoKasaaja.TIME_ZONE));
		
		POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
		
		String effectiveTime = getDateFormat().format(now.getTime());
		//String today = getTodayDateFormat().format(now.getTime());
		if (!onkoNullTaiTyhja(osva.getOid())) {
			setDocumentId(osva.getOid());
		}
		addIdFields(cda, osva, effectiveTime);
		addRecordTarget(cda, osva.getPotilas());
		
		addAuthor(cda);
		
		addCustodian(cda);
		if (cda.getVersionNumber().getValue().intValue()>1 && !onkoNullTaiTyhja(edellinenOid)) { //Kyseessä korvaustilanne
			addRelatedDocument(cda, edellinenOid, edellinenSetId, edellinenVersio, XActRelationshipDocument.RPLC);
		}
		addComponentOf(cda);
		addLocalHeader(cda);
		
		POCDMT000040Component2 component2 = of.createPOCDMT000040Component2();
        cda.setComponent(component2);
        component2.setStructuredBody(of.createPOCDMT000040StructuredBody());
        //ID Structured bodylle
        component2.getStructuredBody().setID(getOID(osva));
        
        POCDMT000040Component3 component3 = of.createPOCDMT000040Component3();
        component2.getStructuredBody().getComponents().add(component3);
        //component3.getTemplateIds().add(of.createPOCDMT000040InfrastructureRootTemplateId());
        // TempalteId
		//fetchAttributes(OstopalvelunvaltuutusKasaaja.template_id, component3.getTemplateIds().get(0));
		component3.setSection(of.createPOCDMT000040Section());
        //component3.getSection().setAttributeID(getNextOID(osva));
		//TemplateId
		component3.getSection().getTemplateIds().add(of.createPOCDMT000040InfrastructureRootTemplateId());
		fetchAttributes(OstopalvelunvaltuutusKasaaja.template_id, component3.getSection().getTemplateIds().get(0));
		
        component3.getSection().setId(of.createII());
        component3.getSection().getId().setRoot(getDocumentId(osva));
        component3.getSection().setCode(of.createCE());
        fetchAttributes(Kasaaja.LM_CONTENTS, component3.getSection().getCode());
        component3.getSection().setTitle(of.createST());
        // Title
        component3.getSection().getTitle().getContent().add(component3.getSection().getCode().getDisplayName());

        //AsiakirjanTunniste
        component3.getSection().getComponents().add(luoAsiakirjanTunniste());

        //OstopalvelunTyyppi
        component3.getSection().getComponents().add(luoOstopalvelunTyyppi());
        //OstopalvelunValtuutuksenVoimassaolo
        component3.getSection().getComponents().add(luoOstopalvelunValtuutuksenVoimassaolo());
        //PalvelunJarjestaja
        component3.getSection().getComponents().add(luoPalvelunJarjestaja());
        //PalvelunTuottaja
        component3.getSection().getComponents().add(luoPalvelunTuottaja());
        //TuottajanOikeusHakeaAsiakirjoja
        if (!KantaCDAUtil.onkoNullTaiTyhja(osva.getHakuRekisterinpitaja())) {
        	component3.getSection().getComponents().add(luoTuottajanOikeusHakeaAsiakirjoja());
        }
        //TuottajanOikeusTallentaaAsiakirjat
        component3.getSection().getComponents().add(luoTuottajanOikeusTallentaaAsiakirjat());
        //Potilas
        component3.getSection().getComponents().add(luoPotilas());
        //LuovutettavatAsiakirjat
        component3.getSection().getComponents().add(luoLuovutettavatAsiakirjat());
        //AsiakirjanTallentaja
        component3.getSection().getComponents().add(luoAsiakirjanTallentaja());
        //LomakkeenMetatiedot
        //String templateId = cda.getTemplateIds().iterator().next().getRoot();
        //component3.getSection().getComponents().add(luoLomakkeenMetatiedot(templateId));
        
		return cda;
	}

	/* (non-Javadoc)
	 * @see fi.kela.kanta.cda.Kasaaja#addCustodian(org.hl7.v3.POCDMT000040ClinicalDocument)
	 */
	@Override
	protected void addCustodian(POCDMT000040ClinicalDocument clinicalDocument) {
		clinicalDocument.setCustodian(of.createPOCDMT000040Custodian());
        POCDMT000040AssignedCustodian assignedCustodian = of.createPOCDMT000040AssignedCustodian();
        clinicalDocument.getCustodian().setAssignedCustodian(assignedCustodian);
        assignedCustodian.setRepresentedCustodianOrganization(of.createPOCDMT000040CustodianOrganization());
        II id = of.createII();
        id.setRoot(osva.getAsiakirjanRekisterinpitaja());
        assignedCustodian.getRepresentedCustodianOrganization().getIds().add(id);
        if (!onkoNullTaiTyhja(osva.getAsiakirjanRekisterinpitajaNimi())) {
        	ON name = of.createON();
        	name.getContent().add(osva.getAsiakirjanRekisterinpitajaNimi());
        	assignedCustodian.getRepresentedCustodianOrganization().setName(name);
        }
	}
	
	/* (non-Javadoc)
	 * @see fi.kela.kanta.cda.Kasaaja#addComponentOf(org.hl7.v3.POCDMT000040ClinicalDocument)
	 */
	@Override
	protected void addComponentOf(POCDMT000040ClinicalDocument clinicalDocument) {
		clinicalDocument.setComponentOf(of.createPOCDMT000040Component1());
        POCDMT000040EncompassingEncounter encompassingEncounter = of.createPOCDMT000040EncompassingEncounter();
        clinicalDocument.getComponentOf().setEncompassingEncounter(encompassingEncounter);
        encompassingEncounter.setEffectiveTime(of.createIVLTS());
        encompassingEncounter.getEffectiveTime().getNullFlavors().add(KantaCDAConstants.NullFlavor.NA.getCode());
        encompassingEncounter.setResponsibleParty(of.createPOCDMT000040ResponsibleParty());

        POCDMT000040AssignedEntity assignedEntity = of.createPOCDMT000040AssignedEntity();
        encompassingEncounter.getResponsibleParty().setAssignedEntity(assignedEntity);
        II nullFlavorId = of.createII();
        nullFlavorId.getNullFlavors().add(KantaCDAConstants.NullFlavor.NA.getCode());
        assignedEntity.getIds().add(nullFlavorId);

        assignedEntity.setRepresentedOrganization(of.createPOCDMT000040Organization());
        assignedEntity.getRepresentedOrganization().getIds().add(of.createII());
        fetchAttributes("componentOf.encompassingEncounter.responsibleParty.assignedEntity.representedOrganization.id",
                assignedEntity.getRepresentedOrganization().getIds().get(0));
        assignedEntity.getRepresentedOrganization().getNames().add(of.createON());
        String name = fetchProperty(
                "componentOf.encompassingEncounter.responsibleParty.assignedEntity.representedOrganization.name");
        if ( !onkoNullTaiTyhja(name) ) {
            assignedEntity.getRepresentedOrganization().getNames().get(0).getContent().add(name);
        }
	}

	/* (non-Javadoc)
	 * @see fi.kela.kanta.cda.ArkistoKasaaja#getTypeKey()
	 */
	@Override
	protected String getTypeKey() {
		return OSVA_PREFIX;
	}

	/**
	 * Luo asiakirjan yksilöivä tunniste lohkon
	 * @return
	 */
	private POCDMT000040Component5 luoAsiakirjanTunniste() {
		POCDMT000040Component5 asiakirjanTunnisteComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.ASIAKIRJAN_TUNNISTE);
		POCDMT000040Component5 asiakirjanYksiloivaTunnisteComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.ASIAKIRJAN_YKSILOIVA_TUNNISTE);
		String asiakirjanTunniste = osva.getOid();
		II value = of.createII();
		value.setRoot(asiakirjanTunniste);
		asiakirjanYksiloivaTunnisteComponent.getSection().setText(luoTextContent(asiakirjanTunniste));
		asiakirjanYksiloivaTunnisteComponent.getSection().getEntries().add(luoEntryObservation(value));
		asiakirjanTunnisteComponent.getSection().getComponents().add(asiakirjanYksiloivaTunnisteComponent);
		return asiakirjanTunnisteComponent;
	}
	
	/**
	 * Luo ostopalvelun tyyppi lohkon
	 * @return
	 */
	private POCDMT000040Component5 luoOstopalvelunTyyppi() {
		POCDMT000040Component5 ostopalvelunTyyppiComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.OSTOPALVELUN_TYYPPI);
		POCDMT000040Component5 ostopalvelunTyyppiTietoComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.OSTOPALVELUN_TYYPPI_KOODI);
		String ostopalvelunTyyppi = String.valueOf(osva.getOstopalvelunTyyppi());
		CV value = of.createCV();
		fetchAttributes(String.format(OSTOPALVELUN_TYYPPI_TEMPLATE, osva.getOstopalvelunTyyppi()),value);
		value.setCode(ostopalvelunTyyppi);
		ostopalvelunTyyppiTietoComponent.getSection().setText(luoTextContent(value.getDisplayName()));
		ostopalvelunTyyppiTietoComponent.getSection().getEntries().add(luoEntryObservation(value));
		ostopalvelunTyyppiComponent.getSection().getComponents().add(ostopalvelunTyyppiTietoComponent);
		return ostopalvelunTyyppiComponent;
	}

	/**
	 * Luo ostopalvelun valtuutuksen voimassaolo lohkon
	 * @return
	 */
	private POCDMT000040Component5 luoOstopalvelunValtuutuksenVoimassaolo() {
		POCDMT000040Component5 valtuutksenVoimassaoloComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.OSTOPALVELUN_VALTUUTUKSEN_VOIMASSAOLO);
		POCDMT000040Component5 voimassaoloComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.ASIAKIRJA_VOIMASSA);

		IVLTS value = of.createIVLTS();
		if (null != osva.getValtuutuksenVoimassaoloAlku()) {
			value.setLow(of.createIVXBTS());
			value.getLow().setValue(getShortDateFormat().format(osva.getValtuutuksenVoimassaoloAlku()));
		}
		if (null != osva.getValtuutuksenVoimassaoloLoppu()) {
			value.setHigh(of.createIVXBTS());
			value.getHigh().setValue(getShortDateFormat().format(osva.getValtuutuksenVoimassaoloLoppu()));
		}
		
		voimassaoloComponent.getSection().setText(luoTextContent(muotoileAikavali(osva.getValtuutuksenVoimassaoloAlku(), osva.getValtuutuksenVoimassaoloLoppu())));
		voimassaoloComponent.getSection().getEntries().add(luoEntryObservation(value));
		valtuutksenVoimassaoloComponent.getSection().getComponents().add(voimassaoloComponent);
		return valtuutksenVoimassaoloComponent;
	}

	/**
	 * Luo palvelun järjestäjä lohkon
	 * @return
	 */
	private POCDMT000040Component5 luoPalvelunJarjestaja() {
		POCDMT000040Component5 palvelunJarjestajaComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_JARJESTAJA);
		POCDMT000040Component5 palvelunJarjestajanYksiloivaTunnisteComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_JARJESTAJA_YKSILOINTITUNNUS);
		POCDMT000040Component5 palvelunJarjestajanNimiComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_JARJESTAJAN_NIMI);
		String palvelunJarjestajanTunniste = osva.getPalvelunJarjestaja();
		String palvelunJarjestajanNimi = osva.getPalvelunJarjestajaNimi();
		
		II palvelunJarjestajanOidValue = of.createII();
		palvelunJarjestajanOidValue.setRoot(palvelunJarjestajanTunniste);
				
		palvelunJarjestajanYksiloivaTunnisteComponent.getSection().setText(luoTextContent(palvelunJarjestajanTunniste));
		palvelunJarjestajanYksiloivaTunnisteComponent.getSection().getEntries().add(luoEntryObservation(palvelunJarjestajanOidValue));
		palvelunJarjestajanNimiComponent.getSection().setText(luoTextContent(palvelunJarjestajanNimi));
		
		palvelunJarjestajaComponent.getSection().getComponents().add(palvelunJarjestajanYksiloivaTunnisteComponent);
		palvelunJarjestajaComponent.getSection().getComponents().add(palvelunJarjestajanNimiComponent);
		
		if (!KantaCDAUtil.onkoNullTaiTyhja(osva.getPalvelunJarjestajanPalveluyksikko())) {
			POCDMT000040Component5 palvelunJarjestajanPalveluyksikkoComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_JARJESTAJA_PALVELUYKSIKKO);
			POCDMT000040Component5 palvelunJarjestajanPalveluyksikonNimiComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_JARJESTAJA_PALVELUYKSIKON_NIMI);

			String palvelunJarjestajanPalveluyksikko = osva.getPalvelunJarjestajanPalveluyksikko();
			String palvelunJarjestajanPalveluykikonNimi = osva.getPalvelunJarjestajanPalveluyksikonNimi();
		
			II palvelunJarjestajanPalveluyksikonOidValue = of.createII();
			palvelunJarjestajanPalveluyksikonOidValue.setRoot(palvelunJarjestajanPalveluyksikko);
		
			palvelunJarjestajanPalveluyksikkoComponent.getSection().setText(luoTextContent(palvelunJarjestajanPalveluyksikko));
			palvelunJarjestajanPalveluyksikkoComponent.getSection().getEntries().add(luoEntryObservation(palvelunJarjestajanPalveluyksikonOidValue));
			palvelunJarjestajanPalveluyksikonNimiComponent.getSection().setText(luoTextContent(palvelunJarjestajanPalveluykikonNimi));
		
			palvelunJarjestajaComponent.getSection().getComponents().add(palvelunJarjestajanPalveluyksikkoComponent);
			palvelunJarjestajaComponent.getSection().getComponents().add(palvelunJarjestajanPalveluyksikonNimiComponent);
		}
		return palvelunJarjestajaComponent;
	}

	/**
	 * Luo palvelun tuottaja lohkon
	 * @return
	 */
	private POCDMT000040Component5 luoPalvelunTuottaja() {
		POCDMT000040Component5 palvelunTuottajaComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_TUOTTAJA);
		POCDMT000040Component5 palvelunTuottajanYksiloivaTunnisteComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_TUOTTAJAN_YKSILOINTITUNNUS);
		POCDMT000040Component5 palvelunTuottajanNimiComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_TUOTTAJAN_NIMI);
		String palvelunTuottajanTunniste = osva.getPalvelunTuottaja();
		String palvelunTuottajanNimi = osva.getPalvelunTuottajanNimi();
		II value = of.createII();
		value.setRoot(palvelunTuottajanTunniste);
		palvelunTuottajanYksiloivaTunnisteComponent.getSection().setText(luoTextContent(palvelunTuottajanTunniste));
		palvelunTuottajanYksiloivaTunnisteComponent.getSection().getEntries().add(luoEntryObservation(value));
		palvelunTuottajanNimiComponent.getSection().setText(luoTextContent(palvelunTuottajanNimi));
		palvelunTuottajaComponent.getSection().getComponents().add(palvelunTuottajanYksiloivaTunnisteComponent);
		palvelunTuottajaComponent.getSection().getComponents().add(palvelunTuottajanNimiComponent);
		return palvelunTuottajaComponent;
	}

	/**
	 * Luo ostopalvelun tuottajan oikeus hakea asiakirjoja järjestäjän reksiteristä lohkon
	 * @return
	 */
	private POCDMT000040Component5 luoTuottajanOikeusHakeaAsiakirjoja() {
		POCDMT000040Component5 tuottajanOikeusHakeaAsiakirjojaComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.OSTOP_TUOTT_OIKEUS_HAKEA_PALVELUN_JARJ_REK);
		POCDMT000040Component5 rekisterinpitajaComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.REKISTERINPITAJA_HAKU);
		POCDMT000040Component5 rekisteriComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.REKISTERI_HAKU);
		
		String rekisterinpitaja = osva.getHakuRekisterinpitaja();
		String rekisteri = String.valueOf(osva.getHakuRekisteri());
		
		II rekisterinpitajaValue = of.createII();
		CV rekisteriValue = of.createCV();
		
		rekisterinpitajaValue.setRoot(rekisterinpitaja);

		fetchAttributes(String.format(POTILASASIAKIRJAN_REKISTERITUNNUS_TEMPLATE, osva.getHakuRekisteri()),rekisteriValue);
		rekisteriValue.setCode(rekisteri);
		
		rekisterinpitajaComponent.getSection().setText(luoTextContent(rekisterinpitaja));
		rekisterinpitajaComponent.getSection().getEntries().add(luoEntryObservation(rekisterinpitajaValue));
		rekisteriComponent.getSection().setText(luoTextContent(rekisteriValue.getDisplayName()));
		rekisteriComponent.getSection().getEntries().add(luoEntryObservation(rekisteriValue));
		
		tuottajanOikeusHakeaAsiakirjojaComponent.getSection().getComponents().add(rekisterinpitajaComponent);
		tuottajanOikeusHakeaAsiakirjojaComponent.getSection().getComponents().add(rekisteriComponent);

		if (!KantaCDAUtil.onkoNullTaiTyhja(osva.getHakuRekisterinTarkenne())) {
			POCDMT000040Component5 rekisterinTarkenneComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.REKISTERIN_TARKENNE_HAKU);
			POCDMT000040Component5 rekisterinTarkentimenNimiComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.REKISTERIN_TARKENTIMEN_NIMI_HAKU);
			
			String rekisterinTarkenne = osva.getHakuRekisterinTarkenne();
			String rekisterinTarkentimenNimi = osva.getHakuRekisterinTarkentimenNimi();
			
			II rekisterinTarkenneValue = of.createII();
			
			//Käytetäänkö Yritys- ja yhteisörekisteriä vai henkilötunnusta
			if (KantaCDAUtil.onkoYTunnus(rekisterinTarkenne)) {
				fetchAttributes(yritysJaYhteisorekisteriRoot, rekisterinTarkenneValue);
			} else {
				fetchAttributes(henkilotunnusRoot, rekisterinTarkenneValue);
			}
			rekisterinTarkenneValue.setExtension(rekisterinTarkenne);
	
			rekisterinTarkenneComponent.getSection().setText(luoTextContent(rekisterinTarkenne));
			rekisterinTarkenneComponent.getSection().getEntries().add(luoEntryObservation(rekisterinTarkenneValue));
			rekisterinTarkentimenNimiComponent.getSection().setText(luoTextContent(rekisterinTarkentimenNimi));
	
			tuottajanOikeusHakeaAsiakirjojaComponent.getSection().getComponents().add(rekisterinTarkenneComponent);
			tuottajanOikeusHakeaAsiakirjojaComponent.getSection().getComponents().add(rekisterinTarkentimenNimiComponent);
		}
		return tuottajanOikeusHakeaAsiakirjojaComponent;
	}

	/**
	 * Luo ostopalvelun tuottajan oikeus tallentaa asiakirjat palvelun järjestäjän rekisteriin lohkon
	 * @return
	 */
	private POCDMT000040Component5 luoTuottajanOikeusTallentaaAsiakirjat() {
		POCDMT000040Component5 tuottajanOikeusTallentaaAsiakirjojaComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.OSTOP_TUOTT_OIKEUS_TALLENTAA_PALV_JARJ_REK);
		POCDMT000040Component5 rekisterinpitajaComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.REKISTERINPITAJA_TALLENNUS);
		POCDMT000040Component5 rekisteriComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.REKISTERI_TALLENNUS);
		String rekisterinpitaja = osva.getTallennusRekisterinpitaja();
		String rekisteri = String.valueOf(osva.getTallennusRekisteri());
		II rekisterinpitajaValue = of.createII();
		CV rekisteriValue = of.createCV();
		
		rekisterinpitajaValue.setRoot(rekisterinpitaja);

		fetchAttributes(String.format(POTILASASIAKIRJAN_REKISTERITUNNUS_TEMPLATE, osva.getTallennusRekisteri()),rekisteriValue);
		rekisteriValue.setCode(rekisteri);
		
		rekisterinpitajaComponent.getSection().setText(luoTextContent(rekisterinpitaja));
		rekisterinpitajaComponent.getSection().getEntries().add(luoEntryObservation(rekisterinpitajaValue));
		rekisteriComponent.getSection().setText(luoTextContent(rekisteriValue.getDisplayName()));
		rekisteriComponent.getSection().getEntries().add(luoEntryObservation(rekisteriValue));
		
		tuottajanOikeusTallentaaAsiakirjojaComponent.getSection().getComponents().add(rekisterinpitajaComponent);
		tuottajanOikeusTallentaaAsiakirjojaComponent.getSection().getComponents().add(rekisteriComponent);

		if (!KantaCDAUtil.onkoNullTaiTyhja(osva.getTallennusRekisterinTarkenne())) {
			POCDMT000040Component5 rekisterinTarkenneComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.REKISTERIN_TARKENNE_TALLENNUS);
			POCDMT000040Component5 rekisterinTarkentimenNimiComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.REKISTERIN_TARKENTIMEN_NIMI_TALLENNUS);
	
			String rekisterinTarkenne = osva.getTallennusRekisterinTarkenne();
			String rekisterinTarkentimenNimi = osva.getTallennusRekisterinTarkentimenNimi();
	
			II rekisterinTarkenneValue = of.createII();
			
			//käytetäänkö Yritys- ja yhteisörekisteriä vai henkilötunnusta
			if (KantaCDAUtil.onkoYTunnus(rekisterinTarkenne)) {
				fetchAttributes(yritysJaYhteisorekisteriRoot, rekisterinTarkenneValue);
			} else {
				fetchAttributes(henkilotunnusRoot, rekisterinTarkenneValue);
			}
			rekisterinTarkenneValue.setExtension(rekisterinTarkenne);
	
			rekisterinTarkenneComponent.getSection().setText(luoTextContent(rekisterinTarkenne));
			rekisterinTarkenneComponent.getSection().getEntries().add(luoEntryObservation(rekisterinTarkenneValue));
			rekisterinTarkentimenNimiComponent.getSection().setText(luoTextContent(rekisterinTarkentimenNimi));
	
			tuottajanOikeusTallentaaAsiakirjojaComponent.getSection().getComponents().add(rekisterinTarkenneComponent);
			tuottajanOikeusTallentaaAsiakirjojaComponent.getSection().getComponents().add(rekisterinTarkentimenNimiComponent);
		}
		return tuottajanOikeusTallentaaAsiakirjojaComponent;
	}

	/**
	 * Luo potilas lohkon
	 * @return
	 */
	private POCDMT000040Component5 luoPotilas() {
		POCDMT000040Component5 potilasComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.POTILAS);
		POCDMT000040Component5 hetuComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.HENKILOTUNNUS);
		POCDMT000040Component5 nimetComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.SUKU_JA_ETUNIMET);
		POCDMT000040Component5 syntymaaikaComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.SYNTYMAAIKA);
		HenkilotiedotTO henkilotiedot = osva.getPotilas();
		II hetuValue = of.createII();

		fetchAttributes(henkilotunnusRoot, hetuValue);
		hetuValue.setExtension(henkilotiedot.getHetu());
		
		TS syntymaaikaValue = of.createTS();
		syntymaaikaValue.setValue(henkilotiedot.getSyntymaaika());
		
		hetuComponent.getSection().setText(luoTextContent(henkilotiedot.getHetu()));
		hetuComponent.getSection().getEntries().add(luoEntryObservation(hetuValue));
		nimetComponent.getSection().setText(luoTextContent(henkilotiedot.getNimi().getSukunimi()+ ", "+henkilotiedot.getNimi().getEtunimi()));
		nimetComponent.getSection().getEntries().add(luoEntryObservation(getNames(henkilotiedot.getNimi())));
		syntymaaikaComponent.getSection().setText(luoTextContent(KantaCDAUtil.hetuToBirthTime(henkilotiedot.getHetu(), "dd.MM.YYYY")));
		syntymaaikaComponent.getSection().getEntries().add(luoEntryObservation(syntymaaikaValue));
		
		potilasComponent.getSection().getComponents().add(hetuComponent);
		potilasComponent.getSection().getComponents().add(nimetComponent);
		potilasComponent.getSection().getComponents().add(syntymaaikaComponent);
		return potilasComponent;
	}

	/**
	 * Luo luovutettavat asiakirjat lohkon
	 * @return
	 */
	private POCDMT000040Component5 luoLuovutettavatAsiakirjat() {
		POCDMT000040Component5 luovutettavatAsiakirjatComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.LUOVUTETTAVAT_ASIAKIRJAT);
		POCDMT000040Component5 kaikkiAsiakirjatComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.KAIKKI_ASIAKIRJAT);
		
		BL value = of.createBL();
		value.setValue(osva.isKaikkiAsiakirjat());
		
		kaikkiAsiakirjatComponent.getSection().setText(luoTextContent(osva.isKaikkiAsiakirjat()?BL_KYLLA:BL_EI));
		kaikkiAsiakirjatComponent.getSection().getEntries().add(luoEntryObservation(value));
		luovutettavatAsiakirjatComponent.getSection().getComponents().add(kaikkiAsiakirjatComponent);
		if (!osva.isKaikkiAsiakirjat()) {
			if (null != osva.getLuovutettavatPalvelutapahtumat() && !osva.getLuovutettavatPalvelutapahtumat().isEmpty()) {
				for (String palvelutapahtuma : osva.getLuovutettavatPalvelutapahtumat()) {
					POCDMT000040Component5 luovutettavaPalvelutapahtumaComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.LUOVUTETTAVAT_PALVELUTAPAHTUMAT);
					II palvelutapahtumaValue = of.createII();
					palvelutapahtumaValue.setRoot(palvelutapahtuma);
					luovutettavaPalvelutapahtumaComponent.getSection().setText(luoTextContent(palvelutapahtuma));
					luovutettavaPalvelutapahtumaComponent.getSection().getEntries().add(luoEntryObservation(palvelutapahtumaValue));
					luovutettavatAsiakirjatComponent.getSection().getComponents().add(luovutettavaPalvelutapahtumaComponent);
				}
			} else {
				POCDMT000040Component5 luovutettavanAineistoinAikavaliComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.LUOVUTETTAVAN_AINEISTON_AIKAVALI);
				IVLTS aikavaliValue = of.createIVLTS();
				
				if (null != osva.getLuovutettavanAineistonAlku()) {
					aikavaliValue.setLow(of.createIVXBTS());
					aikavaliValue.getLow().setValue(getShortDateFormat().format(osva.getLuovutettavanAineistonAlku()));
				}
				if (null != osva.getLuovutettavanAineistonLoppu()) {
					aikavaliValue.setHigh(of.createIVXBTS());
					aikavaliValue.getHigh().setValue(getShortDateFormat().format(osva.getLuovutettavanAineistonLoppu()));
				}
				luovutettavanAineistoinAikavaliComponent.getSection().setText(luoTextContent(muotoileAikavali(osva.getLuovutettavanAineistonAlku(), osva.getLuovutettavanAineistonLoppu())));
				luovutettavatAsiakirjatComponent.getSection().getComponents().add(luovutettavanAineistoinAikavaliComponent);
			}
		}
		return luovutettavatAsiakirjatComponent;
	}

	/**
	 * Luo asiakirjan tallentaja lohkon
	 * @return
	 */
	private POCDMT000040Component5 luoAsiakirjanTallentaja() {
		POCDMT000040Component5 asiakirjanTallentajaComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.ASIAKIRJAN_TALLENTAJA);
		POCDMT000040Component5 ammattihenkilonTunnisteComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.AMMATTIHENKILON_TUNNISTE);
		POCDMT000040Component5 ammattihenkilonNimiComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.AMMATTIHENKILON_NIMI);
		POCDMT000040Component5 ammattihenkilonPalveluyksikkoComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.AMMATTIHENKILON_PALVELUYKSIKKO);
		POCDMT000040Component5 asiakirjanTekemisenAjankohtaComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.ASIAKIRJAN_TEKEMISEN_AJANKOHTA);
		
		HenkilotiedotTO henkilotiedot = osva.getAsiakirjanTallentaja();
		String ammattihenkilonPalveluyksikko = osva.getAmmattihenkilonPalveluyksikko();
		String ammattihenkilonPalveluyksikkoNimi = osva.getAmmattihenkilonPalveluyksikonNimi();
		
		II ammattihenkilonTunnisteValue = of.createII();
		II ammattihenkilonPalveluyksikkoValue = of.createII();
		TS tekemisenAjankohtaValue = of.createTS();
		StrucDocText tunnisteText;
		
		if (tekninenProsessi) {
			fetchAttributes("tekninenProsessi", ammattihenkilonTunnisteValue);
			tunnisteText  = luoTextContent(ammattihenkilonTunnisteValue.getRoot());
		} else {
			if (!onkoNullTaiTyhja(osva.getAmmattihenkilonKatsoTunnus())) {
				fetchAttributes("katsotunnus", ammattihenkilonTunnisteValue);
				ammattihenkilonTunnisteValue.setExtension(osva.getAmmattihenkilonKatsoTunnus());
				tunnisteText = luoTextContent(osva.getAmmattihenkilonKatsoTunnus());
			} else {
				fetchAttributes("henkilotunnus", ammattihenkilonTunnisteValue);
				ammattihenkilonTunnisteValue.setExtension(henkilotiedot.getHetu());
				tunnisteText = luoTextContent(henkilotiedot.getHetu());
			}
		}
		ammattihenkilonPalveluyksikkoValue.setRoot(ammattihenkilonPalveluyksikko);
		tekemisenAjankohtaValue.setValue(getDateFormat().format(osva.getAikaleima()));
		
		ammattihenkilonTunnisteComponent.getSection().setText(tunnisteText);
		ammattihenkilonTunnisteComponent.getSection().getEntries().add(luoEntryObservation(ammattihenkilonTunnisteValue));
		ammattihenkilonNimiComponent.getSection().setText(luoTextContent(henkilotiedot.getNimi().getSukunimi()+ ", "+henkilotiedot.getNimi().getEtunimi()));
		ammattihenkilonNimiComponent.getSection().getEntries().add(luoEntryObservation(getNames(henkilotiedot.getNimi())));
		ammattihenkilonPalveluyksikkoComponent.getSection().setText(luoTextContent(ammattihenkilonPalveluyksikkoNimi));
		ammattihenkilonPalveluyksikkoComponent.getSection().getEntries().add(luoEntryObservation(ammattihenkilonPalveluyksikkoValue));
		asiakirjanTekemisenAjankohtaComponent.getSection().setText(luoTextContent(getTekemisenAjankohtaDateFormat().format(osva.getAikaleima())));
		asiakirjanTekemisenAjankohtaComponent.getSection().getEntries().add(luoEntryObservation(tekemisenAjankohtaValue));
		
		asiakirjanTallentajaComponent.getSection().getComponents().add(ammattihenkilonTunnisteComponent);
		asiakirjanTallentajaComponent.getSection().getComponents().add(ammattihenkilonNimiComponent);
		asiakirjanTallentajaComponent.getSection().getComponents().add(ammattihenkilonPalveluyksikkoComponent);
		asiakirjanTallentajaComponent.getSection().getComponents().add(asiakirjanTekemisenAjankohtaComponent);
		return asiakirjanTallentajaComponent;
	}

	/**
	 * Luo lomakkeen metatiedot lohkon
	 * Deprekoitu koska metatiedot lohkoa ei enää vaadita asiakirjalle
	 * @return
	 */
	@Deprecated
	private POCDMT000040Component5 luoLomakkeenMetatiedot(String templateId) {
		POCDMT000040Component5 metatiedotComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.LOMAKKEEN_METATIEDOT);
		POCDMT000040Component5 templateIdComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.TEMPLATEID);
		POCDMT000040Component5 maarittelyversioComponent = createComponent(KantaCDAConstants.OstopalvelunValtuutus.MAARITTELYVERSIO);
		
		//String templateId = osva.getTemplateIds().iterator().next();
		II value = of.createII();
		value.setRoot(templateId);
		
		templateIdComponent.getSection().setText(luoTextContent(templateId));
		templateIdComponent.getSection().getEntries().add(luoEntryObservation(value));
		maarittelyversioComponent.getSection().setText(luoTextContent(fetchProperty(maarittelyVersio)));
		metatiedotComponent.getSection().getComponents().add(templateIdComponent);
		metatiedotComponent.getSection().getComponents().add(maarittelyversioComponent);
		return metatiedotComponent;
	}
	
	/**
	 * Luo potilaan tiedot lohko mitätåätöitäessä
	 * @return
	 */
	private POCDMT000040Subject luoMitatointiPotilas() {
		POCDMT000040Subject subject = of.createPOCDMT000040Subject();
		subject.setTypeCode(ParticipationTargetSubject.SBJ);
		subject.setRelatedSubject(of.createPOCDMT000040RelatedSubject());
		subject.getRelatedSubject().setClassCode(XDocumentSubject.PAT);
		subject.getRelatedSubject().setCode(of.createCE());
		subject.getRelatedSubject().getCode().setCode(osva.getPotilas().getHetu());
		//TODO: codelle hetu codeSystem 1.2.246.21 haettava jostain
		subject.getRelatedSubject().getCode().setCodeSystem("1.2.246.21");
		subject.getRelatedSubject().setSubject(of.createPOCDMT000040SubjectPerson());
		subject.getRelatedSubject().getSubject().getClassCodes().add("PSN");
		subject.getRelatedSubject().getSubject().getNames().add(getNames(osva.getPotilas().getNimi()));
		return subject;
	}

	/**
	 * Luo merkinnän tekijä tapahtuma-aika jap alveluykiskkö lohko
	 * Tyhjänä vv-järjestelmässä
	 * @return
	 */
	private POCDMT000040Author luoMitatointiAuthor() {
		POCDMT000040Author author = of.createPOCDMT000040Author();
		author.getNullFlavors().add(KantaCDAConstants.NullFlavor.NA.getCode());
		author.setTime(of.createTS());
		author.getTime().getNullFlavors().add(KantaCDAConstants.NullFlavor.NA.getCode());
		author.setAssignedAuthor(of.createPOCDMT000040AssignedAuthor());
		II assignedAuthorId = of.createII();
		assignedAuthorId.getNullFlavors().add(KantaCDAConstants.NullFlavor.NA.getCode());
		author.getAssignedAuthor().getIds().add(assignedAuthorId);
		return author;
	}

	/**
	 * Luo hoitoprosessin vaihe lohko
	 * @return
	 */
	private POCDMT000040Component5 luoMitatointiHoitoprosessinVaihe() {
		POCDMT000040Component5 hoitoprosessinvaiheComponent = createComponent(MAARITTAMATON_HOITOPROSESSIN_VAIHE);
		POCDMT000040Component5 muuMerkintaComponent= createComponent(MUU_MERKINTA);
		StrucDocText text = of.createStrucDocText();
		StrucDocParagraph paragraph1 = of.createStrucDocParagraph();
		StrucDocParagraph paragraph2 = of.createStrucDocParagraph();
		//TODO: Mitätöinnin syy pitäisi varmaan olla annettavissa
		paragraph1.getContent().add("Asiakirja on tyhjä, koska se on mitätöity");
		paragraph2.getContent().add("Mitätöinnin syy: Valinta on poistettu");
		text.getContent().add(of.createStrucDocTextParagraph(paragraph1));
		text.getContent().add(of.createStrucDocTextParagraph(paragraph2));
		muuMerkintaComponent.getSection().setText(text);
		
		hoitoprosessinvaiheComponent.getSection().getComponents().add(muuMerkintaComponent);
		return hoitoprosessinvaiheComponent;
	}

	/**
	 * Apumetodi joka luo SrtucDocText elementin jonka content elementttiin annettu teksti sijoitettaan
	 *
	 * <pre>
	 * {@code
	 * 	<text>
	 * 		<content>[teksti]</content>
	 * 	</text>
	 * }
	 * </pre>
	 * @param teksti
	 * @return text elementti
	 */
	private StrucDocText luoTextContent(String teksti) {
		StrucDocText text = of.createStrucDocText();
		text.getContent().add(teksti);
		return text;
	}

	/**
	 * Apumetodi joka luo POCDMT000040Component5 componentin sekä sectionin jolle code ja title elementit
	 * <pre>
	 * {@code
	 * <component>
	 * 	<section>
	 * 		<code code="[key.code]" codeSystem="[key.codeSystem]" codeSystemName="[key.codeSystemName]" displayName="[key.displayName]"/>
	 * 		<title>[key.displayName]</title>
	 * 	</section>
	 * </component>
	 * }
	 * </pre>
	 * @return
	 */
	private POCDMT000040Entry luoEntryObservation(ANY value) {
		POCDMT000040Entry entry = of.createPOCDMT000040Entry();
		entry.setObservation(luoObservation(value));
		return entry;
	}

	private POCDMT000040Observation luoObservation(ANY value) {
		POCDMT000040Observation observation = of.createPOCDMT000040Observation();
		observation.getClassCodes().add(classCodeCOND);
		observation.setMoodCode(XActMoodDocumentObservation.EVN);
		//II id = of.createII();
		//id.setRoot(getNextId(osva));
		//observation.getIds().add(id);
		CD nullFlavorCode = of.createCD();
		nullFlavorCode.getNullFlavors().add(KantaCDAConstants.NullFlavor.NA.getCode());
		observation.setCode(nullFlavorCode);
		if (value != null) {
			observation.getValues().add(value);
		}
		return observation;
	}
	
	private String muotoileAikavali(Date alku, Date loppu) {
		if (null == alku && null == loppu) {
			return "";
		}
		StringBuilder aikavali = new StringBuilder();
		if (null != alku) {
			aikavali.append(getTodayDateFormat().format(alku));
			aikavali.append(" ");
		}
		aikavali.append("-");
		if (null != loppu) {
			aikavali.append(" ");
			aikavali.append(getTodayDateFormat().format(loppu));
		}
		return aikavali.toString();
		
	}

	private final SimpleDateFormat getTekemisenAjankohtaDateFormat() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone(Kasaaja.TIME_ZONE));
        return sdf;
	}
}
