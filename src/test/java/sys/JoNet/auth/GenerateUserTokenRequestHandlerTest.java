package sys.JoNet.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.*;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.core.*;
import software.amazon.awssdk.services.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.kms.*;
import software.amazon.awssdk.services.kms.model.*;
import sys.JoNet.Answer;
import sys.JoNet.daos.User;
import sys.JoNet.daos.UsersDbDao;
import sys.JoNet.utils.AttachedResources;
import sys.JoNet.utils.SystemKey;

class GenerateUserTokenRequestHandlerTest {

  static final Random random = new Random();

  // We create a test admin user with a random name and password..
  static final String testAdmin = getRandomString();
  static final String testAdminPassword = getRandomString();
  static final String testAdminPasswordHash =
      Hashing.sha256().hashString(testAdminPassword, StandardCharsets.UTF_8).toString();

  // We create a test non-admin user with a random name and password..
  static final String testUser = getRandomString();
  static final String testUserPassword = getRandomString();
  static final String testUserPasswordHash =
      Hashing.sha256().hashString(testUserPassword, StandardCharsets.UTF_8).toString();

  // Instantiate a handler instance and fetch the system key for use later verifying JWTs in these
  // tests.
  static final GenerateUserTokenRequestHandler reqHandler = new GenerateUserTokenRequestHandler();
  static final String SYSTEM_KEY = SystemKey.getSystemKey();

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
  void loginNormalUser() throws UserAuthException {
    GenerateUserTokenPayload payload = new GenerateUserTokenPayload();
    payload.setUsername(testUser);
    payload.setPassword(testUserPassword);

    Answer answer = reqHandler.process(payload);
    String encodedToken = answer.getBody();

    Algorithm algorithm = Algorithm.HMAC256(SYSTEM_KEY);
    JWTVerifier verifier = JWT.require(algorithm).withIssuer("jonet").build();

    DecodedJWT jwt = verifier.verify(encodedToken);
    Assertions.assertFalse(jwt.getClaim("isAdmin").asBoolean());
  }

  /**
   * Here we want to test whether a client presenting valid admin login credentials is returned a
   * valid JWT with the admin attestation signed by system key.
   */
  @Test
  void loginAdminUser() throws UserAuthException {
    GenerateUserTokenPayload payload = new GenerateUserTokenPayload();
    payload.setUsername(testAdmin);
    payload.setPassword(testAdminPassword);

    Answer answer = reqHandler.process(payload);
    String encodedToken = answer.getBody();

    Algorithm algorithm = Algorithm.HMAC256(SYSTEM_KEY);
    JWTVerifier verifier = JWT.require(algorithm).withIssuer("jonet").build();

    DecodedJWT jwt = verifier.verify(encodedToken);
    Assertions.assertTrue(jwt.getClaim("isAdmin").asBoolean());
  }

  @Test
  void failLoginWithBadUsername() {
    GenerateUserTokenPayload payload = new GenerateUserTokenPayload();
    payload.setUsername(getRandomString());
    payload.setPassword(testAdminPassword);

    Answer answer = reqHandler.process(payload);
    String encodedToken = answer.getBody();

    Assertions.assertTrue(answer.getCode() == 400);

    Algorithm algorithm = Algorithm.HMAC256(SYSTEM_KEY);
    JWTVerifier verifier = JWT.require(algorithm).withIssuer("jonet").build();

    try {
      verifier.verify(encodedToken);
    } catch (JWTVerificationException e) {
      return;
    }

    Assertions.fail("User was able to login with a random string for a username");
  }

  @Test
  void failLoginWithBadPassword() {
    GenerateUserTokenPayload payload = new GenerateUserTokenPayload();
    payload.setUsername(testAdmin);
    payload.setPassword(getRandomString());

    Answer answer = reqHandler.process(payload);
    String encodedToken = answer.getBody();

    Assertions.assertTrue(answer.getCode() == 400);

    Algorithm algorithm = Algorithm.HMAC256(SYSTEM_KEY);
    JWTVerifier verifier = JWT.require(algorithm).withIssuer("jonet").build();

    try {
      verifier.verify(encodedToken);
    } catch (JWTVerificationException e) {
      return;
    }

    Assertions.fail("User was able to login with a random string for a password");
  }

  @Test
  void failLoginWithBadUsernameAndPassword() {
    GenerateUserTokenPayload payload = new GenerateUserTokenPayload();
    payload.setUsername(getRandomString());
    payload.setPassword(getRandomString());

    Answer answer = reqHandler.process(payload);
    String encodedToken = answer.getBody();

    Assertions.assertTrue(answer.getCode() == 400);

    Algorithm algorithm = Algorithm.HMAC256(SYSTEM_KEY);
    JWTVerifier verifier = JWT.require(algorithm).withIssuer("jonet").build();

    try {
      verifier.verify(encodedToken);
    } catch (JWTVerificationException e) {
      return;
    }

    Assertions.fail("User was able to login with a random username and password");
  }

  /**
   * To setup the tests we are going to add two transient users to the USERS_DB: a normal user and
   * an admin.
   */
  @BeforeAll
  public static void setup() {
    // Create a new table
    final UsersDbDao usersDb = new UsersDbDao();
    final AttachedResources ar =
        new AttachedResources(new String[] {"USERS_DB"}, "jonet", System.getenv("JONET_ENV"));

    final AttributeDefinition ad =
        AttributeDefinition.builder().attributeName("username").attributeType("S").build();
    final KeySchemaElement kse =
        KeySchemaElement.builder().attributeName("username").keyType("HASH").build();
    final CreateTableRequest req =
        CreateTableRequest.builder()
            .billingMode("PAY_PER_REQUEST")
            .attributeDefinitions(ad)
            .keySchema(kse)
            .tableName(ar.getCanonicalName("USERS_DB"))
            .build();

    final DynamoDbClient rawDbClient = usersDb.getDbClient();
    rawDbClient.createTable(req);

    // Add users to it
    final User admin = new User(testAdmin, testAdminPasswordHash, true);
    final User user = new User(testUser, testUserPasswordHash);

    usersDb.put(admin);
    usersDb.put(user);
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

    final UsersDbDao dbClient = new UsersDbDao();
    final DynamoDbClient rawDb = dbClient.getDbClient();

    // Create AttributeValue variables from the initialized static actor variables.
    AttributeValue testAdminEmail = AttributeValue.builder().s(testAdmin).build();
    AttributeValue testUserEmail = AttributeValue.builder().s(testUser).build();

    // Prepare two delete requests, one for the admin and one for the regular user.
    DeleteRequest adminDeleteRequest =
        DeleteRequest.builder().key(Map.of("username", testAdminEmail)).build();
    DeleteRequest userDeleteRequest =
        DeleteRequest.builder().key(Map.of("username", testUserEmail)).build();

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
    rawDb.batchWriteItem(batchWriteReq);

    final DeleteTableRequest req =
        DeleteTableRequest.builder()
            .tableName(attachedResources.getCanonicalName("USERS_DB"))
            .build();

    rawDb.deleteTable(req);
  }
}
