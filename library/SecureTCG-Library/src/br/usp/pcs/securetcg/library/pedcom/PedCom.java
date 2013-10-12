package br.usp.pcs.securetcg.library.pedcom;

import java.math.BigInteger;
import java.util.Calendar;

import br.usp.pcs.securetcg.library.utils.Prime;

/**
 * Class that uses the commitment scheme created by Pedersen to bind a value.
 * 
 * @author vinmaciel
 * @see<a href="http://www.cs.huji.ac.il/~ns/Papers/pederson91.pdf">Pedersen Commitment Scheme</a> (accessed at October 6th, 2013)
 *
 */
public class PedCom {

	/**
	 * Generates a key to commit as described in Pedersen Commitment Scheme.
	 * The public key is formed with Special RSA Modulus n = pq, a random quadratic residue h and a set of generators g in h.
	 * 
	 * @param keySize length (in bits) of the RSA modulus.
	 * @param generatorSize length of G parameter of the key.
	 * @return new {@link PedPublicKey} with parameters set.
	 */
	public static PedPublicKey generateKey(int keySize, int generatorSize) {
		long init = Calendar.getInstance().getTimeInMillis();
		
		PedPublicKey pk = new PedPublicKey();
		//TODO is this necessary to create a private key with the modulus?
		
		BigInteger[] rsa = Prime.getSpecialRSAModulus(keySize);
		
		BigInteger	p = rsa[0],
					q = rsa[1],
					n = p.multiply(q),
					h = Prime.getQuadaticResidue(n, keySize);
		
		BigInteger[] g = new BigInteger[generatorSize];
		
		for(int i = 0; i < generatorSize; i++)
			do {
				g[i] = Prime.getPrime(keySize);
			} while(g[i].compareTo(h) != -1);
		
		pk.setN(n.toByteArray());
		pk.setH(h.toByteArray());
		pk.allocG(generatorSize);
		for(int i = 0; i < g.length; i++)
			pk.setG(g[i].toByteArray(), i);
		
		System.out.println("Key pair generated in " + (Calendar.getInstance().getTimeInMillis() - init) + "ms.");
		
		return pk;
	}
	
	/**
	 * Creates commitment to a set of messages using a given Pedersen key.
	 * 
	 * @param keySize length (in bits) of the RSA modulus.
	 * @param values block of messages to be committed.
	 * @param pk key used to commit.
	 * 
	 * @return new {@link PedCommitment} that represents the commitment.
	 */
	public static PedCommitment commit(int keySize, byte[][] values, PedPublicKey pk) {
		long init = Calendar.getInstance().getTimeInMillis();
		
		if(values.length != pk.getGSize())
			throw new IllegalArgumentException("Number of values to commit different from number of generators");
		
		PedCommitment commit = new PedCommitment();
		
		BigInteger	n = new BigInteger(pk.getN()),
					h = new BigInteger(pk.getH()),
					t = BigInteger.probablePrime(keySize, Prime.random);
		BigInteger[]	v = new BigInteger[values.length],
						g = new BigInteger[pk.getGSize()];
		
		for(int i = 0; i < g.length; i++)	g[i] = new BigInteger(pk.getG(i));
		for(int i = 0; i < v.length; i++)	v[i] = new BigInteger(values[i]);
		
		BigInteger result = h.modPow(t, n);
		for(int i = 0; i < values.length; i++)
			result = result.multiply(g[i].modPow(v[i], n)).mod(n);
		
		commit.setE(result.toByteArray());
		commit.setT(t.toByteArray());
		
		System.out.println("Commited in " + (Calendar.getInstance().getTimeInMillis() - init) + "ms.");
		
		return commit;
	}
}
