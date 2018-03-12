package com.malikas.greenvision.data;

import com.google.firebase.auth.FirebaseUser;
import com.malikas.greenvision.entities.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Malik on 2018-03-09.
 */

public class DataApp {

    private static DataApp instance;
    private String postId;

    private FirebaseUser currentUser;

    public static DataApp getInstance(){
        if (instance == null){
            instance = new DataApp();
        }
        return instance;
    }

    private DataApp(){
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(FirebaseUser currentUser) {
        this.currentUser = currentUser;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
