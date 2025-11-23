package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.VH> {

    private final List<Comment> data;

    public CommentAdapter(List<Comment> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Comment c = data.get(position);

        h.tvUsername.setText(c.getUsername());
        h.tvContent.setText(c.getContent());
        h.tvRating.setText("★ " + c.getRating());
        h.tvDate.setText(c.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvUsername, tvContent, tvRating, tvDate;

        public VH(@NonNull View v) {
            super(v);
            tvUsername = v.findViewById(R.id.cmtUsername);
            tvContent = v.findViewById(R.id.cmtContent);
            tvRating = v.findViewById(R.id.cmtRating);
            tvDate = v.findViewById(R.id.cmtDate);
        }
    }
}
