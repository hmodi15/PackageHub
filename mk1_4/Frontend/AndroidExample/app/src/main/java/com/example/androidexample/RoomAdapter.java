package com.example.androidexample;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    private final RoomInteractionListener listener;
    private final List<Room> roomList;

    public RoomAdapter(List<Room> roomList, RoomInteractionListener listener) {
        this.roomList = roomList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Room currentRoom = roomList.get(position);

        //update a view
        String apartmentNumber = currentRoom.getApartmentNumber();
        holder.textApartmentName.setText(apartmentNumber);
        String address = currentRoom.getAddress();
        holder.textAddress.setText(address);
        String buildingName = currentRoom.getBuildingName();
        holder.textBuildingName.setText(buildingName);
        String maxTenants = "" + currentRoom.getMaxTenants();
        holder.textMaxTenants.setText(maxTenants);

        final int finalPosition = holder.getBindingAdapterPosition();

        // Set click listener for the update button
        holder.buttonUpdate.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                int postId = roomList.get(adapterPosition).getId();
                currentRoom.setApartmentNumber(holder.editApartmentNameText.getText().toString());
                listener.onUpdateRoom(currentRoom);
            }
        });

        //delete button
        holder.buttonDelete.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                int postId = roomList.get(adapterPosition).getId();
                listener.onDeleteRoom(currentRoom);
            }
        });

        // Set click listener for the increase capacity button
        holder.buttonAddCapacity.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                int postId = roomList.get(adapterPosition).getId();
                currentRoom.setMaxTenants(currentRoom.getMaxTenants() + 1);
                listener.onUpdateRoom(currentRoom);
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textApartmentName;
        public TextView textAddress;
        public TextView textBuildingName;
        public TextView textMaxTenants;

        public EditText editApartmentNameText;

        public Button buttonUpdate;
        public ImageButton buttonDelete;
        public Button buttonAddCapacity;
        public ImageView roomImage;

        public ViewHolder(View itemView) {
            super(itemView);
            textApartmentName = itemView.findViewById(R.id.textApartmentName);
            textAddress = itemView.findViewById(R.id.textAddress);
            textBuildingName = itemView.findViewById(R.id.textBuildingName);
            textMaxTenants = itemView.findViewById(R.id.textMaxTenants);
            editApartmentNameText = itemView.findViewById(R.id.editApartmentNameText);
            buttonUpdate = itemView.findViewById(R.id.buttonUpdate);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonAddCapacity = itemView.findViewById(R.id.buttonAddCapacity);
            roomImage = itemView.findViewById(R.id.roomImage);
        }
    }
}
