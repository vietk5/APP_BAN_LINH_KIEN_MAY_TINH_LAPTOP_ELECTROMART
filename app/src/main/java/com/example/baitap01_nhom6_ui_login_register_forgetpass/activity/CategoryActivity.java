package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.CategoryAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Category;
import  com.example.baitap01_nhom6_ui_login_register_forgetpass.util.BottomNavHelper;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    RecyclerView recyclerCategories;
    List<Category> categoryList;
    CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Fix header bị che
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.header), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
        recyclerCategories = findViewById(R.id.recycler_categories);
        recyclerCategories.setLayoutManager(new GridLayoutManager(this, 2));

        // Dữ liệu mẫu
        categoryList = new ArrayList<>();
        categoryList.add(new Category("Laptop", R.drawable.ic_laptop));
        categoryList.add(new Category("PC", R.drawable.ic_pc));
        categoryList.add(new Category("Bàn phím", R.drawable.ic_keyboard));
        categoryList.add(new Category("Loa", R.drawable.ic_speaker));
        categoryList.add(new Category("Chuột", R.drawable.ic_mouse));
        categoryList.add(new Category("Tai nghe", R.drawable.ic_headphones));
        categoryList.add(new Category("Màn hình", R.drawable.ic_monitor));

        adapter = new CategoryAdapter(this, categoryList);
        recyclerCategories.setAdapter(adapter);
        BottomNavHelper.setup(this, "CATEGORY");
    }
}
