package com.example.mds_user.demovideo.filelist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.mds_user.demovideo.R;
import com.example.mds_user.demovideo.VoideUtils;
import com.example.mds_user.demovideo.Voide_Audio_DataBase;
import com.example.mds_user.demovideo.film.MyFileUtils;
import com.example.mds_user.demovideo.listpage.CasedetailsActivity;

import java.io.File;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnK4LVideoListener;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;


public class TrimmerActivity extends AppCompatActivity implements OnTrimVideoListener, OnK4LVideoListener {

    private K4LVideoTrimmer mVideoTrimmer;
    private ProgressDialog mProgressDialog;
    private Context context;
    String path = "";
    int num = -1;
    Voide_Audio_DataBase dataBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trimmer);
        context = this;
        Intent extraIntent = getIntent();
        if (extraIntent != null) {
            path = extraIntent.getStringExtra("path");
            num = extraIntent.getIntExtra("num",-1);
        }
        dataBase = VoideUtils.VADataLIST.get(num);

        //setting progressbar
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("修剪影片...");

        mVideoTrimmer = ((K4LVideoTrimmer) findViewById(R.id.timeLine));
        if (mVideoTrimmer != null) {
            if (path.contains("voice")) {
                mVideoTrimmer.isVoice(true);
            }
            mVideoTrimmer.setMaxDuration(60*60*5);
            mVideoTrimmer.setOnTrimVideoListener(this);
            mVideoTrimmer.setOnK4LVideoListener(this);
            mVideoTrimmer.setDestinationPath(MyFileUtils.after_path+"/"+dataBase.getFilename()+".mp4");
            mVideoTrimmer.setVideoURI(Uri.parse(path));
            mVideoTrimmer.setVideoInformationVisibility(true);
        }
    }

    @Override
    public void onTrimStarted() {
        mProgressDialog.show();
    }

    @Override
    public void getResult(final Uri uri) {
        mProgressDialog.cancel();

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(TrimmerActivity.this, getString(R.string.video_saved_at, uri.getPath()), Toast.LENGTH_SHORT).show();
//            }
//        });
//        File file = new File(uri.getPath());
//        Uri uri2 = FileProvider.getUriForFile(this,"com.example.mds_user.demovideo.fileProvider",file);
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri2);
//        intent.setDataAndType(uri, "video/mp4");
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        startActivity(intent);

        File file = new File(MyFileUtils.after_path + "/" + dataBase.getFilename() + ".mp4");
        dataBase.setFile(file);
        VoideUtils.UpDataFromDB(dataBase);
        Intent intent = new Intent(context, CasedetailsActivity.class);
        intent.putExtra("num",num);
        startActivity(intent);
        finish();
    }

    @Override
    public void cancelAction() {
        mProgressDialog.cancel();
        mVideoTrimmer.destroy();
        Intent intent = new Intent(context, CasedetailsActivity.class);
        intent.putExtra("num",num);
        startActivity(intent);
        finish();
    }

    @Override
    public void onError(final String message) {
        mProgressDialog.cancel();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TrimmerActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onVideoPrepared() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TrimmerActivity.this, "onVideoPrepared", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mProgressDialog.cancel();
        mVideoTrimmer.destroy();
        Intent intent = new Intent(context, CasedetailsActivity.class);
        intent.putExtra("num",num);
        startActivity(intent);
        finish();
    }
}
