# 悬浮音量控制应用

一个轻量级的安卓悬浮窗应用，提供快速音量控制功能。

## 功能特性

- 🔒 自动请求系统悬浮窗权限
- 🎯 可自由拖拽的悬浮窗
- 🔊 点击悬浮窗打开系统音量控制面板
- ⚡ 轻量级设计，无额外UI
- 🔄 支持启动/停止悬浮窗服务

## 系统要求

- Android 6.0 (API 23) 或更高版本
- 需要悬浮窗权限

## 使用方法

1. 安装应用后首次启动
2. 点击"授予权限"按钮，在系统设置中允许悬浮窗权限
3. 点击"启动悬浮窗"按钮创建悬浮窗
4. 拖拽悬浮窗到合适位置
5. 点击悬浮窗图标打开音量控制面板
6. 点击"停止悬浮窗"按钮关闭悬浮窗

## 技术实现

- 使用 `WindowManager` 创建系统级悬浮窗
- 通过 `AudioManager` 控制音量
- 实现触摸事件处理实现拖拽功能
- 使用 `Service` 保持悬浮窗运行

## 权限说明

- `SYSTEM_ALERT_WINDOW`: 创建悬浮窗
- `MODIFY_AUDIO_SETTINGS`: 音量控制

## 构建说明

```bash
# 克隆项目
git clone [repository-url]

# 构建APK
./gradlew assembleRelease

# 安装到设备
./gradlew installDebug
```

## 许可证

MIT License 