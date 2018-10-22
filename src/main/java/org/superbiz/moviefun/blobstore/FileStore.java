package org.superbiz.moviefun.blobstore;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

public class FileStore implements BlobStore {

    @Override
    public void put(Blob blob) throws IOException {

        File file = new File(blob.name);
        file.delete();
        file.getParentFile().mkdirs();
        file.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(blob.name)) {
            outputStream.write(IOUtils.toByteArray(blob.inputStream));
        }

    }

    @Override
    public Optional<Blob> get(String name) throws IOException {

        Blob blob ;
        FileInputStream fileInputStream;

        File file = new File(name);
        if (file.exists()) {
            fileInputStream = new FileInputStream(file);

            blob = new Blob(name, fileInputStream, new Tika().detect(file));
            return Optional.of(blob);
        } else {
            return Optional.empty();
        }

    }

    @Override
    public void deleteAll() {
        // ...
    }
}