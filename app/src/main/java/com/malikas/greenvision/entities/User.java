package com.malikas.greenvision.entities;


/**
 * Created by Malik on 2018-03-09.
 */

public class User {

    private String personName;
    private String email;
    private String googleId;
    private String photoUrl;

    public User() {
    }

    public User(String personName, String email, String googleId, String photoUrl) {
        this.personName = personName;
        this.email = email;
        this.googleId = googleId;
        this.photoUrl = photoUrl;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
