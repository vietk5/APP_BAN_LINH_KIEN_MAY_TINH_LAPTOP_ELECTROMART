// adapters/ProductAdapter.java
package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.ProductDetailActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Product;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {
    private final List<Product> data;
    public ProductAdapter(List<Product> data) { this.data = data; }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Product p = data.get(pos);
        h.tvName.setText(p.getName());
        h.tvPrice.setText(p.getPrice());

        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
            Glide.with(h.img.getContext())
                    .load(p.getImageUrl())
                    .placeholder(R.drawable.product_item_bg)
                    .error(R.drawable.product_item_bg)
                    .into(h.img);
        } else if (p.getImageResId() != 0) {
            h.img.setImageResource(p.getImageResId());
        } else {
            h.img.setImageResource(R.drawable.product_item_bg);
        }

        // ⬇⬇⬇ SỬA Ở ĐÂY
        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("product_id", p.getId());   // truyền ID *sản phẩm*
            v.getContext().startActivity(intent);
        });
//        h.itemView.setOnClickListener(v -> {
//            long id = p.getId();
//
//
//            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
//            intent.putExtra("product_id", id);
//            v.getContext().startActivity(intent);
//        });

    }


    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img; TextView tvName, tvPrice;
        VH(View v) { super(v);
            img = v.findViewById(R.id.imgProduct);
            tvName = v.findViewById(R.id.tvProductName);
            tvPrice = v.findViewById(R.id.tvProductPrice);
        }
    }
}
