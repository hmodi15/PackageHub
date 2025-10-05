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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private final UserInteractionListener listener;
    private final List<User> userList;

    public UserAdapter(List<User> userList, UserInteractionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User currentUser = userList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemView.setBackgroundColor(0xA0A0A0);
                //Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                //intent.putExtra("user", currentUser);
                //v.getContext().startActivity(intent);
            }
        });
        //update a view
        String userName = currentUser.getUserName();
        holder.textUserName.setText(userName);
        String emailId = currentUser.getEmailId();
        holder.textEmailId.setText(emailId);
        String address = currentUser.getAddress();
        holder.textAddress.setText(address);
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy");
        outputFormat.setTimeZone(TimeZone.getDefault()); // Use the device's default timezone
        String joinDate;
        try {
            Date date = inputFormat.parse(currentUser.getJoiningDate());
            joinDate = "Join Date: " + outputFormat.format(date);
        }
        catch (Exception e){
            joinDate = "Join Date: " + currentUser.getJoiningDate();
        }
        holder.textJoinDate.setText(joinDate);
        String accountActive = "Active: " + currentUser.getIsActive();
        holder.textIsActive.setText(accountActive);
        if(currentUser.getIsAdmin()){
            holder.userImage.setImageResource(R.drawable.baseline_engineering_24);
        }
        else if(currentUser.getIsManager()){
            holder.userImage.setImageResource(R.drawable.homer_profile);
        }
        else{
            holder.userImage.setImageResource(R.drawable.baseline_person_24);
        }

        final int finalPosition = holder.getBindingAdapterPosition();

        // Set click listener for the reset password button
        holder.buttonResetPassword.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChangePasswordActivity.class);
            //intent.putExtra("user", currentUser);
            v.getContext().startActivity(intent);
        });

        // Set click listener for the update button
        holder.buttonUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProfileActivity.class);
            intent.putExtra("user", currentUser);
            v.getContext().startActivity(intent);
        });

        //delete button
        holder.buttonDelete.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                int postId = userList.get(adapterPosition).getId();
                listener.onDeleteUser(currentUser);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textUserName;
        public TextView textEmailId;
        public TextView textAddress;
        public TextView textJoinDate;
        public TextView textIsActive;

        public Button buttonUpdate;
        public ImageButton buttonDelete;
        public Button buttonResetPassword;
        public ImageView userImage;

        public ViewHolder(View itemView) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.textUserName);
            textEmailId = itemView.findViewById(R.id.textEmailId);
            textAddress = itemView.findViewById(R.id.textAddress);
            textJoinDate = itemView.findViewById(R.id.textJoinDate);
            textIsActive = itemView.findViewById(R.id.textIsActive);
            buttonUpdate = itemView.findViewById(R.id.buttonUpdate);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonResetPassword = itemView.findViewById(R.id.buttonResetPassword);
            userImage = itemView.findViewById(R.id.userImage);
        }
    }
}
