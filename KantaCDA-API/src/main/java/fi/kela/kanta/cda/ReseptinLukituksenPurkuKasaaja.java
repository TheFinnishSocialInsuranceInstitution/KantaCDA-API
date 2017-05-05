/*******************************************************************************
 * Copyright 2017 Kansaneläkelaitos
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package fi.kela.kanta.cda;

import java.util.Calendar;
import java.util.Collection;
import java.util.Properties;
import java.util.TimeZone;

import javax.xml.bind.JAXBException;

import org.hl7.v3.II;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component3;
import org.hl7.v3.POCDMT000040Component5;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040InfrastructureRootTemplateId;
import org.hl7.v3.POCDMT000040Reference;
import org.hl7.v3.POCDMT000040RelatedDocument;
import org.hl7.v3.XActClassDocumentEntryAct;
import org.hl7.v3.XActRelationshipDocument;
import org.hl7.v3.XDocumentActMood;

import fi.kela.kanta.cda.KantaCDAConstants.ReseptisanomanTyyppi;
import fi.kela.kanta.cda.validation.ReseptinLukituksenPurkuValidoija;
import fi.kela.kanta.to.LaakemaarayksenLukituksenPurkuTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.util.JaxbUtil;

public class ReseptinLukituksenPurkuKasaaja extends ReseptiKasaaja {

    private final LaakemaarayksenLukituksenPurkuTO lukituksenPurku;
    private final LaakemaaraysTO alkuperainenLaakemaarays;

    /**
     * Kasaa reseptin lukituksen purku asiakirjan annettujen LaakemaaraysTO:n ja LaakemaarayksenLukituksenPurkuTO:n
     * pohjalta
     * 
     * @param properties
     * @param lukituksenPurku
     *            syy ja lukitussanoman tiedot
     * @param alkuperainenLaakemaarays
     *            lukittu laakemaarays jonka lukitus halutaan purkaa
     */
    public ReseptinLukituksenPurkuKasaaja(Properties properties, LaakemaarayksenLukituksenPurkuTO lukituksenPurku,
            LaakemaaraysTO alkuperainenLaakemaarays) {
        super(properties);
        this.lukituksenPurku = lukituksenPurku;
        this.alkuperainenLaakemaarays = alkuperainenLaakemaarays;
        validoija = new ReseptinLukituksenPurkuValidoija(lukituksenPurku, alkuperainenLaakemaarays);
    }

    private POCDMT000040ClinicalDocument kasaaReseptinLukituksenPurku() {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("EET"));
        String effectiveTimeValue = getDateFormat().format(now.getTime());
        String today = getTodayDateFormat().format(now.getTime());

        POCDMT000040ClinicalDocument clinicalDocument = of.createPOCDMT000040ClinicalDocument();

        addIdFields(clinicalDocument, lukituksenPurku, effectiveTimeValue);
        // Korjataan title ja code
        fetchAttributes("lukituksenpurku.code", clinicalDocument.getCode());
        clinicalDocument.getTitle().getContent().clear();
        clinicalDocument.getTitle().getContent().add(fetchProperty("lukituksenpurku.title"));

        addRecordTarget(clinicalDocument, alkuperainenLaakemaarays.getPotilas());

        addAuthor(clinicalDocument, luoAuthor(lukituksenPurku.getPurkaja()));

        addCustodian(clinicalDocument);
        addRelatedDocument(clinicalDocument, alkuperainenLaakemaarays, lukituksenPurku);
        addComponentOf(clinicalDocument, effectiveTimeValue, lukituksenPurku.getPurkaja().getOrganisaatio(), null);
        addLocalHeader(clinicalDocument);

        clinicalDocument.setComponent(of.createPOCDMT000040Component2());
        clinicalDocument.getComponent().setStructuredBody(of.createPOCDMT000040StructuredBody());
        POCDMT000040Component3 component3 = of.createPOCDMT000040Component3();
        clinicalDocument.getComponent().getStructuredBody().getComponents().add(component3);

        if ( lukituksenPurku.getBodyTemplateIds().isEmpty() ) {
            POCDMT000040InfrastructureRootTemplateId templateIdElement = of
                    .createPOCDMT000040InfrastructureRootTemplateId();
            fetchAttributes("templateId", templateIdElement);
            component3.getTemplateIds().add(templateIdElement);
        }
        else {
            for (String templateId : lukituksenPurku.getBodyTemplateIds()) {
                POCDMT000040InfrastructureRootTemplateId templateIdElement = of
                        .createPOCDMT000040InfrastructureRootTemplateId();
                templateIdElement.setRoot(templateId);
                component3.getTemplateIds().add(templateIdElement);
            }
        }

        // Section
        component3.setSection(of.createPOCDMT000040Section());
        component3.getSection().setAttributeID(getNextOID(alkuperainenLaakemaarays));
        component3.getSection().setId(of.createII());
        component3.getSection().getId().setRoot(getId(alkuperainenLaakemaarays));
        component3.getSection().setCode(of.createCE());
        fetchAttributes("lukituksenpurku.code", component3.getSection().getCode());
        component3.getSection().getCode()
                .setCode(String.valueOf(ReseptisanomanTyyppi.LAAKEMAARAYKSEN_LUKITUKSEN_PURKU.getTyyppi()));
        component3.getSection().setTitle(of.createST());

        component3.getSection().getTitle().getContent().add(component3.getSection().getCode().getDisplayName());

        POCDMT000040Component5 component5 = luoComponent(alkuperainenLaakemaarays);
        component3.getSection().getComponents().add(component5);
        // Narrative (paikka, aika, lääkäri)
        component5.getSection().setText(luoNarrativePaikkaPvmLaakari(alkuperainenLaakemaarays, today));

        component5.getSection().getComponents().add(luoLukituksenPurunSelitys(lukituksenPurku, effectiveTimeValue));
        return clinicalDocument;
    }

    /**
     * Luo lukituksen purun selitys osion
     *
     * @param lukituksenPurku
     *            LaakemaarayksenLukituksenPurkuTO josta selitys tiedo poimitaan
     * @param effectiveTimeValue
     *            String lukituksen purun päivämäärä sekunnin tarkkuudella
     * @return POCDMT000040Component5 johon lukituksen purun selitys osio on kasattu
     */
    private POCDMT000040Component5 luoLukituksenPurunSelitys(LaakemaarayksenLukituksenPurkuTO lukituksenPurku,
            String effectiveTimeValue) {
        POCDMT000040Component5 component = luoComponent(lukituksenPurku);
        POCDMT000040Entry entry = of.createPOCDMT000040Entry();
        component.getSection().getEntries().add(entry);
        entry.setAct(of.createPOCDMT000040Act());
        entry.getAct().setClassCode(XActClassDocumentEntryAct.ACT);
        entry.getAct().setMoodCode(XDocumentActMood.RQO);
        entry.getAct().setCode(of.createCD());
        fetchAttributes("lukituksenpurku.code", entry.getAct().getCode());
        entry.getAct().setText(of.createED());
        entry.getAct().getText().getContent().add(lukituksenPurku.getSelitys());
        entry.getAct().setEffectiveTime(of.createIVLTS());
        entry.getAct().getEffectiveTime().setValue(effectiveTimeValue);
        return component;
    }

    /**
     * Lisää clinicalDocumenttiin relateDocument elementin joissa viittaukset lukittuun laakemaaraykseen ja
     * lukitussanomaan joka halutaan purkaa
     *
     * @param clinicalDocument
     *            POCDMT00040ClinicalDocument clinicalDocument johon relatedDocument elementit lisätään
     * @param laakemaarays
     *            LaakemaaraysTO lukittu laakemaarays jonka lukitus halutaan purkaa
     * @param lukituksenPurku
     *            LaakemaarayksenLukituksenPurkuTO josta lukitussanoman tiedot poimitaan
     */
    private void addRelatedDocument(POCDMT000040ClinicalDocument clinicalDocument, LaakemaaraysTO laakemaarays,
            LaakemaarayksenLukituksenPurkuTO lukituksenPurku) {
        clinicalDocument.getRelatedDocuments().add(luoLinkitys(XActRelationshipDocument.APND, laakemaarays.getOid(),
                laakemaarays.getSetId(), ReseptisanomanTyyppi.LAAKEMAARAYS));
        clinicalDocument.getRelatedDocuments()
                .add(luoLinkitys(XActRelationshipDocument.RPLC, lukituksenPurku.getLukitussanomanOid(),
                        lukituksenPurku.getLukitussanomanSetId(), ReseptisanomanTyyppi.LAAKEMAARAYKSEN_LUKITUS));
    }

    /**
     * Luo relatedDocumet rakenteen annettujen tietojen pohjalta
     *
     * <pre>
     * &lt;relatedDocument typeCode="[typeCode]">
     *   &lt;parentDocument>
     *     &lt;id root="[oid]"/>
     *     &lt;code code="[tyyppi]"/>
     *     &lt;setId root="[setId]"/>
     *   &lt;/parentDocument>
     * &lt;/relatedDocument>
     * </pre>
     *
     * @param typeCode
     *            XActRelationshipDocument relatedDocument typeCode
     * @param oid
     *            String linkitetyn dokumentin oid
     * @param setId
     *            String linkitetyn dokumentin setId
     * @param tyyppi
     *            ReseptisanomanTyyppi linkitetyn dokumentin tyyppi
     * @return POCDMT000040RelatedDocument elementin johon linkitys on luotu
     */
    private POCDMT000040RelatedDocument luoLinkitys(XActRelationshipDocument typeCode, String oid, String setId,
            ReseptisanomanTyyppi tyyppi) {
        POCDMT000040RelatedDocument relatedDocument = of.createPOCDMT000040RelatedDocument();
        relatedDocument.setTypeCode(typeCode);
        relatedDocument.setParentDocument(of.createPOCDMT000040ParentDocument());
        II id = of.createII();
        id.setRoot(oid);
        relatedDocument.getParentDocument().getIds().add(id);
        relatedDocument.getParentDocument().setCode(of.createCD());
        fetchAttributes(String.format("%d.reseptisanomantyyppi", tyyppi.getTyyppi()),
                relatedDocument.getParentDocument().getCode());
        relatedDocument.getParentDocument().getCode().setCode(String.valueOf(tyyppi.getTyyppi()));
        relatedDocument.getParentDocument().setSetId(of.createII());
        relatedDocument.getParentDocument().getSetId().setRoot(setId);
        return relatedDocument;
    }

    /**
     * Kasaa reseptin lukituksen purkuasiakirjan konstruktorissa annetun LaakemaarayksenLukituksenPurkuTO:n tietojen
     * perusteella
     * 
     * @return String Reseptin lukituksen purkuasiakirja XML-muodossa
     * @throws JAXBException
     */
    @Override
    public String kasaaReseptiAsiakirja() throws JAXBException {
        return JaxbUtil.getInstance().marshalloi(kasaaReseptiCDA(), "urn:hl7-org:v3 CDA_Fi.xsd");
    }

    @Override
    protected POCDMT000040Entry luoAsiakirjanMuutTiedot(POCDMT000040Entry entry) {
        return entry;
    }

    @Override
    protected void luoAsiakirjakohtaisetRakenteet(LaakemaaraysTO laakemaarays) {
        // TODO Auto-generated method stub

    }

    @Override
    protected Collection<POCDMT000040Reference> luoViittaukset(LaakemaaraysTO laakemaarays) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Kasaa reseptin lukituksen purkuasiakirjan konstruktorissa annetun LaakemaarayksenLukituksenPurkuTO:n tietojen
     * perusteella
     * 
     * @return POCDMT000040ClinicalDocument Reseptin lukituksen purkuasiakirja JAXB-elementteinä
     */
    @Override
    public POCDMT000040ClinicalDocument kasaaReseptiCDA() {
        validoiLaakemaarays();
        return kasaaReseptinLukituksenPurku();
    }
}
