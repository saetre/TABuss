/**
 * Copyright (C) 2010-2012 Magnus Raaum, Lars Moland Eliassen, Christoffer Jun Marcussen, Rune Sætre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * README:
 * 
 */

package test.BusTUC.Database;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;




public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String dbName="tucApp";
	public static final String queryTable="queries";
	public static final String areaTable="areas";
	public static final String rowId="_id";
	public static final String maxLat="maxLat";
	public static final String minLat="minLat";
	public static final String maxLong="maxLong";
	public static final String minLong="minLong";
	public static final String destination="destination";
	public static final String origin="origin";
	public static final String time="time";
	public static final String day="day";
	public static final String success="success";
	public static final String realtimeTable="realtimeTable";
	public static final String stopName="stopName";
	public static final String toFrom="toFrom";

	public DatabaseHelper(Context context) {
		super(context, dbName, null, 6);
		//db.execSQL("DROP TABLE IF EXISTS "+queryTable);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		System.out.println("EKSEKVERER CREATE TABLE");

		db.execSQL("CREATE TABLE "+queryTable+" ("+rowId+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
				destination+" TEXT, "+origin+" Integer, "+time+" INTEGER NOT NULL, "+day+" INTEGER NOT NULL);");
		
		db.execSQL("CREATE TABLE "+areaTable+" ("+rowId+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
				maxLat+" REAL, "+minLat+" REAL, "+maxLong+" REAL, "+minLong+" REAL);");
		
		db.execSQL("CREATE TABLE "+realtimeTable+" ("+rowId+" INTEGER PRIMARY KEY AUTOINCREMENT, "+stopName+" TEXT, "+toFrom+" TEXT);");

	}

	public void addRealTime(String stopName, String toFrom){
		SQLiteDatabase db= this.getWritableDatabase();
		Cursor curse = db.rawQuery("SELECT stopName from "+realtimeTable+ " t WHERE t.stopName = '"+stopName+"' AND t.toFrom = '"+toFrom+"'",null);
		if(curse.getCount()==0){
			ContentValues cv=new ContentValues();

			cv.put(DatabaseHelper.stopName, stopName);
			cv.put(DatabaseHelper.toFrom, toFrom);

			db.insert(realtimeTable, DatabaseHelper.toFrom, cv);
		}
		db.close();
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS "+areaTable);
		db.execSQL("DROP TABLE IF EXISTS "+queryTable);
		db.execSQL("DROP TABLE IF EXISTS "+realtimeTable);
		onCreate(db);
	}

	public int AddArea(double maxLat,double minLat, double maxLong, double minLong){
		SQLiteDatabase db= this.getWritableDatabase();
		//db.execSQL("INSERT INTO "+areaTable+" VALUES("+maxLat+", "+minLat+", "+maxLong+", "+minLong+")");
		ContentValues cv=new ContentValues();

		cv.put(DatabaseHelper.maxLat, maxLat);
		cv.put(DatabaseHelper.minLat, minLat);
		cv.put(DatabaseHelper.maxLong, maxLong);
		cv.put(DatabaseHelper.minLong, minLong);

		db.insert(areaTable, DatabaseHelper.maxLat, cv);
		Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null);
		cursor.moveToFirst();
		int id = cursor.getInt(0);
		db.close();
		return id;
	}
	
	public Cursor getAreaId(double lat, double lon){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT "+rowId+" from "+areaTable+ " t WHERE t.maxLat > "+lat+" AND t.minLat < "+lat+
				" AND t.maxLong > "+lon+" AND t.minLong < "+lon, null);
		return cursor;
	}
	
	public Cursor getArea(int id){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * from "+areaTable+ " WHERE "+rowId+" = "+id , null);
		return cursor;
	}
	
	public void AddQuery(Query query)
	{
		SQLiteDatabase db= this.getWritableDatabase();

		ContentValues cv=new ContentValues();

		cv.put(destination, query.getDestination());
		cv.put(origin, query.getOrigin());
		cv.put(time, query.getTime());
		cv.put(day, query.getDay());

		db.insert(queryTable, destination, cv);
		db.close();
	}
	public Cursor getAllRealtime(){
		SQLiteDatabase db=this.getReadableDatabase();
		Cursor cur= db.rawQuery("Select * from "+realtimeTable +" ORDER BY stopName", null);

		if(cur!=null){
			//db.close();
			return cur;
		}
		//db.close();
		return null;	
	}
	public Cursor getAllQueries(){
		SQLiteDatabase db=this.getReadableDatabase();
		Cursor cur= db.rawQuery("Select * from "+queryTable, null);
		
		if(cur!=null){
			//db.close();
			return cur;
		}
		//db.close();
		return null;	
	}
	
	public Cursor getQueryFromArea(double lat, double lon, int maxTime, int minTime){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * from "+queryTable+" WHERE "+origin+" = (SELECT "+rowId+" from "+areaTable+ " WHERE maxLat > "+lat+" AND minLat < "+lat+
				" AND maxLong > "+lon+" AND minLong < "+lon+") AND "+time+" < "+maxTime+" AND "+time+" > "+minTime, null);
		return cursor;
	}
	
	public void clearDatabase(){
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS "+areaTable);
		db.execSQL("DROP TABLE IF EXISTS "+queryTable);
		db.execSQL("DROP TABLE IF EXISTS "+realtimeTable);
		onCreate(db);
		db.close();
	}
	
	public void clearRealtime(){
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS "+realtimeTable);		
		db.execSQL("CREATE TABLE "+realtimeTable+" ("+rowId+" INTEGER PRIMARY KEY AUTOINCREMENT, "+stopName+" TEXT, "+toFrom+" TEXT);");
		db.close();
	}
	
	public void clearLog(){
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS "+areaTable);
		db.execSQL("DROP TABLE IF EXISTS "+queryTable);
		db.execSQL("CREATE TABLE "+queryTable+" ("+rowId+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
				destination+" TEXT, "+origin+" Integer, "+time+" INTEGER NOT NULL, "+day+" INTEGER NOT NULL);");
		
		db.execSQL("CREATE TABLE "+areaTable+" ("+rowId+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
				maxLat+" REAL, "+minLat+" REAL, "+maxLong+" REAL, "+minLong+" REAL);");
		db.close();
	}
	public int getQueryCount()
	{
		SQLiteDatabase db=this.getReadableDatabase();
		Cursor cur= db.rawQuery("Select * from "+queryTable, null);
		if(cur!=null){
			int x= cur.getCount();
			cur.close();
			db.close();
			return x;
		}
		db.close();
		return 0;		
	}
}
