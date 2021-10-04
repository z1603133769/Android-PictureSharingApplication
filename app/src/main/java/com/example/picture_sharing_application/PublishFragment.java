package com.example.picture_sharing_application;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class PublishFragment extends Fragment {
    private Context context;
    private View rootView;
    private View cardView;
    private RecyclerView lvCardList;
    private List<Card> CardData;
    private CardAdapter adapter;


    public PublishFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity();
        rootView = inflater.inflate(R.layout.fragment_publish, container, false);
        cardView= inflater.inflate(R.layout.card_item,
                container, false);
        //初始化布局
        initView();
        //初始化对象并保存数据
        initData();
        return rootView;
    }

    //初始化布局
    private void initView() {
        lvCardList = rootView.findViewById(R.id.lv_list2);}

    //初始化对象并保存数据
    private void initData() {
        CardData = new ArrayList<>();
//        StaggeredGridLayoutManager layoutManager =
//                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        GridLayoutManager layoutManager = new GridLayoutManager(this.context,2);
        lvCardList.setLayoutManager(layoutManager);
        adapter = new CardAdapter(context, CardData);
        lvCardList.setAdapter(adapter);
        //获取首页卡片数据列表
        getCardData();
    }

    //从Bmob后台数据库，获取首页卡片数据
    private void getCardData() {
        BmobQuery<Card> query = new BmobQuery<>();
        _User user = BmobUser.getCurrentUser(_User.class);
        String name=user.getUsername();
        query.addWhereEqualTo("username",name);
        query.findObjects(new FindListener<Card>() {
            @Override
            public void done(List<Card> cardData, BmobException e) {
                if (e == null) {
                    Log.i("bmob","查询成功：共" + cardData.size() + "条数据。");
                    adapter.setData(cardData);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("BMOB", e.toString());
                    //Snackbar.make(rootView, e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });


    }



}