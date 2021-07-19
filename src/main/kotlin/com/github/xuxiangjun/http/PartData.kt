package com.github.xuxiangjun.http

open class PartData(
    val name: String,
    var fileName: String? = null
) {
    val headers: MutableMap<String, String> = mutableMapOf()

    lateinit var content: ByteArray
}
