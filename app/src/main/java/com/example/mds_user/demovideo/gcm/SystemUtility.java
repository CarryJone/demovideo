package com.example.mds_user.demovideo.gcm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;


import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class SystemUtility {

	private static String TAG = SystemUtility.class.getSimpleName();

	private static ProgressDialog progressDialog;
	public static String regID = null;
	public static void showLoading(Context context) {
		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(context, "", "Loading. Please wait...",true);
			progressDialog.show();
		} else {
			progressDialog.show();
		}
	}

	public static void closeLoading() {
		if (progressDialog != null) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			progressDialog = null;
		}
	}

	/*** start acitvity: 指定 requestCode */
	public static void startActivity(Context context, Class<?> activityClass, Bundle bundle, int requestCode) {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(context, activityClass);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		((Activity) context).startActivityForResult(intent, requestCode);
	}

	public static void startActivity(Context context, Class<?> targetClass, Bundle bundle) {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(context, targetClass);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		((Activity) context).startActivity(intent);
	}

	public static void startActivity(Context context, Class<?> targetClass, Bundle bundle, boolean shouldReturn) {
//		if(shouldReturn){
//			AppInitializer.activityGoTo = targetClass;
//		}
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(context, targetClass);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		((Activity) context).startActivity(intent);
	}

	public static boolean startApp(Context context, String packageName) {
		PackageManager manager = context.getPackageManager();
		try {
			Intent i = manager.getLaunchIntentForPackage(packageName);
			if (i == null) {
				return false;
			}
			i.addCategory(Intent.CATEGORY_LAUNCHER);
			context.startActivity(i);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	public static void closeApp(Context context){
		Activity activity = (Activity) context;
		activity.finish();
		if(getAndroidSDK()>=16){
			activity.finishAffinity();
		}
	}

	public static void setReresult(Context context, Bundle bundle){
		Intent intent =new Intent();
		intent.putExtras(bundle);
		((Activity) context).setResult(Activity.RESULT_OK, intent);
	}

	public static String getDeviceManufacturer() {
		String manufacturer = Build.MANUFACTURER;
		return manufacturer;
	}

	public static String getDeviceModel() {
		String model = Build.MODEL;
		return model;
	}

	/*** 檢查網路是否連線 */
	public static boolean checkNetworkConnected(Context context) {
		NetworkInfo info = (NetworkInfo) ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

		if (info == null) {
			return false;
		} else {
			if (!info.isConnected()) {
				return false;
			}
			if (info.isRoaming()) {
				return false;
			}
		}
		return true;
	}

	/*** 取得手機 device id */
	@SuppressLint("MissingPermission")
	public static String getDeviceID(Context context) {
		String deviceID = "";
		try {
			String imei = ""; // 必須有是手機才有，有些wifi平板會沒有
			String serial = ""; // 硬體的唯一值。API Level 9才支援
			String androidID = ""; // Android_ID 設備第一次啟動時產生的序號。
			String uuid = "";

			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (telephonyManager.getDeviceId() != null) {
				imei = telephonyManager.getDeviceId();
			}

			if (android.os.Build.VERSION.SDK_INT > 8) {
				serial = android.os.Build.SERIAL;
			}

			androidID = Secure.getString(context.getContentResolver(),
					Secure.ANDROID_ID);

			uuid = UUID.randomUUID().toString();

			if (!imei.equals("")) {
				deviceID = imei;
			} else if (!serial.equals("") && !serial.equalsIgnoreCase("unknown")) {
				deviceID = serial;
			} else if (!androidID.equals("") && !androidID.toLowerCase().equalsIgnoreCase("9774d56d682e549c")) {
				deviceID = androidID;		
			} else {
				deviceID = uuid;
			}

			//LogUtility.v(TAG, deviceID);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return deviceID;
	}

	public static String getDeviceName(Context context) {
		String deviceName = "";
		String manufacturer = Build.MANUFACTURER;
		// deviceName = Build.DEVICE;
		String model = Build.MODEL;

		if (model.startsWith(manufacturer)) {
			deviceName = model;
		} else {
			deviceName = manufacturer + " " + model;
		}

		return deviceName;
	}

	/*** 透過手機SIM卡裝置判斷系統是 Phone 或 Pad */
	public String getDeviceType(Context context) {
		String strDeviceType = "pad";
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
				strDeviceType = "phone";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strDeviceType;
	}

	// 取得 App 版本
	public static String getAppVersionName(Context context) {
		try {
			String strVerName = "";
			String strPackageName = "";
			strPackageName = context.getApplicationContext().getPackageName();
			strVerName = context.getPackageManager().getPackageInfo(
					strPackageName, 0).versionName;
			return strVerName;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	// 取得 App package name
	public static String getAppPackageName(Context context) {
		try {
			return context.getPackageName();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/*** 取得 Android 版本名稱, 例如: 4.0.3 */
	public static String getAndroidVersion() {
		String strVersion = "";
		try {
			strVersion = Build.VERSION.RELEASE;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strVersion;
	}
	
	public static String getAndroidOSDesc() {
		String os = "";
		try {
			os = "android " + Build.VERSION.RELEASE;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return os;
	}

	/***
	 * 取得 Android SDK code
	 * 
	 * @return int: 例如，Android 4.0.3 環境，回傳 15，若取不到 android api，回傳 0
	 */
	public static int getAndroidSDK() {
		int sdk = 0;
		try {
			sdk = Build.VERSION.SDK_INT;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sdk;
	}

	/***
	 * 顯示通知訊息 說明: SDK 版本需大於等於 API 11，且行動裝置需安裝 com.google.android.gsf(google play
	 * service)，才可正常使用。
	 */
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	public static void sendNotification(Context context, Class<?> activityClass, int iconID, String title, String text) {
		Notification.Builder builder = new Notification.Builder(context);
		// builder.setTicker(tickerText);
		builder.setContentTitle(title);
		builder.setContentText(text);
		builder.setSmallIcon(iconID);
		builder.setDefaults(Notification.DEFAULT_ALL);
		builder.setAutoCancel(true);// 點到就清除通知訊息

		int requestCode = UUID.randomUUID().hashCode();
		//LogUtility.v(TAG, "sendNotification.requestCode:" + requestCode);
		Intent intent = new Intent(context, activityClass);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pi = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(pi);

		Notification notif = builder.build();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(requestCode, notif);
	}

//	public static void callPhone(final Context context){
//		String message = context.getResources().getString(R.string.product_detail_service_msg);
//		final String tel = context.getResources().getString(R.string.product_detail_service_tel);    			
//		AlertDialog.Builder adb = new AlertDialog.Builder(context);
//		adb.setTitle("");
//		adb.setMessage(message);
//		adb.setNegativeButton("取消", null);
//		adb.setPositiveButton("確定", new DialogInterface.OnClickListener(){
//			public void onClick(DialogInterface dialog, int whichcountry){
//				SystemUtility.callPhone(context, tel);
//			}
//		});
//		adb.show();
//	}
	
	// 撥打電話
//	public static void callPhone(final Context context, final String tel) {
//		TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//		if (telMgr.getSimState() == TelephonyManager.SIM_STATE_READY) {
//			AlertDialog alertlog = new AlertDialog.Builder(context)
//			.setTitle("撥號")
//			.setMessage("確定要撥打:" + tel + "?")
//			.setPositiveButton("是", new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					Intent call = new Intent("android.intent.action.CALL", Uri.parse("tel:" + tel));
//					((Activity) context).startActivity(call);
//				}
//			})
//			.setNegativeButton("否", new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog,	int which) {
//				}
//			 }).show();
//
//			alertlog.setCanceledOnTouchOutside(false);
//		} else {
//			AlertDialog alertlog = new AlertDialog.Builder(context)
//			.setTitle("撥號")
//			.setMessage("此手機無撥號功能")
//			.setPositiveButton("確定", new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog,	int which) {
//
//				}
//			}).show();
//			alertlog.setCanceledOnTouchOutside(false);
//		}
//	}

	/***
	 * 顯示螢幕規格: resolution, density, physical size
	 */
	public static void displayScreenMetrics(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

		// 解析度
		int width = dm.widthPixels;
		int height = dm.heightPixels;

		//LogUtility.v(TAG, "width x height(px):"+ String.valueOf(width) + "x" + String.valueOf(height));

		// 螢幕尺寸
		double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
		double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
		double screenInches = Math.sqrt(x + y);
		//LogUtility.v(TAG, "Screen inches:" + String.valueOf(screenInches));

		// 螢幕密度
		float density = dm.density;
		//LogUtility.v(TAG, "density:" + String.valueOf(density));
	}

	/***
	 * 取得螢幕寬度
	 */
	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		int width = dm.widthPixels;

		// int width = context.getResources().getDisplayMetrics().widthPixels;

		//LogUtility.v(TAG, "screen width(px):" + width);

		return width;
	}
	
	/***
	 * 取得螢幕高度
	 */
	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		int height = dm.heightPixels;

		// int width = context.getResources().getDisplayMetrics().widthPixels;

		//LogUtility.v(TAG, "height(dp):" + height);

		return height;
	}

	/***
	 * 取得螢幕尺寸
	 */
//	public static double getScreenSize(Context context) {
//		DisplayMetrics dm = new DisplayMetrics();
//		((Activity) context).getWindowManager().getDefaultDisplay()
//				.getMetrics(dm);
//		// 螢幕尺寸
//		double width = Math.pow(dm.widthPixels / dm.xdpi, 2);
//		double height = Math.pow(dm.heightPixels / dm.ydpi, 2);
//		double screenSize = NumberUtility.round(Math.sqrt(width + height), 1);
//
//		//LogUtility.v(TAG, "scren width x height(px):" + width + "x" + height);
//		//LogUtility.v(TAG, "screen size:" + String.valueOf(screenSize));
//
//		return screenSize;
//	}

	/***
	 * 取得螢幕解析度: density
	 */
	public static float getScreenDensity(Context context) {
		float density = context.getResources().getDisplayMetrics().density;
//		LogUtility.v(TAG, "density:" + density);
		return density;
	}

	/*** 依 dimen 取得 dp 或 sp */
	public static float getDensitySize(Context context, int dimenRourceID) {
		float pixels = context.getResources().getDimensionPixelSize(dimenRourceID);		
		float size = pixels / getScreenDensity(context);		
		return size;
	}	
	
	public static int getPixelSize(Context context, int size){
		int pixels = (int) (size * getScreenDensity(context));
		return pixels;
	 }
	
	/*
	public static boolean checkActivityIsExit(Context context,String activityName){		
		boolean isAppRunning=false;
		
		Intent intent = new Intent();
		intent.setClassName(getAppPackageName(context), activityName);
		
		if(intent.resolveActivity(context.getPackageManager()) != null) { 
			isAppRunning=true;
		}
		
		return isAppRunning;		
	}
	*/
	
	public static String getLanguageEnv() {
		Locale l = Locale.getDefault();
		String language = l.getLanguage();
		String country = l.getCountry().toLowerCase();
		if ("zh".equals(language)) { 
			if ("cn".equals(country)) { 
				language = "zh-CN"; 
			} else if ("tw".equals(country)) { 
				language = "zh-TW"; 
			} 
		} else if ("pt".equals(language)) { 
			if ("br".equals(country)) { 
				language = "pt-BR"; 
			} else if ("pt".equals(country)) { 
				language = "pt-PT"; 
			} 
		}
		
		return language; 
	}
	
	public static boolean isExsitActivity(Context context, Class<?> cls){
        Intent intent = new Intent(context, cls);
        ComponentName cmpName = intent.resolveActivity(context.getPackageManager());
        boolean flag = false;    
        if (cmpName != null) { // 說明系統中存在這個activity   	 
            ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            List<RunningTaskInfo> taskInfoList = am.getRunningTasks(10);
            for (RunningTaskInfo taskInfo : taskInfoList) {
                if (taskInfo.baseActivity.equals(cmpName)) { // 說明他已經啟動了    
                	flag = true;    
                    break;  //跳出循環，優化效率  
                }    
            }    
        }  
        return flag;    
    } 
	
	public static String getExternalStoragePath(String appExternalDir){
		String path="";
		try{
			File file;
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//有實體 sd card 並掛載，或模擬 sd card
				file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + appExternalDir);
	    		TraceUtility.trace(TraceUtility.TraceType.verbose, TAG, "external storage mounted");	
			}else{//for 異常機型: 無 sdcard，無模擬 sdcard, 或有 sdcard 未掛載。
				String virtualSDPath = "";
				String manufacturer = getDeviceManufacturer();
				if(Build.VERSION.SDK_INT >= 15){//android 4.0  ~ 4.2.2
					if(manufacturer.equals("HTC")){
						virtualSDPath = "/storage/emmc/" + appExternalDir;
					} else{//samsung,sony,asus
						virtualSDPath = "/storage/sdcard0/" + appExternalDir;
					}
				}else{//android 2.3 以下
					virtualSDPath = "/mnt/sdcard0/" + appExternalDir;					
				}			
				
	        	file =new File(virtualSDPath);
	        }	
			
			if(!file.exists()){
    			file.mkdirs();    			
    		}
			
			path = file.getPath();
		}catch(Exception e){
			TraceUtility.trace(TraceUtility.TraceType.error, TAG, e.toString());
		}
		return path;
	}
	
	public static float getDimenSize(Context context, int dimenRourceID){
		 float pixels = context.getResources().getDimension(dimenRourceID);
		 return pixels/getScreenDensity(context);//font
	 }	
	
//	public static void openLoginActivityForTokenFail(Context context, Intent intent, boolean isFinish){
//		((Activity) context).setResult(Mds_Utility.ACT_GOSET_Token_ID, intent);
//		//設定strLoginFlag(是否登入) ,strSaveFlag(是否驗證)
//		Mds_Utility.loginobj.setAllpara(null, null,	null, null, null, null, "N", "N");
//		startActivity(context, LoginSet.class,intent.getExtras());//導向登入頁
//		if(isFinish){
//			((Activity) context).finish();
//		}
//	}
	
}
