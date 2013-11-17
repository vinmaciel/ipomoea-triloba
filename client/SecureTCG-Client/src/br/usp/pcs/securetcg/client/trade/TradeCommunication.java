package br.usp.pcs.securetcg.client.trade;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class TradeCommunication {

	private InputStream in;
	private OutputStream out;
	
	public TradeCommunication(BluetoothSocket socket) throws IOException {
		this.in = socket.getInputStream();
		this.out = socket.getOutputStream();
	}
	
	public void listen(Handler handler) {
		byte[] buffer = new byte[Constants.TRADE_BUFFER];
		int bytes = 0;
		
		while(true) {
			try {
				bytes = in.read(buffer);
				if(bytes == 1 && buffer[0] == -1)
					handler.obtainMessage(Constants.TRADE_INPUT_MESSAGE, Constants.TRADE_RECEIVE_CLOSE, bytes, null).sendToTarget();
				else
					handler.obtainMessage(Constants.TRADE_INPUT_MESSAGE, Constants.TRADE_RECEIVE_MESSAGE, bytes, buffer).sendToTarget();
			}
			catch(IOException ioe) {
				return;
			}
		}
	}
	
	public void speak(byte[] message) throws IOException {
		out.write(message);
	}
}
