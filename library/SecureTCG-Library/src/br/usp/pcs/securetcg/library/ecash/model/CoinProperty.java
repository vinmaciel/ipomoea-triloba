package br.usp.pcs.securetcg.library.ecash.model;

public class CoinProperty {

	private byte[] tag;
	private byte[] r;
	private byte[] info;
	private byte[] hash;
	
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
	
	public byte[] getInfo() {
		return info;
	}
	public void setInfo(byte[] info) {
		this.info = info;
	}
	
	public byte[] getHash() {
		return hash;
	}
	public void setHash(byte[] hash) {
		this.hash = hash;
	}
	
}
