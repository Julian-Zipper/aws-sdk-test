package com.example.SDKTest;

import java.util.List;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;

/**
 * App that interacts with the Recipes Table in DynamoDB
 * trying out a bunch of interactions from https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-dynamodb.html
 * 
 */
public class DynamoDBApp {
    
    Region dynamoDBRegion;
    DynamoDbClient dynamoDBClient;

    public DynamoDBApp() {
        this.dynamoDBRegion = Region.EU_WEST_1;
        this.dynamoDBClient = DynamoDbClient.builder()
            .region(this.dynamoDBRegion)
            .build();

        init();
    }

    public void init() {
        String tableName = "recipes";

        getTableInfo(tableName);
        finishUp();
    }

    /**
     * Retrieves Table info from DynamoDB and prints it out to console
     */
    public void getTableInfo(String tableName) {
        try {
            DescribeTableRequest request = DescribeTableRequest.builder()
                .tableName(tableName)
                .build();
            
            TableDescription tableInfo = this.dynamoDBClient.describeTable(request).table();

            if (tableInfo != null) {
                printTableInfo(tableInfo);
            }
        }
        catch (DynamoDbException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    /**
     * Prints Table Info in a console readable format
     */
    public void printTableInfo(TableDescription tableInfo) {
        System.out.format("Table name : %s\n", tableInfo.tableName());
        System.out.format("Table ARN : %s\n", tableInfo.tableArn());
        System.out.format("Status : %s\n", tableInfo.tableStatus());
        System.out.format("Item count : %d\n", tableInfo.itemCount().longValue());
        System.out.format("Size (bytes) : %d\n", tableInfo.tableSizeBytes().longValue());

        List<AttributeDefinition> attributes = tableInfo.attributeDefinitions();
        System.out.println("Attributes:");

        for (AttributeDefinition a: attributes) {
            System.out.format("  %s (%s)\n", a.attributeName(), a.attributeType());
        }
    }
    
    /**
     * Finishing steps of this app, before shutting off
     */
    public void finishUp() {
        System.out.println("Closing the connection to Amazon DynamoDB");
        this.dynamoDBClient.close();
        System.out.println("Connection closed");
        System.out.println("Exiting...");
    }
}
