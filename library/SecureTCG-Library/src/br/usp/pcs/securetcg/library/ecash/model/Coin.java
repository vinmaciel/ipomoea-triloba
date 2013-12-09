package br.usp.pcs.securetcg.library.ecash.model;

import java.util.ArrayList;
import java.util.List;

public class Coin {

	public Coin() {}
	
	private byte[] serial;
	
	//hidden
	private List<CoinProperty> coinProperties;
	
	private List<byte[]> history;

	public byte[] getSerial() {
		return serial;
	}
	public void setSerial(byte[] serial) {
		this.serial = serial;
	}

	public List<?> getProperties() {
		return coinProperties;
	}
	@SuppressWarnings("unchecked")
	public void setProperties(List<?> properties) {
		this.coinProperties = (List<CoinProperty>) properties;
	}
	public void addProperty(CoinProperty property) {
		if(this.coinProperties == null)
			coinProperties = new ArrayList<CoinProperty>();
		this.coinProperties.add(property);
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
