package br.usp.pcs.securetcg.client.market;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import br.usp.pcs.securetcg.client.R;
import br.usp.pcs.securetcg.client.database.CardClassDAO;
import br.usp.pcs.securetcg.client.deck.CardInfoActivity;
import br.usp.pcs.securetcg.client.model.CardClass;
import br.usp.pcs.securetcg.library.communication.json.MarketCardJson;
import br.usp.pcs.securetcg.library.communication.json.MarketCardSetJson;
import br.usp.pcs.securetcg.library.ecash.model.Wallet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class MarketActivity extends Activity {
	
	/* UI Objects */
	private TextView progressText;
	private ListView cardList;
	
	/* Menu Objects */
	private MenuItem viewInfo;
	private MenuItem buyCard;
	
	/* List Objects */
	private List<CardClass> cards;
	private CardAdapter cardAdapter;
	private int selected = -1;
	
	/* Connection Objects */
	private GetCardsFromMarketTask marketTask;
	private Handler handler;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.market_activity);
		
		cards = new LinkedList<CardClass>();
		
		getLayoutObjects();
		setLayoutObjects();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		cardAdapter.notifyDataSetChanged();
	}
	
	/* Menu methods */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		viewInfo = menu.add("View card info");
		viewInfo.setOnMenuItemClickListener(new OnClickViewInfo());
		
		buyCard = menu.add("Buy this card");
		buyCard.setOnMenuItemClickListener(new OnClickBuy());
		
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(selected == -1) {
			viewInfo.setEnabled(false);
			buyCard.setEnabled(false);
		}
		else {
			viewInfo.setEnabled(true);
			buyCard.setEnabled(true);
		}
		
		return true;
	}
	
	/* UI methods */
	private void getLayoutObjects() {
		progressText = (TextView) findViewById(R.id.market_progress_text);
		cardList = (ListView) findViewById(R.id.market_card_list);
	}
	
	private void setLayoutObjects() {
		cardAdapter = new CardAdapter();
		cardList.setAdapter(cardAdapter);
		cardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public void onItemClick(AdapterView<?> parent, View child, int position, long id) {
				if(selected != position)
					selected = position;
				else
					selected = -1;
				cardAdapter.notifyDataSetChanged();
				
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					invalidateOptionsMenu();
			}
		});
		
		progressText.setText(getResources().getString(R.string.market_get_cards));
		progressText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(marketTask == null || marketTask.getStatus() != AsyncTask.Status.RUNNING) {
					marketTask = new GetCardsFromMarketTask();
					marketTask.execute((long) -1);
				}
			}
		});
	}
	
	/* List methods */
	private class CardAdapter extends ArrayAdapter<CardClass> {
		
		LayoutInflater inflater = getLayoutInflater();
		
		CardAdapter() {
			super(MarketActivity.this, R.layout.card_row, cards);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			
			if(row == null) {
				row = inflater.inflate(R.layout.card_row, parent, false);
			}
			
			LinearLayout background = (LinearLayout) row.findViewById(R.id.card_row_background);
			TextView name = (TextView) row.findViewById(R.id.card_row_name);
			TextView description = (TextView) row.findViewById(R.id.card_row_description);
			ImageView thumbnail = (ImageView) row.findViewById(R.id.card_row_thumbnail);
			
			name.setText("" + cards.get(position).getName());
			description.setText("" + cards.get(position).getDescription());
			String path = cards.get(position).getBitmapPath();
			
			if(path != null) {
				Bitmap bitmap = BitmapFactory.decodeFile(path);
				Matrix matrix = new Matrix();
				matrix.setScale(0.3f, 0.3f);
				thumbnail.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false));
			}
			background.setBackgroundColor(selected == position ? getResources().getColor(R.color.list_selected) : getResources().getColor(R.color.transparent));
			
			return row;
		}
		
	}
	
	/* OnClick Listeners */
	private class OnClickViewInfo implements MenuItem.OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			long cardID = cards.get(selected).getId();
			Intent cardInfoIntent = new Intent(MarketActivity.this, CardInfoActivity.class);
			cardInfoIntent.putExtra(br.usp.pcs.securetcg.client.deck.Constants.CARD_SELECTED, cardID);
			startActivity(cardInfoIntent);
			return false;
		}
		
	}
	
	private class OnClickBuy implements MenuItem.OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			handler = new CustomWithdrawHandler(MarketActivity.this);
			WithdrawThread withdraw = new WithdrawThread(selected, getApplicationContext(), handler);
			withdraw.start();
			return false;
		}
		
	}
	
	private void progressUpdate(String message) {
		progressText.setText(message);
	}
	
	private static class CustomWithdrawHandler extends WithdrawHandler {

		private WeakReference<MarketActivity> activity;
		
		CustomWithdrawHandler(MarketActivity activity) {
			super();
			this.activity = new WeakReference<MarketActivity>(activity);
		}
		
		@Override
		public void onStart() {
			activity.get().progressUpdate("Buying card");
		}

		@Override
		public void onRequestFailed() {
			activity.get().progressUpdate("Unable to connect to mint");
		}

		@Override
		public void onSolutionFailed() {
			activity.get().progressUpdate("Unable to authenticate");
		}

		@Override
		public void onWithdraw(Wallet wallet) {
			activity.get().progressUpdate("New card available");
			// TODO Auto-generated method stub
		}
		
	}
	
	private class GetCardsFromMarketTask extends AsyncTask<Long, CardClass, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressText.setText(getResources().getString(R.string.market_start));
			cards.removeAll(cards);
		}
		
		@Override
		protected Boolean doInBackground(Long... params) {
			CardClass[] retrievedCards;
			try {
				String query = "?" + "cardId=" + params[0] + "&" + "downloadImage=" + false;
				URL url = new URL(Constants.MARKET_URL + query);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				
				synchronized(this){
					try {
						this.wait(1000);
					} catch (InterruptedException e) {}
				}
				
				byte[] buffer = new byte[1000000];
				InputStream in = connection.getInputStream();
				
				synchronized(this){
					try {
						this.wait(2500);
					} catch (InterruptedException e) {}
				}
				
				int bytes = in.read(buffer);
				String json = new String(buffer, 0, bytes);
				MarketCardSetJson cardSetJson = new Gson().fromJson(json, MarketCardSetJson.class);
				MarketCardJson[] cardJsons = cardSetJson.getCardSet();
				
				retrievedCards = new CardClass[cardJsons.length];
				
				for(int i = 0; i < cardJsons.length; i++) {
					CardClass cardClass = new CardClass();
					cardClass.setId(cardJsons[i].getId());
					cardClass.setName(cardJsons[i].getName());
					cardClass.setDescription(cardJsons[i].getDescription());
					
					retrievedCards[i] = cardClass;
					
					publishProgress(cardClass);
				}
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} catch(JsonSyntaxException e) {
				e.printStackTrace();
				return false;
			}
			
			//Download image
			for(CardClass card : retrievedCards) {
				CardClass classStored = new CardClassDAO(MarketActivity.this).get(card.getId());
				
				if(classStored == null || classStored.getBitmapPath() == null) {
					
					try {
						String query = "?" + "cardId=" + card.getId() + "&" + "downloadImage=" + true;
						URL url = new URL(Constants.MARKET_URL + query);
						HttpURLConnection connection = (HttpURLConnection) url.openConnection();
						
						InputStream in = connection.getInputStream();
						Bitmap bitmap = BitmapFactory.decodeStream(in);
						if(bitmap == null) {
							Log.d("Market", "Failed to get " + card.getId() + "\'s bitmap.");
							continue;
						}
						
						String path = MarketActivity.this.getFilesDir().getAbsolutePath();
						path += "/" + card.getName() + ".png";
						FileOutputStream out = new FileOutputStream(path);
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
						out.close();
						
						card.setBitmapPath(path);
						publishProgress(card);
						
					} catch (MalformedURLException e) {
						e.printStackTrace();
						continue;
					} catch (IOException e) {
						e.printStackTrace();
						continue;
					}
				}
			}
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result) {
				progressText.setText(getResources().getString(R.string.market_finish));
			}
			else
				progressText.setText(getResources().getString(R.string.market_error));
		}
		
		@Override
		protected void onProgressUpdate(CardClass... values) {
			super.onProgressUpdate(values);
			
			CardClassDAO classDAO = new CardClassDAO(MarketActivity.this);
			
			if(classDAO.get(values[0].getId()) == null)	classDAO.add(values[0]);
			else	classDAO.update(values[0]);
			
			if(!cards.contains(values[0]))	cards.add(values[0]);
			else	cards.set(cards.indexOf(values[0]), values[0]);
			
			cardAdapter.notifyDataSetChanged();
		}
		
	}
}
