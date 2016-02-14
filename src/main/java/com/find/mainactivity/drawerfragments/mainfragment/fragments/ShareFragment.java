package com.find.mainactivity.drawerfragments.mainfragment.fragments;

//import android.app.Fragment;

import android.os.AsyncTask;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.find.R;
import com.find.mainactivity.MainActivity;
import com.find.mainactivity.drawerfragments.MainFragment;
import com.find.mainactivity.drawerfragments.mainfragment.adapter.ShareAdapter;
import com.find.mainactivity.drawerfragments.mainfragment.adapter.TopMenuAdapter;
import com.find.model.Post;
import com.find.util.DBManager;
import com.find.util.DensityUtil;
import com.find.util.HttpsUtils;
import com.find.util.SDCard;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2014/12/20.
 */
public class ShareFragment extends Fragment {

    private View rootView;
    private ProgressBar mProgressBar;
    //    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private boolean isGlobalCalled = false;   //monitor global layout listener called times

    public static View containerView;
    private boolean isFirstReachTop = true;

    private int lastDy;                     //monitor recyclerView scrolly delta

    private ImageButton writeBtn;
    private boolean isWriteBtnShown = true;

    private int userId = 1;                   //默认当前用户id
    //发请求参数指针
    private int start = 0;
    private int end = 8;

    ShareAdapter mAdapter;

    //顶部menu相关
    RecyclerView mMenuRecyclerView;
    private View topMenu;
    private View menuLayout;
    private ImageView topMenuTriangle;
    //    private boolean isMenuShown=true;
    private boolean isMenuContentShown = false;
    //TopMenu高度
    private int topMenuHeight;
    private boolean isMenuLayoutGlobalFirst = true;

    //刷新按钮
    private ImageButton refreshBtn;
    //上拉加载是否调用flag
    private boolean isLoadCalled=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main_fragment_share_layout, container, false);

        //init ProgressBar
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.main_fragment_share_progressBar);

        writeBtn = (ImageButton) rootView.findViewById(R.id.main_fragment_share_write_btn);
        initTopMenu();
        initRecyclerView();
//        initTopMenu();

        //refresh 按钮更新数据库
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start=0;
                end=8;

                mProgressBar.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
//                mAdapter.getList().clear();
//                mAdapter.notifyDataSetChanged();

                new RefreshTask().execute(HttpsUtils.BASE_URL+HttpsUtils.POSTSERVLET);
                isLoadCalled=false;
            }
        });

        //若数据库中没有数据缓存，则从网络中获取，有则从数据库中获取
        if(DBManager.getInstance(getActivity()).getPostCount()==0) {
            new GetPosts().execute(HttpsUtils.BASE_URL+HttpsUtils.POSTSERVLET);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            },2000);
            List<Post> postList=DBManager.getInstance(getActivity()).getPosts();
            for(int i=0;i<postList.size();i++) {
                if(i==0) {
                    mAdapter.getList().add(0,postList.get(i));
                }else {
                    mAdapter.getList().add(postList.get(i));
                }

            }
            mAdapter.notifyDataSetChanged();
            for(int i=0;i<mAdapter.getList().size();i++) {
                String [] pathArray=mAdapter.getList().get(i).getImagePath().split(",");
                for(String path:pathArray) {
                    new GetImageTask().execute(HttpsUtils.BASE_URL+"/"+path);
                }

                new GetUserImage().execute(HttpsUtils.BASE_URL+"/"+mAdapter.getList().get(i).getUserImagePath());
            }
        }

        //检查是否有post文更新
        new CheckUpdate().execute(HttpsUtils.BASE_URL+HttpsUtils.POSTSERVLET);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(SDCard.isSDCardExist()) {
            for(Post post:mAdapter.getList()) {
                String []imagePaths=post.getImagePath().split(",");
                for(String path:imagePaths) {
                    File file=new File(SDCard.SD_ROOT()+"/photos/"+path.replace("/",""));
                    if(file.exists()) {
                        file.delete();
                    }
                }

                File userImageFile=new File(SDCard.SD_ROOT()+"/photos/"+post.getUserImagePath().replace("/",""));
                if(userImageFile.exists()) {
                    userImageFile.delete();
                }
            }
        }

    }

    //init top menu and menu recyclerView
    private void initTopMenu() {
        topMenu = rootView.findViewById(R.id.main_fragment_share_top_menu_layout);
        menuLayout = rootView.findViewById(R.id.main_fragment_share_menu_recyclerView_layout);
        mMenuRecyclerView = (RecyclerView) rootView.findViewById(R.id.main_fragment_share_menu_mRecylerView);
        topMenuTriangle = (ImageView) rootView.findViewById(R.id.main_fragment_share_triangle_imageView);
//        containerView=findViewById(R.id.main_activiy_share_container_layout);
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        final TopMenuAdapter mAdapter = new TopMenuAdapter();

        mMenuRecyclerView.setLayoutManager(mManager);
        mMenuRecyclerView.setAdapter(mAdapter);

        topMenu.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (isMenuLayoutGlobalFirst) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            DensityUtil.dip2px(getActivity(), 48));
                    params.setMargins(0, MainFragment.headerHeight, 0, 0);
                    topMenu.setLayoutParams(params);
                    RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    lParams.setMargins(0, MainFragment.headerHeight + topMenuHeight, 0, 0);
                    menuLayout.setLayoutParams(lParams);
                    isMenuLayoutGlobalFirst = false;
                }
            }
        });

        topMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMenuContentShown) {
//                    containerView.setVisibility(View.GONE);
                    menuLayout.setVisibility(View.VISIBLE);
                    //make top menu shown with alpha animation
                    AlphaAnimation anim = new AlphaAnimation(0f, 1.0f);
                    anim.setDuration(300);
//                    anim.setFillAfter(true);
                    menuLayout.startAnimation(anim);
                    isMenuContentShown = true;
                    topMenuTriangle.setImageResource(R.drawable.triangle_up);

                } else {
                    menuLayout.setVisibility(View.GONE);
                    topMenuTriangle.setImageResource(R.drawable.triangle_down);
                    isMenuContentShown = false;
//                    containerView.setVisibility(View.VISIBLE);
                }
            }
        });
        //set top menu recyclerview item click listener
        mAdapter.setListener(new TopMenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, int viewType) {
                mAdapter.setCurrentPosition(position);
                mAdapter.notifyDataSetChanged();
                menuLayout.setVisibility(View.GONE);
                ShareFragment.containerView.setVisibility(View.VISIBLE);
                isMenuContentShown = false;
                topMenuTriangle.setImageResource(R.drawable.triangle_down);
//                mProgressBar.setVisibility(View.VISIBLE);
//                mSwipeRefreshLayout.setVisibility(View.GONE);
            }
        });
    }


    //init SwipeRefreshLayout and recyclerView
    private void initRecyclerView() {

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.main_fragment_share_recyclerView);
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mManager);
        refreshBtn=(ImageButton)rootView.findViewById(R.id.main_fragment_share_refresh_btn);
        mAdapter = new ShareAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        containerView = rootView.findViewById(R.id.main_fragment_share_container_layout);

        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!isGlobalCalled) {
                    topMenuHeight = topMenu.getHeight();
                    mRecyclerView.setPadding(0, MainFragment.headerHeight + topMenuHeight, 0, 0);
                    isGlobalCalled = true;
                    lastDy = MainFragment.headerHeight + topMenuHeight;
                }
            }
        });

        //圆形按钮动画
        final TranslateAnimation hideAnim = new TranslateAnimation(0, 0, 0, MainActivity.windowHeight);
        hideAnim.setDuration(300);
        hideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                writeBtn.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        final TranslateAnimation showAnim = new TranslateAnimation(0, 0, MainActivity.windowHeight, 0);
        showAnim.setDuration(300);
        showAnim.setFillAfter(true);
        showAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                writeBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //TopMenu隐藏动画
        final AlphaAnimation topMenuHideAnim = new AlphaAnimation(1.0f, 0.0f);
        topMenuHideAnim.setDuration(300);
//                    topMenuHideAnim.setFillAfter(true);
        topMenuHideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                topMenu.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //TopMenu出现动画
        final AlphaAnimation menuShowAnim = new AlphaAnimation(0.0f, 1.0f);
        menuShowAnim.setDuration(300);
        menuShowAnim.setFillAfter(true);
        menuShowAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                topMenu.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                Log.v("scroll y",recyclerView.getLayoutManager().getChildAt(0).getTop()+"");
                //处理recyclerView滚动动画效果
                if ((isFirstReachTop && recyclerView.getLayoutManager().getChildAt(0).getTop() < 0)
                        || (dy > 10 && !isFirstReachTop)) {
                    MainFragment.mHandler.sendEmptyMessage(MainFragment.HEADER_HIDE_MESSAGE);
                    isFirstReachTop = false;

                    if (isWriteBtnShown) {
                        writeBtn.startAnimation(hideAnim);
                        isWriteBtnShown = false;
                        topMenu.startAnimation(topMenuHideAnim);
                    }
                } else if (dy < -10) {
                    MainFragment.mHandler.sendEmptyMessage(MainFragment.HEADER_SHOW_MESSAGE);
                    if (!isWriteBtnShown) {
                        writeBtn.startAnimation(showAnim);
                        isWriteBtnShown = true;
                        topMenu.startAnimation(menuShowAnim);
                    }
                }

                //handle the recyclerView scrollAnimation
                if ((dy > 0 && recyclerView.getPaddingTop() > 0)
                        || (dy < 0 && recyclerView.getLayoutManager().getChildAt(0).getTop() >= 0)) {
                    lastDy = lastDy - dy;
                }
                if (lastDy < 0) {
                    lastDy = 0;
                } else if (lastDy >= MainFragment.headerHeight + topMenuHeight) {
                    lastDy = MainFragment.headerHeight += topMenuHeight;
                    isFirstReachTop = true;
                }
                recyclerView.setPadding(0, lastDy, 0, 0);

                View lastSecond= mRecyclerView.getLayoutManager().getChildAt(mAdapter.getItemCount()-2);

                if(lastSecond!=null&&dy>0&&!isLoadCalled) {
                    //此处调用继续加载接口
                    Log.v("info","继续加载");
                    start=DBManager.getInstance(getActivity()).getPostCount();
                    end=start+8;
                    new GetPosts().execute(HttpsUtils.BASE_URL+HttpsUtils.POSTSERVLET);
                    isLoadCalled=true;
                }

            }
        });


    }

    //异步发送post请求
    private class GetPosts extends AsyncTask<String, Void, String> {

        @Override
        public String doInBackground(String... params) {
            String result = null;

            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("key", "getPosts");
            paramsMap.put("userId", String.valueOf(userId));
            paramsMap.put("start", String.valueOf(start));
            paramsMap.put("end", String.valueOf(end));

            try {
                result = HttpsUtils.doPost(params[0], paramsMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        public void onPostExecute(String result) {
            if (result != null) {
                mProgressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String info = jsonObject.getString("info");
                    String listSize = jsonObject.getString("listSize");
                    if (info.equals("success") && listSize.equals("0")) {
                        Toast.makeText(getActivity(), "哎，没找到相应的Post文", Toast.LENGTH_SHORT).show();
                    } else if (info.equals("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObj = jsonArray.getJSONObject(i);
                            int id = jsonObj.getInt("id");
                            String title = jsonObj.getString("title");
                            String content = jsonObj.getString("content");
                            if (content == null) {
                                content = "";
                            }
                            int postType = jsonObj.getInt("postType");
                            int userId = jsonObj.getInt("userId");
                            String imagePath = jsonObj.getString("imagePath");
                            int tagId = jsonObj.getInt("tagId");
                            int likeCount = jsonObj.getInt("likeCount");
                            int commentCount = jsonObj.getInt("commentCount");
                            int imageCount = jsonObj.getInt("imageCount");
                            String videoPath = jsonObj.getString("videoPath");
                            int shareCount = jsonObj.getInt("shareCount");
                            String postTime = jsonObj.getString("postTime");
                            String userImagePath=jsonObj.getString("userImagePath");
                            String userName=jsonObj.getString("userName");
                            Post post = new Post.Builder(id,userId,userName,postTime).title(title).content(content)
                                    .imageCount(imageCount).likeCount(likeCount).shareCount(shareCount)
                                    .tagId(tagId).commentCount(commentCount).imagePath(imagePath).videoPath(videoPath).postType(postType)
                                    .userImagePath(userImagePath)
                                    .build();
                            if(DBManager.getInstance(getActivity()).getPostCount()<80) {
                                //插入到数据库中缓存
                                Log.v("postId",post.getId()+"");
                                DBManager.getInstance(getActivity()).addData(post);
                            }

                            //将一个Post卡片插入到最前面
                            if (i == 0) {
                                mAdapter.getList().add(0, post);
                            } else {
                                mAdapter.getList().add(post);
                            }

                            String [] pathArray=imagePath.split(",");
                            for(String path:pathArray) {
                                new GetImageTask().execute(HttpsUtils.BASE_URL+"/"+path);
                            }

                            new GetUserImage().execute(HttpsUtils.BASE_URL+"/"+userImagePath);
                        }
                        mAdapter.notifyDataSetChanged();

                        isLoadCalled=false;


                    } else if (info.equals("fail")) {
                        Toast.makeText(getActivity(), "诶，没找到相应的Post文", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mProgressBar.setVisibility(View.GONE);
            } else {
                try {
                    Toast.makeText(getActivity(), "诶，网络君好像开了小差", Toast.LENGTH_SHORT).show();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //检查是否有post文更新
    private class CheckUpdate extends AsyncTask<String,Void,String> {
        @Override
        public String doInBackground(String...params) {
            String result=null;
            try {
                Map<String,String> paramMap=new HashMap<>();
                paramMap.put("key","checkForUpdate");
                paramMap.put("userId",String.valueOf(userId));

                result=HttpsUtils.doPost(params[0],paramMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        public void onPostExecute(String result) {
            if(result!=null) {
                try {
                    JSONObject jsonObj=new JSONObject(result);
                    String info=jsonObj.getString("info");
                    if(info.equals("success")) {
                        int postId=jsonObj.getInt("postId");
                        List<Post> postList=DBManager.getInstance(getActivity()).getPosts();
                        for(int i=0;i<postList.size();i++) {
                            if(i==0&&postId!=postList.get(i).getId()&&postList.get(i).getUserId()!=userId
                                    ||i==1&&postList.get(0).getUserId()==userId&&postList.get(1).getId()!=postId) {
                                Toast.makeText(getActivity(),"有新的post文，请点击刷新按钮刷新",Toast.LENGTH_LONG).show();
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 异步任务获取图片
     */
    private class GetImageTask extends AsyncTask<String,Void,Void> {
        @Override
        public Void doInBackground(String...params) {
            InputStream is=null;
            try{
                File fileFolder=new File(SDCard.SD_ROOT()+"/photos");
                fileFolder.mkdirs();
                File file=new File(fileFolder.getCanonicalPath()+"/"+params[0].replace(HttpsUtils.BASE_URL,"").replace("/", "").replace(":", ""));
                if(file.exists()) {
                    return null;
                }
                is=HttpsUtils.doGet(params[0],null);
                if(is!=null) {
                    if(SDCard.isSDCardExist()) {
                        if(file.createNewFile()) {
                            FileOutputStream fos=new FileOutputStream(file);

                            byte [] buffer=new byte[1024];
                            int hasRead;
                            while((hasRead=is.read(buffer))!=-1) {
                                fos.write(buffer,0,hasRead);
                            }

                            fos.close();
                        }
                    }
                }
            }catch(Exception e) {
                e.printStackTrace();
            } finally {
                try{
                    if(is!=null) {
                        is.close();
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 异步任务获取用户头像
     */
    private class GetUserImage extends AsyncTask<String,Void,Void> {
        @Override
        public Void doInBackground(String...params) {

            InputStream is=null;
            try {
                File fileFolder=new File(SDCard.SD_ROOT()+"/photos");
                fileFolder.mkdirs();
                File file=new File(fileFolder.getCanonicalPath()+"/"+params[0].replace(HttpsUtils.BASE_URL,"").replace("/","")
                .replace(":",""));
                if(file.exists()) {
                    return null;
                }
                is=HttpsUtils.doGet(params[0],null);
                if(is!=null) {
                    if(file.createNewFile()) {
                        byte [] buffer=new byte[1024];
                        int hasRead=0;
                        OutputStream os=new FileOutputStream(file);

                        while((hasRead=is.read(buffer))!=-1) {
                            os.write(buffer,0,hasRead);
                        }

                        os.flush();
                        is.close();
                        os.close();
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if(is!=null) {
                    try {
                        is.close();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 更新时调用异步任务
     */
    private class RefreshTask extends AsyncTask<String,Void,String> {
        @Override
        public String doInBackground(String... params) {
            String result = null;

            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("key", "getPosts");
            paramsMap.put("userId", String.valueOf(userId));
            paramsMap.put("start", String.valueOf(start));
            paramsMap.put("end", String.valueOf(end));

            try {
                result = HttpsUtils.doPost(params[0], paramsMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        public void onPostExecute(String result) {
            if (result != null) {
                mProgressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String info = jsonObject.getString("info");
                    String listSize = jsonObject.getString("listSize");
                    if (info.equals("success") && listSize.equals("0")) {
                        Toast.makeText(getActivity(), "哎，没找到相应的Post文", Toast.LENGTH_SHORT).show();
                    } else if (info.equals("success")) {
                        DBManager.getInstance(getActivity()).clear("post");
                        mAdapter.getList().clear();
                        //加入Explore卡片
                        Post post0=new Post.Builder(0,0,"","").postType(-1).build();
                        mAdapter.getList().add(post0);
                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObj = jsonArray.getJSONObject(i);
                            int id = jsonObj.getInt("id");
                            String title = jsonObj.getString("title");
                            String content = jsonObj.getString("content");
                            if (content == null) {
                                content = "";
                            }
                            int postType = jsonObj.getInt("postType");
                            int userId = jsonObj.getInt("userId");
                            String imagePath = jsonObj.getString("imagePath");
                            int tagId = jsonObj.getInt("tagId");
                            int likeCount = jsonObj.getInt("likeCount");
                            int commentCount = jsonObj.getInt("commentCount");
                            int imageCount = jsonObj.getInt("imageCount");
                            String videoPath = jsonObj.getString("videoPath");
                            int shareCount = jsonObj.getInt("shareCount");
                            String postTime = jsonObj.getString("postTime");
                            String userImagePath=jsonObj.getString("userImagePath");
                            String userName=jsonObj.getString("userName");
                            Post post = new Post.Builder(id,userId,userName,postTime).title(title).content(content)
                                    .imageCount(imageCount).likeCount(likeCount).shareCount(shareCount)
                                    .tagId(tagId).commentCount(commentCount).imagePath(imagePath).videoPath(videoPath).postType(postType)
                                    .userImagePath(userImagePath)
                                    .build();
                            if(DBManager.getInstance(getActivity()).getPostCount()<80) {

                                DBManager.getInstance(getActivity()).addData(post);
                            }

                            //将一个Post卡片插入到最前面
                            if (i == 0) {
                                mAdapter.getList().add(0, post);
                            } else {
                                mAdapter.getList().add(post);
                            }

                            String [] pathArray=imagePath.split(",");
                            for(String path:pathArray) {
                                new GetImageTask().execute(HttpsUtils.BASE_URL+"/"+path);
                            }

                            new GetUserImage().execute(HttpsUtils.BASE_URL+"/"+userImagePath);
                        }
                        mAdapter.notifyDataSetChanged();

                        isLoadCalled=false;


                    } else if (info.equals("fail")) {
                        Toast.makeText(getActivity(), "诶，没找到相应的Post文", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mProgressBar.setVisibility(View.GONE);
            } else {
                try {
                    Toast.makeText(getActivity(), "诶，网络君好像开了小差", Toast.LENGTH_SHORT).show();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
