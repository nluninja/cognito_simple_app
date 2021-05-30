# A simple Amazon Cognito Client (WIP)

Amazon Cognito is Amazon Web Services’ service for managing user authentication and access control.

## Getting Started
### Sign up for AWS
Before you begin, you need an AWS account and you have to setup a Cognito service to play with it.

### Minimum requirements
To run the Amazon client you will need Java 11. 

### Dependencies  
Check the POM dependencies in the maven configuration.

        <aws.sdk.version>2.16.39</aws.sdk.version>
        <slf4j.version>2.0.0-alpha1</slf4j.version>
        <json.version>20210307</json.version>

## Configure the app
The app looks for the Cognito configuration in the `src/main/resources/config.properties`

<pre>
POOL_ID= the identity pool identifier
CLIENTAPP_ID= the id for the client app that uses Cognito service
CLIENT_SECRET= the secret string for the client
FED_POOL_ID= the federated pool id
REGION= the region where Cognito is configured
</pre>

## Run


