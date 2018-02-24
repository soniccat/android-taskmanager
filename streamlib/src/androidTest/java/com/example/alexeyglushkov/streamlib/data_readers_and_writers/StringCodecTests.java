package com.example.alexeyglushkov.streamlib.data_readers_and_writers;

import com.example.alexeyglushkov.streamlib.codecs.StringCodec;
import com.example.alexeyglushkov.streamlib.convertors.Converter;
import com.example.alexeyglushkov.streamlib.handlers.StringHandler;

import junit.framework.Assert;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by alexeyglushkov on 24.02.18.
 */

public class StringCodecTests {

    class Test {
        private int a;
        private int b;

        public Test(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }

        @Override
        public int hashCode() {
            return a + b;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Test) {
                Test testObj = (Test)obj;
                return a == testObj.getA() && b == testObj.getB();
            }

            return false;
        }
    }

    @org.junit.Test
    public void testReadAndWrite() throws Exception {
        // Arrange
        StringCodec codec = new StringCodec(new Converter() {
            @Override
            public Object convert(Object object) {
                Test testObj = (Test)object;
                return "#" + testObj.getA() + "#" + testObj.getB() + "#";
            }

        }, new StringHandler() {
            @Override
            public Object handleString(String data) {
                String[] values = data.split("#");
                int a = Integer.parseInt(values[1]);
                int b = Integer.parseInt(values[2]);

                Test testObj = new Test(a, b);
                return testObj;
            }

            @Override
            public Object convert(Object object) {
                return handleString((String)object);
            }
        });

        Test testObj = new Test(1312,45432);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Act
        OutputStreamDataWriters.writeOnce(codec, outputStream, testObj);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toString().getBytes());

        Test readObj = (Test) InputStreamDataReaders.readOnce(codec, inputStream);

        // Verify
        Assert.assertEquals(testObj, readObj);
    }
}
