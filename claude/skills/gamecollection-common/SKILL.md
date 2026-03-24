---
name: gamecollection-common
description: Common constants, DI modules, and shared utilities for Game Collection. Use when adding API constants, SharedPreferences, connectivity, or app-wide configuration.
---

# Game Collection Common

## Constants

`core/common/.../constants/AndroidConstants.kt`:

- `ANDROID_PREFERENCES` — SharedPreferences name

## ApplicationModule

`core/common/.../module/ApplicationModule.kt` provides:

- `AssetManager`
- `ConnectivityManager`
- `SharedPreferences` (using `ANDROID_PREFERENCES`)

All are `@Singleton` and `@InstallIn(SingletonComponent::class)`.
