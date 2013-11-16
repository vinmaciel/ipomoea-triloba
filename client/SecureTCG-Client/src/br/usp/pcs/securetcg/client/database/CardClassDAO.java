package br.usp.pcs.securetcg.client.database;

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
		
		db.close();
	}
	
	public CardClass get(long id) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(	TABLE_CLASS, 
								new String[] {CLASS_ID, CLASS_NAME, CLASS_DESCRIPTION, CLASS_BITMAP_PATH}, 
								CLASS_ID + "=?", 
								new String[] {String.valueOf(id)}, 
								null, null, null, null	);
		
		if(cursor != null) cursor.moveToFirst();
		
		CardClass cardClass = new CardClass();
		cardClass.setId(cursor.getLong(0));
		cardClass.setName(cursor.getString(1));
		cardClass.setDescription(cursor.getString(2));
		cardClass.setBitmapPath(cursor.getString(3));
		
		db.close();
		
		return cardClass;
	}
}
