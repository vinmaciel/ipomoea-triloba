package br.usp.pcs.securetcg.client.database;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import br.usp.pcs.securetcg.client.model.CardClass;

public class CardClassDAO extends DatabaseHandler {

	public CardClassDAO(Context context) {
		super(context);
	}
	
	public void add(CardClass cardClass) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(CLASS_ID, cardClass.getId());
		values.put(CLASS_NAME, cardClass.getName());
		values.put(CLASS_DESCRIPTION, cardClass.getDescription());
		values.put(CLASS_BITMAP_PATH, cardClass.getBitmapPath());
		
		db.insert(TABLE_CLASS, null, values);
		
		db.close();
	}
	
	public CardClass get(long id) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(	TABLE_CLASS, 
								new String[] {CLASS_ID, CLASS_NAME, CLASS_DESCRIPTION, CLASS_BITMAP_PATH}, 
								CLASS_ID + "=?", 
								new String[] {String.valueOf(id)}, 
								null, null, null, null	);
		
		CardClass cardClass = null;
		if(cursor != null && cursor.moveToFirst()) {
			cardClass= new CardClass();
			cardClass.setId(cursor.getLong(0));
			cardClass.setName(cursor.getString(1));
			cardClass.setDescription(cursor.getString(2));
			cardClass.setBitmapPath(cursor.getString(3));
			
			cursor.close();
		}
		
		db.close();
		
		return cardClass;
	}
	
	public List<CardClass> getAll() {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(	TABLE_CLASS, 
									new String[] {CLASS_ID, CLASS_NAME, CLASS_DESCRIPTION, CLASS_BITMAP_PATH}, 
									null, null, null, null, null, null	);
		
		List<CardClass> cardClasses = new LinkedList<CardClass>();
		
		if(cursor != null && cursor.moveToFirst()) {
			do{
				CardClass cardClass = new CardClass();
				cardClass.setId(cursor.getLong(0));
				cardClass.setName(cursor.getString(1));
				cardClass.setDescription(cursor.getString(2));
				cardClass.setBitmapPath(cursor.getString(3));
				
				cardClasses.add(cardClass);
			} while(cursor.moveToNext());
			
			cursor.close();
		}
		
		db.close();
		
		return cardClasses;
	}
	
	public void update(CardClass cardClass) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(CLASS_ID, cardClass.getId());
		values.put(CLASS_NAME, cardClass.getName());
		values.put(CLASS_DESCRIPTION, cardClass.getDescription());
		values.put(CLASS_BITMAP_PATH, cardClass.getBitmapPath());
		
		db.update(TABLE_CLASS, values, CLASS_ID + "=?", new String[] {String.valueOf(cardClass.getId())});
		
		db.close();
	}
	
	public void delete(CardClass cardClass) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_CLASS, CLASS_ID + "=?", new String[] {String.valueOf(cardClass.getId())});
		
		db.close();
	}
}
