package br.usp.pcs.securetcg.client.trade;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class TradeConnectionHandler extends Handler {

	private Context context;
	
	public TradeConnectionHandler(Context context) {
		super();
		this.context = context;
	}
	
	@Override
	public void handleMessage(Message msg) {
		switch(msg.what) {
		case Constants.TRADE_CONNECTION_MESSAGE:
			
			switch(msg.arg1) {
			case Constants.TRADE_SERVER_ERROR:
			case Constants.TRADE_CLIENT_ERROR:
				Toast.makeText(context, "Unable to connect to server", Toast.LENGTH_SHORT).show();
				break;
			case Constants.TRADE_SERVER_OK:
				
				break;
			case Constants.TRADE_CLIENT_OK:
				
				break;
				
			default:
				
			}
			break;
			
		case Constants.TRADE_OUTPUT_MESSAGE:
			switch(msg.arg1) {
			case Constants.TRADE_SEND_ERROR:
				
				break;
			case Constants.TRADE_SEND_OK:
				
				break;
				
			default:
					
			}
			break;
			
		default:
			
		}
	}
}
