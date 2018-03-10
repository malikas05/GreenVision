package com.malikas.greenvision.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malikas.greenvision.R;
import com.malikas.greenvision.entities.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by pavle on 2018-03-10.
 */

public class ProfilePostAdapter extends RecyclerView.Adapter< ProfilePostAdapter.ProfilePostViewHolder >{

    private List<Post> dataset;
    private Context context;

    public ProfilePostAdapter( List<Post> dataset , Context context ){
        this.dataset = dataset;
        this.context = context;
    }

    @Override
    public ProfilePostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.profile_post_view_holder , parent, false);

        ProfilePostViewHolder viewHolderAll = new ProfilePostViewHolder(view);

        return viewHolderAll;

    }

    @Override
    public void onBindViewHolder(ProfilePostViewHolder holder, int position) {

        Picasso.with(context).load(this.dataset.get(position).getImage()).into(holder.postImage);
        holder.postTitle.setText( this.dataset.get(position).getTitle() );
        holder.postWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ProfilePostViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout postWrap;
        private TextView postTitle;
        private ImageView postImage;

        public ProfilePostViewHolder(View itemView) {
            super(itemView);

            postWrap = itemView.findViewById(R.id.postWrap);
            postTitle = itemView.findViewById(R.id.postTitle);
            postImage = itemView.findViewById(R.id.postImage);

        }

    }

}
