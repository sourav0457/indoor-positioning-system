package com.example.indoorpositioning.indoorpositioning;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SOURAV SAMANTA on 09-04-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "TrainingSet.db";
    public static final String TABLE_NAME = "PrimaryLocation";
    public static final String ID = "ID";
    public static final String LOCATION_NAME = "LOCATION NAME";
    public static final String MAC_1 = "MAC ADDRESS 1";
    public static final String MAC_2 = "MAC ADDRESS 2";
    public static final String MAC_3 = "MAC ADDRESS 3";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
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
}
