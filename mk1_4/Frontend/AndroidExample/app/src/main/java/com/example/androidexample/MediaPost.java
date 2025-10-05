package com.example.androidexample;

import android.graphics.Bitmap;

public class MediaPost {
    private String postText;
    private String imageURL;
    private Bitmap rawImage;

    private String userName;
    private int numLikes;

    private int id;

    public MediaPost(int id, String postText, String imageURL, String userName, int numLikes, Bitmap rawImage) {
        this.postText = postText;
        this.imageURL = imageURL;
        this.userName = userName;
        this.id = id;
        this.numLikes = numLikes;
        this.rawImage = rawImage;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUserName() {
        return userName;
    }

    public int getNumLikes() {
        return numLikes;
    }
    public void addLike(){
        this.numLikes += 1;
    }

    public Bitmap getRawImage() {
        return rawImage;
    }

    public int getId(){
        return id;
    }
}
