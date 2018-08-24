package com.example.mds_user.demovideo.filelist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.mds_user.demovideo.R;
import com.example.mds_user.demovideo.VoideUtils;
import com.example.mds_user.demovideo.film.Dialog_mes;
import com.example.mds_user.demovideo.film.MyFileUtils;
import com.example.mds_user.demovideo.film.UploadVideoAsyncTask;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import life.knowledge4.videotrimmer.utils.FileUtils;

public class FilelistActivity extends AppCompatActivity {
    ArrayList<File> files;
    ArrayList<FileData> fileDatas;
    Context context;
    TextView title;
    ListView listView;
    Button before,after;
    String beforepath = MyFileUtils.before_path;
    String afterpath = MyFileUtils.after_path;
    FileAdapter adapter;
    private Handler handler;
    private  Runnable r;
    private int count = 0;
    boolean isbefore = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filelist);
        listView = (ListView) findViewById(R.id.list);
        before = (Button) findViewById(R.id.before);
        after = (Button) findViewById(R.id.after);
        title = (TextView) findViewById(R.id.title);
        files = new ArrayList<>();
        context = this;
        before.setOnClickListener(new FileButton());
        after.setOnClickListener(new FileButton());
        inquirefile(beforepath, files);
        changedata();
        handler = new Handler();
        r = new Runnabletime();
        VoideUtils.VADataLIST.size();
        setView();
    }

    public void inquirefile(String directoryName, ArrayList<File> files) {

        File directory = new File(directoryName);
        directory.mkdir();
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
        title.setText("剪輯前");
        adapter = new FileAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isbefore) {
                    Nowdata.name = fileDatas.get(position).name;
                    startTrimActivity(Uri.fromFile(fileDatas.get(position).file));
                    Toast.makeText(context, fileDatas.get(position).getPath(), Toast.LENGTH_SHORT).show();
                }else{
//                    Uri uri = null;
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//                        uri = FileProvider.getUriForFile(context,"com.example.mds_user.demovideo.fileProvider",fileDatas.get(position).file);
//                    }else {
//                        uri = Uri.fromFile(fileDatas.get(position).file);
//                    }
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                    intent.setDataAndType(uri, "video/mp4");
//                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    startActivity(intent);
                    VoideUtils.VideoPlay(context,fileDatas.get(position).file);

                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Nowdata.name = fileDatas.get(position).name;
                final Dialog_mes dialog = new Dialog_mes(context,"是否上傳影片");
                dialog.setDialog_but(new Dialog_mes.DialogResultCallBack() {
                    @Override
                    public void onResult() {
                        String path = fileDatas.get(position).path;
                        updata(path);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return true;
            }
        });
    }
//    影片上傳
    public void updata(String path){
        new UploadVideoAsyncTask(context, mHandler,1).execute(path);
        count = 0;
        handler.post(r);
        Toast.makeText(context,"影片上傳中",Toast.LENGTH_SHORT).show();
    }
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String data = msg.getData().getString("data");
            handler.removeCallbacks(r);
            count = 0;
            Toast.makeText(context,msg.getData().get("data").toString(),Toast.LENGTH_SHORT).show();
        }};
    private void startTrimActivity(@NonNull Uri uri) {
        Intent intent = new Intent(context, TrimmerActivity.class);
        intent.putExtra("path", FileUtils.getPath(context, uri));
        startActivity(intent);
    }
    private void changedata(){
        if (fileDatas == null) {
            fileDatas = new ArrayList<>();
        }else{
            fileDatas.clear();
        }
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.filelist_item,null);
            final TextView name = (TextView) convertView.findViewById(R.id.name);
            final TextView time = (TextView) convertView.findViewById(R.id.time);
            final TextView size = (TextView) convertView.findViewById(R.id.size);
            Button button = (Button) convertView.findViewById(R.id.button);
            final FileData data = fileDatas.get(position);
            if (isbefore){
                button.setText("刪除");
            }else{
                button.setText("上傳");
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isbefore){
                        Nowdata.name = fileDatas.get(position).name;
                        final Dialog_mes dialog = new Dialog_mes(context,"是否刪除影片");
                        dialog.setDialog_but(new Dialog_mes.DialogResultCallBack() {
                            @Override
                            public void onResult() {
                                String path = fileDatas.get(position).path;
                               File file = new File(path);
                               if (file!=null){
                                   file.delete();
                                   if (file.exists()) {
                                       Toast.makeText(getBaseContext(),
                                               "檔案刪除失敗.",
                                               Toast.LENGTH_SHORT).show();
                                   }else if(!file.exists()){
                                       Toast.makeText(getBaseContext(),
                                               "檔案已被刪除.",
                                               Toast.LENGTH_SHORT).show();
                                   }
                               }
                                dialog.dismiss();
                                changebutton(true,beforepath);
                            }
                        });
                        dialog.show();
                    }else{
                        Nowdata.name = fileDatas.get(position).name;
                        final Dialog_mes dialog = new Dialog_mes(context,"是否上傳影片");
                        dialog.setDialog_but(new Dialog_mes.DialogResultCallBack() {
                            @Override
                            public void onResult() {
                                String path = fileDatas.get(position).path;
                                updata(path);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                }
            });
            name.setText(data.getName());
            time.setText(data.getSize());
            size.setText(data.getTime());

            return convertView;
        }
    }
    class FileButton implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == before){
                changebutton(true,beforepath);
                title.setText("剪輯前");
            }else if (v == after){
                changebutton(false,afterpath);
                title.setText("剪輯後");
            }
        }
    }

    private void changebutton(boolean button,String path) {
        files.clear();
        isbefore = button;
        inquirefile(path, files);
        changedata();
        adapter.notifyDataSetChanged();
    }
    class  Runnabletime implements Runnable {
        @Override
        public void run() {
            count++;
            Log.d("data",count+"");
            handler.postDelayed(r,1000);
        }
    }
}