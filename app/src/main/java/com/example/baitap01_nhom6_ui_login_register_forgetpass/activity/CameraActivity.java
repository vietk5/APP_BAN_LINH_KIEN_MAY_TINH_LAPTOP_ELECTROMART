package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.CapturedImageAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.CapturedImage;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.ImageUtils;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity implements CapturedImageAdapter.OnImageRemovedListener {

    private static final String TAG = "CameraActivity";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final int REQUEST_CODE_GALLERY = 20;
    private static final int MAX_IMAGES = 5;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};

    private PreviewView previewView;
    private ImageButton btnClose, btnCapture, btnGallery;
    private Button btnContinue;
    private TextView tvCounter;
    private RecyclerView rvCapturedImages;

    private ImageCapture imageCapture;
    private CapturedImageAdapter adapter;
    private ArrayList<CapturedImage> capturedImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initViews();
        setupRecyclerView();
        setupClickListeners();

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void initViews() {
        previewView = findViewById(R.id.preview_view);
        btnClose = findViewById(R.id.btn_close);
        btnCapture = findViewById(R.id.btn_capture);
        btnGallery = findViewById(R.id.btn_gallery);
        btnContinue = findViewById(R.id.btn_continue);
        tvCounter = findViewById(R.id.tv_counter);
        rvCapturedImages = findViewById(R.id.rv_captured_images);
    }

    private void setupRecyclerView() {
        adapter = new CapturedImageAdapter(this);
        rvCapturedImages.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCapturedImages.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnClose.setOnClickListener(v -> finish());

        btnCapture.setOnClickListener(v -> {
            if (capturedImages.size() >= MAX_IMAGES) {
                Toast.makeText(this, "Bạn đã chụp tối đa " + MAX_IMAGES + " ảnh",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            captureImage();
        });

        btnGallery.setOnClickListener(v -> {
            if (capturedImages.size() >= MAX_IMAGES) {
                Toast.makeText(this, "Bạn đã chọn tối đa " + MAX_IMAGES + " ảnh",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            openGallery();
        });

        btnContinue.setOnClickListener(v -> {
            if (capturedImages.isEmpty()) {
                Toast.makeText(this, "Vui lòng chụp ít nhất 1 ảnh", Toast.LENGTH_SHORT).show();
                return;
            }
            processImages();
        });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera", e);
                Toast.makeText(this, "Không thể khởi động camera", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases(ProcessCameraProvider cameraProvider) {
        // Preview
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // ImageCapture
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        // Camera selector
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
        } catch (Exception e) {
            Log.e(TAG, "Use case binding failed", e);
        }
    }

    private void captureImage() {
        if (imageCapture == null) return;

        File photoFile = new File(getCacheDir(), "captured_" + System.currentTimeMillis() + ".jpg");

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        // Load và compress bitmap
                        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        Bitmap compressedBitmap = ImageUtils.compressBitmap(bitmap, 1024, 1024);

                        // Tạo CapturedImage object
                        CapturedImage capturedImage = new CapturedImage();
                        capturedImage.setImagePath(photoFile.getAbsolutePath());
                        capturedImage.setBitmap(compressedBitmap);

                        // Add to list
                        capturedImages.add(capturedImage);
                        adapter.addImage(capturedImage);

                        updateUI();

                        Toast.makeText(CameraActivity.this, "Đã chụp ảnh", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Photo capture failed: " + exception.getMessage(), exception);
                        Toast.makeText(CameraActivity.this, "Chụp ảnh thất bại",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    Bitmap bitmap = ImageUtils.getBitmapFromUri(this, selectedImageUri);
                    bitmap = ImageUtils.rotateImageIfRequired(this, bitmap, selectedImageUri);
                    Bitmap compressedBitmap = ImageUtils.compressBitmap(bitmap, 1024, 1024);

                    CapturedImage capturedImage = new CapturedImage();
                    capturedImage.setBitmap(compressedBitmap);
                    capturedImage.setImagePath(selectedImageUri.toString());

                    capturedImages.add(capturedImage);
                    adapter.addImage(capturedImage);

                    updateUI();
                } catch (Exception e) {
                    Log.e(TAG, "Error loading image from gallery", e);
                    Toast.makeText(this, "Không thể tải ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onImageRemoved(int position) {
        if (position >= 0 && position < capturedImages.size()) {
            capturedImages.remove(position);
            adapter.removeImage(position);
            updateUI();
        }
    }

    private void updateUI() {
        int count = capturedImages.size();
        tvCounter.setText(count + "/" + MAX_IMAGES + " ảnh");

        if (count > 0) {
            rvCapturedImages.setVisibility(View.VISIBLE);
            btnContinue.setEnabled(true);
            btnContinue.setAlpha(1.0f);
        } else {
            rvCapturedImages.setVisibility(View.GONE);
            btnContinue.setEnabled(false);
            btnContinue.setAlpha(0.5f);
        }
    }

    private void processImages() {
        // Convert images to base64
        ArrayList<String> base64Images = new ArrayList<>();
        for (CapturedImage image : capturedImages) {
            String base64 = ImageUtils.bitmapToBase64(image.getBitmap());
            base64Images.add(base64);
        }

        // Chuyển sang ImageSearchResultActivity
        Intent intent = new Intent(this, ImageSearchResultActivity.class);
        intent.putStringArrayListExtra("CAPTURED_IMAGES", base64Images);
        startActivity(intent);
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Cần cấp quyền camera để sử dụng tính năng này",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}