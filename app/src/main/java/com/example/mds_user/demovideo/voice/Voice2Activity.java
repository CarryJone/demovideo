package com.example.mds_user.demovideo.voice;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Voice2Activity extends Activity {


    private String fileName = null;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    private Button start,stop,pause;
    private Context context;
    private ListView listView;
    private ArrayList<String> data ;
    private TextToSpeech tts; //語音
    private int num =  -1;
    private Voide_Audio_DataBase dataBase;
    private boolean isPause = false;// 当前录音是否处于暂停状态
    private ArrayList<String> mList = new ArrayList<String>();// 待合成的录音片段
    private ArrayList<String> list = new ArrayList<String>();// 已合成的录音片段
    private String deleteStr = null; // 列表中要删除的文件名
    private Timer timer;
    private TextView time;
    private int second = 0;
    private int minute = 0;
    private int hour = 0;
    private long limitTime = 0;// 录音文件最短事件1秒
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
    private String gettim(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
        String str = formatter.format(curDate);
        return str;
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            time.setText("您本次的录音时长为：       "
                    + String.format("%1$02d:%2$02d:%3$02d", hour, minute,
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
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        fileName = MyFileUtils.before_path+"/"+gettim()+".amr";
        myAudioRecorder.setOutputFile(fileName);
        if (!isPause) {
            // 新录音清空列表
            mList.clear();
        }
        isPause = false;
        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
            recordTime();
            limitTime = System.currentTimeMillis();
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

    public void pause(View view){
        if (isPause) {
            // 完成录音
            stop();
        } else {
            // 暂停录音
            try {
                pauseRecord();
            } catch (InterruptedException e) {
                // 当一个线程处于等待，睡眠，或者占用，也就是说阻塞状态，而这时线程被中断就会抛出这类错误
                // 上百次测试还未发现这个异常，但是需要捕获
                e.printStackTrace();
            }
        }
    }
    public void stop(View view){
        stop();
    }
    public void stop() {
        myAudioRecorder.release();
        myAudioRecorder = null;
        stop.setEnabled(false);
        Toast.makeText(getApplicationContext(), "Audio recorded successfully",
                Toast.LENGTH_LONG).show();
//        File file = new File(MyFileUtils.before_path + "/" + MyFileUtils.name + ".mp4");
//        Dialog_mes dialog_mes = new Dialog_mes(context,true,file);
//        dialog_mes.show();


//        File file = new File(MyFileUtils.before_path + "/" + dataBase.getFilename() + ".mp4");
//        dataBase.setOriginal_file(file);
//        dataBase.setFile(null);
//        VoideUtils.UpDataFromDB(dataBase);
//        Intent intent = new Intent(context, CasedetailsActivity.class);
//        intent.putExtra("num", num);
//        setResult(103, intent);
//        finish();
        isPause = false;

        fileName = MyFileUtils.before_path + "/" + gettim() + ".amr";
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileInputStream fileInputStream = null;
        try {
            for (int i = 0; i < mList.size(); i++) {
                File file = new File(mList.get(i));
                // 把因为暂停所录出的多段录音进行读取
                fileInputStream = new FileInputStream(file);
                byte[] mByte = new byte[fileInputStream.available()];
                int length = mByte.length;
                // 第一个录音文件的前六位是不需要删除的
                if (i == 0) {
                    while (fileInputStream.read(mByte) != -1) {
                        fileOutputStream.write(mByte, 0, length);
                    }
                }
                // 之后的文件，去掉前六位
                else {
                    while (fileInputStream.read(mByte) != -1) {

                        fileOutputStream.write(mByte, 6, length - 6);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fileOutputStream.flush();
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 录音结束 、时间归零
            minute = 0;
            hour = 0;
            second = 0;
        }
        // 不管合成是否成功、删除录音片段
        for (int i = 0; i < mList.size(); i++) {
            File file = new File(mList.get(i));
            if (file.exists()) {
                file.delete();
            }
        }
    }
    // 判断点击事件的时间间隔
    // 点击速度过快，比如在同一秒中点击三次，只会产生一个录音文件，因为命名一样。
    private boolean limitTime() {
        limitTime = System.currentTimeMillis() - limitTime;
        if (limitTime >= 1100) {
            limitTime = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }
    private void pauseRecord() throws InterruptedException {
        if (System.currentTimeMillis()-limitTime<1100) {
            //录音文件不得低于一秒钟
            Toast.makeText(this, "录音时间长度不得低于1秒钟！", Toast.LENGTH_SHORT).show();
            return ;
        }

        myAudioRecorder.stop();
        myAudioRecorder.release();
        timer.cancel();
        isPause = true;
        // 将录音片段加入列表
        mList.add(fileName);
        start.setEnabled(true);

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
