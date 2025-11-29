package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    // ===== Callback để Activity biết mình bấm vào danh mục nào =====
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category, int position);
    }

    private final Context context;
    private final List<Category> categoryList;
    @Nullable
    private final OnCategoryClickListener listener;

    // Vị trí đang được chọn (để đổi màu)
    private int selectedPosition = RecyclerView.NO_POSITION;

    // ✅ Constructor cũ – GIỮ NGUYÊN để không phá code cũ
    public CategoryAdapter(Context context, List<Category> categoryList) {
        this(context, categoryList, null);
    }

    // ✅ Constructor mới – dùng ở CategoryActivity mới (có listener)
    public CategoryAdapter(Context context,
                           List<Category> categoryList,
                           @Nullable OnCategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    // Cho phép Activity set tab đang chọn
    public void setSelectedPosition(int position) {
        int old = selectedPosition;
        selectedPosition = position;

        if (old != RecyclerView.NO_POSITION) {
            notifyItemChanged(old);
        }
        if (selectedPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(selectedPosition);
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getName());
        holder.imgCategory.setImageResource(category.getImageRes());

        // ========= Highlight danh mục đang chọn =========
        boolean isSelected = (position == selectedPosition);

        // Màu chữ & icon
        int textColor = isSelected ? 0xFF1F2933 : 0xFF4B5563;   // đậm hơn khi chọn
        int iconColor = isSelected ? 0xFF3B82F6 : 0xFF6B7280;   // icon xanh khi chọn

        holder.tvCategoryName.setTextColor(textColor);
        holder.imgCategory.setColorFilter(iconColor);

        // Background nhẹ (nếu item_category là LinearLayout, sẽ đổi cả nền)
        holder.itemView.setBackgroundColor(isSelected ? 0xFFE3F2FD : 0x00000000);

        // ========= Xử lý click =========
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            if (listener != null) {
                listener.onCategoryClick(categoryList.get(pos), pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategory;
        TextView tvCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.imgCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}
