package com.example.androidexample;

/**
 * Interface for handling interactions with packages.
 */
public interface PackageInteractionListenerManager {
    /**
     * Called when a package is deleted.
     *
     * @param packageId The ID of the package that was deleted.
     * @param position The position of the package in the list.
     */
    void onDeletePackage(int packageId, int position);

    void onMarkPackageDelivered(int position);

    /**
     * Called when the occupant's name is updated.
     *
     * @param position The position of the package in the list.
     * @param updatedName The new name of the occupant.
     */
    void onUpdateOccupantName(int position, String updatedName);
}
