package com.example.mds_user.demovideo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoViewActivity extends AppCompatActivity {
    private VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        String path = getIntent().getStringExtra("path");
        videoView = (VideoView) findViewById(R.id.video);
        videoView.setVideoPath(path);
        videoView.setMediaController(new MediaController(this));
        videoView.start();
    }
}
