package com.meng.picTools.dataBase;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.os.*;

public class DBHelper extends SQLiteOpenHelper{
	//表名
	private static final String TABLE_NAME="notes";
	//表的主键
	private static final String KEY_ID = "_id";
	//表中事件的题目
	private static final String TITLE = "title";
	//添加的内容
	private static final String CONTENT = "content";
	//创建一个表的sql语句
	private String sql = "create table "
	+TABLE_NAME
	+"( "+KEY_ID 
	+" integer primary key autoincrement,"
	+TITLE+" text,"
	+CONTENT+" text)";

	public DBHelper(Context context){
		//创建一个数据库
		super(context,Environment.getExternalStorageDirectory()+"/"+"note.db",null,1);
	  }

	@Override
	public void onCreate(SQLiteDatabase db){
		//数据库中没有表时创建一个表
		db.execSQL(sql);
	  }
	//升级数据库
	@Override
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
//		db.execSQL("drop table notes if exits");
		onCreate(db);
	  }
	//插入一条数据
	public long insertData(String title,String content){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(TITLE,title);
		values.put(CONTENT,content);
		return db.insert(TABLE_NAME,null,values);
	  }
	//查询数据，返回一个Cursor
	public Cursor query(){
		SQLiteDatabase db = getReadableDatabase();
		return db.rawQuery("select * from notes",null);
	  }
	//根据主键删除某条记录
	public void deleteData(int id){
		SQLiteDatabase db = getWritableDatabase();
		db.delete("notes","_id=?",new String[]{String.valueOf(id)});
	  }
  }

