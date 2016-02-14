package com.find.mainactivity.drawerfragments.mainfragment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.find.R;

/**
 * Created by lenovo on 2014/12/29.
 */
public class TopMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //view holder type
    public final int TYPE_RANGE=0x0004;
    public final int TYPE_HOT_TOPIC_SELECTION=0x0005;
    public final int TYPE_HOT_TOPIC=0x0006;
    public final int TYPE_NEARBY_HOT=0x0007;
    public final int TYPE_NEARBY_HOT_SELCTION=0x0008;

    //default tiltes group by 0~3,4~6,7~8
    private String [] titles=new String[] {"搜索Po文,用户,话题",
                                            "所有人",
                                            "附近",
                                            "圈子",
                                            "#ANDROID",
                                            "#摄影",
                                            "#美食",
                                            "#考试求过啊",
                                            "#倒数第二排妹子好漂亮的说"};
    private int [] icons=new int[] {R.drawable.ic_action_search,R.drawable.ic_action_everyone,
                                    R.drawable.ic_action_nearby,R.drawable.ic_action_circle};

    //recycler item click listener
    private OnItemClickListener mListener;

    //field refer current item
    private int currentPosition=1;

    @Override
    public int getItemCount() {
        return titles.length;
    }

    @Override
    public int getItemViewType(int position) {
        if(position>=0&&position<4) {
            return TYPE_RANGE;
        }else if(position==4) {
            return TYPE_HOT_TOPIC_SELECTION;
        }else if(position>4&&position<7) {
            return TYPE_HOT_TOPIC;
        }else if(position==7) {
            return TYPE_NEARBY_HOT_SELCTION;
        }else {
            return TYPE_NEARBY_HOT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        RecyclerView.ViewHolder holder=null;
        View view;
        switch(viewType) {
            case TYPE_RANGE:
                view= LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fragment_share_top_menu_recycler_range_layout, parent, false);
                holder=new RangeViewHolder(view);
                break;
            case TYPE_HOT_TOPIC_SELECTION:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fragment_share_top_menu_recycler_explore_selection_layout, parent, false);
                holder=new ExploreSelectionViewHolder(view);
                break;
            case TYPE_HOT_TOPIC:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fragment_share_top_menu_recycler_explore_layout, parent, false);
                holder=new ExploreViewHolder(view);
                break;
            case TYPE_NEARBY_HOT_SELCTION:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fragment_share_top_menu_recycler_nearby_hot_selection_layout, parent, false);
                holder=new NearbyHotSelectionViewHolder(view);
                break;
            case TYPE_NEARBY_HOT:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fragment_share_top_menu_recycler_nearby_hot_layout, parent, false);
                holder=new NearbyHotViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch(getItemViewType(position)) {
            case TYPE_RANGE:
                if(position==currentPosition) {
                    ((RangeViewHolder)holder).tick.setVisibility(View.VISIBLE);
                }else {
                    ((RangeViewHolder)holder).tick.setVisibility(View.GONE);
                }
                ((RangeViewHolder)holder).rangeText.setText(titles[position]);
                ((RangeViewHolder)holder).icon.setImageResource(icons[position]);
                if(mListener!=null) {
                    ((RangeViewHolder)holder).container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onItemClick(position,getItemViewType(position));
                        }
                    });
                }
                break;
            case TYPE_HOT_TOPIC_SELECTION:
                if(position==currentPosition) {
                    ((ExploreSelectionViewHolder)holder).tick.setVisibility(View.VISIBLE);
                }else {
                    ((ExploreSelectionViewHolder)holder).tick.setVisibility(View.GONE);
                }
                ((ExploreSelectionViewHolder)holder).topicText.setText(titles[position]);
                if(mListener!=null) {
                    ((ExploreSelectionViewHolder)holder).container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onItemClick(position,getItemViewType(position));
                        }
                    });
                }
                break;
            case TYPE_HOT_TOPIC:
                if(position==currentPosition) {
                    ((ExploreViewHolder)holder).tick.setVisibility(View.VISIBLE);
                }else {
                    ((ExploreViewHolder)holder).tick.setVisibility(View.GONE);
                }
                ((ExploreViewHolder)holder).topicText.setText(titles[position]);
                if(mListener!=null) {
                    ((ExploreViewHolder)holder).container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onItemClick(position,getItemViewType(position));
                        }
                    });
                }
                break;
            case TYPE_NEARBY_HOT_SELCTION:
                if(position==currentPosition) {
                    ((NearbyHotSelectionViewHolder)holder).tick.setVisibility(View.VISIBLE);
                }else {
                    ((NearbyHotSelectionViewHolder)holder).tick.setVisibility(View.GONE);
                }
                ((NearbyHotSelectionViewHolder)holder).hotText.setText(titles[position]);
                if(mListener!=null) {
                    ((NearbyHotSelectionViewHolder)holder).container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onItemClick(position,getItemViewType(position));
                        }
                    });
                }
                break;
            case TYPE_NEARBY_HOT:
                if(position==currentPosition) {
                    ((NearbyHotViewHolder)holder).tick.setVisibility(View.VISIBLE);
                }else {
                    ((NearbyHotViewHolder)holder).tick.setVisibility(View.GONE);
                }
                ((NearbyHotViewHolder)holder).hotText.setText(titles[position]);
                if(mListener!=null) {
                    ((NearbyHotViewHolder)holder).container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onItemClick(position,getItemViewType(position));
                        }
                    });
                }
                break;
        }
    }

    public static interface OnItemClickListener {
        public void onItemClick(int position,int viewType);
    }

    public void setListener(OnItemClickListener listener) {
        this.mListener=listener;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition=currentPosition;
    }

    static class RangeViewHolder extends RecyclerView.ViewHolder {

        public View container;
        public ImageView icon;
        public TextView rangeText;
        public ImageView tick;

        public RangeViewHolder(View view) {
            super(view);
            container=view.findViewById(R.id.main_fragment_share_top_menu_recycler_range_container);
            icon=(ImageView)view.findViewById(R.id.main_fragment_share_top_menu_recycler_range_icon_imageView);
            rangeText=(TextView)view.findViewById(R.id.main_fragment_share_top_menu_recycler_range_title_textView);
            tick=(ImageView)view.findViewById(R.id.main_fragment_share_top_menu_recycler_range_tick_imageView);
        }


    }


    static class ExploreSelectionViewHolder extends RecyclerView.ViewHolder {

        public View container;
        public TextView topicText;
        public ImageView tick;

        public ExploreSelectionViewHolder(View view) {
            super(view);
            container=view.findViewById(R.id.main_fragment_share_top_menu_recyler_explore_selection_container);
            topicText=(TextView)view.findViewById(R.id.main_fragment_share_top_menu_recyler_explore_selection_title_textView);
            tick=(ImageView)view.findViewById(R.id.main_fragment_share_top_menu_recyler_explore_selection_tick_imageView);
        }
    }

    static class ExploreViewHolder extends RecyclerView.ViewHolder {

        public View container;
        public TextView topicText;
        public ImageView tick;

        public ExploreViewHolder(View view) {
            super(view);
            container=view.findViewById(R.id.main_fragment_share_top_menu_recyler_explore_container);
            topicText=(TextView)view.findViewById(R.id.main_fragment_share_top_menu_recyler_explore_title_textView);
            tick=(ImageView)view.findViewById(R.id.main_fragment_share_top_menu_recyler_explore_tick_imageView);
        }

    }

    static class NearbyHotViewHolder extends RecyclerView.ViewHolder {

        public View container;
        public TextView hotText;
        public ImageView tick;

        public NearbyHotViewHolder(View view) {
            super(view);
            container=view.findViewById(R.id.main_fragment_share_top_menu_recyler_nearby_hot_container);
            hotText=(TextView)view.findViewById(R.id.main_fragment_share_top_menu_recyler_nearby_hot_title_textView);
            tick=(ImageView)view.findViewById(R.id.main_fragment_share_top_menu_recyler_nearby_hot_tick_imageView);
        }

    }

    static class NearbyHotSelectionViewHolder extends RecyclerView.ViewHolder {

        public View container;
        public TextView hotText;
        public ImageView tick;

        public NearbyHotSelectionViewHolder(View view) {
            super(view);
            container = view.findViewById(R.id.main_fragment_share_top_menu_recycler_nearby_hot_selection_container);
            hotText = (TextView) view.findViewById(R.id.main_fragment_share_top_menu_recyler_nearby_hot_selection_title_textView);
            tick = (ImageView) view.findViewById(R.id.main_fragment_share_top_menu_recyler_nearby_hot_selection_tick_imageView);
        }
    }
}



