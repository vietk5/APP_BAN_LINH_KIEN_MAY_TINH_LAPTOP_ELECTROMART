package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.PcPartSlot;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Product;

import java.util.List;

public class PcBuilderAdapter extends RecyclerView.Adapter<PcBuilderAdapter.ViewHolder> {

    private Context context;
    private List<PcPartSlot> slots;
    private OnSlotClickListener listener;

    public interface OnSlotClickListener {
        void onSelectClick(int position, PcPartSlot slot);
        void onRemoveClick(int position, PcPartSlot slot);
    }

    public PcBuilderAdapter(Context context, List<PcPartSlot> slots, OnSlotClickListener listener) {
        this.context = context;
        this.slots = slots;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo file layout item_pc_slot.xml bên dưới
        View view = LayoutInflater.from(context).inflate(R.layout.item_pc_slot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PcPartSlot slot = slots.get(position);
        Product p = slot.getProduct();

        holder.tvSlotTitle.setText(slot.getTitle());
        holder.imgSlotIcon.setImageResource(slot.getIconRes());

        if (p != null) {
            // Đã chọn sản phẩm
            holder.layoutEmpty.setVisibility(View.GONE);
            holder.layoutSelected.setVisibility(View.VISIBLE);

            holder.tvProductName.setText(p.getName());
            holder.tvProductPrice.setText(p.getPrice());
            Glide.with(context).load(p.getImageUrl()).into(holder.imgProduct);
        } else {
            // Chưa chọn
            holder.layoutEmpty.setVisibility(View.VISIBLE);
            holder.layoutSelected.setVisibility(View.GONE);
        }

        holder.btnSelect.setOnClickListener(v -> listener.onSelectClick(position, slot));
        holder.btnChange.setOnClickListener(v -> listener.onSelectClick(position, slot));
        holder.btnRemove.setOnClickListener(v -> listener.onRemoveClick(position, slot));
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSlotTitle, tvProductName, tvProductPrice;
        ImageView imgSlotIcon, imgProduct;
        Button btnSelect, btnChange, btnRemove;
        LinearLayout layoutEmpty, layoutSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSlotTitle = itemView.findViewById(R.id.tvSlotTitle);
            imgSlotIcon = itemView.findViewById(R.id.imgSlotIcon);
            layoutEmpty = itemView.findViewById(R.id.layoutEmpty);
            btnSelect = itemView.findViewById(R.id.btnSelect);

            layoutSelected = itemView.findViewById(R.id.layoutSelected);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            btnChange = itemView.findViewById(R.id.btnChange);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}