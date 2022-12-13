package com.example.carofinal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "Login.db";

    public DBHelper(Context context) {
        super(context, "Login.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("create table users(username TEXT PRIMARY KEY, password TEXT,gold INTEGER)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("drop table if exists users ");
    }

    public Boolean insertData(String username, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("gold",0);

        long result = MyDB.insert("users", null, contentValues);
        if (result == -1) return false;
        else return true;

    }

    public Boolean checkUsername(String username) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("select * from users where username=?", new String[]{username});
        if (cursor.getCount() > 0) {
            return true;
        } else return false;

    }

    public Boolean checkUsernamePassword(String username, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("select * from users where username=? and password=?", new String[]{username, password});
        if (cursor.getCount() > 0) {
            return true;
        } else return false;
    }
    public void  updateGold(String userName,int gold)
    {
        SQLiteDatabase MyDB=this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username",userName);
        contentValues.put("gold",gold);
        MyDB.update("users",contentValues,"username = ?",new String[]{userName});
        MyDB.close();

    }
    public Integer getGold(String username)
    {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = MyDB.rawQuery("select * from users where username=?", new String[]{username});
        Integer gold=-1;
        if(cursor!=null && cursor.moveToFirst()) {
        gold = cursor.getInt(2);
        cursor.close();
        }
        MyDB.close();
        return gold;
    }

}
