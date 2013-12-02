package br.usp.pcs.securetcg.library.vrf;

import java.math.BigInteger;

public class VRF {

	public static byte[] generate(byte[] base, byte[] sk, byte[] seed, byte[] mod) {
		BigInteger	g = new BigInteger(base),
					s = new BigInteger(sk),
					x = new BigInteger(seed),
					m = new BigInteger(mod);
		
		BigInteger	sign;
		try {
			sign = g.modPow( s.multiply(x.modInverse(m.subtract(BigInteger.ONE))), m);
		} catch(ArithmeticException e) {
			sign = g.modPow( s.multiply(x.add(BigInteger.ONE).modInverse(m.subtract(BigInteger.ONE))), m);
		}
		
		
		return sign.toByteArray();
	}
	
	public static boolean verify(byte[] random, byte[] pk, byte[] seed, byte[] mod) {
		BigInteger	p = new BigInteger(pk),
					x = new BigInteger(seed),
					m = new BigInteger(mod),
					y = new BigInteger(random);
		
		BigInteger	value1 = y.modPow(x, m).multiply(p.modInverse(m)).mod(m),
					value2 = y.modPow(x.add(BigInteger.ONE), m).multiply(p.modInverse(m)).mod(m);
		
		return value1.equals(BigInteger.ONE) || value2.equals(BigInteger.ONE);
	}
}
