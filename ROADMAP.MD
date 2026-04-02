# 🗺️ Project Roadmap - TickerCMPFlow

## Phase 0: Foundation ✅
- [x] Multiplatform Project Scaffolding (CMP).
- [x] Version Catalogs (libs.versions.toml).
- [x] Koin DI Implementation (CommonMain).

## Phase 1: Data & Networking 🛰️
- [ ] Domain Models & Repository Interface.
- [ ] Ktor WebSocket Service (Postman Echo).
- [ ] 2s interval price generation logic.

## Phase 2: Reactive Navigation & Architecture 📱
- [ ] Shared Navigation Graph (Feed <-> Details).
- [ ] ViewModel implementation with StateFlow.
- [ ] Dependency injection for ViewModels.

## Phase 3: Core UI & Real-time Features 📊
- [ ] **Feed Screen**: Real-time sorted LazyColumn.
- [ ] **Detail Screen**: Persistent state via SavedStateHandle.
- [ ] Connection Status & Toggle Feed.

## Phase 4: Android Specifics & Deep Linking 🔗
- [ ] Android Deep Link configuration (`stocks://symbol/{symbol}`).
- [ ] Light/Dark theme support (Material 3).
- [ ] Unit Tests for ViewModels & Repository.

## Phase 5: Premium UX & Animations ✨
- [ ] **Shared Element Transitions**: Smooth transition from Feed to Details.
- [ ] **Price Flash Animations**: High-fidelity color pulses (Green/Red).
- [ ] **List Animations**: `Modifier.animateItemPlacement()` for sorting updates.
- [ ] **Lottie/Compose Runtime Animations**: For connection states.