package fi.kela.kanta.cda.validation;

import java.util.List;

import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.AnnosTO;
import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.OrganisaatioTO;
import fi.kela.kanta.util.KantaCDAUtil;

public abstract class Validoija {

	abstract public void validoi();
	
	protected void validoiAmmattihenkilo(AmmattihenkiloTO ammattihenkilo) {
		if (null == ammattihenkilo) {
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
		if (null == ammattihenkilo.getKokonimi()) {
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
    	if (null == organisaatio) {
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
    	if (null == organisaatio.getOsoite()) {
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
	if (null == henkilotiedot) {
	    throw new IllegalArgumentException("Henkilotiedot cannot be null.");
	}
	if (null == henkilotiedot.getNimi()) {
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
    
    protected void validoiAnnostelukaudenKesto(LaakemaaraysTO laakemaarays) {
		if(laakemaarays.getAnnostelukaudenPituus() != null && laakemaarays.getAnnostelukaudenLoppupvm() != null) {
			throw new IllegalArgumentException("Annostelukauden pituus and Annostelukauden päättymisaika cannot exist at the same time.");
		}
    }
    
    protected void validoiRakenteinenAnnostus(LaakemaaraysTO laakemaarays) {
    	if(laakemaarays == null) {
    	    throw new IllegalArgumentException("Laakemaarays cannot be null.");
    	}
    	if(!laakemaarays.isAnnosteluPelkastaanTekstimuodossa()) {
    		validoiAnnos(laakemaarays.getAnnokset());
    		// TODO: rakenteisen annostuksen muu validointi
    		validoiAnnostelukaudenKesto(laakemaarays);
    	}
    }
    protected void validoiAnnos(List<AnnosTO> annosList) {
    	if(annosList.isEmpty()) {
    	    throw new IllegalArgumentException("Annos cannot be null or empty.");
    	}
    	for(AnnosTO annos : annosList) {
    		if(annos.getHighAnnos() != null || annos.getLowAnnos() != null) {
    			if(annos.getLowAnnos() == null) {
    	    	    throw new IllegalArgumentException("Dosequantity is missing low value when high value is given.");
    			}
    			if(annos.getHighAnnos() == null) {
    	    	    throw new IllegalArgumentException("Dosequantity is missing high value when low value is given.");
    			}
    			if(annos.getFysYksikko() != null || annos.getHighFysAnnos() != null || annos.getLowFysAnnos() != null 
    					|| annos.getVakioFysAnnos() != null) {
    	    	    throw new IllegalArgumentException("Annos and fysikaalinen annos cannot exits at the same time.");
    			}
    			if(annos.getAnnosyksikko() == null) {
    	    	    throw new IllegalArgumentException("Annos is missing unit");
    			}
    		}
    		if(annos.getVakioAnnos() != null) {
    			if(annos.getFysYksikko() != null || annos.getHighFysAnnos() != null || annos.getLowFysAnnos() != null 
    					|| annos.getVakioFysAnnos() != null) {
    	    	    throw new IllegalArgumentException("Annos and fysikaalinen annos cannot exits at the same time.");
    			}
    			if(annos.getAnnosyksikko() == null) {
    	    	    throw new IllegalArgumentException("Annos is missing unit");
    			}
    			if(annos.getHighAnnos() != null) {
    	    	    throw new IllegalArgumentException("Dosequantity cannot have both high and center values.");
    			}
    			if(annos.getLowAnnos() != null) {
    	    	    throw new IllegalArgumentException("Dosequantity cannot have both low and center values.");
    			}
    		}
    		if(annos.getHighFysAnnos() != null || annos.getLowFysAnnos() != null) {
    			if(annos.getLowFysAnnos() == null) {
    	    	    throw new IllegalArgumentException("Fysikaalinen annos is missing low value");
    			}
    			if(annos.getHighFysAnnos() == null) {
    				throw new IllegalArgumentException("Fysikaalinen annos is missing high value");
    			}
    			if(annos.getFysYksikko() == null) {
    	    	    throw new IllegalArgumentException("Fysikaalinen annos is missing unit");
    			}
    			if(annos.getAnnosyksikko() != null || annos.getHighAnnos() != null || annos.getLowAnnos() != null 
    					|| annos.getVakioAnnos() != null) {
    	    	    throw new IllegalArgumentException("Annos and fysikaalinen annos cannot exits at the same time.");
    			}
    		}
    		if(annos.getVakioFysAnnos() != null) {
    			if(annos.getFysYksikko() == null) {
    	    	    throw new IllegalArgumentException("Fysikaalinen annos is missing unit");
    			}
    			if(annos.getHighFysAnnos() != null) {
    	    	    throw new IllegalArgumentException("Fysikaalinen annos cannot have both high and center values.");
    			}
    			if(annos.getLowFysAnnos() != null) {
    	    	    throw new IllegalArgumentException("Fysikaalinen annos cannot have both low and center values.");
    			}
    			if(annos.getAnnosyksikko() != null || annos.getHighAnnos() != null || annos.getLowAnnos() != null) {
    	    	    throw new IllegalArgumentException("Annos and fysikaalinen annos cannot exits at the same time.");
    			}
    		}
    	}
    }
}
