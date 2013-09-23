package br.usp.pcs.securetcg.library.test;

import java.math.BigInteger;
import java.util.Random;

import br.usp.pcs.securetcg.library.clsign.CLSign;
import br.usp.pcs.securetcg.library.clsign.PrivateKey;
import br.usp.pcs.securetcg.library.clsign.PublicKey;
import br.usp.pcs.securetcg.library.clsign.Signature;

public class CLSignTest {
	
	public static void main(String[] args) {
		boolean result = true;
		
		do{
			PrivateKey sk = new PrivateKey();
			PublicKey pk = new PublicKey();
			
			CLSign.generateKeyPair(pk, sk);
			
			System.out.println("sk:");
			System.out.println(sk.toString());
			System.out.println("pk:");
			System.out.println(pk.toString());
			
			byte[][] messages = new byte[5][];
			for(int i = 0; i < 5; i++) {
				messages[i] = new byte[160];
				Random gen = new Random();
				gen.nextBytes(messages[i]);
				
				System.out.println("m[" + i + "]="+new BigInteger(messages[i]));
			}
			
			Signature signature = new Signature();
			
			CLSign.sign(messages, 160*8, pk, sk, signature);
			
			System.out.println("signature");
			System.out.println(signature.toString());
			
			result = CLSign.verify(messages, signature, pk);
			System.out.println(""+result);
			System.out.println("");
			System.out.println("");
		} while(result);
	}
	
}
