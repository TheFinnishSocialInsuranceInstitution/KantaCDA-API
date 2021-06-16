package fi.kela.kanta.cda;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.configuration.ConfigurationException;
import org.hl7.v3.ANY;
import org.hl7.v3.BL;
import org.hl7.v3.CV;
import org.hl7.v3.II;
import org.hl7.v3.IVLTS;
import org.hl7.v3.PN;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component5;
import org.hl7.v3.POCDMT000040CustodianOrganization;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040Organization;
import org.hl7.v3.POCDMT000040RecordTarget;
import org.hl7.v3.POCDMT000040Section;
import org.hl7.v3.StrucDocText;

import fi.kela.kanta.exceptions.PurkuException;
import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.OstopalvelunvaltuutusTO;
import fi.kela.kanta.util.JaxbUtil;
import fi.kela.kanta.util.KantaCDAUtil;

public class OstopalvelunvaltuutusPurkaja extends Purkaja {

	private POCDMT000040ClinicalDocument clinicalDocument;
	
	public OstopalvelunvaltuutusPurkaja() throws ConfigurationException {
		super();
	}

	@Override
	protected String getCodeSystem() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public OstopalvelunvaltuutusTO puraOstopalvelunvaltuutus(String cda) throws PurkuException {
		OstopalvelunvaltuutusTO ostopalvelunvaltuutus = new OstopalvelunvaltuutusTO();
		try {
			clinicalDocument = JaxbUtil.getInstance().unmarshaller(cda);
			puraLeimakentat(clinicalDocument, ostopalvelunvaltuutus);
			puraPotilas(clinicalDocument, ostopalvelunvaltuutus);
			puraAuthor(clinicalDocument, ostopalvelunvaltuutus);
			puraCustodian(clinicalDocument, ostopalvelunvaltuutus);
			puraComponentOf(clinicalDocument, ostopalvelunvaltuutus);
			puraOstopalvelunTiedot(clinicalDocument, ostopalvelunvaltuutus);
		
		} catch (JAXBException e) {
			throw new PurkuException(e);
		}
		return ostopalvelunvaltuutus;

	}

	protected void puraCustodian(POCDMT000040ClinicalDocument clinicalDocument,
			OstopalvelunvaltuutusTO ostopalvelunvaltuutus) {
		POCDMT000040CustodianOrganization representedCustodianOrganization = clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization();
		ostopalvelunvaltuutus.setAsiakirjanRekisterinpitaja(representedCustodianOrganization.getIds().get(0).getRoot());
		ostopalvelunvaltuutus.setAsiakirjanRekisterinpitajaNimi(puraContent(representedCustodianOrganization.getName()));
	}

	protected void puraComponentOf(POCDMT000040ClinicalDocument clinicalDocument,
			OstopalvelunvaltuutusTO ostopalvelunvaltuutus) {
		POCDMT000040Organization representedOrganization = clinicalDocument.getComponentOf().getEncompassingEncounter().getResponsibleParty().getAssignedEntity().getRepresentedOrganization();
		ostopalvelunvaltuutus.setPalvelunTuottaja(representedOrganization.getIds().get(0).getRoot());
		ostopalvelunvaltuutus.setPalvelunTuottajanNimi(puraContent(representedOrganization.getNames().get(0)));
	}

	protected void puraOstopalvelunTiedot(POCDMT000040ClinicalDocument clinicalDocument,
			OstopalvelunvaltuutusTO ostopalvelunvaltuutus) throws PurkuException {
		for (POCDMT000040Component5 component : clinicalDocument.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents()) {
			if (null != component.getSection() && null != component.getSection().getCode() && null != component.getSection().getCode().getCode()) {
				String sectionCodeCode = component.getSection().getCode().getCode();
				if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.OSTOPALVELUN_TYYPPI)) {
					puraOstopalvelunTyyppi(component.getSection(), ostopalvelunvaltuutus);
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.OSTOPALVELUN_VALTUUTUKSEN_VOIMASSAOLO)) {
					puraOstopalvelunvaltuutuksenVoimassaolo(component.getSection(), ostopalvelunvaltuutus);
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_JARJESTAJA)) {
					puraOstopalvelunJarjestaja(component.getSection(), ostopalvelunvaltuutus);
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_TUOTTAJA)) {
					puraOstopalvelunTuottaja(component.getSection(), ostopalvelunvaltuutus);
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.OSTOP_TUOTT_OIKEUS_HAKEA_PALVELUN_JARJ_REK)) {
					puraOstopalvelunTuottajanHakuOikeus(component.getSection(), ostopalvelunvaltuutus);
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.OSTOP_TUOTT_OIKEUS_TALLENTAA_PALV_JARJ_REK)) {
					puraOstopalvelunTuottajanTallennusOikeus(component.getSection(), ostopalvelunvaltuutus);
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.LUOVUTETTAVAT_ASIAKIRJAT)) {
					puraOstopalvelunLuovuttettavatAsiakirjat(component.getSection(), ostopalvelunvaltuutus);
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.ASIAKIRJAN_TALLENTAJA)) {
					puraAsiakirjanTallentaja(component.getSection(), ostopalvelunvaltuutus);
				}
			}
		}
		
	}

	protected void puraOstopalvelunTyyppi(POCDMT000040Section section, OstopalvelunvaltuutusTO ostopalvelunvaltuutus) {
		for(POCDMT000040Component5 component : section.getComponents()) {
			String sectionCodeCode = haeSectionCodeCode(component); 
			if (!KantaCDAUtil.onkoNullTaiTyhja(sectionCodeCode) 
					&& sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.OSTOPALVELUN_TYYPPI_KOODI)) {
				ostopalvelunvaltuutus.setOstopalvelunTyyppi(Integer.valueOf(puraValue(haeEntryObservationValue(component.getSection().getEntries().get(0)))));
			}
		}
		
	}

	protected void puraOstopalvelunvaltuutuksenVoimassaolo(POCDMT000040Section section,
			OstopalvelunvaltuutusTO ostopalvelunvaltuutus) throws PurkuException {
		for(POCDMT000040Component5 component : section.getComponents()) {
			String sectionCodeCode = haeSectionCodeCode(component);
			if (!KantaCDAUtil.onkoNullTaiTyhja(sectionCodeCode)
					&& sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.ASIAKIRJA_VOIMASSA)){
				PurettuAikavali aikavali = puraAikaVali(haeEntryObservationValue(component.getSection().getEntries().get(0)));
				if (null != aikavali.low) {
					ostopalvelunvaltuutus.setValtuutuksenVoimassaoloAlku(aikavali.low);
				}
				if (null != aikavali.high) {
					ostopalvelunvaltuutus.setValtuutuksenVoimassaoloLoppu(aikavali.high);
				}			
			}
		}
		
	}

	protected void puraOstopalvelunJarjestaja(POCDMT000040Section section,
			OstopalvelunvaltuutusTO ostopalvelunvaltuutus) {
		for(POCDMT000040Component5 component : section.getComponents()) {
			String sectionCodeCode = haeSectionCodeCode(component); 
			if (!KantaCDAUtil.onkoNullTaiTyhja(sectionCodeCode)) {
				if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_JARJESTAJA_YKSILOINTITUNNUS)) {
					ostopalvelunvaltuutus.setPalvelunJarjestaja(puraValue(haeEntryObservationValue(component.getSection().getEntries().get(0))));
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_JARJESTAJAN_NIMI)) {
					 String palvelunjarjestajanNimi = puraStrucDocText(component.getSection().getText());
					 if (null != palvelunjarjestajanNimi) {
						 ostopalvelunvaltuutus.setPalvelunJarjestajaNimi(palvelunjarjestajanNimi);
					 }
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_JARJESTAJA_PALVELUYKSIKKO)) {
					ostopalvelunvaltuutus.setPalvelunJarjestajanPalveluyksikko(puraValue(haeEntryObservationValue(component.getSection().getEntries().get(0))));
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_JARJESTAJA_PALVELUYKSIKON_NIMI)) {
					 String palvelunjarjestajanPalveluyksikonNimi = puraStrucDocText(component.getSection().getText());
					 if (null != palvelunjarjestajanPalveluyksikonNimi) {
						 ostopalvelunvaltuutus.setPalvelunJarjestajanPalveluyksikonNimi(palvelunjarjestajanPalveluyksikonNimi);
					 }
				}
			}
		}
	}

	protected void puraOstopalvelunTuottaja(POCDMT000040Section section, OstopalvelunvaltuutusTO ostopalvelunvaltuutus) {
		for(POCDMT000040Component5 component : section.getComponents()) {
			String sectionCodeCode = haeSectionCodeCode(component); 
			if (!KantaCDAUtil.onkoNullTaiTyhja(sectionCodeCode)) {
				if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_TUOTTAJAN_YKSILOINTITUNNUS)) {
					ostopalvelunvaltuutus.setPalvelunTuottaja(puraValue(haeEntryObservationValue(component.getSection().getEntries().get(0))));
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.PALVELUN_TUOTTAJAN_NIMI)) {
					 String palvelunTuottajanNimi = puraStrucDocText(component.getSection().getText());
					 if (null != palvelunTuottajanNimi) {
						 ostopalvelunvaltuutus.setPalvelunTuottajanNimi(palvelunTuottajanNimi);
					 }
				}
			}
		}
	}

	protected void puraOstopalvelunTuottajanHakuOikeus(POCDMT000040Section section,
			OstopalvelunvaltuutusTO ostopalvelunvaltuutus) {
		for(POCDMT000040Component5 component : section.getComponents()) {
			String sectionCodeCode = haeSectionCodeCode(component); 
			if (!KantaCDAUtil.onkoNullTaiTyhja(sectionCodeCode)) {
				if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.REKISTERINPITAJA_HAKU)) {
					ostopalvelunvaltuutus.setHakuRekisterinpitaja(puraValue(haeEntryObservationValue(component.getSection().getEntries().get(0))));
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.REKISTERI_HAKU)) {
					ostopalvelunvaltuutus.setHakuRekisteri(Integer.valueOf(puraValue(haeEntryObservationValue(component.getSection().getEntries().get(0)))));
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.REKISTERIN_TARKENNE_HAKU)) {
					//haetaan vain extension
					ostopalvelunvaltuutus.setHakuRekisterinTarkenne(puraIIValue((II)haeEntryObservationValue(component.getSection().getEntries().get(0)),false,true));
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.REKISTERIN_TARKENTIMEN_NIMI_HAKU)) {
					 String rekisterinTarkentimenNimi = puraStrucDocText(component.getSection().getText());
					 if (null != rekisterinTarkentimenNimi) {
						 ostopalvelunvaltuutus.setHakuRekisterinTarkentimenNimi(rekisterinTarkentimenNimi);
					 }
				}
			}
		}
	}

	protected void puraOstopalvelunTuottajanTallennusOikeus(POCDMT000040Section section,
			OstopalvelunvaltuutusTO ostopalvelunvaltuutus) {
		for(POCDMT000040Component5 component : section.getComponents()) {
			String sectionCodeCode = haeSectionCodeCode(component); 
			if (!KantaCDAUtil.onkoNullTaiTyhja(sectionCodeCode)) {
				if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.REKISTERINPITAJA_TALLENNUS)) {
					ostopalvelunvaltuutus.setTallennusRekisterinpitaja(puraValue(haeEntryObservationValue(component.getSection().getEntries().get(0))));
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.REKISTERI_TALLENNUS)) {
					ostopalvelunvaltuutus.setTallennusRekisteri(Integer.valueOf(puraValue(haeEntryObservationValue(component.getSection().getEntries().get(0)))));
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.REKISTERIN_TARKENNE_TALLENNUS)) {
					//haetaan vain extension
					ostopalvelunvaltuutus.setTallennusRekisterinTarkenne(puraIIValue((II)haeEntryObservationValue(component.getSection().getEntries().get(0)),false, true));
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.REKISTERIN_TARKENTIMEN_NIMI_TALLENNUS)) {
					 String rekisterinTarkentimenNimi = puraStrucDocText(component.getSection().getText());
					 if (null != rekisterinTarkentimenNimi) {
						 ostopalvelunvaltuutus.setTallennusRekisterinTarkentimenNimi(rekisterinTarkentimenNimi);
					 }
				}
			}
		}
	}

	protected void puraOstopalvelunLuovuttettavatAsiakirjat(POCDMT000040Section section,
			OstopalvelunvaltuutusTO ostopalvelunvaltuutus) throws PurkuException {
		for(POCDMT000040Component5 component : section.getComponents()) {
			String sectionCodeCode = haeSectionCodeCode(component); 
			if (!KantaCDAUtil.onkoNullTaiTyhja(sectionCodeCode)) {
				if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.KAIKKI_ASIAKIRJAT)) {
					ostopalvelunvaltuutus.setKaikkiAsiakirjat(puraBLValue(haeEntryObservationValue(component.getSection().getEntries().get(0))));
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.LUOVUTETTAVAN_AINEISTON_AIKAVALI)) {
					PurettuAikavali aikavali = puraAikaVali(haeEntryObservationValue(component.getSection().getEntries().get(0)));
					if (null != aikavali.low) {
						ostopalvelunvaltuutus.setLuovutettavanAineistonAlku(aikavali.low);
					}
					if (null != aikavali.high) {
						ostopalvelunvaltuutus.setLuovutettavanAineistonLoppu(aikavali.high);
					}
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.LUOVUTETTAVAT_PALVELUTAPAHTUMAT)) {
					ostopalvelunvaltuutus.getLuovutettavatPalvelutapahtumat().add(puraValue(haeEntryObservationValue(component.getSection().getEntries().get(0)))); 
				}
			}
		}
	}

	protected void puraAsiakirjanTallentaja(POCDMT000040Section section, OstopalvelunvaltuutusTO ostopalvelunvaltuutus) {
		String tallentajaTunniste = null;
		KokoNimiTO tallentajaNimi = null;
		for(POCDMT000040Component5 component : section.getComponents()) {
			String sectionCodeCode = haeSectionCodeCode(component);
			if (!KantaCDAUtil.onkoNullTaiTyhja(sectionCodeCode)) {
				if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.AMMATTIHENKILON_TUNNISTE)) {
					tallentajaTunniste = puraIIValue((II)haeEntryObservationValue(component.getSection().getEntries().get(0)), false, true);
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.AMMATTIHENKILON_NIMI)) {
					tallentajaNimi = puraPNValue(haeEntryObservationValue(component.getSection().getEntries().get(0)));
				} else if (sectionCodeCode.equals(KantaCDAConstants.OstopalvelunValtuutus.AMMATTIHENKILON_PALVELUYKSIKKO)) {
					ostopalvelunvaltuutus.setAmmattihenkilonPalveluyksikko(puraValue(haeEntryObservationValue(component.getSection().getEntries().get(0))));
					String ammattihenkilonPalveluyksikonNimi = puraStrucDocText(component.getSection().getText());
					 if (null != ammattihenkilonPalveluyksikonNimi) {
						 ostopalvelunvaltuutus.setAmmattihenkilonPalveluyksikonNimi(ammattihenkilonPalveluyksikonNimi);
					 }
				}
			}
		}
		if (null != tallentajaTunniste && null != tallentajaNimi) {
			ostopalvelunvaltuutus.setAsiakirjanTallentaja(new HenkilotiedotTO(tallentajaNimi, tallentajaTunniste));
		}
		
	}

	protected void puraAuthor(POCDMT000040ClinicalDocument clinicalDocument,
			OstopalvelunvaltuutusTO ostopalvelunvaltuutus) {
		//TODO
		
	}

	/**
     * Purkaa potilaan henkilotiedot clinicalDocumentsta
     *
     * @param clinicaldocument
     *            POCDMT000040ClinicalDocument josta tiedot haetaan
     * @param ostopalvelunvaltuutus
     *            OstopalvelunvaltuutusTO johon tiedot sijoitetaan
     */
    protected void puraPotilas(POCDMT000040ClinicalDocument clinicaldocument, OstopalvelunvaltuutusTO ostopalvelunvaltuutus) {
        POCDMT000040RecordTarget recordTarget = clinicaldocument.getRecordTargets().get(0);
        HenkilotiedotTO henkilotiedot;
        if ( !recordTarget.getPatientRole().getIds().isEmpty()
                && recordTarget.getPatientRole().getIds().get(0).getNullFlavors().isEmpty() ) {
            henkilotiedot = new HenkilotiedotTO(puraKokoNimi(recordTarget.getPatientRole().getPatient().getNames()),
                    recordTarget.getPatientRole().getIds().get(0).getExtension());
        }
        else {
            henkilotiedot = new HenkilotiedotTO(puraKokoNimi(recordTarget.getPatientRole().getPatient().getNames()),
                    recordTarget.getPatientRole().getPatient().getBirthTime().getValue(), Integer.parseInt(
                            recordTarget.getPatientRole().getPatient().getAdministrativeGenderCode().getCode()));
        }
        ostopalvelunvaltuutus.setPotilas(henkilotiedot);
    }

	/**
	 * apumetodi IVL_TS aikavälin purkamiseen.
	 * annetun valuen low elementin arvo sijoitetaan annettuun low parametriin ja high 
	 * elementin arvo high parametriin. jos low tai high saadaan asetettua
	 * palauttaa metodi true, muuten false.
	 *  
	 * @param value ANY elementti josta aikaväli koetetaan purkaa
	 * @return PurettuAikavali johon puretut high ja low elementtien arvot sijoitettu
	 * 
	 * @throws PurkuException
	 */
    protected PurettuAikavali puraAikaVali(ANY value) throws PurkuException {
		PurettuAikavali aikavali = new PurettuAikavali();
		if (null != value && value instanceof IVLTS) {
			IVLTS ivlTs = (IVLTS)value;
			if (null != ivlTs.getHigh()) {
				aikavali.high = puraAika(ivlTs.getHigh().getValue());
			}
			if (null != ivlTs.getLow()) {
				aikavali.low = puraAika(ivlTs.getLow().getValue());
			}
		}
		return aikavali;
	}
    protected class PurettuAikavali {
    	public Date high;
    	public Date low;
    }
	
    protected boolean puraBLValue(ANY value) {
		if (null != value && value instanceof BL) {
			return ((BL)value).isValue();
		}
		return false;
	}
	
    protected KokoNimiTO puraPNValue(ANY value) {
		if (null != value && value instanceof PN) {
			List<PN> nameList = new ArrayList<PN>();
			nameList.add((PN)value);
			return puraKokoNimi(nameList);
		}
		return null;
	}
	
    protected String puraIIValue(II value, boolean root, boolean extension) {
    	StringBuilder sb = new StringBuilder();
    	if (null != value) {
    		if (root) {
    			sb.append(value.getRoot());
    		}
    		if (extension) {
    			if (sb.length()>0) {
    				sb.append(".");
    			}
    			sb.append(value.getExtension());
    		}
    	}
    	return sb.toString();
    }
    
    protected String puraValue(ANY value) {
		if (null != value) {
			if (value instanceof CV) {
				return ((CV)value).getCode();
			} else if (value instanceof II) {
				return puraIIValue((II)value, true, false);
			}
		}
		return null;
	}
	
    protected String puraStrucDocText(StrucDocText text) {
		if (null != text && null != text.getContent() && !text.getContent().isEmpty()) {
			for (Serializable ser : text.getContent()) {
				StringBuilder sb = new StringBuilder();
				if (ser instanceof String) {
					sb.append((String)ser);
				}
				return sb.toString();
			}
		}
		return null;
	}

    protected ANY haeEntryObservationValue(POCDMT000040Entry entry) {
		if (null != entry
				&& null != entry.getObservation()
				&& null != entry.getObservation().getValues()
				&& !entry.getObservation().getValues().isEmpty()) {
			return entry.getObservation().getValues().get(0);
		}
		return null;
	}

    protected String haeSectionCodeCode(POCDMT000040Component5 component) {
		if (null != component 
				&& null != component.getSection() 
				&& null != component.getSection().getCode()
				&& !KantaCDAUtil.onkoNullTaiTyhja(component.getSection().getCode().getCode())) {
			return component.getSection().getCode().getCode();
		}
		return null;
	}
}
