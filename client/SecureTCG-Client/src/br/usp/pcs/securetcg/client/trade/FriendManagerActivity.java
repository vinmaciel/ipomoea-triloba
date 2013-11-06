package br.usp.pcs.securetcg.client.trade;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import br.usp.pcs.securetcg.client.R;

public class FriendManagerActivity extends Activity {
	
	/* UI Objects */
	private Button findBluetoothDeviceButton;
	private Button discoverBluetoothButton;
	private ListView friendList;
	
	/* List Objects */
	private List<BluetoothDevice> friends = new ArrayList<BluetoothDevice>();
	private ArrayAdapter<BluetoothDevice> friendListAdapter;
	
	/* Bluetooth Objects */
	private final BroadcastReceiver bluetoothReceiver = new BluetoothBroadcastReceiver();
	private BluetoothAdapter bluetoothAdapter;
	private static final int ENABLE_BLUETOOTH = 1;
	private static final int DISCOVERABLE_BLUETOOTH = 2;
	private boolean discovering = false;
	
	
	/* Life-cycle Methods */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_manager_activity);
		
		getObjects();
		setObjects();
		
		setupBluetooth();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateDeviceDiscovery();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		registerReceiver(bluetoothReceiver, filter);
	}
	
	@Override
	public void onPause() {
		if(discovering) bluetoothAdapter.cancelDiscovery();
		unregisterReceiver(bluetoothReceiver);
		super.onPause();
	}
	
	/* Layout Methods */
	private void getObjects() {
		findBluetoothDeviceButton = (Button) findViewById(R.id.friend_manager_find_button);
		discoverBluetoothButton = (Button) findViewById(R.id.friend_manager_discover_button);
		friendList = (ListView) findViewById(R.id.friend_manager_friend_list);
	}
	
	private void setObjects() {
		findBluetoothDeviceButton.setOnClickListener(onClickFindBluetooth());
		discoverBluetoothButton.setOnClickListener(onClickDiscoverBluetooth());
		friendListAdapter = new FriendAdapter();
		friendList.setAdapter(friendListAdapter);
		friendList.setOnItemClickListener(onClickConnectBluetooth());
	}
	
	private void updateDeviceDiscovery() {
		if(discovering) {
			findBluetoothDeviceButton.setText(R.string.bluetooth_start_find_devices);
			bluetoothAdapter.cancelDiscovery();
		}
		else {
			findBluetoothDeviceButton.setText(R.string.bluetooth_stop_find_devices);
			bluetoothAdapter.startDiscovery();
		}
	}
	
	/* List Methods */
	private class FriendAdapter extends ArrayAdapter<BluetoothDevice> {
		
		LayoutInflater inflater = getLayoutInflater();
		
		FriendAdapter() {
			super(FriendManagerActivity.this, R.layout.friend_row, friends);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			
			if(row == null)
				row = inflater.inflate(R.layout.friend_row, parent, false);
			
			TextView name = (TextView) row.findViewById(R.id.friend_row_name);
			TextView status = (TextView) row.findViewById(R.id.friend_row_status);
			
			name.setText("" + friends.get(position).getName());
			
			switch(friends.get(position).getBondState()) {
			case BluetoothDevice.BOND_BONDED:
				status.setText("Paired");
				break;
			case BluetoothDevice.BOND_NONE:
				status.setText("In range");
				break;
			default:
				//nop
			}
			
			return row;
		}
		
		@Override
		public int getCount() {
			return friends.size();
		}
	}
	
	/* OnClick Listeners */
	private View.OnClickListener onClickFindBluetooth() {
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateDeviceDiscovery();
				discovering = !discovering;
			}
		};
	}
	
	private View.OnClickListener onClickDiscoverBluetooth() {
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent discoverable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 30);
				startActivityForResult(discoverable, DISCOVERABLE_BLUETOOTH);
			}
		};
	}
	
	private AdapterView.OnItemClickListener onClickConnectBluetooth() {
		return new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final BluetoothDevice friendConnected = friends.get(position);
				final Context context = parent.getContext();
				
				new AlertDialog.Builder(parent.getContext())
					.setTitle("Bluetooth connection")
					.setMessage("Connect with " + friends.get(position).getName() + "as...")
					.setPositiveButton("Server", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(context, TradeCenterActivity.class);
							intent.putExtra(Constants.TRADE_CONNECTED_DEVICE, friendConnected);
							intent.putExtra(Constants.TRADE_CONNECTION_TYPE, Constants.TRADE_CONNECTION_SERVER);
							startActivity(intent);
						}
					})
					.setNeutralButton("Client", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(context, TradeCenterActivity.class);
							intent.putExtra(Constants.TRADE_CONNECTED_DEVICE, friendConnected);
							intent.putExtra(Constants.TRADE_CONNECTION_TYPE, Constants.TRADE_CONNECTION_CLIENT);
							startActivity(intent);
						}
					})
					.setNegativeButton("Cancel", null)
					.show();
			}
		};
	}
	
	/* Bluetooth Methods */
	private void setupBluetooth() {
		if(bluetoothAdapter == null) {
			bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if(bluetoothAdapter == null) {
				Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
		
		if(!bluetoothAdapter.isEnabled()) {
			Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enable, ENABLE_BLUETOOTH);
		}
		else {
			getPairedDevices();
		}
	}
	
	private void getPairedDevices() {
		try {
			friends.addAll(bluetoothAdapter.getBondedDevices());
			friendListAdapter.notifyDataSetChanged();
		}
		catch(NullPointerException npe) {
			Toast.makeText(this, "Error requesting bonded devices", Toast.LENGTH_SHORT).show();
		}
	}
	
	private class BluetoothBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(BluetoothDevice.ACTION_FOUND)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if(!friends.contains(device)) {
					friends.add(device);
				}
				friendListAdapter.notifyDataSetChanged();
			}
			else if(action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
				if(intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0) != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
					discoverBluetoothButton.setText(R.string.bluetooth_start_discovery);
				discoverBluetoothButton.setEnabled(true);
			}
		}
		
	}
	
	/* Return methods */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
		case ENABLE_BLUETOOTH:
			if(resultCode == RESULT_OK)
				getPairedDevices();
			else {
				Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_SHORT).show();
				finish();
			}
			break;
		case DISCOVERABLE_BLUETOOTH:
			if(resultCode != RESULT_CANCELED) {
				discoverBluetoothButton.setText(R.string.bluetooth_stop_discovery);
				discoverBluetoothButton.setEnabled(false);
				Toast.makeText(this, "Device discovered for " + resultCode + " seconds", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			//nop
		}
	}
	
}
