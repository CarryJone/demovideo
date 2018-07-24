package com.example.mds_user.demovideo.film;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {

        private final static String TAG = HttpUtil.class.getSimpleName();
        private static final MediaType MEDIA_TYPE_VIDEO = MediaType.parse("video/*");
        public static String doGet(String url) {
                Log.i(TAG, "doGet");
                Log.v(TAG, "url : " + url);
                String result = null;
                /* 透過HTTP連線取得回應 */
                try {
                        /* for port 80 requests */
                        HttpClient httpclient = new DefaultHttpClient();
                        /* for Http Get */
                        HttpGet httpget = new HttpGet(url);
                        /* 取得HTTP response */
                        HttpResponse response = httpclient.execute(httpget);
                        /* 取出回應字串 */
                        result = EntityUtils.toString(response.getEntity(), "UTF-8");
                } catch (Exception e) {
                        e.printStackTrace();
                }
                Log.d(TAG, "doGet Result : " + result);
                return result;
        }

        public static String doPost(String url, List<NameValuePair> params) {
                Log.i(TAG, "doPost");
                Log.v(TAG, "url : " + url);
                String result = null;

                /* 建立HTTP Post連線 */
                HttpPost httpRequest = new HttpPost(url);
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 6000);
                HttpConnectionParams.setSoTimeout(httpParameters, 10000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);

                /* Post運作傳送變數必須用NameValuePair[]陣列儲存 */
                // List<NameValuePair> params = new ArrayList<NameValuePair>();

                try {
                        /* 發出HTTP request */
                        httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                        /* 取得HTTP response */
                        HttpResponse httpResponse = httpclient.execute(httpRequest);
                        /* 若狀態碼為200 ok */
                        if (httpResponse.getStatusLine().getStatusCode() == 200) {
                                /* 取出回應字串 */
                                result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "doPost Exception : " + e.getMessage());
                }
                Log.d(TAG, "doPost Result : " + result);
                return result;
        }
        public static String post(String url, String videoPath) throws IOException {
//                RequestBody body = RequestBody.create(JSON, json);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES);

        OkHttpClient client = builder.build();

        File vdo_mFile = new File(videoPath);
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userid", "takz159")
                .addFormDataPart("meetingid", "28942983")
                .addFormDataPart("filename", FileUtils.name+".mp4")
//                        .addFormDataPart("file", "12rfffsdgseggsegsegsge")
                .addFormDataPart("file",FileUtils.name+".mp4",
                        RequestBody.create(MEDIA_TYPE_VIDEO,vdo_mFile))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
}
}
