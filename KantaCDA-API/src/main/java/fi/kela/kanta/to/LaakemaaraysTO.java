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
package fi.kela.kanta.to;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import fi.kela.kanta.cda.KantaCDAConstants.AsiakirjaVersioYhteensopivuus;
import fi.kela.kanta.to.LeimakentatTO;

public class LaakemaaraysTO extends LeimakentatTO<LaakemaaraysTO> implements Serializable {

    private static final long serialVersionUID = 156473242345L;

    // Alle 12-vuotiaan paino
    private BigDecimal alle12VuotiaanPainoValue;
    private String alle12VuotiaanPainoUnit;
    // Annosjakelu teksti: Ei Käytössä
    private String annosjakeluTeksti;
    // Erillisselvityskoodi
    private String erillisselvitys;
    // Erillisselvitysteksti
    private String erillisselvitysteksti;
    // Erilliselvitysotsikko
    private String erillisselvitysotsikko;
    // Lääkemääräyksen voimassaolon loppuaika
    private Date laakemaarayksenVoimassaolonLoppuaika;
    // Lääketietokannan versio
    private String laaketietokannanVersio;
    // Määräyspäivä
    @NotNull
    private Date maarayspaiva;
    // PKV-lääkemääräys
    private String PKVlaakemaarays;
    // Potilaan tunnistaminen
    private String potilaanTunnistaminen;
    // Potilaan tunnistaminen teksti
    private String potilaanTunnistaminenTeksti;
    // Työnantaja
    private String tyonantaja;
    // Uusimiskiellon perustelu
    private String uusimiskiellonPerustelu;
    // Uusimiskiellon syy
    private String uusimiskiellonSyy;
    // Vakuutuslaitos
    private String vakuutuslaitos;
    // Viesti apteekille
    private String viestiApteekille;

    // Reseptintyyppi: - reseptisanoman tyyppi/määrätyn määrän esittämistapa (1:pakkaus 2:kokonaismäärä 3:lääkehoidon
    // kestoaika)
    private String reseptintyyppi;
    private Integer pakkauksienLukumaara;
    // Lääkkeen kokonaismäärä
    private Integer laakkeenKokonaismaaraValue;
    private String laakkeenKokonaismaaraUnit;
    private Date ajalleMaaratynReseptinAlkuaika;
    private Integer ajalleMaaratynReseptinAikamaaraValue;
    private String ajalleMaaratynReseptinAikamaaraUnit;
    // Iterointi tekstinä
    private String iterointiTeksti;
    private Integer iterointienMaara;
    private Integer iterointienValiValue;
    private String iterointienValiUnit;
    private ValmisteTO valmiste;
    private ApteekissaValmistettavaLaakeTO apteekissaValmistettavaLaake;
    private String laaketietokannanUlkopuolinenValmiste;

    // Lääkkeen määrääjä
    private AmmattihenkiloTO ammattihenkilo;
    private OrganisaatioTO laatimispaikka;
    private HenkilotiedotTO potilas;
    // Apteekissa valmistettavan lääkkeen osoitin
    private Boolean apteekissaValmistettavaLaakeOsoitin;
    private Boolean annosteluPelkastaanTekstimuodossa;
    private String annostusohje;
    private Boolean SICmerkinta;
    private Boolean laakevaihtokielto;
    // Käyttötarkoitus tekstinä
    private String kayttotarkoitusTeksti;
    private Boolean annosjakelu;
    private final ArrayList<String> hoitolajit;
    private Boolean tartuntatauti = new Boolean(false);
    private Boolean pysyvaislaakitys;
    private Boolean kyseessaLaakkeenkaytonAloitus;
    private Boolean huume;
    private String reseptinLaji;
    private Boolean uudistamiskielto;
    private String apteekissaTallennettuLaakemaarays;
    private Double laakarinPalkkio;
    private String valuutta = "EUR";
    private Boolean laakarinpalkkioErikoislaakarina;

    private String palvelutapahtumanOid;

    // Reseptin laadinnassa käytetyn asiakirjamääritysversion oid
    private String asiakirjaVersio;

    // Kertoo järjestelmässä käytetyn asiakirjamääritysversion yhteensopivuuden suhteessa
    // asiakirjan laadinnassa käytettyyn määritysversioon
    private AsiakirjaVersioYhteensopivuus asiakirjaYhteensopivuus = AsiakirjaVersioYhteensopivuus.JARJESTELMA_VERSIO;

    private List<String> nayttomuoto = new ArrayList<String>();

    // Reseptin kirjaaja
    private AmmattihenkiloTO kirjaaja;
    private String apteekissaTallennettuLaakemaaraysPerustelu;
    private String apteekissaTallennettuLaakemaaraysMuuSyy;

    public LaakemaaraysTO(boolean initialize) {
        super();
        if ( initialize ) {
            initialize();
        }
        hoitolajit = new ArrayList<String>();
    }

    public LaakemaaraysTO() {
        this(true);
    }

    private void initialize() {
        alle12VuotiaanPainoValue = BigDecimal.valueOf(0.0);
        pakkauksienLukumaara = 0;
        laakkeenKokonaismaaraValue = 0;
        ajalleMaaratynReseptinAikamaaraValue = 0;
        iterointienMaara = 0;
        iterointienValiValue = 0;
        apteekissaValmistettavaLaakeOsoitin = false;
        annosteluPelkastaanTekstimuodossa = true;
        SICmerkinta = false;
        laakevaihtokielto = false;
        annosjakelu = false;
        pysyvaislaakitys = false;
        kyseessaLaakkeenkaytonAloitus = false;
        huume = false;
        uudistamiskielto = false;
        laakarinpalkkioErikoislaakarina = false;
        tartuntatauti = false;
    }

    public Date getMaarayspaiva() {
        return maarayspaiva;
    }

    public void setMaarayspaiva(Date maarayspaiva) {
        this.maarayspaiva = maarayspaiva;
    }

    public Date getLaakemaarayksenVoimassaolonLoppuaika() {
        return laakemaarayksenVoimassaolonLoppuaika;
    }

    public void setLaakemaarayksenVoimassaolonLoppuaika(Date laakemaarayksenVoimassaolonLoppuaika) {
        this.laakemaarayksenVoimassaolonLoppuaika = laakemaarayksenVoimassaolonLoppuaika;
    }

    public String getReseptintyyppi() {
        return reseptintyyppi;
    }

    public void setReseptintyyppi(String reseptintyyppi) {
        this.reseptintyyppi = reseptintyyppi;
    }

    public Integer getPakkauksienLukumaara() {
        return pakkauksienLukumaara;
    }

    public void setPakkauksienLukumaara(Integer pakkauksienLukumaara) {
        this.pakkauksienLukumaara = pakkauksienLukumaara;
    }

    public Integer getLaakkeenKokonaismaaraValue() {
        return laakkeenKokonaismaaraValue;
    }

    public void setLaakkeenKokonaismaaraValue(Integer laakkeenKokonaismaaraValue) {
        this.laakkeenKokonaismaaraValue = laakkeenKokonaismaaraValue;
    }

    public String getLaakkeenKokonaismaaraUnit() {
        return laakkeenKokonaismaaraUnit;
    }

    public void setLaakkeenKokonaismaaraUnit(String laakkeenKokonaismaaraUnit) {
        this.laakkeenKokonaismaaraUnit = laakkeenKokonaismaaraUnit;
    }

    public Date getAjalleMaaratynReseptinAlkuaika() {
        return ajalleMaaratynReseptinAlkuaika;
    }

    public void setAjalleMaaratynReseptinAlkuaika(Date ajalleMaaratynReseptinAlkuaika) {
        this.ajalleMaaratynReseptinAlkuaika = ajalleMaaratynReseptinAlkuaika;
    }

    public Integer getAjalleMaaratynReseptinAikamaaraValue() {
        return ajalleMaaratynReseptinAikamaaraValue;
    }

    public void setAjalleMaaratynReseptinAikamaaraValue(Integer ajalleMaaratynReseptinAikamaaraValue) {
        this.ajalleMaaratynReseptinAikamaaraValue = ajalleMaaratynReseptinAikamaaraValue;
    }

    public String getAjalleMaaratynReseptinAikamaaraUnit() {
        return ajalleMaaratynReseptinAikamaaraUnit;
    }

    public void setAjalleMaaratynReseptinAikamaaraUnit(String ajalleMaaratynReseptinAikamaaraUnit) {
        this.ajalleMaaratynReseptinAikamaaraUnit = ajalleMaaratynReseptinAikamaaraUnit;
    }

    public String getIterointiTeksti() {
        return iterointiTeksti;
    }

    public void setIterointiTeksti(String iterointiTeksti) {
        this.iterointiTeksti = iterointiTeksti;
    }

    public Integer getIterointienMaara() {
        return iterointienMaara;
    }

    public void setIterointienMaara(Integer iterointienMaara) {
        this.iterointienMaara = iterointienMaara;
    }

    public Integer getIterointienValiValue() {
        return iterointienValiValue;
    }

    public void setIterointienValiValue(Integer iterointienValiValue) {
        this.iterointienValiValue = iterointienValiValue;
    }

    public String getIterointienValiUnit() {
        return iterointienValiUnit;
    }

    public void setIterointienValiUnit(String iterointienValiUnit) {
        this.iterointienValiUnit = iterointienValiUnit;
    }

    public ValmisteTO getValmiste() {
        return valmiste;
    }

    public void setValmiste(ValmisteTO valmiste) {
        this.valmiste = valmiste;
    }

    public ApteekissaValmistettavaLaakeTO getApteekissaValmistettavaLaake() {
        return apteekissaValmistettavaLaake;
    }

    public void setApteekissaValmistettavaLaake(ApteekissaValmistettavaLaakeTO apteekissaValmistettavaLaake) {
        this.apteekissaValmistettavaLaake = apteekissaValmistettavaLaake;
    }

    public String getLaaketietokannanUlkopuolinenValmiste() {
        return laaketietokannanUlkopuolinenValmiste;
    }

    public void setLaaketietokannanUlkopuolinenValmiste(String laaketietokannanUlkopuolinenValmiste) {
        this.laaketietokannanUlkopuolinenValmiste = laaketietokannanUlkopuolinenValmiste;
    }

    public String getTyonantaja() {
        return tyonantaja;
    }

    public void setTyonantaja(String tyonantaja) {
        this.tyonantaja = tyonantaja;
    }

    public String getVakuutuslaitos() {
        return vakuutuslaitos;
    }

    public void setVakuutuslaitos(String vakuutuslaitos) {
        this.vakuutuslaitos = vakuutuslaitos;
    }

    public AmmattihenkiloTO getAmmattihenkilo() {
        return ammattihenkilo;
    }

    public void setAmmattihenkilo(AmmattihenkiloTO ammattihenkilo) {
        this.ammattihenkilo = ammattihenkilo;
    }

    public HenkilotiedotTO getPotilas() {
        return potilas;
    }

    public void setPotilas(HenkilotiedotTO potilas) {
        this.potilas = potilas;
    }

    public Boolean isApteekissaValmistettavaLaake() {
        return apteekissaValmistettavaLaakeOsoitin;
    }

    public void setApteekissaValmistettavaLaake(Boolean apteekissaValmistettavaLaake) {
        apteekissaValmistettavaLaakeOsoitin = apteekissaValmistettavaLaake;
    }

    public Boolean isAnnosteluPelkastaanTekstimuodossa() {
        return annosteluPelkastaanTekstimuodossa;
    }

    public void setAnnosteluPelkastaanTekstimuodossa(Boolean annosteluPelkastaanTekstimuodossa) {

        // Toistaiseksi tuetaan vain tekstimuotoista
        if ( !annosteluPelkastaanTekstimuodossa ) {
            throw new IllegalArgumentException("Annostelu on tuettu vain tekstimuotoisena.");
        }
        this.annosteluPelkastaanTekstimuodossa = annosteluPelkastaanTekstimuodossa;
    }

    public String getAnnostusohje() {
        return annostusohje;
    }

    public void setAnnostusohje(String annostusohje) {
        this.annostusohje = annostusohje;
    }

    public Boolean isSICmerkinta() {
        return SICmerkinta;
    }

    public void setSICmerkinta(Boolean sICmerkinta) {
        SICmerkinta = sICmerkinta;
    }

    public Boolean isLaakevaihtokielto() {
        return laakevaihtokielto;
    }

    public void setLaakevaihtokielto(Boolean laakevaihtokielto) {
        this.laakevaihtokielto = laakevaihtokielto;
    }

    public String getKayttotarkoitusTeksti() {
        return kayttotarkoitusTeksti;
    }

    public void setKayttotarkoitusTeksti(String kayttotarkoitusTeksti) {
        this.kayttotarkoitusTeksti = kayttotarkoitusTeksti;
    }

    public BigDecimal getAlle12VuotiaanPainoValue() {
        return alle12VuotiaanPainoValue;
    }

    public void setAlle12VuotiaanPainoValue(BigDecimal alle12VuotiaanPainoValue) {
        this.alle12VuotiaanPainoValue = alle12VuotiaanPainoValue;
    }

    public String getAlle12VuotiaanPainoUnit() {
        return alle12VuotiaanPainoUnit;
    }

    public void setAlle12VuotiaanPainoUnit(String alle12VuotiaanPainoUnit) {
        this.alle12VuotiaanPainoUnit = alle12VuotiaanPainoUnit;
    }

    public Boolean isAnnosjakelu() {
        return annosjakelu;
    }

    public void setAnnosjakelu(Boolean annosjakelu) {
        this.annosjakelu = annosjakelu;
    }

    public String getAnnosjakeluTeksti() {
        return annosjakeluTeksti;
    }

    public void setAnnosjakeluTeksti(String annosjakeluTeksti) {
        this.annosjakeluTeksti = annosjakeluTeksti;
    }

    public List<String> getHoitolajit() {
        return hoitolajit;
    }

    public Boolean getTartuntatauti() {
        return tartuntatauti;
    }

    public void setTartuntatauti(Boolean tartuntatauti) {
        this.tartuntatauti = tartuntatauti;
    }

    public String getViestiApteekille() {
        return viestiApteekille;
    }

    public void setViestiApteekille(String viestiApteekille) {
        this.viestiApteekille = viestiApteekille;
    }

    public String getErillisselvitys() {
        return erillisselvitys;
    }

    public void setErillisselvitys(String erillisselvitys) {
        this.erillisselvitys = erillisselvitys;
    }

    public String getErillisselvitysteksti() {
        return erillisselvitysteksti;
    }

    public void setErillisselvitysteksti(String erillisselvitysteksti) {
        this.erillisselvitysteksti = erillisselvitysteksti;
    }

    public String getPotilaanTunnistaminen() {
        return potilaanTunnistaminen;
    }

    public void setPotilaanTunnistaminen(String potilaanTunnistaminen) {
        this.potilaanTunnistaminen = potilaanTunnistaminen;
    }

    public String getPotilaanTunnistaminenTeksti() {
        return potilaanTunnistaminenTeksti;
    }

    public void setPotilaanTunnistaminenTeksti(String potilaanTunnistaminenTeksti) {
        this.potilaanTunnistaminenTeksti = potilaanTunnistaminenTeksti;
    }

    public String getPKVlaakemaarays() {
        return PKVlaakemaarays;
    }

    public void setPKVlaakemaarays(String pKVlaakemaarays) {
        PKVlaakemaarays = pKVlaakemaarays;
    }

    public Boolean isPysyvaislaakitys() {
        return pysyvaislaakitys;
    }

    public void setPysyvaislaakitys(Boolean pysyvaislaakitys) {
        this.pysyvaislaakitys = pysyvaislaakitys;
    }

    public Boolean isKyseessaLaakkeenkaytonAloitus() {
        return kyseessaLaakkeenkaytonAloitus;
    }

    public void setKyseessaLaakkeenkaytonAloitus(Boolean kyseessaLaakkeenkaytonAloitus) {
        this.kyseessaLaakkeenkaytonAloitus = kyseessaLaakkeenkaytonAloitus;
    }

    public Boolean isHuume() {
        return huume;
    }

    public void setHuume(Boolean huume) {
        this.huume = huume;
    }

    public String getReseptinLaji() {
        return reseptinLaji;
    }

    public void setReseptinLaji(String reseptinLaji) {
        this.reseptinLaji = reseptinLaji;
    }

    public Boolean isUudistamiskielto() {
        return uudistamiskielto;
    }

    public void setUudistamiskielto(Boolean uudistamiskielto) {
        this.uudistamiskielto = uudistamiskielto;
    }

    public String getUusimiskiellonSyy() {
        return uusimiskiellonSyy;
    }

    public void setUusimiskiellonSyy(String uusimiskiellonSyy) {
        this.uusimiskiellonSyy = uusimiskiellonSyy;
    }

    public String getUusimiskiellonPerustelu() {
        return uusimiskiellonPerustelu;
    }

    public void setUusimiskiellonPerustelu(String uusimiskiellonPerustelu) {
        this.uusimiskiellonPerustelu = uusimiskiellonPerustelu;
    }

    public String getLaaketietokannanVersio() {
        return laaketietokannanVersio;
    }

    public void setLaaketietokannanVersio(String laaketietokannanVersio) {
        this.laaketietokannanVersio = laaketietokannanVersio;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("[oid: ").append(getOid());
        sb.append(", setId: ").append(getSetId());
        sb.append(", versio ").append(getVersio());
        sb.append(", cda tyyppi: ").append(getCdaTyyppi());
        sb.append(", reseptin laji: ").append(getReseptinLaji());
        sb.append(", reseptin tyyppi: ").append(getReseptintyyppi());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        if ( getAikaleima() != null ) {
            sb.append(", aikaleima: ").append(sdf.format(getAikaleima()));
        }
        else {
            sb.append(", aikaleimaa ei ole asetettu");
        }
        if ( getAjalleMaaratynReseptinAlkuaika() != null ) {
            sb.append(", ajallemäärätyn alkuaika: ").append(sdf.format(getAjalleMaaratynReseptinAlkuaika()));
            sb.append(", määrätty ajalle (päivää): ").append(getAjalleMaaratynReseptinAikamaaraValue());
        }
        else {
            sb.append(", ajallemäärätyn alkuaikaa ei ole asetettu");
        }
        if ( getValmiste() != null ) {
            sb.append(", ").append(getValmiste().toString());
        }
        else {
            sb.append(", valmistetta ei ole määritelty");
        }

        // Ammattihenkilön tiedot
        if ( getAmmattihenkilo() != null ) {
            sb.append(", ").append(getAmmattihenkilo().toString());
        }
        else {
            sb.append(", ammattihenkilöä ei ole määritelty");
        }
        sb.append("]");

        return sb.toString();
    }

    public String getPalvelutapahtumanOid() {
        return palvelutapahtumanOid;
    }

    public void setPalvelutapahtumanOid(String palvelutapahtumanOid) {
        this.palvelutapahtumanOid = palvelutapahtumanOid;
    }

    public OrganisaatioTO getLaatimispaikka() {
        return laatimispaikka;
    }

    public void setLaatimispaikka(OrganisaatioTO laatimispaikka) {
        this.laatimispaikka = laatimispaikka;
    }

    public void setAsiakirjaYhteensopivuus(AsiakirjaVersioYhteensopivuus asiakirjaYhteensopivuus) {
        this.asiakirjaYhteensopivuus = asiakirjaYhteensopivuus;
    }

    public boolean isAsiakirjaTaaksepainYhteensopiva() {
        return asiakirjaYhteensopivuus != null
                ? asiakirjaYhteensopivuus.equals(AsiakirjaVersioYhteensopivuus.TAAKSEPAIN) : false;
    }

    public boolean isAsiakirjaEteenpainYhteensopiva() {
        return asiakirjaYhteensopivuus != null ? asiakirjaYhteensopivuus.equals(AsiakirjaVersioYhteensopivuus.ETEENPAIN)
                : false;
    }

    public boolean isAsiakirjaJarjestelmaVersio() {
        return asiakirjaYhteensopivuus != null
                ? asiakirjaYhteensopivuus.equals(AsiakirjaVersioYhteensopivuus.JARJESTELMA_VERSIO) : false;
    }

    public boolean isAsiakirjaVersioTuettu() {
        return asiakirjaYhteensopivuus != null
                ? !asiakirjaYhteensopivuus.equals(AsiakirjaVersioYhteensopivuus.EI_TUETTU) : false;
    }

    public boolean isYllapitoSallittu() {
        return isAsiakirjaVersioTuettu();
    }

    public String getAsiakirjaVersio() {
        return asiakirjaVersio;
    }

    public void setAsiakirjaVersio(String asiakirjaVersio) {
        this.asiakirjaVersio = asiakirjaVersio;
    }

    public List<String> getNayttomuoto() {
        return nayttomuoto;
    }

    public String getErillisselvitysotsikko() {
        return erillisselvitysotsikko;
    }

    public void setErillisselvitysotsikko(String erillisselvitysotsikko) {
        this.erillisselvitysotsikko = erillisselvitysotsikko;
    }

    public String getApteekissaTallennettuLaakemaarays() {
        return apteekissaTallennettuLaakemaarays;
    }

    public void setApteekissaTallennettuLaakemaarays(String apteekissaTallennettuLaakemaarays) {
        this.apteekissaTallennettuLaakemaarays = apteekissaTallennettuLaakemaarays;
    }

    public Boolean isLaakarinpalkkioErikoislaakarina() {
        return laakarinpalkkioErikoislaakarina;
    }

    public void setLaakarinpalkkioErikoislaakarina(Boolean laakarinpalkkioErikoislaakarina) {
        this.laakarinpalkkioErikoislaakarina = laakarinpalkkioErikoislaakarina;
    }

    public Double getLaakarinPalkkio() {
        return laakarinPalkkio;
    }

    public void setLaakarinPalkkio(Double laakarinPalkkio) {
        this.laakarinPalkkio = laakarinPalkkio;
    }

    public String getValuutta() {
        return valuutta;
    }

    public void setValuutta(String valuutta) {
        this.valuutta = valuutta;
    }

    public AmmattihenkiloTO getKirjaaja() {
        return kirjaaja;
    }

    public void setKirjaaja(AmmattihenkiloTO kirjaaja) {
        this.kirjaaja = kirjaaja;
    }

    public String getApteekissaTallennettuLaakemaaraysPerustelu() {
        return apteekissaTallennettuLaakemaaraysPerustelu;
    }

    public void setApteekissaTallennettuLaakemaaraysPerustelu(String apteekissaTallennettuLaakemaaraysPerustelu) {
        this.apteekissaTallennettuLaakemaaraysPerustelu = apteekissaTallennettuLaakemaaraysPerustelu;
    }

    public String getApteekissaTallennettuLaakemaaraysMuuSyy() {
        return apteekissaTallennettuLaakemaaraysMuuSyy;
    }

    public void setApteekissaTallennettuLaakemaaraysMuuSyy(String apteekissaTallennettuLaakemaaraysMuuSyy) {
        this.apteekissaTallennettuLaakemaaraysMuuSyy = apteekissaTallennettuLaakemaaraysMuuSyy;
    }

}
