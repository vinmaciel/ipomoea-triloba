package br.usp.pcs.securetcg.client.deck;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.widget.ListView;
import android.widget.TextView;
import br.usp.pcs.securetcg.client.R;
import br.usp.pcs.securetcg.client.database.DeckDAO;
import br.usp.pcs.securetcg.client.model.Deck;

public class DeckManagerActivity extends Activity {
	
	/* UI Objects */
	private ListView deckList;
	private NewDeckFragment createDeckFragment;
	
	/* Menu Objects */
	private MenuItem createDeck;
	private MenuItem updateDeck;
	private MenuItem destroyDeck;
	
	/* List Objects */
	private List<Deck> decks;
	private DeckAdapter deckAdapter;
	private int selected = -1;
	
	/* Life-cycle Methods */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deck_manager_activity);
		
		decks = new DeckDAO(this).getAll();
		
		getLayoutObjects();
		setLayoutObjects();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		deckAdapter.notifyDataSetChanged();
	}
	
	/* Menu Methods */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		createDeck = menu.add("New Deck");
		updateDeck = menu.add("Manage Deck");
		destroyDeck = menu.add("Remove Deck");
		
		createDeck.setOnMenuItemClickListener(new OnClickCreateDeck());
		updateDeck.setOnMenuItemClickListener(new OnClickUpdateDeck());
		destroyDeck.setOnMenuItemClickListener(new OnClickDestroyDeck());
		
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(selected == -1) {
			updateDeck.setEnabled(false);
			destroyDeck.setEnabled(false);
		}
		else {
			updateDeck.setEnabled(true);
			destroyDeck.setEnabled(true);
		}
		
		return true;
	}
	
	/* Layout Methods */
	private void getLayoutObjects() {
		deckList = (ListView) findViewById(R.id.deck_manager_deck_list);
	}
	
	private void setLayoutObjects() {
		deckAdapter = new DeckAdapter();
		deckList.setAdapter(deckAdapter);
		deckList.setOnItemClickListener(new OnClickSelectDeck());
	}
	
	/* List Methods */
	private class DeckAdapter extends ArrayAdapter<Deck> {
		
		LayoutInflater inflater = getLayoutInflater();
		
		DeckAdapter() {
			super(DeckManagerActivity.this, R.layout.deck_row, decks);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			
			if(row == null) {
				row = inflater.inflate(R.layout.deck_row, parent, false);
			}
			
			TextView name = (TextView) row.findViewById(R.id.deck_row_name);
			TextView description = (TextView) row.findViewById(R.id.deck_row_description);
			TextView numberOfCards = (TextView) row.findViewById(R.id.deck_row_cards_size);
			
			Deck deck = decks.get(position);
			name.setText("" + deck.getName());
			description.setText("" + deck.getDescription());
			numberOfCards.setText("" + deck.getCardsSize());
			
			return row;
		}
	}
	
	/* OnClick Listeners */
	private class OnClickSelectDeck implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View child, int position, long id) {
			if(position == selected) selected = -1;
			else selected = position;
		}
		
	}
	
	private class OnClickCreateDeck implements MenuItem.OnMenuItemClickListener {

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			// TODO create new deck on list and database
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				FragmentManager manager = getFragmentManager();
				FragmentTransaction transaction = manager.beginTransaction();
				createDeckFragment = new NewDeckFragment();
				transaction.add(R.id.deck_manager_activity, createDeckFragment).commit();
			}
			return false;
		}
		
	}
	
	private class OnClickUpdateDeck implements MenuItem.OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			Intent intent = new Intent(DeckManagerActivity.this, CardManagerActivity.class);
			// TODO change selected for the name of the deck (or its index) in the database
			intent.putExtra("DECK", selected);
			startActivity(intent);
			
			return false;
		}
		
	}
	
	private class OnClickDestroyDeck implements MenuItem.OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			// TODO remove deck from list and database
			return false;
		}
		
	}
	
	/* Fragment interface */
	protected NewDeckFragment.NewDeckInterface newDeckListener = new NewDeckFragment.NewDeckInterface() {

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public void submitCreation(String name, String description) {
			Deck deck = new Deck();
			deck.setName(name);
			deck.setDescription(description);
			new DeckDAO(getApplicationContext()).add(deck);
			decks.add(deck);
			deckAdapter.notifyDataSetChanged();
			
			FragmentManager manager = getFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.remove(createDeckFragment).commit();
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public void cancelCreation() {
			FragmentManager manager = getFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.remove(createDeckFragment).commit();
		}
	};
}
