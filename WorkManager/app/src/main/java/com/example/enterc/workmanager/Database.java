package com.example.enterc.workmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);// Khởi tạo giá trị cho contructor
    }
   // Truy vấn SELECT
    public Cursor SQLSelect(String uri){
        SQLiteDatabase database = getReadableDatabase();// Thực hiện đọc cơ sở dữ liệu thông qua biến database
        return  database.rawQuery(uri, null);// Trả về con trỏ dòng đầu tiên đọc được
    }
    // Truy vấn ngoài SELECT
    public void SQLQuery(String uri){
        SQLiteDatabase database = getWritableDatabase();// Thực hiện ghi vào cơ sở dữ liệu thông qua biến database
        database.execSQL(uri);// Thực hiện lệnh ghi
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
// Không dùng
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Không dùng
    }
}
