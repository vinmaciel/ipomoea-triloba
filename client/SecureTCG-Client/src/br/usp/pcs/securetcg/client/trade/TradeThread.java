package br.usp.pcs.securetcg.client.trade;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public abstract class TradeThread extends Thread {

	protected BluetoothSocket socket;
	
	protected Handler handler;
	
	protected TradeCommunication comm;
	
	@Override
	public abstract void run();
	
	public abstract void send(byte[] message);
	
	public abstract void cancel();
}
