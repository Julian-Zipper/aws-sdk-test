package com.example.sdktest;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
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
    DynamoDbTable<Recipe> table;

    public DynamoDBApp() {
        this.dynamoDBRegion = Region.EU_WEST_1;

        this.dynamoDBClient = DynamoDbClient.builder()
            .region(this.dynamoDBRegion)
            .build();
        
        this.dynamoDBClientEnhanced = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(this.dynamoDBClient)
            .build();
    }

    /**
     * Describes the behaviour of this class on loading up
     */
    public void init() {
        String tableName = "recipes";

        loadTable(tableName);
        getTableInfo(tableName);
        scanTable();
        generateRecipe();
        finishUp();
    }

    /**
     * Preload the DynamoDB Table since it will be used in multiple methods
     * TODO: when loading multiple tables (Recipes, Ingredients, Groceries, etc.) change this to returning a table instead of setting a field
     */
    public void loadTable(String tableName) {
        try {
            this.table = this.dynamoDBClientEnhanced.table(tableName, TableSchema.fromBean(Recipe.class));
        }
        catch (DynamoDbException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
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
        System.out.format("Size (bytes) : %d\n", tableInfo.tableSizeBytes().longValue());
        System.out.format("Item count : %d\n", tableInfo.itemCount().longValue());

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
    public void scanTable() {
        try {
            Iterator<Recipe> results = this.table.scan().items().iterator();
            printRecipes(results);

        }
        catch (DynamoDbException e) {
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
        recipes.forEachRemaining((recipe) -> recipe.print());
        System.out.printf("%n");
    }

    /**
     * Generates a recipe and puts it into the DynamoDB Table
     */
    public void generateRecipe() {
        RecipeGenerator generator = new RecipeGenerator();
        Recipe newRecipe = generator.randomRecipe();
        putTable(newRecipe);
    }

    /**
     * Places a recipe in the table
     */
    public void putTable(Recipe recipe) {
        try {
            this.table.putItem(recipe);
        }
        catch (DynamoDbException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    /**
     * A one-time-use method for updating pre-existing table items with a new field + value(s) for that field
     * This method is not used anymore, but kept just to showcase how to retrieve & update certain DynamoDB items.
     */
    public void updateItemsWithServingSuggestions(String tableName) {
        try {
            DynamoDbTable<Recipe> table = this.dynamoDBClientEnhanced.table(tableName, TableSchema.fromBean(Recipe.class));

            List<List<String>> additionalInfo = Arrays.asList(
                Arrays.asList("pasta"),
                Arrays.asList("flatbread", "sweet potato fries"),
                Arrays.asList("toast"),
                Arrays.asList("ketchup", "tomato soup")
            );

            for (int i = 0; i < 4; i++) {
                Key key = Key.builder()
                    .partitionValue(i + 1)
                    .build();
                
                Recipe recipe = table.getItem(key);
                recipe.setServingSuggestions(additionalInfo.get(i));
                table.updateItem(recipe);
            }
        }
        catch (DynamoDbException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
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
