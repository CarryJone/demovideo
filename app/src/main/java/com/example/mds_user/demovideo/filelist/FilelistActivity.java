package com.example.mds_user.demovideo.filelist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mds_user.demovideo.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import life.knowledge4.videotrimmer.utils.FileUtils;

public class FilelistActivity extends AppCompatActivity {
    ArrayList<File> files;
    ArrayList<FileData> fileDatas;
    Context context;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filelist);
        listView = (ListView) findViewById(R.id.list);
        files = new ArrayList<>();
        context = this;
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/demos/file/tmp/test";
        inquirefile(path, files);
        files.size();
        fileDatas = new ArrayList<>();
        for (File data : files) {
            String name = data.getName();
            long time = data.lastModified();
            float length = data.length() / 1024;
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
            String times = df.format(time);
            String size = "";
            if (length > 1024) {
                size = String.format("%.2f MB", length / 1024);
            } else {
                size = String.format("%.0f KB", length);
            }
            FileData fileData = new FileData(data,name, size, times, data.getPath());
            fileDatas.add(fileData);
        }
        fileDatas.size();
        setView();
    }

    public void inquirefile(String directoryName, ArrayList<File> files) {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                inquirefile(file.getAbsolutePath(), files);
            }
        }
    }
    private void setView(){
        FileAdapter adapter = new FileAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startTrimActivity(Uri.fromFile(fileDatas.get(position).file));
                Toast.makeText(context,fileDatas.get(position).getPath(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void startTrimActivity(@NonNull Uri uri) {
        Intent intent = new Intent(context, TrimmerActivity.class);
        intent.putExtra("path", FileUtils.getPath(context, uri));
        startActivity(intent);
    }
    class FileAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return fileDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return fileDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.filelist_item,null);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView time = (TextView) convertView.findViewById(R.id.time);
            TextView size = (TextView) convertView.findViewById(R.id.size);
            FileData data = fileDatas.get(position);
            name.setText(data.getName());
            time.setText(data.getSize());
            size.setText(data.getTime());

            return convertView;
        }
    }
}