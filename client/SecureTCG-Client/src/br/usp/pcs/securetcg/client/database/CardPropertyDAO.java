package br.usp.pcs.securetcg.client.database;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import br.usp.pcs.securetcg.client.model.CardProperty;

public class CardPropertyDAO extends DatabaseHandler {

	public CardPropertyDAO(Context context) {
		super(context);
	}
	
	public void add(CardProperty property) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(PROPERTY_TAG, property.getTag());
		values.put(PROPERTY_R, property.getR());
		values.put(PROPERTY_INFO, property.getInfo());
		
		property.setId( db.insert(TABLE_PROPERTY, null, values) );
		
		db.close();
	}
	
	public CardProperty get(long id) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(	TABLE_PROPERTY,
								new String[] {
									TABLE_PROPERTY + "." + PROPERTY_ID, TABLE_PROPERTY + "." + PROPERTY_TAG,
									TABLE_PROPERTY + "." + PROPERTY_R, TABLE_PROPERTY + "." + PROPERTY_INFO
								},
								PROPERTY_ID + "=?",
								new String[] {String.valueOf(id)},
								null, null, null, null);
		
		CardProperty property = null;
		if(cursor != null && cursor.moveToFirst()) {
			property = new CardProperty();
			property.setId(cursor.getLong(0));
			property.setTag(cursor.getBlob(1));
			property.setR(cursor.getBlob(2));
			property.setInfo(cursor.getBlob(3));
			
			cursor.close();
		}
		
		db.close();
		
		return property;
	}
	
	public List<CardProperty> getAll() {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(	TABLE_PROPERTY,
								new String[] {
									TABLE_PROPERTY + "." + PROPERTY_ID, TABLE_PROPERTY + "." + PROPERTY_TAG,
									TABLE_PROPERTY + "." + PROPERTY_R, TABLE_PROPERTY + "." + PROPERTY_INFO
								},
								null, null, null, null, null, null);
		
		List<CardProperty> properties = new LinkedList<CardProperty>();
		
		if(cursor != null && cursor.moveToFirst()) {
			do{
				CardProperty property = new CardProperty();
				property.setId(cursor.getLong(0));
				property.setTag(cursor.getBlob(1));
				property.setR(cursor.getBlob(2));
				property.setInfo(cursor.getBlob(3));
				
				properties.add(property);
			} while(cursor.moveToNext());
			
			cursor.close();
		}
		
		db.close();
		
		return properties;
	}
	
	public void update(CardProperty property) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(PROPERTY_ID, property.getId());
		values.put(PROPERTY_TAG, property.getTag());
		values.put(PROPERTY_R, property.getR());
		values.put(PROPERTY_INFO, property.getInfo());
		
		db.update(TABLE_PROPERTY, values, PROPERTY_ID + "=?", new String[] {String.valueOf(property.getId())});
		
		db.close();
	}
	
	public void delete(CardProperty property) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_PROPERTY, PROPERTY_ID + "=?", new String[] {String.valueOf(property.getId())});
		
		db.close();
	}
}
