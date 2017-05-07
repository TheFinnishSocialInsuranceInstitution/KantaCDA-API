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
package fi.kela.kanta.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import fi.kela.kanta.cda.KantaCDAConstants.AsiakirjaVersioYhteensopivuus;

/**
 * Järjestelmän tukemien asiakirjamääritysversioiden hallintaan tarkoitettu luokka. Lukee tuettujen määritysversioden
 * oid:t sekä järjestelmän käyttämän määritysversion oid:n properties-tiedoista, jotka annetaan luokan konstruktorin
 * parametrina.
 */
public class AsiakirjaVersioUtil {

    private static final String ASIAKIRJAVERSIO_OID_KEY = "asiakirjaversio.oid.";
    private static final String ASIAKIRJAVERSIO_OID_LKM_KEY = "asiakirjaversio.oid.lkm";
    private static final String ASIAKIRJAVERSIO_OID_JARJESTELMA_KEY = "asiakirjaversio.oid.jarjestelma";
    private static final Map<String, AsiakirjaVersio> asiakirjaVersiot = new HashMap<String, AsiakirjaVersio>();
    private static final Object versiotlock = new Object();

    public AsiakirjaVersioUtil(Properties props) {
        synchronized (versiotlock) {
            if ( asiakirjaVersiot.size() == 0 ) {
                alustaVersiot(props);
            }
        }
    }

    /**
     * Palauttaa järjestelmän käyttämän asiakirjamääritysversion oid:n
     * 
     * @return String
     */
    public String getJarjestelmaAsiakirjaVersio() {
        for (AsiakirjaVersio versio : asiakirjaVersiot.values()) {
            if ( versio.isJarjestelmaVersio() ) {
                return versio.oid;
            }
        }
        return "";
    }

    /**
     * Palauttaa tiedon siitä miten parametrina saatu asiakirjamääritysversio on yhteensopiva järjestelmän käyttämän
     * version kanssa
     * 
     * @param versio
     * @return KantaCDAConstants.AsiakirjaVersioYhteensopivuus
     */
    public AsiakirjaVersioYhteensopivuus getAsiakirjaVersionYhteensopivuus(String versio) {
        final AsiakirjaVersio jarjestelmaVersio = asiakirjaVersiot.get(getJarjestelmaAsiakirjaVersio());
        final AsiakirjaVersio vers = asiakirjaVersiot.get(versio);
        if ( jarjestelmaVersio == null || vers == null ) {
            return AsiakirjaVersioYhteensopivuus.EI_TUETTU;
        }
        final int tulos = Integer.compare(vers.getJarjNro(), jarjestelmaVersio.getJarjNro());
        if (tulos == 0) {
            return AsiakirjaVersioYhteensopivuus.JARJESTELMA_VERSIO;
        } else if (tulos > 0) {
            return AsiakirjaVersioYhteensopivuus.ETEENPAIN;
        } else {
            return AsiakirjaVersioYhteensopivuus.TAAKSEPAIN;
        }
    }

    /**
     * Palauttaa järjestelmän tukemien asiakirjamääritysversioiden oid:t
     * 
     * @return List
     */
    public List<String> getAsiakirjaVersiot() {
        List<String> versiot = new ArrayList<String>();
        for (AsiakirjaVersio versio : asiakirjaVersiot.values()) {
            versiot.add(versio.getOid());
        }
        return versiot;
    }

    private void alustaVersiot(Properties props) {
        String jarjestelmaVersio = props.getProperty(ASIAKIRJAVERSIO_OID_JARJESTELMA_KEY);
        String sLkm = props.getProperty(ASIAKIRJAVERSIO_OID_LKM_KEY);
        if ( sLkm != null ) {
            int lkm = Integer.parseInt(sLkm);
            String oid = null;
            for (int i = 0; i < lkm; i++) {
                oid = props.getProperty(ASIAKIRJAVERSIO_OID_KEY + (i + 1));
                if ( oid != null ) {
                    AsiakirjaVersio versio = new AsiakirjaVersio(oid, i, false);
                    if ( oid.equals(jarjestelmaVersio) ) {
                        versio.setJarjestelmaVersio(true);
                    }
                    asiakirjaVersiot.put(oid, versio);
                }
            }
        }
    }

    private class AsiakirjaVersio {
        private String oid;
        private int jarjNro;
        private boolean jarjestelmaVersio;

        public AsiakirjaVersio(String oid, int jarjNro, boolean jarjestelmaVersio) {
            this.oid = oid;
            this.jarjNro = jarjNro;
            this.jarjestelmaVersio = jarjestelmaVersio;
        }

        public String getOid() {
            return oid;
        }

        public void setOid(String oid) {
            this.oid = oid;
        }

        public int getJarjNro() {
            return jarjNro;
        }

        public void setJarjNro(Integer jarjNro) {
            this.jarjNro = jarjNro;
        }

        public boolean isJarjestelmaVersio() {
            return jarjestelmaVersio;
        }

        public void setJarjestelmaVersio(boolean jarjestelmaVersio) {
            this.jarjestelmaVersio = jarjestelmaVersio;
        }
    }
}
