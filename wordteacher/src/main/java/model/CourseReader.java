package model;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;

import java.io.InputStream;

/**
 * Created by alexeyglushkov on 08.05.16.
 */
public class CourseReader implements InputStreamReader {
    @Override
    public Object readStream(InputStream data) {
        return null;
    }

    @Override
    public void setProgressUpdater(ProgressUpdater progressUpdater) {

    }

    @Override
    public Error getError() {
        return null;
    }
}
