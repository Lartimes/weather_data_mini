package com.lartimes.weather.common;

import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public class FileTypeChecker {

    public static boolean isTextFile(MultipartFile file) {
        Tika tika = new Tika();
        try (TikaInputStream stream = TikaInputStream.get(file.getInputStream())) {
            String mimeType = tika.detect(stream, new Metadata());
            // 假设我们认为text/plain和text/html是"普通文本文件"
            return mimeType.startsWith("text/");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
