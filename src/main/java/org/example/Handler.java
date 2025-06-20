package org.example;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;


public class Handler {
    private final S3Client s3Client;

    public Handler() {
        s3Client = DependencyFactory.s3Client();
    }

    public void sendRequest() {
        String bucket = "bucket" + System.currentTimeMillis();
        String key = "key";

        createBucket(s3Client, bucket);

        System.out.println("Uploading object...");

        s3Client.putObject(PutObjectRequest
                        .builder()
                        .bucket(bucket)
                        .key(key)
                        .build(),
                RequestBody
                        .fromString("Testing with the {java-sdk}"));

        System.out.println("Upload complete");

        //cleanUp(s3Client, bucket, key);
        System.out.println("Closing the connection...");
        s3Client.close();
        System.out.println("Connection closed");
        System.out.println("Exiting");
    }

    private void cleanUp(S3Client s3Client, String bucket, String key) {
        System.out.println("Cleaning up...");
        try {
            System.out.println("Cleaning object...");
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest
                    .builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            System.out.println(key + " has been deleted.");
            System.out.println("Deleting bucket: " + bucket);
            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest
                    .builder()
                    .bucket(bucket)
                    .build();
            s3Client.deleteBucket(deleteBucketRequest);
            System.out.println(bucket + " has been deleted.");
            System.out.printf("%n");
        } catch (S3Exception e) {
            System.out.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Cleanup complete");
    }

    private void createBucket(S3Client s3Client, String bucketName) {
        try {
            s3Client
                    .createBucket(CreateBucketRequest
                            .builder()
                            .bucket(bucketName)
                            .build());
            System.out.println("Creating bucket " + bucketName);
            s3Client
                    .waiter()
                    .waitUntilBucketExists(HeadBucketRequest
                            .builder()
                            .bucket(bucketName)
                            .build());
            System.out.println(bucketName + " is ready");
        } catch (S3Exception e) {
            System.out.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
