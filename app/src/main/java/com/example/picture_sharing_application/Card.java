package com.example.picture_sharing_application;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

public class Card extends BmobObject {

    //用户名
    private String username;

    //昵称
    private String nickName;

    //头像
    private BmobFile headPicture;

    //卡片图片
    private BmobFile Picture;

    //文字内容
    private String Description;

    //点赞数
    private Integer LikeNumber;

    //点赞状态
    private Boolean LikeState;

    //分享状态
    private Boolean ShareState;

    /**
     * 一对多关系：用于存储喜欢该帖子的所有用户
     */
    private BmobRelation likes;

    private BmobRelation shares;


    public Card() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public BmobFile getHeadPicture() {
        return headPicture;
    }

    public void setHeadPicture(BmobFile headPicture) {
        this.headPicture = headPicture;
    }

    public BmobFile getPicture() {
        return Picture;
    }

    public void setPicture(BmobFile picture) {
        Picture = picture;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Integer getLikeNumber() {
        return LikeNumber;
    }

    public void setLikeNumber(Integer likeNumber) {
        LikeNumber = likeNumber;
    }

    public Boolean getLikeState() {
        return LikeState;
    }

    public void setLikeState(Boolean likeState) {
        LikeState = likeState;
    }

    public BmobRelation getLikes() {
        return likes;
    }

    public void setLikes(BmobRelation likes) {
        this.likes = likes;
    }

    public BmobRelation getShares(){
        return shares;
    }

    public void setShares(BmobRelation shares){
        this.shares=shares;
    }

    public Boolean getShareState() {
        return ShareState;
    }

    public void setShareState(Boolean shareState) {
        ShareState = shareState;
    }
}