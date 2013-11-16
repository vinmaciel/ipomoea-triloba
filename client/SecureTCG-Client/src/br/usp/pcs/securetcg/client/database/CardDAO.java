package br.usp.pcs.securetcg.client.database;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import br.usp.pcs.securetcg.client.model.Card;
import br.usp.pcs.securetcg.client.model.CardClass;

public class CardDAO extends DatabaseHandler {

	Context context;
	
	public CardDAO(Context context) {
		super(context);
		this.context = context;
	}
	
	public void add(Card card) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.beginTransaction();
		try {
			long classID = card.getClassID();
			
			CardClassDAO cardClassDAO = new CardClassDAO(context);
			if(cardClassDAO.get(classID) == null) {
				cardClassDAO.add(card.getCardClass());
			}
			
			ContentValues values = new ContentValues();
			values.put(CARD_SERIAL, card.getSerial());
			values.put(CARD_PROPERTIES, card.getProperties());
			values.put(CARD_ID_CLASS, card.getClassID());
			
			card.setId( db.insert(TABLE_CARD, null, values) );
			
			db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
		}
		
		db.close();
	}
	
	public Card get(long id) { 
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(	TABLE_CARD + " INNER JOIN " + TABLE_CLASS + 
								" ON " + TABLE_CARD + "." + CARD_ID_CLASS + "=" + TABLE_CLASS + "." + CLASS_ID, 
								new String[] {
									TABLE_CARD + "." + CARD_ID, TABLE_CARD + "." + CARD_SERIAL, TABLE_CARD + "." + CARD_PROPERTIES,
									TABLE_CLASS + "." + CLASS_ID, TABLE_CLASS + "." + CLASS_NAME, 
									TABLE_CLASS + "." + CLASS_DESCRIPTION, TABLE_CLASS + "." + CLASS_BITMAP_PATH
								}, 
								CARD_ID + "=?", 
								new String[] {String.valueOf(id)}, 
								null, null, null, null	);
		
		if(cursor != null) cursor.moveToFirst();
		
		Card card = new Card();
		card.setId(cursor.getLong(0));
		card.setSerial(cursor.getBlob(1));
		card.setProperties(cursor.getBlob(2));
		
		CardClass cardClass = new CardClass();
		cardClass.setId(cursor.getLong(3));
		cardClass.setName(cursor.getString(4));
		cardClass.setDescription(cursor.getString(5));
		cardClass.setBitmapPath(cursor.getString(6));
		
		card.setCardClass(cardClass);
		
		db.close();
		
		return card;
	}
	
	public List<Card> getAll() {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(	TABLE_CARD + " INNER JOIN " + TABLE_CLASS + 
								" ON " + TABLE_CARD + "." + CARD_ID_CLASS + "=" + TABLE_CLASS + "." + CLASS_ID, 
								new String[] {
									TABLE_CARD + "." + CARD_ID, TABLE_CARD + "." + CARD_SERIAL, TABLE_CARD + "." + CARD_PROPERTIES,
									TABLE_CLASS + "." + CLASS_ID, TABLE_CLASS + "." + CLASS_NAME, 
									TABLE_CLASS + "." + CLASS_DESCRIPTION, TABLE_CLASS + "." + CLASS_BITMAP_PATH
								},  
								null, null, null, null, null, null	);
		
		List<Card> cards = new LinkedList<Card>();
		
		if(cursor != null && cursor.moveToFirst()) {
			do{
				
				Card card = new Card();
				card.setId(cursor.getLong(0));
				card.setSerial(cursor.getBlob(1));
				card.setProperties(cursor.getBlob(2));
				
				CardClass cardClass = new CardClass();
				cardClass.setId(cursor.getLong(3));
				cardClass.setName(cursor.getString(4));
				cardClass.setDescription(cursor.getString(5));
				cardClass.setBitmapPath(cursor.getString(6));
				
				card.setCardClass(cardClass);
				
				cards.add(card);
			} while(cursor.moveToNext());
		}
		
		return null;
	}
	
	public void update(Card card) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.beginTransaction();
		try {
			long classID = card.getClassID();
			
			CardClassDAO cardClassDAO = new CardClassDAO(context);
			if(cardClassDAO.get(classID) == null) {
				cardClassDAO.add(card.getCardClass());
			}
			
			ContentValues values = new ContentValues();
			values.put(CARD_ID, card.getId());
			values.put(CARD_SERIAL, card.getSerial());
			values.put(CARD_PROPERTIES, card.getProperties());
			values.put(CARD_ID_CLASS, card.getClassID());

			db.update(TABLE_CARD, values, CARD_ID + "=?" , new String[] {String.valueOf(card.getId())});
			
			db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
		}
	}
	
	public void delete(Card card) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_DECK_CARD, CARD_ID + "=?", new String[] {String.valueOf(card.getId())});
		db.delete(TABLE_CARD, CARD_ID + "=?", new String[] {String.valueOf(card.getId())});
		
		db.close();
	}

}
