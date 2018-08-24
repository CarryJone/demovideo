package com.example.mds_user.demovideo.voice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mds_user.demovideo.R;
import com.example.mds_user.demovideo.VoideUtils;
import com.example.mds_user.demovideo.Voide_Audio_DataBase;
import com.example.mds_user.demovideo.film.Dialog_mes;
import com.example.mds_user.demovideo.film.MyFileUtils;
import com.example.mds_user.demovideo.listpage.CasedetailsActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class VoiceActivity extends Activity {

    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    private Button start,stop,play,pause;
    private Context context;
    private ListView listView;
    private ArrayList<String> data ;
    private TextToSpeech tts; //語音
    private int num =  -1;
    private Voide_Audio_DataBase dataBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        start = (Button)findViewById(R.id.button1);
        stop = (Button)findViewById(R.id.button2);
        play = (Button)findViewById(R.id.button3);
        pause = (Button)findViewById(R.id.button4);
        listView = (ListView) findViewById(R.id.liststr);
        context = this;
        num = getIntent().getIntExtra("num",-1);
        stop.setEnabled(false);
        play.setEnabled(false);
        dataBase = VoideUtils.VADataLIST.get(num);
        data = new ArrayList<>();
        data.add("先生（女士）您好：為保障您的權益，根據法令規定，現在將以錄音（影）方式紀錄本次銷售過程，請問您是否同意接受錄音（影）？（若客戶不同意，則終止銷售過程，本客戶不可購買投資型商品）");
        data.add("我是，（出示登錄證）登錄證編號為" +
                "目前登錄（服務）於＆＆＆公司，並獲得保誠人壽授權銷售投資型保單。以下銷售過程中我將說明本次銷售商品的重要內容並交付重要文件，且進行錄音（影），請您聽完後出聲回覆是否瞭解，謝謝。");
        data.add("您所選擇的商品是保誠人壽投資型商品保單，繳費年期為年，繳繳費金額為元，保單相關費用包含保險成本與解約費用(轉換費用)，並自（扣除方式）中扣除費用，請問是否瞭解？");
//        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath();
//        String path = getFilesDir().getPath();
//        int result = MyFileUtils.createDir(outputFile + "/demos/file/tmp/before");
        createLanguageTTS();
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);


    }


    @Override
    protected void onResume() {
        super.onResume();
        listView.setAdapter(new VoiceAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tts.speak(data.get(position),TextToSpeech.QUEUE_FLUSH,null);

            }
        });
    }

    public void start(View view){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
        String str = formatter.format(curDate);
        MyFileUtils.name = "video_"+str;
        dataBase = VoideUtils.VADataLIST.get(num);
        dataBase.setFilename(MyFileUtils.name);
        Recording();
    }
    public void Recording(){
        myAudioRecorder.setOutputFile(MyFileUtils.before_path+"/"+ MyFileUtils.name+".mp4");
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pause(View view){
        try {
            myAudioRecorder.pause();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void stop(View view){
        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder  = null;
        stop.setEnabled(false);
        play.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Audio recorded successfully",
                Toast.LENGTH_LONG).show();
//        File file = new File(MyFileUtils.before_path + "/" + MyFileUtils.name + ".mp4");
//        Dialog_mes dialog_mes = new Dialog_mes(context,true,file);
//        dialog_mes.show();
        File file = new File(MyFileUtils.before_path + "/" + dataBase.getFilename()+ ".mp4");
        dataBase.setOriginal_file(file);
        dataBase.setFile(null);
        VoideUtils.UpDataFromDB(dataBase);
        Intent intent = new Intent(context, CasedetailsActivity.class);
        intent.putExtra("num",num);
        setResult(103,intent);
        finish();
    }

    public void play(View view) throws IllegalArgumentException,
            SecurityException, IllegalStateException, IOException{

        MediaPlayer m = new MediaPlayer();
        m.setDataSource(MyFileUtils.before_path+"/"+ MyFileUtils.name+".mp4");
        m.prepare();
        m.start();
        Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();

    }
    class VoiceAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.voice_textitem,null);
            TextView textView = (TextView) view.findViewById(R.id.text);
            textView.setText(data.get(position));
            return view;
        }
    }
    private void createLanguageTTS() {
        if( tts == null ) {
            tts = new TextToSpeech(this, new TextToSpeech.OnInitListener(){
                @Override
                public void onInit(int arg0) {
                    // TTS 初始化成功
                    if( arg0 == TextToSpeech.SUCCESS ) {
                        tts.setPitch(1.0f); // 音調
                        tts.setSpeechRate(1); // 速度
                        // 指定的語系
                        Locale locale = Locale.TAIWAN;
                        // 目前指定的【語系+國家】TTS, 已下載離線語音檔, 可以離線發音
                        if( tts.isLanguageAvailable(locale) == TextToSpeech.LANG_COUNTRY_AVAILABLE ) {
                            tts.setLanguage(locale);
                        }
                    }
                }}
            );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts!=null){
            tts.stop();
        }
    }
}
