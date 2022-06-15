package com.github.xuxiangjun.http

import java.nio.charset.Charset

open class PartData(
    val name: String,
    var fileName: String? = null
) {
    val headers: MutableMap<String, String> = mutableMapOf()

    lateinit var content: ByteArray

    fun setHeader(name: String, value: String) {
        this.headers[name] = value
    }

    fun setHeaders(vararg headers: Pair<String, String>) {
        for (pair in headers) {
            this.headers[pair.first] = pair.second
        }
    }
}

class ByteArrayPartData(
    name: String,
    data: ByteArray
) : PartData(name) {
    init {
        this.content = data
    }
}

class StringPartData(
    name: String,
    value: String,
    charset: Charset = Charsets.UTF_8
) : PartData(name) {
    init {
        this.content = value.toByteArray(charset)
    }
}

class FilePartData(
    name: String,
    fileName: String,
    fileData: ByteArray
) : PartData(name, fileName) {
    init {
        this.content = fileData
    }
}
