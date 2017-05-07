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
package fi.kela.kanta.cda.validation;

import org.codehaus.plexus.util.StringUtils;

import fi.kela.kanta.to.LaakemaaraysTO;

public class ReseptinUusimisValidoija extends LaakemaaraysValidoija {

	protected LaakemaaraysTO uusi;
	
	public LaakemaaraysTO getUusi() {
		return uusi;
	}

	public void setUusi(LaakemaaraysTO uusi) {
		this.uusi = uusi;
	}

	public ReseptinUusimisValidoija(LaakemaaraysTO alkuperainenLaakemaarays, LaakemaaraysTO uusi) {
		super(alkuperainenLaakemaarays);
		setUusi(uusi);
	}

	@Override
	public void validoi() {
		validoiAlkuperainenLaakemaarays();
		validoiLaakemaaraysUusiminen();
	}

    /**
     * Validoi cda:n muodostamisen kannalta tarvittavien parametrien tilan.
     */
    protected void validoiLaakemaaraysUusiminen() {

	if (null == getUusi()) {
	    throw new IllegalArgumentException("Uusittu lääkemääräys ei saa olla null.");
	}
	if (null == getUusi().getMaarayspaiva()) {
	    throw new IllegalArgumentException("Uusitun lääkemääräyksen 'määräyspäivä' ei saa olla null.");
	}
	if (StringUtils.isEmpty(getUusi().getOid())) {
	    throw new IllegalArgumentException("Uusitun lääkemääräyksen 'oid' pitää löytyä.");
	}
	if (null == getUusi().getSetId()) {
	    throw new IllegalArgumentException("Uusitun lääkemääräyksen 'setid' pitää löytyä.");
	}
	validoiHenkilotiedot(getUusi().getPotilas());
    }

    /**
     * Validoi alkuperäisen LaakemaaraysTOn
     */
    public void validoiAlkuperainenLaakemaarays() {

	if (null == getAlkuperainenLaakemaarays()) {
	    throw new IllegalArgumentException("Alkuperäinen lääkemääräys ei saa olla null.");
	}
	if (null == getAlkuperainenLaakemaarays().getMaarayspaiva()) {
	    throw new IllegalArgumentException("Alkuperäisen lääkemääräyksen 'määräyspäivä' ei saa olla null.");
	}
	if (StringUtils.isEmpty(getAlkuperainenLaakemaarays().getOid())) {
	    throw new IllegalArgumentException("Alkuperäisen lääkemääräyksen 'oid' pitää löytyä.");
	}
	if (null == getAlkuperainenLaakemaarays().getSetId()) {
	    throw new IllegalArgumentException("Alkuperäisen lääkemääräyksen 'setid' pitää löytyä.");
	}
    }

}
