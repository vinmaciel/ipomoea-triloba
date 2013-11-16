package br.usp.pcs.securetcg.client.database;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import br.usp.pcs.securetcg.client.model.Card;

public class CardDAO extends DatabaseHandler {

	public CardDAO(Context context) {
		super(context);
	}
	
	public void add(Card card) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(CARD_NAME, card.getName());
		values.put(CARD_DESCRIPTION, card.getDescription());
		values.put(CARD_BITMAP_PATH, card.getBitmapPath());
		values.put(CARD_SERIAL, card.getSerial());
		values.put(CARD_PROPERTIES, card.getProperties());
		values.put(CARD_CLASS, card.getClassID());
		
		card.setId( db.insert(TABLE_CARD, null, values) );
		
		db.close();
	}
	
	public Card get(long id) { 
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(	TABLE_CARD, 
								new String[] {CARD_ID, CARD_NAME, CARD_DESCRIPTION, CARD_BITMAP_PATH, CARD_SERIAL, CARD_PROPERTIES, CARD_CLASS}, 
								CARD_ID + "=?", 
								new String[] {String.valueOf(id)}, 
								null, null, null, null	);
		
		if(cursor != null) cursor.moveToFirst();
		
		Card card = new Card();
		card.setId(cursor.getLong(0));
		card.setName(cursor.getString(1));
		card.setDescription(cursor.getString(2));
		card.setBitmapPath(cursor.getString(3));
		card.setSerial(cursor.getBlob(4));
		card.setProperties(cursor.getBlob(5));
		card.setClassID(cursor.getLong(6));
		
		db.close();
		
		return card;
	}
	
	public List<Card> getAll() {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(	TABLE_CARD, 
								new String[] {CARD_ID, CARD_NAME, CARD_DESCRIPTION, CARD_BITMAP_PATH, CARD_SERIAL, CARD_PROPERTIES, CARD_CLASS}, 
								null, null, null, null, null, null	);
		
		List<Card> cards = new LinkedList<Card>();
		
		if(cursor != null && cursor.moveToFirst()) {
			do{
				Card card = new Card();
				card.setId(cursor.getLong(0));
				card.setName(cursor.getString(1));
				card.setDescription(cursor.getString(2));
				card.setBitmapPath(cursor.getString(3));
				card.setSerial(cursor.getBlob(4));
				card.setProperties(cursor.getBlob(5));
				card.setClassID(cursor.getLong(6));
				
				cards.add(card);
			} while(cursor.moveToNext());
		}
		
		return null;
	}
	
	public void update(Card card) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(CARD_ID, card.getId());
		values.put(CARD_NAME, card.getName());
		values.put(CARD_DESCRIPTION, card.getDescription());
		values.put(CARD_BITMAP_PATH, card.getBitmapPath());
		values.put(CARD_SERIAL, card.getSerial());
		values.put(CARD_PROPERTIES, card.getProperties());
		values.put(CARD_CLASS, card.getClassID());
		
		db.update(TABLE_CARD, values, CARD_ID + "=?" , new String[] {String.valueOf(card.getId())});
		
		db.close();
	}
	
	public void delete(Card card) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_CARD, CARD_ID + "=?", new String[] {String.valueOf(card.getId())});
		db.delete(TABLE_DECK_CARD, CARD_ID + "=?", new String[] {String.valueOf(card.getId())});
		
		db.close();
	}

}
