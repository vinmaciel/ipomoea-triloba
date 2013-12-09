package br.usp.pcs.securetcg.library.communication.json;


public class TradeRequestJson {

	private byte[] pku;
	private long cardID;
	private String cardJson;
	
	public TradeRequestJson() {}
	
	public byte[] getPku() {
		return pku;
	}
	public void setPku(byte[] pku) {
		this.pku = pku;
	}
	
	public long getCardID() {
		return cardID;
	}
	public void setCardID(long cardID) {
		this.cardID = cardID;
	}

	public String getCardJson() {
		return cardJson;
	}
	public void setCardJson(String cardJson) {
		this.cardJson = cardJson;
	}
	
}
