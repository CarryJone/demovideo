package com.example.mds_user.demovideo.voice;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.example.mds_user.demovideo.film.MyFileUtils;
import com.example.mds_user.demovideo.listpage.CasedetailsActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class VoiceActivity extends Activity {

    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    private Button start,stop,pause;
    private Context context;
    private ListView listView;
    private ArrayList<String> data ;
    private TextToSpeech tts; //語音
    private int num =  -1;
    private Timer timer;
    private TextView time;
    private boolean isRecording = false;
    private Voide_Audio_DataBase dataBase;
    private int second = 0;
    private int minute = 0;
    private int hour = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        start = (Button)findViewById(R.id.buttonstart);
        stop = (Button)findViewById(R.id.buttonstop);

        pause = (Button)findViewById(R.id.buttonpause);
        listView = (ListView) findViewById(R.id.liststr);
        time = (TextView) findViewById(R.id.time);
        context = this;
        num = getIntent().getIntExtra("num",-1);
        stop.setEnabled(false);
        pause.setEnabled(false);
        dataBase = VoideUtils.VADataLIST.get(num);
        data = new ArrayList<>();
        data.add("5.說明商品重要條款內容、除外責任、保險商品說明書重要內容、建議書內容\n" +
                "@商品重要條款內容、除外責任、保險商品說明書重要內容\n" +
                "商品說明書的重要保單條款摘要已說明本商品重要條款內容與除外責任，另於重要特性與保險計畫詳細說明中亦說明本商品重要內容，請問您清楚嗎?\n" +
                "@建議書內容\n" +
                "保單帳戶價值試算表於建議書中已有說明，請問您清楚嗎?\n");
        data.add("6.契撤期說明\n" +
                "自簽收保單隔日起算，您有10日撤銷契約的權益，請問您清楚嗎?\n");
        data.add("7.要保人聲明\n" +
                "最後請您念出以下聲明文字以確認您的風險承擔與投保意願。\n" +
                "我，ＯＯＯ(要保人姓名)已充分了解本商且願意承擔投資風險，所有文件均由本人親自簽名投保。\n");
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
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pause(View view){
        myAudioRecorder.pause();
        start.setEnabled(true);
        pause.setEnabled(false);
        timer.cancel();
    }
    @TargetApi(Build.VERSION_CODES.N)
    public void start(View view){
        recordTime();
        if (isRecording){
            myAudioRecorder.resume();
            pause.setEnabled(true);
        }else {
            Recording();
        }
    }
    public void Recording() {
        isRecording = true;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
        String str = formatter.format(curDate);
        MyFileUtils.name = "voice_"+str;
        dataBase = VoideUtils.VADataLIST.get(num);
        dataBase.setFilename(MyFileUtils.name);
        myAudioRecorder.setOutputFile(MyFileUtils.before_path + "/" + MyFileUtils.name + ".mp4");
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
        pause.setEnabled(true);
        stop.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            time.setText(String.format("%1$02d:%2$02d:%3$02d", hour, minute,
                    second));
            super.handleMessage(msg);
        }
    };
    private void recordTime() {
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                second++;
                if (second >= 60) {
                    second = 0;
                    minute++;
                    if (minute >= 60) {
                        minute = 0;
                        hour++;
                    }
                }
                handler.sendEmptyMessage(1);
            }

        };
        timer = new Timer();
        timer.schedule(timerTask, 1000, 1000);
    }



    public void stop(View view){
        isRecording = false;
        timer.cancel();
        second = 0;
        minute = 0;
        hour = 0;
        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder  = null;
        stop.setEnabled(false);
        Toast.makeText(getApplicationContext(), "Audio recorded successfully",
                Toast.LENGTH_LONG).show();
//        File file = new File(MyFileUtils.before_path + "/" + MyFileUtils.name + ".mp4");
//        Dialog_mes dialog_mes = new Dialog_mes(context,true,file);
//        dialog_mes.show();
        File file = new File(MyFileUtils.before_path + "/" + dataBase.getFilename()+ ".mp4");
        if (dataBase.getFile()!=null){
            dataBase.getFile().delete();
        }
        if (dataBase.getOriginal_file()!=null){
            dataBase.getOriginal_file().delete();
        }
        dataBase.setOriginal_file(file);
        dataBase.setFile(null);
        VoideUtils.UpDataFromDB(dataBase);
        Intent intent = new Intent(context, CasedetailsActivity.class);
        intent.putExtra("num",num);
        setResult(103,intent);
        finish();
    }

//    public void play(View view) throws IllegalArgumentException,
//            SecurityException, IllegalStateException, IOException{
//
//        MediaPlayer m = new MediaPlayer();
//        m.setDataSource(MyFileUtils.before_path+"/"+ MyFileUtils.name+".mp4");
//        m.prepare();
//        m.start();
//        Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
//
//    }
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
