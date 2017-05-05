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

public class UusimispyyntoTO extends LaakemaaraysTO {

    private static final long serialVersionUID = -3601668120007586114L;

    private HenkilotiedotTO henkilotiedot;
    private String vastaanottajaId;
    private String vastaanottajaNimi;
    private String vastaanottajaKatu;
    private String vastaanottajaPostinumero;
    private String vastaanottajaKaupunki;
    private String vastaanottajaPuhelinnumero;
    private String uusittavaLaakemaaraysOid;
    private String uusittavaLaakemaaraysSetId;
    private String matkapuhelinnumero;
    private String valmisteenNimi;
    private KokoNimiTO maaraajanKokonimi;
    private String maaraajanId;
    private int suostumus;
    private AmmattihenkiloTO uusija;
    private OrganisaatioTO laatimispaikka;

    /**
     * @return the henkilotiedot
     */
    public HenkilotiedotTO getHenkilotiedot() {
        return henkilotiedot;
    }

    /**
     * @param henkilotiedot
     *            the henkilotiedot to set
     */
    public void setHenkilotiedot(HenkilotiedotTO henkilotiedot) {
        this.henkilotiedot = henkilotiedot;
    }

    /**
     * @return the vastaanottajaId
     */
    public String getVastaanottajaId() {
        return vastaanottajaId;
    }

    /**
     * @param vastaanottajaId
     *            the vastaanottajaId to set
     */
    public void setVastaanottajaId(String vastaanottajaId) {
        this.vastaanottajaId = vastaanottajaId;
    }

    /**
     * @return the vastaanottajaNimi
     */
    public String getVastaanottajaNimi() {
        return vastaanottajaNimi;
    }

    /**
     * @param vastaanottajaNimi
     *            the vastaanottajaNimi to set
     */
    public void setVastaanottajaNimi(String vastaanottajaNimi) {
        this.vastaanottajaNimi = vastaanottajaNimi;
    }

    /**
     * @return the vastaanottajaKatu
     */
    public String getVastaanottajaKatu() {
        return vastaanottajaKatu;
    }

    /**
     * @param vastaanottajaKatu
     *            the vastaanottajaKatu to set
     */
    public void setVastaanottajaKatu(String vastaanottajaKatu) {
        this.vastaanottajaKatu = vastaanottajaKatu;
    }

    /**
     * @return the vastaanottajaPostinumero
     */
    public String getVastaanottajaPostinumero() {
        return vastaanottajaPostinumero;
    }

    /**
     * @param vastaanottajaPostinumero
     *            the vastaanottajaPostinumero to set
     */
    public void setVastaanottajaPostinumero(String vastaanottajaPostinumero) {
        this.vastaanottajaPostinumero = vastaanottajaPostinumero;
    }

    /**
     * @return the vastaanottajaKaupunki
     */
    public String getVastaanottajaKaupunki() {
        return vastaanottajaKaupunki;
    }

    /**
     * @param vastaanottajaKaupunki
     *            the vastaanottajaKaupunki to set
     */
    public void setVastaanottajaKaupunki(String vastaanottajaKaupunki) {
        this.vastaanottajaKaupunki = vastaanottajaKaupunki;
    }

    /**
     * @return the vastaanottajaPuhelinnumero
     */
    public String getVastaanottajaPuhelinnumero() {
        return vastaanottajaPuhelinnumero;
    }

    /**
     * @param vastaanottajaPuhelinnumero
     *            the vastaanottajaPuhelinnumero to set
     */
    public void setVastaanottajaPuhelinnumero(String vastaanottajaPuhelinnumero) {
        this.vastaanottajaPuhelinnumero = vastaanottajaPuhelinnumero;
    }

    /**
     * @return the uusittavaLaakemaaraysOid
     */
    public String getUusittavaLaakemaaraysOid() {
        return uusittavaLaakemaaraysOid;
    }

    /**
     * @param uusittavaLaakemaaraysOid
     *            the uusittavaLaakemaaraysOid to set
     */
    public void setUusittavaLaakemaaraysOid(String uusittavaLaakemaaraysOid) {
        this.uusittavaLaakemaaraysOid = uusittavaLaakemaaraysOid;
    }

    /**
     * @return the uusittavaLaakemaaraysSetId
     */
    public String getUusittavaLaakemaaraysSetId() {
        return uusittavaLaakemaaraysSetId;
    }

    /**
     * @param uusittavaLaakemaaraysSetId
     *            the uusittavaLaakemaaraysSetId to set
     */
    public void setUusittavaLaakemaaraysSetId(String uusittavaLaakemaaraysSetId) {
        this.uusittavaLaakemaaraysSetId = uusittavaLaakemaaraysSetId;
    }

    /**
     * @return the matkapuhelinnumero
     */
    public String getMatkapuhelinnumero() {
        return matkapuhelinnumero;
    }

    /**
     * @param matkapuhelinnumero
     *            the matkapuhelinnumero to set
     */
    public void setMatkapuhelinnumero(String matkapuhelinnumero) {
        this.matkapuhelinnumero = matkapuhelinnumero;
    }

    /**
     * @return the valmisteenNimi
     */
    public String getValmisteenNimi() {
        return valmisteenNimi;
    }

    /**
     * @param valmisteenNimi
     *            the valmisteenNimi to set
     */
    public void setValmisteenNimi(String valmisteenNimi) {
        this.valmisteenNimi = valmisteenNimi;
    }

    /**
     * @return the maaraajanKokonimi
     */
    public KokoNimiTO getMaaraajanKokonimi() {
        return maaraajanKokonimi;
    }

    /**
     * @param maaraajanKokonimi
     *            the maaraajanKokonimi to set
     */
    public void setMaaraajanKokonimi(KokoNimiTO maaraajanKokonimi) {
        this.maaraajanKokonimi = maaraajanKokonimi;
    }

    /**
     * @return the maaraajanId
     */
    public String getMaaraajanId() {
        return maaraajanId;
    }

    /**
     * @param maaraajanId
     *            the maaraajanId to set
     */
    public void setMaaraajanId(String maaraajanId) {
        this.maaraajanId = maaraajanId;
    }

    /**
     * @return the suostumus
     */
    public int getSuostumus() {
        return suostumus;
    }

    /**
     * @param suostumus
     *            the suostumus to set
     */
    public void setSuostumus(int suostumus) {
        this.suostumus = suostumus;
    }

    public AmmattihenkiloTO getUusija() {
        return uusija;
    }

    public void setUusija(AmmattihenkiloTO uusija) {
        this.uusija = uusija;
    }

    @Override
    public OrganisaatioTO getLaatimispaikka() {
        return laatimispaikka;
    }

    @Override
    public void setLaatimispaikka(OrganisaatioTO laatimispaikka) {
        this.laatimispaikka = laatimispaikka;
    }

}
