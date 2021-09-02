package com.example.picture_sharing_application;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>{

        private List<Card> mCardList;
        private Context mContext;

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
}
