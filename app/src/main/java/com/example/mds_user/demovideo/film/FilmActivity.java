package com.example.mds_user.demovideo.film;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.mds_user.demovideo.R;

import java.io.IOException;

/**
 * class name：TestBasicVideo<BR>
 * class description：一個簡單的錄製視頻例子<BR>
 * PS：實現基本的錄製保存檔 <BR>
 *
 * @version 1.00 2011/09/21
 * @author CODYY)peijiangping
 */
public class FilmActivity extends Activity implements SurfaceHolder.Callback {
    private Button start;// 開始錄製按鈕
    private Button stop;// 停止錄製按鈕
    private MediaRecorder mediarecorder;// 錄製視頻的類
    private SurfaceView surfaceview;// 顯示視頻的控制項
    // 用來顯示視頻的一個介面，我靠不用還不行，也就是說用mediarecorder錄製視頻還得給個介面看
// 想偷偷錄視頻的同學可以考慮別的辦法。。嗯需要實現這個介面的Callback介面
    private SurfaceHolder surfaceHolder;
    private Context mContext;
    private Handler handler;
    private  Runnable r;
    private int count = 0;
    private  boolean isgo = false;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉標題列
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 設置全屏
        mContext = this;
// 設置橫屏顯示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
// 選擇支援半透明模式,在有surfaceview的activity中使用。
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_film);
        init();
    }

    private void init() {
        start = (Button) this.findViewById(R.id.start);
        stop = (Button) this.findViewById(R.id.stop);
        start.setOnClickListener(new TestVideoListener());
        stop.setOnClickListener(new TestVideoListener());
        surfaceview = (SurfaceView) this.findViewById(R.id.surfaceview);
        SurfaceHolder holder = surfaceview.getHolder();// 取得holder
        holder.addCallback(this); // holder加入回檔介面
// setType必須設置，要不出錯.
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        stop.setEnabled(false);
        handler = new Handler();
        r = new Runnabletime();
        setPermission();//權限訪問
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        int result = FileUtils.createDir(path + "/demos/file/tmp/test");

    }

    class TestVideoListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == start) {
                if (isgo){
                    Toast.makeText(mContext,"影片上傳中",Toast.LENGTH_SHORT).show();
                    return;
                }
                Dialog_mes dialog_mes = new Dialog_mes(mContext);
                dialog_mes.show();

            }
            if (v == stop) {
                handler.removeCallbacks(r);
                count = 0;
                start.setEnabled(true);
                stop.setEnabled(false);
                if (mediarecorder != null) {
// 停止錄製
                    mediarecorder.stop();
// 釋放資源
                    mediarecorder.release();
                    mediarecorder = null;
                }

                handler.post(r);
                updata();
            }
        }

    }
    public void video(){
        count = 0;
        handler.post(r);
        start.setEnabled(false);
        stop.setEnabled(true);
        mediarecorder = new MediaRecorder();// 創建mediarecorder物件
// 設置錄製視頻源為Camera(相機)
        mediarecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
// 設置錄製完成後視頻的封裝格式THREE_GPP為3gp.MPEG_4為mp4
        mediarecorder
                .setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
// 設置錄製的視頻編碼h263 h264
        mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediarecorder.setVideoEncodingBitRate(1024*1024);
// 設置視頻錄製的解析度。必須放在設置編碼和格式的後面，否則報錯
        mediarecorder.setVideoSize(720,480);
// 設置錄製的視頻幀率。必須放在設置編碼和格式的後面，否則報錯
        mediarecorder.setVideoFrameRate(20);
        mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
// 設置視頻檔輸出的路徑
        mediarecorder.setOutputFile(FileUtils.path+"/"+FileUtils.name+".mp4");
        Toast.makeText(mContext,"開始錄影",Toast.LENGTH_SHORT).show();
        try {
// 準備錄製
            mediarecorder.prepare();
// 開始錄製
            mediarecorder.start();
        } catch (IllegalStateException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void updata(){
        new UploadVideoAsyncTask(mContext, mHandler).execute(FileUtils.path);
        isgo = true;
        Toast.makeText(mContext,"影片上傳中",Toast.LENGTH_SHORT).show();
    }
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String data = msg.getData().getString("data");
            Log.d("data",count+"");
            handler.removeCallbacks(r);
            isgo = false;
            Toast.makeText(mContext,msg.getData().get("data").toString(),Toast.LENGTH_SHORT).show();
        }};

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
// 將holder，這個holder為開始在oncreat裡面取得的holder，將它賦給surfaceHolder
        surfaceHolder = holder;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
// 將holder，這個holder為開始在oncreat裡面取得的holder，將它賦給surfaceHolder
        surfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
// surfaceDestroyed的時候同時物件設置為null
        surfaceview = null;
        surfaceHolder = null;
        mediarecorder = null;
    }

    class  Runnabletime implements Runnable {
        @Override
        public void run() {
            count++;
            Log.d("data",count+"");
            handler.postDelayed(r,1000);
        }
    }
    public void setPermission() {
        int permission1 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);
        int permission2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int permission3 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        String[] permissions = new String[]{ Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (permission1 != PackageManager.PERMISSION_GRANTED||permission2 != PackageManager.PERMISSION_GRANTED||permission3 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( FilmActivity.this,
                    permissions,1);
        }else{
                //已有權限，可進行檔案存取
            }
        }
}

