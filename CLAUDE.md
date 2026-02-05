# NBA Hub — Project Instructions

## What This Is

NBA Hub is an NBA scores and teams app demonstrating a modular Android architecture
with SDK distribution. It uses the BallDontLie API, supports favorites, dark mode,
and mock-driven development.

Full specs live in `.claude/`:
- `PLAN.md` — Architecture guidelines (the source of truth)
- `NBA_HUB_PLAN.md` — Product specification
- `DEV_PLAN.md` — Step-by-step build plan (16 commits, 7 phases)

---

## Architecture Rules (non-negotiable)

1. **Feature modules depend on platform/contracts ONLY** — never on platform implementations, never on each other
2. **Boundaries are framework-free** — consumers create features via plain constructors: `XxxFeature(XxxFeatureConfig(...))`
3. **No DI framework leaks across module boundaries** — Dagger, if used, stays `internal` to the feature
4. **App Shell is the composition root** — it creates platform instances and passes them to features via config classes
5. **Each module is an independent Gradle build** — not a subproject, uses `includeBuild()` + `dependencySubstitution` in root `settings.gradle.kts`

---

## Module Structure

```
app/                          ← App Shell (composition root)
feature/<name>/               ← Independent Gradle build (multi-module)
  ├── <name>/                 ← Feature library
  └── showcase/               ← Standalone app with mock deps
platform/contracts/           ← Pure Kotlin interfaces (NetworkClient, StorageClient)
platform/<name>/              ← Implementation (independent Gradle build)
sdk/<name>-sdk/               ← SDK wrapper (independent Gradle build)
  ├── <name>-sdk/             ← SDK library
  └── showcase/               ← Demo app simulating external company
build-logic/convention/       ← Shared convention plugins
```

---

## Convention Plugins

Apply these in `build.gradle.kts` files — do not configure AGP/Compose/Kotlin manually:

| Plugin ID | Use For |
|-----------|---------|
| `nbahub.kotlin.library` | Pure Kotlin modules (platform/contracts) |
| `nbahub.android.library` | Android library modules |
| `nbahub.android.library.compose` | Android library + Compose |
| `nbahub.android.application` | Android app modules |
| `nbahub.android.application.compose` | Android app + Compose |

---

## DI Strategy Per Feature

| Feature | Strategy | Details |
|---------|----------|---------|
| `feature/teams` | Manual DI | Constructor injection, no framework |
| `feature/scores` | Dagger 2 | Internal `@Component` + `@Module`, all Dagger code is `internal` |

Both expose the same public API: `XxxFeature(XxxFeatureConfig(...))`.

---

## Feature Module Pattern

Every feature module follows this structure:

```kotlin
// Public API — framework-free
data class XxxFeatureConfig(
    val networkClient: NetworkClient,
    val storageClient: StorageClient,
)

class XxxFeature(private val config: XxxFeatureConfig) {
    // Internal wiring (manual DI or Dagger — consumer doesn't know)
    @Composable fun XxxScreen() { ... }
}
```

Internal layers: **UI (Screen + ViewModel) → Domain (optional UseCases) → Data (Service/Repository)**
- No unnecessary interfaces — use them only when there's a real reason
- ViewModel → Service is fine for simple features; add UseCases only for complex business logic

---

## Mock Strategy

- Only the network layer has a mock module (`platform/network-mock`)
- `MockNetworkClient(context)` reads JSON from `assets/mock/` — path mapping: `v1/teams` → `mock/v1_teams.json`
- Mock JSON files live in each feature's `showcase/src/main/assets/mock/`
- App Shell also has mock JSON in `app/src/main/assets/mock/` for full-app mock mode
- Storage uses real DataStore everywhere (no mock needed)

---

## Dependencies & Build

- Version catalog: `gradle/libs.versions.toml`
- Dependencies use **Maven coordinates** in build files (e.g., `com.example:feature-teams:1.0.0`)
- `dependencySubstitution` in root `settings.gradle.kts` redirects to local builds
- Verify individual modules: `./gradlew -p <module-path> build`
- Verify full project: `./gradlew assembleDebug`
- Verify showcase apps: `./gradlew -p feature/<name> :showcase:assembleDebug`

---

## Development Workflow

Follow `DEV_PLAN.md` strictly — implement one step at a time:
1. Implement the step
2. Verify it compiles (`./gradlew` command listed in each step)
3. Commit with the exact message from the plan
4. Move to next step

Showcase apps and mock JSON are set up **first** in each feature phase — devs get a
runnable entry point before building screens.

---

## Commit Convention

Use the exact commit messages from `DEV_PLAN.md`. Format: `type: description`
- `chore:` for infrastructure
- `feat:` for features and modules
- `docs:` for documentation
- **Do NOT add `Co-Authored-By` lines to commit messages**

---

## Key Tech Stack

- Kotlin, Jetpack Compose, Material 3
- OkHttp + kotlinx.serialization (networking)
- DataStore Preferences (local storage)
- Dagger 2 (only inside feature/scores, internal)
- Jetpack Navigation Compose (App Shell navigation)
- Coroutines + Flow
