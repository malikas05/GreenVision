package com.malikas.greenvision.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.malikas.greenvision.R;
import com.malikas.greenvision.entities.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by pavle on 2018-03-09.
 */

public class ContributerAdapter extends RecyclerView.Adapter< ContributerAdapter.ContributerViewHolder >{

    private List<User> dataSet;
    private Context context;

    public ContributerAdapter( List<User> dataSet, Context context  ){
        this.dataSet = dataSet;
        this.context = context;
    }

    @Override
    public ContributerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.contributers_view_holder , parent, false);

        ContributerViewHolder viewHolderAll = new ContributerViewHolder(view);

        return viewHolderAll;
    }

    @Override
    public void onBindViewHolder(ContributerViewHolder holder, int position) {

        Picasso.with(this.context).load( this.dataSet.get(position).getPhotoUrl() ).into( holder.userImage );
        holder.username.setText(this.dataSet.get(position).getPersonName());

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ContributerViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView userImage;
        private TextView username;

        public ContributerViewHolder(View itemView) {
            super(itemView);

            userImage = (CircleImageView) itemView.findViewById(R.id.viewHoler_userImage);
            username  = (TextView)  itemView.findViewById(R.id.viewHolder_username);

        }

    }
}
