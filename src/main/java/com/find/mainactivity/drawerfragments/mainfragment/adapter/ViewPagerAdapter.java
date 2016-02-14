package com.find.mainactivity.drawerfragments.mainfragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by lenovo on 2014/12/12.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private Fragment[] fragments;

    private String [] pageTitle=new String[] {"Share","Match"};

    public ViewPagerAdapter(FragmentManager fm,Fragment[] fragments) {
        super(fm);
        this.fragments=fragments;
    }

    @Override
    public CharSequence  getPageTitle(int position) {
        return pageTitle[position];
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
