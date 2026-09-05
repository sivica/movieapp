# MovieApp review and screenshot guide

## Scope of this polish

This pass improves setup and explains the existing app accurately. It fixes API-key string generation, aligns Kotlin with the existing KSP version, records the Gradle wrapper as executable, corrects the search label, and makes the existing Paging test await its assertion. A GitHub Actions workflow is prepared to check pull requests with unit tests, lint, and a debug build, without API credentials or deployment.

## Automated checks

Use JDK 17 with Android SDK Platform 35 and Build Tools 35.0.0:

```bash
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
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

These are expected outcomes to verify, not a record of checks already passed.

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

- Source and documentation reviewed against the checkout based on `46bb153`.
- The build attempt stopped while downloading Gradle 8.11.1 with `Network is unreachable`, before compilation or test execution.
- A retry through the environment-provided proxy ended with network approval cancelled before a decision was returned. The build and tests remain unexecuted.
- This workspace has a Java 17 runtime, but no configured full JDK, Android SDK, device, or emulator. No screenshots or live TMDB checks were performed.
- Build, lint, unit tests, and device checks still need to run in an Android development environment before merging.
- The new GitHub Actions workflow has not been published or run. Documentation links and the version catalog were checked locally; those checks do not substitute for an Android build.
