# 📈 TickerCMPFlow (KMP Technical Challenge)

A real-time stock market ticker built with **Kotlin Multiplatform (KMP)**, **Compose Multiplatform (CMP)**, and **Koin 4.0**. This project demonstrates a reactive architecture from a WebSocket stream to a shared ViewModel.

## 🚀 Key Features (Phase 2 Completed)
- **Unified Architecture**: ViewModels, UseCases, and Repositories live in `commonMain`.
- **Reactive Data Pipeline**: Ktor WSS stream mapped to `StateFlow` with millisecond precision.
- **Koin 4.0 DSL**: Dependency injection using the latest `viewModel` and `factoryOf` standards.
- **Clean Domain**: Decoupled models and alphabetical sorting logic.

## 🛠️ Tech Stack
- **UI**: Compose Multiplatform (1.10.3)
- **DI**: Koin (4.0.0)
- **Networking**: Ktor (WebSockets, Serialization)
- **Logging**: Napier
- **Time**: Kotlinx-datetime

## 🧪 Testing & Validation (Logcat Protocol)

To verify the data pipeline without a UI yet, filter the Android Studio Logcat by the tag `StockRepo`:

### Expected Output (2s Interval)
The repository processes batches of 25 symbols. You should see a consistent flow like this:

```text
22:29:27.547 D 📈 TICK -> Symbol: AAPL | Price: 450.5 | %: -2.51 | TS: 1775190567546
22:29:27.548 D 📈 TICK -> Symbol: GOOGL | Price: 456.87 | %: -1.94 | TS: 1775190567546
...
22:29:27.549 V ✅ Batch of 25 processed and emitted to Flow