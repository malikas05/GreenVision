package com.malikas.greenvision.entities;

/**
 * Created by pavle on 2018-03-09.
 */

public class Contributer {

    private String postId;
    private String userId;

    public Contributer(){

    }

    public Contributer(String postId, String userId) {
        this.postId = postId;
        this.userId = userId;
    }


    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }



}
