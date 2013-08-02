package br.usp.pcs.securetcg.library.test;

import br.usp.pcs.securetcg.library.clsign.CLSign;
import br.usp.pcs.securetcg.library.clsign.PrivateKey;
import br.usp.pcs.securetcg.library.clsign.PublicKey;

public class CLSignTest {
	
	public static void main(String[] args) {
		PrivateKey sk = new PrivateKey();
		PublicKey pk = new PublicKey();
		
		CLSign.generateKeyPair(32, pk, sk);
		
		System.out.println("sk");
		System.out.println(sk.toString());
		System.out.println("pk");
		System.out.println(pk.toString());
	}
	
}
