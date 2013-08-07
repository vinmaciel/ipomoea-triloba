package br.usp.pcs.securetcg.library.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Calendar;

public final class Prime {
	
	public static final BigInteger BIGINTEGER_TWO = new BigInteger("2");
	
	/**
	 * Instance of a pseudo-random generator with probabilistic algorithm.
	 */
	public static final SecureRandom random = new SecureRandom(BigInteger.valueOf(Calendar.getInstance().getTimeInMillis()).toByteArray());
	
	
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
		long init = Calendar.getInstance().getTimeInMillis();
		
		BigInteger number = BigInteger.TEN;
		
		while(!number.subtract(BigInteger.ONE).divide(BIGINTEGER_TWO).isProbablePrime(100)) {
			number = BigInteger.probablePrime(bitLength, random);
		}
		
		System.out.println("Safe random " + number + " generated in " + (Calendar.getInstance().getTimeInMillis() - init) + "ms.");
		
		return number;
	}
	
	/**
	 * Generates a RSA modulus n = p*q where p and q are safe primes. 
	 * 
	 * @param bitLength size of the modulus.
	 * @return a 2-sized {@link BigInteger} array containing the components of the modulus.
	 */
	public static BigInteger[] getSpecialRSAModulus(int bitLength) {
		long init = Calendar.getInstance().getTimeInMillis();
		
		BigInteger	p = Prime.getSafePrime(bitLength/2),
					q = Prime.getSafePrime(bitLength/2);
		
		while(p.equals(q)) {
			p = Prime.getSafePrime(bitLength/2);
			q = Prime.getSafePrime(bitLength/2);
		}
		
		System.out.println("Special RSA Modulus " + p.multiply(q) + " generated in " + (Calendar.getInstance().getTimeInMillis() - init) + "ms.");
		
		return new BigInteger[] {p, q};
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
	 * Calculates the discrete logarithm.
	 * It will only calculate if its possible to get a unique modular multiplicative inverse.
	 * 
	 * @param expoent of the variable.
	 * @param power equivalence of the exponential in modulus.
	 * @param modulus limiting the calculus.
	 * @return a {@link BigInteger} that represents the logarithm, or <code>null</code> if it is not possible 
	 * to get a unique answer to the problem.
	 */
	public static BigInteger getDiscreteLogarithm(BigInteger expoent, BigInteger power, BigInteger modulus) throws ArithmeticException {
		try {
			long init = Calendar.getInstance().getTimeInMillis();
			BigInteger log = power.modPow(expoent.modInverse(modulus.subtract(BigInteger.ONE)), modulus);
			System.out.println("Multiplicative inverse: " + expoent.modInverse(modulus.subtract(BigInteger.ONE)));
			System.out.println("Discrete logarithm " + log + " calculated in " + (Calendar.getInstance().getTimeInMillis() - init) + "ms.");
			return log;
		}
		catch(NullPointerException npe) {
			return null;
		}
	}
	
}
