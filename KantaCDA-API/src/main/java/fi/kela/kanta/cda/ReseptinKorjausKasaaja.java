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

import org.hl7.v3.CD;
import org.hl7.v3.CE;
import org.hl7.v3.POCDMT000040Author;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component3;
import org.hl7.v3.POCDMT000040Component4;
import org.hl7.v3.POCDMT000040Component5;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040Reference;
import org.hl7.v3.ST;
import org.hl7.v3.XActRelationshipDocument;
import org.hl7.v3.XActRelationshipExternalReference;

import fi.kela.kanta.cda.validation.ReseptinKorjausValidoija;
import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.LaakemaarayksenKorjausTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.util.JaxbUtil;

public class ReseptinKorjausKasaaja extends ReseptiKasaaja {

    LaakemaaraysTO alkuperainenLaakemaarays;
    LaakemaarayksenKorjausTO korjaus;
    ReseptinKorjausValidoija validoija;

    protected static final String VIRHE_KORJAUS_NULL = "Laakemaarayksen korjaus ei saa olla null.";
    protected static final String VIRHE_KORJAAJA_NULL = "Laakemaarayksen korjaaja ei saa olla null.";
    protected static final String VIRHE_KORJAUKSEN_SYY_KOODI_NULL_TAI_TYHJA = "Laakemaarayksen korjauksen syy koodi ei saa olla null tai tyhja.";
    protected static final String VIRHE_KORJAUKSEN_PERUSTELU_NULL_TAI_TYHJA = "Laakemaarayksen korjauksen perustelu ei saa olla null tai tyhja.";
    protected static final String VIRHE_ALKUPERAINEN_LAAKEMAARAYS_NULL = "Alkuperäinen lääkemääräys ei saa olla null.";
    protected static final String VIRHE_ALKUPERAISEN_LAAKEMAARAYKSEN_MAARAYSPAIVA_NULL = "Alkuperäisen lääkemääräyksen 'määräyspäivä' ei saa olla null.";
    protected static final String VIRHE_ALKUPERAISEN_LAAKEMAARAYKSEN_OID_NULL = "Alkuperäisen lääkemääräyksen 'oid' pitää löytyä.";
    protected static final String VIRHE_ALKUPERAISEN_LAAKEMAARAYKSEN_SETID_NULL = "Alkuperäisen lääkemääräyksen 'setid' pitää löytyä.";

    public ReseptinKorjausKasaaja(Properties properties, LaakemaarayksenKorjausTO korjaus,
            LaakemaaraysTO alkuperainenLaakemaarays) {
        super(properties);
        this.korjaus = korjaus;
        this.alkuperainenLaakemaarays = alkuperainenLaakemaarays;
        validoija = new ReseptinKorjausValidoija(korjaus, alkuperainenLaakemaarays);
    }

    /**
     * Täyttää LaakemaarayksenKorjausTOn tyhjät kentät alkuperäisestä lääkemääräyksestä. Asettaa myös korjauksen oidin,
     * setIdn, version ja alkuperainenOit tiedot.
     *
     * @param korjaus
     *            LaakemaarayksenKorjausTO johon poimittavat tiedot sijoitetaan.
     * @param alkuperainenLaakemaarays
     *            LaakemaaraysTO josta tiedot poimitaan.
     */
    protected void paivataKorjaus() {
        if ( alkuperainenLaakemaarays == null ) {
            return;
        }

        korjaus.setAlkuperainenOid(alkuperainenLaakemaarays.getOid());
        korjaus.setSetId(alkuperainenLaakemaarays.getSetId());
        korjaus.setVersio(alkuperainenLaakemaarays.getVersio());
        korjaus.setAlkuperainenCdaTyyppi(alkuperainenLaakemaarays.getCdaTyyppi());

        if ( korjaus.getMaarayspaiva() == null ) {
            korjaus.setMaarayspaiva(alkuperainenLaakemaarays.getMaarayspaiva());
        }

        if ( korjaus.getReseptintyyppi() == null ) {
            korjaus.setReseptintyyppi(alkuperainenLaakemaarays.getReseptintyyppi());
        }
        if ( korjaus.getPakkauksienLukumaara() == null ) {
            korjaus.setPakkauksienLukumaara(alkuperainenLaakemaarays.getPakkauksienLukumaara());
        }
        if ( korjaus.getLaakkeenKokonaismaaraValue() == null ) {
            korjaus.setLaakkeenKokonaismaaraValue(alkuperainenLaakemaarays.getLaakkeenKokonaismaaraValue());
        }
        if ( korjaus.getLaakkeenKokonaismaaraUnit() == null ) {
            korjaus.setLaakkeenKokonaismaaraUnit(alkuperainenLaakemaarays.getLaakkeenKokonaismaaraUnit());
        }
        if ( korjaus.getAjalleMaaratynReseptinAlkuaika() == null ) {
            korjaus.setAjalleMaaratynReseptinAlkuaika(alkuperainenLaakemaarays.getAjalleMaaratynReseptinAlkuaika());
        }
        if ( korjaus.getAjalleMaaratynReseptinAikamaaraValue() == null ) {
            korjaus.setAjalleMaaratynReseptinAikamaaraValue(
                    alkuperainenLaakemaarays.getAjalleMaaratynReseptinAikamaaraValue());
        }
        if ( korjaus.getAjalleMaaratynReseptinAikamaaraUnit() == null ) {
            korjaus.setAjalleMaaratynReseptinAikamaaraUnit(
                    alkuperainenLaakemaarays.getAjalleMaaratynReseptinAikamaaraUnit());
        }
        if ( korjaus.getIterointiTeksti() == null ) {
            korjaus.setIterointiTeksti(alkuperainenLaakemaarays.getIterointiTeksti());
        }
        if ( korjaus.getIterointienMaara() == null ) {
            korjaus.setIterointienMaara(alkuperainenLaakemaarays.getIterointienMaara());
        }
        if ( korjaus.getIterointienValiValue() == null ) {
            korjaus.setIterointienValiValue(alkuperainenLaakemaarays.getIterointienValiValue());
        }
        if ( korjaus.getIterointienValiUnit() == null ) {
            korjaus.setIterointienValiUnit(alkuperainenLaakemaarays.getIterointienValiUnit());
        }
        if ( korjaus.getValmiste() == null ) {
            korjaus.setValmiste(alkuperainenLaakemaarays.getValmiste());
        }
        if ( korjaus.getApteekissaValmistettavaLaake() == null ) {
            korjaus.setApteekissaValmistettavaLaake(alkuperainenLaakemaarays.getApteekissaValmistettavaLaake());
        }
        if ( korjaus.getLaaketietokannanUlkopuolinenValmiste() == null ) {
            korjaus.setLaaketietokannanUlkopuolinenValmiste(
                    alkuperainenLaakemaarays.getLaaketietokannanUlkopuolinenValmiste());
        }
        if ( korjaus.getTyonantaja() == null ) {
            korjaus.setTyonantaja(alkuperainenLaakemaarays.getTyonantaja());
        }
        if ( korjaus.getVakuutuslaitos() == null ) {
            korjaus.setVakuutuslaitos(alkuperainenLaakemaarays.getVakuutuslaitos());
        }
        if ( korjaus.getAmmattihenkilo() == null ) {
            korjaus.setAmmattihenkilo(alkuperainenLaakemaarays.getAmmattihenkilo());
        }
        if ( korjaus.getPotilas() == null ) {
            korjaus.setPotilas(alkuperainenLaakemaarays.getPotilas());
        }
        if ( korjaus.isApteekissaValmistettavaLaake() == null ) {
            korjaus.setApteekissaValmistettavaLaake(alkuperainenLaakemaarays.isApteekissaValmistettavaLaake());
        }
        if ( korjaus.isAnnosteluPelkastaanTekstimuodossa() == null ) {
            korjaus.setAnnosteluPelkastaanTekstimuodossa(
                    alkuperainenLaakemaarays.isAnnosteluPelkastaanTekstimuodossa());
        }
        if ( korjaus.getAnnostusohje() == null ) {
            korjaus.setAnnostusohje(alkuperainenLaakemaarays.getAnnostusohje());
        }
        if ( korjaus.isSICmerkinta() == null ) {
            korjaus.setSICmerkinta(alkuperainenLaakemaarays.isSICmerkinta());
        }
        if ( korjaus.isLaakevaihtokielto() == null ) {
            korjaus.setLaakevaihtokielto(alkuperainenLaakemaarays.isLaakevaihtokielto());
        }
        if ( korjaus.getKayttotarkoitusTeksti() == null ) {
            korjaus.setKayttotarkoitusTeksti(alkuperainenLaakemaarays.getKayttotarkoitusTeksti());
        }
        if ( korjaus.getAlle12VuotiaanPainoValue() == null ) {
            korjaus.setAlle12VuotiaanPainoValue(alkuperainenLaakemaarays.getAlle12VuotiaanPainoValue());
        }
        if ( korjaus.getAlle12VuotiaanPainoUnit() == null ) {
            korjaus.setAlle12VuotiaanPainoUnit(alkuperainenLaakemaarays.getAlle12VuotiaanPainoUnit());
        }
        if ( korjaus.isAnnosjakelu() == null ) {
            korjaus.setAnnosjakelu(alkuperainenLaakemaarays.isAnnosjakelu());
        }
        if ( korjaus.getAnnosjakeluTeksti() == null ) {
            korjaus.setAnnosjakeluTeksti(alkuperainenLaakemaarays.getAnnosjakeluTeksti());
        }
        if ( korjaus.getHoitolajit().isEmpty() ) {
            korjaus.getHoitolajit().addAll(alkuperainenLaakemaarays.getHoitolajit());
        }
        if ( korjaus.getViestiApteekille() == null ) {
            korjaus.setViestiApteekille(alkuperainenLaakemaarays.getViestiApteekille());
        }
        if ( korjaus.getErillisselvitys() == null ) {
            korjaus.setErillisselvitys(alkuperainenLaakemaarays.getErillisselvitys());
        }
        if ( korjaus.getErillisselvitysteksti() == null ) {
            korjaus.setErillisselvitysteksti(alkuperainenLaakemaarays.getErillisselvitysteksti());
        }
        if ( korjaus.getPotilaanTunnistaminen() == null ) {
            korjaus.setPotilaanTunnistaminen(alkuperainenLaakemaarays.getPotilaanTunnistaminen());
        }
        if ( korjaus.getPotilaanTunnistaminenTeksti() == null ) {
            korjaus.setPotilaanTunnistaminenTeksti(alkuperainenLaakemaarays.getPotilaanTunnistaminenTeksti());
        }
        if ( korjaus.getPKVlaakemaarays() == null ) {
            korjaus.setPKVlaakemaarays(alkuperainenLaakemaarays.getPKVlaakemaarays());
        }
        if ( korjaus.isPysyvaislaakitys() == null ) {
            korjaus.setPysyvaislaakitys(alkuperainenLaakemaarays.isPysyvaislaakitys());
        }
        if ( korjaus.isKyseessaLaakkeenkaytonAloitus() == null ) {
            korjaus.setKyseessaLaakkeenkaytonAloitus(alkuperainenLaakemaarays.isKyseessaLaakkeenkaytonAloitus());
        }
        if ( korjaus.isHuume() == null ) {
            korjaus.setHuume(alkuperainenLaakemaarays.isHuume());
        }
        if ( korjaus.getReseptinLaji() == null ) {
            korjaus.setReseptinLaji(alkuperainenLaakemaarays.getReseptinLaji());
        }
        if ( korjaus.isUudistamiskielto() == null ) {
            korjaus.setUudistamiskielto(alkuperainenLaakemaarays.isUudistamiskielto());
        }
        if ( korjaus.getUusimiskiellonSyy() == null ) {
            korjaus.setUusimiskiellonSyy(alkuperainenLaakemaarays.getUusimiskiellonSyy());
        }
        if ( korjaus.getUusimiskiellonPerustelu() == null ) {
            korjaus.setUusimiskiellonPerustelu(alkuperainenLaakemaarays.getUusimiskiellonPerustelu());
        }
        if ( korjaus.getLaaketietokannanVersio() == null ) {
            korjaus.setLaaketietokannanVersio(alkuperainenLaakemaarays.getLaaketietokannanVersio());
        }
        if ( korjaus.getApteekissaTallennettuLaakemaarays() == null ) {
            korjaus.setApteekissaTallennettuLaakemaarays(
                    alkuperainenLaakemaarays.getApteekissaTallennettuLaakemaarays());
        }
        if ( korjaus.getTartuntatauti() == null ) {
            korjaus.setTartuntatauti(alkuperainenLaakemaarays.getTartuntatauti());
        }
        if ( korjaus.getApteekissaTallennettuLaakemaaraysPerustelu() == null ) {
            korjaus.setApteekissaTallennettuLaakemaaraysPerustelu(
                    alkuperainenLaakemaarays.getApteekissaTallennettuLaakemaaraysPerustelu());
        }
        if ( korjaus.getApteekissaTallennettuLaakemaaraysMuuSyy() == null ) {
            korjaus.setApteekissaTallennettuLaakemaaraysMuuSyy(
                    alkuperainenLaakemaarays.getApteekissaTallennettuLaakemaaraysMuuSyy());
        }
    }

    /**
     * Luo lääkemääräyksen korjauksen LaakemaarayksenKorjausTO tiedoista.
     *
     * @param korjaus
     *            LaakemaarayksenKorjausTO josta korjauksen tiedot poimitaan.
     * @param alkuperainenLaakemaarays
     *            alkuperainen LaakemaaraysTO.
     * @return lääkemääräyksen korjaus cda POCDMT00040ClinicalDocumenttinä
     */
    private POCDMT000040ClinicalDocument kasaaReseptinKorjaus(LaakemaarayksenKorjausTO korjaus,
            LaakemaaraysTO alkuperainenLaakemaarays) {

        validoija.validoiKorjaus();
        paivataKorjaus();
        // Varmistetaan vielä, että pakollisen potilaan tiedot on korjauksella.
        validoija.validoiHenkilotiedot(korjaus.getPotilas());
        AmmattihenkiloTO tmp = korjaus.getAmmattihenkilo();
        POCDMT000040ClinicalDocument cda = null;
        try {
            // Asettaa korjaajan käsittelijäksi (koska ko. tieto otetaan tästä).
            // Alkuperäisen määrääjän tiedot otetaan kuitenkin alkuperäisestä määräyksestä
            korjaus.setAmmattihenkilo(korjaus.getKorjaaja());
            cda = kasaaCdaRelatedDocumentTiedonKanssa(korjaus, alkuperainenLaakemaarays);
        }
        finally {
            // Palauta tiedot kuten oli aluksikin
            korjaus.setAmmattihenkilo(tmp);
        }
        return cda;
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
        fetchAttributes("korjaus.code", code);

        // viittaus itseensä
        viittaukset.add(luoViittaus(oid, setId, XActRelationshipExternalReference.SPRT, code));

        // Viittaus alkuperaiseen lääkemääräykseen
        CD alkupCode = of.createCD();
        if ( laakemaarays instanceof LaakemaarayksenKorjausTO ) {
            if ( ((LaakemaarayksenKorjausTO) laakemaarays)
                    .getAlkuperainenCdaTyyppi() == KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYKSEN_KORJAUS
                            .getTyyppi() ) {
                fetchAttributes("korjaus.code", alkupCode);
            }
            else {
                fetchAttributes(Kasaaja.LM_CONTENTS, alkupCode);
            }
            viittaukset.add(luoViittaus(((LaakemaarayksenKorjausTO) laakemaarays).getAlkuperainenOid(), setId,
                    XActRelationshipExternalReference.RPLC, alkupCode));
        }
        return viittaukset;
    }

    /**
     * Luo lääkemääräyksen korjauksen perustelun ja korjaajan tiedot
     *
     * @param korjaus
     *            LaakemaarayksenKorjausTO josta tiedot haetaan
     * @return POCDMT000040Component4 elementin johon annetut korjaus tiedot asetettu
     */
    @Override
    protected POCDMT000040Component4 luoKorjauksenSyyPerusteluJaKorjaaja(LaakemaaraysTO laakemaarays) {

        if ( !(laakemaarays instanceof LaakemaarayksenKorjausTO) ) {
            return null;
        }
        LaakemaarayksenKorjausTO korjausTO = (LaakemaarayksenKorjausTO) laakemaarays;
        if ( onkoNullTaiTyhja(korjausTO.getKorjauksenSyyKoodi()) ) {
            return null;
        }
        POCDMT000040Component4 korjausComp = of.createPOCDMT000040Component4();
        korjausComp.setObservation(of.createPOCDMT000040Observation());
        CE korjausCodeValue = of.createCE();
        fetchAttributes(korjausTO.getKorjauksenSyyKoodi() + ".muutoksensyy", korjausCodeValue);
        korjausCodeValue.setCode(korjausTO.getKorjauksenSyyKoodi());
        asetaObservation("97", korjausCodeValue, korjausComp.getObservation());
        if ( !onkoNullTaiTyhja(korjausTO.getKorjauksenPerustelu()) ) {
            ST korjausPerusteluValue = of.createST();
            korjausPerusteluValue.getContent().add(korjausTO.getKorjauksenPerustelu());
            korjausComp.getObservation().getValues().add(korjausPerusteluValue);
        }
        POCDMT000040Author author = of.createPOCDMT000040Author();
        author.setTime(of.createTS());
        author.getTime().getNullFlavors().add("NI");
        author.setAssignedAuthor(of.createPOCDMT000040AssignedAuthor());
        author.getAssignedAuthor().getIds().add(of.createII());
        author.getAssignedAuthor().getIds().get(0).getNullFlavors().add("NI");
        author.getAssignedAuthor().setAssignedPerson(of.createPOCDMT000040Person());
        author.getAssignedAuthor().getAssignedPerson().getNames().add(getNames(korjausTO.getKorjaaja().getKokonimi()));
        korjausComp.getObservation().getAuthors().add(author);
        return korjausComp;
    }

    /**
     * Palauttaa lääkemääräyksen korjauksen muut tiedot osion entry/orgsnizer/code elementin coden
     *
     * @return String muut tiedot osion code elementin code atribuutin arvo
     */
    @Override
    protected String getMuutTiedotCode() {
        return "99";
    }

    /**
     * Palauttaa CDA-asiakirjan perustuen konstruktorissa annetun ReseptikorjausTO:n tietoihin.
     * 
     * @return String CDA-asiakirja XML-muodossa
     */
    @Override
    public String kasaaReseptiAsiakirja() throws JAXBException {
        return JaxbUtil.getInstance().marshalloi(kasaaReseptiCDA(), "urn:hl7-org:v3 CDA_Fi.xsd");
    }

    @Override
    protected POCDMT000040Entry luoAsiakirjanMuutTiedot(POCDMT000040Entry entry) {
        if ( null != entry.getOrganizer() ) {
            entry.getOrganizer().getComponents().add(luoKorjauksenSyyPerusteluJaKorjaaja(korjaus));
        }
        return entry;
    }

    @Override
    protected void luoAsiakirjakohtaisetRakenteet(LaakemaaraysTO laakemaarays) {
        // ei mitään
    }

    /**
     * Palauttaa CDA-asiakirjan perustuen konstruktorissa annetun ReseptikorjausTO:n tietoihin.
     * 
     * @return POCDMT000040ClinicalDocument CDA-asiakirja JAXB-elementteinä
     */
    @Override
    public POCDMT000040ClinicalDocument kasaaReseptiCDA() {
        return kasaaReseptinKorjaus(korjaus, alkuperainenLaakemaarays);
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
            LaakemaaraysTO alkuperaisetTiedot) {

        String relatedOid, relatedSetId;

        validoija.validoiAlkuperainenLaakemaarays();
        relatedOid = alkuperaisetTiedot.getOid();
        relatedSetId = alkuperaisetTiedot.getSetId();

        Calendar now = Calendar.getInstance(TimeZone.getTimeZone(ReseptiKasaaja.TIME_ZONE));
        String effectiveTimeValue = getDateFormat().format(now.getTime());
        String today = getTodayDateFormat().format(now.getTime());

        POCDMT000040ClinicalDocument clinicalDocument = of.createPOCDMT000040ClinicalDocument();

        addIdFields(clinicalDocument, laakemaarays, effectiveTimeValue);
        Object[] args = { MaaraajanRooli.KORJAAJA.getPropertyAvaimenOsa() };
        // Asetetaan title ja code
        fetchAttributes(String.format(ReseptiKasaaja.code, args), clinicalDocument.getCode());
        clinicalDocument.getTitle().getContent().clear();
        clinicalDocument.getTitle().getContent().add(fetchProperty(String.format(ReseptiKasaaja.title, args)));
        // Korjauksessa korjaajan rooli on oltava "KOR", uusimisessa "LAL"
        laakemaarays.getAmmattihenkilo().setRooli(MaaraajanRooli.KORJAAJA.getRooliKoodi());
        addRecordTarget(clinicalDocument, laakemaarays.getPotilas());

        // Edellisen lääkemääräyksen tehnyt ammattihenkilö (lisätään vain korjauksessa)
        addAuthor(clinicalDocument, luoAuthor(alkuperaisetTiedot.getAmmattihenkilo()));
        // Mahdollinen edellisen lääkemääräyksen kirjaaja (esim. apteekki)
        if ( alkuperaisetTiedot.getKirjaaja() != null ) {
            addAuthor(clinicalDocument, luoAuthor(alkuperaisetTiedot.getKirjaaja()));
        }
        // Uusija / korjaaja
        addAuthor(clinicalDocument, luoAuthor(laakemaarays.getAmmattihenkilo()));

        addCustodian(clinicalDocument);

        addRelatedDocument(clinicalDocument, relatedOid, relatedSetId,
                getPropertyCode(alkuperaisetTiedot.getCdaTyyppi(), args), XActRelationshipDocument.RPLC);

        addComponentOf(clinicalDocument, getDateFormat().format(laakemaarays.getMaarayspaiva())/* effectiveTimeValue */,
                alkuperaisetTiedot.getLaatimispaikka(), alkuperaisetTiedot.getPalvelutapahtumanOid());
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
        component5.getSection().setText(luoKorjausNarrativePaikkaPvmLaakari(alkuperaisetTiedot, laakemaarays, today));

        POCDMT000040Component5 component6 = luoComponent(laakemaarays);
        component5.getSection().getComponents().add(component6);
        // Narrative (lääkemääräyksen tiedot)
        component6.getSection().setText(luoNarrativeLaakemaarays(laakemaarays));
        // Valmisteen ja Pakkausten tiedot
        component6.getSection().getEntries().add(luoValmisteenJaPakkauksenTiedot(laakemaarays, effectiveTimeValue,
                alkuperaisetTiedot.getAmmattihenkilo()));

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

}
