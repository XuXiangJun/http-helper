package com.github.xuxiangjun.http

open class HttpResponse(
    val code: Int,
    val message: String,
    override val headers: Map<String, String>,
    override val body: ByteArray? = null,
    val error: Exception? = null
) : HttpEntity() {
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
            body?.also {
                append(getContentText())
            }
            if (error != null) {
                append(error.stackTraceToString())
            }
        }
        return builder.toString()
    }
}
