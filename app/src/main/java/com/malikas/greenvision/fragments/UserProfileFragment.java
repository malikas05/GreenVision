package com.malikas.greenvision.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.malikas.greenvision.R;
import com.malikas.greenvision.data.DataApp;
import com.malikas.greenvision.entities.Post;
import com.malikas.greenvision.entities.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Malik on 2018-03-09.
 */

public class UserProfileFragment extends Fragment {

    private DatabaseReference mDatabase;

    private StorageReference mStorageRef;

    private RecyclerView postViews;
    private ProfilePostAdapter profilePostAdapter;
    private List<Post> dataset;

    private ImageView profilePicture;
    private TextView  profileName;
    private TextView  profileEmail;

    public PostFragment.Callbacks listener;


    public static String userKey = DataApp.getInstance().getCurrentUser().getUid();

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
        View v = inflater.inflate(R.layout.user_profile_fragment, container, false);
        ButterKnife.bind(this, v);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        dataset = new ArrayList<>();
        postViews = (RecyclerView) v.findViewById(R.id.profilePostList);
        profilePostAdapter = new ProfilePostAdapter( dataset ,getContext());

        postViews.setLayoutManager( new GridLayoutManager(getActivity(), 2) );
        postViews.setAdapter( profilePostAdapter );


        profilePicture = (ImageView) v.findViewById(R.id.profilePicture);
        profileEmail   = (TextView)  v.findViewById(R.id.profileEmail);
        profileName    = (TextView)  v.findViewById(R.id.profileName);

        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(userKey);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user =  dataSnapshot.getValue( User.class );

                Picasso.with(getContext()).load(user.getPhotoUrl()).into( profilePicture );
                profileEmail.setText( user.getEmail() );
                profileName.setText( user.getPersonName() );

                mDatabase = FirebaseDatabase.getInstance().getReference("Post");
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> iterableSnap = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> it = iterableSnap.iterator();

                        while( it.hasNext() ) {

                            DataSnapshot ds = it.next();

                            final Post post = ds.getValue(Post.class);
                            if( post.getUserId().equals(userKey)) {
                                post.setPostId(ds.getKey());

                                StorageReference imageRef = mStorageRef.child("post_images/"+post.getPostId());
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        post.setImage(uri.toString());
                                        dataset.add( post );
                                        profilePostAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }

    public class ProfilePostAdapter extends RecyclerView.Adapter<ProfilePostAdapter.ProfilePostViewHolder>{

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
        public void onBindViewHolder(ProfilePostViewHolder holder, final int position) {

            Picasso.with(context).load(this.dataset.get(position).getImage()).into(holder.postImage);
            holder.postTitle.setText( this.dataset.get(position).getTitle() );
            holder.postWrap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataApp.getInstance().setPostId(dataset.get(position).getPostId());
                    listener.changeFragment(3);
                }
            });

        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }

        public class ProfilePostViewHolder extends RecyclerView.ViewHolder{

            RelativeLayout postWrap;
            TextView postTitle;
            ImageView postImage;

            public ProfilePostViewHolder(View itemView) {
                super(itemView);

                postWrap = itemView.findViewById(R.id.postWrap);
                postTitle = itemView.findViewById(R.id.postTitle);
                postImage = itemView.findViewById(R.id.postImage);

            }

        }

    }


}
