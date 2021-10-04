package com.example.picture_sharing_application;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.makeramen.roundedimageview.RoundedImageView;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import okhttp3.OkHttpClient;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>{
    Bitmap img;
    private List<Card> mCardList;
    private Context mContext;
    private String imgName;
    //大图会话
    private Dialog dialog;
    //显示大图
    private ImageView mImageView;
    //当前用户的点赞列表
    private Map<String, Boolean> likeList = new HashMap<>();
    private _User currentUser;
    private int likeNumber = 0;
    private Map<String, Boolean> shareList = new HashMap<>();
    private ViewHolder targetHolder;
    //分享信息
    public static String TITLE = "PiliPili——图片分享";

    //handler 用于线程间的通信
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //当点击图片时，通过url加载图片，并显示大图
                case 0:
                    //测试
                    Log.d("Adapter","img的内容为: "+imgName);
                    Log.d("Adapter","img的图片为: "+img);
                    mImageView = getImageView(img);
                    //初始化会话
                    initDialog();
                    //显示会话
                    dialog.show();
                    break;
                //分享图片
                case 1:
                    Uri uri =  Uri.parse(MediaStore.Images.Media.insertImage(mContext.getContentResolver(), img, null,null));;
                    shareImage(uri);
                    //激活分享图标
                    targetHolder.ShareIcon.setImageResource(R.drawable.share_a);
                    break;
                default:
                    break;
            }
        }
    };

    static class ViewHolder extends RecyclerView.ViewHolder{
        RoundedImageView CardImage;
        TextView CardContent;
        RoundedImageView HeadPic;
        TextView NickName;
        TextView LikeNumber;
        ImageView LikeIcon;
        ImageView ShareIcon;

        public ViewHolder(View view){
            super(view);
            CardImage = view.findViewById(R.id.iv_image);
            CardContent = view.findViewById(R.id.tv_content);
            HeadPic = view.findViewById(R.id.iv_head);
            NickName = view.findViewById(R.id.tv_name);
            LikeNumber = view.findViewById(R.id.tv_likeNumber);
            LikeIcon = view.findViewById(R.id.iv_love);
            ShareIcon = view.findViewById(R.id.iv_share);
        }

    }

    public CardAdapter(Context context,List<Card> CardList){
        Log.d("Adapter","适配器创建成功");
        //super();
        mContext =context;
        mCardList = CardList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("Adapter","适配器视图创建成功");
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.card_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        //得到当前用户
        currentUser = BmobUser.getCurrentUser(_User.class);

        //图片监听事件
        holder.CardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Card card = mCardList.get(position);
                String imgUrl = null;
                if(card.getPicture()!=null){
                    imgUrl = card.getPicture().getUrl();
                }
                //获取img名称
                imgName = card.getDescription();
                //通过url,加载图片
                if(imgUrl!=null){
                    initNetWorkImage(imgUrl,mContext,0);
                }
                //图片加载失败
                else{
                    BitmapDrawable bd = (BitmapDrawable)mContext.getResources().getDrawable(R.drawable.loadingfail);
                    img = bd.getBitmap();
                    mImageView = getImageView(img);
                    //初始化会话
                    initDialog();
                    //显示会话
                    dialog.show();
                }
                Log.d("Adapter","图片地址为: "+ imgUrl);
            }
        });

        //点赞点击事件
        holder.LikeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Card card = mCardList.get(position);
                String content = holder.CardContent.getText().toString();
                int likes = Integer.parseInt(holder.LikeNumber.getText().toString());
                boolean flag = likeList.get(content);
                if(flag){
                    //已点赞状态 --> 未点赞状态
                    flag = !flag;
                    likeList.put(content,flag);
                    //设置图标
                    holder.LikeIcon.setImageResource(R.drawable.love);
                    //设置点赞数量
                    likes -= 1;
                    String like_Number =  String.valueOf(likes);
                    holder.LikeNumber.setText(like_Number);
                    //移除用户信息
                    removeUserToLikes(card);
                }else{
                    //未点赞状态 --> 已点赞状态
                    flag = !flag;
                    likeList.put(content,flag);
                    //设置图标
                    holder.LikeIcon.setImageResource(R.drawable.love_a);
                    //设置点赞数量
                    likes += 1;
                    String like_Number =  String.valueOf(likes);
                    holder.LikeNumber.setText(like_Number);
                    //添加用户信息
                    addUserToLikes(card);
                }
            }
        });

        //分享点击事件
        holder.ShareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Card card = mCardList.get(position);
                String content = holder.CardContent.getText().toString();
                shareList.put(content,true);
                String url = card.getPicture().getUrl();
                addUserToShares(card);


                //分享图片
                initNetWorkImage(url,mContext,1);
                //设置图标和标志
                targetHolder = holder;

                //holder.ShareIcon.setImageResource(R.drawable.share_a);
                //isShare.put(content,true);
            }
        });
        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Card card = mCardList.get(position);
        String imgUrl = null;
        String headUrl = null;
        String content = "#"+card.getDescription()+"#";
        if( card.getPicture() != null){
            imgUrl = card.getPicture().getUrl();
        }
        if( card.getHeadPicture() != null){
            headUrl = card.getHeadPicture().getUrl();
        }
        //查询该卡片的喜欢用户列表
        queryLikesUser(holder,card);
        queryShareUser(holder,card);

        Log.d("Adapter","图片地址为:"+imgUrl);
        Log.d("Adapter","头像地址为:"+headUrl);
        Log.d("Adapter","用户昵称为:"+card.getNickName());

        //图片
        if(imgUrl!=null){
            Glide.with(mContext).load(imgUrl)
                    .into(holder.CardImage);
        }
        //内容
        if(card.getDescription()!=null){
            String description = "#"+card.getDescription()+"#";
            holder.CardContent.setText(description);
        }
        //头像
        if(headUrl!=null){
            Glide.with(mContext).load(headUrl)
                    .into(holder.HeadPic);
        }
        //昵称
        if(card.getNickName()!= ""){
            holder.NickName.setText(card.getNickName());
        }
//            //收藏数
//            String like_Number =  String.valueOf(likeNumber);
////            if( card.getLikeNumber() != null){
////                likeNumber = card.getLikeNumber().toString();
////            }
//
//            holder.LikeNumber.setText(like_Number);

    }

    @Override
    public int getItemCount() {
        return mCardList.size();
    }

    public void addData(Card card){
        mCardList.add(card);
        //notifyDataSetChanged();
    }

    public void setData(List<Card> cardData){
        mCardList.addAll(cardData);
        //notifyDataSetChanged();
    }

    //查询点赞用户中是否包含已登录用户
    private void queryLikesUser(ViewHolder holder,Card card){
        // 查询喜欢这个帖子的所有用户，因此查询的是用户表
        BmobQuery<_User> query = new BmobQuery<_User>();
        //likes是Card表中的字段，用来存储所有喜欢该帖子的用户
        query.addWhereRelatedTo("likes", new BmobPointer(card));
        query.findObjects(new FindListener<_User>() {
            @Override
            public void done(List<_User> object, BmobException e) {
                if(e==null){
                    likeNumber = object.size();
                    String content = holder.CardContent.getText().toString();
                    Log.i("bmob","查询对象为："+content);
                    Log.i("bmob","查询个数："+object.size());
                    //设置点赞数
                    String like_Number =  String.valueOf(likeNumber);;
                    holder.LikeNumber.setText(like_Number);
                    //查询是否包含当前用户
                    boolean isContain = false;
                    for(_User user : object){
                        String userName = user.getUsername();
                        String currentUserName = currentUser.getUsername();
                        Log.d("Adapter","用户名列表为: "+userName);
                        Log.d("Adapter","当前用户名列表为: "+currentUserName);
                        if(userName.equals(currentUserName)){
                            isContain = true;
                        }
                    }
                    Log.d("Adapter","点赞列表为: "+object);
                    Log.d("Adapter","当前用户为: "+currentUser);
                    Log.i("bmob",   "查询结果为："+isContain);
                    likeList.put(content,isContain);
                    Constants.LIKELIST = likeList;
                    //激活为点赞状态
                    if(isContain){
                        holder.LikeIcon.setImageResource(R.drawable.love_a);
                    }
                    //myHandler.sendEmptyMessage(1);
                }else{
                    Log.i("bmob","失败："+e.getMessage());
                }
            }
        });
    }

    private void queryShareUser(ViewHolder holder,Card card){
        // 查询喜欢这个帖子的所有用户，因此查询的是用户表
        BmobQuery<_User> query = new BmobQuery<_User>();
        //likes是Card表中的字段，用来存储所有喜欢该帖子的用户
        query.addWhereRelatedTo("shares", new BmobPointer(card));
        query.findObjects(new FindListener<_User>() {
            @Override
            public void done(List<_User> object, BmobException e) {
                if(e==null){
                    String content = holder.CardContent.getText().toString();
                    //查询是否包含当前用户
                    boolean isContain = false;
                    for(_User user : object){
                        String userName = user.getUsername();
                        String currentUserName = currentUser.getUsername();
                        Log.d("Adapter","用户名列表为: "+userName);
                        Log.d("Adapter","当前用户名列表为: "+currentUserName);
                        if(userName.equals(currentUserName)){
                            isContain = true;

                        }
                    }
                    shareList.put(content,isContain);
                    Constants.SHARELIST = shareList;
                }else{
                    Log.i("bmob","失败："+e.getMessage());
                }
            }
        });
    }

    //添加当前用户信息到 收藏 表中
    private void addUserToLikes(Card card){
        BmobRelation relation = new BmobRelation();
        //将当前用户添加到多对多关联中
        relation.add(currentUser);
        //多对多关联指向`Card`的`likes`字段
        card.setLikes(relation);
        card.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("bmob","用户B和该帖子关联成功");
                }else{
                    Log.i("bmob","失败："+e.getMessage());
                }
            }

        });
    }

    //移除当前用户信息到 收藏 表中
    private void removeUserToLikes(Card card){
        BmobRelation relation = new BmobRelation();
        relation.remove(currentUser);
        card.setLikes(relation);
        card.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("bmob","关联关系删除成功");
                }else{
                    Log.i("bmob","失败："+e.getMessage());
                }
            }

        });
    }

    private void addUserToShares(Card card){
        BmobRelation relation = new BmobRelation();
        //将当前用户添加到多对多关联中
        relation.add(currentUser);
        //多对多关联指向`Card`的`likes`字段
        card.setShares(relation);
        card.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("bmob","用户B和该帖子关联成功");
                }else{
                    Log.i("bmob","失败："+e.getMessage());
                }
            }

        });
    }


    private void shareImage(Uri uri){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        //intent.putExtra(Intent.EXTRA_STREAM, bitmap);
        mContext.startActivity(Intent.createChooser(intent,TITLE));
    }

    private void initDialog() {
        Log.d("Adapter","点赞列表为: "+likeList);
        //大图所依附的dialog
        dialog = new Dialog(mContext, R.style.Theme_AppCompat);
        dialog.setContentView(mImageView);
        //大图的点击事件（点击让他消失）
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //大图的长按监听
        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //弹出的“保存图片”的Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setItems(new String[]{mContext.getResources().getString(R.string.save_picture)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //saveCroppedImage(((BitmapDrawable) mImageView.getDrawable()).getBitmap());
                        Bitmap img = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                        GalleryFileSaver.saveBitmapToGallery(mContext,imgName,img);
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    //动态的ImageView
    private ImageView getImageView(Bitmap img){
        ImageView iv = new ImageView(mContext);
        //宽高
        iv.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //设置Padding
        iv.setPadding(20,20,20,20);
        //imageView设置图片
        iv.setImageBitmap(img);
        return iv;
    }

    /**
     * 自己写的加载网络图片的方法
     * img_url 图片的网址
     */
    public void initNetWorkImage(final String imgUrl, final Context context,int flag) {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bitmap = null;
                try {
                    bitmap = Glide.with(context)
                            .asBitmap()
                            .load(imgUrl)
                            .submit(360, 480).get();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                img = bitmap;
                myHandler.sendEmptyMessage(flag);
            }

        }.execute();
    }

}