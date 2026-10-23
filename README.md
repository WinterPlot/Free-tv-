# Free TV (Android)

Jetpack Compose Android app (package `online.userlabs.freetv`) that plays the free-to-air channels from the Free-TV/IPTV playlist (~2000 channels, ~97 countries).

- Bundled playlist works offline; the refresh button downloads the latest from GitHub.
- Material 3 dark UI: channel grid with logos, search, country chips, Channels/Favorites tabs, full-screen landscape player with next/previous channel.
- HLS, DASH and RTSP via Media3/ExoPlayer. YouTube/Twitch/Dailymotion entries open in their own app.
- Android 8.0+ (API 26). Also launches on Android TV.

## Build the APK

**Option A – Android Studio:** open this folder, let Gradle sync, press Run (or Build > Build APK).

**Option B – command line** (JDK 17 + Android SDK installed):
`./gradlew assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk`

**Option C – no setup:** push this folder to a GitHub repo; the "Build APK" workflow
(Actions tab) produces the APK as a downloadable artifact.

## Customize
- Playlist source: `PLAYLIST_URL` in `ChannelRepository.kt` (point it at your own list to add channels).
- Bundled list: `app/src/main/assets/playlist.m3u8`.
- Name: `res/values/strings.xml`. Package: `namespace`/`applicationId` in `app/build.gradle` (and the `online/userlabs/freetv` source folder).
- Colors: `Theme.kt`.

## Notes
- Channel data comes from the [Free-TV/IPTV](https://github.com/Free-TV/IPTV) project; check its license/terms before redistributing the bundled playlist.
- If `gradlew` loses its executable bit after cloning on Windows: `git update-index --chmod=+x gradlew`.
