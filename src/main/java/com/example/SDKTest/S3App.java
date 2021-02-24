package com.example.SDKTest;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * App that creates a bucket, adds an object, and removes the bucket again.
 * following this tutorial: https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html#get-started-setup-javamaven
 * 
 */
public class S3App {

    Region s3Region;
    S3Client s3Client;
    
    public S3App() {
        this.s3Region = Region.EU_WEST_1;
        this.s3Client = S3Client.builder()
            .region(this.s3Region)
            .build();

        init();
    }

    public void init() {
        String bucketName = "bucket" + System.currentTimeMillis();
        String key = "key" + System.currentTimeMillis();

        createBucket(bucketName);
        uploadObject(bucketName, key);
        finishUp();
    }

    
    /**
     * Create Bucket
     * Wait until the bucket is created
     * Print the bucket name
     */
    public void createBucket(String bucketName) {
        System.out.println("Creating Bucket...");
        try {
          // create bucket from a createBucketRequest
            CreateBucketConfiguration bucketConfig = CreateBucketConfiguration.builder()
                .locationConstraint(this.s3Region.id())
                .build();
            
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                .bucket(bucketName)
                .createBucketConfiguration(bucketConfig)
                .build();

            this.s3Client.createBucket(bucketRequest);

            // Wait until the bucket is created and print out the response
            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                .bucket(bucketName)
                .build();
            
            this.s3Client.waiter().waitUntilBucketExists(bucketRequestWait);
            System.out.println(String.format("New Bucket %s is ready", bucketName));
            System.out.printf("%n");
        }
        catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    /**
     * Put something in the (newly created) Bucket
     */
    public void uploadObject(String bucketName, String key) {
        System.out.println("Uploading object...");

        PutObjectRequest uploadRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        
        this.s3Client.putObject(uploadRequest, RequestBody.fromString("Testing with the AWS SDK for Java"));

        System.out.println("Upload complete");
        System.out.printf("%n");
    }

    /**
     * Finishing steps of this app, before shutting off
     */
    public void finishUp() {
        System.out.println("Closing the connection to Amazon S3");
        this.s3Client.close();
        System.out.println("Connection closed");
        System.out.println("Exiting...");
    }
}
