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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.lang.Character;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * KantaCDAUtils Apumetodien kaatoluokka
 */
public class KantaCDAUtil {

    private KantaCDAUtil() {
    }

    public static final String CONTROL_PATTERN = "\\r|\\n";

    private static final Logger LOGGER = LogManager.getLogger(KantaCDAUtil.class);

    /* http://www.stat.fi/meta/kas/hetu.html */
    private static final Pattern HETU_PATTERN = Pattern.compile("^[0-3][0-9][0-1][0-9]{3}.[0-9]{3}.$");

    private static final int SUKUPUOLI_TUNTEMATON = 0;
    private static final int SUKUPUOLI_MIES = 1;
    private static final int SUKUPUOLI_NAINEN = 2;

    /**
     * Validoi pintapuolisesti hetun.
     */
    protected static boolean onkoValidiHetu(final String hetu) {
        return !onkoNullTaiTyhja(hetu) && HETU_PATTERN.matcher(hetu).matches();
    }

    /**
     * Muunna hetun vuosisata välimerkki vuosisadaksi
     */
    protected static String vuosisata(final char valimerkki) {
       switch (valimerkki) {
            case '+': return "18";
            case '-': return "19";
            case 'A': // fall through
            case 'a': return "20";
            default: return "21";
        }
    }

    /**
     * Muodostaa syntymäajan hetun perusteella
     */
    public static String hetuToBirthTime(final String hetu) {
        if ( !onkoValidiHetu(hetu) ) {
            return null;
        }
        final String vuosisata = vuosisata(hetu.charAt(6));
        return vuosisata + hetu.substring(4, 6) + hetu.substring(2, 4) + hetu.substring(0, 2);
    }

    /**
     * Muodosta sukupuoli hetusta säännöllä: pariton = mies, parillinen = nainen
     */
    public static int hetuToGender(final String hetu) {
        if ( onkoValidiHetu(hetu) ) {
            return Character.getNumericValue(hetu.charAt(9)) % 2 == 1
                ? SUKUPUOLI_MIES : SUKUPUOLI_NAINEN;
        }
        return SUKUPUOLI_TUNTEMATON;
    }

    public static Properties loadProperties(final String propertyFile) throws IOException {
        try (InputStream input =
                KantaCDAUtil.class.getClassLoader().getResourceAsStream(propertyFile)) {
            if (input == null) {
                LOGGER.error("Unable to read property file " + propertyFile);
                throw new IOException("Unable read property file");
            }
            final InputStreamReader reader = new InputStreamReader(input, "UTF-8");
            final Properties properties = new Properties();
            properties.load(input);
            return properties;
        }
    }

    public static String poistaKontrolliMerkit(final String teksti) {
        if ( !onkoNullTaiTyhja(teksti) ) {
            return teksti.replaceAll(KantaCDAUtil.CONTROL_PATTERN, "");
        }
        return null;
    }

    /**
     * Muuntaa desimaaliluvun merkkijonoksi ja poistaa desimaalit, jotka eivät ole merkitseviä.
     * Esim. 4.0 --> 4
     */
    public static String doubleToString(final double luku) {
        return BigDecimal.valueOf(luku).stripTrailingZeros().toPlainString();
    }

    public static boolean onkoNullTaiTyhja(final String merkkijono) {
        return null == merkkijono || merkkijono.isEmpty();
    }
}
