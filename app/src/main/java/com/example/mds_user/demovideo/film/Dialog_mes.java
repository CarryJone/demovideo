package com.example.mds_user.demovideo.film;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mds_user.demovideo.R;
import com.example.mds_user.demovideo.VoideUtils;
import com.example.mds_user.demovideo.Voide_Audio_DataBase;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by mds_user on 2018/7/12.
 */

public class Dialog_mes extends Dialog {
    private Context context;
    private Button button,button1;
    private TextView dialog_title;
    private Handler mHandler;
    private ListView listView;
    DialogResultCallBack resultCallBack;
    public Dialog_mes(@NonNull final Context context, boolean isvoice, final File file) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout);
        button = (Button) findViewById(R.id.btn);

        final Map<String,String> map = new HashMap<>();
        listView = (ListView) findViewById(R.id.list);
        MyBaseAdapter adapter = new MyBaseAdapter(VoideUtils.text);
        listView.setAdapter(adapter);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                for (int a=0;a<VoideUtils.text.size();a++){
                   View view =  getViewByPosition(a,listView);
                   EditText editText = (EditText) view.findViewById(R.id.edit);
                   if (editText.getText().toString().isEmpty()){
                       Toast.makeText(context,"資料未填寫",Toast.LENGTH_SHORT).show();
                       return;
                   }
                    map.put(VoideUtils.text.get(a),editText.getText().toString());
                }
                    Voide_Audio_DataBase dataBase = new Voide_Audio_DataBase(file,map);
//                    VoideUtils.sendDataFromDB(dataBase);
                    dismiss();
                }
            });

    }

    public Dialog_mes(@NonNull Context context, String title) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        dialog_title = (TextView) findViewById(R.id.text);
        button = (Button) findViewById(R.id.btn);
        button1 = (Button) findViewById(R.id.btn1);
       dialog_title.setGravity(Gravity.CENTER_HORIZONTAL);
        button1.setVisibility(View.VISIBLE);
        setButtonListener();
        dialog_title.setText(title);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    public void setDialog_but( DialogResultCallBack callback){
        resultCallBack = callback;
    }
    private void setButtonListener()
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultCallBack.onResult();
            }
        });
    }
    public interface DialogResultCallBack
    {
        void onResult();
    }
    class MyBaseAdapter extends BaseAdapter{
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

            return convertView;
        }
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
}
