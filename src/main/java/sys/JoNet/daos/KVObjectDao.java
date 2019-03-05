package sys.JoNet.daos;

/**
 * The KVObjectDao is an interface describing the basic operations for a DAO representing any type
 * of key-value store.
 */
public interface KVObjectDao<T> {

  T get(String key) throws NoItemException, Exception;

  /**
   * The put operation adds an object to the key value store, overwriting any which may already
   * exist under the object's primary key.
   */
  void put(T object) throws Exception;
}
