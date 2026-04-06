# 🗺️ Project Roadmap - TickerCMPFlow

## Phase 0: Foundation ✅
- [x] Multiplatform Project Scaffolding (CMP).
- [x] Version Catalogs (libs.versions.toml).
- [x] Koin DI Implementation (CommonMain).

## Phase 1: Data & Networking ✅
- [x] **Ktor Engine**: Configuration for OkHttp (Android) and Darwin (iOS).
- [x] **WebSocket Infrastructure**: `wss` implementation for real-time streams.
- [x] **Mock Generator**: 2s loop interval for 25 financial symbols.

## Phase 2: Reactive Architecture ✅
- [x] **Domain Models**: `StockTick` with `@Serializable` and millisecond precision.
- [x] **Stream Parsing**: Mapper implementation using `kotlin.time.Clock`.
- [x] **Clean Architecture**: `GetStockUpdatesUseCase` with alphabetical sorting logic.
- [x] **State Management**: `StockViewModel` in `commonMain` using `stateIn`.
- [x] **Logcat Validation**: Verified 2ms batch processing (25 items/tick).

## Phase 3: Core UI & Navigation ✅
- [x] **Type-Safe Navigation**: Implementation of `androidx-navigation` in `shared`.
- [x] **Shared Theme**: Material3 setup with OLED-ready Dark Mode.
- [x] **Feed Screen**: `LazyColumn` with `animateItem()` and stable sorting (Price + Symbol).
- [x] **Stock Item**: Enhanced legibility with Company Names and Delta indicators.
- [x] **Detail Screen**: 
    - [x] **Shared Elements**: Seamless transition for Symbol and Price.
    - [x] **Candlestick Chart**: Real-time OHLC visualization using `Canvas`.
    - [x] **Ergonomics**: Bottom-fixed CTA (Zone del Pulgar) and Staggered entrance (80ms).
    - [x] **Sincronización**: Unified `ConnectionBadge` and Persistent User Intent.

## Phase 4: Hardening & Resilience 🛡️ ✅
- [x] **Connection Management**: Auto-reconnect logic for WebSocket failures.
- [x] **Lifecycle Awareness**: Refined energy saving strategies for background processes.
- [x] **Error UI**: Implementation of Empty States, Loading Shimmers, and Error Banners.
- [x] **Unit Testing**: `StockMapperTest` validating decimal rounding and parsing.

## Phase 5: Polish & Performance ⚡ (NEXT)
- [ ] **Image Resources**: Setup `composeResources` for icons and splash screen.
- [ ] **Performance Audit**: Benchmarking recomposition counts on the LazyColumn.
- [ ] **Offline Persistence**: (Optional) Room/SQLDelight for last-seen price caching.
- [ ] *(Optional)* **Builds**: Release APK for Android / XCode config for iOS.
- [ ] *(Optional)* **CI/CD**: Simple GitHub Action for build verification.
