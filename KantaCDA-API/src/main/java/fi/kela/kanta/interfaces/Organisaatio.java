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
package fi.kela.kanta.interfaces;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

public interface Organisaatio extends Serializable {

    /**
     * @return Organisaation nimi.
     */
    public String getNimi();

    /**
     * @param nimi
     *            Organisaation nimi.
     */
    public void setNimi(String nimi);

    /**
     * @return Organisaation osoite tiedot.
     */
    public Osoite getOsoite();

    /**
     * @param osoite
     *            Organisaation osoite tiedot.
     * @throws throws
     *             IllegalAccessException Jos annetaan TO instanssi ja property kopioinnissa tapahtuu virhe
     * @throws InvocationTargetException
     *             Jos annetaan TO instanssi ja property kopioinnissa tapahtuu virhe
     */
    public void setOsoite(Osoite osoite) throws IllegalAccessException, InvocationTargetException;

    /**
     * @return Organisaation yksilöintitunnus (oid).
     */
    public String getYksilointitunnus();

    /**
     * @param yksilointitunnus
     *            Organisaation yksilöintitunnus (oid).
     */
    public void setYksilointitunnus(String yksilointitunnus);

    /**
     * @return Organisaation yritys- ja yksilöintitunnus (y-tunnus).
     */
    public String getYtunnus();

    /**
     * @param yTunnus
     *            Organisaation yritys- ja yksilöintitunnus (y-tunnus).
     */
    public void setYtunnus(String yTunnus);

    /**
     * @return Organisaation puhelinnumero.
     */
    public String getPuhelinnumero();

    /**
     * @param puhelinnumero
     *            Organisaation puhelinnumero.
     */
    public void setPuhelinnumero(String puhelinnumero);

    /**
     * @return Organisaation sähköpostiosoite.
     */
    public String getSahkoposti();

    /**
     * @param sahkoposti
     *            Organisaation sähköpostiosoite.
     */
    public void setSahkoposti(String sahkoposti);
}
