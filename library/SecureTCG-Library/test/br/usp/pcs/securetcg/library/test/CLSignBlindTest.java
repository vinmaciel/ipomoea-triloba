package br.usp.pcs.securetcg.library.test;

import java.math.BigInteger;
import java.util.Random;

import br.usp.pcs.securetcg.library.clsign.CLPrivateKey;
import br.usp.pcs.securetcg.library.clsign.CLPublicKey;
import br.usp.pcs.securetcg.library.clsign.CLSign;
import br.usp.pcs.securetcg.library.clsign.CLSignature;
import br.usp.pcs.securetcg.library.pedcom.PedCom;
import br.usp.pcs.securetcg.library.pedcom.PedCommitment;
import br.usp.pcs.securetcg.library.pedcom.PedPublicKey;

public class CLSignBlindTest {

	public static void main(String[] args) {
		int keySize = 1024;
		
		CLPrivateKey sk = new CLPrivateKey();
		CLPublicKey pk = new CLPublicKey();
		PedPublicKey ck = new PedPublicKey();
		
		CLSign.generateKeyPair(keySize,4, pk, sk);
		ck.setN(pk.getN());
		ck.setH(pk.getR(3));
		ck.setG(new byte[][] {pk.getR(0), pk.getR(1), pk.getR(2)});
		
		System.out.println("sk:");
		System.out.println(sk.toString());
		System.out.println("pk:");
		System.out.println(pk.toString());
		System.out.println("ck:");
		System.out.println(ck.toString());
		
		byte[][] messages = new byte[6][];
		for(int i = 0; i < messages.length; i++) {
				messages[i] = new byte[160];
				Random gen = new Random();
				gen.nextBytes(messages[i]);
				
				System.out.println("m[" + i + "]="+new BigInteger(messages[i]));
		}
		
		PedCommitment com = PedCom.commit(keySize, new byte[][] {messages[0],messages[1],messages[2]}, ck);
		System.out.println("com:");
		System.out.println(com.toString());
		
		CLSignature signature = CLSign.signBlind(com.getE(), ck, 160, pk, sk, keySize);
		System.out.println("signature:");
		System.out.println(signature.toString());
		
		System.out.println("verify:");
		System.out.println("" + CLSign.verify(new byte[][] {messages[0],messages[1],messages[2],com.getT()}, signature, pk));

	}

}
