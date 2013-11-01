package br.usp.pcs.securetcg.library.ecash;


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
	
	
	/** Security parameter */
	//TODO must be final
	private int k;
	
	/** Order of prime group G of generators */
	private byte[] p;
	
	/** Generators of G */
	private byte[][] g;
	
	/** Special generator of G */
	private byte[] h;
	
	public int getK() {
		return k;
	}
	public void setK(int k) {
		this.k = k;
	}
	
	public byte[] getP() {
		byte[] p = new byte[this.p.length];
		for(int i = 0; i < p.length; i++)
			p[i] = this.p[i];
		return p;
	}
	public void setP(byte[] p) {
		this.p = new byte[p.length];
		for(int i = 0; i < this.p.length; i++)
			this.p[i] = p[i];
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
	public void setG(byte[][] g) {
		this.g = new byte[g.length][];
		for(int i = 0; i < this.g.length; i++) {
			this.g[i] = new byte[g[i].length];
			for(int j = 0; j < this.g[i].length; j++)
				this.g[i][j] = g[i][j];
		}
	}
	public byte[] getG(int index) {
		byte[] g = new byte[this.g[index].length];
		for(int i = 0; i < g.length; i++)
			g[i] = this.g[index][i];
		return g;
	}
	public void setG(byte[] g, int index) {
		this.g[index] = new byte[g.length];
		for(int i = 0; i < this.g[index].length; i++)
			this.g[index][i] = g[i];
	}
	public int getGSize() {
		return g.length;
	}
	public void allocG(int length) {
		this.g = new byte[length][];
	}
	
	public byte[] getH() {
		byte[] h = new byte[this.h.length];
		for(int i = 0; i < h.length; i++)
			h[i] = this.h[i];
		return h;
	}
	public void setH(byte[] h) {
		this.h = new byte[h.length];
		for(int i = 0; i < this.h.length; i++)
			this.h[i] = h[i];
	}
	
	
	
}
