package com.malikas.greenvision.viewpagercards;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.malikas.greenvision.R;
import com.malikas.greenvision.entities.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Malik on 2018-03-09.
 */

public class CardPostPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<Post> mData;
    private float mBaseElevation;

    public CardPostPagerAdapter() {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(Post item) {
        mViews.add(null);
        mData.add(item);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.adapter_post, container, false);
        container.addView(view);
        bind(mData.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(Post item, View view) {
        ImageView postImage = (ImageView) view.findViewById(R.id.postImage);
        TextView postTitle = (TextView) view.findViewById(R.id.postTitle);
        Button postOpen = (Button) view.findViewById(R.id.postOpen);
        postTitle.setText(item.getTitle());
        postOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
