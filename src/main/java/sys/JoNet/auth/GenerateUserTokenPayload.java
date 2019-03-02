package sys.JoNet.auth;

import lombok.Data;
import sys.JoNet.Validatable;

@Data
class GenerateUserTokenPayload implements Validatable {
  private String username;
  private String password;

  public boolean isValid() {
    return username != null && !username.isEmpty() && password != null && !password.isEmpty();
  }
}
