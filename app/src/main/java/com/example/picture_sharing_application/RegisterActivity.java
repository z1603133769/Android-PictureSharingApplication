package com.example.picture_sharing_application;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

import android.widget.EditText;
import android.widget.Toast;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity
        implements View.OnClickListener {



    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }




    private  Uri uri;
      String path;


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                uri = data.getData();
                Log.d("sign","原图片URI : " + uri);
                imageView.setImageURI(uri);
                String path= getRealFilePath(this,uri);
                Log.d("sign","图片路径 : " + path);


            }
        }
    }


    private EditText Username;
    private EditText Password;
    private EditText Confirm;
    private Button signup;
    private Button cancel;
    private ImageView imageView;
    private Button upload;

    private void toast(String s) {
        Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        Username=findViewById(R.id.username);
        Password=findViewById(R.id.password);
        Confirm=findViewById(R.id.confirm);
        signup=findViewById(R.id.signup);
        cancel=findViewById(R.id.cancel);
        imageView=findViewById(R.id.imageView);
        upload=findViewById(R.id.upload);



//        toast(path);

        upload.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

                startActivityForResult(intent, 2);
            }
        });



        cancel.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                                          startActivity(intent);
                                      }
                                  });

        signup.setOnClickListener(this);


        signup.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) {


            final _User user=new _User();

            Log.d("sign","图片路径 : " + path);
            final BmobFile file=new BmobFile(new File(path));
            user.setHeadPicture(file);
            user.signUp(new SaveListener<_User>(){
                @Override
                public void done(_User user, BmobException e) {
                    if(e==null){
                        toast("上传成功");
                    }
                    else{
                        toast("上传失败：" + e.getMessage());
                    }
                }
            });








//            BmobFile bmobFile = new BmobFile(new File(path));
//            bmobFile.uploadblock(new UploadFileListener() {
//
//                @Override
//                public void done(BmobException e) {
//                    if(e==null){
//                        //bmobFile.getFileUrl()--返回的上传文件的完整地址
//                        toast("上传文件成功:" + bmobFile.getFileUrl());
//                    }else{
//                        toast("上传文件失败：" + e.getMessage());
//                    }
//                }
//                @Override
//                public void onProgress(Integer value) {
//                    // 返回的上传进度（百分比）
//                }
//            });





            String password=Password.getText().toString();
            String username =Username.getText().toString();
            String confirm=Confirm.getText().toString();

            if(username.length()!=0&&password.length()!=0&&confirm.length()!=0){



                                  if(password.equals(confirm)){

                                      user.setUsername(username);
                                    user.setPassword(password);
                                    user.signUp(new SaveListener<_User>(){
                                        @Override
                                        public void done(_User user, BmobException e) {
                                            if(e==null){
                                                toast("注册成功");
                                                Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                            }
                                            else{
                                                toast("注册用户失败：" + e.getMessage());
                                            }
                                        }
                                    });
                                }
                                else toast("两次输入密码不一致");

                    }
            else toast("请补全以上信息");

            }


    });
}


    @Override
    public void onClick(View v) {

    }
}