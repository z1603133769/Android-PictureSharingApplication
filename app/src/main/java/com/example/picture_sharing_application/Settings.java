package com.example.picture_sharing_application;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {

    private RelativeLayout switch_account;
    private RelativeLayout exit_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        switch_account=findViewById(R.id.switch_account);
        exit_account=findViewById(R.id.exit_account);
        //切换账号
        switch_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                //防止返回键返回上级页面
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(Settings.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        //退出账号
        exit_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                //防止返回键返回上级页面
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(Settings.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }


}
