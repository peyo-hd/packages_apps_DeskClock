# Android `app_function` Dumpsys Analysis

- **Command**: `adb shell dumpsys app_function`
- **Connected Device**: `172.30.1.35:45569` (User `0`)
- **Total Registered AppFunctions**: **83** (across **15 packages/namespaces**)

---

## 1. DeskClock (`com.android.deskclock`) — 10 Functions

All 10 `com.android.deskclock` AppFunctions share the following static/runtime attributes:
- **Package Name**: `com.android.deskclock`
- **Mobile Application Qualified ID**: `android$apps-db/apps#com.android.deskclock`
- **Schema Category**: `clock`
- **Schema Version**: `1`
- **Enabled by Default**: `true`
- **Restrict Callers With Execute App Functions**: `false`
- **Display Name String Res**: `0`
- **Notes**: Parameter/response type metadata is not populated in static metadata for these entries, and `enabled` is not explicitly overridden in runtime metadata.

| Function ID | Schema Name | Enabled by Default | Static Metadata Qualified ID |
| :--- | :--- | :---: | :--- |
| `com.android.deskclock.appaf#ClockFunctions_createAlarm` | `createAlarm` | `true` | `android$apps-db/app_functions#com.android.deskclock/com.android.deskclock.appaf\#ClockFunctions_createAlarm` |
| `com.android.deskclock.appaf#ClockFunctions_findAlarms` | `findAlarms` | `true` | `android$apps-db/app_functions#com.android.deskclock/com.android.deskclock.appaf\#ClockFunctions_findAlarms` |
| `com.android.deskclock.appaf#ClockFunctions_updateAlarm` | `updateAlarm` | `true` | `android$apps-db/app_functions#com.android.deskclock/com.android.deskclock.appaf\#ClockFunctions_updateAlarm` |
| `com.android.deskclock.appaf#ClockFunctions_deleteAlarms` | `deleteAlarms` | `true` | `android$apps-db/app_functions#com.android.deskclock/com.android.deskclock.appaf\#ClockFunctions_deleteAlarms` |
| `com.android.deskclock.appaf#ClockFunctions_showAlarm` | `showAlarm` | `true` | `android$apps-db/app_functions#com.android.deskclock/com.android.deskclock.appaf\#ClockFunctions_showAlarm` |
| `com.android.deskclock.appaf#ClockFunctions_createTimer` | `createTimer` | `true` | `android$apps-db/app_functions#com.android.deskclock/com.android.deskclock.appaf\#ClockFunctions_createTimer` |
| `com.android.deskclock.appaf#ClockFunctions_findTimers` | `findTimers` | `true` | `android$apps-db/app_functions#com.android.deskclock/com.android.deskclock.appaf\#ClockFunctions_findTimers` |
| `com.android.deskclock.appaf#ClockFunctions_updateTimer` | `updateTimer` | `true` | `android$apps-db/app_functions#com.android.deskclock/com.android.deskclock.appaf\#ClockFunctions_updateTimer` |
| `com.android.deskclock.appaf#ClockFunctions_deleteTimers` | `deleteTimers` | `true` | `android$apps-db/app_functions#com.android.deskclock/com.android.deskclock.appaf\#ClockFunctions_deleteTimers` |
| `com.android.deskclock.appaf#ClockFunctions_showTimer` | `showTimer` | `true` | `android$apps-db/app_functions#com.android.deskclock/com.android.deskclock.appaf\#ClockFunctions_showTimer` |

---

## 2. Summary of All Registered AppFunctions by Package

| Package / Namespace | Count | Functions |
| :--- | :---: | :--- |
| **`com.google.android.gms`** (`com.google.android.gms.appfunctions`) | 19 | `getDeviceStateItems`, `getDeviceStateMetadata`, `setDeviceStateItem`, `getUncategorizedDeviceState`, `getDeviceStateItem`, `launchFitDataScreen`, `launchBarcodeScannerWithResult`, `castMyScreen`, `isSystemUpToDate`, `launchBackupSettings`, `openDeviceSetupTips`, `updatePreviewImage`, `extendScreenTime`, `getPLinkUrl`, `launchDocumentScannerWithResult`, `getPlayerStatus`, `openHelpService`, `setAncState`, `addCardToWallet` |
| **`com.xiaomi.aicr`** (`com.xiaomi.aicr.appfunction` & `.devicestate`) | 19 | `queryLastHighSpeedTrainState`, `queryLastGeekEvent`, `queryLastSubwayState`, `searchAppFunctions`, `addGeofence`, `queryGpsStatus`, `perceptionGetElevatorStatus`, `perceptionGetAirplaneStatus`, `listGeofences`, `checkAtHomeOffice`, `removeGeofence`, `perceptionGetMotionStatus`, `setHomeOffice`, `countRecentMediaAdditions`, `clearGeofences`, `getCurrentScreenPagePair`, `getHomeOffice`, `getUncategorizedDeviceState`, `getDeviceStateMetadata` |
| **`com.google.android.apps.adm`** (`com.google.android.apps.adm.memory`) | 12 | `clearAllMemories`, `objectsRemembered`, `rememberAboutPersonalItem`, `ringDevice`, `listDevices`, `recallLocation`, `getAllMemoriesForDisambiguation`, `locateDevice`, `deleteMemory`, `refreshDeviceLocation`, `recallLocationForMultipleObjects`, `recallPersonLocation` |
| **`com.android.deskclock`** (`com.android.deskclock.appaf`) | 10 | `showTimer`, `showAlarm`, `createTimer`, `createAlarm`, `updateTimer`, `updateAlarm`, `deleteTimers`, `findTimers`, `deleteAlarms`, `findAlarms` |
| **`com.miui.calendar`** (`com.miui.calendar.ai.tool.functions`) | 9 | `queryLegalHolidays`, `createEvent`, `updateEvent`, `queryFestivals`, `deleteEvent`, `createBirthday`, `queryEvents`, `createAnniversary`, `createCountdown` |
| **`com.example.chatapp`** (`com.example.chatapp.appfunctions`) | 6 | `sendMessage`, `makeCall`, `fetchSendMessageDetails`, `searchMessages`, `searchContacts`, `updateChatWallpaper` |
| **`com.miui.notes`** (`com.miui.notes.action.provider`) | 4 | `getNotes`, `findNotes`, `createNote`, `showNote` |
| **`com.google.android.apps.wellbeing`** (`...wellbeing.appfunctions`) | 3 | `getWellbeingDeviceState`, `getDeviceStateMetadata`, `setDeviceStateItem` |
| **`com.xiaomi.camera`** (`com.xiaomi.camera.agent.functions`) | 3 | `setCameraTimer`, `startOperation`, `switchMode` |
| **`com.miui.weather2`** (`com.miui.weather2.aitool.functions`) | 2 | `weatherGetCityAlert`, `weatherGetCityWeather` |
| **`com.google.android.googlequicksearchbox`** (`...robinkit.appfunctions`) | 2 | `invokeTextQueryIntelligentAssist`, `invokeRememberThis` |
| **`com.xiaomi.discover`** (`com.xiaomi.market.appfunctions`) | 2 | `getDeviceStateMetadata`, `getUncategorizedDeviceState` |
| **`com.miui.gallery`** (`com.miui.gallery.appfunctions`) | 1 | `searchPhotos` |
| **`com.miui.misound`** (`com.miui.misound.miclaw.functions`) | 1 | `misoundSettings` |
| **Other / Test** | 2 | `com.example.appfunctions.agent.data.AbstractFakeAppFunctionService#fakeFunction`, `getPermissionsDeviceState` |

---

## 3. Full Function ID Listing by Package

### `com.google.android.gms` (19)
- `com.google.android.gms.appfunctions.BaseGmsAppFunctionService#getDeviceStateItems`
- `com.google.android.gms.appfunctions.BaseGmsAppFunctionService#getDeviceStateMetadata`
- `com.google.android.gms.appfunctions.BaseGmsAppFunctionService#setDeviceStateItem`
- `com.google.android.gms.appfunctions.BaseGmsAppFunctionService#getUncategorizedDeviceState`
- `com.google.android.gms.appfunctions.BaseGmsAppFunctionService#getDeviceStateItem`
- `com.google.android.gms.appfunctions.BaseGmsAppFunctionService#launchFitDataScreen`
- `com.google.android.gms.appfunctions.BaseGmsAppFunctionService#launchBarcodeScannerWithResult`
- `com.google.android.gms.appfunctions.BaseGmsAppFunctionService#castMyScreen`
- `com.google.android.gms.appfunctions.BaseGmsAppFunctionService#isSystemUpToDate`
- `com.google.android.gms.appfunctions.BaseGmsAppFunctionService#launchBackupSettings`
- `com.google.android.gms.appfunctions.BaseGmsAppFunctionService#openDeviceSetupTips`
- `com.google.android.gms.appfunctions.BaseGmsAppFunctionService#updatePreviewImage`
- `com.google.android.gms.appfunctions.BaseGmsAppFunctionService#extendScreenTime`
- `com.google.android.gms.appfunctions.BaseGmsAppFunctionService#getPLinkUrl`
- `com.google.android.gms.appfunctions.BaseGmsAppFunctionService#launchDocumentScannerWithResult`
- `com.google.android.gms.appfunctions.BaseGmsAppFunctionService#getPlayerStatus`
- `com.google.android.gms.appfunctions.BaseGmsAppFunctionService#openHelpService`
- `com.google.android.gms.appfunctions.BaseGmsAppFunctionService#setAncState`
- `com.google.android.gms.appfunctions.BaseGmsAppFunctionService#addCardToWallet`

### `com.xiaomi.aicr` (19)
- `com.xiaomi.aicr.appfunction.TrafficStateFunctions#queryLastHighSpeedTrainState`
- `com.xiaomi.aicr.appfunction.EventQueryFunctions#queryLastGeekEvent`
- `com.xiaomi.aicr.appfunction.TrafficStateFunctions#queryLastSubwayState`
- `com.xiaomi.aicr.appfunction.AppFunctionSearchFunctions#searchAppFunctions`
- `com.xiaomi.aicr.appfunction.GeofencingFunctions#addGeofence`
- `com.xiaomi.aicr.appfunction.GpsStatusFunctions#queryGpsStatus`
- `com.xiaomi.aicr.appfunction.SensorStatusFunctions#perceptionGetElevatorStatus`
- `com.xiaomi.aicr.appfunction.SensorStatusFunctions#perceptionGetAirplaneStatus`
- `com.xiaomi.aicr.appfunction.GeofencingFunctions#listGeofences`
- `com.xiaomi.aicr.appfunction.HomeOfficeFunctions#checkAtHomeOffice`
- `com.xiaomi.aicr.appfunction.GeofencingFunctions#removeGeofence`
- `com.xiaomi.aicr.appfunction.SensorStatusFunctions#perceptionGetMotionStatus`
- `com.xiaomi.aicr.appfunction.HomeOfficeFunctions#setHomeOffice`
- `com.xiaomi.aicr.appfunction.MediaGalleryFunctions#countRecentMediaAdditions`
- `com.xiaomi.aicr.appfunction.GeofencingFunctions#clearGeofences`
- `com.xiaomi.aicr.appfunction.ScreenPageFunctions#getCurrentScreenPagePair`
- `com.xiaomi.aicr.appfunction.HomeOfficeFunctions#getHomeOffice`
- `com.xiaomi.aicr.devicestate.GetUncategorizedDeviceStates#getUncategorizedDeviceState`
- `com.xiaomi.aicr.devicestate.GetDeviceStateMetadatas#getDeviceStateMetadata`

### `com.google.android.apps.adm` (12)
- `com.google.android.apps.adm.memory.BaseMemoryAppFunctionService#clearAllMemories`
- `com.google.android.apps.adm.memory.BaseMemoryAppFunctionService#objectsRemembered`
- `com.google.android.apps.adm.memory.BaseMemoryAppFunctionService#rememberAboutPersonalItem`
- `com.google.android.apps.adm.memory.BaseMemoryAppFunctionService#ringDevice`
- `com.google.android.apps.adm.memory.BaseMemoryAppFunctionService#listDevices`
- `com.google.android.apps.adm.memory.BaseMemoryAppFunctionService#recallLocation`
- `com.google.android.apps.adm.memory.BaseMemoryAppFunctionService#getAllMemoriesForDisambiguation`
- `com.google.android.apps.adm.memory.BaseMemoryAppFunctionService#locateDevice`
- `com.google.android.apps.adm.memory.BaseMemoryAppFunctionService#deleteMemory`
- `com.google.android.apps.adm.memory.BaseMemoryAppFunctionService#refreshDeviceLocation`
- `com.google.android.apps.adm.memory.BaseMemoryAppFunctionService#recallLocationForMultipleObjects`
- `com.google.android.apps.adm.memory.BaseMemoryAppFunctionService#recallPersonLocation`

### `com.android.deskclock` (10)
- `com.android.deskclock.appaf#ClockFunctions_showTimer`
- `com.android.deskclock.appaf#ClockFunctions_showAlarm`
- `com.android.deskclock.appaf#ClockFunctions_createTimer`
- `com.android.deskclock.appaf#ClockFunctions_createAlarm`
- `com.android.deskclock.appaf#ClockFunctions_updateTimer`
- `com.android.deskclock.appaf#ClockFunctions_updateAlarm`
- `com.android.deskclock.appaf#ClockFunctions_deleteTimers`
- `com.android.deskclock.appaf#ClockFunctions_findTimers`
- `com.android.deskclock.appaf#ClockFunctions_deleteAlarms`
- `com.android.deskclock.appaf#ClockFunctions_findAlarms`

### `com.miui.calendar` (9)
- `com.miui.calendar.ai.tool.functions.CalendarToolFunctions#queryLegalHolidays`
- `com.miui.calendar.ai.tool.functions.CalendarToolFunctions#createEvent`
- `com.miui.calendar.ai.tool.functions.CalendarToolFunctions#updateEvent`
- `com.miui.calendar.ai.tool.functions.CalendarToolFunctions#queryFestivals`
- `com.miui.calendar.ai.tool.functions.CalendarToolFunctions#deleteEvent`
- `com.miui.calendar.ai.tool.functions.CalendarToolFunctions#createBirthday`
- `com.miui.calendar.ai.tool.functions.CalendarToolFunctions#queryEvents`
- `com.miui.calendar.ai.tool.functions.CalendarToolFunctions#createAnniversary`
- `com.miui.calendar.ai.tool.functions.CalendarToolFunctions#createCountdown`

### `com.example.chatapp` (6)
- `com.example.chatapp.appfunctions.BaseChatAppFunctionService#sendMessage`
- `com.example.chatapp.appfunctions.BaseChatAppFunctionService#makeCall`
- `com.example.chatapp.appfunctions.BaseChatAppFunctionService#fetchSendMessageDetails`
- `com.example.chatapp.appfunctions.BaseChatAppFunctionService#searchMessages`
- `com.example.chatapp.appfunctions.BaseChatAppFunctionService#searchContacts`
- `com.example.chatapp.appfunctions.BaseChatAppFunctionService#updateChatWallpaper`

### `com.miui.notes` (4)
- `com.miui.notes.action.provider.NoteAppFunction#getNotes`
- `com.miui.notes.action.provider.NoteAppFunction#findNotes`
- `com.miui.notes.action.provider.NoteAppFunction#createNote`
- `com.miui.notes.action.provider.NoteAppFunction#showNote`

### `com.google.android.apps.wellbeing` (3)
- `com.google.android.apps.wellbeing.appfunctions.BaseWellbeingAppFunctionService#getWellbeingDeviceState`
- `com.google.android.apps.wellbeing.appfunctions.BaseWellbeingAppFunctionService#getDeviceStateMetadata`
- `com.google.android.apps.wellbeing.appfunctions.BaseWellbeingAppFunctionService#setDeviceStateItem`

### `com.xiaomi.camera` (3)
- `com.xiaomi.camera.agent.functions.AgentToolFunctions#setCameraTimer`
- `com.xiaomi.camera.agent.functions.AgentToolFunctions#startOperation`
- `com.xiaomi.camera.agent.functions.AgentToolFunctions#switchMode`

### `com.miui.weather2` (2)
- `com.miui.weather2.aitool.functions.WeatherToolFunctions#weatherGetCityAlert`
- `com.miui.weather2.aitool.functions.WeatherToolFunctions#weatherGetCityWeather`

### `com.google.android.googlequicksearchbox` / RobinKit (2)
- `com.google.android.apps.search.assistant.surfaces.voice.robin.robinkit.appfunctions.BaseRobinKitAppFunctionService#invokeTextQueryIntelligentAssist`
- `com.google.android.apps.search.assistant.surfaces.voice.robin.robinkit.appfunctions.BaseRobinKitAppFunctionService#invokeRememberThis`

### `com.xiaomi.discover` (2)
- `com.xiaomi.market.appfunctions.GetDiscoverDeviceStateMetadata#getDeviceStateMetadata`
- `com.xiaomi.market.appfunctions.GetDiscoverDeviceState#getUncategorizedDeviceState`

### `com.miui.gallery` (1)
- `com.miui.gallery.appfunctions.GallerySearchFunctions#searchPhotos`

### `com.miui.misound` (1)
- `com.miui.misound.miclaw.functions.MiSoundAppFunctions#misoundSettings`

### Other / Test (2)
- `com.example.appfunctions.agent.data.AbstractFakeAppFunctionService#fakeFunction`
- `getPermissionsDeviceState`
