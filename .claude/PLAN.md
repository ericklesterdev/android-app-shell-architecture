# Android App Shell Architecture — Guidelines

A reusable architecture guideline for structuring large-scale Android applications
with modular architecture and SDK distribution.

---

## 1. Architecture

### Dependency Graph

```
┌──────────────────────────────────────────────────────┐
│                      App Shell                        │
│  (Composition Root — owns platform instances,         │
│   wires feature modules, swap config)                 │
└───┬──────────┬──────────┬───────────────┬────────────┘
    │ creates  │ creates  │ passes deps   │ passes deps
    ▼          ▼          ▼               ▼
┌────────┐ ┌────────┐ ┌─────────────┐ ┌─────────────┐
│Platform│ │Platform│ │Feature      │ │Feature      │
│Module A│ │Module B│ │Module A     │ │Module B     │
└───┬────┘ └───┬────┘ └─────────────┘ └─────────────┘
    │ impl     │ impl        │               │
    ▼          ▼             │ depends on     │ depends on
┌────────────────────────────▼───────────────▼──────────┐
│                  Platform Contracts                     │
│           (interfaces only — pure Kotlin)               │
└────────────────────────────────────────────────────────┘
```

### Key Rules

- **App Shell** → depends on Platform Modules + Feature Modules + Platform Contracts
- **Feature Modules** → depend on Platform Contracts ONLY
- **Feature Modules** → do NOT depend on each other
- **Platform Modules** → implement Platform Contracts
- **Boundaries stay framework-free** — features may use DI internally (see Section 2)

---

## 2. Dependency Injection Strategy

### Boundary Rule

The boundary between consumers (App Shell, SDK) and feature modules is always
**framework-free**. Consumers create features via plain constructors:

```
scoresFeature = ScoresFeature(ScoresFeatureConfig(networkClient, storageClient))
```

No DI framework leaks across this boundary. This keeps feature modules portable —
any app (internal or third-party via SDK) can consume them without adopting a
specific framework.

### Inside Feature Modules

Feature modules choose their own internal DI strategy:

| Strategy | When to use | Example |
|----------|-------------|---------|
| Manual DI (default) | Simple features with few internal dependencies | `XxxFeature` creates services and viewmodels via constructors |
| Dagger 2 (opt-in) | Features with complex internal graphs that benefit from compile-time injection | Feature entry point creates a `@Component`, passes platform contracts as external deps |

Both strategies produce the same public API — `XxxFeature(XxxFeatureConfig(...))`.
The consumer never knows which one a feature uses.

### Why Boundaries Stay Framework-Free

| Concern | Framework-free boundary | DI framework at boundary |
|---------|------------------------|--------------------------|
| Feature module portability | Any app can consume — no framework required | Consumer forced to adopt the same framework |
| Multi-repo friction | Zero — just constructors | Every repo needs the framework |
| Swap implementations | Config flag in App Shell | @Binds / module config |
| External SDK story | Clean — SDK wires constructors | SDK drags framework as transitive dep |

### Dagger 2 Inside a Feature (when opted in)

The feature entry point creates a Dagger `@Component` and passes platform contracts
from the config as external dependencies:

```kotlin
class XxxFeature(private val config: XxxFeatureConfig) {
    private val component = DaggerXxxComponent.builder()
        .platformModule(PlatformModule(config.networkClient, config.storageClient))
        .build()

    @Composable fun XxxScreen() {
        val viewModel = component.viewModel()
        // ...
    }
}
```

The `@Component`, `@Module`, and all Dagger-generated code are `internal` to the
feature module — nothing leaks to the consumer.

---

## 3. Internal Consumption (App Shell)

The App Shell acts as the **composition root**. It:

1. Creates platform instances (network, database, analytics, etc.)
2. Passes those instances into feature modules via config classes
3. Allows swapping implementations via `AppConfig`

```
AppConfig (swap config)
  → useMockNetwork: Boolean
  → useMockDatabase: Boolean
  → ...project-specific flags

AppContainer (composition root)
  → creates platform instances based on AppConfig
  → creates FeatureA(FeatureAConfig(networkClient, ...))
  → creates FeatureB(FeatureBConfig(networkClient, storageClient, ...))
```

Feature modules accept dependencies but don't know where they come from.

---

## 4. External Consumption (SDK Module)

When providing features to third party applications, an **SDK module** wraps the feature
modules into a simple facade. The third party application never touches networking, database,
or internal architecture.

```
INTERNAL                                EXTERNAL
┌────────────────────┐                 ┌────────────────────┐
│    App Shell        │                 │    SDK Module       │
│  (full control)     │                 │  (simple facade)    │
│                     │                 │                     │
│ - Swap impls        │                 │ - Accepts creds     │
│ - Custom config     │                 │ - Accepts env       │
│ - Build variants    │                 │ - Wires everything  │
└────────┬────────────┘                 └────────┬────────────┘
         │                                       │
         ▼                                       ▼
   Feature Modules                         Feature Modules
   (same code, no changes)                 (same code, no changes)
```

The SDK module:
- Depends on feature modules + platform modules
- Creates platform instances internally — third party application never sees them
- Exposes a minimal public API (composable widgets, functions, etc.)
- External company just provides credentials/environment config

```kotlin
val sdk = SomeSDK.initialize(
    SDKConfig(apiKey = "their-key", environment = PROD)
)
// Use exposed API — composable widgets, functions, etc.
```

---

## 5. Feature Module Internals (Clean Architecture)

Each feature module follows Clean Architecture: **UI → Domain → Data**,
**without unnecessary abstractions**.

```
┌──────────────────────────────────────────────────────┐
│  Feature Module                                       │
│                                                        │
│  XxxFeature.kt ← entry point + config class           │
│                                                        │
│  ┌──────────────────────────────────────────────┐     │
│  │ UI Layer                                      │     │
│  │  Screen (Composable) + ViewModel              │     │
│  └────┬─────────────────────────────────────────┘     │
│       │ direct reference (no interface)                │
│  ┌────▼─────────────────────────────────────────┐     │
│  │ Domain Layer (optional)                       │     │
│  │  Use Cases — only when business logic is      │     │
│  │  complex enough to warrant it                 │     │
│  └────┬─────────────────────────────────────────┘     │
│       │ direct reference (no interface)                │
│  ┌────▼─────────────────────────────────────────┐     │
│  │ Data Layer                                    │     │
│  │  Service — talks to platform contracts        │     │
│  │  Repository (optional) — caching, local DB    │     │
│  └──────────────────────────────────────────────┘     │
│                                                        │
│  Simple feature:  ViewModel → Service (direct)        │
│  Complex feature: ViewModel → UseCase → Repo/Service  │
└──────────────────────────────────────────────────────┘
```

**Interfaces are not banned — they're just not the default.**
Use them when there's a real reason (e.g., multiple implementations of a
domain component, swappable data sources). Don't add them as boilerplate
when a concrete class does the job.

### Feature Entry Point Pattern

Every feature module exposes an entry point class + config data class:

```kotlin
data class XxxFeatureConfig(
    val networkClient: NetworkClient,
    // ...other platform contracts this feature needs
)

class XxxFeature(private val config: XxxFeatureConfig) {
    // Builds internal dependency graph
    private val service = XxxService(config.networkClient)
    private val viewModel = XxxViewModel(service)

    // Exposes what the App Shell / SDK needs
    @Composable fun XxxScreen() { ... }
}
```

> This shows the manual DI approach (default). For features that opt into Dagger 2,
> see [Section 2 — Dependency Injection Strategy](#2-dependency-injection-strategy).

### Showcase App (per feature module)

Each feature module contains its own **showcase app** — a minimal Android
application that serves as the entry point for feature developers.

```
feature/<name>/
├── settings.gradle.kts
├── <name>/                       ← Feature library module
│   ├── build.gradle.kts
│   └── src/...
└── showcase/                     ← Showcase app (Android Application)
    ├── build.gradle.kts
    ├── src/main/kotlin/.../
    │   └── ShowcaseApp.kt        ← Wires mock deps, launches feature
    └── src/main/assets/
        └── mock/                 ← Mock JSON response files
```

**Purpose:**
- Feature devs run the showcase app directly — no need to build the full App Shell
- The showcase app wires the feature's dependencies using the **mock service layer**
- Mock service layer reads from JSON files instead of making real API calls
- Feature devs can develop the entire feature without a working backend

---

## 6. Monorepo Strategy (Composite Builds + Dependency Substitution)

Each module is an **independent Gradle build** — not a subproject of one big project.
This mirrors production where each module is its own repo.

### How it works

**App Shell and SDK module** reference dependencies via **Maven coordinates**
(exactly as they would in production). During local development,
`dependencySubstitution` redirects them to local builds.

```kotlin
// root settings.gradle.kts
includeBuild("feature/<name>") {
    dependencySubstitution {
        substitute(module("com.example:feature-<name>")).using(project(":"))
    }
}
includeBuild("platform/<name>") {
    dependencySubstitution {
        substitute(module("com.example:platform-<name>")).using(project(":"))
    }
}
```

```kotlin
// app/build.gradle.kts — Maven coordinates, same as production
dependencies {
    implementation("com.example:feature-<name>:1.0.0")
    implementation("com.example:platform-<name>:1.0.0")
}
```

### To go multi-repo

Remove `includeBuild()` blocks. Maven coordinates resolve from remote repository.
**Zero changes** to app/ or sdk/ build files.

### Structure

```
<project-root>/
├── app/                              ← App Shell (root project module)
│   └── build.gradle.kts              Uses Maven coordinates for deps
│
├── feature/
│   └── <name>/                       ← Independent Gradle build (multi-module)
│       ├── settings.gradle.kts
│       ├── <name>/                   ← Feature library
│       │   ├── build.gradle.kts
│       │   └── src/...
│       └── showcase/                 ← Showcase app (uses mock service layer)
│           ├── build.gradle.kts
│           └── src/...
│
├── platform/
│   ├── contracts/                    ← Independent Gradle build (pure Kotlin)
│   │   ├── settings.gradle.kts
│   │   └── build.gradle.kts
│   ├── <name>/                       ← Independent Gradle build (real impl)
│   │   ├── settings.gradle.kts
│   │   └── build.gradle.kts
│   └── <name>-mock/                  ← Mock impl (only when needed)
│       ├── settings.gradle.kts
│       └── build.gradle.kts
│
├── sdk/
│   └── <name>-sdk/                   ← Independent Gradle build
│       ├── settings.gradle.kts
│       ├── <name>-sdk/               ← SDK library
│       │   └── build.gradle.kts      Uses Maven coordinates for deps
│       └── showcase/                 ← Demo app simulating third party application
│           └── build.gradle.kts
│
├── build-logic/
│   └── convention/                   ← Independent Gradle build
│       ├── settings.gradle.kts       (shared convention plugins)
│       └── build.gradle.kts
│
├── gradle/
│   └── wrapper/
├── settings.gradle.kts               ← Root: includeBuild() + dependencySubstitution
├── build.gradle.kts
└── gradle.properties
```

Each `feature/`, `platform/`, `sdk/` directory is a standalone Gradle project.
They can be extracted to their own repo with zero structural changes.

---

## 7. Mock Strategy

**Not all platform modules need mock implementations.**

Only the **network/service layer** has a mock module (`platform/network-mock`).
It reads JSON files as API responses, enabling feature development without a backend.

- Mock JSON files live in each feature's `showcase/src/main/assets/mock/`
- The mock NetworkClient reads these files and returns them as responses
- Feature showcase apps wire the mock NetworkClient automatically
- The App Shell can also use mock via `AppConfig` for full-app mock mode

Other platform modules (database, analytics, etc.) get mocks only if there's
a specific need — not as a blanket pattern.

---

## 8. Skills & Agents

### Skill: Feature Module Scaffolder
**Trigger:** When a new feature module needs to be created.

**What it does:**
- Creates `feature/<name>/` as an independent Gradle build (multi-module)
- Generates feature library submodule: FeatureConfig, Service, ViewModel, Screen
- Generates showcase app submodule: wires mock service layer, launches feature
- Includes mock JSON files directory in showcase
- Adds `includeBuild()` + `dependencySubstitution` to root settings.gradle.kts
- Outputs wiring instructions for AppContainer

### Skill: Platform Module Scaffolder
**Trigger:** When a new platform capability is needed.

**What it does:**
- Adds interface to `platform/contracts/`
- Creates `platform/<name>/` as independent Gradle build with real implementation
- Mock module only created when explicitly needed (not by default)
- Adds `includeBuild()` + `dependencySubstitution` to root settings.gradle.kts
- Outputs wiring instructions for AppConfig + AppContainer

### Agent: SDK Module Generator
**Trigger:** When bundling features for third party application consumption.

**What it does:**
- Creates `sdk/<name>-sdk/` as independent Gradle build
- Accepts a list of feature modules to bundle
- Generates the facade class with initialize() + public API
- Wires platform dependencies internally (Maven coordinates)
- Generates the SDKConfig data class (creds, environment, etc.)
