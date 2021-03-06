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
    //????????????
    private Dialog dialog;
    //????????????
    private ImageView mImageView;
    //???????????????????????????
    private Map<String, Boolean> likeList = new HashMap<>();
    private _User currentUser;
    private int likeNumber = 0;
    private Map<String, Boolean> shareList = new HashMap<>();
    private ViewHolder targetHolder;
    //????????????
    public static String TITLE = "PiliPili??????????????????";

    //handler ????????????????????????
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //???????????????????????????url??????????????????????????????
                case 0:
                    //??????
                    Log.d("Adapter","img????????????: "+imgName);
                    Log.d("Adapter","img????????????: "+img);
                    mImageView = getImageView(img);
                    //???????????????
                    initDialog();
                    //????????????
                    dialog.show();
                    break;
                //????????????
                case 1:
                    Uri uri =  Uri.parse(MediaStore.Images.Media.insertImage(mContext.getContentResolver(), img, null,null));;
                    shareImage(uri);
                    //??????????????????
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
        Log.d("Adapter","?????????????????????");
        //super();
        mContext =context;
        mCardList = CardList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("Adapter","???????????????????????????");
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.card_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        //??????????????????
        currentUser = BmobUser.getCurrentUser(_User.class);

        //??????????????????
        holder.CardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Card card = mCardList.get(position);
                String imgUrl = null;
                if(card.getPicture()!=null){
                    imgUrl = card.getPicture().getUrl();
                }
                //??????img??????
                imgName = card.getDescription();
                //??????url,????????????
                if(imgUrl!=null){
                    initNetWorkImage(imgUrl,mContext,0);
                }
                //??????????????????
                else{
                    BitmapDrawable bd = (BitmapDrawable)mContext.getResources().getDrawable(R.drawable.loadingfail);
                    img = bd.getBitmap();
                    mImageView = getImageView(img);
                    //???????????????
                    initDialog();
                    //????????????
                    dialog.show();
                }
                Log.d("Adapter","???????????????: "+ imgUrl);
            }
        });

        //??????????????????
        holder.LikeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Card card = mCardList.get(position);
                String content = holder.CardContent.getText().toString();
                int likes = Integer.parseInt(holder.LikeNumber.getText().toString());
                boolean flag = likeList.get(content);
                if(flag){
                    //??????????????? --> ???????????????
                    flag = !flag;
                    likeList.put(content,flag);
                    //????????????
                    holder.LikeIcon.setImageResource(R.drawable.love);
                    //??????????????????
                    likes -= 1;
                    String like_Number =  String.valueOf(likes);
                    holder.LikeNumber.setText(like_Number);
                    //??????????????????
                    removeUserToLikes(card);
                }else{
                    //??????????????? --> ???????????????
                    flag = !flag;
                    likeList.put(content,flag);
                    //????????????
                    holder.LikeIcon.setImageResource(R.drawable.love_a);
                    //??????????????????
                    likes += 1;
                    String like_Number =  String.valueOf(likes);
                    holder.LikeNumber.setText(like_Number);
                    //??????????????????
                    addUserToLikes(card);
                }
            }
        });

        //??????????????????
        holder.ShareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Card card = mCardList.get(position);
                String content = holder.CardContent.getText().toString();
                shareList.put(content,true);
                String url = card.getPicture().getUrl();
                addUserToShares(card);


                //????????????
                initNetWorkImage(url,mContext,1);
                //?????????????????????
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
//        //??????????????????
//        Constants.LIKELIST = likeList;

        //????????????????????????????????????
        queryLikesUser(holder,card);
        queryShareUser(holder,card);

        Log.d("Adapter","???????????????:"+imgUrl);
        Log.d("Adapter","???????????????:"+headUrl);
        Log.d("Adapter","???????????????:"+card.getNickName());

        //??????
        if(imgUrl!=null){
            Glide.with(mContext).load(imgUrl)
                    .into(holder.CardImage);
        }
        //??????
        if(card.getDescription()!=null){
            String description = "#"+card.getDescription()+"#";
            holder.CardContent.setText(description);
        }
        //??????
        if(headUrl!=null){
            Glide.with(mContext).load(headUrl)
                    .into(holder.HeadPic);
        }
        //??????
        if(card.getNickName()!= ""){
            holder.NickName.setText(card.getNickName());
        }
//            //?????????
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

    //????????????????????????????????????????????????
    private void queryLikesUser(ViewHolder holder,Card card){
        // ?????????????????????????????????????????????????????????????????????
        BmobQuery<_User> query = new BmobQuery<_User>();
        //likes???Card????????????????????????????????????????????????????????????
        query.addWhereRelatedTo("likes", new BmobPointer(card));
        query.findObjects(new FindListener<_User>() {
            @Override
            public void done(List<_User> object, BmobException e) {
                if(e==null){
                    likeNumber = object.size();
                    String content = holder.CardContent.getText().toString();
                    Log.i("bmob","??????????????????"+content);
                    Log.i("bmob","???????????????"+object.size());
                    //???????????????
                    String like_Number =  String.valueOf(likeNumber);;
                    holder.LikeNumber.setText(like_Number);
                    //??????????????????????????????
                    boolean isContain = false;
                    for(_User user : object){
                        String userName = user.getUsername();
                        String currentUserName = currentUser.getUsername();
                        Log.d("Adapter","??????????????????: "+userName);
                        Log.d("Adapter","????????????????????????: "+currentUserName);
                        if(userName.equals(currentUserName)){
                            isContain = true;
                        }
                    }
                    Log.d("Adapter","???????????????: "+object);
                    Log.d("Adapter","???????????????: "+currentUser);
                    Log.i("bmob",   "??????????????????"+isContain);
                    likeList.put(content,isContain);
                    Constants.LIKELIST = likeList;
                    //?????????????????????
                    if(isContain){
                        holder.LikeIcon.setImageResource(R.drawable.love_a);
                    }
                    //myHandler.sendEmptyMessage(1);
                }else{
                    Log.i("bmob","?????????"+e.getMessage());
                }
            }
        });
    }

    private void queryShareUser(ViewHolder holder,Card card){
        // ?????????????????????????????????????????????????????????????????????
        BmobQuery<_User> query = new BmobQuery<_User>();
        //likes???Card????????????????????????????????????????????????????????????
        query.addWhereRelatedTo("shares", new BmobPointer(card));
        query.findObjects(new FindListener<_User>() {
            @Override
            public void done(List<_User> object, BmobException e) {
                if(e==null){
                    String content = holder.CardContent.getText().toString();
                    //??????????????????????????????
                    boolean isContain = false;
                    for(_User user : object){
                        String userName = user.getUsername();
                        String currentUserName = currentUser.getUsername();
                        Log.d("Adapter","??????????????????: "+userName);
                        Log.d("Adapter","????????????????????????: "+currentUserName);
                        if(userName.equals(currentUserName)){
                            isContain = true;

                        }
                    }
                    shareList.put(content,isContain);
                    Constants.SHARELIST = shareList;
                }else{
                    Log.i("bmob","?????????"+e.getMessage());
                }
            }
        });
    }

    //??????????????????????????? ?????? ??????
    private void addUserToLikes(Card card){
        BmobRelation relation = new BmobRelation();
        //??????????????????????????????????????????
        relation.add(currentUser);
        //?????????????????????`Card`???`likes`??????
        card.setLikes(relation);
        card.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("bmob","??????B????????????????????????");
                }else{
                    Log.i("bmob","?????????"+e.getMessage());
                }
            }

        });
    }

    //??????????????????????????? ?????? ??????
    private void removeUserToLikes(Card card){
        BmobRelation relation = new BmobRelation();
        relation.remove(currentUser);
        card.setLikes(relation);
        card.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("bmob","????????????????????????");
                }else{
                    Log.i("bmob","?????????"+e.getMessage());
                }
            }

        });
    }

    private void addUserToShares(Card card){
        BmobRelation relation = new BmobRelation();
        //??????????????????????????????????????????
        relation.add(currentUser);
        //?????????????????????`Card`???`likes`??????
        card.setShares(relation);
        card.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("bmob","??????B????????????????????????");
                }else{
                    Log.i("bmob","?????????"+e.getMessage());
                }
            }

        });
    }

    //????????????
    private void shareImage(Uri uri){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        //intent.putExtra(Intent.EXTRA_STREAM, bitmap);
        mContext.startActivity(Intent.createChooser(intent,TITLE));
    }

    private void initDialog() {
        Log.d("Adapter","???????????????: "+likeList);
        //??????????????????dialog
        dialog = new Dialog(mContext, R.style.Theme_AppCompat);
        dialog.setContentView(mImageView);
        //?????????????????????????????????????????????
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //?????????????????????
        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //??????????????????????????????Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setItems(new String[]{mContext.getResources().getString(R.string.save_picture)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //saveCroppedImage(((BitmapDrawable) mImageView.getDrawable()).getBitmap());
                        Bitmap img = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                        //????????????
                        GalleryFileSaver.saveBitmapToGallery(mContext,imgName,img);
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    //?????????ImageView
    private ImageView getImageView(Bitmap img){
        ImageView iv = new ImageView(mContext);
        //??????
        iv.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //??????Padding
        iv.setPadding(20,20,20,20);
        //imageView????????????
        iv.setImageBitmap(img);
        return iv;
    }

    /**
     * ???????????????????????????????????????
     * img_url ???????????????
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