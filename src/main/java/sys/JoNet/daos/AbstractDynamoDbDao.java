package sys.JoNet.daos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.model.*;

/**
 * The AbstractDynamoDbDao implements the KVObjectDao semantics and provides type-agnostic
 * inner-implementations for the put and set methods. Tables with composite keys or secondary
 * indexes are not supported at all. The AbstractDynamoDbDao detects if it is in a test environment
 * and accordingly attempts to find a local dynamodb to test against.
 */
abstract class AbstractDynamoDbDao<T extends Serializable> implements KVObjectDao<T> {

  private DynamoDbClient dbClient;
  private String table;
  private String partitionKey;

  /**
   * The constructor takes a single argument, which is the name of the table to work against.
   *
   * @param table the canonical table name to operate against
   */
  public AbstractDynamoDbDao(String table) {
    this.table = table;
    dbClient = DynamoDbClient.create();
  }

  /**
   * Returns the raw DB client, which may be used for operations not supported by the implementing
   * DAO.
   */
  public DynamoDbClient getDbClient() {
    return dbClient;
  }

  protected void endpointOverride(URI endpointUri) {
    dbClient = DynamoDbClient.builder().endpointOverride(endpointUri).build();
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

  public abstract T get(String key) throws NoItemException, Exception;

  /*
   * Gets some object stored under the provided partition key from the database. The object is
   * deserialized and rehydrated into a POJO of type T.
   * @param key partition key of the object to get
   * @return a POJO of type T
   */
  protected T getImpl(String key) throws NoItemException, Exception {
    if (partitionKey == null) {
      setPartitionKeyAttributeName();
    }

    // Create and send a request to get the user record from the database. Store the response as
    // a Map.
    final GetItemRequest userRecordRequest =
        GetItemRequest.builder()
            .tableName(table)
            .key(Map.of(partitionKey, AttributeValue.builder().s(key).build()))
            .build();
    final Map<String, AttributeValue> itemMap = dbClient.getItem(userRecordRequest).item();

    if (itemMap.isEmpty()) {
      throw new NoItemException(key);
    }

    final ByteArrayInputStream byteInStream =
        new ByteArrayInputStream(itemMap.get("object").b().asByteArray());

    try {
      final ObjectInputStream objectInStream = new ObjectInputStream(byteInStream);
      final T item = (T) objectInStream.readObject();
      objectInStream.close();

      return item;
    } catch (IOException | ClassNotFoundException e) {
      throw e;
    }
  }

  public abstract void put(T object) throws Exception;

  /*
   * Stores some object, overwriting any item which may already exist under the provided key.
   * Stored objects are binary-serialized.
   * @param object the object to serialize and write to the database
   * @param key the partition key to index the item under
   */
  protected void putImpl(T item, String key) throws Exception {
    if (partitionKey == null) {
      setPartitionKeyAttributeName();
    }

    final ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
    try {
      final ObjectOutputStream objectOutStream = new ObjectOutputStream(byteOutStream);

      objectOutStream.writeObject(item);
      objectOutStream.flush();
      objectOutStream.close();

      final byte[] userBytes = byteOutStream.toByteArray();

      // Binary serialize the object and add it to the item map along with the partition key for
      // the item.
      final HashMap<String, AttributeValue> itemMap = new HashMap();
      itemMap.put(partitionKey, AttributeValue.builder().s(key).build());
      itemMap.put("object", AttributeValue.builder().b(SdkBytes.fromByteArray(userBytes)).build());

      // Form a put request and send it to the database
      final PutItemRequest req = PutItemRequest.builder().tableName(table).item(itemMap).build();
      dbClient.putItem(req);
    } catch (IOException e) {
      throw e;
    }
  }
}
