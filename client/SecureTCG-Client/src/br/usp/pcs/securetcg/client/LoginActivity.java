package br.usp.pcs.securetcg.client;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import br.usp.pcs.securetcg.client.market.Constants;
import br.usp.pcs.securetcg.library.ecash.model.UPublicKey;

public class LoginActivity extends Activity {
	
	private TextView progressText;
	
	private LoginTask login;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		progressText = (TextView) findViewById(R.id.login_progress);
		
		login = new LoginTask(this.getApplicationContext());
		login.execute();
	}
	
	@Override
	protected void onDestroy() {
		login.cancel(false);
		super.onDestroy();
	}
	
	private void loginSuccess() {
		synchronized (this) {
			try {
				this.wait(2000);
			} catch (InterruptedException e) {}
		}
		startActivity(new Intent(LoginActivity.this, HomeActivity.class));
		finish();
	}
	
	private void loginFailed() {
		Toast.makeText(this, "Unable to log in", Toast.LENGTH_LONG).show();
		synchronized (this) {
			try {
				this.wait(2000);
			} catch (InterruptedException e) {}
		}
		finish();
	}
	
	private class LoginTask extends AsyncTask<Void, String, Void> {

		private ClientPreferences prefs;
		private AccountManager accManager;
		
		private LoginTask(Context context) {
			super();
			this.prefs = ClientPreferences.get(context);
			this.accManager = AccountManager.get(context);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			publishProgress("Looking for saved keys");
			
			UPublicKey pku = null;
			try{
				pku = prefs.getPublicKey();
				if(pku == null) {
					publishProgress("Generating key pair (this action may take several seconds)");
					prefs.createKey();
					pku = prefs.getPublicKey();
				}
				else {
					publishProgress("Key found");
					return null;
				}
			} catch(IOException e) {
				publishProgress("Error");
				return null;
			}
			
			Account[] accs = accManager.getAccountsByType("com.google");
			if(accs == null || accs.length <= 0) {
				publishProgress("Error");
				return null;
			}
			String name = accs[0].name;
			
			publishProgress("Authenticating before mint");
			
			try {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("name", name));
				pairs.add(new BasicNameValuePair("pku", new BigInteger(pku.getGu()).toString()));
				String values = URLEncodedUtils.format(pairs, "UTF-8");
				
				URL url = new URL(Constants.REGISTRATION_URL);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setRequestProperty("Content-Length", String.valueOf(values.getBytes().length));
				
				connection.setDoOutput(true);
				OutputStream os = connection.getOutputStream();
				os.write(values.getBytes());
				os.close();
				
				if(connection.getResponseCode() == 200)
					publishProgress("Done");
				else
					publishProgress("Error");
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				publishProgress("Error");
			} catch (IOException e) {
				e.printStackTrace();
				publishProgress("Error");
			}
			
			return null;
		}
		
		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			progressText.setText(values[0]);
			
			if(values[0].equals("Key found") || values[0].equals("Done")) {
				loginSuccess();
			}
			
			if(values[0].equals("Error")) {
				loginFailed();
			}
		}
	}
}
