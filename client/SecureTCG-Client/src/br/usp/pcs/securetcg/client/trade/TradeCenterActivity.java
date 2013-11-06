package br.usp.pcs.securetcg.client.trade;

import java.io.IOException;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import br.usp.pcs.securetcg.client.R;

public class TradeCenterActivity extends Activity {

	private TextView deviceText;
	private TextView messageIn;
	private EditText messageOut;
	private Button sendButton;
	
	private BluetoothAdapter adapter;
	private BluetoothDevice device;
	private String threadInstanceType;
	
	private TradeThread friendConnection = null;
	private Handler handler = new CustomTradeConnectionHandler(this);
	private boolean connectionAvailable = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trade_center_activity);
		
		getExtras(getIntent().getExtras());
		getObjects();
		setObjects();
		
		connect();
	}
	
	@Override
	public void onDestroy() {
		if(friendConnection != null) {
			if(friendConnection instanceof TradeServerThread)
				((TradeServerThread) friendConnection).cancel();
			else
				((TradeClientThread) friendConnection).cancel();
			friendConnection.interrupt();
		}
		super.onDestroy();
	}
	
	private void getExtras(Bundle extras) {
		device = (BluetoothDevice) extras.getParcelable(Constants.TRADE_CONNECTED_DEVICE);
		threadInstanceType = extras.getString(Constants.TRADE_CONNECTION_TYPE);
	}
	
	private void getObjects() {
		deviceText = (TextView) findViewById(R.id.trade_center_device_text);
		messageIn = (TextView) findViewById(R.id.trade_center_message_in);
		messageOut = (EditText) findViewById(R.id.trade_center_message_out);
		sendButton = (Button) findViewById(R.id.trade_center_send_button);
	}
	
	private void setObjects() {
		deviceText.setText(device.getName());
		sendButton.setOnClickListener(onClickSendMessage());
		if(!connectionAvailable) sendButton.setEnabled(false);
	}
	
	private void connect() {
		adapter = BluetoothAdapter.getDefaultAdapter();
		try {
			if(threadInstanceType.equals(Constants.TRADE_CONNECTION_SERVER))
				friendConnection = new TradeServerThread(adapter, handler);
			else
				friendConnection = new TradeClientThread(adapter, device, handler);
		} catch (IOException e) {
			Toast.makeText(this, "Unable to start connection", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		friendConnection.start();
	}
	
	/* OnClick Listeners */
	private View.OnClickListener onClickSendMessage() {
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String message = messageOut.getText().toString();
				friendConnection.send(message.getBytes());
			}
		};
	}
	
	private static class CustomTradeConnectionHandler extends TradeConnectionHandler {

		WeakReference<TradeCenterActivity> activity;
		
		CustomTradeConnectionHandler(TradeCenterActivity activity) {
			super();
			this.activity = new WeakReference<TradeCenterActivity>(activity);
		}

		@Override
		void onServerError() {
			Toast.makeText(activity.get(), "Unable to start server", Toast.LENGTH_SHORT).show();
			activity.get().finish();
		}

		@Override
		void onClientError() {
			Toast.makeText(activity.get(), "Unable to connect to server", Toast.LENGTH_SHORT).show();
			activity.get().finish();
		}

		@Override
		void onServerOK() {
			Toast.makeText(activity.get(), "Server started", Toast.LENGTH_SHORT).show();
			activity.get().connectionAvailable = true;
			activity.get().sendButton.setEnabled(true);
		}

		@Override
		void onClientOK() {
			Toast.makeText(activity.get(), "Connected to server", Toast.LENGTH_SHORT).show();
			activity.get().connectionAvailable = true;
			activity.get().sendButton.setEnabled(true);
		}

		@Override
		void onSendError() {
			Toast.makeText(activity.get(), "Message not sent", Toast.LENGTH_SHORT).show();
		}

		@Override
		void onSendOK(String message) {
			activity.get().messageIn.setText(activity.get().messageIn.getText() + "\nMe: " + message);
		}
	}
}
