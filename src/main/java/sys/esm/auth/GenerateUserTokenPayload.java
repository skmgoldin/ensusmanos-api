package sys.esm.auth;

import lombok.Data;
import sys.esm.Validatable;

@Data
class GenerateUserTokenPayload implements Validatable {
  private String username;
  private String password;

  public boolean isValid() {
    return username != null && !username.isEmpty() && password != null && !password.isEmpty();
  }
}
