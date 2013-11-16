package br.usp.pcs.securetcg.client.deck;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;
import br.usp.pcs.securetcg.client.R;
import br.usp.pcs.securetcg.client.database.CardDAO;
import br.usp.pcs.securetcg.client.database.DeckDAO;
import br.usp.pcs.securetcg.client.model.Card;
import br.usp.pcs.securetcg.client.model.Deck;

public class CardManagerActivity extends Activity {
	
	/* UI Objects */
	private ListView cardList;
	
	/* Menu Objects */
	private MenuItem viewInfo;
	private MenuItem unselectAll;
	private MenuItem addCards;
	private MenuItem removeCards;
	private MenuItem insertCards;
	private MenuItem sendCard;
	
	/* List Objects */
	private List<Card> cards;
	private CardAdapter cardAdapter;
	private boolean[] selected;
	private int selectionSize;
	
	/* Private parameters */
	private int source = -1;
	private Deck deck = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_manager_activity);
		
		source = getIntent().getIntExtra(Constants.DECK_SOURCE, -1);
		if(source == -1) {
			Toast.makeText(this, "Failed to get source", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		getCards();
		selected = new boolean[cards.size()];
		unselectAll();
		
		getLayoutObjects();
		setLayoutObjects();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		cardAdapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		unselectAll = menu.add("Unselect all");
		unselectAll.setOnMenuItemClickListener(new OnClickUnselectAll());
		
		viewInfo = menu.add("View card info");
		viewInfo.setOnMenuItemClickListener(new OnClickViewInfo());
		
		if(source == Constants.DECK_ALL) {
			sendCard = menu.add("Send card");
			sendCard.setOnMenuItemClickListener(new OnClickSendCard());
		}
		
		if(source == Constants.DECK_MANAGEMENT) {
			addCards = menu.add("Add cards");
			addCards.setOnMenuItemClickListener(new OnClickAddCards());
			
			removeCards = menu.add("Remove cards");
			removeCards.setOnMenuItemClickListener(new OnClickRemoveCards());
		}
		
		if(source == Constants.DECK_ADD_CARD) {
			insertCards = menu.add("Insert cards");
			insertCards.setOnMenuItemClickListener(new OnClickInsertCards());
		}
		
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		unselectAll.setEnabled(selectionSize > 0);
		viewInfo.setEnabled(selectionSize == 1);
		
		switch(source) {
		case Constants.DECK_ALL:
			sendCard.setEnabled(selectionSize == 1);
			break;
		case Constants.DECK_MANAGEMENT:
			addCards.setEnabled(true);
			removeCards.setEnabled(selectionSize > 0);
			break;
		case Constants.DECK_ADD_CARD:
			insertCards.setEnabled(selectionSize > 0);
			break;
		
		default:
			return false;
		}
		
		return true;
	}
	
	/* Return methods */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == Constants.DECK_ADD_CARD && resultCode == RESULT_OK) {
			long[] cardIDs = data.getLongArrayExtra(Constants.CARDS_INSERTED);
			CardDAO cardDAO = new CardDAO(this);
			DeckDAO deckDAO = new DeckDAO(this);
			for(long cardID : cardIDs) {
				Card card = cardDAO.get(cardID);
				if(card != null) deckDAO.addCard(deck, card);
			}
		}
	}
	
	/* UI methods */
	public void getLayoutObjects() {
		cardList = (ListView) findViewById(R.id.card_manager_card_list);
	}
	
	public void setLayoutObjects() {
		cardAdapter = new CardAdapter();
		cardList.setAdapter(cardAdapter);
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
	
	public void getCards() {
		switch(source) {
		case Constants.DECK_ALL:
			cards = new CardDAO(this).getAll();
			break;
			
		case Constants.DECK_MANAGEMENT:
			long deckID = getIntent().getLongExtra(Constants.DECK_SELECTED, -1);
			if(deckID >= 0) {
				deck = new DeckDAO(this).get(deckID);
				cards = deck.getCards();
			}
			else
				cards = new CardDAO(this).getAll();
			break;
			
		case Constants.DECK_ADD_CARD:
			deckID = getIntent().getLongExtra(Constants.DECK_SELECTED, -1);
			if(deckID >= 0) {
				deck = new DeckDAO(this).get(deckID);
				cards = new CardDAO(this).getAll();
				cards.removeAll(deck.getCards());
			}
			else
				cards = new CardDAO(this).getAll();
			break;
			
		default:
			cards = new CardDAO(this).getAll();
		}
	}
	
	public void unselectAll() {
		for(int i = 0; i < selected.length; i++) selected[i] = false;
		selectionSize = 0;
	}
	
	public void showSelectionError() {
		Toast.makeText(this, "Select only one card", Toast.LENGTH_SHORT).show();
	}
	
	public void removeCards(long[] ids) {
		DeckDAO deckDAO = new DeckDAO(this);
		CardDAO cardDAO = new CardDAO(this);
		for(long cardID : ids) {
			Card card = cardDAO.get(cardID);
			deckDAO.removeCard(deck, card);
		}
	}
	
	/* List methods */
	private class CardAdapter extends ArrayAdapter<Card> {
		
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
	
	/* OnClick Listeners */
	private class OnClickUnselectAll implements MenuItem.OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			unselectAll();
			return false;
		}
		
	}
	
	private class OnClickViewInfo implements MenuItem.OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			if(selectionSize != 1) {
				showSelectionError();
				return false;
			}
			
			long cardID = -1;
			for(int i = 0; i < selected.length; i++)
				if(selected[i]) {
					cardID = cards.get(i).getId();
					break;
				}
			
			Intent cardInfoIntent = new Intent(CardManagerActivity.this, CardInfoActivity.class);
			cardInfoIntent.putExtra(Constants.CARD_SELECTED, cardID);
			startActivity(cardInfoIntent);
			return false;
		}
		
	}
	
	private class OnClickAddCards implements MenuItem.OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			Intent addCardsIntent = new Intent(CardManagerActivity.this, CardInfoActivity.class);
			addCardsIntent.putExtra(Constants.DECK_SOURCE, Constants.DECK_ADD_CARD);
			startActivityForResult(addCardsIntent, Constants.DECK_ADD_CARD);
			return false;
		}
		
	}
	
	private class OnClickRemoveCards implements MenuItem.OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			long[] removedCards = new long[selectionSize];
			int j = 0;
			for(int i = 0; i < selected.length; i++) {
				if(selected[i])
					removedCards[j++] = cards.get(i).getId();
			}
			
			removeCards(removedCards);
			
			return false;
		}
		
	}
	
	private class OnClickInsertCards implements MenuItem.OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			long[] insertedCards = new long[selectionSize];
			int j = 0;
			for(int i = 0; i < selected.length; i++) {
				if(selected[i])
					insertedCards[j++] = cards.get(i).getId();
			}
			
			Intent insertedCardsIntent = new Intent();
			insertedCardsIntent.putExtra(Constants.CARDS_INSERTED, insertedCards);
			setResult(RESULT_OK, insertedCardsIntent);
			return false;
		}
		
	}
	
	private class OnClickSendCard implements MenuItem.OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			long cardID = -1;
			for(int i = 0; i < selected.length; i++)
				if(selected[i]) {
					cardID = cards.get(i).getId();
					break;
				}
			
			//FIXME put right activity
			Intent sendCardIntent = new Intent(CardManagerActivity.this, CardInfoActivity.class);
			sendCardIntent.putExtra(Constants.CARD_SELECTED, cardID);
			startActivity(sendCardIntent);
			
			//TODO remove card from device
			return false;
		}
		
	}
}
