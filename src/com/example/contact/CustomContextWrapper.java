package com.example.contact;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class CustomContextWrapper extends ContextWrapper {

	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory) {
		// TODO Auto-generated method stub
		return super.openOrCreateDatabase(name, mode, factory);
	}
	
	// 该重写方法只支持SDK为11以上
		@SuppressLint("NewApi")
		@Override
		public SQLiteDatabase openOrCreateDatabase(String name, int mode,
				CursorFactory factory, DatabaseErrorHandler errorHandler) {
			// TODO Auto-generated method stub
			return super.openOrCreateDatabase(getDatabasePath(name).getAbsolutePath(), mode, factory, errorHandler);
		}

	@SuppressLint("SdCardPath")
	@Override
	public File getDatabasePath(String name) {
		// TODO Auto-generated method stub
		File dbFolder = new File("/sdcard/CONTACT/DB/" + File.separator + "mycontact2.db");  
        // 目录不存在则自动创建目录  
        if (!dbFolder.getParentFile().exists())
        {  
            try
        	{                 
            	dbFolder.getParentFile().mkdirs();
            }
        	catch(Exception e)
        	{
        		e.printStackTrace();
            }
        }
        
		return dbFolder;
	}

	public CustomContextWrapper(Context base) {
		super(base);
		// TODO Auto-generated constructor stub
	}

}
