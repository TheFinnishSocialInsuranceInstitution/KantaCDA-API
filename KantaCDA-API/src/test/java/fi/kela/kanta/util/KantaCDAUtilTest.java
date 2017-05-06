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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static fi.kela.kanta.util.KantaCDAUtil.doubleToString;
import static fi.kela.kanta.util.KantaCDAUtil.hetuToBirthTime;
import static fi.kela.kanta.util.KantaCDAUtil.hetuToGender;
import static fi.kela.kanta.util.KantaCDAUtil.loadProperties;
import static fi.kela.kanta.util.KantaCDAUtil.onkoNullTaiTyhja;
import static fi.kela.kanta.util.KantaCDAUtil.onkoValidiHetu;
import static fi.kela.kanta.util.KantaCDAUtil.poistaKontrolliMerkit;
import static fi.kela.kanta.util.KantaCDAUtil.vuosisata;

import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

public class KantaCDAUtilTest {
    /* http://www.telepartikkeli.net/tunnusgeneraattori */
    private static final String[] validitHetut = new String[] {
        "101082-931P", "260100A953A", "130300a9371", "270800+979E"};

    @Test
    public void testOnkoValidiHetu() {
    	for (final String validiHetu : validitHetut) {
          	assertTrue(onkoValidiHetu(validiHetu));
        }
        assertFalse(onkoValidiHetu(null));
        assertFalse(onkoValidiHetu(""));
        /* Liian lyhyt */
        assertFalse(onkoValidiHetu(validitHetut[0].substring(0, 10)));
        /* Liian pitkä */
        assertFalse(onkoValidiHetu(validitHetut[0] + "X"));
        /* Väärä muoto */
        assertFalse(onkoValidiHetu("PPKKVV-NNNNX"));
    }

    @Test
    public void testVuosisata() {
        assertEquals("18", vuosisata('+'));
        assertEquals("19", vuosisata('-'));
        assertEquals("20", vuosisata('A'));
        assertEquals("20", vuosisata('a'));
        assertEquals("21", vuosisata('?'));
    }

    @Test
    public void testHetuToBirthTime() {
        assertEquals("20000101", hetuToBirthTime("010100A0011"));
        assertEquals("20000101", hetuToBirthTime("010100a0011"));
        assertEquals("19991231", hetuToBirthTime("311299-1234"));
        assertEquals("18550406", hetuToBirthTime("060455+9870"));
    }

    @Test
    public void testHetuToGender() {
        assertEquals(0, hetuToGender("PPKKVVXNNNX"));
        assertEquals(1, hetuToGender("010100A001A"));
        assertEquals(2, hetuToGender("010100A002A"));
    }

    @Test
    public void testLoadProperties() throws Exception {
        final Properties properties = loadProperties("test.properties");
        assertEquals("test", properties.getProperty("test"));
    }

    @Test(expected=IOException.class)
    public void testLoadPropertiesWhichDoesNotExist() throws Exception {
        loadProperties("fail.properties");
    }

    @Test
    public void testPoistaKontrolliMerkit() {
        assertEquals("", poistaKontrolliMerkit("\n"));
        assertEquals("", poistaKontrolliMerkit("\r"));
        assertNull(poistaKontrolliMerkit(null));
        assertEquals("", poistaKontrolliMerkit("\n\r"));
        assertEquals("foobar", poistaKontrolliMerkit("\rfoo\nbar"));
        assertEquals("foobar", poistaKontrolliMerkit("\nfoo\rbar"));
    }

    @Test
    public void testDoubleToString() {
        assertEquals("0", doubleToString(0.0000d));
        assertEquals("0.0001", doubleToString(0.0001d));
        assertEquals("1.0001", doubleToString(1.000100d));
    }

    @Test
    public void testOnkoTyhjaTaiNull() {
        assertTrue(onkoNullTaiTyhja(null));
        assertTrue(onkoNullTaiTyhja(""));
        assertFalse(onkoNullTaiTyhja(" "));
    }
}
