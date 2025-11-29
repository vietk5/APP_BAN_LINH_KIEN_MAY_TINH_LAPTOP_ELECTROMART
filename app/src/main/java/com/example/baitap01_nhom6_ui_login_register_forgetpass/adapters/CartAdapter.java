package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.CartItem;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Product;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {

    // ====== CALLBACK VỀ ACTIVITY ======
    public interface OnCartChangeListener {
        void onCartChanged();
        void onItemRemoved(int sizeAfterRemove);
    }

    private final List<CartItem> data;
    private final OnCartChangeListener listener;
    private final NumberFormat priceFormat =
            NumberFormat.getInstance(new Locale("vi", "VN"));

    // Thêm mấy field này để gọi API
    private final Context context;
    private final ApiService apiService;
    private final int userId; // lấy từ SharedPrefManager

    public CartAdapter(Context context,
                       List<CartItem> data,
                       OnCartChangeListener listener,
                       int userId) {
        this.context = context;
        this.data = data;
        this.listener = listener;
        this.userId = userId;
        this.apiService = ApiClient.get();
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

        // Ảnh sản phẩm
        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
            Glide.with(h.itemView.getContext())
                    .load(p.getImageUrl())
                    .placeholder(R.drawable.logo)
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
        });

        // Nút thùng rác (xóa sản phẩm khỏi giỏ)
        h.btnDelete.setOnClickListener(v -> {
            int pos = h.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            CartItem ci = data.get(pos);
            Long productId = ci.getProduct().getId();

            // Nếu chưa login / không có userId hoặc productId thì chỉ xóa local
            if (userId <= 0 || productId == null) {
                removeItemLocal(pos);
                return;
            }

            // Gọi API xóa trong DB
            apiService.deleteCartItem(userId, productId)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call,
                                               @NonNull Response<Void> response) {
                            if (response.isSuccessful()) {
                                removeItemLocal(pos);
                                Toast.makeText(context,
                                        "Đã xóa sản phẩm khỏi giỏ hàng",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context,
                                        "Xóa không thành công (code " + response.code() + ")",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call,
                                              @NonNull Throwable t) {
                            Toast.makeText(context,
                                    "Lỗi mạng, không xóa được: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /** Xóa item trong list + báo về Activity */
    private void removeItemLocal(int pos) {
        data.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, data.size() - pos);
        if (listener != null) {
            listener.onItemRemoved(data.size());
        }
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

            cbSelect   = itemView.findViewById(R.id.checkbox_select);
            ivThumb    = itemView.findViewById(R.id.iv_product_image);
            tvName     = itemView.findViewById(R.id.tv_product_name);
            tvPrice    = itemView.findViewById(R.id.tv_product_price);
            btnMinus   = itemView.findViewById(R.id.btn_decrease);
            btnPlus    = itemView.findViewById(R.id.btn_increase);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnDelete  = itemView.findViewById(R.id.btn_delete_item);
        }
    }
}
