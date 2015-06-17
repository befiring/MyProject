package com.example.contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Text;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBaseHelper extends SQLiteOpenHelper{
	private static final String DATABASENAME="mycontact2";
	private static final int DATABASEVERSION=2;
	private static final String TABLENAME="mytab";
    
	public MyDataBaseHelper(Context context) {
		super(new CustomContextWrapper(context), DATABASENAME, null, DATABASEVERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		try {
			String sql="CREATE TABLE "+ TABLENAME+" ( "+"id INTEGER PRIMARY KEY,"+"name  Text not null, "+ "phone Text not null)";
			db.execSQL(sql);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if (newVersion>oldVersion) {
			db.execSQL("DROP TABLE IF EXISTS "+TABLENAME);
			onCreate(db);
		}

	}
	public void insert(SQLiteDatabase db,String name,String phone){
		String sql="INSERT INTO "+TABLENAME+" (name,phone)VALUES('"+name+"','"+phone+"')";
		db.execSQL(sql);
	}
	
	public void update(SQLiteDatabase db,int id,String name,String phone){
		String sql="UPDATE "+TABLENAME+" SET name='"+name+"',phone='"+phone+"'WHERE id="+id;
		db.execSQL(sql);
	}
	public void delete(SQLiteDatabase db,int id){
		String sql="DELETE FROM "+TABLENAME+" WHERE id="+id;
		db.execSQL(sql);
	}
    public List<String> query(SQLiteDatabase db){
    	List<String> all=new ArrayList<String>();
    	String sql="SELECT id,name,phone FROM "+TABLENAME;
    	Cursor result=db.rawQuery(sql, null);
    	for(result.moveToFirst();!result.isAfterLast();result.moveToNext()){
    		all.add("["+result.getInt(0)+"]"+""+result.getString(1)+","+result.getString(2));
    	}
    	
    	return all;
    }
    public List<Map<String, Object>> query2(SQLiteDatabase db){
    	List<Map<String, Object>> all=new ArrayList<Map<String,Object>>();
    	String sql="SELECT id,name,phone FROM "+TABLENAME;
    	Cursor result=db.rawQuery(sql, null);
    	for(result.moveToFirst();!result.isAfterLast();result.moveToNext()){
    		Map<String, Object> map=new HashMap<String, Object>();
    		map.put("id", result.getInt(0));
        	map.put("name", result.getString(1));
        	map.put("phone", result.getString(2));
    		all.add(map);
    	}
    	return all;
    }

	
	
}
