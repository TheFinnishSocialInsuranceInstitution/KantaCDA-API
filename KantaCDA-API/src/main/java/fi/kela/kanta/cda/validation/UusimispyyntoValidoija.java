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

import fi.kela.kanta.to.UusimispyyntoTO;
import fi.kela.kanta.util.KantaCDAUtil;

public class UusimispyyntoValidoija extends Validoija {

	protected UusimispyyntoTO uusimispyynto;
	
	public UusimispyyntoTO getUusimispyynto() {
		return uusimispyynto;
	}

	public void setUusimispyynto(UusimispyyntoTO uusimispyynto) {
		this.uusimispyynto = uusimispyynto;
	}

	public UusimispyyntoValidoija(UusimispyyntoTO uusimispyynto) {
		setUusimispyynto(uusimispyynto);
	}
	
	@Override
	public void validoi() {
		validoiUusimispyyntoTO();
	}
	
	protected void validoiUusimispyynnonVastaanottaja() {
		if (KantaCDAUtil.onkoNullTaiTyhja(getUusimispyynto().getVastaanottajaId())) {
			throw new IllegalArgumentException("Vastaanottajan Id ei saa olla null eikä tyhjä.");
		}
		if (KantaCDAUtil.onkoNullTaiTyhja(getUusimispyynto().getVastaanottajaNimi())) {
			throw new IllegalArgumentException("Vastaanottajan nimi ei saa olla null eikä tyhjä.");
		}
		if (KantaCDAUtil.onkoNullTaiTyhja(getUusimispyynto().getVastaanottajaKatu())) {
			throw new IllegalArgumentException("Vastaanottajan katu ei saa olla null eikä tyhjä.");
		}
		if (KantaCDAUtil.onkoNullTaiTyhja(getUusimispyynto().getVastaanottajaPostinumero())) {
			throw new IllegalArgumentException("Vastaanottajan postinumero ei saa olla null eikä tyhjä.");
		}
		if (KantaCDAUtil.onkoNullTaiTyhja(getUusimispyynto().getVastaanottajaKaupunki())) {
			throw new IllegalArgumentException("Vastaanottajan kaupunki ei saa olla null eikä tyhjä.");
		}
		if (KantaCDAUtil.onkoNullTaiTyhja(getUusimispyynto().getVastaanottajaPuhelinnumero())) {
			throw new IllegalArgumentException("Vastaanottajan puhelinnumero ei saa olla null eikä tyhjä.");
		}
	}
	
	protected void validoiUusimispyynnonMaaraja() {
		if (getUusimispyynto().getMaaraajanKokonimi() == null) {
			throw new IllegalArgumentException("Määrääjän nimi ei saa olla null.");
		}
		if (KantaCDAUtil.onkoNullTaiTyhja(getUusimispyynto().getMaaraajanKokonimi().getEtunimi())) {
			throw new IllegalArgumentException("Määrääjän etunimi ei saa olla null eikä tyhjä.");
		}
		if (KantaCDAUtil.onkoNullTaiTyhja(getUusimispyynto().getMaaraajanKokonimi().getSukunimi())) {
			throw new IllegalArgumentException("Määrääjän sukunimi ei saa olla null eikä tyhjä.");
		}
		if (KantaCDAUtil.onkoNullTaiTyhja(getUusimispyynto().getMaaraajanId())) {
			throw new IllegalArgumentException("Määrääjän Id ei saa olla null eikä tyhjä.");
		}
	}
	
	protected void validoiUusimispyyntoTO() {
		if(getUusimispyynto() == null) {
			throw new IllegalArgumentException("Uusimispyynto ei saa olla null.");
		}
		validoiUusimispyynnonVastaanottaja();
		
		if (KantaCDAUtil.onkoNullTaiTyhja(getUusimispyynto().getUusittavaLaakemaaraysOid())) {
			throw new IllegalArgumentException("Uusittava lääkemääräys oid ei saa olla null eikä tyhjä.");
		}
		if (KantaCDAUtil.onkoNullTaiTyhja(getUusimispyynto().getUusittavaLaakemaaraysSetId())) {
			throw new IllegalArgumentException("Uusittava lääkemääräys setId ei saa olla null eikä tyhjä.");
		}
		if (KantaCDAUtil.onkoNullTaiTyhja(getUusimispyynto().getValmisteenNimi())) {
			throw new IllegalArgumentException("Valmisteen nimi oid ei saa olla null eikä tyhjä.");
		}
		validoiUusimispyynnonMaaraja();
		validoiHenkilotiedot(getUusimispyynto().getHenkilotiedot());
	}

}
