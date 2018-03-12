package com.malikas.greenvision.entities;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

/**
 * Created by Malik on 2018-03-09.
 */

public class Post {

    private String postId;
    private String title;
    private String description;
    private String address;
    private double lat, lon;
    private String image;
    private String userId;

    private final Object timestamp = ServerValue.TIMESTAMP;

    public Post() {
    }


    public Post(String title, String description, String address, double lat, double lon, String userId) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }



    Object getTimestamp() {
        return timestamp;
    }

    @Exclude
    public long timestamp() {
        return (long) timestamp;
    }
}
