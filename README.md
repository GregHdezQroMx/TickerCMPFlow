## 🚀 Phase 1: Infrastructure & WebSocket Smoke Test

The core networking and dependency injection layers are now operational.

### How to Test
1. **Target**: Run the `composeApp` on an Android Emulator or Physical Device (API 24+).
2. **Connectivity**: Ensure the device has internet access (WSS required).
3. **Observation**: Open **Logcat** in Android Studio.

### Expected Output (Logcat)
Filter by tag `StockRepo` to see the real-time communication:

- **Connection**: `D/StockRepo: WSS Session Established: ws.postman-echo.com`
- **Data Out**: `D/StockRepo: ⬆️ Sent WSS: [Symbol],[RandomPrice],[RandomChange]|...`
- **Echo In**: `D/StockRepo: ⬇️ Received Echo: [Symbol],[RandomPrice],[RandomChange]|...`

*Note: The data is currently being emulated by the repository and echoed back by the server to validate the full-duplex secure tunnel.*