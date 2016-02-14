package com.find.mainactivity.drawerfragments;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.astuetz.PagerSlidingTabStrip;
import com.find.mainactivity.drawerfragments.mainfragment.adapter.ViewPagerAdapter;
import com.find.mainactivity.drawerfragments.mainfragment.fragments.MatchFragment;
import com.find.mainactivity.drawerfragments.mainfragment.fragments.ShareFragment;
import com.find.R;

/**
 * Created by maqiang on 2015/1/4.
 */
public class MainFragment extends android.support.v4.app.Fragment {

    View rootView;

    Toolbar mToolbar;

    private ViewPager mViewPager;

    Fragment[] fragments;

    FragmentManager fm;

    //head view contains toobar and viewpager indicator
    private View headerLayout;

    //viewpager indicator
    public static PagerSlidingTabStrip mPagerTab;

    //control the headerLayout animation for showing or hiding;
    public static Handler mHandler;

    //handler control message
    public static final int HEADER_SHOW_MESSAGE=0x001;
    public static final int HEADER_HIDE_MESSAGE=0x002;

    //headerLayout height and width;
    public static int headerHeight;
    public static int topMenuHeight;

    public static boolean headerAnimFlag=false;       //flag for anim called

    //monitor global layout listener called times
    private boolean isGlobalFirstCalled=true;

    //ShareFragment Layout
//    RecyclerView mMenuRecyclerView;
//    private View topMenu;
//    private View menuLayout;
//    private ImageView topMenuTriangle;
//    private boolean isMenuShown=true;
//    private boolean isMenuContentShown=false;

    //current fragment index
    private int currentTabIndex=0;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.main_fragment_layout,container,false);

        headerLayout=rootView.findViewById(R.id.main_fragment_header_layout);
        mToolbar=(Toolbar)rootView.findViewById(R.id.main_fragment_toolBar);
//        getActivity().setSupportActionBar(mToolbar);

        mToolbar.setTitleTextColor(Color.WHITE);
        mViewPager=(ViewPager)rootView.findViewById(R.id.main_fragment_viewPager);

        fm=getActivity().getSupportFragmentManager();
        fragments=new Fragment[] {new ShareFragment(),new MatchFragment()};
        ViewPagerAdapter adapter=new ViewPagerAdapter(fm,fragments);
        mViewPager.setAdapter(adapter);

        //init viewPager indicator
        mPagerTab=(PagerSlidingTabStrip)rootView.findViewById(R.id.main_fragment_tabs);
        mPagerTab.setViewPager(mViewPager);


        mPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                if(position==0) {
//                    mViewPager.setPadding(0,headerHeight,0,0);
//                }else {
//                    mViewPager.setPadding(0,0,0,0);
//                }
                currentTabIndex=position;
                if(position==0&&headerAnimFlag) {
                    final TranslateAnimation showAnim=new TranslateAnimation(0,0,-1*headerHeight,0);
                    showAnim.setDuration(300);
                    showAnim.setFillAfter(true);

                    showAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            headerLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    headerLayout.startAnimation(showAnim);
                    headerAnimFlag=false;


                }



            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        headerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                headerHeight=headerLayout.getHeight();
                if(isGlobalFirstCalled) {
//                    mViewPager.setPadding(0,headerHeight,0,0);
                    isGlobalFirstCalled=false;
                }
            }
        });

        mHandler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == HEADER_HIDE_MESSAGE && !headerAnimFlag) {
                    TranslateAnimation hideAnim = new TranslateAnimation(0, 0, 0, -headerHeight);

                    hideAnim.setDuration(500);
//                    hideAnim.setFillAfter(true);

                    hideAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            headerLayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    headerLayout.startAnimation(hideAnim);
                    headerAnimFlag = true;

                } else if (msg.what == HEADER_SHOW_MESSAGE && headerAnimFlag) {

                    final TranslateAnimation showAnim = new TranslateAnimation(0, 0, -1 * headerHeight, 0);
                    showAnim.setDuration(300);
                    showAnim.setFillAfter(true);

                    showAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            headerLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    headerLayout.startAnimation(showAnim);
                    headerAnimFlag = false;


                }
            }



        };


        return rootView;
    }




}
