package sys.JoNet.daos;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.model.*;

/**
 * The AbstractDynamoDbDao implements the KVObjectDao semantics and provides type-agnostic
 * inner-implementations for the put and set methods. Tables with composite keys or secondary
 * indexes are not supported at all. The AbstractDynamoDbDao detects if it is in a test environment
 * and accordingly attempts to find a local dynamodb to test against.
 */
abstract class AbstractDynamoDbDao<T> implements KVObjectDao<T> {

  private DynamoDbClient dbClient;
  private String table;
  private String partitionKey;

  /**
   * The constructor takes a single argument, which is the name of the table to work against. The
   * constructor also checks the environment it is in and, if it is a test environment, attempts to
   * connect to a local database instance.
   *
   * @param table the canonical table name to operate against
   */
  public AbstractDynamoDbDao(String table) {
    this.table = table;
    if (System.getenv("JONET_TEST").equals("true")) {
      try {
        URI endpointUri =
            new URI(
                "http://"
                    + InetAddress.getByName(System.getenv("JONET_TEST_USERS_DB_HOST_NAME"))
                        .getHostAddress()
                    + ":"
                    + System.getenv("JONET_TEST_USERS_DB_PORT"));
        dbClient = DynamoDbClient.builder().endpointOverride(endpointUri).build();
      } catch (URISyntaxException | UnknownHostException e) {
        System.err.println("The URI for the dev db is malformed. No db client set.");
      }
    } else {
      dbClient = DynamoDbClient.create();
    }
  }

  /**
   * Returns the raw DB client, which may be used for operations not supported by the implementing
   * DAO.
   */
  public DynamoDbClient getDbClient() {
    return dbClient;
  }

  /**
   * Queries the instance table to discover its partition key, and then sets the partition key for
   * the instance.
   */
  private void setPartitionKeyAttributeName() {
    DescribeTableRequest req = DescribeTableRequest.builder().tableName(table).build();

    DescribeTableResponse resp = dbClient.describeTable(req);

    List<KeySchemaElement> keySchemas = resp.table().keySchema();

    partitionKey = keySchemas.get(0).attributeName();
  }

  public abstract T get(String key) throws NoItemException;

  /*
   * @param key partition key of the object to get
   * @return a map representing the queried object
   */
  protected Map<String, AttributeValue> getImpl(String key) {
    if (partitionKey == null) {
      setPartitionKeyAttributeName();
    }

    // Create and send a request to get the user record from the database
    GetItemRequest userRecordRequest =
        GetItemRequest.builder()
            .tableName(table)
            .key(Map.of(partitionKey, AttributeValue.builder().s(key).build()))
            .build();
    return dbClient.getItem(userRecordRequest).item();
  }

  public abstract void put(T object);

  /*
   * Stores a map representation of some object. The map must include one key-value pair whose key is the table's partition key.
   * @param objAsMap a map representing the object to be stored
   */
  protected void putImpl(Map<String, AttributeValue> objAsMap) {
    if (partitionKey == null) {
      setPartitionKeyAttributeName();
    }

    PutItemRequest req = PutItemRequest.builder().tableName(table).item(objAsMap).build();

    dbClient.putItem(req);
  }
}
