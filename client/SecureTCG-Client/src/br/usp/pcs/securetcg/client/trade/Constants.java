package br.usp.pcs.securetcg.client.trade;

import java.util.UUID;

public class Constants {

	protected static final String TRADE_SESSION = "TradingCenterBluetooth";
	
	/** Random UUID - generated from python.uuid **/
	protected static final UUID TRADE_UUID = UUID.fromString("b37db4af-077e-4542-9b27-35a64fc2eafb");
	
	protected static final int TRADE_CONNECTION_MESSAGE = 1;
	protected static final int TRADE_INPUT_MESSAGE = 2;
	protected static final int TRADE_OUTPUT_MESSAGE = 3;
	
	protected static final int TRADE_SERVER_OK = 1;
	protected static final int TRADE_SERVER_ERROR = -1;
	protected static final int TRADE_CLIENT_OK = 2;
	protected static final int TRADE_CLIENT_ERROR = -2;

	protected static final int TRADE_SEND_OK = 1;
	protected static final int TRADE_SEND_ERROR = -1;
	
	protected static final int TRADE_BUFFER = 1024;
}
