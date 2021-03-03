package com.example.sdktest;

import java.io.IOException;

public class App 
{
    public static void main( String[] args )  throws IOException
    {
        RecipeGenerator recipeGenerator = new RecipeGenerator();
        DynamoDBApp dynamoDBApp = new DynamoDBApp();

        System.out.printf("%n");
        System.out.println("Starting...");
        System.out.printf("%n");

        recipeGenerator.init(dynamoDBApp);

        dynamoDBApp.scanTable("recipes");
        dynamoDBApp.putRecipe("recipes", recipeGenerator.randomRecipe());
        dynamoDBApp.finishUp();
        
        System.out.printf("%n");
    }
}
