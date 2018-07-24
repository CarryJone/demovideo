package com.example.mds_user.demovideo.gcm;

import android.content.Context;

import com.google.android.gcm.GCMRegistrar;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import com.example.mds_user.demovideo.gcm.TraceUtility.TraceType;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Map;

public class GCMUtility {
	
	private static String TAG = GCMUtility.class.getSimpleName();
	
	//註冊 GCM 服務   
    public static void register(final Context context, final String senderID){
    	new Thread() {
    	  public void run() {   
		    try {
		    	GCMRegistrar.checkDevice(context);
				GCMRegistrar.checkManifest(context);
//		    	if(!GCMRegistrar.isRegistered(context)){
		    		GCMRegistrar.register(context, senderID);
//					GCMIntentService gcm =  new GCMIntentService();
//	    		}					
			} catch (Exception e) {
				TraceUtility.trace(TraceType.error, TAG, "register:" + e.toString());				
			} 
    	}}.start();    
    }  
    
    //取消 GCM 服務   
    public static void unregister(final Context context){
//    	new Thread() {
//    	  public void run() {   
//		    try {				    	
//		    	if(GCMRegistrar.isRegistered(context)){
//		    		GCMRegistrar.unregister(context);	    		
//	    		}				
//			} catch (Exception e) {				
//				TraceUtility.trace(TraceType.error, TAG, "unregister:" + e.toString());			
//			}
//    	}}.start();   
    }    

	public static String sendTokenToServer(Map<String, String> params, int iConnect_timeout, int iSocket_timeout, Context context) throws Exception {
		String result = "";
		int res=0;
		HttpResponse response = null;
		StringBuilder sb_datas = new StringBuilder("");
		
		StringBuilder url = new StringBuilder(params.get("APURL"));
		url.append("?appname=").append("DemoVideo");
		url.append("&userid=").append(params.get("userid"));
		url.append("&userdeviceid=").append(params.get("DeviceId"));
		url.append("&pushid=").append(params.get("pushid"));
		
		TraceUtility.trace(TraceType.verbose,TAG, "sendTokenToServer.pushid:" + params.get("pushid"));
		
		url.append("&phonetype=").append(params.get("PHONETYPE"));
		
		TraceUtility.trace(TraceType.verbose, TAG, "url: " + url.toString());

		url.append("&devicemodel=").append(URLEncoder.encode(SystemUtility.getDeviceModel()));
		url.append("&devicename=").append(URLEncoder.encode(SystemUtility.getDeviceName(context)));
		url.append("&devicecompany=").append(URLEncoder.encode(SystemUtility.getDeviceManufacturer()));
		url.append("&deviceos=").append(URLEncoder.encode(SystemUtility.getAndroidOSDesc()));
		
//		TraceUtility.trace(TraceType.verbose,TAG,"devicemodel:" + URLEncoder.encode(ScanUtility.encrypt(SystemUtility.getDeviceModel())));
//		TraceUtility.trace(TraceType.verbose,TAG,"devicename:" + URLEncoder.encode(ScanUtility.encrypt(SystemUtility.getDeviceName(context))));
//		TraceUtility.trace(TraceType.verbose,TAG,"devicecompany:" + URLEncoder.encode(ScanUtility.encrypt(SystemUtility.getDeviceManufacturer())));
//		TraceUtility.trace(TraceType.verbose,TAG,"deviceos:" + URLEncoder.encode(ScanUtility.encrypt(SystemUtility.getAndroidOSDesc())));
		
		
		try {
			HttpPost post = new HttpPost(URI.create(url.toString()));
			DefaultHttpClient client = HttpClientProcess.createHttpClient();
			client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
					iSocket_timeout);
			client.getParams().setIntParameter(
					HttpConnectionParams.CONNECTION_TIMEOUT, iConnect_timeout);
			response = client.execute(post);
			res = response.getStatusLine().getStatusCode();
			if (res == HttpStatus.SC_OK) {
				BufferedInputStream bufferedInputStream = new BufferedInputStream(
						response.getEntity().getContent(), 8000);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int length;
				while ((length = bufferedInputStream.read(buffer)) > -1) {
					baos.write(buffer, 0, length);
				}
				sb_datas.append(baos);
				bufferedInputStream.close();
				baos.close();
				
				TraceUtility.trace(TraceType.verbose,TAG, "sendTokenToServer.sb_datas:"+ sb_datas.toString());
				
				if (sb_datas.toString().equals("0")){
				//成功
					res = 1;
				TraceUtility.trace(TraceType.verbose,TAG,"sendTokenToServer.sb_datas.toString():" +  sb_datas.toString());
				}else if(sb_datas.toString().equals("-1")){
				//失敗
					res = 3;
					TraceUtility.trace(TraceType.verbose,TAG,"sendTokenToServer.sb_datas.toString():" + "3");
				}
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res = 900;
		} catch (ConnectTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res = 901;
		} catch (InterruptedIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res = 902;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res = 903;
		}
//		result = Mds_Utility.getTips(res, context);
		TraceUtility.trace(TraceType.verbose,TAG, "sendTokenToServer.result:" + result);
		return result;

	}	

}
