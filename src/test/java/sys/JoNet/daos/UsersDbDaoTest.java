package sys.JoNet.daos;

import org.junit.jupiter.api.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import sys.JoNet.utils.AttachedResources;

class UsersDbDaoTest {

  private static final UsersDbDao usersDb = new UsersDbDao();
  private static final User user = new User("Bob Dole", "DEADBEEF");

  @Test
  void putARecord() {
    usersDb.put(user);
  }

  @Test
  void getARecord() throws NoItemException {
    usersDb.put(user);
    User returnedRecord = usersDb.get("Bob Dole");

    Assertions.assertTrue(returnedRecord.getUsername().equals(user.getUsername()));
    Assertions.assertTrue(returnedRecord.getSecretHash().equals(user.getSecretHash()));
    Assertions.assertTrue(returnedRecord.isAdmin() == false);
  }

  /** Before running these tests, we need to make sure a table exists. */
  @BeforeAll
  public static void setup() {
    AttachedResources ar =
        new AttachedResources(new String[] {"USERS_DB"}, "jonet", System.getenv("JONET_ENV"));

    AttributeDefinition ad =
        AttributeDefinition.builder().attributeName("username").attributeType("S").build();
    KeySchemaElement kse =
        KeySchemaElement.builder().attributeName("username").keyType("HASH").build();
    CreateTableRequest req =
        CreateTableRequest.builder()
            .billingMode("PAY_PER_REQUEST")
            .attributeDefinitions(ad)
            .keySchema(kse)
            .tableName(ar.getCanonicalName("USERS_DB"))
            .build();

    DynamoDbClient dbClient = usersDb.getDbClient();

    dbClient.createTable(req);
  }

  /** Tear down the table. */
  @AfterAll
  public static void teardown() {
    AttachedResources ar =
        new AttachedResources(new String[] {"USERS_DB"}, "jonet", System.getenv("JONET_ENV"));

    DeleteTableRequest req =
        DeleteTableRequest.builder().tableName(ar.getCanonicalName("USERS_DB")).build();

    DynamoDbClient dbClient = usersDb.getDbClient();

    dbClient.deleteTable(req);
  }
}
