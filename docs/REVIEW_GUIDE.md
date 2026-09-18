# MovieApp review and screenshot guide

## Scope of this polish

This pass improves setup and explains the existing app accurately. It fixes API-key string generation, aligns Kotlin with the existing KSP version, records the Gradle wrapper as executable, corrects the search label, and makes the existing Paging test await its assertion. A GitHub Actions workflow is prepared to check pull requests with unit tests, lint, and a debug build, without API credentials or deployment.

## Automated checks

Use JDK 17 with Android SDK Platform 35 and Build Tools 35.0.0:

```bash
./gradlew :shared:jvmTest :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

Confirm that a clean checkout builds with no TMDB key. Then configure a v3 key in `local.properties` and build again before the live-app checks. Preserve existing SDK paths and credentials.

The unit test now calls `asSnapshot` directly inside `runTest`, using an in-memory `Pager` fixture that emits load states. Previously, its assertion was launched in `backgroundScope` with a standard test dispatcher and no wait; that work could be cancelled when the test body finished. The change follows the [Paging testing pattern](https://developer.android.com/topic/libraries/architecture/paging/test).

## Manual app checks

| Scenario | Expected behavior |
|---|---|
| Launch with a valid v3 key | Popular movies load; opening a row shows the matching movie detail. |
| Scroll beyond the first page | More movies load; rows still open the correct detail. |
| Search for a title, then change the query | Results settle on the final query after its debounce period. |
| Search for an unlikely title | A no-results message appears after loading completes. |
| Clear the search field | Results clear and the prompt to enter a query appears. |
| Disable networking and load a new page or detail | A retry control appears; restoring networking and retrying recovers. |
| Open detail and navigate back | The app returns to the previous list/search route. |

These are expected outcomes. The dated results below record the checks completed for this polish branch.

## Capture real screenshots

Use the same device, orientation, and theme for all captures. Configure a valid TMDB v3 key, build the debug app, and navigate to each screen. If multiple devices are attached, select one with `adb -s SERIAL`.

From the repository root, with Android SDK platform-tools on your PATH:

```bash
mkdir -p docs/screenshots
# With the popular-movie list loaded:
adb exec-out screencap -p > docs/screenshots/popular.png
# Open a movie detail, then run:
adb exec-out screencap -p > docs/screenshots/detail.png
# Search for a title and wait for results, then run:
adb exec-out screencap -p > docs/screenshots/search.png
```

Inspect the images for empty/loading states and unrelated notifications. Add image links to the README only after the files exist; include the actual device/API level and capture date.

## Validation status for this pass

- Android Studio Quail 2 (2026.1.2 Patch 1) synced the project and installed the debug app from branch `polish/movieapp-portfolio` at `fea5b39`.
- `:app:installDebug` completed successfully with Android Studio's JDK 17 and a local, ignored TMDB v3 key.
- On September 6, 2026, the popular list, the first movie's detail screen, and search results for `Dune` loaded from TMDB on a Pixel 7 emulator running Android 16 (API 36).
- The three real captures are stored in `docs/screenshots/` and linked from the README.
- [Pull request #1](https://github.com/sivica/movieapp/pull/1) passed its GitHub Actions unit-test, lint, and debug-build jobs for this branch.

### Remaining-scenario results

Tested September 6, 2026 on the same Pixel 7 emulator running Android 16 (API 36). The branch was clean and synchronized with `origin/polish/movieapp-portfolio` at `de34b8d`; the executable application source is unchanged from `fea5b39` because `de34b8d` contains documentation and screenshot updates only. The locally configured TMDB v3 key remained ignored and was not inspected or exposed.

| Scenario | Status | Observed result |
|---|---|---|
| Popular-list pagination | PASS | Scrolling beyond the initial results loaded additional movies without a crash. Opening the newly loaded `Mayday` row showed matching title, year, runtime, genres, and overview; returning preserved the paginated list position. |
| Query changes | PASS | A rapid edit from `Dune` to `Batman` settled on `Batman`; the visible results all corresponded to the final query after the 500 ms debounce. |
| Empty results | PASS | `zzzxqv901734movie` completed with a no-results message for that exact query and no stale movie rows. |
| Clear query | PASS | Clearing loaded search results removed the rows and restored the `Enter a query to search` prompt. |
| Offline page loading and retry | PASS | With emulator airplane mode enabled, scrolling to a genuinely uncached page exposed an append error and `Retry`. After restoring connectivity, `Retry` loaded more rows, including `The Godfather`. |
| Offline details and retry | PASS | Opening the previously unopened `Prison on Fire` detail while offline produced a timeout and `Retry`. After connectivity was restored, retry loaded the correct title and metadata. |
| Back navigation | PASS | Toolbar and Android system Back both returned from details to the correct Popular Movies or Search route. The `Batman` query and search position were retained, and the popular list retained its paginated position. |

Emulator airplane mode was disabled again after the failure-state checks.
