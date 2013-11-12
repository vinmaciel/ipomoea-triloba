package br.usp.pcs.securetcg.library.interfaces;

/**
 * Interface used to generate player (owner) related info, like specified key pairs.
 * 
 * @author mmaciel
 *
 */
public abstract class IPlayer {
	
	private byte[] publicKey;
	
	
	public byte[] getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}
	
}
