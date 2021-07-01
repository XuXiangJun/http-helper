package com.github.xuxiangjun.http

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object HttpHelper {
    const val CODE_IO_EXCEPTION = 9000
    const val CODE_ERROR_OCCURRED = 9001

    private const val TAG = "HttpHelper"

    private const val CONNECT_TIMEOUT = 5000
    private const val READ_TIMEOUT = 6000

    fun request(method: HttpMethod, request: HttpRequest): HttpResponse {
        return execute(request, method)
    }

    fun get(request: HttpRequest): HttpResponse {
        return execute(request, HttpMethod.GET)
    }

    fun head(request: HttpRequest): HttpResponse {
        return execute(request, HttpMethod.HEAD)
    }

    fun post(request: HttpRequest): HttpResponse {
        return execute(request, HttpMethod.POST)
    }

    fun put(request: HttpRequest): HttpResponse {
        return execute(request, HttpMethod.PUT)
    }

    fun delete(request: HttpRequest): HttpResponse {
        return execute(request, HttpMethod.DELETE)
    }

    fun trace(request: HttpRequest): HttpResponse {
        return execute(request, HttpMethod.TRACE)
    }

    fun options(request: HttpRequest): HttpResponse {
        return execute(request, HttpMethod.OPTIONS)
    }

    fun connect(request: HttpRequest): HttpResponse {
        return execute(request, HttpMethod.CONNECT)
    }

    fun patch(request: HttpRequest): HttpResponse {
        return execute(request, HttpMethod.PATCH)
    }

    private fun execute(request: HttpRequest, method: HttpMethod): HttpResponse {
        val url: String = request.getEncodedUrl()
        val headers: Map<String, String> = request.headers
        val body: ByteArray? = request.body

        var conn: HttpURLConnection? = null
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            conn = connection
            connection.requestMethod = method.name
            connection.connectTimeout = if (request.connectTimeout > 0) {
                request.connectTimeout
            } else {
                CONNECT_TIMEOUT
            }
            connection.readTimeout = if (request.readTimeout > 0) {
                request.readTimeout
            } else {
                READ_TIMEOUT
            }
            headers.forEach {
                connection.setRequestProperty(it.key, it.value)
            }

            val doOutput = body != null && body.isNotEmpty()
            if (doOutput) {
                connection.doOutput = true
                if (request.chunkLength >  0) {
                    connection.setChunkedStreamingMode(request.chunkLength)
                } else {
                    connection.setFixedLengthStreamingMode(body!!.size)
                }
            }

            connection.connect()
            if (doOutput) {
                connection.outputStream.write(body!!)
            }

            val code = connection.responseCode
            val message = connection.responseMessage
            val respHeaders = connection.headerFields.filter {
                it.key != null
            }.mapValues {
                it.value.first()
            }

            val input = connection.errorStream ?: connection.inputStream
            val content = input.readBytes()
            return HttpResponse(code, message, respHeaders, content)
        } catch (e: Exception) {
            println("$TAG: execute() Catch Exception: ${e.localizedMessage}")
            val errorCode = if (e is IOException) {
                CODE_IO_EXCEPTION
            } else {
                CODE_ERROR_OCCURRED
            }
            val errorBody = """
                {
                    "error_code":$errorCode,
                    "description":"see Exception instance"
                }
            """.trimIndent().toByteArray()
            return HttpResponse(errorCode, "Error", emptyMap(), errorBody, e)
        } finally {
            conn?.disconnect()
        }
    }
}
