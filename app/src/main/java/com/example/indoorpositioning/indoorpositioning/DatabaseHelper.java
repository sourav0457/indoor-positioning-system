package com.example.indoorpositioning.indoorpositioning;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SOURAV SAMANTA on 09-04-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "TrainingSet.db";
    public static final String TABLE_NAME = "PrimaryLocation";
    public static final String ID = "ID";
    public static final String LOCATION_NAME = "LOCATION_NAME";
    public static final String MAC_1 = "MAC_ADDRESS_1";
    public static final String MAC_2 = "MAC_ADDRESS_2";
    public static final String MAC_3 = "MAC_ADDRESS_3";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+ LOCATION_NAME + " TEXT, " + MAC_1 +" TEXT, "+ MAC_2 +" TEXT, "+ MAC_3 +" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public int deleteData(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        //delete returns the number of rows affected
        int res = db.delete(TABLE_NAME, "ID=?", new String[]{String.valueOf(id)});
        return res;
    }

    public boolean insertData(String locationname, String mac1,String mac2, String mac3){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LOCATION_NAME, locationname);
        contentValues.put(MAC_1, mac1);
        contentValues.put(MAC_2, mac2);
        contentValues.put(MAC_3, mac3);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

}
