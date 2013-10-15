package br.usp.pcs.securetcg.library.ecash.model;

import java.io.Serializable;
import java.math.BigInteger;

public class Wallet implements Serializable {

	public Wallet() {}
	
	/** Mixed (user-bank) serial instance */
	private byte[] s;
	/** User anonymity component */
	private byte[] t;
	/** Random commitment value */
	private byte[] x;
	/** Fixed serial class */
	private byte[] q;
	/** Wallet size */
	private byte[] j;
	/** signature's A of the parameters */
	private byte[] sigA;
	/** Signature's E of the parameters */
	private byte[] sigE;
	/** Signature's V of the parameters */
	private byte[] sigV;
	
	
	public byte[] getS() {
		byte[] s = new byte[this.s.length];
		for(int i = 0; i < s.length; i++)
			s[i] = this.s[i];
		return s;
	}
	public void setS(byte[] s) {
		this.s = new byte[s.length];
		for(int i = 0; i < this.s.length; i++)
			this.s[i] = s[i];
	}
	
	public byte[] getT() {
		byte[] t = new byte[this.t.length];
		for(int i = 0; i < t.length; i++)
			t[i] = this.t[i];
		return t;
	}
	public void setT(byte[] t) {
		this.t = new byte[t.length];
		for(int i = 0; i < this.t.length; i++)
			this.t[i] = t[i];
	}
	
	public byte[] getX() {
		byte[] x = new byte[this.x.length];
		for(int i = 0; i < x.length; i++)
			x[i] = this.x[i];
		return x;
	}
	public void setX(byte[] x) {
		this.x = new byte[x.length];
		for(int i = 0; i < this.x.length; i++)
			this.x[i] = x[i];
	}
	
	public byte[] getQ() {
		byte[] q = new byte[this.q.length];
		for(int i = 0; i < q.length; i++)
			q[i] = this.q[i];
		return q;
	}
	public void setQ(byte[] q) {
		this.q = new byte[q.length];
		for(int i = 0; i < this.q.length; i++)
			this.q[i] = q[i];
	}
	
	public byte[] getJ() {
		byte[] j = new byte[this.j.length];
		for(int i = 0; i < j.length; i++)
			j[i] = this.j[i];
		return j;
	}
	public void setJ(byte[] j) {
		this.j = new byte[j.length];
		for(int i = 0; i < this.j.length; i++)
			this.j[i] = j[i];
	}
	
	public byte[] getSigA() {
		byte[] sigA = new byte[this.sigA.length];
		for(int i = 0; i < sigA.length; i++)
			sigA[i] = this.sigA[i];
		return sigA;
	}
	public void setSigA(byte[] sigA) {
		this.sigA = new byte[sigA.length];
		for(int i = 0; i < this.sigA.length; i++)
			this.sigA[i] = sigA[i];
	}
	
	public byte[] getSigV() {
		byte[] sigV = new byte[this.sigV.length];
		for(int i = 0; i < sigV.length; i++)
			sigV[i] = this.sigV[i];
		return sigV;
	}
	public void setSigV(byte[] sigV) {
		this.sigV = new byte[sigV.length];
		for(int i = 0; i < this.sigV.length; i++)
			this.sigV[i] = sigV[i];
	}
	
	public byte[] getSigE() {
		byte[] sigE = new byte[this.sigE.length];
		for(int i = 0; i < sigE.length; i++)
			sigE[i] = this.sigE[i];
		return sigE;
	}
	public void setSigE(byte[] sigE) {
		this.sigE = new byte[sigE.length];
		for(int i = 0; i < this.sigE.length; i++)
			this.sigE[i] = sigE[i];
	}
	
	@Override
	public String toString() {
		return		"s=" + new BigInteger(this.s) + 
					"\nQ=" + new BigInteger(this.q) +
					"\nJ=" + new BigInteger(this.j) +
					"\nt=" + new BigInteger(this.t) +
					"\nx=" + new BigInteger(this.x) +
					"\nsig:(A=" + new BigInteger(this.sigA) +
					"\tv=" + new BigInteger(this.sigV) +
					"\ne=" + new BigInteger(this.sigE) +
					")";
	}
	
	
}
