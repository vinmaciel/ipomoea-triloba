package br.usp.pcs.securetcg.library.interfaces;

import br.usp.pcs.securetcg.library.ecash.model.Coin;

/**
 * Interface used to generate card (cash) related info, like specified signature and
 * serial id.
 * 
 * @author mmaciel
 *
 */
public abstract class ICard extends Coin {
	
	public ICard(Coin coin) {
		this.setSerial(coin.getSerial());
		this.setProperties(coin.getProperties());
		this.setHistory(coin.getHistory());
	}
	
	private long id;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
}
