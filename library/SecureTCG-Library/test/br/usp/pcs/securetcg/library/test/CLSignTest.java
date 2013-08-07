package br.usp.pcs.securetcg.library.test;

import java.math.BigInteger;
import java.util.Random;

import br.usp.pcs.securetcg.library.clsign.CLSign;
import br.usp.pcs.securetcg.library.clsign.PrivateKey;
import br.usp.pcs.securetcg.library.clsign.PublicKey;
import br.usp.pcs.securetcg.library.clsign.Signature;

public class CLSignTest {
	
	private static final int KEY_SIZE = 7;
	
	public static void main(String[] args) {
		PrivateKey sk = new PrivateKey();
		PublicKey pk = new PublicKey();
		
		CLSign.generateKeyPair(KEY_SIZE, pk, sk);
		
		System.out.println("sk:");
		System.out.println(sk.toString());
		System.out.println("pk:");
		System.out.println(pk.toString());
		
		byte[] message = new byte[2];
		Random gen = new Random();
		gen.nextBytes(message);
		
		System.out.println("m="+new BigInteger(message));
		
		Signature signature = new Signature();
		
		CLSign.sign(message, message.length*8, pk, sk, KEY_SIZE, signature);
		
		System.out.println("signature");
		System.out.println(signature.toString());
		
		System.out.println(""+CLSign.verify(message, signature, pk));
	}
	
}
