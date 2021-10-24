package com.example.picture_sharing_application;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import okhttp3.internal.Internal;


public class AddFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "AddFragment";
    private Context mContext = null;
    private EditText mEditText;
    private ImageView mImage;
    private Button mButton;
    private Uri imgUri;
    private String imgUrl;
    //拍照时，图片保存路径
    private String takePhotoUrl = null;
    //系统相册路径
    private String path = Environment.getExternalStorageDirectory() +
            File.separator + Environment.DIRECTORY_DCIM + File.separator;
    private Uri photoUri;
    //add视图
    private View rootView;
    //照片选取类
    PhotoPopupWindow mPhotoPopupWindow;
    //权限常量
    private static final int REQUEST_IMAGE_GET = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SMALL_IMAGE_CUTTING = 2;
    private static final int REQUEST_CHANGE_USER_NICK_NAME = 10;
    private static final String IMAGE_FILE_NAME = "myImage.jpg";

    public AddFragment() {

    }

    public static AddFragment newInstance(String param1, String param2) {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();
        rootView = inflater.inflate(R.layout.fragment_add,
                container, false);
        //初始化布局
        initView();
        //初始化对象并保存数据
        initData();
        return rootView;
    }

    private void initView() {
        mEditText = rootView.findViewById(R.id.add_content);
        mImage = rootView.findViewById(R.id.add_img);
        mButton = rootView.findViewById(R.id.add_button);
        //设置点击事件
        mImage.setOnClickListener(this);
        mButton.setOnClickListener(this);
    }

    private void initData(){

    }


    //重写每一个功能的点击事件
    @Override
    public void onClick(View v){
        switch(v.getId()){
            //相册、照相、裁剪
            case R.id.add_img:
                showSelectDialog();
                break;
            //上传
            case R.id.add_button:
                uploadCard();
                break;

            default:
                break;
        }
    }

    //根据Uri得到图片路径
    public static String getRealPathFromURI(Uri contentUri, Context mContext) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(mContext, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    //上传Card数据到Bmob数据库
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
        //imgUrl = getRealPathFromURI(imgUri,mContext);
        //卡片信息
        String description = mEditText.getText().toString();
        BmobFile Picture = new BmobFile( new File(imgUrl) );
        Integer likeNumber = 0;
        Boolean likeState = false;
        Boolean shareState=false;
        //BmobFile Picture = null;
        //用户信息
        String username = null;
        String nickName = null;
        BmobFile headPicture = null;

        //检查登录信息
        _User user = BmobUser.getCurrentUser(_User.class);
        if (user.isLogin()) {
            username = user.getUsername();
            nickName = user.getNickName();
            headPicture = user.getHeadPicture();
            //Snackbar.make(view, "当前用户：" + user.getUsername() + "-" , Snackbar.LENGTH_LONG).show();
            //String username = (String) BmobUser.getObjectByKey("username");
            //Integer age = (Integer) BmobUser.getObjectByKey("age");
            //Snackbar.make(view, "当前用户属性：" + username + "-" + age, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(rootView, "尚未登录，请先登录", Snackbar.LENGTH_LONG).show();
        }

        //打印信息
        Log.d(TAG,"图片描述为: " + description);
        Log.d(TAG,"图片Uri为: " + imgUri);
        Log.d(TAG,"图片地址为: " + imgUrl);
        Log.d(TAG,"图片文件为: " + Picture);
        Log.d(TAG,"用户名为: " + username);
        Log.d(TAG,"用户昵称为: " + nickName);
        Log.d(TAG,"用户头像为: " + headPicture);

        //设置数据到card里
        card.setDescription(description);
        card.setPicture(Picture);
        card.setUsername(username);
        card.setNickName(nickName);
        card.setHeadPicture(headPicture);
        card.setLikeNumber(likeNumber);
        card.setLikeState(likeState);
        card.setShareState(shareState);

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

    //相册和拍照选择会话框
    private void showSelectDialog(){
        //创建存放图片的文件夹
        //PictureUtil.mkdirMyPetRootDirectory();
        mPhotoPopupWindow = new PhotoPopupWindow( getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 文件权限申请
                if (ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // 权限还没有授予，进行申请
                    ActivityCompat.requestPermissions( getActivity(),
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
                    ActivityCompat.requestPermissions(getActivity(),
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

    //捕获权限返回状态
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPhotoPopupWindow.dismiss();
                    //相册
                    getImageFromAlbum();
                } else {
                    mPhotoPopupWindow.dismiss();
                }
                break;
            case 300:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPhotoPopupWindow.dismiss();
                    //相机
                    getImageFromCamera();
                } else {
                    mPhotoPopupWindow.dismiss();
                }
                break;
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //得到图片文件名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return "IMG_" + dateFormat.format(date);
    }
    //相册
    protected void getImageFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_IMAGE_GET);
    }

    //相机
    protected void getImageFromCamera() {
        takePhotoUrl = null;
        String state = Environment.getExternalStorageState();
        //取消严格模式  FileProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy( builder.build() );
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
            takePhotoUrl = path+fileName;
            Log.d(TAG,"图片保存路径为: "+takePhotoUrl);
            photoUri = Uri.fromFile(new File(takePhotoUrl));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }

//        if (state.equals(Environment.MEDIA_MOUNTED)) {
//            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
//            startActivityForResult(getImageByCamera, REQUEST_IMAGE_CAPTURE);
//        }
//        else {
//            Toast.makeText(mContext, "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
//        }
    }

    //裁剪图片
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

    //捕获状态
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = null;
        switch (requestCode){
            case REQUEST_IMAGE_GET:
                uri = data.getData();
                Log.d(TAG,"图片选择的Uri为: "+uri);
                startPhotoZoom(uri);
                //mImage.setImageURI(uri);
                break;

            case REQUEST_IMAGE_CAPTURE:
                if (data != null && data.getData() != null) {
                    uri = data.getData();
                    Log.d(TAG,"照相的Uri为: "+ uri);
                    //mImage.setImageURI(photoUri);
                }
                if (uri == null) {
                    if (photoUri != null) {
                        uri = photoUri;
                        Log.d(TAG,"uri为null,照相图片的Uri为: "+ uri);
                        startPhotoZoom(uri);
                    }
                }

//                uri = data.getData();
//                if(uri == null){
//                    //use bundle to get data
//                    Bundle bundle = data.getExtras();
//                    if (bundle != null) {
//                        Bitmap  photo = (Bitmap) bundle.get("data"); //get bitmap
//                        Log.d(TAG,"照相的Bitmap为: "+ photo);
//                        //Bitmap转换为uri
//                        uri = Uri.parse(MediaStore.Images.Media.insertImage(mContext.getContentResolver(), photo, null,null));
//                        Log.d(TAG,"照相的Uri为: "+ uri);
//                        startPhotoZoom(uri);
//                        //mImage.setImageBitmap(photo);
//                    } else {
//                        Toast.makeText(mContext, "err****", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                }else{
//                    //to do find the path of pic by uri
//                    Log.d(TAG,"找不到Uti");
//                }

                break;

            case REQUEST_SMALL_IMAGE_CUTTING:
                uri = data.getData();
                imgUri = uri;
                if(uri!=null){
                    Log.d(TAG,"图片Uri为: "+uri);
                    Bitmap img = null;
                    try {
                        img = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG,"图片大小为: "+img.getWidth()+img.getHeight());
                    mImage.setImageBitmap(img);
                }
                else{
                    Log.d(TAG,"找不到Uti");
                }
                break;

            default:
                break;
        }
    }

}