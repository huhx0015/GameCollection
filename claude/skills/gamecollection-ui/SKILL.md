---
name: gamecollection-ui
description: UI components and utilities for Game Collection. Use when building list screens, image loading, dialogs, snackbars, or display/unit conversions.
---

# Game Collection UI

## SnackbarUtils

`core/ui/.../utils/SnackbarUtils.kt`:
- `displaySnackbar(parentView, message, length, color)`
- `displaySnackbarWithAction(parentView, message, length, color, actionText, listener)`

## DialogUtils

`core/ui/.../utils/DialogUtils.kt`:
- `displayAlertDialog(context, title, message)` — Simple alert
- `createProgressDialog(context)` — Progress dialog with `dialog_progress` layout

## DisplayUtils

`core/ui/.../utils/DisplayUtils.kt`:
- `getOrientation(context)` — Returns `Configuration.ORIENTATION_PORTRAIT` or `ORIENTATION_LANDSCAPE`

## UnitUtils

`core/ui/.../utils/UnitUtils.kt`:
- `convertDpToPixels(context, dp)` — dp → px
- `convertPixelsToDp(context, px)` — px → dp
