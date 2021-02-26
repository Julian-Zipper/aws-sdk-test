package com.example.sdktest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

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
    int itemCount;

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
        generateRecipe();
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
        System.out.format("Size (bytes) : %d\n", tableInfo.tableSizeBytes().longValue());
        System.out.format("Item count : %d\n", tableInfo.itemCount().longValue());

        this.itemCount = tableInfo.itemCount().intValue();

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

    public void generateRecipe() {
        List<String> adjectives = Arrays.asList("Delicious", "Funky", "Must-have", "Glazed", "Roasted", "Oven-baked", "Quick&dirty", "Easy-peasy", "Healthy", "Pickled", "Supreme");
        List<String> postModifiers = Arrays.asList("a la chef", "burger", "salad", "poke bowl", "stir-fry", "dip", "stew", "casserole", "sauce", "breakfest", "dinner", "dessert", "rolls", "sandwhich");
        List<String> carbs = Arrays.asList("potatoes", "rice", "pasta", "quinoa", "toast", "slices of bread", "flour");
        List<String> vegetables = Arrays.asList("tomatoes", "carrots", "onions", "mushrooms", "garlic", "celery", "corn", "cauliflower", "broccoli", "peas", "sugar snaps", "bell peppers", "chili pepper", "avocado");
        List<String> proteins = Arrays.asList("chicken", "beef", "tofu", "tempeh", "beans", "halloumi", "mozzerella");
        List<String> additions = Arrays.asList( "feta cheese", "parmesan cheese", "cashews", "pine nuts", "thyme", "basil", "honey", "tomato paste", "soy sauce", "lemon juice", "mayo", "creme fraiche", "balsamic vinegar", "cumin", "parsley", "turmeric");

        List<String> ingredients = new ArrayList<String>();
        ingredients.add(getRandom(carbs));
        ingredients.addAll(getRandom(vegetables, 3));
        ingredients.addAll(getRandom(proteins, 2));
        String mainIngredient = getRandom(ingredients);
        ingredients.addAll(getRandom(additions, 4));

        String recipeName = String.format("%s %s %s", getRandom(adjectives), mainIngredient, getRandom(postModifiers));
        int recipeId = this.itemCount + 1;

        System.out.println("Generating new recipe...");
        System.out.format("recipe id (%s)\n", recipeId);
        System.out.format("recipe name (%s)\n", recipeName);
        System.out.format("ingredients (%s)\n", ingredients.toString());
        System.out.printf("%n");

    }

    public List<String> getRandom(List<String> list, int max) {
        Collections.shuffle(list);
        int num = (int) (1 + (Math.random() * (max - 1)));
        return list.subList(0, num);
    }

    public String getRandom(List<String> list) {
        return list.get((int)(Math.random() * list.size()));
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
