package com.example.picture_sharing_application;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class RegisterActivity extends AppCompatActivity
        implements View.OnClickListener {


    PhotoPopupWindow mPhotoPopupWindow;

    //点击选择头像后显示浮窗
    private static final String TAG = "PhotoPopupWindow";
    private View mView; // PopupWindow 菜单布局
    private View.OnClickListener mSelectListener; // 相册选取的点击监听器
    private View.OnClickListener mCaptureListener; // 拍照的点击监听器
    private Context mContext=null;
    private EditText Nickname;
    private EditText Username;
    private EditText Password;
    private EditText Confirm;
    private Button signup;
    private Button cancel;
    private ImageView imageView;
    private String imgUrl;
    private Button upload;
    private RelativeLayout key;
    //拍照时，图片保存路径
    private String takePhotoUrl = null;
    //系统相册路径
    private String path = Environment.getExternalStorageDirectory() +
            File.separator + Environment.DIRECTORY_DCIM + File.separator;
    private Uri photoUri;
    private Uri imgUri;
    //权限常量
    private static final int REQUEST_IMAGE_GET = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SMALL_IMAGE_CUTTING = 2;
    private static final int REQUEST_CHANGE_USER_NICK_NAME = 10;
    private static final String IMAGE_FILE_NAME = "myImage.jpg";

    private Map<String,String> passwordList = new HashMap<>();

    //浮窗提示
    private void toast(String s) {
        Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Username = findViewById(R.id.account);
        Password = findViewById(R.id.new_password);
        Confirm = findViewById(R.id.confirm_password);
        signup = findViewById(R.id.update);
        cancel = findViewById(R.id.back);
        imageView = findViewById(R.id.imageView);
        upload = findViewById(R.id.upload);
        Nickname=findViewById(R.id.old_password);
        key=findViewById(R.id.keyboard);
        mContext=this;

        //点击空白区域收起软键盘
        key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        //“上传头像”
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog();
            }
        });

       //“取消“
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        //“注册”
        signup.setOnClickListener(this);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 _User user = new _User();
                if(imgUri == null){
                    Toast.makeText(mContext, "请补全以上信息!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d(TAG,"目标图片uri为: "+imgUri);
                if(takePhotoUrl != null){
                    imgUrl = takePhotoUrl;
                }
                else{
                    imgUrl = getRealPathFromURI(imgUri,mContext);
                }

                final  BmobFile headPicture = new BmobFile( new File(imgUrl) );


                String password = Password.getText().toString();
                String username = Username.getText().toString();
                String nickname = Nickname.getText().toString();
                String confirm = Confirm.getText().toString();

                if (username.length() != 0 && password.length() != 0 && confirm.length() != 0 &&nickname.length()!=0) {

                    if (password.equals(confirm)) {

                        LoadingDialog laoding = new LoadingDialog(mContext,0);
                        laoding.show();
//                        BmobQuery<_User> bmobQuery = new BmobQuery<_User>();
//                        bmobQuery.addWhereEqualTo("username",username);
//                        bmobQuery.findObjects(new FindListener<_User>() {
//                            @Override
//                            public void done(List<_User> object, BmobException e) {
//                                if (e == null) {
//                                    laoding.dismiss();
//                                    toast("该用户已存在！！！");
//                                    return ;
//                                } else {
//                                    Log.e("BMOB", e.toString());
//                                }
//                            }
//                        });

                        user.setNickName(nickname);
                        user.setUsername(username);
                        user.setPassword(password);
                        user.setHeadPicture(headPicture);
                        user.setImagePath(imgUrl);
                        user.setPassword_record(password);
                        headPicture.uploadblock(new UploadFileListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){

                                    user.signUp(new SaveListener<_User>() {
                                        @Override
                                        public void done(_User user, BmobException e) {
                                            if (e == null) {
                                                laoding.dismiss();
                                                toast("注册成功");
                                                passwordList.put(username,password);
                                                Constants.PasswordList=passwordList;
                                                Intent intent=new Intent();
                                                //防止返回键返回上级页面
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.setClass(RegisterActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                            } else
                                            {
                                                toast(e.getMessage());
                                                laoding.dismiss();
                                            }
                                        }
                                    });
                                }else{
                                    laoding.dismiss();
                                    toast("上传文件失败：" + e.getMessage());
                                }

                            }
                        });

                    }
                    else {
                        toast("两次输入密码不一致");
                    }

                } else {
                    toast("请补全以上信息");
                }

            }


        });



    }


    @Override
    public void onClick(View v) {

    }

    //得到图片地址
    public static String getRealPathFromURI(Uri contentUri, Context mContext) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(mContext, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    //得到图片文件名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return "IMG_" + dateFormat.format(date);
    }

    //拍照
    protected void getImageFromCamera() {
        takePhotoUrl = null;
        String state = Environment.getExternalStorageState();
        //取消严格模式  FileProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        //保存到相册
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
            String fileName = getPhotoFileName() + ".jpg";
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //photoUri = Uri.fromFile(new File(file, fileName));
            takePhotoUrl = path + fileName;
            Log.d(TAG, "图片保存路径为: " + takePhotoUrl);
            photoUri = Uri.fromFile(new File(takePhotoUrl));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }


    }


    //相册和拍照选择会话框
    private void showSelectDialog(){
        //创建存放头像的文件夹
        //PictureUtil.mkdirMyPetRootDirectory();
        mPhotoPopupWindow = new PhotoPopupWindow( this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 文件权限申请
                if (ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // 权限还没有授予，进行申请
                    ActivityCompat.requestPermissions((Activity)mContext,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200); // 申请的 requestCode 为 200
                } else {
                    // 如果权限已经申请过，直接进行图片选择
                    mPhotoPopupWindow.dismiss();
                    getImageFromAlbum();
                }
            }
        }, new View.OnClickListener()
        {
            @Override
            public void onClick (View v){
                // 拍照及文件权限申请
                if (ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // 权限还没有授予，进行申请
                    ActivityCompat.requestPermissions((Activity)mContext,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 300); // 申请的 requestCode 为 300
                } else {
                    // 权限已经申请，直接拍照
                    mPhotoPopupWindow.dismiss();
                    getImageFromCamera();
                }
            }
        });

        View rootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_add, null);
        mPhotoPopupWindow.showAtLocation(rootView,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //相册
    protected void getImageFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_IMAGE_GET);
    }

    private void startPhotoZoom(Uri uri) {
        Uri cropUri;
        cropUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        //Log.e(TAG,"cropUri = "+cropUri.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, REQUEST_SMALL_IMAGE_CUTTING);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = null;
        switch (requestCode) {
            case REQUEST_IMAGE_GET:
                uri = data.getData();
                Log.d(TAG, "图片选择的Uri为: " + uri);
                startPhotoZoom(uri);
                //mImage.setImageURI(uri);
                break;

            case REQUEST_IMAGE_CAPTURE:
                if (data != null && data.getData() != null) {
                    uri = data.getData();
                    Log.d(TAG, "照相的Uri为: " + uri);
                    //mImage.setImageURI(photoUri);
                }
                if (uri == null) {
                    if (photoUri != null) {
                        uri = photoUri;
                        Log.d(TAG, "uri为null,照相图片的Uri为: " + uri);
                        startPhotoZoom(uri);
                    }
                }
                break;

            case REQUEST_SMALL_IMAGE_CUTTING:
                uri = data.getData();
                imgUri = uri;
                if (uri != null) {
                    Log.d(TAG, "图片Uri为: " + uri);
                    Bitmap img = null;
                    try {
                        img = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "图片大小为: " + img.getWidth() + img.getHeight());
                    imageView.setImageBitmap(img);
                } else {
                    Log.d(TAG, "找不到Uti");
                }
                break;

            default:
                break;
        }
    }

    private  void uploadCard(){
        //找不到图片Uri
        if(imgUri == null){
            Toast.makeText(mContext, "图片未上传，请重新上传图片!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG,"目标图片uri为: "+imgUri);
        Card card = new Card();
        if(takePhotoUrl != null){
            imgUrl = takePhotoUrl;
        }
        else{
            imgUrl = getRealPathFromURI(imgUri,mContext);
        }

        BmobFile Picture = new BmobFile( new File(imgUrl) );
        String username = null;
        String nickName = null;
        BmobFile headPicture = null;

        //打印信息
        Log.d(TAG,"图片Uri为: " + imgUri);
        Log.d(TAG,"图片地址为: " + imgUrl);
        Log.d(TAG,"图片文件为: " + Picture);
        //设置加载样式
        LoadingDialog laoding = new LoadingDialog(mContext,0);
        laoding.show();

        //将图片上传到服务器
        //用uploadblock将图片上传至服务器
        Picture.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){

                    //上传至Bmob
                    card.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e == null){
                                laoding.dismiss();
                                Toast.makeText(mContext, "图片上传成功!!!",
                                        Toast.LENGTH_SHORT).show();
                            }else{
                                Log.d(TAG,"失败信息为: " + e.getMessage());
                                Toast.makeText(mContext, "图片上传失败!!!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    Log.d(TAG,"上传文件成功:" + Picture.getFileUrl());
                }else{
                    Log.d(TAG,"上传文件失败：" + e.getMessage());
                    laoding.dismiss();
                    Toast.makeText(mContext, "图片上传失败!!!",
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });

    }

}


