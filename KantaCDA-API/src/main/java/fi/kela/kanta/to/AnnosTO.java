package fi.kela.kanta.to;

import java.io.Serializable;
import java.time.LocalTime;

public class AnnosTO implements Serializable {

    private static final long serialVersionUID = -42255555108L;

    private String annosId;
	private Double lowAnnos;
    private Double highAnnos;
	private Double vakioAnnos;
	private Double lowFysAnnos;
    private Double highFysAnnos;
	private Double vakioFysAnnos;
    private String fysYksikko;	
    private Boolean annosTarvittaessa = Boolean.FALSE;
    private KoodiTO annosajankohta;
    private KoodiTO annosjaksonPaiva;
	private LocalTime annosaika;
	private KoodiTO annosyksikko;
 
	public String getAnnosId() {
		return annosId;
	}
	public void setAnnosId(String annosId) {
		this.annosId = annosId;
	}
	public Double getLowAnnos() {
		return lowAnnos;
	}
	public void setLowAnnos(Double lowAnnos) {
		this.lowAnnos = lowAnnos;
	}
	public Double getHighAnnos() {
		return highAnnos;
	}
	public void setHighAnnos(Double highAnnos) {
		this.highAnnos = highAnnos;
	}

	public Double getVakioAnnos() {
		return vakioAnnos;
	}

	public void setVakioAnnos(Double vakioAnnos) {
		this.vakioAnnos = vakioAnnos;
	}

	public Boolean getAnnosTarvittaessa() {
		return annosTarvittaessa;
	}
	public void setAnnosTarvittaessa(Boolean annosTarvittaessa) {
		this.annosTarvittaessa = annosTarvittaessa;
	}
	public KoodiTO getAnnosajankohta() {
		return annosajankohta;
	}
	public void setAnnosajankohta(KoodiTO annosajankohta) {
		this.annosajankohta = annosajankohta;
	}
	public KoodiTO getAnnosjaksonPaiva() {
		return annosjaksonPaiva;
	}
	public void setAnnosjaksonPaiva(KoodiTO annosjaksonPaiva) {
		this.annosjaksonPaiva = annosjaksonPaiva;
	}
	public Double getLowFysAnnos() {
		return lowFysAnnos;
	}
	public void setLowFysAnnos(Double lowFysAnnos) {
		this.lowFysAnnos = lowFysAnnos;
	}
	public Double getHighFysAnnos() {
		return highFysAnnos;
	}
	public void setHighFysAnnos(Double highFysAnnos) {
		this.highFysAnnos = highFysAnnos;
	}
	public Double getVakioFysAnnos() {
		return vakioFysAnnos;
	}
	public void setVakioFysAnnos(Double vakioFysAnnos) {
		this.vakioFysAnnos = vakioFysAnnos;
	}
	public String getFysYksikko() {
		return fysYksikko;
	}
	public void setFysYksikko(String fysYksikko) {
		this.fysYksikko = fysYksikko;
	}
	// TODO: onko oikea tapa?
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((annosTarvittaessa == null) ? 0 : annosTarvittaessa.hashCode());
		result = prime * result + ((annosaika == null) ? 0 : annosaika.hashCode());
		result = prime * result + ((annosajankohta == null) ? 0 : annosajankohta.hashCode());
		result = prime * result + ((annosjaksonPaiva == null) ? 0 : annosjaksonPaiva.hashCode());
		result = prime * result + ((highAnnos == null) ? 0 : highAnnos.hashCode());
		result = prime * result + ((lowAnnos == null) ? 0 : lowAnnos.hashCode());
		result = prime * result + ((vakioAnnos == null) ? 0 : vakioAnnos.hashCode());
		result = prime * result + ((annosyksikko == null) ? 0 : annosyksikko.hashCode());
		result = prime * result + ((highFysAnnos == null) ? 0 : highFysAnnos.hashCode());
		result = prime * result + ((lowFysAnnos == null) ? 0 : lowFysAnnos.hashCode());
		result = prime * result + ((vakioFysAnnos == null) ? 0 : vakioFysAnnos.hashCode());
		result = prime * result + ((fysYksikko == null) ? 0 : fysYksikko.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnnosTO other = (AnnosTO) obj;

		if (annosTarvittaessa == null) {
			if (other.annosTarvittaessa != null)
				return false;
		} else if (!annosTarvittaessa.equals(other.annosTarvittaessa))
			return false;
		if (annosaika == null) {
			if (other.annosaika != null)
				return false;
		} else if (!annosaika.equals(other.annosaika))
			return false;
		if (annosajankohta == null) {
			if (other.annosajankohta != null)
				return false;
		} else if (!annosajankohta.equals(other.annosajankohta))
			return false;
		if (annosjaksonPaiva == null) {
			if (other.annosjaksonPaiva != null)
				return false;
		} else if (!annosjaksonPaiva.equals(other.annosjaksonPaiva))
			return false;
		if (highAnnos == null) {
			if (other.highAnnos != null)
				return false;
		} else if (!highAnnos.equals(other.highAnnos))
			return false;
		if (lowAnnos == null) {
			if (other.lowAnnos != null)
				return false;
		} else if (!lowAnnos.equals(other.lowAnnos))
			return false;
		if (vakioAnnos == null) {
			if (other.vakioAnnos != null)
				return false;
		} else if (!vakioAnnos.equals(other.vakioAnnos))
			return false;
		if (highFysAnnos == null) {
			if (other.highFysAnnos != null)
				return false;
		} else if (!highAnnos.equals(other.highFysAnnos))
			return false;
		if (lowFysAnnos == null) {
			if (other.lowFysAnnos != null)
				return false;
		} else if (!lowFysAnnos.equals(other.lowFysAnnos))
			return false;
		if (vakioFysAnnos == null) {
			if (other.vakioFysAnnos != null)
				return false;
		} else if (!vakioFysAnnos.equals(other.vakioFysAnnos))
			return false;
		if (annosyksikko == null) {
			if (other.annosyksikko != null)
				return false;
		} else if (!annosyksikko.equals(other.annosyksikko))
			return false;
		if (fysYksikko == null) {
			if (other.fysYksikko != null)
				return false;
		} else if (!fysYksikko.equals(other.fysYksikko))
			return false;
		return true;
	}
	
	public LocalTime getAnnosaika() {
		return annosaika;
	}
	
	public void setAnnosaika(LocalTime annosaika) {
		this.annosaika = annosaika;
	}
	
	public KoodiTO getAnnosyksikko() {
		return annosyksikko;
	}
	
	public void setAnnosyksikko(KoodiTO annosyksikko) {
		this.annosyksikko = annosyksikko;
	}
}
