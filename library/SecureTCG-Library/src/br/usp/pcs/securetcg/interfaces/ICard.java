package br.usp.pcs.securetcg.interfaces;

/**
 * Interface used to generate card (cash) related info, like specified signature and
 * serial id.
 * 
 * @author mmaciel
 *
 */
public abstract class ICard {
	
	private long id;
	
	private byte[] signature;
	
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public byte[] getSignature() {
		return signature;
	}
	
	public void setSignature(byte[] signature) {
		this.signature = signature;
	}
	
}
