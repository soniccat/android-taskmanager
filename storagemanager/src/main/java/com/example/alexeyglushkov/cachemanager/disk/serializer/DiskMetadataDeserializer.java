package com.example.alexeyglushkov.cachemanager.disk.serializer;

import com.example.alexeyglushkov.cachemanager.disk.DiskStorageMetadata;
import com.example.alexeyglushkov.jacksonlib.CustomDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by alexeyglushkov on 18.09.16.
 */
public class DiskMetadataDeserializer extends CustomDeserializer<DiskStorageMetadata> {

    public DiskMetadataDeserializer(Class<DiskStorageMetadata> vc) {
        super(vc);
    }

    @Override
    protected DiskStorageMetadata createObject() {
        return new DiskStorageMetadata();
    }

    @Override
    protected boolean handle(JsonParser p, DeserializationContext ctxt, DiskStorageMetadata metadata) throws IOException {
        String name = p.getCurrentName();
        boolean isHandled = false;

        switch (name) {
            case "contentSize":
                long contentSize = _parseLong(p, ctxt);
                metadata.setContentSize(contentSize);
                isHandled = true;
                break;

            case "createTime":
                long createTime = _parseLong(p, ctxt);
                metadata.setCreateTime(createTime);
                isHandled = true;
                break;

            case "expireTime":
                long expireTime = _parseLong(p, ctxt);
                metadata.setExpireTime(expireTime);
                isHandled = true;
                break;

            case "entryClass":
                String className = _parseString(p, ctxt);
                try {
                    metadata.setEntryClass(Class.forName(className));

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                isHandled = true;
                break;
        }

        return isHandled;
    }
}
