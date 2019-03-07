package sys.JoNet.daos;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import sys.JoNet.utils.AttachedResources;

/**
 * The UsersDbDao provides a more ergonomic wrapper on the AWS DynamoDb semantics for get and put
 * operations.
 */
public class UsersDbDao extends AbstractDynamoDbDao<User> {

  private static final AttachedResources ar =
      new AttachedResources(new String[] {"USERS_DB"}, "jonet", System.getenv("JONET_API_ENV"));
  private static final String TABLE = ar.getCanonicalName("USERS_DB");

  /**
   * The constructor checks the environment it is in and, if it is a test environment, attempts to
   * connect to a local database instance.
   */
  public UsersDbDao() {
    super(TABLE);
    if (System.getenv("JONET_API_TEST").equals("true")) {
      try {
        URI endpointUri =
            new URI(
                "http://"
                    + InetAddress.getByName(System.getenv("JONET_API_TEST_USERS_DB_HOST_NAME"))
                        .getHostAddress()
                    + ":"
                    + System.getenv("JONET_API_TEST_USERS_DB_PORT"));
        endpointOverride(endpointUri);
      } catch (URISyntaxException | UnknownHostException e) {
        System.err.println("The URI for the dev db is malformed. No db client set.");
      }
    }
  }

  /**
   * Query a user in the database
   *
   * @param key the partition key of the user being queried
   * @return a User object representing the queried user record
   */
  public User get(String key) throws NoItemException, Exception {
    return getImpl(key);
  }

  /**
   * Add a user to the database. If the user already exists, its record is overwritten.
   *
   * @param user the user to write to the database
   */
  public void put(User user) throws Exception {
    putImpl(user, user.getUsername());
  }
}
