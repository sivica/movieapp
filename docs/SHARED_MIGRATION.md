# Shared Kotlin Multiplatform extraction

Path 1 continues: `:shared` now owns TMDB networking, DTO mapping, paging sources, and `MovieRepositoryImpl`. Compose UI is **not** Compose Multiplatform, and there is no iOS UI in this change.

## Inventory

### In `:shared` `commonMain`

| Type | Why it can be shared |
|---|---|
| `Movie`, `MovieDetails`, `SearchResultItem`, `MediaType` | Data classes / enum with no Android types |
| `Result` | Sealed domain result wrapper |
| `MovieRepository` | Interface-only contract. Popular/search still expose `Flow<PagingData<T>>` via `paging-common`. |
| DTOs (`MovieDTO`, `MovieListDTO`, `MovieDetailsDTO`, `GenreDTO`) + `ModelMapper` | kotlinx.serialization models and pure mapping |
| `createTmdbHttpClient` + `TmdbRemoteDataSource` | Ktor Client in `commonMain`. The v3 API key is a constructor argument. |
| `MoviePagingSource`, `SearchPagingSource`, `MovieRepositoryImpl` | `Pager` / `PagingSource` from `paging-common`; they call the Ktor data source |

Packages for domain types are unchanged (`com.example.movieapp.domain.*`). Data types live under `com.example.movieapp.data.*`.

### Android-only (`:app` + `:shared` `androidMain`)

| Area | Why it stays |
|---|---|
| Jetpack Compose screens, theme, navigation, `MainActivity` | Android Compose UI; not being rewritten to Compose Multiplatform |
| `MovieApplication`, Hilt `NetworkModule`, `@HiltViewModel`, `@Inject` use cases | Hilt / Android DI |
| `createAndroidTmdbHttpClient`, `createAndroidMovieRepository` | OkHttp engine + debug BODY logging. The app passes `BuildConfig.TMDB_API_KEY`. |
| ViewModels, Coil, Navigation Compose | AndroidX / Compose |
| Use cases | Tiny wrappers that stay in `:app` because of Hilt `@Inject` |

### API key (not in git)

Same approach as before: `local.properties` `tmdb_api_key`, or env `TMDB_API_KEY`, compiled into `BuildConfig.TMDB_API_KEY`. `:shared` never reads those sources. Hilt builds the repository with:

```kotlin
createAndroidMovieRepository(
    apiKey = BuildConfig.TMDB_API_KEY,
    debugLogging = BuildConfig.DEBUG,
)
```

That Android adapter creates an OkHttp Ktor engine and calls `createTmdbHttpClient(apiKey, engine)`, which attaches `api_key` as a query parameter.

## What this PR does not do

- No Compose Multiplatform / iOS UI.
- No change to list/detail/search Compose behavior.
- TMDB key handling is unchanged (`local.properties` / `TMDB_API_KEY`). Secrets are not committed.

## Gradle tasks

```bash
./gradlew :shared:jvmTest
./gradlew :app:assembleDebug
```

`:shared:jvmTest` runs `commonTest` on the JVM target (mapping + Ktor `MockEngine` repository/paging tests). iOS targets are declared (`iosArm64`, `iosSimulatorArm64`) so an Xcode app can link the framework later; they are not compiled in this Linux CI workflow (`kotlin.native.ignoreDisabledTargets=true`).

## Suggested next step

Move the three use cases into `:shared` (drop Hilt `@Inject` there; keep `@Provides` or constructor calls in `:app`). Abstracting `PagingData` out of the domain contract is optional and can wait until an iOS client needs a non-Paging consumer.
