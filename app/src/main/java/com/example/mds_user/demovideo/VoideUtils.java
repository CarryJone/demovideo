package com.example.mds_user.demovideo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.example.mds_user.demovideo.db.DataBaseHelper;
import com.example.mds_user.demovideo.film.MyFileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mds_user on 2018/8/17.
 */

public class VoideUtils {
    public static DataBaseHelper databaseHelper; // 建立DatabaseHelper物件-tammy
    public static ArrayList<Voide_Audio_DataBase> VADataLIST ;

    public static  List<String> text;
    public static void initSystem(Context aContext) {
        databaseHelper = new DataBaseHelper(aContext);
        VADataLIST = new ArrayList<>();
        text = new ArrayList<>();
        //創資料夾
        String path = aContext.getFilesDir().getPath();//內部路徑
        int result = MyFileUtils.createDir(path + "/demos/file/tmp/before",0);//創建剪輯前資料夾
        int result2 = MyFileUtils.createDir(path + "/demos/file/tmp/after",1);//創建剪輯後資料夾
    }
    public static void VideoPlay(Context context,File file){
        Uri uri = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//                uri = FileProvider.getUriForFile(context, "com.example.mds_user.demovideo.fileProvider",file);
//            }else{
//                uri = Uri.fromFile(file);
//            }
//        uri = FileProvider.getUriForFile(context, "com.example.mds_user.demovideo.fileProvider",file);
//        uri = Uri.parse(file.getPath());
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        intent.setDataAndType(uri, "video/mp4");
//        context.grantUriPermission(context.getPackageName(),uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        context.startActivity(intent);
        Intent intent = new Intent(context, VideoViewActivity.class);
        intent.putExtra("path",file.getPath());
        context.startActivity(intent);
    }
     synchronized public static void sendDataFromDB(Voide_Audio_DataBase dataBase){
        SQLiteDatabase db =databaseHelper.getWritableDatabase();
        String sql=" select count(*) from " +
               "video";
        //判斷是否有這張tab
        try{
            Cursor cursor = db.rawQuery(sql, null);
        }catch (Exception e){
            String strInit_Table = null;
            strInit_Table = "CREATE TABLE IF NOT EXISTS " +"video"+ " (" +
                    "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "file_path" + " TEXT, " +  //路徑
                    "original_file" + " TEXT, " +  //原檔路徑
                    "filename" + " TEXT, " +  //檔案名稱
                    "casename" + " TEXT, " +  //案件名稱
                    "policy_number" + " TEXT, " +  //保單編號
                    "identity_card" + " TEXT, " +//身分證
                    "creatid"+"TEXT"+");"; //ID
            db.execSQL(strInit_Table);
        }
        String casename = dataBase.getData().get(text.get(0));
        String identity_card = dataBase.getData().get(text.get(1));
        String policy_number = dataBase.getData().get(text.get(2));
        String file_path = "";
        String original_file = "";
        String creatid = dataBase.getCreatid();
        ContentValues hm = new ContentValues();
        if ( dataBase.getFile()!= null) {
            file_path = dataBase.getFile().getPath();
        }
         if ( dataBase.getOriginal_file()!= null) {
             original_file = dataBase.getOriginal_file().getPath();
         }

         hm.put("file_path", file_path);
         hm.put("original_file",original_file);
        hm.put("filename",dataBase.getFilename() );
        hm.put("casename",casename );
        hm.put("identity_card", identity_card);
        hm.put("policy_number", policy_number);
        hm.put("creatid", creatid);

        db.insert("video", null, hm);
    }
    synchronized public static void UpDataFromDB(Voide_Audio_DataBase dataBase) {
        SQLiteDatabase db =databaseHelper.getWritableDatabase();
        String casename = dataBase.getData().get(text.get(0));
        String identity_card = dataBase.getData().get(text.get(1));
        String policy_number = dataBase.getData().get(text.get(2));
        String file_path = "";
        String original_file = "";
        String creatid = dataBase.getCreatid();
        ContentValues hm = new ContentValues();
        if ( dataBase.getFile()!= null) {
            file_path = dataBase.getFile().getPath();
        }
        if ( dataBase.getOriginal_file()!= null) {
            original_file = dataBase.getOriginal_file().getPath();
        }

        hm.put("file_path", file_path);
        hm.put("original_file",original_file);
        hm.put("filename",dataBase.getFilename() );
        hm.put("casename",casename );
        hm.put("identity_card", identity_card);
        hm.put("policy_number", policy_number);
        hm.put("creatid", creatid);

        db.update("video", hm,"creatid = ?", new String[]{(dataBase.getCreatid())});
    }
}
