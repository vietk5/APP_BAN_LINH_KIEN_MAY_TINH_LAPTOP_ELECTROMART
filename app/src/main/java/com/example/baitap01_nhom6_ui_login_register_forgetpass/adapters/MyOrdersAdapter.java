package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;

import android.content.Context;
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
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Order;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.OrderProduct;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onViewDetailClick(Order order);
        void onReorderClick(Order order);
        void onCancelOrderClick(Order order);
    }

    public MyOrdersAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_myorder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Set ngày đơn hàng
        holder.tvOrderDate.setText("Đơn hàng " + formatDate(order.getCreatedAt()));

        // Set trạng thái
        String statusText = getStatusText(order.getStatus());
        int statusColor = getStatusColor(order.getStatus());
        holder.tvOrderStatus.setText(statusText);
        holder.tvOrderStatus.setTextColor(statusColor);
        holder.imgStatusDot.setColorFilter(statusColor);

        // Set thông tin giao hàng
        String shippingType = "Nhận tại cửa hàng"; // hoặc "Giao tận nơi"
        holder.tvShippingInfo.setText(shippingType + " • #" + order.getOrderId());

        // Set tổng tiền
        holder.tvTotalPrice.setText(formatPrice(order.getTotalPrice()));

        // Hiển thị sản phẩm đầu tiên
        if (order.getProducts() != null && !order.getProducts().isEmpty()) {
            OrderProduct firstProduct = order.getProducts().get(0);
            holder.tvProductName.setText(firstProduct.getName());

            // Load ảnh sản phẩm
            if (firstProduct.getImage() != null && !firstProduct.getImage().isEmpty()) {
                Glide.with(context)
                        .load(firstProduct.getImage())
                        .placeholder(R.drawable.placeholder_product)
                        .error(R.drawable.placeholder_product)
                        .centerCrop()
                        .into(holder.imgProduct);
            } else {
                holder.imgProduct.setImageResource(R.drawable.placeholder_product);
            }

            // ===== SỬA PHẦN NÀY =====
            // Hiển thị số sản phẩm khác nếu có nhiều hơn 1 loại
            if (order.getProducts().size() > 1) {
                holder.tvProductCount.setVisibility(View.VISIBLE);
                holder.tvProductCount.setText("+" + (order.getProducts().size() - 1) + " sản phẩm khác");
            } else {
                holder.tvProductCount.setVisibility(View.GONE);
            }
            // ===== HẾT PHẦN SỬA =====
        }

        // Click listeners
        holder.btnViewDetail.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewDetailClick(order);
            }
        });

        holder.btnReorder.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReorderClick(order);
            }
        });
        holder.btnCancelOrder.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancelOrderClick(order);
            }
        });

        // Ẩn/hiện nút Mua lại
        if (order.getStatus().equals("HOAN_THANH") || order.getStatus().equals("DA_HUY")) {
            holder.btnReorder.setVisibility(View.VISIBLE);
        } else {
            holder.btnReorder.setVisibility(View.GONE);
        }
        // Ẩn/hiện nút Huỷ
        if ("DANG_XU_LY".equals(order.getStatus())) {
            holder.btnCancelOrder.setVisibility(View.VISIBLE);
        } else {
            holder.btnCancelOrder.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public void updateData(List<Order> newList) {
        this.orderList = newList;
        notifyDataSetChanged();
    }

    private String formatPrice(long price) {
        if (price == 0) return "0đ";
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(price) + "đ";
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private String getStatusText(String status) {
        if (status == null) return "";
        switch (status) {
            case "DANG_XU_LY":
                return "Đang xử lý";
            case "DANG_GIAO":
                return "Đang giao";
            case "HOAN_THANH":
                return "Đã giao";
            case "DA_HUY":
                return "Đã hủy";
            default:
                return status;
        }
    }

    private int getStatusColor(String status) {
        if (status == null) return 0xFF757575;
        switch (status) {
            case "DANG_XU_LY":
                return 0xFFFFA726; // Cam
            case "DANG_GIAO":
                return 0xFF1976D2; // Xanh dương
            case "HOAN_THANH":
                return 0xFF4CAF50; // Xanh lá
            case "DA_HUY":
                return 0xFFF44336; // Đỏ
            default:
                return 0xFF757575; // Gray
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderDate, tvOrderStatus, tvShippingInfo;
        ImageView imgProduct, imgStatusDot;
        TextView tvProductName, tvProductCount;
        TextView btnViewDetail, tvTotalPrice;
        MaterialButton btnReorder;
        MaterialButton btnCancelOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            imgStatusDot = itemView.findViewById(R.id.imgStatusDot);
            tvShippingInfo = itemView.findViewById(R.id.tvShippingInfo);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductCount = itemView.findViewById(R.id.tvProductCount);
            btnViewDetail = itemView.findViewById(R.id.btnViewDetail);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            btnReorder = itemView.findViewById(R.id.btnReorder);
            btnCancelOrder = itemView.findViewById(R.id.btnCancelOrder);
        }
    }
}