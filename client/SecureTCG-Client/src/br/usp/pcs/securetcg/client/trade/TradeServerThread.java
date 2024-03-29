package br.usp.pcs.securetcg.client.trade;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.os.Handler;

public class TradeServerThread extends TradeThread {

	private final BluetoothServerSocket bluetoothServerSocket;
	
	public TradeServerThread(BluetoothAdapter bluetoothAdapter, Handler handler) throws IOException {
		super();
		this.bluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(Constants.TRADE_SESSION, Constants.TRADE_UUID);
		this.handler = handler;
	}
	
	@Override
	public void run() {
		// Yes, I really want my socket
		while(true) {
			try {
				socket = bluetoothServerSocket.accept();
			}
			catch(IOException ioe) {
				handler.obtainMessage(Constants.TRADE_CONNECTION_MESSAGE, Constants.TRADE_SERVER_ERROR, 0, null).sendToTarget();
				break;
			}
			
			if(socket != null) {
				try {
					bluetoothServerSocket.close();
					comm = new TradeCommunication(socket);
				} catch (IOException e) {
					handler.obtainMessage(Constants.TRADE_CONNECTION_MESSAGE, Constants.TRADE_SERVER_ERROR, 0, null).sendToTarget();
					return;
				}
				
				handler.obtainMessage(Constants.TRADE_CONNECTION_MESSAGE, Constants.TRADE_SERVER_OK, 0, null).sendToTarget();
				break;
			}
		}
		
		if(comm != null) comm.listen(handler);
	}
	
	@Override
	public void send(byte[] message) {
		try {
			comm.speak(message);
		} catch (IOException e) {
			handler.obtainMessage(Constants.TRADE_OUTPUT_MESSAGE, Constants.TRADE_SEND_ERROR, 0, null).sendToTarget();
			return;
		}
		handler.obtainMessage(Constants.TRADE_OUTPUT_MESSAGE, Constants.TRADE_SEND_OK, 0, message).sendToTarget();
	}
	
	public void cancel() {
		try {
			if(comm != null) comm.speak(new byte[] {(byte) 0xFF});
		} catch (IOException ioe) { }
		try {
			if(socket != null) socket.close();
		} catch (IOException ioe) { }
		try {
			bluetoothServerSocket.close();
		} catch (IOException ioe) { }
	}
	
}
