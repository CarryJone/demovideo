package com.example.mds_user.demovideo.film;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mds_user.demovideo.R;
import com.example.mds_user.demovideo.VoideUtils;
import com.example.mds_user.demovideo.Voide_Audio_DataBase;
import com.example.mds_user.demovideo.listpage.CasedetailsActivity;
import com.example.mds_user.demovideo.listpage.ListpageActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * class name：TestBasicVideo<BR>
 * class description：一個簡單的錄製視頻例子<BR>
 * PS：實現基本的錄製保存檔 <BR>
 *
 * @version 1.00 2011/09/21
 * @author CODYY)peijiangping
 */
public class FilmActivity extends Activity implements SurfaceHolder.Callback {
    private ImageView start;// 開始錄製按鈕
    private ImageView stop;// 停止錄製按鈕
    private ImageView change;// 鏡頭轉換按鈕
    private MediaRecorder mediarecorder;// 錄製視頻的類
    private SurfaceView surfaceview;// 顯示視頻的控制項
    // 用來顯示視頻的一個介面，我靠不用還不行，也就是說用mediarecorder錄製視頻還得給個介面看
// 想偷偷錄視頻的同學可以考慮別的辦法。。嗯需要實現這個介面的Callback介面
    private SurfaceHolder surfaceHolder;
    private Context mContext;
    private Handler handler;
    private  Runnable r;
    private int count = 0;
    private Camera camera;
    private Chronometer chronometer;
    private int num = -1;
    private Voide_Audio_DataBase dataBase;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉標題列
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 設置全屏
        mContext = this;
// 設置橫屏顯示
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
// 選擇支援半透明模式,在有surfaceview的activity中使用。
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_film);
        num = getIntent().getIntExtra("num",-1);
        init();
    }

    private void init() {
        start = (ImageView) this.findViewById(R.id.start);
        stop = (ImageView) this.findViewById(R.id.stop);
        change = (ImageView) this.findViewById(R.id.change);
        chronometer = (Chronometer) findViewById(R.id.chronometer2);
        start.setOnClickListener(new TestVideoListener());
        stop.setOnClickListener(new TestVideoListener());
        change.setOnClickListener(new TestVideoListener());
        surfaceview = (SurfaceView) this.findViewById(R.id.surfaceview);
        SurfaceHolder holder = surfaceview.getHolder();// 取得holder
        holder.addCallback(this); // holder加入回檔介面

// setType必須設置，要不出錯.

        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        handler = new Handler();
        r = new Runnabletime();
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
////        String path = getFilesDir().getPath();
//        int result = MyFileUtils.createDir(path + "/demos/file/tmp/before");

    }
    private void addView()
    {
        LayoutInflater controlInflater = LayoutInflater.from(getBaseContext());
        View viewControl = controlInflater.inflate(R.layout.overlay, null);
        LinearLayout.LayoutParams layoutParamsControl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        this.addContentView(viewControl, layoutParamsControl);

    }
    class TestVideoListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == start) {
//                Dialog_mes dialog_mes = new Dialog_mes(mContext,false);
//                dialog_mes.show();

                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                String str = formatter.format(curDate);
                MyFileUtils.name = "video_"+str;
                dataBase = VoideUtils.VADataLIST.get(num);
                dataBase.setFilename(MyFileUtils.name);
                video();
            }
            if (v == stop) {
                handler.removeCallbacks(r);
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
                count = 0;
                start.setVisibility(View.VISIBLE);
                stop.setVisibility(View.GONE);
                change.setVisibility(View.VISIBLE);
                if (mediarecorder != null) {
// 停止錄製
                    mediarecorder.stop();
// 釋放資源
                    mediarecorder.release();
                    mediarecorder = null;
                }
                releaseCamera();
//                File file = new File(MyFileUtils.before_path + "/" + MyFileUtils.name + ".mp4");
//                Dialog_mes dialog_mes = new Dialog_mes(mContext,false,file);
//                dialog_mes.show();
//                VoideUtils.VideoPlay(mContext,file);
//                dataBase.setOriginal_file(file);
                File file = new File(MyFileUtils.before_path + "/" + dataBase.getFilename()+ ".mp4");
                dataBase.setOriginal_file(file);
                dataBase.setFile(null);
                VoideUtils.UpDataFromDB(dataBase);
                Intent intent = new Intent(mContext, CasedetailsActivity.class);
                intent.putExtra("num",num);
                setResult(101,intent);
                finish();
            }
            if (v == change){
                switchFrontCamera();
            }
        }
    }
    public void video(){
        count = 0;
        handler.post(r);
        start.setVisibility(View.GONE);
        stop.setVisibility(View.VISIBLE);
        change.setVisibility(View.GONE);
        mediarecorder = new MediaRecorder();// 創建mediarecorder物件
        releaseCamera();
        reStartCamera(cameraPosition==0?1:0);

        camera.unlock();
        mediarecorder.setCamera(camera);
// 設置錄製視頻源為Camera(相機)
//        mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediarecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediarecorder.setOrientationHint(90);
        if (cameraPosition == 0){
            mediarecorder.setOrientationHint(270);
        }
// 設置錄製完成後視頻的封裝格式THREE_GPP為3gp.MPEG_4為mp4
        mediarecorder
                .setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
// 設置錄製的視頻編碼h263 h264
        mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediarecorder.setVideoEncodingBitRate(512*1024);
// 設置視頻錄製的解析度。必須放在設置編碼和格式的後面，否則報錯
        mediarecorder.setVideoSize(720,480);
//        mediarecorder.setVideoSize(720,480);
// 設置錄製的視頻幀率。必須放在設置編碼和格式的後面，否則報錯
        mediarecorder.setVideoFrameRate(20);
        mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
// 設置視頻檔輸出的路徑
        mediarecorder.setOutputFile(MyFileUtils.before_path+"/"+ MyFileUtils.name+".mp4");
        Toast.makeText(mContext,"開始錄影",Toast.LENGTH_SHORT).show();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
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

    //影片上傳
//    public void updata(){
//        new UploadVideoAsyncTask(mContext, mHandler).execute(MyFileUtils.path);
//
//        Toast.makeText(mContext,"影片上傳中",Toast.LENGTH_SHORT).show();
//    }
//    public Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            String data = msg.getData().getString("data");
//            Log.d("data",count+"");
//            handler.removeCallbacks(r);
//            Toast.makeText(mContext,msg.getData().get("data").toString(),Toast.LENGTH_SHORT).show();
//        }};

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
        switchFrontCamera();
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

    private void releaseCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    //切换摄像头
    private int cameraPosition = 0; //当前选用的摄像头，1后置 0前置

    public void switchFrontCamera() {
        int cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if (cameraPosition == 1) {
                //现在是后置，变更为前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    //重新打开
                    reStartCamera(i);
                    cameraPosition = 0;
                    break;
                }
            } else {
                //现在是前置， 变更为后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    reStartCamera(i);
                    cameraPosition = 1;
                    break;
                }
            }
        }
    }

    //重新打开预览
    public void reStartCamera(int i) {
        releaseCamera();
        try {
            camera = Camera.open(i);//打开当前选中的摄像头
            camera.setPreviewDisplay(surfaceHolder);//通过surfaceview显示取景画面
            camera.setDisplayOrientation(90);
//            camera.setDisplayOrientation(90);// 屏幕方向
            camera.startPreview();//开始预览
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
        mediarecorder = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(mContext, CasedetailsActivity.class);
        intent.putExtra("num",num);
        setResult(103,intent);
        finish();
    }
}

