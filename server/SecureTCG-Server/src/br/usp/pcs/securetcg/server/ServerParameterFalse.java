package br.usp.pcs.securetcg.server;

import java.math.BigInteger;

import br.usp.pcs.securetcg.library.clsign.CLPrivateKey;
import br.usp.pcs.securetcg.library.clsign.CLPublicKey;
import br.usp.pcs.securetcg.library.ecash.SystemParameterFalse;
import br.usp.pcs.securetcg.library.pedcom.PedPublicKey;

public class ServerParameterFalse {

	private static final String p = "137133950337670114170073765425795154994783959317563002726879422108076139289949486034916434026773240495615191492261587057742303193146342132300959694861859831180068942315833896226732462535248726852571671537675871676453195897237966664070291859015072659553917105528671448761162792222085963953262904116295602619083";
	
	/* Singleton */
	private ServerParameterFalse() {
		SystemParameterFalse pars = SystemParameterFalse.get();
		
		CLPrivateKey skbTemp = new CLPrivateKey();
		CLPublicKey pkbTemp = new CLPublicKey();
		PedPublicKey pedkbTemp = new PedPublicKey();
		
		skbTemp.setP(new BigInteger(p).toByteArray());
		
		pkbTemp.setN(pars.getN());
		pkbTemp.setS(pars.getH());
		pkbTemp.setZ(pars.getZ());
		pkbTemp.setR(new byte[][] {	pars.getG(0),
									pars.getG(1),
									pars.getG(2),
									pars.getG(3),
									pars.getG(4),
									pars.getG(5),	});
		
		pedkbTemp.setN(pars.getN());
		pedkbTemp.setH(pars.getG(4));
		pedkbTemp.setG(new byte[][] {	pars.getG(0),
										pars.getG(1),
										pars.getG(2),	});
		
		this.skb = skbTemp;
		this.pkb = pkbTemp;
		this.pedkb = pedkbTemp;
		
		this.n = new BigInteger(pars.getN());
		this.phiN = new BigInteger(this.pkb.getN()).divide(new BigInteger(this.skb.getP())).subtract(BigInteger.ONE)
				.multiply( new BigInteger(this.skb.getP()).subtract(BigInteger.ONE) );
	}
	
	private static ServerParameterFalse instance = null;
	
	/* Lazy singleton instantiation */
	public static ServerParameterFalse get() {
		if(instance == null)
			instance = new ServerParameterFalse();
		return instance;
	}
	
	
	private final CLPrivateKey skb;
	private final CLPublicKey pkb;
	
	private final PedPublicKey pedkb;
	
	private final BigInteger n;
	private final BigInteger phiN;

	public CLPrivateKey getSkb() {
		return skb;
	}

	public CLPublicKey getPkb() {
		return pkb;
	}

	public PedPublicKey getPedkb() {
		return pedkb;
	}

	public BigInteger getN() {
		return n;
	}

	public BigInteger getPhiN() {
		return phiN;
	}
	
}
