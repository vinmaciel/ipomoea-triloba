package br.usp.pcs.securetcg.library.ecash.model;

import java.util.ArrayList;
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

	public List<?> getProperties() {
		return properties;
	}
	@SuppressWarnings("unchecked")
	public void setProperties(List<?> properties) {
		this.properties = (List<CoinProperty>) properties;
	}
	public void addProperty(CoinProperty property) {
		if(this.properties == null)
			properties = new ArrayList<CoinProperty>();
		this.properties.add(property);
	}
	
	public List<byte[]> getHistory() {
		return history;
	}
	public void setHistory(List<byte[]> history) {
		this.history = history;
	}
	public void addEventToHistory(byte[] event) {
		if(this.history == null)
			this.history = new ArrayList<byte[]>();
		this.history.add(event);
	}
	
}
