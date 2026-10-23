package online.userlabs.freetv

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ChannelRepository(private val ctx: Context) {

    companion object {
        /** Upstream playlist. Point this at your own list to add channels. */
        const val PLAYLIST_URL = "https://raw.githubusercontent.com/Free-TV/IPTV/master/playlist.m3u8"
        private const val CACHE_NAME = "playlist.m3u8"
        private const val KEY_FAVORITES = "favorites"
    }

    private val prefs = ctx.getSharedPreferences("freetv", Context.MODE_PRIVATE)
    private val cacheFile get() = File(ctx.filesDir, CACHE_NAME)

    fun favorites(): Set<String> =
        (prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()).toSet()

    fun saveFavorites(urls: Set<String>) {
        prefs.edit().putStringSet(KEY_FAVORITES, HashSet(urls)).apply()
    }

    /** Cached playlist if present, otherwise the one bundled in assets. */
    suspend fun loadLocal(): List<Channel> = withContext(Dispatchers.IO) {
        val cached = runCatching {
            if (cacheFile.exists()) cacheFile.inputStream().use { M3uParser.parse(it) } else null
        }.getOrNull()
        if (!cached.isNullOrEmpty()) cached
        else ctx.assets.open("playlist.m3u8").use { M3uParser.parse(it) }
    }

    /** Downloads the latest playlist, validates it, then replaces the cache. */
    suspend fun refresh(): List<Channel> = withContext(Dispatchers.IO) {
        val tmp = File(ctx.filesDir, "$CACHE_NAME.tmp")
        val conn = URL(PLAYLIST_URL).openConnection() as HttpURLConnection
        try {
            conn.connectTimeout = 15000
            conn.readTimeout = 30000
            conn.setRequestProperty("User-Agent", "FreeTV-Android")
            if (conn.responseCode != 200) throw IOException("HTTP ${conn.responseCode}")
            conn.inputStream.use { input -> tmp.outputStream().use { input.copyTo(it) } }
            val parsed = tmp.inputStream().use { M3uParser.parse(it) }
            if (parsed.size < 100) throw IOException("playlist looks invalid")
            cacheFile.delete()
            if (!tmp.renameTo(cacheFile)) throw IOException("cannot save playlist")
            parsed
        } catch (e: Exception) {
            tmp.delete()
            throw e
        } finally {
            conn.disconnect()
        }
    }
}
