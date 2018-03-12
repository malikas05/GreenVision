package com.malikas.greenvision.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.malikas.greenvision.data.DataApp;
import com.malikas.greenvision.entities.Contributer;
import com.malikas.greenvision.entities.Post;
import com.malikas.greenvision.entities.User;
import com.malikas.greenvision.viewpagercards.CardAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Malik on 2018-03-09.
 */

public class PostDetailsFragment extends Fragment {

    public static final String POST_ID = "1";
    private DatabaseReference mDatabase;

    StorageReference mStorageRef;

    private boolean alreadyContr = false;

    private TextView   postTitle;
    private TextView   postDescription;
    private ImageButton postContribute;
    private ImageView  postImage;
    private TextView   postAddress;
    private ImageButton     postAddressButton;

    private TextView   postUserUsername;
    private CircleImageView postUserImage;

    private RecyclerView postContributersList;
    private ContributerAdapter contributerAdapter;
    private List<User> dataset;

    public PostFragment.Callbacks listener;
    String postID;

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

        postID = getArguments().getString(POST_ID);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.post_detail_fragment, container, false);
        ButterKnife.bind(this, v);

        dataset = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Post/"+postID);


        postTitle = (TextView) v.findViewById(R.id.postTitle);
        postDescription = (TextView) v.findViewById(R.id.postDescription);
        postContribute = (ImageButton) v.findViewById(R.id.postContributeButton);
        postImage = (ImageView) v.findViewById(R.id.postImage);
        postAddressButton = (ImageButton) v.findViewById(R.id.postGoToAddressMap);

        postUserUsername = (TextView) v.findViewById(R.id.postUserUsername);
        postUserImage = (CircleImageView) v.findViewById(R.id.postUserImage);

        postContributersList = ( RecyclerView ) v.findViewById(R.id.postAllContributers);
        postContributersList.setLayoutManager( new LinearLayoutManager(getActivity()));
        contributerAdapter   = new ContributerAdapter( dataset , getContext() );
        postContributersList.setAdapter(contributerAdapter);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Post post = dataSnapshot.getValue( Post.class );

                postTitle.setText( post.getTitle() );
                postDescription.setText( post.getDescription() );
//                postAddress.setText( post.getAddress() );

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users/"+post.getUserId());
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue( User.class );

                        postUserUsername.setText( user.getPersonName() );

                        Picasso.with(getContext()).load( user.getPhotoUrl() ).into( postUserImage );

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                mStorageRef = FirebaseStorage.getInstance().getReference().child("post_images/"+dataSnapshot.getKey());

                //final long ONE_MEGABYTE = 1024 * 1024 * 10;
                //mStorageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                //    @Override
                //    public void onSuccess(byte[] bytes) {
                //        postImage.setImageBitmap(BitmapFactory.decodeByteArray( bytes , 0 , bytes.length ));
//
//
                //    }
                //}).addOnFailureListener(new OnFailureListener() {
                //    @Override
                //    public void onFailure(@NonNull Exception exception) {
                //        // Handle any errors
                //    }
                //});

                mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png
                        // Pass it to Picasso to download, show in ImageView and caching
                        Picasso.with(getContext()).load(uri.toString()).into(postImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child( "Contributer/"+postID );
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> contributerIterable = dataSnapshot.getChildren();
                Iterator<DataSnapshot> it = contributerIterable.iterator();


                while( it.hasNext() ){

                    Contributer contributer = it.next().child("Contributer").getValue( Contributer.class );

                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users/"+contributer.getUserId());

                   // Toast.makeText(getContext(), "worked?", Toast.LENGTH_SHORT).show();

                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue( User.class );
                            dataset.add(user);
                          //  Toast.makeText( getContext(), dataset.get(0).getPersonName() , Toast.LENGTH_LONG ).show();
                            contributerAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        checkForContribution();

        postContribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mDatabase = FirebaseDatabase.getInstance().getReference().child("Contributer/"+postID);
//                mDatabase.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Iterable<DataSnapshot> contributerIterable = dataSnapshot.getChildren();
//                        Iterator<DataSnapshot> it = contributerIterable.iterator();
//
//                        boolean alreadyContributing = false;
//                        String userId = DataApp.getInstance().getCurrentUser().getUid();
//                        while( it.hasNext() && !alreadyContributing ){
//                            Contributer contributer = it.next().child("Contributer").getValue( Contributer.class );
//                            if( contributer.getUserId().equals(userId)) {
//                                alreadyContributing = true;
//                            }
//                        }
//
//                        if( !alreadyContributing ) {
//                            postContribute.setBackgroundResource(R.drawable.btn_upvote_green);
//                            Contributer contributer = new Contributer(DataApp.getInstance().getCurrentUser().getUid());
//                            DatabaseReference contributeRefrence = FirebaseDatabase.getInstance().getReference().child("Contributer");
//                            contributeRefrence.child(postID).push().child(contributeRefrence.getKey()).setValue(contributer);
//                        }
//                        else {
//                            Toast.makeText(getContext() , "You are already contributing to the post!" , Toast.LENGTH_LONG).show();
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
                // check if the user is already a contributer,
                // option one : loop through all contributer data looking for a user ID ? maybe 50 -1000 contributers?


                if (alreadyContr){
                    Toast.makeText(getContext() , "You are already contributing to the post!" , Toast.LENGTH_LONG).show();
                }
                else {
                    postContribute.setBackgroundResource(R.drawable.btn_upvote_green);
                    Contributer contributer = new Contributer(DataApp.getInstance().getCurrentUser().getUid());
                    DatabaseReference contributeRefrence = FirebaseDatabase.getInstance().getReference().child("Contributer");
                    contributeRefrence.child(postID).push().child(contributeRefrence.getKey()).setValue(contributer);
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users/"+contributer.getUserId());
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue( User.class );
                            dataset.add(user);
                            //  Toast.makeText( getContext(), dataset.get(0).getPersonName() , Toast.LENGTH_LONG ).show();
                            contributerAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }
        });


        return v;
    }

    private void checkForContribution(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Contributer/"+postID);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> contributerIterable = dataSnapshot.getChildren();
                Iterator<DataSnapshot> it = contributerIterable.iterator();

                boolean alreadyContributing = false;
                String userId = DataApp.getInstance().getCurrentUser().getUid();
                while( it.hasNext() && !alreadyContributing ){
                    Contributer contributer = it.next().child("Contributer").getValue( Contributer.class );
                    if( contributer.getUserId().equals(userId)) {
                        alreadyContributing = true;
                    }
                }

                if( !alreadyContributing ) {
                    postContribute.setBackgroundResource(R.drawable.btn_upvote_red);
                    contributerAdapter.notifyDataSetChanged();
                }
                else {
                    postContribute.setBackgroundResource(R.drawable.btn_upvote_green);
                }
                alreadyContr = alreadyContributing;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static PostDetailsFragment newInstance(String postId){
        Bundle args = new Bundle();
        args.putSerializable(POST_ID, postId);
        PostDetailsFragment fragment = new PostDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
