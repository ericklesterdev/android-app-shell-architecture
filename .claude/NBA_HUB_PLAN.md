# NBA Hub — Implementation Plan

> Architecture reference: [PLAN.md](./PLAN.md)
> API: [BallDontLie NBA API](https://nba.balldontlie.io) (Free tier + mocks)

---

## 1. App Overview

**NBA Hub** — An NBA scores and teams app with favorites support and dark mode.

- 2-tab bottom navigation: Scores, Teams
- Light/dark mode toggle in top bar
- Favorite teams with local persistence
- Favorites surface in both Scores and Teams screens
- Mock-driven development (Free API tier for JSON structure only)

---

## 2. Feature Modules

### feature/scores
**Screens:** Scores list

| Element | Detail |
|---------|--------|
| Header | "Scores" + live game count ("2 games live now") |
| YOUR TEAMS section | Highlighted cards (blue) for games involving favorited teams, star icon. Only shown when user has favorites |
| ALL GAMES section | All remaining games |
| Game card | Date, two teams (logo + city + name + score), status badge |
| Status badges | Live: red "Q4 2:34" / Completed: gray "FINAL" / Upcoming: "7:30 PM" |
| Upcoming games | Show scheduled time, "-" for scores |

**Data flow:**
- Uses Dagger 2 internally to wire dependencies
- ScoresFeature entry point creates a Dagger @Component, passing platform
  contracts from ScoresFeatureConfig as external dependencies
- ViewModel → Service (simple — no use case needed)
- Service calls NetworkClient for games list
- Reads favorite team IDs from platform storage contract
- Splits games into "your teams" vs "all games" based on favorites

**API endpoints:**
- `GET /v1/games` (dates, seasons)
- `GET /v1/box_scores/live` (for live game data)

---

### feature/teams
**Screens:** Teams list, Team detail

#### Teams List Screen

| Element | Detail |
|---------|--------|
| Header | "Teams" |
| Filter tabs | All, Eastern, Western |
| FAVORITES section | Highlighted cards (blue) with star, only when user has favorites |
| ALL TEAMS section | Remaining teams |
| Team card | Logo (circle + abbreviation), full name, conference + division chips |

#### Team Detail Screen

| Element | Detail |
|---------|--------|
| Back navigation | Arrow in top bar |
| Team header | Banner with team color background, large logo, city + name |
| Team Information | Chips: conference, division, city |
| Roster | Player list: name, position, height, weight |
| FAB | Orange star to add/remove from favorites |

**Data flow:**
- Uses manual DI (pure constructor injection) — no framework needed
- Teams list: ViewModel → Service (direct)
- Team detail: ViewModel → Service (direct)
- Favorite toggle: ViewModel calls platform storage contract

**API endpoints:**
- `GET /v1/teams` (with conference filter)
- `GET /v1/teams/<ID>`
- `GET /v1/players` (filtered by team_ids[] for roster)

---

## 3. Platform Modules

### platform/contracts
Interfaces consumed by feature modules:

```
NetworkClient          — HTTP GET/POST with JSON deserialization
StorageClient          — Key-value local persistence (favorites)
```

### platform/network
Real implementation using OkHttp + kotlinx.serialization.
Base URL: `https://api.balldontlie.io/v1/`

### platform/network-mock
Mock implementation that reads JSON files from assets.
Used by feature showcase apps and App Shell in mock mode.

### platform/storage
Real implementation for local persistence (DataStore or SharedPreferences).
Stores favorite team IDs.

> No mock needed for storage — real DataStore works fine in showcase apps.

---

## 4. Cross-Cutting: Favorites

Favorites is **not a feature module** — it's a platform capability.

- `StorageClient` in platform/contracts exposes read/write for favorite team IDs
- `platform/storage` implements it (DataStore)
- Both feature/scores and feature/teams access favorites via StorageClient
- App Shell provides the StorageClient instance to both features

```
App Shell
  → creates StorageClient
  → passes to ScoresFeature(ScoresFeatureConfig(networkClient, storageClient))
  → passes to TeamsFeature(TeamsFeatureConfig(networkClient, storageClient))
```

---

## 5. App Shell Wiring

### AppConfig
```
AppConfig
  → useMockNetwork: Boolean (default: true for showcase)
  → apiKey: String
  → baseUrl: String
```

### AppContainer
```
AppContainer(config)
  → networkClient = if mock → MockNetworkClient() else OkHttpNetworkClient(...)
  → storageClient = DataStoreStorageClient(context)
  → scoresFeature = ScoresFeature(ScoresFeatureConfig(networkClient, storageClient))
  → teamsFeature = TeamsFeature(TeamsFeatureConfig(networkClient, storageClient))
```

### MainActivity
- Single Activity with Compose
- Bottom navigation: Scores tab, Teams tab
- Navigation handles: scores list, teams list, team detail
- Light/dark mode toggle in top bar (theme state in App Shell)

---

## 6. Navigation

```
Bottom Nav
├── Scores tab → ScoresScreen (from feature/scores)
└── Teams tab  → TeamsListScreen (from feature/teams)
                    └── tap team → TeamDetailScreen (from feature/teams)
```

Cross-feature navigation note: Scores screen shows game cards with teams.
If tapping a team in a game card navigates to team detail, that's
cross-feature navigation. The App Shell handles this via a navigation
callback — feature/scores emits a "navigate to team" event, App Shell
routes it to feature/teams.

---

## 7. Theming

- Light/dark mode toggle icon in top bar
- Theme state owned by App Shell (not a feature module concern)
- Persisted via StorageClient (same platform contract)
- Material 3 with team-colored dynamic theming on team detail screen

---

## 8. Module Dependency Graph (NBA Hub specific)

```
┌────────────────────────────────────────────────────┐
│                    App Shell                        │
│  AppConfig + AppContainer + MainActivity            │
│  Theme toggle + Bottom navigation                   │
└──┬──────────┬───────────┬──────────┬───────────────┘
   │          │           │          │
   ▼          ▼           ▼          ▼
┌───────┐ ┌────────┐ ┌────────┐ ┌─────────┐
│Scores │ │Teams   │ │Network │ │Storage  │
│Feature│ │Feature │ │(Real)  │ │(Real)   │
└───┬───┘ └───┬────┘ └───┬────┘ └────┬────┘
    │         │           │           │
    ▼         ▼           ▼           ▼
┌────────────────────────────────────────────────────┐
│              Platform Contracts                     │
│         NetworkClient + StorageClient               │
└────────────────────────────────────────────────────┘
```

**Internal DI strategy per feature:**
- Scores Feature → Dagger 2 (internal wiring, invisible at module boundary)
- Teams Feature → Manual DI (pure constructor injection)

Both features are consumed identically by the App Shell and SDK —
the DI strategy is an internal implementation detail.

---

## 9. Mock JSON Files

Each feature showcase app includes mock JSON files:

**feature/scores/showcase/src/main/assets/mock/**
- `games_live.json` — games with live status (Q3, Q4)
- `games_final.json` — completed games
- `games_upcoming.json` — scheduled games with no scores

**feature/teams/showcase/src/main/assets/mock/**
- `teams_all.json` — full team list
- `team_detail_lakers.json` — single team detail
- `players_lakers.json` — roster for a team

---

## 10. SDK Module: Embeddable Live Scores

The SDK demonstrates external consumption by exposing **embeddable composable
widgets** that any company can drop into their app.

### What it exposes

```kotlin
// External company usage
val nbaSDK = NbaSDK.initialize(
    NbaSDKConfig(apiKey = "their-key", environment = PROD)
)

// Embed in their Compose UI
@Composable
fun TheirScreen() {
    nbaSDK.LiveScoresWidget()                // all live games
    nbaSDK.TeamScoresWidget(teamId = 5)      // single team's games
}
```

### What it bundles internally

```
sdk/nba-scores-sdk/
├── settings.gradle.kts
├── nba-scores-sdk/
│   ├── build.gradle.kts          ← Maven coordinates for deps
│   └── src/.../
│       ├── NbaSDK.kt             ← Public entry point
│       ├── NbaSDKConfig.kt       ← apiKey, environment
│       └── internal/             ← Hidden from consumer
│           └── SDKContainer.kt   ← Wires network + scores feature
└── showcase/                     ← Demo app showing SDK integration
    └── src/.../
        └── SDKShowcaseApp.kt     ← Simulates an external company's app
```

### SDK internals (hidden from consumer)

```
NbaSDK.initialize(config)
  → SDKContainer creates:
      → OkHttpNetworkClient(apiKey, baseUrl)  ← bundled, consumer never sees
      → ScoresFeature(ScoresFeatureConfig(networkClient, ...))
  → Exposes composable widgets that delegate to ScoresFeature
```

> Note: `dagger-runtime` becomes a transitive dependency of the SDK artifact
> since the SDK bundles feature/scores, which uses Dagger 2 internally.
> This is an implementation detail — the SDK consumer's build pulls it in
> automatically via the Maven artifact.

### Dependency graph

```
┌─────────────────────┐
│  External Company    │
│  App                 │
└──────┬──────────────┘
       │ depends on (Maven artifact)
       ▼
┌─────────────────────┐
│  sdk/nba-scores-sdk  │
│  - NbaSDK            │
│  - NbaSDKConfig      │
│  - LiveScoresWidget  │
└──┬───────────┬──────┘
   │           │  (internal, not exposed)
   ▼           ▼
┌───────┐  ┌────────┐
│Scores │  │Network │
│Feature│  │(Real)  │
└───┬───┘  └───┬────┘
    ▼          ▼
┌─────────────────────┐
│  Platform Contracts   │
└─────────────────────┘
```

The SDK showcase app acts as if it's an external company's app —
it only uses the public SDK API, proving the integration story works.

---

## 11. Implementation Order

1. **Gradle infrastructure** — root project, build-logic, version catalog
2. **platform/contracts** — NetworkClient + StorageClient interfaces
3. **platform/network-mock** — mock implementation reading JSON files
4. **platform/network** — real OkHttp implementation
5. **platform/storage** — DataStore implementation for favorites
6. **feature/teams** — teams list + team detail + roster + favorite toggle
   - Includes showcase app with mocks
7. **feature/scores** — scores list with live/final/upcoming states + favorites section
   - Add Dagger 2 dependencies to build.gradle.kts (dagger-compiler, dagger)
   - Create internal @Component + @Module for feature-internal wiring
   - Includes showcase app with mocks
8. **App Shell** — wires everything, bottom nav, theme toggle, cross-feature navigation
9. **sdk/nba-scores-sdk** — embeddable live scores widget
   - Bundles feature/scores + platform/network
   - SDK showcase app simulating external company integration

---

## 12. Verification

1. `./gradlew assembleDebug` — full project compiles
2. Run feature/scores showcase app — see mock scores with favorites
3. Run feature/teams showcase app — see teams, detail, favorite toggle
4. Run App Shell — both tabs work, navigation between features, theme toggle
5. Toggle `useMockNetwork` in AppConfig — verify swap works
6. Favorite a team — verify it persists and shows in both tabs
7. Run SDK showcase app — verify embeddable widget works in isolation
