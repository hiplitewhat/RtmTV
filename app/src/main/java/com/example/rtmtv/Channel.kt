package com.example.rtmtv

data class Channel(
    val name: String,
    val url: String,
    val logo: String? = null,
    val group: String? = null,
    val headers: Map<String, String> = emptyMap()
)
