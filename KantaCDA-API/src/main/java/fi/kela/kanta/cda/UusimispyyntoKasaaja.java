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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import javax.xml.bind.JAXBException;

import org.codehaus.plexus.util.StringUtils;
import org.hl7.v3.AD;
import org.hl7.v3.AdxpCity;
import org.hl7.v3.AdxpPostalCode;
import org.hl7.v3.AdxpStreetAddressLine;
import org.hl7.v3.CE;
import org.hl7.v3.II;
import org.hl7.v3.ON;
import org.hl7.v3.POCDMT000040Act;
import org.hl7.v3.POCDMT000040Author;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component3;
import org.hl7.v3.POCDMT000040Component5;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040EntryRelationship;
import org.hl7.v3.POCDMT000040InformationRecipient;
import org.hl7.v3.POCDMT000040InfrastructureRootTemplateId;
import org.hl7.v3.POCDMT000040Participant2;
import org.hl7.v3.POCDMT000040PatientRole;
import org.hl7.v3.POCDMT000040RecordTarget;
import org.hl7.v3.POCDMT000040Reference;
import org.hl7.v3.POCDMT000040RelatedDocument;
import org.hl7.v3.POCDMT000040Subject;
import org.hl7.v3.ParticipationTargetSubject;
import org.hl7.v3.ST;
import org.hl7.v3.SXCMTS;
import org.hl7.v3.StrucDocText;
import org.hl7.v3.TEL;
import org.hl7.v3.XActClassDocumentEntryAct;
import org.hl7.v3.XActMoodDocumentObservation;
import org.hl7.v3.XActRelationshipDocument;
import org.hl7.v3.XActRelationshipEntryRelationship;
import org.hl7.v3.XDocumentActMood;
import org.hl7.v3.XDocumentSubject;
import org.hl7.v3.XDocumentSubstanceMood;

import fi.kela.kanta.cda.validation.UusimispyyntoValidoija;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.UusimispyyntoTO;
import fi.kela.kanta.util.JaxbUtil;
import fi.kela.kanta.util.KantaCDAUtil;

public class UusimispyyntoKasaaja extends ReseptiKasaaja {

    private static final String UUSITTAVAN_LÄÄKEMÄÄRÄYKSEN_TEKIJÄ = "Uusittavan lääkemääräyksen tekijä: ";
    private static final String UUSITTAVAN_VALMISTEEN_NIMI = "Uusittavan valmisteen nimi: ";
    private static final String UUSIJA = "Uusija: ";
    private static final String UUSIMISPYYNNÖN_LUONTI_AIKA = "Uusimispyynnön luonti aika: ";
    private final UusimispyyntoTO uusimispyynto;

    public UusimispyyntoKasaaja(Properties properties, UusimispyyntoTO uusimispyynto) {
        super(properties);

        this.uusimispyynto = uusimispyynto;
        validoija = new UusimispyyntoValidoija(uusimispyynto);
    }

    @Override
    protected String getTypeKey() {
        return "UUSIMIS";
    }

    /**
     * Kasaa uusimispyyntöasiakirjan konstruktorissa annetun UusimispyyntoTO:n tietojen perusteella.
     * 
     * @return Uusimispyyntöasiakirja JAXB-elementteinä
     */
    public POCDMT000040ClinicalDocument kasaaUusimispyynto() {
        validoiLaakemaarays();

        POCDMT000040ClinicalDocument doc = of.createPOCDMT000040ClinicalDocument();
        String effectiveTimeValue = getDateFormat().format(new Date());
        addIdFields(doc, uusimispyynto, effectiveTimeValue);
        addRecordTarget(doc, uusimispyynto.getHenkilotiedot());
        lisaaPuhnoRecordTargettiin(doc, uusimispyynto.getMatkapuhelinnumero());
        doc.getAuthors().add(luoAuthor(uusimispyynto));
        addCustodian(doc);
        doc.getInformationRecipients().add(luoInformationRecipient(uusimispyynto));
        addRelatedDocument(doc, uusimispyynto.getUusittavaLaakemaaraysOid(),
                uusimispyynto.getUusittavaLaakemaaraysSetId(),
                KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYS.getTyyppi() + ".reseptisanomantyyppi",
                XActRelationshipDocument.APND);

        // luoComponentOffin palvelutapahtumanOid voi lienee olla null koska kyseinen pakollinen vain Lääkemääräyksissä,
        // niiden korjauksissa ja mitätöinneissä jos ovat laadittu palvelutapahtuman yhteydessä.
        addComponentOf(doc, effectiveTimeValue, uusimispyynto.getLaatimispaikka(), null);

        addLocalHeader(doc);
        // Tarvitseeko / pitääkö uusimispyynnöllä olla tabelOfContents?
        if ( doc.getLocalHeader() != null && doc.getLocalHeader().getTableOfContents() != null
                && !doc.getLocalHeader().getTableOfContents().getContentsCodes().isEmpty() ) {
            doc.getLocalHeader().setTableOfContents(null);
        }

        // CDA Body
        doc.setComponent(of.createPOCDMT000040Component2());
        doc.getComponent().setStructuredBody(of.createPOCDMT000040StructuredBody());
        POCDMT000040Component3 component3 = of.createPOCDMT000040Component3();
        POCDMT000040InfrastructureRootTemplateId templateId = of.createPOCDMT000040InfrastructureRootTemplateId();
        fetchAttributes(ReseptiKasaaja.template_id, templateId);
        component3.getTemplateIds().add(templateId);
        component3.setSection(of.createPOCDMT000040Section());
        component3.getSection().setAttributeID(getNextOID(uusimispyynto));
        component3.getSection().setId(of.createII());
        component3.getSection().getId().setRoot(getId(uusimispyynto));
        component3.getSection().setCode(of.createCE());
        fetchAttributes("contentsCode", component3.getSection().getCode());
        component3.getSection().setTitle(of.createST());
        component3.getSection().getTitle().getContent().add(component3.getSection().getCode().getDisplayName());
        POCDMT000040Component5 component5a = of.createPOCDMT000040Component5();
        component5a.setSection(of.createPOCDMT000040Section());
        component5a.getSection().setAttributeID(getNextOID(uusimispyynto));
        component5a.getSection().setId(of.createII());
        component5a.getSection().getId().setRoot(getId(uusimispyynto));

        // näyttömuoto
        component5a.getSection().setText(luoNarrativeUusimispyynto(uusimispyynto));

        POCDMT000040Component5 component5b = of.createPOCDMT000040Component5();
        component5b.setSection(of.createPOCDMT000040Section());
        component5b.getSection().setAttributeID(getNextOID(uusimispyynto));
        component5b.getSection().setId(of.createII());
        component5b.getSection().getId().setRoot(getId(uusimispyynto));

        component5b.getSection().getEntries().add(luoPaaosa(uusimispyynto));
        component5b.getSection().getEntries().add(luoValimisteenNimiMaaraajaJaMaaraysPvm(uusimispyynto));

        component5a.getSection().getComponents().add(component5b);
        component3.getSection().getComponents().add(component5a);
        doc.getComponent().getStructuredBody().getComponents().add(component3);

        return doc;

    }

    /**
     * Luo uusumispyynnön narrative osion. Osioon tulee oma kappaleensa uusimispyynnön tiedoille.
     * 
     * @param uusimispyynto
     *            UusimispyyntoTO josta tiedot pomitaan.
     * @return StrucDocText elementti johon tiedot on sijoitettu.
     */
    private StrucDocText luoNarrativeUusimispyynto(UusimispyyntoTO uusimispyynto) {
        StrucDocText text = of.createStrucDocText();

        if ( null != uusimispyynto ) {
            if ( null != uusimispyynto.getLaatimispaikka()
                    && onkoNullTaiTyhja(uusimispyynto.getLaatimispaikka().getNimi()) ) {
                text.getContent().add(of
                        .createStrucDocItemParagraph(luoParagraphContent(uusimispyynto.getLaatimispaikka().getNimi())));
            }
            SimpleDateFormat sdfLong = new SimpleDateFormat("d.M.yyyy HH:mm");
            sdfLong.setTimeZone(TimeZone.getTimeZone("EET"));
            text.getContent().add(of.createStrucDocItemParagraph(
                    luoParagraphContent(UUSIMISPYYNNÖN_LUONTI_AIKA + sdfLong.format(new Date()))));
            if ( null != uusimispyynto.getUusija() && null != uusimispyynto.getUusija().getKokonimi() ) {
                text.getContent().add(of.createStrucDocItemParagraph(
                        luoParagraphContent(UUSIJA + uusimispyynto.getUusija().getKokonimi().getKokoNimi())));
            }
            text.getContent().add(of.createStrucDocItemParagraph(
                    luoParagraphContent(UUSITTAVAN_VALMISTEEN_NIMI + uusimispyynto.getValmisteenNimi())));
            text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent(
                    UUSITTAVAN_LÄÄKEMÄÄRÄYKSEN_TEKIJÄ + uusimispyynto.getMaaraajanKokonimi().getKokoNimi())));
        }
        return text;
    }

    private POCDMT000040Entry luoPaaosa(UusimispyyntoTO uusimispyynto) {
        POCDMT000040Entry entry = of.createPOCDMT000040Entry();
        entry.setAct(of.createPOCDMT000040Act());
        entry.getAct().setClassCode(XActClassDocumentEntryAct.ACT);
        entry.getAct().setMoodCode(XDocumentActMood.RQO);
        entry.getAct().setCode(of.createCD());
        fetchAttributes(String.valueOf(KantaCDAConstants.ReseptisanomanTyyppi.UUSIMISPYYNTO.getTyyppi())
                + ".reseptisanomantyyppi", entry.getAct().getCode());
        entry.getAct().setSubject(luoPotilaantiedot(uusimispyynto));
        entry.getAct().getParticipants().add(luoKohdeOrganisaatio(uusimispyynto));
        lisaaMuuttiedot(entry.getAct(), uusimispyynto);
        return entry;
    }

    /**
     * Luo entryn valmisteen nimelle, määräjälle ja määräys päivälle
     * 
     * <pre>
     * {@code
     *   <entry>
     *     <substanceAdministration classCode="SBADM" moodCode="EVN">
     *       <effectiveTime value="[AIKALEIMA]">
     *       <consumable>
     *         <manufacturedProduct>
     *           <manufacturedLabeledDrug>
     *             <name>[VALMISTEEN_NIMI]</name>
     *           </manufacturedLabeledDrug>
     *         </manufacturedProduct>
     *       </consumable>
     *       <author>
     *         <time />
     *         <assignedAuthor>
     *           <id extension="[MAARAAJAN_ID]" muut attribuutit keyllä "author.assignedAuthor.id" />
     *           <assignedPerson>
     *             <name>
     *               <given>[MAARAAJAN_ETUNIMI]</given>
     *               <family>[MAARAAJAN_SUKUNIMI]</family>
     *             </name>
     *           </assignedPerson>
     *         </assignedAuthor>
     *       </author>
     *     </substanceAdministration>
     *   </entry>
     * }
     * </pre>
     * 
     * @param uusimispyynto
     *            UusimispyyntoTO josta tiedot poimitaan
     */
    private POCDMT000040Entry luoValimisteenNimiMaaraajaJaMaaraysPvm(UusimispyyntoTO uusimispyynto) {
        POCDMT000040Entry entry = of.createPOCDMT000040Entry();
        entry.setSubstanceAdministration(of.createPOCDMT000040SubstanceAdministration());
        entry.getSubstanceAdministration().getClassCodes().add("SBADM");
        entry.getSubstanceAdministration().setMoodCode(XDocumentSubstanceMood.EVN);
        SXCMTS time = of.createSXCMTS();
        time.setValue(getDateFormat().format(uusimispyynto.getAikaleima()));
        entry.getSubstanceAdministration().getEffectiveTimes().add(time);
        entry.getSubstanceAdministration().setConsumable(of.createPOCDMT000040Consumable());
        entry.getSubstanceAdministration().getConsumable()
                .setManufacturedProduct(of.createPOCDMT000040ManufacturedProduct());
        entry.getSubstanceAdministration().getConsumable().getManufacturedProduct()
                .setManufacturedLabeledDrug(of.createPOCDMT000040LabeledDrug());
        entry.getSubstanceAdministration().getConsumable().getManufacturedProduct().getManufacturedLabeledDrug()
                .setName(of.createEN());
        entry.getSubstanceAdministration().getConsumable().getManufacturedProduct().getManufacturedLabeledDrug()
                .getName().getContent().add(uusimispyynto.getValmisteenNimi());
        POCDMT000040Author author = of.createPOCDMT000040Author();
        author.setAssignedAuthor(of.createPOCDMT000040AssignedAuthor());
        author.setTime(of.createTS());
        II authorId = of.createII();
        fetchAttributes("author.assignedAuthor.id", authorId);
        authorId.setExtension(uusimispyynto.getMaaraajanId());
        author.getAssignedAuthor().getIds().add(authorId);
        author.getAssignedAuthor().setAssignedPerson(of.createPOCDMT000040Person());
        author.getAssignedAuthor().getAssignedPerson().getNames().add(getNames(uusimispyynto.getMaaraajanKokonimi()));
        entry.getSubstanceAdministration().getAuthors().add(author);
        return entry;
    }

    /**
     * Lisää actiin entryRelationship rakenteet uusimispyynnön kirjaajan viestille ja suostumustyypille jos uusijaa ei
     * ole annettu viestiin sijoitetaan teksti "potilas itse", muuten ..jotain muuta TODO: mitä?
     * 
     * <pre>
     * {@code
     * <act>
     *   <entryRelationship typeCode="COMP">
     *     <observation classCode="OBS" moodCode="EVN">
     *       <code code="113" loput attribuutit preperykeyllä [113]>
     *       <value xsi:type="ST">[potilas itse || ???]</value>
     *     </observation>
     *   </entryRelationship>
     *   <entryRelationship typeCode="COMP">
     *     <observation classCode="OBS" moodCode="EVN">
     *       <code code="120" loput attribuutit preperykeyllä [120]>
     *       <value xsi:type="CE" code="[SUOSTUMUS]" loput attribuutit SUOSTUMS keyllä/>
     *     </observation>
     *   </entryRelationship>
     * </act>
     * }
     * </pre>
     * 
     * @param act
     *            POCDMT00040Act johon entryRelationShipit lisätään
     * @param uusimispyynto
     *            UusimispyyntoTO josta tiedot poimitaan
     */
    private void lisaaMuuttiedot(POCDMT000040Act act, UusimispyyntoTO uusimispyynto) {
        POCDMT000040EntryRelationship viesti = luoEntryRelationshipObservationCodeRakenne(
                KantaCDAConstants.Laakityslista.UUSIMISPYYNNON_KIRJAAJAN_ANTAMA_VIESTI);
        ST viestiValue = of.createST();
        if ( uusimispyynto.getUusija() == null ) {
            viestiValue.getContent().add("Potilas itse");
        }
        else {
            viestiValue.getContent().add("");
        }
        viesti.getObservation().getValues().add(viestiValue);
        act.getEntryRelationships().add(viesti);

        POCDMT000040EntryRelationship suostumus = luoEntryRelationshipObservationCodeRakenne(
                KantaCDAConstants.Laakityslista.UUSIMISPYYNNON_SUOSTUMUSTYYPPI);
        CE suostumusValue = of.createCE();
        fetchAttributes(String.valueOf(uusimispyynto.getSuostumus()) + ".suostumustyyppi", suostumusValue);
        suostumus.getObservation().getValues().add(suostumusValue);
        act.getEntryRelationships().add(suostumus);
    }

    /**
     * Apumetodi joka luo entryRelationship/Observation/Code rakenteen ja noutaa attribuutit codelle annetulla
     * codeKeyllä
     * 
     * <pre>
     * {@code
     * <entryRelationship typeCode="COMP">
     *   <observation classCode="OBS" moodCode="EVN">
     *     <code [attribuutit codeKeyllä]/>
     *   </observation>
     * </entryRelationship>
     * }
     * </pre>
     * 
     * @param codeKey
     *            String jolla code elementille haetaan attribuutteja
     * @return esitäytetty POCDMT000040EntryRelationship entryRelationship rakenne
     */
    private POCDMT000040EntryRelationship luoEntryRelationshipObservationCodeRakenne(String codeKey) {
        POCDMT000040EntryRelationship entryRelationship = of.createPOCDMT000040EntryRelationship();
        entryRelationship.setTypeCode(XActRelationshipEntryRelationship.COMP);
        entryRelationship.setObservation(of.createPOCDMT000040Observation());
        entryRelationship.getObservation().getClassCodes().add("OBS");
        entryRelationship.getObservation().setMoodCode(XActMoodDocumentObservation.EVN);
        entryRelationship.getObservation().setCode(of.createCD());
        fetchAttributes(codeKey, entryRelationship.getObservation().getCode());
        return entryRelationship;
    }

    /**
     * Luo kohde organisaation participantin uusimispyntoTOn pohjalta
     * 
     * <pre>
     * {@code
     *   <participant typeCode="DIR">
     *     <participantRole classCode="ROL">
     *       <!--addr lisätään jos vastaanottajan katu, postinumero tai kaupunki on annettu-->
     *       <!-- ja addr:n alle vastaavasti lisätään kyseinen elementti jos se on uusimispyyntoTO:ssa annettu -->
     *       <addr>
     *         <streetAddressLine>[VASTAANOTTAJA_KATU]</streetAddressLine>
     *         <postalCode>[VASTAANOTTAJA_POSTINUMERO]</postalCode>
     *         <city>[VASTAANOTTAJA_KAUPUNKI]</city>
     *       </addr>
     *       <!--telecom lisätään jos vastaanottaja matkapuhelinnumero on annettu-->
     *       <telecom use="MC" value="[VASTAANOTTAJA_MATKAPUHELINNUMERO]" />
     *       <playingEntity/>
     *       <scopingEntity classCode="ORG">
     *         <id root="[VASTAANOTTAJA_ID]" />
     *         <desc>[VASTAANOTTAJA_NIMI]</desc>
     *       </scopingEntity>
     *     </participantRole>
     *   </participant>
     * }
     * </pre>
     * 
     * @param act
     * @param uusimispyynto
     */
    private POCDMT000040Participant2 luoKohdeOrganisaatio(UusimispyyntoTO uusimispyynto) {
        POCDMT000040Participant2 participant = of.createPOCDMT000040Participant2();
        participant.getTypeCodes().add("DIR");
        participant.setParticipantRole(of.createPOCDMT000040ParticipantRole());
        participant.getParticipantRole().getClassCodes().add("ROL");
        if ( !onkoNullTaiTyhja(uusimispyynto.getVastaanottajaKatu())
                || !onkoNullTaiTyhja(uusimispyynto.getVastaanottajaPostinumero())
                || !onkoNullTaiTyhja(uusimispyynto.getVastaanottajaKaupunki()) ) {
            AD addr = of.createAD();
            if ( !onkoNullTaiTyhja(uusimispyynto.getVastaanottajaKatu()) ) {
                AdxpStreetAddressLine streetAddressLine = of.createAdxpStreetAddressLine();
                streetAddressLine.getContent().add(uusimispyynto.getVastaanottajaKatu());
                addr.getContent().add(of.createADStreetAddressLine(streetAddressLine));
            }
            if ( !onkoNullTaiTyhja(uusimispyynto.getVastaanottajaPostinumero()) ) {
                AdxpPostalCode postalCode = of.createAdxpPostalCode();
                postalCode.getContent().add(uusimispyynto.getVastaanottajaPostinumero());
                addr.getContent().add(of.createADPostalCode(postalCode));
            }
            if ( !onkoNullTaiTyhja(uusimispyynto.getVastaanottajaKaupunki()) ) {
                AdxpCity city = of.createAdxpCity();
                city.getContent().add(uusimispyynto.getVastaanottajaKaupunki());
                addr.getContent().add(of.createADCity(city));
            }
            participant.getParticipantRole().getAddrs().add(addr);
        }

        if ( !onkoNullTaiTyhja(uusimispyynto.getVastaanottajaPuhelinnumero()) ) {
            TEL puhnro = of.createTEL();
            puhnro.getUses().add("DIR");
            puhnro.setValue(
                    muodostaTelecomValue(uusimispyynto.getVastaanottajaPuhelinnumero(), KantaCDAConstants.TEL_PREFIX));
            participant.getParticipantRole().getTelecoms().add(puhnro);
        }

        participant.getParticipantRole().setPlayingEntity(of.createPOCDMT000040PlayingEntity());

        participant.getParticipantRole().setScopingEntity(of.createPOCDMT000040Entity());
        participant.getParticipantRole().getScopingEntity().getClassCodes().add("ORG");
        II id = of.createII();
        id.setRoot(uusimispyynto.getVastaanottajaId());
        participant.getParticipantRole().getScopingEntity().getIds().add(id);
        participant.getParticipantRole().getScopingEntity().setDesc(of.createED());
        participant.getParticipantRole().getScopingEntity().getDesc().getContent()
                .add(uusimispyynto.getVastaanottajaNimi());
        return participant;
    }

    /**
     * Luo potilaan tiedot subjectin annetusta uusimispyyntoTOsta
     * 
     * <pre>
     * {@code
     *   <subject typecode="SBJ">
     *     <relatedSubject classCode="PAT">
     *       <code code="[HETU]" loput attribuutit [subject.relatedSubject.code] />
     *       <!--jos matkapuhelinumero on annettu lisätään telecoms-->
     *       <telecom use="MC" value="[MATKAPUHELINNUMERO]" />
     *       <subject classCode="PSN">
     *         <name>
     *           <given>[ETUNIMI]</given>
     *           <family>[SUKUNIMI]</family>
     *         </name>
     *         <birthTime value="[BIRTHTIME]"/>
     *       </subject>
     *     </relatedSubject>
     *   </subject>
     * }
     * </pre>
     * 
     * @param uusimispyynto
     *            UusimispyyntoTO josta potilaan tiedot poimitaan
     */
    private POCDMT000040Subject luoPotilaantiedot(UusimispyyntoTO uusimispyynto) {
        POCDMT000040Subject subject = of.createPOCDMT000040Subject();
        subject.setTypeCode(ParticipationTargetSubject.SBJ);
        subject.setRelatedSubject(of.createPOCDMT000040RelatedSubject());
        subject.getRelatedSubject().setClassCode(XDocumentSubject.PAT);
        CE relatedSubjectCode = of.createCE();
        fetchAttributes("subject.relatedSubject.code", relatedSubjectCode);
        relatedSubjectCode.setCode(uusimispyynto.getHenkilotiedot().getHetu());
        subject.getRelatedSubject().setCode(relatedSubjectCode);

        if ( !onkoNullTaiTyhja(uusimispyynto.getMatkapuhelinnumero()) ) {
            TEL puhnro = of.createTEL();
            puhnro.getUses().add("MC");
            puhnro.setValue(muodostaTelecomValue(uusimispyynto.getMatkapuhelinnumero(), KantaCDAConstants.TEL_PREFIX));
            subject.getRelatedSubject().getTelecoms().add(puhnro);
        }
        subject.getRelatedSubject().setSubject(of.createPOCDMT000040SubjectPerson());
        subject.getRelatedSubject().getSubject().getClassCodes().add("PSN");
        subject.getRelatedSubject().getSubject().getNames().add(getNames(uusimispyynto.getHenkilotiedot().getNimi()));
        subject.getRelatedSubject().getSubject().setBirthTime(of.createTS());
        subject.getRelatedSubject().getSubject().getBirthTime()
                .setValue(KantaCDAUtil.hetuToBirthTime(uusimispyynto.getHenkilotiedot().getHetu()));
        return subject;
    }

    /**
     * Luo informationRecipent rakenteen johon sijoitetaan uusimispyynnön vastaanottajan id ja nimi
     * 
     * <pre>
     *  {@code
     *  <informationRecipient>
     *    <intendedRecipient>
     *      <receivedOrganization>
     *        <id root="[VASTAANOTTAJA_ID]" />
     *        <name>[VASTAANOTTAJA_NIMI]</name>
     *      </receivedOrganization>
     *    </intendedRecipient>
     *  </informationRecipient>
     *  }
     * </pre>
     * 
     * @param uusimispyynto
     *            UusimispyyntoTO josta vastaanottajan tiedot poimitaan
     */
    private POCDMT000040InformationRecipient luoInformationRecipient(UusimispyyntoTO uusimispyynto) {
        POCDMT000040InformationRecipient informationRecipient = of.createPOCDMT000040InformationRecipient();
        informationRecipient.setIntendedRecipient(of.createPOCDMT000040IntendedRecipient());
        informationRecipient.getIntendedRecipient().setReceivedOrganization(of.createPOCDMT000040Organization());

        II orgId = of.createII();
        orgId.setRoot(uusimispyynto.getVastaanottajaId());

        ON name = of.createON();
        name.getContent().add(uusimispyynto.getVastaanottajaNimi());

        informationRecipient.getIntendedRecipient().getReceivedOrganization().getIds().add(orgId);
        informationRecipient.getIntendedRecipient().getReceivedOrganization().getNames().add(name);
        return informationRecipient;
    }

    /**
     * Luo uusimispyyntoTOn tietojen pohjalta author/assignedAuthor/representedOrganization rakenteen:
     * 
     * <pre>
     * {@code
     * <author [nullFlavor="NA" || typeCode="AUT"]>
     * 	<time nullFlavor="NA"/>
     * 	<assignedAuthor>
     * 		<id [nullFlavor="NA" || root="1.2.246.537.26" extension=UUSIJAN_SV_NUMERO]/>
     * 			<representedOrganization>
     * 				<id root=author.assignedAuthor.representedOrganization.id.root/>
     * 			</representedOrganization>
     * 	</assignedAutor>
     * </author>
     * }
     * </pre>
     * 
     * Jos uusija on annettu asetetaan author elementille typeCode attribuuttin arvoksi AUT muuten nullFlavor arvoksi NA
     * Jos uusija on annettu asetetaan suthor/assigendAuthor/id elementille root attribuutti ja extensioniksi uusijan
     * svnumero muuten nullFalvor arvoksi NA
     * 
     * 
     * @param uusimispyynto
     *            UusimispyytntoTO josta author tiedot poimitaan jos uusijaa ei ole asetettu asetetaan nullFlavor
     *            attribuutit
     */
    private POCDMT000040Author luoAuthor(UusimispyyntoTO uusimispyynto) {
        POCDMT000040Author author = of.createPOCDMT000040Author();
        if ( uusimispyynto.getUusija() == null ) {
            // TEsti
            POCDMT000040ClinicalDocument doc = of.createPOCDMT000040ClinicalDocument();
            addAuthor(doc);
            author = doc.getAuthors().get(0);

            // representedOrganization
            author.getAssignedAuthor().setRepresentedOrganization(of.createPOCDMT000040Organization());
            II orgId = of.createII();
            fetchAttributes("author.assignedAuthor.representedOrganization.id", orgId);
            author.getAssignedAuthor().getRepresentedOrganization().getIds().add(orgId);
        }
        else {
            author = luoAuthor(uusimispyynto.getUusija());
            author.getTypeCodes().add("AUT");
        }
        return author;
    }

    /**
     * Lisää matkapuhelinnumeron clinicalDocument/recordTarget/patientRole/telecom elementtin jos annettu numero ei ole
     * tyhjä tai null ja cda/recordTarget/patientRole/patient on asetettu
     * 
     * @param doc
     *            POCDMT000040ClinicalDocument johon puh. sijoitetaan jos ehdot täyttyvät
     * @param matkapuhelinnumero
     *            String matkapuhelinnumero joka telecom elementtiin sijoitetaan
     */
    private void lisaaPuhnoRecordTargettiin(POCDMT000040ClinicalDocument doc, String matkapuhelinnumero) {
        if ( !onkoNullTaiTyhja(matkapuhelinnumero) ) {
            POCDMT000040PatientRole patientRole = getRecordTargetPatientRole(doc);
            if ( null != patientRole ) {
                TEL puhelinnumero = of.createTEL();
                puhelinnumero.setValue(muodostaTelecomValue(matkapuhelinnumero, KantaCDAConstants.TEL_PREFIX));
                puhelinnumero.getUses().add("MC");
                patientRole.getTelecoms().add(puhelinnumero);
            }
        }

    }

    /**
     * Apumetodi joka palauttaa clinicalDocument/recordTarget/patientRole elementin jos patientRole elementtin luotu ja
     * sen patient elementti luotu
     * 
     * @param doc
     *            POCDMT000040ClinicalDocument josta patientRolea etsitään
     * @return POCDMT000040PatientRole elementti jos löytyi, muuten null
     */
    private POCDMT000040PatientRole getRecordTargetPatientRole(POCDMT000040ClinicalDocument doc) {
        if ( doc == null || doc.getRecordTargets().isEmpty() ) {
            return null;
        }
        for (POCDMT000040RecordTarget recordTarget : doc.getRecordTargets()) {
            POCDMT000040PatientRole patientRole = recordTarget.getPatientRole();
            if ( null != patientRole && null != patientRole.getPatient() ) {
                return patientRole;
            }
        }
        return null;
    }

    @Override
    public String kasaaReseptiAsiakirja() throws JAXBException {
        return JaxbUtil.getInstance().marshalloi(kasaaReseptiCDA(), "urn:hl7-org:v3 CDA_Fi.xsd");
    }

    @Override
    protected POCDMT000040Entry luoAsiakirjanMuutTiedot(POCDMT000040Entry entry) {
        // Tämä tila arkoituksella tyhjä
        return null;
    }

    @Override
    protected void luoAsiakirjakohtaisetRakenteet(LaakemaaraysTO laakemaarays) {
        // Tämä tila arkoituksella tyhjä
    }

    @Override
    protected Collection<POCDMT000040Reference> luoViittaukset(LaakemaaraysTO laakemaarays) {
        // Tämä tila arkoituksella tyhjä
        return new ArrayList<POCDMT000040Reference>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.kela.kanta.cda.Kasaaja#addRelatedDocument(org.hl7.v3.POCDMT000040ClinicalDocument, java.lang.String,
     * java.lang.String, java.lang.String, org.hl7.v3.XActRelationshipDocument)
     */
    @Override
    protected void addRelatedDocument(POCDMT000040ClinicalDocument clinicalDocument, String oid, String setid,
            String propertycode, XActRelationshipDocument relationType) {
        POCDMT000040RelatedDocument relatedDocument = of.createPOCDMT000040RelatedDocument();

        relatedDocument.setTypeCode(relationType);
        relatedDocument.setParentDocument(of.createPOCDMT000040ParentDocument());
        relatedDocument.getParentDocument().getIds().add(of.createII());
        relatedDocument.getParentDocument().getIds().get(0).setRoot(oid);
        relatedDocument.getParentDocument().setCode(of.createCE());
        fetchAttributes(propertycode, relatedDocument.getParentDocument().getCode());
        clinicalDocument.getRelatedDocuments().add(relatedDocument);

        if ( !StringUtils.isEmpty(setid) ) {
            relatedDocument.getParentDocument().setSetId(of.createII());
            relatedDocument.getParentDocument().getSetId().setRoot(setid);
        }
    }

    /**
     * Kasaa uusimispyyntöasiakirjan konstruktorissa annetun UusimispyyntoTO:n tietojen perusteella.
     * 
     * @return Uusimispyyntöasiakirja JAXB-elementteinä
     */
    @Override
    public POCDMT000040ClinicalDocument kasaaReseptiCDA() {
        return kasaaUusimispyynto();
    }
}
