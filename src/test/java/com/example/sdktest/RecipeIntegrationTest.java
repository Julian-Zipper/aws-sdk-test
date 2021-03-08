package com.example.sdktest;

import org.junit.ClassRule;

public class RecipeIntegrationTest {

    @ClassRule
    public static LocalDbCreationRule dynamoDB = new LocalDbCreationRule();
    
}
