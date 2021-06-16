package fi.kela.kanta.cda;

import static fi.kela.kanta.cda.KantaCDAConstants.Laakityslista.ANNOKSET_CODE;
import static fi.kela.kanta.cda.KantaCDAConstants.Laakityslista.ANNOSJAKSON_PITUUS_CODE;
import static fi.kela.kanta.cda.KantaCDAConstants.Laakityslista.ANNOSTELUKAUDEN_KESTO_CODE;
import static fi.kela.kanta.cda.KantaCDAConstants.Laakityslista.ANNOSTELUKAUSI_CODE;
import static fi.kela.kanta.cda.KantaCDAConstants.Laakityslista.ANNOSTUKSEN_LISATIEDOT_CODE;
import static fi.kela.kanta.cda.KantaCDAConstants.Laakityslista.ANNOSTUS_TARVITTAESSA_CODE;
import static fi.kela.kanta.cda.KantaCDAConstants.Laakityslista.ANNOS_TARVITTAESSA_CODE;
import static fi.kela.kanta.cda.KantaCDAConstants.Laakityslista.KAYTTOOHJEEN_LISATIETO_CODE;
import static fi.kela.kanta.cda.KantaCDAConstants.Laakityslista.ANNOSAIKA_CODE;
import static fi.kela.kanta.cda.KantaCDAConstants.Laakityslista.FYS_ANNOKSET_CODE;
import static fi.kela.kanta.cda.KantaCDAConstants.Laakityslista.ANNOSAJANKOHTA_CODE;
import static fi.kela.kanta.cda.KantaCDAConstants.Laakityslista.ANNOSJAKSON_PAIVA_CODE;
import static fi.kela.kanta.cda.KantaCDAConstants.Laakityslista.LAAKE_TAUOLLA_CODE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.hl7.v3.ANY;
import org.hl7.v3.BL;
import org.hl7.v3.CD;
import org.hl7.v3.CE;
import org.hl7.v3.CR;
import org.hl7.v3.II;
import org.hl7.v3.INT;
import org.hl7.v3.IVLPQ;
import org.hl7.v3.IVLTS;
import org.hl7.v3.MO;
import org.hl7.v3.POCDMT000040Author;
import org.hl7.v3.POCDMT000040Authorization;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component4;
import org.hl7.v3.POCDMT000040Consumable;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040EntryRelationship;
import org.hl7.v3.POCDMT000040HealthCareFacility;
import org.hl7.v3.POCDMT000040LabeledDrug;
import org.hl7.v3.POCDMT000040Observation;
import org.hl7.v3.POCDMT000040Participant2;
import org.hl7.v3.POCDMT000040RecordTarget;
import org.hl7.v3.POCDMT000040SubstanceAdministration;
import org.hl7.v3.POCDMT000040Supply;
import org.hl7.v3.PQ;
import org.hl7.v3.ST;

import fi.kela.kanta.cda.Kasaaja.MaaraajanRooli;
import fi.kela.kanta.exceptions.PurkuException;
import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.AnnosTO;
import fi.kela.kanta.to.AnnosjaksonPituusTO;
import fi.kela.kanta.to.AnnostelukaudenPituusTO;
import fi.kela.kanta.to.ApteekissaValmistettavaLaakeTO;
import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.JaksonPituusTO;
import fi.kela.kanta.to.LaakemaarayksenKorjausTO;
import fi.kela.kanta.to.LaakemaarayksenMitatointiTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.KoodiTO;
import fi.kela.kanta.to.MuuAinesosaTO;
import fi.kela.kanta.to.OrganisaatioTO;
import fi.kela.kanta.to.VaikuttavaAineTO;
import fi.kela.kanta.to.VaikuttavaAinesosaTO;
import fi.kela.kanta.to.ValmisteTO;
import fi.kela.kanta.to.ValmisteenKayttotapaTO;
import fi.kela.kanta.to.ValmisteenYksilointitiedotTO;
import fi.kela.kanta.util.JaxbUtil;
import fi.kela.kanta.util.KantaCDAUtil;

public class ReseptiPurkaja extends Purkaja {

	private static final String yksilointitunnusRoot = "1.2.246.537.25";
	private static final String rekisterointinumeroRoot = "1.2.246.537.26";
	private static final String tekninenCDAR2rakennekoodisto = "1.2.246.537.6.12.999.2003";
	private static final String virkanimikeCode = "1.2";
	private static final String oppiarvoCode = "1.3";
	private static final String participantRoleOWN = "OWN";
	private static final String participantRoleEMP = "EMP";
	private static final String participantRolePAYOR = "PAYOR";
	private static final String reseptinTyyppiPakkaus = "1";
	private static final String reseptinTyyppiKokonaismaara = "2";
	private static final String reseptinTyyppiKestoaika = "3";
	private static final String nullFlavorNI = "NI";
	private static final String oletus_reseptilaji = "1";

	private POCDMT000040ClinicalDocument clinicalDocument;

	public ReseptiPurkaja() throws ConfigurationException {
		super();
	}

	@Override
	protected String getCodeSystem() {
		return "1.2.246.537.5.40105.2006.1";
	}

	/**
	 * Purkaa lääkemääräyksen tiedot LaakemaaraysTO luokkaan
	 *
	 * @param cda String cda josta tiedot puretaan
	 * @return LaakemaaraysTO johon kerätyt tiedot on sijoitettu
	 * @throws PurkuException
	 */
	public LaakemaaraysTO puraLaakemaarays(String cda) throws ConfigurationException, PurkuException {
		try {
			clinicalDocument = JaxbUtil.getInstance().unmarshaller(cda);
			LaakemaaraysTO laakemaarays = luoLaakemaaraysTO(clinicalDocument);
			tarkistaAsiakirjaVersio(clinicalDocument, laakemaarays);
			if (laakemaarays.isAsiakirjaVersioTuettu()) {
				puraLeimakentat(clinicalDocument, laakemaarays);
				puraPotilas(clinicalDocument, laakemaarays);
				puraAuthor(clinicalDocument, laakemaarays);
				puraAuthorization(clinicalDocument, laakemaarays);
				puraComponentOf(clinicalDocument, laakemaarays);
				puraEntryt(clinicalDocument, laakemaarays);
				varmistaValmisteenlaji(laakemaarays);
				varmistaValmisteenTunnuksenTyyppi(laakemaarays);
				puraLaatijaNayttoMuoto(clinicalDocument, laakemaarays);
				if (laakemaarays instanceof LaakemaarayksenKorjausTO) {
					puraKorjausAuthor(clinicalDocument, (LaakemaarayksenKorjausTO) laakemaarays);
				} else if (laakemaarays instanceof LaakemaarayksenMitatointiTO) {
					puraMitatointiAuthor(clinicalDocument, (LaakemaarayksenMitatointiTO) laakemaarays);
				}
				return laakemaarays;
			} else {
				ReseptiNayttomuodonPurkaja purkaja = new ReseptiNayttomuodonPurkaja();
				return purkaja.puraLaakemaarays(cda);
			}
		} catch (JAXBException e) {
			throw new PurkuException(e);
		}
	}

	/**
	 * Purkaa
	 * clinicalDocument/component/structuredBody/component/section/component/section/component/section
	 * rakenteen alta löytyvien entryen tiedot LaakemaaraysTOn
	 *
	 * @param clinicalDocument POCDMT000040ClinicalDocument josta tiedot hetaan
	 * @param laakemaarays     LaakemaaraysTO johon tiedot sijoitetaan
	 * @throws PurkuException
	 */
	protected void puraEntryt(POCDMT000040ClinicalDocument clinicalDocument, LaakemaaraysTO laakemaarays)
			throws PurkuException {
		for (POCDMT000040Entry entry : haeEntryt(clinicalDocument)) {
			String organizerCode = "";
			if (null != entry.getOrganizer() && null != entry.getOrganizer().getCode()
					&& null != entry.getOrganizer().getCode().getCode()) {
				organizerCode = entry.getOrganizer().getCode().getCode();
			}
			if (organizerCode.equals(KantaCDAConstants.Laakityslista.LAAKEVALMISTEEN_JA_PAKKAUKSEN_TIEDOT)
					&& !entry.getOrganizer().getComponents().isEmpty()) {
				puraLaakevalmisteenJaPakkauksenTiedot(entry.getOrganizer().getComponents().get(0), laakemaarays);
			} else if (organizerCode.equals(KantaCDAConstants.Laakityslista.VAIKUTTAVAT_AINESOSAT)) {
				puraVaikuttavatAinesosat(entry.getOrganizer().getComponents(), laakemaarays);
			} else if (organizerCode.equals(KantaCDAConstants.Laakityslista.MUUT_AINESOSAT)) {
				puraMuutAinesosat(entry.getOrganizer().getComponents(), laakemaarays);
			} else if (organizerCode.equals(KantaCDAConstants.Laakityslista.ANNOSOSIO_JA_JATKOOSIOT)) {
				puraAnnostus(entry.getOrganizer().getComponents(), laakemaarays);
			} else if (organizerCode.equals(KantaCDAConstants.Laakityslista.RESEPTIN_MUUT_TIEDOT)
					|| organizerCode.equals(KantaCDAConstants.Laakityslista.LAAKEMAARAYKSEN_KORJAUKSEN_MUUT_TIEDOT)
					|| organizerCode.equals(KantaCDAConstants.Laakityslista.LAAKEMAARAYKSEN_MITATOINNIN_MUUT_TIEDOT)) {
				puraLaakemaarayksenMuutTiedot(entry.getOrganizer().getComponents(), laakemaarays);
			}
		}

	}

	/**
	 * Hakee clinicalDocument elementistä entryt rakenteesta
	 * component/structuredBody/component/section/component/section/component/section/
	 *
	 * @param clinicalDocument POCDMT00040ClinicalDocument josta entryjä haetaan
	 * @return POCDMT00040Entry lista
	 * @throws PurkuException
	 */
	private List<POCDMT000040Entry> haeEntryt(POCDMT000040ClinicalDocument clinicalDocument) throws PurkuException {
		if (null == clinicalDocument || null == clinicalDocument.getComponent()
				|| null == clinicalDocument.getComponent().getStructuredBody()
				|| clinicalDocument.getComponent().getStructuredBody().getComponents().isEmpty()
				|| null == clinicalDocument.getComponent().getStructuredBody().getComponents().get(0).getSection()
				|| clinicalDocument.getComponent().getStructuredBody().getComponents().get(0).getSection()
						.getComponents().isEmpty()
				|| null == clinicalDocument.getComponent().getStructuredBody().getComponents().get(0).getSection()
						.getComponents().get(0).getSection()
				|| clinicalDocument.getComponent().getStructuredBody().getComponents().get(0).getSection()
						.getComponents().get(0).getSection().getComponents().isEmpty()
				|| null == clinicalDocument.getComponent().getStructuredBody().getComponents().get(0).getSection()
						.getComponents().get(0).getSection().getComponents().get(0).getSection()) {
			throw new PurkuException("component/structuredBody/component/section/component/section/component/section/");
		}
		return clinicalDocument.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents()
				.get(0).getSection().getComponents().get(0).getSection().getEntries();
	}

	/**
	 * Purkaa lääkevalmisteen ja pakkauksen tiedot osion LaakemaaraysTOhon
	 *
	 * @param component    POCDMT000040Component4 josta tiedot heataan
	 * @param laakemaarays LaakemaaraysTO johon tiedot sijoitetaan
	 * @throws PurkuException
	 */
	protected void puraLaakevalmisteenJaPakkauksenTiedot(POCDMT000040Component4 component, LaakemaaraysTO laakemaarays)
			throws PurkuException {
		if (null == component || null == component.getSubstanceAdministration()) {
			return;
		}
		String aptValmistusOhje = null;
		if (null != component.getSubstanceAdministration().getText()) {
			aptValmistusOhje = puraContent(component.getSubstanceAdministration().getText());
		}
		puraVoimassaolonLoppuaika(component.getSubstanceAdministration(), laakemaarays);
		puraLaakeaineenVahvuus(component.getSubstanceAdministration().getDoseQuantity(), laakemaarays);
		puraValmiste(component.getSubstanceAdministration().getConsumable(), laakemaarays);

		for (POCDMT000040EntryRelationship entryRelationsip : component.getSubstanceAdministration()
				.getEntryRelationships()) {
			puraAsiakirjakohtaisetValmisteenJapakkauksenEntryRelationshipit(entryRelationsip, laakemaarays);
			POCDMT000040Supply supply = entryRelationsip.getSupply();
			if (null != supply) {
				puraReseptinTyyppijaMaaraTiedot(supply, laakemaarays);
				// Potilaan tiedot lienee jo poimittukkin

				puraTuoteTiedot(supply, laakemaarays);

				// Lääkkeen määränneen lääkärin ja organisaation tiedot lienee jo poimittukkin

				puraParticipantTiedot(supply, laakemaarays);

				puraValmisteenJaPakkauksenSupplyEntryrelationshipObservationit(supply.getEntryRelationships(),
						laakemaarays);
			}
		}

		if (StringUtils.isNotEmpty(aptValmistusOhje)) {
			if (null == laakemaarays.getApteekissaValmistettavaLaake()) {
				laakemaarays.setApteekissaValmistettavaLaake(new ApteekissaValmistettavaLaakeTO());
			}
			laakemaarays.getApteekissaValmistettavaLaake().setValmistusohje(aptValmistusOhje);
		}
	}

	protected void puraAsiakirjakohtaisetValmisteenJapakkauksenEntryRelationshipit(
			POCDMT000040EntryRelationship entryRelationsip, LaakemaaraysTO laakemaarays) {
		return;
	}

	/**
	 * Purkaa substanceAdministration elementistä voimassaolon loppuajan jos annettu
	 *
	 * @param substanceAdministration POCDMT000040SubstanceAdministration josta
	 *                                tieto haetaan
	 * @param laakemaarays            LaakemaaraysTO johon tieto sijoitetaan
	 * @throws PurkuException
	 */
	private void puraVoimassaolonLoppuaika(POCDMT000040SubstanceAdministration substanceAdministration,
			LaakemaaraysTO laakemaarays) throws PurkuException {
		if (null != substanceAdministration && !substanceAdministration.getEffectiveTimes().isEmpty()) {
			if (null != substanceAdministration.getEffectiveTimes().get(0).getValue()) {
				laakemaarays.setMaarayspaiva(puraAika(substanceAdministration.getEffectiveTimes().get(0).getValue()));
			} else if (substanceAdministration.getEffectiveTimes().get(0) instanceof IVLTS) {
				if (null != ((IVLTS) substanceAdministration.getEffectiveTimes().get(0)).getLow()) {
					laakemaarays.setMaarayspaiva(
							puraAika(((IVLTS) substanceAdministration.getEffectiveTimes().get(0)).getLow().getValue()));
				}
				if (null != ((IVLTS) substanceAdministration.getEffectiveTimes().get(0)).getHigh()) {
					laakemaarays.setLaakemaarayksenVoimassaolonLoppuaika(puraAika(
							((IVLTS) substanceAdministration.getEffectiveTimes().get(0)).getHigh().getValue()));
				}
			}
		}
	}

	/**
	 * Purkaa supply elementistä myyntiluvanhaltijan, työnantajan ja
	 * vakuutuslaitoksen
	 *
	 * @param laakemaarays LaakemaaraysTO johon tiedot sijoitetaan
	 * @param supply       POCDMT00040Supply josta tietoja haetaan
	 */
	private void puraParticipantTiedot(POCDMT000040Supply supply, LaakemaaraysTO laakemaarays) {
		if (null == supply) {
			return;
		}

		for (POCDMT000040Participant2 participant : supply.getParticipants()) {
			String participantRole = participant.getParticipantRole().getClassCodes().get(0);
			String name = puraContent(participant.getParticipantRole().getPlayingEntity().getNames().get(0));
			if (participantRole.equals(participantRoleOWN)) {
				alustaYksilointitiedot(laakemaarays);
				laakemaarays.getValmiste().getYksilointitiedot().setMyyntiluvanHaltija(name);
			} else if (participantRole.equals(participantRoleEMP)) {
				laakemaarays.setTyonantaja(name);
			} else if (participantRole.equals(participantRolePAYOR)) {
				laakemaarays.setVakuutuslaitos(name);
			}
		}
	}

	/**
	 * Purkaa supply elementistä yksiköintitunnuksen ja kauppanimen
	 *
	 * @param supply       POCDMT00040Supply josta tietoja haetaan
	 * @param laakemaarays LaakemaaraysTO johon tiedot siojoitetaan
	 */
	private void puraTuoteTiedot(POCDMT000040Supply supply, LaakemaaraysTO laakemaarays) {
		// VNR-koodi
		if (null == supply || null == supply.getProduct()) {
			return;
		}

		String yksilointitunnus = null;
		String kauppanimi = null;

		yksilointitunnus = supply.getProduct().getManufacturedProduct().getManufacturedLabeledDrug().getCode()
				.getCode();
		if (!onkoNullTaiTyhja(yksilointitunnus)) {
			alustaYksilointitiedot(laakemaarays);
			laakemaarays.getValmiste().getYksilointitiedot().setYksilointitunnus(yksilointitunnus);
			// Sanomalla oleva yksilöintitunnus on aina VNR-koodi (jos kyseessä on
			// myyntiluvallinen valmiste)
			laakemaarays.getValmiste().getYksilointitiedot().setTunnuksenTyyppi(KantaCDAConstants.VNR_tunnus);
		}

		// Jos manufacturedLabeledDrug/name on olemassa ja se ei ole tyhjä haetaan
		// kauppanimi sieltä
		// muuten haetaan kauppanimeä polusta manufacturedLabeledDrug/code[@displayName]
		if (null != supply.getProduct().getManufacturedProduct().getManufacturedLabeledDrug().getName() && !supply
				.getProduct().getManufacturedProduct().getManufacturedLabeledDrug().getName().getContent().isEmpty()) {
			kauppanimi = puraContent(
					supply.getProduct().getManufacturedProduct().getManufacturedLabeledDrug().getName());
		} else {
			kauppanimi = supply.getProduct().getManufacturedProduct().getManufacturedLabeledDrug().getCode()
					.getDisplayName();
		}

		if (!onkoNullTaiTyhja(kauppanimi)) {
			// jos valmisteen laji = 1
			alustaYksilointitiedot(laakemaarays);
			laakemaarays.getValmiste().getYksilointitiedot().setKauppanimi(kauppanimi);
		}

		// Vanhemmilla asiakirjoilla valmisteenlaji on
		// manufacturedLabeledDrug-rakenteessa
		if (laakemaarays.isAsiakirjaTaaksepainYhteensopiva()) {
			puraValmisteenlaji(supply.getProduct().getManufacturedProduct().getManufacturedLabeledDrug(), laakemaarays);
		}
	}

	/**
	 * Purkaa vanhemmilta asiakirjaversioilta valmisteen lajin (taaksepäin
	 * yhteensopivuus)
	 *
	 * @param manufacturedLabeledDrug
	 * @param laakemaarays
	 */
	protected void puraValmisteenlaji(POCDMT000040LabeledDrug manufacturedLabeledDrug, LaakemaaraysTO laakemaarays) {
		if (manufacturedLabeledDrug == null || manufacturedLabeledDrug.getCode() == null
				|| manufacturedLabeledDrug.getCode().getTranslations() == null
				|| manufacturedLabeledDrug.getCode().getTranslations().isEmpty()) {
			return;
		}
		List<CR> qualifiers = manufacturedLabeledDrug.getCode().getTranslations().get(0).getQualifiers();
		if (qualifiers != null && !qualifiers.isEmpty()) {
			CR qualifier = manufacturedLabeledDrug.getCode().getTranslations().get(0).getQualifiers().get(0);
			qualifier.getValue().getCode();
			laakemaarays.getValmiste().getYksilointitiedot().setValmisteenLaji(qualifier.getValue().getCode());
			laakemaarays.getValmiste().getYksilointitiedot()
					.setValmisteenLajiNimi(qualifier.getValue().getDisplayName());
			laakemaarays.setApteekissaValmistettavaLaake(KantaCDAUtil.onkoApteekissaValmistettava(laakemaarays));
		}
	}

	protected void puraAsiakirjakohtaisetKokoJaMaaraTiedot(POCDMT000040Supply supply, LaakemaaraysTO laakemaarays) {
		return;
	}

	/**
	 * Purkaa supply elementistä reseptin tyypin ja tyyppikohtaiset määrä tai aika
	 * tiedot Jos lääkemääräys tyypiltään 1 (Pakkaus) puretaan pakkauksien
	 * lukumäärä, pakkaus ykiskkö ja koko Jos lääkemääräys tyypiltään 2
	 * (kokonainsmäärä) puretaan kokonaismäärän yksikkö ja arvo Jos lääkemääräys
	 * tyypiltään 3 (kestoaika) puretaan alkuaika, keston määrä ja yksikkö
	 *
	 * @param supply       POCDMT00040Supply josta tietoja haetaan
	 * @param laakemaarays LaakemaaraysTO johon tiedot sijoitetaan
	 * @throws PurkuException
	 */
	private void puraReseptinTyyppijaMaaraTiedot(POCDMT000040Supply supply, LaakemaaraysTO laakemaarays)
			throws PurkuException {
		puraAsiakirjakohtaisetKokoJaMaaraTiedot(supply, laakemaarays);
		if (null == supply || null == supply.getCode()) {
			return;
		}
		laakemaarays.setReseptintyyppi(supply.getCode().getCode());
		// Ainakin jos reseptintyyppi =1
		if (laakemaarays.getReseptintyyppi().equals(reseptinTyyppiPakkaus)) {
			laakemaarays.setPakkauksienLukumaara(supply.getRepeatNumber().getValue().intValue());
			if (null != supply.getQuantity() && null != supply.getQuantity().getUnit()
					&& null != supply.getQuantity().getValue()) {
				alustaYksilointitiedot(laakemaarays);
				laakemaarays.getValmiste().getYksilointitiedot().setPakkausyksikko(supply.getQuantity().getUnit());
				laakemaarays.getValmiste().getYksilointitiedot()
						.setPakkauskoko(Double.parseDouble(supply.getQuantity().getValue()));
			}
			alustaYksilointitiedot(laakemaarays);
		} else if (laakemaarays.getReseptintyyppi().equals(reseptinTyyppiKokonaismaara)) {
			laakemaarays.setLaakkeenKokonaismaaraUnit(supply.getQuantity().getUnit());
			// Joillakin lääkemääräyksillä kokonaismäärä voi olla ilmaistuna esim. 123.0
			BigDecimal bd = new BigDecimal(supply.getQuantity().getValue());
			laakemaarays.setLaakkeenKokonaismaaraValue(bd.intValue());
		} else if (laakemaarays.getReseptintyyppi().equals(reseptinTyyppiKestoaika)) {
			IVLTS time = (IVLTS) supply.getEffectiveTimes().get(0);
			laakemaarays.setAjalleMaaratynReseptinAlkuaika(puraAika(time.getLow().getValue()));
			laakemaarays.setAjalleMaaratynReseptinAikamaaraUnit(time.getWidth().getUnit());
			laakemaarays.setAjalleMaaratynReseptinAikamaaraValue(Integer.parseInt(time.getWidth().getValue()));
		}
	}

	/**
	 * Purkaa entryRelationShip elemettien Observationeista lääkemuodon, iteroinnin,
	 * apteekissa valmistettavan lääkken osoittimen, pakkauskoon, myyntiluvan
	 * haltijan, säilytys astian ja valmisteen lajin LaakemaaraysTOn
	 *
	 * @param entryRelationships List<POCDMT000040EntryRelationship> joista tietoja
	 *                           haetaan
	 * @param laakemaarays       LaakemaaraysTO johon tiedot sijoitetaan
	 */
	private void puraValmisteenJaPakkauksenSupplyEntryrelationshipObservationit(
			List<POCDMT000040EntryRelationship> entryRelationships, LaakemaaraysTO laakemaarays) {
		for (POCDMT000040EntryRelationship entryRelationship : entryRelationships) {
			String observationCode = entryRelationship.getObservation().getCode().getCode();
			if (KantaCDAConstants.Laakityslista.LAAKEMUOTO.equals(observationCode)) {
				puraLaakemuoto(entryRelationship.getObservation(), laakemaarays);
			} else if (KantaCDAConstants.Laakityslista.ITEROINTI.equals(observationCode)) {
				puraIterointi(entryRelationship.getObservation(), laakemaarays);
			} else if (KantaCDAConstants.Laakityslista.APTEEKISSA_VALMISTETTAVAN_LAAKKEEN_OSOITIN
					.equals(observationCode)) {
				puraApteekissavalmistettavanLaakkeenOsoitin(entryRelationship.getObservation(), laakemaarays);
			} else if (KantaCDAConstants.Laakityslista.PAKKAUSKOON_KERROIN.equals(observationCode)) {
				puraPakkauskoonKerroin(entryRelationship.getObservation(), laakemaarays);
			} else if (KantaCDAConstants.Laakityslista.PAKKAUSKOKO_TEKSTIMUODOSSA.equals(observationCode)) {
				puraPakkauskoko(entryRelationship.getObservation(), laakemaarays);
			} else if (KantaCDAConstants.Laakityslista.LAITE.equals(observationCode)) {
				puraPakkauskoonLaite(entryRelationship.getObservation(), laakemaarays);
			} else if (KantaCDAConstants.Laakityslista.SAILYTYSASTIA.equals(observationCode)) {
				puraSailytysastia(entryRelationship.getObservation(), laakemaarays);
			} else if (KantaCDAConstants.Laakityslista.LAAKKEEN_LAJI.equals(observationCode)) {
				puraValmisteenlaji(entryRelationship.getObservation(), laakemaarays);
			} else {
				puraAsiakirjakohtainenSupplyEntryrelationsipObservation(entryRelationship.getObservation(),
						observationCode, laakemaarays);
			}
		}

	}

	protected void puraAsiakirjakohtainenSupplyEntryrelationsipObservation(POCDMT000040Observation observation,
			String observationCode, LaakemaaraysTO laakemaarays) {
		return;
	}

	/**
	 * Purkaa apteekissavalmistettaval laakkeen osoittimen observation elementistä
	 * LaakemaaraysTOn
	 *
	 * @param observation  POCDMT000040Observation josta tietoja haetaan
	 * @param laakemaarays LaakemaaraysTO johon tieto sijoitetaan
	 */
	private void puraApteekissavalmistettavanLaakkeenOsoitin(POCDMT000040Observation observation,
			LaakemaaraysTO laakemaarays) {
		if (observation.getCode().getCode()
				.equals(KantaCDAConstants.Laakityslista.APTEEKISSA_VALMISTETTAVAN_LAAKKEEN_OSOITIN)) {
			laakemaarays.setApteekissaValmistettavaLaake(((BL) observation.getValues().get(0)).isValue());
		}
	}

	/**
	 * Purkaa pakkauskoon kertoimen observation elementistä LaakemaaraysTOn
	 *
	 * @param observation  POCDMT000040Observation josta tietoja haetaan
	 * @param laakemaarays LaakemaaraysTO johon tiedot sijoitetaan
	 */
	private void puraPakkauskoonKerroin(POCDMT000040Observation observation, LaakemaaraysTO laakemaarays) {
		if (observation.getCode().getCode().equals(KantaCDAConstants.Laakityslista.PAKKAUSKOON_KERROIN)
				&& !observation.getValues().isEmpty()) {
			alustaYksilointitiedot(laakemaarays);
			laakemaarays.getValmiste().getYksilointitiedot()
					.setPakkauskokokerroin(((INT) observation.getValues().get(0)).getValue().intValue());
		}
	}

	/**
	 * Purkaa pakkauskoon observation elementistä LaakemaaraysTOn
	 *
	 * @param observation  POCDMT000040Observation josta tietoja haetaan
	 * @param laakemaarays LaakemaaraysTO johon tiedot sijoitetaan
	 */
	private void puraPakkauskoko(POCDMT000040Observation observation, LaakemaaraysTO laakemaarays) {
		if (observation.getCode().getCode().equals(KantaCDAConstants.Laakityslista.PAKKAUSKOKO_TEKSTIMUODOSSA)) {
			alustaYksilointitiedot(laakemaarays);
			laakemaarays.getValmiste().getYksilointitiedot()
					.setPakkauskokoteksti(puraContent(observation.getValues().get(0)));
		}
	}

	/**
	 * Purkaa säilytysastian observation elementistä LaakemaaraysTOn
	 *
	 * @param observation  POCDMT000040Observation josta tietoja haetaan
	 * @param laakemaarays LaakemaaraysTO johon tiedot sijoitetaan
	 */
	private void puraSailytysastia(POCDMT000040Observation observation, LaakemaaraysTO laakemaarays) {
		if (observation.getCode().getCode().equals(KantaCDAConstants.Laakityslista.SAILYTYSASTIA)
				&& !observation.getValues().isEmpty()) {
			alustaYksilointitiedot(laakemaarays);
			laakemaarays.getValmiste().getYksilointitiedot()
					.setSailytysastia(puraContent(observation.getValues().get(0)));
		}
	}

	/**
	 * Purkaa valmisteen lajin observation elementistä LaakemaaraysTOn
	 *
	 * @param observation  POCDMT000040Observation josta tietoja haetaan
	 * @param laakemaarays LaakemaaraysTO johon tiedot sijoitetaan
	 */
	private void puraValmisteenlaji(POCDMT000040Observation observation, LaakemaaraysTO laakemaarays) {
		if (observation.getCode().getCode().equals(KantaCDAConstants.Laakityslista.LAAKKEEN_LAJI)
				&& !observation.getValues().isEmpty()) {
			alustaYksilointitiedot(laakemaarays);
			laakemaarays.getValmiste().getYksilointitiedot()
					.setValmisteenLaji(((CD) observation.getValues().get(0)).getCode());
			laakemaarays.getValmiste().getYksilointitiedot()
					.setValmisteenLajiNimi(((CD) observation.getValues().get(0)).getDisplayName());
			laakemaarays.setApteekissaValmistettavaLaake(KantaCDAUtil.onkoApteekissaValmistettava(laakemaarays));
		}
	}

	/**
	 * Purkaa iteroinnin observation elementistä LaakemaaraysTOn
	 *
	 * @param observation  POCDMT000040Observation josta tietoja haetaan
	 * @param laakemaarays LaakemaaraysTO johon tiedot sijoitetaan
	 */
	private void puraIterointi(POCDMT000040Observation observation, LaakemaaraysTO laakemaarays) {
		if (observation.getCode().getCode().equals(KantaCDAConstants.Laakityslista.ITEROINTI)) {
			if (null != observation.getText()) {
				laakemaarays.setIterointiTeksti(puraContent(observation.getText()));
			}
			IVLTS iterointiValue = observation.getEffectiveTime();
			if (null != iterointiValue && null != iterointiValue.getWidth()) {
				laakemaarays.setIterointienValiUnit(iterointiValue.getWidth().getUnit());
				laakemaarays.setIterointienValiValue(Integer.parseInt(iterointiValue.getWidth().getValue()));
			}
			laakemaarays.setIterointienMaara(observation.getRepeatNumber().getValue().intValue());
		}

	}

	/**
	 * Purkaa valmisteen käyttötavat observation elementistä LaakemaaraytTOn
	 * valmisteen käyttötapoihin
	 *
	 * @param observation  POCDMT000040Observation josta tietoja haetaan
	 * @param laakemaarays LaakemaaraysTO johon tiedot sijoitetaan
	 */
	private void puraLaakemuoto(POCDMT000040Observation observation, LaakemaaraysTO laakemaarays) {
		if (observation.getCode().getCode().equals(KantaCDAConstants.Laakityslista.LAAKEMUOTO)
				&& !observation.getValues().isEmpty()) {
			ST value = (ST) observation.getValues().get(0);
			alustaValmiste(laakemaarays);
			ValmisteenKayttotapaTO kayttotapa = new ValmisteenKayttotapaTO();
			kayttotapa.setLaakemuoto(puraContent(value));
			laakemaarays.getValmiste().getKayttotavat().add(kayttotapa);
		}
	}

	/**
	 * Purkaa lääkeaineen vahvuuden LaakemaaraysTOn valmisteen yksilöintitietoihin
	 *
	 * @param doseQuantity IVLPQ josta tiedot puretaan
	 * @param laakemaarays LaakemaaraysTO jonka valmisteen yksilöintitietohin tiedot
	 *                     sijoitetaan
	 */
	private void puraLaakeaineenVahvuus(IVLPQ doseQuantity, LaakemaaraysTO laakemaarays) {
		if (null == doseQuantity || doseQuantity.getTranslations().isEmpty()) {
			return;
		}
		String vahvuus = puraContent(doseQuantity.getTranslations().get(0).getOriginalText());
		if (!onkoNullTaiTyhja(vahvuus)) {
			alustaYksilointitiedot(laakemaarays);
			laakemaarays.getValmiste().getYksilointitiedot().setVahvuus(vahvuus);
		}
	}

	/**
	 * Purkaa valmisteen tiedot LaakemaaraysTO
	 *
	 * @param consumable
	 * @param laakemaarays
	 */
	private void puraValmiste(POCDMT000040Consumable consumable, LaakemaaraysTO laakemaarays) {
		if (null == consumable || null == consumable.getManufacturedProduct()) {
			return;
		}

		if (null != consumable.getManufacturedProduct().getManufacturedLabeledDrug()) {
			if (null == laakemaarays.getLaaketietokannanVersio()) { // Jos versiotieto on asetettu jo aiemmin, ei
				// ylikirjoiteta sitä
				laakemaarays.setLaaketietokannanVersio(consumable.getManufacturedProduct().getManufacturedLabeledDrug()
						.getCode().getCodeSystemVersion());
			}
			String atcCode = consumable.getManufacturedProduct().getManufacturedLabeledDrug().getCode().getCode();
			if (!onkoNullTaiTyhja(atcCode)) {
				alustaYksilointitiedot(laakemaarays);
				laakemaarays.getValmiste().getYksilointitiedot().setATCkoodi(atcCode);
				laakemaarays.getValmiste().getYksilointitiedot().setATCnimi(
						consumable.getManufacturedProduct().getManufacturedLabeledDrug().getCode().getDisplayName());
			}
		}
		if (null != consumable.getManufacturedProduct().getManufacturedMaterial()) {
			laakemaarays.setLaaketietokannanUlkopuolinenValmiste(
					puraContent(consumable.getManufacturedProduct().getManufacturedMaterial().getName()));
		}

	}

	/**
	 * Purkaa lääkemääräyksen vaikuttavat ainesosat osion tiedot LaakemaaraysTOn
	 *
	 * @param components   POCDMT000040Component4 lista joista tiedot haetaan
	 * @param laakemaarays LaakemaaraysTO johon tiedot sijoitetaan
	 */
	private void puraVaikuttavatAinesosat(List<POCDMT000040Component4> components, LaakemaaraysTO laakemaarays) {
		for (POCDMT000040Component4 component : components) {
			CE code = component.getSubstanceAdministration().getConsumable().getManufacturedProduct()
					.getManufacturedLabeledDrug().getCode();
			if (null == laakemaarays.getLaaketietokannanVersio()) { // Jos versiotieto on asetettu jo aiemmin, ei
				// ylikirjoiteta sitä
				laakemaarays.setLaaketietokannanVersio(code.getCodeSystemVersion());
			}
			if (laakemaarays.isApteekissaValmistettavaLaake() != null && laakemaarays.isApteekissaValmistettavaLaake()) {
				alustaApteekissaValmistettavaLaake(laakemaarays);
				VaikuttavaAinesosaTO ainesosa = new VaikuttavaAinesosaTO();
				ainesosa.setATCkoodi(code.getCode());
				ainesosa.setATCnimi(code.getDisplayName());
				if (null != component.getSubstanceAdministration().getDoseQuantity().getCenter()) {
					ainesosa.setAinesosanMaaraValue(Double.parseDouble(
							component.getSubstanceAdministration().getDoseQuantity().getCenter().getValue()));
					ainesosa.setAinesosanMaaraUnit(
							component.getSubstanceAdministration().getDoseQuantity().getCenter().getUnit());
				} // TODO: pitäsikö olla else if, koska
					// Lääkemääräyksen_sanomat_CDA_R2_rakenteena_v.3.30 4.3.2
					// ...Yksittäisen ainesosan vahvuuden (määrän) ilmoittamiseen pitää valita
					// jompikumpi ilmoitustapa..
				if (null != component.getSubstanceAdministration().getDoseQuantity().getTranslations()
						&& !component.getSubstanceAdministration().getDoseQuantity().getTranslations().isEmpty()) {
					ainesosa.setAinesosanMaaraTekstina(puraContent(component.getSubstanceAdministration()
							.getDoseQuantity().getTranslations().get(0).getOriginalText()));
				}
				ainesosa.setKoodamatonNimi(puraContent(component.getSubstanceAdministration().getConsumable()
						.getManufacturedProduct().getManufacturedLabeledDrug().getName()));
				laakemaarays.getApteekissaValmistettavaLaake().getVaikuttavatAinesosat().add(ainesosa);
			} else {
				String name = puraContent(component.getSubstanceAdministration().getConsumable()
						.getManufacturedProduct().getManufacturedLabeledDrug().getName());
				alustaYksilointitiedot(laakemaarays);

				// DONE: Mistä päätellään että poimittanko manfacturedLabeledDrug\name
				// kauppanimeksi vai valmisteen
				// vaikuttavanaineen lääkeaineeksi
				// laakemaarays.getValmiste().getYksilointitiedot().setKauppanimi(name);
				// vai
				VaikuttavaAineTO vaikuttavaAine = new VaikuttavaAineTO();
				vaikuttavaAine.setLaakeaine(name);
				laakemaarays.getValmiste().getVaikuttavatAineet().add(vaikuttavaAine);
			}
		}
	}

	/**
	 * Purkaa lääkemääräyksen apteekissavalmistettavan lääkkeen muut ainesosat
	 * LaakmaaraysTOn apteekissavalmistettavan muihin ainesosiin
	 *
	 * @param components   POCDMT000040Component4 lista joista tiedot haetaan
	 * @param laakemaarays LaakemaaraysTO johon tiedot sijoitetaan
	 */
	private void puraMuutAinesosat(List<POCDMT000040Component4> components, LaakemaaraysTO laakemaarays) {
		for (POCDMT000040Component4 component : components) {
			alustaApteekissaValmistettavaLaake(laakemaarays);
			MuuAinesosaTO ainesosa = new MuuAinesosaTO();
			ainesosa.setNimi(puraContent(component.getSubstanceAdministration().getConsumable().getManufacturedProduct()
					.getManufacturedLabeledDrug().getName()));
			if (null != component.getSubstanceAdministration().getDoseQuantity().getCenter()) {
				ainesosa.setAinesosanMaaraValue(Double
						.parseDouble(component.getSubstanceAdministration().getDoseQuantity().getCenter().getValue()));
				ainesosa.setAinesosanMaaraUnit(
						component.getSubstanceAdministration().getDoseQuantity().getCenter().getUnit());
			}
			if (null != component.getSubstanceAdministration().getDoseQuantity().getTranslations()
					&& !component.getSubstanceAdministration().getDoseQuantity().getTranslations().isEmpty()) {
				ainesosa.setAinesosanMaaraTekstina(puraContent(component.getSubstanceAdministration().getDoseQuantity()
						.getTranslations().get(0).getOriginalText()));
			}
			laakemaarays.getApteekissaValmistettavaLaake().getMuutAinesosat().add(ainesosa);
		}
	}

	/**
	 * Purkaa lääkemääräyksen annostus osion ja sijoitaa tiedot LaakemaaraysTOn
	 *
	 * @param components   POCDMT000040Component4 lista joista tietoja haetaan
	 * @param laakemaarays LaakemaaraysTO johon tiedot sijoitetaan
	 */
	private void puraAnnostus(List<POCDMT000040Component4> components, LaakemaaraysTO laakemaarays)
			throws PurkuException {
		for (POCDMT000040Component4 component : components) {
			if (onkoObservationCodeCode(component.getObservation(),
					KantaCDAConstants.Laakityslista.ANNOSTELU_VAIN_TEKSTINA)) {
				laakemaarays.setAnnosteluPelkastaanTekstimuodossa(
						((BL) component.getObservation().getValues().get(0)).isValue());
			} else if (onkoAnnostelukausiComponent(component)) {
				puraAnnostelukausiComponent(component, laakemaarays);
			} else if (onkoAnnostuksenLisatiedotComponent(component)) {
				puraAnnostuksenLisatiedotComponent(component, laakemaarays);
			} else {
				laakemaarays.setAnnostusohje(KantaCDAUtil
						.poistaKontrolliMerkit(puraContent(component.getSubstanceAdministration().getText())));
				if (!component.getSubstanceAdministration().getEntryRelationships().isEmpty()
						&& onkoObservationCodeCode(
								component.getSubstanceAdministration().getEntryRelationships().get(0).getObservation(),
								KantaCDAConstants.Laakityslista.SIC_MERKINTA)) {// 56
					laakemaarays.setSICmerkinta(((BL) component.getSubstanceAdministration().getEntryRelationships()
							.get(0).getObservation().getValues().get(0)).isValue());
				}
			}
		}
	}

	/**
	 * Purkaa lääkemääräyksen muut tiedot osion ja siojottaa tiedot LaakemaaraysTOn
	 * Purkaa käyttötarkoitus tekstin, hoitolajit, uusimiskeillon perustelun,
	 * viestin apteekille, PKV merkinnän, reseptin lajin sekä pysyväislääkitysm
	 * uudistamiskielto, lääkevaihtokielto, laakkeen käytön aloitus ja huume
	 * merkinnät
	 *
	 * @param components   POCDMT000040Component4 list joista tietoja haetaan
	 * @param laakemaarays LaakemaaraysTO johon tiedot siojitetaan
	 */
	private void puraLaakemaarayksenMuutTiedot(List<POCDMT000040Component4> components, LaakemaaraysTO laakemaarays) {
		for (POCDMT000040Component4 component : components) {
			if (null != component.getObservation() && !component.getObservation().getValues().isEmpty()) {
				if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.KAYTTOTARKOITUS_TEKSTINA)) {
					if (((ST) component.getObservation().getValues().get(0)).getNullFlavors().isEmpty()
							|| !((ST) component.getObservation().getValues().get(0)).getNullFlavors().get(0)
									.equals(nullFlavorNI)) {
						laakemaarays
								.setKayttotarkoitusTeksti(puraContent(component.getObservation().getValues().get(0)));
					}
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.HOITOLAJI)) {
					for (ANY value : component.getObservation().getValues()) {
						laakemaarays.getHoitolajit().add(((CE) value).getCode());
					}
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.PYSYVA_LAAKITYS)) {
					if (component.getObservation().getValues().get(0) instanceof BL) {
						laakemaarays
								.setPysyvaislaakitys(((BL) component.getObservation().getValues().get(0)).isValue());
					}
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.UUSIMISKIELTO)) {
					laakemaarays.setUudistamiskielto(((BL) component.getObservation().getValues().get(0)).isValue());
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.UUDISTAMISKIELLON_SYY)) {
					laakemaarays.setUusimiskiellonSyy(((CE) component.getObservation().getValues().get(0)).getCode());
					if (null != ((CE) component.getObservation().getValues().get(0)).getOriginalText()) {
						laakemaarays.setUusimiskiellonPerustelu(
								puraContent(((CE) component.getObservation().getValues().get(0)).getOriginalText()));
					}
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.LAAKEVAIHTOKIELTO)) {
					laakemaarays.setLaakevaihtokielto(((BL) component.getObservation().getValues().get(0)).isValue());
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.ALLE_12VUOTIAAN_PAINO)) {
					laakemaarays
							.setAlle12VuotiaanPainoUnit(((PQ) component.getObservation().getValues().get(0)).getUnit());
					laakemaarays.setAlle12VuotiaanPainoValue(
							new BigDecimal(((PQ) component.getObservation().getValues().get(0)).getValue()));
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.ANNOSJAKELU)) {
					laakemaarays.setAnnosjakelu(((BL) component.getObservation().getValues().get(0)).isValue());
					if (null != component.getObservation().getText()) {
						laakemaarays.setAnnosjakeluTeksti(puraContent(component.getObservation().getText()));
					}
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.VIESTI_APTEEKILLE)) {
					laakemaarays.setViestiApteekille(puraContent(component.getObservation().getValues().get(0)));
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.ERILLISSELVITYS)) {
					laakemaarays.setErillisselvitys(((CE) component.getObservation().getValues().get(0)).getCode());
					if (null != component.getObservation().getText()) {
						laakemaarays.setErillisselvitysteksti(puraContent(component.getObservation().getText()));
					}
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.POTILAAN_TUNNISTAMINEN)) {
					if (null != component.getObservation().getText()) {
						laakemaarays.setPotilaanTunnistaminenTeksti(puraContent(component.getObservation().getText()));
					}
					laakemaarays
							.setPotilaanTunnistaminen(((CE) component.getObservation().getValues().get(0)).getCode());
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.HUUMAUSAINE_PKV_LAAKEMAARAYS)) {
					laakemaarays.setPKVlaakemaarays(((CE) component.getObservation().getValues().get(0)).getCode());
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.KYSEESSA_LAAKKEEN_KAYTON_ALOITUS)) {
					laakemaarays.setKyseessaLaakkeenkaytonAloitus(
							((BL) component.getObservation().getValues().get(0)).isValue());
				} else if (onkoObservationCodeCode(component.getObservation(), KantaCDAConstants.Laakityslista.HUUME)) {
					laakemaarays.setHuume(((BL) component.getObservation().getValues().get(0)).isValue());
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.RESEPTIN_LAJI)) {
					laakemaarays.setReseptinLaji(((CE) component.getObservation().getValues().get(0)).getCode());
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.KORJAUKSEN_PERUSTELU)) {
					if (laakemaarays instanceof LaakemaarayksenKorjausTO) {
						puraLaakemaarayksenKorjauksenPerustelu(component.getObservation(),
								(LaakemaarayksenKorjausTO) laakemaarays);
					}
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.LAAKEMAARAYKSEN_MITATOINNIN_SYY)) {
					if (laakemaarays instanceof LaakemaarayksenMitatointiTO) {
						puraLaakemaarayksenMitatoinninSyy(component.getObservation(),
								(LaakemaarayksenMitatointiTO) laakemaarays);
					}
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.LAAKEMAARAYKSEN_MITATOINNIN_TYYPPI)) {
					if (laakemaarays instanceof LaakemaarayksenMitatointiTO) {
						puraLaakemaarayksenMitatoinninTyyppi(component.getObservation(),
								(LaakemaarayksenMitatointiTO) laakemaarays);
					}
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.APTEEKISSA_TALLENNETTU_LAAKEMAARAYS)) {
					laakemaarays.setApteekissaTallennettuLaakemaarays(
							((CE) component.getObservation().getValues().get(0)).getCode());
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.LAAKARINPALKKIO)) {
					laakemaarays.setLaakarinPalkkio(
							new Double(((MO) component.getObservation().getValues().get(0)).getValue()));
					laakemaarays.setValuutta(((MO) component.getObservation().getValues().get(0)).getCurrency());
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.LAAKARINPALKKIO_ERIKOISLAAKARINA)) {
					laakemaarays.setLaakarinpalkkioErikoislaakarina(
							((BL) component.getObservation().getValues().get(0)).isValue());
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.TARTUNTATAUTILAIN_MUKAINEN_LAAKE)) {
					laakemaarays.setTartuntatauti(((BL) component.getObservation().getValues().get(0)).isValue());
				} else if (onkoObservationCodeCode(component.getObservation(),
						KantaCDAConstants.Laakityslista.APTEEKISSA_TALLENNETTU_LAAKEMAARAYS_PERUSTELU)) {
					laakemaarays.setApteekissaTallennettuLaakemaaraysPerustelu(
							((CE) component.getObservation().getValues().get(0)).getCode());
					if (null != ((CE) component.getObservation().getValues().get(0)).getOriginalText()) {
						laakemaarays.setApteekissaTallennettuLaakemaaraysMuuSyy(
								puraContent(((CE) component.getObservation().getValues().get(0)).getOriginalText()));
					}
				}
			}
		}

		if (onkoNullTaiTyhja(laakemaarays.getReseptinLaji())) {
			asetaReseptinLaji(laakemaarays);
		}
	}

	private void asetaReseptinLaji(LaakemaaraysTO laakemaarays) {
		if (laakemaarays.isAsiakirjaTaaksepainYhteensopiva()) {// Ennen versiota 1.2.246.777.11.2015.11 oli vain
			// tavallisia reseptejä
			laakemaarays.setReseptinLaji(oletus_reseptilaji);
		}
	}

	/**
	 * Purkaa määräyspäivän ja palveluyksikön tiedot componentOf elementistä
	 *
	 * @param clinicalDocument POCDMT000040ClinicalDocument jonka componentOf
	 *                         elementistä tiedot haetaan
	 * @param laakemaarays     LaakemaaraysTO johon tiedot sijoitetaan
	 * @throws PurkuException
	 */
	protected void puraComponentOf(POCDMT000040ClinicalDocument clinicalDocument, LaakemaaraysTO laakemaarays)
			throws PurkuException {
		laakemaarays.setMaarayspaiva(
				puraAika(clinicalDocument.getComponentOf().getEncompassingEncounter().getEffectiveTime().getValue()));
		if (!clinicalDocument.getComponentOf().getEncompassingEncounter().getIds().isEmpty() && clinicalDocument
				.getComponentOf().getEncompassingEncounter().getIds().get(0).getNullFlavors().isEmpty()) {
			laakemaarays.setPalvelutapahtumanOid(
					clinicalDocument.getComponentOf().getEncompassingEncounter().getIds().get(0).getRoot());
		}
		// TODO: tee palvelutapahtuma oid -purkamisen käsittely (huomio nullflavorit)
		// laakemaarays.getPalvelutapahtuma().setOid(clinicalDocument.getComponentOf().getEncompassingEncounter().getIds().get(0).);

		puraPalveluyksikko(
				clinicalDocument.getComponentOf().getEncompassingEncounter().getLocation().getHealthCareFacility(),
				laakemaarays);
	}

	/**
	 * Purkaa palveluyksikön tiedot healthCareFacility elementistä
	 *
	 * @param healthCareFacility POCDMT000040HealthCareFacility josta tiedot heataan
	 * @param laakemaarays       LaakemaaraysTO johon tiedot sijoitetaan
	 */
	private void puraPalveluyksikko(POCDMT000040HealthCareFacility healthCareFacility, LaakemaaraysTO laakemaarays) {
		OrganisaatioTO palveluYksikko = new OrganisaatioTO();
		palveluYksikko.setYksilointitunnus(healthCareFacility.getIds().get(0).getRoot());
		if (healthCareFacility.getLocation().getName() != null) {
			palveluYksikko.setNimi(puraContent(healthCareFacility.getLocation().getName()));
		}
		if (healthCareFacility.getLocation().getAddr() != null) {
			palveluYksikko.setOsoite(puraOsoite(healthCareFacility.getLocation().getAddr()));
		}
		if (healthCareFacility.getServiceProviderOrganization() != null) {
			// palveluYksikko.setPalveluYksikko(puraOrganisaatio(healthCareFacility.getServiceProviderOrganization()));
			palveluYksikko.setToimintaYksikko(puraOrganisaatio(healthCareFacility.getServiceProviderOrganization()));
		}

		/*
		 * if(laakemaarays.getAmmattihenkilo() == null) {
		 * laakemaarays.setAmmattihenkilo(new AmmattihenkiloTO()); }
		 * laakemaarays.getAmmattihenkilo().setOrganisaatio(palveluYksikko);
		 */
		laakemaarays.setLaatimispaikka(palveluYksikko);

		// if(laakemaarays.getPalvelutapahtuma() != null) {
		// laakemaarays.getPalvelutapahtuma().setToimintayksikko(palveluYksikko);
		// }
	}

	/**
	 * Purkaa potilaan henkilotiedot clinicalDocumentsta
	 *
	 * @param clinicaldocument POCDMT000040ClinicalDocument josta tiedot haetaan
	 * @param laakemaarays     LaakemaaraysTO johon tiedot sijoitetaan
	 */
	protected void puraPotilas(POCDMT000040ClinicalDocument clinicaldocument, LaakemaaraysTO laakemaarays) {
		POCDMT000040RecordTarget recordTarget = clinicaldocument.getRecordTargets().get(0);
		HenkilotiedotTO henkilotiedot;
		if (!recordTarget.getPatientRole().getIds().isEmpty()
				&& recordTarget.getPatientRole().getIds().get(0).getNullFlavors().isEmpty()) {
			henkilotiedot = new HenkilotiedotTO(puraKokoNimi(recordTarget.getPatientRole().getPatient().getNames()),
					recordTarget.getPatientRole().getIds().get(0).getExtension());
		} else {
			henkilotiedot = new HenkilotiedotTO(puraKokoNimi(recordTarget.getPatientRole().getPatient().getNames()),
					recordTarget.getPatientRole().getPatient().getBirthTime().getValue(), Integer.parseInt(
							recordTarget.getPatientRole().getPatient().getAdministrativeGenderCode().getCode()));
		}
		laakemaarays.setPotilas(henkilotiedot);
	}

	/**
	 * Purkaa lääkemääräyksen tekijän ja mahdollisen kirjaajan tiedot
	 * clinicaDocumentista
	 *
	 * @param clinicalDocument POCDMT000040ClinicalDocument josta tiedot haetaan
	 * @param laakemaarays     LaakemaaraysTO johon tiedot sijoitetaan
	 * @throws PurkuException
	 */
	protected void puraAuthor(POCDMT000040ClinicalDocument clinicalDocument, LaakemaaraysTO laakemaarays)
			throws PurkuException {
		POCDMT000040Author author = clinicalDocument.getAuthors().get(0);
		laakemaarays.setAmmattihenkilo(luoAmmattihenkilo(author));
		List<POCDMT000040Author> authors = clinicalDocument.getAuthors();
		if (authors != null && !authors.isEmpty()) {
			for (POCDMT000040Author auth : authors) {
				if (auth.getFunctionCode() != null
						&& MaaraajanRooli.KIRJAAJA.getRooliKoodi().equals(auth.getFunctionCode().getCode())) {
					laakemaarays.setKirjaaja(luoAmmattihenkilo(auth));
					break;
				}
			}
		}
	}

	/**
	 * Purkaa Alaikäisen potilastietojen luovuttaminen huoltajille
	 * clinicaDocumentista
	 *
	 * @param clinicalDocument POCDMT000040ClinicalDocument josta tiedot haetaan
	 * @param laakemaarays     LaakemaaraysTO johon tiedot sijoitetaan
	 * @throws PurkuException
	 */
	protected void puraAuthorization(POCDMT000040ClinicalDocument clinicalDocument, LaakemaaraysTO laakemaarays)
			throws PurkuException {
		List<POCDMT000040Authorization> authorizations = clinicalDocument.getAuthorizations();
		if (authorizations != null && !authorizations.isEmpty()) {
			POCDMT000040Authorization auth = authorizations.get(0);
			laakemaarays.setAlaikaisenKieltoKoodi(auth.getConsent().getCode().getCode());
		}
	}

	/**
	 * Apumetodi observation/code/coden tarkistamiseen Varmistaa että
	 * observation/code/code on olemassa ja vertaa annettuun codeen
	 *
	 * @param observation POCDMT000040Observation jonka code/code halutaan tarkistaa
	 * @param code        String koodi johon codea verratan
	 * @return true jos code on sama muuten false (myös jos elementtejä ei ole
	 *         olemassa)
	 */
	private boolean onkoObservationCodeCode(POCDMT000040Observation observation, String code) {
		return null != observation && null != observation.getCode() && null != observation.getCode().getCode()
				&& code.equals(observation.getCode().getCode());
	}

	/**
	 * Apumetodi laakemaarayksen valmisteen yksilöintitietojen alustamiseen
	 * Varmistaa että valmiste ja sen yksilöintitiedot on luotu jotta niihin voidaan
	 * tietoa sijoittaa
	 *
	 * @param laakemaarays LaakemaaraysTO jonka valmisteen yksilöintitiedot
	 *                     alustetaan
	 */
	protected void alustaYksilointitiedot(LaakemaaraysTO laakemaarays) {
		alustaValmiste(laakemaarays);
		if (null == laakemaarays.getValmiste().getYksilointitiedot()) {
			laakemaarays.getValmiste().setYksilointitiedot(new ValmisteenYksilointitiedotTO());
		}
	}

	/**
	 * Apumetodi lääkemääräyksen valmisteen alustamiseen Varmistaa että valimiste on
	 * luotu jotta siihen voidaan tieoa sijoittaa
	 *
	 * @param laakemaarays LaakemaaraysTO jonka valmiste alustetaan
	 */
	private void alustaValmiste(LaakemaaraysTO laakemaarays) {
		if (null == laakemaarays.getValmiste()) {
			laakemaarays.setValmiste(new ValmisteTO());
		}
	}

	/**
	 * Apumetodi lääkemääräyksen apteekissavalmistettavan lääkkeen valmisteen
	 * alustamiseetn Varmistaa että apteekissa ApteekissaValmistettavaLaakeTO on
	 * luotu jotta siihen voidaan sijoittaa tietoja
	 *
	 * @param laakemaarays LaakemaaraysTO jonka apteekissavelmistettava laake
	 *                     alustetaan
	 */
	private void alustaApteekissaValmistettavaLaake(LaakemaaraysTO laakemaarays) {
		if (null == laakemaarays.getApteekissaValmistettavaLaake()) {
			laakemaarays.setApteekissaValmistettavaLaake(new ApteekissaValmistettavaLaakeTO());
		}
	}

	/**
	 * Purkaa pakkauksen laitteen observation elementistä LaakemaaraysTOn
	 *
	 * @param observation  POCDMT000040Observation josta tietoja haetaan
	 * @param laakemaarays LaakemaaraysTO johon tiedot sijoitetaan
	 */
	private void puraPakkauskoonLaite(POCDMT000040Observation observation, LaakemaaraysTO laakemaarays) {
		if (observation.getCode().getCode().equals(KantaCDAConstants.Laakityslista.LAITE)
				&& !observation.getValues().isEmpty()) {
			alustaYksilointitiedot(laakemaarays);
			laakemaarays.getValmiste().getYksilointitiedot()
					.setPakkauslaite(puraContent(observation.getValues().get(0)));
		}
	}

	/**
	 * Purkaa korjauksen syyn ja perustelun LaakemaarayksenKorjausTOn Korjauksen syy
	 * ja perustelu ovat observationin alla value elementeissä
	 *
	 * <pre>
	 * {@code
	 * <observation classCode="OBS" moodCode="EVN">
	 *   <code code="97" codeSystem="1.2.246.537.6.12.2002.126" codeSystemName=
	"Lääkityslista" displayName=
	"Lääkemääräyksen korjauksen perustelu"/>
	 *   <value xsi:type=”CE” code=”[SYYKOODI]” codeSystem=
	”1.2.246.537.6.600.2013”/>
	 *   <value xsi:type="ST">[PERUSTELU]</value>
	 * </observation>
	 * }
	 * </pre>
	 *
	 * @param observation  POCDMT000040Observation josta tietoja haetaan
	 * @param laakemaarays LaakemaarayksenKorjausTO johon tiedot sijoitetaan
	 */
	private void puraLaakemaarayksenKorjauksenPerustelu(POCDMT000040Observation observation,
			LaakemaarayksenKorjausTO laakemaarays) {
		if (observation.getValues() != null && !observation.getValues().isEmpty()) {
			for (ANY value : observation.getValues()) {
				if (value instanceof CE) {
					laakemaarays.setKorjauksenSyyKoodi(((CE) value).getCode());
				} else if (value instanceof ST) {
					laakemaarays.setKorjauksenPerustelu(puraContent(value));
				}
			}
		}
	}

	/**
	 * Purkaa mitätöinnin syyn ja perustelun LaakemaarayksenMitatointiTOn
	 *
	 * @param observation  POCDMT000040Observation josta tietoja haetaan
	 * @param laakemaarays LaakemaarayksenMitatointiTO johon tiedot sijoitetaan
	 */
	private void puraLaakemaarayksenMitatoinninSyy(POCDMT000040Observation observation,
			LaakemaarayksenMitatointiTO laakemaarays) {
		if (observation != null && observation.getValues() != null && !observation.getValues().isEmpty()) {
			for (ANY value : observation.getValues()) {
				if (value instanceof CE) {
					laakemaarays.setMitatoinninSyyKoodi(((CE) value).getCode());
					if (observation.getValues().size() > 1) {
						laakemaarays.setMitatoinninPerustelu(puraContent(observation.getValues().get(1)));
					}
				} else if (value instanceof ST) {
					laakemaarays.setMitatoinninPerustelu(puraContent(value));
				}
			}
		}
	}

	/**
	 * Purkaa mitätöinnin syyn ja perustelun LaakemaarayksenMitatointiTOn
	 *
	 * @param observation  POCDMT000040Observation josta tietoja haetaan
	 * @param laakemaarays LaakemaarayksenMitatointiTO johon tiedot sijoitetaan
	 */
	private void puraLaakemaarayksenMitatoinninTyyppi(POCDMT000040Observation observation,
			LaakemaarayksenMitatointiTO laakemaarays) {
		if (observation != null && observation.getValues() != null && !observation.getValues().isEmpty()) {
			CD value = (CD) observation.getValues().get(0);
			laakemaarays.setMitatoinninTyyppiKoodi(value.getCode());
			List<CR> qualifiers = value.getQualifiers();
			if (qualifiers != null && !qualifiers.isEmpty()) {
				for (CR qualifier : qualifiers) {
					if (qualifier.getName() != null && qualifier.getValue() != null) {

						if (KantaCDAConstants.Laakityslista.LAAKEMAARAYKSEN_MITATOINNIN_SUOSTUMUS
								.equals(qualifier.getName().getCode())) {
							laakemaarays.setMitatoinninSuostumusKoodi(qualifier.getValue().getCode());
						} else if (KantaCDAConstants.Laakityslista.LAAKEMAARAYKSEN_MITATOINNIN_OSAPUOLI
								.equals(qualifier.getName().getCode())) {
							laakemaarays.setMitatoinninOsapuoli(qualifier.getValue().getCode());
						}
					}
				}
			}
		}
	}

	/**
	 * Tarkistaa, että valmisteen laji on asetettu. Jos ei ole, pyrkii päättelemään
	 * lajin muiden tietojen perusteella (taaksepäin yhteensopivuus)
	 *
	 * @param clinicalDocument
	 * @param laakemaarays
	 */
	protected void varmistaValmisteenlaji(LaakemaaraysTO laakemaarays) {
		alustaYksilointitiedot(laakemaarays);
		if (!onkoNullTaiTyhja(laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji())) {
			return;
		}
		// Apteekissa valmistettava lääke (7)
		if (laakemaarays.isApteekissaValmistettavaLaake() != null && laakemaarays.isApteekissaValmistettavaLaake()) {
			laakemaarays.getValmiste().getYksilointitiedot().setValmisteenLaji("7");
			// laakemaarays.getValmiste().getYksilointitiedot().setValmisteenLajiNimi("");
			return;
		}
		// Lääketietokannan ulkopuolinen valmiste (6)
		if (!onkoNullTaiTyhja(laakemaarays.getLaaketietokannanUlkopuolinenValmiste())
				&& (laakemaarays.isApteekissaValmistettavaLaake() == null
						|| !laakemaarays.isApteekissaValmistettavaLaake())) {
			laakemaarays.getValmiste().getYksilointitiedot().setValmisteenLaji("6");
		}
		// Vaikuttavalla aineella määrätty (9)
		if (!onkoNullTaiTyhja(laakemaarays.getValmiste().getYksilointitiedot().getATCkoodi())
				&& onkoNullTaiTyhja(laakemaarays.getValmiste().getYksilointitiedot().getYksilointitunnus())
				&& onkoNullTaiTyhja(laakemaarays.getValmiste().getYksilointitiedot().getKauppanimi())) {
			laakemaarays.getValmiste().getYksilointitiedot().setValmisteenLaji("9");
		}
	}

	/**
	 * varmistaa että valmisteen tunnuksen tyyppi asetetaan VNR-tunnukseski jos
	 * kyseessä on myyntiluvallinen valmiste.
	 *
	 * @param laakemaarays LaakemaaraysTO jonka valmisteen tunnuksen tyyppi
	 *                     varmistetaan
	 */
	protected void varmistaValmisteenTunnuksenTyyppi(LaakemaaraysTO laakemaarays) {
		alustaYksilointitiedot(laakemaarays);
		if (onkoNullTaiTyhja(laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji())
				|| onkoNullTaiTyhja(laakemaarays.getValmiste().getYksilointitiedot().getYksilointitunnus())) {
			return;
		}
		// Myyntiluvallinen lääkevalmiste (1) => Tunnus VNR-numero
		if ("1".equals(laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji())) {
			laakemaarays.getValmiste().getYksilointitiedot().setTunnuksenTyyppi(KantaCDAConstants.VNR_tunnus);
		} else {
			laakemaarays.getValmiste().getYksilointitiedot().setTunnuksenTyyppi(0);
		}
	}

	/**
	 * Purkaa lääkemääräyksen korjaajan tiedot clinicaDocumentista
	 *
	 * @param clinicalDocument POCDMT000040ClinicalDocument josta tiedot haetaan
	 * @param laakemaarays     LaakemaaraysTO johon tiedot sijoitetaan
	 * @throws PurkuException
	 */
	protected void puraKorjausAuthor(POCDMT000040ClinicalDocument clinicalDocument,
			LaakemaarayksenKorjausTO laakemaarays) throws PurkuException {
		List<POCDMT000040Author> authors = clinicalDocument.getAuthors();
		if (authors != null && !authors.isEmpty()) {
			for (POCDMT000040Author author : authors) {
				if (author.getFunctionCode() != null
						&& MaaraajanRooli.KORJAAJA.getRooliKoodi().equals(author.getFunctionCode().getCode())) {
					laakemaarays.setKorjaaja(luoAmmattihenkilo(author));
				}
			}
		}
	}

	// TODO: puraMitatointiAuthor ja puraKorjausAuthor voisi kenties refaktoroida
	// (ja ehkäpä purkajan puraAuthor metodi
	// myös) yhdeksi authorin purku metodiksi
	protected void puraMitatointiAuthor(POCDMT000040ClinicalDocument clinicalDocument,
			LaakemaarayksenMitatointiTO laakemaarays) throws PurkuException {
		List<POCDMT000040Author> authors = clinicalDocument.getAuthors();
		if (authors != null && !authors.isEmpty()) {
			for (POCDMT000040Author author : authors) {
				if (author.getFunctionCode() != null
						&& MaaraajanRooli.MITATOIJA.getRooliKoodi().equals(author.getFunctionCode().getCode())) {
					laakemaarays.setMitatoija(luoAmmattihenkilo(author));
				}
			}
		}
	}

	protected AmmattihenkiloTO luoAmmattihenkilo(POCDMT000040Author author) throws PurkuException {
		AmmattihenkiloTO ammattihenkilo = new AmmattihenkiloTO();
		if (null != author.getFunctionCode()) {
			ammattihenkilo.setRooli(author.getFunctionCode().getCode());
		}
		if (null != author.getTime() && author.getTime().getNullFlavors().isEmpty()
				&& !onkoNullTaiTyhja(author.getTime().getValue())) {
			ammattihenkilo.setKirjautumisaika(puraAika(author.getTime().getValue()));
		}
		for (II id : author.getAssignedAuthor().getIds()) {
			if (id.getRoot().startsWith(yksilointitunnusRoot)) {
				ammattihenkilo.setSvNumero(id.getExtension());
			} else if (id.getRoot().equals(rekisterointinumeroRoot)) {
				ammattihenkilo.setRekisterointinumero(id.getExtension());
			}
		}
		if (null != author.getAssignedAuthor().getCode()) {
			ammattihenkilo.setErikoisala(author.getAssignedAuthor().getCode().getCode());
			ammattihenkilo.setErikoisalaName(author.getAssignedAuthor().getCode().getDisplayName());
			for (CR qualifier : author.getAssignedAuthor().getCode().getTranslations().get(0).getQualifiers()) {
				String qualifierCodeSystem = qualifier.getName().getCodeSystem();
				String qualifierCode = qualifier.getName().getCode();

				if (qualifierCodeSystem.equals(tekninenCDAR2rakennekoodisto)) {
					if (qualifierCode.equals(virkanimikeCode)) {
						ammattihenkilo.setVirkanimike(puraContent(qualifier.getValue().getOriginalText()));
					} else if (qualifierCode.equals(oppiarvoCode)) {
						ammattihenkilo.setOppiarvo(qualifier.getValue().getCode());
						// originalText pitäisi löytyä aina, laitetaan ensisijaisesti se.
						// Jos se puuttuu, kokellaan laittaa displayName
						ammattihenkilo.setOppiarvoTekstina(puraContent(qualifier.getValue().getOriginalText()));
						if (onkoNullTaiTyhja(ammattihenkilo.getOppiarvoTekstina())) {
							ammattihenkilo.setOppiarvoTekstina(qualifier.getValue().getDisplayName());
						}
					}
				} else if (qualifierCodeSystem.equals(KantaCDAConstants.Laakityslista.CODESYSTEM)
						&& qualifierCode.equals(KantaCDAConstants.Laakityslista.AMMATTIOIKEUS)) {
					ammattihenkilo.setAmmattioikeus(qualifier.getValue().getCode());
					ammattihenkilo.setAmmattioikeusName(qualifier.getValue().getDisplayName());
				}
			}
		}
		ammattihenkilo.setKokonimi(puraKokoNimi(author.getAssignedAuthor().getAssignedPerson().getNames()));
		ammattihenkilo.setOrganisaatio(puraOrganisaatio(author.getAssignedAuthor().getRepresentedOrganization()));
		return ammattihenkilo;
	}

	protected void puraLaatijaNayttoMuoto(POCDMT000040ClinicalDocument clinicalDocument, LaakemaaraysTO laakemaarays) {
		List<String> nayttomuoto = haeLaatijanJaKorjaajanNayttomuoto(clinicalDocument);
		if (nayttomuoto != null && !nayttomuoto.isEmpty()) {
			laakemaarays.getNayttomuoto().clear();
			laakemaarays.getNayttomuoto().addAll(nayttomuoto);
		}
	}

	/**
	 * Hakee lääkemääräyksen laatijan ja mahdollisen korjaajan näyttömuodon
	 * rakenteesta
	 * component/structuredBody/component/section/component/section/text/
	 *
	 * @param clinicalDocument POCDMT00040ClinicalDocument josta näyttömuotoa
	 *                         haetaan
	 * @return List<String> lista (lista on tyhjä, jos näyttömuotoa ei löydy)
	 */
	private List<String> haeLaatijanJaKorjaajanNayttomuoto(POCDMT000040ClinicalDocument clinicalDocument) {
		List<String> nayttomuoto = new ArrayList<String>();
		if (null == clinicalDocument || null == clinicalDocument.getComponent()
				|| null == clinicalDocument.getComponent().getStructuredBody()
				|| clinicalDocument.getComponent().getStructuredBody().getComponents().isEmpty()
				|| null == clinicalDocument.getComponent().getStructuredBody().getComponents().get(0).getSection()
				|| clinicalDocument.getComponent().getStructuredBody().getComponents().get(0).getSection()
						.getComponents().isEmpty()
				|| null == clinicalDocument.getComponent().getStructuredBody().getComponents().get(0).getSection()
						.getComponents().get(0).getSection()
				|| null == clinicalDocument.getComponent().getStructuredBody().getComponents().get(0).getSection()
						.getComponents().get(0).getSection().getText()) {
			return nayttomuoto;
		}
		puraText(clinicalDocument.getComponent().getStructuredBody().getComponents().get(0).getSection().getComponents()
				.get(0).getSection(), nayttomuoto);
		return nayttomuoto;
	}

	/**
	 * Palauttaa konstruktorissa XML-muodossa annetun Lääkemääräysasiakirjan
	 * JAXB-elementtimuodossa.
	 * 
	 * @return Lääkemääräysasiakirja JAXB-elementtimuodossa
	 */
	public POCDMT000040ClinicalDocument getCda() {
		return clinicalDocument;
	}

	protected void puraAnnostelukausiComponent(POCDMT000040Component4 component, LaakemaaraysTO laakemaarays)
			throws PurkuException {
		POCDMT000040SubstanceAdministration sa = component.getSubstanceAdministration();
		if (sa.getIds() != null && !sa.getIds().isEmpty()) {
			laakemaarays.setAnnostelukausiId(sa.getIds().get(0).getRoot());
		}

		if (sa.getEffectiveTimes() != null && !sa.getEffectiveTimes().isEmpty()) {
			puraAnnostelukaudenAika(sa, laakemaarays);
		}

		for (POCDMT000040EntryRelationship er : sa.getEntryRelationships()) {
			if (onkoObservationCodeCode(er.getObservation(), ANNOSTUS_TARVITTAESSA_CODE)) {
				laakemaarays.setAnnostusTarvittaessa(puraObservationBooleanValue(er.getObservation()));
			} else if (onkoObservationCodeCode(er.getObservation(), ANNOSTELUKAUDEN_KESTO_CODE)) {
				laakemaarays.setAnnostelukaudenPituus(puraJaksonPituus((IVLPQ) er.getObservation().getValues().get(0),
						new AnnostelukaudenPituusTO()));
			} else if (onkoObservationCodeCode(er.getObservation(), LAAKE_TAUOLLA_CODE)) {
				puraLaakeTauolla(er.getObservation(), laakemaarays);
			} else if (onkoObservationCodeCode(er.getObservation(), ANNOSJAKSON_PITUUS_CODE)) {
				laakemaarays.setAnnosajaksonPituus(
						puraJaksonPituus((IVLPQ) er.getObservation().getValues().get(0), new AnnosjaksonPituusTO()));
			} else if (onkoAnnosEntry(er)) {
				laakemaarays.getAnnokset().add(puraAnnos(er));
			}
		}
	}

	protected boolean onkoAnnostelukausiComponent(POCDMT000040Component4 component) {
		POCDMT000040SubstanceAdministration sa = component.getSubstanceAdministration();
		return sa != null && sa.getCode() != null && ANNOSTELUKAUSI_CODE.equals(sa.getCode().getCode());
	}

	protected boolean puraObservationBooleanValue(POCDMT000040Observation o) throws PurkuException {

		if (o.getValues().isEmpty() || !(o.getValues().get(0) instanceof BL)) {
			throw new PurkuException("Could'n parse ObservationBooleanValue");
		}

		return ((BL) o.getValues().get(0)).isValue();
	}

	protected boolean onkoAnnosEntry(POCDMT000040EntryRelationship e) {
		return e.getSubstanceAdministration() != null && e.getSubstanceAdministration().getCode() != null
				&& ANNOKSET_CODE.equals(e.getSubstanceAdministration().getCode().getCode());
	}

	protected AnnosTO puraAnnos(POCDMT000040EntryRelationship er) throws PurkuException {
		AnnosTO annos = new AnnosTO();

		POCDMT000040SubstanceAdministration annosSa = er.getSubstanceAdministration();

		if (annosSa.getIds().size() > 0) {
			annos.setAnnosId(annosSa.getIds().get(0).getRoot());
		}

		puraAnnosMaara(annosSa.getDoseQuantity(), annos);
		if (annosSa.getAdministrationUnitCode() != null) {
			CE unit = annosSa.getAdministrationUnitCode();
			KoodiTO yksikko = new KoodiTO();
			yksikko.setTunniste(unit.getCode());
			yksikko.setLyhytNimi(unit.getDisplayName());
			annos.setAnnosyksikko(yksikko);
		}

		for (POCDMT000040EntryRelationship saEr : annosSa.getEntryRelationships()) {
			if (onkoObservationCodeCode(saEr.getObservation(), ANNOS_TARVITTAESSA_CODE)) {
				annos.setAnnosTarvittaessa(puraObservationBooleanValue(saEr.getObservation()));
			} else if (onkoObservationCodeCode(saEr.getObservation(), ANNOSAJANKOHTA_CODE)) {
				annos.setAnnosajankohta( new KoodiTO(((CE) saEr.getObservation().getValues().get(0)).getCode(), null));
			} else if (onkoObservationCodeCode(saEr.getObservation(), ANNOSAIKA_CODE)) {
				annos.setAnnosaika(puraKellonaika(puraContent(saEr.getObservation().getValues().get(0))));
			} else if (onkoObservationCodeCode(saEr.getObservation(), ANNOSJAKSON_PAIVA_CODE)) {
				annos.setAnnosjaksonPaiva(new KoodiTO(((CE) saEr.getObservation().getValues().get(0)).getCode(), null));				
			} else if (onkoFysikaalinenAnnosEntry(saEr)) {
				puraFysikaalinenAnnosMaara(saEr.getSubstanceAdministration().getDoseQuantity(), annos);
			}
		}

		return annos;
	}

	protected void puraAnnosMaara(IVLPQ doseQ, AnnosTO annos) throws PurkuException {
		Double center = puraDoubleValue(doseQ.getCenter());

		if (center != null) {
			annos.setVakioAnnos(center);
		} else {
			annos.setLowAnnos(puraDoubleValue(doseQ.getLow()));
			annos.setHighAnnos(puraDoubleValue(doseQ.getHigh()));
		}
	}

	protected void puraAnnostuksenLisatiedotComponent(POCDMT000040Component4 component, LaakemaaraysTO laakemaarays)
			throws PurkuException {
		if (!onkoAnnostuksenLisatiedotComponent(component)) {
			return;
		}

		POCDMT000040SubstanceAdministration sa = component.getSubstanceAdministration();

		if (sa.getRouteCode() != null) {
			CE route = sa.getRouteCode();
			KoodiTO reitti = new KoodiTO();
			reitti.setTunniste(route.getCode());
			reitti.setLyhytNimi(route.getDisplayName());
			laakemaarays.setLaakkeenantoreitti(reitti);
		}

		for (POCDMT000040EntryRelationship er : sa.getEntryRelationships()) {
			if (onkoObservationCodeCode(er.getObservation(), KAYTTOOHJEEN_LISATIETO_CODE)) {
				if (((ST) er.getObservation().getValues().get(0)).getNullFlavors().isEmpty()
						|| !((ST) er.getObservation().getValues().get(0)).getNullFlavors().get(0)
								.equals(nullFlavorNI)) {
					laakemaarays.setKayttoOhjeLisatiedot(puraContent(er.getObservation().getValues().get(0)));
				}
			}
		}
	}

	protected boolean onkoAnnostuksenLisatiedotComponent(POCDMT000040Component4 component) {
		if (component == null) {
			return false;
		}

		POCDMT000040SubstanceAdministration sa = component.getSubstanceAdministration();
		return sa != null && sa.getCode() != null && ANNOSTUKSEN_LISATIEDOT_CODE.equals(sa.getCode().getCode());
	}

	protected LaakemaaraysTO puraAnnostelukaudenAika(POCDMT000040SubstanceAdministration substanceAdministration,
			LaakemaaraysTO laakemaarays) throws PurkuException {
		if (substanceAdministration != null && !substanceAdministration.getEffectiveTimes().isEmpty()
				&& laakemaarays != null) {
			IVLTS time = (IVLTS) substanceAdministration.getEffectiveTimes().get(0);

			if (time.getLow() != null) {
				laakemaarays.setAnnostelukaudenAlkupvm(puraAika(time.getLow().getValue()));
				
				if (laakemaarays.getAnnostelukaudenAlkupvm() == null) {
					throw new PurkuException("Annostelukauden alkupäivämäärää ei saatu muodostettua: " + time.getLow().getValue());
				}
			}

			if (time.getHigh() != null) {
				laakemaarays.setAnnostelukaudenLoppupvm(puraAika(time.getHigh().getValue()));
				
				if (laakemaarays.getAnnostelukaudenLoppupvm() == null) {
					throw new PurkuException("Annostelukauden loppupäivämäärää ei saatu muodostettua: " + time.getHigh().getValue());
				}
			}
		}

		return laakemaarays;
	}

	protected <T extends JaksonPituusTO> T puraJaksonPituus(IVLPQ value, T to) throws PurkuException {
		if (value == null || to == null) {
			return null;
		}

		if (value.getWidth() != null && StringUtils.isNotEmpty(value.getUnit())) {
			to.setYksikko(value.getWidth().getUnit());
			to.setVakio(puraIntegerValue(value.getWidth()));

		} else if (value.getLow() != null && value.getHigh() != null
				&& StringUtils.isNotEmpty(value.getLow().getUnit())) {
			to.setLow(puraIntegerValue(value.getLow()));
			to.setYksikko(value.getLow().getUnit());
			to.setHigh(puraIntegerValue(value.getHigh()));
		}

		return to;
	}

	protected LaakemaaraysTO puraLaakeTauolla(POCDMT000040Observation observation, LaakemaaraysTO laakemaarays)
			throws PurkuException {
		if (observation != null && observation.getEffectiveTime() != null && laakemaarays != null) {
			IVLTS time = observation.getEffectiveTime();

			if (time.getLow() != null) {
				laakemaarays.setLaakeTauollaAlkupvm(puraAika(time.getLow().getValue()));

				if (laakemaarays.getLaakeTauollaAlkupvm() == null) {
					throw new PurkuException("Lääkkeen tauon alkupäivämäärää ei saatu muodostettua: " + time.getLow().getValue());
				}
			}

			if (time.getHigh() != null) {
				laakemaarays.setLaakeTauollaLoppupvm(puraAika(time.getHigh().getValue()));

				if (laakemaarays.getLaakeTauollaLoppupvm() == null) {
					throw new PurkuException("Lääkkeen tauon loppupäivämäärää ei saatu muodostettua: " + time.getHigh().getValue());
				}
			}
		}

		return laakemaarays;
	}

	protected boolean onkoFysikaalinenAnnosEntry(POCDMT000040EntryRelationship e) {
		return e.getSubstanceAdministration() != null && e.getSubstanceAdministration().getCode() != null
				&& FYS_ANNOKSET_CODE.equals(e.getSubstanceAdministration().getCode().getCode());
	}

	protected AnnosTO puraFysikaalinenAnnosMaara(IVLPQ value, AnnosTO annos) throws PurkuException {
		if (value != null && annos != null) {
			Double center = puraDoubleValue(value.getCenter());
	
			if (center != null && StringUtils.isNotEmpty(value.getCenter().getUnit())) {
				annos.setVakioFysAnnos(center);
				annos.setFysYksikko(value.getCenter().getUnit());
			} else if (value.getLow() != null && value.getHigh() != null && 
					StringUtils.isNotEmpty(value.getLow().getUnit())) {
				annos.setLowFysAnnos(puraDoubleValue(value.getLow()));
				annos.setHighFysAnnos(puraDoubleValue(value.getHigh()));
				annos.setFysYksikko(value.getLow().getUnit());
			}
		}
		
		return annos;
	}
	
	

}
