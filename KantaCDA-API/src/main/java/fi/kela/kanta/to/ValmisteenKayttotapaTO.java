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

public class ValmisteenKayttotapaTO implements Serializable {

    private static final long serialVersionUID = 135111113252356L;
    // Lääkemuoto:CV
    private String laakemuoto;
    // Lääkemuodon lyhenne:String
    private String laakemuodonLyhenne;
    // Lääkkeenantoreitti:CV
    private String laakeenantoreitti;

    public String getLaakemuoto() {
        return laakemuoto;
    }

    public void setLaakemuoto(String laakemuoto) {
        this.laakemuoto = laakemuoto;
    }

    public String getLaakemuodonLyhenne() {
        return laakemuodonLyhenne;
    }

    public void setLaakemuodonLyhenne(String laakemuodonLyhenne) {
        this.laakemuodonLyhenne = laakemuodonLyhenne;
    }

    public String getLaakeenantoreitti() {
        return laakeenantoreitti;
    }

    public void setLaakeenantoreitti(String laakeenantoreitti) {
        this.laakeenantoreitti = laakeenantoreitti;
    }
}
