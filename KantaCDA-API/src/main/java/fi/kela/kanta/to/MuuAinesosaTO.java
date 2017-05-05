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

public class MuuAinesosaTO implements Serializable {

    private static final long serialVersionUID = 64754788881181L;
    // Nimi:String
    private String nimi;
    // Ainesosan määrä:PQ
    private double ainesosanMaaraValue;
    private String ainesosanMaaraUnit;
    // Ainesosan määrä tekstinä:String
    private String ainesosanMaaraTekstina;

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
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

}
