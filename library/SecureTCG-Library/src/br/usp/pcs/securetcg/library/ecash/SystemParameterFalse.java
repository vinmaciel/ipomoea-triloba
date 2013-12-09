package br.usp.pcs.securetcg.library.ecash;

import java.math.BigInteger;

import br.usp.pcs.securetcg.library.config.SecurityConstantsFalse;


/**
 * Singleton to handle the global parameters of the system.
 * 
 * @author augusto
 *
 */
public final class SystemParameterFalse {

	/* Singleton */
	private SystemParameterFalse() {
		 this.n = new BigInteger(SecurityConstantsFalse.n).toByteArray();
		 this.p = new BigInteger(SecurityConstantsFalse.q).toByteArray();
		 this.g = new byte[7][];
		 for(int i = 0; i < g.length; i++)
			 this.g[i] = new BigInteger(SecurityConstantsFalse.r[i]).toByteArray();
		 this.h = new BigInteger(SecurityConstantsFalse.s).toByteArray();
		 this.z = new BigInteger(SecurityConstantsFalse.z).toByteArray();
	}
	private static SystemParameterFalse instance = null;
	
	/* Lazy singleton instantiation */
	public static SystemParameterFalse get() {
		if(instance == null) instance = new SystemParameterFalse();
		return instance;
	}
	
	
	/** Security parameter k */
	private final int k = 1024;
	
	/**
	 * Special RSA modulus n. <br/>
	 * n = pq, where p and q are both safe primes (i.e. x = 2*x'+1 is safe prime if x' is prime). <br/>
	 * length(n) ~ 2*k <br/
	 * Used in PedCom and CLSign
	 */
	private final byte[] n;
	
	/** 
	 * Prime p order group G of generators <br>
	 * Used to produce the public generators g<sub>i</sub>, and other (pseudo)random operations.
	 */
	private final byte[] p;
	
	/** Generators of G */
	private final byte[][] g;
	
	/** Special generator of G */
	private final byte[] h;
	
	/** Constant generator of G **/
	private final byte[] z;
	
	
	public int getK() {
		return k;
	}
	
	public byte[] getN() {
		byte[] n = new byte[this.n.length];
		for(int i = 0; i < n.length; i++)
			n[i] = this.n[i];
		return n;
	}
	
	public byte[] getP() {
		byte[] p = new byte[this.p.length];
		for(int i = 0; i < p.length; i++)
			p[i] = this.p[i];
		return p;
	}
	
	public byte[][] getG() {
		byte[][] g = new byte[this.g.length][];
		for(int i = 0; i < g.length; i++) {
			g[i] = new byte[this.g[i].length];
			for(int j = 0; j < g[i].length; j++)
				g[i][j] = this.g[i][j];
		}
		return g;
	}
	public byte[] getG(int index) {
		byte[] g = new byte[this.g[index].length];
		for(int i = 0; i < g.length; i++)
			g[i] = this.g[index][i];
		return g;
	}
	public int getGSize() {
		return this.g.length;
	}
	
	public byte[] getH() {
		byte[] h = new byte[this.h.length];
		for(int i = 0; i < h.length; i++)
			h[i] = this.h[i];
		return h;
	}
	
	public byte[] getZ() {
		byte[] z = new byte[this.z.length];
		for(int i = 0; i < z.length; i++)
			z[i] = this.z[i];
		return z;
	}
	
}
