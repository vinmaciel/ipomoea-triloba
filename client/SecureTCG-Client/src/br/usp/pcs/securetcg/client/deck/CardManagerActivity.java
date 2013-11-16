package br.usp.pcs.securetcg.client.deck;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import br.usp.pcs.securetcg.client.R;

public class CardManagerActivity extends Activity {
	
	/** UI Objects **/
	private ListView cardList;
	
	/** Menu Objects **/
	private MenuItem addCard;
	private MenuItem removeCards;
	private MenuItem viewInfo;
	
	/** List Objects **/
	private List<Object> cards;
	private boolean[] selected;
	private int selectionSize;
	
	/** Constants **/
	private static final int ADD_CARDS = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_manager_activity);
		
		// TODO instantiate cards from database
		selected = new boolean[cards.size()];
		for(int i = 0; i < selected.length; i++) selected[i] = false;
		selectionSize = 0;
		
		getLayoutObjects();
		setLayoutObjects();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		addCard = menu.add("Add card");
		removeCards = menu.add("Remove card");
		viewInfo = menu.add("View card info");
		
		addCard.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(CardManagerActivity.this, CardManagerActivity.class);
				// TODO try calling same activity (-1 is for all cards)
				intent.putExtra("DECK", -1);
				startActivityForResult(intent, ADD_CARDS);
				
				return false;
			}
		});
		
		removeCards.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO remove cards from list and database
				return false;
			}
		});
		
		viewInfo.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				startActivity(new Intent(CardManagerActivity.this, CardInfoActivity.class));
				return false;
			}
		});
		
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(selectionSize > 0) removeCards.setEnabled(true);
		else removeCards.setEnabled(false);
		
		if(selectionSize == 1) viewInfo.setEnabled(true);
		else viewInfo.setEnabled(false);
		
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case ADD_CARDS:
			// TODO retrieve cards from data or a memory class.
			
		default:
			// TODO handle error
		}
	}
	
	public void getLayoutObjects() {
		cardList = (ListView) findViewById(R.id.card_manager_card_list);
	}
	
	public void setLayoutObjects() {
		cardList.setAdapter(new CardAdapter());
		cardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View child, int position, long id) {
				if(!selected[position]) {
					selected[position] = true;
					selectionSize++;
				}
				else {
					selected[position] = false;
					selectionSize--;
				}
			}
		});
	}
	
	private class CardAdapter extends ArrayAdapter<Object> {
		
		LayoutInflater inflater = getLayoutInflater();
		
		CardAdapter() {
			super(CardManagerActivity.this, R.layout.card_row, cards);
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
			background.setBackgroundColor(selected[position] ? getResources().getColor(R.color.list_selected) : getResources().getColor(R.color.transparent));
			
			return row;
		}
	}
}
