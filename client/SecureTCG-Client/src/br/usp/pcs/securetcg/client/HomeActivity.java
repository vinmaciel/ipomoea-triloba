package br.usp.pcs.securetcg.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import br.usp.pcs.securetcg.client.deck.CardManagerActivity;
import br.usp.pcs.securetcg.client.deck.DeckManagerActivity;
import br.usp.pcs.securetcg.client.market.MarketActivity;

public class HomeActivity extends Activity {
	
	/** UI Objects **/
	private Button marketButton;
	private Button decksButton;
	private Button cardsButton;
	
	/** Life-cycle Methods **/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// FIXME layout
		setContentView(R.layout.home_activity);
		
		getLayoutObjects();
		setLayoutObjects();
	}
	
	/** Layout Methods **/
	private void getLayoutObjects() {
		marketButton = (Button) findViewById(R.home.market_button);
		decksButton = (Button) findViewById(R.home.decks_button);
		cardsButton = (Button) findViewById(R.home.cards_button);
	}
	
	private void setLayoutObjects() {
		marketButton.setOnClickListener(new OnClickMarket());
		decksButton.setOnClickListener(new OnClickDeck());
		cardsButton.setOnClickListener(new OnClickCards());
	}
	
	/** OnClick Listeners **/
	private class OnClickMarket implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			startActivity(new Intent(HomeActivity.this, MarketActivity.class));
		}
		
	}
	
	private class OnClickDeck implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			startActivity(new Intent(HomeActivity.this, DeckManagerActivity.class));
		}
		
	}
	
	private class OnClickCards implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			startActivity(new Intent(HomeActivity.this, CardManagerActivity.class));
		}
		
	}
}
