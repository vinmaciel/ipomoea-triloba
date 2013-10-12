package br.usp.pcs.securetcg.library.clsign;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * POJO to represent a private (secrete) key in the Camenisch and Lysyanskaya scheme.
 * 
 * @author Vinicius
 *
 */
public class CLPrivateKey implements Serializable {
	
	public CLPrivateKey() {}
	
	/** Component of special RSA modulus */
	private byte[] p;

	public byte[] getP() {
		return p;
	}
	public void setP(byte[] p) {
		this.p = p;
	}
	
	@Override
	public String toString() {
		return "p=" + new BigInteger(this.p);
	}
	
}
