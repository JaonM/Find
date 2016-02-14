package com.find.model;

/**
 * Created by maqiang on 2015/1/26.
 *
 * 利用Builder模式构造 model Post
 */
public class Post {

    private int id;
    private String title;
    private String content;
    private int userId;
    private String imagePath;
    private int postType;
    private int tagId;
    private int likeCount;
    private int commentCount;
    private int imageCount;
    private String videoPath;
    private int shareCount;
    private String postTime;
    private String userImagePath;
    private String userName;

    public static class Builder {
        private int id;
        private String title="";
        private String content="";
        private int postType=0;
        private int userId;
        private String imagePath="";
        private int tagId=0;
        private int likeCount=1;
        private int commentCount=0;
        private int imageCount=0;
        private String videoPath="";
        private int shareCount=0;
        private String postTime;
        private String userImagePath="";
        private String userName;

        public Builder(int id,int userId,String userName,String postTime) {
            this.id=id;
            this.userId=userId;
            this.userName=userName;
            this.postTime=postTime;
        }

        public Builder title(String val) {
            this.title=val;
            return this;
        }
        public Builder content(String val) {
            this.content=val;
            return this;
        }
        public Builder imagePath(String val) {
            this.imagePath=val;
            return this;
        }
        public Builder tagId(int val) {
            this.tagId=val;
            return this;
        }
        public Builder likeCount(int val) {
            this.likeCount=val;
            return this;
        }
        public Builder commentCount(int val) {
            this.commentCount=val;
            return this;
        }
        public Builder imageCount(int val) {
            this.imageCount=val;
            return this;
        }
        public Builder videoPath(String val) {
            this.videoPath=val;
            return this;
        }
        public Builder shareCount(int val) {
            this.shareCount=val;
            return this;
        }
        public Builder postType(int val) {
            this.postType=val;
            return this;
        }
        public Builder userImagePath(String val){
            this.userImagePath=val;
            return this;
        }

        public Post build() {
            return new Post(this);
        }
    }

    private Post(Builder builder) {
        this.id=builder.id;
        this.title=builder.title;
        this.content=builder.content;
        this.userId=builder.userId;
        this.postType=builder.postType;
        this.imagePath=builder.imagePath;
        this.tagId=builder.tagId;
        this.likeCount=builder.likeCount;
        this.commentCount=builder.commentCount;
        this.imageCount=builder.imageCount;
        this.videoPath=builder.videoPath;
        this.shareCount=builder.shareCount;
        this.postTime=builder.postTime;
        this.userImagePath=builder.userImagePath;
        this.userName=builder.userName;
    }

    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public int getUserId() {
        return userId;
    }
    public String getImagePath() {
        return imagePath;
    }
    public int getTagId() {
        return tagId;
    }
    public int getLikeCount() {
        return likeCount;
    }
    public int getCommentCount() {
        return commentCount;
    }
    public int getImageCount() {
        return imageCount;
    }
    public String getVideoPath() {
        return videoPath;
    }
    public int getShareCount() {
        return shareCount;
    }
    public String getPostTime() {
        return postTime;
    }
    public int getPostType() {return postType;}
    public String getUserImagePath(){return userImagePath;}
    public String getUserName(){return userName;}
}
