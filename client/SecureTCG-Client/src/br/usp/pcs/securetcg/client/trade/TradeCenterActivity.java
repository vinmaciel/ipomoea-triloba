package br.usp.pcs.securetcg.client.trade;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import br.usp.pcs.securetcg.client.ClientPreferences;
import br.usp.pcs.securetcg.client.R;
import br.usp.pcs.securetcg.client.database.CardDAO;
import br.usp.pcs.securetcg.client.deck.CardManagerActivity;
import br.usp.pcs.securetcg.client.model.Card;
import br.usp.pcs.securetcg.library.communication.ISpendCommunication;
import br.usp.pcs.securetcg.library.communication.json.TradeAcceptJson;
import br.usp.pcs.securetcg.library.communication.json.TradeCardJson;
import br.usp.pcs.securetcg.library.communication.json.TradeCardPropertyJson;
import br.usp.pcs.securetcg.library.communication.json.TradeRequestJson;
import br.usp.pcs.securetcg.library.ecash.CompactEcash;
import br.usp.pcs.securetcg.library.ecash.model.Coin;
import br.usp.pcs.securetcg.library.ecash.model.CoinProperty;
import br.usp.pcs.securetcg.library.ecash.model.UPrivateKey;
import br.usp.pcs.securetcg.library.ecash.model.UPublicKey;

import com.google.gson.Gson;

public class TradeCenterActivity extends Activity {

	private static final int TRADE_CARD = 2;
	
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
	
	private ReceiveCommunication receiver = new ReceiveCommunication();
	
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
	
	@Override
	public void onBackPressed() {
		if(connectionAvailable)
			new AlertDialog.Builder(TradeCenterActivity.this)
					.setTitle("Trade Center")
					.setMessage("Keep connection alive?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent tradeIntent = new Intent(TradeCenterActivity.this, CardManagerActivity.class);
							tradeIntent.putExtra(br.usp.pcs.securetcg.client.deck.Constants.DECK_SOURCE, br.usp.pcs.securetcg.client.deck.Constants.DECK_SEND_CARD);
							startActivityForResult(tradeIntent, TRADE_CARD);
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							TradeCenterActivity.this.finish();
						}
					})
					.show();
		else
			TradeCenterActivity.this.finish();
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ClientPreferences prefs = ClientPreferences.get(TradeCenterActivity.this);
		
		if(requestCode == TRADE_CARD) {
			if(resultCode == RESULT_OK) {
				long cardID = data.getLongExtra(br.usp.pcs.securetcg.client.deck.Constants.CARD_SELECTED, -1);
				if(cardID == -1) {
					Toast.makeText(TradeCenterActivity.this, "Unable to choose card", Toast.LENGTH_SHORT).show();
					messageIn.setText(messageIn.getText() + "\nSystem: Unable to choose card.");
				}
				else {
					UPublicKey pku;
					UPrivateKey sku;
					try {
						pku = prefs.getPublicKey();
						sku = prefs.getPrivateKey();
					} catch (IOException e) {
						e.printStackTrace();
						Toast.makeText(TradeCenterActivity.this, "Failed to load key pair.", Toast.LENGTH_SHORT).show();
						messageIn.setText(messageIn.getText() + "\nSystem: Failed to load key pair.");
						return;
					}
					
					Card card = new CardDAO(TradeCenterActivity.this).get(cardID);
					if( CompactEcash.spend_SpenderSide(sku, pku, new SpendCommunication(), (Coin) card, cardID) ) {
						new CardDAO(TradeCenterActivity.this).delete(card);
					}
					else {
						Toast.makeText(TradeCenterActivity.this, "Failed to send card.", Toast.LENGTH_SHORT).show();
						messageIn.setText(messageIn.getText() + "\nSystem: Failed to send card.");
					}
				}
			}
		}
	}
	
	/* OnClick Listeners */
	private View.OnClickListener onClickSendMessage() {
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String message = messageOut.getText().toString();
				messageOut.setText("");
				Log.d("Bluetooth", "MESSAGE: " + message);
				friendConnection.send(message.getBytes());
			}
		};
	}
	
	private class SpendCommunication implements ISpendCommunication {

		@Override
		public String[] spend_request(UPublicKey pku, Coin coin, long cardID) {
			TradeRequestJson tradeJson = new TradeRequestJson();
			tradeJson.setPku(pku.getGu());
			tradeJson.setCardID(cardID);
			
			TradeCardJson cardJson = new TradeCardJson();
			cardJson.setCardID(cardID);
			cardJson.setSerial(coin.getSerial());
			cardJson.setHistory(coin.getHistory());
			
			TradeCardPropertyJson[] propertiesJson = new TradeCardPropertyJson[coin.getProperties().size()];
			for(int i = 0; i < propertiesJson.length; i++) {
				CoinProperty property = (CoinProperty) coin.getProperties().get(i);
				propertiesJson[i] = new TradeCardPropertyJson();
				propertiesJson[i].setR(property.getR());
				propertiesJson[i].setHash(property.getHash());
				propertiesJson[i].setInfo(property.getInfo());
				propertiesJson[i].setTag(property.getTag());
			}
			cardJson.setProperties(propertiesJson);
			
			tradeJson.setCardJson(new Gson().toJson(cardJson, TradeCardJson.class));
			
			String json = new Gson().toJson(tradeJson, TradeRequestJson.class);
			
			String message = Constants.TRADE_PROTOCOL + Constants.TRADE_REQUEST + json;
			friendConnection.send(message.getBytes());
			
			while(!receiver.hasReceived()) {
				synchronized(SpendCommunication.this) {
					try {
						SpendCommunication.this.wait(1000);
					} catch (InterruptedException e) {}
				}
			}
			
			json = receiver.retrieve();
			TradeAcceptJson acceptJson = new Gson().fromJson(json, TradeAcceptJson.class);
			
			return new String[] {acceptJson.getPku(), acceptJson.getTimestamp()};
		}

		@Override
		public String[] spend_resolve(Coin coin, long cardID) {
			TradeCardJson cardJson = new TradeCardJson();
			cardJson.setCardID(cardID);
			cardJson.setSerial(coin.getSerial());
			cardJson.setHistory(coin.getHistory());
			
			TradeCardPropertyJson[] propertiesJson = new TradeCardPropertyJson[coin.getProperties().size()];
			for(int i = 0; i < propertiesJson.length; i++) {
				CoinProperty property = (CoinProperty) coin.getProperties().get(i);
				propertiesJson[i] = new TradeCardPropertyJson();
				propertiesJson[i].setR(property.getR());
				propertiesJson[i].setHash(property.getHash());
				propertiesJson[i].setInfo(property.getInfo());
				propertiesJson[i].setTag(property.getTag());
			}
			cardJson.setProperties(propertiesJson);
			
			String json = new Gson().toJson(cardJson, TradeCardJson.class);
			
			String message = Constants.TRADE_PROTOCOL + Constants.TRADE_SEND + json;
			friendConnection.send(message.getBytes());
			
			while(!receiver.hasReceived()) {
				synchronized(SpendCommunication.this) {
					try {
						SpendCommunication.this.wait(1000);
					} catch (InterruptedException e) {}
				}
			}
			
			String confirmation = receiver.retrieve();
			
			return new String[] {confirmation};
		}
		
	}
	
	private class ReceiveCommunication {
		
		private boolean received = false;
		private String buffer = null;
		
		private boolean hasReceived() {
			return this.received;
		}
		
		private void receive(String message) {
			buffer = message;
			received = true;
		}
		
		private String retrieve() {
			received = false;
			return buffer;
		}
		
		private synchronized void receive_request(String json) {
			final TradeRequestJson tradeJson = new Gson().fromJson(json, TradeRequestJson.class);
			final long cardID = tradeJson.getCardID();
			
			new AlertDialog.Builder(TradeCenterActivity.this)
				.setTitle("Trade Request")
				.setMessage("Accept card with id " + cardID + "?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ClientPreferences prefs = ClientPreferences.get(TradeCenterActivity.this);
						UPublicKey pku;
						try {
							pku = prefs.getPublicKey();
						} catch (IOException e) {
							e.printStackTrace();
							return;
						}
						
						UPublicKey pku2 = new UPublicKey();
						pku2.setGu(tradeJson.getPku());
						
						TradeCardJson cardJson = new Gson().fromJson(tradeJson.getCardJson(), TradeCardJson.class);
						Coin coin = new Coin();
						coin.setSerial(cardJson.getSerial());
						coin.setHistory(cardJson.getHistory());
						
						List<CoinProperty> properties = new LinkedList<CoinProperty>();
						for(TradeCardPropertyJson propertyJson : cardJson.getProperties()) {
							CoinProperty property = new CoinProperty();
							property.setR(propertyJson.getR());
							property.setHash(propertyJson.getHash());
							property.setInfo(propertyJson.getInfo());
							property.setTag(propertyJson.getTag());
							
							properties.add(property);
						}
						coin.setProperties(properties);
						
						TradeAcceptJson acceptJson = new TradeAcceptJson();
						acceptJson.setTimestamp( CompactEcash.spend_ReceiverSide_Key(pku2, coin).toString() );
						acceptJson.setPku(pku.toString());
						String json = new Gson().toJson(acceptJson, TradeAcceptJson.class);
						String message = Constants.TRADE_PROTOCOL + Constants.TRADE_ACCEPT + json;
						friendConnection.send(message.getBytes());
					}
					
				})
				.setNegativeButton("No", null)
				.show();
		}
		
		private synchronized void receive_resolve(String json) {
			ClientPreferences prefs = ClientPreferences.get(TradeCenterActivity.this);
			UPublicKey pku;
			try {
				pku = prefs.getPublicKey();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			TradeCardJson cardJson = new Gson().fromJson(json, TradeCardJson.class);
			Coin coin = new Coin();
			coin.setSerial(cardJson.getSerial());
			coin.setHistory(cardJson.getHistory());
			
			List<CoinProperty> properties = new LinkedList<CoinProperty>();
			for(TradeCardPropertyJson propertyJson : cardJson.getProperties()) {
				CoinProperty property = new CoinProperty();
				property.setR(propertyJson.getR());
				property.setHash(propertyJson.getHash());
				property.setInfo(propertyJson.getInfo());
				property.setTag(propertyJson.getTag());
				
				properties.add(property);
			}
			coin.setProperties(properties);
			
			if( !CompactEcash.spend_ReceiverSide_Receive(pku, coin) ) {
				Toast.makeText(TradeCenterActivity.this, "Coin not accepted.", Toast.LENGTH_LONG).show();
				return;
			}
			else {
				String message = Constants.TRADE_PROTOCOL + Constants.TRADE_CONFIRM + String.valueOf(true);
				friendConnection.send(message.getBytes());
				
				
			}
		}
	}
	
	private static class CustomTradeConnectionHandler extends TradeConnectionHandler {

		WeakReference<TradeCenterActivity> activity;
		int countError = 0;
		
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
			if(countError++ == 5) {
				this.onConnectionClosed();
			}
		}

		@Override
		void onSendOK(String message) {
			activity.get().messageIn.setText(activity.get().messageIn.getText() + "\nMe: " + message);
		}

		@Override
		void onReceive(String message) {
			if(message.startsWith(Constants.TRADE_PROTOCOL)) {
				message = message.substring(Constants.TRADE_PROTOCOL.length());
				if(message.startsWith(Constants.TRADE_REQUEST)) {
					message = message.substring(Constants.TRADE_REQUEST.length());
					activity.get().receiver.receive_request(message);
				}
				else if(message.startsWith(Constants.TRADE_ACCEPT)) {
					message = message.substring(Constants.TRADE_ACCEPT.length());
					activity.get().receiver.receive(message);
				}
				else if(message.startsWith(Constants.TRADE_SEND)) {
					message = message.substring(Constants.TRADE_SEND.length());
					activity.get().receiver.receive_resolve(message);
				}
				else if(message.startsWith(Constants.TRADE_CONFIRM)) {
					message = message.substring(Constants.TRADE_CONFIRM.length());
					activity.get().receiver.receive(message);
				}
			}
			else
				activity.get().messageIn.setText(activity.get().messageIn.getText() + "\nYou: " + message);
		}

		@Override
		void onConnectionClosed() {
			Toast.makeText(activity.get(), "Connection closed", Toast.LENGTH_SHORT).show();
			activity.get().finish();
		}
	}
}
