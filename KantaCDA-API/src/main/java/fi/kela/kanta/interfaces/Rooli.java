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

/**
 * Rajapinta käyttäjärekisterin, shiron ja mahdollisesti logituksen tarvitsemien roolitietojen käsittelyyn. <br/>
 * Pitäisi selvittää löytyvätkö arvot koodistosta. Esim: 1.2.246.537.6.246 (THL - Toimenpiteen tekijän rooli)
 */
public interface Rooli {

    /**
     * PK arvo joka pitäisi löytyä käyttäjärekisteristä, sekä mahdollisesti myös koodistosta.
     *
     * @return Id roolille.
     */
    public String getRooliId();

    /**
     * Tämä arvo antaa selkokielisen (lyhyen) kuvauksen roolista.
     *
     * @return Roolille annettu nimi.
     */
    public String getRooliNimi();

    /**
     * @return Vapaamuotoista lisätietoa roolista.
     */
    public String getLisatieto();
}
