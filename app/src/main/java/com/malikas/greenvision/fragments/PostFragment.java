package com.malikas.greenvision.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.malikas.greenvision.R;
//import com.malikas.greenvision.viewpagercards.CardPostPagerAdapter;
//import com.malikas.greenvision.viewpagercards.ShadowTransformer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


public class PostFragment extends Fragment {

    public Callbacks listener;
//    private CardPostPagerAdapter postPagerAdapter;
//    private ShadowTransformer mCardShadowTransformer;

//    @BindView(R.id.viewPagerPost)
    ViewPager viewPagerPost;

    public interface Callbacks{
        void changeFragment(int fragmentNum);
    }

    //lifecycle methods
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.listener = (PostFragment.Callbacks)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.fragment_all_posts, container, false);
//        ButterKnife.bind(this, v);

//        postPagerAdapter = new CardPostPagerAdapter();
//        for (CardEmojiItem cardEmojiItem : DataApp.getInstance().getEmojis()){
//            postPagerAdapter.addCardItem(cardEmojiItem);
//        }
//
//        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
//
//        viewPagerPost.setAdapter(postPagerAdapter);
//        viewPagerPost.setPageTransformer(false, mCardShadowTransformer);
//        viewPagerPost.setOffscreenPageLimit(3);
//        viewPagerPost.setCurrentItem(2);

 //      return v;
        return null;
    }
    //

}
