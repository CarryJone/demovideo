package com.example.mds_user.demovideo.gcm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.baidu.android.pushservice.PushMessageReceiver;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.mds_user.demovideo.GCMIntentService;
import com.example.mds_user.demovideo.gcm.TraceUtility.TraceType;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

	/*
	 * Push消息處理receiver。請編寫您需要的回調函數， 一般來說： onBind是必須的，用來處理startWork返回值；
	 *onMessage用來接收透傳消息； onSetTags、onDelTags、onListTags是tag相關操作的回調；
	 *onNotificationClicked在通知被點擊時回調； onUnbind是stopWork介面的返回值回調
	 * 返回值中的errorCode，解釋如下：
	 *0 - Success
	 *10001 - Network Problem
	 *10101  Integrate Check Error
	 *30600 - Internal Server Error
	 *30601 - Method Not Allowed
	 *30602 - Request Params Not Valid
	 *30603 - Authentication Failed
	 *30604 - Quota Use Up Payment Required
	 *30605 -Data Required Not Found
	 *30606 - Request Time Expires Timeout
	 *30607 - Channel Token Timeout
	 *30608 - Bind Relation Not Found
	 *30609 - Bind Number Too Many
	 * 當您遇到以上返回錯誤時，如果解釋不了您的問題，請用同一請求的返回值requestId和errorCode聯繫我們追查問題。
	 *
	 */


public class BaiduPushMessageReceiver extends PushMessageReceiver {
    /** TAG to Log */
    public static final String TAG = BaiduPushMessageReceiver.class
            .getSimpleName();

    public static final String pushYype = "baidu";
    private Context context;
    /**
     * 調用PushManager.startWork後，sdk將對push
     * server發起綁定請求，這個過程是非同步的。綁定請求的結果通過onBind返回。 如果您需要用單播推送，需要把這裏獲取的channel
     * id和user id上傳到應用server中，再調用server介面用channel id和user id給單個手機或者用戶推送。
     *
     * @param context
     *            BroadcastReceiver的執行Context
     * @param errorCode
     *            綁定介面返回值，0 - 成功
     * @param appid
     *            應用id。errorCode非0時為null
     * @param userId
     *            應用user id。errorCode非0時為null
     * @param channelId
     *            應用channel id。errorCode非0時為null
     * @param requestId
     *            向服務端發起的請求id。在追查問題時有用；
     * @return none
     */
    @Override
    public void onBind(final Context context, int errorCode, String appid,
                       String userId, String channelId, String requestId) {
        TraceUtility.trace(TraceType.verbose, TAG ,"onBind ... ");
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
        TraceUtility.trace(TraceType.info,TAG, "onBind."+responseString);
        this.context = context;
        if (errorCode == 0) {
            // 綁定成功
           TraceUtility.trace(TraceType.info,TAG, "百度推送綁定成功");
        }

        // 記錄 push id
//        AppMain appMain = (AppMain) context.getApplicationContext();

        try {
            JSONObject baiduJson=new JSONObject();
            baiduJson.put("appid", appid);
            baiduJson.put("userId", userId);
            baiduJson.put("channelId", channelId);
            baiduJson.put("requestId", requestId);
            baiduJson.put("errorCode", errorCode);
//            appMain.systemset.setPushBaiduJson(baiduJson.toString(), true);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            TraceUtility.trace(TraceType.info,TAG, "onBind."+"百度推播OnBind資訊(Json)儲存失敗！！");
        }
        // 百度推播設定處理完畢，解除設定
//        appMain.setUtilityParam(ParamKey.push_status_baidu, "N", false);

        String strURL = "cloud.mds.com.tw/wistronmobile" ;
        strURL +="/Sysfun/PushService/setDevicePushID.aspx";
        TraceUtility.trace(TraceType.verbose, TAG ,"onBind-strURL:" +  strURL);

//        final String registration = appMain.systemset.getPushID();
        final List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("userid","takz159"));//使用者帳號
        params.add(new BasicNameValuePair("userdeviceid",SystemUtility.getDeviceID(context)));//裝置id
        params.add(new BasicNameValuePair("pushid",SystemUtility.regID));//GCM 推播 Token(token)
        params.add(new BasicNameValuePair("phonetype","android"));

        params.add(new BasicNameValuePair("devicemodel",SystemUtility.getDeviceModel()));
        params.add(new BasicNameValuePair("devicecompany",SystemUtility.getDeviceManufacturer()));
        params.add(new BasicNameValuePair("deviceos",SystemUtility.getAndroidOSDesc()));

        JSONArray paramArray=new JSONArray();
        JSONObject paramJson=new JSONObject();
        try {
            paramJson.put("pushtype", pushYype);

            JSONArray jsonArray = new JSONArray();

            JSONObject jsonObj=null;
            jsonObj = new JSONObject();
            jsonObj.put("param_id", "channel_id");
            jsonObj.put("param_value", channelId);
            jsonArray.put(jsonObj);

            jsonObj = new JSONObject();
            jsonObj.put("param_id", "user_id");
            jsonObj.put("param_value", userId);
            jsonArray.put(jsonObj);

            paramJson.put("param", jsonArray);

            paramArray.put(paramJson);

            TraceUtility.trace(TraceType.verbose, TAG, "onBind paramJson:" + paramArray.toString());
            params.add(new BasicNameValuePair("pushparam",paramArray.toString()));

            //向後台作推播綁定
//            TraceUtility.trace(TraceType.verbose, TAG, "onBind GCM registrationId:" + registration);

            final String strServiceUrl = strURL;
            new Thread() {
                public void run() {
                    try {

                        TraceUtility.trace(TraceType.verbose, TAG, "傳送後台:" + strServiceUrl);
                        Map<String, String> map = callMcpSimpleService(strServiceUrl, params);
                        String strData = map.get("DATA");
                        String strSysErrmsg = map.get("ERRMSG"); // 系統錯誤訊息
                        TraceUtility.trace(TraceType.verbose,TAG,"onBind.strData: " + strData);
                        TraceUtility.trace(TraceType.verbose,TAG,"onBind.strSysErrmsg: " + strSysErrmsg);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        TraceUtility.trace(TraceType.verbose, TAG,"onBind.exception:" + e.toString());
                    }
                }
            }.start();


        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    /**
     * 接收透傳消息的函數。
     *
     * @param context
     *            上下文
     * @param message
     *            推送的消息
     * @param customContentString
     *            自定義內容,為空或者json字串
     */
    @Override
    public void onMessage(Context context, String message,
                          String customContentString) {
        String messageString = "透傳消息 message=\"" + message
                + "\" customContentString=" + customContentString;
       TraceUtility.trace(TraceType.info,TAG,"onMessage.messageString:"+ messageString);


       if (TextUtils.isEmpty(message)) {
           TraceUtility.trace(TraceType.verbose, TAG, "BaiduPushMessageReceiver.onNotificationArrived.message is Empty");
       }else{
           try {
                   JSONObject jsonMessage = new JSONObject(message);
                JSONObject jsonObject = jsonMessage.getJSONObject("custom_content");

                String mykey1=jsonObject.getString("mykey1");
                String mykey2=jsonObject.getString("mykey2");
                String type=jsonObject.getString("type");
                String id=jsonObject.getString("id");
                String uuid=jsonObject.getString("uuid");

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                //訊息
                bundle.putString("mykey1", mykey1+".");
                //筆數
                bundle.putString("mykey2", mykey2);
                //推播訊息種類(form/announcement/general)
                bundle.putString("type", type);
                //訊息ID
                bundle.putString("id", id);
                //訊息UUID
                bundle.putString("uuid", uuid);

                //Debug
                //bundle.putString("pushType", "BaiDu");

                intent.putExtras(bundle);

                GCMIntentService gCMIntentService=new GCMIntentService();
                gCMIntentService.handleMessage(context, intent);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
       }
    }

    /**
     * 接收通知點擊的函數。
     *
     * @param context
     *            上下文
     * @param title
     *            推送的通知的標題
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            自定義內容，為空或者json字串
     */
    @Override
    public void onNotificationClicked(Context context, String title,
                                      String description, String customContentString) {
        String notifyString = "通知點擊 title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
       TraceUtility.trace(TraceType.info,TAG, "onNotificationClicked.notifyString:"+notifyString);

    }

    /**
     * 接收通知到達的函數。
     *
     * @param context
     *            上下文
     * @param title
     *            推送的通知的標題
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            自定義內容，為空或者json字串
     */

    @Override
    public void onNotificationArrived(Context context, String title,
                                      String description, String customContentString) {

        String notifyString = "onNotificationArrived  title=\"" + title
                + "\" description=\"" + description + "\" customContent="
                + customContentString;
       TraceUtility.trace(TraceType.info,TAG, "messageString.notifyString:"+notifyString);

    }

    /**
     * setTags() 的回調函數。
     *
     * @param context
     *            上下文
     * @param errorCode
     *            錯誤碼。0表示某些tag已經設置成功；非0表示所有tag的設置均失敗。
     * @param successTags
     *            設置成功的tag
     * @param failTags
     *            設置失敗的tag
     * @param requestId
     *            分配給對雲推送的請求的id
     */
    @Override
    public void onSetTags(Context context, int errorCode,
                          List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onSetTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
       TraceUtility.trace(TraceType.info,TAG, responseString);

        // Demo更新介面展示代碼，應用請在這裏加入自己的處理邏輯
        updateContent(context, responseString);
    }

    /**
     * delTags() 的回調函數。
     *
     * @param context
     *            上下文
     * @param errorCode
     *            錯誤碼。0表示某些tag已經刪除成功；非0表示所有tag均刪除失敗。
     * @param successTags
     *            成功刪除的tag
     * @param failTags
     *            刪除失敗的tag
     * @param requestId
     *            分配給對雲推送的請求的id
     */
    @Override
    public void onDelTags(Context context, int errorCode,
                          List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onDelTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
       TraceUtility.trace(TraceType.info,TAG, responseString);

        // Demo更新介面展示代碼，應用請在這裏加入自己的處理邏輯
        updateContent(context, responseString);
    }

    /**
     * listTags() 的回調函數。
     *
     * @param context
     *            上下文
     * @param errorCode
     *            錯誤碼。0表示列舉tag成功；非0表示失敗。
     * @param tags
     *            當前應用設置的所有tag。
     * @param requestId
     *            分配給對雲推送的請求的id
     */
    @Override
    public void onListTags(Context context, int errorCode, List<String> tags,
                           String requestId) {
        String responseString = "onListTags errorCode=" + errorCode + " tags="
                + tags;
       TraceUtility.trace(TraceType.info,TAG, responseString);

        // Demo更新介面展示代碼，應用請在這裏加入自己的處理邏輯
        updateContent(context, responseString);
    }

    /**
     * PushManager.stopWork() 的回調函數。
     *
     * @param context
     *            上下文
     * @param errorCode
     *            錯誤碼。0表示從雲推送解綁定成功；非0表示失敗。
     * @param requestId
     *            分配給對雲推送的請求的id
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
       TraceUtility.trace(TraceType.info,TAG, responseString);

        if (errorCode == 0) {
            // 解綁定成功
           TraceUtility.trace(TraceType.info,TAG, "解綁成功");
        }
        // Demo更新介面展示代碼，應用請在這裏加入自己的處理邏輯
        updateContent(context, responseString);
    }

    private void updateContent(Context context, String content) {
       TraceUtility.trace(TraceType.info,TAG, "updateContent");
//	        String logText = "" + Utils.logStringCache;
//
//	        if (!logText.equals("")) {
//	            logText += "\n";
//	        }
//
//	        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH-mm-ss");
//	        logText += sDateFormat.format(new Date()) + ": ";
//	        logText += content;
//
//	        Utils.logStringCache = logText;
//
//	        Intent intent = new Intent();
//	        intent.setClass(context.getApplicationContext(), PushDemoActivity.class);
//	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	        context.getApplicationContext().startActivity(intent);
    }

    /*
    public static void initCustomPushNotificationBuilder(Context context){
        Resources resource = context.getResources();
        String pkgName = context.getPackageName();
        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
                resource.getIdentifier("notification_custom_builder", "layout", pkgName),
                resource.getIdentifier("notification_icon", "id", null),
                resource.getIdentifier("notification_title", "id", null),
                resource.getIdentifier("notification_text", "id", null));
        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        cBuilder.setNotificationDefaults(Notification.DEFAULT_VIBRATE);
        cBuilder.setStatusbarIcon(context.getApplicationInfo().icon);
        cBuilder.setLayoutDrawable(resource.getIdentifier(
                "simple_notification_icon", "drawable", pkgName));
        cBuilder.setNotificationSound(Uri.withAppendedPath(
                Audio.Media.INTERNAL_CONTENT_URI, "6").toString());
        PushManager.setNotificationBuilder(context, 1, cBuilder);
    }
    */
    public Map<String, String> callMcpSimpleService(String strUrl, List<BasicNameValuePair> litParam) throws Exception {
        int intRes = 0; // 處理狀態
        String strErrMsg = ""; // 錯誤訊息
        Map<String, String> mapReturn = new HashMap<String, String>(); // 回傳資料
        StringBuilder stringBuilder = new StringBuilder("");

        if (strUrl.equalsIgnoreCase("")) {
            strErrMsg = "無網址資料";
        } else {

            HttpResponse httpResponse = null;
            DefaultHttpClient defaultHttpClient = null;
            HttpPost httpPost = null;
            HttpEntity httpEntity = null;
            List<BasicNameValuePair> litParamValues;
            InputStream inputStream;
            BufferedInputStream bisBufferedInputStream;
            ByteArrayOutputStream baoByteArrayOutputStream;

            // 組合傳遞參數
            if (litParam == null) {
                // 無參數需處理
                litParamValues = new ArrayList<BasicNameValuePair>();
            } else {
                // 有參數需傳遞
                litParamValues = litParam;
                TraceUtility.trace(TraceType.verbose,TAG,"callMcpSimpleService.param:" + litParamValues.toString());
            }

            try {
//				defaultHttpClient = new DefaultHttpClient();
                defaultHttpClient = HttpClientProcess.createHttpClient(context);
                httpPost = new HttpPost(strUrl);
                httpEntity = new UrlEncodedFormEntity(litParamValues, HTTP.UTF_8);

                httpPost.setEntity(httpEntity);

                defaultHttpClient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, HttpClientProcess.Socket_timeout);// strConnectTimeout
                defaultHttpClient.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT,HttpClientProcess.Connect_timeout);// strSocketTimeout
                httpResponse = defaultHttpClient.execute(httpPost);

                intRes = httpResponse.getStatusLine().getStatusCode();
                if (intRes == HttpStatus.SC_OK) {
                    inputStream = httpResponse.getEntity().getContent();
                    httpResponse = null;
                    bisBufferedInputStream = new BufferedInputStream(inputStream, 8000);
                    inputStream = null;
                    baoByteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] bytBuffer = new byte[1024];
                    int intLength;
                    while ((intLength = bisBufferedInputStream.read(bytBuffer)) > -1) {
                        baoByteArrayOutputStream.write(bytBuffer, 0, intLength);
                    }
                    stringBuilder.append(baoByteArrayOutputStream);

                    TraceUtility.trace(TraceType.verbose,TAG,"callMcpSimpleService.response:" + stringBuilder.toString());

                    bytBuffer = null;
                    bisBufferedInputStream.close();
                    baoByteArrayOutputStream.close();

                    intRes = 1;
                } else {
                    // 未知錯誤
                    intRes = 0;
                }
            } catch (UnsupportedEncodingException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
                // 未知錯誤
                intRes = 0;
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                intRes = 900;
            } catch (ConnectTimeoutException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                intRes = 901;
            } catch (InterruptedIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                intRes = 902;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                intRes = 903;
            }

            // 清除物件
            inputStream = null;
            bisBufferedInputStream = null;
            baoByteArrayOutputStream = null;
            httpEntity = null;
            httpPost = null;
            defaultHttpClient = null;
            httpResponse = null;


        }

        mapReturn.put("ERRMSG", strErrMsg);
        mapReturn.put("DATA", stringBuilder.toString());

        return mapReturn;
    }
}
