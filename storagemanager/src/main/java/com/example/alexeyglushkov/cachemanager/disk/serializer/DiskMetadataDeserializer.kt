package com.example.alexeyglushkov.cachemanager.disk.serializer

import com.example.alexeyglushkov.cachemanager.disk.DiskStorageMetadata
import com.example.alexeyglushkov.jacksonlib.CustomDeserializer
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import java.io.IOException

/**
 * Created by alexeyglushkov on 18.09.16.
 */
class DiskMetadataDeserializer(vc: Class<DiskStorageMetadata>) : CustomDeserializer<DiskStorageMetadata>(vc) {
    override fun createObject(): DiskStorageMetadata {
        return DiskStorageMetadata()
    }

    @Throws(IOException::class)
    protected override fun handle(p: JsonParser, ctxt: DeserializationContext, metadata: DiskStorageMetadata): Boolean {
        val name = p.currentName
        var isHandled = false
        when (name) {
            "contentSize" -> {
                val contentSize = _parseLong(p, ctxt)
                metadata.contentSize = contentSize
                isHandled = true
            }
            "createTime" -> {
                val createTime = _parseLong(p, ctxt)
                metadata.createTime = createTime
                isHandled = true
            }
            "expireTime" -> {
                val expireTime = _parseLong(p, ctxt)
                metadata.expireTime = expireTime
                isHandled = true
            }
            "entryClass" -> {
                val className = _parseString(p, ctxt)
                try {
                    metadata.entryClass = Class.forName(className)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
                isHandled = true
            }
        }
        return isHandled
    }
}