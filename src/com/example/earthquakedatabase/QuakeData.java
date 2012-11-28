package com.example.earthquakedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class QuakeData {

	private static final String DATABASE_NAME = "QuakeBase";
	private static final String DATE_NAME = "date";
	private static final String MAG_NAME = "magnitude";
	private static final String DETAILS_NAME = "details";
	private static final String LON_NAME = "longitude";
	private static final String LAT_NAME = "latitude";
	private static final String DATE_ATTR = "date TEXT";
	private static final String MAG_ATTR = "magnitude REAL";
	private static final String DETAILS_ATTR = "details TEXT";
	private static final String LON_ATTR = "longitude REAL";
	private static final String LAT_ATTR = "latitude REAL";
	
	private DatabaseOpenHelp dbOpenHelper;
	private SQLiteDatabase database;
	
	
	public QuakeData(Context context){
		this.dbOpenHelper = new DatabaseOpenHelp(context, DATABASE_NAME, null, 1);
	}
	
	public void open(){
		this.database = this.dbOpenHelper.getWritableDatabase();
		
	}
	
	public void close(){
		if(this.database!=null)
			this.database.close();
	}
	
	public void insertQuakeInfo(QuakeInfo q){
		
		ContentValues newQuake = new ContentValues();
		newQuake.put(DATE_NAME, q.getDate().toString());
		newQuake.put(MAG_NAME, q.getMagnitude());
		newQuake.put(DETAILS_NAME, q.getDetails());
		newQuake.put(LAT_NAME, q.getLocation().getLatitude()*1e6);
		newQuake.put(LON_NAME, q.getLocation().getLongitude()*1e6);
		
		this.open();
		this.database.insert(DATABASE_NAME, null, newQuake);
		this.close();
	}
	
	public Cursor getAllQuakes(){
		Cursor result = database.query(DATABASE_NAME, null, null, null, null, null, null);
		return result;
	}
	
	
	public void clearDatabase(){
		this.open();
		this.database.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
		//this.database.delete(DATABASE_NAME, null, null);
		dbOpenHelper.onCreate(database);
		this.close();
	}
	
	
	
	//responsible for opening and closing db yourself
	public Cursor getDates(){
		String[] columns = {DATE_NAME};
		Cursor result = database.query(DATABASE_NAME, columns, null, null, null, null, null);
		
		return result;
	}
	
	
	public void logDBInfo(){
		this.open();
		Cursor result = this.database.query(DATABASE_NAME, null, null, null, null, null, null);
		int rowcount=0;
		
		while(result.moveToNext()){
			rowcount++;
		}
		Log.e("DB Row Count",""+rowcount);
		this.close();
	}
	
	//are responsible for opening and closing
	public boolean deleteRow(int rowID){
		return this.database.delete(DATABASE_NAME, "_id=" + rowID, null) > 0;
	}
	
/*
 * 	========================================================
 */
	
	
	private class DatabaseOpenHelp extends SQLiteOpenHelper{
		
		private static final String DB_CREATE_QUERY = "CREATE TABLE "+DATABASE_NAME
				+"( _id integer primary key autoincrement, "
				+DATE_ATTR+", "+MAG_ATTR+", "+DETAILS_ATTR+", "
				+LAT_ATTR+", "+LON_ATTR+");";
		
		public DatabaseOpenHelp(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		
		}
		
		@Override
		public void onCreate(SQLiteDatabase db){
			db.execSQL(DB_CREATE_QUERY);
			
		}
		
		//onUpgrade() is used to update the database to a newer version... 
		//just creates a new db... this won't ever be called most likely.
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			Log.w(DatabaseOpenHelp.class.getName(),
			        "Upgrading database from version " + oldVersion + " to "
			            + newVersion + ", which will destroy all old data");
			    db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
			    onCreate(db);
		}
	}
	
}
