package br.usp.pcs.securetcg.client.trade;

import java.io.IOException;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import br.usp.pcs.securetcg.client.R;

public class TradeCenterActivity extends Activity {

	private TextView deviceText;
	private TextView messageIn;
	private EditText messageOut;
	private Button sendButton;
	
	private BluetoothAdapter adapter;
	private BluetoothDevice device;
	private String threadInstanceClass;
	
	private TradeThread friendConnection = null;
	private Handler handler = new TradeConnectionHandler(this);
	
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
		threadInstanceClass = extras.getString(Constants.TRADE_CONNECTION_TYPE);
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
	}
	
	private void connect() {
		adapter = BluetoothAdapter.getDefaultAdapter();
		try {
			if(threadInstanceClass.equals(Constants.TRADE_CONNECTION_SERVER))
				friendConnection = new TradeServerThread(adapter, handler);
			else
				friendConnection = new TradeClientThread(adapter, device, handler);
		} catch (IOException e) {
			return;
		}
		
		friendConnection.run();
	}
	
	/* OnClick Listeners */
	private View.OnClickListener onClickSendMessage() {
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String message = messageOut.getText().toString();
				messageIn.setText(messageIn.getText() + "\nMe: " + message);
				friendConnection.send(message.getBytes());
			}
		};
	}
}
