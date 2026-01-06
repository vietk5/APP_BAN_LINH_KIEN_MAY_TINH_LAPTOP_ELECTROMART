package com.example.baitap01_nhom6_ui_login_register_forgetpass.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.AddressListActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.ChangePasswordActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.LoginActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.MyOrdersActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.RegisterActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.UpdateProfileActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ApiResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.UserDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private SharedPrefManager sharedPref;
    private ApiService apiService;

    private TextView tvUserName, tvUserEmail;
    private LinearLayout menuProfile, menuOrders, menuAddress, menuChangePassword, btnLogout;

    // Loading overlay
    private FrameLayout loadingOverlay;
    private ProgressBar progressLoading;
    private TextView txtLoading;

    // Prevent double loading
    private Call<ApiResponse<UserDto>> profileCall;
    private boolean isFetchingProfile = false;

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    loadUserInfoFromApi();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPref = new SharedPrefManager(requireContext());
        apiService = ApiClient.get();

        initViews(view);
        setupClickListeners();

        if (!sharedPref.isLoggedIn()) {
            showLoading(false, null);
            loadUserInfoFromSharedPrefGuest();
            showLoginDialog();
            return;
        }

        loadUserInfoFromApi();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sharedPref != null && sharedPref.isLoggedIn()) {
            loadUserInfoFromApi(); // không chồng do isFetchingProfile + cancel
        }
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);

        menuProfile = view.findViewById(R.id.menu_profile);
        menuOrders = view.findViewById(R.id.menu_orders);
        menuAddress = view.findViewById(R.id.menu_address);
        menuChangePassword = view.findViewById(R.id.menu_change_password);
        btnLogout = view.findViewById(R.id.btn_logout);

        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        progressLoading = view.findViewById(R.id.progressLoading);
        txtLoading = view.findViewById(R.id.txtLoading);

        showLoading(false, null);
    }

    private void showLoading(boolean show, String message) {
        if (!isAdded()) return;
        if (txtLoading != null && message != null) txtLoading.setText(message);
        if (loadingOverlay != null) loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void loadUserInfoFromApi() {
        if (sharedPref == null || !sharedPref.isLoggedIn()) return;

        long userId = sharedPref.getUserId();
        if (userId <= 0) {
            loadUserInfoFromSharedPref();
            return;
        }

        // chặn gọi chồng
        if (isFetchingProfile) return;
        isFetchingProfile = true;

        // cancel call cũ
        if (profileCall != null && !profileCall.isCanceled()) {
            profileCall.cancel();
        }

        showLoading(true, "Đang tải thông tin...");

        profileCall = apiService.getProfile(userId);
        profileCall.enqueue(new Callback<ApiResponse<UserDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserDto>> call, Response<ApiResponse<UserDto>> response) {
                try {
                    if (!isAdded()) return;

                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<UserDto> apiResponse = response.body();
                        if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                            UserDto user = apiResponse.getData();
                            displayUserInfo(user);

                            sharedPref.saveUser(
                                    user.getId(),
                                    user.getEmail(),
                                    user.getHoTen() != null ? user.getHoTen() : ""
                            );
                        } else {
                            loadUserInfoFromSharedPref();
                        }
                    } else {
                        loadUserInfoFromSharedPref();
                    }
                } finally {
                    isFetchingProfile = false;
                    showLoading(false, null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserDto>> call, Throwable t) {
                try {
                    if (!isAdded()) return;
                    if (call.isCanceled()) return;
                    loadUserInfoFromSharedPref();
                } finally {
                    isFetchingProfile = false;
                    showLoading(false, null);
                }
            }
        });
    }

    private void loadUserInfoFromSharedPref() {
        String hoTen = sharedPref.getName();
        String email = sharedPref.getEmail();

        tvUserName.setText((hoTen != null && !hoTen.isEmpty()) ? hoTen : "Người dùng");
        tvUserEmail.setText(email != null ? email : "");
    }

    private void loadUserInfoFromSharedPrefGuest() {
        tvUserName.setText("Khách");
        tvUserEmail.setText("Vui lòng đăng nhập");
    }

    private void displayUserInfo(UserDto user) {
        tvUserName.setText((user.getHoTen() != null && !user.getHoTen().isEmpty()) ? user.getHoTen() : "Người dùng");
        tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "");
    }

    private void setupClickListeners() {
        if (menuOrders != null) {
            menuOrders.setOnClickListener(v -> {
                if (checkLogin()) startActivity(new Intent(requireContext(), MyOrdersActivity.class));
            });
        }

        if (menuProfile != null) {
            menuProfile.setOnClickListener(v -> {
                if (checkLogin()) launcher.launch(new Intent(requireContext(), UpdateProfileActivity.class));
            });
        }

        if (menuAddress != null) {
            menuAddress.setOnClickListener(v -> {
                if (checkLogin()) launcher.launch(new Intent(requireContext(), AddressListActivity.class));
            });
        }

        if (menuChangePassword != null) {
            menuChangePassword.setOnClickListener(v -> {
                if (checkLogin()) launcher.launch(new Intent(requireContext(), ChangePasswordActivity.class));
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Đăng xuất")
                        .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                        .setPositiveButton("Đăng xuất", (dialog, which) -> {
                            sharedPref.logout();

                            Intent intent = new Intent(requireContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                            Toast.makeText(requireContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

                            if (getActivity() != null) getActivity().finishAffinity();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            });
        }
    }

    private boolean checkLogin() {
        if (sharedPref == null || !sharedPref.isLoggedIn()) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập trước", Toast.LENGTH_SHORT).show();
            showLoginDialog();
            return false;
        }
        return true;
    }

    private void showLoginDialog() {
        if (!isAdded()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Yêu cầu đăng nhập");
        builder.setMessage("Bạn cần đăng nhập để xem trang cá nhân.");

        builder.setPositiveButton("Đăng nhập", (dialog, which) ->
                startActivity(new Intent(requireContext(), LoginActivity.class)));

        builder.setNegativeButton("Đăng ký", (dialog, which) ->
                startActivity(new Intent(requireContext(), RegisterActivity.class)));

        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (profileCall != null) profileCall.cancel();
        showLoading(false, null);
    }
}
