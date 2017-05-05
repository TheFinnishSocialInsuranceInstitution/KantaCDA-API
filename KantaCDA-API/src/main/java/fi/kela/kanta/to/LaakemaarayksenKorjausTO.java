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

public class LaakemaarayksenKorjausTO extends LaakemaaraysTO {

    private static final long serialVersionUID = -211777865037738094L;

    private AmmattihenkiloTO korjaaja;
    private String korjauksenPerustelu;
    private String korjauksenSyyKoodi;
    private String alkuperainenOid;
    private int alkuperainenCdaTyyppi;

    public LaakemaarayksenKorjausTO() {
        super(false);
    }

    public AmmattihenkiloTO getKorjaaja() {
        return korjaaja;
    }

    public void setKorjaaja(AmmattihenkiloTO korjaaja) {
        this.korjaaja = korjaaja;
    }

    public String getKorjauksenPerustelu() {
        return korjauksenPerustelu;
    }

    public void setKorjauksenPerustelu(String korjauksenPerustelu) {
        this.korjauksenPerustelu = korjauksenPerustelu;
    }

    public String getKorjauksenSyyKoodi() {
        return korjauksenSyyKoodi;
    }

    public void setKorjauksenSyyKoodi(String korjauksenSyyKoodi) {
        this.korjauksenSyyKoodi = korjauksenSyyKoodi;
    }

    public String getAlkuperainenOid() {
        return alkuperainenOid;
    }

    public void setAlkuperainenOid(String alkuperainenOid) {
        this.alkuperainenOid = alkuperainenOid;
    }

    public int getAlkuperainenCdaTyyppi() {
        return alkuperainenCdaTyyppi;
    }

    public void setAlkuperainenCdaTyyppi(int cdaTyyppi) {
        this.alkuperainenCdaTyyppi = cdaTyyppi;
    }

}
