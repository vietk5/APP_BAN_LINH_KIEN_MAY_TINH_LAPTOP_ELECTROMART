package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.admin.LowStockProduct;

import java.util.List;

public class LowStockAdapter extends RecyclerView.Adapter<LowStockAdapter.LowStockVH> {

    private final List<LowStockProduct> data;

    public LowStockAdapter(List<LowStockProduct> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public LowStockVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_low_stock, parent, false);
        return new LowStockVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LowStockVH h, int position) {
        LowStockProduct p = data.get(position);
        h.tvName.setText(p.name);
        h.tvStock.setText("Tồn kho: " + p.stock);

        Glide.with(h.imgProduct.getContext())
                .load(p.imageUrl)
                .placeholder(R.drawable.ic_laptop)
                .into(h.imgProduct);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class LowStockVH extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvStock;

        LowStockVH(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvStock = itemView.findViewById(R.id.tvStock);
        }
    }
}
