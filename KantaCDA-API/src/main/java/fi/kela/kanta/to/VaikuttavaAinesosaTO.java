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

public class VaikuttavaAinesosaTO implements Serializable {

    private static final long serialVersionUID = 14232211111L;
    // ATC-koodi:CV
    private String ATCkoodi;
    private String ATCnimi;
    // Koodaamaton nimi:String
    private String koodamatonNimi;
    // Kauppanimi, vahvuus ja lääkemuoto:String
    private String kauppanimiVahvuusJaLaakemuoto;
    // Suolamuoto:String
    private String suolamuoto;
    // Ainesosan määrä:PQ
    private double ainesosanMaaraValue;
    private String ainesosanMaaraUnit;
    // Ainesosan määrä tekstinä:String
    private String ainesosanMaaraTekstina;

    public String getATCkoodi() {
        return ATCkoodi;
    }

    public void setATCkoodi(String aTCkoodi) {
        ATCkoodi = aTCkoodi;
    }

    public String getKoodamatonNimi() {
        return koodamatonNimi;
    }

    public void setKoodamatonNimi(String koodamatonNimi) {
        this.koodamatonNimi = koodamatonNimi;
    }

    public String getKauppanimiVahvuusJaLaakemuoto() {
        return kauppanimiVahvuusJaLaakemuoto;
    }

    public void setKauppanimiVahvuusJaLaakemuoto(String kauppanimiVahvuusJaLaakemuoto) {
        this.kauppanimiVahvuusJaLaakemuoto = kauppanimiVahvuusJaLaakemuoto;
    }

    public String getSuolamuoto() {
        return suolamuoto;
    }

    public void setSuolamuoto(String suolamuoto) {
        this.suolamuoto = suolamuoto;
    }

    public double getAinesosanMaaraValue() {
        return ainesosanMaaraValue;
    }

    public void setAinesosanMaaraValue(double ainesosanMaaraValue) {
        this.ainesosanMaaraValue = ainesosanMaaraValue;
    }

    public String getAinesosanMaaraUnit() {
        return ainesosanMaaraUnit;
    }

    public void setAinesosanMaaraUnit(String ainesosanMaaraUnit) {
        this.ainesosanMaaraUnit = ainesosanMaaraUnit;
    }

    public String getAinesosanMaaraTekstina() {
        return ainesosanMaaraTekstina;
    }

    public void setAinesosanMaaraTekstina(String ainesosanMaaraTekstina) {
        this.ainesosanMaaraTekstina = ainesosanMaaraTekstina;
    }

    public String getATCNimi() {
        return ATCnimi;
    }

    public void setATCnimi(String ATCnimi) {
        this.ATCnimi = ATCnimi;
    }

}
