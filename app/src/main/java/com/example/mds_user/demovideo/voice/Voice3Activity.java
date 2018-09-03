package com.example.mds_user.demovideo.voice;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mds_user.demovideo.R;
import com.example.mds_user.demovideo.RecorderApplication;
import com.example.mds_user.demovideo.VoideUtils;
import com.example.mds_user.demovideo.Voide_Audio_DataBase;
import com.example.mds_user.demovideo.film.MyFileUtils;
import com.example.mds_user.demovideo.listpage.CasedetailsActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Voice3Activity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainFragment";
    private static final int REQUEST_CODE_PERMISSIONS = 0x1;

    private Button mStartButton;
    private Button mPauseButton;
    private Button mStopButton;
    private TextView type;
    private Uri mAudioRecordUri;
    private String mActiveRecordFileName;
    private Timer timer;
    private TextView time;
    private AudioRecorder mAudioRecorder;
    private Voide_Audio_DataBase dataBase;
    private int num =  -1;
    private int second = 0;
    private int minute = 0;
    private int hour = 0;
    private ArrayList<String> data ;
    private ListView listView;
    private TextToSpeech tts; //語音
    private Context context;
    private boolean isPause = false;
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice3);
        context = this;
        num = getIntent().getIntExtra("num",-1);
        dataBase = VoideUtils.VADataLIST.get(num);
        final RecorderApplication application = RecorderApplication.getApplication(this);
        mAudioRecorder = application.getRecorder();
//        if (mAudioRecorder == null
//                || mAudioRecorder.getStatus() == AudioRecorder.Status.STATUS_UNKNOWN) {
        mAudioRecorder = AudioRecorderBuilder.with(application)
                    .fileName(getNextFileName())
                    .config(AudioRecorder.MediaRecorderConfig.DEFAULT)
                    .loggable()
                    .build();
        application.setRecorder(mAudioRecorder);
//        }
        mStartButton = (Button) findViewById(R.id.buttonstart);
        mStartButton.setOnClickListener(this);
        mPauseButton = (Button) findViewById(R.id.buttonpause);
        mPauseButton.setOnClickListener(this);
        mStopButton = (Button) findViewById(R.id.buttonstop);
        mStopButton.setOnClickListener(this);
        time = (TextView) findViewById(R.id.time);
        type = (TextView) findViewById(R.id.type);
        listView = (ListView) findViewById(R.id.liststr);

        mStartButton.setEnabled(true);
        mPauseButton.setEnabled(false);
        mStopButton.setEnabled(false);
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
        createLanguageTTS();
    }
    @Override
    protected void onResume() {
        super.onResume();
        listView.setAdapter(new VoiceAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tts.speak(data.get(position), TextToSpeech.QUEUE_FLUSH,null);

            }
        });
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
    @Override
    public void onDestroy() {
        if (mAudioRecorder.isRecording()) {
            mAudioRecorder.cancel();
          setResult(Activity.RESULT_CANCELED);
        }
        if (tts!=null){
            tts.stop();
        }
        super.onDestroy();
    }


    private String getNextFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
        String str = formatter.format(curDate);
        MyFileUtils.name = "voice_"+str;
        return  MyFileUtils.before_path + "/" + MyFileUtils.name + ".mp4";
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void tryStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final int checkAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
            final int checkStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkAudio != PackageManager.PERMISSION_GRANTED || checkStorage != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

                } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                } else {
                    requestPermissions(new String[]{
                                    Manifest.permission.RECORD_AUDIO,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_PERMISSIONS);
                }
            } else {
                start();
            }
        } else {
            start();
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                boolean userAllowed = true;
                for (final int result : grantResults) {
                    userAllowed &= result == PackageManager.PERMISSION_GRANTED;
                }
                if (userAllowed) {
                    start();
                } else {

                }
                break;
            default:
                break;
        }
    }


    private void start() {
        mAudioRecorder.start(new AudioRecorder.OnStartListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onException(Exception e) {
            setResult(Activity.RESULT_CANCELED);

            }
        });
        recordTime();
    }

    synchronized void pause(final boolean isStop) {
        mAudioRecorder.pause(new AudioRecorder.OnPauseListener() {
            @Override
            public void onPaused(String activeRecordFileName) {
                mActiveRecordFileName = activeRecordFileName;
                setResult(Activity.RESULT_OK,
                        //new Intent().setData(Uri.parse(mActiveRecordFileName)));
                        new Intent().setData(saveCurrentRecordToMediaDB(mActiveRecordFileName)));
                dataBase.setFilename( MyFileUtils.name);
                File file = new File(mActiveRecordFileName);
                dataBase.setOriginal_file(file);
                dataBase.setFile(null);
                VoideUtils.UpDataFromDB(dataBase);
                if (isStop){
                    Intent intent = new Intent(context, CasedetailsActivity.class);
                    intent.putExtra("num",num);
                    setResult(103,intent);
                    finish();
                }
            }

            @Override
            public void onException(Exception e) {
                setResult(Activity.RESULT_CANCELED);

            }
        });
    }


    /**
     * Creates new item in the system's media database.
     *
     * @see <a href="https://github.com/android/platform_packages_apps_soundrecorder/blob/master/src/com/android/soundrecorder/SoundRecorder.java">Android Recorder source</a>
     */
    public Uri saveCurrentRecordToMediaDB(final String fileName) {
        if (mAudioRecordUri != null) return mAudioRecordUri;

        final Activity activity = this;
        final Resources res = activity.getResources();
        final ContentValues cv = new ContentValues();
        final File file = new File(fileName);
        final long current = System.currentTimeMillis();
        final long modDate = file.lastModified();
        final Date date = new Date(current);
        final String dateTemplate = res.getString(R.string.audio_db_title_format);
        final SimpleDateFormat formatter = new SimpleDateFormat(dateTemplate, Locale.getDefault());
        final String title = formatter.format(date);
        final long sampleLengthMillis = 1;
        // Lets label the recorded audio file as NON-MUSIC so that the file
        // won't be displayed automatically, except for in the playlist.
        cv.put(MediaStore.Audio.Media.IS_MUSIC, "0");

        cv.put(MediaStore.Audio.Media.TITLE, title);
        cv.put(MediaStore.Audio.Media.DATA, file.getAbsolutePath());
        cv.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
        cv.put(MediaStore.Audio.Media.DATE_MODIFIED, (int) (modDate / 1000));
        cv.put(MediaStore.Audio.Media.DURATION, sampleLengthMillis);
        cv.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*");
        cv.put(MediaStore.Audio.Media.ARTIST, res.getString(R.string.audio_db_artist_name));
        cv.put(MediaStore.Audio.Media.ALBUM, res.getString(R.string.audio_db_album_name));

        Log.d(TAG, "Inserting audio record: " + cv.toString());

        final ContentResolver resolver = activity.getContentResolver();
        final Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Log.d(TAG, "ContentURI: " + base);

        mAudioRecordUri = resolver.insert(base, cv);
        if (mAudioRecordUri == null) {
            return null;
        }
        activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mAudioRecordUri));
        return mAudioRecordUri;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonstart) {
            tryStart();
            if (!isPause) {
                if (dataBase.getFile() != null) {
                    dataBase.getFile().delete();
                }
                if (dataBase.getOriginal_file() != null) {
                    dataBase.getOriginal_file().delete();
                }
                dataBase.setFile(null);
                dataBase.setOriginal_file(null);
            }
            isPause = false;
            type.setText("正在錄音...");
            mStartButton.setEnabled(false);
            mPauseButton.setEnabled(true);
            mStopButton.setEnabled(true);


        } else if (i == R.id.buttonpause) {
            pause(false);
            isPause = true;
            type.setText("暫停錄音...");
            timer.cancel();
            mStartButton.setEnabled(true);
            mPauseButton.setEnabled(false);
            mStopButton.setEnabled(true);

        } else if (i == R.id.buttonstop) {
            if (isPause) {
                Intent intent = new Intent(context, CasedetailsActivity.class);
                intent.putExtra("num", num);
                setResult(103, intent);
                finish();
            } else {
                pause(true);
            }


        } else {
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
    class VoiceAdapter extends BaseAdapter {

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

}
