package com.example.floatingvolume;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1001;
    private Button btnGrantPermission, btnStartFloating, btnStopFloating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupClickListeners();
        updateButtonStates();
    }

    private void initViews() {
        btnGrantPermission = findViewById(R.id.btnGrantPermission);
        btnStartFloating = findViewById(R.id.btnStartFloating);
        btnStopFloating = findViewById(R.id.btnStopFloating);
    }

    private void setupClickListeners() {
        btnGrantPermission.setOnClickListener(v -> requestOverlayPermission());
        btnStartFloating.setOnClickListener(v -> startFloatingWindow());
        btnStopFloating.setOnClickListener(v -> stopFloatingWindow());
    }

    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
            } else {
                Toast.makeText(this, "悬浮窗权限已授予", Toast.LENGTH_SHORT).show();
                updateButtonStates();
            }
        }
    }

    private void startFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(this, FloatingWindowService.class);
            intent.setAction("START");
            startService(intent);
            Toast.makeText(this, "悬浮窗已启动", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "请先授予悬浮窗权限", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopFloatingWindow() {
        Intent intent = new Intent(this, FloatingWindowService.class);
        intent.setAction("STOP");
        startService(intent);
        Toast.makeText(this, "悬浮窗已停止", Toast.LENGTH_SHORT).show();
    }

    private void updateButtonStates() {
        boolean hasPermission = Settings.canDrawOverlays(this);
        btnStartFloating.setEnabled(hasPermission);
        btnStopFloating.setEnabled(hasPermission);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "悬浮窗权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "悬浮窗权限被拒绝", Toast.LENGTH_SHORT).show();
            }
            updateButtonStates();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateButtonStates();
    }
} 