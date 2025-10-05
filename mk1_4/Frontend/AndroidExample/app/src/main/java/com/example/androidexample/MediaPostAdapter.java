package com.example.androidexample;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MediaPostAdapter extends RecyclerView.Adapter<MediaPostAdapter.ViewHolder> {
    private final MediaPostInteractionListener listener;
    private final List<MediaPost> postList;

    public MediaPostAdapter(List<MediaPost> postList, MediaPostInteractionListener listener) {
        this.postList = postList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MediaPost currentPost = postList.get(position);

        //update a view
        String nameText = currentPost.getUserName();
        holder.textOccupantName.setText(nameText);
        String postText = currentPost.getPostText();
        holder.textDeliveryDate.setText(postText);
        Bitmap postImg = currentPost.getRawImage();
        holder.postImage.setImageBitmap(postImg);
        holder.textSecurityCode.setText("Likes: " + currentPost.getNumLikes());

        final int finalPosition = holder.getBindingAdapterPosition();

        // Set the current occupant name in the edit text
        holder.editOccupantEditText.setText(currentPost.getPostText());

        // Set click listener for the like button
        holder.updateOccupantButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                int postId = postList.get(adapterPosition).getId();
                listener.onUpdatePost(postId, holder.editOccupantEditText.getText().toString());
            }
        });


        holder.buttonDelete.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                int postId = postList.get(adapterPosition).getId();
                listener.onDeletePost(postId, adapterPosition);
            }
        });

        holder.likeButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                int postId = postList.get(adapterPosition).getId();
                listener.onLikePost(postId, adapterPosition);
            }
        });

        /*
        // Set an OnClickListener for the delete button
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    int packageId = postList.get(adapterPosition).getId();
                    listener.onDeletePost(packageId, adapterPosition);
                }
            }
        });
         */
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //FIXME needs to be updated to fit social media post naming conventions
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textOccupantName;
        public TextView textDeliveryDate;
        public TextView textSecurityCode;

        public EditText editOccupantEditText;

        public Button updateOccupantButton;
        public Button buttonDelete;
        public ImageView postImage;
        public Button likeButton;

        public ViewHolder(View itemView) {
            super(itemView);
            textOccupantName = itemView.findViewById(R.id.textOccupantName);
            textDeliveryDate = itemView.findViewById(R.id.textDeliveryDate);
            textSecurityCode = itemView.findViewById(R.id.textSecurityCode);
            editOccupantEditText = itemView.findViewById(R.id.editOccupantEditText);
            updateOccupantButton = itemView.findViewById(R.id.updateOccupantbtn);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            postImage = itemView.findViewById(R.id.postImage);
            likeButton = itemView.findViewById(R.id.likeButton);
        }
    }





}
