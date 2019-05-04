package com.meng.picTools.dataBase;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.os.*;

public class DBHelper extends SQLiteOpenHelper{
	//表名
	private static final String TABLE_NAME="record";
	//表的主键
	private static final String KEY_ID = "_id";
	private static final String ID = "pixivId";
	private static final String TYPE = "type";
	private static final String STATU = "statu";
	//创建一个表的sql语句
	private String sql = "create table "
	+TABLE_NAME
	+"( "+KEY_ID 
	+" integer primary key autoincrement,"
	+ID+" text,"
	+TYPE+" type,"
	+STATU+" statu)";

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
	public long insertData(String id,String type,boolean ok){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ID,id);
		values.put(TYPE,type);
		values.put(STATU,ok);
		return db.insert(TABLE_NAME,null,values);
	  }
	//查询数据，返回一个Cursor
	public Cursor query(){
		SQLiteDatabase db = getReadableDatabase();
		return db.rawQuery("select * from record",null);
	  }
	//根据主键删除某条记录
	public void deleteData(int id){
		SQLiteDatabase db = getWritableDatabase();
		db.delete("record","_id=?",new String[]{String.valueOf(id)});
	  }

	public long updateData(String id,String type,boolean ok){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ID,id);
		values.put(TYPE,type);
		values.put(STATU,ok);
		return db.update("record",values,ID+"=?",new String[]{id});	  
	  }
  }

