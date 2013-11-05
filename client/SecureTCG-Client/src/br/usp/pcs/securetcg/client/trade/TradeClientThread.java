package br.usp.pcs.securetcg.client.trade;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class TradeClientThread extends Thread {

	private BluetoothAdapter bluetoothAdapter;
	private BluetoothSocket clientSocket;
	
	private Handler handler;
	
	private TradeCommunication comm;
	
	public TradeClientThread(BluetoothAdapter bluetoothAdapter, BluetoothDevice bluetoothDevice, Handler handler) throws IOException {
		super();
		this.bluetoothAdapter = bluetoothAdapter;
		this.clientSocket = bluetoothDevice.createRfcommSocketToServiceRecord(Constants.TRADE_UUID);
		this.handler = handler;
	}
	
	@Override
	public void run() {
		bluetoothAdapter.cancelDiscovery();
		
		try {
			clientSocket.connect();
		}
		catch(IOException ioe) {
			try {
				clientSocket.close();
			}
			catch(IOException ioe2) { }
			
			handler.obtainMessage(Constants.TRADE_CONNECTION_MESSAGE, Constants.TRADE_SERVER_ERROR, 0, null).sendToTarget();
			return;
		}
		
		try {
			comm = new TradeCommunication(clientSocket);
		} catch (IOException e) {
			handler.obtainMessage(Constants.TRADE_CONNECTION_MESSAGE, Constants.TRADE_SERVER_ERROR, 0, null).sendToTarget();
			return;
		}
		
		handler.obtainMessage(Constants.TRADE_CONNECTION_MESSAGE, Constants.TRADE_SERVER_OK, 0, null).sendToTarget();
		
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
			clientSocket.close();
		}
		catch(IOException ioe) { }
	}
}
