package com.example.picture_sharing_application;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Card {

    @Expose(serialize = false, deserialize = false)
    private Integer mId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("picUrl")
    private String mPicUrl;

    @SerializedName("url")
    private String mContentUrl;

    @SerializedName("ctime")
    private String mPublishTime;

    @SerializedName("nickname")
    private String mNickName;

    @SerializedName("copyright")
    private String mLikeNumber;

//    @SerializedName("picUrl")
    private String mHeadPic;

    public Card() {
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getPicUrl() {
        return mPicUrl;
    }

    public void setPicUrl(String mPicUrl) {
        this.mPicUrl = mPicUrl;
    }

    public String getContentUrl() {
        return mContentUrl;
    }

    public void setContentUrl(String mContentUrl) {
        this.mContentUrl = mContentUrl;
    }

    public Integer getId() {
        return mId;
    }

    public String getDate() {
        return mPublishTime;
    }

    public String getNickName() {
        return mNickName;
    }

    public void setNickName(String NickName) {
        this.mNickName = NickName;
    }

    public String getLikeName() {
        return mLikeNumber;
    }

    public void setLikeName(String LikeNumber) {
        this.mLikeNumber = LikeNumber;
    }

    public String getHeadPic() {
        return mHeadPic;
    }

    public void setHeadPic(String HeadPic) {
        this.mHeadPic = HeadPic;
    }

    public void updateData(){
        this.mDescription = "#"+this.mDescription+"#";
    }
}
