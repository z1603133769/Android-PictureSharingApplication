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
    //??????????????????????????????
    private String takePhotoUrl = null;
    //??????????????????
    private String path = Environment.getExternalStorageDirectory() +
            File.separator + Environment.DIRECTORY_DCIM + File.separator;
    private Uri photoUri;
    //add??????
    private View rootView;
    //???????????????
    PhotoPopupWindow mPhotoPopupWindow;
    //????????????
    private static final int REQUEST_IMAGE_GET = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SMALL_IMAGE_CUTTING = 2;
    private static final int REQUEST_CHANGE_USER_NICK_NAME = 10;
    private static final String IMAGE_FILE_NAME = "myImage.jpg";


    //????????????????????????
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

        //????????????
        mImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showSelectDialog();
            }
        });

        //????????????
        safe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent(getActivity(),updatePassword.class);
                startActivity(intent);
            }
        });

        //???????????????
        help.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Help.class);
                startActivity(intent);
            }
        });

        //????????????
        aboutus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutUs.class);
                startActivity(intent);
            }
        });

        //??????
        settings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Settings.class);
                startActivity(intent);
            }
        });

        //"????????????"
        like.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_body,lFragment).commit();
            }
        });

        //"???????????????
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_body,pFragment).commit();
            }
        });

       //"???????????????
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_body,sFragment).commit();
            }
        });
        return rootView;
    }

    //??????????????????????????????
    private void showSelectDialog(){
        //??????????????????????????????
        //PictureUtil.mkdirMyPetRootDirectory();
        mPhotoPopupWindow = new PhotoPopupWindow(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ??????????????????
                if (ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // ????????????????????????????????????
                    ActivityCompat.requestPermissions( getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200); // ????????? requestCode ??? 200
                } else {
                    // ??????????????????????????????????????????????????????
                    mPhotoPopupWindow.dismiss();
                    getImageFromAlbum();

                }
            }
        }, new View.OnClickListener()
        {
            @Override
            public void onClick (View v){
                // ???????????????????????????
                if (ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // ????????????????????????????????????
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 300); // ????????? requestCode ??? 300
                } else {
                    // ?????????????????????????????????
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
        //???????????????Uri
        if(imgUri == null){
            Toast.makeText(mContext, "???????????????????????????????????????!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG,"????????????uri???: "+imgUri);

        if(takePhotoUrl != null){
            imgUrl = takePhotoUrl;
        }
        else{
            imgUrl = getRealPathFromURI(imgUri,mContext);
        }

        //??????????????????
        _User user = BmobUser.getCurrentUser(_User.class);

        Log.d(TAG,"????????????: "+user.getHeadPicture());
        Log.d(TAG,"?????????url???: "+user.getImagePath());

        BmobFile oldpicture=user.getHeadPicture();
        oldpicture.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.d("????????????:","success"+user.getHeadPicture());
                }else{
                    Log.d("???????????????",e.getErrorCode()+","+user.getHeadPicture());
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
                    Log.d("??????????????????:","success"+headPicture);
                }else{
                    Log.d("??????????????????:","success"+headPicture);
                }
            }
            @Override
            public void onProgress(Integer value) {
                // ????????????????????????????????????
            }
        });
        //??????????????????
        LoadingDialog laoding = new LoadingDialog(mContext,0);
        laoding.show();

        headPicture.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {


                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        laoding.dismiss();
                        Toast.makeText(mContext, "???????????????????????????",
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"???????????? "+user.getNickName());
                    }
                });


            }



        });


//        ???????????????????????????
//        ???uploadblock???????????????????????????
//        headPicture.uploadblock(new UploadFileListener() {
//            @Override
//            public void done(BmobException e) {
//                if(e==null){
//                    //?????????Bmob
//                    user.signUp(new SaveListener<_User>() {
//                        @Override
//                        public void done(_User user, BmobException e) {
//                            if(e == null){
//                                laoding.dismiss();
//                                Toast.makeText(mContext, "??????????????????!",
//                                        Toast.LENGTH_SHORT).show();
//                            }else{
//                                Log.d(TAG,"???????????????: " + e.getMessage());
//                                Toast.makeText(mContext, "??????????????????",
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//
//                }
//
//                else{
//                    Log.d(TAG,"?????????????????????" + e.getMessage());
//                    laoding.dismiss();
//                    Toast.makeText(mContext, "??????????????????!!!",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void onProgress(Integer value) {
//                // ????????????????????????????????????
//            }
//        });

    }

    //????????????
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = null;
        switch (requestCode){
            case REQUEST_IMAGE_GET:
                uri = data.getData();
                Log.d(TAG,"???????????????Uri???: "+uri);
                startPhotoZoom(uri);

                //mImage.setImageURI(uri);
                break;

            case REQUEST_IMAGE_CAPTURE:
                if (data != null && data.getData() != null) {
                    uri = data.getData();
                    Log.d(TAG,"?????????Uri???: "+ uri);

                    //mImage.setImageURI(photoUri);
                }
                if (uri == null) {
                    if (photoUri != null) {
                        uri = photoUri;
                        Log.d(TAG,"uri???null,???????????????Uri???: "+ uri);
                        startPhotoZoom(uri);

                    }
                }
                break;

            case REQUEST_SMALL_IMAGE_CUTTING:
                uri = data.getData();
                imgUri = uri;
                if(uri!=null){
                    Log.d(TAG,"??????Uri???: "+uri);
                    Bitmap img = null;
                    try {
                        img = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                        uploadCard();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG,"???????????????: "+img.getWidth()+img.getHeight());
                    mImage.setImageBitmap(img);

                }
                else{
                    Log.d(TAG,"?????????Uti");
                }
                break;
            default:
                break;
        }
    }

    //????????????????????????
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return "IMG_" + dateFormat.format(date);
    }
    //??????
    protected void getImageFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//????????????
        startActivityForResult(intent, REQUEST_IMAGE_GET);
    }

    //??????
    protected void getImageFromCamera() {
        takePhotoUrl = null;
        String state = Environment.getExternalStorageState();
        //??????????????????  FileProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy( builder.build() );
        }
        //???????????????
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
            String fileName = getPhotoFileName() + ".jpg";
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //photoUri = Uri.fromFile(new File(file, fileName));
            takePhotoUrl = path+fileName;
            Log.d(TAG,"?????????????????????: "+takePhotoUrl);
            photoUri = Uri.fromFile(new File(takePhotoUrl));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }

    }



    //????????????
    private void startPhotoZoom(Uri uri) {
        Uri cropUri;
        cropUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //???????????????????????????????????????????????????Uri??????????????????
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

    //????????????????????????
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPhotoPopupWindow.dismiss();
                    //??????
                    getImageFromAlbum();
                } else {
                    mPhotoPopupWindow.dismiss();
                }
                break;
            case 300:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPhotoPopupWindow.dismiss();
                    //??????
                    getImageFromCamera();
                } else {
                    mPhotoPopupWindow.dismiss();
                }
                break;
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    //??????Uri??????????????????
    public static String getRealPathFromURI(Uri contentUri, Context mContext) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(mContext, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}