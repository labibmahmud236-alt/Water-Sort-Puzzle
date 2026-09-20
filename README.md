# AQUA SORT — Native Android Water Sort Puzzle Game

A complete, production-ready, commercial-grade native Android game built entirely with **Java**, **Android XML**, and the **Android SDK**.

---

## 🌊 Game Overview

**Aqua Sort** is a premium casual liquid-sorting puzzle game designed with a modern glassmorphism aesthetic, realistic liquid physics rendering, satisfying pouring animations, ambient relaxing audio, and 100 guaranteed-solvable puzzles.

### Key Features
- **100 Curated & Validated Solvable Levels**:
  - **Levels 1–20 (Easy)**: 4–6 tubes, 3–4 vibrant liquid colors, gentle onboarding curve.
  - **Levels 21–60 (Medium)**: 7–9 tubes, 5–7 colors, layered sorting strategy.
  - **Levels 61–100 (Hard)**: 10–14 tubes, 8+ colors, complex multidirectional mixing.
- **Glassmorphic Custom TubeView**: Custom-drawn canvas rendering with rounded lip rim, specular glass reflections, curved meniscuses, floating effervescent bubbles, selection glow aura, and shake warning animations.
- **Dynamic Pouring Physics & Animation**:
  - Source tube lifts and tilts smoothly toward destination mouth.
  - Parabolic liquid flow stream rendered via custom `PourOverlayView`.
  - Continuous animated meniscus surface and height drainage/fill transitions.
- **Game Engine & State Management**:
  - Pure Java `GameEngine` enforcing strict Water Sort rules.
  - Multi-layer simultaneous pouring for contiguous color stacks.
  - Full move history and state snapshots.
  - 3 free undos per level with option for extra undos via coins or rewarded ads.
  - Built-in AI Solver (`LevelSolver`) providing optimal moves for the smart **Hint System**.
- **Progression & Economy**:
  - 3-star rating system based on move efficiency.
  - In-game Coin system for purchasing hints and extra undos.
  - 7-day Daily Reward calendar with increasing rewards.
  - In-game Coin Shop with booster packs and coin bundles.
- **Audio & Haptics**:
  - Dedicated `SoundManager` utilizing `SoundPool` for ultra-low latency audio effects and `MediaPlayer` for looping ambient background music.
  - Dedicated `VibrationManager` with safe backwards-compatible haptic feedback.
- **Monetization (Google AdMob)**:
  - Configurable `AdManager` supporting:
    - **Banner Ads**: Non-intrusive banner on gameplay screen.
    - **Interstitial Ads**: Displayed once every 2 completed levels (Levels 2, 4, 6, 8, etc.).
    - **Rewarded Ads**: Unlocks +3 Undos, +1 Smart Hint, +100 Coins, or doubles level win rewards.
    - **App Open Ads**: Non-intrusive lifecycle ads after splash.
- **Complete Screen Architecture**:
  - Animated Splash Screen
  - Welcome Screen with floating interactive glass tubes
  - Interactive Step-by-Step Tutorial / How to Play
  - Main Home Dashboard with continue card and progress tracking
  - 100-Level Selection Grid (completed stars, current glow, locked indicators)
  - Full Gameplay Screen with dynamic responsive layout
  - Victory Celebration Dialog with 3 animated stars and confetti particles
  - Pause Dialog & Restart Confirmation Dialog
  - Settings Screen (Sound, Music, Vibration, Notifications toggles)
  - Native Share & Rate Us Integration
  - Privacy Policy & Terms of Service screens
  - "More Levels Coming Soon" milestone celebration screen

---

## 📁 Architecture & Project Structure

```
water-sort-puzzle/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/aquasort/puzzle/
│   │   │   ├── MainActivity.java
│   │   │   ├── game/
│   │   │   │   ├── GameEngine.java          # Core puzzle rules & turn manager
│   │   │   │   ├── GameState.java           # Complete active puzzle state & undo stack
│   │   │   │   ├── LevelManager.java        # 100 verified level definitions
│   │   │   │   ├── LevelSolver.java         # BFS solver & optimal hint calculator
│   │   │   │   ├── Move.java                # Immutable move descriptor
│   │   │   │   └── Tube.java                # Test tube model with liquid layers
│   │   │   ├── models/
│   │   │   │   ├── DailyReward.java         # 7-day reward model
│   │   │   │   ├── Level.java               # Level data model
│   │   │   │   ├── LevelProgress.java       # Completed status, stars, best score
│   │   │   │   └── PlayerData.java          # Coins, level, hints, boosters, preferences
│   │   │   ├── views/
│   │   │   │   ├── ConfettiView.java        # Win celebration particle explosion
│   │   │   │   ├── PourOverlayView.java     # Parabolic liquid stream overlay
│   │   │   │   └── TubeView.java            # Glass tube & liquid custom Canvas view
│   │   │   ├── screens/
│   │   │   │   ├── ComingSoonFragment.java  # Level 100 completion screen
│   │   │   │   ├── DailyRewardFragment.java # 7-day calendar reward screen
│   │   │   │   ├── GameFragment.java        # Active gameplay screen
│   │   │   │   ├── HomeFragment.java        # Main dashboard screen
│   │   │   │   ├── LegalFragment.java       # Privacy Policy & Terms viewer
│   │   │   │   ├── LevelSelectFragment.java # 100 level grid view
│   │   │   │   ├── SettingsFragment.java    # Sound, music, vibration, legal toggles
│   │   │   │   ├── ShopFragment.java        # Coin bundles & power-up shop
│   │   │   │   ├── SplashFragment.java      # Logo animation & launch router
│   │   │   │   └── WelcomeFragment.java     # Welcome screen with floating tubes
│   │   │   ├── adapters/
│   │   │   │   └── LevelAdapter.java        # 100-level RecyclerView grid adapter
│   │   │   ├── dialogs/
│   │   │   │   ├── HintDialog.java          # Hint purchase / watch ad dialog
│   │   │   │   ├── HowToPlayDialog.java     # Step-by-step interactive tutorial
│   │   │   │   ├── NeedMoreUndosDialog.java # Extra undos purchase / watch ad dialog
│   │   │   │   ├── PauseDialog.java         # In-game pause menu
│   │   │   │   ├── RateUsDialog.java        # Rating prompt dialog
│   │   │   │   ├── RestartDialog.java       # Restart confirmation dialog
│   │   │   │   └── RewardDialog.java        # Victory popup with stars & confetti
│   │   │   ├── services/
│   │   │   │   ├── AdManager.java           # AdMob Banner, Interstitial, Rewarded, AppOpen
│   │   │   │   ├── ShareManager.java        # Native Android Intent sharing
│   │   │   │   ├── SoundManager.java        # SoundPool SFX & MediaPlayer ambient music
│   │   │   │   ├── StorageManager.java      # SharedPreferences persistence
│   │   │   │   └── VibrationManager.java    # Haptic feedback system
│   │   │   └── utils/
│   │   │       ├── ColorUtils.java          # 12 liquid gradient palettes & highlights
│   │   │       └── Constants.java           # Shared constants & preferences keys
│   │   └── res/
│   │       ├── drawable/                    # Glass cards, buttons, vector icons, gradients
│   │       ├── layout/                      # XML layouts for all screens and dialogs
│   │       ├── mipmap-anydpi-v26/           # Adaptive launcher icons
│   │       ├── raw/                         # 10 synthesized WAV audio sound effects
│   │       └── values/                      # colors.xml, strings.xml, dimens.xml, themes.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── gradle.properties
└── settings.gradle
```

---

## 🛠️ Build & Run Instructions

### Prerequisites
- JDK 17 (or newer)
- Android SDK Platform 34 / 35 / 36
- Android SDK Build-Tools 35.0.0
- Gradle 8.11+ / 8.14+

### Compiling the Project
To compile and assemble the Debug APK:
```bash
./gradlew assembleDebug
```
The resulting APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Running Unit Tests
To run unit tests verifying the game engine, move validation, and level solvability:
```bash
./gradlew testDebugUnitTest
```

---

## ➕ How to Add Level 101, 102, and Beyond

Adding new levels is clean and modular:

1. Open `app/src/main/java/com/aquasort/puzzle/game/LevelManager.java`.
2. In the `buildLevel(int id)` switch statement, add a new `case`:
   ```java
   case 101:
       // Define tube layers from bottom to top (Color IDs: 1 to 12)
       tubes.add(Arrays.asList(Constants.COLOR_RED, Constants.COLOR_BLUE, Constants.COLOR_YELLOW, Constants.COLOR_GREEN));
       tubes.add(Arrays.asList(Constants.COLOR_GREEN, Constants.COLOR_RED, Constants.COLOR_BLUE, Constants.COLOR_YELLOW));
       tubes.add(Arrays.asList(Constants.COLOR_YELLOW, Constants.COLOR_GREEN, Constants.COLOR_RED, Constants.COLOR_BLUE));
       tubes.add(Arrays.asList(Constants.COLOR_BLUE, Constants.COLOR_YELLOW, Constants.COLOR_GREEN, Constants.COLOR_RED));
       tubes.add(Collections.<Integer>emptyList()); // Empty tube 1
       tubes.add(Collections.<Integer>emptyList()); // Empty tube 2
       return new Level(101, 4, tubes);
   ```
3. Update `Constants.TOTAL_LEVELS` in `Constants.java` if increasing the total count (e.g., from 100 to 120).
4. Run `LevelSolver.solve(tubes, 40)` in a unit test to verify that the newly added level is guaranteed solvable.

---

## 🚀 Google Play Release Preparation

### 1. AdMob Production IDs
Before publishing to Google Play, replace the Google test Ad Unit IDs in `app/src/main/res/values/strings.xml`:
```xml
<!-- Replace with your live AdMob IDs from Google AdMob Dashboard -->
<string name="admob_app_id">ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX</string>
<string name="admob_banner_id">ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX</string>
<string name="admob_interstitial_id">ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX</string>
<string name="admob_rewarded_id">ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX</string>
<string name="admob_app_open_id">ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX</string>
```

### 2. Building Release AAB / APK
Configure your signing keystore in `app/build.gradle` or sign manually, then run:
```bash
./gradlew bundleRelease
```
The App Bundle (.aab) will be located at:
```
app/build/outputs/bundle/release/app-release.aab
```

---

## 📜 License
Aqua Sort is an original casual puzzle game developed with 100% native Java and Android SDK. All code and assets are production-ready.
