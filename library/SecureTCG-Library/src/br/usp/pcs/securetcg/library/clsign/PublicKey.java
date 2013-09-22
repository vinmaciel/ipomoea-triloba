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
	private byte[][] r;
	private byte[] s;
	private byte[] z;
	
	public byte[] getN() {
		return n;
	}
	public void setN(byte[] n) {
		this.n = n;
	}

	public byte[][] getR() {
		return r;
	}
	public void setR(byte[][] r) {
		this.r = r;
	}
	public byte[] getR(int index) {
		return r[index];
	}
	public void setR(byte[] r, int index) {
		this.r[index] = r;
	}
	public int getRSize() {
		return r.length;
	}
	public void allocR(int size) {
		r = new byte[size][];
	}
	
	public byte[] getS() {
		return s;
	}
	public void setS(byte[] s) {
		this.s = s;
	}
	
	public byte[] getZ() {
		return z;
	}
	public void setZ(byte[] z) {
		this.z = z;
	}
	
	@Override
	public String toString() {
		String string = "";
		
		string += "n=" + new BigInteger(this.n);
		for(int i = 0; i < r.length; i++)
			string += "\nr[" + i + "]=" + new BigInteger(this.r[i]);
		string += "\ns=" + new BigInteger(this.s);
		string += "\nz=" + new BigInteger(this.z);
		
		return string;
	}
	
}
