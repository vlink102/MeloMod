package me.vlink102.melomod.util;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

    public static final String PREFIX = "stream2file";
    public static final String SUFFIX = ".";

    public static File stream2file (InputStream in, String ext) throws IOException {
        final File tempFile = File.createTempFile(PREFIX, SUFFIX + ext);
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }

}