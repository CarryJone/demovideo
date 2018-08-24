package com.example.mds_user.demovideo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.mds_user.demovideo.filelist.FilelistActivity;
import com.example.mds_user.demovideo.film.FilmActivity;
import com.example.mds_user.demovideo.gcm.GCMUtility;
import com.example.mds_user.demovideo.listpage.ListpageActivity;
import com.example.mds_user.demovideo.video.VideoActivity;
import com.example.mds_user.demovideo.voice.VoiceActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
Button videobtn,filmbtn,filebtn,voice,listpage;
Context context;
boolean isPermission = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String userid = bundle.getString("userid");
            String pwd = bundle.getString("pwd");
        }
        context = this;
        videobtn = (Button) findViewById(R.id.video);
        filmbtn = (Button) findViewById(R.id.film);
        filebtn = (Button) findViewById(R.id.modify);
        voice = (Button) findViewById(R.id.voice);
        listpage = (Button) findViewById(R.id.listpage);
        videobtn.setOnClickListener(this);
        filmbtn.setOnClickListener(this);
        filebtn.setOnClickListener(this);
        voice.setOnClickListener(this);
        listpage.setOnClickListener(this);
        VoideUtils.initSystem(context);
//        String path = getFilesDir().getPath();//內部路徑
////      String path = Environment.getExternalStorageDirectory().getAbsolutePath();//外部路徑
//        int result = MyFileUtils.createDir(path + "/demos/file/tmp/before",0);//創建剪輯前資料夾
//        int result2 = MyFileUtils.createDir(path + "/demos/file/tmp/after",1);//創建剪輯後資料夾
        setPermission();


    }


    @Override
    protected void onResume() {
        super.onResume();
       final Handler setGCM = new Handler();
       setGCM.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPermission){
                    callGCM();
                    setGCM.removeCallbacks(this);
                }else{
                    setPermission();
                    setGCM.postDelayed(this,1000);
                }
            }
        }, 1000);

       //設定填寫欄位
        final List<String> textdata = new ArrayList<>();
        textdata.add("檔案名稱");
        textdata.add("身分證");
        textdata.add("保單編號");
        VoideUtils.text = textdata;
       //讀取DB  將資料寫入共用變數
        VoideUtils.VADataLIST.clear();
        SQLiteDatabase db = VoideUtils.databaseHelper.getWritableDatabase();
        Cursor cursor = VoideUtils.databaseHelper.getCursor(db);
        cursor.moveToFirst();
        for (int i=0;i<cursor.getCount();i++){
            File file = null;
            File file1 = null;
            if (!cursor.getString(1).isEmpty()) {
                file = new File(cursor.getString(1));
            }
            if (!cursor.getString(2).isEmpty()) {
                file1 = new File(cursor.getString(2));
            }
            Map<String,String> map = new HashMap<>();
            map.put(VoideUtils.text.get(0),cursor.getString(4));
            map.put(VoideUtils.text.get(1),cursor.getString(6));
            map.put(VoideUtils.text.get(2),cursor.getString(5));
            Voide_Audio_DataBase dataBase = new Voide_Audio_DataBase(file,map);
            dataBase.setOriginal_file(file1);
            dataBase.setCreatid(cursor.getString(7));
            dataBase.setFilename(cursor.getString(3));
            VoideUtils.VADataLIST.add(dataBase);
            cursor.moveToNext();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video:
               Intent intent = new Intent(context, VideoActivity.class);
                startActivity(intent);
                return;
            case R.id.film:
                Intent intent2 = new Intent(context, FilmActivity.class);
                startActivity(intent2);
                return;
            case R.id.modify:
                Intent intent3 = new Intent(context, FilelistActivity.class);
                startActivity(intent3);
                return;
            case R.id.voice:
                Intent intent4 = new Intent(context, VoiceActivity.class);
                startActivity(intent4);
                return;
            case R.id.listpage:
                Intent intent5 = new Intent(context, ListpageActivity.class);
                startActivity(intent5);
                return;

        }
    }
    private void callGCM(){
        GCMUtility.register(context,"772493589586");
    }
    public void setPermission() {
        int permission1 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO);
        int permission2 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA);
        int permission3 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission4 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_PHONE_STATE);
        String[] permissions = new String[]{ android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_PHONE_STATE};
        if (permission1 != PackageManager.PERMISSION_GRANTED||permission2 != PackageManager.PERMISSION_GRANTED||permission3 != PackageManager.PERMISSION_GRANTED||permission4 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( MainActivity.this,
                    permissions,1);
          isPermission = false;
        }else{
            //已有權限，可進行檔案存取
         isPermission = true;
        }
    }


}
