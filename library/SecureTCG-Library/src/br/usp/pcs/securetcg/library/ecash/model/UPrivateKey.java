package br.usp.pcs.securetcg.library.ecash.model;

import java.io.Serializable;

public class UPrivateKey implements Serializable {

	public UPrivateKey() {}
	
	private byte[] u;
	
	public byte[] getU() {
		return u;
	}
	public void setU(byte[] u) {
		this.u = u;
	}
	
}
