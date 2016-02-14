package com.find.mainactivity.drawerfragments.mainfragment.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;

import com.find.mainactivity.drawerfragments.MainFragment;
import com.find.mainactivity.drawerfragments.mainfragment.adapter.MatchAdapter;
import com.find.R;
import com.find.custom.view.ScanView;

/**
 * Created by lenovo on 2014/12/20.
 */
public class MatchFragment extends Fragment {

    private ScanView mScanView;
    private ImageButton mSettingsBtn;
    private RecyclerView mRecyclerView;
    private View scanSuccessLayout;
    View rootView;                      //rootView;
//    int scrollYAmount=0;            //monitor recyclerview scrollY total amount;

    private int lastDy;             //monitor recyclerView last dy
    private boolean isFirstGetHeight=true;       //is it first time to get the header height;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.main_fragment_match_layout,parent,false);
        scanSuccessLayout=rootView.findViewById(R.id.main_fragment_scan_success_layout);
        initScanView();
        initSettingBtn();
        initMatchView();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MatchFragment","onDestroy");
    }


    //init scanView
    private void initScanView() {
        mScanView=(ScanView)rootView.findViewById(R.id.main_fragment_scanView);
        final View scanRoot=rootView.findViewById(R.id.main_fragment_scanLayout);
        mScanView.setCallable(new ScanView.Callable() {
            @Override
            public void call() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //scan success layout show
                        scanSuccessLayout.setVisibility(View.VISIBLE);
                    }
                }, 3000);

                scanRoot.setVisibility(View.GONE);
            }
        });
    }

    //init settings btn
    private void initSettingBtn() {
        mSettingsBtn=(ImageButton)rootView.findViewById(R.id.main_fragment_scanView_setting_btn);
        //scale animation for settings btn when clicked
        final ScaleAnimation scaleAnimation1=new ScaleAnimation(
                1.0f,0f,1.0f,0f, Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f
        );

        scaleAnimation1.setDuration(300);
        scaleAnimation1.setFillAfter(true);

        final ScaleAnimation scaleAnimation2=new ScaleAnimation(
                0f,1.0f,0f,1.0f, Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f
        );
        scaleAnimation2.setDuration(300);
        scaleAnimation2.setFillAfter(true);

        scaleAnimation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSettingsBtn.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        scaleAnimation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mSettingsBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //attach a listener to setting button
        mSettingsBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mSettingsBtn.startAnimation(scaleAnimation1);
                new AlertDialog.Builder(getActivity())
                        .setTitle("更改匹配精度")
                        .setPositiveButton("更改",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSettingsBtn.startAnimation(scaleAnimation2);
                            }
                        })
                        .setNegativeButton("取消",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSettingsBtn.startAnimation(scaleAnimation2);
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();;
            }
        }) ;
    }

    //init match view
    private void initMatchView(){
        mRecyclerView=(RecyclerView)rootView.findViewById(R.id.main_fragment_scan_success_recyclerView);
        LinearLayoutManager mManager=new LinearLayoutManager(getActivity());
        mManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mManager);

        MatchAdapter mAdapter=new MatchAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if(isFirstGetHeight) {
                    lastDy=MainFragment.headerHeight;
                    isFirstGetHeight=false;
                    mRecyclerView.setPadding(0, MainFragment.headerHeight,0,0);
                }
//                if(lastDy>0&&lastDy<=MainActivity.headerHeight) {
//                    mRecyclerView.setPadding(0,lastDy,0,0);
//                }else if(lastDy<=0) {
//                    mRecyclerView.setPadding(0,0,0,0);
//                    lastDy=0;
//                }else if(lastDy>MainActivity.headerHeight) {
//                    mRecyclerView.setPadding(0,lastDy,0,0);
//                    lastDy=MainActivity.headerHeight;
//                }
            }
        });

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //handle the recyclerView scrollAnimation
                if(dy>0&&recyclerView.getPaddingTop()>0
                        ||dy<0&&recyclerView.getLayoutManager().getChildAt(0).getTop()>=0) {
                    lastDy=lastDy-dy;
                }

                if(lastDy<0) {
                    lastDy=0;
                }else if(lastDy>MainFragment.headerHeight) {
                    lastDy=MainFragment.headerHeight;
                }
                recyclerView.setPadding(0,lastDy,0,0);

                //send the message to animate the header
                if(dy>10) {
                    MainFragment.mHandler.sendEmptyMessage(MainFragment.HEADER_HIDE_MESSAGE);
                }else if(dy<-10) {
                    MainFragment.mHandler.sendEmptyMessage(MainFragment.HEADER_SHOW_MESSAGE);
                }
            }
        });


    }
 }
