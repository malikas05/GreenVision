package com.malikas.greenvision.entities;

/**
 * Created by Malik on 2018-03-09.
 */

public class Contribution {

    private String postId;
    private String userId;

    public Contribution() {
    }

    public Contribution(String postId, String userId) {
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
