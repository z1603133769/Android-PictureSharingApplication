package com.example.picture_sharing_application;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //来自activity_main.xml
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
    private MyFragment mMyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化页面
        initView();
        //设置开始页面
        setMain();
    }

    //实例化
    private void initView(){
        //底部导航栏
        main_body=findViewById(R.id.main_body);
        //包含底部 android:id="@+id/main_bottom_bar"
        main_bottom_bar=findViewById(R.id.main_bottom_bar);

        //首页
        bottom_bar_1_btn=findViewById(R.id.bottom_bar_1_btn);
        bottom_bar_text_1=findViewById(R.id.bottom_bar_text_1);
        bottom_bar_image_1=findViewById(R.id.bottom_bar_image_1);
        //添加点击事件
        bottom_bar_1_btn.setOnClickListener(this);
        //发布
        bottom_bar_2_btn=findViewById(R.id.bottom_bar_2_btn);
        bottom_bar_text_2=findViewById(R.id.bottom_bar_text_2);
        bottom_bar_image_2=findViewById(R.id.bottom_bar_image_2);
        //添加点击事件
        bottom_bar_2_btn.setOnClickListener(this);
        //我的
        bottom_bar_3_btn=findViewById(R.id.bottom_bar_3_btn);
        bottom_bar_text_3=findViewById(R.id.bottom_bar_text_3);
        bottom_bar_image_3=findViewById(R.id.bottom_bar_image_3);
        //添加点击事件
        bottom_bar_3_btn.setOnClickListener(this);

        //Fragment页面
        mHomeFragment = new HomeFragment();
        mAddFragment = new com.example.picture_sharing_application.AddFragment();
        mMyFragment = new MyFragment();

    }
    //点击Button切换状态
    private void setSelectStatus(int index) {
        switch (index){
            case 0:
                //图片点击选择变换图片，颜色的改变，其他变为原来的颜色，并保持原有的图片
                initButton(bottom_bar_1_btn);
                bottom_bar_image_1.setImageResource(R.drawable.home_a);
                bottom_bar_text_1.setTextColor( getResources().getColor(R.color.textActiveColor) );
                //其他的文本颜色不变
                bottom_bar_text_2.setTextColor(getResources().getColor(R.color.textColor));
                bottom_bar_text_3.setTextColor(getResources().getColor(R.color.textColor));
                //图片也不变
                bottom_bar_image_2.setImageResource(R.drawable.add);
                bottom_bar_image_3.setImageResource(R.drawable.my);
                break;
            case 1://同理如上
                //图片点击选择变换图片，颜色的改变，其他变为原来的颜色，并保持原有的图片
                initButton(bottom_bar_2_btn);
                bottom_bar_image_2.setImageResource(R.drawable.add_a);
                bottom_bar_text_2.setTextColor( getResources().getColor(R.color.textActiveColor) );
                //其他的文本颜色不变
                bottom_bar_text_1.setTextColor(getResources().getColor(R.color.textColor));
                bottom_bar_text_3.setTextColor(getResources().getColor(R.color.textColor));
                //图片也不变
                bottom_bar_image_1.setImageResource(R.drawable.home);
                bottom_bar_image_3.setImageResource(R.drawable.my);
                break;
            case 2://同理如上
                //图片点击选择变换图片，颜色的改变，其他变为原来的颜色，并保持原有的图片
                initButton(bottom_bar_3_btn);
                bottom_bar_image_3.setImageResource(R.drawable.my_a);
                bottom_bar_text_3.setTextColor( getResources().getColor(R.color.textActiveColor) );
                //其他的文本颜色不变
                bottom_bar_text_1.setTextColor(getResources().getColor(R.color.textColor));
                bottom_bar_text_2.setTextColor(getResources().getColor(R.color.textColor));
                //图片也不变
                bottom_bar_image_1.setImageResource(R.drawable.home);
                bottom_bar_image_2.setImageResource(R.drawable.add);
                break;
        }
    }

    //按钮特效
    private void initButton(RelativeLayout bt){
        Animation animation=new AlphaAnimation(1.0f,0.0f);
        animation.setDuration(300);
        bt.startAnimation(animation);
    }

    //重写底部导航栏的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bottom_bar_1_btn:
                //切换Fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.main_body,mHomeFragment).commit();
                setSelectStatus(0);
                break;
            case R.id.bottom_bar_2_btn:
                //切换Fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.main_body,mAddFragment).commit();
                setSelectStatus(1);
                break;
            case R.id.bottom_bar_3_btn:
                //切换Fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.main_body,mMyFragment).commit();
                setSelectStatus(2);
                break;
        }
    }

    //用于打开初始页面
    private void setMain() {
        //默认进入首页
        setSelectStatus(0);
        //getSupportFragmentManager() -> beginTransaction() -> add -> (R.id.main_boy,显示课程 new CourseFragment()
        this.getSupportFragmentManager().beginTransaction().add(R.id.main_body,mHomeFragment).commit();
    }

}