// app/src/main/java/.../adapters/AdminRevenueAdapter.java
package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.RevenuePointDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.PriceFormatter;

import java.util.List;

public class AdminRevenueAdapter extends RecyclerView.Adapter<AdminRevenueAdapter.VH> {

    private final List<RevenuePointDto> data;

    public AdminRevenueAdapter(List<RevenuePointDto> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_revenue, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        RevenuePointDto p = data.get(position);
        h.tvLabel.setText(p.label);
        h.tvAmount.setText(PriceFormatter.vnd(p.total));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvLabel, tvAmount;
        VH(@NonNull View itemView) {
            super(itemView);
            tvLabel  = itemView.findViewById(R.id.tvLabel);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
