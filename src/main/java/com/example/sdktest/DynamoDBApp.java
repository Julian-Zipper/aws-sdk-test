package com.example.sdktest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

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
        
        init();
    }

    public void init() {
        String tableName = "recipes";

        loadTable(tableName);

        getTableInfo(tableName);
        scanTable();
        Recipe newRecipe = generateRecipe();
        putTable(newRecipe);

        finishUp();
    }

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
        System.out.format("  serve with (%s)\n", recipe.getSuggestionsPrintable());
        System.out.println("--------------------------");
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
     * Generates a recipe from random ingredients/keywords.
     * TODO: extract to own class
     */
    public Recipe generateRecipe() {
        System.out.println("Generating new recipe...");

        List<String> adjectives = Arrays.asList("Delicious", "Funky", "Must-have", "Finger-lickin", "Simply the best", "Glazed", "Roasted", "Oven-baked", "Quick&dirty", "Easy-peasy", "Healthy", "Pickled", "Supreme");
        List<String> postModifiers = Arrays.asList("a la chef", "burger", "salad", "poke bowl", "stir-fry", "soup", "stew", "casserole", "sauce", "breakfest", "dinner", "rolls", "sandwich", "curry", "mash-up");
        List<String> vegetables = Arrays.asList("tomato", "potato", "carrot", "lettuce", "spinach", "kale", "onion", "leek", "mushroom", "garlic", "celery", "corn", "cauliflower", "broccoli", "peas", "sugar snap", "bell pepper", "chili pepper", "avocado");
        List<String> proteins = Arrays.asList("chicken", "beef", "tofu", "tempeh", "bean", "halloumi", "mozzerella");
        List<String> additions = Arrays.asList("feta cheese", "parmesan cheese", "flour", "cornstarch", "cashew", "pine nut", "thyme", "basil", "spring onion", "honey", "tomato paste", "soy sauce", "lemon juice", "mayo", "greek yoghurt", "creme fraiche", "balsamic vinegar", "cumin", "parsley", "turmeric", "MSG");
        List<String> alongsides = Arrays.asList("pasta", "quinoa", "rice", "bulgur", "fries", "sweet potato fries", "mashed potatoes", "toast", "naan", "fries", "chips", "tortillas", "bread", "flatbread", "salad", "dip", "tomato soup", "vegetable soup", "BBQ sauce", "ketchup", "sriracha");
        
        List<String> ingredients = new ArrayList<String>();
        ingredients.addAll(getRandom(vegetables, 4));
        ingredients.addAll(getRandom(proteins, 2));
        String mainIngredient = getRandom(ingredients);
        ingredients.addAll(getRandom(additions, 5));

        String recipeName = String.format("%s %s %s", getRandom(adjectives), mainIngredient, getRandom(postModifiers));
        List<String> servingSuggestions = getRandom(alongsides, 2);

        Recipe recipe = new Recipe(recipeName, ingredients, servingSuggestions);

        System.out.format("recipe id (%s)\n", recipe.getId());
        System.out.format("recipe name (%s)\n", recipe.getName());
        System.out.format("ingredients (%s)\n", recipe.getIngredients().toString());
        System.out.format("Serve with (%s)\n", recipe.getSuggestionsPrintable());
        System.out.printf("%n");

        return recipe;
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
