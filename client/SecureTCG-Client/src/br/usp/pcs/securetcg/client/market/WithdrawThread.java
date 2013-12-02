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
import br.usp.pcs.securetcg.library.clsign.CLPublicKey;
import br.usp.pcs.securetcg.library.clsign.CLSign;
import br.usp.pcs.securetcg.library.clsign.CLSignature;
import br.usp.pcs.securetcg.library.communication.ICommunication;
import br.usp.pcs.securetcg.library.communication.json.WithdrawChallengeJson;
import br.usp.pcs.securetcg.library.communication.json.WithdrawRequestJson;
import br.usp.pcs.securetcg.library.communication.json.WithdrawSolveJson;
import br.usp.pcs.securetcg.library.communication.json.WithdrawWalletJson;
import br.usp.pcs.securetcg.library.ecash.CompactEcash;
import br.usp.pcs.securetcg.library.ecash.model.Coin;
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
			UPrivateKey sku = prefs.getPrivateKey();
			UPublicKey pku = prefs.getPublicKey();
			
			Wallet wallet = CompactEcash.withdraw_UserSide(sku, pku, new WithdrawCommunication(), new BigInteger(String.valueOf(this.cardID)));
			
			CLSignature sig = new CLSignature();
			sig.setA(wallet.getSigA());
			sig.setE(wallet.getSigE());
			sig.setV(wallet.getSigV());
			CLPublicKey pkb = prefs.getBankKey();
			if( !CLSign.verify(new byte[][] {sku.getU(), wallet.getS(), wallet.getT(), wallet.getJ(), wallet.getX(), wallet.getQ()}, sig, pkb) ) {
				message = handler.obtainMessage(WithdrawHandler.STATE_SIGNATURE_ERROR, 0, 0, null);
				message.sendToTarget();
				return;
			}
			
			Coin coin = CompactEcash.spend_Wallet(sku, pku, wallet);
			
			message = handler.obtainMessage(WithdrawHandler.STATE_DONE, 0, 0, new Object[] {coin, cardID});
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
				
				synchronized(this){
					try {
						this.wait(1000);
					} catch (InterruptedException e) {}
				}
				
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
				
				int responseCode = connection.getResponseCode();
				Log.d("Withdraw", "withdraw request with response code: " + responseCode);
				if(responseCode != 202) throw new IOException();
				
				byte[] buffer = new byte[1000000];
				InputStream in = connection.getInputStream();
				
				synchronized(this){
					try {
						this.wait(2500);
					} catch (InterruptedException e) {}
				}
				
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
				
				synchronized(this){
					try {
						this.wait(1000);
					} catch (InterruptedException e) {}
				}
				
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
				
				int responseCode = connection.getResponseCode();
				Log.d("Withdraw", "withdraw request with response code: " + responseCode);
				if(responseCode != 200) throw new IOException();
				
				byte[] buffer = new byte[1000000];
				InputStream in = connection.getInputStream();
				
				synchronized(this){
					try {
						this.wait(2500);
					} catch (InterruptedException e) {}
				}
				
				int bytes = in.read(buffer);
				in.close();
				
				if(bytes == -1)	{
					throw new IOException();
				}
				
				json = new String(buffer, 0, bytes);
				WithdrawWalletJson walletJson = new Gson().fromJson(json, WithdrawWalletJson.class);
				byte[] serial = walletJson.getSerialComponent();
				byte[] sigA = walletJson.getSignatureA();
				byte[] sigE = walletJson.getSignatureE();
				byte[] sigV = walletJson.getSignatureV();
				
				String[] response = new String[5];
				response[0] = String.valueOf(true);
				response[1] = new BigInteger(serial).toString();
				response[2] = new BigInteger(sigA).toString();
				response[3] = new BigInteger(sigE).toString();
				response[4] = new BigInteger(sigV).toString();
				
				return response;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				Message handlerMessage = handler.obtainMessage(WithdrawHandler.STATE_SOLUTION_FAILED);
				handlerMessage.sendToTarget();
				return new String[] {String.valueOf(false)};
			} catch (IOException e) {
				e.printStackTrace();
				Message handlerMessage = handler.obtainMessage(WithdrawHandler.STATE_SOLUTION_FAILED);
				handlerMessage.sendToTarget();
				return new String[] {String.valueOf(false)};
			}
		}

		@Override
		public String[] spend_request(String[] message) {
			return null;
		}
		
	}
	
}
