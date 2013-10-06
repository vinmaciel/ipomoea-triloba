package br.usp.pcs.securetcg.library.ecash;

import java.math.BigInteger;

/**
 * Class that uses the commitment scheme created by Pedersen to bind a value.
 * 
 * @author vinmaciel
 * @see<a href="http://www.cs.huji.ac.il/~ns/Papers/pederson91.pdf">Pedersen Commitment Scheme</a> (accessed at October 6th, 2013)
 *
 */
public class PedCom {

	/**
	 * Create commitment to message s given a random t.
	 * 
	 * @param gens (n+1) generators used in the commitment (<code>gen[0]<SUP>rand</SUP> * <SUB>i=1 to n</SUB>[ gen[i>0]<SUP>values[i-1]</SUP> ]</code>)
	 * @param values block of n messages to be committed.
	 * @param rand random value used to commit.
	 * 
	 * @return representation of the commitment.
	 */
	public static byte[] commit(byte[][] values, byte[] rand) {
		SystemParameter par = SystemParameter.get();
		
		BigInteger	q = BigInteger.valueOf(par.getPedOrder()),
					t = new BigInteger(rand);
		BigInteger[]	v = new BigInteger[values.length],
						g = new BigInteger[par.getPedGeneratorLength()];
		
		for(int i = 0; i < g.length; i++)	g[i] = new BigInteger(par.getPedGenerator(i));
		for(int i = 0; i < v.length; i++)	v[i] = new BigInteger(values[i]);
		
		BigInteger result = g[0].modPow(t, q);
		for(int i = 0; i < values.length; i++)
			result = result.multiply(g[i+1].modPow(v[i], q)).mod(q);
		
		return result.toByteArray();
	}
}
