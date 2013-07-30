package br.usp.pcs.securetcg.utils;

import java.math.BigInteger;
import java.security.SecureRandom;

public final class Prime {
	
	public static final BigInteger BIGINTEGER_TWO = new BigInteger("2");
	
	/**
	 * Instance of a pseudo-random generator with probabilistic algorithm.
	 */
	public static SecureRandom random = new SecureRandom();
	
	
	/**
	 * Generates a prime number of <code>bitLength</code> bits.
	 * 
	 * @param bitLength size of the number.
	 * @return a {@link BigInteger} that represents the prime generated.
	 */
	public static BigInteger getPrime(int bitLength) {
		return BigInteger.probablePrime(bitLength, random);
	}
	
	/**
	 * Generates a prime number n of <code>bitLength</code> bits so that
	 * (n-1)/2 is also a prime number.
	 * 
	 * @param bitLength size of the number.
	 * @return a {@link BigInteger} that represents the safe prime generated.
	 */
	public static BigInteger getSafePrime(int bitLength) {
		BigInteger number = BigInteger.TEN;
		
		while(!number.subtract(BigInteger.ONE).divide(BIGINTEGER_TWO).isProbablePrime(100))
			number = BigInteger.probablePrime(bitLength, random);
		
		return number;
	}
	
	/**
	 * Generates a RSA modulus n = p*q where p and q are safe primes. 
	 * 
	 * @param p component of the modulus.
	 * @param bitLength size of the modulus.
	 * @return a {@link BigInteger} that represents the modulus generated.
	 */
	public static BigInteger getSpecialRSAModulus(BigInteger p, int bitLength) {
		BigInteger q = Prime.getSafePrime(bitLength/2);
		return p.multiply(q);
	}
	
	/**
	 * Generates a quadratic residue in modulus n.
	 * 
	 * @param modulus limiting the residue.
	 * @param bitLength size of the modulus.
	 * @return a {@link BigInteger} that represents the residue.
	 */
	public static BigInteger getQuadaticResidue(BigInteger modulus, int bitLength) {
		BigInteger number = BigInteger.probablePrime(bitLength, random);
		return number.pow(2).mod(modulus);
	}
	
	/**
	 * Generates the modular multiplicative inverse based on the extended euclidean algorithm. 
	 * It will only calculate the inverse if the power and modulus-1 are coprimes.
	 * 
	 * @param power to be compared.
	 * @param modulus of the operation.
	 * @return a {@link BigInteger} that represents the inverse, or <code>null</code> if there can be 
	 * more than one answer to the problem.
	 */
	public static BigInteger getModularMultiplicativeInverse(BigInteger power, BigInteger modulus) {
		BigInteger	a = power,
					b = modulus.subtract(BigInteger.ONE);
		
		//Oneness: GCD(a, b) == 1
		if(!a.gcd(b).equals(BigInteger.ONE))
			return null;
		
		BigInteger	x = BigInteger.ZERO,
					y = BigInteger.ONE;
		
		BigInteger	lastX = BigInteger.ONE,
					lastY = BigInteger.ZERO;
		
		while(!b.equals(BigInteger.ZERO)) {
			BigInteger quotient = a.divide(b);
			
			BigInteger aux = a;
			a = b;
			b = aux.mod(b);
			
			aux = x;
			x = lastX.subtract(quotient.multiply(x));
			lastX = aux;
			
			aux = y;
			y = lastY.subtract(quotient.multiply(y));
			lastY = aux;
		}
		
		return lastX;
	}
	
}
