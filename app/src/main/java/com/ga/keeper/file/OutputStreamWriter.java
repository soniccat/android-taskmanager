package com.ga.keeper.file;

import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 01.02.15.
 */
public interface OutputStreamWriter {
    Error writeToStream(OutputStream stream);
}
