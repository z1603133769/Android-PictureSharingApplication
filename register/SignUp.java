package com.example.picture_sharing_application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class SignUp extends Activity
        implements View.OnClickListener {

    private EditText Account;
    private EditText Password;
    private EditText Confirm;
    private Button signup;
    private Button cancel;
    private ImageView imageView;


    private void toast(String s) {
        Toast.makeText(SignUp.this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bmob.resetDomain("https://open3.bmob.cn/8/");
        Bmob.initialize(this, "e1708cc4012f7c433a0e65ad4b6e4386");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter_dialog_setview);
        Account=findViewById(R.id.account);
        Password=findViewById(R.id.password);
        Confirm=findViewById(R.id.confirm);
        signup=findViewById(R.id.signup);
        cancel=findViewById(R.id.cancel);
        imageView=findViewById(R.id.imageView);

        cancel.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          Intent intent=new Intent(SignUp.this, Register.class);
                                          startActivity(intent);
                                      }
                                  });

                signup.setOnClickListener(this);
        signup.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            String password=Password.getText().toString();
            String account =Account.getText().toString();
            String confirm=Confirm.getText().toString();

            if(account.length()!=0&&password.length()!=0&&confirm.length()!=0){
                if(password.equals(confirm)){
                    User user=new User();
                    user.setAccount(account);
                    user.setPassword(password);
                    user.save(new SaveListener<String>() {
                        @Override
                        public void done(String objectId, BmobException e) {
                            if(e==null){
                                toast("注册用户成功，返回objectId为："+objectId);
                                Intent intent=new Intent(SignUp.this, Register.class);
                                startActivity(intent);
                            }else{
                                toast("注册用户失败失败：" + e.getMessage());
                            }
                        }
                    });
                }
                else toast("两次输入密码不一致");
            }
else toast("用户名及密码不能为空");
        }
    });
}


    @Override
    public void onClick(View v) {

    }
}