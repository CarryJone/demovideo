package com.example.mds_user.demovideo.film;

import android.util.Log;

import com.example.mds_user.demovideo.filelist.Nowdata;

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
import okio.BufferedSink;
import okio.Buffer;
import okio.Okio;
import okio.Source;

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
                .addFormDataPart("filename",  Nowdata.name)
//                        .addFormDataPart("file", "12rfffsdgseggsegsegsge")
                .addFormDataPart("file", Nowdata.name,
                        RequestBody.create(MEDIA_TYPE_VIDEO,vdo_mFile))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
}
        public static String post2(String url, String videoPath) throws IOException {
//                RequestBody body = RequestBody.create(JSON, json);

                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(5, TimeUnit.MINUTES)
                        .writeTimeout(5, TimeUnit.MINUTES)
                        .readTimeout(5, TimeUnit.MINUTES);

                OkHttpClient client = builder.build();

                File vdo_mFile = new File(videoPath);

                MultipartBody.Builder builder1 = new MultipartBody.Builder();
                builder1.setType(MultipartBody.FORM);
                 builder1.addFormDataPart("file", Nowdata.name,
                        createCustomRequestBody(MEDIA_TYPE_VIDEO, vdo_mFile, vdo_mFile, new ProgressListener() {
                                @Override
                                public void onProgress(long totalBytes, long remainingBytes, boolean done) {
                                        System.out.print((totalBytes - remainingBytes) * 100 / totalBytes + "%");
                                        Log.d("進度:",(totalBytes - remainingBytes) * 100 / totalBytes + "%");
                                }
                        }));
                RequestBody body = builder1.build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();
        }
        public static RequestBody createCustomRequestBody(final MediaType contentType, File vdo_mFile, final File file, final ProgressListener listener) {
                return new RequestBody() {
                        @Override public MediaType contentType() {
                                return contentType;
                        }

                        @Override public long contentLength() {
                                return file.length();
                        }

                        @Override public void writeTo(BufferedSink sink) throws IOException {
                                Source source;
                                try {
                                        source = Okio.source(file);
                                        //sink.writeAll(source);
                                        Buffer buf = new Buffer();
                                        Long remaining = contentLength();
                                        for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                                                sink.write(buf, readCount);
                                                listener.onProgress(contentLength(), remaining -= readCount, remaining == 0);

                                        }
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }
                };
        }

        interface ProgressListener {
                void onProgress(long totalBytes, long remainingBytes, boolean done);
        }
}

