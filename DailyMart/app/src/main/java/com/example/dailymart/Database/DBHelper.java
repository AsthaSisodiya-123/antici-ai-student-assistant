package com.example.dailymart.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class DBHelper extends SQLiteOpenHelper {
    private static final String dbName="UserData.db";
    private static final int dbVersion=1;
    public DBHelper(@Nullable Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user("+"id INTEGER PRIMARY KEY AUTOINCREMENT,"+"name TEXT,"+"mobileno TEXT UNIQUE,"+"emailid TEXT UNIQUE,"+"username TEXT UNIQUE,"+"password TEXT UNIQUE)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS user");
            onCreate(db);

    }


    // 🔹 Register a new user
    public boolean registerUser(String name, String mobileno, String emailid, String username, String password) {
        // Check for duplicates first
        if (checkUserExists(mobileno, emailid, username,password)) {
            return false; // User already exists
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("mobileno", mobileno);
        values.put("emailid", emailid);
        values.put("username", username);
        values.put("password", password); // ⚠️ Reminder: hash this in production

        long result = db.insert("user", null, values);
        db.close();

        return result != -1;
    }

    private boolean checkUserExists(String mobileno, String emailid, String username, String password) {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM user WHERE mobileno=? AND emailid=? AND username=? AND password=?",
                new String[]{mobileno,emailid,username,password});

        boolean exists = cursor.getCount() > 0;
        return exists;
    }


    // 🔹 Login check
    public boolean loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE username = ? AND password = ?",
                new String[]{username, password});

        boolean success = cursor.getCount() > 0;

        return success;
    }

    public boolean updateUser(String oldusername,String name,String mobileno, String emailid, String username)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("name",name);
        contentValues.put("mobileno",mobileno);
        contentValues.put("emailid",emailid);
        contentValues.put("username",username);

        int result=sqLiteDatabase.update("user",contentValues,"username=?",new String[]{oldusername});
        return result > 0;
    }

    public boolean deleteUser(String username)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        int result = sqLiteDatabase.delete("user","username=?",new String[]{username});
        return result > 0;
    }
}
