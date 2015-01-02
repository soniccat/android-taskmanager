package com.ga.keeper.data;

import org.apache.http.util.ByteArrayBuffer;

import com.ga.task.DataFormat;

public interface DataProvider {
    ByteArrayBuffer getData();
}
