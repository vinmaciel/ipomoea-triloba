package br.usp.pcs.securetcg.client.database;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import br.usp.pcs.securetcg.client.model.Card;
import br.usp.pcs.securetcg.client.model.Deck;

public class DeckDAO extends DatabaseHandler {

	Context context;
	
	public DeckDAO(Context context) {
		super(context);
		this.context = context;
	}
	
	public void add(Deck deck) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(DECK_NAME, deck.getName());
		values.put(DECK_DESCRIPTION, deck.getDescription());
		
		deck.setId( db.insert(TABLE_DECK, null, values) );
		deck.setCards(new LinkedList<Card>());
		
		db.close();
	}
	
	public Deck get(long id) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor deckCursor = db.query(	TABLE_DECK, 
									new String[] {DECK_ID, DECK_NAME, DECK_DESCRIPTION}, 
									DECK_ID + "=?", 
									new String[] {String.valueOf(id)}, 
									null, null, null, null	);
		
		Deck deck = null;
		List<Long> cardIDs = null;
		if(deckCursor != null && deckCursor.moveToFirst()) {
			deck = new Deck();
			deck.setId(deckCursor.getLong(0));
			deck.setName(deckCursor.getString(1));
			deck.setDescription(deckCursor.getString(2));
			
			Cursor cardsCursor = db.query(	TABLE_DECK_CARD, 
										new String[] {DECK_CARD_ID_DECK, DECK_CARD_ID_CARD}, 
										DECK_CARD_ID_DECK + "=?", 
										new String[] {String.valueOf(id)}, 
										null, null, null, null	);
			
			cardIDs = new LinkedList<Long>();
			if(cardsCursor != null && cardsCursor.moveToFirst()) {
				do{
					cardIDs.add(cardsCursor.getLong(1));
				} while(cardsCursor.moveToNext());
				
				cardsCursor.close();
			}
			
			deckCursor.close();
		}
		
		db.close();
		
		if(deck != null) {
			List<Card> cards = new LinkedList<Card>();
			for(Long cardID : cardIDs) {
				CardDAO cardDAO = new CardDAO(context);
				Card card = cardDAO.get(cardID);
				cards.add(card);
			}
			
			deck.setCards(cards);
		}
		
		return deck;
	}
	
	public List<Deck> getAll() {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(	TABLE_DECK, 
									new String[] {DECK_ID, DECK_NAME, DECK_DESCRIPTION}, 
									null, null, null, null, null, null	);
		
		List<Deck> decks = new LinkedList<Deck>();
		
		if(cursor != null && cursor.moveToFirst()) {
			do{
				Deck deck = new Deck();
				deck.setId(cursor.getLong(0));
				deck.setName(cursor.getString(1));
				deck.setDescription(cursor.getString(2));
				
				Cursor cardsCursor = db.query(	TABLE_DECK_CARD, 
											new String[] {DECK_CARD_ID_DECK, DECK_CARD_ID_CARD}, 
											DECK_CARD_ID_DECK + "=?", 
											new String[] {String.valueOf(deck.getId())}, 
											null, null, null, null	);
				
				List<Card> cards = new LinkedList<Card>();
				if(cardsCursor != null && cardsCursor.moveToFirst()) {
					do{
						Card card = new Card();
						card.setId(cardsCursor.getLong(1));
						cards.add(card);
					} while(cardsCursor.moveToNext());
					
					cardsCursor.close();
				}
				deck.setCards(cards);
				
				decks.add(deck);
			} while(cursor.moveToNext());
			
			cursor.close();
		}
		
		db.close();
		
		for(int i = 0; i < decks.size(); i++) {
			CardDAO cardDAO = new CardDAO(context);
			
			for(int j = 0; j < decks.get(i).getCards().size(); j++) {
				Card card = cardDAO.get(decks.get(i).getCards().get(j).getId());
				decks.get(i).getCards().set(j, card);
			}
		}
		
		return decks;
	}
	
	public void update(Deck deck) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(DECK_ID, deck.getId());
		values.put(DECK_NAME, deck.getName());
		values.put(DECK_DESCRIPTION, deck.getDescription());
		
		db.update(TABLE_DECK, values, DECK_ID + "=?", new String[] {String.valueOf(deck.getId())});
		
		db.close();
	}
	
	public void delete(Deck deck) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_DECK_CARD, DECK_CARD_ID_DECK + "=?", new String[] {String.valueOf(deck.getId())});
		db.delete(TABLE_DECK, DECK_ID + "=?", new String[] {String.valueOf(deck.getId())});
		
		db.close();
	}
	
	public void addCard(Deck deck, Card card) {
		CardDAO cardDAO = new CardDAO(context);
		if(this.get(deck.getId()) == null)
			throw new SQLiteException("Cannot find deck");
		if(cardDAO.get(card.getId()) == null)
			throw new SQLiteException("Cannot find card");
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(DECK_CARD_ID_CARD, card.getId());
		values.put(DECK_CARD_ID_DECK, deck.getId());
		
		db.insert(TABLE_DECK_CARD, null, values);
		
		db.close();
		
		if(!deck.getCards().contains(card))
			deck.getCards().add(card);
	}
	
	public void removeCard(Deck deck, Card card) {
		CardDAO cardDAO = new CardDAO(context);
		if(this.get(deck.getId()) == null)
			throw new SQLiteException("Cannot find deck");
		if(cardDAO.get(card.getId()) == null)
			throw new SQLiteException("Cannot find card");
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_DECK_CARD, DECK_CARD_ID_DECK + "=? AND " + DECK_CARD_ID_CARD + "=?", new String[] {String.valueOf(deck.getId()), String.valueOf(card.getId())});
		
		db.close();
		
		if(deck.getCards().contains(card))
			deck.getCards().remove(card);
	}
}
