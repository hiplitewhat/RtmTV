package com.example.rtmtv

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object PlaylistRepository {
    private const val PLAYLIST_URL = "https://rtm.samsam123.name.my/rtm-live.m3u8"
    private const val UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36"

    // Verified fallback list, used if the online playlist cannot be fetched.
    val FALLBACK: List<Channel> = listOf(
        ch("TV1", "tv1", "id=1"),
        ch("TV2", "tv2", "id=2"),
        ch("TV Okey", "okey", "id=3"),
        ch("Berita RTM", "berita", "id=5"),
        ch("Sukan RTM", "sukan", "id=4"),
        ch("TV6", "tv6", "id=6"),
        ch("Dewan Rakyat", "rakyat", "id=7"),
        ch("Dewan Negara", "negara", "id=8")
    )

    private fun ch(name: String, slug: String, q: String) = Channel(
        name = name,
        url = "https://d25tgymtnqzu8s.cloudfront.net/smil:$slug/playlist.m3u8?$q",
        group = "RTM",
        headers = mapOf(
            "Referer" to "https://rtmklik.rtm.gov.my/",
            "User-Agent" to UA
        )
    )

    suspend fun loadChannels(): List<Channel> = withContext(Dispatchers.IO) {
        runCatching {
            val conn = (URL(PLAYLIST_URL).openConnection() as HttpURLConnection).apply {
                connectTimeout = 10000
                readTimeout = 10000
                setRequestProperty("User-Agent", UA)
                setRequestProperty("Referer", "https://rtmklik.rtm.gov.my/")
            }
            val text = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            M3uParser.parse(text)
        }.getOrDefault(emptyList()).ifEmpty { FALLBACK }
    }
}
