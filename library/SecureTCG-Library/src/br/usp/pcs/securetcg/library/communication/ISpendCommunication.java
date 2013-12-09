package br.usp.pcs.securetcg.library.communication;

import br.usp.pcs.securetcg.library.ecash.model.Coin;
import br.usp.pcs.securetcg.library.ecash.model.UPublicKey;

public interface ISpendCommunication {

	
	public abstract String[] spend_request(UPublicKey pku, Coin coin, long cardID);
	
	public abstract String[] spend_resolve(Coin coin, long cardID);
}
