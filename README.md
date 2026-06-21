# Curate

Curate is an Android wallpaper discovery app built with Kotlin, Jetpack Compose, MVVM, Hilt, Paging 3, Coil, Ktor, Supabase, and Material 3 Expressive.

The app currently covers wallpaper discovery, detail viewing, user authentication, and favorites:

- Unsplash-powered wallpaper discovery.
- Infinite scrolling with Paging 3.
- Staggered image grid with subtle expressive item animations on fresh open and downward scroll.
- Coil image loading and near-viewport image prefetching.
- BlurHash placeholder decoding so every image loads into a color-accurate blur before the preview arrives.
- Shared element transitions from grid images into the detail screen.
- Detail image loading that uses the cached preview image while the full image loads.
- Dynamic button contrast on the detail screen via the Palette API — icon tints adapt to the wallpaper's dominant colors.
- Compose cinematic splash screen with bundled artwork and a timed transition into the app.
- Email/password sign-in and sign-up backed by Supabase Auth, with session refresh and sign-out.
- Auth state machine (`Loading → Authenticated / Unauthenticated / Error / ConfigUnavailable`) propagated app-wide via `AuthSessionViewModel`.
- Authenticated users can add or remove favorites from the wallpaper detail screen.
- Favorites are persisted in Room, observed reactively, and mirrored to Supabase on add/remove.
- The auth-gated Library displays favorites in a two-column staggered grid with empty and signed-out states.
- Selecting a Library favorite opens its detail screen using the same shared-element transition as the feed and search grids.
- Reusable presentation components for loading, empty/error states, wallpaper cards, search, auth top bar, and detail chrome.

## Tech Stack

| Area | Technology |
| --- | --- |
| UI | Jetpack Compose, Material 3 Expressive |
| Architecture | MVVM with domain/data/presentation layers |
| Dependency injection | Hilt |
| Networking | Ktor with OkHttp |
| Images | Coil 3, BlurHash, AndroidX Palette |
| Pagination | Paging 3 |
| Local persistence | Room |
| Backend services | Unsplash API, Supabase Kotlin (Auth and favorite mutations) |
| Splash screen | Jetpack Compose |
| Logging | Timber |

## Requirements

- Android Studio with Android Gradle Plugin 9.x support.
- Android SDK Platform 37 installed.
- JDK compatible with the project Gradle setup.
- Unsplash access key.
- Supabase URL and anon key if Supabase-backed features are enabled.

Current Android config:

| Setting | Value |
| --- | --- |
| `compileSdk` | 37 |
| `targetSdk` | 36 |
| `minSdk` | 29 |
| `applicationId` | `com.example.curate` |

## Local Configuration

Create or update `local.properties` with environment-specific values. This file is local-only and should not be committed.

```properties
DEV_BASE_URL=https://your-dev-supabase-url.supabase.co
STAGING_BASE_URL=https://your-staging-supabase-url.supabase.co
PRODUCTION_BASE_URL=https://your-production-supabase-url.supabase.co

SUPABASE_ANON_KEY=your_supabase_anon_key
UNSPLASH_ACCESS_KEY=your_unsplash_access_key
```

The app maps these into `BuildConfig`:

| BuildConfig field | Source |
| --- | --- |
| `BASE_URL` | Environment flavor base URL |
| `SUPABASE_URL` | Environment flavor base URL |
| `SUPABASE_ANON_KEY` | `SUPABASE_ANON_KEY` |
| `UNSPLASH_ACCESS_KEY` | `UNSPLASH_ACCESS_KEY` |
| `LOGS_ENABLED` | Enabled for debug builds, disabled for release builds |

Do not put Unsplash secret keys in the Android app. Client APKs can be inspected, so only public/client-safe keys should be exposed.

## Build Variants

Build types control how the app is built. Product flavors control which environment the app uses.

| Build Type | Purpose | Logs | Minification |
| --- | --- | --- | --- |
| `debug` | Local/debuggable builds | Enabled | Disabled |
| `release` | Release builds | Disabled | Enabled |

| Environment Flavor | Purpose | Base application ID suffix |
| --- | --- | --- |
| `dev` | Local development services | `.dev` |
| `staging` | Staging services | `.staging` |
| `production` | Production services | none |

Common commands:

```powershell
.\gradlew.bat :app:assembleDevDebug
.\gradlew.bat :app:assembleStagingDebug
.\gradlew.bat :app:assembleProductionRelease
.\gradlew.bat :app:testDevDebugUnitTest
```

## Project Structure

```text
app/src/main/java/com/example/curate/
|-- CurateApplication.kt
|-- core/
|   `-- config/
|-- data/
|   |-- local/
|   |-- paging/
|   |-- remote/
|   |   |-- supabase/
|   |   `-- unsplash/
|   `-- repository/
|-- di/
|-- domain/
|   |-- model/
|   |-- repository/
|   `-- usecase/
|-- presentation/
|   |-- auth/
|   |   |-- signin/
|   |   |-- signup/
|   |   `-- account/
|   |-- components/
|   |-- detail/
|   |-- discover/
|   |-- home/
|   |-- library/
|   |-- main/
|   |-- search/
|   `-- navigation/
`-- ui/
    `-- theme/
```

## Architecture

Curate follows MVVM with clear layer boundaries.

### Presentation

`presentation/` contains Compose screens, ViewModels, navigation, UI models, and reusable UI components.

ViewModels:

- Own UI state and user intent functions.
- Depend on domain use cases.
- Do not own `Context`, Coil `ImageLoader`, scroll state, or Compose-specific behavior.
- May own presentation policy state, such as whether newly visible grid items should animate and which wallpaper IDs have already completed their one-shot entrance animation.

Composables:

- Render state and call callbacks.
- Own visual rendering mechanics such as shared element transitions, staggered item transforms, and Coil image requests.
- Report UI events, such as scroll direction and completed item entrance animations, back to the relevant ViewModel.
- May do presentation-only image prefetching when it is tied to what the UI is about to display.

### Domain

`domain/` contains app models, repository interfaces, and use cases. It should stay independent of Android framework, Compose, Supabase, and Unsplash DTOs.

### Data

`data/` contains repository implementations, remote clients, paging sources, DTOs, and mappers. DTOs should not leak into `domain` or `presentation`.

Favorites use the local Room table as the reactive UI source. `WallpaperRepositoryImpl.observeFavorites()` maps DAO entities into domain models, while favorite mutations update Room and make a best-effort Supabase upsert or delete for the authenticated user.

The Library data path is:

```text
FavoriteWallpaperDao
    -> WallpaperRepository.observeFavorites()
    -> ObserveFavoriteWallpapersUseCase
    -> LibraryViewModel.uiState
    -> LibraryRoute
    -> LibraryScreen
```

### DI

`di/` contains Hilt modules for network clients, Supabase, and repository bindings.

## Current UI Notes

- The home screen uses a two-column `LazyVerticalStaggeredGrid`.
- Grid items animate in through a reusable `StaggeredGridItem`.
- The initial feed animates on fresh app open.
- New grid items animate only while scrolling down.
- Items do not reanimate after their wallpaper ID has been recorded in `HomeUiState.animatedGridItemIds`.
- Visible and near-future wallpaper previews are prefetched with `WallpaperImagePrefetcher`.
- Every wallpaper image decodes its BlurHash via `BlurHashDecoder` and shows it as a placeholder before the Coil request completes.
- Shared element transitions use stable keys: `wallpaper-image-${id}`.
- The detail screen loads `fullUrl` while using `previewUrl` as the memory-cache placeholder.
- The detail screen uses `WallpaperButtonContrastAnalyzer` (Palette API) to tint action icons so they remain legible against the loaded wallpaper.
- The splash screen exits only after the home feed's initial refresh state is known, preventing a blank-screen flash.
- The bottom navigation uses icon brightness for selection, with no selected-item background indicator.
- Auth screens share a reusable `AuthTopBar` component.
- `AuthSessionViewModel` is scoped to the activity and drives auth-gated routes throughout the nav graph.
- `LibraryViewModel` maps observed domain favorites into `LibraryUiState` and presentation-ready `WallpaperUiModel` values.
- The Library renders an empty message until the authenticated user has favorites, then displays them in a two-column staggered grid.
- Library cards reuse `WallpaperCard` and navigate to wallpaper details with the stable `wallpaper-image-${id}` shared-element key.

## Logging

Use Timber instead of Android `Log`.

```kotlin
Timber.d("Loaded %s wallpapers", wallpapers.size)
Timber.e(error, "Unable to load wallpapers")
```

Production behavior:

- `BuildConfig.LOGS_ENABLED` is `false`.
- `Timber.DebugTree()` is not planted.
- R8 strips Timber logging calls.

## Planned Features

Likely future areas:

- Search and filter controls backed by Unsplash search parameters.
- User-scoped local favorite storage and full cross-device favorite hydration from Supabase.
- Collections synced with Supabase.
- Wallpaper apply/download flows with Unsplash download tracking.
- Discover screen content — currently a placeholder.
- Additional top-level screens using the shared animation and component framework.

## References

- [Unsplash API Documentation](https://unsplash.com/documentation)
- [Supabase Kotlin Documentation](https://supabase.com/docs/reference/kotlin)
- [Android WallpaperManager](https://developer.android.com/reference/android/app/WallpaperManager)
