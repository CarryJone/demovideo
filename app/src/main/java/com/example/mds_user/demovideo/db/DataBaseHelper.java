package com.example.mds_user.demovideo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import mds.approval.lib.XmlUtility;

public class DataBaseHelper extends SQLiteOpenHelper {
	
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context The context to use. Usually your Application or Activity object.
     */
    public DataBaseHelper(Context context) {
    	super(context, "Voide.db", null,1);
    }
    

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
	    String strInit_Table = null;
		strInit_Table = "CREATE TABLE IF NOT EXISTS " +"video"+ " (" +
				"_id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"file_path" + " TEXT, " +  //路徑
				"original_file" + " TEXT, " +  //原檔路徑
				"filename" + " TEXT, " +  //檔案名稱
				"casename" + " TEXT, " +  //案件名稱
				"policy_number" + " TEXT, " +  //保單編號
				"identity_card" + " TEXT, " +//身分證
				"creatid" + " TEXT "+");"; //ID

		
		db.execSQL(strInit_Table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	//查詢資料表
    public Cursor getCursor(SQLiteDatabase db){
    	String[] columns = 	{"_id" ,
				"file_path" ,
				"original_file",
				"filename" ,
				"casename",
				"policy_number",
				"identity_card" ,
				"creatid",};
    	
    	Cursor cursor = db.query("video", columns, null, null, null, null, null);
    	
    	return cursor;
    }




}
