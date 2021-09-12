package com.example.picture_sharing_application;
import java.io.File;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;

public class _User extends BmobUser {


    private String nickName;
    private BmobFile headPicture;

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


}
