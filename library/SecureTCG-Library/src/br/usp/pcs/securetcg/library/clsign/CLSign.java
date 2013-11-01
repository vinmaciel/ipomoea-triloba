package br.usp.pcs.securetcg.library.clsign;

import java.math.BigInteger;
import java.util.Calendar;

import br.usp.pcs.securetcg.library.pedcom.PedPublicKey;
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
	 * Generates a pair of a public key (PK) and a private key (SK) as described in CLSign scheme. 
	 * The public key is formed with Special RSA Modulus n = pq, and a set of random quadratic residues in n. 
	 * The private key is the safe prime p. 
	 * Both input keys can be <code>null</code>.
	 * 
	 * @param keySize length (in bits) of the RSA modulus.
	 * @param residueSize length of R parameter of the public key.
	 * @param pk public key to be generated.
	 * @param sk private key to be generated.
	 * 
	 */
	public static void generateKeyPair(int keySize, int residueSize, CLPublicKey pk, CLPrivateKey sk) {
		long init = Calendar.getInstance().getTimeInMillis();
		
		if(pk == null) pk = new CLPublicKey();
		if(sk == null) sk = new CLPrivateKey();
		
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
	 * Generates a pair of a public key (PK) and a private key (SK) as described in CLSign scheme with default size. 
	 * See {@link CLSign#generateKeyPair(int, CLPublicKey, CLPrivateKey)} for more details.
	 * 
	 * @param pk public key to be generated.
	 * @param sk private key to be generated.
	 * 
	 */
	public static void generateKeyPair(CLPublicKey pk, CLPrivateKey sk) {
		CLSign.generateKeyPair(MODULUS_LENGTH, MESSAGE_SET, pk, sk);
	}
	
	/**
	 * Creates a signature based on the message and a secret key.<br/>
	 * Signature is <code>(e, v, A)</code> where <code>A = (r<SUB><font size="1">i</font></SUB><SUP>m<SUB><font size="1">i</font></SUB></SUP>*s<SUP>v</SUP>*z )<SUP>1/e</SUP><SUB>i = 0..k</SUB></code>
	 * for <code>k</code> messages, random <code>e</code> and <code>v</code>, and parameters <code>r</code>, <code>s</code> and <code>z</code>.
	 * 
	 * @param messages to be signed.
	 * @param messageSize length (in bits) of the message.
	 * @param pk public key used to sign.
	 * @param sk private key used to sign.
	 * @param keySize of the RSA modulus.
	 * 
	 * @return new {@link CLSignature} with the signed hash from the message.
	 */
	public static CLSignature sign(byte[][] messages, int messageSize, CLPublicKey pk, CLPrivateKey sk, int keySize) {
		long init = Calendar.getInstance().getTimeInMillis();
		
		if(messages.length != pk.getRSize())
			throw new IllegalArgumentException("Number of messages to sign different from number of generators");
		
		CLSignature signature = new CLSignature();
		
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
		
		BigInteger rightMember = s.modPow(v, n).multiply(z).mod(n);
		for(int i = 0; i < r.length; i++)
			rightMember = rightMember.multiply(r[i].modPow(m[i], n)).mod(n);
		
		BigInteger	logP = Prime.getDiscreteLogarithm(e, rightMember, p),
					logQ = Prime.getDiscreteLogarithm(e, rightMember, q),
					a = Prime.getCRTResult( new BigInteger[]{logP, logQ}, new BigInteger[]{p, q} );
		
//		System.out.println("a^m * b^s * c=" + rightMember);
		
		signature.setA(a.toByteArray());
		signature.setE(e.toByteArray());
		signature.setV(v.toByteArray());
		
		System.out.println("Signed in " + (Calendar.getInstance().getTimeInMillis() - init) + "ms.");
		
		return signature;
	}
	
	/**
	 * Creates a signature based on the message and a secret key, with default key size.
	 * See {@link CLSign#sign(byte[], int, CLPublicKey, CLPrivateKey, int, CLSignature)} for more details.
	 * 
	 * @param message to be signed.
	 * @param messageSize length (in bits) of the message.
	 * @param pk public key used to sign.
	 * @param sk private key used to sign.
	 * 
	 * @return new {@link CLSignature} with the signed hash from the message.
	 */
	public static CLSignature sign(byte[][] message, int messageSize, CLPublicKey pk, CLPrivateKey sk) {
		return CLSign.sign(message, messageSize, pk, sk, MODULUS_LENGTH);
	}
	
	/**
	 * Creates a blind signature based on a Pedersen commitment and a secret key.
	 * The key from the commitment must have the same RSA modulus from the one used to sign. <br/>
	 * Signature is <code>(e, v, A)</code> where <code>A = (C*h<SUP>v</SUP>*z )<SUP>1/e</SUP></code>
	 * for commitment <code>C</code>, random <code>e</code> and <code>v</code>, and parameters <code>h</code> and <code>z</code>.<br/>
	 * After signing, must substitute <code>v</code> by <code>v+r</code>, where <code>r</code> is the random parameter in the commitment.
	 * 
	 * @param commitment to be signed
	 * @param ck key used to commit
	 * @param messageSize length of the commitment()
	 * @param pk public key used to sign
	 * @param sk private key used to sign
	 * @param keySize of the RSA modulus
	 * 
	 * @return new {@link CLSignature} with the signed hash from the commitment.
	 * 
	 * @see {@link PedCom}
	 */
	public static CLSignature signBlind(byte[] commitment, PedPublicKey ck, int messageSize, CLPublicKey pk, CLPrivateKey sk, int keySize) {
		long init = Calendar.getInstance().getTimeInMillis();
		
		if(!(new BigInteger(ck.getN())).equals(new BigInteger(pk.getN())))
			throw new IllegalArgumentException("Modulus from key used to commit different from the one used to sign");
		
		CLSignature signature = new CLSignature();
		
		BigInteger	n = new BigInteger(pk.getN()),
					p = new BigInteger(sk.getP()),
					q = n.divide(new BigInteger(sk.getP())),
					z = new BigInteger(pk.getZ()),
					b = new BigInteger(ck.getH()),
					m = new BigInteger(commitment);
		
		BigInteger	e = BigInteger.TEN;
		while(e.compareTo(Prime.BIGINTEGER_TWO.pow(messageSize + 1)) <= 0) {
			e = Prime.getPrime(messageSize + 2);
		}
		
		BigInteger	v = BigInteger.probablePrime(keySize + messageSize + SECURITY_PARAMETER, Prime.random);
		
		BigInteger	rightMember = b.modPow(v, n).multiply(m).mod(n).multiply(z).mod(n),
					logP = Prime.getDiscreteLogarithm(e, rightMember, p),
					logQ = Prime.getDiscreteLogarithm(e, rightMember, q),
					a = Prime.getCRTResult( new BigInteger[]{logP, logQ}, new BigInteger[]{p, q} );
		
//		System.out.println("a^m * b^s * c=" + rightMember);
		
		signature.setA(a.toByteArray());
		signature.setE(e.toByteArray());
		signature.setV(v.toByteArray());
		
		System.out.println("Signed in " + (Calendar.getInstance().getTimeInMillis() - init) + "ms.");
		
		return signature;
	}
	
	/**
	 * Verifies if the signature of the message is valid. 
	 * 
	 * @param message that was signed.
	 * @param signature of the message.
	 * @param pk public key used to verify the signature.
	 * @return <code>true</code> if the signature is valid.
	 */
	public static boolean verify(byte[][] messages, CLSignature signature, CLPublicKey pk) {
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
		
		BigInteger	leftMember = a.modPow(e, n),
					rightMember = s.modPow(v, n).multiply(z).mod(n);
		for(int i = 0; i < r.length; i++)
			rightMember = rightMember.multiply(r[i].modPow(m[i], n)).mod(n);
		
		System.out.println("Verified in " + (Calendar.getInstance().getTimeInMillis() - init) + "ms.");
		
		return leftMember.equals(rightMember);
	}
}
