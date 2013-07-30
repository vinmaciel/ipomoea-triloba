package br.usp.pcs.securetcg.clsign;

/**
 * Class that uses the scheme of signature created by Camenisch and Lysyanskaya to generate the key pair,
 * sign a message and verify the signature.
 * 
 * @author mmaciel
 * @see<a href="http://groups.csail.mit.edu/cis/pubs/lysyanskaya/cl02b.pdf">Camenish-Lysyanskaya Scheme</a>
 *
 */
public class CLSign {
	/**
	 * Generate a pair of a public key (PK) and a private key (SK) as described in CLSign scheme
	 * 
	 * @param size of the RSA modulus n.
	 * @return byte array containing the key pair (PK, SK).
	 * 
	 */
	public static byte[][] generateKeyPair(long size) {
		return null;
	}
	
	/**
	 * Create a signature based on the message and a secret key.
	 * 
	 * @param message to be signed.
	 * @param key to sign.
	 * @return byte array containing the signature.
	 */
	public static byte[] sign(byte[] message, byte[] key) {
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
