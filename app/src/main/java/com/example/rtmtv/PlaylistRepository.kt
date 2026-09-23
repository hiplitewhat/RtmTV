package com.example.rtmtv

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PlaylistRepository {
    private const val BASE = "https://d25tgymtnqzu8s.cloudfront.net"
    private const val UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36"

    private fun dash(name: String, slug: String, logo: String?) = Channel(
        name = name,
        url = "$BASE/smil:$slug/manifest.mpd",
        logo = logo,
        group = "RTM",
        headers = mapOf("User-Agent" to UA)
    )

    // DASH endpoints (verified HTTP 200 from CloudFront).
    val CHANNELS: List<Channel> = listOf(
        dash("TV1", "tv1", "http://rtm.samsam123.tk/Channel%20Logo/TV1.jpg"),
        dash("TV2", "tv2", "http://rtm.samsam123.tk/Channel%20Logo/TV2.jpg"),
        dash("TV Okey", "okey", "http://rtm.samsam123.tk/Channel%20Logo/TV%20Okey.jpg"),
        dash("Berita RTM", "berita", "http://rtm.samsam123.tk/Channel%20Logo/Berita%20RTM.jpg"),
        dash("Sukan RTM", "sukan", "http://rtm.samsam123.tk/Channel%20Logo/Sukan%20RTM.jpg"),
        dash("TV6", "tv6", "http://rtm.samsam123.tk/Channel%20Logo/TV6.jpg"),
        dash("Dewan Rakyat", "rakyat", "http://rtm.samsam123.tk/Channel%20Logo/Dewan%20Rakyat.jpg"),
        dash("Dewan Negara", "negara", "http://rtm.samsam123.tk/Channel%20Logo/Dewan%20Negara.jpg")
    )

    suspend fun loadChannels(): List<Channel> = withContext(Dispatchers.IO) { CHANNELS }
}
