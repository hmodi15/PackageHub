package com.example.androidexample;

public interface MediaPostInteractionListener {
    void onDeletePost(int postId, int position);
    void onUpdatePost(int position, String updatedText);
    void onLikePost(int postId, int position);
}
