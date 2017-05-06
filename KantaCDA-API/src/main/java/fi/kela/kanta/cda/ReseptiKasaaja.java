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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.hl7.v3.ANY;
import org.hl7.v3.ActClassSupply;
import org.hl7.v3.BL;
import org.hl7.v3.CD;
import org.hl7.v3.CE;
import org.hl7.v3.ED;
import org.hl7.v3.INT;
import org.hl7.v3.IVLPQ;
import org.hl7.v3.IVLTS;
import org.hl7.v3.MO;
import org.hl7.v3.PN;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component4;
import org.hl7.v3.POCDMT000040Component5;
import org.hl7.v3.POCDMT000040Consumable;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040EntryRelationship;
import org.hl7.v3.POCDMT000040Observation;
import org.hl7.v3.POCDMT000040Participant2;
import org.hl7.v3.POCDMT000040Product;
import org.hl7.v3.POCDMT000040Reference;
import org.hl7.v3.POCDMT000040Subject;
import org.hl7.v3.POCDMT000040SubstanceAdministration;
import org.hl7.v3.POCDMT000040Supply;
import org.hl7.v3.PQ;
import org.hl7.v3.PQR;
import org.hl7.v3.ParticipationTargetSubject;
import org.hl7.v3.SC;
import org.hl7.v3.ST;
import org.hl7.v3.SXCMTS;
import org.hl7.v3.StrucDocContent;
import org.hl7.v3.StrucDocParagraph;
import org.hl7.v3.StrucDocText;
import org.hl7.v3.XActClassDocumentEntryOrganizer;
import org.hl7.v3.XActMoodDocumentObservation;
import org.hl7.v3.XActRelationshipEntryRelationship;
import org.hl7.v3.XActRelationshipExternalReference;
import org.hl7.v3.XDocumentSubject;
import org.hl7.v3.XDocumentSubstanceMood;

import fi.kela.kanta.cda.validation.Validoija;
import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.LeimakentatTO;
import fi.kela.kanta.to.MuuAinesosaTO;
import fi.kela.kanta.to.VaikuttavaAineTO;
import fi.kela.kanta.to.VaikuttavaAinesosaTO;
import fi.kela.kanta.to.ValmisteenKayttotapaTO;
import fi.kela.kanta.util.KantaCDAUtil;

public abstract class ReseptiKasaaja extends Kasaaja {

    protected static final String template_id = "templateId";
    protected static final String code = "%s.code";
    protected static final String title = "%s.title";
    protected static final String code_title = "%s.code.title";
    protected static final String code_system_name = "%s.code.codeSystemName";
    protected static final String code_display_name = "%s.code.displayName";
    protected static final String code_ = "%s.title";

    private static final String TEXT_COMPLETED = "completed";
    private static final String TEXT_SBADM = "SBADM";

    protected Validoija validoija;

    public ReseptiKasaaja(Properties properties) {
        super(properties);
    }

    /**
     * Validoi LaakemaaraysTO:n. Tarkistaa että kasaukseen annetun tiedon (alkup. laakemaarays, korjaus, mitätöinti,
     * uusiminen jne.) pohjalta pystytään kasaamaan cda.
     *
     */
    protected void validoiLaakemaarays() {
        validoija.validoi();
    };

    /**
     * Palauttaa CDA-asiakirjan, perustuen asiakirjakohtaisen kasaajan konstruktorissa annettuun TO:hon
     * 
     * @return CDA-asiakirja
     */
    abstract public String kasaaReseptiAsiakirja() throws JAXBException;

    /**
     * Palauttaa CDA-asiakirjan, perustuen asiakirjakohtaisen kasaajan konstruktorissa annettuun TO:hon
     * 
     * @return CDA-asiakirjan tiedot POCDMT00040ClinicalDocumentissä
     */
    abstract public POCDMT000040ClinicalDocument kasaaReseptiCDA();

    /**
     * Luo narrative osion johon sijoitetaan Paikka, tekopäivämäärä ja lääkärin nimi.
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta poimitaan lääkärin nimi
     * @param today
     *            String joka sijoitetaan tekopäivämääräksi.
     * @return StrucDocText jossa tiedot omissa kappaleissaan
     */
    protected StrucDocText luoNarrativePaikkaPvmLaakari(LaakemaaraysTO laakemaarays, String today) {
        StrucDocText text = of.createStrucDocText();
        text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent("Kelain")));
        text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent(today)));
        text.getContent().add(of.createStrucDocItemParagraph(
                luoParagraphContent(laakemaarays.getAmmattihenkilo().getKokonimi().getKokoNimi())));
        return text;
    }

    /**
     * Luo StucDocParagraph kappale elementin johon annettu string sijoitettu
     *
     * @param string
     *            String joka sojoitetaan kappaleen contenttiin
     * @return StrucDocParagraph jonka contenttiin string on sijoitettu
     */
    protected StrucDocParagraph luoParagraphContent(String string) {
        StrucDocParagraph paragraph = of.createStrucDocParagraph();
        StrucDocContent content = of.createStrucDocContent();
        content.getContent().add(string);
        paragraph.getContent().add(of.createStrucDocItemContent(content));
        return paragraph;
    }

    /**
     * Apumetodi joka kertoo tuleeko vaikuttavat aineet lisätä Vaikuttavat aineet lisätään: jos kyseessä apteekissa
     * valmistettava lääke jos kauppanimi on annettu ja kyseessä ei ole lääketietokannssa oleva perusvoide
     *
     * @param laakemaarays
     *            LaakemaaraysTO jonka tiedoista koetetaan päätellä tuleeko vaikuttavat aineet lisätä
     * @return true jos vaikuttavat aineet tulee lisätä muuten false
     */
    protected boolean lisataankoVaikuttavatAineet(LaakemaaraysTO laakemaarays) {
        if ( laakemaarays.isApteekissaValmistettavaLaake() ) {
            return true;
        }
        if ( !onkoNullTaiTyhja(laakemaarays.getValmiste().getYksilointitiedot().getKauppanimi()) ) {
            // On kauppanimi
            String valmisteenLaji = laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji();
            if ( "2".equals(valmisteenLaji) ) {// Laaketietokannassa oleva preusvoide
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Apumetodi joka kertoo onko kyseessä lääketietokannan valmiste tai potilaskohtainen erityislupavalmiste. Kyseessä
     * on lääketietokannan valmiste: jos valmisteen laji: myyntiluvallinen valmiste, lääketietokannassa oleva
     * perusvoide, lääketietokannassa oleva kliininen ravintovalmiste, määräaikainen ertyislupavalmiste.
     *
     * @param laakemaarays
     *            LaakemaaraysTO jonka valmisteen yksilöintitietojen valmisteenlajin peruteella koetetaan päätellä onko
     *            kyseessä lääketietokannan valmiste
     * @return true jos kyseessä lääketietokannan valmiste muuten false
     */
    private static boolean onkoLaaketietokannanValmiste(LaakemaaraysTO laakemaarays) {
        String valmisteenLaji = laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji();
        if ( "1".equals(valmisteenLaji) ) { // Myyntiluvallinen valmiste
            return true;
        }
        else if ( "2".equals(valmisteenLaji) ) {// Lääketietokannassa oleva perusvoide
            return true;
        }
        else if ( "3".equals(valmisteenLaji) ) {// Lääketietokannassa oleva kliininen ravintovalmiste
            return true;
        }
        else if ( "4".equals(valmisteenLaji) ) { // Määräaikainen ertyislupavalmiste
            return true;
        }
        else if ( "11".equals(valmisteenLaji) ) { // Potilaskohtainen erityislupavalmiste
            return true;
        }
        // 5 : Lääketietokannan rekisteröity lääkevalmiste
        // 6 : Lääketietokannan ulkopuolinen valmiste
        // 7 : Apteekissa valmistettava lääke
        // 8 : Tutkimuslääke
        // 9 : Vaikuttavan aineen nimellä määrätty lääke
        // 10: Hoitotarvike
        return false;
    }

    /**
     * Apumetodi jolla voidaan luoda POCDMT00040Component5 component elementti luo componentille POCDMT0004Sectionin
     * jonka ID attribuuttin haetaan seuraava OID sectionille luodaan id elementti jonka root attribuuttiin haetaan Id
     *
     * @return esitäytetyn POCDMT00040Component5 elementin
     */
    protected POCDMT000040Component5 luoComponent(LeimakentatTO<?> leimakentat) {
        POCDMT000040Component5 component = of.createPOCDMT000040Component5();
        component.setSection(of.createPOCDMT000040Section());
        component.getSection().setAttributeID(getNextOID(leimakentat));
        component.getSection().setId(of.createII());
        component.getSection().getId().setRoot(getId(leimakentat));
        return component;
    }

    /**
     * Asiakirjakohtaisen kasaajan toteuttama. Kutsutaan luoMuutTiedot -metodista.
     * 
     * @param entry
     *            Entry, johon kasaaja tekee MuutTiedot -muutokset. Mikäli muutoksia ei tehdä, tulee palauttaa annettu
     *            entry.
     * @return entry (mahdollisesti) muokattu entry, johon asiakirjakohtainen kasaaja on tehnyt lisäyksensä/muutoksensa
     */
    protected abstract POCDMT000040Entry luoAsiakirjanMuutTiedot(POCDMT000040Entry entry);

    /**
     * Luo lääkemääräyksen muut tiedot osion. luo entry/organizer rakenteen johon observation elemeteissä lisätään
     * seuraavat tiedot: lääkevaihtokielto,Käyttötarkiotus teksinä, alle 12-vuotiaan paino, annosjakelu&annosjakelu
     * teksti, hoitolaji,viesti apteekille,erillisselvitys&erillisselvitys teksti, potilaan tunnistaminen,
     * PKV-lääkemääräys, potilas kieltäytynyt potilasohjeen tulostamisesta(poistettu), pysyvä lääkitys, kysessä lääkkeen
     * käytön aloitus, huume, reseptin laji, uudistamis kielto, uudistamis kiellon syy, apteekissa tallennettu
     * lääkemääräys
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tiedot poimitaan
     * @return POCDMT00004Entry elementtti johon tiedot sijoitettu
     */
    protected POCDMT000040Entry luoMuutTiedot(LaakemaaraysTO laakemaarays) {
        POCDMT000040Entry entry = of.createPOCDMT000040Entry();

        entry.setOrganizer(of.createPOCDMT000040Organizer());
        entry.getOrganizer().setClassCode(XActClassDocumentEntryOrganizer.CLUSTER);
        entry.getOrganizer().getMoodCodes().add("EVN");
        entry.getOrganizer().setCode(of.createCD());
        fetchAttributes(getMuutTiedotCode(), entry.getOrganizer().getCode());

        entry.getOrganizer().setStatusCode(of.createCS());
        entry.getOrganizer().getStatusCode().setCode(TEXT_COMPLETED);

        // Käyttötarkoitus
        POCDMT000040Component4 kayttotarkoitusComp = of.createPOCDMT000040Component4();
        kayttotarkoitusComp.setObservation(of.createPOCDMT000040Observation());
        ST kayttotarkoitusValue = of.createST();
        kayttotarkoitusValue.getContent().add(laakemaarays.getKayttotarkoitusTeksti());
        asetaObservation(KantaCDAConstants.Laakityslista.KAYTTOTARKOITUS_TEKSTINA, kayttotarkoitusValue,
                kayttotarkoitusComp.getObservation());
        entry.getOrganizer().getComponents().add(kayttotarkoitusComp);

        // Hoitolaji
        POCDMT000040Component4 hoitolajiComp = of.createPOCDMT000040Component4();
        hoitolajiComp.setObservation(of.createPOCDMT000040Observation());
        hoitolajiComp.getObservation().getClassCodes().add("OBS");
        hoitolajiComp.getObservation().setMoodCode(XActMoodDocumentObservation.EVN);
        hoitolajiComp.getObservation().setCode(of.createCD());
        fetchAttributes(KantaCDAConstants.Laakityslista.HOITOLAJI, hoitolajiComp.getObservation().getCode());
        hoitolajiComp.getObservation().getCode().setCode(KantaCDAConstants.Laakityslista.HOITOLAJI);
        for (String hoitolaji : laakemaarays.getHoitolajit()) {
            CE hoitolajiValue = of.createCE();
            fetchAttributes(hoitolaji + ".hoitolaji", hoitolajiValue);
            hoitolajiValue.setCode(hoitolaji);
            hoitolajiComp.getObservation().getValues().add(hoitolajiValue);
        }
        entry.getOrganizer().getComponents().add(hoitolajiComp);

        // pysyvälääkitys
        entry.getOrganizer().getComponents().add(
                luoBLComponent(KantaCDAConstants.Laakityslista.PYSYVA_LAAKITYS, laakemaarays.isPysyvaislaakitys()));

        // uudistamiskielto
        entry.getOrganizer().getComponents()
                .add(luoBLComponent(KantaCDAConstants.Laakityslista.UUSIMISKIELTO, laakemaarays.isUudistamiskielto()));

        // uudistamiskiellon syy ja perustelu jos uudistamiskielto arvossa true
        if ( laakemaarays.isUudistamiskielto() ) {
            // uudistamiskiellon syy
            POCDMT000040Component4 uudistamiskiellonSyyComp = of.createPOCDMT000040Component4();
            uudistamiskiellonSyyComp.setObservation(of.createPOCDMT000040Observation());
            CE uudistamiskiellonsyyValue = of.createCE();
            fetchAttributes(laakemaarays.getUusimiskiellonSyy() + ".muutoksensyy", uudistamiskiellonsyyValue);
            uudistamiskiellonsyyValue.setCode(laakemaarays.getUusimiskiellonSyy());
            // uudistamiskiellon perustelu
            uudistamiskiellonsyyValue.setOriginalText(of.createED());
            uudistamiskiellonsyyValue.getOriginalText().getContent().add(laakemaarays.getUusimiskiellonPerustelu());

            asetaObservation(KantaCDAConstants.Laakityslista.UUDISTAMISKIELLON_SYY, uudistamiskiellonsyyValue,
                    uudistamiskiellonSyyComp.getObservation());
            entry.getOrganizer().getComponents().add(uudistamiskiellonSyyComp);
        }

        // laakevaihtokielto
        entry.getOrganizer().getComponents().add(
                luoBLComponent(KantaCDAConstants.Laakityslista.LAAKEVAIHTOKIELTO, laakemaarays.isLaakevaihtokielto()));

        // alle 12-vuotiaan paino
        entry.getOrganizer().getComponents().addAll(luoAlle12VuotiaanPaino(laakemaarays));

        // annosjakelu
        POCDMT000040Component4 annosjakeluComp = luoBLComponent(KantaCDAConstants.Laakityslista.ANNOSJAKELU,
                laakemaarays.isAnnosjakelu());
        if ( !onkoNullTaiTyhja(laakemaarays.getAnnosjakeluTeksti()) ) {
            ED annosjakeluTeksti = of.createED();
            annosjakeluTeksti.getContent().add(laakemaarays.getAnnosjakeluTeksti());
            annosjakeluComp.getObservation().setText(annosjakeluTeksti);
        }
        entry.getOrganizer().getComponents().add(annosjakeluComp);

        // viesti apteekille
        if ( !onkoNullTaiTyhja(laakemaarays.getViestiApteekille()) ) {
            POCDMT000040Component4 viestiComp = of.createPOCDMT000040Component4();
            viestiComp.setObservation(of.createPOCDMT000040Observation());
            ST viestiValue = of.createST();
            viestiValue.getContent().add(laakemaarays.getViestiApteekille());
            asetaObservation(KantaCDAConstants.Laakityslista.VIESTI_APTEEKILLE, viestiValue,
                    viestiComp.getObservation());
            entry.getOrganizer().getComponents().add(viestiComp);
        }

        // Erillisselvitys ja erillisselvitys teksti
        entry.getOrganizer().getComponents().addAll(luoErillisselvitys(laakemaarays));

        // Potilaan tunnistaminen (pakollinen, jos huumausaineresepti)
        if ( laakemaarays.isHuume() ) {
            POCDMT000040Component4 tunnistaminenComp = of.createPOCDMT000040Component4();
            tunnistaminenComp.setObservation(of.createPOCDMT000040Observation());
            CE tunnistaminenValue = of.createCE();
            fetchAttributes(laakemaarays.getPotilaanTunnistaminen() + ".tunnistaminen", tunnistaminenValue);
            tunnistaminenValue.setCode(laakemaarays.getPotilaanTunnistaminen());
            asetaObservation(KantaCDAConstants.Laakityslista.POTILAAN_TUNNISTAMINEN, tunnistaminenValue,
                    tunnistaminenComp.getObservation());
            if ( !onkoNullTaiTyhja(laakemaarays.getPotilaanTunnistaminenTeksti()) ) {
                tunnistaminenComp.getObservation().setText(of.createED());
                tunnistaminenComp.getObservation().getText().getContent()
                        .add(laakemaarays.getPotilaanTunnistaminenTeksti());
            }
            entry.getOrganizer().getComponents().add(tunnistaminenComp);
        }

        // PKV laake
        if ( onkoPKVLaake(laakemaarays) ) {
            POCDMT000040Component4 pkvComp = of.createPOCDMT000040Component4();
            pkvComp.setObservation(of.createPOCDMT000040Observation());
            CE pkvValue = of.createCE();
            fetchAttributes(laakemaarays.getPKVlaakemaarays() + ".pkv", pkvValue);
            pkvValue.setCode(laakemaarays.getPKVlaakemaarays());
            asetaObservation(KantaCDAConstants.Laakityslista.HUUMAUSAINE_PKV_LAAKEMAARAYS, pkvValue,
                    pkvComp.getObservation());
            entry.getOrganizer().getComponents().add(pkvComp);
        }

        // potilas kieltäyynyt potilasohjeen tulostamisesta (TRVITAANKO?)

        // Kyseessä on lääkkeen käytön aloitus
        entry.getOrganizer().getComponents()
                .add(luoBLComponent(KantaCDAConstants.Laakityslista.KYSEESSA_LAAKKEEN_KAYTON_ALOITUS,
                        laakemaarays.isKyseessaLaakkeenkaytonAloitus()));

        // Huume
        entry.getOrganizer().getComponents()
                .add(luoBLComponent(KantaCDAConstants.Laakityslista.HUUME, laakemaarays.isHuume()));

        // reseptin laji
        POCDMT000040Component4 reseptinLajiComp = of.createPOCDMT000040Component4();
        reseptinLajiComp.setObservation(of.createPOCDMT000040Observation());
        CE reseptinLajiValue = of.createCE();
        fetchAttributes(laakemaarays.getReseptinLaji() + ".reseptinlaji", reseptinLajiValue);
        reseptinLajiValue.setCode(laakemaarays.getReseptinLaji());
        asetaObservation(KantaCDAConstants.Laakityslista.RESEPTIN_LAJI, reseptinLajiValue,
                reseptinLajiComp.getObservation());
        entry.getOrganizer().getComponents().add(reseptinLajiComp);

        // apteekissa tallennettu lääkemääräys
        if ( !onkoNullTaiTyhja(laakemaarays.getApteekissaTallennettuLaakemaarays()) ) {
            POCDMT000040Component4 apteekissaTallennettuComp = of.createPOCDMT000040Component4();
            apteekissaTallennettuComp.setObservation(of.createPOCDMT000040Observation());
            CE apteekissaTallennettuValue = of.createCE();
            fetchAttributes(laakemaarays.getApteekissaTallennettuLaakemaarays() + ".apteekissatallennettulaakemaarays",
                    apteekissaTallennettuValue);
            apteekissaTallennettuValue.setCode(laakemaarays.getApteekissaTallennettuLaakemaarays());
            asetaObservation(KantaCDAConstants.Laakityslista.APTEEKISSA_TALLENNETTU_LAAKEMAARAYS,
                    apteekissaTallennettuValue, apteekissaTallennettuComp.getObservation());
            entry.getOrganizer().getComponents().add(apteekissaTallennettuComp);
        }

        // apteekissa tallennettu lääkemääräys / perustelu
        if ( !onkoNullTaiTyhja(laakemaarays.getApteekissaTallennettuLaakemaaraysPerustelu()) ) {
            POCDMT000040Component4 perusteluComp = of.createPOCDMT000040Component4();
            perusteluComp.setObservation(of.createPOCDMT000040Observation());
            CE perusteluValue = of.createCE();
            fetchAttributes(laakemaarays.getApteekissaTallennettuLaakemaaraysPerustelu()
                    + ".apteekissatallennettulaakemaaraysPerustelu", perusteluValue);
            perusteluValue.setCode(laakemaarays.getApteekissaTallennettuLaakemaaraysPerustelu());
            if ( !onkoNullTaiTyhja(laakemaarays.getApteekissaTallennettuLaakemaaraysMuuSyy()) ) {
                perusteluValue.setOriginalText(of.createED());
                perusteluValue.getOriginalText().getContent()
                        .add(laakemaarays.getApteekissaTallennettuLaakemaaraysMuuSyy());
            }
            asetaObservation(KantaCDAConstants.Laakityslista.APTEEKISSA_TALLENNETTU_LAAKEMAARAYS_PERUSTELU,
                    perusteluValue, perusteluComp.getObservation());
            entry.getOrganizer().getComponents().add(perusteluComp);
        }

        // lääkärinpalkkio
        if ( null != laakemaarays.getLaakarinPalkkio() ) {
            POCDMT000040Component4 palkkioComp = of.createPOCDMT000040Component4();
            palkkioComp.setObservation(of.createPOCDMT000040Observation());
            MO palkkioCompValue = of.createMO();
            palkkioCompValue.setValue(String.format(Locale.ROOT, "%.2f", laakemaarays.getLaakarinPalkkio()));
            palkkioCompValue.setCurrency(laakemaarays.getValuutta());
            asetaObservation(KantaCDAConstants.Laakityslista.LAAKARINPALKKIO, palkkioCompValue,
                    palkkioComp.getObservation());
            entry.getOrganizer().getComponents().add(palkkioComp);
            entry.getOrganizer().getComponents()
                    .add(luoBLComponent(KantaCDAConstants.Laakityslista.LAAKARINPALKKIO_ERIKOISLAAKARINA,
                            laakemaarays.isLaakarinpalkkioErikoislaakarina()));
        }

        // Tartuntatautilain mukaine lääke
        entry.getOrganizer().getComponents().add(luoBLComponent(
                KantaCDAConstants.Laakityslista.TARTUNTATAUTILAIN_MUKAINEN_LAAKE, laakemaarays.getTartuntatauti()));

        // kutsutaan toteutuskohtaista toteutusmetodia, joka muokkaa/lisää tarvittaessa muutTiedot -osioita
        entry = luoAsiakirjanMuutTiedot(entry);
        return entry;
    }

    /**
     * Luo erillisselivtys osion
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tiedot haetaan
     * @return POCDMT00040Component4 Collection johon tiedot sijoitettuna, jos tietoja ei ole annettu palauttaa tyhjän
     *         collectionin
     */
    private Collection<? extends POCDMT000040Component4> luoErillisselvitys(LaakemaaraysTO laakemaarays) {
        Collection<POCDMT000040Component4> retval = new ArrayList<POCDMT000040Component4>();
        if ( !onkoNullTaiTyhja(laakemaarays.getErillisselvitys())
                && !onkoNullTaiTyhja(laakemaarays.getErillisselvitysteksti()) ) {
            POCDMT000040Component4 component = of.createPOCDMT000040Component4();
            component.setObservation(of.createPOCDMT000040Observation());
            CE value = of.createCE();
            fetchAttributes("erillisselvitys", value);
            value.setCode(laakemaarays.getErillisselvitys());
            value.setDisplayName(laakemaarays.getErillisselvitysotsikko());
            value.setCodeSystemName(null);
            asetaObservation(KantaCDAConstants.Laakityslista.ERILLISSELVITYS, value, component.getObservation());
            ST te = of.createST();
            te.getContent().add(laakemaarays.getErillisselvitysteksti());
            component.getObservation().setText(te);
            retval.add(component);
        }
        return retval;
    }

    /**
     * Luo alle 12-vuotiaan paino osion
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tiedot haetaan
     * @return POCDMT00040Component4 Collection johon tiedot sijoitettuna, jos tietoja ei ole annettu palauttaa tyhjän
     *         collectionin
     */
    private Collection<? extends POCDMT000040Component4> luoAlle12VuotiaanPaino(LaakemaaraysTO laakemaarays) {
        Collection<POCDMT000040Component4> retval = new ArrayList<POCDMT000040Component4>();
        if ( !onkoNullTaiTyhja(laakemaarays.getAlle12VuotiaanPainoUnit())
                && null != laakemaarays.getAlle12VuotiaanPainoValue() ) {
            POCDMT000040Component4 component = of.createPOCDMT000040Component4();
            component.setObservation(of.createPOCDMT000040Observation());
            PQ value = of.createPQ();
            value.setUnit(laakemaarays.getAlle12VuotiaanPainoUnit());
            value.setValue(laakemaarays.getAlle12VuotiaanPainoValue().toString());
            asetaObservation(KantaCDAConstants.Laakityslista.ALLE_12VUOTIAAN_PAINO, value, component.getObservation());
            retval.add(component);
        }
        return retval;
    }

    /**
     * Palauttaa lääkemääräyksen muut tiedot osion entry/orgsnizer/code elementin coden metodi voidaan ylikirjoittaa kun
     * halutaan käyttää eri koodia
     *
     * @return String muut tiedot osion code elementin code atribuutin arvo
     */
    protected String getMuutTiedotCode() {
        return KantaCDAConstants.Laakityslista.RESEPTIN_MUUT_TIEDOT;
    }

    /**
     * placeholder korjauksen syyn perustelun ja korjaajan tiedot sisältävän comopnent elementin luomseeen voidaan
     * ylikirjoittaa jos halutaan lisätä tiedot
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tiedot poimitaan
     * @return null POCDMT000040Component4
     */
    protected POCDMT000040Component4 luoKorjauksenSyyPerusteluJaKorjaaja(LaakemaaraysTO laakemaarays) {
        return null;
    }

    /**
     * Apu metodi toistuvan component/observation/code/value rakenteen luomiseen
     *
     * @param code
     *            String coden code
     * @param value
     *            boolean arvo joka viedään BL tyypisenä valueen
     * @return POCDMT000040Component4 component johon rakenne täytetty
     */
    protected POCDMT000040Component4 luoBLComponent(String code, boolean value) {
        POCDMT000040Component4 component = of.createPOCDMT000040Component4();
        component.setObservation(of.createPOCDMT000040Observation());
        BL blValue = of.createBL();
        blValue.setValue(value);
        asetaObservation(code, blValue, component.getObservation());
        return component;
    }

    /**
     * Luo lääkemääräyksen annostus osion. luo entry/organizer rakenteen johon observation elemeteissä lisätään
     * seuraavat tiedot: Annostelu vain tekstinä ja annostelu tekstimuodossa Huomaa:
     * Laakemaarayksen_sanomat_CDA_R2_rakenteena_v3.30 4.5 sanoo että: Vain annosohje tekstimuodossa on käytössä
     * alkuvaiheessa. Muut annosteluvaihtoehdot otetaan käyttöön jatkokehityksessä.
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tiedot poimitaan
     * @return POCDMT00040Entry rakenteen johon tiedot on sijoitettu.
     */
    protected POCDMT000040Entry luoAnnostus(LaakemaaraysTO laakemaarays) {

        POCDMT000040Entry entry = of.createPOCDMT000040Entry();
        entry.setOrganizer(of.createPOCDMT000040Organizer());
        entry.getOrganizer().setClassCode(XActClassDocumentEntryOrganizer.CLUSTER);
        entry.getOrganizer().getMoodCodes().add("EVN");
        entry.getOrganizer().setCode(of.createCD());
        fetchAttributes(KantaCDAConstants.Laakityslista.ANNOSOSIO_JA_JATKOOSIOT, entry.getOrganizer().getCode());
        entry.getOrganizer().getCode().setCode(KantaCDAConstants.Laakityslista.ANNOSOSIO_JA_JATKOOSIOT);
        entry.getOrganizer().setStatusCode(of.createCS());
        entry.getOrganizer().getStatusCode().setCode(TEXT_COMPLETED);
        // Annostelu vain tekstinä
        POCDMT000040Component4 annosteluVainTekstinaComp = of.createPOCDMT000040Component4();
        annosteluVainTekstinaComp.setObservation(of.createPOCDMT000040Observation());
        BL annosteluVainTekstinaValue = of.createBL();
        annosteluVainTekstinaValue.setValue(laakemaarays.isAnnosteluPelkastaanTekstimuodossa());
        asetaObservation(KantaCDAConstants.Laakityslista.ANNOSTELU_VAIN_TEKSTINA, annosteluVainTekstinaValue,
                annosteluVainTekstinaComp.getObservation());
        entry.getOrganizer().getComponents().add(annosteluVainTekstinaComp);

        // Annostusohje tekstinä
        POCDMT000040Component4 annostusohjeComp = of.createPOCDMT000040Component4();
        annostusohjeComp.setSubstanceAdministration(of.createPOCDMT000040SubstanceAdministration());
        annostusohjeComp.getSubstanceAdministration().getClassCodes().add(TEXT_SBADM);
        annostusohjeComp.getSubstanceAdministration().setMoodCode(XDocumentSubstanceMood.EVN);
        annostusohjeComp.getSubstanceAdministration().setText(of.createED());
        annostusohjeComp.getSubstanceAdministration().getText().getContent()
                .add(KantaCDAUtil.poistaKontrolliMerkit(laakemaarays.getAnnostusohje()));
        annostusohjeComp.getSubstanceAdministration().setConsumable(of.createPOCDMT000040Consumable());
        annostusohjeComp.getSubstanceAdministration().getConsumable()
                .setManufacturedProduct(of.createPOCDMT000040ManufacturedProduct());
        annostusohjeComp.getSubstanceAdministration().getConsumable().getManufacturedProduct()
                .setManufacturedLabeledDrug(of.createPOCDMT000040LabeledDrug());
        annostusohjeComp.getSubstanceAdministration().getConsumable().getManufacturedProduct()
                .getManufacturedLabeledDrug().getNullFlavors().add("NI");

        // SIC merkintä
        annostusohjeComp.getSubstanceAdministration().getEntryRelationships()
                .add(of.createPOCDMT000040EntryRelationship());
        annostusohjeComp.getSubstanceAdministration().getEntryRelationships().get(0)
                .setTypeCode(XActRelationshipEntryRelationship.COMP);
        annostusohjeComp.getSubstanceAdministration().getEntryRelationships().get(0)
                .setObservation(of.createPOCDMT000040Observation());
        BL sicValue = of.createBL();
        sicValue.setValue(laakemaarays.isSICmerkinta());
        asetaObservation(KantaCDAConstants.Laakityslista.SIC_MERKINTA, sicValue,
                annostusohjeComp.getSubstanceAdministration().getEntryRelationships().get(0).getObservation());
        entry.getOrganizer().getComponents().add(annostusohjeComp);
        return entry;
    }

    /**
     * Luo lääkemääräyksen muut ainesosat osion. luo entry/organizer rakenteen johon lisätään seuraavat tiedot: muun
     * ainesosan vahvuus/määrä, muun ainesosan vahvuuden/määrän yksikkö, muun ainesosan vahvuus/määärä tekstimuotoisena,
     * muun ainesosan ATC-koodi, muun ainesosan ATC-koodin mukainen nimi ja muun ainesosan koodamaton nimi
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tiedot poimitaan
     * @return POCDMT00040Entry elementti johon tiedot on sijoitettu
     */
    protected POCDMT000040Entry luoMuutAinesosat(LaakemaaraysTO laakemaarays) {
        POCDMT000040Entry entry = of.createPOCDMT000040Entry();
        entry.setOrganizer(of.createPOCDMT000040Organizer());
        entry.getOrganizer().setClassCode(XActClassDocumentEntryOrganizer.CLUSTER);
        entry.getOrganizer().getMoodCodes().add("EVN");
        entry.getOrganizer().setCode(of.createCD());
        fetchAttributes(KantaCDAConstants.Laakityslista.MUUT_AINESOSAT, entry.getOrganizer().getCode());
        entry.getOrganizer().getCode().setCode(KantaCDAConstants.Laakityslista.MUUT_AINESOSAT);

        entry.getOrganizer().setStatusCode(of.createCS());
        entry.getOrganizer().getStatusCode().setCode(TEXT_COMPLETED);

        if ( laakemaarays.isApteekissaValmistettavaLaake() && null != laakemaarays.getApteekissaValmistettavaLaake() ) {
            for (MuuAinesosaTO ainesosa : laakemaarays.getApteekissaValmistettavaLaake().getMuutAinesosat()) {
                entry.getOrganizer().getComponents()
                        .add(luoVaikuttavaAine(String.valueOf(ainesosa.getAinesosanMaaraValue()),
                                ainesosa.getAinesosanMaaraUnit(), ainesosa.getAinesosanMaaraTekstina(), null, null,
                                ainesosa.getNimi(), laakemaarays.getLaaketietokannanVersio()));
            }
        }
        return entry;
    }

    abstract protected void luoAsiakirjakohtaisetRakenteet(LaakemaaraysTO laakemaarays);

    /**
     * Luo lääkemääräyksen vaikuttavat ainesosat osion. luo entry/organizer rakenteen johon lisätty seuraavat tiedot:
     * vaikuttavan ainesosan vahvuus/määrä, vaikuttavan ainesosan vahvuuden/määrän yksikkö, vaikuttavan ainesosan
     * vahvuus/määrä tekstimuotoisena, vaikuttavan ainesosan ATC-koodi, vaikuttavan ainesosan ATC-koodin mukainen nimi
     * ja vaikuttavan ainesosan koodamaton nimi.
     *
     * @param laakemaarays
     *            LaakemaraysTO josta tiedot poimitaan
     * @return POCDMT00040Entry elementti johon tiedot on sijoitettu
     */
    protected POCDMT000040Entry luoVaikuttavatAinesosat(LaakemaaraysTO laakemaarays) {
        POCDMT000040Entry entry = of.createPOCDMT000040Entry();
        entry.setOrganizer(of.createPOCDMT000040Organizer());
        entry.getOrganizer().setClassCode(XActClassDocumentEntryOrganizer.CLUSTER);
        entry.getOrganizer().getMoodCodes().add("EVN");
        entry.getOrganizer().setCode(of.createCD());
        fetchAttributes(KantaCDAConstants.Laakityslista.VAIKUTTAVAT_AINESOSAT, entry.getOrganizer().getCode());
        entry.getOrganizer().getCode().setCode(KantaCDAConstants.Laakityslista.VAIKUTTAVAT_AINESOSAT);

        entry.getOrganizer().setStatusCode(of.createCS());
        entry.getOrganizer().getStatusCode().setCode(TEXT_COMPLETED);
        if ( laakemaarays.isApteekissaValmistettavaLaake() && null != laakemaarays.getApteekissaValmistettavaLaake() ) {
            for (VaikuttavaAinesosaTO ainesosa : laakemaarays.getApteekissaValmistettavaLaake()
                    .getVaikuttavatAinesosat()) {
                entry.getOrganizer().getComponents()
                        .add(luoVaikuttavaAine(String.valueOf(ainesosa.getAinesosanMaaraValue()),
                                ainesosa.getAinesosanMaaraUnit(), ainesosa.getAinesosanMaaraTekstina(),
                                ainesosa.getATCkoodi(), ainesosa.getATCNimi(), ainesosa.getKoodamatonNimi(),
                                laakemaarays.getLaaketietokannanVersio()));

            }
        }
        else {
            // jos kauppanimellä määrätty ja löytyy lääketietokannasta
            if ( null != laakemaarays.getValmiste() && null != laakemaarays.getValmiste().getYksilointitiedot()
                    && !onkoNullTaiTyhja(laakemaarays.getValmiste().getYksilointitiedot().getKauppanimi()) ) {
                for (VaikuttavaAineTO aine : laakemaarays.getValmiste().getVaikuttavatAineet()) {
                    entry.getOrganizer().getComponents()
                            .add(luoVaikuttavaAine(null, // String.valueOf(aine.getVahvuus()),
                                    null, // aine.getVahvuusYksikko(),
                                    // aine.getLaakeaine(),
                                    null, null, null, aine.getLaakeaine(), laakemaarays.getLaaketietokannanVersio()));
                }
            }
        }
        return entry;
    }

    /**
     * Apumetodi jolla voidaan luoda POCDMT00040Component4 elementti ainesosalle. luo component/substanceAdministration
     * rakenteen johon jos vahvuus ja yksikkö annettu luodaan doseQuantitty/center rakenne jossa value vahvuus ja unit
     * yksikkö jos vahvuutta tai yksikköä ei ole annettu, mutta vahvuusTeksti on annettu luodaan
     * doseQuantity/translation/originalText rakenne johon sijoitettan vahvuusTeksti Lisäksi
     * component/substanceAdministration rakenteeseen luodaan consumable/manufacturedProduct/ManufacturedLabeledDrug
     * rakenne jonka codeen sijoitettaan: codeSystemVersion attribuuttiin lääketietokannanVersio jos annettu, code
     * attribuuttiin atcKoodi jos annettu mmuten nullFlavor attribuuttiin NI displayNameen atcNimi jos atcKoodi ja
     * actNimi on annettu Sekä consumable/manufacturedProduct/ManufacturedLabeledDrug/name elementtiin koodaamaton nimi
     * jos annettu
     *
     * @param vahvuus
     *            String ainesosan vahvuus/määrä
     * @param yksikko
     *            String ainesosan yksikkö
     * @param vahvuusTeksti
     *            String ainesosan vahvuus tekstimuodossa
     * @param atcKoodi
     *            String ainesosan ATC-koodi
     * @param atcNimi
     *            String ainesosan ATC-koodin mukainen nimi
     * @param koodaamatonNimi
     *            String ainesosan koodaamaton nimi
     * @param laaketietokannanVersio
     *            String lääketietokannan versio
     * @return POCDMT000040Componen4 elementin johon tiedot on sijoitettu
     */
    private POCDMT000040Component4 luoVaikuttavaAine(String vahvuus, String yksikko, String vahvuusTeksti,
            String atcKoodi, String atcNimi, String koodaamatonNimi, String laaketietokannanVersio) {
        POCDMT000040Component4 component = of.createPOCDMT000040Component4();
        component.setSubstanceAdministration(of.createPOCDMT000040SubstanceAdministration());
        component.getSubstanceAdministration().getClassCodes().add(TEXT_SBADM);
        component.getSubstanceAdministration().setMoodCode(XDocumentSubstanceMood.EVN);
        // Vaikuttavan ainesosan vahvuus/määrä
        if ( !onkoNullTaiTyhja(vahvuus) && !onkoNullTaiTyhja(yksikko) ) {
            component.getSubstanceAdministration().setDoseQuantity(of.createIVLPQ());
            component.getSubstanceAdministration().getDoseQuantity().setCenter(of.createPQ());
            component.getSubstanceAdministration().getDoseQuantity().getCenter().setValue(vahvuus);
            component.getSubstanceAdministration().getDoseQuantity().getCenter().setUnit(yksikko);
        }
        else if ( !onkoNullTaiTyhja(vahvuusTeksti) ) {
            component.getSubstanceAdministration().setDoseQuantity(of.createIVLPQ());
            component.getSubstanceAdministration().getDoseQuantity().getNullFlavors().add("OTH");
            PQR translation = of.createPQR();
            translation.setOriginalText(of.createED());
            translation.getOriginalText().getContent().add(vahvuusTeksti);
            component.getSubstanceAdministration().getDoseQuantity().getTranslations().add(translation);
        }
        component.getSubstanceAdministration().setConsumable(of.createPOCDMT000040Consumable());
        component.getSubstanceAdministration().getConsumable()
                .setManufacturedProduct(of.createPOCDMT000040ManufacturedProduct());
        component.getSubstanceAdministration().getConsumable().getManufacturedProduct()
                .setManufacturedLabeledDrug(of.createPOCDMT000040LabeledDrug());
        CE drugCode = of.createCE();
        fetchAttributes(
                "4.component.substanceAdministration.consumable.manufacturedProduct.manufactiredLabeledDrug.code",
                drugCode);
        if ( !onkoNullTaiTyhja(laaketietokannanVersio) ) {
            drugCode.setCodeSystemVersion(laaketietokannanVersio);
        }
        if ( !onkoNullTaiTyhja(atcKoodi) ) {
            drugCode.setCode(atcKoodi);
            if ( !onkoNullTaiTyhja(atcNimi) ) {
                drugCode.setDisplayName(atcNimi);
            }
        }
        else {
            drugCode.getNullFlavors().add("NI");
        }
        component.getSubstanceAdministration().getConsumable().getManufacturedProduct().getManufacturedLabeledDrug()
                .setCode(drugCode);
        if ( !onkoNullTaiTyhja(koodaamatonNimi) ) {
            component.getSubstanceAdministration().getConsumable().getManufacturedProduct().getManufacturedLabeledDrug()
                    .setName(of.createEN());
            component.getSubstanceAdministration().getConsumable().getManufacturedProduct().getManufacturedLabeledDrug()
                    .getName().getContent().add(koodaamatonNimi);
        }

        return component;
    }

    /**
     * Luo lääkemääräyksen lääkevalmisteen ja pakkauksen tiedot sekä reseptin perustiedot osion. luo entry/organizer
     * rakenteen johon sijoitetaan seuraavat tiedot: lääkeaineen vahvuus , lääkevalmisteen ATC-koodi, ATC-koodin
     * mukainen nimi, pakkauksien lukumäärä, pakkauskoko, pakkauskoon yksikkö, lääkkeen kokonaismäärä, lääkettä tietyksi
     * ajaksi, kauppanimi, koodaamaton kauppanimi, lääkemuoto, iterointi, valmistusohje, lääketietokannan ulkopuolinen
     * valmiste, lääkemääräyksen määräyspäivä, työnantaja, vakuutuslaitos, lääkärin erikoisala, lääkkeen määrääjän
     * yksilöintitunnus (SV-numero), lääkkeen määrääjän rekisteröintinumero (terhikkitunnus), lääkkeen määräjän nimi,
     * lääkärin oppiarvo, lääkkeen määrääjän ammattioikeus, kandin/sairaanhoitajan virka, tehtävä tai toimi,
     * organisaation tunnus, organisaation nimi, organisaation osoite, organisaation puhelinnumero, organisaation
     * sähköposti(TODO), alkuperäisen lääkemääräyksen id, lääkemääräyksen id , lääkemääräyksen voimassaolon loppuaika,
     * reseptin tyyppi, potilaan henkilötunnus, potilaan nimi, potilaan syntymäaika, apteekissa valmistettavan lääkkeen
     * osoitin, pakkauskoko tekstimuodossa, pakkauskoon kerroin, laite(TODO), myyntiluvan haltija, astiatunnus ja
     * valmisteen laji
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tiedot poimitaan
     * @param effectiveTimeValue
     *            String määräyspäivä
     * @param maaraaja
     *            AmmattihenkiloTO lääkkeen määrääjä
     * @return POCDMT00040Entry elementti johon tiedot on sijoitettu
     */

    protected POCDMT000040Entry luoValmisteenJaPakkauksenTiedot(LaakemaaraysTO laakemaarays, String effectiveTimeValue,
            AmmattihenkiloTO maaraaja) {
        POCDMT000040Entry entry = of.createPOCDMT000040Entry();
        entry.setOrganizer(of.createPOCDMT000040Organizer());
        entry.getOrganizer().setClassCode(XActClassDocumentEntryOrganizer.CLUSTER);
        entry.getOrganizer().getMoodCodes().add("EVN");
        entry.getOrganizer().setCode(of.createCD());
        fetchAttributes(KantaCDAConstants.Laakityslista.LAAKEVALMISTEEN_JA_PAKKAUKSEN_TIEDOT,
                entry.getOrganizer().getCode());

        entry.getOrganizer().setStatusCode(of.createCS());
        entry.getOrganizer().getStatusCode().setCode(TEXT_COMPLETED);
        entry.getOrganizer().getComponents().add(of.createPOCDMT000040Component4());
        POCDMT000040SubstanceAdministration substanceAdministration = of.createPOCDMT000040SubstanceAdministration();
        entry.getOrganizer().getComponents().get(0).setSubstanceAdministration(substanceAdministration);
        substanceAdministration.getClassCodes().add(TEXT_SBADM);
        substanceAdministration.setMoodCode(XDocumentSubstanceMood.EVN);

        // Valmistusohje
        if ( laakemaarays.isApteekissaValmistettavaLaake() ) {
            substanceAdministration.setText(of.createED());
            substanceAdministration.getText().getContent()
                    .add(laakemaarays.getApteekissaValmistettavaLaake().getValmistusohje());
        }

        // EffectiveTime
        substanceAdministration.getEffectiveTimes().add(luoMaaraysPaiva(laakemaarays, effectiveTimeValue));

        // Lääkeaineen vahvuus (pakollinen vaikuttavan aineen nimellä määrätyllä lääkkeellä ja valmisteella, jos löytyy
        // lääketietokannasta)
        substanceAdministration.setDoseQuantity(luoLaakeaineenVahvuus(laakemaarays));
        // Lääkevalmisteen ATC-koodi, ATC-koodin mukainen nimi ja lääketietokannan versio (Pakollinen vaikuttavan aineen
        // nimellä määrätyllä lääkkeellä ja valmisteella, jos löytyy lääketietokannasta)
        substanceAdministration.setConsumable(luoValmiste(laakemaarays));

        substanceAdministration.getEntryRelationships().add(of.createPOCDMT000040EntryRelationship());
        substanceAdministration.getEntryRelationships().get(0).setTypeCode(XActRelationshipEntryRelationship.COMP);
        POCDMT000040Supply supply = of.createPOCDMT000040Supply();
        substanceAdministration.getEntryRelationships().get(0).setSupply(supply);
        supply.setClassCode(ActClassSupply.SPLY);
        supply.setMoodCode(XDocumentSubstanceMood.EVN);
        supply.setCode(of.createCD());
        // Lääkemääräyksen tyyppi

        // Haetaan pohjalle yleiset
        fetchAttributes("83.component.substanceAdministration.entryRelationship.supply.code", supply.getCode());
        // haetaan päälle reseptityyppi kohtainen displayName
        supply.getCode().setDisplayName(fetchProperty(laakemaarays.getReseptintyyppi()
                + ".83.component.substanceAdministration.entryRelationship.supply.code.displayName"));

        supply.getCode().setCode(laakemaarays.getReseptintyyppi());
        // pakkauksien lukumäärä (Pakollinen jos reseptin tyyppi on 1)
        if ( "1".equals(laakemaarays.getReseptintyyppi()) ) {
            supply.setRepeatNumber(of.createIVLINT());
            supply.getRepeatNumber().setValue(BigInteger.valueOf(laakemaarays.getPakkauksienLukumaara()));
        }
        supply.setIndependentInd(of.createBL());
        supply.getIndependentInd().setValue(false);
        // lääkettä tietyksi ajaksi (Pakollinen jos reseptin tyyppi on 3)
        if ( "3".equals(laakemaarays.getReseptintyyppi()) ) {
            IVLTS ilvts = of.createIVLTS();
            ilvts.setLow(of.createIVXBTS());
            ilvts.getLow().setValue(getShortDateFormat().format(laakemaarays.getAjalleMaaratynReseptinAlkuaika()));
            ilvts.setWidth(of.createPQ());
            ilvts.getWidth().setUnit(laakemaarays.getAjalleMaaratynReseptinAikamaaraUnit());
            ilvts.getWidth().setValue(String.valueOf(laakemaarays.getAjalleMaaratynReseptinAikamaaraValue()));
            supply.getEffectiveTimes().add(ilvts);
        }
        // Pakkauskoko ja pakkauskoon yksikkö (Pakollinen jos reseptin tyyppi on 1 ja tieto löytyy lääketietokannasta)
        if ( "1".equals(laakemaarays.getReseptintyyppi()) && null != laakemaarays.getValmiste()
                && null != laakemaarays.getValmiste().getYksilointitiedot()
                && !onkoNullTaiTyhja(laakemaarays.getValmiste().getYksilointitiedot().getPakkausyksikko()) ) {
            supply.setQuantity(of.createPQ());
            supply.getQuantity().setValue(
                    KantaCDAUtil.doubleToString(laakemaarays.getValmiste().getYksilointitiedot().getPakkauskoko()));
            supply.getQuantity().setUnit(laakemaarays.getValmiste().getYksilointitiedot().getPakkausyksikko());
        }
        // lääkkeen kokonaismäärä (Pakollinen jos reseptin tyyppi on 2)
        if ( "2".equals(laakemaarays.getReseptintyyppi()) ) {
            supply.setQuantity(of.createPQ());
            supply.getQuantity().setUnit(laakemaarays.getLaakkeenKokonaismaaraUnit());
            supply.getQuantity().setValue(String.valueOf(laakemaarays.getLaakkeenKokonaismaaraValue()));
        }

        // Potilaan tiedot
        supply.setSubject(luoPotilaantiedot(laakemaarays));

        // VNR-koodi (Pakollinen jos valmisteen laji on 1)
        supply.setProduct(luoVNRKoodi(laakemaarays));
        // Lääkkeen määränneen lääkärin ja organisaation tiedot
        supply.getAuthors().add(luoBodyAuthor(maaraaja));

        // Lääkemuoto (pakollinen lääketietokannan valmisteilla tai määrättäessä vaikuttavalla aineella)
        supply.getEntryRelationships().addAll(luoLaakemuoto(laakemaarays));
        // Iterointi
        supply.getEntryRelationships().addAll(luoIterointi(laakemaarays));
        // Työnantaja ja Vakuutusyhtiö
        supply.getParticipants().addAll(luoTyonantajaJaVakuutuslaitos(laakemaarays));

        // Apteekissa valmistettavan lääkkeen osoitin
        POCDMT000040EntryRelationship aptValmistettava = of.createPOCDMT000040EntryRelationship();
        aptValmistettava.setTypeCode(XActRelationshipEntryRelationship.COMP);
        aptValmistettava.setObservation(of.createPOCDMT000040Observation());
        BL aptValmistettavaValue = of.createBL();
        aptValmistettavaValue.setValue(laakemaarays.isApteekissaValmistettavaLaake());
        asetaObservation(KantaCDAConstants.Laakityslista.APTEEKISSA_VALMISTETTAVAN_LAAKKEEN_OSOITIN,
                aptValmistettavaValue, aptValmistettava.getObservation());
        supply.getEntryRelationships().add(aptValmistettava);

        // pakkauskoon kerroin (Pakollinen jos reseptin tyyppi on 1 ja tieto löytyy lääketietokannasta)
        // pakkauskoko tekstimuodossa (Pakollinen jos reseptin tyyppi on 1)
        if ( "1".equals(laakemaarays.getReseptintyyppi()) ) {
            // pakkauskoon kerroion
            if ( null != laakemaarays.getValmiste() && null != laakemaarays.getValmiste().getYksilointitiedot()
                    && laakemaarays.getValmiste().getYksilointitiedot().getPakkauskokokerroin() > 0 ) {
                POCDMT000040EntryRelationship pakkauskoonKerroin = of.createPOCDMT000040EntryRelationship();
                pakkauskoonKerroin.setTypeCode(XActRelationshipEntryRelationship.COMP);
                pakkauskoonKerroin.setObservation(of.createPOCDMT000040Observation());
                INT pakkauskoonKerroinValue = of.createINT();
                pakkauskoonKerroinValue.setValue(
                        BigInteger.valueOf(laakemaarays.getValmiste().getYksilointitiedot().getPakkauskokokerroin()));
                asetaObservation(KantaCDAConstants.Laakityslista.PAKKAUSKOON_KERROIN, pakkauskoonKerroinValue,
                        pakkauskoonKerroin.getObservation());
                supply.getEntryRelationships().add(pakkauskoonKerroin);
            }
            // pakkauskoko
            POCDMT000040EntryRelationship pakkauskoko = of.createPOCDMT000040EntryRelationship();
            pakkauskoko.setTypeCode(XActRelationshipEntryRelationship.COMP);
            pakkauskoko.setObservation(of.createPOCDMT000040Observation());
            ST pakkauskokoValue = of.createST();
            pakkauskokoValue.getContent().add(laakemaarays.getValmiste().getYksilointitiedot().getPakkauskokoteksti());
            asetaObservation(KantaCDAConstants.Laakityslista.PAKKAUSKOKO_TEKSTIMUODOSSA, pakkauskokoValue,
                    pakkauskoko.getObservation());
            supply.getEntryRelationships().add(pakkauskoko);
        }
        // laite (Pakollinen jos tieto löytyy lääketietokannasta) rakenne sama kuin pakkauskoossa
        supply.getEntryRelationships().addAll(luoPakkauksenLaite(laakemaarays));

        // myyntiluvan haltija (Pakollinen jos tieto löytyy lääketietokannasta)
        supply.getParticipants().addAll(luoMyyntiluvanHaltija(laakemaarays));

        // säilytysastia (Pakollinen jos tieto löytyy lääketietokannasta)
        supply.getEntryRelationships().addAll(luoSailytysastia(laakemaarays));
        // Valmisteen laji
        supply.getEntryRelationships().addAll(luoValmisteenlaji(laakemaarays));
        // viittaukset
        // Viittaus lääkemääräykseen itseensä
        supply.getReferences().addAll(luoViittaukset(laakemaarays));
        return entry;
    }

    /**
     * Luo participant rakenteen työnantajan ja vakuutusyhtiön tiedoille
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tiedot haetaan
     * @return POCDMT00040Participant2 collection
     */
    private Collection<? extends POCDMT000040Participant2> luoTyonantajaJaVakuutuslaitos(LaakemaaraysTO laakemaarays) {
        Collection<POCDMT000040Participant2> participants = new ArrayList<POCDMT000040Participant2>();
        if ( !onkoNullTaiTyhja(laakemaarays.getTyonantaja()) ) {
            participants.add(luoParticipant(laakemaarays.getTyonantaja(), "EMP"));
        }
        if ( !onkoNullTaiTyhja(laakemaarays.getVakuutuslaitos()) ) {
            participants.add(luoParticipant(laakemaarays.getVakuutuslaitos(), "PAYOR"));
        }
        return participants;
    }

    /**
     * Luo participant rakenteen annetun nimen ja tyypin mukaan
     *
     * @param name
     *            String joka sijoitetaan name elementtiin
     * @param type
     *            String joka sijoitetaan prticipantRole classCodeen
     * @return POCDMT00040Participant2 elementti johon tiedot on sijoitettu
     */
    private POCDMT000040Participant2 luoParticipant(String name, String type) {
        POCDMT000040Participant2 participant = of.createPOCDMT000040Participant2();
        participant.getTypeCodes().add("HLD");
        participant.setParticipantRole(of.createPOCDMT000040ParticipantRole());
        participant.getParticipantRole().getClassCodes().add(type);
        participant.getParticipantRole().setPlayingEntity(of.createPOCDMT000040PlayingEntity());
        PN nameElement = of.createPN();
        nameElement.getContent().add(name);
        participant.getParticipantRole().getPlayingEntity().getNames().add(nameElement);
        return participant;
    }

    /**
     * Luo lääkemääräyksen määräyspäivän annettujen tietojen pohjalta Jos LaakemaaraysTOn määräyspäivää ei ole asetettu
     * käytetään effecitveTimeValuea määräyspäivänä Jos lääkemääräyksen voimassaolon loppuaika on annettu palautetaan
     * määräyspäivä ja voimassaolon loppuaika low/high rakenteessa
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta määräyspäivää ensisijaisesti haetaan
     * @param effectiveTimeValue
     *            String jota käyetään jos määräyspäivää ei muuten saada muodostettua
     * @return SXCMTS elementti jossa määräyspäivä tai IVLTS jossa määräyspäivä ja voimassaolon loppuaika
     */
    protected SXCMTS luoMaaraysPaiva(LaakemaaraysTO laakemaarays, String effectiveTimeValue) {
        String maaraysPaivaValue = effectiveTimeValue;
        if ( null != laakemaarays.getMaarayspaiva() ) {
            maaraysPaivaValue = getDateFormat().format(laakemaarays.getMaarayspaiva());
        }
        if ( null != laakemaarays.getLaakemaarayksenVoimassaolonLoppuaika() ) {
            IVLTS aika = of.createIVLTS();
            aika.setLow(of.createIVXBTS());
            aika.getLow().setValue(maaraysPaivaValue);
            aika.setHigh(of.createIVXBTS());
            // Loppuaika annetaan päivän tarkkuudella (määrittelyjen esimerkin mukaisesti)
            aika.getHigh()
                    .setValue(getShortDateFormat().format(laakemaarays.getLaakemaarayksenVoimassaolonLoppuaika()));
            return aika;
        }
        SXCMTS time = of.createSXCMTS();
        time.setValue(maaraysPaivaValue);
        return time;
    }

    /**
     * Luo entryRelationShip/observation rakenteet lääkemuodoille luo elementinn jos kyseessä lääketietokannan valmiste
     * tai valmisteen laji on: vaikuttavan aineen nimellä määrätty lääke(9) tai vääketietokannan ulkopuolinen
     * valmiste(6) elementti luodaan kaikille laakemaarayksen valmisteen käyttötavoille
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tiedot haetaan
     * @return POCDMT000040EntryRelationShip Collection johon laakemuodot on sijoitettu.
     */
    private Collection<? extends POCDMT000040EntryRelationship> luoLaakemuoto(LaakemaaraysTO laakemaarays) {
        Collection<POCDMT000040EntryRelationship> list = new ArrayList<POCDMT000040EntryRelationship>();
        if ( null != laakemaarays.getValmiste() && (onkoLaaketietokannanValmiste(laakemaarays)
                || "9".equals(laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji())
                || "6".equals(laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji())) ) {
            for (ValmisteenKayttotapaTO kayttotapa : laakemaarays.getValmiste().getKayttotavat()) {
                POCDMT000040EntryRelationship laakemuoto = of.createPOCDMT000040EntryRelationship();
                laakemuoto.setTypeCode(XActRelationshipEntryRelationship.COMP);
                laakemuoto.setObservation(of.createPOCDMT000040Observation());
                ST laakemuotoValue = of.createST();
                laakemuotoValue.getContent().add(kayttotapa.getLaakemuoto());
                asetaObservation(KantaCDAConstants.Laakityslista.LAAKEMUOTO, laakemuotoValue,
                        laakemuoto.getObservation());
                list.add(laakemuoto);
            }
        }
        return list;
    }

    /**
     * Luo entryRelationship/observation rakenteen säilytysastialle jos tieto on annettu hakee säilytysastia tiedon
     * laakemaarayksen valmisteen yksilöintitiedotista
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tietoa haetaan
     * @return POCDMT000040EntryRelationship Collectionin jossa säilytysastia tieto jos se on annettu, muuten tyjhä
     *         colletion
     */
    private Collection<? extends POCDMT000040EntryRelationship> luoSailytysastia(LaakemaaraysTO laakemaarays) {
        Collection<POCDMT000040EntryRelationship> retval = new ArrayList<POCDMT000040EntryRelationship>();
        if ( laakemaarays.getValmiste() == null || laakemaarays.getValmiste().getYksilointitiedot() == null
                || onkoNullTaiTyhja(laakemaarays.getValmiste().getYksilointitiedot().getSailytysastia()) ) {
            return retval;
        }
        POCDMT000040EntryRelationship sailytysastia = of.createPOCDMT000040EntryRelationship();
        sailytysastia.setTypeCode(XActRelationshipEntryRelationship.COMP);
        sailytysastia.setObservation(of.createPOCDMT000040Observation());
        SC sailytysastiaValue = of.createSC();
        sailytysastiaValue.getContent().add(laakemaarays.getValmiste().getYksilointitiedot().getSailytysastia());
        asetaObservation(KantaCDAConstants.Laakityslista.SAILYTYSASTIA, sailytysastiaValue,
                sailytysastia.getObservation());
        retval.add(sailytysastia);
        return retval;
    }

    /**
     * Luo entryRelationship/observation rakenteen valmisteen lajille hakee valmisteen laji tiedon laakemaarayksen
     * valmisteen yksilointitiedoista
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tieto haetaan
     * @return POCDMT000040EntryRelationship Collectionin johon valmisteen laji tieto on sijoitettu
     */
    private Collection<? extends POCDMT000040EntryRelationship> luoValmisteenlaji(LaakemaaraysTO laakemaarays) {
        POCDMT000040EntryRelationship valmisteenlaji = of.createPOCDMT000040EntryRelationship();
        valmisteenlaji.setTypeCode(XActRelationshipEntryRelationship.COMP);
        valmisteenlaji.setObservation(of.createPOCDMT000040Observation());
        CD valmisteenLajiValue = of.createCD();
        String valmisteenLajiCode = laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji();
        // tämä hakee valmisteenlajille oikean displayname, mutta väärät codeSystem ja codeSystemNamet
        fetchAttributes(valmisteenLajiCode + ".valmisteenlaji", valmisteenLajiValue);
        // joten haetaan päälle valmisteenlajin oikeat codeSystem ja codeSystemName
        fetchAttributes("valmisteenlaji", valmisteenLajiValue);

        valmisteenLajiValue.setCode(valmisteenLajiCode);

        asetaObservation(KantaCDAConstants.Laakityslista.LAAKKEEN_LAJI, valmisteenLajiValue,
                valmisteenlaji.getObservation());
        Collection<POCDMT000040EntryRelationship> list = new ArrayList<POCDMT000040EntryRelationship>();
        list.add(valmisteenlaji);
        return list;
    }

    /**
     * Luo participant/participantRole/palyingEntity/name rakenteen myyntiluvan haltijalle jos tieto on annettu hakee
     * myyntiluvan haltija tiedon laakemaarayksen valmisteeen yksilointitiedoista
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tietoa haetaan
     * @return POCDMT000040Participant2 Collection johon myyntiluvan haltija tieto sijoitettu jos se on annettu muuten
     *         tyhjä collection.
     */
    private Collection<? extends POCDMT000040Participant2> luoMyyntiluvanHaltija(LaakemaaraysTO laakemaarays) {
        Collection<POCDMT000040Participant2> list = new ArrayList<POCDMT000040Participant2>();
        if ( laakemaarays.getValmiste() == null || laakemaarays.getValmiste().getYksilointitiedot() == null
                || onkoNullTaiTyhja(laakemaarays.getValmiste().getYksilointitiedot().getMyyntiluvanHaltija()) ) {
            return list;
        }
        POCDMT000040Participant2 participant = of.createPOCDMT000040Participant2();
        participant.getTypeCodes().add("HLD");
        participant.setParticipantRole(of.createPOCDMT000040ParticipantRole());
        participant.getParticipantRole().getClassCodes().add("OWN");
        participant.getParticipantRole().setPlayingEntity(of.createPOCDMT000040PlayingEntity());
        participant.getParticipantRole().getPlayingEntity().getNames().add(of.createPN());
        participant.getParticipantRole().getPlayingEntity().getNames().get(0).getContent()
                .add(laakemaarays.getValmiste().getYksilointitiedot().getMyyntiluvanHaltija());
        list.add(participant);
        return list;
    }

    /**
     * Luo POCDMT000040Reference elementin annetun oidin, setid, typeCode, ja typen pohjalta
     *
     * @param oid
     *            String joka sijoitetaan reference/ExternalDocument/id[@root] attribuuttiin
     * @param setId
     *            String joka sijoitetaan reference/Externaldocument/setId[@root] attribuuttiin
     * @param typeCode
     *            XActRelationshipExternalReference enum joka sijoitetaan reference[@typeCode] attribuutttin
     * @param type
     *            CD elementti jonka tiedot kopioidaan reference/externalDocument/code elementin attribuutteihin
     * @return POCDMT000040Reference elementin
     */
    protected POCDMT000040Reference luoViittaus(String oid, String setId, XActRelationshipExternalReference typeCode,
            CD type) {
        POCDMT000040Reference reference = of.createPOCDMT000040Reference();
        reference.setTypeCode(typeCode);
        reference.setExternalDocument(of.createPOCDMT000040ExternalDocument());
        reference.getExternalDocument().getIds().add(of.createII());
        reference.getExternalDocument().getIds().get(0).setRoot(oid);
        reference.getExternalDocument().setCode(of.createCD());
        copyCodeElement(reference.getExternalDocument().getCode(), type);
        reference.getExternalDocument().setSetId(of.createII());
        reference.getExternalDocument().getSetId().setRoot(setId);
        return reference;
    }

    /**
     * Luo viittauksen itseensä
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tietoja haetaan
     * @return POCDMT000040Reference lista
     */
    protected abstract Collection<POCDMT000040Reference> luoViittaukset(LaakemaaraysTO laakemaarays);

    /**
     * Luo entryRelationship/observation rakenteen lääkemääräyksen iteroinnille jos: iteroinnin määrä ja iterointi
     * teksti on annettu. Iterointi teksti sijoitetaan entryRelationship/observation/text elementtiin
     * entryRelationship/observation/repeatNumber/valueen sijoitetaan iterointien määrä ja
     * entryRelationship/observation/effectiveTime elementin width/unittiin iteroinnin välin yksikkö ja width/valueen
     * iteroinnin väli
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tietoja haetaan
     * @return POCDMT00040EntryRelationship collection johon iteroinnin tiedot on sijoitettu
     */
    private Collection<POCDMT000040EntryRelationship> luoIterointi(LaakemaaraysTO laakemaarays) {
        Collection<POCDMT000040EntryRelationship> list = new ArrayList<POCDMT000040EntryRelationship>();
        if ( laakemaarays.getIterointienMaara() == null || laakemaarays.getIterointienMaara() <= 0
                || onkoNullTaiTyhja(laakemaarays.getIterointiTeksti()) ) {
            return list;
        }
        POCDMT000040EntryRelationship iterointi = of.createPOCDMT000040EntryRelationship();
        iterointi.setTypeCode(XActRelationshipEntryRelationship.COMP);
        iterointi.setObservation(of.createPOCDMT000040Observation());
        iterointi.getObservation().setText(of.createED());
        iterointi.getObservation().getText().getContent().add(laakemaarays.getIterointiTeksti());
        iterointi.getObservation().setRepeatNumber(of.createIVLINT());
        iterointi.getObservation().getRepeatNumber().setValue(BigInteger.valueOf(laakemaarays.getIterointienMaara()));
        IVLTS iterointiValue = of.createIVLTS();
        if ( !onkoNullTaiTyhja(laakemaarays.getIterointienValiUnit())
                && null != laakemaarays.getIterointienValiValue() ) {
            iterointiValue.setWidth(of.createPQ());
            iterointiValue.getWidth().setUnit(laakemaarays.getIterointienValiUnit());
            iterointiValue.getWidth().setValue(String.valueOf(laakemaarays.getIterointienValiValue()));
        }
        iterointi.getObservation().setEffectiveTime(iterointiValue);
        asetaObservation(KantaCDAConstants.Laakityslista.ITEROINTI, null, iterointi.getObservation());
        list.add(iterointi);
        return list;
    }

    /**
     * Luo consumable/manufacturedProduct/ManufacturedMaterial/name rakenteen lääketietokanna ulkopuoliselle
     * valmisteelle
     *
     * @param laaketietokannanUlkopuolinenValmiste
     *            String joka name elementtiin sijoitettan
     * @return POCDMT00040Consumable elmentti johon annettu tieto on sijoitettu
     */
    private POCDMT000040Consumable luoLaaketietokannanlkopuolinenValmiste(String laaketietokannanUlkopuolinenValmiste) {
        POCDMT000040Consumable consumable = of.createPOCDMT000040Consumable();
        consumable.setManufacturedProduct(of.createPOCDMT000040ManufacturedProduct());
        consumable.getManufacturedProduct().setManufacturedLabeledDrug(of.createPOCDMT000040LabeledDrug());
        consumable.getManufacturedProduct().setManufacturedMaterial(of.createPOCDMT000040Material());
        consumable.getManufacturedProduct().getManufacturedMaterial().setCode(of.createCE());
        consumable.getManufacturedProduct().getManufacturedMaterial().getCode().getNullFlavors().add("NI");
        consumable.getManufacturedProduct().getManufacturedMaterial().setName(of.createEN());
        consumable.getManufacturedProduct().getManufacturedMaterial().getName().getContent()
                .add(laaketietokannanUlkopuolinenValmiste);
        return consumable;
    }

    /**
     * Luo consumable/manufacturedProduct rakenteen valmisteelle Jos lääketietokannan ulkopulinen valmiste on annettu
     * luo manufacturedMaterial rakenteen muuten luo manufacturedLabeledDrug rakenteen jonka code elementtiin
     * codeSystemVersion attribuuttiin lääketietokannaversio ja jos atc-koodi on annettu code attribuuttiin atc-koodi ja
     * displayName attribuuttiin atc-koodin nimi muuten lisätään coden nullFlavor attribuutti jonka arvoksi merkitään NI
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tietoja haetaan
     * @return POCDMT000040Consumable elementti johon tiedot on sijoitettu jos valmiste tai laaketietokanna ulkopuolinen
     *         valmiste on annettu muuten null
     */
    private POCDMT000040Consumable luoValmiste(LaakemaaraysTO laakemaarays) {
        if ( null != laakemaarays.getValmiste() ) {
            POCDMT000040Consumable consumable = of.createPOCDMT000040Consumable();
            consumable.setManufacturedProduct(of.createPOCDMT000040ManufacturedProduct());
            // Jos kyseessä on lääketietokannan ulkopuolinen valmiste tai apteekissa valmistettava valmiste
            if ( !onkoNullTaiTyhja(laakemaarays.getLaaketietokannanUlkopuolinenValmiste()) ) {
                consumable.getManufacturedProduct().setManufacturedMaterial(of.createPOCDMT000040Material());
                consumable.getManufacturedProduct().getManufacturedMaterial().setCode(of.createCE());
                consumable.getManufacturedProduct().getManufacturedMaterial().getCode().getNullFlavors().add("NI");
                consumable.getManufacturedProduct().getManufacturedMaterial().setName(of.createEN());
                consumable.getManufacturedProduct().getManufacturedMaterial().getName().getContent()
                        .add(laakemaarays.getLaaketietokannanUlkopuolinenValmiste());
            }
            else {

                consumable.getManufacturedProduct().setManufacturedLabeledDrug(of.createPOCDMT000040LabeledDrug());
                consumable.getManufacturedProduct().getManufacturedLabeledDrug().setCode(of.createCE());

                fetchAttributes(
                        "83.component.substanceAdministration.consumable.manufacturedProduct.manufacturedLabeledDrug.code",
                        consumable.getManufacturedProduct().getManufacturedLabeledDrug().getCode());
                // Lääketietokannan versio
                consumable.getManufacturedProduct().getManufacturedLabeledDrug().getCode()
                        .setCodeSystemVersion(laakemaarays.getLaaketietokannanVersio());

                if ( null != laakemaarays.getValmiste().getYksilointitiedot()
                        && !onkoNullTaiTyhja(laakemaarays.getValmiste().getYksilointitiedot().getATCkoodi()) ) {
                    consumable.getManufacturedProduct().getManufacturedLabeledDrug().getCode()
                            .setCode(laakemaarays.getValmiste().getYksilointitiedot().getATCkoodi());
                    consumable.getManufacturedProduct().getManufacturedLabeledDrug().getCode()
                            .setDisplayName(laakemaarays.getValmiste().getYksilointitiedot().getATCnimi());
                }
                else {
                    consumable.getManufacturedProduct().getManufacturedLabeledDrug().getCode().getNullFlavors()
                            .add("NI");
                }
            }
            return consumable;
        }
        else if ( !onkoNullTaiTyhja(laakemaarays.getLaaketietokannanUlkopuolinenValmiste()) ) {
            return luoLaaketietokannanlkopuolinenValmiste(laakemaarays.getLaaketietokannanUlkopuolinenValmiste());
        }
        return null;
    }

    /**
     * Luo product/manufacturedProduct/manufacturedLabeledDrug rakenteen jos valmisteen laji on annettu ja ja kyseessä
     * on laaketietokannan valmiste. manufacturedLabeledDrug elementin coden codeSystemVersion attribuuttin sijoitettaan
     * lääketietokannan versio code elementin code attribuuttiin laakemaarayksen valmisteen yksilointitietojen
     * yksilointitunnus ja displayName attribuuttiin kauppanimi jos valmisteen laji Myyntiluvallinen lääkevalmiste(1),
     * Lääketietokannassa oleva perusvoide(2), Lääketietokannassa oleva kliininen ravintovalmiste(3), Määräaikainen
     * erityislupavalmiste(4) tai Lääketietokannan rekisteröity lääkevalmiste(5), ja kauppanimi on annettu jos
     * yksilöintitunnusta ei ole annettu: code nullFlavor attrubuuttin arvoksi laitetaan NI ja
     * manufacturedLabeledDrug/name elementin arvoksi kauppanimi
     * 
     * @param laakemaarays
     *            LaakemaaraysTO josta tietoja haetaan
     * @return POCDMT00040Product elementti jos valmisteen laji on annettu ja kyseessä on lääketietokannan valmiste
     *         muuten null
     */
    private POCDMT000040Product luoVNRKoodi(LaakemaaraysTO laakemaarays) {
        if ( laakemaarays.getValmiste() == null || laakemaarays.getValmiste().getYksilointitiedot() == null
                || laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji() == null
                || !onkoLaaketietokannanValmiste(laakemaarays) ) {
            return null;
        }
        POCDMT000040Product product = of.createPOCDMT000040Product();
        product.setManufacturedProduct(of.createPOCDMT000040ManufacturedProduct());
        product.getManufacturedProduct().setManufacturedLabeledDrug(of.createPOCDMT000040LabeledDrug());
        product.getManufacturedProduct().getManufacturedLabeledDrug().setCode(of.createCE());
        product.getManufacturedProduct().getManufacturedLabeledDrug().setName(of.createEN());
        fetchAttributes(
                "83.component.substanceAdministration.entryRelationship.supply.manufacturedProduct.manufacturedLabeledDrug.code",
                product.getManufacturedProduct().getManufacturedLabeledDrug().getCode());
        product.getManufacturedProduct().getManufacturedLabeledDrug().getCode()
                .setCodeSystemVersion(laakemaarays.getLaaketietokannanVersio());
        // Viedään yksilöintitunnus sanomalle vain, jos se on VNR-numero, muita yksilöintitunnuksia ei välitetä
        // (toistaiseksi)
        if ( laakemaarays.getValmiste().getYksilointitiedot().getTunnuksenTyyppi() == KantaCDAConstants.VNR_tunnus
                && !onkoNullTaiTyhja(laakemaarays.getValmiste().getYksilointitiedot().getYksilointitunnus()) ) {
            product.getManufacturedProduct().getManufacturedLabeledDrug().getCode()
                    .setCode(laakemaarays.getValmiste().getYksilointitiedot().getYksilointitunnus());
            if ( ("1".equals(laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji())
                    || "2".equals(laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji())
                    || "3".equals(laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji())
                    || "4".equals(laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji())
                    || "5".equals(laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji())) && // Myyntiluvallisella
            // lääkkeellä
            // (valmisteen
            // laji=1) on
            // aina
            // ilmoitettava
            // VNR-numero
            // ja
            // kauppanimi.
                    !onkoNullTaiTyhja(laakemaarays.getValmiste().getYksilointitiedot().getKauppanimi()) ) {
                product.getManufacturedProduct().getManufacturedLabeledDrug().getCode()
                        .setDisplayName(laakemaarays.getValmiste().getYksilointitiedot().getKauppanimi());
            }
        }
        else {
            product.getManufacturedProduct().getManufacturedLabeledDrug().getCode().getNullFlavors().add("NI");
            // Koska valmisteella ei ole VNR-numeroa, ilmoitetaan kauppanimi name elementissä
            product.getManufacturedProduct().getManufacturedLabeledDrug().getName().getContent()
                    .add(laakemaarays.getValmiste().getYksilointitiedot().getKauppanimi());
        }
        return product;
    }

    /**
     * Luo subject/relatedSubject rakenteen potilaan tiedoille relatedSubject/code elementin code attribuuttin
     * sijoitettan potilaan hetu relatedSubject/subject/name elementteihin potilaan nimet ja
     * relatedSubject/subject/birthTime/valueen potilaan syntymä aika
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tiedot haetaan
     * @return POCDMT00040Subject elementti johon tiedot on sijoitettu.
     */
    private POCDMT000040Subject luoPotilaantiedot(LaakemaaraysTO laakemaarays) {
        POCDMT000040Subject subject = of.createPOCDMT000040Subject();
        subject.setTypeCode(ParticipationTargetSubject.SBJ);
        subject.setRelatedSubject(of.createPOCDMT000040RelatedSubject());
        subject.getRelatedSubject().setClassCode(XDocumentSubject.PAT);
        subject.getRelatedSubject().setCode(of.createCE());
        // potilaan hetu
        if ( !onkoNullTaiTyhja(laakemaarays.getPotilas().getHetu()) ) {
            subject.getRelatedSubject().getCode().setCode(laakemaarays.getPotilas().getHetu());
            subject.getRelatedSubject().getCode().setCodeSystem(fetchProperty("recordTarget.patientRole.id.root"));
        }
        else {
            subject.getRelatedSubject().getCode().getNullFlavors().add(KantaCDAConstants.NullFlavor.NA.getCode());
        }
        subject.getRelatedSubject().setSubject(of.createPOCDMT000040SubjectPerson());
        subject.getRelatedSubject().getSubject().getClassCodes().add("PSN");
        // potilaan nimi
        subject.getRelatedSubject().getSubject().getNames().add(getNames(laakemaarays.getPotilas().getNimi()));
        subject.getRelatedSubject().getSubject().setBirthTime(of.createTS());
        // potilaan syntymäaika
        subject.getRelatedSubject().getSubject().getBirthTime().setValue(laakemaarays.getPotilas().getSyntymaaika());
        return subject;
    }

    /**
     * Luo doseQuantity/translation/originalText rakenteen lääkeaineen vahvuudelle jos vahvuus annettu originalText
     * elementtiin sijoitettan laakemaarayksen valmisteen yksilointitietojen vahvuus jos kysessä apteekissa valmisttava
     * lääke ja laakemaarayksen apteekissa valmistettavan lääkkeen valmistus ohje on annettu lisätään doseQuantityyn
     * translation/originalText johon sijoitettu laakemaarayksen apteekissa valmistettavan lääkkeen valmistusohje
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tietoja heataan
     * @return IVLPQ elementti jos laakemaarayksen valmisteen yksilointitetojen vahvuus annettu muuten null
     */
    private IVLPQ luoLaakeaineenVahvuus(LaakemaaraysTO laakemaarays) {
        if ( laakemaarays.getValmiste() == null || laakemaarays.getValmiste().getYksilointitiedot() == null
                || onkoNullTaiTyhja(laakemaarays.getValmiste().getYksilointitiedot().getVahvuus()) ) {
            return null;
        }
        IVLPQ doseQuantity = of.createIVLPQ();
        doseQuantity.getNullFlavors().add("NA");
        doseQuantity.getTranslations().add(of.createPQR());
        doseQuantity.getTranslations().get(0).setOriginalText(of.createED());
        doseQuantity.getTranslations().get(0).getOriginalText().getContent()
                .add(laakemaarays.getValmiste().getYksilointitiedot().getVahvuus());

        // TODO: voiko olla apteekissa valmistettava ilman että on määrätty vaikuttavan aineen nimellä ja valmisteella??
        if ( laakemaarays.isApteekissaValmistettavaLaake() && null != laakemaarays.getApteekissaValmistettavaLaake()
                && !onkoNullTaiTyhja(laakemaarays.getApteekissaValmistettavaLaake().getValmistusohje()) ) {
            PQR valmistusOhje = of.createPQR();
            valmistusOhje.setOriginalText(of.createED());
            valmistusOhje.getOriginalText().getContent()
                    .add(laakemaarays.getApteekissaValmistettavaLaake().getValmistusohje());
            doseQuantity.getTranslations().add(valmistusOhje);
        }
        return doseQuantity;
    }

    /**
     * Luo lääkemääräyksen narrative osuuden. Narrative osuioon tulee omakappaleensa Vaikuttavalle aineelle tai
     * kauppanimelle sekä vahvuudelle, lääkemuodolle ja annostukselle jos ne löytyvät laakemaarayksesta.
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tiedot poimitaan.
     * @return StucDocText elementti johon tiedot on sijoitettu
     */
    protected StrucDocText luoNarrativeLaakemaarays(LaakemaaraysTO laakemaarays) {
        StrucDocText text = of.createStrucDocText();
        if ( "9".equals(laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji()) ) { // Vaikuttavalla
            // aineella
            // Vaikuttava aine (jos vaikuvalla aineella)
            text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent(
                    "Vaikuttava aine: " + laakemaarays.getValmiste().getYksilointitiedot().getATCnimi())));
        }
        else {
            // Kauppanimi (jos kauppanimellä || apteekissa valmistettava)
            String kauppanimi = laakemaarays.getValmiste().getYksilointitiedot().getKauppanimi();
            if ( laakemaarays.isApteekissaValmistettavaLaake() ) {
                kauppanimi = "Apteekissa valmistettava lääke";
            }
            if ( !onkoNullTaiTyhja(laakemaarays.getLaaketietokannanUlkopuolinenValmiste()) ) {
                kauppanimi = laakemaarays.getLaaketietokannanUlkopuolinenValmiste();
            }
            text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent("Kauppanimi: " + kauppanimi)));
        }
        // Vahvuus
        if ( !onkoNullTaiTyhja(laakemaarays.getValmiste().getYksilointitiedot().getVahvuus()) ) {
            text.getContent().add(of.createStrucDocItemParagraph(
                    luoParagraphContent("Vahvuus: " + laakemaarays.getValmiste().getYksilointitiedot().getVahvuus())));
        }
        // Lääkemuoto
        StringBuilder buffer = new StringBuilder();
        for (ValmisteenKayttotapaTO kayttotapa : laakemaarays.getValmiste().getKayttotavat()) {
            buffer.append(" " + kayttotapa.getLaakemuoto());
        }
        String laakemuoto = buffer.toString();
        if ( !laakemuoto.isEmpty() ) {
            text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent("Lääkemuoto:" + laakemuoto)));
        }
        // Annostus
        if ( !onkoNullTaiTyhja(KantaCDAUtil.poistaKontrolliMerkit(laakemaarays.getAnnostusohje())) ) {
            text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent(
                    "Annostus: " + KantaCDAUtil.poistaKontrolliMerkit(laakemaarays.getAnnostusohje()))));
        }
        return text;
    }

    /**
     * Palauttaa property cda tyyppiä vastaavan property avaimen jolla saadaan property tiedostosta luettua dokumentin
     * tyyppi tiedot
     *
     * @param cdaTyyppi
     *            int dokumentin cda tyyppi koodi
     * @param args
     *            String formatia varten property avaimen osa ([0]), jota käytetään propertyn tunnistamiseen jos tietoa
     *            tarvitaan
     * @return Sting property avain. Null jos ei validi tyyppi.
     */
    protected String getPropertyCode(int cdaTyyppi, Object[] args) {
        if ( cdaTyyppi == KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYS.getTyyppi() ) {
            return "code";
        }
        else if ( cdaTyyppi == KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYKSEN_KORJAUS.getTyyppi()
                || cdaTyyppi == KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYKSEN_MITATOINTI.getTyyppi() ) {
            return String.format(ReseptiKasaaja.code, args);
        }
        return null;
    }

    /**
     * Apumetodi POCDMT00040Observation elementin täyttämiseen asettaa annetun observation: classCodeksi "OBS"
     * moodCodeksi "EVN" luo observationille coden jonka attribuutteihin tietoja haetaan annetulla keyllä lisää
     * observaionille valueksi annetun valuen
     *
     * @param key
     *            String avain jolla observationini attribuutteihin tietoja haetaan
     * @param value
     *            ANY joka lisätään observationin valueihin
     * @param observation
     *            POCDMT00040Observation johon tiedot sijoitetaan
     */
    protected void asetaObservation(String key, ANY value, POCDMT000040Observation observation) {
        observation.getClassCodes().add("OBS");
        observation.setMoodCode(XActMoodDocumentObservation.EVN);
        observation.setCode(of.createCD());
        fetchAttributes(key, observation.getCode());
        observation.getCode().setCode(key);
        if ( null != value ) {
            observation.getValues().add(value);
        }
    }

    /**
     * Luo entryRelationship/observation rakenteen laitteelle jos tieto on annettu hakee laitetiedon laakemaarayksen
     * valmisteen yksilöintitiedotista
     *
     * @param laakemaarays
     *            LaakemaaraysTO josta tietoa haetaan
     * @return POCDMT000040EntryRelationship Collectionin jossa laite tieto jos se on annettu, muuten tyjhä colletion
     */
    private Collection<? extends POCDMT000040EntryRelationship> luoPakkauksenLaite(LaakemaaraysTO laakemaarays) {
        Collection<POCDMT000040EntryRelationship> retval = new ArrayList<POCDMT000040EntryRelationship>();
        if ( laakemaarays.getValmiste() == null || laakemaarays.getValmiste().getYksilointitiedot() == null
                || onkoNullTaiTyhja(laakemaarays.getValmiste().getYksilointitiedot().getPakkauslaite()) ) {
            return retval;
        }
        POCDMT000040EntryRelationship laite = of.createPOCDMT000040EntryRelationship();
        laite.setTypeCode(XActRelationshipEntryRelationship.COMP);
        laite.setObservation(of.createPOCDMT000040Observation());
        ST laiteValue = of.createST();
        laiteValue.getContent().add(laakemaarays.getValmiste().getYksilointitiedot().getPakkauslaite());
        asetaObservation(KantaCDAConstants.Laakityslista.LAITE, laiteValue, laite.getObservation());
        retval.add(laite);
        return retval;
    }

    private boolean onkoPKVLaake(LaakemaaraysTO to) {
        if ( onkoNullTaiTyhja(to.getPKVlaakemaarays()) || "-".equals(to.getPKVlaakemaarays()) ) {
            return false;
        }
        return true;
    }

    /**
     * Luo reseptin korjaukselle narrative osion johon sijoitetaan alkuperäisen reseptin Paikka, tekopäivämäärä ja
     * lääkärin nimi sekä korjatun reseptin Paikka, tekopäivämäärä ja lääkärin nimi
     *
     *
     * @param alkuperainenLaakemaarays
     *            LaakemaaraysTO josta poimitaan alkuperäiset tiedot
     * @param uusiLaakemaarays
     *            LaakemaaraysTO josta poimitaan lääkärin nimi
     * @param today
     *            String joka sijoitetaan tekopäivämääräksi.
     * @return StrucDocText jossa tiedot omissa kappaleissaan
     */
    protected StrucDocText luoKorjausNarrativePaikkaPvmLaakari(LaakemaaraysTO alkuperainenLaakemaarays,
            LaakemaaraysTO uusiLakemaarays, String today) {
        StrucDocText text = of.createStrucDocText();
        text.getContent().add(of.createStrucDocItemParagraph(
                luoParagraphContent(alkuperainenLaakemaarays.getAmmattihenkilo().getOrganisaatio().getNimi())));
        text.getContent().add(of.createStrucDocItemParagraph(
                luoParagraphContent(getTodayDateFormat().format(alkuperainenLaakemaarays.getMaarayspaiva()))));
        text.getContent().add(of.createStrucDocItemParagraph(
                luoParagraphContent(alkuperainenLaakemaarays.getAmmattihenkilo().getKokonimi().getKokoNimi())));
        text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent("Lääkemääräyksen korjaaja:")));
        text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent("Kelain")));
        text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent(today)));
        text.getContent().add(of.createStrucDocItemParagraph(
                luoParagraphContent(uusiLakemaarays.getAmmattihenkilo().getKokonimi().getKokoNimi())));
        return text;
    }

    /**
     * Luo reseptin mitätöinnille narrative osion, joka kopioidaan alkuperäiseltä reseptiltä
     *
     * @param alkuperainenLaakemaarays
     *            LaakemaaraysTO josta poimitaan alkuperäiset tiedot
     * @return StrucDocText jossa tiedot omissa kappaleissaan
     */
    protected StrucDocText luoMitatointiNarrativePaikkaPvmLaakari(LaakemaaraysTO alkuperainenLaakemaarays) {
        StrucDocText text = of.createStrucDocText();
        if ( alkuperainenLaakemaarays.getNayttomuoto() != null
                && !alkuperainenLaakemaarays.getNayttomuoto().isEmpty() ) {
            for (String teksti : alkuperainenLaakemaarays.getNayttomuoto()) {
                text.getContent().add(of.createStrucDocItemParagraph(luoParagraphContent(teksti)));
            }
        }
        return text;
    }

}
