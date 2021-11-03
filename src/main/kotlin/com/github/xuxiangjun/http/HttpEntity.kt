package com.github.xuxiangjun.http

import java.nio.charset.Charset

abstract class HttpEntity {
    abstract val headers: Map<String, String>

    abstract val body: ByteArray?

    fun getHeader(name: String): String? {
        for (entity in headers) {
            if (entity.key.equals(name, true)) {
                return entity.value
            }
        }

        return null
    }

    fun getContentType(): String? {
        return getHeader(HttpHeaders.ContentType)
    }

    fun getContentLength(): Long {
        return getHeader(HttpHeaders.ContentLength)?.toLong() ?: -1L
    }

    fun getContentText(): String? {
        val charset = getContentType()?.split(";")
            ?.map {
                it.split("=")
            }
            ?.firstOrNull {
                it.size == 2 && it[0].trim().equals("charset", true)
            }
            ?.let {
                try {
                    Charset.forName(it[1].trim())
                } catch (e: Exception) {
                    null
                }
            }
            ?: Charsets.UTF_8
        return body?.toString(charset)
    }

    fun getAuthorization(): String? {
        return getHeader(HttpHeaders.Authorization)
    }

    fun getDate(): String? {
        return getHeader(HttpHeaders.Date)
    }
}
