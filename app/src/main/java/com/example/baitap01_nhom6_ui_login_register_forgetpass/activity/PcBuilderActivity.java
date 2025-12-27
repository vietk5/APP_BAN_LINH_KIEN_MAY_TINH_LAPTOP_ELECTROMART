package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.fragment.PcBuilderFragment;

public class PcBuilderActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pc_builder);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new PcBuilderFragment())
                    .commit();
        }
    }
}