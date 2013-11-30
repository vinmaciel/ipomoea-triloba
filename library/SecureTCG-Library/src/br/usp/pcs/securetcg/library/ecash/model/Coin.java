package br.usp.pcs.securetcg.library.ecash.model;

import java.util.List;

public class Coin {

	public Coin() {}
	
	private byte[] serial;
	
	private List<CoinProperty> properties;
	
	private List<byte[]> history;

	public byte[] getSerial() {
		return serial;
	}
	public void setSerial(byte[] serial) {
		this.serial = serial;
	}

	public List<CoinProperty> getProperties() {
		return properties;
	}
	public void setProperties(List<CoinProperty> properties) {
		this.properties = properties;
	}
	public void addProperty(CoinProperty property) {
		this.properties.add(property);
	}
	
	public List<byte[]> getHistory() {
		return history;
	}
	public void setHistory(List<byte[]> history) {
		this.history = history;
	}
	public void addEventToHistory(byte[] event) {
		this.history.add(event);
	}
	
}
