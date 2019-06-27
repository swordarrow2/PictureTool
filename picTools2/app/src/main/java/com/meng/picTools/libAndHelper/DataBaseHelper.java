package com.meng.picTools.libAndHelper;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.os.*;
import android.util.Log;

import com.meng.picTools.LogTool;

public class DataBaseHelper {
    //表名
    private static final String TABLE_NAME = "record";//"failedPic";
    //表的主键
    private static final String KEY_ID = "_id";
    private static final String ID = "pixivId";
    //创建一个表的sql语句
    private static final String sql = "create table "
            + TABLE_NAME + "( " + KEY_ID
            + " integer primary key autoincrement,"
            + ID + " text)";
    private static SQLiteOpenHelper sqLiteOpenHelper;

    public static void init(Context context) {
        sqLiteOpenHelper = new SQLiteOpenHelper(context, Environment.getExternalStorageDirectory() + "/" + "note.db", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(sql);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                //   db.execSQL("drop table record");
                //onCreate(db);
            }
        };
    }

    public static void searchDataBase() {
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        StringBuilder stringBuilder = new StringBuilder();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            stringBuilder.append("word:").append(cursor.getString(0)).append(" ").append("detail").append(cursor.getString(1)).append("\n");
        }
        cursor.close();
        db.close();
        LogTool.i(stringBuilder.toString());
    }

    //插入一条数据
    public static long insertData(String id) {
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, id);
        return db.insert(TABLE_NAME, null, values);
    }

    //查询数据，返回一个Cursor
    public static Cursor query() {
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        return db.rawQuery("select * from " + TABLE_NAME, null);
    }

    //根据主键删除某条记录
    public static void deleteData(String pixivId) {
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        db.delete(TABLE_NAME, ID + "=?", new String[]{pixivId});
    }

  /* public static long updateData(String id, String type, boolean ok) {
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, id);
        values.put(TYPE, type);
        values.put(STATU, ok);
        return db.update("record", values, ID + "=?", new String[]{id});
    }*/
}

