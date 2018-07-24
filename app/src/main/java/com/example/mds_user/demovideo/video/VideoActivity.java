package com.example.mds_user.demovideo.video;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mds_user.demovideo.R;

import us.zoom.sdk.MeetingError;
import us.zoom.sdk.MeetingEvent;
import us.zoom.sdk.MeetingOptions;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class VideoActivity extends AppCompatActivity
        implements MeetingServiceListener, ZoomSDKInitializeListener {
    User currentSDKUser;
    Button button ;
   EditText name,id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity);
        button = (Button) findViewById(R.id.btn) ;
        name = (EditText) findViewById(R.id.name);
        id = (EditText) findViewById(R.id.id);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initZoomSDK();
                joinMeeting(id.getText().toString(),"");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public  void joinMeeting(String meetingNo, String password){

        if(meetingNo.length() == 0) {
            Toast.makeText(this, "您需要輸入預定的會議號碼", Toast.LENGTH_LONG).show();
            return;
        }

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if(!zoomSDK.isInitialized()) {
            Toast.makeText(this, "ZoomSDK尚未成功初始化", Toast.LENGTH_LONG).show();
            return;
        }

        MeetingService meetingService = zoomSDK.getMeetingService();

        MeetingOptions opts = new MeetingOptions();
//		opts.no_driving_mode = true;
//		opts.no_invite = true;
//		opts.no_meeting_end_message = true;
//		opts.no_titlebar = true;
//		opts.no_bottom_toolbar = true;
//		opts.no_dial_in_via_phone = true;
//		opts.no_dial_out_to_phone = true;
//		opts.no_disconnect_audio = true;
        String nametx = name.getText().toString().isEmpty()?"test":name.getText().toString();
        int ret = meetingService.joinMeeting(this, meetingNo, nametx, password, opts);
        Log.i("JOIN : ", "JOIN Meeting, ret=" + ret);

    }
    public void initZoomSDK(){

        ZoomSDK sdk = ZoomSDK.getInstance();
        if(!sdk.isInitialized()) {
            currentSDKUser = new User();
            currentSDKUser.setAPI_KEY("MAj2EJLfR_mYgo1Wi_Dhqg");
            currentSDKUser.setAPI_SECRET("DYBFM7ueRSdfHN8lBnQIO7zHq0LmhF1zFrIk");
            currentSDKUser.setAPP_KEY("Ch5qlJfmNqb3MknRa7BIwBQslkGfvCdNaLtP");
            currentSDKUser.setAPP_SECRET("MzLtAfE2UwXoLWVEXbcfF8r74JjmbfeCvD6p");
            currentSDKUser.setUSER_EMAIL("");
            currentSDKUser.setUSER_ID("");
            currentSDKUser.setWEB_DOMAIN("");
            sdk.initialize(this, "Ch5qlJfmNqb3MknRa7BIwBQslkGfvCdNaLtP", "MzLtAfE2UwXoLWVEXbcfF8r74JjmbfeCvD6p", "zoom.us", this);
            //set your own keys for dropbox , oneDrive and googleDrive
            sdk.setDropBoxAppKeyPair(this, null/*DROPBOX_APP_KEY*/, null/*DROPBOX_APP_SECRET*/);
            sdk.setOneDriveClientId(this, null/*ONEDRIVE_CLIENT_ID*/);
            sdk.setGoogleDriveClientId(this,null /*GOOGLE_DRIVE_CLIENT_ID*/);
        }

    }

    @Override
    public void onMeetingEvent(int meetingEvent, int errorCode, int internalErrorCode) {
        Log.d("test","1234124");
        if(meetingEvent == MeetingEvent.MEETING_CONNECT_FAILED && errorCode == MeetingError.MEETING_ERROR_CLIENT_INCOMPATIBLE) {
            Toast.makeText(getApplicationContext(), "Version of ZoomSDK is too low!", Toast.LENGTH_LONG).show();
        }

        if(meetingEvent == MeetingEvent.MEETING_DISCONNECTED || meetingEvent == MeetingEvent.MEETING_CONNECT_FAILED) {
            Toast.makeText(getApplicationContext(),"會議結束",Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
        Log.i("INITIALIZATION: ", "onZoomSDKInitializeResult, errorCode=" + errorCode + ", internalErrorCode=" + internalErrorCode);

        if(errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
            Toast.makeText(getApplicationContext(), "Failed to initialize Zoom SDK. Error: " + errorCode + ", internalErrorCode=" + internalErrorCode, Toast.LENGTH_LONG);
        } else {
            Toast.makeText(getApplicationContext(), "成功初始化ZOOM SDK", Toast.LENGTH_LONG).show();

            registerMeetingServiceListener();
        }
    }
    private void registerMeetingServiceListener() {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        MeetingService meetingService = zoomSDK.getMeetingService();
        if(meetingService != null) {
            meetingService.addListener(this);
        }
    }
    @Override
    protected void onDestroy() {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if(zoomSDK.isInitialized()) {
            MeetingService meetingService = zoomSDK.getMeetingService();
            meetingService.removeListener(this);
        }

        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}
