package com.github.xuxiangjun.http

import java.io.InputStream
import java.net.URLEncoder

open class HttpRequest : HttpEntity {
    private val parameters: MutableMap<String, String> = mutableMapOf()
    override val headers: MutableMap<String, String> = mutableMapOf()

    val url: String
    private var innerBody: ByteArray?
    val inputStream: InputStream?

    var chunkLength = -1
    var connectTimeout = -1
    var readTimeout = -1

    override var body: ByteArray?
        get() {
            return innerBody
        }
        set(value) {
            if (inputStream != null) {
                throw IllegalArgumentException("inputStream has been set")
            }
            innerBody = value
        }

    constructor(url: String, body: ByteArray? = null) {
        this.url = url
        this.innerBody = body
        this.inputStream = null
    }

    constructor(url: String, inputStream: InputStream) {
        this.url = url
        this.innerBody = null
        this.inputStream = inputStream
    }

    fun addParameter(name: String, value: String) {
        this.parameters[name] = value
    }

    fun addParameters(vararg params: Pair<String, String>) {
        for (p in params) {
            this.parameters[p.first] = p.second
        }
    }

    fun addHeader(name: String, value: String) {
        this.headers[name] = value
    }

    fun addHeaders(vararg headers: Pair<String, String>) {
        for (h in headers) {
            this.headers[h.first] = h.second
        }
    }

    fun getEncodedUrl(charset: String = "UTF-8"): String {
        val paramsStr = parameters.run {
            val builder = StringBuilder()
            var index = 0
            forEach {
                if (index == 0) {
                    builder.append("?")
                }
                builder.append(URLEncoder.encode(it.key, charset))
                builder.append("=")
                builder.append(URLEncoder.encode(it.value, charset))
                if (index < size - 1) {
                    builder.append("&")
                }
                ++index
            }
            builder.toString()
        }

        return "$url$paramsStr"
    }
}
