package br.usp.pcs.securetcg.library.test;

import java.util.Random;

import br.usp.pcs.securetcg.library.clsign.CLSign;
import br.usp.pcs.securetcg.library.clsign.PrivateKey;
import br.usp.pcs.securetcg.library.clsign.PublicKey;
import br.usp.pcs.securetcg.library.clsign.Signature;

public class CLSignTest {
	
	public static void main(String[] args) {
		PrivateKey sk = new PrivateKey();
		PublicKey pk = new PublicKey();
		
		CLSign.generateKeyPair(32, pk, sk);
		
		System.out.println("sk");
		System.out.println(sk.toString());
		System.out.println("pk");
		System.out.println(pk.toString());
		
		byte[] message = new byte[160];
		Random gen = new Random();
		gen.nextBytes(message);
		
		Signature signature = new Signature();
		
		CLSign.sign(message, message.length, pk, sk, 32, signature);
		
		System.out.println("signature");
		System.out.println(signature.toString());
		
		System.out.println(""+CLSign.verify(message, signature, pk));
	}
	
}
