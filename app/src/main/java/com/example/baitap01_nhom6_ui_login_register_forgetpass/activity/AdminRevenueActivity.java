package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.AdminRevenueAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.RevenuePointDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.AdminNavHelper;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.PriceFormatter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRevenueActivity extends AppCompatActivity {

    private TextView tvFromDate, tvToDate, tvTotalRevenue;
    private Spinner spGroupBy;
    private RecyclerView recycler;
    private MaterialButton btnApply;

    private final List<RevenuePointDto> data = new ArrayList<>();
    private AdminRevenueAdapter adapter;

    private LocalDate fromDate;
    private LocalDate toDate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_revenue);
        MaterialToolbar toolbar = findViewById(R.id.adminToolbar);
        AdminNavHelper.setupToolbar(this, toolbar, "Admin - Quản lí doanh thu");
        initViews();
        setupSpinner();
        setupDateDefault();
        setupEvents();
        loadRevenue();
    }

    private void initViews() {
        tvFromDate     = findViewById(R.id.tvFromDate);
        tvToDate       = findViewById(R.id.tvToDate);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        spGroupBy      = findViewById(R.id.spGroupBy);
        recycler       = findViewById(R.id.recyclerRevenue);
        btnApply       = findViewById(R.id.btnApply);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminRevenueAdapter(data);
        recycler.setAdapter(adapter);
    }

    private void setupSpinner() {
        // Hiển thị tiếng Việt, map sang code groupBy
        String[] labels = {"Theo ngày", "Theo tuần", "Theo tháng", "Theo năm"};
        ArrayAdapter<String> adapterSp = new ArrayAdapter<>(
                this,
                R.layout.item_admin_spinner,
                labels
        );
        adapterSp.setDropDownViewResource(R.layout.item_admin_spinner_dropdown);
        spGroupBy.setAdapter(adapterSp);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupDateDefault() {
        // mặc định: 7 ngày gần nhất
        toDate   = LocalDate.now();
        fromDate = toDate.minusDays(6);
        tvFromDate.setText(fromDate.toString());
        tvToDate.setText(toDate.toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupEvents() {
        tvFromDate.setOnClickListener(v -> showDatePicker(true));
        tvToDate.setOnClickListener(v -> showDatePicker(false));

        btnApply.setOnClickListener(v -> loadRevenue());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDatePicker(boolean isFrom) {
        LocalDate current = isFrom ? fromDate : toDate;
        Calendar c = Calendar.getInstance();
        c.set(current.getYear(), current.getMonthValue() - 1, current.getDayOfMonth());

        new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    LocalDate d = LocalDate.of(year, month + 1, dayOfMonth);
                    if (isFrom) {
                        fromDate = d;
                        tvFromDate.setText(d.toString());
                    } else {
                        toDate = d;
                        tvToDate.setText(d.toString());
                    }
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private String getGroupByCode() {
        int pos = spGroupBy.getSelectedItemPosition();
        switch (pos) {
            case 0: return "DAY";
            case 1: return "WEEK";
            case 2: return "MONTH";
            case 3: return "YEAR";
            default: return "DAY";
        }
    }

    private void loadRevenue() {
        String fromStr = fromDate.toString(); // yyyy-MM-dd
        String toStr   = toDate.toString();
        String groupBy = getGroupByCode();

        ApiClient.get().getRevenue(fromStr, toStr, groupBy)
                .enqueue(new Callback<List<RevenuePointDto>>() {
                    @Override
                    public void onResponse(Call<List<RevenuePointDto>> call,
                                           Response<List<RevenuePointDto>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(AdminRevenueActivity.this,
                                    "Không tải được dữ liệu",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        data.clear();
                        data.addAll(response.body());
                        adapter.notifyDataSetChanged();

                        long sum = 0;
                        for (RevenuePointDto p : data) sum += p.total;
                        tvTotalRevenue.setText("Tổng doanh thu: " + PriceFormatter.vnd(sum));
                    }

                    @Override
                    public void onFailure(Call<List<RevenuePointDto>> call, Throwable t) {
                        Toast.makeText(AdminRevenueActivity.this,
                                t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
