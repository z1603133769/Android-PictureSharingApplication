package com.example.picture_sharing_application;
import android.net.Uri;

import java.io.File;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;

public class _User extends BmobUser {


    private String nickName;
    //头像
    private BmobFile headPicture;
    //头像URL
    private String imagePath;

    private String password_record;

    public String getPassword_record() {
        return password_record;
    }

    public void setPassword_record(String password_record) {
        this.password_record = password_record;
    }

    public BmobFile getHeadPicture() {
        return headPicture;
    }

    public void setHeadPicture(BmobFile headPicture) {
        this.headPicture = headPicture;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
