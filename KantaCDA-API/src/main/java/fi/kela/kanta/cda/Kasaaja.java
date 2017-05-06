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

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.TimeZone;

import org.codehaus.plexus.util.StringUtils;
import org.hl7.v3.AD;
import org.hl7.v3.AdxpCity;
import org.hl7.v3.AdxpPostBox;
import org.hl7.v3.AdxpPostalCode;
import org.hl7.v3.AdxpStreetAddressLine;
import org.hl7.v3.CD;
import org.hl7.v3.CR;
import org.hl7.v3.CV;
import org.hl7.v3.EnDelimiter;
import org.hl7.v3.EnFamily;
import org.hl7.v3.EnGiven;
import org.hl7.v3.EnPrefix;
import org.hl7.v3.EnSuffix;
import org.hl7.v3.II;
import org.hl7.v3.MO;
import org.hl7.v3.ON;
import org.hl7.v3.ObjectFactory;
import org.hl7.v3.PN;
import org.hl7.v3.POCDMT000040AssignedCustodian;
import org.hl7.v3.POCDMT000040AssignedEntity;
import org.hl7.v3.POCDMT000040Author;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component5;
import org.hl7.v3.POCDMT000040EncompassingEncounter;
import org.hl7.v3.POCDMT000040HealthCareFacility;
import org.hl7.v3.POCDMT000040InfrastructureRootTemplateId;
import org.hl7.v3.POCDMT000040Organization;
import org.hl7.v3.POCDMT000040OrganizationPartOf;
import org.hl7.v3.POCDMT000040Patient;
import org.hl7.v3.POCDMT000040RecordTarget;
import org.hl7.v3.POCDMT000040RelatedDocument;
import org.hl7.v3.TEL;
import org.hl7.v3.XActRelationshipDocument;

import fi.kela.kanta.interfaces.Osoite;
import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.LeimakentatTO;
import fi.kela.kanta.to.NimiTO;
import fi.kela.kanta.to.OrganisaatioTO;
import fi.kela.kanta.util.OidGenerator;
import hl7finland.LocalHeader;
import hl7finland.LocalHeader.SoftwareSupport;

public abstract class Kasaaja {

    public static final String TIME_ZONE = "EET";

    public static final String BASE_OID_PROPERTY = "base.oid";
    public static final String LM_PROPERTY_PREFIX = "LM";
    public static final String LM_CONTENTS = "contentsCode";
    /**
     * Avain jota käytetään property-tiedostosta code arvojen hakuun. Esim. 'LM.uusimispyynto.code'.
     */
    public static final String LM_UUSIMISPYYNTO = "uusimispyynto";
    private long sequence = 0;

    // http://koodistopalvelu.kanta.fi/codeserver/pages/code-list-page.xhtml?versionKey=347
    // TODO: Olemassa HenkilotietoTarkennin luokka joka tekee samaa asiaa
    public enum MaaraajanRooli {
        KORJAAJA("KOR", "korjaus"),
        LAAK_ALOIT_HENK("LAL", "uusiminen"),
        MITATOIJA("MIT", "mitatointi"),
        KIRJAAJA("KIR", "kirjaus");

        private final String rooli;
        private String propertyAvaimenOsa;

        MaaraajanRooli(String rooli, String propertyAvaimenOsa) {
            this.rooli = rooli;
            this.propertyAvaimenOsa = propertyAvaimenOsa;
        }

        public String getRooliKoodi() {
            return rooli;
        }

        public String getPropertyAvaimenOsa() {
            return propertyAvaimenOsa;
        }
    }

    private final OidGenerator generator;
    private String documentId;
    private String lastOid;
    private final Properties properties;
    private int sectionId = 100;

    protected ObjectFactory of;

    public Kasaaja(Properties properties) {
        this.properties = properties;
        generator = OidGenerator.getInstance();
        OidGenerator.setBaseOid(properties.getProperty(Kasaaja.BASE_OID_PROPERTY));
        of = new ObjectFactory();
    }

    protected final SimpleDateFormat getDateFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone(Kasaaja.TIME_ZONE));
        return sdf;
    }

    protected final SimpleDateFormat getShortDateFormat() {
        SimpleDateFormat shortSDF = new SimpleDateFormat("yyyyMMdd");
        shortSDF.setTimeZone(TimeZone.getTimeZone(Kasaaja.TIME_ZONE));
        return shortSDF;
    }

    protected final SimpleDateFormat getTodayDateFormat() {
        SimpleDateFormat todaySDF = new SimpleDateFormat("d.M.yyyy");
        todaySDF.setTimeZone(TimeZone.getTimeZone(Kasaaja.TIME_ZONE));
        return todaySDF;
    }

    protected synchronized String getId(LeimakentatTO<?> leimakentat) {

        if ( StringUtils.isEmpty(lastOid) ) {
            lastOid = generator.createNewOidManualSequence(getDocumentId(leimakentat), getSequence());
        }
        return lastOid;
    }

    protected String getOID(LeimakentatTO<?> leimakentat) {
        return "OID" + getId(leimakentat);
    }

    protected synchronized String getNextId(LeimakentatTO<?> leimakentat) {
        lastOid = generator.createNewOidManualSequence(getDocumentId(leimakentat), getSequence());
        return lastOid;
    }

    protected String getNextOID(LeimakentatTO<?> leimakentat) {
        return "OID" + getNextId(leimakentat);
    }

    protected String getNextObservationID(LeimakentatTO<?> leimakentat) {

        StringBuilder obsId = new StringBuilder(getDocumentId(leimakentat));
        obsId.append(".3.1.").append(sectionId++);
        return obsId.toString();
    }

    protected String getDocumentId(final LeimakentatTO<?> leimakentat) {

        if ( StringUtils.isEmpty(documentId) ) {
            documentId = generator.createNewDocumentOid(leimakentat.getCDAOidBase());
        }
        return documentId;
    }

    protected String getTypeKey() {
        return Kasaaja.LM_PROPERTY_PREFIX;
    }

    protected void addIdFields(POCDMT000040ClinicalDocument clinicalDocument, LeimakentatTO<?> leimakentat,
            String effectiveTimeValue) {
        // RealmCode
        clinicalDocument.getRealmCodes().add(of.createCS());
        fetchAttributes("realmCode", clinicalDocument.getRealmCodes().get(0));

        // TypeId
        clinicalDocument.setTypeId(of.createPOCDMT000040InfrastructureRootTypeId());
        fetchAttributes("typeId", clinicalDocument.getTypeId());

        // TemplateId
        if ( (leimakentat == null) || leimakentat.getTemplateIds().isEmpty() ) {
            POCDMT000040InfrastructureRootTemplateId templateIdElement = of
                    .createPOCDMT000040InfrastructureRootTemplateId();
            fetchAttributes("templateId1", templateIdElement);
            clinicalDocument.getTemplateIds().add(templateIdElement);

            // templateId 2 ja 3
            String templateId2 = getProperty("templateId2.root");
            if ( !onkoNullTaiTyhja(templateId2) ) {
                POCDMT000040InfrastructureRootTemplateId tempateIdElement2 = of
                        .createPOCDMT000040InfrastructureRootTemplateId();
                fetchAttributes("templateId2", tempateIdElement2);
                clinicalDocument.getTemplateIds().add(tempateIdElement2);
                String templateId3 = getProperty("templateId3.root");
                if ( !onkoNullTaiTyhja(templateId3) ) {
                    POCDMT000040InfrastructureRootTemplateId temptaeIdElement3 = of
                            .createPOCDMT000040InfrastructureRootTemplateId();
                    fetchAttributes("templateId3", temptaeIdElement3);
                    clinicalDocument.getTemplateIds().add(temptaeIdElement3);
                }
            }

        }
        else {
            for (String templateId : leimakentat.getTemplateIds()) {
                POCDMT000040InfrastructureRootTemplateId templateIdElement = of
                        .createPOCDMT000040InfrastructureRootTemplateId();
                templateIdElement.setRoot(templateId);
                clinicalDocument.getTemplateIds().add(templateIdElement);
            }
        }

        // Id
        clinicalDocument.setId(of.createII());
        if ( leimakentat.getOid() == null ) {
            clinicalDocument.getId().setRoot(getDocumentId(leimakentat));
            leimakentat.setOid(getDocumentId(leimakentat));
        }
        else {
            clinicalDocument.getId().setRoot(leimakentat.getOid());
        }

        // Code
        clinicalDocument.setCode(of.createCE());
        fetchAttributes("code", clinicalDocument.getCode());

        // Title
        clinicalDocument.setTitle(of.createST());
        clinicalDocument.getTitle().getContent().add(getProperty("title"));

        // EffectiveTime
        clinicalDocument.setEffectiveTime(of.createTS());
        clinicalDocument.getEffectiveTime().setValue(effectiveTimeValue);

        // ConfidentialityCode
        clinicalDocument.setConfidentialityCode(of.createCE());
        fetchAttributes("confidentialityCode", clinicalDocument.getConfidentialityCode());

        // LanguageCode
        clinicalDocument.setLanguageCode(of.createCS());
        fetchAttributes("languageCode", clinicalDocument.getLanguageCode());

        // SetId
        clinicalDocument.setSetId(of.createII());
        if ( !onkoNullTaiTyhja(leimakentat.getSetId()) ) {
            clinicalDocument.getSetId().setRoot(leimakentat.getSetId());
        }
        else {
            // TODO: Onko oikein?
            clinicalDocument.getSetId().setRoot(getDocumentId(leimakentat));
        }

        // VersionNumber
        clinicalDocument.setVersionNumber(of.createINT());
        clinicalDocument.getVersionNumber().setValue(BigInteger.valueOf(((long) leimakentat.getVersio()) + 1));
    }

    protected void addRecordTarget(POCDMT000040ClinicalDocument clinicalDocument, HenkilotiedotTO henkilotiedot) {
        POCDMT000040RecordTarget recordTarget = of.createPOCDMT000040RecordTarget();
        recordTarget.setPatientRole(of.createPOCDMT000040PatientRole());
        II idElement = of.createII();
        if ( !onkoNullTaiTyhja(henkilotiedot.getHetu()) ) {
            fetchAttributes("recordTarget.patientRole.id", idElement);
            idElement.setExtension(henkilotiedot.getHetu());
        }
        else {
            idElement.getNullFlavors().add(KantaCDAConstants.NullFlavor.UNK.getCode());
        }
        recordTarget.getPatientRole().getIds().add(idElement);
        POCDMT000040Patient patient = of.createPOCDMT000040Patient();
        recordTarget.getPatientRole().setPatient(patient);
        patient.getNames().add(getNames(henkilotiedot.getNimi()));
        patient.setAdministrativeGenderCode(of.createCE());
        fetchAttributes("recordTarget.patientRole.patient.administrativeGenderCode",
                patient.getAdministrativeGenderCode());
        patient.getAdministrativeGenderCode().setCode(String.valueOf(henkilotiedot.getSukupuoli()));
        patient.getAdministrativeGenderCode()
                .setDisplayName(fetchProperty(henkilotiedot.getSukupuoli() + ".sukupuoli.displayName"));
        patient.setBirthTime(of.createTS());
        patient.getBirthTime().setValue(henkilotiedot.getSyntymaaika());
        clinicalDocument.getRecordTargets().add(recordTarget);
    }

    protected void addAuthor(POCDMT000040ClinicalDocument clinicalDocument) {
        POCDMT000040Author author = of.createPOCDMT000040Author();
        clinicalDocument.getAuthors().add(author);
        author.getNullFlavors().add("NA");
        author.setTime(of.createTS());
        author.getTime().getNullFlavors().add("NA");
        author.setAssignedAuthor(of.createPOCDMT000040AssignedAuthor());
        author.getAssignedAuthor().getIds().add(of.createII());
        author.getAssignedAuthor().getIds().get(0).getNullFlavors().add("NA");
    }

    protected void addAuthor(POCDMT000040ClinicalDocument clinicalDocument, POCDMT000040Author author) {
        clinicalDocument.getAuthors().add(author);
    }

    protected POCDMT000040Author luoAuthor(AmmattihenkiloTO ammattihenkilo) {
        POCDMT000040Author author = of.createPOCDMT000040Author();
        // Ammattihenkilön rooli
        author.setFunctionCode(of.createCE());
        if ( !onkoNullTaiTyhja(ammattihenkilo.getRooli()) ) {
            fetchAttributes(ammattihenkilo.getRooli() + ".author.functionCode", author.getFunctionCode());
            author.getFunctionCode().setCode(ammattihenkilo.getRooli());
        }
        else {
            fetchAttributes("author.functionCode", author.getFunctionCode());
            author.getFunctionCode().getNullFlavors().add("NI");
        }

        addAuthor(ammattihenkilo, author);

        if ( null != ammattihenkilo.getOrganisaatio() ) {
            OrganisaatioTO org = ammattihenkilo.getOrganisaatio();
            // Organisaatio
            author.getAssignedAuthor().setRepresentedOrganization(luoOrganization(org));
            if ( org.getToimintaYksikko() != null ) {
                addOrganizationPartOf(author.getAssignedAuthor().getRepresentedOrganization(), org);
            }
        }
        return author;
    }

    /**
     * Luo POCDMT000040Organization elementin OrganisaatioTOn pohjalta
     *
     * @param organisaatio
     *            OrganisaatioTO josta tiedot haetaan
     * @return POCDMT000040Organization elementti johon tiedot on sijoitettu
     */
    private POCDMT000040Organization luoOrganization(OrganisaatioTO organisaatio) {
        POCDMT000040Organization organization = of.createPOCDMT000040Organization();
        II organizationId = of.createII();
        organizationId.setRoot(organisaatio.getYksilointitunnus());
        organization.getIds().add(organizationId);
        organization.getNames().add(of.createON());
        organization.getNames().get(0).getContent().add(organisaatio.getNimi());
        if ( organisaatio.getPuhelinnumero() != null ) {
            TEL puhelinnumero = of.createTEL();
            puhelinnumero.setValue(muodostaTelecomValue(organisaatio.getPuhelinnumero(), KantaCDAConstants.TEL_PREFIX));
            if ( !onkoNullTaiTyhja(organisaatio.getPuhelinumeroKayttotarkoitus()) ) {
                puhelinnumero.getUses().add(organisaatio.getPuhelinumeroKayttotarkoitus());
            }
            else {
                puhelinnumero.getUses().add("DIR");
            }
            organization.getTelecoms().add(puhelinnumero);
        }
        if ( !onkoNullTaiTyhja(organisaatio.getSahkoposti()) ) {
            TEL sahkoposti = of.createTEL();
            sahkoposti.setValue(muodostaTelecomValue(organisaatio.getSahkoposti(), KantaCDAConstants.EMAIL_PREFIX));
            organization.getTelecoms().add(sahkoposti);
        }
        if ( organisaatio.getOsoite() != null ) {
            organization.getAddrs().add(luoAddress(organisaatio.getOsoite()));
        }
        return organization;
    }

    /**
     * Luo osoite(addr) elementin annetun OsoiteTOn pohjalta luo seuraavan rakenteen: <addr>
     * <streetAddressLine>osoite.katuosoite</streetAddressline> <postalCode>ososite.postinumero</postalCode>
     * <city>osoite.postitoimipaikka</city> </addr>
     *
     * @param osoite
     *            OsoiteTO josta tiedot poimittaan
     * @return AD johon tiedot on sijoitettu
     */
    private AD luoAddress(Osoite osoite) {
        AD addr = of.createAD();
        AdxpStreetAddressLine streetAddressLine = of.createAdxpStreetAddressLine();
        AdxpPostalCode postalCode = of.createAdxpPostalCode();
        AdxpCity city = of.createAdxpCity();
        streetAddressLine.getContent().add(osoite.getKatuosoite());
        postalCode.getContent().add(osoite.getPostinumero());
        city.getContent().add(osoite.getPostitoimipaikka());
        addr.getContent().add(of.createADStreetAddressLine(streetAddressLine));
        addr.getContent().add(of.createADPostalCode(postalCode));
        addr.getContent().add(of.createADCity(city));
        return addr;
    }

    /**
     * Apumetodi joka muodostaa telecom elementtiin sijoitettavan merkkijonon Tarkistaa ja lisää tarvittaessa annetun
     * etuliitteen annettuun merkkijonoon
     *
     * @param telecomvalue
     *            String joka halutaan lisätä telecom elementtiin
     * @param prefix
     *            String etuliite joka telecomvaluella tulee olla
     * @return String jossa annettu etuliite mukana
     */
    protected String muodostaTelecomValue(String telecomvalue, String prefix) {

        if ( telecomvalue == null ) {
            return prefix;
        }

        String modified = telecomvalue.replaceAll("\\s", "");

        if ( modified.startsWith(prefix) ) {
            return telecomvalue;
        }
        return prefix + modified;
    }

    protected void addCustodian(POCDMT000040ClinicalDocument clinicalDocument) {
        clinicalDocument.setCustodian(of.createPOCDMT000040Custodian());
        POCDMT000040AssignedCustodian assignedCustodian = of.createPOCDMT000040AssignedCustodian();
        clinicalDocument.getCustodian().setAssignedCustodian(assignedCustodian);
        assignedCustodian.setRepresentedCustodianOrganization(of.createPOCDMT000040CustodianOrganization());
        assignedCustodian.getRepresentedCustodianOrganization().getIds().add(of.createII());
        fetchAttributes("custodian.assignedCustodian.representedCustodianOrganization.id",
                assignedCustodian.getRepresentedCustodianOrganization().getIds().get(0));
        assignedCustodian.getRepresentedCustodianOrganization().setName(of.createON());
        String name = getProperty("custodian.assignedCustodian.representedCustodianOrganization.name");
        if ( !onkoNullTaiTyhja(name) ) {
            assignedCustodian.getRepresentedCustodianOrganization().getName().getContent().add(name);
        }
        AD addr = of.createAD();
        addr.getUses().add("PST");
        AdxpPostBox postBox = of.createAdxpPostBox();
        AdxpPostalCode postalCode = of.createAdxpPostalCode();
        AdxpCity city = of.createAdxpCity();
        postBox.getContent().add(getProperty("custodian.assignedCustodian.representedCustodianOrganization.postBox"));
        postalCode.getContent()
                .add(getProperty("custodian.assignedCustodian.representedCustodianOrganization.postNumber"));
        city.getContent().add(getProperty("custodian.assignedCustodian.representedCustodianOrganization.city"));
        addr.getContent().add(of.createADPostBox(postBox));
        addr.getContent().add(of.createADPostalCode(postalCode));
        addr.getContent().add(of.createADCity(city));
        assignedCustodian.getRepresentedCustodianOrganization().setAddr(addr);
    }

    /**
     * Lisää clinicalDocumentiin relatedDocument rakenteen johon sijoitetaan annetut oid ja setId ja haetaan code
     * elemettiin arvot annetulla propertycodella <relatedDocument typeCode="RPLC/APND/..."> <parentDocument>
     * <id root="[oid]"/> <code code="{propertycode.code}" codeSystem="{propertycode.codeSystem}" codesystemName=
     * "{propertycode.codeSystemName}" displayName= "{propertycode.displayName}"/> <setId root="[setId]"/>
     * </parentDocument> </relatedDocument>
     *
     * @param clinicalDocument
     *            POCDMT00040ClinicalDocument johon relatedDocument elementti lisätään
     * @param oid
     *            String alkuperäisen dokumentin oid
     * @param setid
     *            String alkuperäisen dokumentin setId
     * @param propertycode
     *            String avain jolla code kenttä täytetään property tiedoista
     * @param relationType
     *            Relaation tyyppikoodi (RPLC korjaus, APND uusiminen)
     */
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

        if ( relationType == XActRelationshipDocument.APND ) {
            CD value = new CD();
            fetchAttributes(Kasaaja.LM_UUSIMISPYYNTO, value);
            relatedDocument.getParentDocument().setCode(value);
            if ( !StringUtils.isEmpty(setid) ) {
                relatedDocument.getParentDocument().setSetId(of.createII());
                relatedDocument.getParentDocument().getSetId().setRoot(setid);
            }
        }
        else {
            relatedDocument.getParentDocument().setSetId(of.createII());
            relatedDocument.getParentDocument().getSetId().setRoot(setid);
        }
    }

    protected void addComponentOf(POCDMT000040ClinicalDocument clinicalDocument) {
        clinicalDocument.setComponentOf(of.createPOCDMT000040Component1());
        POCDMT000040EncompassingEncounter encompassingEncounter = of.createPOCDMT000040EncompassingEncounter();
        clinicalDocument.getComponentOf().setEncompassingEncounter(encompassingEncounter);
        encompassingEncounter.setEffectiveTime(of.createIVLTS());
        encompassingEncounter.getEffectiveTime().getNullFlavors().add("NA");
        encompassingEncounter.setResponsibleParty(of.createPOCDMT000040ResponsibleParty());

        POCDMT000040AssignedEntity assignedEntity = of.createPOCDMT000040AssignedEntity();
        encompassingEncounter.getResponsibleParty().setAssignedEntity(assignedEntity);
        assignedEntity.getIds().add(of.createII());
        fetchAttributes("componentOf.encompassingEncounter.responsibleParty.assignedEntity.id",
                assignedEntity.getIds().get(0));

        assignedEntity.setRepresentedOrganization(of.createPOCDMT000040Organization());
        assignedEntity.getRepresentedOrganization().getIds().add(of.createII());
        fetchAttributes("componentOf.encompassingEncounter.responsibleParty.assignedEntity.representedOrganization.id",
                assignedEntity.getRepresentedOrganization().getIds().get(0));
        assignedEntity.getRepresentedOrganization().getNames().add(of.createON());
        String name = getProperty(
                "componentOf.encompassingEncounter.responsibleParty.assignedEntity.representedOrganization.name");
        if ( !onkoNullTaiTyhja(name) ) {
            assignedEntity.getRepresentedOrganization().getNames().get(0).getContent().add(name);
        }
    }

    protected void addComponentOf(POCDMT000040ClinicalDocument clinicalDocument, String effectivetimeValue,
            OrganisaatioTO organisaatio, String palvelutapahtumanOid) {
        clinicalDocument.setComponentOf(of.createPOCDMT000040Component1());
        clinicalDocument.getComponentOf().setEncompassingEncounter(of.createPOCDMT000040EncompassingEncounter());

        // palvelutapahtuman tunnus
        if ( !onkoNullTaiTyhja(palvelutapahtumanOid) ) {
            II palvelutapahtumantunnus = of.createII();
            palvelutapahtumantunnus.setRoot(palvelutapahtumanOid);
            clinicalDocument.getComponentOf().getEncompassingEncounter().getIds().add(palvelutapahtumantunnus);
        }

        clinicalDocument.getComponentOf().getEncompassingEncounter().setEffectiveTime(of.createIVLTS());
        clinicalDocument.getComponentOf().getEncompassingEncounter().getEffectiveTime().setValue(effectivetimeValue);
        // laatimispaikka
        // palveluyksikkö ??
        if ( null != organisaatio ) {
            clinicalDocument.getComponentOf().getEncompassingEncounter().setLocation(of.createPOCDMT000040Location());
            POCDMT000040HealthCareFacility healthCareFacility = of.createPOCDMT000040HealthCareFacility();
            clinicalDocument.getComponentOf().getEncompassingEncounter().getLocation()
                    .setHealthCareFacility(healthCareFacility);
            // palveluyksikön oid (pakollinen)
            II id = of.createII();
            id.setRoot(organisaatio.getYksilointitunnus());
            healthCareFacility.getIds().add(id);
            healthCareFacility.setLocation(of.createPOCDMT000040Place());
            healthCareFacility.getLocation().setName(of.createEN());
            healthCareFacility.getLocation().getName().getContent().add(organisaatio.getNimi());
            AD addr = of.createAD();
            AdxpStreetAddressLine streetAddressLine = of.createAdxpStreetAddressLine();
            AdxpPostalCode postalCode = of.createAdxpPostalCode();
            AdxpCity city = of.createAdxpCity();
            streetAddressLine.getContent().add(organisaatio.getOsoite().getKatuosoite());
            postalCode.getContent().add(organisaatio.getOsoite().getPostinumero());
            city.getContent().add(organisaatio.getOsoite().getPostitoimipaikka());
            addr.getContent().add(of.createADStreetAddressLine(streetAddressLine));
            addr.getContent().add(of.createADPostalCode(postalCode));
            addr.getContent().add(of.createADCity(city));
            healthCareFacility.getLocation().setAddr(addr);
            if ( organisaatio.getToimintaYksikko() != null ) {
                healthCareFacility.setServiceProviderOrganization(
                        luoServiceProviderOrganization(organisaatio.getToimintaYksikko()));
            }
        }
    }

    protected void addLocalHeader(POCDMT000040ClinicalDocument clinicalDocument) {
        hl7finland.ObjectFactory hl7fiOF = new hl7finland.ObjectFactory();
        LocalHeader localHeader = hl7fiOF.createLocalHeader();

        CV contentsCode = of.createCV();
        if ( fetchAttributes(Kasaaja.LM_CONTENTS, contentsCode) ) {
            localHeader.setTableOfContents(hl7fiOF.createTableOfContents());
            localHeader.getTableOfContents().getContentsCodes().add(contentsCode);
        }
        CV fileFormat = of.createCV();
        if ( fetchAttributes("localHeader.fileFormat", fileFormat) ) {
            localHeader.setFileFormat(fileFormat);
        }
        SoftwareSupport softwareSupport = new SoftwareSupport();
        softwareSupport.setModerator(properties.getProperty("moderator", "Kansaneläkelaitos"));
        softwareSupport.setProduct(properties.getProperty("product", "KELAIN"));
        softwareSupport.setVersion(properties.getProperty("version", "0.0.1"));
        softwareSupport.setValue(properties.getProperty("value", "Kelain"));
        localHeader.setSoftwareSupport(softwareSupport);

        CV documentType = of.createCV();
        if ( fetchAttributes("localHeader.documentType", documentType) ) {
            localHeader.setDocumentType(documentType);
        }
        CV functionCode = of.createCV();
        if ( fetchAttributes("localHeader.functionCode", functionCode) ) {
            localHeader.setFunctionCode(functionCode);
        }
        CV recordStatus = of.createCV();
        if ( fetchAttributes("localHeader.recordStatus", recordStatus) ) {
            localHeader.setRecordStatus(recordStatus);
        }
        CV retentionPeriodClass = of.createCV();
        if ( fetchAttributes("localHeader.retentionPeriodClass", retentionPeriodClass) ) {
            localHeader.setRetentionPeriodClass(retentionPeriodClass);
        }
        clinicalDocument.setLocalHeader(localHeader);
    }

    protected POCDMT000040Component5 createComponent(String codeNum) {
        POCDMT000040Component5 component = of.createPOCDMT000040Component5();
        component.setSection(of.createPOCDMT000040Section());
        component.getSection().setCode(of.createCE());
        fetchAttributes(codeNum + ".component", component.getSection().getCode());
        component.getSection().getCode().setCode(codeNum);
        component.getSection().setTitle(of.createST());
        component.getSection().getTitle().getContent().add(component.getSection().getCode().getDisplayName());
        return component;
    }

    protected PN getNames(KokoNimiTO kokonimi) {
        PN uusiNimi = new PN();
        for (NimiTO nimi : kokonimi.getNimet()) {
            if ( "given".equals(nimi.getTyyppi()) ) {
                EnGiven given = of.createEnGiven();
                given.getContent().add(nimi.getNimi());
                if ( !onkoNullTaiTyhja(nimi.getMaare()) ) {
                    given.getQualifiers().add(nimi.getMaare());
                }
                uusiNimi.getContent().add(of.createENGiven(given));
            }
            else if ( "family".equals(nimi.getTyyppi()) ) {
                EnFamily family = of.createEnFamily();
                family.getContent().add(nimi.getNimi());
                if ( !onkoNullTaiTyhja(nimi.getMaare()) ) {
                    family.getQualifiers().add(nimi.getMaare());
                }
                uusiNimi.getContent().add(of.createENFamily(family));
            }
            else if ( "prefix".equals(nimi.getTyyppi()) ) {
                EnPrefix prefix = of.createEnPrefix();
                prefix.getContent().add(nimi.getNimi());
                if ( !onkoNullTaiTyhja(nimi.getMaare()) ) {
                    prefix.getQualifiers().add(nimi.getMaare());
                }
                uusiNimi.getContent().add(of.createENPrefix(prefix));
            }
            else if ( "suffix".equals(nimi.getTyyppi()) ) {
                EnSuffix suffix = of.createEnSuffix();
                suffix.getContent().add(nimi.getNimi());
                if ( !onkoNullTaiTyhja(nimi.getMaare()) ) {
                    suffix.getQualifiers().add(nimi.getMaare());
                }
                uusiNimi.getContent().add(of.createENSuffix(suffix));
            }
            else if ( "delimiter".equals(nimi.getTyyppi()) ) {
                EnDelimiter delimiter = of.createEnDelimiter();
                delimiter.getContent().add(nimi.getNimi());
                if ( !onkoNullTaiTyhja(nimi.getMaare()) ) {
                    delimiter.getQualifiers().add(nimi.getMaare());
                }
                uusiNimi.getContent().add(of.createENDelimiter(delimiter));
            }
        }
        return uusiNimi;
    }

    /**
     * Apumetodi oikean property avaimen hakemiseen. Palauttaa cda tyyppiä vastaavan property avaimen
     * 
     * @param cdaTyyppi
     * @return
     */
    protected String getKeyByDocumentType(int cdaTyyppi) {
        if ( cdaTyyppi == KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYS.getTyyppi() ) {
            return "code";
        }
        else if ( cdaTyyppi == KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYKSEN_KORJAUS.getTyyppi() ) {
            return "korjaus.code";
        }
        else if ( cdaTyyppi == KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYKSEN_MITATOINTI.getTyyppi() ) {
            return "mitatointi.code";
        }
        return null;
    }

    /**
     * Apumetodi propertyjen hakuun. Palauttaa ensimmäisenä löytyneen osuman esim. haetaan realmCode.code arvoa
     * kasaajalla jonka getTypeKey palautta TESTI. jolloin key on realmCode ja extension on code Koettaa hakea
     * järjestyksessä TESTI.realmCode.code, realmCode.code ja TESTI.code
     *
     * @param key
     *            String avain jolla haetaan
     * @param extension
     *            String avaimen laajennos
     * @return löytyneen arvon tai null jos ei löytynyt
     * @see fetchAttributes
     */
    private String getProperty(String key, String extension) {
        // Haetaan typeKey.key.extension:lla
        StringBuilder concKey = new StringBuilder(getTypeKey());
        concKey.append(".").append(key).append(".").append(extension);
        String value = properties.getProperty(concKey.toString());
        if ( null != value ) {
            return value;
        }
        // Haetaan key.extension:lla
        value = properties.getProperty(key + "." + extension);
        if ( null != value ) {
            return value;
        }
        // Haetaan typeKey.extension:lla
        value = properties.getProperty(getTypeKey() + "." + extension);
        return value;
    }

    private String getProperty(String key) {
        String value = properties.getProperty(getTypeKey() + "." + key);
        if ( null != value ) {
            return value;
        }
        value = properties.getProperty(key);
        return value;
    }

    /**
     * hakee key propertyn. Pyrkii ensin etsimään getTypeKey().key avaimella jos sillä ei löydy palauttaa arvon pelkällä
     * keyllä
     *
     * @param key
     *            String avain jolla propertyä haetaan
     * @return String property joka vastaa joko getTypeKey().key tai key avainta muuten null.
     */
    protected String fetchProperty(String key) {
        return getProperty(key);
    }

    protected void fetchAttributes(String key, II element) {
        String extension = getProperty(key, "extension");
        String root = getProperty(key, "root");
        if ( !onkoNullTaiTyhja(extension) ) {
            element.setExtension(extension);
        }
        if ( !onkoNullTaiTyhja(root) ) {
            element.setRoot(root);
        }
    }

    protected boolean fetchAttributes(String key, CD element) {
        boolean retval = false;

        String code = getProperty(key, "code");
        String codeSystem = getProperty(key, "codeSystem");
        String codeSystemName = getProperty(key, "codeSystemName");
        String displayName = getProperty(key, "displayName");
        String codeSystemVersion = getProperty(key, "codeSystemVersion");

        if ( !onkoNullTaiTyhja(code) ) {
            element.setCode(code);
            retval = true;
        }
        if ( !onkoNullTaiTyhja(codeSystem) ) {
            element.setCodeSystem(codeSystem);
        }
        if ( !onkoNullTaiTyhja(codeSystemName) ) {
            element.setCodeSystemName(codeSystemName);
        }
        if ( !onkoNullTaiTyhja(displayName) ) {
            element.setDisplayName(displayName);
        }
        if ( !onkoNullTaiTyhja(codeSystemVersion) ) {
            element.setCodeSystemVersion(codeSystemVersion);
        }
        return retval;
    }

    protected boolean fetchAttributes(String key, MO element) {
        boolean retval = false;

        String value = getProperty(key, "value");
        String currency = getProperty(key, "currency");

        if ( !onkoNullTaiTyhja(value) ) {
            element.setValue(value);
            retval = true;
        }
        if ( !onkoNullTaiTyhja(currency) ) {
            element.setCurrency(currency);
        }
        return retval;
    }

    /**
     * Kopio lähde CD elementin attribuutit kohde elementiin
     *
     * @param kohde
     *            CD elementti johon tiedot kopioidaan
     * @param lahde
     *            CD elementti josta tietoja kopiodaan
     */
    protected void copyCodeElement(CD kohde, CD lahde) {
        if ( lahde == null ) {
            return;
        }
        if ( !onkoNullTaiTyhja(lahde.getCode()) ) {
            kohde.setCode(lahde.getCode());
        }
        if ( !onkoNullTaiTyhja(lahde.getCodeSystem()) ) {
            kohde.setCodeSystem(lahde.getCodeSystem());
        }
        if ( !onkoNullTaiTyhja(lahde.getCodeSystemName()) ) {
            kohde.setCodeSystemName(lahde.getCodeSystemName());
        }
        if ( !onkoNullTaiTyhja(lahde.getDisplayName()) ) {
            kohde.setDisplayName(lahde.getDisplayName());
        }
        if ( !onkoNullTaiTyhja(lahde.getCodeSystemVersion()) ) {
            kohde.setCodeSystemVersion(lahde.getCodeSystemVersion());
        }
    }

    protected boolean onkoNullTaiTyhja(String merkkijono) {
        return merkkijono == null || merkkijono.isEmpty();
    }

    /**
     * Apumetodi joka poistaa nollat annetun merkkijonon alusta
     * 
     * @param tunnus
     * @return tunnus josta etunollat poistettu tai null jos tunnus on null.
     */
    protected String poistaEtuNollat(String tunnus) {
        if ( onkoNullTaiTyhja(tunnus) ) {
            return tunnus;
        }
        return tunnus.replaceFirst("^0*", "");
    }

    protected long getSequence() {
        return ++sequence;
    }

    protected POCDMT000040Organization luoServiceProviderOrganization(OrganisaatioTO organisaatio) {
        POCDMT000040Organization provider = of.createPOCDMT000040Organization();
        II id = of.createII();
        id.setRoot(organisaatio.getYksilointitunnus());
        provider.getIds().add(id);
        provider.getNames().add(luoNames(organisaatio));
        if ( organisaatio.getOsoite() != null ) {
            provider.getAddrs().add(luoAddress(organisaatio.getOsoite()));
        }
        return provider;
    }

    protected ON luoNames(OrganisaatioTO organisaatio) {
        ON names = null;
        if ( organisaatio != null && !onkoNullTaiTyhja(organisaatio.getNimi()) ) {
            names = of.createON();
            names.getContent().add(organisaatio.getNimi());
        }
        return names;
    }

    protected POCDMT000040Author luoBodyAuthor(AmmattihenkiloTO ammattihenkilo) {
        POCDMT000040Author author = of.createPOCDMT000040Author();
        addAuthor(ammattihenkilo, author);

        if ( null != ammattihenkilo.getOrganisaatio() ) {
            author.getAssignedAuthor().setRepresentedOrganization(luoOrganization(ammattihenkilo.getOrganisaatio()));
        }
        return author;
    }

    private void addAuthor(AmmattihenkiloTO ammattihenkilo, POCDMT000040Author author) {
        // kirjautumisaika (opt)
        author.setTime(of.createTS());
        if ( null != ammattihenkilo.getKirjautumisaika() ) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            sdf.setTimeZone(TimeZone.getTimeZone(Kasaaja.TIME_ZONE));
            author.getTime().setValue(sdf.format(ammattihenkilo.getKirjautumisaika()));
        }
        else {
            author.getTime().getNullFlavors().add(KantaCDAConstants.NullFlavor.NI.getCode());
        }
        author.setAssignedAuthor(of.createPOCDMT000040AssignedAuthor());

        // yksilöintitunnus
        String svNumero = ammattihenkilo.getSvNumero();
        if ( !onkoNullTaiTyhja(svNumero) ) {
            II yksilointitunnus = of.createII();
            fetchAttributes("author.id.svnumero", yksilointitunnus);
            yksilointitunnus.setExtension(svNumero);
            author.getAssignedAuthor().getIds().add(yksilointitunnus);
        }
        // rekisteröintinumero (terhikki)
        if ( StringUtils.isNotEmpty(ammattihenkilo.getRekisterointinumero()) ) {
            II rekisterointinumero = of.createII();
            fetchAttributes("author.id.terhikki", rekisterointinumero);
            rekisterointinumero.setExtension(ammattihenkilo.getRekisterointinumero());
            author.getAssignedAuthor().getIds().add(rekisterointinumero);
        }

        author.getAssignedAuthor().setCode(of.createCE());
        fetchAttributes("author.assignedAuthor.code", author.getAssignedAuthor().getCode());
        // erikois ala (pakollinen erikoislääkäreillä)
        if ( !onkoNullTaiTyhja(ammattihenkilo.getErikoisala()) ) {
            author.getAssignedAuthor().getCode().setCode(ammattihenkilo.getErikoisala());
            author.getAssignedAuthor().getCode().setDisplayName(ammattihenkilo.getErikoisalaName());
        }
        else {
            author.getAssignedAuthor().getCode().getNullFlavors().add("NI");
        }
        author.getAssignedAuthor().getCode().getTranslations().add(of.createCD());
        if ( !onkoNullTaiTyhja(ammattihenkilo.getVirkanimike()) ) {
            CR virkanimike = of.createCR();
            virkanimike.setName(of.createCV());
            fetchAttributes("author.assignedAuthor.code.translation.qualifier.title", virkanimike.getName());
            virkanimike.setValue(of.createCD());
            virkanimike.getValue().setOriginalText(of.createED());
            virkanimike.getValue().getOriginalText().getContent().add(ammattihenkilo.getVirkanimike());
            // virkanimike(opt)
            author.getAssignedAuthor().getCode().getTranslations().get(0).getQualifiers().add(virkanimike);
        }
        if ( !onkoNullTaiTyhja(ammattihenkilo.getOppiarvo())
                || !onkoNullTaiTyhja(ammattihenkilo.getOppiarvoTekstina()) ) {
            CR oppiarvo = of.createCR();
            oppiarvo.setName(of.createCV());
            fetchAttributes("author.assignerAuthor.code.translation.qualifier.degree", oppiarvo.getName());
            oppiarvo.setValue(of.createCD());

            if ( !onkoNullTaiTyhja(ammattihenkilo.getOppiarvoTekstina()) ) { // OrignalText on ensisijainen
                oppiarvo.getValue().setOriginalText(of.createED());
                oppiarvo.getValue().getOriginalText().getContent().add(ammattihenkilo.getOppiarvoTekstina());
            }
            else if ( !onkoNullTaiTyhja(ammattihenkilo.getOppiarvo()) ) {
                // oppi arvo on koodattuna
                oppiarvo.getValue().setCode(ammattihenkilo.getOppiarvo());
                oppiarvo.getValue().setDisplayName(ammattihenkilo.getOppiarvo()); // Tekstiä ei ole, laitetaan koodi
                // ettei attribuutti jää tyhjäksi
            }
            // oppiarvo(opt)
            author.getAssignedAuthor().getCode().getTranslations().get(0).getQualifiers().add(oppiarvo);
        }

        CR ammattioikeus = of.createCR();
        ammattioikeus.setName(of.createCV());
        fetchAttributes("author.assignerAuthor.code.translation.qualifier.name", ammattioikeus.getName());
        ammattioikeus.setValue(of.createCD());

        fetchAttributes("author.assignerAuthor.code.translation.qualifier.value", ammattioikeus.getValue());
        ammattioikeus.getValue().setCode(ammattihenkilo.getAmmattioikeus());
        ammattioikeus.getValue().setDisplayName(ammattihenkilo.getAmmattioikeusName());
        // ammattioikeus
        author.getAssignedAuthor().getCode().getTranslations().get(0).getQualifiers().add(ammattioikeus);

        author.getAssignedAuthor().setAssignedPerson(of.createPOCDMT000040Person());
        // Lääkeen määräjän nimi
        author.getAssignedAuthor().getAssignedPerson().getNames().add(getNames(ammattihenkilo.getKokonimi()));
    }

    private void addOrganizationPartOf(POCDMT000040Organization organization, OrganisaatioTO organisaatio) {
        POCDMT000040OrganizationPartOf yksik = of.createPOCDMT000040OrganizationPartOf();
        yksik.setWholeOrganization(luoOrganization(organisaatio.getToimintaYksikko()));
        organization.setAsOrganizationPartOf(yksik);
    }

}
