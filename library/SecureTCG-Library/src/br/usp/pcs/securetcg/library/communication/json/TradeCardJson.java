package br.usp.pcs.securetcg.library.communication.json;

import java.util.List;

public class TradeCardJson {

	private long cardID;
	private byte[] serial;
	private List<byte[]> history;
	private TradeCardPropertyJson[] properties;
	
	public TradeCardJson() {}
	
	public long getCardID() {
		return cardID;
	}
	public void setCardID(long cardID) {
		this.cardID = cardID;
	}
	
	public byte[] getSerial() {
		return serial;
	}
	public void setSerial(byte[] serial) {
		this.serial = serial;
	}
	
	public List<byte[]> getHistory() {
		return history;
	}
	public void setHistory(List<byte[]> history) {
		this.history = history;
	}
	
	public TradeCardPropertyJson[] getProperties() {
		return properties;
	}
	public void setProperties(TradeCardPropertyJson[] properties) {
		this.properties = properties;
	}
	
}
