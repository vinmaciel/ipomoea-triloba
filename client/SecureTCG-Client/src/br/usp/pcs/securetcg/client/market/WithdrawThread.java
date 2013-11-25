package br.usp.pcs.securetcg.client.market;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.util.Log;
import br.usp.pcs.securetcg.client.ClientPreferences;
import br.usp.pcs.securetcg.library.communication.ICommunication;
import br.usp.pcs.securetcg.library.communication.json.WithdrawChallengeJson;
import br.usp.pcs.securetcg.library.communication.json.WithdrawWalletJson;
import br.usp.pcs.securetcg.library.ecash.CompactEcash;
import br.usp.pcs.securetcg.library.ecash.model.UPrivateKey;
import br.usp.pcs.securetcg.library.ecash.model.UPublicKey;
import br.usp.pcs.securetcg.library.ecash.model.Wallet;

import com.google.gson.Gson;

public class WithdrawThread extends Thread {

	private long cardID;
	
	private ClientPreferences prefs;
	private WithdrawThreadCallback callback;
	
	public WithdrawThread(long cardID, Context context, WithdrawThreadCallback callback) {
		super();
		this.cardID = cardID;
		this.prefs = ClientPreferences.get(context);
		this.callback = callback;
	}
	
	@Override
	public void run() {
		callback.onStart();
		
		try {
			UPrivateKey pku = prefs.getPrivateKey();
			UPublicKey sku = prefs.getPublicKey();
			
			Wallet wallet = CompactEcash.withdraw_UserSide(pku, sku, new WithdrawCommunication(), new BigInteger(String.valueOf(this.cardID)));
			
			callback.onWithdraw(wallet);
		} catch(IOException e) {
			
		}
	}
	
	private class WithdrawCommunication implements ICommunication {
		
		@Override
		public String[] withdraw_request(String[] message) {
			try {
				String query =	"?" + "request" + "&" +
								"card_id=" + message[0] + "&" +
								"wallet_size=" + message[1] + "&" +
								"commitment=" + message[2] + "&" +
								"pku=" + message[3] + "&" +
								"proof=" + message[4] + "&" +
								"tr=" + message[5];
				URL url = new URL(Constants.MARKET_URL + query);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				
				byte[] buffer = new byte[1000000];
				InputStream in = connection.getInputStream();
				int bytes = in.read(buffer);
				in.close();
				
				String json = new String(buffer, 0, bytes);
				WithdrawChallengeJson challengeJson = new Gson().fromJson(json, WithdrawChallengeJson.class);
				byte[][] challenge = challengeJson.getChallenges();
				
				String[] response = new String[challenge.length];
				for(int i = 0; i < challenge.length; i++)
					response[i] = new BigInteger(challenge[i]).toString();
				
				return response;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				callback.onRequestFailed();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				callback.onRequestFailed();
				return null;
			}
		}
		
		@Override
		public String[] withdraw_solve(String[] message) {
			try {
				String query =	"?" + "solve" + "&" +
								"sr=" + message[0];
				URL url = new URL(Constants.MARKET_URL + query);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				
				Log.d("Withdraw", String.valueOf(connection.getResponseCode()));
				byte[] buffer = new byte[1000000];
				InputStream in = connection.getInputStream();
				int bytes = in.read(buffer);
				in.close();
				
				String json = new String(buffer, 0, bytes);
				WithdrawWalletJson walletJson = new Gson().fromJson(json, WithdrawWalletJson.class);
				byte[] serial = walletJson.getSerialComponent();
				byte[] signature = walletJson.getSignature();
				byte[] sigRandom = walletJson.getSignatureRandom();
				
				String[] response = new String[3];
				response[0] = new BigInteger(signature).toString();
				response[1] = new BigInteger(sigRandom).toString();
				response[2] = new BigInteger(serial).toString();
				
				return response;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				callback.onSolutionFailed();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				callback.onSolutionFailed();
				return null;
			}
		}
		
	}
	
}
