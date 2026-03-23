---
name: gamecollection-common
description: Common constants, DI modules, and shared utilities for Game Collection. Use when adding API constants, SharedPreferences, connectivity, or app-wide configuration.
---

# Game Collection Common

## Constants

`core/common/.../constants/AndroidConstants.kt`:

- `API_VERSION_1`, `CURRENT_API_VERSION` — API version strings
- `ANDROID_PREFERENCES` — SharedPreferences name
- `API_URL` — Base API URL

## ApplicationModule

`core/common/.../module/ApplicationModule.kt` provides:

- `AssetManager`
- `ConnectivityManager`
- `SharedPreferences` (using `ANDROID_PREFERENCES`)

All are `@Singleton` and `@InstallIn(SingletonComponent::class)`.
