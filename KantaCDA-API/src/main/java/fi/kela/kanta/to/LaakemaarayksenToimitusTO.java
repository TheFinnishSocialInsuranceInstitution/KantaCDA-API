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

import java.util.Date;

/**
 * Lääkemääräyksen toimitus -tietomallin luokkaa vastaava luokka.
 */
public class LaakemaarayksenToimitusTO extends LaakemaaraysTO {

    private static final long serialVersionUID = 66345612225555L;

    private String laakemaarayksenYksilointitunnus;

    private boolean osapakkaus;

    private String koostumuksenSelitys;
    // numeerinen muoto
    private int toimitettuKokonaismaaraValue;
    // numeerisen muodon yksikkö
    private String toimitettuKokonaismaaraUnit;
    // laskukaavamuoto
    private String toimitettuKokonaismaaraOriginal;
    // tekstimuoto
    private String toimitettuKokonaismaaraText;
    // numeerinen muoto
    private int jaljellaOlevaMaaraValue;
    // numeerisen muodon yksikkö
    private String jaljellaOlevaMaaraUnit;
    // laskukaavamuoto
    private String jaljellaOlevaMaaraOriginal;
    // tekstimuoto
    private String jaljellaOlevaMaaraText;

    private boolean laakeVaihdettu;

    private String apteekinHuomautus;

    private String lisaselvitysKelalle;
    private double toimituksenHintaValue;
    // esim. EUR, tarvitaanko?
    private String toimituksenHintaUnit;
    // HL7 CE (koodi)
    private String laakevaihtokiellonSyy;

    private String laakevaihtokiellonLisaselvitys;

    private int omavastuuosuuksienLukumaara;

    private boolean kokonaanToimitettu;

    public Date getToimituspaiva() {
        return getMaarayspaiva();
    }

    public void setToimituspaiva(Date toimituspaiva) {
        this.setMaarayspaiva(toimituspaiva);
    }

    public String getLaakemaarayksenYksilointitunnus() {
        return laakemaarayksenYksilointitunnus;
    }

    public void setLaakemaarayksenYksilointitunnus(String laakemaarayksenYksilointitunnus) {
        this.laakemaarayksenYksilointitunnus = laakemaarayksenYksilointitunnus;
    }

    public OrganisaatioTO getApteekki() {
        return getLaatimispaikka();
    }

    public void setApteekki(OrganisaatioTO apteekki) {
        this.setLaatimispaikka(apteekki);
    }

    public AmmattihenkiloTO getLaaketoimituksenTehnytAmmattihenkilo() {
        return getAmmattihenkilo();
    }

    public void setLaaketoimituksenTehnytAmmattihenkilo(AmmattihenkiloTO laaketoimituksenTehnytAmmattihenkilo) {
        this.setAmmattihenkilo(laaketoimituksenTehnytAmmattihenkilo);
    }

    public boolean isOsapakkaus() {
        return osapakkaus;
    }

    public void setOsapakkaus(boolean osapakkaus) {
        this.osapakkaus = osapakkaus;
    }

    public String getKoostumuksenSelitys() {
        return koostumuksenSelitys;
    }

    public void setKoostumuksenSelitys(String koostumuksenSelitys) {
        this.koostumuksenSelitys = koostumuksenSelitys;
    }

    public int getToimitettuKokonaismaaraValue() {
        return toimitettuKokonaismaaraValue;
    }

    public void setToimitettuKokonaismaaraValue(int toimitettuKokonaismaaraValue) {
        this.toimitettuKokonaismaaraValue = toimitettuKokonaismaaraValue;
    }

    public String getToimitettuKokonaismaaraUnit() {
        return toimitettuKokonaismaaraUnit;
    }

    public void setToimitettuKokonaismaaraUnit(String toimitettuKokonaismaaraUnit) {
        this.toimitettuKokonaismaaraUnit = toimitettuKokonaismaaraUnit;
    }

    public String getToimitettuKokonaismaaraOriginal() {
        return toimitettuKokonaismaaraOriginal;
    }

    public void setToimitettuKokonaismaaraOriginal(String toimitettuKokonaismaaraOriginal) {
        this.toimitettuKokonaismaaraOriginal = toimitettuKokonaismaaraOriginal;
    }

    public String getToimitettuKokonaismaaraText() {
        return toimitettuKokonaismaaraText;
    }

    public void setToimitettuKokonaismaaraText(String toimitettuKokonaismaaraText) {
        this.toimitettuKokonaismaaraText = toimitettuKokonaismaaraText;
    }

    public int getJaljellaOlevaMaaraValue() {
        return jaljellaOlevaMaaraValue;
    }

    public void setJaljellaOlevaMaaraValue(int jaljellaOlevaMaaraValue) {
        this.jaljellaOlevaMaaraValue = jaljellaOlevaMaaraValue;
    }

    public String getJaljellaOlevaMaaraUnit() {
        return jaljellaOlevaMaaraUnit;
    }

    public void setJaljellaOlevaMaaraUnit(String jaljellaOlevaMaaraUnit) {
        this.jaljellaOlevaMaaraUnit = jaljellaOlevaMaaraUnit;
    }

    public String getJaljellaOlevaMaaraOriginal() {
        return jaljellaOlevaMaaraOriginal;
    }

    public void setJaljellaOlevaMaaraOriginal(String jaljellaOlevaMaaraOriginal) {
        this.jaljellaOlevaMaaraOriginal = jaljellaOlevaMaaraOriginal;
    }

    public String getJaljellaOlevaMaaraText() {
        return jaljellaOlevaMaaraText;
    }

    public void setJaljellaOlevaMaaraText(String jaljellaOlevaMaaraText) {
        this.jaljellaOlevaMaaraText = jaljellaOlevaMaaraText;
    }

    public boolean isLaakeVaihdettu() {
        return laakeVaihdettu;
    }

    public void setLaakeVaihdettu(boolean laakeVaihdettu) {
        this.laakeVaihdettu = laakeVaihdettu;
    }

    public String getApteekinHuomautus() {
        return apteekinHuomautus;
    }

    public void setApteekinHuomautus(String apteekinHuomautus) {
        this.apteekinHuomautus = apteekinHuomautus;
    }

    public String getLisaselvitysKelalle() {
        return lisaselvitysKelalle;
    }

    public void setLisaselvitysKelalle(String lisaselvitysKelalle) {
        this.lisaselvitysKelalle = lisaselvitysKelalle;
    }

    public double getToimituksenHintaValue() {
        return toimituksenHintaValue;
    }

    public void setToimituksenHintaValue(double toimituksenHintaValue) {
        this.toimituksenHintaValue = toimituksenHintaValue;
    }

    public String getToimituksenHintaUnit() {
        return toimituksenHintaUnit;
    }

    public void setToimituksenHintaUnit(String toimituksenHintaUnit) {
        this.toimituksenHintaUnit = toimituksenHintaUnit;
    }

    public String getLaakevaihtokiellonSyy() {
        return laakevaihtokiellonSyy;
    }

    public void setLaakevaihtokiellonSyy(String laakevaihtokiellonSyy) {
        this.laakevaihtokiellonSyy = laakevaihtokiellonSyy;
    }

    public String getLaakevaihtokiellonLisaselvitys() {
        return laakevaihtokiellonLisaselvitys;
    }

    public void setLaakevaihtokiellonLisaselvitys(String laakevaihtokiellonLisaselvitys) {
        this.laakevaihtokiellonLisaselvitys = laakevaihtokiellonLisaselvitys;
    }

    public int getOmavastuuosuuksienLukumaara() {
        return omavastuuosuuksienLukumaara;
    }

    public void setOmavastuuosuuksienLukumaara(int omavastuuosuuksienLukumaara) {
        this.omavastuuosuuksienLukumaara = omavastuuosuuksienLukumaara;
    }

    public boolean isKokonaanToimitettu() {
        return kokonaanToimitettu;
    }

    public void setKokonaanToimitettu(boolean kokonaanToimitettu) {
        this.kokonaanToimitettu = kokonaanToimitettu;
    }

    public String getToimitustietotarranAnnostusohje() {
        return this.getAnnostusohje();
    }

    public void setToimitustietotarranAnnostusohje(String toimitustietotarranAnnostusohje) {
        setAnnostusohje(toimitustietotarranAnnostusohje);
    }

}
