package br.usp.pcs.securetcg.library.pedcom;

import java.io.Serializable;

public class PedCommitment implements Serializable {

	public PedCommitment() {}
	
	/** Commitment */
	private byte[] e;
	/** Random value */
	private byte[] t;
	
	public byte[] getE() {
		return e;
	}
	public void setE(byte[] e) {
		this.e = e;
	}
	
	public byte[] getT() {
		return t;
	}
	public void setT(byte[] t) {
		this.t = t;
	}
	
	
}
