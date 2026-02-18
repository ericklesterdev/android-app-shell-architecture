# NBA Hub

A reference Android project demonstrating how to structure a large-scale, multi-module codebase where every feature is an independent Gradle build. The app itself is an NBA scores and teams viewer, but the real focus is the architecture: strict module boundaries, framework-free public APIs, composite builds with dependency substitution, and packaging a feature into a redistributable SDK that external companies can embed without knowing anything about the internals.

## Demo

| Light Mode | Dark Mode |
|:----------:|:---------:|
| <video src="https://github.com/user-attachments/assets/f66557d1-2cbd-40b8-8a4e-2013f9ad738a" width="200"/> | <video src="https://github.com/user-attachments/assets/37c5128f-3c4b-4846-96b0-7b293ab0b42a" width="200"/> |

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        App Shell                            │
│              (composition root, navigation)                  │
├──────────────────────┬──────────────────────────────────────┤
│   feature/teams      │          feature/scores              │
│   (Manual DI)        │          (Dagger 2)                  │
├──────────┬───────────┴──────────┬───────────────────────────┤
│ platform │     platform         │      platform             │
│ /design  │     /network         │      /storage             │
│ (Theme)  │ (NetworkClient)      │  (StorageClient)          │
└──────────┴──────────────────────┴───────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                   sdk/nba-scores-sdk                         │
│         (redistributable SDK wrapping Scores)                │
│     NbaSDK.initialize() → LiveScoresWidget()                 │
└─────────────────────────────────────────────────────────────┘
```

### Module Structure

```
app/                          ← App Shell (composition root)
feature/teams/                ← Independent Gradle build
  ├── teams/                  ← Feature library (Manual DI)
  └── showcase/               ← Standalone app with mock deps
feature/scores/               ← Independent Gradle build
  ├── scores/                 ← Feature library (Dagger 2)
  └── showcase/               ← Standalone app with mock deps
platform/network/             ← NetworkClient + OkHttp + Mock
platform/storage/             ← StorageClient + DataStore
platform/design/              ← Shared NbaHubTheme, colors, typography
sdk/nba-scores-sdk/           ← Independent Gradle build
  ├── nba-scores-sdk/         ← SDK library
  └── showcase/               ← Demo app simulating external company
build-logic/convention/       ← Shared convention plugins
```

Every module is an **independent Gradle build** connected via `includeBuild()` and `dependencySubstitution`. This means each module can be built, tested, and developed in isolation. Note that `includeBuild()` with `dependencySubstitution` is used here for the **development phase only** — in a production setup, modules would be connected via **published Maven artifacts** (e.g., from a private artifact repository), removing the need for composite builds entirely. This architecture works equally well in a **monorepo** (all modules in one repository, as shown here) or a **multi-repo** setup (each module in its own repository, consuming dependencies as published artifacts).

### Key Design Decisions

**Feature modules depend on platform interfaces only** — never on each other. The App Shell is the composition root that wires everything together.

**Boundaries are framework-free** — each feature exposes a single public `@Composable` function and a dependencies data class. Everything else is `internal`.

```kotlin
// Public API — only these symbols are visible outside the module
data class TeamsFeatureDependencies(
    val networkClient: NetworkClient,
    val storageClient: StorageClient,
)

@Composable
fun TeamsScreen(
    dependencies: TeamsFeatureDependencies,
    modifier: Modifier = Modifier,
)
```

**No DI framework leaks across boundaries** — Dagger stays `internal` to `feature/scores`. External consumers don't know or care which DI strategy a feature uses.

## DI Strategy

Two DI approaches are demonstrated side-by-side to show that both work behind the same public API:

| Feature | Strategy | How It Works |
|---------|----------|--------------|
| `feature/teams` | **Manual DI** | `CompositionLocalProvider` propagates dependencies; services created in ViewModel factories |
| `feature/scores` | **Dagger 2** | Internal `@Component` + `@Module`; `DaggerScoresComponent` creates the ViewModel factory |

Both expose the identical public surface: `XxxScreen(dependencies, modifier)`. The App Shell calls them the same way — it doesn't know which uses Dagger and which doesn't.

## Tech Stack

- Kotlin, Jetpack Compose, Material 3
- OkHttp + kotlinx-serialization
- DataStore Preferences
- Dagger 2 (internal to `feature/scores`)
- Navigation Compose
- Coroutines + Flow

## SDK Usage

The NBA Scores SDK wraps the Scores feature into an embeddable widget for external apps:

```kotlin
// 1. Initialize once (e.g., in Application.onCreate or Activity.onCreate)
NbaSDK.initialize(
    NbaSDKConfig(
        context = applicationContext,
        apiKey = "your-api-key",
        environment = NbaEnvironment.PRODUCTION,
    )
)

// 2. Drop the widget into any Compose layout
@Composable
fun YourScreen() {
    LiveScoresWidget(modifier = Modifier.fillMaxSize())
}
```

The SDK handles theming, networking, and state management internally. The consumer only needs to initialize and place the widget.

## Mock Strategy

All showcase apps and the App Shell (in default mode) use `MockNetworkClient`, which reads JSON from `assets/mock/`. Path mapping: API path `v1/teams` maps to file `mock/v1_teams.json`.

This enables:
- Development without an API key
- Deterministic UI for screenshots and demos
- Offline-first showcase apps

## License

This project is for demonstration purposes.
