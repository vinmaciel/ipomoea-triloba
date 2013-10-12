package br.usp.pcs.securetcg.library.pedcom;

import java.io.Serializable;
import java.math.BigInteger;

public class PedPublicKey implements Serializable {

	public PedPublicKey() {}
	
	/** RSA modulus */
	private byte[] n;
	/** Quadratic residue in n */
	private byte[] h;
	/** Generators in h */
	private byte[][] g;
	
	public byte[] getN() {
		return n;
	}
	public void setN(byte[] n) {
		this.n = n;
	}
	
	public byte[][] getG() {
		return g;
	}
	public void setG(byte[][] g) {
		this.g = g;
	}
	public byte[] getG(int index) {
		return g[index];
	}
	public void setG(byte[] g, int index) {
		this.g[index] = g;
	}
	public int getGSize() {
		return g.length;
	}
	public void allocG(int size) {
		g = new byte[size][];
	}
	
	public byte[] getH() {
		return h;
	}
	public void setH(byte[] h) {
		this.h = h;
	}
	
	@Override
	public String toString() {
		String string = "";
		
		string += "n=" + new BigInteger(this.n);
		for(int i = 0; i < g.length; i++)
			string += "\ng[" + i + "]=" + new BigInteger(this.g[i]);
		string += "\nh=" + new BigInteger(this.h);
		
		return string;
		
	}
}
