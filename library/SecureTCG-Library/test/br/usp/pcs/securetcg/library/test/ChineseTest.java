package br.usp.pcs.securetcg.library.test;

import java.math.BigInteger;

import br.usp.pcs.securetcg.library.utils.Prime;

public class ChineseTest {

	public static void main(String[] args) {
		BigInteger[] a, mod;
		
		a = new BigInteger[]{BigInteger.valueOf(3), BigInteger.valueOf(2), BigInteger.valueOf(4)};
		mod = new BigInteger[]{BigInteger.valueOf(4), BigInteger.valueOf(3), BigInteger.valueOf(5)};
		
		Prime.getCRTResult(a, mod);
		
		a = new BigInteger[]{BigInteger.valueOf(1), BigInteger.valueOf(2), BigInteger.valueOf(3)};
		mod = new BigInteger[]{BigInteger.valueOf(7), BigInteger.valueOf(8), BigInteger.valueOf(9)};
		
		Prime.getCRTResult(a, mod);
		
		a = new BigInteger[]{BigInteger.valueOf(1), BigInteger.valueOf(2), BigInteger.valueOf(3)};
		mod = new BigInteger[]{BigInteger.valueOf(3), BigInteger.valueOf(5), BigInteger.valueOf(7)};
		
		Prime.getCRTResult(a, mod);
	}
}
