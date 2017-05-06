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

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import javax.xml.bind.JAXBElement;

import org.apache.commons.configuration.ConfigurationException;
import org.hl7.v3.AD;
import org.hl7.v3.ANY;
import org.hl7.v3.AdxpCity;
import org.hl7.v3.AdxpCountry;
import org.hl7.v3.AdxpPostalCode;
import org.hl7.v3.AdxpStreetAddressLine;
import org.hl7.v3.BIN;
import org.hl7.v3.EN;
import org.hl7.v3.ENXP;
import org.hl7.v3.EnDelimiter;
import org.hl7.v3.EnFamily;
import org.hl7.v3.EnGiven;
import org.hl7.v3.EnPrefix;
import org.hl7.v3.EnSuffix;
import org.hl7.v3.PN;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040InfrastructureRootTemplateId;
import org.hl7.v3.POCDMT000040Organization;
import org.hl7.v3.POCDMT000040Section;
import org.hl7.v3.StrucDocContent;
import org.hl7.v3.StrucDocParagraph;
import org.hl7.v3.StrucDocText;
import org.hl7.v3.TEL;

import fi.kela.kanta.exceptions.PurkuException;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.LaakemaarayksenKorjausTO;
import fi.kela.kanta.to.LaakemaarayksenMitatointiTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.LeimakentatTO;
import fi.kela.kanta.to.OrganisaatioTO;
import fi.kela.kanta.to.OsoiteTO;
import fi.kela.kanta.util.AsiakirjaVersioUtil;
import fi.kela.kanta.util.KantaCDAUtil;

public abstract class Purkaja {
    final private static String sdfKuvio = "yyyyMMddHHmmss";
    private static final String TelPrefix = "tel:";
    private static final String EmailPrefix = "mailto:";
    private static final String resepti_properties = "resepti.properties";

    protected abstract String getCodeSystem();

    private AsiakirjaVersioUtil versioUtil;

    private MaarittelyKonfiguraatio maarittelyKonfiguraatio;

    protected Purkaja() throws ConfigurationException {
        if ( maarittelyKonfiguraatio == null ) {
            maarittelyKonfiguraatio = MaarittelyKonfiguraatio.lueKonfiguraatio();
        }
    }

    protected void puraLeimakentat(POCDMT000040ClinicalDocument clinicalDocument, LeimakentatTO<?> kentat)
            throws PurkuException {
        puraLeimakentat(clinicalDocument, kentat, false);
    }

    /**
     * Purkaa annetusta clinicalDocumentista leimakentät Asettaa Oidin, SetIdn, CdaTyypin, versionumeron ja aikaleiman
     * annettuun LeimakentatTO:sta perittyyn TOhon, annetun clinicaldocumentin pohjalta. jos isKooste on true asettaa
     * cdaTyypiksi -1
     *
     * @param clinicalDocument
     *            POCDMT000040ClinicalDocument cda josta tietoja puretaan
     * @param kentat
     *            LeimakantatTOsta periytyvä TO luokkaa johon tiedot sijoitetaan
     * @param isKooste
     *            boolean tieto siitä onko kyseessä kooste
     * @throws PurkuException
     */
    protected void puraLeimakentat(POCDMT000040ClinicalDocument clinicalDocument, LeimakentatTO<?> kentat,
            boolean isKooste) throws PurkuException {
        kentat.setOid(clinicalDocument.getId().getRoot());
        kentat.setSetId(clinicalDocument.getSetId().getRoot());

        final String tyyppiKoodi = clinicalDocument.getCode().getCode();
        if ( null != tyyppiKoodi && tyyppiKoodi.length() > 0 ) {
            kentat.setCdaTyyppi(Integer.parseInt(tyyppiKoodi));
        }
        else {
            if ( !isKooste ) {
                throw new PurkuException("clinicalDocument/code/@code");
            }
            else {
                kentat.setCdaTyyppi(-1);
            }
        }

        final String versionumero = String.valueOf(clinicalDocument.getVersionNumber().getValue());
        if ( versionumero.length() > 0 && !"null".equals(versionumero) ) {
            kentat.setVersio(Integer.parseInt(versionumero));
        }
        else {
            kentat.setVersio(0);
        }

        final String aikaleima = clinicalDocument.getEffectiveTime().getValue();
        if ( null != aikaleima && aikaleima.length() > 12 && !"nulldate".equals(aikaleima) ) {
            kentat.setAikaleima(puraAika(aikaleima.substring(0, 12)));
        }

        /*
         * Järjestelmän ja version lukeminen
         */
        if ( null != clinicalDocument.getLocalHeader()
                && null != clinicalDocument.getLocalHeader().getSoftwareSupport() ) {
            String product = clinicalDocument.getLocalHeader().getSoftwareSupport().getProduct();
            if ( null != product && product.length() > 0 ) {
                kentat.setProduct(product);
            }
            String version = clinicalDocument.getLocalHeader().getSoftwareSupport().getVersion();
            if ( null != version && version.length() > 0 ) {
                kentat.setProductVersion(version);
            }
        }

        /*
         * CDA:n määrittelyversion päätteleminen / Header
         */
        for (POCDMT000040InfrastructureRootTemplateId templateId : clinicalDocument.getTemplateIds()) {
            if ( templateId.getRoot() != null && !templateId.getRoot().isEmpty() ) {
                if ( templateId.getExtension() != null && !templateId.getExtension().isEmpty() ) {
                    kentat.getTemplateIds().add(templateId.getRoot() + "." + templateId.getExtension());
                }
                else {
                    kentat.getTemplateIds().add(templateId.getRoot());
                }
            }
        }

        /*
         * CDA:n määrittelyversio / StructuredBody
         */
        for (POCDMT000040InfrastructureRootTemplateId templateId : haeStructuredBodyTemplateIs(clinicalDocument)) {
            if ( templateId.getRoot() != null && !templateId.getRoot().isEmpty() ) {
                if ( templateId.getExtension() != null && !templateId.getExtension().isEmpty() ) {
                    kentat.getBodyTemplateIds().add(templateId.getRoot() + "." + templateId.getExtension());
                }
                else {
                    kentat.getBodyTemplateIds().add(templateId.getRoot());
                }
            }
        }

        kentat.setMaarittelyLuokka(maarittelyKonfiguraatio.haeMaarittelyLuokka(kentat.getTemplateIds(),
                clinicalDocument.getCode().getCode()));
    }

    /**
     * Apumetodi nimen purkamiseen Muodostaan KokoNimiTOn annetun PN Listan pohjalta
     *
     * @param names
     *            List<PN> nimet
     * @return KokoNimiTO nimistä
     */
    protected KokoNimiTO puraKokoNimi(List<PN> names) {
        KokoNimiTO kokoNimi = new KokoNimiTO();
        for (PN name : names) {
            for (Serializable element : name.getContent()) {
                if ( element instanceof JAXBElement<?> ) {
                    JAXBElement<?> el = (JAXBElement<?>) element;
                    if ( el.getValue() instanceof ENXP ) {
                        puraNimitieto((ENXP) el.getValue(), kokoNimi);
                    }
                }
            }

        }
        return kokoNimi;
    }

    /**
     * Apumetodi organisaation tietojen purkamiseen
     *
     * @param representedOrganization
     *            POCDMT000040Organization josta tiedot poimitaan
     * @return OrganisaatioTO johon tiedot sijoitetaan
     */
    protected OrganisaatioTO puraOrganisaatio(POCDMT000040Organization organization) {
        OrganisaatioTO organisaatioTO = new OrganisaatioTO();
        organisaatioTO.setYksilointitunnus(organization.getIds().get(0).getRoot());
        if ( !organization.getNames().isEmpty() ) {
            organisaatioTO.setNimi((String) organization.getNames().get(0).getContent().get(0));
        }
        for (TEL tel : organization.getTelecoms()) {
            if ( tel.getValue().startsWith(Purkaja.TelPrefix) ) {
                organisaatioTO.setPuhelinnumero(tel.getValue());
                if ( !tel.getUses().isEmpty() ) {
                    organisaatioTO.setPuhelinumeroKayttotarkoitus(tel.getUses().get(0));
                }
            }
            else if ( tel.getValue().startsWith(Purkaja.EmailPrefix) ) {
                organisaatioTO.setSahkoposti(tel.getValue());
            }
        }

        if ( !organization.getAddrs().isEmpty() ) {
            organisaatioTO.setOsoite(puraOsoite(organization.getAddrs().get(0)));
        }
        if ( organization.getAsOrganizationPartOf() != null
                && organization.getAsOrganizationPartOf().getWholeOrganization() != null ) {
            organisaatioTO.setToimintaYksikko(
                    puraOrganisaatio(organization.getAsOrganizationPartOf().getWholeOrganization()));
        }

        return organisaatioTO;
    }

    /**
     * Apumetodi osoitetietojen purkamiseen
     *
     * @param adddr
     *            AD josta osoitetietoja haetaan
     * @return OsoiteTO johon löytyneet osoitetiedot on sijoitettu
     */
    protected OsoiteTO puraOsoite(AD addr) {
        // TODO: Parempi tapa tunnistaa elementit?
        OsoiteTO osoite = new OsoiteTO();
        for (Serializable serializable : addr.getContent()) {
            if ( !(serializable instanceof JAXBElement<?>) ) {
                continue;
            }
            JAXBElement<?> element = (JAXBElement<?>) serializable;
            if ( element.getValue() instanceof AdxpStreetAddressLine ) {
                AdxpStreetAddressLine value = (AdxpStreetAddressLine) element.getValue();
                if ( value.getContent() != null && !value.getContent().isEmpty() ) {
                    osoite.setKatuosoite((String) value.getContent().get(0));
                }
            }
            else if ( element.getValue() instanceof AdxpPostalCode ) {
                AdxpPostalCode value = (AdxpPostalCode) element.getValue();
                if ( value.getContent() != null && !value.getContent().isEmpty() ) {
                    osoite.setPostinumero((String) value.getContent().get(0));
                }
            }
            else if ( element.getValue() instanceof AdxpCity ) {
                AdxpCity value = (AdxpCity) element.getValue();
                if ( value.getContent() != null && !value.getContent().isEmpty() ) {
                    osoite.setPostitoimipaikka((String) value.getContent().get(0));
                }
            }
            else if ( element.getValue() instanceof AdxpCountry ) {
                AdxpCountry value = (AdxpCountry) element.getValue();
                if ( value.getContent() != null && !value.getContent().isEmpty() ) {
                    osoite.setMaa((String) value.getContent().get(0));
                }
            }
        }
        return osoite;
    }

    /**
     * Apumetodi ajanpurkamiseen parsii annetun ajan jos aika ei ole null eikä sisällä muuta kuin numeroita olettaa että
     * aika annetaan yyyyMMddHHmmss formaatissa Jos annettu aika pitempikuin 14 merkkiä, yli menevä osa pätkäistään pois
     * Jos formaatti pattern on pidempi kuin annettu aika, pätkäistään siitä ylimenevä osa pois
     *
     * @param aika
     *            String purettava aika
     * @return Date tai null jos annettu null tai liian lyhyt tai ei numeroita sisältävä merkkijono
     * @throws PurkuException
     */
    protected Date puraAika(String aika) throws PurkuException {
        if ( aika != null ) {
            String lyhytAika;
            if ( aika.length() > Purkaja.sdfKuvio.length() ) {
                lyhytAika = aika.substring(0, Purkaja.sdfKuvio.length());
            }
            else {
                lyhytAika = aika;
            }
            // 0000-99999999999999
            if ( lyhytAika.matches("[0-9]{4,14}") ) {
                SimpleDateFormat sdf = new SimpleDateFormat(Purkaja.sdfKuvio.substring(0, lyhytAika.length()));
                sdf.setTimeZone(TimeZone.getTimeZone(ReseptiKasaaja.TIME_ZONE));
                try {
                    return sdf.parse(lyhytAika);
                }
                catch (ParseException e) {
                    throw new PurkuException(aika);
                }
            }
        }
        return null;
    }

    /**
     * Apumetodi jolla tarkistetaan onko merkkijono null tai tyhjä
     *
     * @param merkkijono
     *            Sring tarkistettava merkkijono
     * @return boolean true jos merkkijono on null tai tyhja, muuten false
     */
    protected boolean onkoNullTaiTyhja(String merkkijono) {
        return merkkijono == null || merkkijono.isEmpty();
    }

    /**
     * Apumetodi jolla voidaan hakea elementin content palauttaa elementin content listasta ensimmäisen itemin jos lista
     * ei ole tyhjä.
     *
     * @param element
     *            ANY jonka content halutaan hakea
     * @return String content listan ensimmäisestä itemistä, null jos lista tyhjä tai elementillä ei content listaa ole.
     */
    protected String puraContent(ANY element) {
        if ( element instanceof BIN && !((BIN) element).getContent().isEmpty() ) {
            return (String) ((BIN) element).getContent().get(0);
        }
        else if ( element instanceof EN && !((EN) element).getContent().isEmpty() ) {
            return (String) ((EN) element).getContent().get(0);
        }
        else if ( element instanceof AD && !((AD) element).getContent().isEmpty() ) {
            return (String) ((AD) element).getContent().get(0);
        }
        return null;
    }

    /**
     * Apumetodi nimitietojen purkamiseen. Purkaa ENXP elementin contentin ja qualifierin jos sellainen on. Lisää
     * löytyneet nimi tiedot annettuun kokonimeen jos nimen tyyppi pystytään tunnistamaan.
     *
     * @param value
     *            ENXP elementti josta nimitietoja haetaan.
     * @param kokoNimi
     *            KokoNimiTO johon nimitiedot laitetaan.
     */
    private void puraNimitieto(ENXP value, KokoNimiTO kokoNimi) {
        if ( value == null || value.getContent().isEmpty() ) {
            return;
        }
        String nimi = (String) value.getContent().get(0);
        String maare = null;
        if ( !value.getQualifiers().isEmpty() ) {
            maare = value.getQualifiers().get(0);
        }
        String tyyppi = null;
        if ( value instanceof EnGiven ) {
            tyyppi = "given";
        }
        else if ( value instanceof EnFamily ) {
            tyyppi = "family";
        }
        else if ( value instanceof EnPrefix ) {
            tyyppi = "prefix";
        }
        else if ( value instanceof EnSuffix ) {
            tyyppi = "suffix";
        }
        else if ( value instanceof EnDelimiter ) {
            tyyppi = "delimiter";
        }
        if ( null != nimi && null != tyyppi ) {
            kokoNimi.lisaa(tyyppi, maare, nimi);
        }
    }

    protected LaakemaaraysTO luoLaakemaaraysTO(POCDMT000040ClinicalDocument clinicalDocument) {
        int tyyppiKoodi = Integer.parseInt(clinicalDocument.getCode().getCode());
        if ( KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYKSEN_KORJAUS.getTyyppi() == tyyppiKoodi ) {
            return new LaakemaarayksenKorjausTO();
        }
        if ( KantaCDAConstants.ReseptisanomanTyyppi.LAAKEMAARAYKSEN_MITATOINTI.getTyyppi() == tyyppiKoodi ) {
            return new LaakemaarayksenMitatointiTO();
        }

        return new LaakemaaraysTO();
    }

    protected Properties loadProperties() {
        Properties props = null;
        try {
            props = KantaCDAUtil.loadProperties(resepti_properties);
        }
        catch (IOException e) {
            throw new RuntimeException("KantaCDA-API / Purkaja: properties tiedoston luku epäonnistui", e);
        }
        return props;
    }

    protected void tarkistaAsiakirjaVersio(POCDMT000040ClinicalDocument clinicalDocument, LaakemaaraysTO laakemaarays) {
        laakemaarays.setAsiakirjaYhteensopivuus(KantaCDAConstants.AsiakirjaVersioYhteensopivuus.EI_TUETTU);
        List<POCDMT000040InfrastructureRootTemplateId> templatet = clinicalDocument.getTemplateIds();
        if ( templatet != null && !templatet.isEmpty() ) {
            String id = templatet.get(0).getRoot();
            laakemaarays.setAsiakirjaVersio(id);

            if ( versioUtil == null ) {
                versioUtil = new AsiakirjaVersioUtil(loadProperties());
            }

            laakemaarays.setAsiakirjaYhteensopivuus(versioUtil.getAsiakirjaVersionYhteensopivuus(id));
        }
    }

    /**
     * Asetetaan asiakirjamääritysversioiden käsittelyyn tarvittava luokka ulkopäin. Tarkoitettu lähinnä testauksen
     * helpottamiseen.
     *
     * @param versioUtil
     * @deprecated
     */
    @Deprecated
    protected void setVersioUtil(AsiakirjaVersioUtil versioUtil) {
        this.versioUtil = versioUtil;
    }

    protected void puraText(POCDMT000040Section section, List<String> nayttomuoto) {
        StrucDocText text = section.getText();
        if ( text != null && text.getContent() != null && !text.getContent().isEmpty() ) {
            List<Serializable> content = text.getContent();
            for (int i = 0; i < content.size(); i++) {
                if ( !(content.get(i) instanceof JAXBElement) ) {
                    continue;
                }
                JAXBElement<?> elem = (JAXBElement<?>) content.get(i);
                if ( elem.getValue() instanceof StrucDocParagraph ) {
                    StrucDocParagraph paragraph = (StrucDocParagraph) elem.getValue();
                    puraDocParagraph(paragraph, nayttomuoto);
                }
            }
        }
    }

    protected void puraDocParagraph(StrucDocParagraph paragraph, List<String> nayttomuoto) {
        List<Serializable> content = paragraph.getContent();
        for (int i = 0; i < content.size(); i++) {
            if ( content.get(i) instanceof JAXBElement ) {
                JAXBElement<?> elem = (JAXBElement<?>) content.get(i);
                if ( elem.getValue() instanceof StrucDocContent ) {
                    StrucDocContent doc = (StrucDocContent) elem.getValue();
                    puraDocContent(doc, nayttomuoto);
                }
            }

        }
    }

    protected void puraDocContent(StrucDocContent content, List<String> nayttomuoto) {
        List<Serializable> cont = content.getContent();
        for (int i = 0; i < cont.size(); i++) {
            if ( cont.get(i) instanceof String ) {
                String arvo = (String) cont.get(i);
                nayttomuoto.add(arvo);
            }
        }
    }

    /**
     * Hakee clinicalDocument elementistä templateId:n rakenteesta component/structuredBody/component
     *
     * @param clinicalDocument
     *            POCDMT00040ClinicalDocument josta entryjä haetaan
     * @return POCDMT000040InfrastructureRootTemplateId lista / tyhjä, jos rakennetta ei löydy
     */
    private List<POCDMT000040InfrastructureRootTemplateId> haeStructuredBodyTemplateIs(
            POCDMT000040ClinicalDocument clinicalDocument) {
        if ( clinicalDocument == null || clinicalDocument.getComponent() == null
                || clinicalDocument.getComponent().getStructuredBody() == null
                || clinicalDocument.getComponent().getStructuredBody().getComponents().isEmpty() || clinicalDocument
                        .getComponent().getStructuredBody().getComponents().get(0).getTemplateIds().isEmpty() ) {
            return new ArrayList<POCDMT000040InfrastructureRootTemplateId>();
        }
        return clinicalDocument.getComponent().getStructuredBody().getComponents().get(0).getTemplateIds();
    }
}
