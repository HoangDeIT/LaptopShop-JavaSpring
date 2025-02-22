package com.project.LaptopShop.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
    @Value("${upload-file.base-uri}")
    private String baseUri;

    public void createUploadFolder(String folder) throws URISyntaxException {
        URI uri = new URI(baseUri + folder);
        // Path path = Paths.get(uri);
        // File tmpDir = new File(path.toString());
        File files = new File(uri);
        if (!files.isDirectory()) {
            try {
                Files.createDirectory(files.toPath());
            } catch (IOException e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    public String store(MultipartFile multipartFile, String folder) throws URISyntaxException, IOException {
        String finalName = System.currentTimeMillis() + "-"
                + URLEncoder.encode(multipartFile.getOriginalFilename(), "UTF-8");
        URI uri = new URI(baseUri + folder + "/" + finalName);
        Path path = Paths.get(uri);
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }
        return finalName;
    }

    public List<String> storeMultipleFiles(List<MultipartFile> multipartFiles, String folder)
            throws URISyntaxException, IOException {
        List<String> fileNames = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String finalName = System.currentTimeMillis() + "-"
                    + URLEncoder.encode(multipartFile.getOriginalFilename(), "UTF-8");
            URI uri = new URI(baseUri + folder + "/" + finalName);
            Path path = Paths.get(uri);
            try (InputStream inputStream = multipartFile.getInputStream()) {
                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
            }
            fileNames.add(finalName);
        }
        return fileNames;
    }
}
