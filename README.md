# 📈 TickerCMPFlow (KMP Technical Challenge)

A real-time stock market terminal built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform (CMP)**. This project implements a high-frequency reactive architecture using WebSockets and Clean Architecture principles.

## 🛡️ Resilience & Hardening (Phase 4 In Progress)
- **Auto-Reconnect Engine**: Implements **Exponential Backoff** (2s, 4s, 8s... up to 30s) for WebSocket recovery.
- **Zombie Connection Detection**: Proactive health checks on the stream during data transmission.
- **Visual Feedback**: Dynamic `RETRYING` state in the Connection Badge with pulse animations.

## 🚀 Key Features
- **OLED Design System**: High-contrast dark theme with neon accents for financial clarity.
- **Real-time Candlestick Charts**: Custom `Canvas` implementation for OHLC visualization.
- **Shared Element Transitions**: Fluid UI transitions between Feed and Detail screens.
- **Ergonomic Navigation**: Bottom-fixed CTA (Buy Button) in the "Thumb Zone".

## 🛠️ Tech Stack
- **UI**: Compose Multiplatform (1.10.3)
- **Navigation**: Jetpack Navigation Compose (Type-Safe)
- **DI**: Koin (4.0.0)
- **Networking**: Ktor 3.0 (WebSockets, Logging)
- **Logging**: Napier

## 🧪 How to Test Resilience (Phase 4)

### 1. Connection Recovery (The "Tunnel" Test)
- **Scenario**: Simulate entering a tunnel with no signal.
- **Steps**:
    1. Open the app and toggle the Switch to `ON`. Wait for the `LIVE` badge.
    2. **Disable Wi-Fi/Data** on your device.
    3. Observe the `ConnectionBadge`: It will transition from `LIVE` (Green) to `RETRYING` (Amber/Pulse).
    4. Check **Logcat** (Filter: `ToggleUseCase`): You will see logs like `🔄 Retry connection in 2000ms...`, then 4000ms, etc.
    5. **Enable Wi-Fi/Data**: The app will automatically reconnect and transition back to `LIVE` without user intervention.

### 2. Manual Interruption
- **Scenario**: Server-side disconnection.
- **Steps**:
    1. While in `LIVE` mode, put the app in the background.
    2. Check Logcat: `🔋 App Backgrounded: Pausing WebSocket`.
    3. Bring the app back: It should resume the connection immediately if the switch was previously `ON`.

## 🛠️ Build & Run
1. Clone the repo.
2. Open in **Android Studio Ladybug**.
3. Run `:composeApp` on an Android Emulator or physical device.
