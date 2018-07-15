package com.example.alexeyglushkov.streamlib.data_readers_and_writers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.alexeyglushkov.streamlib.handlers.StringHandler;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;

/**
 * Created by alexeyglushkov on 24.02.18.
 */

@RunWith(AndroidJUnit4.class)
public class StringReaderTests {

    private String testString = "It’s official, the Dropbox IPO filing is here.\n" +
            "\n" +
            "Going public is a huge milestone for Dropbox and has been one of the most anticipated tech IPOs for several years now. The cloud storage company has been around since 2007 and has raised more than $600 million in funding.\n" +
            "\n" +
            "We knew that it had already filed confidentially, but the company has now unveiled its filing, meaning the actual IPO is likely very soon, probably late March.\n" +
            "\n" +
            "The company says it will be targeting a $500 million fundraise, but this number is usually just a placeholder.\n" +
            "\n" +
            "The filing shows that Dropbox had $1.1 billion in revenue last year. This compares to $845 million in revenue the year before and $604 million for 2015.\n" +
            "\n" +
            "The company is not yet profitable, having lost nearly $112 million last year. This shows significantly improved margins when compared to losses of $210 million for 2016 and $326 million for 2015.";

    private StringReader<String> reader;

    @Before
    public void setUp() throws Exception {
        String path = getFilePath();
        File file = new File(path);

        FileWriter fos = new FileWriter(file);
        fos.write(testString);
        fos.close();
    }

    @After
    public void tearDown() throws Exception {
        String path = getFilePath();
        File file = new File(path);
        file.delete();
    }

    @Test
    public void testReadString() throws Exception {
        // Arrange
        reader = new StringReader<>(null);

        String path = getFilePath();
        File file = new File(path);

        FileWriter fos = new FileWriter(file);
        fos.write(testString);
        fos.close();

        // Act
        FileInputStream is = new FileInputStream(file);
        String readString = InputStreamDataReaders.readOnce(reader, is);

        // Verify
        Assert.assertEquals(testString, readString);
    }

    @NonNull
    private String getFilePath() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        File testDir = appContext.getDir("StringReaderTestsDir", Context.MODE_PRIVATE);
        return testDir + "/testReadString";
    }
}
