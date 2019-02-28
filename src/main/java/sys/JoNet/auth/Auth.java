package sys.JoNet.auth;

import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.hash.Hashing;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.secretsmanager.*;
import software.amazon.awssdk.services.secretsmanager.model.*;
import sys.JoNet.utils.AttachedResources;

class Auth {

  private static final String[] resourceRefNames = {"SYSTEM_KEY", "USERS_DB"};
  private static final String appName = "jonet";
  private static final String env = System.getenv("JONET_ENV");
  private static final AttachedResources attachedResources =
      new AttachedResources(resourceRefNames, appName, env);
  private static String SYSTEM_KEY = fetchSystemKey();

  /**
   * Generate a JWT for a user.
   *
   * @param username the USERS_DB key which indexes the user's record
   * @param secret the user's unhashed password
   * @throws UserAuthException if the user provides bad credentials
   * @return a JSON web token which may be kept in browser local storage
   */
  public String generateUserToken(String username, String secret) throws UserAuthException {
    DynamoDbClient userDb = DynamoDbClient.create();
    String secretHash = Hashing.sha256().hashString(secret, StandardCharsets.UTF_8).toString();

    // Create and send a request to get the user record from the database
    GetItemRequest userRecordRequest =
        GetItemRequest.builder()
            .tableName(attachedResources.getCanonicalName("USERS_DB"))
            .key(Map.of("user", AttributeValue.builder().s(username).build()))
            .build();
    Map<String, AttributeValue> userRecord = userDb.getItem(userRecordRequest).item();

    // If the user provided a non-existant username, the record will be empty
    if (userRecord.isEmpty()) {
      throw new UserAuthException();
    }

    // Parse out the stored secretHash and make sure it matches that provided by the user
    String storedSecretHash = userRecord.get("secretHash").s();
    if (!secretHash.equals(storedSecretHash)) {
      throw new UserAuthException();
    }

    // If the user is an admin, make note of this so we can provide that claim in their JWT
    boolean isAdmin = false;
    if (userRecord.containsKey("isAdmin")) {
      isAdmin = userRecord.get("isAdmin").bool();
    }

    // Generate a JWT
    Algorithm algorithm = Algorithm.HMAC256(SYSTEM_KEY);
    String token =
        JWT.create()
            .withIssuer(appName)
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(new Date().getTime() + 604800000)) // Expires in seven days
            .withClaim("isAdmin", isAdmin)
            .sign(algorithm);

    return token;
  }

  /**
   * Authenticate a JWT presented by a user.
   *
   * @param token an encoded JWT
   * @return true if the token is valid, false if the token is in any way invalid.
   */
  public boolean authenticateUserToken(String token) {
    Algorithm algo = Algorithm.HMAC256(SYSTEM_KEY);
    JWTVerifier verifier = JWT.require(algo).withIssuer(appName).build();

    try {
      verifier.verify(token);
    } catch (Exception e) {
      return false;
    }

    return true;
  }

  // The system key is used for signing and verifying user JWTs.
  static String fetchSystemKey() {
    SecretsManagerClient client = SecretsManagerClient.builder().build();
    String keyCName = attachedResources.getCanonicalName("SYSTEM_KEY");

    // Create and send a request to get the secret value.
    GetSecretValueRequest request = GetSecretValueRequest.builder().secretId(keyCName).build();
    GetSecretValueResponse response = client.getSecretValue(request);

    // Return the secret as a string
    if (response.secretString() != null) {
      return response.secretString();
    } else {
      return new String(
          Base64.getDecoder().decode(response.secretBinary().asByteArray()),
          Charset.forName("UTF-8"));
    }
  }
}
