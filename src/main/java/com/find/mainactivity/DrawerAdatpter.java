package com.find.mainactivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.find.R;

/**
 * Created by maqiang on 2015/1/4.
 */
public class DrawerAdatpter extends BaseAdapter {

    private String [] titles=new String [] {"首页","圈子","消息","设置"};
    private int [] drawables=new int [] {R.drawable.ic_action_everyone,R.drawable.ic_action_circle,
                                        R.drawable.ic_action_email,R.drawable.ic_action_settings_dark};

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return titles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        if(convertView==null) {
            convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.main_activity_drawer_layout,parent,false);
        }
        TextView titleTextView=(TextView)convertView.findViewById(R.id.main_activity_title_textView);
        titleTextView.setText(titles[position]);

        ImageView imageView=(ImageView)convertView.findViewById(R.id.main_activity_drawer_icon_imageView);
        imageView.setImageResource(drawables[position]);

        return convertView;
    }
}
