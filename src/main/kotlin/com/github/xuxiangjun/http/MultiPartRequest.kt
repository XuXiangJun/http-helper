package com.github.xuxiangjun.http

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class MultiPartRequest(
    url: String,
    parts: List<PartData>? = null
) : HttpRequest(url) {

    var boundary: String = ""
        set(value) {
            setHeader(HttpHeaders.ContentType, "multipart/form-data;boundary=$value")
            field = value
        }

    private val partDataList: MutableList<PartData> = mutableListOf()


    init {
        boundary = UUID.randomUUID().toString().replace("-", "")
        if (parts != null) {
            partDataList.addAll(parts)
        }
    }

    fun addPartData(partData: PartData) {
        partDataList.add(partData)
    }

    private fun OutputStream.writeHeaderPart(partData: PartData) {
        // Write disposition header
        write("--$boundary\r\n".toByteArray())
        write("${HttpHeaders.ContentDisposition}: form-data;name=${partData.name}".toByteArray())
        partData.fileName?.also { write(";filename=$it".toByteArray()) }
        write("\r\n".toByteArray())

        // Write part data headers
        partData.headers.forEach { (k, v) ->
            write("$k: $v\r\n".toByteArray())
        }
        write("\r\n".toByteArray())
    }

    private fun OutputStream.writeTailPart() {
        write("--$boundary--\r\n".toByteArray())
    }

    private fun useStream(): Boolean {
        var useStream = false
        for (partData in partDataList) {
            if (partData.inputStream != null) {
                useStream = true
                break
            }
        }
        return useStream
    }

    private fun useBody(): Boolean {
        return !useStream()
    }

    private fun createMultiStream(): MultiInputStream {
        val streamList = mutableListOf<InputStream>()
        val output = ByteArrayOutputStream()
        for (partData in partDataList) {
            output.writeHeaderPart(partData)
            val headerStream = ByteArrayInputStream(output.toByteArray())
            streamList.add(headerStream)
            output.reset()

            val content = partData.content
            if (content != null) {
                output.write(content)
                output.write("\r\n".toByteArray())

                val contentStream = ByteArrayInputStream(output.toByteArray())
                streamList.add(contentStream)
                output.reset()
            } else {
                streamList.add(partData.inputStream!!)
            }
        }

        output.reset()
        output.writeTailPart()
        val tailStream = ByteArrayInputStream(output.toByteArray())
        streamList.add(tailStream)

        return MultiInputStream(streamList)
    }


    override var inputStream: InputStream?
        get() {
            return if (useStream()) {
                createMultiStream()
            } else {
                null
            }
        }
        set(_) {
            throw IllegalAccessException("Please use [fun addPartData(partData: PartData)] to set body")
        }

    override var body: ByteArray?
        get() {
            return if (useBody()) {
                val output = ByteArrayOutputStream()

                for (partData in partDataList) {
                    output.writeHeaderPart(partData)

                    // Write part data content
                    output.write(partData.content!!)
                    output.write("\r\n".toByteArray())
                }

                output.writeTailPart()

                output.toByteArray()
            } else {
                null
            }
        }
        set(_) {
            throw IllegalAccessException("Please use [fun addPartData(partData: PartData)] to set body")
        }
}
