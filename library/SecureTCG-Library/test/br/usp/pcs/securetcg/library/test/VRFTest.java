package br.usp.pcs.securetcg.library.test;

import java.math.BigInteger;

import br.usp.pcs.securetcg.library.ecash.CompactEcash;
import br.usp.pcs.securetcg.library.ecash.SystemParameter;
import br.usp.pcs.securetcg.library.ecash.model.UPrivateKey;
import br.usp.pcs.securetcg.library.ecash.model.UPublicKey;
import br.usp.pcs.securetcg.library.vrf.VRF;

public class VRFTest {

	public static void main(String[] args) {
		SystemParameter par = SystemParameter.get();
		
		boolean result = false;
		do {
			UPublicKey pku = new UPublicKey();
			UPrivateKey sku = new UPrivateKey();
			CompactEcash.uKeyGen(pku, sku);
			
			System.out.println("pk: " + new BigInteger(pku.getGu()));
			System.out.println("sk: " + new BigInteger(sku.getU()));
			
			BigInteger g = new BigInteger(par.getG(0));
			BigInteger n = new BigInteger(par.getP());
			BigInteger seed = new BigInteger("123456789");
			
			BigInteger sign = new BigInteger( VRF.generate(g.toByteArray(), sku.getU(), seed.toByteArray(), n.toByteArray()) );
			result = VRF.verify(sign.toByteArray(), pku.getGu(), seed.toByteArray(), n.toByteArray());
			System.out.println( result );
		} while(result);
	}

}
