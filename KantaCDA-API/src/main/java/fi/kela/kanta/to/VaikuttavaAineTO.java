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

public class VaikuttavaAineTO implements Serializable {

    private static final long serialVersionUID = 16785685655533L;

    private String laakeaine;
    private String laakeaineenTarkenne;
    private double vahvuus;
    private String vahvuusYksikko;

    public String getLaakeaine() {
        return laakeaine;
    }

    public void setLaakeaine(String laakeaine) {
        this.laakeaine = laakeaine;
    }

    public String getLaakeaineenTarkenne() {
        return laakeaineenTarkenne;
    }

    public void setLaakeaineenTarkenne(String laakeaineenTarkenne) {
        this.laakeaineenTarkenne = laakeaineenTarkenne;
    }

    public double getVahvuus() {
        return vahvuus;
    }

    public void setVahvuus(double vahvuus) {
        this.vahvuus = vahvuus;
    }

    public String getVahvuusYksikko() {
        return vahvuusYksikko;
    }

    public void setVahvuusYksikko(String vahvuusYksikko) {
        this.vahvuusYksikko = vahvuusYksikko;
    }
}
