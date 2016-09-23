package com.example.leo.mainview.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
/*
    정보문화사의 모바일 애플리케이션 개발(이성환 지음)을 참고
 */

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Travel.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Travel";
    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" ( "
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "_photo TEXT, "
                    + "_lat double, "
                    + "_lon double, "
                    + "_time TEXT, "
                    + "_tag TEXT )";

    SQLiteDatabase db;

    public DbOpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        /*String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" ( "
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + "_photo BLOB, "
                + "_lat double, "
                + "_lon double, "
                + "_time TEXT, "
                + "_tag TEXT )";
        */
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }

    public void create(Travel travel){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(CREATE_TABLE);

        ContentValues values = new ContentValues();
        values.put("_photo", travel._photo);
        values.put("_lat", travel._lat);
        values.put("_lon", travel._lon);
        values.put("_time", travel._time);
        values.put("_tag", travel._tag);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public Travel read(int id){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] {"_id", "_photo", "_lat", "_lon", "_time", "_tag"}, " _id = ? ", new String[] {String.valueOf("_id")}, null, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }

        Travel travel = parseCursor(cursor);
        return travel;

    }

    private Travel parseCursor(Cursor cursor){
        Travel travel = new Travel();

        travel._id = cursor.getInt(cursor.getColumnIndex("_id"));
        travel._photo = cursor.getString(cursor.getColumnIndex("_photo"));
        travel._lat = cursor.getDouble(cursor.getColumnIndex("_lat"));
        travel._lon = cursor.getDouble(cursor.getColumnIndex("_lon"));
        travel._time = cursor.getString(cursor.getColumnIndex("_time"));
        travel._tag = cursor.getString(cursor.getColumnIndex("_tag"));

        return travel;
    }

    public int update(Travel travel){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("_photo", travel._photo);

        return db.update(TABLE_NAME, values, "_id=?", new String[] {String.valueOf(travel._id)});

    }

    public void delete(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "_id = ?", new String[] {String.valueOf("_id")});
        db.close();
    }

    public List<Travel> list() {
        List<Travel> list = new ArrayList<Travel>();

        String selectQuery = "SELECT _id, _photo, _lat, _lon, _time, _tag FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Travel travel = parseCursor(cursor);
                list.add(travel);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public void reset () throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("drop table " + TABLE_NAME);
        //db.close();
        this.onCreate(db);
    }

    public Cursor getAllPoints() {
        return getReadableDatabase().query(TABLE_NAME,
                new String[] {"_id", "_photo", "_lat", "_lon", "_time", "_tag"}, null, null, null, null, null);
    }
}