# TickerCMPFlow 📈

**TickerCMPFlow** is a high-performance, real-time stock price tracker built using **Compose Multiplatform (CMP)**. While the primary requirement targets Android, this architecture ensures business logic and UI are shared across platforms, demonstrating a scalable, production-ready approach.

## 🚀 Core Tech Stack
- **Compose Multiplatform:** Shared UI for Android & iOS.
- **Kotlin Coroutines & Flow:** Reactive data streams for real-time updates.
- **Koin:** Dependency Injection optimized for KMP and MVVM.
- **Ktor Client:** Multiplatform WebSocket & HTTP engine.
- **MVVM Architecture:** Clean separation of concerns with immutable StateFlow.

## 🛠️ Setup & Execution
1. Clone the repository.
2. Open in **Android Studio Ladybug** (or later).
3. Run the `composeApp` module on an Android Emulator/Device.