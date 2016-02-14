package com.find.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by maqiang on 2015/2/4.
 */
public class DBHelper extends SQLiteOpenHelper {

    //创建Post表
    private static final String CREATE_POST="create table post(" +
            "_id integer primary key autoincrement," +
            "server_id integer," +
            "post_type tinyint," +
            "user_name varchar," +
            "user_image_path varchar," +
            "user_id integer,"+
            "post_time varchar,"+
            "title varchar," +
            "content text," +
            "image_path varchar,"+
            "tag_id tinyint,"+
            "like_count integer," +
            "comment_count integer," +
            "image_count tinyint," +
            "video_path varchar," +
            "share_count integer)";

    private static final String CREATE_USER="create table user(" +
            "_id integer primary key autoincrement," +
            "server_id integer," +
            "nick_name varchar," +
            "phone varchar," +
            "mail varchar," +
            "post_count integer," +
            "gender tinyint," +
            "longitude float," +
            "latitude float," +
            "image_path varchar," +
            "ip varchar," +
            "circle_count tinyint," +
            "friend_count integer)";

    public DBHelper(Context context,String dbName,int version) {
        super(context,dbName,null,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_POST);
        db.execSQL(CREATE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {

    }
}
