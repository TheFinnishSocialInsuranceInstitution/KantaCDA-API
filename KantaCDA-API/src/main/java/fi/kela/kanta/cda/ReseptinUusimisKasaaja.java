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
import java.util.Properties;
import java.util.TimeZone;

import javax.xml.bind.JAXBException;

import org.codehaus.plexus.util.StringUtils;
import org.hl7.v3.CD;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component3;
import org.hl7.v3.POCDMT000040Component5;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040Reference;
import org.hl7.v3.XActRelationshipDocument;
import org.hl7.v3.XActRelationshipExternalReference;

import fi.kela.kanta.cda.validation.ReseptinUusimisValidoija;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.util.JaxbUtil;

public class ReseptinUusimisKasaaja extends ReseptiKasaaja {

    private final LaakemaaraysTO uusi;
    private final LaakemaaraysTO alkuperainen;
    private final String uusimispyynnonOid;
    private final String uusimispyynnonSetId;

    /**
     * Kasaa reseptin uusimisasiakirja
     *
     * @param properties
     *            resepti.properties KantaCDA-AP:ista.
     * @param uusi
     *            Uusi lääkemääräys, josta työstetään cda.
     * @param alkuperainen
     *            Alkuperäinen lääkemääräys, josta poimitaan uuteen määräykseen tarvittavat tiedot.
     * @param uusimispyynnonOid
     * @param uusimispyynnonSetId
     */
    public ReseptinUusimisKasaaja(Properties properties, LaakemaaraysTO uusi, LaakemaaraysTO alkuperainen,
            String uusimispyynnonOid, String uusimispyynnonSetId) {
        super(properties);
        this.uusi = uusi;
        this.alkuperainen = alkuperainen;
        this.uusimispyynnonOid = uusimispyynnonOid;
        this.uusimispyynnonSetId = uusimispyynnonSetId;

        paivataUusimisTiedot();

        validoija = new ReseptinUusimisValidoija(alkuperainen, uusi);
    }

    /**
     * Luo viittaukset alkuperäiseen lääkemääräykseen. (Kutsutaan 'isäntä' luokasta)
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tietoja haetaan
     * @return POCDMT000040Reference lista
     * @see ReseptiKasaaja#luoValmisteenJaPakkauksenTiedot(LaakemaaraysTO, String)
     */
    @Override
    protected Collection<POCDMT000040Reference> luoViittaukset(LaakemaaraysTO laakemaarays) {
        Collection<POCDMT000040Reference> viittaukset = new ArrayList<POCDMT000040Reference>();

        /*
         * Referencen typeCode on SPRT. Varsinaisessa reseptissä viitataan siis takaisin samaan dokumenttiin
         */
        CD code = of.createCD();
        fetchAttributes(Kasaaja.LM_CONTENTS, code);
        viittaukset.add(luoViittaus(laakemaarays.getOid(), laakemaarays.getSetId(),
                XActRelationshipExternalReference.SPRT, code));

        /*
         * Jos lääkemääräys on syntynyt uusimispyynnön seurauksena, viitataan myös uusimispyyntöön siten, että
         * typeCode=”REFR”.
         */
        CD alkupCode = of.createCD();
        fetchAttributes(Kasaaja.LM_UUSIMISPYYNTO, alkupCode);
        viittaukset.add(
                luoViittaus(uusimispyynnonOid, uusimispyynnonSetId, XActRelationshipExternalReference.REFR, alkupCode));
        return viittaukset;
    }

    /**
     * Palauttaa lääkemääräyksen uusimisen (=uusi resepti) muut tiedot osion entry/orgsnizer/code elementin coden
     *
     * @return String muut tiedot osion code elementin code atribuutin arvo
     */
    @Override
    protected String getMuutTiedotCode() {
        return KantaCDAConstants.Laakityslista.RESEPTIN_MUUT_TIEDOT;
    }

    /**
     * Kasaa reseptin uusimisasiakirja konstruktorissa annetun LaakemaaraysTO:n tietojen perusteella
     * 
     * @return Reseptin uusimisasiakirja XML-muodossa
     * @throws JAXBException
     */
    @Override
    public String kasaaReseptiAsiakirja() throws JAXBException {
        validoiLaakemaarays();
        return kasaaReseptinUusiminen();
    }

    @Override
    protected POCDMT000040Entry luoAsiakirjanMuutTiedot(POCDMT000040Entry entry) {
        return entry;
    }

    @Override
    protected void luoAsiakirjakohtaisetRakenteet(LaakemaaraysTO laakemaarays) {
    }

    /**
     * Luo lääkemääryksen tekemiseen käytettävän cda:n uusimistapausta varten.
     *
     * @param laakemaarays
     *            Lääkemääräys sisältäen määräystiedot cda:n luontia varten
     * @param alkuperainen
     *            Alkuperäinen lääkemääräys (ja sen määrääjän tiedot), johon viitataan uudesta määräyksestä
     * @param uusimispyynnonOid
     *            Uusimispyynnössä käytetty oid
     * @param uusimispyynnonSetId
     *            Uusimispyynnössä käytetty setId
     * @return CDA dokumentti Stringinä
     * @throws JAXBException
     */
    private String kasaaReseptinUusiminen() throws JAXBException {
        return JaxbUtil.getInstance().marshalloi(
                kasaaCdaRelatedDocumentTiedonKanssa(uusi, alkuperainen, uusimispyynnonOid, uusimispyynnonSetId),
                "urn:hl7-org:v3 CDA_Fi.xsd");
    }

    /**
     * Uusittavaan lääkemääräykseen tietojen asettaminen alkuperäisen lääkemääräyksen tietojen pohjalta, mikäli tietoja
     * ei vielä ennestään uudessa lääkemääräyksessä ole.
     */
    private void paivataUusimisTiedot() {

        // TODO: Onko tietojen kopiointi kasaajassa järkevää?
        if ( uusi != null && StringUtils.isEmpty(uusi.getOid()) ) {
            // korjaus.setOid(getDocumentId(laakemaarays));
            String oid = getDocumentId(uusi);
            uusi.setOid(oid);
            uusi.setSetId(oid);
        }

        if ( alkuperainen == null || uusi == null ) {
            return;
        }

        if ( uusi.getMaarayspaiva() == null ) {
            uusi.setMaarayspaiva(alkuperainen.getMaarayspaiva());
        }

        if ( uusi.getReseptintyyppi() == null ) {
            uusi.setReseptintyyppi(alkuperainen.getReseptintyyppi());
        }
        if ( uusi.getPakkauksienLukumaara() == null ) {
            uusi.setPakkauksienLukumaara(alkuperainen.getPakkauksienLukumaara());
        }
        if ( uusi.getLaakkeenKokonaismaaraValue() == null ) {
            uusi.setLaakkeenKokonaismaaraValue(alkuperainen.getLaakkeenKokonaismaaraValue());
        }
        if ( uusi.getLaakkeenKokonaismaaraUnit() == null ) {
            uusi.setLaakkeenKokonaismaaraUnit(alkuperainen.getLaakkeenKokonaismaaraUnit());
        }
        if ( uusi.getAjalleMaaratynReseptinAlkuaika() == null ) {
            uusi.setAjalleMaaratynReseptinAlkuaika(alkuperainen.getAjalleMaaratynReseptinAlkuaika());
        }
        if ( uusi.getAjalleMaaratynReseptinAikamaaraValue() == null ) {
            uusi.setAjalleMaaratynReseptinAikamaaraValue(alkuperainen.getAjalleMaaratynReseptinAikamaaraValue());
        }
        if ( uusi.getAjalleMaaratynReseptinAikamaaraUnit() == null ) {
            uusi.setAjalleMaaratynReseptinAikamaaraUnit(alkuperainen.getAjalleMaaratynReseptinAikamaaraUnit());
        }
        if ( uusi.getIterointiTeksti() == null ) {
            uusi.setIterointiTeksti(alkuperainen.getIterointiTeksti());
        }
        if ( uusi.getIterointienMaara() == null ) {
            uusi.setIterointienMaara(alkuperainen.getIterointienMaara());
        }
        if ( uusi.getIterointienValiValue() == null ) {
            uusi.setIterointienValiValue(alkuperainen.getIterointienValiValue());
        }
        if ( uusi.getIterointienValiUnit() == null ) {
            uusi.setIterointienValiUnit(alkuperainen.getIterointienValiUnit());
        }
        if ( uusi.getValmiste() == null ) {
            uusi.setValmiste(alkuperainen.getValmiste());
        }
        if ( uusi.getApteekissaValmistettavaLaake() == null ) {
            uusi.setApteekissaValmistettavaLaake(alkuperainen.getApteekissaValmistettavaLaake());
        }
        if ( uusi.getLaaketietokannanUlkopuolinenValmiste() == null ) {
            uusi.setLaaketietokannanUlkopuolinenValmiste(alkuperainen.getLaaketietokannanUlkopuolinenValmiste());
        }
        if ( uusi.getTyonantaja() == null ) {
            uusi.setTyonantaja(alkuperainen.getTyonantaja());
        }
        if ( uusi.getVakuutuslaitos() == null ) {
            uusi.setVakuutuslaitos(alkuperainen.getVakuutuslaitos());
        }
        if ( uusi.getAmmattihenkilo() == null ) {
            uusi.setAmmattihenkilo(alkuperainen.getAmmattihenkilo());
        }
        if ( uusi.getPotilas() == null ) {
            uusi.setPotilas(alkuperainen.getPotilas());
        }
        if ( uusi.isApteekissaValmistettavaLaake() == null ) {
            uusi.setApteekissaValmistettavaLaake(alkuperainen.isApteekissaValmistettavaLaake());
        }
        if ( uusi.isAnnosteluPelkastaanTekstimuodossa() == null ) {
            uusi.setAnnosteluPelkastaanTekstimuodossa(alkuperainen.isAnnosteluPelkastaanTekstimuodossa());
        }
        if ( uusi.getAnnostusohje() == null ) {
            uusi.setAnnostusohje(alkuperainen.getAnnostusohje());
        }
        if ( uusi.isSICmerkinta() == null ) {
            uusi.setSICmerkinta(alkuperainen.isSICmerkinta());
        }
        if ( uusi.isLaakevaihtokielto() == null ) {
            uusi.setLaakevaihtokielto(alkuperainen.isLaakevaihtokielto());
        }
        if ( uusi.getKayttotarkoitusTeksti() == null ) {
            uusi.setKayttotarkoitusTeksti(alkuperainen.getKayttotarkoitusTeksti());
        }
        if ( uusi.getAlle12VuotiaanPainoValue() == null ) {
            uusi.setAlle12VuotiaanPainoValue(alkuperainen.getAlle12VuotiaanPainoValue());
        }
        if ( uusi.getAlle12VuotiaanPainoUnit() == null ) {
            uusi.setAlle12VuotiaanPainoUnit(alkuperainen.getAlle12VuotiaanPainoUnit());
        }
        if ( uusi.isAnnosjakelu() == null ) {
            uusi.setAnnosjakelu(alkuperainen.isAnnosjakelu());
        }
        if ( uusi.getAnnosjakeluTeksti() == null ) {
            uusi.setAnnosjakeluTeksti(alkuperainen.getAnnosjakeluTeksti());
        }
        if ( uusi.getHoitolajit().isEmpty() ) {
            uusi.getHoitolajit().addAll(alkuperainen.getHoitolajit());
        }
        if ( uusi.getViestiApteekille() == null ) {
            uusi.setViestiApteekille(alkuperainen.getViestiApteekille());
        }
        if ( uusi.getErillisselvitys() == null ) {
            uusi.setErillisselvitys(alkuperainen.getErillisselvitys());
        }
        if ( uusi.getErillisselvitysteksti() == null ) {
            uusi.setErillisselvitysteksti(alkuperainen.getErillisselvitysteksti());
        }
        if ( uusi.getPotilaanTunnistaminen() == null ) {
            uusi.setPotilaanTunnistaminen(alkuperainen.getPotilaanTunnistaminen());
        }
        if ( uusi.getPotilaanTunnistaminenTeksti() == null ) {
            uusi.setPotilaanTunnistaminenTeksti(alkuperainen.getPotilaanTunnistaminenTeksti());
        }
        if ( uusi.getPKVlaakemaarays() == null ) {
            uusi.setPKVlaakemaarays(alkuperainen.getPKVlaakemaarays());
        }
        if ( uusi.isPysyvaislaakitys() == null ) {
            uusi.setPysyvaislaakitys(alkuperainen.isPysyvaislaakitys());
        }
        if ( uusi.isKyseessaLaakkeenkaytonAloitus() == null ) {
            uusi.setKyseessaLaakkeenkaytonAloitus(alkuperainen.isKyseessaLaakkeenkaytonAloitus());
        }
        if ( uusi.isHuume() == null ) {
            uusi.setHuume(alkuperainen.isHuume());
        }
        if ( uusi.getReseptinLaji() == null ) {
            uusi.setReseptinLaji(alkuperainen.getReseptinLaji());
        }
        if ( uusi.isUudistamiskielto() == null ) {
            uusi.setUudistamiskielto(alkuperainen.isUudistamiskielto());
        }
        if ( uusi.getUusimiskiellonSyy() == null ) {
            uusi.setUusimiskiellonSyy(alkuperainen.getUusimiskiellonSyy());
        }
        if ( uusi.getUusimiskiellonPerustelu() == null ) {
            uusi.setUusimiskiellonPerustelu(alkuperainen.getUusimiskiellonPerustelu());
        }
        if ( uusi.getLaaketietokannanVersio() == null ) {
            uusi.setLaaketietokannanVersio(alkuperainen.getLaaketietokannanVersio());
        }
        if ( uusi.getApteekissaTallennettuLaakemaarays() == null ) {
            uusi.setApteekissaTallennettuLaakemaarays(alkuperainen.getApteekissaTallennettuLaakemaarays());
        }
        if ( uusi.getTartuntatauti() == null ) {
            uusi.setTartuntatauti(alkuperainen.getTartuntatauti());
        }
    }

    /**
     * Luo lääkemääräyksen annetuista tiedoista, lisäten related document osion, jolla viitataan aiempaan
     * lääkemääräykseen johon tämä (uusittava/korjattava/...) lääkemääräys viittaa.
     *
     * @param laakemaarays
     *            Lääkemääräys TO, jonka pohjalta cda luodaan
     * @param alkuperainenLaakemaarays
     *            Alkuperäinen lääkemääräys, johon viitataan related document osiossa, sekä josta katsotaan alkuperäisen
     *            määrääjän author tiedot
     * @param rooli
     *            Rooli, jota käytetään määräyksen tekijäksi cda:ssa (kts. {@link http
     *            ://91.202.112.142/codeserver/pages/code-list-page.xhtml?versionKey=347})
     * @param uusimispyynnonOid
     *            Oid joka on luotu uusimispyyntöön. Anna "tyhjä", jos käytetään alkuperäisen lääkemääräyksen oidia
     *            viittauksessa
     * @param uusimispyynnonOid
     *            Uusimispyynnössä käytetty oid
     * @param uusimispyynnonSetId
     *            Uusimispyynnössä käytetty setId
     * @return Muodostettu cda
     */
    protected POCDMT000040ClinicalDocument kasaaCdaRelatedDocumentTiedonKanssa(LaakemaaraysTO laakemaarays,
            LaakemaaraysTO alkuperaisetTiedot, String uusimispyynnonOid, String uusimispyynnonSetId) {

        String relatedOid, relatedSetId;
        // uusiminen
        ((ReseptinUusimisValidoija) validoija).validoiAlkuperainenLaakemaarays();
        if ( StringUtils.isEmpty(uusimispyynnonOid) ) {
            throw new IllegalArgumentException("Uusimispyynnön oid pitää antaa.");
        }
        else if ( StringUtils.isEmpty(uusimispyynnonSetId) ) {
            throw new IllegalArgumentException("Uusimispyynnön setId pitää antaa.");
        }
        relatedOid = uusimispyynnonOid;
        relatedSetId = uusimispyynnonSetId;

        Calendar now = Calendar.getInstance(TimeZone.getTimeZone(ReseptiKasaaja.TIME_ZONE));
        String effectiveTimeValue = getDateFormat().format(now.getTime());
        String today = getTodayDateFormat().format(now.getTime());

        POCDMT000040ClinicalDocument clinicalDocument = of.createPOCDMT000040ClinicalDocument();

        addIdFields(clinicalDocument, laakemaarays, effectiveTimeValue);
        Object[] args = { MaaraajanRooli.LAAK_ALOIT_HENK.getPropertyAvaimenOsa() };
        // Asetetaan title ja code
        fetchAttributes(String.format(ReseptiKasaaja.code, args), clinicalDocument.getCode());
        clinicalDocument.getTitle().getContent().clear();

        clinicalDocument.getTitle().getContent().add(fetchProperty(String.format(ReseptiKasaaja.title, args)));
        laakemaarays.getAmmattihenkilo().setRooli(MaaraajanRooli.LAAK_ALOIT_HENK.getRooliKoodi());
        addRecordTarget(clinicalDocument, laakemaarays.getPotilas());

        // Uusija
        addAuthor(clinicalDocument, luoAuthor(laakemaarays.getAmmattihenkilo()));

        addCustodian(clinicalDocument);

        // Valitse relaation tyyppi
        addRelatedDocument(clinicalDocument, relatedOid, relatedSetId,
                getPropertyCode(KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYS.getTyyppi(), args),
                XActRelationshipDocument.APND);

        addComponentOf(clinicalDocument, getDateFormat().format(laakemaarays.getMaarayspaiva())/* effectiveTimeValue */,
                laakemaarays.getAmmattihenkilo().getOrganisaatio(), laakemaarays.getPalvelutapahtumanOid());
        addLocalHeader(clinicalDocument);

        clinicalDocument.setComponent(of.createPOCDMT000040Component2());
        clinicalDocument.getComponent().setStructuredBody(of.createPOCDMT000040StructuredBody());

        POCDMT000040Component3 component3 = of.createPOCDMT000040Component3();
        clinicalDocument.getComponent().getStructuredBody().getComponents().add(component3);

        component3.getTemplateIds().add(of.createPOCDMT000040InfrastructureRootTemplateId());
        // TemplateId
        fetchAttributes(ReseptiKasaaja.template_id, component3.getTemplateIds().get(0));
        component3.setSection(of.createPOCDMT000040Section());
        component3.getSection().setAttributeID(getNextOID(laakemaarays));
        component3.getSection().setId(of.createII());
        component3.getSection().getId().setRoot(getId(laakemaarays));
        component3.getSection().setCode(of.createCE());
        fetchAttributes(String.format(ReseptiKasaaja.code, args), component3.getSection().getCode());
        component3.getSection().setTitle(of.createST());
        // Title
        component3.getSection().getTitle().getContent().add(component3.getSection().getCode().getDisplayName());

        POCDMT000040Component5 component5 = luoComponent(laakemaarays);
        component3.getSection().getComponents().add(component5);
        // Narrative (paikka, aika, lääkäri)
        component5.getSection().setText(luoNarrativePaikkaPvmLaakari(laakemaarays, today));

        POCDMT000040Component5 component6 = luoComponent(laakemaarays);
        component5.getSection().getComponents().add(component6);
        // Narrative (lääkemääräyksen tiedot)
        component6.getSection().setText(luoNarrativeLaakemaarays(laakemaarays));
        // Valmisteen ja Pakkausten tiedot
        component6.getSection().getEntries().add(
                luoValmisteenJaPakkauksenTiedot(laakemaarays, effectiveTimeValue, laakemaarays.getAmmattihenkilo()));

        // Vaikuttavat aineet
        if ( lisataankoVaikuttavatAineet(laakemaarays) ) {
            component6.getSection().getEntries().add(luoVaikuttavatAinesosat(laakemaarays));
        }
        // Muut ainesosat
        // Vain apteekissa valmistettaville?
        if ( laakemaarays.isApteekissaValmistettavaLaake() ) {
            component6.getSection().getEntries().add(luoMuutAinesosat(laakemaarays));
        }

        // annostus
        component6.getSection().getEntries().add(luoAnnostus(laakemaarays));

        // Lääkemääräyksen muut tiedot
        component6.getSection().getEntries().add(luoMuutTiedot(laakemaarays));

        return clinicalDocument;
    }

    /**
     * Kasaa reseptin uusimisasiakirja konstruktorissa annetun LaakemaaraysTO:n tietojen perusteella.
     * 
     * @return Reseptin uusimisasiakirja JAXB-elementteinä
     */
    @Override
    public POCDMT000040ClinicalDocument kasaaReseptiCDA() {
        validoiLaakemaarays();
        return kasaaCdaRelatedDocumentTiedonKanssa(uusi, alkuperainen, uusimispyynnonOid, uusimispyynnonSetId);
    }

}
