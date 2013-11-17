package br.usp.pcs.securetcg.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;
import br.usp.pcs.securetcg.client.utils.Constants;

public class LoginActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		TextView progress = (TextView) findViewById(R.id.login_progress);
		PrivateKey sk = null;
		
		try {
			progress.setText("Looking for private key");
			
			FileInputStream fis = new FileInputStream(Environment.getDataDirectory() + "/." + Constants.USER_PRIVATE_KEY);
			ObjectInputStream ois = new ObjectInputStream(fis);
			sk = (PrivateKey) ois.readObject();
			ois.close();
			fis.close();
			
			progress.setText("Key found");
		}
		//PrivateKey has not been created yet.
		catch(FileNotFoundException fnfe) {
			progress.setText("Generating key pair (this action may take several seconds)");
			
			//TODO Generate a user key pair
			
			try {
				FileOutputStream fos = new FileOutputStream(Environment.getDataDirectory() + "/." + Constants.USER_PRIVATE_KEY);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(sk);
				oos.close();
				fos.close();
			}
			//It is not supposed to happen, but...
			catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, "ERROR: Cannot create key file.", Toast.LENGTH_LONG).show();
				finish();
			}
			
			progress.setText("Authenticating before mint");
			
			//TODO Get mint certificate
			
			progress.setText("Done");
		}
		//Problems with deserializing the private key.
		catch(Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "ERROR: Cannot load key file.", Toast.LENGTH_LONG).show();
			finish();
		}
		
		Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
		intent.putExtra(Constants.USER_PRIVATE_KEY, sk);
		startActivity(intent);
	}
}
