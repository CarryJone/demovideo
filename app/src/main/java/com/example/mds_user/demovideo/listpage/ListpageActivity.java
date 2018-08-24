package com.example.mds_user.demovideo.listpage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mds_user.demovideo.R;
import com.example.mds_user.demovideo.VoideUtils;
import com.example.mds_user.demovideo.Voide_Audio_DataBase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ListpageActivity extends AppCompatActivity {

    private ListPageAdapter adapter ;
    private Context context;
    private ListView listView;
    private ImageButton add;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listpage);
        context = this;
        listView = (ListView) findViewById(R.id.list);
        add = (ImageButton) findViewById(R.id.add);
        textView = (TextView) findViewById(R.id.title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        textView.setText("案件清單");
        adapter = new ListPageAdapter( VoideUtils.VADataLIST,context);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context,CasedetailsActivity.class);
                intent.putExtra("num",position);
                startActivity(intent);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> map = new HashMap<>();
                for (int a=0;a<VoideUtils.text.size();a++){
                    map.put(VoideUtils.text.get(a),"");
                }
                Voide_Audio_DataBase dataBase = new Voide_Audio_DataBase(null,map);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                String str = formatter.format(curDate);
                dataBase.setCreatid(str);
                VoideUtils.sendDataFromDB(dataBase);
                VoideUtils.VADataLIST.add(dataBase);
                adapter.notifyDataSetChanged();
                Intent intent = new Intent(context,CasedetailsActivity.class);
                intent.putExtra("num",VoideUtils.VADataLIST.size()-1);
                startActivity(intent);
            }
        });

    }
}
