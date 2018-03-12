package com.malikas.greenvision.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.malikas.greenvision.R;
import com.malikas.greenvision.data.DataApp;
import com.malikas.greenvision.entities.Post;
import com.malikas.greenvision.viewpagercards.CardAdapter;
import com.malikas.greenvision.viewpagercards.ShadowTransformer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostFragment extends Fragment {

    public Callbacks listener;
    private CardPostPagerAdapter postPagerAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private ProgressDialog mProgressDialog;
    private int itemPosition = 0;
    private String mLastKey = "", mPrevKey = "";

    //firebase realtime db reference
    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    // Storage Firebase
    private StorageReference mStorageRef;

    @BindView(R.id.viewPagerPost)
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
        View v = inflater.inflate(R.layout.fragment_all_posts, container, false);
        ButterKnife.bind(this, v);

        postPagerAdapter = new CardPostPagerAdapter(getContext());
        mCardShadowTransformer = new ShadowTransformer(viewPagerPost, postPagerAdapter);

        showProgressDialog();

        loadPosts();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        viewPagerPost.setAdapter(postPagerAdapter);
        viewPagerPost.setPageTransformer(false, mCardShadowTransformer);
        viewPagerPost.setOffscreenPageLimit(3);
        viewPagerPost.setCurrentItem(2);

        viewPagerPost.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                if (position == postPagerAdapter.getCount() - 2) {
//                    loadMorePosts();
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        hideProgressDialog();

        return v;
    }
    //

    private void loadMorePosts(){
        showProgressDialog();
        Query postQuery = dbRef.orderByChild("timestamp").endAt(mLastKey).limitToLast(10);
        postQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long size = dataSnapshot.getChildrenCount();
                int count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Post post = snapshot.getValue(Post.class);
                    StorageReference imageRef = mStorageRef.child("post_images/"+snapshot.getKey());
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            post.setImage(uri.toString());
                            postPagerAdapter.addCardItem(post);
                            postPagerAdapter.notifyDataSetChanged();
                            Log.d("postimageurl", uri.toString());
                        }
                    });

                    if (!mPrevKey.equals(dataSnapshot.getKey())) {
                        postPagerAdapter.addCardItemToEnd(post, size - count++);
                        itemPosition++;
                    }
                    else {
                        mPrevKey = mLastKey;
                    }

                    if (itemPosition == 1){
                        mLastKey = dataSnapshot.getKey();
                    }
                }
                postPagerAdapter.notifyDataSetChanged();
                hideProgressDialog();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void loadPosts(){
        showProgressDialog();
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("Post");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> iterableData = dataSnapshot.getChildren();
                Iterator<DataSnapshot> it = iterableData.iterator();



                while( it.hasNext() ) {

                    final DataSnapshot ds = it.next();

                    final Post post = ds.getValue( Post.class );
                    post.setPostId(ds.getKey());

                    StorageReference imageRef = mStorageRef.child("post_images/"+ds.getKey());
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            post.setImage(uri.toString());
                            postPagerAdapter.addCardItem(post);
                            postPagerAdapter.notifyDataSetChanged();
                            Log.d("postimageurl", uri.toString());
                        }
                    });

                }


                //for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    itemPosition++;
//
//                    if (itemPosition == 1){
//                        mLastKey = dataSnapshot.getKey();
//                        mPrevKey = dataSnapshot.getKey();
//                    }

                    // Post post = snapshot.getValue(Post.class);

//                    StorageReference imageRef = mStorageRef.child("post_images/"+snapshot.getKey());
//                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            post.setImage(uri.toString());
//                            postPagerAdapter.addCardItem(post);
//                            postPagerAdapter.notifyDataSetChanged();
//                            Log.d("postimageurl", uri.toString());
//                        }
//                    });
//                    postPagerAdapter.addCardItem(post);
                //    postPagerAdapter.notifyDataSetChanged();
                //}
                //hideProgressDialog();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //helper methods
    //show/hide progress dialog
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    //

    public class CardPostPagerAdapter extends PagerAdapter implements CardAdapter {

        private List<CardView> mViews;
        private List<Post> mData;
        private float mBaseElevation;
        private Context context;

        public CardPostPagerAdapter(Context context) {
            this.context = context;
            mData = new ArrayList<>();
            mViews = new ArrayList<>();
        }

        public void addCardItem(Post item) {
            mViews.add(null);
            mData.add(item);
        }

        public void addCardItemToEnd(Post item, long pos) {
            mViews.add(null);
            mData.add((int) pos, item);
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

        private void bind(final Post item, View view) {
            final RelativeLayout postLayout = (RelativeLayout) view.findViewById(R.id.postLayout);
            TextView postTitle = (TextView) view.findViewById(R.id.postTitle);
            postTitle.setText(item.getTitle());
            postLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataApp.getInstance().setPostId(item.getPostId());
                    listener.changeFragment(3);
                }
            });
            final ImageView img = new ImageView(context);
            Picasso.with(img.getContext())
                    .load(item.getImage())
                    .into(img, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            postLayout.setBackground(img.getDrawable());
                        }

                        @Override
                        public void onError() {
                        }
                    });
        }


    }

}
