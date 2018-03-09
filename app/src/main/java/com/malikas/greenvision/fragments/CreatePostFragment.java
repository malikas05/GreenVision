package com.malikas.greenvision.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.malikas.greenvision.R;
import com.malikas.greenvision.data.DataApp;
import com.malikas.greenvision.entities.Post;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by Malik on 2018-03-09.
 */

public class CreatePostFragment extends Fragment {

    public PostFragment.Callbacks listener;
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
    @BindView(R.id.createPostImage)
    ImageView createPostImage;
    @BindView(R.id.createPostTitle)
    EditText createPostTitle;
    @BindView(R.id.createPostDesc)
    EditText createPostDesc;
    @BindView(R.id.createPostLocationText)
    EditText createPostLocationText;
    @BindView(R.id.createPostLocationBtn)
    Button createPostLocationBtn;
    @BindView(R.id.createPostTakePhoto)
    Button createPostTakePhoto;
    @BindView(R.id.createPostChoosePhoto)
    Button createPostChoosePhoto;
    //

    //lifecycle methods
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.listener = (PostFragment.Callbacks) context;
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

    @Override
    public void onStart() {
        super.onStart();

        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        checkPermission();
        if (isPermissionGranted){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, new LocationListener() {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_post, container, false);
        ButterKnife.bind(this, v);

        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("Post");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("post_images");



        return v;
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


    @OnClick(R.id.createPostSubmit)
    public void createPostSubmitClicked(View view){
        LatLng location = getLocationFromAddress(createPostLocationText.getText().toString());
        if (location != null && checkFields()){
//            Toast.makeText(getContext(), location.latitude + location.longitude + "", Toast.LENGTH_SHORT).show();
            showProgressDialog();
            uploadPostToFirebase(location);
        }
        else {
            Toast.makeText(getContext(), "Please input the fields.", Toast.LENGTH_SHORT).show();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void uploadPostToFirebase(LatLng location){
        Post post = new Post(createPostTitle.getText().toString(), createPostDesc.getText().toString(),
                createPostLocationText.getText().toString(), location.latitude, location.longitude);
        DatabaseReference pushedKey = dbRef.push();
        pushedKey.setValue(post);
        StorageReference image_filePathFB = mStorageRef.child(pushedKey.getKey() + ".jpg");
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, bao); // bmp is bitmap from user image file
        byte[] byteArray = bao.toByteArray();
        image_filePathFB.putBytes(byteArray).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    // if successful uploading image, getting url for it
                    final String download_url = task.getResult().getDownloadUrl().toString();
                    Toast.makeText(getContext(), download_url, Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }
        });
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

        Geocoder coder = new Geocoder(getActivity());
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
            Toast.makeText(getContext(), "The address doesn't exist.", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }

        return p1;
    }
    //

    // callback methods
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            createPostImage.setImageBitmap(photo);
        }

        if (requestCode == GALLERY_PICK && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                createPostImage.setImageBitmap(photo);
            } catch (IOException e) {
            }
        }
    }
    //

    // Permission for location
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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
                Toast.makeText(getActivity(), "You must give permissions to use this app.", Toast.LENGTH_SHORT).show();
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

}
