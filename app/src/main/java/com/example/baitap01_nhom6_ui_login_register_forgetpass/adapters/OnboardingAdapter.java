package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.OnboardingItem;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.VH> {

    private final List<OnboardingItem> items;

    public OnboardingAdapter(List<OnboardingItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        OnboardingItem it = items.get(pos);
        h.img.setImageResource(it.imageRes);
        h.title.setText(it.title);
        h.desc.setText(it.desc);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title, desc;
        VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            title = itemView.findViewById(R.id.tvTitle);
            desc = itemView.findViewById(R.id.tvDesc);
        }
    }
}
