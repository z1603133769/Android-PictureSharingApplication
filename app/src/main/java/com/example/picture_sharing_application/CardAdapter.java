package com.example.picture_sharing_application;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
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
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

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

        //handler 用于线程间的通信
        //当点击图片时，通过url加载图片，并显示大图
        private Handler myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
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

            public ViewHolder(View view){
                super(view);
                CardImage = view.findViewById(R.id.iv_image);
                CardContent = view.findViewById(R.id.tv_content);
                HeadPic = view.findViewById(R.id.iv_head);
                NickName = view.findViewById(R.id.tv_name);
                LikeNumber = view.findViewById(R.id.tv_likeNumber);
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

            //图片监听事件
            holder.CardImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getAdapterPosition();
                    Card card = mCardList.get(position);
                    String imgUrl = card.getPicture().getUrl();
                    //获取img名称
                    imgName = card.getDescription();
                    //通过url,加载图片
                    initNetWorkImage(imgUrl,mContext);
                    Log.d("Adapter","图片地址为: "+ imgUrl);
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Card card = mCardList.get(position);
            String imgUrl = card.getPicture().getUrl();
            String headUrl = card.getHeadPicture().getUrl();

            Log.d("Adapter","图片地址为:"+imgUrl);
            Log.d("Adapter","头像地址为:"+headUrl);
            //图片
            Glide.with(mContext).load(imgUrl)
                    .into(holder.CardImage);
            //内容
            holder.CardContent.setText(card.getDescription());
            //头像
            Glide.with(mContext).load(headUrl)
                    .into(holder.HeadPic);
            //昵称
            holder.NickName.setText(card.getNickName());
            //收藏数
            String likeNumber = card.getLikeNumber().toString();
            holder.LikeNumber.setText(likeNumber);
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

    private void initDialog() {
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
    public void initNetWorkImage(final String imgUrl, final Context context) {
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
                myHandler.sendEmptyMessage(0);
            }

        }.execute();
    }

}
