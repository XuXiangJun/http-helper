package com.github.xuxiangjun.http

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

object HttpHelper {
    const val CODE_ERROR_OCCURRED = 10500
    const val CODE_IO_EXCEPTION = 10600

    private const val TAG = "HttpHelper"

    private const val CONNECT_TIMEOUT = 5000
    private const val READ_TIMEOUT = 6000

    fun request(method: HttpMethod, request: HttpRequest): HttpResponse {
        return execute(request, method)
    }

    fun request(
        method: HttpMethod,
        url: String,
        block: (HttpRequest.() -> Unit)? = null
    ): HttpResponse {
        val request = HttpRequest(url)
        if (block != null) {
            block(request)
        }
        return request(method, request)
    }

    fun get(request: HttpRequest): HttpResponse {
        return execute(request, HttpMethod.GET)
    }

    fun get(
        url: String,
        block: (HttpRequest.() -> Unit)? = null
    ): HttpResponse {
        val request = HttpRequest(url)
        if (block != null) {
            block(request)
        }
        return get(request)
    }

    fun head(request: HttpRequest): HttpResponse {
        return execute(request, HttpMethod.HEAD)
    }

    fun head(
        url: String,
        block: (HttpRequest.() -> Unit)? = null
    ): HttpResponse {
        val request = HttpRequest(url)
        if (block != null) {
            block(request)
        }
        return head(request)
    }

    fun post(request: HttpRequest): HttpResponse {
        return execute(request, HttpMethod.POST)
    }

    fun post(
        url: String,
        block: (HttpRequest.() -> Unit)? = null
    ): HttpResponse {
        val request = HttpRequest(url)
        if (block != null) {
            block(request)
        }
        return post(request)
    }

    fun put(request: HttpRequest): HttpResponse {
        return execute(request, HttpMethod.PUT)
    }

    fun put(
        url: String,
        block: (HttpRequest.() -> Unit)? = null
    ): HttpResponse {
        val request = HttpRequest(url)
        if (block != null) {
            block(request)
        }
        return put(request)
    }

    fun delete(request: HttpRequest): HttpResponse {
        return execute(request, HttpMethod.DELETE)
    }

    fun delete(
        url: String,
        block: (HttpRequest.() -> Unit)? = null
    ): HttpResponse {
        val request = HttpRequest(url)
        if (block != null) {
            block(request)
        }
        return delete(request)
    }

    fun trace(request: HttpRequest): HttpResponse {
        return execute(request, HttpMethod.TRACE)
    }

    fun trace(
        url: String,
        block: (HttpRequest.() -> Unit)? = null
    ): HttpResponse {
        val request = HttpRequest(url)
        if (block != null) {
            block(request)
        }
        return trace(request)
    }

    fun options(request: HttpRequest): HttpResponse {
        return execute(request, HttpMethod.OPTIONS)
    }

    fun options(
        url: String,
        block: (HttpRequest.() -> Unit)? = null
    ): HttpResponse {
        val request = HttpRequest(url)
        if (block != null) {
            block(request)
        }
        return options(request)
    }

    fun connect(request: HttpRequest): HttpResponse {
        return execute(request, HttpMethod.CONNECT)
    }

    fun connect(
        url: String,
        block: (HttpRequest.() -> Unit)? = null
    ): HttpResponse {
        val request = HttpRequest(url)
        if (block != null) {
            block(request)
        }
        return connect(request)
    }

    fun patch(request: HttpRequest): HttpResponse {
        return execute(request, HttpMethod.PATCH)
    }

    fun patch(
        url: String,
        block: (HttpRequest.() -> Unit)? = null
    ): HttpResponse {
        val request = HttpRequest(url)
        if (block != null) {
            block(request)
        }
        return patch(request)
    }

    private fun execute(request: HttpRequest, method: HttpMethod): HttpResponse {
        val url: String = request.getEncodedUrl()
        val headers: Map<String, String> = request.headers
        val body: ByteArray? = request.body

        var conn: HttpURLConnection? = null
        var requestStreamFinal: InputStream? = null
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

            val requestStream = request.inputStream
            requestStreamFinal = requestStream
            val doOutput = if (requestStream != null) {
                true
            } else {
                body != null && body.isNotEmpty()
            }
            val chunkLength = if (request.chunkLength > 0) {
                request.chunkLength
            } else if (requestStream != null) {
                4096
            } else {
                -1
            }
            if (doOutput) {
                connection.doOutput = true
                if (chunkLength > 0) {
                    connection.setChunkedStreamingMode(chunkLength)
                } else {
                    connection.setFixedLengthStreamingMode(body!!.size)
                }
            }
            connection.connect()
            if (doOutput) {
                if (requestStream != null) {
                    write(connection.outputStream, requestStream, chunkLength)
                } else {
                    connection.outputStream.write(body!!)
                }
            }

            val code = connection.responseCode
            val message = connection.responseMessage ?: ""
            val respHeaders = connection.headerFields.filter {
                it.key != null
            }.mapValues {
                it.value.first()
            }

            val input = connection.errorStream ?: connection.inputStream
            val content = input.readBytes()
            return HttpResponse(code, message, respHeaders, content)
        } catch (e: Exception) {
            e.printStackTrace()
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
            try {
                requestStreamFinal?.close()
            } catch (ignore: Exception) {
            }
        }
    }

    private fun write(output: OutputStream, input: InputStream, bufSize: Int) {
        val buf = ByteArray(bufSize)
        while (true) {
            val read = input.read(buf)
            if (read == -1) {
                break
            }

            output.write(buf, 0, read)
        }
    }
}
