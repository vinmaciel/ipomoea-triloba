package br.usp.pcs.securetcg.library.clsign;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * POJO to represent a public key in the Camenisch and Lysyanskaya scheme.
 * 
 * @author Vinicius
 *
 */
public class PublicKey implements Serializable {
	
	public PublicKey() {}
	
	private byte[] n;
	private byte[] a;
	private byte[] b;
	private byte[] c;
	
	public byte[] getN() {
		return n;
	}
	public void setN(byte[] n) {
		this.n = n;
	}
	
	public byte[] getA() {
		return a;
	}
	public void setA(byte[] a) {
		this.a = a;
	}
	
	public byte[] getB() {
		return b;
	}
	public void setB(byte[] b) {
		this.b = b;
	}
	
	public byte[] getC() {
		return c;
	}
	public void setC(byte[] c) {
		this.c = c;
	}
	
	@Override
	public String toString() {
		return	"n=" + new BigInteger(this.n) + 
				"\na=" + new BigInteger(this.a) + 
				"\nb=" + new BigInteger(this.b) + 
				"\nc=" + new BigInteger(this.c);
	}
	
}
