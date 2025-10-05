package com.example.androidexample;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Adapter for the RecyclerView that displays a list of Packages.
 */
public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {
    private final PackageInteractionListener listener;
    private final List<Package> packageList;

    /**
     * Constructs a new PackageAdapter with the given list of packages and interaction listener.
     *
     * @param packageList The list of packages to display.
     * @param listener The listener for package interactions.
     */
    public PackageAdapter(List<Package> packageList, PackageInteractionListener listener) {
        this.packageList = packageList;
        this.listener = listener;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_package, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Package currentPackage = packageList.get(position);
        String nameText = "Package Delivered for:\n" + currentPackage.getOccupantName();
        holder.textOccupantName.setText(nameText);
        String dateText = "Delivered on " + currentPackage.getDeliveryDate();
        holder.textDeliveryDate.setText(dateText);
        String securityText = "Security Code: " + currentPackage.getSecurityCode();
        holder.textSecurityCode.setText(securityText);
        boolean deliveredStatus = currentPackage.getPickUpStatus();
        if (deliveredStatus){
            holder.textIsDelivered.setText("Picked Up");
        }else{
            holder.textIsDelivered.setText("Not Picked Up");
        }
        final int finalPosition = holder.getBindingAdapterPosition();

        // Set the current occupant name in the edit text
        /*holder.editOccupantEditText.setText(currentPackage.getOccupantName());

        // Set click listener for the update button
        holder.updateOccupantButton.setOnClickListener(v -> {
            String updatedName = holder.editOccupantEditText.getText().toString();
            if (!TextUtils.isEmpty(updatedName)) {
                // Update the occupant name and notify the listener
                listener.onUpdateOccupantName(position, updatedName);
            } else {
                Toast.makeText(holder.itemView.getContext(), "Please enter an updated name", Toast.LENGTH_SHORT).show();
            }
        });*/


        /*holder.buttonDelete.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                int packageId = packageList.get(adapterPosition).getId();
                listener.onDeletePackage(packageId, adapterPosition);
            }
        });
        /*
         */

        // Set an OnClickListener for the delete button
        /*holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    int packageId = packageList.get(adapterPosition).getId();
                    listener.onDeletePackage(packageId, adapterPosition);
                }
            }
        });*/
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return packageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textOccupantName;
        public TextView textDeliveryDate;
        public TextView textSecurityCode;
        public TextView textIsDelivered;


        public Button updateOccupantButton;
        public Button buttonDelete;

        /**
         * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            textOccupantName = itemView.findViewById(R.id.textOccupantName);
            textDeliveryDate = itemView.findViewById(R.id.textDeliveryDate);
            textSecurityCode = itemView.findViewById(R.id.textSecurityCode);
            updateOccupantButton = itemView.findViewById(R.id.updateOccupantbtn);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            textIsDelivered = itemView.findViewById(R.id.pickupstatus);

        }
    }
}
