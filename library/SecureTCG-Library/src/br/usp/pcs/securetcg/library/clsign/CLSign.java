package br.usp.pcs.securetcg.library.clsign;

import java.math.BigInteger;

import br.usp.pcs.securetcg.library.utils.Prime;

/**
 * Class that uses the scheme of signature created by Camenisch and Lysyanskaya to generate the key pair,
 * sign a message and verify the signature.
 * 
 * @author mmaciel
 * @see<a href="http://groups.csail.mit.edu/cis/pubs/lysyanskaya/cl02b.pdf">Camenish-Lysyanskaya Scheme</a> (accessed at July 31st, 2013)
 *
 */
public class CLSign {
	
	public static final int MODULUS_LENGTH = 1024;
	public static final int MESSAGE_LENGTH = 160;
	
	public static final int SECURITY_PARAMETER = 160;
	
	
	/**
	 * Generate a pair of a public key (PK) and a private key (SK) as described in CLSign scheme. 
	 * The public key is formed with Special RSA Modulus n = pq, and three random quadratic residues in n. 
	 * The private key is the safe prime p. 
	 * Both input keys can be <code>null</code>.
	 * 
	 * @param keySize length (in bits) of the RSA modulus.
	 * @param pk public key to be generated.
	 * @param sk private key to be generated.
	 * 
	 */
	public static void generateKeyPair(int keySize, PublicKey pk, PrivateKey sk) {
		if(pk == null) pk = new PublicKey();
		if(sk == null) sk = new PrivateKey();
		
		BigInteger	p = Prime.getSafePrime(keySize/2),
					n = Prime.getSpecialRSAModulus(p, keySize),
					a = Prime.getQuadaticResidue(n, keySize),
					b = Prime.getQuadaticResidue(n, keySize),
					c = Prime.getQuadaticResidue(n, keySize);

		System.out.println("p=" + p.toString());
		System.out.println("n=" + n.toString());
		System.out.println("a=" + a.toString());
		System.out.println("b=" + b.toString());
		System.out.println("c=" + c.toString());

		pk.setN(n.toByteArray());
		pk.setA(a.toByteArray());
		pk.setB(b.toByteArray());
		pk.setC(c.toByteArray());
		
		sk.setP(p.toByteArray());
	}

	/**
	 * Generate a pair of a public key (PK) and a private key (SK) as described in CLSign scheme with default size. 
	 * See {@link CLSign#generateKeyPair} for more details.
	 * 
	 * @param pk public key to be generated.
	 * @param sk private key to be generated.
	 * 
	 */
	public static void generateKeyPair(PublicKey pk, PrivateKey sk) {
		CLSign.generateKeyPair(MODULUS_LENGTH, pk, sk);
	}
	
	/**
	 * Create a signature based on the message and a secret key.
	 * 
	 * @param message to be signed.
	 * @param messageSize length (in bits) of the message.
	 * @param pk public key used to sign.
	 * @param sk private key used to sign.
	 * @param keySize of the RSA modulus.
	 * @return byte array containing the signature.
	 */
	public static byte[] sign(byte[] message, int messageSize, PublicKey pk, PrivateKey sk, int keySize) {
		BigInteger	m = new BigInteger(message),
					n = new BigInteger(pk.getN()),
					p = new BigInteger(sk.getP()),
					q = n.divide(new BigInteger(sk.getP())),
					a = new BigInteger(pk.getA()),
					b = new BigInteger(pk.getB()),
					c = new BigInteger(pk.getC());
		
		BigInteger	e = BigInteger.TEN;
		while(e.compareTo(Prime.BIGINTEGER_TWO.pow(messageSize + 1)) <= 0) {
			e = Prime.getPrime(message.length + 2);
		}
		
		BigInteger	s = BigInteger.probablePrime(keySize + messageSize + SECURITY_PARAMETER, Prime.random);
		
		//TODO use CRT to calculate answer in acceptable time (decomposing n in p,q primes)
		
		return Prime.getDiscreteLogarithm(e, a.modPow(m, n).multiply(b.modPow(s, n)).multiply(c), n).toByteArray();
	}
	
	/**
	 * Verifies if the signature of the message is valid. 
	 * 
	 * @param message that was signed.
	 * @param signature of the message.
	 * @param pk public key used to verify the signature.
	 * @return <code>true</code> if the signature is valid.
	 */
	public static boolean verify(byte[] message, Signature signature, PublicKey pk) {
		BigInteger	m = new BigInteger(message),
					e = new BigInteger(signature.getE()),
					s = new BigInteger(signature.getS()),
					v = new BigInteger(signature.getV()),
					n = new BigInteger(pk.getN()),
					a = new BigInteger(pk.getA()),
					b = new BigInteger(pk.getB()),
					c = new BigInteger(pk.getC());
		
		return v.modPow(e, n).equals(a.modPow(m, n).multiply(b.modPow(s, n)).multiply(c));
	}
}
