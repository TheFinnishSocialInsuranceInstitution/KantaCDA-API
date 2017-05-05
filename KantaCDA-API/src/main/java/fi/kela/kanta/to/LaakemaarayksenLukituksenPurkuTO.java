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

public class LaakemaarayksenLukituksenPurkuTO extends LeimakentatTO<LaakemaarayksenLukituksenPurkuTO> {

    private static final long serialVersionUID = 82469109222248362L;

    private String lukitussanomanOid;
    private String lukitussanomanSetId;
    private int lukitussanomanVersio;
    private AmmattihenkiloTO purkaja;
    private String selitys;

    public String getLukitussanomanOid() {
        return lukitussanomanOid;
    }

    public void setLukitussanomanOid(String lukitussanomanOid) {
        this.lukitussanomanOid = lukitussanomanOid;
    }

    public String getLukitussanomanSetId() {
        return lukitussanomanSetId;
    }

    public void setLukitussanomanSetId(String lukitussanomanSetId) {
        this.lukitussanomanSetId = lukitussanomanSetId;
    }

    public int getLukitussanomanVersio() {
        return lukitussanomanVersio;
    }

    public void setLukitussanomanVersio(int lukitussanomanVersio) {
        this.lukitussanomanVersio = lukitussanomanVersio;
    }

    public AmmattihenkiloTO getPurkaja() {
        return purkaja;
    }

    public void setPurkaja(AmmattihenkiloTO purkaja) {
        this.purkaja = purkaja;
    }

    public String getSelitys() {
        return selitys;
    }

    public void setSelitys(String selitys) {
        this.selitys = selitys;
    }

}
