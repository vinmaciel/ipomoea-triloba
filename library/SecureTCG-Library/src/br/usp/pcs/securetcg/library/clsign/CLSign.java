package br.usp.pcs.securetcg.library.clsign;

import java.math.BigInteger;
import java.util.Calendar;

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
	
	/** Default length of the security parameter {1, 0}<SUP>k</SUP>. */
	public static final int MODULUS_LENGTH = 1024;
	/** Default length of the number of messages to be signed. */
	public static final int MESSAGE_SET = 1;
	/** Default message length. */
	public static final int MESSAGE_LENGTH = 160;
	
	public static final int SECURITY_PARAMETER = 8;
	
	
	/**
	 * Generate a pair of a public key (PK) and a private key (SK) as described in CLSign scheme. 
	 * The public key is formed with Special RSA Modulus n = pq, and three random quadratic residues in n. 
	 * The private key is the safe prime p. 
	 * Both input keys can be <code>null</code>.
	 * 
	 * @param keySize length (in bits) of the RSA modulus.
	 * @param residueSize length of R parameter of the public key.
	 * @param pk public key to be generated.
	 * @param sk private key to be generated.
	 * 
	 */
	public static void generateKeyPair(int keySize, int residueSize, PublicKey pk, PrivateKey sk) {
		long init = Calendar.getInstance().getTimeInMillis();
		
		if(pk == null) pk = new PublicKey();
		if(sk == null) sk = new PrivateKey();
		
		BigInteger[] rsa	= Prime.getSpecialRSAModulus(keySize);
		
		BigInteger	p = rsa[0],
					q = rsa[1],
					n = p.multiply(q);
		
		BigInteger[] r = new BigInteger[residueSize];
		
		for(int i = 0; i < residueSize; i++)
			r[i] = Prime.getQuadaticResidue(n, keySize);
		
		BigInteger	s = Prime.getQuadaticResidue(n, keySize),
					z = Prime.getQuadaticResidue(n, keySize);

		pk.setN(n.toByteArray());
		pk.allocR(residueSize);
		for(int i = 0; i < residueSize; i++)
			pk.setR(r[i].toByteArray(), i);
		pk.setS(s.toByteArray());
		pk.setZ(z.toByteArray());
		
		sk.setP(p.toByteArray());
		
		System.out.println("Key pair generated in " + (Calendar.getInstance().getTimeInMillis() - init) + "ms.");
	}

	/**
	 * Generate a pair of a public key (PK) and a private key (SK) as described in CLSign scheme with default size. 
	 * See {@link CLSign#generateKeyPair(int, PublicKey, PrivateKey)} for more details.
	 * 
	 * @param pk public key to be generated.
	 * @param sk private key to be generated.
	 * 
	 */
	public static void generateKeyPair(PublicKey pk, PrivateKey sk) {
		CLSign.generateKeyPair(MODULUS_LENGTH, MESSAGE_SET, pk, sk);
	}
	
	/**
	 * Create a signature based on the message and a secret key.
	 * 
	 * @param message to be signed.
	 * @param messageSize length (in bits) of the message.
	 * @param pk public key used to sign.
	 * @param sk private key used to sign.
	 * @param keySize of the RSA modulus.
	 * @param signature signed hash from the message.
	 */
	public static void sign(byte[][] messages, int messageSize, PublicKey pk, PrivateKey sk, int keySize, Signature signature) {
		long init = Calendar.getInstance().getTimeInMillis();
		
		if(signature == null) signature = new Signature();
		
		BigInteger	n = new BigInteger(pk.getN()),
					p = new BigInteger(sk.getP()),
					q = n.divide(new BigInteger(sk.getP())),
					s = new BigInteger(pk.getS()),
					z = new BigInteger(pk.getZ());
		
		BigInteger[]	m = new BigInteger[messages.length],
						r = new BigInteger[pk.getRSize()];
		
		for(int i = 0; i < m.length; i++)
			m[i] = new BigInteger(messages[i]);
		for(int i = 0; i < r.length; i++)
			r[i] = new BigInteger(pk.getR(i));
		
		BigInteger	e = BigInteger.TEN;
		while(e.compareTo(Prime.BIGINTEGER_TWO.pow(messageSize + 1)) <= 0) {
			e = Prime.getPrime(messageSize + 2);
		}
		
		BigInteger	v = BigInteger.probablePrime(keySize + messageSize + SECURITY_PARAMETER, Prime.random);
		
		BigInteger numerator = s.modPow(v, n);
		for(int i = 0; i < r.length; i++)
			numerator = numerator.multiply(r[i].modPow(m[i], n)).mod(n);
		
		BigInteger	rightMember = z.multiply(numerator.modInverse(n)).mod(n),
					logP = Prime.getDiscreteLogarithm(e, rightMember, p),
					logQ = Prime.getDiscreteLogarithm(e, rightMember, q),
					a = Prime.getCRTResult( new BigInteger[]{logP, logQ}, new BigInteger[]{p, q} );
		
//		System.out.println("a^m * b^s * c=" + rightMember);
		
		signature.setA(a.toByteArray());
		signature.setE(e.toByteArray());
		signature.setV(v.toByteArray());
		
		System.out.println("Signed in " + (Calendar.getInstance().getTimeInMillis() - init) + "ms.");
	}
	
	/**
	 * Create a signature based on the message and a secret key, with default key size.
	 * See {@link CLSign#sign(byte[], int, PublicKey, PrivateKey, int, Signature)} for more details.
	 * 
	 * @param message to be signed.
	 * @param messageSize length (in bits) of the message.
	 * @param pk public key used to sign.
	 * @param sk private key used to sign.
	 * @param signature signed hash from the message.
	 */
	public static void sign(byte[][] message, int messageSize, PublicKey pk, PrivateKey sk, Signature signature) {
		CLSign.sign(message, messageSize, pk, sk, MODULUS_LENGTH, signature);
	}
	
	/**
	 * Verifies if the signature of the message is valid. 
	 * 
	 * @param message that was signed.
	 * @param signature of the message.
	 * @param pk public key used to verify the signature.
	 * @return <code>true</code> if the signature is valid.
	 */
	public static boolean verify(byte[][] messages, Signature signature, PublicKey pk) {
		long init = Calendar.getInstance().getTimeInMillis();
		
		BigInteger	a = new BigInteger(signature.getA()),
					e = new BigInteger(signature.getE()),
					v = new BigInteger(signature.getV()),
					n = new BigInteger(pk.getN()),
					z = new BigInteger(pk.getZ()),
					s = new BigInteger(pk.getS());
		
		BigInteger[]	m = new BigInteger[messages.length],
						r = new BigInteger[pk.getRSize()];
		
		for(int i = 0; i < m.length; i++)
			m[i] = new BigInteger(messages[i]);
		for(int i = 0; i < r.length; i++)
			r[i] = new BigInteger(pk.getR(i));

//		System.out.println("left=" + v.modPow(e, n));
//		System.out.println("right=" + a.modPow(m, n).multiply(b.modPow(s, n)).multiply(c).mod(n));
		
		BigInteger result = a.modPow(e, n).multiply(s.modPow(v, n)).mod(n);
		for(int i = 0; i < r.length; i++)
			result = result.multiply(r[i].modPow(m[i], n)).mod(n);
		
		System.out.println("Verified in " + (Calendar.getInstance().getTimeInMillis() - init) + "ms.");
		
		return z.equals(result);
	}
}
