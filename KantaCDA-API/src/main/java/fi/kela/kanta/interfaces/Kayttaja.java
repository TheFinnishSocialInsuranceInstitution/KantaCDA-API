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

import java.util.List;

public interface Kayttaja extends Henkilotiedot {

    /**
     * @return Sisäänkirjautuneen käyttäjän rekisteröitymistunnus (terhikki tunnus).
     */
    public String getRekisterinumero();

    /**
     * @return Henkilön 'katso id'.
     */
    public String getKatsoId();

    /**
     * Näistä tiedosta asetetaan shiro roolit, käyttäen <b>tunnisteina</b> roolien id:tä. Muuta tietoa voidaan käyttää
     * mm. logituksen tarpeissa.
     *
     * @return Roolit jotka käyttäjälle on määritelty organisaatiokäytön oikeudeksi.
     */
    public List<Rooli> getOrganisaatioroolit();
}
