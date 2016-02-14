package com.find.mainactivity.drawerfragments.mainfragment.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.find.R;
import com.find.model.Post;
import com.find.util.BitmapUtils;
import com.find.util.DensityUtil;
import com.find.util.HttpsUtils;
import com.find.util.SDCard;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by maqiang on 2015/1/1.
 */
public class ShareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    //Post维护队列
    private List<Post> postList=new ArrayList<>();

    private final int TYPE_ONE_PICTURE=1;
    private final int TYPE_NO_PICTURE=0;
    private final int TYPE_MULTIPLE_PICTURE=2;
    private final int TYPE_URL=3;
    private final int TYPE_VIDEO=4;

    //发现类型
    private final int TYPE_EXPLORE=-1;
    private int lastPosition=-1;

    public ShareAdapter(Context context) {
        //加入Explore卡片
        Post post=new Post.Builder(0,0,"","").postType(-1).build();
        postList.add(post);
        mContext=context;
    }

    public List<Post> getList() {
        return postList;
    }

    @Override
    public int getItemViewType(int position) {
        return postList.get(position).getPostType();
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View view;
        RecyclerView.ViewHolder holder=null;
        switch(viewType) {
            case TYPE_ONE_PICTURE:
                view= LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fragment_share_card_post_one_picture_layout,parent,false);
                holder=new OnePictureViewHolder(view);
                break;
            case TYPE_NO_PICTURE:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fragment_share_card_post_one_picture_layout,parent,false);
                holder=new OnePictureViewHolder(view);
                break;
            case TYPE_MULTIPLE_PICTURE:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fragment_share_card_post_multiple_layout,parent,false);
                holder=new MultiplePictureViewHolder(view);
                break;
            case TYPE_EXPLORE:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fragment_share_explore_card_layout,parent,false);
                holder=new ExploreViewHolder(view);
                break;
            case TYPE_URL:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fragment_share_card_post_url_layout,parent,false);
                holder=new URLViewHolder(view);
                break;
            case TYPE_VIDEO:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fragment_share_card_post_video_layout,parent,false);
                holder=new VideoViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        String title;
        switch(getItemViewType(position)) {
            case TYPE_NO_PICTURE:
                ((OnePictureViewHolder)holder).contentImage.setVisibility(View.GONE);
                setItemAnimation(((OnePictureViewHolder)holder).root,position);
                ((OnePictureViewHolder)holder).userNameText.setText(postList.get(position).getUserName());
                ((OnePictureViewHolder)holder).timeText.setText(postList.get(position).getPostTime());
                title=postList.get(position).getTitle();
                if(title.equals("")) {
                    ((OnePictureViewHolder)holder).titleText.setVisibility(View.GONE);
                }else {
                    ((OnePictureViewHolder)holder).titleText.setText(title);
                }
                ((OnePictureViewHolder)holder).contentText.setText(postList.get(position).getContent());
                ((OnePictureViewHolder)holder).likeCountText.setText(String.valueOf(postList.get(position).getLikeCount()));
                ((OnePictureViewHolder)holder).commentCountText.setText(String.valueOf(postList.get(position).getLikeCount()));
                break;
            case TYPE_ONE_PICTURE:
                setItemAnimation(((OnePictureViewHolder)holder).root,position);

                ((OnePictureViewHolder)holder).userNameText.setText(postList.get(position).getUserName());
                ((OnePictureViewHolder)holder).timeText.setText(postList.get(position).getPostTime());
                title=postList.get(position).getTitle();
                if(title.equals("")) {
                    ((OnePictureViewHolder)holder).titleText.setVisibility(View.GONE);
                }else {
                    ((OnePictureViewHolder)holder).titleText.setText(title);
                }
                ((OnePictureViewHolder)holder).contentText.setText(postList.get(position).getContent());
                ((OnePictureViewHolder)holder).likeCountText.setText(String.valueOf(postList.get(position).getLikeCount()));
                ((OnePictureViewHolder)holder).commentCountText.setText(String.valueOf(postList.get(position).getCommentCount()));

                if(new File(SDCard.SD_ROOT()+"/photos/"+postList.get(position).getImagePath().replace("/", "")).exists()) {
                    //获取bitmap的宽高
                    BitmapFactory.Options options=new BitmapFactory.Options();
                    options.inJustDecodeBounds=true;
                    BitmapFactory.decodeFile(SDCard.SD_ROOT()+"/photos/"+postList.get(position).getImagePath().replace("/", ""),options);
                    ((OnePictureViewHolder)holder).contentImage.getLayoutParams().height=options.outHeight;
//                Log.v("image height",position+" "+options.outHeight);
//                RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,options.outHeight);
//                ((OnePictureViewHolder)holder).contentImage.setLayoutParams(params);
//                ((OnePictureViewHolder)holder).contentImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    int totalWidth=mContext.getResources().getDisplayMetrics().widthPixels;
                    int imageWidth=totalWidth-DensityUtil.dip2px(mContext,16);
                    //从sdcard中读取图片
                    BitmapUtils.loadBitmap(mContext, ((OnePictureViewHolder) holder).contentImage,
                            SDCard.SD_ROOT() + "/photos/" + postList.get(position).getImagePath().replace("/", ""),
                            imageWidth,
                            options.outHeight);
                    Log.v("bitmap width height", imageWidth + " " + options.outHeight);
                }


                BitmapUtils.loadBitmap(mContext,((OnePictureViewHolder)holder).userImageView,
                        SDCard.SD_ROOT()+"/photos/"+postList.get(position).getUserImagePath().replace("/",""),
                        DensityUtil.dip2px(mContext,48),
                        DensityUtil.dip2px(mContext,48));
                break;
            case TYPE_MULTIPLE_PICTURE:
                setItemAnimation(((MultiplePictureViewHolder)holder).root,position);

                ((MultiplePictureViewHolder)holder).userNameText.setText(postList.get(position).getUserName());
                ((MultiplePictureViewHolder)holder).timeText.setText(postList.get(position).getPostTime());
                title=postList.get(position).getTitle();
                if(title.equals("")) {
                    ((MultiplePictureViewHolder)holder).titleText.setVisibility(View.GONE);
                }else {
                    ((MultiplePictureViewHolder)holder).titleText.setText(title);
                }
                ((MultiplePictureViewHolder)holder).contentText.setText(postList.get(position).getContent());
                ((MultiplePictureViewHolder)holder).likeCountText.setText(String.valueOf(postList.get(position).getLikeCount()));
                ((MultiplePictureViewHolder)holder).commentCountText.setText(String.valueOf(postList.get(position).getCommentCount()));

               String [] imagePaths=postList.get(position).getImagePath().split(",");

                ((MultiplePictureViewHolder)holder).container.removeAllViews();
                for(int i=0;i< imagePaths.length;i++) {
                    ImageView imageView=new ImageView(mContext);

                    if(new File(SDCard.SD_ROOT()+"/photos/"+imagePaths[i].replace("/", "")).exists()) {
                        //获取bitmap的宽高
                        BitmapFactory.Options options0=new BitmapFactory.Options();
                        options0.inJustDecodeBounds=true;
                        BitmapFactory.decodeFile(SDCard.SD_ROOT()+"/photos/"+imagePaths[0].replace("/", ""),options0);
                        int windowWidth=mContext.getResources().getDisplayMetrics().widthPixels;
                        int bitmapWidth=options0.outWidth;
                        if(bitmapWidth>windowWidth) {
                            bitmapWidth=windowWidth-120;
                        }
                        LinearLayout.LayoutParams params0=new LinearLayout.LayoutParams(bitmapWidth,
                                options0.outHeight);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        if(i>0) {
                            params0.setMargins(8,0,0,0);
                        }
                        imageView.setLayoutParams(params0);
                        BitmapUtils.loadBitmap(mContext, imageView,
                                SDCard.SD_ROOT() + "/photos/" + imagePaths[i].replace("/", ""),
                                bitmapWidth,
                                options0.outHeight);
                        ((MultiplePictureViewHolder)holder).container.addView(imageView);
                    }


                }
//                Log.v("view count",((MultiplePictureViewHolder)holder).container.getChildCount()+"");

                BitmapUtils.loadBitmap(mContext,((MultiplePictureViewHolder)holder).userImageView,
                        SDCard.SD_ROOT()+"/photos/"+postList.get(position).getUserImagePath().replace("/",""),
                        DensityUtil.dip2px(mContext,48),
                        DensityUtil.dip2px(mContext,48));
                break;
            case TYPE_EXPLORE:
                setItemAnimation(((ExploreViewHolder)holder).root,position);
                break;
            case TYPE_URL:
                setItemAnimation(((URLViewHolder)holder).root,position);

                ((URLViewHolder)holder).userNameText.setText(postList.get(position).getUserName());
                ((URLViewHolder)holder).timeText.setText(postList.get(position).getPostTime());
                title=postList.get(position).getTitle();
                if(title.equals("")) {
                    ((URLViewHolder)holder).titleText.setVisibility(View.GONE);
                }else {
                    ((URLViewHolder)holder).titleText.setText(title);
                }

                ((URLViewHolder)holder).likeCountText.setText(String.valueOf(postList.get(position).getLikeCount()));
                ((URLViewHolder)holder).commentCountText.setText(String.valueOf(postList.get(position).getCommentCount()));
                ((URLViewHolder)holder).loadHtml(postList.get(position).getContent());

                ((URLViewHolder)holder).urlLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri=Uri.parse(postList.get(position).getContent());
                        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                        mContext.startActivity(intent);
                    }
                });

                BitmapUtils.loadBitmap(mContext,((URLViewHolder)holder).userImageView,
                        SDCard.SD_ROOT()+"/photos/"+postList.get(position).getUserImagePath().replace("/",""),
                        DensityUtil.dip2px(mContext,48),
                        DensityUtil.dip2px(mContext,48));
                break;
            case TYPE_VIDEO:
                setItemAnimation(((VideoViewHolder)holder).root,position);
                ((VideoViewHolder)holder).userNameText.setText(postList.get(position).getUserName());
                ((VideoViewHolder)holder).timeText.setText(postList.get(position).getPostTime());
                title=postList.get(position).getTitle();
                if(title.equals("")) {
                    ((VideoViewHolder)holder).titleText.setVisibility(View.GONE);
                }else {
                    ((VideoViewHolder)holder).titleText.setText(title);
                }
                ((VideoViewHolder)holder).contentText.setText(postList.get(position).getContent());
                ((VideoViewHolder)holder).likeCountText.setText(String.valueOf(postList.get(position).getLikeCount()));
                ((VideoViewHolder)holder).commentCountText.setText(String.valueOf(postList.get(position).getLikeCount()));

//                Log.v("video path", HttpsUtils.HTTP_BASE_URL+HttpsUtils.POSTSERVLET+"?key=getVideo&postId="+
//                        postList.get(position).getId());
//
               final VideoView videoView=new VideoView(mContext);;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
                        videoView.setLayoutParams(params);
                        ((VideoViewHolder)holder).containerView.addView(videoView);
                    }
                },300);


                ((VideoViewHolder)holder).getThumbnail(HttpsUtils.HTTP_BASE_URL + "/" + postList.get(position).getVideoPath());
//
                ((VideoViewHolder)holder).playImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((VideoViewHolder)holder).playImageView.setVisibility(View.GONE);
                        ((VideoViewHolder)holder).thumbnailImageView.setVisibility(View.GONE);
//                        ((VideoViewHolder)holder).videoView.setVisibility(View.VISIBLE);
//                        ((VideoViewHolder)holder).playVideo(HttpsUtils.HTTP_BASE_URL+"/"+postList.get(position).getVideoPath());

                        try {
                            Uri uri=Uri.parse(HttpsUtils.HTTP_BASE_URL+"/"+postList.get(position).getVideoPath());
                            videoView.setMediaController(new MediaController(mContext));
                            videoView.setVideoURI(uri);
                            videoView.start();
                            videoView.requestFocus();
                        } catch(Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                break;
        }
    }

    //set the recylcerView item animtion
    private void setItemAnimation(View view,int position) {
        if(position>lastPosition) {
            Animation anim= AnimationUtils.loadAnimation(view.getContext(), R.anim.recycler_item_slid_in_bottom);
            view.startAnimation(anim);
            lastPosition=position;
        }
    }

    class OnePictureViewHolder extends RecyclerView.ViewHolder {

        public TextView userNameText;
        public ImageView userImageView;
        public TextView titleText;
        public TextView timeText;
        public TextView contentText;
        public ImageView contentImage;
        public ImageButton likeBtn;
        public TextView likeCountText;
        public ImageButton collectBtn;
        public ImageButton overflowImageBtn;
        public ImageButton shareBtn;
        public ImageButton commentBtn;
        public TextView commentCountText;
        public TextView randomCommentText;

        public View root;
        public OnePictureViewHolder(View view) {
            super(view);
            root=view;
            userNameText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_one_picture_user_name_textView);
            contentImage=(ImageView)view.findViewById(R.id.main_fragment_share_card_post_one_picture_content_imageView);
            userImageView=(ImageView)view.findViewById(R.id.main_fragment_share_card_post_one_picture_user_imageView);
            titleText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_one_picture_title_textView);
            timeText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_one_picture_post_time_textView);
            contentText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_one_picture_content_textView);
            likeBtn=(ImageButton)view.findViewById(R.id.main_fragment_share_card_post_one_picture_like_btn);
            likeCountText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_one_picture_like_count);
            collectBtn=(ImageButton)view.findViewById(R.id.main_fragment_share_card_post_one_picture_collect_btn);
            overflowImageBtn=(ImageButton)view.findViewById(R.id.main_fragment_share_card_post_one_picture_overflow_menu_imageBtn);
            shareBtn=(ImageButton)view.findViewById(R.id.main_fragment_share_card_post_one_picture_share_btn);
            commentBtn=(ImageButton)view.findViewById(R.id.main_fragment_share_card_post_one_picture_comment_btn);
            commentCountText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_one_picture_comment_count);
            randomCommentText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_one_random_comment_textView);
        }
    }

    class MultiplePictureViewHolder extends RecyclerView.ViewHolder {

        public View root;
//        public ViewPager mViewPager;
        public LinearLayout container;

        public TextView userNameText;
        public ImageView userImageView;
        public TextView titleText;
        public TextView timeText;
        public TextView contentText;
        public ImageButton likeBtn;
        public TextView likeCountText;
        public ImageButton collectBtn;
        public ImageButton overflowImageBtn;
        public ImageButton shareBtn;
        public ImageButton commentBtn;
        public TextView commentCountText;
        public TextView randomCommentText;

        public MultiplePictureViewHolder(View view) {
            super(view);
            root=view;
            container=(LinearLayout)view.findViewById(R.id.main_fragment_share_card_post_multiple_picture_container_linearLayout);

            userNameText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_multiple_picture_user_name_textView);
            userImageView=(ImageView)view.findViewById(R.id.main_fragment_share_card_post_multiple_picture_user_imageView);
            titleText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_multiple_picture_title_textView);
            timeText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_multiple_picture_post_time_textView);
            contentText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_multiple_picture_content_textView);
            likeBtn=(ImageButton)view.findViewById(R.id.main_fragment_share_card_post_multiple_picture_like_btn);
            likeCountText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_multiple_picture_like_count);
            collectBtn=(ImageButton)view.findViewById(R.id.main_fragment_share_card_post_multiple_picture_collect_btn);
            overflowImageBtn=(ImageButton)view.findViewById(R.id.main_fragment_share_card_post_multiple_picture_overflow_menu_imageBtn);
            shareBtn=(ImageButton)view.findViewById(R.id.main_fragment_share_card_post_multiple_picture_share_btn);
            commentBtn=(ImageButton)view.findViewById(R.id.main_fragment_share_card_post_multiple_picture_comment_btn);
            commentCountText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_multiple_picture_comment_count);
            randomCommentText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_multiple_random_comment_textView);
        }

    }


    class ExploreViewHolder extends RecyclerView.ViewHolder {

        public View root;
        //各类板块(标签)
        public View food;
        public View photography;
        public View tech;
        public View sport;
        public View comic;
        public View music;
        public View vehicle;
        public View design;
        public View game;

        public ExploreViewHolder(View view) {
            super(view);
            root=view;
            food=view.findViewById(R.id.main_fragment_share_explore_card_food);
            photography=view.findViewById(R.id.main_fragment_share_explore_card_photography);
            tech=view.findViewById(R.id.main_fragment_share_explore_card_tech);
            sport=view.findViewById(R.id.main_fragment_share_explore_card_sport);
            comic=view.findViewById(R.id.main_fragment_share_explore_card_comic);
            music=view.findViewById(R.id.main_fragment_share_explore_card_music);
            vehicle=view.findViewById(R.id.main_fragment_share_explore_card_vehicle);
            design=view.findViewById(R.id.main_fragment_share_explore_card_design);
            game=view.findViewById(R.id.main_fragment_share_explore_card_game);
        }
    }

    class URLViewHolder extends RecyclerView.ViewHolder {

        public TextView userNameText;
        public ImageView userImageView;
        public TextView titleText;
        public TextView timeText;
        public ImageView urlImage;
        public TextView urlTitle;
        public ImageButton likeBtn;
        public TextView likeCountText;
        public ImageButton collectBtn;
        public ImageButton overflowImageBtn;
        public ImageButton shareBtn;
        public ImageButton commentBtn;
        public TextView commentCountText;
        public TextView randomCommentText;

        public View urlLayout;

        public View root;

        public URLViewHolder(View view) {
            super(view);
            root=view;

            urlImage=(ImageView)view.findViewById(R.id.main_fragment_share_card_post_url_content_imageView);
            urlTitle=(TextView)view.findViewById(R.id.main_fragment_share_card_post_url_content_textView);
            urlLayout=view.findViewById(R.id.main_fragment_share_card_post_url_content_layout);

            userNameText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_url_user_name_textView);
            userImageView=(ImageView)view.findViewById(R.id.main_fragment_share_card_post_url_user_imageView);
            titleText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_url_title_textView);
            timeText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_url_post_time_textView);
            likeBtn=(ImageButton)view.findViewById(R.id.main_fragment_share_card_post_url_like_btn);
            likeCountText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_url_like_count);
            collectBtn=(ImageButton)view.findViewById(R.id.main_fragment_share_card_post_url_collect_btn);
            overflowImageBtn=(ImageButton)view.findViewById(R.id.main_fragment_share_card_post_url_overflow_menu_imageBtn);
            shareBtn=(ImageButton)view.findViewById(R.id.main_fragment_share_card_post_url_share_btn);
            commentBtn=(ImageButton)view.findViewById(R.id.main_fragment_share_card_post_url_comment_btn);
            commentCountText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_url_comment_count);
            randomCommentText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_url_random_comment_textView);
        }

        /**
         * 解析链接中的html
         * @param url URL地址
         */
        public void loadHtml(String url) {
            new LoadHtmlText().execute(url);
            new LoadHtmlImage().execute(url);

        }

        private class LoadHtmlText extends AsyncTask<String,Void,String> {
            @Override
            public String doInBackground(String...params) {
                String result=null;
                try {
                    Document doc=Jsoup.parse(new URL(params[0]),8000);
                    result=doc.head().text();
                }catch(Exception e) {
                    e.printStackTrace();
                }
                return result;
            }
            @Override
            public void onPostExecute(String result) {
                if(result!=null) {
                    urlTitle.setText(result);
                }
            }
        }

        private class LoadHtmlImage extends AsyncTask<String,Void,Bitmap> {
            @Override
            public Bitmap doInBackground(String...params) {
                Bitmap bitmap=null;
                try {
                    Document doc=Jsoup.parse(new URL(params[0]),8000);

                    Element link=doc.select("img[src$=.jpg]").first();
                    String imagePath=link.attr("src");

                    URL imageURL=new URL(imagePath);
                    HttpURLConnection conn=(HttpURLConnection)imageURL.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    InputStream is=conn.getInputStream();

                    BitmapFactory.Options options=new BitmapFactory.Options();
                    options.inJustDecodeBounds=true;
                    int sampleSize=BitmapUtils.calculateSampleSize(options,DensityUtil.dip2px(mContext,56),DensityUtil.dip2px(mContext,56));
                    options.inJustDecodeBounds=false;
                    options.inSampleSize=sampleSize;
                    bitmap=BitmapFactory.decodeStream(is,null,options);
                    is.close();
                }catch(Exception e) {
                    e.printStackTrace();
                }
                return bitmap;
            }
            @Override
            public void onPostExecute(Bitmap result) {
                if(result!=null) {
                    urlImage.setImageBitmap(result);
                }
            }
        }
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {

        public TextView userNameText;
        public ImageView userImageView;
        public TextView titleText;
        public TextView timeText;
        public TextView contentText;
        public ImageButton likeBtn;
        public TextView likeCountText;
        public ImageButton collectBtn;
        public ImageButton overflowImageBtn;
        public ImageButton shareBtn;
        public ImageButton commentBtn;
        public TextView commentCountText;
        public TextView randomCommentText;

        public View videoLayout;
//        public VideoView videoView;
//        public ProgressBar progressBar;
        public LinearLayout containerView;
        public ImageView thumbnailImageView;
        public ImageView playImageView;

        public View root;
        public VideoViewHolder(View view) {
            super(view);
            root=view;
            userNameText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_video_user_name_textView);
            userImageView=(ImageView)view.findViewById(R.id.main_fragment_share_card_post_video_user_imageView);
            titleText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_video_title_textView);
            timeText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_video_post_time_textView);
            contentText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_video_content_textView);
            likeBtn=(ImageButton)view.findViewById(R.id.main_fragment_share_card_post_video_like_btn);
            likeCountText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_video_like_count);
            collectBtn=(ImageButton)view.findViewById(R.id.main_fragment_share_card_post_video_collect_btn);
            overflowImageBtn=(ImageButton)view.findViewById(R.id.main_fragment_share_card_post_video_overflow_menu_imageBtn);
            shareBtn=(ImageButton)view.findViewById(R.id.main_fragment_share_card_post_video_share_btn);
            commentBtn=(ImageButton)view.findViewById(R.id.main_fragment_share_card_post_video_comment_btn);
            commentCountText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_video_comment_count);
            randomCommentText=(TextView)view.findViewById(R.id.main_fragment_share_card_post_video_random_comment_textView);

            videoLayout=view.findViewById(R.id.main_fragment_share_card_post_video_layout);
//            videoView=(VideoView)view.findViewById(R.id.main_fragment_share_card_post_video_videoView);
//            progressBar=(ProgressBar)view.findViewById(R.id.main_fragment_share_card_post_video_progressBar);
            containerView=(LinearLayout)view.findViewById(R.id.main_fragment_share_card_post_video_container_layout);
            thumbnailImageView=(ImageView)view.findViewById(R.id.main_fragment_share_card_post_video_thumbnail_imageView);
            playImageView=(ImageView)view.findViewById(R.id.main_fragment_share_card_post_video_play_imageView);
        }

//        public void playVideo(String url) {
//            try {
//                Uri uri=Uri.parse(url);
//                videoView.setMediaController(new MediaController(mContext));
//                videoView.setVideoURI(uri);
//                videoView.start();
//                videoView.requestFocus();
//            } catch(Exception e) {
//                e.printStackTrace();
//            }
//        }

        public void getThumbnail(String url) {
            new GetThumbnailTask().execute(url);
        }

        private class GetThumbnailTask extends AsyncTask<String,Void,Bitmap> {
            @Override
            public Bitmap doInBackground(String...params) {
                Bitmap bitmap=null;
                MediaMetadataRetriever retriever=new MediaMetadataRetriever();
                try {
                    retriever.setDataSource(params[0],new HashMap<String, String>());
                    bitmap=retriever.getFrameAtTime();
                }catch(Exception e) {
                    e.printStackTrace();
                }finally{
                    try{
                        retriever.release();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return bitmap;
            }

            @Override
            public void onPostExecute(Bitmap result) {
                if(result!=null) {
                    thumbnailImageView.setImageBitmap(result);
                }
            }
        }
    }


}