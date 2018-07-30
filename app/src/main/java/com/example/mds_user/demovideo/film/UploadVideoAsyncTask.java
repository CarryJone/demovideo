package com.example.mds_user.demovideo.film;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hermes.Hsieh on 2015/8/31.
 */
public class UploadVideoAsyncTask extends AsyncTask<String, Void, String> {

    private final static String TAG = UploadVideoAsyncTask.class.getSimpleName();

    private Context mContext;

    private Handler mHandler;

    public UploadVideoAsyncTask(Context context, Handler handler) {
        super();
        mContext = context;
        mHandler = handler;
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "doInBackground");
        String result = null;

        /** AVI Base64編碼, 傳入檔案路徑 **/
//        String mp4 =  getBase64EncoderString(FileUtils.path+"/"+FileUtils.name+".mp4");
//
//        List<NameValuePair> litParams = new ArrayList<NameValuePair>();
//        litParams.add(new BasicNameValuePair("userid", "takz159"));
//        litParams.add(new BasicNameValuePair("meetingid", "3345678"));
//        litParams.add(new BasicNameValuePair("filename", FileUtils.getName()+".mp4"));
//        litParams.add(new BasicNameValuePair("file", mp4));
//
//
//        result = HttpUtil.doPost("http://cloud.mds.com.tw/Demo/SysFun/WebService/Demo_UploadVideo.ashx", litParams);
        try {
//            result = HttpUtil.post("http://cloud.mds.com.tw/Demo/SysFun/WebService/Demo_UploadVideo.ashx", params[0]);
            result = HttpUtil.post2("http://wcsap3.mds.com.tw:7001/mliweb/uploadTest.do", params[0]);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d(TAG, "onPostExecute");
        if (mHandler != null) {
            sendMessage(result);
        } else {
            Log.d(TAG, "mHandler == null");
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute");
    }

    private void sendMessage(String result) {
        Message msg = new Message();
        Bundle data = new Bundle();
        if (result==null){
            result = "上傳失敗";
        }
        data.putString("data", result);
        msg.setData(data);
//        msg.what = ResultCode.UPLOAD_VIDEO_RESULT;
        mHandler.sendMessage(msg);
    }

    private String getBase64EncoderString(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            ByteArrayOutputStream butterStream = new ByteArrayOutputStream();

            int bytesAvailable;
            int bufferSize;
            int maxBufferSize = 1*1024*1024;
            int bytesRead;

            bytesAvailable = fis.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            bytesRead = fis.read(buffer, 0, bufferSize);

            int totalSize = bytesRead;
            // 檔案寫出
            while (bytesRead > 0) {
                butterStream.write(buffer, 0, bufferSize);
                bytesAvailable = fis.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fis.read(buffer, 0, bufferSize);
                totalSize += bytesRead;
                // System.out.println(totalSize + "/" +
                // fis.available());
            }

            fis.close();
            butterStream.flush();
            butterStream.close();

            // 把檔案位置改成檔案的字串流放回去
            path = "";
            path = Base64.encodeToString(butterStream.toByteArray(), Base64.DEFAULT);

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return path;
    }
}
