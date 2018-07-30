package com.example.mds_user.demovideo.voice;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mds_user.demovideo.R;
import com.example.mds_user.demovideo.film.Dialog_mes;
import com.example.mds_user.demovideo.film.FileUtils;

import java.io.IOException;

public class VoiceActivity extends Activity {

    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    private Button start,stop,play;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        start = (Button)findViewById(R.id.button1);
        stop = (Button)findViewById(R.id.button2);
        play = (Button)findViewById(R.id.button3);
        context = this;
        stop.setEnabled(false);
        play.setEnabled(false);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath();
//        String path = getFilesDir().getPath();
        int result = FileUtils.createDir(outputFile + "/demos/file/tmp/before");

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);


    }

    public void start(View view){
        Dialog_mes dialog_mes = new Dialog_mes(context,true);
        dialog_mes.show();
    }
    public void Recording(){
        myAudioRecorder.setOutputFile(FileUtils.path+"/"+FileUtils.name+".mp4");
        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        start.setEnabled(false);
        stop.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();

    }

    public void stop(View view){
        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder  = null;
        stop.setEnabled(false);
        play.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Audio recorded successfully",
                Toast.LENGTH_LONG).show();
    }

    public void play(View view) throws IllegalArgumentException,
            SecurityException, IllegalStateException, IOException{

        MediaPlayer m = new MediaPlayer();
        m.setDataSource(FileUtils.path+"/"+FileUtils.name+".mp4");
        m.prepare();
        m.start();
        Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();

    }

}
