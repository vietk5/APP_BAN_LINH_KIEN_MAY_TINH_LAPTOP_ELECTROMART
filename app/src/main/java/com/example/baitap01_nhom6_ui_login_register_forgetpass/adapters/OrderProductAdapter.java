package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;

import android.content.Context;
import android.graphics.Paint;
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

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.ViewHolder> {

    private Context context;
    private List<OrderProduct> productList;

    // Constructor
    public OrderProductAdapter(Context context, List<OrderProduct> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderProduct product = productList.get(position);

        // Set tên sản phẩm
        holder.tvProductName.setText(product.getName());

        // Set giá sản phẩm
        holder.tvProductPrice.setText(formatPrice(product.getPrice()));

        // Set số lượng
        holder.tvProductQuantity.setText("x" + product.getQuantity());

        // Hiển thị variant nếu có
        if (product.getVariant() != null && !product.getVariant().isEmpty()) {
            holder.tvProductVariant.setVisibility(View.VISIBLE);
            holder.tvProductVariant.setText(product.getVariant());
        } else {
            holder.tvProductVariant.setVisibility(View.GONE);
        }

        // Hiển thị giá gốc nếu có giảm giá
        if (product.getOriginalPrice() > product.getPrice()) {
            holder.tvProductOriginalPrice.setVisibility(View.VISIBLE);
            holder.tvProductOriginalPrice.setText(formatPrice(product.getOriginalPrice()));
            // hien thi dau gach ngang
            holder.tvProductOriginalPrice.setPaintFlags(
                    holder.tvProductOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
        } else {
            holder.tvProductOriginalPrice.setVisibility(View.GONE);
        }

        // Load ảnh sản phẩm bằng Glide
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            Glide.with(context)
                    .load(product.getImage())
                    .placeholder(R.drawable.placeholder_product)
                    .error(R.drawable.placeholder_product)
                    .centerCrop()
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.placeholder_product);
        }
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    // Format giá tiền VND
    private String formatPrice(long price) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(price) + "đ";
    }

    // Cập nhật danh sách sản phẩm
    public void updateData(List<OrderProduct> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    // Thêm sản phẩm
    public void addProduct(OrderProduct product) {
        if (productList != null) {
            productList.add(product);
            notifyItemInserted(productList.size() - 1);
        }
    }

    // Xóa sản phẩm
    public void removeProduct(int position) {
        if (productList != null && position >= 0 && position < productList.size()) {
            productList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, productList.size());
        }
    }

    // Lấy tổng tiền
    public long getTotalPrice() {
        long total = 0;
        if (productList != null) {
            for (OrderProduct product : productList) {
                total += product.getPrice() * product.getQuantity();
            }
        }
        return total;
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName;
        TextView tvProductVariant;
        TextView tvProductPrice;
        TextView tvProductOriginalPrice;
        TextView tvProductQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductVariant = itemView.findViewById(R.id.tvProductVariant);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductOriginalPrice = itemView.findViewById(R.id.tvProductOriginalPrice);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
        }
    }
}
