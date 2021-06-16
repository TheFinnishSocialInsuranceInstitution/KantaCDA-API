package fi.kela.kanta.to;

import java.io.Serializable;

public class KoodiTO implements Serializable {
	private static final long serialVersionUID = 2537482749213692669L;
	
	private String tunniste;
	private String lyhytNimi;
	
	public KoodiTO() {
		
	}
	
	public KoodiTO(String tunniste, String lyhytNimi) {
		this.tunniste = tunniste;
		this.lyhytNimi = lyhytNimi;
	}

	public String getTunniste() {
		return tunniste;
	}
	
	public void setTunniste(String tunniste) {
		this.tunniste = tunniste;
	
	}
	public String getLyhytNimi() {
		return lyhytNimi;
	}
	
	public void setLyhytNimi(String lyhytNimi) {
		this.lyhytNimi = lyhytNimi;
	}
}
