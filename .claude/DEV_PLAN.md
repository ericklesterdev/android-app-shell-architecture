# NBA Hub — Incremental Development Plan

18 commits across 8 phases. Each step compiles independently.

Entry-point-first approach — the App Shell is created early so you always have a
runnable app in Android Studio. Features replace placeholders as they're built.

---

## Phase 1: Foundation (Steps 1–2) ✅

### Step 1 — Gradle infrastructure + convention plugins ✅
- `git init`, `.gitignore`, `gradle.properties`, wrapper, root `settings.gradle.kts` + `build.gradle.kts`
- `gradle/libs.versions.toml` — full version catalog (AGP, Kotlin, Compose BOM, OkHttp, kotlinx-serialization, DataStore, Dagger, Navigation, Coroutines, Lifecycle, Activity)
- `build-logic/convention/` — independent Gradle build with binary convention plugins:
  - `nbahub.kotlin.library` — pure Kotlin (JVM, serialization)
  - `nbahub.android.library` — Android library defaults
  - `nbahub.android.library.compose` — adds Compose compiler + BOM
  - `nbahub.android.application` — Android app defaults
  - `nbahub.android.application.compose` — app + Compose
- **Commit:** `chore: initialize Gradle infrastructure with version catalog and convention plugins`
- **Verify:** `./gradlew -p build-logic/convention build`

### Step 2 — Platform contracts ✅
- `platform/contracts/` — independent Gradle build, pure Kotlin library
- `NetworkClient` interface — `suspend fun <T> get(path, queryParams, deserializer): T`
- `StorageClient` interface — `observeFavoriteTeamIds(): Flow<Set<Int>>`, `toggleFavoriteTeam(id)`, `isFavoriteTeam(id)`
- Root `settings.gradle.kts` updated with `includeBuild` + dependency substitution
- **Commit:** `feat: add platform/contracts with NetworkClient and StorageClient interfaces`
- **Verify:** `./gradlew -p platform/contracts build`

---

## Phase 2: App Shell Entry Point (Step 3)

### Step 3 — Minimal App Shell
- `app/` module — Android application in root project
- `NbaHubApplication` — empty Application class
- `MainActivity` — Scaffold, bottom nav (Scores + Teams tabs), placeholder screens
- Placeholder composables: "Scores coming soon", "Teams coming soon"
- Material 3 theme basics (`Theme.kt`, `Color.kt`)
- Theme toggle icon in top bar (local state, not persisted yet)
- **Commit:** `feat: add minimal App Shell with bottom navigation and placeholder screens`
- **Verify:** `./gradlew :app:assembleDebug` — runs on emulator with placeholder tabs

---

## Phase 3: Platform Implementations (Steps 4–6)

### Step 4 — Mock network
- `platform/network-mock/` — Android library, implements `NetworkClient`
- `MockNetworkClient(context)` reads JSON from `assets/mock/` — path mapping: `v1/teams` → `mock/v1_teams.json`
- **Commit:** `feat: add platform/network-mock for asset-based JSON responses`
- **Verify:** `./gradlew -p platform/network-mock build`

### Step 5 — Real network
- `platform/network/` — Android library, implements `NetworkClient`
- `OkHttpNetworkClient(baseUrl, apiKey)` — OkHttp + kotlinx.serialization, `Dispatchers.IO`
- **Commit:** `feat: add platform/network with OkHttp NetworkClient implementation`
- **Verify:** `./gradlew -p platform/network build`

### Step 6 — Storage
- `platform/storage/` — Android library, implements `StorageClient`
- `DataStoreStorageClient(context)` — DataStore Preferences, stores favorite team IDs as string set
- **Commit:** `feat: add platform/storage with DataStore-backed favorites persistence`
- **Verify:** `./gradlew -p platform/storage build`

---

## Phase 4: Teams Feature (Steps 7–10) — Manual DI

### Step 7 — Scaffold feature/teams with showcase app, mock data, and data layer
- `feature/teams/` — independent multi-module Gradle build (`teams/` library + `showcase/` app)
- **Showcase app first:** `feature/teams/showcase/` — Android app wiring `MockNetworkClient` + `DataStoreStorageClient`, placeholder screen ("Teams feature coming soon")
- **Mock JSON:** `showcase/src/main/assets/mock/` — `v1_teams.json` (30 teams), `v1_teams_1.json` (Lakers detail), `v1_players.json` (roster)
- Data models: `Team`, `Player`, `TeamsResponse`, `PlayersResponse` (`@Serializable`, matching BallDontLie API)
- `TeamsService` — calls `NetworkClient` for teams list, team detail, players by team
- `TeamsFeatureConfig` data class + `TeamsFeature` entry point (placeholder composable)
- Root settings updated with `includeBuild` + dependency substitution
- **Commit:** `feat: scaffold feature/teams with showcase app, mock data, and service layer`
- **Verify:** `./gradlew -p feature/teams :showcase:assembleDebug` — runs on emulator with placeholder

### Step 8 — Teams list screen
- `TeamsListViewModel` — teams list + favorites + conference filter (All/Eastern/Western)
- `TeamsListScreen` — header, filter tabs, FAVORITES section (blue cards + star), ALL TEAMS section
- `TeamsFeature` entry point updated to render real list screen
- Components: `TeamCard`, `TeamLogo` (circle + abbreviation), `ConferenceFilterTabs`
- **Commit:** `feat: add Teams list screen with conference filters and favorites`
- **Verify:** `./gradlew -p feature/teams :showcase:assembleDebug` — showcase now shows real teams list

### Step 9 — Team detail screen with roster and favorite FAB
- `TeamDetailViewModel` — team info + roster + favorite state
- `TeamDetailScreen` — back nav, colored banner, info chips, roster list, orange star FAB
- Components: `TeamBanner`, `RosterItem`, `FavoriteFab`
- `TeamsFeature` updated with `TeamDetailScreen(teamId, onBackClick)` method
- Showcase app updated with navigation: list → detail → back
- **Commit:** `feat: add Team detail screen with roster and favorite toggle FAB`
- **Verify:** `./gradlew -p feature/teams :showcase:assembleDebug` — full Teams flow works in showcase

### Step 10 — Wire Teams into App Shell
- Update `app/build.gradle.kts` with feature-teams + platform dependencies
- `AppConfig` — `useMockNetwork`, `apiKey`, `baseUrl`
- `AppContainer` — composition root creating platform instances + `TeamsFeature`
- Replace Teams tab placeholder with real `TeamsFeature` screens
- Teams list → detail navigation via `NavHost`
- Copy teams mock JSON to `app/src/main/assets/mock/`
- **Commit:** `feat: wire Teams feature into App Shell with navigation`
- **Verify:** `./gradlew :app:assembleDebug` — Teams tab shows real content

---

## Phase 5: Scores Feature (Steps 11–13) — Dagger 2

### Step 11 — Scaffold feature/scores with showcase app, mock data, data layer, and Dagger setup
- `feature/scores/` — independent multi-module build (`scores/` library + `showcase/` app)
- **Showcase app first:** `feature/scores/showcase/` — Android app wiring `MockNetworkClient` + `DataStoreStorageClient`, placeholder screen
- **Mock JSON:** `showcase/src/main/assets/mock/` — `v1_games.json` (2 live + 2 final + 2 upcoming games)
- Data models: `Game`, `GameTeam`, `GamesResponse` (`@Serializable`)
- `ScoresService` — calls `NetworkClient` for games
- Internal Dagger 2: `@Component` + `@Module` + `PlatformModule` (bridges platform contracts)
- `ScoresFeatureConfig` data class + `ScoresFeature` entry point (placeholder composable)
- Root settings updated
- **Commit:** `feat: scaffold feature/scores with showcase app, mock data, and Dagger 2 DI`
- **Verify:** `./gradlew -p feature/scores :showcase:assembleDebug` — runs on emulator with placeholder

### Step 12 — Scores list screen
- `ScoresViewModel` (`@Inject`) — games list, favorites split (YOUR TEAMS vs ALL GAMES), live count
- `ScoresScreen` — header + live count subtitle, YOUR TEAMS section (blue), ALL GAMES section
- `ScoresFeature` entry point updated to use `DaggerScoresComponent` and render real screen
- Components: `GameCard` (date, two team rows, status badge), `StatusBadge` (Live red / Final gray / Upcoming outlined)
- **Commit:** `feat: add Scores list screen with game cards, status badges, and favorites`
- **Verify:** `./gradlew -p feature/scores :showcase:assembleDebug` — showcase now shows real scores

### Step 13 — Wire Scores into App Shell
- Update `AppContainer` with `ScoresFeature`
- Replace Scores tab placeholder with real `ScoresFeature` screen
- Cross-feature navigation: Scores `onTeamClick` → Teams detail
- Copy scores mock JSON to `app/src/main/assets/mock/`
- **Commit:** `feat: wire Scores feature into App Shell with cross-feature navigation`
- **Verify:** `./gradlew :app:assembleDebug` — both tabs work, cross-feature nav works

---

## Phase 6: Polish (Steps 14–15)

### Step 14 — Polish theme + loading/error states
- Refined Material 3 colors matching mockup palette
- Team color mapping for detail banners
- Loading spinners and error states for both features
- Navigation transitions
- **Commit:** `feat: polish theme, team colors, loading states, and navigation transitions`

### Step 15 — Dark mode + persistent theme preference
- Dark color scheme for all screens
- Theme preference persisted via StorageClient / DataStore
- Restored on app launch
- **Commit:** `feat: add dark mode with persistent theme preference`

---

## Phase 7: SDK (Steps 16–17)

### Step 16 — NBA Scores SDK
- `sdk/nba-scores-sdk/` — independent Gradle build with `nba-scores-sdk/` library
- `NbaSDK.initialize(NbaSDKConfig(apiKey, environment))` — public entry point
- `LiveScoresWidget()`, `TeamScoresWidget(teamId)` — public composables
- Internal `SDKContainer` wires `OkHttpNetworkClient` + `ScoresFeature`
- All internals hidden from consumer
- Root settings updated
- **Commit:** `feat: add nba-scores-sdk with embeddable live scores widget`
- **Verify:** `./gradlew -p sdk/nba-scores-sdk :nba-scores-sdk:build`

### Step 17 — SDK showcase app
- `sdk/nba-scores-sdk/showcase/` — simulates external company app
- Initializes `NbaSDK`, embeds `LiveScoresWidget` in a simple layout
- Uses `com.example.externalapp` package (not `com.nbahub`)
- **Commit:** `feat: add SDK showcase app simulating external company integration`
- **Verify:** `./gradlew -p sdk/nba-scores-sdk :showcase:assembleDebug` — run on emulator

---

## Phase 8: Documentation (Step 18)

### Step 18 — README
- Architecture diagram, module breakdown, tech stack
- How to run each app (App Shell, Teams showcase, Scores showcase, SDK showcase)
- DI strategy explanation
- Screenshots
- **Commit:** `docs: add comprehensive README with architecture docs and setup instructions`
- **Verify:** Full build: `./gradlew assembleDebug`

---

## Commit History Summary

| # | Commit | Phase |
|---|--------|-------|
| 1 | `chore: initialize Gradle infrastructure with version catalog and convention plugins` | Foundation ✅ |
| 2 | `feat: add platform/contracts with NetworkClient and StorageClient interfaces` | Foundation ✅ |
| 3 | `feat: add minimal App Shell with bottom navigation and placeholder screens` | App Shell Entry Point |
| 4 | `feat: add platform/network-mock for asset-based JSON responses` | Platform |
| 5 | `feat: add platform/network with OkHttp NetworkClient implementation` | Platform |
| 6 | `feat: add platform/storage with DataStore-backed favorites persistence` | Platform |
| 7 | `feat: scaffold feature/teams with showcase app, mock data, and service layer` | Teams |
| 8 | `feat: add Teams list screen with conference filters and favorites` | Teams |
| 9 | `feat: add Team detail screen with roster and favorite toggle FAB` | Teams |
| 10 | `feat: wire Teams feature into App Shell with navigation` | Teams |
| 11 | `feat: scaffold feature/scores with showcase app, mock data, and Dagger 2 DI` | Scores |
| 12 | `feat: add Scores list screen with game cards, status badges, and favorites` | Scores |
| 13 | `feat: wire Scores feature into App Shell with cross-feature navigation` | Scores |
| 14 | `feat: polish theme, team colors, loading states, and navigation transitions` | Polish |
| 15 | `feat: add dark mode with persistent theme preference` | Polish |
| 16 | `feat: add nba-scores-sdk with embeddable live scores widget` | SDK |
| 17 | `feat: add SDK showcase app simulating external company integration` | SDK |
| 18 | `docs: add comprehensive README with architecture docs and setup instructions` | Docs |

---

## Workflow Per Step

1. I implement the step
2. You review the implementation
3. We commit with the planned message
4. Move to next step
