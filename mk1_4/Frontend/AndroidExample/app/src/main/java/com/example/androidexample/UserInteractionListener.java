package com.example.androidexample;

public interface UserInteractionListener {
    void onDeleteUser(User userToDelete);
    void onUpdateUser(User updatedUser);
    void onCreateUser(User newUser);
}
