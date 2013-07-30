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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// FIXME layout
		setContentView(R.layout.main_activity);
		
		getLayoutObjects();
		setLayoutObjects();
	}
	
	private void getLayoutObjects() {
		marketButton = (Button) findViewById(R.main.market_button);
		decksButton = (Button) findViewById(R.main.decks_button);
		cardsButton = (Button) findViewById(R.main.cards_button);
	}
	
	private void setLayoutObjects() {
		marketButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, MarketActivity.class));
			}
		});
		
		decksButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, DeckManagerActivity.class));
			}
		});
		
		cardsButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, CardManagerActivity.class));
			}
		});
	}
}
