package sys.JoNet.daos;

import org.junit.jupiter.api.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

class UsersDbDaoTest {

  private static final UsersDbDao usersDb = new UsersDbDao();
  private static final User user = new User("Bob Dole", "DEADBEEF");

  @Test
  void putARecord() throws Exception {
    usersDb.put(user);
  }

  @Test
  void getARecord() throws NoItemException, Exception {
    usersDb.put(user);
    User returnedRecord = usersDb.get("Bob Dole");

    Assertions.assertTrue(returnedRecord.getUsername().equals(user.getUsername()));
    Assertions.assertTrue(returnedRecord.getSecretHash().equals(user.getSecretHash()));
    Assertions.assertTrue(returnedRecord.isAdmin() == false);
  }

  /** Before running these tests, we need to make sure a table exists. */
  @BeforeAll
  public static void setup() {

    AttributeDefinition ad =
        AttributeDefinition.builder().attributeName("username").attributeType("S").build();
    KeySchemaElement kse =
        KeySchemaElement.builder().attributeName("username").keyType("HASH").build();
    CreateTableRequest req =
        CreateTableRequest.builder()
            .billingMode("PAY_PER_REQUEST")
            .attributeDefinitions(ad)
            .keySchema(kse)
            .tableName(System.getenv("JONET_API_USERS_DB_NAME"))
            .build();

    DynamoDbClient dbClient = usersDb.getDbClient();

    dbClient.createTable(req);
  }

  /** Tear down the table. */
  @AfterAll
  public static void teardown() {
    DeleteTableRequest req =
        DeleteTableRequest.builder().tableName(System.getenv("JONET_API_USERS_DB_NAME")).build();

    DynamoDbClient dbClient = usersDb.getDbClient();

    dbClient.deleteTable(req);
  }
}
