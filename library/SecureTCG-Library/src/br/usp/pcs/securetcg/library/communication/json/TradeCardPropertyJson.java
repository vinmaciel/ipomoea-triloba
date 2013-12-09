package br.usp.pcs.securetcg.library.communication.json;

public class TradeCardPropertyJson {

	private byte[] tag;
	private byte[] r;
	private byte[] hash;
	private byte[] info;
	
	public TradeCardPropertyJson() {}
	
	public byte[] getTag() {
		return tag;
	}
	public void setTag(byte[] tag) {
		this.tag = tag;
	}
	
	public byte[] getR() {
		return r;
	}
	public void setR(byte[] r) {
		this.r = r;
	}
	
	public byte[] getHash() {
		return hash;
	}
	public void setHash(byte[] hash) {
		this.hash = hash;
	}
	
	public byte[] getInfo() {
		return info;
	}
	public void setInfo(byte[] info) {
		this.info = info;
	}
	
}
