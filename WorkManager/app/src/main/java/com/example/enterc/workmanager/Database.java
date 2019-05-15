package com.example.enterc.workmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
   // Truy vấn SELECT
    public Cursor SQLSelect(String uri){
        SQLiteDatabase database = getReadableDatabase();
        return  database.rawQuery(uri, null);
    }
    // Truy vấn ngoiaf SELECT
    public void SQLQuery(String uri){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(uri);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
