package com.find.mainactivity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.find.R;
import com.find.mainactivity.drawerfragments.CircleFragment;
import com.find.mainactivity.drawerfragments.MainFragment;
import com.find.mainactivity.drawerfragments.MessageFragment;
import com.find.mainactivity.drawerfragments.SettingFragment;


public class MainActivity extends ActionBarActivity {

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    //window width height
    public static int windowHeight;
    public static int windowWidth;

    FragmentManager fm;
    Fragment[] fragments;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_layout);

        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        windowHeight=metrics.heightPixels;
        windowWidth=metrics.widthPixels;

        drawerLayout=(DrawerLayout)findViewById(R.id.main_activity_drawer_layout);
        drawerList=(ListView)findViewById(R.id.main_activity_drawer_menu_list);

        fragments=new Fragment[] {new MainFragment(),new CircleFragment(),new MessageFragment(),new SettingFragment()};

        fm=getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.main_activity_drawer_cotainer,fragments[0]).commit();

        DrawerAdatpter mAdapter=new DrawerAdatpter();
        drawerList.setAdapter(mAdapter);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectFragment(position);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if(DBManager.getInstance(this)!=null) {
//            DBManager.getInstance(this).;
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void selectFragment(int position) {

        if(fragments[position]!=null) {
            fragments[position]=null;
        }
        switch(position) {
            case 0:
                fragments[position]=new MainFragment();
                break;
            case 1:
                fragments[position]=new CircleFragment();
                break;
            case 2:
                fragments[position]=new MessageFragment();
                break;
            case 3:
                fragments[position]=new SettingFragment();
                break;
            default:
                break;
        }
        fm.beginTransaction().replace(R.id.main_activity_drawer_cotainer,fragments[position]).commit();
        drawerLayout.closeDrawers();
    }
}
