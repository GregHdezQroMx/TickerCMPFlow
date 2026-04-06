# 📈 TickerCMPFlow (KMP Technical Challenge)

A real-time stock market terminal built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform (CMP)**. This project implements a high-frequency reactive architecture using WebSockets and Clean Architecture principles.

## 🛡️ Resilience & Hardening (Phase 4 Completed) ✅
- **Auto-Reconnect Engine**: Implements **Exponential Backoff** (2s, 4s, 8s... up to 30s) for WebSocket recovery.
- **Fast Persistent Error Detection**: UI transitions to critical error state after 30s of total failure.
- **Lifecycle-Aware Network**: Proactive background silencing to prevent battery drain and crashes.
- **Error UI Pro**: Integrated Shimmers, Empty States, and dynamic Error Banners.
- **Unit Testing**: Math-validated precision for financial rounding and protocol parsing.

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

## 🧪 How to Test Resilience & Quality

### 1. Connection Recovery (The "Tunnel" Test)
- **Scenario**: Simulate entering a tunnel with no signal.
- **Steps**:
    1. Open the app and toggle the Switch to `ON`. Wait for the `LIVE` badge.
    2. **Disable Wi-Fi/Data** on your device.
    3. Observe the `ConnectionBadge`: It will transition from `LIVE` (Green) to `RETRYING` (Amber/Pulse).
    4. Observe the **Error Banner**: After ~30s, it will change to "🚨 Persistent connection issue".
    5. **Enable Wi-Fi/Data**: The app will automatically reconnect.

### 2. Precision & Data Validation (Unit Tests)
- **Scenario**: Validate math rounding and network protocol resilience.
- **Steps**:
    1. Run the following command in the terminal:
       ```bash
       ./gradlew :shared:testDebugUnitTest
       ```
    2. **Verify**: Ensure all tests in `StockMapperTest` pass. This confirms that prices like `120.339` are correctly rounded to `120.34` and malformed WebSocket packets don't crash the terminal.

### 3. Lifecycle Awareness
- Check **Logcat** (Filter: `ToggleUseCase`) while putting the app in background. You will see `🔽 App Backgrounded: Silencing all network activity`, ensuring zero battery usage when not visible.

## 🛠️ Build & Run
1. Clone the repo.
2. Open in **Android Studio Ladybug**.
3. Run `:composeApp` on an Android Emulator or physical device.
