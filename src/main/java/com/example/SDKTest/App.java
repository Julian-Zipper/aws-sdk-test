package com.example.SDKTest;

import java.io.IOException;
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
public class App 
{
    public static void main( String[] args ) throws IOException
    {
        Region region = Region.EU_WEST_1;
        S3Client s3 = S3Client.builder().region(region).build();

        String bucketName = "bucket" + System.currentTimeMillis();
        String key = "key";

        createBucket(s3, bucketName, region);
        uploadObject(s3, bucketName, key);

        finishUp(s3);
    }

    /**
     * Create Bucket
     * Wait until the bucket is created
     * Print the bucket name
     */
    public static void createBucket(S3Client s3Client, String bucketName, Region region) {
        System.out.println("Creating Bucket...");
        try {
          // create bucket from a createBucketRequest
            CreateBucketConfiguration bucketConfig = CreateBucketConfiguration.builder()
                .locationConstraint(region.id())
                .build();
            
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                .bucket(bucketName)
                .createBucketConfiguration(bucketConfig)
                .build();

            s3Client.createBucket(bucketRequest);

            // Wait until the bucket is created and print out the response
            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                .bucket(bucketName)
                .build();
            
            s3Client.waiter().waitUntilBucketExists(bucketRequestWait);
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
    public static void uploadObject(S3Client s3Client, String bucketName, String key) {
        System.out.println("Uploading object...");

        PutObjectRequest uploadRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        
        s3Client.putObject(uploadRequest, RequestBody.fromString("Testing with the AWS SDK for Java"));

        System.out.println("Upload complete");
        System.out.printf("%n");
    }

    /**
     * Finishing steps of this app, before shutting off
     */
    public static void finishUp(S3Client s3Client) {
        System.out.println("Closing the connection to Amazon S3");
        s3Client.close();
        System.out.println("Connection closed");
        System.out.println("Exiting...");
    }
}
