package com.example.alexeyglushkov.cachemanager.disk.serializer

import com.example.alexeyglushkov.cachemanager.disk.DiskStorageMetadata
import com.example.alexeyglushkov.streamlib.codecs.SimpleCodec

/**
 * Created by alexeyglushkov on 18.09.16.
 */
class DiskMetadataCodec : SimpleCodec<DiskStorageMetadata>(DiskMetadataWriter(), DiskMetadataReader())