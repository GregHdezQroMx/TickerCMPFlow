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
- [x] **Feed Screen**: `LazyColumn` with `animateItem()` and stable sorting.
- [x] **Stock Item**: Enhanced legibility with Company Names and Delta indicators.
- [x] **Detail Screen**: 
    - [x] **Shared Elements**: Seamless transition for Symbol and Price.
    - [x] **Candlestick Chart**: Real-time OHLC visualization using `Canvas`.
    - [x] **Ergonomics**: Bottom-fixed CTA and Staggered entrance (80ms).
    - [x] **Sincronización**: Unified `ConnectionBadge` and Persistent User Intent.

## Phase 4: Hardening & Resilience 🛡️ ✅
- [x] **Connection Management**: Auto-reconnect logic with Exponential Backoff.
- [x] **Lifecycle Awareness**: Proactive background silencing to prevent battery drain.
- [x] **Error UI**: Implementation of Empty States, Shimmers, and dynamic Banners.
- [x] **Unit Testing**: `StockMapperTest` validating financial precision and parsing.

## Phase 5: Polish & Performance ⚡ ✅
- [x] **Branding & Assets**: Custom "T-Black Core" logo and official Splash Screen API.
- [x] **Performance Hardening**: stability wrappers and @Immutable optimization.
- [x] **Bonus (Expert Tier)**:
    - [x] **Price Flashing**: Brightness pulse effect (1s duration) on price updates. ✅
    - [x] **Deep Linking**: support for `stocks://symbol/{symbol}` scheme. ✅
    - [x] **Dynamic Theme**: Real-time Light/Dark mode toggle in TopBar. ✅
    - [x] **UI Testing**: Compose UI Test for core terminal verification. ✅

---

## 🚀 Next Steps (Future Roadmap)
- [ ] **Offline Persistence**: Room/SQLDelight integration for "Last-Seen" price caching.
- [ ] **Advanced Analytics**: Technical indicators (RSI, Moving Averages) on the Canvas chart.
- [ ] **Push Notifications**: Price alert system using background workers.
- [ ] **CI/CD Pipeline**: GitHub Actions for automated multiplatform builds and tests.
