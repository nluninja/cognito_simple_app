package nluninja.aws.cognito;

import org.json.JSONObject;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClientBuilder;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;


public class CognitoClient {

    private String POOL_ID;
    private String CLIENTAPP_ID;
    private String CLIENT_SECRET;
    private String FED_POOL_ID;
    private String CUSTOMDOMAIN;
    private String REGION;

    final public static String REDIRECT_URL = "https://developer.expert.ai";

    public CognitoClient() {

        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = getClass().getClassLoader().getResourceAsStream("config.properties");

            // load a properties file
            prop.load(input);

            // Read the property values
            POOL_ID = prop.getProperty("POOL_ID");
            CLIENTAPP_ID = prop.getProperty("CLIENTAPP_ID");
            CLIENT_SECRET = prop.getProperty("CLIENT_SECRET");
            FED_POOL_ID = prop.getProperty("FED_POOL_ID");
            CUSTOMDOMAIN = prop.getProperty("CUSTOMDOMAIN");
            REGION = prop.getProperty("REGION");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    String GetHostedSignInURL() {
        String customurl = "https://%s.auth.%s.amazoncognito.com/login?response_type=code&client_id=%s&redirect_uri=%s";

        return String.format(customurl, CUSTOMDOMAIN, REGION, CLIENTAPP_ID, CognitoClient.REDIRECT_URL);
    }


     public static void main(String[] args) {
         Scanner scanner = new Scanner(System.in);

         CognitoClient client = new CognitoClient();
         String username, password, email, phonenumber = "";

         System.out.println("Welcome to the Cognito Simple App. Please enter your choice (1 or 2).\n" +
                 "1. Add a new user\n" +
                 "2. Authenticate a user and display its buckets\n" +
                 "");
         int choice = 0;

         try {
             choice = Integer.parseInt(scanner.nextLine());
         } catch (NumberFormatException exp) {
             System.out.println("Please enter a choice (1, 2).");
             System.exit(1);
         }
         switch (choice) {
             case 1:

                 System.out.println("Please enter a username: ");
                 username = scanner.nextLine();

                 System.out.println("Please enter a password: ");
                 password = scanner.nextLine();

                 System.out.println("Please enter an email: ");
                 email = scanner.nextLine();

                 System.out.println("Please enter a phone number (+11234567890): ");
                 phonenumber = scanner.nextLine();

                 boolean success = client.signup(username, password, email, phonenumber);
                 if (success) {
                     System.out.println("User added.");
                     System.out.println("Enter your validation code on phone: ");

                     String code = scanner.nextLine();
                     client.checkAccessCode(username, code);
                     System.out.println("User verification succeeded.");
                 } else {
                     System.out.println("User creation failed.");
                 }
                 break;
             case 2:
                 System.out.println("Please enter the username: ");
                 username = scanner.nextLine();
                 System.out.println("Please enter the password: ");
                 password = scanner.nextLine();
                 String result = client.validate(username, password);
                 if (result != null) {
                     System.out.println("User is authenticated: " + result);
                 } else {
                     System.out.println("Username/password is invalid.");
                     System.exit(1);
                 }
                 JSONObject payload = JWTUtils.getPayload(result);

                 String provider = payload.get("iss").toString().replace("https://", "");

                 //Credentials credentials = helper.GetCredentials(provider, result);
                 //  ListBuckets(credentials);
                 break;
             default:
                 System.out.println("Valid choices are 1 and 2");
         }





     }

    String validate(String username, String password) {
        AuthenticationManager helper = new AuthenticationManager(POOL_ID, CLIENTAPP_ID, CLIENT_SECRET);
        return helper.PerformSRPAuthentication(username, password);
    }

    boolean signup(String username, String password, String email, String phonenumber) {
        CognitoIdentityProviderClientBuilder builder =  CognitoIdentityProviderClient.builder();
        builder.region(Region.of(REGION));
        builder.credentialsProvider(AnonymousCredentialsProvider.create());

        CognitoIdentityProviderClient cognitoIdentityProvider = builder.build();

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setClientId(CLIENTAPP_ID);
        signUpRequest.setUsername(username);
        signUpRequest.setPassword(password);
        List<AttributeType> list = new ArrayList<>();

        AttributeType attributeType = new AttributeType();
        attributeType.setName("phone_number");
        attributeType.setValue(phonenumber);
        list.add(attributeType);

        AttributeType attributeType1 = new AttributeType();
        attributeType1.setName("email");
        attributeType1.setValue(email);
        list.add(attributeType1);

        signUpRequest.setUserAttributes(list);

        try {
            SignUpResult result = cognitoIdentityProvider.signUp(signUpRequest);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    boolean checkAccessCode(String username, String code) {
        CognitoIdentityProviderClientBuilder builder =  CognitoIdentityProviderClient.builder();
        builder.region(Region.of(REGION));
        builder.credentialsProvider(AnonymousCredentialsProvider.create());

        CognitoIdentityProviderClient cognitoIdentityProvider = builder.build();

        ConfirmSignUpRequest confirmSignUpRequest = new ConfirmSignUpRequest();
        confirmSignUpRequest.setUsername(username);
        confirmSignUpRequest.setConfirmationCode(code);
        confirmSignUpRequest.setClientId(CLIENTAPP_ID);

        System.out.println("username=" + username);
        System.out.println("code=" + code);
        System.out.println("clientid=" + CLIENTAPP_ID);

        try {
            ConfirmSignUpResult confirmSignUpResult = cognitoIdentityProvider.confirmSignUp(confirmSignUpRequest);
            System.out.println("confirmSignupResult=" + confirmSignUpResult.toString());
        } catch (Exception ex) {
            System.out.println(ex);
            return false;
        }
        return true;
    }

}
