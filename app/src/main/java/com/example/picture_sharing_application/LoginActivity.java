package com.example.picture_sharing_application;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class LoginActivity extends AppCompatActivity
        implements View.OnClickListener {
    private Boolean bPwdSwitch = false;
    private EditText etPwd;
    private EditText etUsername;
    private CheckBox cbRememberPwd;
    private TextView Sign;
    private Context mContext = this;
    private RelativeLayout key;

    public static Uri imgurl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bmob.resetDomain("https://open3.bmob.cn/8/");
        Bmob.initialize(this, "e1708cc4012f7c433a0e65ad4b6e4386");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final ImageView ivPwdSwitch = findViewById(R.id.iv_pwd_switch);
        etPwd = findViewById(R.id.et_pwd);
        etUsername = findViewById(R.id.et_username);
        cbRememberPwd = findViewById(R.id.cb_remember_pwd);
        key=findViewById(R.id.keyboard_);
        Button btLogin = findViewById(R.id.bt_login);
        Sign=findViewById(R.id.tv_sign_up);

        //“注册”
        Sign.setOnClickListener(this);
        Sign.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });

        //点击空白区域收起软键盘
        key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        //”登录“
        btLogin.setOnClickListener(this);
        btLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                LoadingDialog laoding = new LoadingDialog(mContext,0);
                laoding.show();

                String spFileName = getResources()
                        .getString(R.string.shared_preferences_file_name);
                String accountKey = getResources()
                        .getString(R.string.login_account_name);
                String passwordKey =  getResources()
                        .getString(R.string.login_password);
                String rememberPasswordKey = getResources()
                        .getString(R.string.login_remember_passoword);


                SharedPreferences spFile = getSharedPreferences(
                        spFileName,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = spFile.edit();

                if(cbRememberPwd.isChecked()) {
                    String password = etPwd.getText().toString();
                    String account = etUsername.getText().toString();
                    editor.putString(accountKey, account);
                    editor.putString(passwordKey, password);
                    editor.putBoolean(rememberPasswordKey, true);
                    editor.apply();
                } else {
                    editor.remove(accountKey);
                    editor.remove(passwordKey);
                    editor.remove(rememberPasswordKey);
                    editor.apply();
                }

                final _User user= new _User();
                user.setUsername(etUsername.getText().toString());
                user.setPassword(etPwd.getText().toString());

                user.login(new SaveListener<_User>() {
                    @Override
                    public void done(_User bmobUser, BmobException e) {
                        if (e==null) {
                            _User user = BmobUser.getCurrentUser(_User.class);
                            //Snackbar.make(view, "登录成功：" + user.getUsername(), Snackbar.LENGTH_LONG).show();
                            if (user.isLogin()) {
                                 user = BmobUser.getCurrentUser(_User.class);
                                //Snackbar.make(view, "当前用户：" + user.getUsername() + "-" , Snackbar.LENGTH_LONG).show();
                                //String username = (String) BmobUser.getObjectByKey("username");
                                //Integer age = (Integer) BmobUser.getObjectByKey("age");
                                //Snackbar.make(view, "当前用户属性：" + username + "-" + age, Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(view, "尚未登录，请先登录", Snackbar.LENGTH_LONG).show();
                            }
                            Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);

                        } else {
                            laoding.dismiss();
                            Snackbar.make(view, "登录失败：" + e.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });


        ivPwdSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bPwdSwitch = !bPwdSwitch;
                if (bPwdSwitch) {
                    ivPwdSwitch.setImageResource(
                            R.drawable.ic_baseline_visibility_24);
                    etPwd.setInputType(
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    ivPwdSwitch.setImageResource(
                            R.drawable.ic_baseline_visibility_off_24);
                    etPwd.setInputType(
                            InputType.TYPE_TEXT_VARIATION_PASSWORD |
                                    InputType.TYPE_CLASS_TEXT);
                    etPwd.setTypeface(Typeface.DEFAULT);
                }
            }
        });

        String spFileName = getResources()
                .getString(R.string.shared_preferences_file_name);
        String accountKey = getResources()
                .getString(R.string.login_account_name);
        String passwordKey =  getResources()
                .getString(R.string.login_password);
        String rememberPasswordKey = getResources()
                .getString(R.string.login_remember_passoword);

        SharedPreferences spFile = getSharedPreferences(
                spFileName,
                MODE_PRIVATE);
        String account = spFile.getString(accountKey, null);
        String password = spFile.getString(passwordKey, null);
        Boolean rememberPassword = spFile.getBoolean(
                rememberPasswordKey,
                false);

        if (account != null && !TextUtils.isEmpty(account)) {
            etUsername.setText(account);
        }

        if (password != null && !TextUtils.isEmpty(password)) {
            etPwd.setText(password);
        }

        cbRememberPwd.setChecked(rememberPassword);

    }

    private void toast(String s) {
        Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        String spFileName = getResources()
                .getString(R.string.shared_preferences_file_name);
        String accountKey = getResources()
                .getString(R.string.login_account_name);
        String passwordKey =  getResources()
                .getString(R.string.login_password);
        String rememberPasswordKey = getResources()
                .getString(R.string.login_remember_passoword);

        SharedPreferences spFile = getSharedPreferences(
                spFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spFile.edit();

         if(cbRememberPwd.isChecked()) {
            String password = etPwd.getText().toString();
            String account = etUsername.getText().toString();

            editor.putString(accountKey, account);
            editor.putString(passwordKey, password);
            editor.putBoolean(rememberPasswordKey, true);
            editor.apply();
        } else {
            editor.remove(accountKey);
            editor.remove(passwordKey);
            editor.remove(rememberPasswordKey);
            editor.apply();
        }

    }



}
