package com.example.baitap01_nhom6_ui_login_register_forgetpass.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.LoginActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.SearchActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.PcBuilderAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.PcPartSlot;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Product;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.CartRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.singleton.CartManager;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.PcCompatibilityManager;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.PcIssue;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SharedPrefManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PcBuilderFragment extends Fragment implements PcBuilderAdapter.OnSlotClickListener {

    private RecyclerView recyclerPcBuilder;
    private TextView tvTotalPrice, tvWarning;
    private Button btnAddAllToCart, btnSuggest;

    private PcBuilderAdapter adapter;
    private PcCompatibilityManager manager;

    private ApiService api;
    private SharedPrefManager pref;

    private String currentSelectingKey = ""; // slot đang chọn
    private final List<PcIssue.Suggestion> lastSuggestions = new ArrayList<>();

    private ActivityResultLauncher<Intent> selectionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = PcCompatibilityManager.getInstance();
        api = ApiClient.get();

        if (getContext() != null) {
            pref = new SharedPrefManager(getContext());
        }

        selectionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Product selectedProduct = (Product) result.getData().getSerializableExtra("selected_product");
                        if (selectedProduct != null) {
                            manager.selectProductForSlot(currentSelectingKey, selectedProduct);
                            updateUI();
                        }
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pc_builder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ===== Fix tai thỏ: apply WindowInsets =====
        MaterialToolbar toolbar = view.findViewById(R.id.toolbarPcBuilder);
        View bottomSummary = view.findViewById(R.id.bottomSummary);

        if (toolbar != null) {
            ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(v.getPaddingLeft(), bars.top, v.getPaddingRight(), v.getPaddingBottom());
                return insets;
            });
        }

        if (bottomSummary != null) {
            ViewCompat.setOnApplyWindowInsetsListener(bottomSummary, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                // thêm padding bottom để không dính gesture bar
                v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), bars.bottom);
                return insets;
            });
        }

        // ===== Views =====
        recyclerPcBuilder = view.findViewById(R.id.recyclerPcBuilder);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        tvWarning = view.findViewById(R.id.tvWarning);
        btnAddAllToCart = view.findViewById(R.id.btnAddAllToCart);
        btnSuggest = view.findViewById(R.id.btnSuggest);

        recyclerPcBuilder.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PcBuilderAdapter(getContext(), manager.getSlots(), this);
        recyclerPcBuilder.setAdapter(adapter);

        btnSuggest.setOnClickListener(v -> {
            if (lastSuggestions.isEmpty()) {
                Toast.makeText(getContext(), "Chưa có gợi ý.", Toast.LENGTH_SHORT).show();
                return;
            }

            PcIssue.Suggestion s = lastSuggestions.get(0);
            currentSelectingKey = s.targetSlot;

            Intent intent = new Intent(getContext(), SearchActivity.class);
            intent.putExtra("keyword", s.keyword);
            intent.putExtra("is_selection_mode", true);
            selectionLauncher.launch(intent);
        });

        // ===== Add all to cart thật =====
        btnAddAllToCart.setOnClickListener(v -> addAllSelectedToCart());

        updateUI();
    }

    private void updateUI() {
        adapter.notifyDataSetChanged();

        long total = manager.getTotalPrice();
        tvTotalPrice.setText(NumberFormat.getInstance(new Locale("vi", "VN")).format(total) + " đ");

        List<PcIssue> issues = manager.analyze();
        tvWarning.setVisibility(View.VISIBLE);

        lastSuggestions.clear();

        if (issues.isEmpty()) {
            tvWarning.setText("Cấu hình tương thích tốt!");
            tvWarning.setBackgroundResource(R.drawable.bg_status_ok);
            btnSuggest.setVisibility(View.GONE);
        } else {
            StringBuilder sb = new StringBuilder("Không tương thích:\n");
            for (PcIssue i : issues) {
                sb.append("• ").append(i.message).append("\n");
                lastSuggestions.addAll(i.suggestions);
            }

            tvWarning.setText(sb.toString().trim());
            tvWarning.setBackgroundResource(R.drawable.bg_status_warn);

            if (!lastSuggestions.isEmpty()) {
                btnSuggest.setVisibility(View.VISIBLE);
                btnSuggest.setText(lastSuggestions.get(0).title);
            } else {
                btnSuggest.setVisibility(View.GONE);
            }
        }
    }

    private void addAllSelectedToCart() {
        if (pref == null || !pref.isLoggedIn()) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập trước", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
            return;
        }

        int userId = pref.getUserId();
        if (userId <= 0) {
            Toast.makeText(getContext(), "Lỗi tài khoản, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
            return;
        }

        // ✅ Chọn được cái nào add cái đó (không bắt buộc đủ bộ)
        List<Product> selectedProducts = new ArrayList<>();
        for (PcPartSlot slot : manager.getSlots()) {
            if (slot.getProduct() != null) {
                selectedProducts.add(slot.getProduct());
            }
        }

        if (selectedProducts.isEmpty()) {
            Toast.makeText(getContext(), "Bạn chưa chọn linh kiện nào để thêm vào giỏ", Toast.LENGTH_SHORT).show();
            return;
        }

        btnAddAllToCart.setEnabled(false);

        final int totalItems = selectedProducts.size();
        final int[] done = {0};
        final int[] success = {0};

        for (Product p : selectedProducts) {
            CartRequest req = new CartRequest(userId, p.getId(), 1);

            api.addToCart(req).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> res) {
                    done[0]++;

                    if (res.isSuccessful()) {
                        success[0]++;
                        CartManager.getInstance().addProduct(p);
                    }

                    if (done[0] == totalItems) {
                        btnAddAllToCart.setEnabled(true);
                        Toast.makeText(getContext(),
                                "Đã thêm " + success[0] + "/" + totalItems + " sản phẩm vào giỏ hàng",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    done[0]++;

                    if (done[0] == totalItems) {
                        btnAddAllToCart.setEnabled(true);
                        Toast.makeText(getContext(),
                                "Có lỗi mạng khi thêm giỏ, thử lại nhé",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    @Override
    public void onSelectClick(int position, PcPartSlot slot) {
        currentSelectingKey = slot.getKey();

        Intent intent = new Intent(getContext(), SearchActivity.class);
        intent.putExtra("keyword", slot.getKey());
        intent.putExtra("is_selection_mode", true);
        selectionLauncher.launch(intent);
    }

    @Override
    public void onRemoveClick(int position, PcPartSlot slot) {
        manager.selectProductForSlot(slot.getKey(), null);
        updateUI();
    }
}
