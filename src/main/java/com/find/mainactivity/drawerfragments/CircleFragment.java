package com.find.mainactivity.drawerfragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.find.mainactivity.drawerfragments.circlefragment.adapter.CircleAdapter;
import com.find.R;

/**
 * Created by maqiang on 2015/1/6.
 */
public class CircleFragment extends Fragment{

    private View rootView;
    private RecyclerView mRecyclerView;
    private CircleAdapter mAdapter;

    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstancestate) {
        rootView=inflater.inflate(R.layout.circle_fragment_layout,container,false);
        mRecyclerView=(RecyclerView)rootView.findViewById(R.id.circle_fragment_recyclerView);

        LinearLayoutManager mManager=new LinearLayoutManager(getActivity());
        mManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mManager);

        mAdapter=new CircleAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mProgressBar=(ProgressBar)rootView.findViewById(R.id.circle_fragment_progressBar);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        },2000);
        return rootView;
    }
}
