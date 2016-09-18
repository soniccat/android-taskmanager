package com.example.alexeyglushkov.cachemanager.disk.serializer;

import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;
import com.example.alexeyglushkov.streamlib.readersandwriters.OutputStreamWriter;
import com.example.alexeyglushkov.streamlib.serializers.Serializer;
import com.example.alexeyglushkov.streamlib.serializers.SimpleSerializer;

/**
 * Created by alexeyglushkov on 18.09.16.
 */
public class DiskMetadataSerializer extends SimpleSerializer {
    public DiskMetadataSerializer() {
        super(new DiskMetadataWriter(), new DiskMetadataReader());
    }
}
