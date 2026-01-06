package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.CapturedImage;

import java.util.ArrayList;
import java.util.List;

public class CapturedImageAdapter extends RecyclerView.Adapter<CapturedImageAdapter.ViewHolder> {

    private List<CapturedImage> images = new ArrayList<>();
    private OnImageRemovedListener listener;

    public interface OnImageRemovedListener {
        void onImageRemoved(int position);
    }

    public CapturedImageAdapter(OnImageRemovedListener listener) {
        this.listener = listener;
    }

    public void setImages(List<CapturedImage> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    public void addImage(CapturedImage image) {
        this.images.add(image);
        notifyItemInserted(images.size() - 1);
    }

    public void removeImage(int position) {
        if (position >= 0 && position < images.size()) {
            images.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_captured_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CapturedImage image = images.get(position);
        holder.bind(image, position);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        ImageButton btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }

        public void bind(CapturedImage image, int position) {
            // Load image với Glide
            if (image.getBitmap() != null) {
                Glide.with(itemView.getContext())
                        .load(image.getBitmap())
                        .centerCrop()
                        .into(ivImage);
            } else if (image.getImagePath() != null) {
                Glide.with(itemView.getContext())
                        .load(image.getImagePath())
                        .centerCrop()
                        .into(ivImage);
            }

            // Remove button click
            btnRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onImageRemoved(position);
                }
            });
        }
    }
}