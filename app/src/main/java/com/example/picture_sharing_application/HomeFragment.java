package com.example.picture_sharing_application;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Debug;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private RecyclerView lvCardList;
    private List<Card> CardData;
    private int page = 1;
    private int mCurrentColIndex = 0;
    private CardAdapter adapter;
    private Context context = null;
    //home视图
    private View rootView;
    //card视图
    private View cardView;
//    //新闻来源
//    private String source;
//    //下拉组件
//    private SwipeRefreshLayout swipe;
//    //当前新闻页数
//    private int mPage=1;

//    private int[] mCols = new int[]{Constants.Card_COL5,
//            Constants.Card_COL7, Constants.Card_COL8,
//            Constants.Card_COL10, Constants.Card_COL11};

    public HomeFragment(){

    }

    //进行数据请求与解析
    private okhttp3.Callback callback = new okhttp3.Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG, "Failed to connect server!");
            e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response)
                throws IOException {
            if (response.isSuccessful()) {
                Log.d(TAG,"请求成功!!!");
                final String body = response.body().string();
                List<Card> data;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        Type jsonType =
                                new TypeToken<BaseResponse<List<Card>>>() {}.getType();
                        BaseResponse<List<Card>> CardListResponse =
                                gson.fromJson(body, jsonType);
                        for (Card card:CardListResponse.getData()) {
                            Log.d(TAG,"数据为:"+card.getDescription());
                            Log.d(TAG,"头像为:"+card.getHeadPic());
                            card.updateData();
                            adapter.addData(card);
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
            } else {
                Log.d(TAG,"请求失败!!!");
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        rootView = inflater.inflate(R.layout.fragment_home,
                container, false);
        cardView = inflater.inflate(R.layout.card_item,
                container, false);
        //初始化布局
        initView();
        //初始化对象并保存数据
        initData();
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //初始化布局
    private void initView() {
        lvCardList = rootView.findViewById(R.id.lv_card_list);

//        lvCardList.setOnItemClickListener(
//                new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView,
//                                            View view, int i, long l) {
//
//                        Intent intent = new Intent(context,
//                                DetailActivity.class);
//
//                        Card Card = adapter.getItem(i);
//                        Log.d(TAG, "Card为: " + Card);
//                        intent.putExtra(Constants.Card_DETAIL_URL_KEY,
//                                Card.getContentUrl());
//                        intent.putExtra(Constants.GET_Card_KEY,
//                                Card);
//                        startActivity(intent);
//                    }
//                });

//        //下拉功能
//        swipe = rootView.findViewById(R.id.swipe);
//        swipe.setOnRefreshListener(
//                new SwipeRefreshLayout.OnRefreshListener() {
//                    @Override
//                    public void onRefresh() {
//                        downRefreshData();
//                    }
//                });
    }

//    //下拉刷新数据
//    private void downRefreshData() {
//        mPage++;
//        Log.d(TAG,"下拉刷新");
//        adapter.clear();
//        adapter.notifyDataSetChanged();
//        refreshData(mPage,source);
//        swipe.setRefreshing(false);
//    }


    //初始化对象并保存数据
    private void initData() {
        CardData = new ArrayList<>();
//        StaggeredGridLayoutManager layoutManager =
//                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        GridLayoutManager layoutManager = new GridLayoutManager(this.context,2);
        lvCardList.setLayoutManager(layoutManager);
        adapter = new CardAdapter(context, CardData);
        lvCardList.setAdapter(adapter);

        refreshData(1);
    }

    //实际的API请求(子线程)(异步请求)
    private void refreshData(final int page) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CardRequest requestObj = new CardRequest();

                requestObj.setNum(Constants.CARD_NUM);
                requestObj.setPage(page);
                String urlParams = requestObj.toString();

                Log.d(TAG,"地址为:"+Constants.GENERAL_CARD_URL + urlParams);
                Request request = new Request.Builder()
                        .url(Constants.GENERAL_CARD_URL + urlParams)
                        .get().build();
                try {
                    OkHttpClient client = new OkHttpClient();
                    client.newCall(request).enqueue(callback);
                } catch (NetworkOnMainThreadException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

}