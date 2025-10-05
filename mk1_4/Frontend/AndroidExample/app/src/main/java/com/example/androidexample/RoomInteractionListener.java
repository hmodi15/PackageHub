package com.example.androidexample;

public interface RoomInteractionListener {
    void onDeleteRoom(Room roomToDelete);
    void onUpdateRoom(Room updatedRoom);
    void onCreateRoom(Room newRoom);
}
