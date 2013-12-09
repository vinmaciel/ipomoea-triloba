package br.usp.pcs.securetcg.server;

import java.math.BigInteger;

import br.usp.pcs.securetcg.library.clsign.CLPrivateKey;
import br.usp.pcs.securetcg.library.clsign.CLPublicKey;
import br.usp.pcs.securetcg.library.ecash.SystemParameterFalse2;
import br.usp.pcs.securetcg.library.pedcom.PedPublicKey;

public class ServerParameterFalse2 {

	private static final String p = "176670430072285685032924905778599025424213076782359193494291891372321496643874766454462183952171815190980534316452215218860093173681829366974097335001781740433835317469516300981563856262991727885244184552338691453746462533933160862259357650950026034058650682308716023918589520554584188885902663688619691244659";
	
	/* Singleton */
	private ServerParameterFalse2() {
		SystemParameterFalse2 pars = SystemParameterFalse2.get();
		
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
	
	private static ServerParameterFalse2 instance = null;
	
	/* Lazy singleton instantiation */
	public static ServerParameterFalse2 get() {
		if(instance == null)
			instance = new ServerParameterFalse2();
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
