package com.example.androidexample;

import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.ViewHolder> {
    private final BuildingInteractionListener listener;
    private final List<Building> buildingList;

    public BuildingAdapter(List<Building> buildingList, BuildingInteractionListener listener) {
        this.buildingList = buildingList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_building, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Building currentBuilding = buildingList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemView.setBackgroundColor(0xA0A0A0);
                //Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                //intent.putExtra("building", currentBuilding);
                //v.getContext().startActivity(intent);
            }
        });
        //update a view
        String buildingName = currentBuilding.getName();
        holder.textBuildingName.setText(buildingName);
        String accountActive = "Active: " + currentBuilding.getIsActive();
        holder.textIsActive.setText(accountActive);
        holder.editBuildingNameText.setText(currentBuilding.getName());

        final int finalPosition = holder.getBindingAdapterPosition();

        // Set click listener for the update button
        holder.buttonUpdate.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                int buildingId = buildingList.get(adapterPosition).getId();
                currentBuilding.setName(holder.editBuildingNameText.getText().toString());
                listener.onUpdateBuilding(currentBuilding);
            }
        });

        //delete button
        holder.buttonDelete.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                int postId = buildingList.get(adapterPosition).getId();
                listener.onDeleteBuilding(currentBuilding);
            }
        });
    }

    @Override
    public int getItemCount() {
        return buildingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textBuildingId;
        public TextView textBuildingName;
        public TextView textIsActive;
        public EditText editBuildingNameText;

        public Button buttonUpdate;

        public ImageButton buttonDelete;
        public ImageView buildingImage;

        public ViewHolder(View itemView) {
            super(itemView);
            textBuildingName = itemView.findViewById(R.id.textBuildingName);
            textIsActive = itemView.findViewById(R.id.textBuildingIsActive);
            buttonUpdate = itemView.findViewById(R.id.buttonUpdate);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buildingImage = itemView.findViewById(R.id.buildingImage);
            editBuildingNameText = itemView.findViewById(R.id.editBuildingNameText);
        }
    }
}
