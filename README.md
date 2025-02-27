### Running the code :sun_with_face:
1. `mvn package`
2. `mvn exec:java` (everytime you want to run the code)
3. or a oneliner: `mvn package exec:java`


### Checking to see if everything compiles
1. `mvn clean validate`
2. `mvn compile`
3. or a oneliner: `mvn clean validate compile`

### initial setup :seedling:
1. To even run this code, AWS credentials are needed.
2. AWS Documentation on the subject: [link](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html)
3. for now, setting up a `[default]` credentials file in `~/.aws/credentials` seems to work fine. 
4. Dynamo Tables need to be set up in AWS. 5 read/write units + 25GB DynamoDB storage are [free-tier eligible](https://aws.amazon.com/dynamodb/pricing/)
5. Currently using a `recipes` table, and a `recipegeneration` table pre-populated with some keywords and categories.


### How to get VSCode intellisense to work properly
1. Close and Relaunch VSCode, to no avail
2. Question life itself
1. Close and Relaunch VSCode, to no avail (x4)
3. ?????
4. :sos:
5. `ctrl+p`, enter `>` then `reload`. select the reload windows option (`ctrl+r` doesn't work at all). This helps 99% of the time :octocat:

### Next steps
- ~~refactor recipe generation to own class~~
- ~~javadoc above every method~~
- ~~another table for populating the recipe generator with keywords (no longer use the hardcoded values)~~
- ~~update DynamoDBApp.java to handle multiple tables instead of just the recipes table~~
- add a local DynamoDB instance for running integration tests
- write some unit &/ integration tests
- another table for dinner weekplanning
- another table for grocery lists
- another table for ingredients
- connection/junction between recipes/weekplanning/groceries
- AWS trigger when a grocery list is uploaded
- send a mail from this trigger (SES, SNS)
- send a mail at the start of the week with the dinner weekplanning (or manually with the SDK for SES and a call in the code, or through the backend/API)

## Future steps :alien:
- backend/API to interact with the database, where calls can be made to Create/Update/Delete Recipes, ingredients, grocery lists, etc.
- frontend to interact with the backend/API, where you can create new Recipes, grocery lists, tweak the recipe generator, etc.

### Some relevant Dynamo info:
- Amazon DynamoDB documentation - Getting Started with Java and DynamoDB: [link](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/GettingStarted.Java.html)
- Enhanced DynamoDB client in the AWS SDK for Java v2: [link](https://aws.amazon.com/blogs/developer/introducing-enhanced-dynamodb-client-in-the-aws-sdk-for-java-v2/)
- Java Annotations for DynamoDB: [link](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBMapper.Annotations.html)
- AWS SDK examples for Enhanced DynamoDB client: [link](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/example_code/dynamodb/src/main/java/com/example/dynamodb)
- Running DynamoDB locally: [link](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html)

### Some relevant Spring info:
- DynamoDB in Spring Boot App using Spring Data: [link](https://www.baeldung.com/spring-data-dynamodb)
- Deploy Sprint Boot App to AWS ECS: [link](https://medium.com/swlh/build-deploy-a-rest-api-from-scratch-using-spring-boot-and-aws-ecs-eb369137a020)
- Auto deployment of Spring Boot App to ECS with jenkins: [link](https://medium.com/@KTree_Blog/auto-deployment-of-spring-boot-applications-to-aws-ecs-with-jenkins-3ef5245f5e7e)