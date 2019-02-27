package sys.JoNet.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.*;
import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.model.*;
import sys.JoNet.utils.AttachedResources;

class AuthTest {

  static final String testAdmin = "joann.arosemena@gmail.com";
  static final String testAdminPassword = "password123";
  static final String testAdminPasswordHash =
      Hashing.sha256().hashString(testAdminPassword, StandardCharsets.UTF_8).toString();
  static final Auth auth = new Auth();
  static final String SYSTEM_KEY = Auth.fetchSystemKey();
  static boolean initialized = false; // NOPMD

  public AuthTest() throws Exception {
    if (!initialized) {
      final String[] resourceRefNames = {"SYSTEM_KEY", "USERS_DB"};
      final String appName = "jonet";
      final String env = System.getenv("JONET_ENV");
      final AttachedResources attachedResources =
          new AttachedResources(resourceRefNames, appName, env);

      AttributeValue testUserEmail = AttributeValue.builder().s(testAdmin).build();
      AttributeValue testUserSecretHash = AttributeValue.builder().s(testAdminPasswordHash).build();

      PutItemRequest putRequest =
          PutItemRequest.builder()
              .tableName(attachedResources.getCanonicalName("USERS_DB"))
              .item(
                  Map.of(
                      "user", testUserEmail,
                      "secretHash", testUserSecretHash))
              .build();

      DynamoDbClient ddbc = DynamoDbClient.create();
      ddbc.putItem(putRequest);

      initialized = true;
    }
  }

  @Test
  void passLoginOnGoodCredentials() throws AuthException {
    String encodedToken = auth.loginUser(testAdmin, testAdminPassword);

    Algorithm algorithm = Algorithm.HMAC256(SYSTEM_KEY);
    JWTVerifier verifier = JWT.require(algorithm).withIssuer("jonet").build();

    verifier.verify(encodedToken);
  }

  @Test
  @Disabled
  void failLoginOnBadCredentials() {}

  @Test
  @Disabled
  void rejectInvalidSignature() {}

  @Test
  @Disabled
  void acceptValidSignature() {}

  @Test
  @Disabled
  void readValidClaim() {}

  @Test
  @Disabled
  void rejectInvalidClaim() {}
}
