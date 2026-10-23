package online.userlabs.freetv

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ChannelRepository(app)

    var channels by mutableStateOf<List<Channel>>(emptyList()); private set
    /** Group name to channel count, sorted by name. */
    var groups by mutableStateOf<List<Pair<String, Int>>>(emptyList()); private set
    var favorites by mutableStateOf(repo.favorites()); private set
    var loading by mutableStateOf(true); private set
    var message by mutableStateOf<String?>(null)

    var query by mutableStateOf("")
    var group by mutableStateOf<String?>(null)
    var tab by mutableIntStateOf(0) // 0 = channels, 1 = favorites

    /** Channels the player can step through; empty when the player is closed. */
    var queue by mutableStateOf<List<Channel>>(emptyList()); private set
    var playIndex by mutableIntStateOf(-1); private set

    init {
        viewModelScope.launch {
            try {
                applyChannels(repo.loadLocal())
            } catch (e: Exception) {
                message = getApplication<Application>().getString(R.string.update_failed, e.message ?: "")
            } finally {
                loading = false
            }
        }
    }

    private fun applyChannels(list: List<Channel>) {
        channels = list
        groups = list.groupingBy { it.group }.eachCount().toSortedMap(String.CASE_INSENSITIVE_ORDER).toList()
        if (group != null && groups.none { it.first == group }) group = null
    }

    fun refresh() {
        if (loading) return
        viewModelScope.launch {
            loading = true
            val app = getApplication<Application>()
            message = try {
                val list = repo.refresh()
                applyChannels(list)
                app.getString(R.string.updated, list.size)
            } catch (e: Exception) {
                app.getString(R.string.update_failed, e.message ?: e.toString())
            } finally {
                loading = false
            }
        }
    }

    fun toggleFavorite(c: Channel) {
        favorites = if (c.url in favorites) favorites - c.url else favorites + c.url
        repo.saveFavorites(favorites)
    }

    /** Opens the player on [c], with [visible] (minus web-only entries) as the zapping list. */
    fun play(c: Channel, visible: List<Channel>) {
        val playable = visible.filterNot { it.isExternalOnly }
        val i = playable.indexOf(c)
        if (i < 0) return
        queue = playable
        playIndex = i
    }

    fun closePlayer() {
        playIndex = -1
        queue = emptyList()
    }
}
