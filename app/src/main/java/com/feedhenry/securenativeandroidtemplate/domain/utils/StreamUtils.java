package com.feedhenry.securenativeandroidtemplate.domain.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Some utility functions to perform IO stream related operations.
 */

public class StreamUtils {

    /**
     * Read UTF8 string from the given input steam.
     * @param in the input stream
     * @return the string value from the input stream
     * @throws IOException
     */
    public static String readStream(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int numberOfBytesRead;
        while ((numberOfBytesRead = in.read(b)) >= 0) {
            baos.write(b, 0, numberOfBytesRead);
        }
        String content = new String(baos.toByteArray(), "utf-8");
        return content;
    }

}
