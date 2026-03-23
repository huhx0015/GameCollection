---
name: gamecollection-index
description: Master index for Game Collection project skills. Use when working in Game Collection to discover available architecture, audio, network, UI, and common patterns.
---

# Game Collection Project Skills

When working in this project, apply the following skills based on context:

| Skill | Use When |
|-------|----------|
| `gamecollection-architecture` | Creating activities, fragments, ViewModels; implementing MVI, MVVM, MVP, VIPER |
| `gamecollection-audio` | Music or sound playback; HXMusic, HXSound |
| `gamecollection-network` | API endpoints, Retrofit, network configuration |
| `gamecollection-common` | Constants, SharedPreferences, app-wide config |
| `gamecollection-ui` | List screens, image loading, dialogs, snackbars, display utils |
| `gamecollection-location` | Location services, LocationManager, LocationRequest |
| `gamecollection-localization` | Adding/changing strings.xml; propagating translations to locale directories |

## Module Structure

- `core/architecture` — MVI, MVVM, MVP, VIPER, base classes, DI
- `core/audio` — HX Audio Player (hxaudio-v331.aar)
- `core/network` — Retrofit, OkHttp, Gson
- `core/ui` — ApiRecyclerView, BindingUtils, DialogUtils, SnackbarUtils, DisplayUtils, UnitUtils
- `core/common` — AndroidConstants, ApplicationModule
- `core/location` — LocationModule
- `core/database` — (reserved)
