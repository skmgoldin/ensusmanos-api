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

  final AttachedResources attachedResources;
  final String SYSTEM_KEY;

  public Auth(AttachedResources attachedResources) {
    this.attachedResources = attachedResources;
    SYSTEM_KEY = fetchSystemKey();
  }

  protected String loginUser(String username, String secret) throws AuthException {
    DynamoDbClient userDb = DynamoDbClient.create();

    // Hash the received secret
    String secretHash = Hashing.sha256().hashString(secret, StandardCharsets.UTF_8).toString();

    // Create a request to get the user record from the database
    GetItemRequest userRecordRequest =
        GetItemRequest.builder()
            .tableName(attachedResources.getCanonicalName("USERS_DB"))
            .key(Map.of("user", AttributeValue.builder().s(username).build()))
            .build();

    // Send the record request and parse out the stored secretHash for the user
    String storedSecretHash = userDb.getItem(userRecordRequest).item().get("secretHash").s();

    String token = null;
    if (secretHash.equals(storedSecretHash)) {
      // Only Jo-Ann should be an admin
      boolean isJoAnn = username.equals("joann.arosemena@gmail.com") ? true : false;

      Algorithm algorithm = Algorithm.HMAC256(SYSTEM_KEY);
      token =
          JWT.create()
              .withIssuer("jonet")
              .withIssuedAt(new Date())
              .withClaim("isAdmin", isJoAnn)
              .sign(algorithm);
    } else {
      throw new AuthException(
          "Provided password did not match that " + "stored for user " + username);
    }

    return token;
  }

  // The system key is used for signing and verifying user JWTs.
  String fetchSystemKey() {
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
