package br.usp.pcs.securetcg.library.ecash;

import java.math.BigInteger;

/**
 * Singleton to handle the global parameters of the system.
 * 
 * @author mmaciel
 *
 */
public final class SystemParameter {

	/* Singleton */
	private SystemParameter() {}
	private static SystemParameter instance = null;
	
	public static SystemParameter get() {
		if(instance == null) instance = new SystemParameter();
		return instance;
	}
	
	/** Key size */
	//TODO turn into final
	private int k;
	/** Bank chosen modulus */
	private BigInteger n;
	/** Generator of the commitment for user key generator */
	//FIXME is G really global???
	private BigInteger g;

	public int getK() {
		return k;
	}
	public void setK(int k) {
		this.k = k;
	}
	
	public BigInteger getN() {
		return n;
	}
	public void setN(BigInteger n) {
		this.n = n;
	}
	
	public BigInteger getG() {
		return g;
	}
	public void setG(BigInteger g) {
		this.g = g;
	}
	
}
