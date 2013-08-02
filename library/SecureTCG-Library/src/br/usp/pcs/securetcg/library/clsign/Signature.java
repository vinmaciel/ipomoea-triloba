package br.usp.pcs.securetcg.library.clsign;

import java.io.Serializable;

/**
 * POJO to represent the signature of a message in the Camenisch and Lysyanskaya scheme.
 * 
 * @author Vinicius
 *
 */
public class Signature implements Serializable {
	
	private byte[] e;
	private byte[] s;
	private byte[] v;
	
	public byte[] getE() {
		return e;
	}
	public void setE(byte[] e) {
		this.e = e;
	}
	
	public byte[] getS() {
		return s;
	}
	public void setS(byte[] s) {
		this.s = s;
	}
	
	public byte[] getV() {
		return v;
	}
	public void setV(byte[] v) {
		this.v = v;
	}
	
	
}
