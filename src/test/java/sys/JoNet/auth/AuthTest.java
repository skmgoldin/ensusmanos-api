package sys.JoNet.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.*;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.core.*;
import software.amazon.awssdk.services.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.kms.*;
import software.amazon.awssdk.services.kms.model.*;
import sys.JoNet.utils.AttachedResources;

/** These tests are for the access control and user accounts system. */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.JUnitSpelling"})
class AuthTest {

  // We get seed randomness from the AWS Key Management Service, kms, and provide it to Java's
  // SecureRandom implementation.
  static final KmsClient kms = KmsClient.create();
  static final GenerateRandomRequest seedReq =
      GenerateRandomRequest.builder().numberOfBytes(32).build();
  static final byte[] randomSeed = kms.generateRandom(seedReq).plaintext().asByteArray();
  static final SecureRandom random = new SecureRandom(randomSeed);

  // We create a test admin user.
  static final String testAdmin = getRandomString();
  static final String testAdminPassword = getRandomString();
  static final String testAdminPasswordHash =
      Hashing.sha256().hashString(testAdminPassword, StandardCharsets.UTF_8).toString();

  // We create a test non-admin user.
  static final String testUser = getRandomString();
  static final String testUserPassword = getRandomString();
  static final String testUserPasswordHash =
      Hashing.sha256().hashString(testUserPassword, StandardCharsets.UTF_8).toString();

  // Instantiate an Auth instance and fetch the system key for use later verifying JWTs in these
  // tests.
  static final Auth auth = new Auth();
  static final String SYSTEM_KEY = Auth.fetchSystemKey();

  /** Uses the SecureRandom instance to return random 32-byte hex-encoded strings. */
  private static String getRandomString() {
    byte[] randomBytes = new byte[32];
    random.nextBytes(randomBytes);
    return BaseEncoding.base16().encode(randomBytes);
  }

  /**
   * Here we want to test whether a client presenting valid user login credentials is returned a
   * valid JWT signed by system key.
   */
  @Test
  void passLoginOnGoodCredentials() throws AuthException {
    String encodedToken = auth.loginUser(testUser, testUserPassword);

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

  /**
   * To setup the tests we are going to add two transient users to the USERS_DB: a normal user and
   * an admin.
   */
  @BeforeAll
  public static void setup() {
    // Initialize attached resources canonical names
    final String[] resourceRefNames = {"SYSTEM_KEY", "USERS_DB"};
    final String appName = "jonet";
    final String env = System.getenv("JONET_ENV");
    final AttachedResources attachedResources =
        new AttachedResources(resourceRefNames, appName, env);

    // Create AttributeValue variables from the initialized static actor variables.
    AttributeValue testAdminEmail = AttributeValue.builder().s(testAdmin).build();
    AttributeValue testAdminSecretHash = AttributeValue.builder().s(testAdminPasswordHash).build();
    AttributeValue testUserEmail = AttributeValue.builder().s(testUser).build();
    AttributeValue testUserSecretHash = AttributeValue.builder().s(testUserPasswordHash).build();

    // Prepare two put requests, one for the admin and one for the regular user. For the admin we
    // add an "isAdmin" property and set it to true.
    PutRequest adminPutRequest =
        PutRequest.builder()
            .item(
                Map.of(
                    "user", testAdminEmail,
                    "secretHash", testAdminSecretHash,
                    "isAdmin", AttributeValue.builder().bool(true).build()))
            .build();
    PutRequest userPutRequest =
        PutRequest.builder()
            .item(
                Map.of(
                    "user", testUserEmail,
                    "secretHash", testUserSecretHash))
            .build();

    // Create a list of writes we desire to make
    final LinkedList<WriteRequest> writeReqsList = new LinkedList();
    writeReqsList.add(WriteRequest.builder().putRequest(adminPutRequest).build());
    writeReqsList.add(WriteRequest.builder().putRequest(userPutRequest).build());

    // Create a mapping of tables we desire to write to, along with the lists of writes we desire to
    // make to those tables.
    final HashMap<String, LinkedList<WriteRequest>> writeReqMap = new HashMap();
    writeReqMap.put(attachedResources.getCanonicalName("USERS_DB"), writeReqsList);

    // Create a BatchWriteItemRequest and send it
    final BatchWriteItemRequest batchWriteReq =
        BatchWriteItemRequest.builder().requestItems(writeReqMap).build();
    DynamoDbClient ddbc = DynamoDbClient.create();
    ddbc.batchWriteItem(batchWriteReq);
  }

  /** After the tests, we should delete the temporary users from the database. */
  @AfterAll
  public static void teardown() {
    // Initialize attached resources canonical names
    final String[] resourceRefNames = {"SYSTEM_KEY", "USERS_DB"};
    final String appName = "jonet";
    final String env = System.getenv("JONET_ENV");
    final AttachedResources attachedResources =
        new AttachedResources(resourceRefNames, appName, env);

    // Create AttributeValue variables from the initialized static actor variables.
    AttributeValue testAdminEmail = AttributeValue.builder().s(testAdmin).build();
    AttributeValue testUserEmail = AttributeValue.builder().s(testUser).build();

    // Prepare two delete requests, one for the admin and one for the regular user.
    DeleteRequest adminDeleteRequest =
        DeleteRequest.builder().key(Map.of("user", testAdminEmail)).build();
    DeleteRequest userDeleteRequest =
        DeleteRequest.builder().key(Map.of("user", testUserEmail)).build();

    // Create a list of writes (deletes in this case) we desire to make
    final LinkedList<WriteRequest> deleteReqsList = new LinkedList();
    deleteReqsList.add(WriteRequest.builder().deleteRequest(adminDeleteRequest).build());
    deleteReqsList.add(WriteRequest.builder().deleteRequest(userDeleteRequest).build());

    // Create a mapping of tables we desire to write to, along with the lists of writes (deletes) we
    // desire to make to those tables.
    final HashMap<String, LinkedList<WriteRequest>> deleteReqMap = new HashMap();
    deleteReqMap.put(attachedResources.getCanonicalName("USERS_DB"), deleteReqsList);

    // Create a BatchWriteItemRequest and send it
    final BatchWriteItemRequest batchWriteReq =
        BatchWriteItemRequest.builder().requestItems(deleteReqMap).build();
    DynamoDbClient ddbc = DynamoDbClient.create();
    ddbc.batchWriteItem(batchWriteReq);
  }
}
