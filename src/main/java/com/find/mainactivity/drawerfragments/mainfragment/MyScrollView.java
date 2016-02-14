package com.find.mainactivity.drawerfragments.mainfragment;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

/**
 * Created by maqiang on 2015/4/19.
 */
public class MyScrollView extends ScrollView {

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context,AttributeSet attrs) {
        super(context,attrs);
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l,t,oldl,oldt);
        Log.d("scrollY",t+" ");
    }
}
