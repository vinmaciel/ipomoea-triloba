package br.usp.pcs.securetcg.library.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Random;

import br.usp.pcs.securetcg.library.clsign.CLPrivateKey;
import br.usp.pcs.securetcg.library.clsign.CLPublicKey;
import br.usp.pcs.securetcg.library.clsign.CLSign;
import br.usp.pcs.securetcg.library.clsign.CLSignature;
import br.usp.pcs.securetcg.library.config.SecurityConstants;
import br.usp.pcs.securetcg.library.ecash.CompactEcash;

public class BKeyGenTest {

	public static void main(String[] args) {
		CLPrivateKey sk = new CLPrivateKey();
		CLPublicKey pk = new CLPublicKey();
		
		String path = "sk";
		try {
			OutputStream out = new FileOutputStream(path);
			out.write(new BigInteger(SecurityConstants.p).toByteArray());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		CompactEcash.bKeyGenDefault(pk, sk, path);
		
		System.out.println("sk:");
		System.out.println(sk.toString());
		System.out.println("pk:");
		System.out.println(pk.toString());
		
		boolean result = false;
		do{
			byte[][] messages = new byte[7][];
			for(int i = 0; i < messages.length; i++) {
					messages[i] = new byte[160];
					Random gen = new Random();
					gen.nextBytes(messages[i]);
					
					System.out.println("m[" + i + "]="+new BigInteger(messages[i]));
			}
			
			CLSignature signature = new CLSignature();
			
			signature = CLSign.sign(messages, 160*8, pk, sk);
			
			System.out.println("signature");
			System.out.println(signature.toString());
			
			result = CLSign.verify(messages, signature, pk);
			System.out.println(""+result);
			System.out.println("");
			System.out.println("");
		}while(result);
	}

}
