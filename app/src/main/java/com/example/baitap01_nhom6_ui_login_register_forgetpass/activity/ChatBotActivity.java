package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.chatbot.GeminiPro;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.chatbot.ResponseCallback;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.AdminProductDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatBotActivity extends AppCompatActivity {

    private LinearLayout chatContainer;
    private ScrollView chatScrollView;
    private GeminiPro model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_bot);

        TextInputEditText queryEditText = findViewById(R.id.queryEditText);
        MaterialButton sendQueryButton = findViewById(R.id.sendPromptButton);
        TextView responseTextView = findViewById(R.id.modelResponseTextView);
        LinearLayout typingIndicator = findViewById(R.id.typingIndicator);
        chatScrollView = findViewById(R.id.chatScrollView);

        ImageButton backToHomeButton = findViewById(R.id.backToHomeButton);
        backToHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChatBotActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });


        model = new GeminiPro(this);
        sendQueryButton.setOnClickListener(v -> {
            String query = queryEditText.getText().toString().trim();

            if (query.isEmpty()) {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add user message
            addUserMessage(query);

            // Clear input
            queryEditText.setText("");

            // Show typing indicator
            typingIndicator.setVisibility(View.VISIBLE);
            scrollToBottom();

            // Get AI response
            model.getResponse(query, new ResponseCallback() {
                @Override
                public void onResponse(String response) {
                    typingIndicator.setVisibility(View.GONE);
                    addBotMessage(response);
                    scrollToBottom();
                }

                @Override
                public void onError(Throwable throwable) {
                    typingIndicator.setVisibility(View.GONE);
                    Toast.makeText(ChatBotActivity.this,
                            "Error: " + throwable.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void addUserMessage(String message) {
        LinearLayout messageLayout = new LinearLayout(this);
        messageLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        messageLayout.setGravity(android.view.Gravity.END);
        messageLayout.setPadding(0, 0, 0, 24);

        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(100, 0, 0, 0);
        textView.setLayoutParams(params);
        textView.setText(message);
        textView.setTextSize(14);
        textView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        textView.setPadding(32, 24, 32, 24);
        textView.setBackground(ContextCompat.getDrawable(this, R.drawable.user_message_background));

        messageLayout.addView(textView);

        TextView responseView = findViewById(R.id.modelResponseTextView);
        LinearLayout parent = (LinearLayout) responseView.getParent();
        parent.addView(messageLayout, parent.indexOfChild(responseView));
    }

    private void addBotMessage(String message) {
        LinearLayout messageLayout = new LinearLayout(this);
        messageLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        messageLayout.setGravity(Gravity.START);
        messageLayout.setPadding(0, 0, 0, 24);
        messageLayout.setOrientation(LinearLayout.VERTICAL);

        // Nếu có URL ảnh
        if (message.contains("http")) {
            // Tìm link ảnh trong text (rất đơn giản)
            Pattern pattern = Pattern.compile("(https?://\\S+)");
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String imageUrl = matcher.group(1);

                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        600
                );
                imgParams.setMargins(0, 0, 100, 10);
                imageView.setLayoutParams(imgParams);

                Glide.with(this).load(imageUrl).into(imageView);
                messageLayout.addView(imageView);
            }
        }

        // Text phần còn lại
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 100, 0);
        textView.setLayoutParams(params);
        textView.setText(message);
        textView.setTextSize(14);
        textView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        textView.setPadding(32, 24, 32, 24);
        textView.setBackground(ContextCompat.getDrawable(this, R.drawable.bot_message_background));

        messageLayout.addView(textView);

        TextView responseView = findViewById(R.id.modelResponseTextView);
        LinearLayout parent = (LinearLayout) responseView.getParent();
        parent.addView(messageLayout, parent.indexOfChild(responseView));
    }

    private void scrollToBottom() {
        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
    }
}