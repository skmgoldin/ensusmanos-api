package sys.esm.daos;

public class NoItemException extends Exception {
  public NoItemException(String key) {
    super("No item exists for the key " + key);
  }
}
