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

public class ValmisteenYksilointitiedotTO implements Serializable {

    private static final long serialVersionUID = 223352177777222L;
    // Kauppanimi:String
    private String kauppanimi;
    // Tunnuksen tyyppi:int
    private int tunnuksenTyyppi;
    // Yksilöintitunnus :String
    private String yksilointitunnus;
    // Valmisteen laji:CV
    private String valmisteenLaji;
    private String valmisteenLajiNimi;
    // ATC-koodi:CV
    private String ATCkoodi;
    private String ATCnimi;
    // Vahvuus:Sting
    private String vahvuus;
    // Pakkauskokoteksti:String
    private String pakkauskokoteksti;
    // Pakkauskokokerroin:int
    private int pakkauskokokerroin;
    // Pakkauskoko: decimal
    private double pakkauskoko;
    // Pakkausyksikkö: String
    private String pakkausyksikko;
    // Säilytysastia:CV
    private String sailytysastia;
    // Valmisteen lisätieto:String
    private String valmisteenLisatieto;
    // Myyntiluvan haltija:String
    private String myyntiluvanHaltija;
    // Markkinoija:String
    private String markkinoija;
    // Pakkauslaite
    private String pakkauslaite;

    public String getKauppanimi() {
        return kauppanimi;
    }

    public void setKauppanimi(String kauppanimi) {
        this.kauppanimi = kauppanimi;
    }

    public int getTunnuksenTyyppi() {
        return tunnuksenTyyppi;
    }

    public void setTunnuksenTyyppi(int tunnuksenTyyppi) {
        this.tunnuksenTyyppi = tunnuksenTyyppi;
    }

    public String getYksilointitunnus() {
        return yksilointitunnus;
    }

    public void setYksilointitunnus(String yksilointitunnus) {
        this.yksilointitunnus = yksilointitunnus;
    }

    public String getValmisteenLaji() {
        return valmisteenLaji;
    }

    public void setValmisteenLaji(String valmisteenLaji) {
        this.valmisteenLaji = valmisteenLaji;
    }

    public String getATCkoodi() {
        return ATCkoodi;
    }

    public void setATCkoodi(String aTCkoodi) {
        ATCkoodi = aTCkoodi;
    }

    public String getVahvuus() {
        return vahvuus;
    }

    public void setVahvuus(String vahvuus) {
        this.vahvuus = vahvuus;
    }

    public String getPakkauskokoteksti() {
        return pakkauskokoteksti;
    }

    public void setPakkauskokoteksti(String pakkauskokoteksti) {
        this.pakkauskokoteksti = pakkauskokoteksti;
    }

    public int getPakkauskokokerroin() {
        return pakkauskokokerroin;
    }

    public void setPakkauskokokerroin(int pakkauskokokerroin) {
        this.pakkauskokokerroin = pakkauskokokerroin;
    }

    public double getPakkauskoko() {
        return pakkauskoko;
    }

    public void setPakkauskoko(double pakkauskoko) {
        this.pakkauskoko = pakkauskoko;
    }

    public String getPakkausyksikko() {
        return pakkausyksikko;
    }

    public void setPakkausyksikko(String pakkausyksikko) {
        this.pakkausyksikko = pakkausyksikko;
    }

    public String getSailytysastia() {
        return sailytysastia;
    }

    public void setSailytysastia(String sailytysastia) {
        this.sailytysastia = sailytysastia;
    }

    public String getValmisteenLisatieto() {
        return valmisteenLisatieto;
    }

    public void setValmisteenLisatieto(String valmisteenLisatieto) {
        this.valmisteenLisatieto = valmisteenLisatieto;
    }

    public String getMyyntiluvanHaltija() {
        return myyntiluvanHaltija;
    }

    public void setMyyntiluvanHaltija(String myyntiluvanHaltija) {
        this.myyntiluvanHaltija = myyntiluvanHaltija;
    }

    public String getMarkkinoija() {
        return markkinoija;
    }

    public void setMarkkinoija(String markkinoija) {
        this.markkinoija = markkinoija;
    }

    public String getATCnimi() {
        return ATCnimi;
    }

    public void setATCnimi(String ATCnimi) {
        this.ATCnimi = ATCnimi;
    }

    /**
     * @deprecated
     * @return
     */
    @Deprecated
    public String getValmisteenLajiNimi() {
        return valmisteenLajiNimi;
    }

    /**
     * @deprecated
     * @param valmisteenLajiNimi
     */
    @Deprecated
    public void setValmisteenLajiNimi(String valmisteenLajiNimi) {
        this.valmisteenLajiNimi = valmisteenLajiNimi;
    }

    public void setPakkauslaite(String pakkauslaite) {
        this.pakkauslaite = pakkauslaite;
    }

    public String getPakkauslaite() {
        return pakkauslaite;
    }
}
