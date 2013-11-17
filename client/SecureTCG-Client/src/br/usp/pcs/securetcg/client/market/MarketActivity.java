package br.usp.pcs.securetcg.client.market;

import java.util.LinkedList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
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
import br.usp.pcs.securetcg.client.deck.Constants;
import br.usp.pcs.securetcg.client.model.CardClass;

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
			cardInfoIntent.putExtra(Constants.CARD_SELECTED, cardID);
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
}
