package com.example.sdktest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RecipeGenerator {

    List<String> adjectives;
    List<String> postModifiers;
    List<String> vegetables;
    List<String> proteins;
    List<String> additions;
    List<String> alongsides;


    public RecipeGenerator() {
        this.populateLists();
    }

    /**
     * Populates lists of strings with ingredients/keywords
     * TODO: save ingredients/keywords in its own DynamoDB Table, and extract them from there instead of using hardcoded values.
     */
    public void populateLists() {
        adjectives = Arrays.asList(
            "Delicious", "Funky", "Must-have", "Finger-lickin", "Simply the best", "Glazed", "Roasted", "Oven-baked", "Quick&dirty",
            "Easy-peasy", "Healthy", "Pickled", "Supreme"
        );

        postModifiers = Arrays.asList(
            "a la chef", "burger", "salad", "poke bowl", "stir-fry", "soup", "stew", "casserole", "sauce","breakfest", "dinner",
            "rolls", "sandwich", "curry", "mash-up"
        );

        vegetables = Arrays.asList(
            "tomato", "potato", "carrot", "lettuce", "spinach", "kale", "onion", "leek", "mushroom", "garlic", "celery", "corn",
            "cauliflower", "broccoli", "peas", "sugar snap", "bell pepper", "chili pepper", "avocado"
        );

        proteins = Arrays.asList(
            "chicken", "beef", "tofu", "tempeh", "bean", "halloumi", "mozzerella"
        );

        additions = Arrays.asList(
            "feta cheese", "parmesan cheese", "flour", "cornstarch", "cashew", "pine nut", "thyme", "basil", "spring onion", "honey",
            "tomato paste", "soy sauce", "lemon juice", "mayo", "greek yoghurt", "creme fraiche", "balsamic vinegar", "cumin",
            "parsley", "turmeric", "MSG"
        );

        alongsides = Arrays.asList(
            "pasta", "quinoa", "rice", "bulgur", "fries", "sweet potato fries", "mashed potatoes", "toast", "naan", "fries", "chips",
            "tortillas", "bread", "flatbread", "salad", "dip", "tomato soup", "vegetable soup", "BBQ sauce", "ketchup", "sriracha"
        );
    }

    /**
     * Generates a recipe from random ingredients/keywords.
     */
    public Recipe randomRecipe() {
        System.out.println("Generating new recipe...");

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