package com.meng.picTools.dataBase;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.os.*;

public class DataBaseHelper {
    //表名
    private static final String TABLE_NAME = "record";
    //表的主键
    private static final String KEY_ID = "_id";
    private static final String ID = "pixivId";
    private static final String TYPE = "type";
    private static final String STATU = "statu";
    //创建一个表的sql语句
    private static final String sql = "create table "
            + TABLE_NAME + "( " + KEY_ID
            + " integer primary key autoincrement,"
            + ID + " text," + TYPE + " type," + STATU + " statu)";
    private static SQLiteOpenHelper sqLiteOpenHelper;

    public static void init(Context context) {
        sqLiteOpenHelper = new SQLiteOpenHelper(context, Environment.getExternalStorageDirectory() + "/" + "note.db", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(sql);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		db.execSQL("drop table notes if exits");
                onCreate(db);
            }
        };
    }

    //插入一条数据
    public static long insertData(String id, String type, boolean ok) {
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, id);
        values.put(TYPE, type);
        values.put(STATU, ok);
        return db.insert(TABLE_NAME, null, values);
    }

    //查询数据，返回一个Cursor
    public static Cursor query() {
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        return db.rawQuery("select * from record", null);
    }

    //根据主键删除某条记录
    public static void deleteData(String pixivId) {
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        db.delete("record", "pixivId=?", new String[]{pixivId});
    }

    public static long updateData(String id, String type, boolean ok) {
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, id);
        values.put(TYPE, type);
        values.put(STATU, ok);
        return db.update("record", values, ID + "=?", new String[]{id});
    }
}

