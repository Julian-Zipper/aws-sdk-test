package com.example.sdktest;

import java.io.IOException;

public class App 
{
    public static void main( String[] args )  throws IOException
    {
        DynamoDBApp dynamoDBApp = new DynamoDBApp();
        dynamoDBApp.init();
    }
}
