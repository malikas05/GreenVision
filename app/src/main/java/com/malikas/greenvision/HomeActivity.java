package com.malikas.greenvision;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malikas.greenvision.data.DataApp;
import com.malikas.greenvision.fragments.CreatePostFragment;
import com.malikas.greenvision.fragments.PostDetailsFragment;
import com.malikas.greenvision.fragments.PostFragment;
import com.malikas.greenvision.fragments.UserProfileFragment;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements PostFragment.Callbacks {

    //constants

    // UI variables
    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    private Drawer drawer;
    private AccountHeader headerResult;
    //

    private String personName;
    private String personEmail;
    private String personId;
    private Uri personPhoto;

    //signin variables
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    //

    //firebase realtime db reference
    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    //

    //lifecycle methods
    @Override
    protected void onStart() {
        super.onStart();
        updateUI(currentUser);
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.frame);
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.frame, fragment)
                    .commit();
        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            personName = currentUser.getDisplayName();
            personEmail = currentUser.getEmail();
            personPhoto = currentUser.getPhotoUrl();
            Log.d("photourl", personPhoto.toString());
            DataApp.getInstance().setCurrentUser(currentUser);
        }

        ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem().withName(personName).withEmail(personEmail)
                .withIcon(personPhoto).withIdentifier(1);
        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withTextColor(getResources().getColor(R.color.colorBlack))
                .addProfiles(
                        profileDrawerItem
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();


        //create the drawer and remember the `Drawer` drawer object
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new SecondaryDrawerItem().withIdentifier(1)
                                .withName("Create Post")
                                .withTextColor(getResources().getColor(R.color.colorBlack)),
                        new SecondaryDrawerItem().withIdentifier(2)
                                .withName("Post Details Test")
                                .withTextColor(getResources().getColor(R.color.colorBlack)),
                        new SecondaryDrawerItem().withIdentifier(3)
                                .withName("Display posts")
                                .withTextColor(getResources().getColor(R.color.colorBlack)),
                        new SecondaryDrawerItem().withIdentifier(4)
                                .withName("User Profile")
                                .withTextColor(getResources().getColor(R.color.colorBlack))
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        drawer.closeDrawer();
                        switch ((int)drawerItem.getIdentifier()){
                            case 1:
                                Intent signInActivity = new Intent(HomeActivity.this, CreatePostActivity.class);
                                startActivity(signInActivity);
                                return true;
                            case 2:
                                changeFragment( 3 );
                                return true;
                            case 3:
                                changeFragment( 1 );
                                return true;
                            case 4:
                                changeFragment(4);
                            default:
                                return true;
                        }
                    }
                })
                .build();

    }
    //

    //helper methods
    protected Fragment createFragment(){
        return new PostFragment();
    }

    //change fragment when user chooses different fragments
    @Override
    public void changeFragment(int fragmentNum) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (fragmentNum == 1) {
            ft.replace(R.id.frame, new PostFragment());
        }
        else if (fragmentNum == 2) {
            ft.replace(R.id.frame, new CreatePostFragment());
        }
        else if (fragmentNum == 3) {
            ft.replace(R.id.frame, new PostDetailsFragment());
        }
        else if (fragmentNum == 4) {
            ft.replace(R.id.frame, new UserProfileFragment());
        }
        ft.addToBackStack(null);
        ft.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
    //

    //sign out
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }
    //

    // update UI if user signed out
    public void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
            Intent signInActivity = new Intent(this, SigninActivity.class);
            startActivity(signInActivity);
            finish();
        }
    }
    //
}
