package com.github.xuxiangjun.http

import java.io.InputStream
import java.net.URL
import java.net.URLEncoder

open class HttpRequest : HttpEntity {
    private val parameters: MutableMap<String, String> = mutableMapOf()
    override val headers: MutableMap<String, String> = mutableMapOf()

    val url: String
    private var innerBody: ByteArray?
    private var innerStream: InputStream?

    var chunkLength = -1
    var connectTimeout = -1
    var readTimeout = -1

    override var body: ByteArray?
        get() {
            return innerBody
        }
        set(value) {
            innerBody = value
            innerStream = null
        }

    open var inputStream: InputStream?
        get() {
            return innerStream
        }
        set(value) {
            innerStream = value
            innerBody = null
        }

    constructor(url: String, body: ByteArray? = null) {
        this.url = url
        this.innerBody = body
        this.innerStream = null
    }

    constructor(url: String, inputStream: InputStream) {
        this.url = url
        this.innerBody = null
        this.innerStream = inputStream
    }

    fun setParameter(name: String, value: String) {
        this.parameters[name] = value
    }

    fun setParameters(vararg params: Pair<String, String>) {
        for (p in params) {
            this.parameters[p.first] = p.second
        }
    }

    fun setParameters(params: Map<String, String>) {
        for (entry in params) {
            this.parameters[entry.key] = entry.value
        }
    }

    fun setHeader(name: String, value: String) {
        this.headers[name] = value
    }

    fun setHeaders(vararg headers: Pair<String, String>) {
        for (h in headers) {
            this.headers[h.first] = h.second
        }
    }

    fun setHeaders(headers: Map<String, String>) {
        for (entry in headers) {
            this.headers[entry.key] = entry.value
        }
    }

    fun getEncodedUrl(charset: String = "UTF-8"): String {
        val builder = StringBuilder(url)
        val jUrl = URL(url)
        if (parameters.isNotEmpty()) {
            if (jUrl.query == null) {
                builder.append("?")
            } else if (jUrl.query.isNotEmpty()) {
                builder.append("&")
            }
        }
        var index = 0
        for ((key, value) in parameters) {
            builder.append(URLEncoder.encode(key, charset))
            builder.append("=")
            builder.append(URLEncoder.encode(value, charset))
            if (index < parameters.size - 1) {
                builder.append("&")
            }
            ++index
        }

        return builder.toString()
    }
}
