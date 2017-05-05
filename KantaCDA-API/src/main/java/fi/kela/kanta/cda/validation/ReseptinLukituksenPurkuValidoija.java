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

import fi.kela.kanta.to.LaakemaarayksenLukituksenPurkuTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.util.KantaCDAUtil;

public class ReseptinLukituksenPurkuValidoija extends LaakemaaraysValidoija {

	protected LaakemaarayksenLukituksenPurkuTO lukituksenPurku;
	
	public LaakemaarayksenLukituksenPurkuTO getLukituksenPurku() {
		return lukituksenPurku;
	}

	public void setLukituksenPurku(LaakemaarayksenLukituksenPurkuTO lukituksenPurku) {
		this.lukituksenPurku = lukituksenPurku;
	}

	public ReseptinLukituksenPurkuValidoija(LaakemaarayksenLukituksenPurkuTO laakemaarayksenLukituksenPurku,LaakemaaraysTO alkuperainenLaakemaarays) {
		super(alkuperainenLaakemaarays);
		setLukituksenPurku(laakemaarayksenLukituksenPurku);
	}

	@Override
	public void validoi() {
		validoiAlkuperainenLaakemaarays();
		validoiLukituksenPurku();
	}
	
	/**
     * validoi alkuperäisestä lääkemääräyksestä tarvittavat tiedot
     * 
     * @param alkuperainenLaakemaarays LaakemaaraysTO alkuperäinen lääkeämäräys
     */
    protected void validoiAlkuperainenLaakemaarays() {
	if (null == getAlkuperainenLaakemaarays()) {
	    throw new IllegalArgumentException("alkuperainen laakemaarays cannot be null.");
	}
	if (null == getAlkuperainenLaakemaarays().getPotilas()) {
	    throw new IllegalArgumentException("alkuperaisen laakemaarayksen potilas cannot be null.");
	}
	if (KantaCDAUtil.onkoNullTaiTyhja(getAlkuperainenLaakemaarays().getPotilas().getHetu())) {
	    throw new IllegalArgumentException("alkuperaisen potilaan hetu cannot be empty or null.");
	}
	if (KantaCDAUtil.onkoNullTaiTyhja(getAlkuperainenLaakemaarays().getPotilas().getNimi().getKokoNimi())) {
	    throw new IllegalArgumentException("alkuperaisen potilaan nimi cannot be empty or null.");
	}
	if (getAlkuperainenLaakemaarays().getPotilas().getSukupuoli() == null) {
	    throw new IllegalArgumentException("alkuperaisen potilaan sukupuoli cannot be null.");
	}
	if (KantaCDAUtil.onkoNullTaiTyhja(getAlkuperainenLaakemaarays().getOid())) {
	    throw new IllegalArgumentException("alkuperaisen laakemaarayksen oid cannot be empty or null.");
	}
	if (KantaCDAUtil.onkoNullTaiTyhja(getAlkuperainenLaakemaarays().getSetId())) {
	    throw new IllegalArgumentException("alkuperaisen laakemaarayksen setId cannot be empty or null.");
	}
	if (null == getAlkuperainenLaakemaarays().getAmmattihenkilo()) {
	    throw new IllegalArgumentException("alkuperaisen laakemaarayksen ammattihenkilo cannot be null.");
	}
	if (null == getAlkuperainenLaakemaarays().getAmmattihenkilo().getKokonimi()) {
	    throw new IllegalArgumentException("alkuperaisen laakemaarayksen ammattihenkilon kokonimi cannot be null.");
	}
	if (KantaCDAUtil.onkoNullTaiTyhja(getAlkuperainenLaakemaarays().getAmmattihenkilo().getKokonimi().getKokoNimi())) {
	    throw new IllegalArgumentException("alkuperaisen laakemaarayksen ammattihenkilon kokonimi cannot be empty or null.");
	}
    }
    
    protected void validoiLukituksenPurku() {
    	if (null == getLukituksenPurku()) {
    	    throw new IllegalArgumentException("lukituksen purku cannot be null.");
    	}
    	if (null == getLukituksenPurku().getPurkaja()) {
    	    throw new IllegalArgumentException("lukituksen purkaja cannot be null.");
    	}
    	if (null == getLukituksenPurku().getPurkaja().getOrganisaatio()) {
    	    throw new IllegalArgumentException("lukituksen purkajan organisaatio cannot be null.");
    	}
    	if (KantaCDAUtil.onkoNullTaiTyhja(getLukituksenPurku().getLukitussanomanOid())) {
    	    throw new IllegalArgumentException("lukitussanoman oid cannot be empty or null.");
    	}
    	if (KantaCDAUtil.onkoNullTaiTyhja(getLukituksenPurku().getLukitussanomanSetId())) {
    	    throw new IllegalArgumentException("lukitusesanoman setId cannot be empty or null.");
    	}
    	if (KantaCDAUtil.onkoNullTaiTyhja(getLukituksenPurku().getSelitys())) {
    	    throw new IllegalArgumentException("lukituksen purkamisen selitys cannot be empty or null.");
    	}

        }

}
