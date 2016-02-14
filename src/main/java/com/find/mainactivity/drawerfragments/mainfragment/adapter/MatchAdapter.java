package com.find.mainactivity.drawerfragments.mainfragment.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.find.R;

/**
 * 匹配成功卡片布局Adapter
 * Created by lenovo on 2014/12/5.
 */
public class MatchAdapter extends RecyclerView.Adapter<CardViewHolder> {

    //final card layout type
    private final int TYPE_HEAD=0x0001;
    private final int TYPE_MATCH=0x0002;

    // temp user image array
    private int [] images=new int[] {0,R.drawable.luffy,R.drawable.nami,R.drawable.chopper,R.drawable.robin};

    private int lastPosition=0;     //monitor the item position while animating

    @Override
    public int getItemCount() {
        return images.length;
    }

    @Override
    public int getItemViewType(int position) {
        switch(position) {
            case 0:
                return TYPE_HEAD;
            default:
                return TYPE_MATCH;
        }
    }
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View view=null;
        CardViewHolder viewHolder=null;
        switch(viewType) {
            case TYPE_HEAD:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fragment_match_recycler_header_layout,parent,false);
                viewHolder=new HeaderCardViewHolder(view);
                break;
            case TYPE_MATCH:
                view= LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fragment_match_recycler_card_layout,parent,false);
                viewHolder=new MatchCardViewHolder(view);
                break;
            default:
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder,int position) {
        switch(getItemViewType(position)) {
            case TYPE_HEAD:
                setItemAnimation(((HeaderCardViewHolder)holder).container,position);
                break;
            case TYPE_MATCH:
                ((MatchCardViewHolder)holder).userImage.setImageResource(images[position]);
                setItemAnimation(((MatchCardViewHolder)holder).container,position);
                break;
        }
    }

    //set the recylcerView item animtion
    private void setItemAnimation(View view,int position) {
        if(position>=lastPosition) {
            Animation anim= AnimationUtils.loadAnimation(view.getContext(),R.anim.recycler_item_slid_in_bottom);
            view.startAnimation(anim);
            lastPosition=position;
        }
    }
}


class CardViewHolder extends RecyclerView.ViewHolder {

    public CardViewHolder(View view) {
        super(view);
    }


}

class HeaderCardViewHolder extends CardViewHolder {

    public ImageView imageView;
    public View container;
    public HeaderCardViewHolder(View view) {
        super(view);
        imageView=(ImageView)view.findViewById(R.id.main_fragment_scan_view_recycler_header_imageView);
        container=view.findViewById(R.id.main_fragment_match_recycler_header_container);
    }
}

class MatchCardViewHolder extends CardViewHolder {

    public View container;
    public ImageView userImage;
    public MatchCardViewHolder(View view) {
        super(view);
        container=view.findViewById(R.id.main_fragment_match_card_container);
        userImage=(ImageView)view.findViewById(R.id.main_fragment_match_card_imageView);
    }
}
