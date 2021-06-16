/**
 *
 */
package fi.kela.kanta.cda;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import javax.xml.bind.JAXBException;

import org.hl7.v3.CD;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component2;
import org.hl7.v3.POCDMT000040Component3;
import org.hl7.v3.POCDMT000040Component5;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040Reference;
import org.hl7.v3.XActRelationshipExternalReference;

import fi.kela.kanta.cda.validation.ReseptinUudenValidoija;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.util.JaxbUtil;

public class ReseptinUudenKasaaja extends ReseptiKasaaja {

    private final LaakemaaraysTO laakemaarays;

    public ReseptinUudenKasaaja(Properties properties, LaakemaaraysTO laakemaarays) {
        super(properties);
        this.laakemaarays = laakemaarays;
        validoija = new ReseptinUudenValidoija(laakemaarays);
    }

    /**
     * @see fi.kela.kanta.cda.ReseptiKasaaja#luoAsiakirjakohtaisetRakenteet(fi.kela.kanta.to.LaakemaaraysTO)
     */
    @Override
    protected void luoAsiakirjakohtaisetRakenteet(LaakemaaraysTO laakemaarays) {
        // TODO Auto-generated method stub

    }

    /**
     * Kasaa resepti cdan annetun LaakemaaraysTOn pohjalta.
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta cda muodostetaan
     * @return POCDMT000040ClinicalDocument lääkemääräys cda
     */
    private POCDMT000040ClinicalDocument kasaaResepti(LaakemaaraysTO laakemaarays) {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone(ReseptiKasaaja.TIME_ZONE));

        // TODO: Lisää laakemaarays.isKyseessaLaakkeenkaytonAloitus();
        POCDMT000040ClinicalDocument clinicalDocument = of.createPOCDMT000040ClinicalDocument();

        String effectiveTimeValue = getDateFormat().format(now.getTime());
        String today = getTodayDateFormat().format(now.getTime());

        addIdFields(clinicalDocument, laakemaarays, effectiveTimeValue);

        addRecordTarget(clinicalDocument, laakemaarays.getPotilas());
        addAuthor(clinicalDocument, luoAuthor(laakemaarays.getAmmattihenkilo()));
        addCustodian(clinicalDocument);
       	addAuthorization(clinicalDocument, laakemaarays.getAlaikaisenKieltoKoodi());
        
        addComponentOf(clinicalDocument, effectiveTimeValue, laakemaarays.getLaatimispaikka(),
                laakemaarays.getPalvelutapahtumanOid());
        addLocalHeader(clinicalDocument);

        POCDMT000040Component2 component2 = of.createPOCDMT000040Component2();
        clinicalDocument.setComponent(component2);
        component2.setStructuredBody(of.createPOCDMT000040StructuredBody());
        POCDMT000040Component3 component3 = of.createPOCDMT000040Component3();
        component2.getStructuredBody().getComponents().add(component3);
        component3.getTemplateIds().add(of.createPOCDMT000040InfrastructureRootTemplateId());
        // TempalteId
        fetchAttributes(ReseptiKasaaja.template_id, component3.getTemplateIds().get(0));
        component3.setSection(of.createPOCDMT000040Section());
        component3.getSection().setAttributeID(getNextOID(laakemaarays));
        component3.getSection().setId(of.createII());
        component3.getSection().getId().setRoot(getId(laakemaarays));
        component3.getSection().setCode(of.createCE());
        fetchAttributes(Kasaaja.LM_CONTENTS, component3.getSection().getCode());
        component3.getSection().setTitle(of.createST());
        // Title
        component3.getSection().getTitle().getContent().add(component3.getSection().getCode().getDisplayName());

        POCDMT000040Component5 component5 = luoComponent(laakemaarays);
        component3.getSection().getComponents().add(component5);
        // Narrative (paikka, aika, lääkäri)
        component5.getSection().setText(luoNarrativePaikkaPvmLaakari(laakemaarays, today));

        component5.getSection().getComponents().add(luoComponent(laakemaarays));
        // Narrative (lääkemääräyksen tiedot)
        component5.getSection().getComponents().get(0).getSection().setText(luoNarrativeLaakemaarays(laakemaarays));

        List<POCDMT000040Entry> entries = component5.getSection().getComponents().get(0).getSection().getEntries();
        // Valmisteen ja Pakkausten tiedot
        entries.add(
                luoValmisteenJaPakkauksenTiedot(laakemaarays, effectiveTimeValue, laakemaarays.getAmmattihenkilo()));

        // Vaikuttavat aineet
        if ( lisataankoVaikuttavatAineet(laakemaarays) ) {
            entries.add(luoVaikuttavatAinesosat(laakemaarays));
        }
        // Muut ainesosat
        // Vain apteekissa valmistettaville?
        if ( laakemaarays.isApteekissaValmistettavaLaake() ) {
            entries.add(luoMuutAinesosat(laakemaarays));
        }

        // annostus
        entries.add(luoAnnostus(laakemaarays));

        // Lääkemääräyksen muut tiedot
        entries.add(luoMuutTiedot(laakemaarays));

        return clinicalDocument;
    }

    @Override
    protected POCDMT000040Entry luoAsiakirjanMuutTiedot(POCDMT000040Entry entry) {
        return entry;
    }

    @Override
    protected Collection<POCDMT000040Reference> luoViittaukset(LaakemaaraysTO laakemaarays) {
        Collection<POCDMT000040Reference> viittaukset = new ArrayList<POCDMT000040Reference>();
        CD code = of.createCD();
        String oid = getDocumentId(laakemaarays);
        String setId = laakemaarays.getSetId();
        fetchAttributes(Kasaaja.LM_CONTENTS, code);

        if ( onkoNullTaiTyhja(setId) ) {
            setId = getDocumentId(laakemaarays);
            // Pitäisikö tässä olla
            // setId = getId(laakemaarays);
        }

        // viittaus itseensä
        viittaukset.add(luoViittaus(oid, setId, XActRelationshipExternalReference.SPRT, code));
        return viittaukset;
    }

    /**
     * Kasaa uusi resepti konstruktorissa annetun LaakemaaraysTOn pohjalta.
     * 
     * @return Uusi reseptiasiakirja XML-muodossa
     * @throws JAXBException
     */
    @Override
    public String kasaaReseptiAsiakirja() throws JAXBException {
        return JaxbUtil.getInstance().marshalloi(kasaaReseptiCDA(), "urn:hl7-org:v3 CDA_Fi.xsd");
    }

    /**
     * Kasaa uusi resepti konstruktorissa annetun LaakemaaraysTOn pohjalta.
     * 
     * @return Uusi reseptiasiakirja JAXB-elementteinä
     */
    @Override
    public POCDMT000040ClinicalDocument kasaaReseptiCDA() {
        validoiLaakemaarays();
        return kasaaResepti(laakemaarays);
    }
}
