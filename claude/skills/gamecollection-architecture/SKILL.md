---
name: gamecollection-architecture
description: Defines architecture patterns for Game Collection. Use when implementing MVI, MVVM, VIPER, MVP, or base UI components. Use when creating activities, fragments, ViewModels, or structuring feature modules.
---

# Game Collection Architecture

Use the architecture base classes from `core/architecture/src/main/kotlin/com/huhx0015/gamecollection/architecture/` when building features.

## Base Classes (Always Use)

Extend these for all activities, fragments, and ViewModels:

| Class | Path | Usage |
|-------|------|-------|
| `BaseActivity` | `architecture/base/BaseActivity.kt` | Extend `AppCompatActivity`; includes Hilt `@AndroidEntryPoint`, `CompositeDisposable` |
| `BaseFragment` | `architecture/base/BaseFragment.kt` | Extend `Fragment`; includes Hilt `@AndroidEntryPoint` |
| `BaseViewModel` | `architecture/base/BaseViewModel.kt` | Extend `AndroidViewModel`; includes DataBinding `Observable`, `CompositeDisposable`, lifecycle callbacks |

## MVI Architecture

Use `architecture/mvi/` when implementing Model-View-Intent:

| Class | Purpose |
|-------|---------|
| `BaseContract` | Defines `View : BaseView` |
| `BaseView` | `initView()`, `observe()` |
| `BaseViewModel<S, I, E>` | `state: StateFlow<S>`, `events: Flow<E>`, `sendIntent(I)` |
| `BaseState` | State object observed by View via `StateFlow` |
| `BaseIntent` | Intents sent from View → ViewModel |
| `BaseEvent` | Events sent from ViewModel → View |

**Flow:** View sends intents via `sendIntent()` → ViewModel processes in `processIntent()` → ViewModel updates `state` and emits `events` → View observes `state` and handles `events`.
