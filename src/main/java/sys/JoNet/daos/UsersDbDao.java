package sys.JoNet.daos;

import sys.JoNet.utils.AttachedResources;

/**
 * The UsersDbDao provides a more ergonomic wrapper on the AWS DynamoDb semantics for get and put
 * operations.
 */
public class UsersDbDao extends AbstractDynamoDbDao<User> {

  private static final AttachedResources ar =
      new AttachedResources(new String[] {"USERS_DB"}, "jonet", System.getenv("JONET_ENV"));
  private static final String TABLE = ar.getCanonicalName("USERS_DB");

  public UsersDbDao() {
    super(TABLE);
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
