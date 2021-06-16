package fi.kela.kanta.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OstopalvelunvaltuutusTO extends LeimakentatTO<OstopalvelunvaltuutusTO> implements Serializable {
	private static final long serialVersionUID = 637659999303710507L;

	private HenkilotiedotTO potilas;
	private int ostopalvelunTyyppi;
	private Date valtuutuksenVoimassaoloAlku;
	private Date valtuutuksenVoimassaoloLoppu;
	private String asiakirjanRekisterinpitaja;
	private String asiakirjanRekisterinpitajaNimi;
	private String palvelunJarjestaja;
	private String palvelunJarjestajaNimi;
	private String palvelunJarjestajanPalveluyksikko;
	private String palvelunJarjestajanPalveluyksikonNimi;
	private String palvelunTuottaja;
	private String palvelunTuottajanNimi;
	private String hakuRekisterinpitaja;
	private int hakuRekisteri;
	private String hakuRekisterinTarkenne;
	private String hakuRekisterinTarkentimenNimi;
	private String tallennusRekisterinpitaja;
	private int tallennusRekisteri;
	private String tallennusRekisterinTarkenne;
	private String tallennusRekisterinTarkentimenNimi;
	private boolean kaikkiAsiakirjat;
	private HenkilotiedotTO asiakirjanTallentaja;
	private String ammattihenkilonPalveluyksikko;
	private String ammattihenkilonPalveluyksikonNimi;
	private String ammattihenkilonKatsoTunnus;
	private Date luovutettavanAineistonAlku;
	private Date luovutettavanAineistonLoppu;
	private List<String> luovutettavatPalvelutapahtumat = new ArrayList<String>();

	public HenkilotiedotTO getPotilas() {
		return potilas;
	}

	public void setPotilas(HenkilotiedotTO potilas) {
		this.potilas = potilas;
	}

	/**
	 * @return the ostopalvelunTyyppi
	 */
	public int getOstopalvelunTyyppi() {
		return ostopalvelunTyyppi;
	}

	/**
	 * @param ostopalvelunTyyppi the ostopalvelunTyyppi to set
	 */
	public void setOstopalvelunTyyppi(int ostopalvelunTyyppi) {
		this.ostopalvelunTyyppi = ostopalvelunTyyppi;
	}

	/**
	 * @return the valtuutuksenVoimassaoloAlku
	 */
	public Date getValtuutuksenVoimassaoloAlku() {
		return valtuutuksenVoimassaoloAlku;
	}

	/**
	 * @param valtuutuksenVoimassaoloAlku the valtuutuksenVoimassaoloAlku to set
	 */
	public void setValtuutuksenVoimassaoloAlku(Date valtuutuksenVoimassaoloAlku) {
		this.valtuutuksenVoimassaoloAlku = valtuutuksenVoimassaoloAlku;
	}

	/**
	 * @return the valtuutuksenVoimassaoloLoppu
	 */
	public Date getValtuutuksenVoimassaoloLoppu() {
		return valtuutuksenVoimassaoloLoppu;
	}

	/**
	 * @param valtuutuksenVoimassaoloLoppu the valtuutuksenVoimassaoloLoppu to set
	 */
	public void setValtuutuksenVoimassaoloLoppu(Date valtuutuksenVoimassaoloLoppu) {
		this.valtuutuksenVoimassaoloLoppu = valtuutuksenVoimassaoloLoppu;
	}

	public String getAsiakirjanRekisterinpitaja() {
		return asiakirjanRekisterinpitaja;
	}

	public void setAsiakirjanRekisterinpitaja(String asiakirjanRekisterinpitaja) {
		this.asiakirjanRekisterinpitaja = asiakirjanRekisterinpitaja;
	}

	public String getAsiakirjanRekisterinpitajaNimi() {
		return asiakirjanRekisterinpitajaNimi;
	}

	public void setAsiakirjanRekisterinpitajaNimi(String asiakirjanRekisterinpitajaNimi) {
		this.asiakirjanRekisterinpitajaNimi = asiakirjanRekisterinpitajaNimi;
	}

	/**
	 * @return the palvelunJarjestaja
	 */
	public String getPalvelunJarjestaja() {
		return palvelunJarjestaja;
	}

	/**
	 * @param palvelunJarjestaja the palvelunJarjestaja to set
	 */
	public void setPalvelunJarjestaja(String palvelunJarjestaja) {
		this.palvelunJarjestaja = palvelunJarjestaja;
	}

	/**
	 * @return the palvelunJarjestajaNimi
	 */
	public String getPalvelunJarjestajaNimi() {
		return palvelunJarjestajaNimi;
	}

	/**
	 * @param palvelunJarjestajaNimi the palvelunJarjestajaNimi to set
	 */
	public void setPalvelunJarjestajaNimi(String palvelunJarjestajaNimi) {
		this.palvelunJarjestajaNimi = palvelunJarjestajaNimi;
	}

	/**
	 * @return the palvelunJarjestajanPalveluyksikko
	 */
	public String getPalvelunJarjestajanPalveluyksikko() {
		return palvelunJarjestajanPalveluyksikko;
	}

	/**
	 * @param palvelunJarjestajanPalveluyksikko the palvelunJarjestajanPalveluyksikko to set
	 */
	public void setPalvelunJarjestajanPalveluyksikko(String palvelunJarjestajanPalveluyksikko) {
		this.palvelunJarjestajanPalveluyksikko = palvelunJarjestajanPalveluyksikko;
	}

	/**
	 * @return the palvelunJarjestajanPalveluyksikonNimi
	 */
	public String getPalvelunJarjestajanPalveluyksikonNimi() {
		return palvelunJarjestajanPalveluyksikonNimi;
	}

	/**
	 * @param palvelunJarjestajanPalveluyksikonNimi the palvelunJarjestajanPalveluyksikonNimi to set
	 */
	public void setPalvelunJarjestajanPalveluyksikonNimi(String palvelunJarjestajanPalveluyksikonNimi) {
		this.palvelunJarjestajanPalveluyksikonNimi = palvelunJarjestajanPalveluyksikonNimi;
	}

	/**
	 * @return the palvelunTuottaja
	 */
	public String getPalvelunTuottaja() {
		return palvelunTuottaja;
	}

	/**
	 * @param palvelunTuottaja the palvelunTuottaja to set
	 */
	public void setPalvelunTuottaja(String palvelunTuottaja) {
		this.palvelunTuottaja = palvelunTuottaja;
	}

	/**
	 * @return the palvelunTuottajanNimi
	 */
	public String getPalvelunTuottajanNimi() {
		return palvelunTuottajanNimi;
	}

	/**
	 * @param palvelunTuottajanNimi the palvelunTuottajanNimi to set
	 */
	public void setPalvelunTuottajanNimi(String palvelunTuottajanNimi) {
		this.palvelunTuottajanNimi = palvelunTuottajanNimi;
	}

	/**
	 * @return the hakuRekisterinpitaja
	 */
	public String getHakuRekisterinpitaja() {
		return hakuRekisterinpitaja;
	}

	/**
	 * @param hakuRekisterinpitaja the hakuRekisterinpitaja to set
	 */
	public void setHakuRekisterinpitaja(String hakuRekisterinpitaja) {
		this.hakuRekisterinpitaja = hakuRekisterinpitaja;
	}

	/**
	 * @return the hakuRekisteri
	 */
	public int getHakuRekisteri() {
		return hakuRekisteri;
	}

	/**
	 * @param hakuRekisteri the hakuRekisteri to set
	 */
	public void setHakuRekisteri(int hakuRekisteri) {
		this.hakuRekisteri = hakuRekisteri;
	}

	/**
	 * @return the hakuRekisterinTarkenne
	 */
	public String getHakuRekisterinTarkenne() {
		return hakuRekisterinTarkenne;
	}

	/**
	 * @param hakuRekisterinTarkenne the hakuRekisterinTarkenne to set
	 */
	public void setHakuRekisterinTarkenne(String hakuRekisterinTarkenne) {
		this.hakuRekisterinTarkenne = hakuRekisterinTarkenne;
	}

	/**
	 * @return the hakuRekisterinTarkentimenNimi
	 */
	public String getHakuRekisterinTarkentimenNimi() {
		return hakuRekisterinTarkentimenNimi;
	}

	/**
	 * @param hakuRekisterinTarkentimenNimi the hakuRekisterinTarkentimenNimi to set
	 */
	public void setHakuRekisterinTarkentimenNimi(String hakuRekisterinTarkentimenNimi) {
		this.hakuRekisterinTarkentimenNimi = hakuRekisterinTarkentimenNimi;
	}

	/**
	 * @return the tallennusRekisterinpitaja
	 */
	public String getTallennusRekisterinpitaja() {
		return tallennusRekisterinpitaja;
	}

	/**
	 * @param tallennusRekisterinpitaja the tallennusRekisterinpitaja to set
	 */
	public void setTallennusRekisterinpitaja(String tallennusRekisterinpitaja) {
		this.tallennusRekisterinpitaja = tallennusRekisterinpitaja;
	}

	/**
	 * @return the tallennusRekisteri
	 */
	public int getTallennusRekisteri() {
		return tallennusRekisteri;
	}

	/**
	 * @param tallennusRekisteri the tallennusRekisteri to set
	 */
	public void setTallennusRekisteri(int tallennusRekisteri) {
		this.tallennusRekisteri = tallennusRekisteri;
	}

	/**
	 * @return the tallennusRekisterinTarkenne
	 */
	public String getTallennusRekisterinTarkenne() {
		return tallennusRekisterinTarkenne;
	}

	/**
	 * @param tallennusRekisterinTarkenne the tallennusRekisterinTarkenne to set
	 */
	public void setTallennusRekisterinTarkenne(String tallennusRekisterinTarkenne) {
		this.tallennusRekisterinTarkenne = tallennusRekisterinTarkenne;
	}

	/**
	 * @return the tallennusRekisterinTarkentimenNimi
	 */
	public String getTallennusRekisterinTarkentimenNimi() {
		return tallennusRekisterinTarkentimenNimi;
	}

	/**
	 * @param tallennusRekisterinTarkentimenNimi the tallennusRekisterinTarkentimenNimi to set
	 */
	public void setTallennusRekisterinTarkentimenNimi(String tallennusRekisterinTarkentimenNimi) {
		this.tallennusRekisterinTarkentimenNimi = tallennusRekisterinTarkentimenNimi;
	}

	/**
	 * @return the kaikkiAsiakirjat
	 */
	public boolean isKaikkiAsiakirjat() {
		return kaikkiAsiakirjat;
	}

	/**
	 * @param kaikkiAsiakirjat the kaikkiAsiakirjat to set
	 */
	public void setKaikkiAsiakirjat(boolean kaikkiAsiakirjat) {
		this.kaikkiAsiakirjat = kaikkiAsiakirjat;
	}

	/**
	 * @return the asiakirjanTallentaja
	 */
	public HenkilotiedotTO getAsiakirjanTallentaja() {
		return asiakirjanTallentaja;
	}

	/**
	 * @param asiakirjanTallentaja the asiakirjanTallentaja to set
	 */
	public void setAsiakirjanTallentaja(HenkilotiedotTO asiakirjanTallentaja) {
		this.asiakirjanTallentaja = asiakirjanTallentaja;
	}

	/**
	 * @return the ammattihenkilonPalveluyksikko
	 */
	public String getAmmattihenkilonPalveluyksikko() {
		return ammattihenkilonPalveluyksikko;
	}

	/**
	 * @param ammattihenkilonPalveluyksikko the ammattihenkilonPalveluyksikko to set
	 */
	public void setAmmattihenkilonPalveluyksikko(String ammattihenkilonPalveluyksikko) {
		this.ammattihenkilonPalveluyksikko = ammattihenkilonPalveluyksikko;
	}

	/**
	 * @return the ammattihenkilonPalveluyksikonNimi
	 */
	public String getAmmattihenkilonPalveluyksikonNimi() {
		return ammattihenkilonPalveluyksikonNimi;
	}

	/**
	 * @param ammattihenkilonPalveluyksikonNimi the ammattihenkilonPalveluyksikonNimi to set
	 */
	public void setAmmattihenkilonPalveluyksikonNimi(String ammattihenkilonPalveluyksikonNimi) {
		this.ammattihenkilonPalveluyksikonNimi = ammattihenkilonPalveluyksikonNimi;
	}

	public String getAmmattihenkilonKatsoTunnus() {
		return ammattihenkilonKatsoTunnus;
	}

	public void setAmmattihenkilonKatsoTunnus(String ammattihenkilonKatsoTunnus) {
		this.ammattihenkilonKatsoTunnus = ammattihenkilonKatsoTunnus;
	}

	/**
	 * @return the luovutettavanAineistonAlku
	 */
	public Date getLuovutettavanAineistonAlku() {
		return luovutettavanAineistonAlku;
	}

	/**
	 * @param luovutettavanAineistonAlku the luovutettavanAineistonAlku to set
	 */
	public void setLuovutettavanAineistonAlku(Date luovutettavanAineistonAlku) {
		this.luovutettavanAineistonAlku = luovutettavanAineistonAlku;
	}

	/**
	 * @return the luovutettavanAineistonLoppu
	 */
	public Date getLuovutettavanAineistonLoppu() {
		return luovutettavanAineistonLoppu;
	}

	/**
	 * @param luovutettavanAineistonLoppu the luovutettavanAineistonLoppu to set
	 */
	public void setLuovutettavanAineistonLoppu(Date luovutettavanAineistonLoppu) {
		this.luovutettavanAineistonLoppu = luovutettavanAineistonLoppu;
	}

	/**
	 * @return the luovutettavatPalvelutapahtumat
	 */
	public List<String> getLuovutettavatPalvelutapahtumat() {
		return luovutettavatPalvelutapahtumat;
	}

	/**
	 * @param luovutettavatPalvelutapahtumat the luovutettavatPalvelutapahtumat to set
	 */
	public void setLuovutettavatPalvelutapahtumat(List<String> luovutettavatPalvelutapahtumat) {
		this.luovutettavatPalvelutapahtumat = luovutettavatPalvelutapahtumat;
	}
}
