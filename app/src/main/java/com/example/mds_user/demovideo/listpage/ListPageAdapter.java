package com.example.mds_user.demovideo.listpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mds_user.demovideo.R;
import com.example.mds_user.demovideo.VoideUtils;
import com.example.mds_user.demovideo.Voide_Audio_DataBase;

import java.util.List;

/**
 * Created by mds_user on 2018/8/23.
 */

public class ListPageAdapter extends BaseAdapter {
    private List<Voide_Audio_DataBase> data ;
    private Context context;

    public ListPageAdapter(List<Voide_Audio_DataBase> data, Context context) {
        this.data = data;
        this.context = context;
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
        View view = LayoutInflater.from(context).inflate(R.layout.listpage_item,null);
        TextView num = (TextView) view.findViewById(R.id.text1);
        TextView msg = (TextView) view.findViewById(R.id.text2);
        TextView status = (TextView) view.findViewById(R.id.text3);
        Voide_Audio_DataBase mydata = data.get(position);
        num.setText(position+1+"");
        msg.setText(mydata.getData().get(VoideUtils.text.get(0)));
        status.setText(mydata.getStatus());
        return view;
    }
}
