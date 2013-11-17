package br.usp.pcs.securetcg.client.market;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import br.usp.pcs.securetcg.client.deck.CardInfoActivity;
import br.usp.pcs.securetcg.client.model.CardClass;
import br.usp.pcs.securetcg.library.communication.json.MarketCardJson;
import br.usp.pcs.securetcg.library.communication.json.MarketCardSetJson;

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
	public void getLayoutObjects() {
		progressText = (TextView) findViewById(R.id.market_progress_text);
		cardList = (ListView) findViewById(R.id.market_card_list);
	}
	
	public void setLayoutObjects() {
		cardAdapter = new CardAdapter();
		cardList.setAdapter(cardAdapter);
		cardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public void onItemClick(AdapterView<?> parent, View child, int position, long id) {
				if(selected == -1)
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
			
			name.setText("" + cards.get(position));
			description.setText("" + cards.get(position));
			//TODO set image to thumbnail
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
			// TODO withdraw card
			return false;
		}
		
	}
	
	private class GetCardsFromMarketTask extends AsyncTask<Long, String, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			publishProgress(getResources().getString(R.string.market_start));
		}
		
		@Override
		protected Boolean doInBackground(Long... params) {
			String query = "?" + "cardId=" + params[0] + "&" + "downloadImage=" + false;
			try {
				URL url = new URL(Constants.MARKET_URL + query);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				
				int bytes = connection.getContentLength();
				byte[] buffer = new byte[bytes == -1? 1000000 : bytes+1];
				InputStream in = connection.getInputStream();
				bytes = in.read(buffer);
				
				String json = new String(buffer, 0, bytes);
				MarketCardSetJson cardSetJson = new Gson().fromJson(json, MarketCardSetJson.class);
				MarketCardJson[] cardJsons = cardSetJson.getCardSet();
				
				List<CardClass> marketCards = new LinkedList<CardClass>();
				for(MarketCardJson cardJson :  cardJsons) {
					CardClass cardClass = new CardClass();
					cardClass.setId(cardJson.getId());
					cardClass.setName(cardJson.getName());
					cardClass.setDescription(cardJson.getDescription());
					
					marketCards.add(cardClass);
				}
				cards = marketCards;
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
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result) {
				publishProgress(getResources().getString(R.string.market_finish));
				cardAdapter.notifyDataSetChanged();
			}
			else
				publishProgress(getResources().getString(R.string.market_error));
		}
		
		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			progressText.setText(values[0]);
		}
		
	}
}
