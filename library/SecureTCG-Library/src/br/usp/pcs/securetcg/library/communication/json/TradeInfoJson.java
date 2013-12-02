package br.usp.pcs.securetcg.library.communication.json;

public class TradeInfoJson {

	private byte[] pku;
	private String timestamp;
	private byte[] r;
	private byte[] info;
	
	public TradeInfoJson() {}
	
	public byte[] getPku() {
		return pku;
	}
	public void setPku(byte[] pku) {
		this.pku = pku;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public byte[] getR() {
		return r;
	}
	public void setR(byte[] r) {
		this.r = r;
	}
	
	public byte[] getInfo() {
		return info;
	}
	public void setInfo(byte[] info) {
		this.info = info;
	}
	
}
