package br.usp.pcs.securetcg.client.trade;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class TradeServerThread extends Thread {

	private final BluetoothServerSocket bluetoothServerSocket;
	private BluetoothSocket serverSocket = null;
	
	private Handler handler;
	
	private TradeCommunication comm;
	
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
				serverSocket = bluetoothServerSocket.accept();
			}
			catch(IOException ioe) {
				handler.obtainMessage(Constants.TRADE_CONNECTION_MESSAGE, Constants.TRADE_SERVER_ERROR, 0, null).sendToTarget();
				break;
			}
			
			if(serverSocket != null) {
				try {
					comm = new TradeCommunication(serverSocket);
				} catch (IOException e) {
					handler.obtainMessage(Constants.TRADE_CONNECTION_MESSAGE, Constants.TRADE_SERVER_ERROR, 0, null).sendToTarget();
					break;
				}
				
				handler.obtainMessage(Constants.TRADE_CONNECTION_MESSAGE, Constants.TRADE_SERVER_OK, 0, null).sendToTarget();
				break;
			}
		}
		
		if(comm != null) comm.listen(handler);
	}
	
	public void send(byte[] message) {
		try {
			comm.speak(message);
		} catch (IOException e) {
			handler.obtainMessage(Constants.TRADE_OUTPUT_MESSAGE, Constants.TRADE_SEND_ERROR, 0, null).sendToTarget();
			return;
		}
		handler.obtainMessage(Constants.TRADE_OUTPUT_MESSAGE, Constants.TRADE_SEND_OK, 0, null).sendToTarget();
	}
	
	public void cancel() {
		try {
			if(serverSocket != null) serverSocket.close();
			bluetoothServerSocket.close();
		} catch (IOException ioe) { }
	}
	
}
