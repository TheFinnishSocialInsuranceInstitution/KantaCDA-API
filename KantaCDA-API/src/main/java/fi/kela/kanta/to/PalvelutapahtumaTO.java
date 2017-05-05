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
import java.util.Date;

public class PalvelutapahtumaTO implements Serializable {

    private static final long serialVersionUID = -688883223902178L;

    private Date aikaleima;
    private IdTO oid;
    private OrganisaatioTO toimintayksikko;

    public Date getAikaleima() {
        return aikaleima;
    }

    public void setAikaleima(Date aikaleima) {
        this.aikaleima = aikaleima;
    }

    public IdTO getOid() {
        return oid;
    }

    public void setOid(IdTO oid) {
        this.oid = oid;
    }

    public OrganisaatioTO getToimintayksikko() {
        return toimintayksikko;
    }

    public void setToimintayksikko(OrganisaatioTO toimintayksikko) {
        this.toimintayksikko = toimintayksikko;
    }

}
