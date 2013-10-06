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
	
	/* Lazy singleton instantiation */
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
	
	/* Pedersen commitment */
	/** Pedersen commitment: Order of group G */
	private int pedOrder;
	/** Pedersen commitment: Generator set from G */
	private byte[][] pedGenerator;

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

	/** Pedersen commitment: GET order of group G */
	public int getPedOrder() {
		return pedOrder;
	}
	/** Pedersen commitment: SET order of group G */
	public void setPedOrder(int pedOrder) {
		this.pedOrder = pedOrder;
	}
	/** Pedersen commitment: GET size of generator set from G */
	public int getPedGeneratorLength() {
		return this.pedGenerator.length;
	}
	/** Pedersen commitment: GET generator set from G */
	public byte[][] getPedGenerator() {
		return pedGenerator;
	}
	/** Pedersen commitment: SET generator set from G */
	public void setPedGenerator(byte[][] pedGenerator) {
		this.pedGenerator = pedGenerator;
	}
	/** Pedersen commitment: GET generator in index from G */
	public byte[] getPedGenerator(int index) {
		return pedGenerator[index];
	}
	/** Pedersen commitment: SET generator in index from G */
	public void setPedGenerator(byte[] pedGenerator, int index) {
		this.pedGenerator[index] = pedGenerator;
	}
	
}
