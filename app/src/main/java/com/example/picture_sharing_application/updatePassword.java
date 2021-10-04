package com.example.picture_sharing_application;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.Map;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class updatePassword extends AppCompatActivity {

    private TextView  Account;
    private TextView  Old_password;
    private TextView  New_password;
    private TextView  Confirm_password;
    private Button    update;
    private TextView  back;
    private MyFragment mMyFragment;
    private RelativeLayout main_body;
    private LinearLayout main_bottom_bar;


    private RelativeLayout bottom_bar_1_btn;
    private TextView bottom_bar_text_1;
    private ImageView bottom_bar_image_1;
    private RelativeLayout bottom_bar_2_btn;
    private TextView bottom_bar_text_2;
    private ImageView bottom_bar_image_2;
    private RelativeLayout bottom_bar_3_btn;
    private TextView bottom_bar_text_3;
    private ImageView bottom_bar_image_3;

    //Fragment页面
    private HomeFragment mHomeFragment;
    private AddFragment mAddFragment;

    private LinearLayout key;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_password);

        Account=findViewById(R.id.account);
        Old_password=findViewById(R.id.old_password);
        New_password=findViewById(R.id.new_password);
        Confirm_password=findViewById(R.id.confirm_password);
        update=findViewById(R.id.update);
        back=findViewById(R.id.back);
        mMyFragment = new MyFragment();
        key=findViewById(R.id.keyboard2);


        //点击空白区域收起软键盘
        key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String account=Account.getText().toString();
                String old_password=Old_password.getText().toString();
                String new_password=New_password.getText().toString();
                String password_record=New_password.getText().toString();
                String confirm_password=Confirm_password.getText().toString();

                if (BmobUser.isLogin()) {
                    _User user = BmobUser.getCurrentUser(_User.class);

                    if(new_password.length()!=0&&confirm_password.length()!=0&&user.getUsername().length()!=0)
                    {
                        if(user.getUsername().equals(account)){
                        if(new_password.equals(confirm_password)){
                               if(user.getPassword_record().equals(old_password)){
                                   user.setPassword(new_password);
                                   user.setPassword_record(new_password);
                                   user.update(new UpdateListener() {
                                       @Override
                                       public void done(BmobException e) {
                                           if(e==null){
                                               toast("修改密码成功！请重新登录");
                                           }
                                           else toast(e.getMessage());
                                       }
                                   });
                                   Intent intent=new Intent();
                                   //防止返回键返回上级页面
                                   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                   intent.setClass(updatePassword.this, LoginActivity.class);
                                   startActivity(intent);
                               } else toast("原密码输入错误，请重新输入！");

                        }
                        else toast("两次输入密码不一致，请重新输入！");
                    }
                        else toast("请输入正确账号");
                    }
                   else  toast("请填写上述信息");
                }
               else toast("请先登录账号");
            }

        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(updatePassword.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void toast(String s) {
        Toast.makeText(updatePassword.this, s, Toast.LENGTH_SHORT).show();
    }


}
