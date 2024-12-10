package com.dlskawo0409.demo.common.storage.application;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface StorageService {

    void init();
    void store(MultipartFile file, String fileName);
    Path load(String filename);
    Object loadAsResource(String fileName, String url) throws IOException;

    //    void deleteAll();
    void deleteOne(String fileName);
}