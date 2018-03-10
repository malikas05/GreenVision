package com.malikas.greenvision;

import android.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.malikas.greenvision.data.DataApp;
import com.malikas.greenvision.entities.Contributer;
import com.malikas.greenvision.entities.Post;
import com.malikas.greenvision.fragments.CreatePostFragment;
import com.malikas.greenvision.fragments.PostDetailsFragment;
import com.malikas.greenvision.fragments.PostFragment;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreatePostActivity extends AppCompatActivity  {

    private Bitmap photo;
    private LocationManager locationManager;
    private boolean isPermissionGranted = false;
    private Geocoder geocoder;
    private List<Address> addresses;
    private ProgressDialog mProgressDialog;

    //firebase realtime db reference
    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    // Storage Firebase
    private StorageReference mStorageRef;

    // constants
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int GALLERY_PICK = 2;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 3;
    //

    // UI variables
    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.createPostImage)
    ImageView createPostImage;
    @BindView(R.id.createPostTitle)
    EditText createPostTitle;
    @BindView(R.id.createPostDesc)
    EditText createPostDesc;
    @BindView(R.id.createPostLocationText)
    EditText createPostLocationText;
    @BindView(R.id.createPostLocationBtn)
    ImageButton createPostLocationBtn;
    @BindView(R.id.createPostTakePhoto)
    ImageButton createPostTakePhoto;
    @BindView(R.id.createPostChoosePhoto)
    ImageButton createPostChoosePhoto;
    //

    //lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();

        geocoder = new Geocoder(this, Locale.getDefault());
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        checkPermission();
        if (isPermissionGranted){
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    createPostLocationText.setText(getAddress(location));
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            }, null);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("Post");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("post_images");
    }
    //

    //Listeners
    @OnClick(R.id.createPostLocationBtn)
    public void createPostLocationBtnClicked(View view){
        checkPermission();
        if (isPermissionGranted) {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            createPostLocationText.setText(getAddress(location));
        }
    }

    @OnClick(R.id.createPostChoosePhoto)
    public void createPostChoosePhotoClicked(View view){
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        startActivityForResult(i, GALLERY_PICK);
    }

    @OnClick(R.id.createPostTakePhoto)
    public void createPostTakePhotoClicked(View view){
        dispatchTakePictureIntent();
    }


    @OnClick(R.id.btnCreatePost)
    public void btnCreatePostClicked(View view){
        LatLng location = getLocationFromAddress(createPostLocationText.getText().toString());
        if (location != null && checkFields()){
            showProgressDialog();
            uploadPostToFirebase(location);
        }
        else {
            Toast.makeText(this, "Please input the fields.", Toast.LENGTH_SHORT).show();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void uploadPostToFirebase(LatLng location){
        Post post = new Post(createPostTitle.getText().toString(), createPostDesc.getText().toString(),
                createPostLocationText.getText().toString(), location.latitude, location.longitude, DataApp.getInstance().getCurrentUser().getUid() );
        DatabaseReference pushedKey = dbRef.push();
        pushedKey.setValue(post);
        StorageReference image_filePathFB = mStorageRef.child(pushedKey.getKey());
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 25, bao); // bmp is bitmap from user image file
        byte[] byteArray = bao.toByteArray();
        image_filePathFB.putBytes(byteArray).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    // if successful uploading image, getting url for it
                    final String download_url = task.getResult().getDownloadUrl().toString();
                    Toast.makeText(CreatePostActivity.this, download_url, Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }
        });

        Contributer contributer = new Contributer( DataApp.getInstance().getCurrentUser().getUid() );
        DatabaseReference contributeRefrence = FirebaseDatabase.getInstance().getReference().child("Contributer");
        contributeRefrence.child(pushedKey.getKey()).push().child(contributeRefrence.getKey()).setValue( contributer );


    }

    private String getAddress(Location location){
        String address = "";
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            address = addresses.get(0).getAddressLine(0);
            addresses.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null || address.isEmpty()) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {
            Toast.makeText(this, "The address doesn't exist.", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }

        return p1;
    }

    private void imageChosen(){
        createPostImage.setVisibility(View.VISIBLE);
        createPostTakePhoto.setVisibility(View.INVISIBLE);
        createPostChoosePhoto.setVisibility(View.INVISIBLE);
    }
    //

    // callback methods
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            createPostImage.setImageBitmap(photo);
            imageChosen();
        }

        if (requestCode == GALLERY_PICK && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                createPostImage.setImageBitmap(photo);
                imageChosen();
            } catch (IOException e) {
            }
        }
    }
    //

    // Permission for location
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
        else {
            isPermissionGranted = true;
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED){

                isPermissionGranted = true;

            } else {
                Toast.makeText(this, "You must give permissions to use this app.", Toast.LENGTH_SHORT).show();
                isPermissionGranted = false;
            }
        }
    }

    private void checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            getPermissionToLocation();
        else{
            isPermissionGranted = true;
        }
    }
    //

    private boolean checkFields(){
        if (!createPostTitle.getText().toString().isEmpty() && !createPostDesc.getText().toString().isEmpty() &&
                !createPostLocationText.getText().toString().isEmpty() && photo != null)
            return true;
        return false;
    }

    //helper methods
    //show/hide progress dialog
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
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

}
