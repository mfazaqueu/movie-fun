package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

public class S3Store implements BlobStore{
    private final AmazonS3Client s3Client;
    private final String photoStorageBucket;

    public S3Store(AmazonS3Client s3Client, String photoStorageBucket){
        this.s3Client = s3Client;
        this.photoStorageBucket = photoStorageBucket;
    }


    @Override
    public void put(Blob blob) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(blob.getContentType());

        try{
            s3Client.putObject(photoStorageBucket, blob.getName(), blob.getInputStream(), objectMetadata);
        }catch(Exception e){
            throw e;
        }
    }

    @Override
    public Optional<Blob> get(String name) throws IOException, URISyntaxException {
        S3Object s3Object = null;
        try{
            s3Object = s3Client.getObject(photoStorageBucket, name);
        }catch(Exception e){
            throw e;
            //return Object.empty();
        }
        Blob blob = new Blob(s3Object.getKey(), s3Object.getObjectContent(), s3Object.getObjectMetadata().getContentType());
        return Optional.of(blob);
    }

    @Override
    public void deleteAll() {

    }
}
