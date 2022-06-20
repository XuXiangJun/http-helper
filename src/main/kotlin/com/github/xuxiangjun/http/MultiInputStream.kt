package com.github.xuxiangjun.http

import java.io.InputStream
import java.util.*

internal class MultiInputStream(
    parts: List<InputStream>
) : InputStream() {
    private val queue = LinkedList<InputStream>()
    private val list = mutableListOf<InputStream>()

    @Volatile
    private var currentStream: InputStream? = null

    init {
        queue.addAll(parts)
        list.addAll(queue)

        currentStream = queue.poll()
    }

    override fun read(): Int {
        if (currentStream == null) {
            return -1
        }

        var read = currentStream!!.read()
        if (read == -1) {
            currentStream = queue.poll()
            if (currentStream != null) {
                read = currentStream!!.read()
            }
        }

        return read
    }

    override fun close() {
        for (stream in list) {
            try {
                stream.close()
            } catch (ignore: Exception) {
            }
        }
    }
}
