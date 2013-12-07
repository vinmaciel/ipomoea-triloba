package br.usp.pcs.securetcg.library.communication;

import java.math.BigInteger;

import br.usp.pcs.securetcg.library.clsign.CLSignature;

public interface IWithdrawCommunication {

	/**
	 * Requests a wallet from the bank, sending its parameters and random values for the Proof of Knowledge.
	 * Must receive the following parameters (in order): <br/>
	 * <ul>
	 * <li>cardID (Q).
	 * <li>wallet size (J) - must be one (1).
	 * <li>partial wallet commitment (Cx).
	 * <li>user public keu (pku).
	 * <li>4 random values for Proof of Knowledge for Cx (u, s', t, x).
	 * <li>1 random value for Proof of Knowledge for user private key sku (u). 
	 * </ul>
	 * <br/>
	 * This method results in challenging knowledge of the parameters, receiving 5 random challenges to solve.
	 * 
	 * @param message set of representations of {@link BigInteger} to be sent (length=9).
	 * @return set of representations of {@link BigInteger} as challenges (length=2).
	 */
	public abstract String[] withdraw_request(String[] message);
	
	/**
	 * Sends the responses for each value to be proven at the Proof of Knowledge,
	 * receiving 5 values computed for each parameter to be proven Cx (u, s', t, x) and sku (u).
	 * <br/>
	 * This method results in an confirmation of the proof and, if <code>true</code>,
	 * the 3 parameters of a {@link CLSignature} (A, e, s").
	 * 
	 * @param message representation of {@link BigInteger} to be sent (length=5).
	 * @return representation of boolean as confirmation (length=1 or 4).
	 */
	public abstract String[] withdraw_solve(String[] message);
	
}
