package br.usp.pcs.securetcg.server;

import java.math.BigInteger;

import br.usp.pcs.securetcg.library.clsign.CLPrivateKey;
import br.usp.pcs.securetcg.library.clsign.CLPublicKey;
import br.usp.pcs.securetcg.library.ecash.SystemParameter;
import br.usp.pcs.securetcg.library.pedcom.PedPublicKey;

public class ServerParameter {

	private static final String p = "108634678979496362334143612128841271224351462434331837572903313120719929917324801981053399981758240783356106760930552406746590686982342590392743193109965147302499364803499197162886591346077497193253080879887051709914188121934817215253376226775047751262488375686856875940991012233838388917192492846144745972639";
	
	/* Singleton */
	private ServerParameter() {
		SystemParameter pars = SystemParameter.get();
		
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
	}
	
	private static ServerParameter instance = null;
	
	/* Lazy singleton instantiation */
	public static ServerParameter get() {
		if(instance == null)
			instance = new ServerParameter();
		return instance;
	}
	
	
	private final CLPrivateKey skb;
	private final CLPublicKey pkb;
	
	private final PedPublicKey pedkb;

	public CLPrivateKey getSkb() {
		return skb;
	}

	public CLPublicKey getPkb() {
		return pkb;
	}

	public PedPublicKey getPedkb() {
		return pedkb;
	}
}
