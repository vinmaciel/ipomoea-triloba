package br.usp.pcs.securetcg.library.config;

import java.math.BigInteger;

import br.usp.pcs.securetcg.library.ecash.SystemParameter;
import br.usp.pcs.securetcg.library.utils.Prime;

/**
 * Class used to generate the parameters of the system.
 * 
 * @author mmaciel
 */
public class MintParameterGenerator {

	public static void main(String[] args) {
		SystemParameter par = SystemParameter.get();
		
		/* Getting security parameter (final) */
		int k = par.getK();
		
		/*
		 *  Getting special RSA modulus n = pq.
		 *  n will be public, p will be private to the server. 
		 */
		BigInteger[] rsa	= Prime.getSpecialRSAModulus(2*k);
		BigInteger	p = rsa[0],
					q = rsa[1],
					n = p.multiply(q);
		
		System.out.println("n:\n" + n.toString());	//Public
		System.out.println("p:\n" + p.toString());	//Private to server

		do{
			p = Prime.getSafePrime(k);
		}while(p.compareTo(n) >=0);

		System.out.println("");
		System.out.println("p:\n" + p.toString());
		
		/* Getting residues from n that are generators from p */
		BigInteger[] r = new BigInteger[7];
		for(int i = 0; i < r.length; i++) {
			do{
				r[i] = Prime.getQuadaticResidue(n, k);
			}while(!r[i].modPow(p.subtract(BigInteger.ONE).divide(new BigInteger("2")), p).equals(p.subtract(BigInteger.ONE)));
			System.out.println("r[" + i + "]:\n" + r[i].toString());
		}
		
		BigInteger s;
		do{
			s = Prime.getQuadaticResidue(n, k);
		}while(!s.modPow(p.subtract(BigInteger.ONE).divide(new BigInteger("2")), p).equals(p.subtract(BigInteger.ONE)));
		System.out.println("s:\n" + s.toString());
		
		BigInteger z;
		do{
			z = Prime.getQuadaticResidue(n, k);
		}while(!z.modPow(p.subtract(BigInteger.ONE).divide(new BigInteger("2")), p).equals(p.subtract(BigInteger.ONE)));
		System.out.println("z:\n" + z.toString());
	}

}
