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

## Phase 3: Core UI & Navigation 📱 🕒 (NEXT)
- [ ] **Type-Safe Navigation**: implementation of `androidx-navigation` in `shared`.
- [ ] **Shared Theme**: Material3 setup with custom color schemes for Dark/Light mode.
- [ ] **Feed Screen**: `LazyColumn` with high-frequency updates and `Modifier.animateItem()`.
- [ ] **Stock Item**: Visual indicators (Red/Green) based on `changePercentage` delta.
- [ ] **Detail Screen**: Deep-dive view for a single ticker using Navigation arguments.

## Phase 4: Hardening & Resilience 🛡️
- [ ] **Connection Management**: Auto-reconnect logic for WebSocket failures.
- [ ] **Error UI**: Implementation of Empty States, Loading Shimmers, and Error Banners.
- [ ] **Unit Testing**: 
    - [ ] `StockMapperTest`: Validating decimal rounding and parsing.
    - [ ] `StockViewModelTest`: Mocking the UseCase flow.
- [ ] **Lifecycle Awareness**: Ensuring `WhileSubscribed(5000)` kills the WSS on background.

## Phase 5: Polish & Performance ⚡
- [ ] **Image Resources**: Setup `composeResources` for icons and splash screen.
- [ ] **Performance Audit**: Benchmarking recomposition counts on the LazyColumn.
- [ ] **Offline Persistence**: (Optional) Room/SQLDelight for last-seen price caching.
- [ ] *(Optional)* **Builds**: Release APK for Android / XCode config for iOS.
- [ ] *(Optional)* **CI/CD**: Simple GitHub Action for build verification.
