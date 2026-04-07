# 📈 TickerCMPFlow (KMP Technical Challenge)

A real-time stock market terminal built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform (CMP)**. This project implements a high-frequency reactive architecture using WebSockets and Clean Architecture principles.

## ⚡ Performance & Polish (Phase 5 Completed) ✅
- **Stability Hardening**: Custom `@Immutable` and `@Stable` markers to minimize recompositions.
- **UI Stability Wrappers**: Isolated `StockList` component to enable Compose **Smart Skipping**.
- **Institutional Branding**: Custom "T-Black Core" logo and official Android Splash Screen API.
- **OLED Optimized**: High-contrast theme with zero-latency high-frequency updates.

## 🛡️ Resilience & Hardening
- **Auto-Reconnect Engine**: Implements **Exponential Backoff** (2s, 4s, 8s... up to 30s) for WebSocket recovery.
- **Fast Persistent Error Detection**: UI transitions to critical error state after 30s of total failure.
- **Lifecycle-Aware Network**: Proactive background silencing to prevent battery drain and crashes.
- **Error UI Pro**: Integrated Shimmers, Empty States, and dynamic Error Banners.

## 🧪 How to Test & Audit

### 1. Performance Audit (Benchmarking)
- **Scenario**: Validate that high-frequency updates (25 tickers/2s) don't cause performance leaks.
- **Steps**:
    1. Open Logcat and filter by tag: `PERF`.
    2. Observe the logs: `⚡ Item [SYMBOL] recomposed X times`.
    3. **The Audit**: Verify that counters increase linearly (5, 10, 15...). If an item moves position due to sorting, its counter **must not reset**, confirming that item identity is preserved and the UI is being reused, not recreated.
    4. **Smart Skipping**: Notice that when the connection status changes, items that haven't changed their price **skip** recomposition entirely.

### 2. Connection Recovery (The "Tunnel" Test)
- **Scenario**: Simulate entering a tunnel with no signal.
- **Steps**:
    1. Toggle the Switch to `ON`. Wait for the `LIVE` badge.
    2. **Disable Wi-Fi/Data**.
    3. Observe: `ConnectionBadge` pulsates in Amber (`RETRYING`) and after 30s the `NetworkErrorBanner` shows "🚨 Persistent connection issue".
    4. **Enable Wi-Fi/Data**: The app restores the stream automatically.

### 3. Precision & Data Validation (Unit Tests)
- Run: `./gradlew :shared:testDebugUnitTest`.
- Confirms mathematical rounding accuracy (`120.339` -> `120.34`) and protocol resilience.

## 🛠️ Build & Run
1. Clone the repo.
2. Open in **Android Studio Ladybug**.
3. Run `:composeApp` on an Android Emulator or physical device.
