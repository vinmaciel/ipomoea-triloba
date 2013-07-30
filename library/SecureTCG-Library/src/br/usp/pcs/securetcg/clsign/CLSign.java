package br.usp.pcs.securetcg.clsign;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import br.usp.pcs.securetcg.utils.Prime;

/**
 * Class that uses the scheme of signature created by Camenisch and Lysyanskaya to generate the key pair,
 * sign a message and verify the signature.
 * 
 * @author mmaciel
 * @see<a href="http://groups.csail.mit.edu/cis/pubs/lysyanskaya/cl02b.pdf">Camenish-Lysyanskaya Scheme</a>
 *
 */
public class CLSign {
	
	public static final int MODULUS_LENGTH = 1024;
	public static final int MESSAGE_LENGTH = 160;
	
	public static final int SECURITY_PARAMETER = 160;
	
	
	/**
	 * Generate a pair of a public key (PK) and a private key (SK) as described in CLSign scheme
	 * 
	 * @param size of the RSA modulus.
	 * @return byte array containing the key pair (PK, SK).
	 * 
	 */
	public static byte[][] generateKeyPair(int size) {
		BigInteger	p = Prime.getSafePrime(size/2),
					n = Prime.getSpecialRSAModulus(p, size),
					a = Prime.getQuadaticResidue(n, size),
					b = Prime.getQuadaticResidue(n, size),
					c = Prime.getQuadaticResidue(n, size);
		
		byte[] sk = p.toByteArray();
		byte[] pk;
		
		// concatenates n|a|b|c
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			out.write(n.toByteArray());
			out.write(a.toByteArray());
			out.write(b.toByteArray());
			out.write(c.toByteArray());
			
			pk = out.toByteArray();
		}
		catch(IOException ioe) {
			return null;
		}
		
		return new byte[][] {pk, sk};
	}

	public static byte[][] generateKeyPair() {
		return CLSign.generateKeyPair(MODULUS_LENGTH);
	}
	
	/**
	 * Create a signature based on the message and a secret key.
	 * 
	 * @param message to be signed.
	 * @param key to sign.
	 * @param size of the RSA modulus.
	 * @return byte array containing the signature.
	 */
	public static byte[] sign(byte[] message, byte[] key, int size) {
		BigInteger	m = new BigInteger(message),
					n = new BigInteger(Arrays.copyOfRange(key, 0, key.length/4-1)),
					a = new BigInteger(Arrays.copyOfRange(key, key.length/4, key.length/2-1)),
					b = new BigInteger(Arrays.copyOfRange(key, key.length/2, 3*key.length/4-1)),
					c = new BigInteger(Arrays.copyOfRange(key, 3*key.length/4, key.length-1));
		
		BigInteger	e = BigInteger.TEN;
		while(e.compareTo(Prime.BIGINTEGER_TWO.pow(message.length + 1)) <= 0) {
			e = Prime.getPrime(message.length + 2);
		}
		
		BigInteger	s = BigInteger.probablePrime(size + message.length, Prime.random);
		
		// TODO finish
	
		return null;
	}
	
	/**
	 * Verifies if the signature of the message is valid. 
	 * 
	 * @param message that was signed.
	 * @param signature of the message.
	 * @param key used to verify the signature.
	 * @return <code>true</code> if the signature is valid.
	 */
	public static boolean verify(byte[] message, byte[] signature, byte[] key) {
		return false;
	}
}
