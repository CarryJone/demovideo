package com.example.mds_user.demovideo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.mds_user.demovideo.filelist.FilelistActivity;
import com.example.mds_user.demovideo.film.FilmActivity;
import com.example.mds_user.demovideo.gcm.GCMUtility;
import com.example.mds_user.demovideo.video.VideoActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
Button videobtn,filmbtn,filebtn;
Context context;
boolean isPermission = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        videobtn = (Button) findViewById(R.id.video);
        filmbtn = (Button) findViewById(R.id.film);
        filebtn = (Button) findViewById(R.id.modify);
        videobtn.setOnClickListener(this);
        filmbtn.setOnClickListener(this);
        filebtn.setOnClickListener(this);
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
