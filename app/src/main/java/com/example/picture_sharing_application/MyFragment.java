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
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.CursorLoader;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFragment extends Fragment  {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RelativeLayout safe;
    private RelativeLayout help;
    private RelativeLayout settings;
    private RelativeLayout aboutus;
    private String mParam1;
    private String mParam2;
    private TextView username;
    private TextView nickname;
    private TextView update_password;
    private ImageView mImage;
    private static final String TAG = "MyFragment";
    private Context mContext = null;
    private EditText mEditText;
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


    //个人空间对应组件
    private ImageView like;
    private FragmentManager fm=null;
    private LikeFragment lFragment;
    private ImageView publish;
    private PublishFragment pFragment;
    private ImageView share;
    private ShareFragment sFragment;

    public MyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyFragment newInstance(String param1, String param2) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();
        rootView = inflater.inflate(R.layout.fragment_my, container, false);
        like=rootView.findViewById(R.id.like);
        publish=rootView.findViewById(R.id.publish);
        share=rootView.findViewById(R.id.share);
        safe=rootView.findViewById(R.id.safe);
        help=rootView.findViewById(R.id.help);
        aboutus=rootView.findViewById(R.id.about_us);
        settings=rootView.findViewById(R.id.settings);
        nickname = rootView.findViewById(R.id.nick_name);
        username = rootView.findViewById(R.id.user_name);
        mImage=rootView.findViewById(R.id.picture);
        lFragment=new LikeFragment();
        pFragment=new PublishFragment();
        sFragment=new ShareFragment();

        _User user = BmobUser.getCurrentUser(_User.class);
        if (user.isLogin()) {
            user.getUsername();
            user.getNickName();
            user.getHeadPicture();
            String url=user.getImagePath();
            Uri uri=Uri.parse(url);

            username.setText(user.getUsername());
            nickname.setText(user.getNickName());
            mImage.setImageURI(uri);
            Log.d("username","username : " + user.getUsername());
            Log.d("nickname","nickname : " + user.getNickName());
            Log.d("imgUrl","imgUrl : " + user.getImagePath());
            // Inflate the layout for this fragment

        }

        //更换头像
        mImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showSelectDialog();
            }
        });

        //修改密码
        safe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent(getActivity(),updatePassword.class);
                startActivity(intent);
            }
        });

        //帮助与反馈
        help.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Help.class);
                startActivity(intent);
            }
        });

        //关于我们
        aboutus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutUs.class);
                startActivity(intent);
            }
        });

        //设置
        settings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Settings.class);
                startActivity(intent);
            }
        });

        //"我点赞的"
        like.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_body,lFragment).commit();
            }
        });

        //"我发布的“
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_body,pFragment).commit();
            }
        });

       //"我转发的“
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_body,sFragment).commit();
            }
        });
        return rootView;
    }

    //相册和拍照选择会话框
    private void showSelectDialog(){
        //创建存放头像的文件夹
        //PictureUtil.mkdirMyPetRootDirectory();
        mPhotoPopupWindow = new PhotoPopupWindow(getActivity(), new View.OnClickListener() {
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
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_my, null);
        mPhotoPopupWindow.showAtLocation(rootView,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private  void uploadCard(){
        //找不到图片Uri
        if(imgUri == null){
            Toast.makeText(mContext, "图片未上传，请重新上传图片!",
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

        //检查登录信息
        _User user = BmobUser.getCurrentUser(_User.class);

        Log.d(TAG,"原图片为: "+user.getHeadPicture());
        Log.d(TAG,"原图片url为: "+user.getImagePath());

        BmobFile oldpicture=user.getHeadPicture();
        oldpicture.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.d("删除成功:","success"+user.getHeadPicture());
                }else{
                    Log.d("删除失败：",e.getErrorCode()+","+user.getHeadPicture());
                }

            }
        });

        BmobFile headPicture = new BmobFile(new File(imgUrl));
        user.setHeadPicture(headPicture);
        user.setImagePath(imgUrl);
        headPicture.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.d("上传文件成功:","success"+headPicture);
                }else{
                    Log.d("上传文件失败:","success"+headPicture);
                }
            }
            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });
        //设置加载样式
        LoadingDialog laoding = new LoadingDialog(mContext,0);
        laoding.show();

        headPicture.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {


                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        laoding.dismiss();
                        Toast.makeText(mContext, "更改头像成功！！！",
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"修改成功 "+user.getNickName());
                    }
                });


            }



        });


//        将图片上传到服务器
//        用uploadblock将图片上传至服务器
//        headPicture.uploadblock(new UploadFileListener() {
//            @Override
//            public void done(BmobException e) {
//                if(e==null){
//                    //上传至Bmob
//                    user.signUp(new SaveListener<_User>() {
//                        @Override
//                        public void done(_User user, BmobException e) {
//                            if(e == null){
//                                laoding.dismiss();
//                                Toast.makeText(mContext, "更改头像成功!",
//                                        Toast.LENGTH_SHORT).show();
//                            }else{
//                                Log.d(TAG,"失败信息为: " + e.getMessage());
//                                Toast.makeText(mContext, "更改头像失败",
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//
//                }
//
//                else{
//                    Log.d(TAG,"上传文件失败：" + e.getMessage());
//                    laoding.dismiss();
//                    Toast.makeText(mContext, "图片上传失败!!!",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void onProgress(Integer value) {
//                // 返回的上传进度（百分比）
//            }
//        });

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
                break;

            case REQUEST_SMALL_IMAGE_CUTTING:
                uri = data.getData();
                imgUri = uri;
                if(uri!=null){
                    Log.d(TAG,"图片Uri为: "+uri);
                    Bitmap img = null;
                    try {
                        img = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                        uploadCard();
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


    //根据Uri得到图片路径
    public static String getRealPathFromURI(Uri contentUri, Context mContext) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(mContext, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}