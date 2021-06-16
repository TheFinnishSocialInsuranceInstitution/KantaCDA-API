package fi.kela.kanta.to;

public class JaksonPituusTO {

	private String yksikko;
	private Integer low;
	private Integer high;
	private Integer vakio;

	public JaksonPituusTO() {
		super();
	}

	public String getYksikko() {
		return yksikko;
	}

	public void setYksikko(String yksikko) {
		this.yksikko = yksikko;
	}

	public Integer getLow() {
		return low;
	}

	public void setLow(Integer low) {
		this.low = low;
	}

	public Integer getHigh() {
		return high;
	}

	public void setHigh(Integer high) {
		this.high = high;
	}

	public Integer getVakio() {
		return vakio;
	}

	public void setVakio(Integer vakio) {
		this.vakio = vakio;
	}

}