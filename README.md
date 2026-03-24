# GameCollection

## Description

**GameCollection** is an Android app that **leverages the [IGDB API](https://api-docs.igdb.com/) for game metadata** and the **[Twitch API](https://dev.twitch.tv/docs/api/) for authentication** (client-credentials access tokens required by IGDB). Users browse the [IGDB](https://www.igdb.com/) catalog: they pick a platform, explore a filterable list, open rich game details, and save titles to a personal collection stored locally with Room.

## Instructions

To compile this project, add your Twitch developer **Client ID** and **Client Secret** (the same credentials you use for IGDB API access) to a file named `keys.properties` at the **root** of the repository:

```properties
# Twitch / IGDB API credentials
twitch.client.id=<INSERT_CLIENT_ID>
twitch.client.secret=<INSERT_CLIENT_SECRET>
```

Alternatively, you can define the same keys in `local.properties`; the build prefers `keys.properties` when both are present.

Do not commit real secrets—keep `keys.properties` out of version control (it should remain git-ignored).

## Background

The app was built with [Cursor](https://cursor.com/) using agentic AI workflows (Composer 2 Fast in Plan mode). The goal was to show that a small, production-style Android app—modularized, test-aware, and aligned with common architecture guidance—can be delivered through AI-assisted development without cutting corners on structure or verification.

## Foundation

The project uses **[AndroidBooster](https://github.com/huhx0015/AndroidBooster)** as a starting point for modular Gradle layout, shared patterns, and conventions. From that base, GameCollection focuses on **Clean Architecture** (domain / data / presentation), **MVI-style** contracts where appropriate, **Hilt** for dependency injection, and **Jetpack Compose** for UI. Claude skills shipped with AndroidBooster were used as additional context while generating and refining code.

## Architecture (High Level)

| Layer | Role |
|--------|------|
| **app** | Compose UI, navigation, ViewModels, Hilt entry points |
| **core:domain** | Entities, repository interfaces, use cases (pure Kotlin) |
| **core:data** | IGDB + Twitch auth, Retrofit/Moshi, repository implementations, paging |
| **core:database** | Room database and DAOs for owned games |
| **core:architecture** | Shared MVI base types (intents, state, events) |
| **core:common** | Cross-cutting Android bindings (e.g. system services) via Hilt |

API credentials are wired at compile time; see **Instructions** above.

## Resources — dependencies in use

Listed below are the libraries this app **actually builds on** (runtime behavior, `core:*` modules, and **current** unit tests). See `gradle/libs.versions.toml` and each module’s `build.gradle.kts` for the full Gradle graph.

The `app` module still declares some **unused** artifacts (for example WorkManager, Espresso, Robolectric, Mockito, Arch Core testing, and AndroidX Test runner/rules) that are not referenced by source today; they are omitted here.

### Android Jetpack

- **Core KTX** — Kotlin extensions for Android APIs  
- **Material Components** — Material widgets alongside Compose  
- **Lifecycle** — `lifecycle-runtime-ktx`, `lifecycle-viewmodel-ktx`, `lifecycle-common`, `lifecycle-viewmodel-compose`  
- **Navigation** — `navigation-compose`  
- **Paging** — `paging-runtime-ktx`, `paging-compose` (IGDB game list)

### Compose

- **Compose BOM** — Aligned Compose versions  
- **Material 3**  
- **Activity Compose** — `ComponentActivity` + `setContent`  
- **Tooling** — `ui-tooling`, `ui-tooling-preview`  
- **Material Icons Extended**

### Dependency injection

- **Hilt** — `hilt-android` with **KSP** (`hilt-android-compiler`, `hilt-compiler`)  
- **Hilt Navigation Compose**

### Images

- **Coil 3** — `coil`, `coil-compose`, `coil-svg`, `coil-network-okhttp`

### Logging

- **Timber** — Debug vs release logging

### Network (`core:data`)

- **OkHttp** — Client BOM, core, logging interceptor  
- **Retrofit** — IGDB and Twitch token API  
- **Moshi** — JSON (`moshi`, `moshi-kotlin`, Retrofit Moshi converter)

### Persistence (`core:database`)

- **Room** — `room-runtime`, `room-ktx`, KSP `room-compiler`; `room-testing` for database unit tests

### Coroutines

- **Kotlinx Coroutines** — `kotlinx-coroutines-android` (Android modules), `kotlinx-coroutines-core` (domain), `kotlinx-coroutines-test` (tests)

### Other

- **javax.inject** — Annotations in the domain module  
- **LeakCanary** — Debug-only leak detection

### Testing (in use today)

- **JUnit 4**  
- **Kotlinx Coroutines Test** — `runTest`, test dispatchers  
- **Turbine** — `Flow` assertions in domain tests  

---

## License

See [LICENSE](LICENSE) (Apache License 2.0).
