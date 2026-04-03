# 🗺️ Project Roadmap - TickerCMPFlow

## Phase 0: Foundation ✅
- [x] Multiplatform Project Scaffolding (CMP).
- [x] Version Catalogs (libs.versions.toml).
- [x] Koin DI Implementation (CommonMain).

## Phase 1: Data & Networking 🛰️ ✅
- [x] Domain Models & Repository Interface.
- [x] Network Infrastructure (Ktor & Serialization).
- [x] **Platform-specific Logging**: Napier integration with `DebugAntilog` (androidMain).
- [x] **Secure WebSocket Service**: Ktor `wss` implementation for Postman Echo.
- [x] **Interval Generation**: 2s loop logic with random price/change emulation.
- [x] **Infra-UI Decoupling**: Centralized `KoinInitializer` (expect/actual) for clean startup.

## Phase 2: Reactive Navigation & Architecture 📱 🕒
- [ ] **Stream Parsing**: Transform WSS Echo strings into `List<StockTick>`.
- [ ] Shared Navigation Graph (Feed <-> Details).
- [ ] ViewModel implementation with `StateFlow`.
- [ ] Dependency injection for ViewModels in Shared Module.

## Phase 3: Core UI & Real-time Features 📊
- [ ] **Feed Screen**: Real-time sorted LazyColumn reacting to Repository Flow.
- [ ] **Detail Screen**: Persistent state via `SavedStateHandle`.
- [ ] Connection Status & Toggle Feed logic.

## Phase 4: Android Specifics & Deep Linking 🔗
- [ ] Android Deep Link configuration (`stocks://symbol/{symbol}`).
- [ ] Light/Dark theme support (Material 3).
- [ ] Unit Tests for ViewModels & Repository (Mocking WSS).

## Phase 5: Premium UX & Animations ✨
- [ ] **Shared Element Transitions**: Smooth transition from Feed to Details.
- [ ] **Price Flash Animations**: High-fidelity color pulses (Green/Red).
- [ ] **List Animations**: `Modifier.animateItemPlacement()` for sorting updates.
- [ ] **Lottie/Compose Runtime Animations**: For connection states.

---

### 📋 Phase 1 - Verification Notes
* **Network**: WSS is fully operational and verified via Logcat.
* **DI**: Koin graph is correctly injecting `baseUrl`, `apiPath`, and `json` into `StockRepositoryImpl`.
* **Logging**: Infrastructure logs are active on Android without leaking into `MainActivity`.