
package com.example.baitap01_nhom6_ui_login_register_forgetpass.util;

import android.app.AlertDialog;
import android.content.Context;
import java.util.Arrays;
import java.util.List;

public class LocationPickerHelper {

    public interface OnLocationSelectedListener {
        void onLocationSelected(String province, String district, String ward);
    }

    // Danh sách tỉnh/thành phố mẫu
    private static final List<String> PROVINCES = Arrays.asList(
            "Hà Nội", "Hồ Chí Minh", "Đà Nẵng", "Hải Phòng", "Cần Thơ",
            "Bình Dương", "Đồng Nai", "Khánh Hòa", "Lâm Đồng", "Ninh Bình"
    );

    // Danh sách quận/huyện mẫu (đơn giản hóa)
    private static final List<String> DISTRICTS = Arrays.asList(
            "Quận 1", "Quận 2", "Quận 3", "Quận 4", "Quận 5",
            "Quận 6", "Quận 7", "Quận 8", "Quận 9", "Quận 10"
    );

    // Danh sách phường/xã mẫu
    private static final List<String> WARDS = Arrays.asList(
            "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5",
            "Phường Bến Nghé", "Phường Bến Thành", "Phường Cầu Kho", "Phường Dĩ An"
    );

    public static void showLocationPicker(Context context, OnLocationSelectedListener listener) {
        final String[] selectedProvince = {""};
        final String[] selectedDistrict = {""};
        final String[] selectedWard = {""};

        // Step 1: Chọn tỉnh/thành phố
        AlertDialog.Builder provinceBuilder = new AlertDialog.Builder(context);
        provinceBuilder.setTitle("Chọn Tỉnh/Thành phố");
        provinceBuilder.setItems(PROVINCES.toArray(new String[0]), (dialog, which) -> {
            selectedProvince[0] = PROVINCES.get(which);

            // Step 2: Chọn quận/huyện
            AlertDialog.Builder districtBuilder = new AlertDialog.Builder(context);
            districtBuilder.setTitle("Chọn Quận/Huyện");
            districtBuilder.setItems(DISTRICTS.toArray(new String[0]), (dialog2, which2) -> {
                selectedDistrict[0] = DISTRICTS.get(which2);

                // Step 3: Chọn phường/xã
                AlertDialog.Builder wardBuilder = new AlertDialog.Builder(context);
                wardBuilder.setTitle("Chọn Phường/Xã");
                wardBuilder.setItems(WARDS.toArray(new String[0]), (dialog3, which3) -> {
                    selectedWard[0] = WARDS.get(which3);

                    // Callback kết quả
                    if (listener != null) {
                        listener.onLocationSelected(
                                selectedProvince[0],
                                selectedDistrict[0],
                                selectedWard[0]
                        );
                    }
                });
                wardBuilder.show();
            });
            districtBuilder.show();
        });
        provinceBuilder.show();
    }
}