package br.usp.pcs.securetcg.client.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	//Database version (upgradable)
	private static final int DATABASE_VERSION = 1;
	
	//Database name
	private static final String DATABASE_NAME = "securetcg_db";
	
	//Database tables
	protected static final String TABLE_DECK = "deck";
	protected static final String TABLE_CARD = "card";
	protected static final String TABLE_DECK_CARD = "deck_card";
	protected static final String TABLE_CLASS = "class";

	//Table TABLE_DECK columns
	protected static final String DECK_ID = "id";
	protected static final String DECK_NAME = "name";
	protected static final String DECK_DESCRIPTION = "description";
	
	//Table TABLE_CARD columns
	protected static final String CARD_ID = "id";
	protected static final String CARD_SERIAL = "serial";
	protected static final String CARD_PROPERTIES = "properties";
	protected static final String CARD_ID_CLASS = "id_class";
	
	//Table TABLE_DECK_CARD columns
	protected static final String DECK_CARD_ID_CARD = "id_card";
	protected static final String DECK_CARD_ID_DECK = "id_deck";
	
	//Table TABLE_CLASS columns
	protected static final String CLASS_ID = "id";
	protected static final String CLASS_NAME = "name";
	protected static final String CLASS_DESCRIPTION = "description";
	protected static final String CLASS_BITMAP_PATH = "bitmap_path";
	
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CARD_TABLE =	"CREATE TABLE " + TABLE_CARD + "(" +
									CARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
									CARD_SERIAL + " BLOB," +
									CARD_PROPERTIES + " BLOB," +
									CARD_ID_CLASS + " INTEGER," +
									"FOREIGN KEY(" + CARD_ID_CLASS + ") REFERENCES " + TABLE_CLASS + "(" + CLASS_ID + ")" + 
									")";
		String CREATE_DECK_TABLE =	"CREATE TABLE " + TABLE_DECK + "(" +
									DECK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
									DECK_NAME + " TEXT," +
									DECK_DESCRIPTION + " TEXT" +
									")";
		String CREATE_DECK_CARD_TABLE =	"CREATE TABLE " + TABLE_DECK_CARD + "(" +
										DECK_CARD_ID_CARD + " INTEGER," +
										DECK_CARD_ID_DECK + " INTEGER," +
										"FOREIGN KEY(" + DECK_CARD_ID_CARD + ") REFERENCES " + TABLE_CARD + "(" + CARD_ID + ")," +
										"FOREIGN KEY(" + DECK_CARD_ID_DECK + ") REFERENCES " + TABLE_DECK + "(" + DECK_ID + ")" +
										")";
		String CREATE_CLASS_TABLE =	"CREATE TABLE" + TABLE_CLASS + "(" +
									CLASS_ID + " INTEGER PRIMARY KEY," +
									CLASS_NAME + " TEXT," +
									CLASS_DESCRIPTION + " TEXT," +
									CLASS_BITMAP_PATH + " TEXT" +
									")";
		
		db.execSQL(CREATE_DECK_TABLE);
		db.execSQL(CREATE_CARD_TABLE);
		db.execSQL(CREATE_DECK_CARD_TABLE);
		db.execSQL(CREATE_CLASS_TABLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//FIXME For now, just recreate the tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DECK_CARD);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DECK);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARD);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);
		this.onCreate(db);
	}
	
}
