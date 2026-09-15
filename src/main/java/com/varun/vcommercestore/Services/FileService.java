package com.varun.vcommercestore.Services;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Service
public interface FileService {
    String uploadFile(MultipartFile file,String path) throws IOException;

    InputStream getResource(String path,String name) throws FileNotFoundException;


}
