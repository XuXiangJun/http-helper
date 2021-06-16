package com.github.xuxiangjun.http

open class HttpResponse(
    val code: Int,
    val message: String,
    val headers: Map<String, String>,
    val body: ByteArray? = null,
    val error: Exception? = null
) {
    val isSuccess = code in 200..299

    override fun toString(): String {
        return "code:$code, message:$message, headers:$headers, body size:${body?.size ?: 0}, error:${error?.localizedMessage}"
    }

    open fun formatString(): String {
        val builder = StringBuilder().apply {
            append(code).append(" ").append(message).append("\r\n")
            headers.forEach {
                append(it.key).append(": ").append(it.value).append("\r\n")
            }
            append("\r\n")
            if (body != null) {
                append(String(body))
            }
            if (error != null) {
                append(error.stackTraceToString())
            }
        }
        return builder.toString()
    }
}
