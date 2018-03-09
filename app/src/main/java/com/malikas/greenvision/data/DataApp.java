package com.malikas.greenvision.data;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Malik on 2018-03-09.
 */

public class DataApp {

    private FirebaseUser currentUser;
    private static DataApp instance;

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

}
