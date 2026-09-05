# MovieApp

An Android movie browser built with Kotlin, Jetpack Compose, and the [TMDB API](https://developer.themoviedb.org/docs/getting-started). Originally a technical assignment, this small project demonstrates paginated data loading, screen state, dependency injection, and Compose navigation.

## What you can try

- Browse popular movies with paging, poster placeholders, and retry controls.
- Open a movie for its overview, rating, release year, runtime, and genres.
- Search movies by title. Queries are debounced for 500 ms; changing the query switches to a new paging stream.
- Exercise loading, empty-result, and network-error states.

The implemented endpoints cover **movies only**. There is no TV search or video playback.

## Run locally

### Requirements

- JDK 17 for Gradle. The app targets Java/Kotlin JVM bytecode 11.
- Android SDK Platform 35 and Build Tools 35.0.0.
- An emulator or Android device running API 24 or later.
- A TMDB account and v3 API key to load live movie data.

The wrapper pins Gradle 8.11.1. The project uses Android Gradle Plugin 8.9.2, Kotlin 2.1.20, and KSP 2.1.20-2.0.0. See the [AGP compatibility table](https://developer.android.com/build/releases/agp-8-9-0-release-notes) and [KSP version guidance](https://kotlinlang.org/docs/ksp-faq.html).

### 1. Clone and open

```bash
git clone https://github.com/sivica/movieapp.git
cd movieapp
```

Open the repository root in Android Studio. Select JDK 17 as the Gradle JDK and install the SDK packages above using SDK Manager. Android Studio normally writes the SDK location to `local.properties`; command-line builds can instead use `ANDROID_HOME`.

### 2. Configure TMDB

Request a **v3 API key** from your [TMDB account settings](https://www.themoviedb.org/settings/api). This app authenticates using the `api_key` query parameter. A bearer read-access token is a different credential; see [TMDB application authentication](https://developer.themoviedb.org/docs/authentication-application).

Add this entry to the root `local.properties`, keeping any existing `sdk.dir` entry:

```properties
tmdb_api_key=YOUR_TMDB_V3_API_KEY
```

If the file does not exist, copy [local.properties.example](local.properties.example) to `local.properties` first. Values surrounded by one pair of double quotes are also accepted for compatibility with earlier checkouts.

For automation, the `TMDB_API_KEY` environment variable takes precedence over `local.properties`. An unset key becomes an empty string so compilation and unit tests do not require a credential. Live browsing still needs a valid key.

`local.properties` is ignored by Git. The key is compiled into the app, and debug HTTP logging includes request URLs, so do not share APKs or logs containing your real key.

### 3. Build and launch

On macOS or Linux:

```bash
./gradlew :app:testDebugUnitTest :app:assembleDebug
./gradlew :app:installDebug
```

The install task needs a running emulator or connected device. Launch the installed app, or select the `app` run configuration in Android Studio. The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

On Windows, use `gradlew.bat` in place of `./gradlew`. If a downloaded ZIP loses executable permissions, run `chmod +x gradlew` first.

## Architecture and code tour

The app uses one Gradle module with packages for UI, domain, data, and dependency injection. These are package boundaries, not independently enforced module boundaries.

| Area | Responsibility | Starting point |
|---|---|---|
| Compose UI | Render state, collect paging data, and handle user actions | [MovieListScreen](app/src/main/java/com/example/movieapp/ui/movielist/MovieListScreen.kt), [SearchScreen](app/src/main/java/com/example/movieapp/ui/search/SearchScreen.kt) |
| ViewModels | Cache paging streams and expose screen state | [MovieListViewModel](app/src/main/java/com/example/movieapp/ui/movielist/MovieListViewModel.kt), [SearchViewModel](app/src/main/java/com/example/movieapp/ui/search/SearchViewModel.kt) |
| Domain | Models, use cases, and the repository contract | [MovieRepository](app/src/main/java/com/example/movieapp/domain/repository/MovieRepository.kt) |
| Data | Create pagers, load API pages, and map DTOs into app models | [MovieRepositoryImpl](app/src/main/java/com/example/movieapp/data/repository/MovieRepositoryImpl.kt), [ModelMapper](app/src/main/java/com/example/movieapp/data/mapper/ModelMapper.kt) |
| Networking and DI | Wire Retrofit, OkHttp, serialization, and Hilt dependencies | [NetworkModule](app/src/main/java/com/example/movieapp/di/NetworkModule.kt), [TmdbRemoteDataSource](app/src/main/java/com/example/movieapp/data/datasource/TmdbRemoteDataSource.kt) |
| Navigation | Connect the popular list, search, and movie detail routes | [MainActivity](app/src/main/java/com/example/movieapp/MainActivity.kt) |

### Decisions and tradeoffs

- **Paging 3** owns page loading and exposes load states to the lists. `cachedIn(viewModelScope)` retains the paging stream for the ViewModel lifetime; it does not provide offline persistence.
- **StateFlow and Compose** separate screen state from rendering. Detail and search state are collected with lifecycle awareness. Search uses `debounce` and `flatMapLatest` to switch streams as the query changes.
- **DTO mapping** separates API payloads from displayed models. The domain repository contract exposes `PagingData`, so the domain layer still depends on AndroidX Paging.
- **Hilt and repository interfaces** make dependencies explicit and allow test doubles. Use cases are small in this sample; the project does not demonstrate complex business rules or a multi-module build.

## Verification

```bash
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
# Requires a running emulator or connected device:
./gradlew :app:connectedDebugAndroidTest
```

The [ViewModel test](app/src/test/java/com/example/movieapp/ui/movielist/MovieListViewModelTest.kt) awaits a Paging snapshot and checks the returned movies. The instrumentation test is only a package-name smoke test, not end-to-end UI coverage. Unit-test reports are generated under `app/build/reports/tests/testDebugUnitTest/`.

The [Android checks workflow](.github/workflows/android.yml) is configured to run unit tests, Android lint, and a debug build on pull requests and pushes to `master`. It uses JDK 17 and builds without a TMDB key. It does not publish an APK or deploy the app. Check [GitHub Actions](https://github.com/sivica/movieapp/actions) for run results.

Use the [review and screenshot guide](docs/REVIEW_GUIDE.md) for manual happy-path and failure-state checks. Real screenshots have not been captured in this polish pass; the guide records what to capture.

## Current scope and next improvements

- No offline persistence, favorites, sign-in, or playback.
- No implemented TV endpoints, even though some internal types retain a media-type parameter.
- Search queries are held in ViewModel memory and are not restored after process death.
- Automated coverage is narrow. The next useful tests are paging boundaries, API failures, query changes, and detail retry behavior.
- API errors currently expose technical exception messages; friendlier user-facing errors and accessibility/localization review remain follow-up work.

Movie data and images are provided by TMDB. This product uses the TMDB API but is not endorsed or certified by TMDB.
