package com.example.androidexample;

public interface BuildingInteractionListener {
    void onDeleteBuilding(Building buildingToDelete);
    void onUpdateBuilding(Building updatedBuilding);
    void onCreateBuilding(Building newBuilding);
}
