package com.example.mds_user.demovideo.film;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mds_user.demovideo.R;


/**
 * Created by mds_user on 2018/7/12.
 */

public class Dialog_mes extends Dialog {
    private Context context;
    private EditText editText;
    private Button button;
    private Handler mHandler;
    public Dialog_mes(@NonNull final Context context) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        editText = (EditText) findViewById(R.id.edit);
        button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().isEmpty()){
                    FileUtils.setName(editText.getText().toString());
                    ((FilmActivity)context).video();
                    dismiss();
                }else{
                    Toast.makeText(context,"請輸入檔名",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
