
package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.Address;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private List<Address> addressList = new ArrayList<>();
    private OnAddressClickListener listener;

    public interface OnAddressClickListener {
        void onEditClick(Address address);
        void onSetDefaultClick(Address address);
        void onSelectAddress(Address address);
    }

    public AddressAdapter(OnAddressClickListener listener) {
        this.listener = listener;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.bind(address);
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvAddress, tvOldAddress, tvEdit, tvDefaultBadge;
        MaterialButton btnSetDefault;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvOldAddress = itemView.findViewById(R.id.tv_old_address);
            tvEdit = itemView.findViewById(R.id.tv_edit);
            tvDefaultBadge = itemView.findViewById(R.id.tv_default_badge);
            btnSetDefault = itemView.findViewById(R.id.btn_set_default);
        }

        public void bind(Address address) {
            tvName.setText(address.getTenNguoiNhan());
            tvPhone.setText(address.getSoDienThoai());
            tvAddress.setText(address.getFullAddress());

            // Hiển thị hoặc ẩn badge/button mặc định
            if (address.isDefault()) {
                tvDefaultBadge.setVisibility(View.VISIBLE);
                btnSetDefault.setVisibility(View.GONE);
            } else {
                tvDefaultBadge.setVisibility(View.GONE);
                btnSetDefault.setVisibility(View.VISIBLE);
            }

            // Click sửa
            tvEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(address);
                }
            });

            // Click đặt làm mặc định
            btnSetDefault.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSetDefaultClick(address);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSelectAddress(address);
                }
            });

        }
    }
}