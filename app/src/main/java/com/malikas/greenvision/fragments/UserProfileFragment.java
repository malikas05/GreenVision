package com.malikas.greenvision.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.malikas.greenvision.R;
import com.malikas.greenvision.adapters.ContributerAdapter;
import com.malikas.greenvision.adapters.ProfilePostAdapter;
import com.malikas.greenvision.data.DataApp;
import com.malikas.greenvision.entities.Contributer;
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


    public static String userKey = "viiZqsKtHyRPELCeqX7ZhSmW7Di2";

    //lifecycle methods
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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


        dataset = new ArrayList<>();
        postViews = (RecyclerView) v.findViewById(R.id.profilePostList);
        profilePostAdapter = new ProfilePostAdapter( dataset ,getContext());

        postViews.setLayoutManager( new StaggeredGridLayoutManager( 2 , 1) );
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

                mDatabase = FirebaseDatabase.getInstance().getReference().child("Post");
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> iterableSnap = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> it = iterableSnap.iterator();

                        while( it.hasNext() ) {
                            Post post = it.next().getValue(Post.class);
                            if( post.getUserId() == userKey ) {
                                dataset.add( post );
                                profilePostAdapter.notifyDataSetChanged();
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



}
