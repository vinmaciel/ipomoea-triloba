package br.usp.pcs.securetcg.client.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import br.usp.pcs.securetcg.client.model.Deck;

public class DeckDAO extends DatabaseHandler {

	public DeckDAO(Context context) {
		super(context);
	}
	
	public void add(Deck deck) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(DECK_ID, deck.getId());
		values.put(DECK_NAME, deck.getName());
		
		db.insert(TABLE_DECK, null, values);
		
		db.close();
	}
	
	public Deck get(long id) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(	TABLE_DECK, 
									new String[] {DECK_ID, DECK_NAME}, 
									DECK_ID + "=?", 
									new String[] {String.valueOf(id)}, 
									null, null, null, null	);
		
		if(cursor != null) cursor.moveToFirst();
		
		Deck deck = new Deck();
		deck.setId(cursor.getLong(0));
		deck.setName(cursor.getString(1));
		//TODO Add cards to deck
		
		db.close();
		
		return deck;
	}
	
	public List<Deck> getAll() {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(	TABLE_DECK, 
									new String[] {DECK_ID, DECK_NAME}, 
									null, null, null, null, null, null	);
		
		List<Deck> decks = new ArrayList<Deck>();
		
		if(cursor != null && cursor.moveToFirst()) {
			do{
				Deck deck = new Deck();
				deck.setId(cursor.getLong(0));
				deck.setName(cursor.getString(1));
				//TODO Add cards to deck
				
				decks.add(deck);
			} while(cursor.moveToNext());
		}
		
		db.close();
		
		return decks;
	}
	
	public void update(Deck deck) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(DECK_ID, deck.getId());
		values.put(DECK_NAME, deck.getName());
		
		db.update(TABLE_DECK, values, DECK_ID + "=?", new String[] {String.valueOf(deck.getId())});
		
		db.close();
	}
	
	public void delete(Deck deck) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_DECK, DECK_ID + "=?", new String[] {String.valueOf(deck.getId())});
		//TODO Remove references to cards
		
		db.close();
	}
}
