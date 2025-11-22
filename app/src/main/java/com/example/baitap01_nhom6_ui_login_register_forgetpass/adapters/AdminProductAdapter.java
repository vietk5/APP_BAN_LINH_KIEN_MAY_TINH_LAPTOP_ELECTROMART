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
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.AdminProductDto;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ViewHolder> {

    // Interface để Activity xử lý sự kiện
    public interface Listener {
        void onNhapHang(AdminProductDto p);
        void onXoa(AdminProductDto p);
    }

    private final List<AdminProductDto> data;
    private final Listener listener;

    public AdminProductAdapter(List<AdminProductDto> data, Listener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        AdminProductDto p = data.get(position);

        // 1. Tên sản phẩm
        h.tvName.setText(p.ten);

        // 2. Giá tiền (Format VND)
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        String priceStr = nf.format(p.gia) + " ₫";
        h.tvPrice.setText(priceStr);

        // 3. Tồn kho
        h.tvStock.setText("Tồn kho: " + p.tonKho);

        // 4. Thông tin phụ (Thương hiệu - Loại)
        String sub = "";
        if (p.thuongHieuTen != null) sub += p.thuongHieuTen;
        if (p.loaiTen != null) {
            if (!sub.isEmpty()) sub += " • ";
            sub += p.loaiTen;
        }
        h.tvSub.setText(sub);

        // 5. Ảnh sản phẩm
        Glide.with(h.imgProduct.getContext())
                .load(p.imageUrl)
                .placeholder(R.drawable.sample_pc) // Đảm bảo bạn có ảnh này trong drawable
                .error(R.drawable.sample_pc)
                .into(h.imgProduct);

        // 6. Sự kiện nút bấm
        h.btnNhap.setOnClickListener(v -> {
            if (listener != null) listener.onNhapHang(p);
        });

        h.btnXoa.setOnClickListener(v -> {
            if (listener != null) listener.onXoa(p);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice, tvStock, tvSub;
        Button btnNhap, btnXoa;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName     = itemView.findViewById(R.id.tvName);
            tvPrice    = itemView.findViewById(R.id.tvPrice);
            tvStock    = itemView.findViewById(R.id.tvStock);
            // --- QUAN TRỌNG: Phải ánh xạ view này, nếu không sẽ bị Crash ---
            tvSub      = itemView.findViewById(R.id.tvCategoryBrand);

            btnNhap    = itemView.findViewById(R.id.btnNhap);
            btnXoa     = itemView.findViewById(R.id.btnXoa);
        }
    }
}