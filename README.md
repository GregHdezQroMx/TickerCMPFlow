# 📈 TickerCMPFlow (KMP Technical Challenge)

A real-time stock market terminal built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform (CMP)**. This project implements a high-frequency reactive architecture using WebSockets and Clean Architecture principles.

## 🚀 Key Features (Phase 3 Completed)
- **OLED Design System**: High-contrast dark theme with neon accents for financial clarity.
- **Real-time Candlestick Charts**: Custom `Canvas` implementation for OHLC visualization.
- **Airbnb-style Animations**: Coreographed staggered entrances (80ms) and Shared Element transitions.
- **Ergonomic Navigation**: Bottom-fixed CTA (Buy Button) in the "Thumb Zone".
- **Resilient Connectivity**: Unified `ConnectionBadge` with atomic state synchronization.
- **Reactive Domain**: Simulation loop with ±1.5% volatility and stable sorting (Price + Symbol).

## 🛠️ Tech Stack
- **UI**: Compose Multiplatform (1.10.3)
- **Navigation**: Jetpack Navigation Compose (Type-Safe)
- **DI**: Koin (4.0.0)
- **Networking**: Ktor 3.0 (WebSockets, Logging)
- **Logging**: Napier

## 🧪 How to Test Phase 3

### 1. Visual Validation (UI/UX)
- **Feed Screen**: Toggle the Switch at the TopAppBar. The status should change to `LIVE` (Green) only when the connection is established.
- **Stable Sort**: Observe how symbols reorder smoothly based on price without "jumping" erratically.
- **Shared Elements**: Tap on any Ticker (e.g., NVDA). The Symbol and Price should "fly" to the detail screen header.
- **Detail Screen**: Appreciate the staggered load of content (80ms delay). The Candlestick chart updates in real-time with the feed.

### 2. Network & Lifecycle (Logcat Protocol)
Filter Android Studio Logcat by tag `HTTP_CLIENT` or `ToggleUseCase`:

- **WebSocket Frames**: You will see real binary/text frames being logged by Ktor.
- **Backgrounding**: Press Home button. You should see `🔋 App Backgrounded: Pausing WebSocket`. The connection closes physically to save battery.
- **Resuming**: Open the app again. Connection restores automatically if the switch was ON.

### 3. Error Handling
- Turn off your internet or wait for a server timeout. The `ConnectionBadge` will turn `OFFLINE` (Red) and the Switch will auto-revert to OFF, maintaining UI integrity.
