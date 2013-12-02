package br.usp.pcs.securetcg.library.communication.json;

public class TradeRequestJson {

	private byte[] pku;
	private long cardID;
	
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
	
}
