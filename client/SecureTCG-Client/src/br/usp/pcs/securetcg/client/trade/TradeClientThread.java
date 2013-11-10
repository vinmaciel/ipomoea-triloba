package br.usp.pcs.securetcg.client.trade;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

public class TradeClientThread extends TradeThread {

	private BluetoothAdapter bluetoothAdapter;
	
	public TradeClientThread(BluetoothAdapter bluetoothAdapter, BluetoothDevice bluetoothDevice, Handler handler) throws IOException {
		super();
		this.bluetoothAdapter = bluetoothAdapter;
		this.socket = bluetoothDevice.createRfcommSocketToServiceRecord(Constants.TRADE_UUID);
		this.handler = handler;
	}
	
	@Override
	public void run() {
		bluetoothAdapter.cancelDiscovery();
		
		try {
			socket.connect();
		}
		catch(IOException ioe) {
			try {
				socket.close();
			}
			catch(IOException ioe2) { }
			
			handler.obtainMessage(Constants.TRADE_CONNECTION_MESSAGE, Constants.TRADE_SERVER_ERROR, 0, null).sendToTarget();
			return;
		}
		
		try {
			comm = new TradeCommunication(socket);
		} catch (IOException e) {
			handler.obtainMessage(Constants.TRADE_CONNECTION_MESSAGE, Constants.TRADE_SERVER_ERROR, 0, null).sendToTarget();
			return;
		}
		
		handler.obtainMessage(Constants.TRADE_CONNECTION_MESSAGE, Constants.TRADE_SERVER_OK, 0, null).sendToTarget();
		
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
	
	@Override
	public void cancel() {
		try {
			if(comm != null) comm.speak(new byte[] {(byte) 0xFF});
		}
		catch(IOException ioe) { }
		try {
			socket.close();
		}
		catch(IOException ioe) { }
	}
}
