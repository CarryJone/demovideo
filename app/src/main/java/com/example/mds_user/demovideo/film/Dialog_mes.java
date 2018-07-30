package com.example.mds_user.demovideo.film;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mds_user.demovideo.R;
import com.example.mds_user.demovideo.voice.VoiceActivity;


/**
 * Created by mds_user on 2018/7/12.
 */

public class Dialog_mes extends Dialog {
    private Context context;
    private EditText editText;
    private Button button,button1;
    private TextView dialog_title;
    private Handler mHandler;
    DialogResultCallBack resultCallBack;
    public Dialog_mes(@NonNull final Context context,boolean isvoice) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout);
        editText = (EditText) findViewById(R.id.edit);
        button = (Button) findViewById(R.id.btn);
        if (!isvoice) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!editText.getText().toString().isEmpty()) {
                        FileUtils.setName(editText.getText().toString());
                        ((FilmActivity) context).video();
                        dismiss();
                    } else {
                        Toast.makeText(context, "請輸入檔名", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!editText.getText().toString().isEmpty()) {
                        FileUtils.setName(editText.getText().toString()+"_voice");
                        ((VoiceActivity) context).Recording();
                        dismiss();
                    } else {
                        Toast.makeText(context, "請輸入檔名", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public Dialog_mes(@NonNull Context context, String title) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        LinearLayout layout1,layout2;
        editText = (EditText) findViewById(R.id.edit);
        dialog_title = (TextView) findViewById(R.id.dialog_title);
        button = (Button) findViewById(R.id.btn);
        button1 = (Button) findViewById(R.id.btn1);
       layout1 = (LinearLayout) findViewById(R.id.layout1);
       layout2 = (LinearLayout) findViewById(R.id.layout2);
       layout1.setVisibility(View.GONE);
       layout2.setVisibility(View.GONE);
       dialog_title.setGravity(Gravity.CENTER_HORIZONTAL);
        button1.setVisibility(View.VISIBLE);
        editText.setVisibility(View.GONE);
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
}
