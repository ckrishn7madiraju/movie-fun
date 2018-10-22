package org.superbiz.moviefun.albums;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import org.superbiz.moviefun.blobstore.Blob;
import org.superbiz.moviefun.blobstore.BlobStore;

import java.io.IOException;
import java.util.Optional;

public class S3Store implements BlobStore {
    private String bucket;
    private AmazonS3Client s3Client;

    public S3Store(AmazonS3Client s3Client, String photoStorageBucket) {
        this.bucket = photoStorageBucket;
        this.s3Client = s3Client;
    }

    @Override
    public void put(Blob blob) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(blob.contentType);
        s3Client.putObject(bucket, blob.name, blob.inputStream, objectMetadata);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        if (!s3Client.doesObjectExist(bucket, name)) {
            return Optional.empty();
        }

        S3Object s3Object = s3Client.getObject(bucket, name);
        Blob blob = new Blob(name, s3Object.getObjectContent(), s3Object.getObjectMetadata().getContentType());
        return Optional.of(blob);
    }

    @Override
    public void deleteAll() {

    }
}
