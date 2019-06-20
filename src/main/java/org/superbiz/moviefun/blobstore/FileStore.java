package org.superbiz.moviefun.blobstore;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.ByteLookupTable;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;

public class FileStore implements BlobStore {

    @Override
    public void put(Blob blob) throws IOException {
        File file = new File(blob.getName());

        file.delete();
        file.getParentFile().mkdirs();
        file.createNewFile();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(blob.getInputStream(), out);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(out.toByteArray());
        }
    }

    @Override
    public Optional<Blob> get(String name) throws IOException, URISyntaxException {
        Path coverFilePath = null;
        try{
            coverFilePath = getExistingCoverPath(name);
        }catch(URISyntaxException e){
            return Optional.empty();
        }
        byte[] imageBytes = readAllBytes(coverFilePath);
        String contentType = new Tika().detect(coverFilePath);
        Blob blob = new Blob(name, new ByteArrayInputStream(imageBytes), contentType);
        return Optional.of(blob);
    }

    @Override
    public void deleteAll() {

    }

    private Path getExistingCoverPath(String name) throws URISyntaxException, IOException {
        File coverFile = new File(name);
        Path coverFilePath;

        if (coverFile.exists()) {
            coverFilePath = coverFile.toPath();
        } else {
            coverFilePath = Paths.get(getSystemResource("default-cover.jpg").toURI());
        }

        return coverFilePath;
    }

}