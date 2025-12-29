package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.voucher.VoucherDto;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AdminVoucherAdapter extends RecyclerView.Adapter<AdminVoucherAdapter.VH> {

    public interface Listener {
        void onEdit(VoucherDto v);
        void onToggle(VoucherDto v);
        void onDelete(VoucherDto v);
    }

    private final Context ctx;
    private final Listener listener;
    private final List<VoucherDto> data = new ArrayList<>();

    public AdminVoucherAdapter(Context ctx, Listener listener) {
        this.ctx = ctx;
        this.listener = listener;
    }

    public void submit(List<VoucherDto> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_admin_voucher, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        VoucherDto v = data.get(position);

        h.tvCode.setText(v.code == null ? "" : v.code);

        String pct = (v.phanTram == null ? "0" : String.valueOf(v.phanTram)) + "%";
        BigDecimal max = v.giamToiDa == null ? BigDecimal.ZERO : v.giamToiDa;

        h.tvInfo.setText("Giảm " + pct + " • Tối đa " + max.toPlainString() + "đ");

        String sl = (v.soLuongPhatHanh == null ? "∞" : String.valueOf(v.soLuongPhatHanh));
        String used = (v.daSuDung == null ? "0" : String.valueOf(v.daSuDung));
        String time = "Hiệu lực: " + safeShortDate(v.hieuLucTu) + " - " + safeShortDate(v.hieuLucDen);

        h.tvTimeQty.setText(time + " • SL: " + sl + " • Đã dùng: " + used);

        if (v.hoatDong) {
            h.chipStatus.setText("Đang bật");
            h.chipStatus.setChipBackgroundColorResource(R.color.green_500);
        } else {
            h.chipStatus.setText("Tắt");
            h.chipStatus.setChipBackgroundColorResource(R.color.gray_600);
        }

        h.btnMore.setOnClickListener(btn -> {
            PopupMenu pm = new PopupMenu(ctx, btn);
            pm.getMenuInflater().inflate(R.menu.menu_admin_voucher_item, pm.getMenu());
            pm.setOnMenuItemClickListener(item -> handleMenu(item, v));
            pm.show();
        });
    }

    private boolean handleMenu(MenuItem item, VoucherDto v) {
        int id = item.getItemId();
        if (id == R.id.action_edit) { listener.onEdit(v); return true; }
        if (id == R.id.action_toggle) { listener.onToggle(v); return true; }
        if (id == R.id.action_delete) { listener.onDelete(v); return true; }
        return false;
    }

    private String safeShortDate(String iso) {
        if (iso == null || iso.isEmpty()) return "--/--";
        // hiển thị nhanh: lấy 10 ký tự đầu (yyyy-MM-dd)
        return iso.length() >= 10 ? iso.substring(0, 10) : iso;
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvCode, tvInfo, tvTimeQty;
        Chip chipStatus;
        MaterialButton btnMore;
        VH(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            tvTimeQty = itemView.findViewById(R.id.tvTimeQty);
            chipStatus = itemView.findViewById(R.id.chipStatus);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}
