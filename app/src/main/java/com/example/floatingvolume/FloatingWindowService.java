package com.example.floatingvolume;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class FloatingWindowService extends Service {
    
    private WindowManager windowManager;
    private View floatingView;
    private AudioManager audioManager;
    private boolean isFloating = false;
    
    // 悬浮窗位置参数
    private int initialX, initialY;
    private float initialTouchX, initialTouchY;
    private WindowManager.LayoutParams params;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case "START":
                    if (!isFloating) {
                        createFloatingWindow();
                    }
                    break;
                case "STOP":
                    if (isFloating) {
                        removeFloatingWindow();
                    }
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    private void createFloatingWindow() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "需要悬浮窗权限", Toast.LENGTH_SHORT).show();
            return;
        }

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // 创建悬浮窗视图
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_window, null);
        ImageView volumeIcon = floatingView.findViewById(R.id.volumeIcon);

        // 设置悬浮窗参数
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 100;
        params.y = 200;

        // 添加触摸事件监听器
        floatingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingView, params);
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (Math.abs(event.getRawX() - initialTouchX) < 10 && 
                            Math.abs(event.getRawY() - initialTouchY) < 10) {
                            // 点击事件 - 打开音量控制
                            openVolumeControl();
                        }
                        return true;
                }
                return false;
            }
        });

        // 添加悬浮窗到窗口管理器
        try {
            windowManager.addView(floatingView, params);
            isFloating = true;
        } catch (Exception e) {
            Toast.makeText(this, "无法创建悬浮窗: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openVolumeControl() {
        try {
            // 尝试打开系统音量控制面板
            Intent intent = new Intent("android.settings.SOUND_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            // 如果无法打开设置，则直接调整音量
            try {
                audioManager.adjustVolume(AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
            } catch (Exception ex) {
                Toast.makeText(this, "无法打开音量控制", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void removeFloatingWindow() {
        if (floatingView != null && windowManager != null) {
            try {
                windowManager.removeView(floatingView);
                isFloating = false;
            } catch (Exception e) {
                // 忽略异常
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeFloatingWindow();
    }
} 