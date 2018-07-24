package com.example.mds_user.demovideo;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.mds_user.demovideo.gcm.GCMUtility;
import com.example.mds_user.demovideo.gcm.SystemUtility;
import com.example.mds_user.demovideo.gcm.TraceUtility;
import com.example.mds_user.demovideo.video.VideoActivity;
import com.google.android.gcm.GCMBaseIntentService;

import org.json.JSONException;
import org.json.JSONObject;
import com.example.mds_user.demovideo.gcm.TraceUtility.TraceType;
import java.util.HashMap;
import java.util.Map;


public class GCMIntentService extends GCMBaseIntentService{
	
	private static String TAG = GCMIntentService.class.getSimpleName();
	public static String ACTION = GCMIntentService.class.getSimpleName();
	public static final String MDM_ACTION = "mdm_action";
	public static final String PUSH_SENDER_ID = "772493589586";//GCM
	public GCMIntentService() {		
		super(PUSH_SENDER_ID);
	}	 
	
	@Override
	protected void onRegistered(Context context, String registrationId) {
		TraceUtility.trace(TraceType.info, TAG,"onRegistered.regID:" + registrationId);
		
		handleRegistration(context, registrationId);
	}
	 
	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		TraceUtility.trace(TraceType.info, TAG,"onUnregistered.arg1:" + arg1);		
		
	}
	
	@Override
	protected void onMessage(Context context, Intent intent) {
		handleMessage(context, intent);
	}	
	 
	@Override
	protected void onError(Context arg0, String errorId) {
		TraceUtility.trace(TraceType.info, TAG,"onError.errorId: " + errorId);
	}
	 
	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		return super.onRecoverableError(context, errorId);
	}
	
	/**
	 * 組合收到的訊息變數組合成Toast
	 * @param context
	 * @param intent
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void handleMessage(final Context context, Intent intent) {
//		final AppMain appMain = (AppMain) context.getApplicationContext();
		TraceUtility.trace(TraceType.verbose,TAG, "handle message");
		//訊息
		String test = intent.getExtras().getString("message");
//		String msg = intent.getExtras().getString("mykey1");
//		//筆數
//		String msg2 = intent.getExtras().getString("mykey2");
//		//推播訊息種類(form/announcement/general)
//		String msg3 = intent.getExtras().getString("type");
//		//訊息ID
//		String msg4 = intent.getExtras().getString("id");
//		//訊息UUID
//		String msg5 = intent.getExtras().getString("uuid");
//
//		TraceUtility.trace(TraceType.verbose, TAG, "msg=" + msg);
//		TraceUtility.trace(TraceType.verbose, TAG, "msg2=" + msg2);
//		TraceUtility.trace(TraceType.verbose, TAG, "msg3=" + msg3);
//		TraceUtility.trace(TraceType.verbose, TAG, "msg4=" + msg4);
//		TraceUtility.trace(TraceType.verbose, TAG, "msg5=" + msg5);
//
//		//判斷是否已經接收過相同的推播訊息 by uuid
////		if(msg5!=null && !msg5.equals("")){
////			if(Mds_Utility.dataprocess.checkPushReceiveCounterExist(msg5)){
////				//已接收
////				TraceUtility.trace(TraceType.verbose, TAG, "PushMessage:已接收相同的 uuid :"+msg5);
////				return;
////			}else{
////				//String receiveDt=DateUtility.getDateString(new Date(),DateUtility.DATE_TIME_FORMAT_TO_SECOND);
////				//receiveDt=receiveDt.replace("-", "//");
////				Mds_Utility.dataprocess.insertPushReceiveCounter(msg5,null);
////				Mds_Utility.dataprocess.delPushReceiveCounter();//刪除 推播訊息接收計數器資料(一天之前)
////			}
////		}
////
//		// 只有有訊息時才顯示窗簾
//        if (msg==null) {
//        	TraceUtility.trace(TraceType.verbose, TAG, "msg is null");
//        } else if (msg.trim().equalsIgnoreCase("")) {
//        	TraceUtility.trace(TraceType.verbose, TAG, "msg is Empty");
//        } else {
//        		//mdm 指令不顯示在訊息列
//            	try {
//        			JSONObject jsonObject = new JSONObject(msg);
//        			if(jsonObject.has("mdm")){
//        				TraceUtility.trace(TraceType.verbose, TAG, "msg: mdm");
//        				if(true){
////        					handleMDMMessage(context, msg);
//        				}
//        			}
//        			return;
//        		} catch (JSONException e) {
//        			TraceUtility.trace(TraceType.verbose, TAG, e.toString());
//        		}
        	
	        // 顯示出一個狀態列的訊息告知
	        String ns = Context.NOTIFICATION_SERVICE;
	        NotificationManager notificationManager = (NotificationManager) context.getSystemService(ns);
	        long when = System.currentTimeMillis();

			CharSequence contentTitle = "DemoVideo";
			CharSequence contentText = test;


//			Intent notificationIntent = null ;
//			if (msg3.equalsIgnoreCase("form")) {
//				notificationIntent = new Intent(context, MainActivity.class);
//			}else if(msg3.equalsIgnoreCase("announcement")){
//				notificationIntent = new Intent(context, Announcement_List.class);
//			}else if(msg3.equalsIgnoreCase("general")){
//				notificationIntent = new Intent(context, EIPActivity.class);
//			}else{
//				// bryan 7/5
//				for (GridItem data :Itemlist){
//					if (msg3.equalsIgnoreCase(data.name)){
//						ShareUtility.openApp(context, data.item);
//					}
//				}
//			}

			//Intent.FLAG_ACTIVITY_CLEAR_TOP ：如果在當前Task中，有要啟動的Activity，那麼把該Acitivity之前的所有Activity都關掉，並把此Activity置前以避免創建Activity的實例
			//Intent.FLAG_ACTIVITY_NEW_TASK ：系統會檢查當前所有已創建的Task中是否有該要啟動的Activity的Task，若有，則在該Task上創建Activity，若沒有則新建具有該Activity屬性的Task，並在該新建的Task上創建Activity。
//			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
//			notificationIntent.putExtra("PUSH", true);
			Intent intent1 = new Intent(context,MainActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
//	        Notification notification = new Notification(R.drawable.push, msg, when);
			Notification notification = new Notification.Builder(context)
					.setContentTitle(contentTitle)
					.setContentText(contentText)
					.setSmallIcon(R.drawable.zm_mm_opt_panel_camera_icon)
					.setContentIntent(contentIntent)
//					.setLargeIcon(R.drawable.zm_btn_switch_camera)
					.build();
	        //設定窗簾點選動作
	        notification.flags = Notification.FLAG_AUTO_CANCEL;
	        //設定接收到push時，通知預設的鈴聲、振動、light
	        //通知的時候需發出預設的通知震動，因此需在AndroidManifest.xml允許存取android.permission.VIBRATE這個權限。
	        notification.defaults= Notification.DEFAULT_ALL;

//	        if (msg2==null) {
//	        	TraceUtility.trace(TraceType.verbose, TAG, "msg2 is null");
//	        } else if (msg2.trim().equalsIgnoreCase("")) {
//	        	TraceUtility.trace(TraceType.verbose, TAG, "msg2 is Empty");
//	        } else {
//		        int Receivecounter= Integer.valueOf(msg2);
//		        notification.number= Receivecounter;  //通知顯示幾筆資料
//	        }

	        notificationManager.notify(1, notification);	       
        }
        
//		TraceUtility.trace(TraceType.verbose, TAG, "intAppRunID = " + String.valueOf(Mds_Utility.intAppRunID));
//        if (Mds_Utility.intAppRunID>=0) {
//				// App已開啟
//				if (msg3 == null){
//
//				} else {
//					//Portal App news Count Reflush
//					context.getApplicationContext().sendBroadcast(new Intent(Mds_Utility.ACT_PortalAppNewsCountReflush), PermissionKey.PERMISSION_RECEIVE_MESSAGE);//fortify
//
//					if (msg3.equalsIgnoreCase("form")) {
//						TraceUtility.trace(TraceType.verbose, "Mds.GCMIntentService.handle message", "msg3=form");
//						// 簽核表單相關推播訊息
//						if (Mds_Utility.intAppRunID == Mds_Utility.ID_DATALIST) {
//							context.getApplicationContext().sendBroadcast(new Intent(Mds_Utility.ACT_DataListPushReflush), PermissionKey.PERMISSION_RECEIVE_MESSAGE);//fortify
//						} else {
//							new Thread() {
//								public void run() {
//									try {
//										//Mds_Utility.dataprocess.getDataFromDB(context);
//										//Mds_Utility.dataprocess.getDataFromServ(context);
//										context.getApplicationContext().sendBroadcast(new Intent(Mds_Utility.ACT_DataListPushReflush), PermissionKey.PERMISSION_RECEIVE_MESSAGE);//fortify
//
//									} catch (Exception e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//								}
//							}.start();
//						}
//					} else if (msg3.equalsIgnoreCase("announcement")) {
//						// 重大訊息相關推播訊息
//						if (Mds_Utility.intAppRunID == Mds_Utility.ID_ANNOUNCEMENTLIST) {
//							context.getApplicationContext().sendBroadcast(new Intent(Mds_Utility.ACT_AnnouncementPushReflush), PermissionKey.PERMISSION_RECEIVE_MESSAGE);//fortify
//						} else {
//							new Thread() {
//								public void run() {
//									try {
//										Mds_Utility.dataprocess.insertAnnouncementList(context);
//										context.getApplicationContext().sendBroadcast(new Intent(Mds_Utility.ACT_CountReflush), PermissionKey.PERMISSION_RECEIVE_MESSAGE);//fortify
//									} catch (Exception e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//								}
//							}.start();
//						}
//					} else if (msg3.equalsIgnoreCase("general")) {
//						// 一般推播訊息
//					}
//				}
//        }
        
//	}

//	private void handleMDMMessage(final Context context, String msg){
//		// 解析指令，取回處理
//    	TraceUtility.trace(TraceType.verbose, TAG,"handleMDMMessage:" + msg);
//		// 回傳為JSON格式
//		try {
//			JSONObject JsonRetrunval;
//			JsonRetrunval = new JSONObject(msg);
//			String strMDM = JsonRetrunval.getString("mdm");
////			AppMain appMain = (AppMain) context.getApplicationContext();
//
//			if (strMDM.toLowerCase().equalsIgnoreCase("location")) {
//				// 定位處理
////				if(Mds_Utility.locationHelper == null){
////					TraceUtility.trace(TraceType.info,TAG,"locationHelper is null");
////					Mds_Utility.locationHelper  = new LocationHelper(context);
////					Mds_Utility.locationHelper.setLocation();
////				}
//
//				final Context aContext = context;
////				final AppMain aAppMain = appMain;
//
//				new Thread() {
//					public void run() {
//						try {
//							// TODO Auto-generated method stub
//							// 發送位置
//							boolean blnProcess=true;
//							int blnLoopCount = 0;
////							int intErrCount = Integer.valueOf(aAppMain.getUtilityParam(ParamKey.proc_errcount));//處理錯誤上限
//
//							while(blnProcess){
//								if (Mds_Utility.chkUserData(aContext)){
//									TraceUtility.trace(TraceType.debug,TAG, "LOCATION:" + "回傳定位記錄第 " + (blnLoopCount+1) + " 次");
//
//									Map<String, String> mapLocation = Mds_Utility.callService(context ,ControlEvent.sys_locate);
//
//									if (mapLocation.get("code").equalsIgnoreCase("0")) {
//										blnProcess = false;
//									} else if (intErrCount > -1){
//										// 只有錯誤上限大於-1才有統計，否則只要錯誤就重做，不停止。
//										blnLoopCount++;
//										// 判斷是否超出錯誤上限
//										if (blnLoopCount > intErrCount ){
//											blnProcess = false;
//										}
//
//										Thread.sleep(Mds_Utility.MDM_RETRY_WAIT_TIME);//等待時間再重試
//									}
//									mapLocation = null;
//								} else {
//									// 沒有足夠資料，進行錯誤次數計算
//									if (intErrCount > -1){
//										// 只有錯誤上限大於-1才有統計，否則只要錯誤就重做，不停止。
//										blnLoopCount++;
//										// 判斷是否超出錯誤上限
//										if (blnLoopCount > intErrCount ){
//											blnProcess = false;
//										}
//									}
//
//									Thread.sleep(Mds_Utility.MDM_RETRY_WAIT_TIME);//等待時間再重試
//
//									TraceUtility.trace(TraceType.verbose, TAG, "sys_locate.err:AgentCode or LicenseKey no data!");
//								}
//							}
//						} catch (final Exception ex) {
//		            		ex.printStackTrace();
//							TraceUtility.trace(TraceType.error, TAG,"sys_locate:" + ex.toString());
//						}
//					}
//				}.start();
//			} else {
//				TraceUtility.trace(TraceType.verbose, TAG, "mdm_status:" + appMain.getUtilityParam(ParamKey.mdm_status));
//				if (appMain.getUtilityParam(ParamKey.mdm_status).equalsIgnoreCase("Y")) {
//					// 表已在處理MDM指令中，不需重複啟動
//				} else {
//					// 取回MDM指令設定
//					// 先設定處理旗標
//					final Context fin_Context = context;
//					final AppMain fin_appMain = appMain;
//					new Thread() {
//						public void run() {
//							try {
//								// TODO Auto-generated method stub
//								fin_appMain.setUtilityParam(ParamKey.mdm_status, "Y", true);
//								boolean blnMDMDone = false; //處理結束旗標
//								int intErrLoop = 0;         //處理錯誤次數
//								int intErrCount;        //處理錯誤上限
//								Map<String, String> map;
//
//								intErrCount = Integer.valueOf(fin_appMain.getUtilityParam(ParamKey.proc_errcount));
//
//								TraceUtility.trace(TraceType.verbose, TAG,"mdm_status:" + fin_appMain.getUtilityParam(ParamKey.mdm_status));
//
//								while (!blnMDMDone) {
//									// 處理結束
//									if (Mds_Utility.chkUserData(fin_Context)){
//										map = Mds_Utility.getMDMCommand(fin_Context);
//										if (map.get("ErrorMsg").equalsIgnoreCase("")) {
//
//											TraceUtility.trace(TraceType.verbose, TAG,"map.size:" + String.valueOf(map.size()));
//
//											if (map.size()<3) {
//												// MDM指令已都執行完畢
//												blnMDMDone = true;
//											} else {
//												//重新計數
//
//												TraceUtility.trace(TraceType.verbose, TAG,"RequestType:" + map.get("RequestType"));
//
//												if (map.get("RequestType").equalsIgnoreCase("EraseDevice")) {
//													// 裝置抹除
//													Mds_Utility.doMDMSetting(fin_Context, ControlEvent.sys_wipe_external);
//												} else if (map.get("RequestType").equalsIgnoreCase("DeviceLock")) {
//													// 裝置上鎖
//													Mds_Utility.doMDMSetting(fin_Context, ControlEvent.sys_lock);
//												} else if (map.get("RequestType").equalsIgnoreCase("RemoveProfile")) {
//													// 裝置解除控管
//													Mds_Utility.doMDMSetting(fin_Context, ControlEvent.sys_unenroll);
//												} else if (map.get("RequestType").equalsIgnoreCase("ResetPassword")) {
//													// 裝置密碼重設
//													fin_appMain.setUtilityParam(ParamKey.enroll_passwd,map.get("PassWord").toString(),true);
//													Mds_Utility.doMDMSetting(fin_Context, ControlEvent.sys_resetpwd);
//												}else if(map.get("RequestType").equalsIgnoreCase("CameraLock")) {
//													// lock camera
//													Mds_Utility.doMDMSetting(fin_Context, ControlEvent.sys_camera_lock);
//												}else if(map.get("RequestType").equalsIgnoreCase("CameraUnlock")) {
//													// unlock camera
//													Mds_Utility.doMDMSetting(fin_Context, ControlEvent.sys_camera_unlock);
//												}else if(map.get("RequestType").equalsIgnoreCase("ScreenShotLock")) {
//													// lock screenshot
//													Mds_Utility.doMDMSetting(fin_Context, ControlEvent.sys_screenshot_lock);
//												}else if(map.get("RequestType").equalsIgnoreCase("ScreenShotUnLock")) {
//													// unlock screenshot
//													Mds_Utility.doMDMSetting(fin_Context, ControlEvent.sys_screenshot_unlock);
//												}else if(map.get("RequestType").equalsIgnoreCase("ClearData")) {
//													// 清空  app 資料
//													Mds_Utility.doMDMSetting(fin_Context, ControlEvent.sys_clear_appdata);
//												}else if(map.get("RequestType").equalsIgnoreCase("InstallApplication")) {//安裝 app
//													final String appURL = map.get(MapKey.MANIFEST_URL);
//
//													if(Mds_Utility.intAppRunID <= 0){//app 在背景(0)或未開啟(-1)
//														Intent intent = new Intent();
//														intent.putExtra(BundleKey.MDM_DOWNLOAD_URL, appURL);
//														intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//														intent.setClass(context, DataList.class);
//														context.startActivity(intent);
//													} else{// app 已開啟在前景
//														//廣播訊息
//														Intent intent = new Intent();
//														intent.setAction(MDM_ACTION);
//														intent.putExtra(BundleKey.MDM_DOWNLOAD_URL, appURL);
//														context.getApplicationContext().sendBroadcast(intent, PermissionKey.PERMISSION_RECEIVE_MESSAGE);//fortify
//													}
//												}else if(map.get("RequestType").equalsIgnoreCase("RemoveApplication")) {//移除 app
//													final String packageName = map.get(MapKey.IDENTIFIER);
//
//													TraceUtility.trace(TraceType.debug, TAG, "packageName:" +packageName);
//
//													if(Mds_Utility.intAppRunID <= 0){//app 在背景(0)或未開啟(-1)
//														Intent intent = new Intent();
//														intent.putExtra(BundleKey.MDM_PACKAGE_NAME, packageName);
//														intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//														intent.setClass(context, DataList.class);
//														context.startActivity(intent);
//													} else{// app 已開啟在前景
//														//廣播訊息
//														Intent intent = new Intent();
//														intent.setAction(MDM_ACTION);
//														intent.putExtra(BundleKey.MDM_PACKAGE_NAME, packageName);
//														context.getApplicationContext().sendBroadcast(intent, PermissionKey.PERMISSION_RECEIVE_MESSAGE);//fortify
//													}
//												}
//											}
//										} else {
//											if (intErrCount > -1){
//												// 只有錯誤上限大於-1才有統計，否則只要錯誤就重做，不停止。
//												intErrLoop++;
//												// 判斷是否超出錯誤上限
//												if (intErrLoop > intErrCount ){
//													blnMDMDone = true;
//												}
//											}
//											Thread.sleep(Mds_Utility.MDM_RETRY_WAIT_TIME);//等待時間再重試
//										}
//										map = null;
//									} else {
//										// 沒有足夠資料，進行錯誤次數計算
//										if (intErrCount > -1){
//											// 只有錯誤上限大於-1才有統計，否則只要錯誤就重做，不停止。
//											intErrLoop++;
//											// 判斷是否超出錯誤上限
//											if (intErrLoop > intErrCount ){
//												blnMDMDone = true;
//											}
//										}
////										Thread.sleep(3000);
//										Thread.sleep(Mds_Utility.MDM_RETRY_WAIT_TIME);//等待時間再重試
//										TraceUtility.trace(TraceType.verbose, TAG,"mdm.err:" + "AgentCode or LicenseKey no data!");
//									}
//								}
//								// 處理結束，還原旗標，釋放處理權
//								fin_appMain.setUtilityParam(ParamKey.mdm_status, "N", true);
//							} catch (final Exception ex) {
//			            		ex.printStackTrace();
//			            		// 發生異常，還原旗標，釋放處理權
//								fin_appMain.setUtilityParam(ParamKey.mdm_status, "N", true);
//								TraceUtility.trace(TraceType.error,TAG,"MDM.Exception" + ex.getMessage());
//							}
//						}
//					}.start();
//				}
//			}
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			TraceUtility.trace(TraceType.error, "Mds.GCMIntentService.handlemessage.JSONException",e.toString());
//		}
//	}
	
	/**
	 * 處理雲端訊息推播的註冊程序
	 * 
	 * @param context
	 * @param regID
	 */
	private void handleRegistration(final Context context, String regID) {
		final String registration = regID;
		TraceUtility.trace(TraceType.verbose, TAG ,"GCMIntentService-handleRegistration:" + "handleRegistration");
		SystemUtility.regID = regID;
		String strURL = "https://cloud.mds.com.tw/wistronmobile";
		strURL +="/Sysfun/PushService/setDevicePushID.aspx";

		TraceUtility.trace(TraceType.verbose, TAG ,"handleRegistration-strURL:" +  strURL);
		
		final Map<String, String> UserLoginData = new HashMap<String, String>();

		UserLoginData.put("APURL", strURL);
		UserLoginData.put("PHONETYPE", "android");
		UserLoginData.put("pushid", registration);
		UserLoginData.put("userid", "takz159");
		UserLoginData.put("DeviceId", SystemUtility.getDeviceID(context));

//		// 記錄 push id
//		AppMain appMain = (AppMain) context.getApplicationContext();
//		appMain.systemset.setPushID(registration, true);
//		// 推播設定處理完畢，解除設定
//		appMain.setUtilityParam(ParamKey.push_status, "N", false);
//
		//向後台作推播綁定
		TraceUtility.trace(TraceType.verbose, TAG, "handleRegistration.registrationId:" + registration);
		final int iConnect_timeout = 10000;
		final int iSocket_timeout = 60000;
		new Thread() {
			public void run() {
				try {
					String responsetext;
					responsetext = GCMUtility.sendTokenToServer(UserLoginData, iConnect_timeout, iSocket_timeout, context);
					TraceUtility.trace(TraceType.verbose, TAG, "handleRegistration.responsetext:" + responsetext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					TraceUtility.trace(TraceType.verbose, TAG,"handleRegistration.exception:" + e.toString());
				}
				TraceUtility.trace(TraceType.verbose, TAG, "handleRegistration.register success:" + registration);
			}
		}.start();
		
	}
}
