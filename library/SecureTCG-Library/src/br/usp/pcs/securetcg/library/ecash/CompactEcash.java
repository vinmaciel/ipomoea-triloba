package br.usp.pcs.securetcg.library.ecash;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import br.usp.pcs.securetcg.library.clsign.CLPrivateKey;
import br.usp.pcs.securetcg.library.clsign.CLPublicKey;
import br.usp.pcs.securetcg.library.clsign.CLSign;
import br.usp.pcs.securetcg.library.clsign.CLSignature;
import br.usp.pcs.securetcg.library.communication.ICommunication;
import br.usp.pcs.securetcg.library.ecash.model.Coin;
import br.usp.pcs.securetcg.library.ecash.model.CoinProperty;
import br.usp.pcs.securetcg.library.ecash.model.UPrivateKey;
import br.usp.pcs.securetcg.library.ecash.model.UPublicKey;
import br.usp.pcs.securetcg.library.ecash.model.Wallet;
import br.usp.pcs.securetcg.library.pedcom.PedCom;
import br.usp.pcs.securetcg.library.pedcom.PedCommitment;
import br.usp.pcs.securetcg.library.pedcom.PedPublicKey;
import br.usp.pcs.securetcg.library.utils.Prime;
import br.usp.pcs.securetcg.library.vrf.VRF;

/**
 * Class that implements the basics of the Compact E-Cash Scheme described by Camenisch, Hohenberger and Lysyanskaya.<br/>
 * 
 * @author vinmaciel
 *
 */
public class CompactEcash {

	protected enum Withdraw {
		PROOF_SETUP,
		PROOF_RESPONSE,
		MINT
	};
	
	public static final int WALLET_PARAM_SIZE = 5;
	
	/**
	 * Generates the public parameters of the system: the security parameter, the order of the generators, 
	 * the set of generators (six in base plus one special). <br/>
	 * The generators must be used as follow:
	 * <ul>
	 * <li> g<SUB>0</SUB>: user private key (sku)
	 * <li> g<SUB>1</SUB>: mixed (bank and user) wallet secret (s)
	 * <li> g<SUB>2</SUB>: user wallet secret (t)
	 * <li> g<SUB>3</SUB>: size of the wallet (J)
	 * <li> g<SUB>4</SUB>: random commitment value (x)
	 * </ul>
	 */
	public static void setup() {
		return;
	}
	
	/**
	 * Generates the Bank key-pair using unrestricted generators.
	 * 
	 * @param pkb bank public key.
	 * @param skb bank private key.
	 */
	@Deprecated
	public static void bKeyGen(CLPublicKey pkb, CLPrivateKey skb) {
		SystemParameter par = SystemParameter.get();
		CLSign.generateKeyPair(2*par.getK(), 7, pkb, skb);
		
		//May generate two key pairs (sign wallet and enroll user) - won't emit certificates
	}
	
	/**
	 * Generates the default Bank key-pair.
	 * This method doesn't generate the 
	 * 
	 * @param pkb bank public key.
	 * @param skb bank private key.
	 * @param path String that contains the path to the secure parameter stored in a sandbox.
	 */
	public static void bKeyGenDefault(CLPublicKey pkb, CLPrivateKey skb, String path) {
		SystemParameter par = SystemParameter.get();

		if(pkb == null) pkb = new CLPublicKey();
		if(skb == null) skb = new CLPrivateKey();
		
		pkb.setN(par.getN());
		pkb.setR(par.getG());
		pkb.setS(par.getH());
		pkb.setZ(par.getZ());
		
		byte[] key = null;
		try {
			byte[] buffer = new byte[par.getK()];
			InputStream in = new FileInputStream(path);
			int bytes = in.read(buffer);
			in.close();
			
			key = new byte[bytes];
			for(int i = 0; i < bytes; i++)
				key[i] = buffer[i];
		} catch(IOException e) {
			e.printStackTrace();
		}
		skb.setP(key);
	}
	
	/**
	 * Generates the User key-pair.
	 * 
	 * @param par all security parameters.
	 * @param pku user public key.
	 * @param sku user private key.
	 */
	public static void uKeyGen(UPublicKey pku, UPrivateKey sku) {
		SystemParameter par = SystemParameter.get();
		
		if(pku == null) pku = new UPublicKey();
		if(sku == null) sku = new UPrivateKey();
		
		BigInteger u;
		do {
			u = new BigInteger(par.getK(), Prime.random);
		} while(u.compareTo(new BigInteger(par.getP())) >= 0);
		sku.setU(u.toByteArray());
		//is this really P (or N)? it's P
		pku.setGu((new BigInteger(par.getG(0))).modPow(u, new BigInteger(par.getP())).toByteArray());
		
		//may need a certificate from B - won't be done
	}
	
	/**
	 * Generates a coin from the bank (client-side).
	 * 
	 * @param sku user private key.
	 * @param pku user public key.
	 * @param comm communication interface.
	 * @param cardID serial identification.
	 * 
	 * @return a new {@link Wallet} with the requested coins, or <code>null</code> in case of error.
	 */
	public static Wallet withdraw_UserSide(UPrivateKey sku, UPublicKey pku, ICommunication comm, BigInteger cardID) {
		SystemParameter par = SystemParameter.get();
		
		BigInteger n = new BigInteger(par.getN());
		BigInteger p = new BigInteger(par.getP());
		
		//selects mixed s' and user t randoms
		BigInteger	_s, t;
		do {
			_s = new BigInteger(par.getK(), Prime.random);
		} while(_s.compareTo(n) >= 0);
		do {
			t = new BigInteger(par.getK(), Prime.random);
		} while(t.compareTo(n) >= 0);

		//commits A' = PedCom(sku, s', t, x)
		PedPublicKey ck = new PedPublicKey();
		ck.setN(par.getN());
		ck.setG(new byte[][] {par.getG(0), par.getG(1), par.getG(2)});
		ck.setH(par.getG(4));
		PedCommitment _A = PedCom.commit(par.getK(), new byte[][] {sku.getU(), _s.toByteArray(), t.toByteArray()}, ck);
		
		//generates 4 random numbers to A' and 1 to sku
		BigInteger[]	rr = new BigInteger[WALLET_PARAM_SIZE],
						tr = new BigInteger[WALLET_PARAM_SIZE];
		for(int i = 0; i < tr.length; i++) {
			do {
				rr[i] = new BigInteger(par.getK(), Prime.random);
			} while(rr[i].compareTo(new BigInteger(par.getP())) >= 0);
		}
		//replicates the commitment to random values
		tr[0] = new BigInteger(par.getG(0))	.modPow(rr[0], n);
		tr[1] = new BigInteger(par.getG(1))	.modPow(rr[1], n);
		tr[2] = new BigInteger(par.getG(2))	.modPow(rr[2], n);
		tr[3] = new BigInteger(par.getG(4))	.modPow(rr[3], n);
		tr[4] = new BigInteger(par.getG(0))	.modPow(rr[4], p);
		
		//sends the commitment to the bank, awaits challenges
		String[] response = comm.withdraw_request(new String[] {	
											cardID.toString(),
											null, 
											new BigInteger(_A.getE()).toString(), 
											new BigInteger(pku.getGu()).toString(),
											tr[0].toString(),
											tr[1].toString(),
											tr[2].toString(),
											tr[3].toString(),	
											tr[4].toString()	});
		
		//parses challenge (2 challenges)
		BigInteger[] challenge_cmt = new BigInteger[response.length];
		for(int i = 0; i < challenge_cmt.length; i++)
			challenge_cmt[i] = new BigInteger( response[i] );
		
		//solves the PK
		BigInteger[] sr = new BigInteger[WALLET_PARAM_SIZE];
		sr[0] = rr[0]	.subtract(challenge_cmt[0]	.multiply( new BigInteger(sku.getU()) ));
		sr[1] = rr[1]	.subtract(challenge_cmt[0]	.multiply( _s ));
		sr[2] = rr[2]	.subtract(challenge_cmt[0]	.multiply( t ));
		sr[3] = rr[3]	.subtract(challenge_cmt[0]	.multiply( new BigInteger(_A.getT()).mod(n) ));
		sr[4] = rr[4]	.subtract(challenge_cmt[1]	.multiply( new BigInteger(sku.getU()) ));
		
		//sends the solution to the bank, awaits confirmation and the signature
		response = comm.withdraw_solve(new String[] {
											sr[0].toString(),
											sr[1].toString(),
											sr[2].toString(),
											sr[3].toString(),
											sr[4].toString()	});
		
		//parses confirmation [0]
		if(!Boolean.parseBoolean(response[0]))
			return null;
		
		//parses signature [1..3]
		CLSignature signature = new CLSignature();
		signature.setA(new BigInteger(response[2]).toByteArray());
		signature.setE(new BigInteger(response[3]).toByteArray());
		signature.setV(new BigInteger(response[4]).toByteArray());
		
		//creates wallet
		Wallet wallet = new Wallet();
		wallet.setS(_s.add(new BigInteger(response[1])).mod(n).toByteArray());
		wallet.setT(t.toByteArray());
		wallet.setX(_A.getT());
		wallet.setQ(cardID.toByteArray());
		wallet.setJ(BigInteger.ONE.toByteArray());
		wallet.setSigA(signature.getA());
		wallet.setSigE(signature.getE());
		wallet.setSigV(signature.getV());
		
		return wallet;
	}
	
	/**
	 * First step to provide a wallet to a user.
	 * After receiving the random values of the proof, the Bank provides 2 challenges for the user (for C' and for sku).
	 * 
	 * @return array of {@link BigInteger} with 2 challenges for the Proof of Knowledge.
	 */
	public static BigInteger[] withdraw_BankSide_Challenge() {
		SystemParameter par = SystemParameter.get();
		
		BigInteger challenge, challengePK;
		do {
			challenge = new BigInteger(par.getK(), Prime.random);
		} while(challenge.compareTo(new BigInteger(par.getP())) >= 0);;
		do {
			challengePK = new BigInteger(par.getK(), Prime.random);
		} while(challengePK.compareTo(new BigInteger(par.getP())) >= 0);
		
		return new BigInteger[] {challenge, challengePK};
	}
	
	/**
	 * Second step to provide a wallet to a user.
	 * After receiving the solution to the challenges, the Bank verifies the solution and,
	 * if it is valid, provides a signature over the committed wallet, and its part on the serial number.
	 * 
	 * @param _A commitment C'.
	 * @param Gu user public key pku.
	 * @param J wallet size.
	 * @param Q cardID.
	 * @param tr random values to prove knowledge of C' and sku.
	 * @param sr solution to challenge to prove knowledge of C' and sku.
	 * @param challenge set of challenges for each proof.
	 * @param sk bank private key skb.
	 * @param pk bank public key pkb.
	 * @param ck bank public key to commit ckb.
	 * 
	 * @return a set of {@link Object} containing 1 or 3 values: <br/>
	 * <ul>
	 * <li> 1: a boolean <code>false</code>, rejecting the proof.
	 * <li> 3: a boolean <code>true</code> accepting the proof, a {@link BigInteger} s' to compose the random serial,
	 * and a {@link CLSignature} on the committed value.
	 * </ul>
	 */
	public static Object[] withdraw_BankSide_Proof(BigInteger _A, BigInteger Gu, BigInteger J, BigInteger Q, 
											BigInteger[] tr, BigInteger[] sr, BigInteger[] challenge,
											CLPrivateKey sk, CLPublicKey pk, PedPublicKey ck) {
		SystemParameter par = SystemParameter.get();
		
		BigInteger n = new BigInteger(par.getN());
		BigInteger p = new BigInteger(par.getP());
		
		if(tr == null || sr == null || challenge == null || tr.length != WALLET_PARAM_SIZE || sr.length != WALLET_PARAM_SIZE)
			return new Object[] {false};
		
		//computes T = t0*t1*t2*t3
		BigInteger T =	BigInteger.ONE
							.multiply(tr[0]).mod(n)
							.multiply(tr[1]).mod(n)
							.multiply(tr[2]).mod(n)
							.multiply(tr[3]).mod(n);
		
		//computes G = g0^sr0 * g1^sr1 * g2^sr2 * g4^sr3
		BigInteger G =	BigInteger.ONE
							.multiply(new BigInteger(par.getG(0))	.modPow(sr[0], n))
							.multiply(new BigInteger(par.getG(1))	.modPow(sr[1], n))
							.multiply(new BigInteger(par.getG(2))	.modPow(sr[2], n))
							.multiply(new BigInteger(par.getG(4))	.modPow(sr[3], n));
		
		//verifies if T == G * A'^c
		if(!T.equals(G.multiply(_A.mod(n).modPow(challenge[0], n)).mod(n)))
			return new Object[] {false};
		
		//computes T = t4
		T = tr[4];
		//computes G = g0^sr4
		G = new BigInteger(par.getG(0)).modPow(sr[4], p);
		
		//verifies if T == G * pku^cpk
		if(!T.equals(G.multiply(Gu.modPow(challenge[1], p)).mod(p)))
			return new Object[] {false};
		
		//generates s"
		BigInteger	_s;
		do {
			_s = new BigInteger(par.getK(), Prime.random);
		} while(_s.compareTo(n) >= 0);
		
		//updates the commitment
		BigInteger A = _A
				.multiply(new BigInteger(par.getG(1)).modPow(_s, n)).mod(n)
				.multiply(new BigInteger(par.getG(3)).modPow(J, n)).mod(n)
				.multiply(new BigInteger(par.getG(5)).modPow(Q, n)).mod(n);
		
		//FIXME message size
		CLSignature signature = CLSign.signBlind(A.toByteArray(), ck, A.toByteArray().length, pk, sk, par.getK());
		
		//may need to reduce the user credits - won't do
		
		return new Object[] {true, _s, signature};
	}
	
	public static Coin spend_Wallet(UPrivateKey sku, UPublicKey pku, Wallet wallet) {
		SystemParameter par = SystemParameter.get();
		
		BigInteger	n = new BigInteger( par.getP() );
		
		byte[] timestamp = String.valueOf(Calendar.getInstance().getTimeInMillis()).getBytes();
		byte[] info = new byte[pku.getGu().length + timestamp.length];
		//info = pku||timestamp
		for(int i = 0; i < info.length; i++)
			for(byte[] bytes : new byte[][] {pku.getGu(), timestamp})
				for(int j = 0; j < bytes.length; j++)
					info[i] = bytes[j];
		
		BigInteger	r = new BigInteger( VRF.generate(par.getG(0), sku.getU(), info, n.toByteArray()) );
		
		BigInteger R = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.update(r.toByteArray());
			digest.update(info);
			R = new BigInteger(digest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		
		BigInteger	S = new BigInteger( VRF.generate(par.getG(5), BigInteger.ONE.toByteArray(), sku.getU(), n.toByteArray()) );
		
		BigInteger	T = new BigInteger(pku.getGu())
				.multiply(new BigInteger( VRF.generate(par.getG(5), R.toByteArray(), wallet.getT(), n.toByteArray()) ));
		
		CoinProperty property = new CoinProperty();
		property.setInfo(info);
		property.setTag(T.toByteArray());
		property.setR(R.toByteArray());
		
		Coin coin = new Coin();
		coin.setSerial(S.toByteArray());
		coin.addProperty(property);
		coin.addEventToHistory(S.toByteArray());
		coin.addEventToHistory(T.toByteArray());
		
		return coin;
	}
	
	public static boolean spend_SpenderSide(UPrivateKey sku, UPublicKey pku, ICommunication comm, Coin coin, long coinID) {
		SystemParameter par = SystemParameter.get();
		
		BigInteger	n = new BigInteger( par.getP() );
		
		String[] response = comm.spend_request(new String[] {pku.toString(), String.valueOf(coinID)});
		BigInteger pku2 = new BigInteger(response[0]);
		String timestamp = response[1];
		BigInteger r = new BigInteger(response[2]);
		byte[] info = response[3].getBytes();
		
		byte[] infoX = new byte[pku.getGu().length + pku2.toByteArray().length + timestamp.getBytes().length];
		//info = pku1||pku2||timestamp
		for(int i = 0; i < info.length; i++)
			for(byte[] bytes : new byte[][] {pku.getGu(), pku2.toByteArray(), timestamp.getBytes()})
				for(int j = 0; j < bytes.length; j++)
					info[i] = bytes[j];
		
		if( !VRF.verify(r.toByteArray(), pku2.toByteArray(), info, n.toByteArray()) ) {
			System.out.println("Verify failure");
			return false;
		}
		
		BigInteger R = null;
		BigInteger h = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.update(r.toByteArray());
			digest.update(info);
			R = new BigInteger(digest.digest());
			
			digest.reset();
			
			for(byte[] input : coin.getHistory())
				digest.update(input);
			h = new BigInteger(digest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		}
		
		BigInteger	T = new BigInteger(pku.getGu())
				.multiply(new BigInteger( VRF.generate(par.getG(5), R.toByteArray(), new BigInteger(pku.getGu()).add(h).toByteArray(), n.toByteArray()) ));
		
		CoinProperty property = new CoinProperty();
		property.setInfo(info);
		property.setTag(T.toByteArray());
		property.setR(R.toByteArray());
		
		coin.addProperty(property);
		coin.addEventToHistory(T.toByteArray());
		
		//TODO complete
		response = comm.spend_resolve(new String[] {});
		
		
		return false;
	}
	
	public static boolean spend_ReceiverSide_Key() {
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
