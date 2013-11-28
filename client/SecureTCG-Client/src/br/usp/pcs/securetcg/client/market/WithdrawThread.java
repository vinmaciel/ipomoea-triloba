package br.usp.pcs.securetcg.client.market;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import br.usp.pcs.securetcg.client.ClientPreferences;
import br.usp.pcs.securetcg.library.communication.ICommunication;
import br.usp.pcs.securetcg.library.communication.json.WithdrawChallengeJson;
import br.usp.pcs.securetcg.library.communication.json.WithdrawRequestJson;
import br.usp.pcs.securetcg.library.communication.json.WithdrawSolveJson;
import br.usp.pcs.securetcg.library.communication.json.WithdrawWalletJson;
import br.usp.pcs.securetcg.library.ecash.CompactEcash;
import br.usp.pcs.securetcg.library.ecash.model.UPrivateKey;
import br.usp.pcs.securetcg.library.ecash.model.UPublicKey;
import br.usp.pcs.securetcg.library.ecash.model.Wallet;

import com.google.gson.Gson;

public class WithdrawThread extends Thread {

	private long cardID;
	
	private ClientPreferences prefs;
	private Handler handler;
	
	public WithdrawThread(long cardID, Context context, Handler handler) {
		super();
		this.cardID = cardID;
		this.prefs = ClientPreferences.get(context);
		this.handler = handler;
	}
	
	@Override
	public void run() {
		Message message = handler.obtainMessage(WithdrawHandler.STATE_START);
		message.sendToTarget();
		
		try {
			UPrivateKey pku = prefs.getPrivateKey();
			UPublicKey sku = prefs.getPublicKey();
			
			Wallet wallet = CompactEcash.withdraw_UserSide(pku, sku, new WithdrawCommunication(), new BigInteger(String.valueOf(this.cardID)));

			message = handler.obtainMessage(WithdrawHandler.STATE_DONE, 0, 0, wallet);
			message.sendToTarget();
		} catch(IOException e) {
			
		}
	}
	
	private class WithdrawCommunication implements ICommunication {
		
		private String cookie = null;
		
		@Override
		public String[] withdraw_request(String[] message) {
			try {
				String query = "?" + "option=request";
				URL url = new URL(Constants.WITHDRAW_URL + query);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoOutput(true);
				
				WithdrawRequestJson request = new WithdrawRequestJson();
				request.setCardID(Long.valueOf(message[0]));
				request.setJ(message[1] == null ? 1 : Long.valueOf(message[1]));
				request.setCommitment(new BigInteger(message[2]).toByteArray());
				request.setPku(new BigInteger(message[3]).toByteArray());
				request.setTr(new byte[][] {	new BigInteger(message[4]).toByteArray(),
												new BigInteger(message[5]).toByteArray(),
												new BigInteger(message[6]).toByteArray(),
												new BigInteger(message[7]).toByteArray(),
												new BigInteger(message[8]).toByteArray()	});
				String json = new Gson().toJson(request, WithdrawRequestJson.class);
				
				List<NameValuePair> values = new ArrayList<NameValuePair>();
				values.add(new BasicNameValuePair("json", json));
				String params = URLEncodedUtils.format(values, "UTF-8");
				
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setRequestProperty("Content-Length", String.valueOf(params.getBytes().length));
				
				OutputStream out = connection.getOutputStream();
				out.write(params.getBytes());
				
				byte[] buffer = new byte[1000000];
				Log.d("Withdraw", "withdraw request with response code: " + connection.getResponseCode());
				InputStream in = connection.getInputStream();
				int bytes = in.read(buffer);
				in.close();
				
				List<String> gotCookie = connection.getHeaderFields().get("Set-Cookie");
				if(gotCookie != null)
					for(String cookie : gotCookie)
						this.cookie = cookie;
				
				json = new String(buffer, 0, bytes);
				WithdrawChallengeJson challengeJson = new Gson().fromJson(json, WithdrawChallengeJson.class);
				byte[][] challenge = challengeJson.getChallenges();
				
				String[] response = new String[challenge.length];
				for(int i = 0; i < challenge.length; i++)
					response[i] = new BigInteger(challenge[i]).toString();
				
				return response;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				Message handlerMessage = handler.obtainMessage(WithdrawHandler.STATE_REQUEST_FAILED);
				handlerMessage.sendToTarget();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				Message handlerMessage = handler.obtainMessage(WithdrawHandler.STATE_REQUEST_FAILED);
				handlerMessage.sendToTarget();
				return null;
			}
		}
		
		@Override
		public String[] withdraw_solve(String[] message) {
			try {
				String query =	"?" + "option=solve";
				URL url = new URL(Constants.WITHDRAW_URL + query);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoOutput(true);
				
				WithdrawSolveJson solve = new WithdrawSolveJson();
				solve.setSr(new byte[][] {	new BigInteger(message[0]).toByteArray(),
											new BigInteger(message[1]).toByteArray(),
											new BigInteger(message[2]).toByteArray(),
											new BigInteger(message[3]).toByteArray(),
											new BigInteger(message[4]).toByteArray()	});
				String json = new Gson().toJson(solve, WithdrawSolveJson.class);
				
				List<NameValuePair> values = new ArrayList<NameValuePair>();
				values.add(new BasicNameValuePair("json", json));
				String params = URLEncodedUtils.format(values, "UTF-8");
				
				if(this.cookie != null && !this.cookie.equals("")) connection.setRequestProperty("Cookie", this.cookie);
				
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setRequestProperty("Content-Length", String.valueOf(params.getBytes().length));
				OutputStream out = connection.getOutputStream();
				out.write(params.getBytes());
				
				byte[] buffer = new byte[1000000];
				Log.d("Withdraw", "withdraw solve with response code: " + connection.getResponseCode());
				InputStream in = connection.getInputStream();
				int bytes = in.read(buffer);
				in.close();
				
				json = new String(buffer, 0, bytes);
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
				Message handlerMessage = handler.obtainMessage(WithdrawHandler.STATE_SOLUTION_FAILED);
				handlerMessage.sendToTarget();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				Message handlerMessage = handler.obtainMessage(WithdrawHandler.STATE_SOLUTION_FAILED);
				handlerMessage.sendToTarget();
				return null;
			}
		}
		
	}
	
}
