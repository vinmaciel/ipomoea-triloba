package br.usp.pcs.securetcg.client.market;

public class Constants {

	private static final String SERVER_PROTOCOL = "http://";
	private static final String SERVER_ADDRESS = "172.20.5.15";
	private static final String SERVER_PORT = ":8080/";
	private static final String SERVER_CONTEXT = "SecureTCG-Server/";
	
	private static final String MARKET_ACTION = "market.do";
	private static final String REGISTER_ACTION = "login.do";
	
	public static final String MARKET_URL = SERVER_PROTOCOL + SERVER_ADDRESS + SERVER_PORT + SERVER_CONTEXT + MARKET_ACTION;
	public static final String REGISTRATION_URL = SERVER_PROTOCOL + SERVER_ADDRESS + SERVER_PORT + SERVER_CONTEXT + REGISTER_ACTION;
	
}
