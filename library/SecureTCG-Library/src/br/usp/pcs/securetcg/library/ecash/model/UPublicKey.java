package br.usp.pcs.securetcg.library.ecash.model;

import java.io.Serializable;

public class UPublicKey implements Serializable {

	public UPublicKey() {}
	
	private byte[] gu;
	
	public byte[] getGu() {
		return gu;
	}
	public void setGu(byte[] gu) {
		this.gu = gu;
	}
	
}
