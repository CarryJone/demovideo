package com.example.mds_user.demovideo.listpage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mds_user.demovideo.NoScrollListView;
import com.example.mds_user.demovideo.R;
import com.example.mds_user.demovideo.VoideUtils;
import com.example.mds_user.demovideo.Voide_Audio_DataBase;
import com.example.mds_user.demovideo.filelist.TrimmerActivity;
import com.example.mds_user.demovideo.film.Dialog_mes;
import com.example.mds_user.demovideo.film.FilmActivity;
import com.example.mds_user.demovideo.film.MyFileUtils;
import com.example.mds_user.demovideo.film.UploadVideoAsyncTask;
import com.example.mds_user.demovideo.voice.VoiceActivity;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import life.knowledge4.videotrimmer.utils.FileUtils;

public class CasedetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private Button play,record,store,upload,edit;
    private NoScrollListView listView;
    private TextView title;
    private Context context;
    private int num = -1;
    private Voide_Audio_DataBase database;
    private Switch aSwitch ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_casedetails);
        init();
    }

    private void init() {
        context = this;
        listView = (NoScrollListView) findViewById(R.id.list);
        play = (Button) findViewById(R.id.play);
        record = (Button) findViewById(R.id.record);
        store = (Button) findViewById(R.id.store);
        upload = (Button) findViewById(R.id.upload);
        edit = (Button) findViewById(R.id.edit);
        title = (TextView) findViewById(R.id.title);
        aSwitch = (Switch) findViewById(R.id.btnSwitch);
        play.setOnClickListener(this);
        record.setOnClickListener(this);
        store.setOnClickListener(this);
        upload.setOnClickListener(this);
        edit.setOnClickListener(this);
        getbundle();
        setdata();
    }

    private void setdata() {
        title.setText("案件明細");
        database = VoideUtils.VADataLIST.get(num);
        MyBaseAdapter adapter = new MyBaseAdapter(VoideUtils.text);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit:
              if(database.getOriginal_file() !=null){//原檔
                    startTrimActivity(Uri.fromFile(database.getOriginal_file()));
                }else{//沒有檔案
                  if (!aSwitch.isChecked()) {
                      gotovideo();
                  }else{
                      gotovoice();
                  }
                }
                break;
            case R.id.record:
                if (!aSwitch.isChecked()) {
                    gotovideo();
                }else{
                    gotovoice();
                }
                break;
            case R.id.store:
                store();
                break;
            case R.id.upload:
                final Dialog_mes dialog = new Dialog_mes(context,"是否上傳影片");
                dialog.setDialog_but(new Dialog_mes.DialogResultCallBack() {
                    @Override
                    public void onResult() {
                        String path = "";
                        if (database.getFile()!=null) {
                            path = database.getFile().getPath();
                        }else if (database.getOriginal_file()!=null){
                            path = database.getOriginal_file().getPath();
                        }
                        updata(path);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.play:
                if(database.getFile() !=null){//原檔
                    VoideUtils.VideoPlay(context,database.getFile());
                }else if(database.getOriginal_file() !=null){//原檔
                    VoideUtils.VideoPlay(context,database.getOriginal_file());
                }else{
                    Toast.makeText(context,"無檔案",Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void store() {
        Map<String,String> map = new HashMap<>();
        for (int a = 0; a< VoideUtils.text.size(); a++){
            View view =  getViewByPosition(a,listView);
            EditText editText = (EditText) view.findViewById(R.id.edit);
            if (editText.getText().toString().isEmpty()){
                Toast.makeText(context,"資料未填寫",Toast.LENGTH_SHORT).show();
                return;
            }
            map.put(VoideUtils.text.get(a),editText.getText().toString());
        }
        database.setData(map);
        VoideUtils.UpDataFromDB(database);
        Toast.makeText(context,"儲存完成",Toast.LENGTH_SHORT).show();
    }

    private void gotovideo(){
        Intent intent = new Intent(context, FilmActivity.class);
        intent.putExtra("num",num);
        startActivityForResult(intent,101);
    }
    private void gotovoice(){
        Intent intent = new Intent(context, VoiceActivity.class);
        intent.putExtra("num",num);
        startActivityForResult(intent,102);
    }
    private void getbundle(){
        num = getIntent().getIntExtra("num",-1);
    }
    class MyBaseAdapter extends BaseAdapter {
        List<String> data;
        MyBaseAdapter(List<String> data){
            this.data = data;
        }
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.inputdata_item,null);
            TextView textView = (TextView) convertView.findViewById(R.id.dialog_title);
            EditText editText = (EditText) convertView.findViewById(R.id.edit);
            textView.setText(data.get(position));
            editText.setText(database.getData().get(data.get(position)));
            return convertView;
        }
    }
    private void startTrimActivity(@NonNull Uri uri) {
        Intent intent = new Intent(context, TrimmerActivity.class);
        intent.putExtra("path", FileUtils.getPath(context, uri));
        intent.putExtra("num",num);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        VoideUtils.UpDataFromDB(database);
    }
    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
    public void updata(String path){
        new UploadVideoAsyncTask(context, mHandler,num).execute(path);
        Toast.makeText(context,"影片上傳中",Toast.LENGTH_SHORT).show();
    } public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String data = msg.getData().getString("data");
            database.setStatus("以上傳");
            VoideUtils.UpDataFromDB(database);
            Toast.makeText(context,msg.getData().get("data").toString(),Toast.LENGTH_SHORT).show();
        }};

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        store();
    }
}
