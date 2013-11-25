package br.usp.pcs.securetcg.client.market;

import br.usp.pcs.securetcg.library.ecash.model.Wallet;

public interface WithdrawThreadCallback {

	public void onStart();
	
	public void onRequestFailed();
	
	public void onSolutionFailed();
	
	public void onWithdraw(Wallet wallet);
}
