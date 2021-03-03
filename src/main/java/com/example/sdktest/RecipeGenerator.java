package com.example.sdktest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RecipeGenerator {

    public static final String TABLE_NAME = "recipeGeneration";

    Map<String, List<String>> keywords;

    public RecipeGenerator() {
        this.keywords = new HashMap<>();
    }

    /**
     * Initializes the Recipe generator - loads necessary keyword from database
     * @param dynamoDBApp DynamoDBApp instance needed to populate keywords from database
     */
    public void init(DynamoDBApp dynamoDBApp) {
        System.out.println("Initializing Recipe Generator");
        this.populateWordLists(dynamoDBApp.getGenerationData(TABLE_NAME));
        System.out.println("Recipe Generator ready to cook up some recipes");
        System.out.printf("%n");
    }

    /**
     * Processes DynamoDB Scan result into keywords loaded into lists of strings
     * @param tableData result from DynamoDBClient scan on entire generationData table
     */
    public void populateWordLists(Iterator<GenerationData> tableData) {
        if(tableData != null) {
            System.out.println("Populating RecipeGenerator with keywords...");
            tableData.forEachRemaining((generationData) -> fillKeywords(generationData));
        }
        else {
            System.out.println("Something went wrong with retrieving the RecipeGeneration Table Data");
        }
    }

    /**
     * Processes one item from the DynamoDB recipeGeneration table into the Keyword Map.
     * Keyword map is then used for recipe generation
     * @param data item from recipeGeneration table. Contains a category, and a list of keywords
     */
    public void fillKeywords(GenerationData data) {
        keywords.put(data.getCategory(), data.getWords());
    }

    /**
     * Generates a recipe from random ingredients/keywords.
     */
    public Recipe randomRecipe() {
        System.out.println("Generating new recipe...");

        List<String> ingredients = new ArrayList<String>();
        ingredients.addAll(getRandom(this.keywords.get("vegetables"), 4));
        ingredients.addAll(getRandom(this.keywords.get("proteins"), 2));
        String recipeName = randomRecipeName(ingredients);
        ingredients.addAll(getRandom(this.keywords.get("additions"), 5));
        List<String> servingSuggestions = getRandom(this.keywords.get("alongsides"), 2);

        Recipe recipe = new Recipe(recipeName, ingredients, servingSuggestions);
        recipe.print();
        System.out.printf("%n");
        
        return recipe;
    }

    /**
     * recipe name is generated with vegetables/proteins in the ingredient list only.
     * Main ingredient will be feature in the name, and a main ingredient should always be a vegetable/protein.
     */
    public String randomRecipeName(List<String> ingredients) {
        String mainIngredient = getRandom(ingredients);
        String adjective = getRandom(this.keywords.get("adjectives"));
        String postModifier = getRandom(this.keywords.get("postModifiers"));
        String recipeName = String.format("%s %s %s", adjective, mainIngredient, postModifier);
        return recipeName;
    }

     /**
      * Randomly selects a range of strings from a list.
      * Range size is random as well, and it cannot exceed 'max' parameter.
      * @param max the maximum amount of Strings you'd want returned
      */
    public List<String> getRandom(List<String> list, int max) {
        Collections.shuffle(list);
        int num = (int) (1 + (Math.random() * (max - 1)));
        return list.subList(0, num);
    }

    /**
     * Used for obtaining a random String from a list
     */
    public String getRandom(List<String> list) {
        return list.get((int)(Math.random() * list.size()));
    }
}