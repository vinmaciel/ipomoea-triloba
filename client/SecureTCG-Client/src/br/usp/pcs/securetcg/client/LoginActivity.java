package br.usp.pcs.securetcg.client;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
		try {
			synchronized (this) {
				this.wait(5000);
			}
		} catch (InterruptedException e) {}
		super.onDestroy();
	}
	
	private void loginSuccess() {
		startActivity(new Intent(LoginActivity.this, HomeActivity.class));
	}
	
	private void loginFailed() {
		Toast.makeText(this, "Unable to log in", Toast.LENGTH_LONG).show();
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
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(Constants.REGISTRATION_URL);
				List<NameValuePair> values = new ArrayList<NameValuePair>();
				values.add(new BasicNameValuePair("name", name));
				values.add(new BasicNameValuePair("pku", new BigInteger(pku.getGu()).toString()));
				String s = values.toString();
				Log.d("Login", s);
				post.setEntity(new ByteArrayEntity(s.getBytes()));
				HttpResponse response = client.execute(post);
				Log.d("Login", "code: " + response.getStatusLine().getStatusCode());
				if(response.getStatusLine().getStatusCode() != 200) {
					throw new ClientProtocolException();
				}
				
				publishProgress("Done");
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
			
			if(progressText.equals("Done")) {
				loginSuccess();
			}
			
			if(progressText.equals("Error")) {
				loginFailed();
			}
		}
		
		@Override
		protected void onCancelled() {
			try {
				prefs.destroyKeys();
			} catch (IOException e) {
				publishProgress("Error");
				e.printStackTrace();
			}
			super.onCancelled();
		}
	}
}
