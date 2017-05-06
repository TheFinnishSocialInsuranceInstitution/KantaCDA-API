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

import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.OrganisaatioTO;
import fi.kela.kanta.util.KantaCDAUtil;

public abstract class Validoija {

	abstract public void validoi();
	
	protected void validoiAmmattihenkilo(AmmattihenkiloTO ammattihenkilo) {
		if (ammattihenkilo == null) {
		    throw new IllegalArgumentException("Ammattihenkilo cannot be null.");
		}
		if (KantaCDAUtil.onkoNullTaiTyhja(ammattihenkilo.getRooli())) {
		    throw new IllegalArgumentException("Rooli cannot be null or empty.");
		}
		if (KantaCDAUtil.onkoNullTaiTyhja(ammattihenkilo.getSvNumero())) {
		    throw new IllegalArgumentException("SvNumero cannot be null or empty.");
		}
		if (KantaCDAUtil.onkoNullTaiTyhja(ammattihenkilo.getRekisterointinumero())) {
		    throw new IllegalArgumentException("Rekisterointinumero cannot be null or empty.");
		}

		if (KantaCDAUtil.onkoNullTaiTyhja(ammattihenkilo.getAmmattioikeus())) {
		    throw new IllegalArgumentException("Ammattioikeus cannot be null or empty.");
		}
		if (ammattihenkilo.getKokonimi() == null) {
		    throw new IllegalArgumentException("Kokonimi cannot be null.");
		}
		if (KantaCDAUtil.onkoNullTaiTyhja(ammattihenkilo.getKokonimi().getKokoNimi())) {
		    throw new IllegalArgumentException("Ammattihenkilö nimi cannot be null or empty.");
		}

	    }
	
	/**
     * Validoi annetun OraganisaatioTO:n tiedot
     * Jos organisaatioEP: true (organisaatio on ehdollisesti pakollinen (eli ei ole pakollinen)) ja organisaatio on null. OrganisaatioTOta ei tarkisteta
     * Jos failOnTel: true niin organisaatiolla on oltava puhelinnumero.
     * 
     * @param organisaatio validoitava organistaatioTO
     * @param organisaatioEP onko organisaatio ehdollisesti pakollinen
     * @param failOnTel onko organisaatiolla oltava puhelinumero
     */
    protected void validoiOrganisaatio(OrganisaatioTO organisaatio, boolean organisaatioEP, boolean failOnTel) {
    	if (organisaatio == null) {
    		if (!organisaatioEP) {
    			throw new IllegalArgumentException("Organisaatio cannot be null.");
    		} else {
    			return; //ehdollisesti pakollinen ja organisaatiota ei annettu => ok
    		}
    	}
    	if (KantaCDAUtil.onkoNullTaiTyhja(organisaatio.getYksilointitunnus())) {
    	    throw new IllegalArgumentException("Organisaatio yksilointitunnus cannot be null or empty.");
    	}
    	if (KantaCDAUtil.onkoNullTaiTyhja(organisaatio.getNimi())) {
    	    throw new IllegalArgumentException("Organisaatio nimi cannot be null or empty.");
    	}
    	if (KantaCDAUtil.onkoNullTaiTyhja(organisaatio.getPuhelinnumero()) && failOnTel) {
    	    throw new IllegalArgumentException("Orgnisaatio puhelinnumero cannot be null or empty.");
    	}
    	if (organisaatio.getOsoite() == null) {
    	    throw new IllegalArgumentException("Organisaatio osoite cannot be null.");
    	}
    	if (KantaCDAUtil.onkoNullTaiTyhja(organisaatio.getOsoite().getKatuosoite())) {
    	    throw new IllegalArgumentException("Organisaatio katuosoite cannot be null or empty.");
    	}
    	if (KantaCDAUtil.onkoNullTaiTyhja(organisaatio.getOsoite().getPostinumero())) {
    	    throw new IllegalArgumentException("Organisaatio postinumero cannot be null or empty.");
    	}
    	if (KantaCDAUtil.onkoNullTaiTyhja(organisaatio.getOsoite().getPostitoimipaikka())) {
    	    throw new IllegalArgumentException("Organisaatio postitoimipaikka cannot be null or empty.");
    	}	
    }


    public void validoiHenkilotiedot(HenkilotiedotTO henkilotiedot) {
	if (henkilotiedot == null) {
	    throw new IllegalArgumentException("Henkilotiedot cannot be null.");
	}
	if (henkilotiedot.getNimi() == null) {
	    throw new IllegalArgumentException("Nimi cannot be null.");
	}
	if (KantaCDAUtil.onkoNullTaiTyhja(henkilotiedot.getSyntymaaika())) {
	    throw new IllegalArgumentException("Syntymaaika cannot be null or empty.");
	}
	if (henkilotiedot.getSukupuoli() == null) {
	    throw new IllegalArgumentException("Sukupuoli cannot be null.");
	}
	if (KantaCDAUtil.onkoNullTaiTyhja(henkilotiedot.getNimi().getKokoNimi())) {
	    throw new IllegalArgumentException("Nimi cannot be null or empty.");
	}
    }

}
