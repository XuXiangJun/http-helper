package com.github.xuxiangjun.http

import java.io.ByteArrayOutputStream
import java.util.*

class MultiPartRequest(
    url: String,
) : HttpRequest(url) {

    var boundary: String = ""
        set(value) {
            addHeader(HttpHeaders.ContentType, "multipart/form-data;boundary=$value")
            field = value
        }

    private val partDataList: MutableList<PartData> = mutableListOf()

    override var body: ByteArray?
        get() {
            val output = ByteArrayOutputStream()

            for (partData in partDataList) {
                // Write disposition header
                output.writeBytes("--$boundary\r\n".toByteArray())
                output.writeBytes(
                    "${HttpHeaders.ContentDisposition}: form-data;name=${partData.name}".toByteArray()
                )
                partData.fileName?.also {
                    output.writeBytes(";filename=$it".toByteArray())
                }
                output.writeBytes("\r\n".toByteArray())

                // Write part data headers
                partData.headers.forEach { (k, v) ->
                    output.writeBytes("$k: $v\r\n".toByteArray())
                }
                output.writeBytes("\r\n".toByteArray())


                // Write part data content
                output.writeBytes(partData.content)
                output.writeBytes("\r\n".toByteArray())
            }

            output.writeBytes("--$boundary--\r\n".toByteArray())

            return output.toByteArray()
        }
        set(_) {
            throw IllegalAccessException("Please use [fun addPartData(partData: PartData)] to set body")
        }

    init {
        boundary = UUID.randomUUID().toString().replace("-", "")
    }

    fun addPartData(partData: PartData) {
        partDataList.add(partData)
    }
}
