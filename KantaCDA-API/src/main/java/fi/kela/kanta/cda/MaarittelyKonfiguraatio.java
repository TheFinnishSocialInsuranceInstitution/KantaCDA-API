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
package fi.kela.kanta.cda;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class MaarittelyKonfiguraatio {

    final private Map<String, MaarittelyLuokka> maarittelyt;

    /**
     * @param templateId
     * @return
     * @throws HL7Exception
     */
    public MaarittelyLuokka haeMaarittelyLuokka(List<String> templateIds, String code) {
        MaarittelyLuokka luokka = MaarittelyLuokka.PUUTTUU;
        if ( (templateIds == null) || templateIds.isEmpty() ) {
            return luokka;
        }
        for (String templateId : templateIds) {
            MaarittelyLuokka seuraava = haeMaarittelyLuokka(templateId, code);
            if ( seuraava == MaarittelyLuokka.EI_TUETTU ) {
                return MaarittelyLuokka.EI_TUETTU;
            }
            else if ( seuraava == MaarittelyLuokka.TULEVA ) {
                if ( luokka == MaarittelyLuokka.PUUTTUU || luokka == MaarittelyLuokka.VANHA
                        || luokka == MaarittelyLuokka.NYKYINEN ) {
                    luokka = seuraava;
                }
            }
            else if ( seuraava == MaarittelyLuokka.VANHA ) {
                if ( luokka == MaarittelyLuokka.PUUTTUU || luokka == MaarittelyLuokka.NYKYINEN ) {
                    luokka = seuraava;
                }
            }
            else if ( seuraava == MaarittelyLuokka.NYKYINEN && luokka == MaarittelyLuokka.PUUTTUU ) {
                luokka = seuraava;
            }
        }
        return luokka;
    }

    private MaarittelyLuokka haeMaarittelyLuokka(String templateId, String code) {
        if ( (templateId == null) || "".equals(templateId) ) {
            return MaarittelyLuokka.PUUTTUU;
        }
        if ( maarittelyt.containsKey(templateId + "..." + code) ) {
            return maarittelyt.get(templateId + "..." + code);
        }
        else if ( maarittelyt.containsKey(templateId) ) {
            return maarittelyt.get(templateId);
        }
        else {
            maarittelyt.put(templateId, MaarittelyLuokka.EI_TUETTU);
            return MaarittelyLuokka.EI_TUETTU;
        }
    }

    /**
     * @return
     */
    public static MaarittelyKonfiguraatio lueKonfiguraatio() throws ConfigurationException {
        return new MaarittelyKonfiguraatio();
    }

    @SuppressWarnings("unchecked")
    private MaarittelyKonfiguraatio() throws ConfigurationException {
        maarittelyt = new HashMap<String, MaarittelyLuokka>();

        try {
            Configuration config = new PropertiesConfiguration("cda_template.properties");

            Iterator<String> templateIds = (Iterator<String>) config.getKeys();
            while (templateIds.hasNext()) {
                kasitteleTyyppi(config, templateIds.next());
            }
        }
        catch (ConfigurationException e) {
            throw e;
        }
    }

    private void kasitteleTyyppi(Configuration config, String templateId) {
        MaarittelyLuokka luokka = MaarittelyLuokka.VANHA;
        for (String token : config.getStringArray(templateId)) {
            if ( (token == null) || "".equals(token) ) {
                continue;
            }
            token = token.trim().toUpperCase();
            if ( "NYKYINEN".equals(token) ) {
                luokka = MaarittelyLuokka.NYKYINEN;
            }
            else if ( "TULEVA".equals(token) ) {
                luokka = MaarittelyLuokka.TULEVA;
            }
            else if ( "VANHA".equals(token) ) {
                luokka = MaarittelyLuokka.VANHA;
            }
            else if ( "EI_TUETTU".equals(token) ) {
                luokka = MaarittelyLuokka.EI_TUETTU;
            }
            else if ( "*".equals(token) ) {
                kasitteleKoodi(templateId, luokka);
            }
            else {
                kasitteleKoodi(templateId + "..." + token, luokka);
            }
        }
    }

    private void kasitteleKoodi(String templateIdKoodi, MaarittelyLuokka luokka) {
        if ( maarittelyt.containsKey(templateIdKoodi) ) {
            MaarittelyLuokka vanha = maarittelyt.get(templateIdKoodi);
            if ( vanha == MaarittelyLuokka.VANHA ) {
                maarittelyt.put(templateIdKoodi, luokka);
            }
            else if ( luokka == MaarittelyLuokka.EI_TUETTU ) {
                maarittelyt.put(templateIdKoodi, luokka);
            }
            else if ( (vanha == MaarittelyLuokka.TULEVA) && (luokka == MaarittelyLuokka.NYKYINEN) ) {
                maarittelyt.put(templateIdKoodi, luokka);
            }
        }
        else {
            maarittelyt.put(templateIdKoodi, luokka);
        }
    }
}
