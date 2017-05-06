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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import javax.xml.bind.JAXBException;

import org.codehaus.plexus.util.StringUtils;
import org.hl7.v3.CD;
import org.hl7.v3.CE;
import org.hl7.v3.CR;
import org.hl7.v3.CV;
import org.hl7.v3.POCDMT000040Author;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component3;
import org.hl7.v3.POCDMT000040Component4;
import org.hl7.v3.POCDMT000040Component5;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040Observation;
import org.hl7.v3.POCDMT000040Reference;
import org.hl7.v3.ST;
import org.hl7.v3.XActMoodDocumentObservation;
import org.hl7.v3.XActRelationshipDocument;
import org.hl7.v3.XActRelationshipExternalReference;

import fi.kela.kanta.cda.KantaCDAConstants.ReseptisanomanTyyppi;
import fi.kela.kanta.cda.validation.ReseptinMitatointiValidoija;
import fi.kela.kanta.to.LaakemaarayksenMitatointiTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.util.JaxbUtil;
import hl7finland.LocalHeader;
import hl7finland.LocalHeader.SoftwareSupport;

public class ReseptinMitatointiKasaajaEteenpainYht extends ReseptiKasaaja {

    private final LaakemaarayksenMitatointiTO mitatointi;
    private final LaakemaaraysTO alkuperainenLaakemaarays;
    private final POCDMT000040ClinicalDocument alkuperainenClinicalDocument;
    private final Properties properties;

    public ReseptinMitatointiKasaajaEteenpainYht(Properties properties, LaakemaarayksenMitatointiTO mitatointi,
            LaakemaaraysTO alkuperainenLaakemaarays, POCDMT000040ClinicalDocument alkuperainenClinicalDocument) {
        super(properties);
        this.properties = properties;
        this.mitatointi = mitatointi;
        this.alkuperainenLaakemaarays = alkuperainenLaakemaarays;
        this.alkuperainenClinicalDocument = alkuperainenClinicalDocument;
        validoija = new ReseptinMitatointiValidoija(alkuperainenLaakemaarays, mitatointi);
    }

    /**
     * Täyttää LaakemaarayksenMitatointiTOn tyhjät kentät alkuperäisestä lääkemääräyksestä. Asettaa myös mitätöinnin
     * oidin, setIdn, version ja alkuperainenOit tiedot.
     *
     * @param mitatointi
     *            LaakemaarayksenMitatointiTO johon poimittavat tiedot sijoitetaan.
     * @param alkuperainenLaakemaarays
     *            LaakemaaraysTO josta tiedot poimitaan.
     */
    private void paivitaMitatointi() {
        if ( null == alkuperainenLaakemaarays ) {
            return;
        }
        if ( StringUtils.isBlank(mitatointi.getOid()) ) {
            mitatointi.setOid(getDocumentId(mitatointi));
        }
        mitatointi.setAlkuperainenOid(alkuperainenLaakemaarays.getOid());
        mitatointi.setSetId(alkuperainenLaakemaarays.getSetId());
        mitatointi.setVersio(alkuperainenLaakemaarays.getVersio());
        mitatointi.setAlkuperainenCdaTyyppi(alkuperainenLaakemaarays.getCdaTyyppi());

        if ( null == mitatointi.getMaarayspaiva() ) {
            mitatointi.setMaarayspaiva(alkuperainenLaakemaarays.getMaarayspaiva());
        }

        if ( null == mitatointi.getLaakemaarayksenVoimassaolonLoppuaika() ) {
            mitatointi.setLaakemaarayksenVoimassaolonLoppuaika(
                    alkuperainenLaakemaarays.getLaakemaarayksenVoimassaolonLoppuaika());
            mitatointi.setLaakemaarayksenVoimassaolonLoppuaika(
                    alkuperainenLaakemaarays.getLaakemaarayksenVoimassaolonLoppuaika());
        }
        if ( null == mitatointi.getReseptintyyppi() ) {
            mitatointi.setReseptintyyppi(String.valueOf(ReseptisanomanTyyppi.LAAKEMAARAYKSEN_MITATOINTI.getTyyppi()));
        }
        if ( null == mitatointi.getPakkauksienLukumaara() ) {
            mitatointi.setPakkauksienLukumaara(alkuperainenLaakemaarays.getPakkauksienLukumaara());
        }
        if ( null == mitatointi.getLaakkeenKokonaismaaraValue() ) {
            mitatointi.setLaakkeenKokonaismaaraValue(alkuperainenLaakemaarays.getLaakkeenKokonaismaaraValue());
        }
        if ( null == mitatointi.getLaakkeenKokonaismaaraUnit() ) {
            mitatointi.setLaakkeenKokonaismaaraUnit(alkuperainenLaakemaarays.getLaakkeenKokonaismaaraUnit());
        }
        if ( null == mitatointi.getAjalleMaaratynReseptinAlkuaika() ) {
            mitatointi.setAjalleMaaratynReseptinAlkuaika(alkuperainenLaakemaarays.getAjalleMaaratynReseptinAlkuaika());
        }
        if ( null == mitatointi.getAjalleMaaratynReseptinAikamaaraValue() ) {
            mitatointi.setAjalleMaaratynReseptinAikamaaraValue(
                    alkuperainenLaakemaarays.getAjalleMaaratynReseptinAikamaaraValue());
            mitatointi.setAjalleMaaratynReseptinAikamaaraValue(
                    alkuperainenLaakemaarays.getAjalleMaaratynReseptinAikamaaraValue());
        }
        if ( null == mitatointi.getAjalleMaaratynReseptinAikamaaraUnit() ) {
            mitatointi.setAjalleMaaratynReseptinAikamaaraUnit(
                    alkuperainenLaakemaarays.getAjalleMaaratynReseptinAikamaaraUnit());
        }
        if ( null == mitatointi.getIterointiTeksti() ) {
            mitatointi.setIterointiTeksti(alkuperainenLaakemaarays.getIterointiTeksti());
        }
        if ( null == mitatointi.getIterointienMaara() ) {
            mitatointi.setIterointienMaara(alkuperainenLaakemaarays.getIterointienMaara());
        }
        if ( null == mitatointi.getIterointienValiValue() ) {
            mitatointi.setIterointienValiValue(alkuperainenLaakemaarays.getIterointienValiValue());
        }
        if ( null == mitatointi.getIterointienValiUnit() ) {
            mitatointi.setIterointienValiUnit(alkuperainenLaakemaarays.getIterointienValiUnit());
        }
        if ( null == mitatointi.getValmiste() ) {
            mitatointi.setValmiste(alkuperainenLaakemaarays.getValmiste());
        }
        if ( null == mitatointi.getApteekissaValmistettavaLaake() ) {
            mitatointi.setApteekissaValmistettavaLaake(alkuperainenLaakemaarays.getApteekissaValmistettavaLaake());
        }
        if ( null == mitatointi.getLaaketietokannanUlkopuolinenValmiste() ) {
            mitatointi.setLaaketietokannanUlkopuolinenValmiste(
                    alkuperainenLaakemaarays.getLaaketietokannanUlkopuolinenValmiste());
            mitatointi.setLaaketietokannanUlkopuolinenValmiste(
                    alkuperainenLaakemaarays.getLaaketietokannanUlkopuolinenValmiste());
        }
        if ( null == mitatointi.getTyonantaja() ) {
            mitatointi.setTyonantaja(alkuperainenLaakemaarays.getTyonantaja());
        }
        if ( null == mitatointi.getVakuutuslaitos() ) {
            mitatointi.setVakuutuslaitos(alkuperainenLaakemaarays.getVakuutuslaitos());
        }
        if ( null == mitatointi.getAmmattihenkilo() ) {
            mitatointi.setAmmattihenkilo(alkuperainenLaakemaarays.getAmmattihenkilo());
        }
        if ( null == mitatointi.getPotilas() ) {
            mitatointi.setPotilas(alkuperainenLaakemaarays.getPotilas());
        }
        if ( null == mitatointi.isApteekissaValmistettavaLaake() ) {
            mitatointi.setApteekissaValmistettavaLaake(alkuperainenLaakemaarays.isApteekissaValmistettavaLaake());
        }
        if ( null == mitatointi.isAnnosteluPelkastaanTekstimuodossa() ) {
            mitatointi.setAnnosteluPelkastaanTekstimuodossa(
                    alkuperainenLaakemaarays.isAnnosteluPelkastaanTekstimuodossa());
        }
        if ( null == mitatointi.getAnnostusohje() ) {
            mitatointi.setAnnostusohje(alkuperainenLaakemaarays.getAnnostusohje());
        }
        if ( null == mitatointi.isSICmerkinta() ) {
            mitatointi.setSICmerkinta(alkuperainenLaakemaarays.isSICmerkinta());
        }
        if ( null == mitatointi.isLaakevaihtokielto() ) {
            mitatointi.setLaakevaihtokielto(alkuperainenLaakemaarays.isLaakevaihtokielto());
        }
        if ( null == mitatointi.getKayttotarkoitusTeksti() ) {
            mitatointi.setKayttotarkoitusTeksti(alkuperainenLaakemaarays.getKayttotarkoitusTeksti());
        }
        if ( null == mitatointi.getAlle12VuotiaanPainoValue() ) {
            mitatointi.setAlle12VuotiaanPainoValue(alkuperainenLaakemaarays.getAlle12VuotiaanPainoValue());
        }
        if ( null == mitatointi.getAlle12VuotiaanPainoUnit() ) {
            mitatointi.setAlle12VuotiaanPainoUnit(alkuperainenLaakemaarays.getAlle12VuotiaanPainoUnit());
        }
        if ( null == mitatointi.isAnnosjakelu() ) {
            mitatointi.setAnnosjakelu(alkuperainenLaakemaarays.isAnnosjakelu());
        }
        if ( null == mitatointi.getAnnosjakeluTeksti() ) {
            mitatointi.setAnnosjakeluTeksti(alkuperainenLaakemaarays.getAnnosjakeluTeksti());
        }
        if ( mitatointi.getHoitolajit().isEmpty() ) {
            mitatointi.getHoitolajit().addAll(alkuperainenLaakemaarays.getHoitolajit());
        }
        if ( null == mitatointi.getViestiApteekille() ) {
            mitatointi.setViestiApteekille(alkuperainenLaakemaarays.getViestiApteekille());
        }
        if ( null == mitatointi.getErillisselvitys() ) {
            mitatointi.setErillisselvitys(alkuperainenLaakemaarays.getErillisselvitys());
        }
        if ( null == mitatointi.getErillisselvitysteksti() ) {
            mitatointi.setErillisselvitysteksti(alkuperainenLaakemaarays.getErillisselvitysteksti());
        }
        if ( null == mitatointi.getPotilaanTunnistaminen() ) {
            mitatointi.setPotilaanTunnistaminen(alkuperainenLaakemaarays.getPotilaanTunnistaminen());
        }
        if ( null == mitatointi.getPotilaanTunnistaminenTeksti() ) {
            mitatointi.setPotilaanTunnistaminenTeksti(alkuperainenLaakemaarays.getPotilaanTunnistaminenTeksti());
        }
        if ( null == mitatointi.getPKVlaakemaarays() ) {
            mitatointi.setPKVlaakemaarays(alkuperainenLaakemaarays.getPKVlaakemaarays());
        }
        if ( null == mitatointi.isPysyvaislaakitys() ) {
            mitatointi.setPysyvaislaakitys(alkuperainenLaakemaarays.isPysyvaislaakitys());
        }
        if ( null == mitatointi.isKyseessaLaakkeenkaytonAloitus() ) {
            mitatointi.setKyseessaLaakkeenkaytonAloitus(alkuperainenLaakemaarays.isKyseessaLaakkeenkaytonAloitus());
        }
        if ( null == mitatointi.isHuume() ) {
            mitatointi.setHuume(alkuperainenLaakemaarays.isHuume());
        }
        if ( null == mitatointi.getReseptinLaji() ) {
            mitatointi.setReseptinLaji(alkuperainenLaakemaarays.getReseptinLaji());
        }
        if ( null == mitatointi.isUudistamiskielto() ) {
            mitatointi.setUudistamiskielto(alkuperainenLaakemaarays.isUudistamiskielto());
        }
        if ( null == mitatointi.getUusimiskiellonSyy() ) {
            mitatointi.setUusimiskiellonSyy(alkuperainenLaakemaarays.getUusimiskiellonSyy());
        }
        if ( null == mitatointi.getUusimiskiellonPerustelu() ) {
            mitatointi.setUusimiskiellonPerustelu(alkuperainenLaakemaarays.getUusimiskiellonPerustelu());
        }
        if ( null == mitatointi.getLaaketietokannanVersio() ) {
            mitatointi.setLaaketietokannanVersio(alkuperainenLaakemaarays.getLaaketietokannanVersio());
        }
        if ( null == mitatointi.getApteekissaTallennettuLaakemaarays() ) {
            mitatointi.setApteekissaTallennettuLaakemaarays(
                    alkuperainenLaakemaarays.getApteekissaTallennettuLaakemaarays());
        }
        if ( null == mitatointi.getTartuntatauti() ) {
            mitatointi.setTartuntatauti(alkuperainenLaakemaarays.getTartuntatauti());
        }
        if ( null == mitatointi.getApteekissaTallennettuLaakemaaraysPerustelu() ) {
            mitatointi.setApteekissaTallennettuLaakemaaraysPerustelu(
                    alkuperainenLaakemaarays.getApteekissaTallennettuLaakemaaraysPerustelu());
        }
        if ( null == mitatointi.getApteekissaTallennettuLaakemaaraysMuuSyy() ) {
            mitatointi.setApteekissaTallennettuLaakemaaraysMuuSyy(
                    alkuperainenLaakemaarays.getApteekissaTallennettuLaakemaaraysMuuSyy());
        }
    }

    /**
     * Luo lääkemääräyksen korjauksen LaakemaarayksenMitatointiTO tiedoista.
     *
     * @param mitatointi
     *            LaakemaarayksenMitatointiTO josta mitätöinnin tiedot poimitaan.
     * @param alkuperainenLaakemaarays
     *            alkuperainen LaakemaaraysTO.
     * @return lääkemääräyksen mitätöinti POCDMT000040ClinicalDocumenttinä
     */
    private POCDMT000040ClinicalDocument kasaaReseptinMitatointi() {

        paivitaMitatointi();
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("EET"));
        String effectiveTimeValue = getDateFormat().format(now.getTime());

        alkuperainenClinicalDocument.getRealmCodes().clear();
        alkuperainenClinicalDocument.getTemplateIds().clear();

        addIdFields(alkuperainenClinicalDocument, mitatointi, effectiveTimeValue);

        // Korjataan title ja code
        fetchAttributes("mitatointi.code", alkuperainenClinicalDocument.getCode());
        alkuperainenClinicalDocument.getTitle().getContent().clear();
        alkuperainenClinicalDocument.getTitle().getContent().add(fetchProperty("mitatointi.title"));

        // //////////////////////////////////////////////////////////////////////
        /*
         * CDAR2 header 3.30: Ammattihenkilön rooli ilmoitetaan koodiston 1.2.246.537.5.40006.2003 eArkisto - tekninen
         * CDA R2 henkilötarkennin 2009 mukaisena arvona. Alkuperäinen lääkkeen määrääjä: code="LAL" Toimituksen tekijä:
         * code=”LTE” Jos kyseessä on korjaus tai mitätöinti, author-elementti toistuu korjaajan tai mitätöijän
         * tiedoille korjaajalle code=”KOR” mitätöijälle code= ”MIT” Jos siis kyse on lääkemääräyksen mitätöinnistä tai
         * korjauksesta, toisessa author-elementissä on ”LAL” ja toisessa ”MIT” tai ”KOR”. Jos taas kyse on
         * lääketoimituksen mitätöinnistä tai korjauksesta, toisessa author-elementissä on ”LTE” ja toisessa ”MIT” tai
         * ”KOR.” Jos lääkemääräystä tai toimitusta on korjattu ja/tai mitätöity useampia kertoja, ilmoitetaan
         * lääkemääräyksen/toimituksen korjauksessa/mitätöinnissä vain alkuperäisen version ja kyseisen version laatija.
         */
        mitatointi.getMitatoija().setRooli("MIT");
        mitatointi.getAmmattihenkilo().setRooli(MaaraajanRooli.LAAK_ALOIT_HENK.getRooliKoodi());
        // /////////////////////////////////////////////////////////////////////////

        addAuthor(alkuperainenClinicalDocument, luoAuthor(mitatointi.getMitatoija()));
        addCustodian(alkuperainenClinicalDocument);
        // Poistetaan alkuperäisellä dokumentilla olevat viittaukset
        alustaRelatedDocuments();
        addRelatedDocument(alkuperainenClinicalDocument, alkuperainenLaakemaarays.getOid(),
                alkuperainenLaakemaarays.getSetId(), getKeyByDocumentType(alkuperainenLaakemaarays.getCdaTyyppi()),
                XActRelationshipDocument.RPLC);

        if ( alkuperainenClinicalDocument.getComponentOf().getEncompassingEncounter().getEffectiveTime() == null ) {
            alkuperainenClinicalDocument.getComponentOf().getEncompassingEncounter().setEffectiveTime(of.createIVLTS());
            alkuperainenClinicalDocument.getComponentOf().getEncompassingEncounter().getEffectiveTime()
                    .setValue(effectiveTimeValue);
        }

        hl7finland.ObjectFactory hl7fiOF = new hl7finland.ObjectFactory();
        LocalHeader localHeader = alkuperainenClinicalDocument.getLocalHeader();
        if ( localHeader == null ) {
            localHeader = hl7fiOF.createLocalHeader();
            alkuperainenClinicalDocument.setLocalHeader(localHeader);

            // TableOfContents tableOfContents = hl7fiOF.createTableOfContents();
            CV contentsCode = of.createCV();
            if ( fetchAttributes(Kasaaja.LM_CONTENTS, contentsCode) ) {
                localHeader.setTableOfContents(hl7fiOF.createTableOfContents());
                localHeader.getTableOfContents().getContentsCodes().add(contentsCode);
            }
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

        if ( localHeader.getSignatureCollection() != null ) {
            localHeader.getSignatureCollection().getSignatures().clear();
        }

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

        POCDMT000040Component3 component3 = alkuperainenClinicalDocument.getComponent().getStructuredBody()
                .getComponents().get(0);
        fetchAttributes("templateId", component3.getTemplateIds().get(0));
        component3.getSection().setAttributeID(getNextOID(mitatointi));
        component3.getSection().setId(of.createII());
        component3.getSection().getId().setRoot(getId(mitatointi));
        component3.getSection().setCode(of.createCE());
        fetchAttributes("mitatointi.code", component3.getSection().getCode());
        component3.getSection().setTitle(of.createST());
        // Title
        component3.getSection().getTitle().getContent().add(component3.getSection().getCode().getDisplayName());

        if ( !component3.getSection().getComponents().isEmpty() ) {
            POCDMT000040Component5 component5 = component3.getSection().getComponents().get(0);
            if ( !component5.getSection().getComponents().isEmpty() ) {
                component5.getSection().setAttributeID(getNextOID(mitatointi));
                component5.getSection().setId(of.createII());
                component5.getSection().getId().setRoot(getId(mitatointi));
                POCDMT000040Component5 component6 = component5.getSection().getComponents().get(0);
                component6.getSection().setAttributeID(getNextOID(mitatointi));
                component6.getSection().setId(of.createII());
                component6.getSection().getId().setRoot(getId(mitatointi));
                POCDMT000040Entry entry = findEntryByCode(component6,
                        KantaCDAConstants.Laakityslista.LAAKEVALMISTEEN_JA_PAKKAUKSEN_TIEDOT);
                if ( entry != null ) {
                    List<POCDMT000040Reference> references = entry.getOrganizer().getComponents().get(0)
                            .getSubstanceAdministration().getEntryRelationships().get(0).getSupply().getReferences();
                    references.clear();
                    references.addAll(luoViittaukset(mitatointi));
                }
                // Lääkemääräys
                POCDMT000040Entry laakemaarayksenMuuttiedotEntry = getLaakemaarayksenMuutTiedot(component6);
                if ( null != laakemaarayksenMuuttiedotEntry ) {
                    // XXX: Tulisiko tuo code ensin varmuuden vuoksi poistaa kokonaan?
                    fetchAttributes(getMuutTiedotCode(), laakemaarayksenMuuttiedotEntry.getOrganizer().getCode());
                    luoAsiakirjanMuutTiedot(laakemaarayksenMuuttiedotEntry);
                }
            }
        }

        return alkuperainenClinicalDocument;
    }

    private POCDMT000040Entry findEntryByCode(POCDMT000040Component5 component6, String code) {
        for (POCDMT000040Entry entry : component6.getSection().getEntries()) {
            if ( code.equals(entry.getOrganizer().getCode().getCode()) ) {
                return entry;
            }
        }
        return null;
    }

    /**
     * Luo viittaukset alkuperaiseen lääkemääräykseen ja lääkemääräyksen korjaukseen itseensä sekä
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tietoja haetaan
     * @return POCDMT000040Reference lista
     */
    @Override
    protected Collection<POCDMT000040Reference> luoViittaukset(LaakemaaraysTO laakemaarays) {
        Collection<POCDMT000040Reference> viittaukset = new ArrayList<POCDMT000040Reference>();
        CD code = of.createCD();
        String oid = laakemaarays.getOid();
        String setId = laakemaarays.getSetId();
        fetchAttributes("mitatointi.code", code);

        // viittaus itseensä
        viittaukset.add(luoViittaus(oid, setId, XActRelationshipExternalReference.SPRT, code));

        // Viittaus edellisen version lääkemääräykseen tai lääkemääryksen korjaukseen
        CD alkupCode = of.createCD();
        if ( laakemaarays instanceof LaakemaarayksenMitatointiTO ) {
            if ( ((LaakemaarayksenMitatointiTO) laakemaarays)
                    .getAlkuperainenCdaTyyppi() == KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYKSEN_MITATOINTI
                            .getTyyppi() ) {
                fetchAttributes("mitatointi.code", alkupCode);
            }
            else if ( ((LaakemaarayksenMitatointiTO) laakemaarays)
                    .getAlkuperainenCdaTyyppi() == KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYKSEN_KORJAUS
                            .getTyyppi() ) {
                fetchAttributes("korjaus.code", alkupCode);
            }
            else {
                fetchAttributes(Kasaaja.LM_CONTENTS, alkupCode);
            }
            viittaukset.add(luoViittaus(((LaakemaarayksenMitatointiTO) laakemaarays).getAlkuperainenOid(), setId,
                    XActRelationshipExternalReference.RPLC, alkupCode));
        }

        return viittaukset;
    }

    /**
     * Luo lääkemääräyksen mitatoinnin perustelun ja korjaajan tiedot
     *
     * @param laakemaarays
     *            LaakemaarayksenMitatointiTO josta tiedot haetaan
     * @return POCDMT000040Component4 elementin johon annetut mitatointitiedot asetettu
     */
    protected POCDMT000040Component4 luoMitatoinninSyyPerusteluJaMitatoija(LaakemaaraysTO laakemaarays) {

        /*
         * <!-- Lääkemääräyksen mitätöintitiedot --> <!-- mitätöinnin syy --> <component><observation classCode="OBS"
         * moodCode="EVN"><code code="95" codeSystem="1.2.246.537.6.12.2002.126" codeSystemName="Lääkityslista"
         * displayName="Lääkemääräyksen mitätöinnin syy tekstinä"/><!-- Mitätöinnin perustelu koodiarvolla ilmoitettuna
         * (pakollinen) uusi tieto versiossa 1.2.246.777.11.2015.2. --><value code="2"
         * codeSystem="1.2.246.537.6.600.2013" codeSystemName="THL - Lääkehoidon muutoksen syy" displayName=
         * "Lääkkeen tarve on päättynyt" xsi:type="CE"/><!-- Mitätöinnin tekstimuotoinen perustelu (pakollinen, jos
         * koodiston mukaisena arvona annettu 5=Muu syy, muilla arvoilla vapaaehtoinen) --><value xsi:type="ST">Tähän
         * lisää tekstiselvitystä, jos on tarpeen.</value></observation></component>
         */

        if ( !(laakemaarays instanceof LaakemaarayksenMitatointiTO) ) {
            return null;
        }
        LaakemaarayksenMitatointiTO mitatointiTO = (LaakemaarayksenMitatointiTO) laakemaarays;
        if ( onkoNullTaiTyhja(mitatointiTO.getMitatoinninSyyKoodi()) ) {
            return null;
        }

        // Observation = 95
        /*
         * <!-- Lääkemääräyksen mitätöintitiedot --> <!-- mitätöinnin syy --> <component> <observation classCode="OBS"
         * moodCode="EVN"> <code code="95" codeSystem="1.2.246.537.6.12.2002.126" codeSystemName="Lääkityslista"
         * displayName="Lääkemääräyksen mitätöinnin syy tekstinä"/> <!-- Mitätöinnin perustelu koodiarvolla ilmoitettuna
         * (pakollinen) uusi tieto versiossa 1.2.246.777.11.2015.2. --> <value code="2"
         * codeSystem="1.2.246.537.6.600.2013" codeSystemName="THL - Lääkehoidon muutoksen syy" displayName=
         * "Lääkkeen tarve on päättynyt" xsi:type="CE"/><!-- Mitätöinnin tekstimuotoinen perustelu (pakollinen, jos
         * koodiston mukaisena arvona annettu 5=Muu syy, muilla arvoilla vapaaehtoinen)--> <value xsi:type="ST">Tähän
         * lisää tekstiselvitystä, jos on tarpeen.</value></observation></component>
         */
        POCDMT000040Component4 mitatointiComp = of.createPOCDMT000040Component4();
        mitatointiComp.setObservation(of.createPOCDMT000040Observation());
        CE mitatointiCodeValue = of.createCE();
        fetchAttributes(mitatointiTO.getMitatoinninSyyKoodi() + ".muutoksensyy", mitatointiCodeValue);
        mitatointiCodeValue.setCode(mitatointiTO.getMitatoinninSyyKoodi());
        asetaObservation("95", mitatointiCodeValue, mitatointiComp.getObservation());
        if ( !onkoNullTaiTyhja(mitatointiTO.getMitatoinninPerustelu()) ) {
            ST mitatointiPerusteluValue = of.createST();
            mitatointiPerusteluValue.getContent().add(mitatointiTO.getMitatoinninPerustelu());
            mitatointiComp.getObservation().getValues().add(mitatointiPerusteluValue);
        }

        return mitatointiComp;
    }

    /**
     * Luo lääkemääräyksen mitatoinnin perustelun ja korjaajan tiedot
     *
     * @param laakemaarays
     *            LaakemaarayksenMitatointiTO josta tiedot haetaan
     * @return POCDMT000040Component4 elementin johon annetut mitatointitiedot asetettu
     */
    protected POCDMT000040Component4 luoMitatoinninTyyppi(LaakemaaraysTO laakemaarays) {

        if ( !(laakemaarays instanceof LaakemaarayksenMitatointiTO) ) {
            return null;
        }
        LaakemaarayksenMitatointiTO mitatointiTO = (LaakemaarayksenMitatointiTO) laakemaarays;
        if ( onkoNullTaiTyhja(mitatointiTO.getMitatoinninSyyKoodi()) ) {
            return null;
        }

        // Observation = 96
        /*
         * <!-- mitätöinnin tyyppi (pakollinen) --> <component> <observation classCode="OBS" moodCode="EVN"> <code
         * code="96" codeSystem="1.2.246.537.6.12.2002.126" codeSystemName="Lääkityslista" displayName=
         * "Lääkemääräyksen mitätöinnin tyyppi"/> <value xsi:type="CD" code="1" codeSystem="1.2.246.537.5.40103.2006"
         * codeSystemName="Lääkemääräyksen mitätöinnin tyyppi" displayName="Hoidollinen syy"> <!-- mitätöinnin osapuoli
         * (pakollinen)--> <qualifier> <name code="96.1" codeSystem="1.2.246.537.6.12.2002.126"
         * codeSystemName="Lääkityslista" displayName="Mitätöinnin osapuoli"/><value code="1"
         * codeSystem="1.2.246.537.5.40102.2006" codeSystemName="Lääkemääräyksen mitätöinnin osapuoli"
         * displayName="Lääkäri"/> </qualifier> <!-- potilaan antama suostumus (pakollinen)--> <qualifier> <name
         * code="96.2" codeSystem="1.2.246.537.6.12.2002.126" codeSystemName="Lääkityslista" displayName=
         * "Mitätöinnin suostumus"/><value code="1" codeSystem="1.2.246.537.5.40119.2006"
         * codeSystemName="Suostumustyypit" displayName="Suullinen suostumus"/> </qualifier>
         * </value></observation></component>
         */
        POCDMT000040Component4 mitatointiTyyppiComp = of.createPOCDMT000040Component4();
        mitatointiTyyppiComp.setObservation(of.createPOCDMT000040Observation());
        CE mitatointiTyyppiCodeValue = of.createCE();
        fetchAttributes(mitatointiTO.getMitatoinninTyyppiKoodi() + ".mitatoinnintyyppi", mitatointiTyyppiCodeValue);
        mitatointiTyyppiCodeValue.setCode(mitatointiTO.getMitatoinninTyyppiKoodi());

        CD mitatoinninTyyppiValue = of.createCD();

        // Lisää osapuoli
        CR qualifiersOsapuoli = of.createCR();

        CV cvNameOsapuoli = of.createCV();
        fetchAttributes("mitatoinninosapuoliName", cvNameOsapuoli);

        CD cdValueOsapuoli = of.createCD();

        // ospuolet: lääkäri, apteekki tai reseptikeskus.
        // Kelaimessa siis aina lääkäri
        fetchAttributes("mitatoinninosapuoliValue", cdValueOsapuoli);

        qualifiersOsapuoli.setName(cvNameOsapuoli);
        qualifiersOsapuoli.setValue(cdValueOsapuoli);

        mitatoinninTyyppiValue.getQualifiers().add(qualifiersOsapuoli);

        // Lisää suostumus
        CR qualifiersSuostumus = of.createCR();
        CV cvNameSuostumus = of.createCV();
        fetchAttributes("mitatoinninsuostumusName", cvNameSuostumus);

        CD cdValueSuostumus = of.createCD();
        fetchAttributes(mitatointiTO.getMitatoinninSuostumusKoodi() + ".suostumustyyppi", cdValueSuostumus);

        qualifiersSuostumus.setName(cvNameSuostumus);
        qualifiersSuostumus.setValue(cdValueSuostumus);
        mitatoinninTyyppiValue.getQualifiers().add(qualifiersSuostumus);
        mitatoinninTyyppiValue.setCode(mitatointiTO.getMitatoinninTyyppiKoodi());
        // inject data into observation
        mitatointiTyyppiComp.getObservation().getClassCodes().add("OBS");
        mitatointiTyyppiComp.getObservation().setMoodCode(XActMoodDocumentObservation.EVN);
        mitatointiTyyppiComp.getObservation().setCode(of.createCD());
        fetchAttributes("96", mitatointiTyyppiComp.getObservation().getCode());
        fetchAttributes(mitatointiTO.getMitatoinninTyyppiKoodi() + ".mitatoinnintyyppi", mitatoinninTyyppiValue);
        mitatointiTyyppiComp.getObservation().getCode().setCode("96");
        mitatointiTyyppiComp.getObservation().getValues().add(mitatoinninTyyppiValue);

        return mitatointiTyyppiComp;
    }

    /**
     * Palauttaa lääkemääräyksen mitätöinnin muut tiedot osion entry/organizer/code elementin coden
     *
     * @return String muut tiedot osion code elementin code atribuutin arvo
     */
    @Override
    protected String getMuutTiedotCode() {
        return KantaCDAConstants.Laakityslista.LAAKEMAARAYKSEN_MITATOINNIN_MUUT_TIEDOT;
    }

    /**
     * Kasaa reseptin mitätöintiasiakirjan konstruktorissa annetun LaakemaarayksenMitatointiTO:n tietojen perusteella.
     *
     * @return Reseptin mitätöintiasiakirja XML-muodossa
     * @throws JAXBException
     */
    @Override
    public String kasaaReseptiAsiakirja() throws JAXBException {
        return JaxbUtil.getInstance().marshalloi(kasaaReseptiCDA(), "urn:hl7-org:v3 CDA_Fi.xsd");
    }

    // TODO: Tarvitaanko tähän mitään mitätöinnin osalta edes?
    @Override
    protected void luoAsiakirjakohtaisetRakenteet(LaakemaaraysTO laakemaarays) {
        // TODO Auto-generated method stub
    }

    @Override
    protected POCDMT000040Entry luoAsiakirjanMuutTiedot(POCDMT000040Entry entry) {
        luoTartuntatautilainMukainen(entry);

        // Mitätöinnin perustelu ja korjaaja
        POCDMT000040Component4 mitatoinninPerusteluJaMitatoijaComponent = luoMitatoinninSyyPerusteluJaMitatoija(
                mitatointi);
        if ( null != mitatoinninPerusteluJaMitatoijaComponent ) {
            entry.getOrganizer().getComponents().add(mitatoinninPerusteluJaMitatoijaComponent);
        }

        POCDMT000040Component4 mitatoinninTyyppiComponent = luoMitatoinninTyyppi(mitatointi);
        if ( null != mitatoinninTyyppiComponent ) {
            entry.getOrganizer().getComponents().add(mitatoinninTyyppiComponent);
        }

        return entry;
    }

    /**
     * Kasaa reseptin mitätöintiasiakirjan konstruktorissa annetun LaakemaarayksenMitatointiTO:n tietojen perusteella.
     *
     * @return Reseptin mitätöintiasiakirja JAXB-elementteinä
     */
    @Override
    public POCDMT000040ClinicalDocument kasaaReseptiCDA() {
        return kasaaReseptinMitatointi();
    }

    /**
     * Luo lääkemääräyksen korjauksen perustelun ja korjaajan tiedot, kun ollaan mitätöimässä lääkemääräyksen korjausta.
     * Alkuperäiset perustelu ja korjaaja täytyy olla mitätöintisanomassa mukana.
     *
     * @param korjaus
     *            LaakemaarayksenMitatointiTO josta tiedot haetaan
     * @return POCDMT000040Component4 elementin johon annetut korjaus tiedot asetettu
     */
    @Override
    protected POCDMT000040Component4 luoKorjauksenSyyPerusteluJaKorjaaja(LaakemaaraysTO laakemaarays) {

        if ( !(laakemaarays instanceof LaakemaarayksenMitatointiTO) ) {
            return null;
        }
        LaakemaarayksenMitatointiTO mitatointiTO = (LaakemaarayksenMitatointiTO) laakemaarays;
        if ( onkoNullTaiTyhja(mitatointiTO.getAlkuperainenKorjauksenSyyKoodi()) ) {
            return null;
        }
        POCDMT000040Component4 korjausComp = of.createPOCDMT000040Component4();
        korjausComp.setObservation(of.createPOCDMT000040Observation());
        CE korjausCodeValue = of.createCE();
        fetchAttributes(mitatointiTO.getAlkuperainenKorjauksenSyyKoodi() + ".muutoksensyy", korjausCodeValue);
        korjausCodeValue.setCode(mitatointiTO.getAlkuperainenKorjauksenSyyKoodi());
        asetaObservation("97", korjausCodeValue, korjausComp.getObservation());
        if ( !onkoNullTaiTyhja(mitatointiTO.getAlkuperainenKorjauksenPerustelu()) ) {
            ST korjausPerusteluValue = of.createST();
            korjausPerusteluValue.getContent().add(mitatointiTO.getAlkuperainenKorjauksenPerustelu());
            korjausComp.getObservation().getValues().add(korjausPerusteluValue);
        }
        POCDMT000040Author author = of.createPOCDMT000040Author();
        author.setTime(of.createTS());
        author.getTime().getNullFlavors().add("NI");
        author.setAssignedAuthor(of.createPOCDMT000040AssignedAuthor());
        author.getAssignedAuthor().getIds().add(of.createII());
        author.getAssignedAuthor().getIds().get(0).getNullFlavors().add("NI");
        author.getAssignedAuthor().setAssignedPerson(of.createPOCDMT000040Person());
        author.getAssignedAuthor().getAssignedPerson().getNames()
                .add(getNames(mitatointiTO.getAlkuperainenKorjaaja().getKokonimi()));
        korjausComp.getObservation().getAuthors().add(author);
        return korjausComp;
    }

    private void alustaRelatedDocuments() {
        alkuperainenClinicalDocument.getRelatedDocuments().clear();
    }

    protected POCDMT000040Entry getLaakemaarayksenMuutTiedot(POCDMT000040Component5 component) {
        POCDMT000040Entry laakemaarayksenMuuttiedotEntry = findEntryByCode(component,
                KantaCDAConstants.Laakityslista.RESEPTIN_MUUT_TIEDOT);
        if ( laakemaarayksenMuuttiedotEntry == null ) {
            laakemaarayksenMuuttiedotEntry = findEntryByCode(component,
                    KantaCDAConstants.Laakityslista.LAAKEMAARAYKSEN_KORJAUKSEN_MUUT_TIEDOT);
        }
        return laakemaarayksenMuuttiedotEntry;
    }

    protected void luoTartuntatautilainMukainen(POCDMT000040Entry entry) {
        // Jos alkuperäisellä asiakirjalla ei ollut tietoa tartuntatautilain mukaisesta lääkevalmisteesta, lisätään se.
        POCDMT000040Component4 component = findComponentByCode(entry.getOrganizer().getComponents(),
                KantaCDAConstants.Laakityslista.TARTUNTATAUTILAIN_MUKAINEN_LAAKE);
        if ( component == null ) {
            entry.getOrganizer().getComponents().add(luoBLComponent(
                    KantaCDAConstants.Laakityslista.TARTUNTATAUTILAIN_MUKAINEN_LAAKE, false));
        }
    }

    protected POCDMT000040Component4 findComponentByCode(List<POCDMT000040Component4> components, String code) {
        for (POCDMT000040Component4 component : components) {
            if ( onkoObservationCodeCode(component.getObservation(), code) ) {
                return component;
            }
        }
        return null;
    }

    protected boolean onkoObservationCodeCode(POCDMT000040Observation observation, String code) {
        return null != observation && null != observation.getCode() && null != observation.getCode().getCode()
                && code.equals(observation.getCode().getCode());
    }
}
