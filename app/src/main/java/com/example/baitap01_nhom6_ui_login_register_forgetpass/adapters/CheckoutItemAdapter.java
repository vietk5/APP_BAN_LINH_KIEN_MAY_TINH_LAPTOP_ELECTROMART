package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.CheckoutItem;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.PriceFormatter;

import java.util.List;

public class CheckoutItemAdapter extends RecyclerView.Adapter<CheckoutItemAdapter.ViewHolder> {

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }

    private final List<CheckoutItem> items;
    private final OnQuantityChangeListener listener;

    public CheckoutItemAdapter(List<CheckoutItem> items,
                               OnQuantityChangeListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkout_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        CheckoutItem item = items.get(position);

        h.tvName.setText(item.getName());
        h.tvPrice.setText(PriceFormatter.vnd(item.getUnitPrice()));
        h.tvQuantity.setText(String.valueOf(item.getQuantity()));

        Glide.with(h.img.getContext())
                .load(item.getImageUrl())
                .into(h.img);

        // Minus
        h.btnMinus.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            CheckoutItem ci = items.get(pos);
            if (ci.getQuantity() > 1) {           // không cho về 0
                ci.setQuantity(ci.getQuantity() - 1);
                notifyItemChanged(pos);
                if (listener != null) listener.onQuantityChanged();
            }
        });

        // Plus
        h.btnPlus.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            CheckoutItem ci = items.get(pos);
            ci.setQuantity(ci.getQuantity() + 1);
            notifyItemChanged(pos);
            if (listener != null) listener.onQuantityChanged();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvPrice, tvQuantity;
        Button btnMinus, btnPlus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            img        = itemView.findViewById(R.id.imgProduct);
            tvName     = itemView.findViewById(R.id.tvName);
            tvPrice    = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnMinus   = itemView.findViewById(R.id.btnMinus);
            btnPlus    = itemView.findViewById(R.id.btnPlus);
        }
    }
}
