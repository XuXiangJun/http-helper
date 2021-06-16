package com.github.xuxiangjun.http

import java.net.URLEncoder

open class HttpRequest(
    val url: String,
    var body: ByteArray? = null
) {
    private val parameters: MutableMap<String, String> = mutableMapOf()
    val headers: MutableMap<String, String> = mutableMapOf()

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
