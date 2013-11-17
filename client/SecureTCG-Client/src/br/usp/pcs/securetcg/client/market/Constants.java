package br.usp.pcs.securetcg.client.market;

public class Constants {

	private static final String MARKET_PROTOCOL = "http://";
	private static final String MARKET_ADDRESS = "192.168.1.24";
	private static final String MARKET_PORT = ":8080/";
	private static final String MARKET_CONTEXT = "SecureTCG-Server/";
	private static final String MARKET_ACTION = "market.do";
	
	protected static final String MARKET_URL = MARKET_PROTOCOL + MARKET_ADDRESS + MARKET_PORT + MARKET_CONTEXT + MARKET_ACTION;
	
}
