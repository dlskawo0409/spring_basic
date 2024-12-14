package com.dlskawo0409.demo.common.storage.application;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface StorageService {

    String store(MultipartFile file, String fileName) throws IOException;
    String getPreSignedUrl(String filename);

    //    void deleteAll();
    void deleteOne(String fileName);
}