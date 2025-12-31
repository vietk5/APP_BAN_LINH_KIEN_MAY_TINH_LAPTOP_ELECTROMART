package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.OrderProduct;

import java.text.DecimalFormat;
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

    private Context context;
    private List<OrderProduct> list;

    public OrderDetailAdapter(Context context, List<OrderProduct> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_order_detail_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        OrderProduct p = list.get(position);
        h.tvName.setText(p.getName());
        h.tvQty.setText("x" + p.getQuantity());
        h.tvPrice.setText(formatPrice(p.getPrice()));

        Glide.with(context)
                .load(p.getImage())
                .placeholder(R.drawable.placeholder_product)
                .into(h.img);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvQty, tvPrice;

        ViewHolder(View v) {
            super(v);
            img = v.findViewById(R.id.imgProduct);
            tvName = v.findViewById(R.id.tvProductName);
            tvQty = v.findViewById(R.id.tvQuantity);
            tvPrice = v.findViewById(R.id.tvPrice);
        }
    }

    private String formatPrice(long price) {
        return new DecimalFormat("#,###").format(price) + "đ";
    }
}

