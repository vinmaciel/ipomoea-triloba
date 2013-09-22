package br.usp.pcs.securetcg.library.clsign;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * POJO to represent the signature of a message in the Camenisch and Lysyanskaya scheme.
 * 
 * @author Vinicius
 *
 */
public class Signature implements Serializable {

	private byte[] a;
	private byte[] e;
	private byte[] v;
	
	public byte[] getA() {
		return a;
	}
	public void setA(byte[] a) {
		this.a = a;
	}
	
	public byte[] getE() {
		return e;
	}
	public void setE(byte[] e) {
		this.e = e;
	}
	
	public byte[] getV() {
		return v;
	}
	public void setV(byte[] v) {
		this.v = v;
	}
	
	@Override
	public String toString() {
		return	"a=" + new BigInteger(this.a) + 
				"\ne=" + new BigInteger(this.e) + 
				"\nv=" + new BigInteger(this.v);
	}
	
}
