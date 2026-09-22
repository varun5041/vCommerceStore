package com.varun.vcommercestore.Services.Impls;

import com.varun.vcommercestore.Exceptions.InvalidFileTypeException;
import com.varun.vcommercestore.Services.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String uploadFile(MultipartFile file, String path) throws IOException {
        String OriginalFileName = file.getOriginalFilename();
        logger.info("Uploading File... ");
        logger.info("Filename : {}",OriginalFileName);

        String randomFilename = UUID.randomUUID().toString();
        String extension = OriginalFileName.substring(OriginalFileName.lastIndexOf("."));
        String randomFilenameWithExtension = randomFilename+extension;

        String randomFilenameWithExtensionAndPath=path+ File.separator+randomFilenameWithExtension;

        if(extension.equalsIgnoreCase(".png") || extension.equalsIgnoreCase(".jpg") || extension.equalsIgnoreCase(".jpeg") || extension.equalsIgnoreCase(".gif")){
            File folder = new File(path);
            if(!folder.exists()){
                folder.mkdirs();
            }

            Files.copy(file.getInputStream(), Paths.get(randomFilenameWithExtensionAndPath));

            return randomFilenameWithExtension;

        }else{
            throw new InvalidFileTypeException("File Type should be jpg,.jpeg,.png,.gif");
        }

    }

    @Override
    public InputStream getResource(String path, String name) throws FileNotFoundException {
        String fullpath = path+File.separator+name;

        InputStream inputStream = new FileInputStream(fullpath);

        return inputStream;
    }

    @Override
    public void deleteFile(String name, String path) throws IOException {
        String fullpath = path+File.separator+name;
        Path path1 = Paths.get(fullpath);
        if (Files.exists(path1)) {
            Files.delete(path1);
        }
    }

}
