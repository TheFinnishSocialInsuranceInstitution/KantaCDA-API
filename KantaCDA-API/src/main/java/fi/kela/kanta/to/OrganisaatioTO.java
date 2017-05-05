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

import fi.kela.kanta.interfaces.Organisaatio;
import fi.kela.kanta.interfaces.Osoite;

public class OrganisaatioTO implements Organisaatio {

    private static final long serialVersionUID = 3772259136758301810L;

    // Nimi:String
    private String nimi;
    // Osoite:Osoite
    private Osoite osoite;
    // Yksilöintitunnus:II
    private String yksilointitunnus;
    private String ytunnus;
    // Puhelinnumero:String
    private String puhelinnumero;
    // Sähköpostiosoite
    private String sahkoposti;
    private String puhelinumeroKayttotarkoitus;
    private OrganisaatioTO toimintaYksikko;
    private OrganisaatioTO palveluYksikko;
    private String tyyppi;

    @Override
    public String getNimi() {
        return nimi;
    }

    @Override
    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    @Override
    public Osoite getOsoite() {
        return osoite;
    }

    @Override
    public void setOsoite(Osoite osoite) {
        this.osoite = osoite;
    }

    @Override
    public String getYksilointitunnus() {
        return yksilointitunnus;
    }

    @Override
    public void setYksilointitunnus(String yksilointitunnus) {
        this.yksilointitunnus = yksilointitunnus;
    }

    @Override
    public String getPuhelinnumero() {
        return puhelinnumero;
    }

    @Override
    public void setPuhelinnumero(String puhelinnumero) {
        this.puhelinnumero = puhelinnumero;
    }

    @Override
    public String getSahkoposti() {
        return sahkoposti;
    }

    @Override
    public void setSahkoposti(String sahkoposti) {
        this.sahkoposti = sahkoposti;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Organisaatio [oid: ").append(getYksilointitunnus());
        sb.append(", Y-tunnus: ").append(getYtunnus());
        sb.append(", nimi: ").append(getNimi());
        sb.append(", puhnro: ").append(getPuhelinnumero());
        sb.append(", email: ").append(getSahkoposti());
        if ( getOsoite() != null ) {
            sb.append(", ").append(getOsoite().toString());
        }
        else {
            sb.append(", orgnanisaatiolla ei ole osoite tietoja");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * @return the puhelinumeroUse
     */
    public String getPuhelinumeroKayttotarkoitus() {
        return puhelinumeroKayttotarkoitus;
    }

    /**
     * @param puhelinumeroUse
     *            the puhelinumeroUse to set
     */
    public void setPuhelinumeroKayttotarkoitus(String puhelinumeroKayttotarkoitus) {
        this.puhelinumeroKayttotarkoitus = puhelinumeroKayttotarkoitus;
    }

    public OrganisaatioTO getToimintaYksikko() {
        return toimintaYksikko;
    }

    public void setToimintaYksikko(OrganisaatioTO toimintaYksikko) {
        this.toimintaYksikko = toimintaYksikko;
    }

    public OrganisaatioTO getPalveluYksikko() {
        return palveluYksikko;
    }

    public void setPalveluYksikko(OrganisaatioTO palveluYksikko) {
        this.palveluYksikko = palveluYksikko;
    }

    /**
     * @return the tyyppi
     */
    public String getTyyppi() {
        return tyyppi;
    }

    /**
     * @param tyyppi
     *            the tyyppi to set
     */
    public void setTyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }

    @Override
    public String getYtunnus() {
        return ytunnus;
    }

    @Override
    public void setYtunnus(String yTunnus) {
        this.ytunnus = yTunnus;
    }
}
