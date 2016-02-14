package com.find.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.find.model.Post;
import com.find.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maqiang on 2015/2/4.
 * 对数据对象操作的封装
 */
public class DBManager {

    private static DBManager manager=null;

    private DBHelper helper;
    private SQLiteDatabase db;

    private DBManager(DBHelper helper) {
        this.helper=helper;
        db=helper.getReadableDatabase();
    }

    public static DBManager getInstance(Context context) {
        if(manager==null) {
            manager=new DBManager(new DBHelper(context,"find.db",1));
        }
        return manager;
    }

    /**
     * 向数据库中insert 数据
     * Object 提供类型转换 Post User
     */
    public void addData(Object object) {
        if(object instanceof Post) {
            Post post=(Post)object;
            db.execSQL("insert into post values(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    new Object[]{post.getId(),post.getPostType(),post.getUserName(),post.getUserImagePath(),
                            post.getUserId(),post.getPostTime(),post.getTitle(),post.getContent(),post.getImagePath(),
                            post.getTagId(),post.getLikeCount(),post.getCommentCount(),post.getImageCount(),
                            post.getVideoPath(),post.getShareCount()});
        }else if(object instanceof User) {

        }
    }

    /**
     * 查询返回List<Post>
     */
    public List<Post> getPosts() {
        Cursor c=db.rawQuery("select * from post",null);
        List<Post> postList=new ArrayList<>();
        while(c.moveToNext()) {
            Post post = new Post.Builder(c.getInt(c.getColumnIndex("server_id")), c.getInt(c.getColumnIndex("user_id")),
                    c.getString(c.getColumnIndex("user_name")),c.getString(c.getColumnIndex("post_time"))).title(c.getString(c.getColumnIndex("title")))
                    .content(c.getString(c.getColumnIndex("content")))
                    .imageCount(c.getInt(c.getColumnIndex("image_count"))).likeCount(c.getInt(c.getColumnIndex("like_count")))
                    .shareCount(c.getInt(c.getColumnIndex("share_count")))
                    .tagId(c.getInt(c.getColumnIndex("tag_id"))).commentCount(c.getInt(c.getColumnIndex("comment_count")))
                    .imagePath(c.getString(c.getColumnIndex("image_path"))).videoPath(c.getString(c.getColumnIndex("video_path")))
                    .postType(c.getInt(c.getColumnIndex("post_type")))
                    .userImagePath(c.getString(c.getColumnIndex("user_image_path")))
                    .build();
            postList.add(post);
        }
        c.close();
        return postList;
    }

    /**
     * 清空表数据
     */
    public void clear(String table) {
        db.execSQL("delete from "+table);
        db.execSQL("update sqlite_sequence set seq=0 where name='"+table+"'");       //设置SQLite表自增长序列为0
    }

    public int getPostCount() {
        Cursor c=db.rawQuery("select count(*) from post",null);
        if(c.moveToFirst()) {
            int count=c.getInt(0);
            c.close();
            return count;
        }
        c.close();
        return 0;
    }

    /**
     * 返回登录的User
     */

    /**
     * 关闭数据库
     */
    public void closeDatabase() {
        if(helper!=null) {
            helper.close();
        }
    }
}
