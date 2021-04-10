package nluninja.aws.cognito;

import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;


public class CognitoClient {

    private String POOL_ID;
    private String CLIENTAPP_ID;
    private String CLIENT_SECRET;
    private String FED_POOL_ID;
    private String CUSTOMDOMAIN;
    private String REGION;

    final public static String REDIRECT_URL = "https://sid343.reinvent-workshop.com";

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

         System.out.println("Cognito Sample Login App");

         System.out.println("Please enter the username: ");
         String username = scanner.nextLine();

         System.out.println("Please enter the password: ");
         String password = scanner.nextLine();

         String result = client.validate(username, password);
         if (result != null) {
             System.out.println("User is authenticated: " + result);
         } else {
             System.out.println("Username/password is invalid.");
         }

         JSONObject payload = JWTUtils.getPayload(result);
     //    String provider = payload.get("iss").toString().replace("https://", "");

        // Credentials credentials = helper.GetCredentials(provider, result);
       //  ListBuckets(credentials);

     }

    String validate(String username, String password) {
        AuthenticationManager helper = new AuthenticationManager(POOL_ID, CLIENTAPP_ID, CLIENT_SECRET);
        return helper.PerformSRPAuthentication(username, password);
    }
}
