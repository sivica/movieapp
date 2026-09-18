# Shared Kotlin Multiplatform extraction

This is the first slice of a KMP split: a `:shared` module with Android, JVM, and iOS targets. Compose UI is **not** Compose Multiplatform, and there is no iOS UI in this change.

## Inventory

### Moved into `:shared` `commonMain` (pure Kotlin domain types, plus the existing repository contract)

| Type | Why it can be shared |
|---|---|
| `Movie`, `MovieDetails`, `SearchResultItem`, `MediaType` | Data classes / enum with no Android types |
| `Result` | Sealed domain result wrapper |
| `MovieRepository` | Interface-only contract. It still exposes `Flow<PagingData<T>>` for popular/search; `paging-common` 3.3.6 is already a Kotlin Multiplatform artifact, so the **shape** of the contract can live in `commonMain` without rewriting paging. |

Packages are unchanged (`com.example.movieapp.domain.*`), so `:app` keeps compiling with the same imports.

### Stays in `:app` (Android-only)

| Area | Why it stays |
|---|---|
| Jetpack Compose screens, theme, navigation, `MainActivity` | Android Compose UI; not being rewritten to Compose Multiplatform |
| `MovieApplication`, Hilt modules, `@HiltViewModel`, `@Inject` use cases | Hilt / Android DI |
| Retrofit `TmdbApiService`, OkHttp, `NetworkModule`, `BuildConfig.TMDB_API_KEY` | Android networking and local.properties / `TMDB_API_KEY` handling |
| `MovieRepositoryImpl`, `MoviePagingSource`, `SearchPagingSource` | Use AndroidX Paging **runtime** (`Pager`, `PagingSource`) and Retrofit |
| DTOs + `ModelMapper` | kotlinx.serialization models tied to the Retrofit layer; move with Ktor later |
| ViewModels, Coil, Navigation Compose | AndroidX / Compose |

### Near-term shared candidates (not moved)

- **Use cases** (`GetPopularMoviesUseCase`, `SearchMediaUseCase`, `GetMovieDetailsUseCase`): tiny wrappers. They stay in `:app` because of Hilt `@Inject` and `PagingData`.
- **DTOs and mapper**: pure Kotlin besides Retrofit URL constants. Natural follow-up once networking is Ktor.

## What this PR does not do

- No Compose Multiplatform / iOS UI.
- No Ktor move; Retrofit and Hilt remain on Android.
- TMDB key handling is unchanged (`local.properties` / `TMDB_API_KEY`). Secrets are not committed.

## Gradle tasks

```bash
./gradlew :shared:jvmTest
./gradlew :app:assembleDebug
```

`:shared:jvmTest` runs `commonTest` on the JVM target. iOS targets are declared (`iosArm64`, `iosSimulatorArm64`) so an Xcode app can link the framework later; they are not compiled in this Linux CI workflow (`kotlin.native.ignoreDisabledTargets=true`).

## Suggested next step

Move **networking** into `:shared`: replace Retrofit with Ktor in `commonMain`, take DTOs + mapping with it, and keep Hilt/OkHttp logging only as an Android HTTP engine adapter if needed. After that, `MovieRepositoryImpl` (or a Ktor-backed impl) can live in `shared`, with `:app` only providing UI + Hilt bindings. Abstracting `PagingData` out of the domain contract is optional and can wait until an iOS client needs a non-Paging consumer.
