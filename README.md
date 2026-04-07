# 📈 TickerCMPFlow (KMP Technical Challenge)

A real-time stock market terminal built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform (CMP)**. This project implements a high-frequency reactive architecture using WebSockets and Clean Architecture principles.

## ⚡ Performance & Polish (Phase 5 Completed) ✅
- **Stability Hardening**: Custom `@Immutable` and `@Stable` markers to minimize recompositions.
- **UI Stability Wrappers**: Isolated `StockList` component to enable Compose **Smart Skipping**.
- **Institutional Branding**: Custom "T-Black Core" logo and official Splash Screen API.
- **OLED Optimized**: High-contrast theme with zero-latency high-frequency updates.
- **Bonus Features**:
    - **Price Flashing**: Brightness pulse effect (1s duration) on text color.
    - **Dynamic Theme**: Real-time Light/Dark mode toggle.
    - **Deep Linking**: Direct navigation to specific assets.

## 🛡️ Resilience & Hardening
- **Auto-Reconnect Engine**: Implements **Exponential Backoff** (2s to 30s) for WebSocket recovery.
- **Fast Persistent Error Detection**: UI transitions to critical error state after 30s of total failure.
- **Lifecycle-Aware Network**: Proactive background silencing to prevent battery drain.

## 🧪 How to Test & Audit

### 1. Deep Link Navigation (Bonus)
- **Scenario**: Open the app directly into a specific stock detail from an external source.
- **Command**:
  ```bash
  adb shell am start -W -a android.intent.action.VIEW -d "stocks://symbol/BTC" com.jght.business.stockmarket.ticker_cmp_flow
  ```
- **Expectation**: The app should launch and navigate immediately to the **Bitcoin (BTC)** details screen.

### 2. Performance Audit (Benchmarking)
- **Scenario**: Validate that high-frequency updates don't cause performance leaks.
- **Steps**:
    1. Open Logcat and filter by tag: `PERF`.
    2. Observe linear counters (confirming component reuse).
    3. Notice **Smart Skipping** when switching themes or connection states.

### 3. Connection Recovery (The "Tunnel" Test)
- **Scenario**: Simulate signal loss.
- **Steps**:
    1. Toggle Switch to `ON`.
    2. **Disable Wi-Fi/Data**.
    3. Observe Amber `RETRYING` badge and eventually the 30s Error Banner.
    4. **Enable Wi-Fi**: Auto-restoration of the stream.

### 4. Precision & Data Validation (Unit Tests)
- Run: `./gradlew :shared:testDebugUnitTest`.
- Confirms mathematical rounding accuracy and protocol resilience.

## 🛠️ Build & Run
1. Clone the repo.
2. Open in **Android Studio Ladybug**.
3. Run `:composeApp` on an Android Emulator or physical device.
