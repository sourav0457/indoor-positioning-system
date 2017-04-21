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

    //Column Names for selectLocation class table
    public static final String TABLE_NAME = "PrimaryLocation";
    public static final String ID = "ID";
    public static final String LOCATION_NAME = "LOCATION_NAME";
    public static final String MAC_1 = "MAC_ADDRESS_1";
    public static final String MAC_2 = "MAC_ADDRESS_2";
    public static final String MAC_3 = "MAC_ADDRESS_3";

    //Column Names for TrainingSet class table
    public static final String TABLE_NAME_TRAINING = "TrainingSet";
    public static final String TRAINING_ID = "TRAINING_ID";
    public static final String X_VALUE = "X";
    public static final String Y_VALUE = "Y";
    public static final String RSS_M1 = "M1";
    public static final String RSS_M2 = "M2";
    public static final String RSS_M3 = "M3";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 6);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+ LOCATION_NAME + " TEXT, " + MAC_1 +" TEXT, "+ MAC_2 +" TEXT, "+ MAC_3 +" TEXT);");
        //db.execSQL("CREATE TABLE " + TABLE_NAME_TRAINING + " (" + TRAINING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+ X_VALUE + " INTEGER, " + Y_VALUE +" INTEGER, "+ RSS_M1 +" INTEGER, "+ RSS_M2 +" INTEGER, "+ RSS_M3 +" INTEGER, "+ ID +" INTEGER);");
        db.execSQL("create table " + TABLE_NAME_TRAINING + " (" + TRAINING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+ X_VALUE + " INTEGER, " + Y_VALUE +" INTEGER, "+ RSS_M1 +" INTEGER, "+ RSS_M2 +" INTEGER, "+ RSS_M3 +" INTEGER, "+ ID +" INTEGER, " + " FOREIGN KEY ("+ID+") REFERENCES "+TABLE_NAME+"("+ID+"));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TRAINING);
        onCreate(db);
    }

    //METHODS FOR TrainingSet class TABLES
    public boolean insertDataTraining(int x, int y, int rss_m1, int rss_m2, int rss_m3, int i){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(X_VALUE, x);
        contentValues.put(Y_VALUE, y);
        contentValues.put(RSS_M1, rss_m1);
        contentValues.put(RSS_M2, rss_m2);
        contentValues.put(RSS_M3, rss_m3);
        contentValues.put(ID, i);
        long result = db.insert(TABLE_NAME_TRAINING, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getXYDataTraining(int i){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT " + X_VALUE + " , " + Y_VALUE + " FROM " + TABLE_NAME_TRAINING + " WHERE " + ID + " = " + i , null);
        //Cursor res = db.rawQuery("SELECT " + X_VALUE + " , " + Y_VALUE + " FROM " + TABLE_NAME_TRAINING + " WHERE " + ID + " = " + i , null);
        return res;
    }

    public Cursor getAllDataTraining(int i){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME_TRAINING + " WHERE " + ID + " = " + i , null);
        return res;
    }

    public int deleteTrainingData(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        //delete returns the number of rows affected
        int res = db.delete(TABLE_NAME_TRAINING, "TRAINING_ID=?", new String[]{String.valueOf(id)});
        return res;
    }

    //METHODS FOR selectLocation class TABLES

    public Cursor getDataById(int id){
        SQLiteDatabase db = this .getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ID + " = " + id, null);
        return res;
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
