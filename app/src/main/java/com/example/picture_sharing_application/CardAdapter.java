package com.example.picture_sharing_application;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>{
        Bitmap bitmap;
        private List<Card> mCardList;
        private Context mContext;
        private String imgName;
        //大图会话
        private Dialog dialog;
        //显示大图
        private ImageView mImageView;

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
                    String imgUrl = card.getPicUrl();
                    Log.d("Adapter","图片地址为: "+ imgUrl);
                    //
                    holder.CardImage.setDrawingCacheEnabled(true);
                    //之前的已经回收掉了，必须要先创建好
                    Bitmap img =  Bitmap.createBitmap(holder.CardImage.getDrawingCache());
                    holder.CardImage.setDrawingCacheEnabled(false);
                    //获取img名称
                    imgName = card.getDescription();
                    //测试
                    Log.d("Adapter","img的名称为: "+imgName);
                    Log.d("Adapter","img的图片为: "+img);
                    mImageView = getImageView(img);
                    //初始化会话
                    initDialog();
                    //显示会话
                    dialog.show();
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Card card = mCardList.get(position);
            Log.d("Adapter","图片地址为:"+card.getPicUrl());
            Log.d("Adapter","头像地址为:"+card.getHeadPic());
            //图片
            Glide.with(mContext).load(card.getPicUrl())
                    .into(holder.CardImage);
            //内容
            holder.CardContent.setText(card.getDescription());
            //头像
            if(card.getHeadPic() != null){
                Glide.with(mContext).load(card.getHeadPic())
                        .into(holder.HeadPic);
            }
            //昵称
            holder.NickName.setText(card.getNickName());
            //收藏数
            holder.LikeNumber.setText(card.getLikeName());

        }

        @Override
        public int getItemCount() {
            return mCardList.size();
        }

        public void addData(Card card){
            mCardList.add(card);
            //notifyDataSetChanged();
        }

    private void initDialog() {
//        mImageView = getImageView();
        //大图所依附的dialog
        dialog = new Dialog(mContext, R.style.Theme_AppCompat_Light_Dialog);
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

}
