package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {

    // ====== MODEL NỘI BỘ CHO CART (không cần tạo file riêng) ======
    public static class CartItem {
        private final Product product;
        private int quantity;
        private boolean selected;

        public CartItem(Product product, int quantity, boolean selected) {
            this.product = product;
            this.quantity = quantity;
            this.selected = selected;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            if (quantity < 1) quantity = 1;
            this.quantity = quantity;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    // ====== CALLBACK VỀ ACTIVITY ======
    public interface OnCartChangeListener {
        void onCartChanged();
        void onItemRemoved(int sizeAfterRemove);
    }

    private final List<CartItem> data;
    private final OnCartChangeListener listener;
    private final NumberFormat priceFormat =
            NumberFormat.getInstance(new Locale("vi", "VN"));

    public CartAdapter(List<CartItem> data, OnCartChangeListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        CartItem item = data.get(position);
        Product p = item.getProduct();

        h.tvName.setText(p.getName());
        try {
            long price = Long.parseLong(p.getPrice());
            h.tvPrice.setText(String.format("%s đ", priceFormat.format(price)));
        } catch (NumberFormatException e) {
            h.tvPrice.setText(p.getPrice()); // Giữ nguyên nếu không phải số
        }
        h.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // Checkbox chọn sản phẩm
        h.cbSelect.setOnCheckedChangeListener(null);
        h.cbSelect.setChecked(item.isSelected());
        h.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int pos = h.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            data.get(pos).setSelected(isChecked);
            if (listener != null) listener.onCartChanged();
        });

        // Ảnh sản phẩm (nếu Product có url / resource)
        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
            Glide.with(h.itemView.getContext())
                    .load(p.getImageUrl())
                    .placeholder(R.drawable.logo) // bạn có thể tạo icon này
                    .into(h.ivThumb);
        } else {
            h.ivThumb.setImageResource(R.drawable.logo);
        }

        // Nút +
        h.btnPlus.setOnClickListener(v -> {
            int pos = h.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            CartItem ci = data.get(pos);
            ci.setQuantity(ci.getQuantity() + 1);
            notifyItemChanged(pos);
            if (listener != null) listener.onCartChanged();
        });

        // Nút -
        h.btnMinus.setOnClickListener(v -> {
            int pos = h.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            CartItem ci = data.get(pos);
            if (ci.getQuantity() > 1) {
                ci.setQuantity(ci.getQuantity() - 1);
                notifyItemChanged(pos);
                if (listener != null) listener.onCartChanged();
            }
            // Nếu muốn: quantity = 1 thì không cho giảm nữa
        });

        // Nút thùng rác (xóa sản phẩm khỏi giỏ)
        h.btnDelete.setOnClickListener(v -> {
            int pos = h.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            data.remove(pos);
            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos, data.size() - pos);
            if (listener != null) {
                listener.onItemRemoved(data.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    // ====== VIEW HOLDER ======
    public static class VH extends RecyclerView.ViewHolder {

        CheckBox cbSelect;
        ImageView ivThumb;
        TextView tvName;
        TextView tvPrice;
        ImageButton btnMinus;
        ImageButton btnPlus;
        TextView tvQuantity;
        ImageButton btnDelete;

        public VH(@NonNull View itemView) {
            super(itemView);

            // Các ID dưới đây cần khớp với file item_cart.xml của bạn
            cbSelect = itemView.findViewById(R.id.checkbox_select);
            ivThumb = itemView.findViewById(R.id.iv_product_image);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            btnMinus = itemView.findViewById(R.id.btn_decrease);
            btnPlus = itemView.findViewById(R.id.btn_increase);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnDelete = itemView.findViewById(R.id.btn_delete_item);
        }
    }
}
