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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SearchResultProductAdapter extends RecyclerView.Adapter<SearchResultProductAdapter.ViewHolder> {

    private List<Product> products = new ArrayList<>();
    private Map<Long, Integer> selectedProducts = new HashMap<>(); // productId -> quantity
    private OnProductSelectionChangedListener listener;

    public interface OnProductSelectionChangedListener {
        void onSelectionChanged(int selectedCount);
    }

    public SearchResultProductAdapter(OnProductSelectionChangedListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    public Map<Long, Integer> getSelectedProducts() {
        return selectedProducts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelect;
        ImageView ivProduct;
        TextView tvProductName, tvProductPrice, tvQuantity;
        ImageButton btnDecrease, btnIncrease;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
        }

        public void bind(Product product) {
            // Product info
            tvProductName.setText(product.getName());

            // Hiển thị giá (price đã được format sẵn từ backend)
            tvProductPrice.setText(product.getPrice());

            // Load image - Ưu tiên imageUrl từ backend, fallback về imageResId
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                // Load ảnh từ URL (backend)
                Glide.with(itemView.getContext())
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.placeholder_product)
                        .error(R.drawable.placeholder_product)
                        .centerCrop()
                        .into(ivProduct);
            } else if (product.getImageResId() != 0) {
                // Load ảnh local (dữ liệu cũ)
                Glide.with(itemView.getContext())
                        .load(product.getImageResId())
                        .placeholder(R.drawable.placeholder_product)
                        .error(R.drawable.placeholder_product)
                        .centerCrop()
                        .into(ivProduct);
            } else {
                // Không có ảnh, dùng placeholder
                ivProduct.setImageResource(R.drawable.placeholder_product);
            }

            // Get current quantity
            int currentQty = selectedProducts.getOrDefault(product.getId(), 0);
            tvQuantity.setText(String.valueOf(currentQty));

            // Checkbox state
            cbSelect.setChecked(currentQty > 0);

            // Checkbox change listener
            cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    int qty = selectedProducts.getOrDefault(product.getId(), 0);
                    if (qty == 0) {
                        selectedProducts.put(product.getId(), 1);
                        tvQuantity.setText("1");
                    }
                } else {
                    selectedProducts.remove(product.getId());
                    tvQuantity.setText("0");
                }

                if (listener != null) {
                    listener.onSelectionChanged(selectedProducts.size());
                }
            });

            // Decrease button
            btnDecrease.setOnClickListener(v -> {
                int qty = selectedProducts.getOrDefault(product.getId(), 0);
                if (qty > 1) {
                    qty--;
                    selectedProducts.put(product.getId(), qty);
                    tvQuantity.setText(String.valueOf(qty));
                } else if (qty == 1) {
                    selectedProducts.remove(product.getId());
                    tvQuantity.setText("0");
                    cbSelect.setChecked(false);
                }

                if (listener != null) {
                    listener.onSelectionChanged(selectedProducts.size());
                }
            });

            // Increase button
            btnIncrease.setOnClickListener(v -> {
                int qty = selectedProducts.getOrDefault(product.getId(), 0);
                qty++;
                selectedProducts.put(product.getId(), qty);
                tvQuantity.setText(String.valueOf(qty));

                if (!cbSelect.isChecked()) {
                    cbSelect.setChecked(true);
                }

                if (listener != null) {
                    listener.onSelectionChanged(selectedProducts.size());
                }
            });
        }
    }
}