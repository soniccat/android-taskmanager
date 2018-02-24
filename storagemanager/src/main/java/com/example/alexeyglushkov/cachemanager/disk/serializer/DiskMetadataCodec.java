package com.example.alexeyglushkov.cachemanager.disk.serializer;

import com.example.alexeyglushkov.streamlib.codecs.SimpleCodec;

/**
 * Created by alexeyglushkov on 18.09.16.
 */
public class DiskMetadataCodec extends SimpleCodec {
    public DiskMetadataCodec() {
        super(new DiskMetadataWriter(), new DiskMetadataReader());
    }
}
