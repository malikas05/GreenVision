package com.malikas.greenvision.entities;

/**
 * Created by pavle on 2018-03-09.
 */

public class Contributer {

    private String userId;

    public Contributer(){

    }

    public Contributer( String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }



}
