package br.usp.pcs.securetcg.client.database;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import br.usp.pcs.securetcg.client.model.Card;
import br.usp.pcs.securetcg.client.model.CardClass;
import br.usp.pcs.securetcg.client.model.CardProperty;

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
			values.put(CARD_ID_CLASS, card.getClassID());
			
			card.setId( db.insert(TABLE_CARD, null, values) );
			card.setProperties(new LinkedList<CardProperty>());
			
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
									TABLE_CARD + "." + CARD_ID, TABLE_CARD + "." + CARD_SERIAL,
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
		
		CardClass cardClass = new CardClass();
		cardClass.setId(cursor.getLong(2));
		cardClass.setName(cursor.getString(3));
		cardClass.setDescription(cursor.getString(4));
		cardClass.setBitmapPath(cursor.getString(5));
		
		card.setCardClass(cardClass);
		
		Cursor propertiesCursor = db.query(	TABLE_PROPERTY,
										new String[] {PROPERTY_ID, PROPERTY_TAG, PROPERTY_R, PROPERTY_INFO},
										PROPERTY_ID_CARD + "=?",
										new String[] {String.valueOf(id)},
										null, null, null, null);
		
		List<CardProperty> properties = new LinkedList<CardProperty>();
		if(propertiesCursor != null && propertiesCursor.moveToFirst()) {
			do{
				CardPropertyDAO propertyDAO = new CardPropertyDAO(context);
				CardProperty property = propertyDAO.get(propertiesCursor.getLong(0));
				properties.add(property);
			} while(propertiesCursor.moveToNext());
		}
		card.setProperties(properties);
		
		db.close();
		
		return card;
	}
	
	public List<Card> getAll() {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(	TABLE_CARD + " INNER JOIN " + TABLE_CLASS + 
								" ON " + TABLE_CARD + "." + CARD_ID_CLASS + "=" + TABLE_CLASS + "." + CLASS_ID, 
								new String[] {
									TABLE_CARD + "." + CARD_ID, TABLE_CARD + "." + CARD_SERIAL,
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
				
				CardClass cardClass = new CardClass();
				cardClass.setId(cursor.getLong(2));
				cardClass.setName(cursor.getString(3));
				cardClass.setDescription(cursor.getString(4));
				cardClass.setBitmapPath(cursor.getString(5));
				
				card.setCardClass(cardClass);
				
				Cursor propertiesCursor = db.query(	TABLE_PROPERTY,
												new String[] {PROPERTY_ID, PROPERTY_TAG, PROPERTY_R, PROPERTY_INFO},
												PROPERTY_ID_CARD + "=?",
												new String[] {String.valueOf(card.getId())},
												null, null, null, null);
				
				List<CardProperty> properties = new LinkedList<CardProperty>();
				if(propertiesCursor != null && propertiesCursor.moveToFirst()) {
					do{
						CardPropertyDAO propertyDAO = new CardPropertyDAO(context);
						CardProperty property = propertyDAO.get(propertiesCursor.getLong(0));
						properties.add(property);
					} while(propertiesCursor.moveToNext());
				}
				card.setProperties(properties);
				
				cards.add(card);
			} while(cursor.moveToNext());
		}
		
		db.close();
		
		return cards;
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
			values.put(CARD_ID_CLASS, card.getClassID());

			db.update(TABLE_CARD, values, CARD_ID + "=?" , new String[] {String.valueOf(card.getId())});
			
			db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
		}
		
		db.close();
	}
	
	public void delete(Card card) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_DECK_CARD, CARD_ID + "=?", new String[] {String.valueOf(card.getId())});
		db.delete(TABLE_CARD, CARD_ID + "=?", new String[] {String.valueOf(card.getId())});
		
		db.close();
	}
	
	public void addProperty(Card card, CardProperty property) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		CardPropertyDAO propertyDAO = new CardPropertyDAO(context);
		if(this.get(card.getId()) == null)
			throw new SQLiteException("Cannot find card");
		if(propertyDAO.get(property.getId()) == null)
			throw new SQLiteException("Cannot find property");
		
		ContentValues values = new ContentValues();
		values.put(PROPERTY_ID, property.getInfo());
		values.put(PROPERTY_TAG, property.getTag());
		values.put(PROPERTY_R, property.getR());
		values.put(PROPERTY_INFO, property.getInfo());
		values.put(PROPERTY_ID_CARD, card.getId());
		
		db.update(TABLE_PROPERTY, values, PROPERTY_ID + "=?", new String[] {String.valueOf(property.getId())});
		
		db.close();
	}
	
	public void removeProperty(Card card, CardProperty property) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		CardPropertyDAO propertyDAO = new CardPropertyDAO(context);
		if(this.get(card.getId()) == null)
			throw new SQLiteException("Cannot find card");
		if(propertyDAO.get(property.getId()) == null)
			throw new SQLiteException("Cannot find property");
		
		ContentValues values = new ContentValues();
		values.put(PROPERTY_ID, property.getInfo());
		values.put(PROPERTY_TAG, property.getTag());
		values.put(PROPERTY_R, property.getR());
		values.put(PROPERTY_INFO, property.getInfo());
		values.putNull(PROPERTY_ID_CARD);
		
		db.update(TABLE_PROPERTY, values, PROPERTY_ID + "=?", new String[] {String.valueOf(property.getId())});
		
		db.close();
	}

}
