package sys.esm.daos;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @Setter(AccessLevel.PRIVATE)
  private String username;

  @Setter(AccessLevel.PRIVATE)
  private String secretHash;

  @Setter(AccessLevel.PRIVATE)
  private boolean isAdmin;

  public User(String username, String secretHash) {
    this.username = username;
    this.secretHash = secretHash;
    this.isAdmin = false;
  }

  public User(String username, String secretHash, boolean isAdmin) {
    this.username = username;
    this.secretHash = secretHash;
    this.isAdmin = isAdmin;
  }
}
