package com.github.xuxiangjun.http

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.charset.Charset

open class PartData(
    val name: String,
    var fileName: String? = null
) {
    val headers: MutableMap<String, String> = mutableMapOf()

    var content: ByteArray? = null

    var inputStream: InputStream? = null

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

class FilePartData : PartData{

    constructor(
        name: String,
        fileData: ByteArray,
        fileName: String
    ) : super(name, fileName) {
        this.content = fileData
    }

    constructor(
        name: String,
        file: File,
        fileName: String = file.name
    ) : super(name, fileName) {
        this.inputStream = FileInputStream(file)
    }
}
