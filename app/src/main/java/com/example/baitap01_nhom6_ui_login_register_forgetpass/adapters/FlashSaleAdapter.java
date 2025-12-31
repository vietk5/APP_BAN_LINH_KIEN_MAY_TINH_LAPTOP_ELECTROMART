package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;

import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ProductDto;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FlashSaleAdapter extends RecyclerView.Adapter<FlashSaleAdapter.VH> {

    public interface OnItemClick {
        void onClick(ProductDto p);
    }

    private final List<ProductDto> data;
    private final OnItemClick listener;

    // ✅ Constructor mới (không cần listener)
    public FlashSaleAdapter(List<ProductDto> data) {
        this(data, null);
    }

    // ✅ Constructor có listener (để HomeFragment dùng)
    public FlashSaleAdapter(List<ProductDto> data, OnItemClick listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flash_sale, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ProductDto p = data.get(position);

        // ====== NAME ======
        h.tvName.setText(p.name != null ? p.name : "Sản phẩm");

        // ====== PRICE (-5%) ======
        long price = p.price;
        if (price <= 0) {
            h.tvPriceNew.setText("Liên hệ");
            h.tvPriceOld.setVisibility(View.GONE);
        } else {
            long newPrice = Math.round(price * 0.95);
            h.tvPriceOld.setVisibility(View.VISIBLE);
            h.tvPriceOld.setText(vnd(price));
            h.tvPriceOld.setPaintFlags(h.tvPriceOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            h.tvPriceNew.setText(vnd(newPrice));
        }

        // ====== IMAGE (fix ảnh giống nhau) ======
        h.img.setImageResource(R.drawable.placeholder_product);

        String url = p.imageUrl;
        if (!TextUtils.isEmpty(url)) {
            Glide.with(h.itemView.getContext())
                    .load(url)
                    .placeholder(R.drawable.placeholder_product)
                    .error(R.drawable.placeholder_product)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(h.img);
        }

        // ====== CLICK ======
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(p);
        });
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvPriceOld, tvPriceNew;

        VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvPriceOld = itemView.findViewById(R.id.tvPriceOld);
            tvPriceNew = itemView.findViewById(R.id.tvPriceNew);
        }
    }

    private String vnd(long price) {
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(price) + " đ";
    }
}
