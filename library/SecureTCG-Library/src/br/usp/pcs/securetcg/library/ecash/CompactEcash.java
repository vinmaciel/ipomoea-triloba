package br.usp.pcs.securetcg.library.ecash;

import java.math.BigInteger;

import br.usp.pcs.securetcg.library.clsign.CLPrivateKey;
import br.usp.pcs.securetcg.library.clsign.CLPublicKey;
import br.usp.pcs.securetcg.library.clsign.CLSign;
import br.usp.pcs.securetcg.library.ecash.key.UPrivateKey;
import br.usp.pcs.securetcg.library.ecash.key.UPublicKey;
import br.usp.pcs.securetcg.library.utils.Prime;

public class CompactEcash {
	
	/**
	 * Outputs the parameter of the system.
	 * 
	 * @param k
	 * @return all security parameters.
	 */
	public static SystemParameter ParamGen(long k) {
		return null;
	}
	
	/**
	 * Generates the Bank key-pair.
	 * 
	 * @param pkb bank public key.
	 * @param skb bank private key.
	 */
	public static void BKeyGen(CLPublicKey pkb, CLPrivateKey skb) {
		SystemParameter par = SystemParameter.get();
		//TODO fix residue size
		CLSign.generateKeyPair(2*par.getK(), 3, pkb, skb);
		par.setN(new BigInteger(pkb.getN()));
	}
	
	/**
	 * Generates the User key-pair.
	 * 
	 * @param par all security parameters.
	 * @param pku user public key.
	 * @param sku user private key.
	 */
	public static void UKeyGen(UPublicKey pku, UPrivateKey sku) {
		SystemParameter par = SystemParameter.get();
		
		if(pku == null) pku = new UPublicKey();
		if(sku == null) sku = new UPrivateKey();
		
		BigInteger urand = Prime.getPrime(par.getK()); 
		sku.setU(urand.toByteArray());
		pku.setGu(par.getG().modPow(urand, par.getN()).toByteArray());
	}
	
	public static boolean Withdraw_UserSide() {
		//TODO identifies itself to the bank (proving knowledge of its secret key(?)
		
		//TODO contribution to randomness
		//selects s' and t from Zq
		//commits A' = PedCom(sku, s', t, r)		(what is r?)
		//sends A' to the bank
		//awaits random r'
		//sets s = s' + r'
		//sets A = g<bold>^r' * A' = PedCom(sku, s'+r', t, r) = PedCom(sku, s, t, r)
		
		//TODO runs CL protocol(?)
		//gets signature(sku, s, t)					(where is r?)
		
		//TODO gets wallet W = (sku, s, t, sig(sku, s, t), J)				(don't need J)
		return false;
	}
	
	public static boolean Withdraw_BankSide() {
		//TODO awaits user identification
		
		//TODO contribution to randomness
		//awaits commitment A' from user
		//sends a random r' to the user
		//sets A = g<bold>^r' * A' = PedCom(sku, s'+r', t, r) = PedCom(sku, s, t, r)
		
		//TODO runs CL protocol(?)
		
		//TODO debits
		return false;
	}
	
	public static boolean Spend() {
		return false;
	}
	
	public static boolean Deposit() {
		return false;
	}
	
	public static boolean Identify() {
		return false;
	}
	
	public static boolean VerifyGuilt() {
		return false;
	}
}
