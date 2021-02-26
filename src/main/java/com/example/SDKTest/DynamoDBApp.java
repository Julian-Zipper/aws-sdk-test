package com.example.sdktest;

import java.util.Iterator;
import java.util.List;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
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
    DynamoDbEnhancedClient dynamoDBClientEnhanced;

    public DynamoDBApp() {
        this.dynamoDBRegion = Region.EU_WEST_1;

        this.dynamoDBClient = DynamoDbClient.builder()
            .region(this.dynamoDBRegion)
            .build();
        
        this.dynamoDBClientEnhanced = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(this.dynamoDBClient)
            .build();
        
        init();
    }

    public void init() {
        String tableName = "recipes";

        getTableInfo(tableName);
        scanTable(tableName);
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

        System.out.printf("%n");
    }

    /**
     * Scans table for items and prints them out to the console
     */
    public void scanTable(String tableName) {
        try {
            DynamoDbTable<Recipe> table = this.dynamoDBClientEnhanced.table(tableName, TableSchema.fromBean(Recipe.class));
            Iterator<Recipe> results = table.scan().items().iterator();
            printRecipes(results);

        } catch (DynamoDbException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    /**
     * Prints all Recipes in the scanned tabel to the console
     */
    public void printRecipes(Iterator<Recipe> recipes) {
        System.out.println("Recipes:");
        System.out.println("--------------------------");
        recipes.forEachRemaining((recipe) -> printRecipe(recipe));
        System.out.printf("%n");
    }

    /**
     * Prints recipe fields in a console readable format
     */
    public void printRecipe(Recipe recipe) {
        System.out.format("  id (%s)\n", recipe.getId());
        System.out.format("  name (%s)\n", recipe.getName());
        System.out.format("  ingredients (%s)\n", recipe.getIngredients().toString());
        System.out.println("--------------------------");
    }


    public void putData() {

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
