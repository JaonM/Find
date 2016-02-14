package com.find.mainactivity.drawerfragments.circlefragment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.find.R;

/**
 * Created by maqiang on 2015/1/8.
 */
public class CircleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int TYPE_ROUND=1;         //round type
    private final int TYPE_SQUARE=2;        //square type

    private int lastPosition=0;

    @Override
    public int getItemViewType(int position) {
        if(position%2==0) {
            return TYPE_ROUND;
        }else
            return TYPE_SQUARE;
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View view;
        RecyclerView.ViewHolder holder=null;
        switch(viewType) {
            case TYPE_ROUND:
                view= LayoutInflater.from(parent.getContext()).inflate(R.layout.circle_fragment_recycler_layout1,parent,false);
                holder=new RoundViewHolder(view);
                break;
            case TYPE_SQUARE:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.circle_fragment_recycler_layout2,parent,false);
                holder=new SquareViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,int position) {
        switch (getItemViewType(position)) {
            case TYPE_ROUND:
                setItemAnimation(((RoundViewHolder)holder).view,position);
                ((RoundViewHolder)holder).mGridView.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return 4;
                    }

                    @Override
                    public Object getItem(int position) {
                        return null;
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if(convertView==null) {
                            convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.circle_fragment_recycler_1_gridview_layout,parent,false);
                        }

                        return convertView;
                    }
                });
                break;
            case TYPE_SQUARE:
                setItemAnimation(((SquareViewHolder)holder).view,position);
                break;
        }
    }

    //set the recylcerView item animtion
    private void setItemAnimation(View view,int position) {
        if(position>=lastPosition) {
            Animation anim= AnimationUtils.loadAnimation(view.getContext(), R.anim.recycler_item_slid_in_bottom);
            view.startAnimation(anim);
            lastPosition=position;
        }
    }
}

//round circle layout view holder
class RoundViewHolder extends RecyclerView.ViewHolder {

    //root view
    public View view;
    public GridView mGridView;

    public RoundViewHolder(View view) {
        super(view);
        this.view=view;
        mGridView=(GridView)view.findViewById(R.id.circle_fragment_recycler_1_gridView);
    }
}

//square circle layout view holder
class SquareViewHolder extends RecyclerView.ViewHolder {

    //root view
    public View view;
//    public GridView mGridView;
    public ImageView headImageView;
//    public TextView headTextView;


    public SquareViewHolder(View view) {
        super(view);
        this.view=view;
//        mGridView=(GridView)view.findViewById(R.id.circle_fragment_recycler_2_gridView);
        headImageView=(ImageView)view.findViewById(R.id.circle_fragment_recycler_2_head_imageView);
//        headTextView=(TextView)view.findViewById(R.id.circle_fragment_recycler_2_head_name_textView);
    }


}