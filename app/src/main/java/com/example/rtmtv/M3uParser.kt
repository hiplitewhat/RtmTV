package com.example.rtmtv

object M3uParser {
    private val logoRe = Regex("tvg-logo=\"([^\"]*)\"")
    private val groupRe = Regex("group-title=\"([^\"]*)\"")

    fun parse(content: String): List<Channel> {
        val out = mutableListOf<Channel>()
        var name: String? = null
        var logo: String? = null
        var group: String? = null
        var headers = mutableMapOf<String, String>()

        content.lineSequence().forEach { raw ->
            val line = raw.trim()
            when {
                line.startsWith("#EXTINF") -> {
                    name = line.substringAfterLast(',', "").trim()
                    logo = logoRe.find(line)?.groupValues?.get(1)
                    group = groupRe.find(line)?.groupValues?.get(1)
                    headers = mutableMapOf()
                }
                line.startsWith("#EXTVLCOPT:http-referrer=") ->
                    headers["Referer"] = line.substringAfter("=").trim()
                line.startsWith("#EXTVLCOPT:http-user-agent=") ->
                    headers["User-Agent"] = line.substringAfter("=").trim()
                line.isNotEmpty() && !line.startsWith("#") -> {
                    name?.let {
                        out += Channel(it, line, logo, group, headers.toMap())
                    }
                    name = null
                }
            }
        }
        return out
    }
}
