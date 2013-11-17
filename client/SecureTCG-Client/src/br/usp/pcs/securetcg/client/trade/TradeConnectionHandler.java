package br.usp.pcs.securetcg.client.trade;

import android.os.Handler;
import android.os.Message;

public abstract class TradeConnectionHandler extends Handler {

	public TradeConnectionHandler() {
		super();
	}
	
	abstract void onServerError();
	abstract void onClientError();
	abstract void onServerOK();
	abstract void onClientOK();
	
	abstract void onSendError();
	abstract void onSendOK(String message);
	
	abstract void onReceive(String message);
	
	abstract void onConnectionClosed();
	
	@Override
	public void handleMessage(Message msg) {
		switch(msg.what) {
		case Constants.TRADE_CONNECTION_MESSAGE:
			
			switch(msg.arg1) {
			case Constants.TRADE_SERVER_ERROR:
				onServerError();
				break;
			case Constants.TRADE_CLIENT_ERROR:
				onClientError();
				break;
			case Constants.TRADE_SERVER_OK:
				onServerOK();
				break;
			case Constants.TRADE_CLIENT_OK:
				onClientOK();
				break;
			}
			break;
			
		case Constants.TRADE_OUTPUT_MESSAGE:
			switch(msg.arg1) {
			case Constants.TRADE_SEND_ERROR:
				onSendError();
				break;
			case Constants.TRADE_SEND_OK:
				onSendOK(new String((byte[]) msg.obj));
				break;
			}
			break;
			
		case Constants.TRADE_INPUT_MESSAGE:
			switch(msg.arg1) {
			case Constants.TRADE_RECEIVE_MESSAGE:
				onReceive(new String((byte[]) msg.obj));
				break;
			case Constants.TRADE_RECEIVE_CLOSE:
				onConnectionClosed();
				break;
			}
			break;
		}
	}
}
